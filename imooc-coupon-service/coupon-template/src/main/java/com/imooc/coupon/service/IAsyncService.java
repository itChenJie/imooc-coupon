package com.imooc.coupon.service;

import com.imooc.coupon.entity.CouponTemplate;

/**
 * @Annotation 异步服务接口定义
 * @ClassName IAsyncService
 * @Author ChenWenJie
 * @Data 2020/5/15 11:30 上午
 * @Version 1.0
 **/
public interface IAsyncService {
    /**
     * 根据模版异步的创建优惠卷码
     * @param template {@link CouponTemplate}优惠卷模版实体
     */
    void asyncConstructCouponByTemplate(CouponTemplate template);
}
