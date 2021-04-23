package com.imooc.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Annotation 通用响应对象都要
 * @ClassName CommonResponse
 * @Author ChenWenJie
 * @Data 2020/5/10 4:57 下午
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse<T> implements Serializable {
    private Integer code;
    private String message;
    private T date;

    public CommonResponse(Integer code,String message){
        this.code = code;
        this.message = message;
    }
}
