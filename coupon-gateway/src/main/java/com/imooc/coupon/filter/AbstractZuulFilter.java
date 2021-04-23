package com.imooc.coupon.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;

/**
 * @Annotation 通用的抽象过滤类
 * @ClassName AbstractZuulFilter
 * @Author ChenWenJie
 * @Data 2020/5/10 10:49 上午
 * @Version 1.0
 **/
public abstract class AbstractZuulFilter extends ZuulFilter{
    /**
     * 用于在过滤器之间传统消息，数据保存在每个请求的 ThreadLocal中
     * 扩展了 ConcurrentHashMap
     */
    RequestContext context;
    private final static String NEXT="next";

    /**
     * 此方法的“ true”返回意味着应该调用run（）方法
     * @return true如果应该调用run（）方法。 false不会调用run（）方法
     */
    @Override
    public boolean shouldFilter() {
        RequestContext requestContext = RequestContext.getCurrentContext();

        return (boolean) requestContext.getOrDefault(NEXT,true);
    }

    @Override
    public Object run() throws ZuulException {
        context = RequestContext.getCurrentContext();

        return cRun();
    }

    protected abstract Object cRun();

    /**
     * 公用失败报文设置
     * @param code
     * @param msg
     * @return
     */
    Object fail(int code,String msg){
        context.set(NEXT,false);
        context.setSendZuulResponse(false);
        context.getResponse().setContentType("text/html;charset=UTF-8");
        context.setResponseStatusCode(code);
        context.setResponseBody(String.format("{\"result\":\"%s!\"}",msg));
        return null;
    }

    Object success(){
        context.set(NEXT,true);
        return null;
    }
}
