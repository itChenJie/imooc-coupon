package com.imooc.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Annotation 获取优惠卷请求对象定义
 * @ClassName AcquireTemplateRequest
 * @Author ChenWenJie
 * @Data 2020/5/16 10:20 下午
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AcquireTemplateRequest {
    /**用户 id*/
    private Long userId;
    /**优惠卷模版信息 */
    private CouponTemplateSDK templateSDK;
}
