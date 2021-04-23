package com.imooc.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Annotation 模版微服务的启动入口
 * EnableJpaAuditing 审计功能 自动填充或更新实体中的 CreateDate、CreatedBy
 * @ClassName TemplateApplication
 * @Author ChenWenJie
 * @Data 2020/5/13 5:18 下午
 * @Version 1.0
 **/
@EnableScheduling
@EnableJpaAuditing
@EnableEurekaClient
@SpringBootApplication
public class TemplateApplication {
    public static void main(String[] args) {
        SpringApplication.run(TemplateApplication.class,args);
    }
}
