package com.imooc.coupon.service;

import com.imooc.coupon.entity.Coupon;
import com.imooc.coupon.exception.CouponException;

import java.util.List;

/**
 * redis 相关的操作服务接口定义
 * 1.用的三个状态优惠卷Cache 相关操作
 * 2.优惠卷模板生成的优惠卷码Cache操作
 */
public interface IRedisService {
    /**
     * 根据userId 和状态找到缓存的优惠卷列表数据
     * @param userId 用户Id
     * @param status 优惠卷状态{@link com.imooc.coupon.constant.CouponStatus}
     * @return {@link Coupon}s,注意，可能会返回 null，代表从没有过记录
     */
    List<Coupon> getCachedCoupons(Long userId,Integer status);

    /**
     * 保存空的优惠卷列表到缓存中
     * @param userId
     * @param status
     */
    void saveEmptyCouponListToCache(Long userId,List<Integer> status);

    /**
     * 尝试从Cache中获取一个优惠卷码
     * @param templateId 优惠卷模板主键
     * @return 优惠卷码
     */
    String tryToAcquireCouponCodeFromCache(Integer templateId);

    /**
     * 将优惠卷保存到Cache中
     * @param userId
     * @param coupons
     * @param status
     * @return保存成功的个数
     * @throws CouponException
     */
    Integer addCouponToCache(Long userId,List<Coupon> coupons,
                             Integer status)throws CouponException;
}
