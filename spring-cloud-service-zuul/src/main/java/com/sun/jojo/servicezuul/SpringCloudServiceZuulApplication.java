package com.sun.jojo.servicezuul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * @author sunjiamin
 */
@SpringBootApplication
@EnableZuulProxy
@EnableEurekaClient
public class SpringCloudServiceZuulApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudServiceZuulApplication.class, args);
	}
}
