package com.sun.jojo.configclient.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * description:
 *
 * @author sunjiamin
 * @date 2018-05-15 09:52
 */
@RestController
public class HiController {
    /**
     * 从配置中心获取配置信息
     */
    @Value("${foo}")
    String foo;

    @RequestMapping(value = "/hi")
    public String hi(){
        return foo;
    }

}
