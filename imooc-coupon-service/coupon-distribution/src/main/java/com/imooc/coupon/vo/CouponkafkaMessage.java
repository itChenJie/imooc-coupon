package com.imooc.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Annotation
 * @ClassName CouponkafkaMessage
 * @Author ChenWenJie
 * @Data 2020/5/17 5:00 下午
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponkafkaMessage {
    /** 优惠卷状态*/
    private Integer status;
    /** coupon 主键*/
    private List<Integer> ids;

}
