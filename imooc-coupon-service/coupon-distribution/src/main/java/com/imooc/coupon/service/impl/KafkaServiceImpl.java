package com.imooc.coupon.service.impl;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.constant.Constant;
import com.imooc.coupon.constant.CouponStatus;
import com.imooc.coupon.dao.CouponDao;
import com.imooc.coupon.entity.Coupon;
import com.imooc.coupon.service.IKafkaService;
import com.imooc.coupon.vo.CouponkafkaMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Vector;

/**
 * @Annotation kafka 相关的服务接口实现
 * 核心思想；是将Cache 中的coupon 的状态变化同步机制
 * @ClassName KafkaServiceImpl
 * @Author ChenWenJie
 * @Data 2020/5/17 4:54 下午
 * @Version 1.0
 **/
@Slf4j
@Service
public class KafkaServiceImpl implements IKafkaService {
    @Autowired
    private CouponDao couponDao;
    /**
     * 消费优惠卷 kafka信息
     * @param record
     * 本系统是为了方便医院的科研项目申报与经费管理等
     */
    @Override
    @KafkaListener(topics = {Constant.TOPIC},groupId = "imooc-coupon-1")
    public void consumeCouponKafakaMessage(ConsumerRecord<?, ?> record) {
        Optional<?> kafkaMessage = Optional.ofNullable(record.value());
        if (kafkaMessage.isPresent()){
            Object message = kafkaMessage.get();
            CouponkafkaMessage couponInfo = JSON.parseObject(
                    message.toString(),CouponkafkaMessage.class);
            log.info("Receive CouponkafkaMessage:{}",message.toString());
            CouponStatus status = CouponStatus.of(couponInfo.getStatus());
            switch (status){
                case USABLE:
                    break;
                case USED:
                    processUsedCoupons(couponInfo,status);
                    break;
                case EXPIRED:
                    processExpiredCoupons(couponInfo,status);
                    break;
            }
        }
    }

    /**
     * 处理已使用的用户优惠卷
     * @param kafkaMessage
     * @param status
     */
    private void processUsedCoupons(CouponkafkaMessage kafkaMessage,CouponStatus status){
        //TODO 给用户发送短信
        processCouponByStatus(kafkaMessage, status);
    }
    /**
     * 处理过期的用户优惠卷
     * @param kafkaMessage
     * @param status
     */
    private void processExpiredCoupons(CouponkafkaMessage kafkaMessage,CouponStatus status){
       //Todo 给用户发送推送
        processCouponByStatus(kafkaMessage, status);
    }
    /**
     * 根据状态处理优惠卷信息
     * @param kafkaMessage
     * @param status
     */
    private void processCouponByStatus(CouponkafkaMessage kafkaMessage,
                                       CouponStatus status){
        List<Coupon> coupons =  couponDao.findAllById(kafkaMessage.getIds());
        if (CollectionUtils.isEmpty(coupons)
                || coupons.size()!= kafkaMessage.getIds().size()){
            log.error("can not find right coupon info:{}",JSON.toJSONString(kafkaMessage));
            //发送邮件
            return;
        }
        coupons.forEach(c->c.setStatus(status));
        log.info("CouponKafkamessage Op Coupon Count:{}" ,
                couponDao.saveAll(coupons).size());
    }

}
