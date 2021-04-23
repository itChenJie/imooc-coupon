package com.imooc.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.constant.Constant;
import com.imooc.coupon.constant.CouponStatus;
import com.imooc.coupon.entity.Coupon;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.service.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Annotation
 * @ClassName RedisServiceImpl
 * @Author ChenWenJie
 * @Data 2020/5/17 11:09 上午
 * @Version 1.0
 **/
@Slf4j
@Service
public class RedisServiceImpl implements IRedisService {
    private final StringRedisTemplate redisTemplate;
    @Autowired
    public RedisServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 根据userId 和状态找到缓存的优惠卷列表数据
     * 目的：避免换成穿透
     * @param userId 用户Id
     * @param status 优惠卷状态{@link CouponStatus}
     * @return {@link Coupon}s,注意，可能会返回 null，代表从没有过记录
     */
    @Override
    public List<Coupon> getCachedCoupons(Long userId,Integer status) {
        log.info("get Coupons from cache:{},{}",userId,status);
        String redisKey = status2RedisKey(status, userId);
        List<String> couponStrs = redisTemplate.opsForHash().values(redisKey)
                .stream()
                .map(o->Objects.toString(o,null))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(couponStrs)){
            saveEmptyCouponListToCache(userId, Collections.singletonList(status));
            return Collections.emptyList();
        }
        return couponStrs.stream()
                .map(cs->JSON.parseObject(cs,Coupon.class))
                .collect(Collectors.toList());
    }

    /**
     * 保存空的优惠卷列表到缓存中
     *
     * @param userId
     * @param status
     */
    @Override
    public void saveEmptyCouponListToCache(Long userId, List<Integer> status) {
        log.info("Save Empty List to Cache For User:{},Status",
                userId,status);
        //key 是coupon_id，value是系列化的Coupon
        HashMap<String, String> invalidCouponMap = new HashMap<>();
        invalidCouponMap.put("-1", JSON.toJSONString(Coupon.invalidCoupon()));

        //用户优惠卷缓存信息
        // KV K:status ->redisKey
        // V:{coupon_id:系列化的Coupon}

        //使用 SessionCallBack 把数据命令放入到Redis 的 pipeline
        SessionCallback<Object> sessionCallback = new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                status.forEach(s->{
                    String redisKey = status2RedisKey(s,userId);
                    redisOperations.opsForHash().putAll(redisKey,invalidCouponMap);
                });
                return null;
            }
        };
        log.info("Pipeline Exe Result:{}",
                JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));

    }

    /**
     * 尝试从Cache中获取一个优惠卷码
     *
     * @param templateId 优惠卷模板主键
     * @return 优惠卷码
     */
    @Override
    public String tryToAcquireCouponCodeFromCache(Integer templateId) {
        String redisKey = String.format("%s%s",
                Constant.RedisPrefix.COUPON_TEMPLATE,templateId.toString());
        //因为优惠卷码不存在顺序关系，左边pop或右边pop，没有影响
        String couponCode = redisTemplate.opsForList().leftPop(redisKey);
        log.info("Acquire Coupon Code:{},{},{}",templateId,redisKey,couponCode);

        return couponCode;
    }

    /**
     * 将优惠卷保存到Cache中
     *
     * @param userId
     * @param coupons
     * @param status
     * @throws CouponException
     * @return 保存成功的个数
     */
    @Override
    public Integer addCouponToCache(Long userId, List<Coupon> coupons, Integer status) throws CouponException {
        log.info("Add Coupon To Cache:{},{},{}",userId,JSON.toJSONString(coupons),status);
        Integer result =-1;
        CouponStatus couponStatus = CouponStatus.of(status);
        switch (couponStatus){
            case USABLE:
                result=addCouponToCacheForUsable(userId,coupons);
                break;
            case USED:
                result = addCouponToCacheForUsed(userId, coupons);
                break;
            case EXPIRED:
                result = addCouponToCacheForExpired(userId, coupons);
                break;
        }
        return result;
    }

    /**
     * 新增加优惠卷到 Cache 中
     * @param userId
     * @param coupons
     * @return
     */
    private Integer addCouponToCacheForUsable(Long userId,List<Coupon> coupons) throws CouponException{
        //如果 status 是 USABLE，代表是新增的优惠卷
        //只会影响一个Cache：USER_COUPON_USABLE
        HashMap<String, String> needCachedObject = new HashMap<>();
        coupons.forEach(c->{
            needCachedObject.put(c.getId().toString(),
                    JSON.toJSONString(c));
        });
        String redisKey = status2RedisKey(CouponStatus.USABLE.getCode(), userId);
        redisTemplate.opsForHash().putAll(redisKey,needCachedObject);
        log.info("Add{} Coupon To Cache:{},{}",needCachedObject.size(),userId,redisKey);
        redisTemplate.expire(redisKey,getRandomExpirationTime(1,2), TimeUnit.SECONDS);
        return needCachedObject.size();
    }

    /**
     * 将已使用的优惠卷加入到Cache中
     * @param userId
     * @param coupons
     * @return
     * @throws CouponException
     */
    private Integer addCouponToCacheForUsed(Long userId,List<Coupon> coupons)throws CouponException{
        //如果status 是 USED 代表用户操作是使用当前的优惠卷，影响到两个Cache
        //USABLE，USED
        log.debug("Add Coupon To Cache For Used.");
        HashMap<String, String> needCachedForUsed = new HashMap<>(coupons.size());
        String redisKeyUsable = status2RedisKey(CouponStatus.USABLE.getCode(), userId);
        String redisKeyUsed = status2RedisKey(CouponStatus.USED.getCode(), userId);

        //获取当前用户可用的优惠卷
        List<Coupon> curUsableCoupons = getCachedCoupons(userId,CouponStatus.USABLE.getCode());
        //当前可用的优惠卷个数一定是大于1的
        assert curUsableCoupons.size()>coupons.size();
        coupons.forEach(c->needCachedForUsed.put(
                c.getId().toString(),
                JSON.toJSONString(c)
        ));
        //校验当前的优惠卷参数是否与 Cached中的匹配
        List<Integer> curUsableIds = curUsableCoupons.stream()
                .map(Coupon::getId).collect(Collectors.toList());

        List<Integer> paramIds = coupons.stream()
                .map(Coupon::getId).collect(Collectors.toList());

        if (!CollectionUtils.isSubCollection(paramIds,curUsableIds)){
            log.error("CurCoupons Is Not Equal ToCache:{},{},{}",
                    userId,JSON.toJSONString(curUsableIds),JSON.toJSONString(paramIds));
            throw new CouponException("CurCoupons Is not equal to cache!");
        }

        List<String> needCleanKey = paramIds.stream()
                .map(i -> i.toString()).collect(Collectors.toList());
        SessionCallback<Objects> sessionCallback = new SessionCallback<Objects>() {
            @Override
            public Objects execute(RedisOperations redisOperations) throws DataAccessException {
                //1.已使用的优惠卷Cache缓存添加
                redisOperations.opsForHash().putAll(redisKeyUsed,needCachedForUsed);
                //2.可用的优惠卷Cache需要清理
                redisOperations.opsForHash().delete(redisKeyUsable,needCleanKey.toArray());
                //3.重置过期时间
                redisOperations.expire(redisKeyUsable,
                        getRandomExpirationTime(1,2),
                        TimeUnit.SECONDS);
                redisOperations.expire(redisKeyUsed,
                        getRandomExpirationTime(1,2),
                        TimeUnit.SECONDS);
                return null;
            }
        };

        log.info("Pipeline Exe Result:{}",
                JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));
        return coupons.size();
    }

    /**
     * 将过期优惠卷加入到Cache中
     * @param userId
     * @param coupons
     * @return
     * @throws CouponException
     */
    private Integer addCouponToCacheForExpired(Long userId,List<Coupon> coupons)throws CouponException{
        //status 是 EXPIRED，代表是已有的优惠卷过期了，影响到两个Cache
        //USABLE，EXPIRED
        log.debug("Add Coupon to cache for expired");
        //最终需要保存到 cache
        HashMap<String, String> needCachedForExpired = new HashMap<>(coupons.size());
        String redisKeyForUsable = status2RedisKey(
                CouponStatus.USABLE.getCode(), userId
        );
        String redisKeyForExpired = status2RedisKey(
                CouponStatus.EXPIRED.getCode(),userId
        );
        List<Coupon> curUsableCoupons = getCachedCoupons(userId, CouponStatus.USABLE.getCode());
        List<Coupon> curExpiredCoupons = getCachedCoupons(userId, CouponStatus.EXPIRED.getCode());
        //当前可用的优惠卷个数一定是大于一的
        assert curUsableCoupons.size()>coupons.size();
        coupons.forEach(c->needCachedForExpired.put(
                c.getId().toString(),JSON.toJSONString(c)));
        //校验当前的优惠卷参数是否与Cached中的匹配
        List<Integer> curUsableIds = curUsableCoupons.stream()
                .map(Coupon::getId).collect(Collectors.toList());
        List<Integer> paramIds = coupons.stream()
                .map(Coupon::getId).collect(Collectors.toList());
        if (CollectionUtils.isSubCollection(paramIds,curUsableIds)){
            log.error("CurCoupons Is Not equal To Cache;{},{},{}",
                    userId,JSON.toJSONString(curUsableIds),JSON.toJSONString(paramIds));
            throw new CouponException("CurCoupons Is not equal to cache!");
        }
        List<String> needCleanKey = paramIds.stream()
                .map(i-> toString()).collect(Collectors.toList());
        SessionCallback<Objects> sessionCallback = new SessionCallback<Objects>() {
            @Override
            public  Objects execute(RedisOperations redisOperations) throws DataAccessException {
                //1:已过期的优惠卷 cache缓存
                redisOperations.opsForHash().putAll(redisKeyForExpired,needCachedForExpired);
                //2.可用的优惠卷Cache 需要清理
                redisOperations.opsForHash().delete(redisKeyForUsable,needCleanKey.toArray());
                //重置过期时间
                redisOperations.expire(redisKeyForUsable,
                        getRandomExpirationTime(1,2),TimeUnit.SECONDS);
                redisOperations.expire(redisKeyForExpired,
                        getRandomExpirationTime(1,2),TimeUnit.SECONDS);
                return null;
            }
        };
        log.info("Pipeline Exe Result:{}",JSON.toJSONString(redisTemplate.executePipelined(sessionCallback)));
        return coupons.size();
    }

    /**
     * 根据status获取到对应到 Redis key
     * @param status
     * @param userId
     * @return
     */
    private String status2RedisKey(Integer status,Long userId){
        String redisKey="";
        CouponStatus couponStatus = CouponStatus.of(status);
        switch (couponStatus){
            case USABLE:
                redisKey = String.format("%s%s",
                        Constant.RedisPrefix.USER_COUPON_USABLE,userId);
                break;
            case USED:
                redisKey = String.format("%s%s",
                        Constant.RedisPrefix.USER_COUPON_USED,userId);
                break;
            case EXPIRED:
                redisKey = String.format("%s%s",
                        Constant.RedisPrefix.USER_COUPON_EXPIRED,userId);
        }
        return redisKey;
    }

    /**
     * 获取一个随机的过期时间
     * 缓存雪崩：key 在同一时间生效
     * @param min 最小的小时数
     * @param max 最多的小时数
     * @return 返回[min,max]之间的随机秒数
     */
    private Long getRandomExpirationTime(Integer min,Integer max){
        return RandomUtils.nextLong(
                min * 60 * 60,
                max * 60 * 60);
    }


}
