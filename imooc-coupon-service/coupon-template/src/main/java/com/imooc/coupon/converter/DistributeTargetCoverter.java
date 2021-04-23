package com.imooc.coupon.converter;

import com.imooc.coupon.constant.DistributeTarget;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * @Annotation 分发目标枚举属性转换器
 * @ClassName DistributeTargetCoverter
 * @Author ChenWenJie
 * @Data 2020/5/14 6:08 下午
 * @Version 1.0
 **/
@Converter
public class DistributeTargetCoverter implements AttributeConverter<DistributeTarget,Integer> {
    @Override
    public Integer convertToDatabaseColumn(DistributeTarget distributeTarget) {
        return distributeTarget.getCode();
    }

    @Override
    public DistributeTarget convertToEntityAttribute(Integer code) {
        return DistributeTarget.of(code);
    }
}
