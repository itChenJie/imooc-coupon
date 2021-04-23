package com.imooc.coupon.vo;

import com.imooc.coupon.constant.PeriodType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @Annotation 优惠卷规则对象定义
 * @ClassName TemplateRule
 * @Author ChenWenJie
 * @Data 2020/5/14 10:26 上午
 * @Version 1.0
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateRule {
    /** 优惠卷过期规则*/
    private EXpiration eXpiration;

    /** 折扣*/
    private Discount discount;

    /** 每个人最多领几张的限制*/
    private Integer limitation;

    /** 使用范围：地域 + 商品类型*/
    private Usage usage;
    /** 权重(可以和哪些优惠卷叠加使用，同一类型的优惠卷一定不能叠加：list【】，优惠卷的唯一编码)*/
    private String weight;

    /**
     * 校验功能
     * @return
     */
    public boolean validate(){
        return eXpiration.validate() && discount.validate()
                && limitation >0 && usage.validate()
                && StringUtils.isNotEmpty(weight);
    }
    /**
     * 有效期限规则
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class EXpiration{
        /** 有效期规则，对应PeriodType 的code*/
        private Integer period;

        /** 有效间隔：只对变动性有效期有效*/
        private Integer gap;

        /** 优惠卷模版的失效日期，两类规则都有效*/
        private Long deadline;

        boolean validate(){
            return null != PeriodType.of(period) && gap>0&&deadline>0;
        }
    }

    /**
     * 折扣需要与类型配合决定
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Discount{

        /** 额度：满减(20),折扣(85),立减(10)*/
        private Integer quata;

        /** 基准，需要满多少才可用 满减才可以*/
        private Integer base;

        boolean validate(){
            return quata>0 && base>0;
        }
    }

    /**
     * 使用范围
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Usage{
        /** 省份*/
        private String province;
        /** 城市*/
        private String city;
        /** 商品类型 list【文娱，生鲜，家居，全品类】*/
        private String goodsType;
        boolean validate(){
            return StringUtils.isNotEmpty(province)
                    &&StringUtils.isNotEmpty(city)
                    &&StringUtils.isNotEmpty(goodsType);
        }

    }
}
