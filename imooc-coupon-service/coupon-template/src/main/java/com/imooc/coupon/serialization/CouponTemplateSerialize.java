package com.imooc.coupon.serialization;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.imooc.coupon.entity.CouponTemplate;

import java.io.IOException;
import java.text.SimpleDateFormat;

/**
 * @Annotation 优惠卷模块实体类自定义系列化器
 * @ClassName CouponTemplateSerialize
 * @Author ChenWenJie
 * @Data 2020/5/14 6:34 下午
 * @Version 1.0
 **/
public class CouponTemplateSerialize extends JsonSerializer<CouponTemplate> {
    @Override
    public void serialize(CouponTemplate template, JsonGenerator gen,
                          SerializerProvider serializers) throws IOException {
        //开始系列化对象
        gen.writeStartObject();
        gen.writeStringField("id",template.getId().toString());
        gen.writeStringField("name",template.getName());
        gen.writeStringField("logo",template.getLogo());
        gen.writeStringField("desc",template.getDesc());
        gen.writeStringField("category",template.getCategory().getDescription());
        gen.writeStringField("productline",template.getProductLine().getDescription());
        gen.writeStringField("count",template.getCount().toString());
        gen.writeStringField("createTime",
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(template.getCreatetime()));
        gen.writeStringField("userId",template.getUserId().toString());
        gen.writeStringField("key",
                template.getKey()+String.format("%04d",template.getId()));
        gen.writeStringField("target",template.getTarget().getDescription());
        gen.writeStringField("rule", JSON.toJSONString(template.getRule()));
        //结束系列化对象
        gen.writeEndObject();
    }
}
