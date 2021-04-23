package com.imooc.coupon.executor;

import com.imooc.coupon.constant.RuleFlag;
import com.imooc.coupon.vo.SettlementInfo;

/**
 * @Annotation 优惠卷模板规则处理器接口
 * @Author ChenWenJie
 * @Data 2020/5/19 8:46 下午
 */
public interface RuleExecutor {
    /**
     * 规则类型标记
     * @return
     */
    RuleFlag ruleFlag();

    /**
     * 优惠卷规则的计算
     * @param settlement 包含选择的优惠卷
     * @return 修正过的结算信息
     */
    SettlementInfo computeRule(SettlementInfo settlement);


}
