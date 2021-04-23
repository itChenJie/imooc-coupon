package com.imooc.coupon.service;

import com.imooc.coupon.entity.Coupon;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.vo.AcquireTemplateRequest;
import com.imooc.coupon.vo.CouponTemplateSDK;
import com.imooc.coupon.vo.SettlementInfo;

import java.util.List;

/**
 * @Annotation 用户服务相关的接口定义接口
 * 1。用户三类状态优惠卷信息展示服务
 * 2。查看用户当前可以领取的优惠卷模板 -coupon-template 微服务配合实现
 * 3。用户领取优惠卷服务
 * 4。用户消费优惠卷服务 - coupon-settlement 微服务配合实现
 * @Author ChenWenJie
 * @Data 2020/5/16 10:13 下午
 */
public interface IUserservice {
    /**
     * 构建用户名id和状态查询优惠卷记录
     * @param userId
     * @param status
     * @return {@link Coupon}s
     * @throws CouponException
     */
    List<Coupon> findCouponsByStatus(Long userId,Integer status)throws CouponException;

    /**
     * 根据用户id查找当前可以领取的优惠卷模板
     * @param userId
     * @return
     * @throws CouponException
     */
    List<CouponTemplateSDK> findAvailableTemplate(Long userId)throws CouponException;

    /**
     * 用户领取优惠卷
     * @param request
     * @return
     * @throws CouponException
     */
    Coupon acquireTemplate(AcquireTemplateRequest request)throws CouponException;

    /**
     * 结算(核销)优惠卷
     * @param info {@link SettlementInfo}
     * @return {@link SettlementInfo}
     * @throws CouponException
     */
    SettlementInfo settlement(SettlementInfo info)throws CouponException;

}
