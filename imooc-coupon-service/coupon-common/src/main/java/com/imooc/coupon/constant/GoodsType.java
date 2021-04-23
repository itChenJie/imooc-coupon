package com.imooc.coupon.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.stream.Stream;

/**
 * @Annotation 商品类型枚举
 * @Author ChenWenJie
 * @Data 2020/5/16 10:26 下午
 */
@Getter
@AllArgsConstructor
public enum GoodsType {
    /**
     * 文娱
     */
    WENYU("文娱",1),
    SHEXIAN("生鲜",2),
    JIAJU("家居",3),
    OTHERS("其他",4),
    ALL("全品类",5);

    /** 商品类型描述*/
    private String description;
    /** 商品类型编码*/
    private Integer code;

    public static GoodsType of(Integer code){
        Objects.requireNonNull(code);
        return Stream.of(values())
                .filter(bean -> bean.code.equals(code))
                .findAny()
                .orElseThrow(()-> new IllegalArgumentException(code +" not exists"));
    }
}
