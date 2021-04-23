package com.imooc.coupon.executor.impl;

import com.imooc.coupon.constant.RuleFlag;
import com.imooc.coupon.executor.AbstractExecutor;
import com.imooc.coupon.executor.RuleExecutor;
import com.imooc.coupon.vo.CouponTemplateSDK;
import com.imooc.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Annotation 折扣优惠卷结算规则执行器
 * @ClassName ZheKouExecutor
 * @Author ChenWenJie
 * @Data 2020/5/27 11:41 上午
 * @Version 1.0
 **/
@Slf4j
@Component
public class ZheKouExecutor extends AbstractExecutor implements RuleExecutor {
    /**
     * 规则类型标记
     *
     * @return
     */
    @Override
    public RuleFlag ruleFlag() {
        return RuleFlag.ZHEKOU;
    }

    /**
     * 优惠卷规则的计算
     *
     * @param settlement 包含选择的优惠卷
     * @return 修正过的结算信息
     */
    @Override
    public SettlementInfo computeRule(SettlementInfo settlement) {
        double goodsSum = retain2Decimals(goodsCostSum(
                settlement.getGoodsInfos()
        ));
        SettlementInfo probability = processGoodsTypeNotSatisfy(
                settlement, goodsSum);
        if (null != probability){
            log.debug("ZheKou Template Is Not Match GoodsType!");
            return probability;
        }
        //折扣优惠卷可以直接使用，没有门槛
        CouponTemplateSDK templateSDK = settlement.getCouponAndtemplateInfos().get(0).getTemplate();
        double quata = (double)templateSDK.getRule().getDiscount().getQuata();
        //计算使用优惠卷之后的价格
        settlement.setCost(retain2Decimals(
                goodsSum * (quata * 1.0 / 100)) > minCost() ?
                retain2Decimals(goodsSum * (quata * 1.0 / 100))
                :minCost()
        );
        log.debug("Use ZheKou Coupon make Goods Cost From {} To {}",goodsSum,settlement.getCost());
        return settlement;
    }
}
