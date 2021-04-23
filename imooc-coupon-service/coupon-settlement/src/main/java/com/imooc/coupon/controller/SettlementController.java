package com.imooc.coupon.controller;

import com.alibaba.fastjson.JSON;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.executor.ExecuteManager;
import com.imooc.coupon.vo.SettlementInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Annotation
 * @ClassName SettlementController
 * @Author ChenWenJie
 * @Data 2020/5/29 2:38 下午
 * @Version 1.0
 **/
@Slf4j
@RestController
public class SettlementController {
    /** 结算规则执行管理器*/
    private final ExecuteManager executeManager;

    @Autowired
    public SettlementController(ExecuteManager executeManager){
        this.executeManager = executeManager;
    }

    /**
     * 优惠价结算
     */
    @PostMapping("/settlement/compute")
    public SettlementInfo computeRule(@RequestBody SettlementInfo settlement)throws CouponException {
        log.info("settlement:{}", JSON.toJSONString(settlement));
        return executeManager.computeRule(settlement);
    }
}
