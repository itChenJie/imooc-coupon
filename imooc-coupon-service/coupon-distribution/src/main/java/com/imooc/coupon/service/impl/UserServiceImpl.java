package com.imooc.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.constant.Constant;
import com.imooc.coupon.constant.CouponStatus;
import com.imooc.coupon.dao.CouponDao;
import com.imooc.coupon.entity.Coupon;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.feign.SettlementClient;
import com.imooc.coupon.feign.TemplateClient;
import com.imooc.coupon.service.IRedisService;
import com.imooc.coupon.service.IUserservice;
import com.imooc.coupon.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Annotation 用户服务相关接口的实现
 * 所有的操作过程，状态都保存在Redis中，并通过Kafka把消息传递到 MYSQL中
 * 为什么使用 Kafka ，而不是直接使用SpringBoot中的异步处理？安全性
 * SpringBoot异步处理是存在失败的
 * @ClassName UserServiceImpl
 * @Author ChenWenJie
 * @Data 2020/5/17 9:07 下午
 * @Version 1.0
 **/
@Slf4j
@Service
public class UserServiceImpl implements IUserservice {
    private final CouponDao couponDao;

    private final IRedisService redisService;
    /** 模板微服务客户端*/
    private final TemplateClient templateClient;
    /** 结算微服务客户端*/
    private final SettlementClient settlementClient;
    /** Kafka客户端*/
    private final KafkaTemplate<String,String> kafkaTemplate;
    @Autowired
    @SuppressWarnings("all")
    public UserServiceImpl(CouponDao couponDao, IRedisService redisService,
                           TemplateClient templateClient, KafkaTemplate<String, String> kafkaTemplate, SettlementClient settlementClient) {
        this.couponDao = couponDao;
        this.redisService = redisService;
        this.templateClient = templateClient;
        this.kafkaTemplate = kafkaTemplate;
        this.settlementClient = settlementClient;
    }

    /**
     * 构建用户名id和状态查询优惠卷记录
     *
     * @param userId
     * @param status
     * @return {@link Coupon}s
     * @throws CouponException
     */
    @Override
    public List<Coupon> findCouponsByStatus(Long userId, Integer status) throws CouponException {
        List<Coupon> curCached = redisService.getCachedCoupons(userId, status);
        List<Coupon> pretarget ;
        if (CollectionUtils.isNotEmpty(curCached)){
            log.debug("coupon cache is not empty:{},{}",userId,status);
            pretarget = curCached;
        }else {
            log.debug("coupon cache is empty,get coupon from db:{},{}",userId,status);
            List<Coupon> dbCoupons = couponDao.findAllByUserIdAndStatus(userId, CouponStatus.of(status));
            //如果数据库中没有记录，直接返回就可以，Cache 中已经加入了一张无效的优惠卷
            if (CollectionUtils.isEmpty(dbCoupons)){
                log.debug("current user do have coupon:{},{]",userId,status);
                return dbCoupons;
            }
            // 填充 dbCoupon的 templateSDK字段
            Map<Integer,CouponTemplateSDK> id2TemplateSDK =
                    templateClient.findIds2TemplateSDK(
                            dbCoupons.stream()
                            .map(Coupon::getTemplateId)
                            .collect(Collectors.toList())
                    ).getDate();
            dbCoupons.forEach(
                    dc->dc.setTemplateSDK(
                            id2TemplateSDK.get(dc.getTemplateId()))
            );
            //数据库中存在记录
            pretarget = dbCoupons;
            //将记录写入Cache
            redisService.addCouponToCache(userId,pretarget,status);
        }
        //将无效优惠卷剔除
        pretarget = pretarget.stream()
                .filter(c->c.getId()!=-1)
                .collect(Collectors.toList());
        //如果当前获取的是可用优惠卷，还需要做对已过期优惠卷的延迟处理
        if (CouponStatus.of(status) == CouponStatus.USED){
            CouponClassify classify = CouponClassify.classify(pretarget);
            //如果已过期状态不为空，需要做延迟处理
            if (CollectionUtils.isNotEmpty(classify.getExpired())){
                log.info("Add Expired Coupons To Cache From FindCouponByStatus:{},{}",userId,status);
                redisService.addCouponToCache(userId,classify.getExpired(),CouponStatus.EXPIRED.getCode());
                //发送到 kafka 中做异步处理
                kafkaTemplate.send(Constant.TOPIC,
                        JSON.toJSONString(new CouponkafkaMessage(
                               CouponStatus.EXPIRED.getCode(),
                                classify.getExpired().stream()
                        .map(Coupon::getId).collect(Collectors.toList()))));
            }
        }
        return pretarget;
    }

