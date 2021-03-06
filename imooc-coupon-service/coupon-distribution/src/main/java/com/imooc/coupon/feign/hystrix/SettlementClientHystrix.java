package com.imooc.coupon.feign.hystrix;

import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.feign.SettlementClient;
import com.imooc.coupon.vo.CommonResponse;
import com.imooc.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Annotation 结算微服务熔断微服务策略实现
 * @ClassName SettlementClientHystrix
 * @Author ChenWenJie
 * @Data 2020/5/17 6:34 下午
 * @Version 1.0
 **/
@Slf4j
@Component
public class SettlementClientHystrix implements SettlementClient {
    /**
     * 优惠卷规则计算
     *
     * @param settlement
     * @return
     * @throws CouponException
     */
    @Override
    public CommonResponse<SettlementInfo> computeRule(SettlementInfo settlement) throws CouponException {
        log.error("[eureka-client-coupon-settlement] computeRule request error");
        settlement.setEmploy(false);
        settlement.setCost(-1.0);

        return new CommonResponse<>(
                -1,
                "[eureka-client-coupon-settlement] request error",
                settlement
        );
    }
}
