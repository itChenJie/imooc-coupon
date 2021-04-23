package com.imooc.coupon.executor.impl;

import com.imooc.coupon.constant.RuleFlag;
import com.imooc.coupon.executor.AbstractExecutor;
import com.imooc.coupon.executor.RuleExecutor;
import com.imooc.coupon.vo.CouponTemplateSDK;
import com.imooc.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;

/**
 * @Annotation 满减优惠卷结算规则执行器
 * @ClassName ManJianExecutor
 * @Author ChenWenJie
 * @Data 2020/5/27 10:01 上午
 * @Version 1.0
 **/
@Slf4j
@Component
public class ManJianExecutor extends AbstractExecutor implements RuleExecutor {
    /**
     * 规则类型标记
     *
     * @return
     */
    @Override
    public RuleFlag ruleFlag() {
        return RuleFlag.MANJIAN;
    }

    /**
     * 优惠卷规则的计算
     * @param settlement 包含选择的优惠卷
     * @return 修正过的结算信息
     */
    @Override
    public SettlementInfo computeRule(SettlementInfo settlement) {
        double goodsSum = retain2Decimals(
                goodsCostSum((settlement.getGoodsInfos())));
        SettlementInfo probability = processGoodsTypeNotSatisfy(
                settlement, goodsSum);
        if (null !=probability){
            log.debug("ManJian Template Is Not Match To GoodsType!");
            return probability;
        }
        //判断满减是否符合折扣标准
        CouponTemplateSDK templateSDK = settlement.getCouponAndtemplateInfos()
                .get(0).getTemplate();
        double base = templateSDK.getRule().getDiscount().getBase();
        double quota = templateSDK.getRule().getDiscount().getQuata();
        //如果不符合标准，直接返回商品总价
        if (goodsSum<base){
            log.debug("Current Goods Cost Sum < ManJian Coupon Base!");
            settlement.setCost(goodsSum);
            settlement.setCouponAndtemplateInfos(Collections.emptyList());
            return settlement;
        }
        //计算使用优惠卷之后的价格
        settlement.setCost(retain2Decimals(
                (goodsSum - quota)>minCost()?(goodsSum -quota):minCost()
        ));
        log.debug("Use ManJian Coupon make Goods Cost From {} To {}",goodsSum,settlement.getCost());
        return settlement;
    }




}
