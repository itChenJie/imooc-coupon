package com.imooc.coupon.serialization;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.imooc.coupon.entity.Coupon;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * @Annotation 自定义系列化器
 * @ClassName CouponSerialize
 * @Author ChenWenJie
 * @Data 2020/5/16 9:40 下午
 * @Version 1.0
 **/
public class CouponSerialize extends JsonSerializer<Coupon> {

    @Override
    public void serialize(Coupon coupon, JsonGenerator gen,
                          SerializerProvider serializers) throws IOException {
        //开始序列化
        gen.writeStartObject();
        gen.writeStringField("id",coupon.getId().toString());
        gen.writeStringField("templateId",coupon.getTemplateId().toString());
        gen.writeStringField("userId",coupon.getUserId().toString());
        gen.writeStringField("couponCode",coupon.getCouponCode());
        gen.writeStringField("assignTime",
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                        .format(coupon.getAssignTime()));
        gen.writeStringField("name",coupon.getTemplateSDK().getName());
        gen.writeStringField("logo",coupon.getTemplateSDK().getLogo());
        gen.writeStringField("desc",coupon.getTemplateSDK().getDesc());
        gen.writeStringField("expiration",
                JSON.toJSONString(coupon.getTemplateSDK().getRule().getEXpiration()));
        gen.writeStringField("usage",
                JSON.toJSONString(coupon.getTemplateSDK().getRule().getUsage()));
        gen.writeEndObject();

    }
}
