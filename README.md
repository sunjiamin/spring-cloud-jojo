
**1）eureka 服务注册**

 module:spring-cloud-eureka-server
 
 **1.1 添加Pom引用**
 
 	<dependencies>
 		<dependency>
 			<groupId>org.springframework.boot</groupId>
 			<artifactId>spring-boot-starter-web</artifactId>
 		</dependency>
 		<dependency>
 			<groupId>org.springframework.cloud</groupId>
 			<artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
 		</dependency>
 
 		<dependency>
 			<groupId>org.springframework.boot</groupId>
 			<artifactId>spring-boot-starter-test</artifactId>
 			<scope>test</scope>
 		</dependency>
 	</dependencies>
 
 
 **1.2 启动一个服务注册中心**
   只需要一个注解@EnableEurekaServer，这个注解需要在springboot工程的启动application类上加
 
    @EnableEurekaServer
    @SpringBootApplication
    public class SpringCloudEurekaServerApplication {
 	  public static void main(String[] args) {
 	    	SpringApplication.run(SpringCloudEurekaServerApplication.class, args);
 	  }
    }
  

 **1.3 添加配置**
   eureka是一个高可用的组件，它没有后端缓存，每一个实例注册之后需要向注册中心发送心跳（因此可以在内存中完成）,在默认情况下erureka server也是一个eureka client ,必须要指定一个 server。
   eureka server的配置文件appication.yml:
   
       server:
          port: 7070
          eureka:
            instance:
              hostname: localhost
            client:
              register-with-eureka: false
              fetch-registry: false
              service-url:
                defaultZone: http://${eureka.instance.hostname}:${server.port}/eureka/
         
 通过eureka.client.registerWithEureka：false和fetchRegistry：false来表明自己是一个eureka server.
 
 
 **2) 创建一个服务提供者 (eureka client)**
 
   module:spring-cloud-service-a
   
   **2.1 创建项目**
   
   创建过程同server类似，POM引用：
   
        	<dependencies>
        		<dependency>
        			<groupId>org.springframework.boot</groupId>
        			<artifactId>spring-boot-starter-web</artifactId>
        		</dependency>
        		<dependency>
        			<groupId>org.springframework.cloud</groupId>
        			<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        		</dependency>
        		<dependency>
        			<groupId>org.springframework.cloud</groupId>
        			<artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
        		</dependency>
        
        		<dependency>
        			<groupId>org.springframework.boot</groupId>
        			<artifactId>spring-boot-starter-test</artifactId>
        			<scope>test</scope>
        		</dependency>
        	</dependencies>
 
 
    
   **2.2 通过注解@EnableEurekaClient 表明自己是一个eurekaclient.**
   ```java
        @EnableEurekaClient
        @SpringBootApplication
        public class Application {
        
        	public static void main(String[] args) {
        		SpringApplication.run(Application.class, args);
        	}
        }

   ```
   
   **2.3 创建Controller** 
   ```java
      /**
       * description:
       *
       * @author sunjiamin
       * @date 2018-05-11 15:48
       */
      @RestController
      public class HomeController {
      
          @Value("${server.port}")
          String port;
      
          @RequestMapping("/hi")
          public String home(@RequestParam String name) {
              return "hi "+name+",i am from port:" +port;
          }
      }
   ```

   **2.4 修改配置文件**
   ```json
server:
  port: 7071


eureka:
  instance:
    hostname: localhost
  client:
    service-url:
      defaultZone: http://localhost:7070/eureka/
spring:
  application:
    # 需要指明spring.application.name,这个很重要，这在以后的服务与服务之间相互调用一般都是根据这个name
    name: service-hi
```

eureka-server项目启动后，启动服务提供项目

