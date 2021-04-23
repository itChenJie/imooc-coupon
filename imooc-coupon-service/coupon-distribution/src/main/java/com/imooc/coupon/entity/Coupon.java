package com.imooc.coupon.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.imooc.coupon.constant.CouponStatus;
import com.imooc.coupon.converter.CouponStatusConverter;
import com.imooc.coupon.serialization.CouponSerialize;
import com.imooc.coupon.vo.CouponTemplateSDK;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * @Annotation 优惠卷(用户领取的优惠卷记录)实体表
 * @ClassName Coupon
 * @Author ChenWenJie
 * @Data 2020/5/16 6:37 下午
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name ="coupon")
@JsonSerialize(using = CouponSerialize.class)
public class Coupon {
    /** 自增主键*/
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id",nullable = false)
    private Integer id;
    /** 关联优惠卷模板的主键(逻辑外键)*/
    @Column(name = "template_id",nullable = false)
    private Integer templateId;

    /** 领取用户id*/
    @Column(name = "user_id",nullable = false)
    private Long userId;
    /** 优惠卷码*/
    @Column(name = "coupon_code",nullable = false)
    private String couponCode;
    /** 领取时间*/
    @CreatedDate
    @Column(name = "assign_time",nullable = false)
    private Date assignTime;

    /** 优惠卷状态*/
    @Column(name = "status",nullable = false)
    @Convert(converter = CouponStatusConverter.class)
    private CouponStatus status;
    /** 用户优惠卷对应的模版信息*/
    @Transient
    private CouponTemplateSDK templateSDK;

    /**
     * 返回一个无效的 Coupon 对象
     * @return
     */
    public static Coupon invalidCoupon(){
        Coupon coupon = new Coupon();
        coupon.setId(-1);
        return coupon;
    }

    /**
     * 构造优惠卷
     */
    public Coupon(Integer templateId,Long userId,String couponCode,CouponStatus status){
        this.templateId = templateId;
        this.userId = userId;
        this.couponCode = couponCode;
        this.status = status;
    }
}
