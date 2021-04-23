package com.imooc.coupon.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Annotation 结算信息对象定义
 * 包含
 * 1：userid
 * 2：商品信息
 * 3：优惠卷列表
 * 4：结算结果金额
 * @ClassName SettlementInfo
 * @Author ChenWenJie
 * @Data 2020/5/16 10:40 下午
 * @Version 1.0
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettlementInfo {
    /** 用户id*/
    private Long userId;
    /** 商品信息*/
    private List<GoodsInfo> goodsInfos;
    /** 优惠卷列表*/
    private List<CouponAndtemplateInfo> couponAndtemplateInfos;
    /** 结果结算金额*/
    private Double cost;
    /** 是否使结算生效，即核销*/
    private Boolean employ;
    /**
     * 优惠卷和模板信息
     * */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CouponAndtemplateInfo{
        /** Coupon 的主键*/
        private Integer id;
        /** 优惠卷对应的模板对象*/
        private CouponTemplateSDK template;
    }
}
