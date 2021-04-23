package com.imooc.coupon.service.impl;

import com.google.common.base.Stopwatch;
import com.imooc.coupon.constant.Constant;
import com.imooc.coupon.dao.CouponTemplateDao;
import com.imooc.coupon.entity.CouponTemplate;
import com.imooc.coupon.service.IAsyncService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Annotation  异步处理类
 * @ClassName AsyncServiceImpl
 * @Author ChenWenJie
 * @Data 2020/5/15 5:04 下午
 * @Version 1.0
 **/
@Slf4j
@Service
public class AsyncServiceImpl implements IAsyncService {
    private final CouponTemplateDao templateDao;

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public AsyncServiceImpl(CouponTemplateDao templateDao, StringRedisTemplate redisTemplate) {
        this.templateDao = templateDao;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 根据模块异步的创建优惠卷码
     * @param template {@link CouponTemplate}优惠卷模版实体
     */
    @Async("getAsyncExecutor")
    @Override
    public void asyncConstructCouponByTemplate(CouponTemplate template) {
        Stopwatch watch = Stopwatch.createStarted();
        Set<String> couponCode = buildCouponCode(template);
        String redisKey = String.format("%s%s",
                Constant.RedisPrefix.COUPON_TEMPLATE,template.getId().toString());
        log.info("Push CouponCode To Redis:{}",
                redisTemplate.opsForList().rightPushAll(redisKey,couponCode));
        template.setAvailable(true);
        templateDao.save(template);
        watch.stop();
        log.info("Construct CouponCode By Template Cost:{}ms",
                watch.elapsed(TimeUnit.MILLISECONDS));
        log.info("CouponTemplate IS Available!",template.getId());
        // TODO 发送短信或者邮件通知优惠卷模版已经可用

    }

    /**
     * 构造优惠卷码
     * 优惠卷码(对应于每一张优惠卷，18位)
     * 前四位：产品线 + 类型
     * 中间六位：日期随机(190101)
     * 后八位：0～9 随机数构成
     * @param template ${@link CouponTemplate} 实体类
     * @return Set<String> 与template.count 相同个数的优惠卷码
     */
    private Set<String> buildCouponCode(CouponTemplate template){
        Stopwatch watch = Stopwatch.createStarted();
        HashSet<String> result = new HashSet<>(template.getCount());
        //前四位
        String prefix4=template.getProductLine().getCode().toString()
                +template.getCategory().getCode();
        String data = new SimpleDateFormat("yyMMdd")
                .format(template.getCreatetime());
        for (int i = 0; i < template.getCount(); i++) {
            result.add(prefix4 + buildCouponCodeSuffix14(data));
        }
        while (result.size()<template.getCount()){
            result.add(prefix4 + buildCouponCodeSuffix14(data));
        }
        assert result.size() == template.getCount();
        watch.stop();
        log.info("Build Coupon Code Cost: {}ms", watch.elapsed(TimeUnit.MILLISECONDS));
        return result;
    }

    /**
     * 构造优惠卷码的后14位
     * @param date 创建优惠卷的日期
     * @return 14 位优惠码
     */
    public String buildCouponCodeSuffix14(String date){
        char[] bases = new char[]{'1','2','3','4','5','6','7','8','9'};
        //中间六位
        List<Character> chars= date.chars()
                .mapToObj(e -> (char) e).collect(Collectors.toList());
        //重新组合
        Collections.shuffle(chars);
        String mid6 = chars.stream()
                .map(Objects::toString).collect(Collectors.joining());
        //后八位
        String prefix8 = RandomStringUtils.random(1,bases)
                +RandomStringUtils.randomNumeric(7);
        return mid6+prefix8;
    }
}
