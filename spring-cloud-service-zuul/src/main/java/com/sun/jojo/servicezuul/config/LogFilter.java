package com.sun.jojo.servicezuul.config;

import brave.Tracer;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

/**
 * description:
 *
 * @author sunjiamin
 * @date 2018-05-17 10:36
 */
@Component
public class LogFilter extends ZuulFilter {

    @Autowired
    Tracer tracer;

    @Override
    public String filterType() {
        return FilterConstants.POST_TYPE;
    }

    @Override
    public int filterOrder() {
        return 900;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {

        tracer.currentSpan().tag("operator","sunjiamin");
        System.out.print(tracer.currentSpan().context().spanId());
        return null;
    }
}
