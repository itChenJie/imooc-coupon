package com.imooc.coupon.filter;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

/**
 * @Annotation 自定义前置过滤器抽象类
 * @ClassName AbstractPreZuulFilter
 * @Author ChenWenJie
 * @Data 2020/5/10 11:06 上午
 * @Version 1.0
 **/
public abstract class AbstractPreZuulFilter extends AbstractZuulFilter{
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }
}