    /**
     * 根据用户id查找当前可以领取的优惠卷模板
     * @param userId
     * @return
     * @throws CouponException
     */
    @Override
    public List<CouponTemplateSDK> findAvailableTemplate(Long userId) throws CouponException {
        long curTime = System.currentTimeMillis();
        List<CouponTemplateSDK> templateSDKS = templateClient.findAllUsableTemplate().getDate();
        log.debug("Find All Template(From TemplateClient) count:{}",templateSDKS.size());
        //过滤过期的优惠卷模板
        templateSDKS = templateSDKS.stream()
                .filter(t->t.getRule().getEXpiration().getDeadline()>curTime)
                .collect(Collectors.toList());
        log.info("Find Usable Template Count:{}",templateSDKS.size());
        //key 是TemplateId
        //value 中的key 是Template limit2Template，value是优惠卷模板
        Map<Integer, Pair<Integer,CouponTemplateSDK>> limit2Template = new HashMap<>(templateSDKS.size());
        templateSDKS.forEach(
                t->limit2Template.put(
                        t.getId(),
                        Pair.of(t.getRule().getLimitation(),t)
                )
        );
        List<CouponTemplateSDK> result = new ArrayList<>(limit2Template.size());
        List<Coupon> userUsableCoupons = findCouponsByStatus(userId,CouponStatus.USABLE.getCode());
        log.debug("Current User Has Usable Coupons:{},{}",userId,userUsableCoupons.size());
        //key   是 TemplateId
        Map<Integer,List<Coupon>> templateId2coupons = userUsableCoupons
                .stream()
                .collect(Collectors.groupingBy(Coupon::getTemplateId));
        //根据 Template 的Rule 判断是否可以领取优惠卷模板
        limit2Template.forEach((k,v)->{
            int limitation = v.getLeft();
            CouponTemplateSDK templateSDK = v.getRight();
            if (templateId2coupons.containsKey(k) && templateId2coupons.get(k).size()>=limitation){
                return;
            }
            result.add(templateSDK);
        });
        return result;
    }

    /**
     * 用户领取优惠卷
     * 1.从 TemplateClient中拿到对应的优惠卷，并检查是否过期
     * 2.根据limitation 判断用户是否可以领取
     * 3.save to db
     * 4.填充CouponTemplateSDK
     * 5。save to cache
     * @param request
     * @return
     * @throws CouponException
     */
    @Override
    public Coupon acquireTemplate(AcquireTemplateRequest request) throws CouponException {
        Map<Integer, CouponTemplateSDK> ids2Template =
                templateClient.findIds2TemplateSDK(
                        Collections.singletonList(request.getTemplateSDK().getId())
                ).getDate();
        //优惠卷模板是否存在
        if (ids2Template.size()<=0){
            log.error("Can Not Acquire Template From TemplateClient:{}",
                    request.getTemplateSDK().getId());
            throw new CouponException("Can not acquire Template from templateClint");
        }
        //用户是否可以领取这张优惠卷
        List<Coupon> userUsableCoupons = findCouponsByStatus(request.getUserId(), CouponStatus.USABLE.getCode());
        Map<Integer, List<Coupon>> templateId2Coupns = userUsableCoupons.stream()
                .collect(Collectors.groupingBy(Coupon::getTemplateId));
        if (templateId2Coupns.containsKey(request.getTemplateSDK().getId())
                &&templateId2Coupns.get(request.getTemplateSDK().getId()).size()>=
        request.getTemplateSDK().getRule().getLimitation()){
            log.error("Exceed template Assign Limitation:{}",request.getTemplateSDK().getId());
            throw new CouponException("Exceed template Assign Limitation");
        }
        //尝试去获取优惠卷码
        String couponCode = redisService.tryToAcquireCouponCodeFromCache(request.getTemplateSDK().getId());
        if (StringUtils.isEmpty(couponCode)){
            log.error("Can not acquire Coupon Code:{}",
                    request.getTemplateSDK().getId());
            throw new CouponException("Can not acquire Coupon Code");
        }
        Coupon newCoupon = new Coupon(request.getTemplateSDK().getId(),
                request.getUserId(), couponCode, CouponStatus.USABLE);
        newCoupon = couponDao.save(newCoupon);
        //填充Coupon 对象的CoupomTemplateSDk ，一定要在放入缓存之前去填充
        newCoupon.setTemplateSDK(request.getTemplateSDK());
        //放入缓存中
        redisService.addCouponToCache(request.getUserId(),
                Collections.singletonList(newCoupon),
                CouponStatus.USABLE.getCode());

        return newCoupon;
    }

    /**
     * 结算(核销)优惠卷
     * @param info {@link SettlementInfo}
     * @return {@link SettlementInfo}
     * @throws CouponException
     */
    @Override
    public SettlementInfo settlement(SettlementInfo info) throws CouponException {
        return null;
    }
}
