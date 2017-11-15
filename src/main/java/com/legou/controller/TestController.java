package com.legou.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Administrator on 2017/11/13.
 */
@Controller
@RequestMapping(value = "/webtest")
public class TestController{
    private static  final Logger logger= LoggerFactory.getLogger(TestController.class);
    @RequestMapping("test.do")
    @ResponseBody
    public String test(String str){
     logger.error("testerror");
     logger.info("testinfo");
     logger.warn("testwarn");
     return "testvalue"+str;
    }
}
