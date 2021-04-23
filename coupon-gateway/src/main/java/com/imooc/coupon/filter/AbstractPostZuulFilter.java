package com.imooc.coupon.filter;

import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

/**
 * @Annotation 自定义后置过滤器抽象类
 * @ClassName AbstractPostZuulFilter
 * @Author ChenWenJie
 * @Data 2020/5/10 11:08 上午
 * @Version 1.0
 **/
public abstract class AbstractPostZuulFilter extends AbstractZuulFilter{
    @Override
    public String filterType() {
        return FilterConstants.POST_TYPE;
    }
}
