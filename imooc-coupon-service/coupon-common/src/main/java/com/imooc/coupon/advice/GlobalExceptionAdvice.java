package com.imooc.coupon.advice;

import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.vo.CommonResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * @Annotation 全局异常处理
 * @ClassName GlobalExceptionAdvice
 * @Author ChenWenJie
 * @Data 2020/5/10 5:56 下午
 * @Version 1.0
 **/
@RestControllerAdvice
public class GlobalExceptionAdvice {

    /**
     * 对CouponException异常进行统一处理
     * @param req
     * @param couponException
     * @return
     */
    @ExceptionHandler(value = CouponException.class)
    public CommonResponse<String> handlerCouponException(HttpServletRequest req,
                                                         CouponException couponException){
        CommonResponse<String> response = new CommonResponse<>(-1, "business error");
        response.setDate(couponException.getMessage());
        return response;
    }
}
