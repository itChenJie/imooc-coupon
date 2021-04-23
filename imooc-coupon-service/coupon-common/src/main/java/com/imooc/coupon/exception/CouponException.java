package com.imooc.coupon.exception;

/**
 * @Annotation 优惠卷项目通用异常定义
 * @ClassName CouponException
 * @Author ChenWenJie
 * @Data 2020/5/10 5:49 下午
 * @Version 1.0
 **/
public class CouponException extends Exception{
    public CouponException(String message){
        super(message);
    }

}
