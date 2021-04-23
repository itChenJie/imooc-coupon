package com.imooc.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * @Annotation 枚举
 * @Author ChenWenJie
 * @Data 2020/5/19 8:43 下午
 */
@Getter
@AllArgsConstructor
public enum RuleFlag {
    // 单类别优惠卷定义
    MANJIAN("满减卷的计算规则"),
    ZHEKOU("折扣卷的计算规则"),
    LIJIAN("立减卷的计算规则"),

    //多类别优惠卷定义
    MANJIAN_ZHEKOW("满减卷+折扣卷的计算规则");

    //TODO 更多优惠卷类别的组合

    /**
     * 描述
     */
    private String description;

}
