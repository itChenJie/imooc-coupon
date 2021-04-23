package com.imooc.coupon.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Annotation 设置请求访问时间
 * @ClassName PreRequestFilter
 * @Author ChenWenJie
 * @Data 2020/5/10 4:01 下午
 * @Version 1.0
 **/
@Slf4j
@Component
public class PreRequestFilter extends AbstractPreZuulFilter{
    @Override
    protected Object cRun() {
        context.set("startTime",System.currentTimeMillis());
        return success();
    }

    @Override
    public int filterOrder() {
        return 0;
    }
}
