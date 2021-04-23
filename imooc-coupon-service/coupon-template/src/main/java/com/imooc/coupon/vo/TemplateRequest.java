package com.imooc.coupon.vo;

import com.imooc.coupon.constant.CouponCategory;
import com.imooc.coupon.constant.DistributeTarget;
import com.imooc.coupon.constant.ProductLine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @Annotation 优惠卷创建请求对象
 * @ClassName TemplateRequest
 * @Author ChenWenJie
 * @Data 2020/5/15 10:40 上午
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateRequest {
    /** 优惠卷 logo*/
    private String logo;
    /** 优惠卷名称*/
    private String name;
    /** 优惠卷描述*/
    private String desc;

    /** 优惠卷分类*/
    private String category;

    /** 产品线*/
    private Integer productLine;

    /** 总数*/
    private Integer count;

    /** 创建用户*/
    private Long userId;

    /** 目标用户*/
    private Integer target;

    /** 优惠卷规则*/
    private TemplateRule rule;

    /**
     * 校验对象的合法性
     * @return
     */
    public boolean validate(){

        boolean stringValid = StringUtils.isNoneEmpty(name)
                && StringUtils.isNotEmpty(logo)
                && StringUtils.isNotEmpty(desc);
        //判断枚举值是否存在
        boolean enumValid = null != CouponCategory.of(category)
                && null != ProductLine.of(productLine)
                && null != DistributeTarget.of(target);
        boolean numValid = count>0 && userId>0;

        return stringValid && enumValid && numValid && rule.validate();
    }
}
