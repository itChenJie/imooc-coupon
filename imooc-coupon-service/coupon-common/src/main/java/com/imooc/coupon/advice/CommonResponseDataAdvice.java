package com.imooc.coupon.advice;

import com.imooc.coupon.annotation.IgnoreResponseAdvice;
import com.imooc.coupon.vo.CommonResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @Annotation 统一响应
 * RestControllerAdvice 对 RestController注解做增强处理
 * ResponseBodyAdvice 请求响应body增强接口
 * @ClassName CommonResponseDataAdvice
 * @Author ChenWenJie
 * @Data 2020/5/10 5:09 下午
 * @Version 1.0
 **/
@RestControllerAdvice
public class CommonResponseDataAdvice implements ResponseBodyAdvice<Object> {
    /**
     *判断是否要对响应进行处理
     * @param methodParameter
     * @param aClass
     * @return
     */
    @Override
    @SuppressWarnings("all")
    public boolean supports(MethodParameter methodParameter,
                            Class<? extends HttpMessageConverter<?>> aClass) {
        //如果当前所在的类标识类IgnoreResponseAdvice注解，不需要处理
        if (methodParameter.getDeclaringClass().isAnnotationPresent(
                IgnoreResponseAdvice.class
        )){
            return false;
        }
        //如果当前所在的方法，标识了IgnoreResponseAdvice注解，不需要处理
        if (methodParameter.getMethod().isAnnotationPresent(
                IgnoreResponseAdvice.class
        )){
            return false;
        }
        //对响应进行处理，执行beforeBodyWrite方法
        return true;
    }

    /**
     * 响应返回之前的处理
     * @return
     */
    @Override
    @SuppressWarnings("all")
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType,
                                  Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest,
                                  ServerHttpResponse serverHttpResponse) {
        //定义最终的返回对象
        CommonResponse<Object> commonResponse = new CommonResponse<>(0, "");
        //如果 0 是null ，response不需要设置data
        if (null == o){
            return commonResponse;
            //如果    o 已经是 CommonResponse，不需要再次处理了
        }else if(o instanceof CommonResponse){
            commonResponse = (CommonResponse<Object>) o;
            //否则，把响应对象作为CommonResponse 的data部分
        }else {
            commonResponse.setDate(o);
        }
        return commonResponse;
    }
}
