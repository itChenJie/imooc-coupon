package com.imooc.coupon.service;

import com.imooc.coupon.entity.CouponTemplate;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.vo.TemplateRequest;

/**
 * 构建优惠卷模版接口定义
 */
public interface IBuildTemplateService {
    /**
     * 创建优惠卷模版
     * @param request {@link TemplateRequest} 模版信息请求对象
     * @return {@link CouponTemplate} 优惠卷模版实体
     * @throws CouponException
     */
    CouponTemplate buildTemplate(TemplateRequest request)throws CouponException;
}
