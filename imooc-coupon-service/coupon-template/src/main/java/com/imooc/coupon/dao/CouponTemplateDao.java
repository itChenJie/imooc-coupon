package com.imooc.coupon.dao;

import com.imooc.coupon.entity.CouponTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *CouponTemplate Dao接口定义
 */
public interface CouponTemplateDao extends JpaRepository<CouponTemplate,Integer> {
    /**
     * 根据模版名称查询模块
     * @param name
     * @return
     */
    CouponTemplate findByName(String name);

    /**
     * 根据 available 和 expired 标记查找模版记录
     * where available=。。。。and expired=。。。
     * @param available
     * @param expired
     * @return
     */
    List<CouponTemplate> findAllByAvailableAndExpired(Boolean available,Boolean expired);

    /**
     * 根据expired 标记查找模版记录
     * where expired=。。。
     * @param expired
     * @return
     */
    List<CouponTemplate> findAllByExpired(Boolean expired);
}
