package com.imooc.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Annotation fake 商品信息
 * @ClassName GoodsInfo
 * @Author ChenWenJie
 * @Data 2020/5/16 10:37 下午
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsInfo {
    /** 商品类型：{@link com.imooc.coupon.constant.GoodsType}*/
    private Integer type;

    /** 商品价格*/
    private Double price;
    /** 商品数量*/
    private Integer count;

    //TODO 名称，使用信息
}
