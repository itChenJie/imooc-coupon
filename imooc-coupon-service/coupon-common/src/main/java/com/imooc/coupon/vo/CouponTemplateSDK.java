package com.imooc.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Annotation 微服务之间用的优惠卷模版信息定义
 * @ClassName CouponTemplateSDK
 * @Author ChenWenJie
 * @Data 2020/5/15 11:36 上午
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponTemplateSDK {
    /** 优惠卷模版主键*/
    private Integer id;
    /** 优惠卷模版名称*/
    private String name;
    /** 优惠卷 logo*/
    private String logo;
    /** 优惠卷描述*/
    private String desc;
    /** 优惠卷分类*/
    private String category;
    /**产品线*/
    private Integer productLine;
    /** 优惠卷模版的编码*/
    private String key;
    /** 目标用户*/
    private Integer target;
    /** 优惠卷规则*/
    private TemplateRule rule;
}
