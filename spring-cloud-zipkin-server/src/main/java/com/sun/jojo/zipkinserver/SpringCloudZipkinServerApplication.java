package com.sun.jojo.zipkinserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.cloud.sleuth.zipkin.stream.EnableZipkinStreamServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zipkin.server.internal.EnableZipkinServer;
//import zipkin.server.internal.EnableZipkinServer;


/**
 * @author sunjiamin
 */
@SpringBootApplication
@EnableZipkinServer
//@EnableZipkinStreamServer
public class SpringCloudZipkinServerApplication {



	public static void main(String[] args) {
		SpringApplication.run(SpringCloudZipkinServerApplication.class, args);
	}
}
