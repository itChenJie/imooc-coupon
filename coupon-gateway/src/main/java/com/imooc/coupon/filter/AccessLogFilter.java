package com.imooc.coupon.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @Annotation 访问日志过滤器
 * @ClassName AccessLogFilter
 * @Author ChenWenJie
 * @Data 2020/5/10 4:03 下午
 * @Version 1.0
 **/
@Slf4j
@Component
public class AccessLogFilter extends AbstractPreZuulFilter{
    @Override
    protected Object cRun() {
        HttpServletRequest request = context.getRequest();
        Long startTime= (Long)context.get("startTime");
        String uri = request.getRequestURI();
        Long duration=System.currentTimeMillis() - startTime;
        //从网关通过到请求都会打印日志记录
        log.info("uri:{},duration:{}",uri,duration);

        return success();
    }

    @Override
    public int filterOrder() {
        //发送响应过滤器命令最后到过滤器级别
        return FilterConstants.SEND_RESPONSE_FILTER_ORDER -1;
    }
}
