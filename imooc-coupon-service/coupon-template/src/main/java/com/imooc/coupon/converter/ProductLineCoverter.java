package com.imooc.coupon.converter;

import com.imooc.coupon.constant.ProductLine;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * @Annotation
 * @ClassName ProductLineCoverter
 * @Author ChenWenJie
 * @Data 2020/5/14 5:57 下午
 * @Version 1.0
 **/
@Converter
public class ProductLineCoverter implements AttributeConverter<ProductLine,Integer> {
    @Override
    public Integer convertToDatabaseColumn(ProductLine productLine) {
        return productLine.getCode();
    }

    @Override
    public ProductLine convertToEntityAttribute(Integer code) {
        return ProductLine.of(code);
    }
}
