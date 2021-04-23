package com.imooc.coupon.constant;

/**
 * @Annotation 常用常量定义
 * @ClassName Constant
 * @Author ChenWenJie
 * @Data 2020/5/15 4:55 下午
 * @Version 1.0
 **/
public class Constant {
    public static final String TOPIC ="imooc_user_coupon_op";

    /**
     * Redis Key 前缀定义
     */
    public static class RedisPrefix{
        /** 优惠卷码 key前缀*/
        public static final  String COUPON_TEMPLATE=
                "imooc_coupon_template_code_";
        /** 用户当前所有已使用的优惠卷 key 前缀*/
        public static final String USER_COUPON_USABLE =
                "imooc_user_coupon_usable_";
        /** 使用的用户优惠 key 前缀*/
        public static final String USER_COUPON_USED ="imooc_user_coupon_used";
        /** 用户当前所有已过期的优惠卷 key 前缀*/
        public static final String USER_COUPON_EXPIRED=
                "imooc_user_coupon_expired_";
    }
}
