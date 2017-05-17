package com.jd.uwp.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by
 */
@Controller
public class IndexController{

    private static final Logger LOGGER = LoggerFactory.getLogger(IndexController.class);


    /**
     * 在本controller方法调用前执行,可以存放一些共享变量,如枚举值,或,是一些初始化操作
     */
    @ModelAttribute
    public void init(ModelMap model) {
        model.put("now", new java.sql.Timestamp(System.currentTimeMillis()));
    }

    @RequestMapping(value = {"/", "/index"}, method = {RequestMethod.POST, RequestMethod.GET})
    public String index(ModelMap model){
        try {
            LOGGER.info("index");
        } catch (Exception e) {
            LOGGER.error("error in controller",e);
        }
        return "index";
    }


}
