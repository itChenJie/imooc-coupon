package com.imooc.coupon.converter;

import com.imooc.coupon.constant.CouponCategory;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * @Annotation 优惠卷分类枚举属性转换器
 * @ClassName CouponCategoryCoverter
 * @Author ChenWenJie
 * @Data 2020/5/14 5:39 下午
 * @Version 1.0
 **/
@Converter
public class CouponCategoryCoverter implements AttributeConverter<CouponCategory,String> {
    /**
     * 将实体属性X转换为Y存储到数据库中，插入和更新时执行的动作
     * */
    @Override
    public String convertToDatabaseColumn(CouponCategory couponCategory) {
        return couponCategory.getCode();
    }

    /**
     * 将数据库中的字段Y转换为实体属性X，查询操作执行的动作
     * */
    @Override
    public CouponCategory convertToEntityAttribute(String code) {
        return CouponCategory.of(code);
    }
}
