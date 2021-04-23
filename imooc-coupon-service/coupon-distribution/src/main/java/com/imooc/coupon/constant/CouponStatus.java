package com.imooc.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * @Annotation 用户优惠卷的状态
 * @ClassName CouponStatus
 * @Author ChenWenJie
 * @Data 2020/5/16 6:27 下午
 * @Version 1.0
 **/
@Getter
@AllArgsConstructor
public enum  CouponStatus {
    USABLE("可用的",1),
    USED("已使用的",2),
    EXPIRED("过期的(未被使用的)",3);

    /** 优惠卷状态描述信息*/
    private String description;

    /** 优惠卷状态编码*/
    private Integer code;
    public static CouponStatus of(Integer code){
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(bean ->bean.code.equals(code))
                .findAny()
                .orElseThrow(
                        ()-> new IllegalArgumentException(code +"  not exists")
                );
    }
}
