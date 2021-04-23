package com.imooc.coupon.service.impl;

import com.imooc.coupon.dao.CouponTemplateDao;
import com.imooc.coupon.entity.CouponTemplate;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.service.ITemplateBaseService;
import com.imooc.coupon.vo.CouponTemplateSDK;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Annotation
 * @ClassName TemplateBaseServiceImpl
 * @Author ChenWenJie
 * @Data 2020/5/15 10:23 下午
 * @Version 1.0
 **/
@Slf4j
@Service
public class TemplateBaseServiceImpl implements ITemplateBaseService {
    private final CouponTemplateDao templateDao;

    @Autowired
    public TemplateBaseServiceImpl(CouponTemplateDao templateDao) {
        this.templateDao = templateDao;
    }
    /**
     * 根据优惠卷模版 id 获取优惠卷模版信息
     *
     * @param id 模版 id
     * @return {@link CouponTemplate} 优惠卷模版实体
     * @throws CouponException
     */
    @Override
    public CouponTemplate buildTemplateInfo(Integer id) throws CouponException {
        Optional<CouponTemplate> template = templateDao.findById(id);
        if (!template.isPresent()){
            throw new CouponException("Template Is Not Exist:"+id);
        }
        return template.get();
    }

    /**
     * 查找所有可用的优惠卷模版
     *
     * @return {@link CouponTemplateSDK}s
     */
    @Override
    public List<CouponTemplateSDK> findAllUsableTmplate() {
        List<CouponTemplate> templates =
                templateDao.findAllByAvailableAndExpired(true, false);
        return templates.stream()
                .map(this::template2TemplateSDK).collect(Collectors.toList());
    }

    /**
     * 获取模版 ids 到 CouponTemplateSDK 到映射
     * @param ids 模版 ids
     * @return Map<key:模版 id ， value: CouponTemplateSDK>
     */
    @Override
    public Map<Integer, CouponTemplateSDK> findIds2TemplateSDK(Collection<Integer> ids) {
        List<CouponTemplate> templates = templateDao.findAllById(ids);
        return templates.stream()
                .map(this::template2TemplateSDK).collect(Collectors.toMap(
                        CouponTemplateSDK::getId, Function.identity()));
    }

    /**
     * 将CouponTemplate 转换成  CouponTemplateSDK
     * @param template
     * @return
     */
    private CouponTemplateSDK template2TemplateSDK(CouponTemplate template){
        return new CouponTemplateSDK(
                template.getId(),
                template.getName(),
                template.getLogo(),
                template.getDesc(),
                template.getCategory().getCode(),
                template.getProductLine().getCode(),
                //并不是拼装好的 Template Key
                template.getKey(),
                template.getTarget().getCode(),
                template.getRule());
    }
}
