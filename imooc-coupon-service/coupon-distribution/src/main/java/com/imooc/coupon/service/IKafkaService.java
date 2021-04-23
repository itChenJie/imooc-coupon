package com.imooc.coupon.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * @Annotation kafka相关的服务接口定义
 * @Author ChenWenJie
 * @Data 2020/5/16 10:10 下午
 **/
public interface IKafkaService {
    /**
     * 消费优惠卷 kafka信息
     * @param record
     */
    void consumeCouponKafakaMessage(ConsumerRecord<?,?> record);
}
