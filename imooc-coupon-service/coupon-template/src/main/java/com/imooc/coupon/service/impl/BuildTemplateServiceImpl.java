package com.imooc.coupon.service.impl;

import com.imooc.coupon.dao.CouponTemplateDao;
import com.imooc.coupon.entity.CouponTemplate;
import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.service.IAsyncService;
import com.imooc.coupon.service.IBuildTemplateService;
import com.imooc.coupon.vo.TemplateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Annotation 构建优惠卷模版接口实现
 * @ClassName BuildTemplateServiceImpl
 * @Author ChenWenJie
 * @Data 2020/5/15 10:06 下午
 * @Version 1.0
 **/
@Slf4j
@Service
public class BuildTemplateServiceImpl implements IBuildTemplateService {
    private final IAsyncService asyncService;
    private final CouponTemplateDao templateDao;

    @Autowired
    public BuildTemplateServiceImpl(CouponTemplateDao templateDao, IAsyncService asyncService) {
        this.templateDao = templateDao;
        this.asyncService = asyncService;
    }

    /**
     * 创建优惠卷模版
     * @param request {@link TemplateRequest} 模版信息请求对象
     * @return {@link CouponTemplate} 优惠卷模版实体
     * @throws CouponException
     */
    @Override
    public CouponTemplate buildTemplate(TemplateRequest request) throws CouponException {
        //参数合法性校验
        if (!request.validate()){
            throw new CouponException("BuildTemplate Param Is Not Valid!");
        }
        if (null != templateDao.findByName(request.getName())){
            throw new CouponException("Exist Same Name Template!");
        }
        //构造 CouponTemplate 并保存到数据库中
        CouponTemplate template = requestToTemplate(request);
        template = templateDao.save(template);
        //根据优惠卷模版异步生成优惠卷码
        asyncService.asyncConstructCouponByTemplate(template);
        return template;
    }

    /**
     * 将 TemplateReauest 转换为 CouponTemplate
     * @param request
     * @return
     */
    private CouponTemplate requestToTemplate(TemplateRequest request){
        return new CouponTemplate(
                request.getName(),
                request.getLogo(),
                request.getDesc(),
                request.getCategory(),
                request.getProductLine(),
                request.getCount(),
                request.getUserId(),
                request.getTarget(),
                request.getRule());
    }
}
