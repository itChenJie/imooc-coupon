package com.imooc.coupon.converter;

import com.imooc.coupon.constant.CouponStatus;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * @Annotation 用户优惠卷的状态枚举属性转换器
 * @ClassName CouponStatusConverter
 * @Author ChenWenJie
 * @Data 2020/5/16 6:52 下午
 * @Version 1.0
 **/
@Converter
public class CouponStatusConverter implements AttributeConverter<CouponStatus,Integer> {
    /**
     * 将实体属性X转换为Y存储到数据库中，插入和更新时执行的动作
     * */
    @Override
    public Integer convertToDatabaseColumn(CouponStatus status) {
        return status.getCode();
    }

    /**
     * 将数据库中的字段Y转换为实体属性X，查询操作执行的动作
     * */
    @Override
    public CouponStatus convertToEntityAttribute(Integer integer) {
        return CouponStatus.of(integer);
    }
}
