package com.imooc.coupon.service.impl;

import com.imooc.coupon.exception.CouponException;
import com.imooc.coupon.service.ITemplateBaseService;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TemplateBaseServiceImplTest extends TestCase {
    @Autowired
    private ITemplateBaseService templateBaseService;
    @Test
    public void testBuildTemplateInfo() throws CouponException {
        System.out.println(templateBaseService.buildTemplateInfo(12));
    }

    @Test
    public void findAllUsableTmplate(){
        System.out.println(templateBaseService.findAllUsableTmplate());
    }

    @Test
    public void findIds2TemplateSDK(){
        System.out.println(templateBaseService.findIds2TemplateSDK(Collections.singleton(12)));
    }
}