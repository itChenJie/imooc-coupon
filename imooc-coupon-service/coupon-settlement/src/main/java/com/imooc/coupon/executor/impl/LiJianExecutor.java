package com.imooc.coupon.executor.impl;

import com.imooc.coupon.constant.RuleFlag;
import com.imooc.coupon.executor.AbstractExecutor;
import com.imooc.coupon.executor.RuleExecutor;
import com.imooc.coupon.vo.CouponTemplateSDK;
import com.imooc.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Annotation 立减优惠卷结算规则执行器
 * @ClassName LiJian
 * @Author ChenWenJie
 * @Data 2020/5/27 4:40 下午
 * @Version 1.0
 **/
@Slf4j
@Component
public class LiJianExecutor extends AbstractExecutor implements RuleExecutor {
    /**
     * 规则类型标记
     *
     * @return
     */
    @Override
    public RuleFlag ruleFlag() {
        return RuleFlag.LIJIAN;
    }

    /**
     * 优惠卷规则的计算
     *
     * @param settlement 包含选择的优惠卷
     * @return 修正过的结算信息
     */
    @Override
    public SettlementInfo computeRule(SettlementInfo settlement) {
        double goodsSum = retain2Decimals(
                goodsCostSum(settlement.getGoodsInfos()));
        SettlementInfo probability = processGoodsTypeNotSatisfy(
                settlement, goodsSum);
        if (null !=probability){
            log.debug("ManJian Template Is Not Match To GoodsType!");
            return probability;
        }
        //判断满减是否符合折扣标准
        CouponTemplateSDK templateSDK = settlement.getCouponAndtemplateInfos()
                .get(0).getTemplate();
        double quata = (double)templateSDK.getRule().getDiscount().getQuata();

        //计算使用优惠卷之后的价格
        settlement.setCost(retain2Decimals(
                (goodsSum-quata)>minCost()?(goodsSum-quata):minCost()
        ));
        log.debug("Use LiJian Coupon make Goods Cost From {} To {}",goodsSum,settlement.getCost());
        return settlement;
    }
}
