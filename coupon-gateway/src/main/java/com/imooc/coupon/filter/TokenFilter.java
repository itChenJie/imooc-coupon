package com.imooc.coupon.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * @Annotation 判断token是否存在过滤器
 * @ClassName TokenFilter
 * @Author ChenWenJie
 * @Data 2020/5/10 3:22 下午
 * @Version 1.0
 **/
@Slf4j
@Component
public class TokenFilter extends AbstractPreZuulFilter{
    @Override
    protected Object cRun() {
        HttpServletRequest request = context.getRequest();
        log.info(String.format("%s request to%s",request.getMethod())
                ,request.getRequestURL().toString());
        String token = request.getParameter("token");
        if (token == null){
            log.error("error: token is empty");
            return fail(401,"error:token is empty");
        }
        return success();
    }

    /**
     * 优先级
     * @return
     */
    @Override
    public int filterOrder() {
        return 1;
    }
}
