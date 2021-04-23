package com.imooc.coupon.executor.impl;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.constant.CouponCategory;
import com.imooc.coupon.constant.RuleFlag;
import com.imooc.coupon.executor.AbstractExecutor;
import com.imooc.coupon.executor.RuleExecutor;
import com.imooc.coupon.vo.CouponTemplateSDK;
import com.imooc.coupon.vo.GoodsInfo;
import com.imooc.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Annotation 满减 + 折扣优惠卷结算规则执行器
 * @ClassName ManJianZheKouExecutor
 * @Author ChenWenJie
 * @Data 2020/5/29 11:29 上午
 * @Version 1.0
 **/
@Slf4j
@Component
public class ManJianZheKouExecutor  extends AbstractExecutor implements RuleExecutor {
    /**
     * 规则类型标记
     *
     * @return
     */
    @Override
    public RuleFlag ruleFlag() {
        return RuleFlag.MANJIAN_ZHEKOW;
    }

    /**
     * 校验商品类型与优惠卷是否匹配
     * 需要注意
     * 1.这里实现的单品类优惠卷的校验，多品类优惠卷重载此方法
     * 2.如果想要使用优惠卷，则必须要所有的商品类型都包含在内，即差集为空
     *
     * @param settlement {@link SettlementInfo }用户传递的计算信息
     * @return
     */
    @Override
    protected boolean isGoodsTypeSatisfy(SettlementInfo settlement) {
        log.debug("Check ManJian And ZheKou Match Or Not!");
        List<Integer> goodsType = settlement.getGoodsInfos().stream()
                .map(GoodsInfo::getType).collect(Collectors.toList());
        List<Integer> templateGoodsType = new ArrayList<>();
        settlement.getCouponAndtemplateInfos().forEach(ct ->{
            templateGoodsType.addAll(JSON.parseObject(
                    ct.getTemplate().getRule().getUsage().getGoodsType(),
                    List.class
            ));
        });
        //如果想要使用优惠卷，则必须要所有的商品类型都包含在内，即差集为空
        return CollectionUtils.isEmpty(CollectionUtils.subtract(
                goodsType,templateGoodsType));
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
                goodsCostSum((settlement.getGoodsInfos())));
        //商品类型的校验
        SettlementInfo probability = processGoodsTypeNotSatisfy(
                settlement, goodsSum);
        if (null !=probability){
            log.debug("ManJian And ZheKou Template Is Not Match To GoodsType!");
            return probability;
        }
        SettlementInfo.CouponAndtemplateInfo  manJian = null;
        SettlementInfo.CouponAndtemplateInfo zheKou = null;
        for (SettlementInfo.CouponAndtemplateInfo ct :
                settlement.getCouponAndtemplateInfos()) {
            if (CouponCategory.of(ct.getTemplate().getCategory())==
                    CouponCategory.MANJIAN){
                manJian = ct;
            }else {
                zheKou = ct;
            }
        }
        assert null != manJian;
        assert null != zheKou;
        //当前的优惠卷和满减卷如果不能共用(一起使用)，清空优惠卷，返回商品原价
        if (!isTemplateCanShared(manJian,zheKou)){
            log.debug("Current ManJian And ZheKou Can Not Shared!");
            settlement.setCost(goodsSum);
            settlement.setCouponAndtemplateInfos(Collections.emptyList());
            return settlement;
        }
        List<SettlementInfo.CouponAndtemplateInfo> ctInfos = new ArrayList<>();
        double manJianBase = (double) manJian.getTemplate().getRule()
                .getDiscount().getBase();
        double manJianQuota = (double) manJian.getTemplate().getRule()
                .getDiscount().getQuata();
        //最终的价格
        double targetSum = goodsSum;
        if (targetSum>=manJianBase){
            targetSum-=manJianQuota;
            ctInfos.add(manJian);
        }
        //再计算折扣
        double zheKouQuota = (double) zheKou.getTemplate().getRule()
                .getDiscount().getQuata();
        targetSum *= zheKouQuota * 1.0 /100;
        ctInfos.add(zheKou);
        settlement.setCouponAndtemplateInfos(ctInfos);
        settlement.setCost(retain2Decimals(
                targetSum>minCost()?targetSum:minCost()
        ));
        log.debug("Use ManJian And ZheKou Coupon Make Goods Cost From {} To {}",
                goodsSum,settlement.getCost());
        return null;
    }

    /**
     * 当前的两张优惠卷是否可以共用
     * 即校验TemplateRule 中的weight 是否满足条件
     * @param manJian
     * @param zheKou
     * @return
     */
    private boolean isTemplateCanShared(SettlementInfo.CouponAndtemplateInfo  manJian,
                                        SettlementInfo.CouponAndtemplateInfo zheKou){
       String manJianKey = manJian.getTemplate().getKey()
                +String.format("%04d",manJian.getTemplate().getId());
       String zheKouKey = zheKou.getTemplate().getKey()
               +String.format("%04d",zheKou.getTemplate().getId());
        List<String> allSharedKeyForManjian = new ArrayList<>();
        allSharedKeyForManjian.add(manJianKey);
        allSharedKeyForManjian.addAll(JSON.parseObject(
                manJian.getTemplate().getRule().getWeight(),
                List.class
        ));
        List<String> allSharedKeyForForZheKou = new ArrayList<>();
        allSharedKeyForForZheKou.add(zheKouKey);
        allSharedKeyForForZheKou.addAll(JSON.parseObject(
           zheKou.getTemplate().getRule().getWeight(),
           List.class
        ));

        return CollectionUtils.isSubCollection(
                Arrays.asList(manJianKey,zheKouKey),allSharedKeyForManjian)
                || CollectionUtils.isSubCollection(
                        Arrays.asList(manJianKey,zheKouKey),allSharedKeyForForZheKou
                );
    }
}
