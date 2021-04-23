package com.imooc.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * @Annotation 网关应用启动入口
 * EnableZuulProxy 标识当前应用是zuul Server
 * SpringCloudApplication 组合了 SpringApplication EnableDiscoveryClient EnableCircuitBreaker
 *  @EnableEurekaClient上有@EnableDiscoveryClient注解，可以说基本就是EnableEurekaClient有@EnableDiscoveryClient的功能，
 *  另外上面的注释中提到，其实**@EnableEurekaClient**z注解就是一种方便使用eureka的注解而已，可以说使用其他的注册中心后，
 *  都可以使用@EnableDiscoveryClient注解，但是使用@EnableEurekaClient的情景，就是在服务采用eureka作为注册中心的时候，使用场景较为单一
 *  @EnableCircuitBreaker注解之后，就可以使用断路器功能
 * @ClassName ZuulGatewayApplication
 * @Author ChenWenJie
 * @Data 2020/5/10 10:16 上午
 * @Version 1.0
 **/
@EnableZuulProxy
@SpringCloudApplication
public class ZuulGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ZuulGatewayApplication.class,args);
    }
}
