package com.imooc.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * 优惠卷分类
 */
@Getter
@AllArgsConstructor
public enum CouponCategory {
    MANJIAN("满减卷","001"),
    ZHEKOU("折扣卷","002"),
    LIJIAN("立减卷","003");

    /** 优惠卷描述(分类)*/
    private String description;
    /** 优惠卷分类编码*/
    private String code;

    public static CouponCategory of(String code){
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(()->new IllegalArgumentException(code +" not exists"));
    }

}