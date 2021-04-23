package com.imooc.coupon.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.imooc.coupon.constant.CouponCategory;
import com.imooc.coupon.constant.DistributeTarget;
import com.imooc.coupon.constant.ProductLine;
import com.imooc.coupon.converter.CouponCategoryCoverter;
import com.imooc.coupon.converter.DistributeTargetCoverter;
import com.imooc.coupon.converter.ProductLineCoverter;
import com.imooc.coupon.converter.RuleCoverter;
import com.imooc.coupon.serialization.CouponTemplateSerialize;
import com.imooc.coupon.vo.TemplateRule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Annotation 优惠卷模版实体类定义：基础属性 +规则属性
 * 实体类上添加 @EntityListeners(AuditingEntityListener.class)
 *
 * 在需要的字段上加上 @CreatedDate、@CreatedBy、@LastModifiedDate、@LastModifiedBy 等注解。
 *
 * 在Xxx Application 启动类上添加 @EnableJpaAuditing
 *
 * 实现 AuditorAware 接口来返回你需要插入的值。重点！
 * @ClassName CouponTemplate
 * @Author ChenWenJie
 * @Data 2020/5/14 11:26 上午
 * @Version 1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "coupon_template")
//自定义系列化器
@JsonSerialize(using = CouponTemplateSerialize.class)
public class CouponTemplate implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id" ,nullable = false)
    private Integer id;

    /** 是否是可用状态*/
    @Column(name = "available",nullable = false)
    private Boolean available;

    /** 是否过期*/
    @Column(name = "expired",nullable = false)
    private Boolean expired;

    /** 优惠卷名称*/
    @Column(name = "name",nullable = false)
    private String name;

    /** 优惠卷logo*/
    @Column(name = "logo",nullable = false)
    private String logo;

    /** 优惠卷描述*/
    @Column(name = "description",nullable = false)
    private String desc;

    /** 优惠卷分类*/
    @Column(name = "category",nullable = false)
    @Convert(converter = CouponCategoryCoverter.class)
    private CouponCategory category;
    /** 产品线*/
    @Column(name = "product_line",nullable = false)
    @Convert(converter = ProductLineCoverter.class)
    private ProductLine productLine;
    /** 总数*/
    @Column(name = "coupon_count",nullable = false)
    private Integer count;

    /** 创建时间*/
    @CreatedDate
    @Column(name="create_time",nullable = false)
    private Date createtime;
    /** 创建用户*/
    @Column(name = "user_id",nullable = false)
    private Long userId;
    /** 优惠卷模版的编码*/
    @Column(name = "template_key",nullable = false)
    private String key;
    /** 目标用户*/
    @Column(name = "target" ,nullable = false)
    @Convert(converter = DistributeTargetCoverter.class)
    private DistributeTarget target;
    /** 优惠卷规则*/
    @Column(name = "rule" ,nullable = false)
    @Convert(converter = RuleCoverter.class)
    private TemplateRule rule;

    public CouponTemplate(String name,String logo,String desc,String category,Integer productLine
                            ,Integer count,Long userId,Integer target,TemplateRule rule){
        this.available = false;
        this.expired = false;
        this.name = name;
        this.logo = logo;
        this.desc = desc;
        this.category = CouponCategory.of(category);
        this.productLine = ProductLine.of(productLine);
        this.count = count;
        this.userId = userId;
        //优惠卷唯一编码 = 4(产品线和类型) + 8(日期：20190101)+ id(扩充为4位)
        this.key = productLine.toString() + category+
                new SimpleDateFormat("yyyyMMdd").format(new Date());
        this.target = DistributeTarget.of(target);
        this.rule = rule;
    }
}
