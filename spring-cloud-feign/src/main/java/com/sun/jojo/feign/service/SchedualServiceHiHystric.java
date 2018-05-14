package com.sun.jojo.feign.service;

import org.springframework.stereotype.Component;

/**
 * description:
 *
 * @author sunjiamin
 * @date 2018-05-14 10:23
 */
@Component
public class SchedualServiceHiHystric implements SchedualServiceHi {
    @Override
    public String sayHiFromClientOne(String name) {
        return "sorry "+name;
    }
}