package com.imooc.coupon.service;

import com.imooc.coupon.entity.CouponTemplate;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.vo.CouponTemplateSDK;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 优惠卷模块基础（view，delete。。。）服务定义
 */
public interface ITemplateBaseService {
    /**
     * 根据优惠卷模版 id 获取优惠卷模版信息
     * @param id 模版 id
     * @return {@link CouponTemplate} 优惠卷模版实体
     * @throws CouponException
     */
    CouponTemplate buildTemplateInfo(Integer id)throws CouponException;

    /**
     * 查找所有可用的优惠卷模版
     * @return {@link CouponTemplateSDK}s
     */
    List<CouponTemplateSDK> findAllUsableTmplate();

    /**
     * 获取模版 ids 到 CouponTemplateSDK 到影射
     * @param ids 模版 ids
     * @return Map<key:模版 id，value: CouponTemplateSDK>
     */
    Map<Integer,CouponTemplateSDK> findIds2TemplateSDK(Collection<Integer> ids);
}
