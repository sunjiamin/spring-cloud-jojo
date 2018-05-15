
**第一篇: eureka 服务注册**

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
 
 
 
 
 
 ----------------------------------华丽的分割线--------------------------------------------------------------------------
 
 
 
 **第二篇: 创建一个服务提供者 (eureka client)**
 
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



 ----------------------------------华丽的分割线--------------------------------------------------------------------------
 
 
 **第三篇:服务消费者（rest+ribbon）**
 
    module:spring-cloud-service-ribbon
 
 在微服务架构中，业务都会被拆分成一个独立的服务，服务与服务的通讯是基于http restful的。
 Spring cloud有两种服务调用方式，一种是ribbon+restTemplate，另一种是feign。在这一篇文章首先讲解下基于ribbon+rest。
 
 **3.1 ribbon简介**
 
   ribbon是一个负载均衡客户端，可以很好的控制htt和tcp的一些行为。Feign默认集成了ribbon。
 
 **3.2 准备工作**
 
   启动eureka-server 工程；启动service-a工程，它的端口为7071；将service-a的配置文件的端口改为7072,并启动，
   这时你会发现：service-hi在eureka-server注册了2个实例，这就相当于一个小的集群。
        
        java -jar xxxx.jar --server.port=7071
        java -jar xxxx.jar --server.port=7072
 
 **3.3 建一个服务消费者**
 
 重新新建一个spring-boot工程，取名为：spring-cloud-service-ribbon pom文件：
 
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
    			<groupId>org.springframework.cloud</groupId>
    			<artifactId>spring-cloud-starter-netflix-ribbon</artifactId>
    		</dependency>
    		<!-- 添加熔断器-->
    		<dependency>
    			<groupId>org.springframework.cloud</groupId>
    			<artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
    		</dependency>
    
    		<dependency>
    			<groupId>org.springframework.boot</groupId>
    			<artifactId>spring-boot-starter-test</artifactId>
    			<scope>test</scope>
    		</dependency>
    	</dependencies>
 
 **3.4 添加配置**
 
   在工程的配置文件指定服务的注册中心地址为http://localhost:7070/eureka/，程序名称为 service-ribbon，7075。配置文件application.yml如下：
   
        server:
          port: 7075
        
        spring:
          application:
            name: service-ribbon
        
        
        eureka:
          client:
            serviceUrl:
              defaultZone: http://localhost:7070/eureka/
          instance:
            hostname: localhost
            
 **3.5 修改启动类**
 
 在工程的启动类中,通过@EnableDiscoveryClient向服务中心注册；并且向程序的ioc注入一个bean: restTemplate;
 并通过@LoadBalanced注解表明这个restRemplate开启负载均衡的功能
 
 ```java
    @EnableDiscoveryClient
    @SpringBootApplication
    public class RibbonApplication {
    
        @Bean
        @LoadBalanced
        RestTemplate restTemplate() {
            return new RestTemplate();
        }
        public static void main(String[] args) {
            SpringApplication.run(RibbonApplication.class, args);
        }
    }
```
 
  **3.6 添加HelloService**
  
 写一个测试类HelloService，通过之前注入ioc容器的restTemplate来消费service-hi服务的“/hi”接口，
 在这里我们直接用的程序名替代了具体的url地址，在ribbon中它会根据服务名来选择具体的服务实例，根据服务实例在请求的时候会用具体的url替换掉服务名，
```java
/**
 * description:
 *
 * @author sunjiamin
 * @date 2018-05-11 16:22
 */
@Service
public class HelloService {
    @Autowired
    RestTemplate restTemplate;

 
    public String hiService(String name) {
        //return restTemplate.getForObject("http://localhost:7071/hi?name="+name,String.class);
        return restTemplate.getForObject("http://service-hi/hi?name="+name,String.class);
    }

 
}
```
  **3.7 添加Controller**
  
  写一个controller，在controller中用调用HelloService 的方法，代码如下：

  ```java
/**
 * description:
 *
 * @author sunjiamin
 * @date 2018-05-11 16:23
 */
@RestController
public class HelloController {

    @Autowired
    HelloService helloService;

    @RequestMapping(value = "/hi")
    public String hi(@RequestParam String name){
        return helloService.hiService(name);
    }
}
```
在浏览器上多次访问http://localhost:7075/hi?name=jojo ,浏览器交替显示：
                                             
           hi jojo,i am from port:7071
                                             
           hi jojjo,i am from port:7072

 这说明当我们通过调用restTemplate.getForObject(“http://SERVICE-HI/hi?name=“+name,String.class)方法时，已经做了负载均衡，
 访问了不同的端口的服务实例。
 
 
 
 **此时的架构：**
 
 一个服务注册中心，eureka server,7070
 
 spring-cloud-service-a工程跑了两个实例，端口分别为7071,7072，分别向服务注册中心注册
 
 spring-cloud-sercvice-ribbon端口为7075,向服务注册中心注册
 
 当spring-cloud-sercvice-ribbon通过restTemplate调用service-hi的hi接口时，因为用ribbon进行了负载均衡，会轮流的调用service-hi：7071和7072 两个端口的hi接口；
 
 
 
 
  ----------------------------------华丽的分割线--------------------------------------------------------------------------
  
  
 
 
 
 *第四篇: 服务消费者（Feign）**
 
     module:spring-cloud-feigon
 
 
 上面，讲述了如何通过RestTemplate+Ribbon去消费服务，这里主要讲述如何通过Feign去消费服务。
 
 **4.1 Feign简介**
 Feign是一个声明式的伪Http客户端，它使得写Http客户端变得更简单。使用Feign，只需要创建一个接口并注解。它具有可插拔的注解特性，
 可使用Feign 注解和JAX-RS注解。Feign支持可插拔的编码器和解码器。Feign默认集成了Ribbon，并和Eureka结合，默认实现了负载均衡的效果。
 
 简而言之：
 
 Feign 采用的是基于接口的注解
 Feign 整合了ribbon
 
 **4.2 准备工作**
 继续用上一节的工程， 启动eureka-server，端口为7070; 启动service-a 两次，端口分别为7071 、7072.
 
 **4.3 创建一个feign的服务**
 
 新建一个spring-boot工程，取名为spring-cloud-feign，pom文件依赖：
 
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
       			<groupId>org.springframework.cloud</groupId>
       			<artifactId>spring-cloud-starter-openfeign</artifactId>
       		</dependency>
       
       		<dependency>
       			<groupId>org.springframework.boot</groupId>
       			<artifactId>spring-boot-starter-test</artifactId>
       			<scope>test</scope>
       		</dependency>
       	</dependencies>
 
 
 
 **4.4 修改配置文件**
 
    server:
      port: 7076
    
    spring:
      application:
        name: service-feign
    
    
    eureka:
      client:
        serviceUrl:
          defaultZone: http://localhost:7070/eureka/
      instance:
        hostname: localhost
        
        
 **4.5 修改启动类**
 
 在程序的启动类ServiceFeignApplication ，加上@EnableFeignClients注解开启Feign的功能：
 
 ```java
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class SpringCloudFeignApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudFeignApplication.class, args);
	}
}

```
 
 **4.6 定义接口**
 
 定义一个feign接口，通过@ FeignClient（“服务名”），来指定调用哪个服务。比如在代码中调用了service-hi服务的“/hi”接口，代码如下：
 
 ```java
/**
 * description:
 *
 * @author sunjiamin
 * @date 2018-05-14 09:58
 */
@FeignClient(value = "service-hi")
public interface SchedualServiceHi {
    @RequestMapping(value = "/hi",method = RequestMethod.GET)
    String sayHiFromClientOne(@RequestParam(value = "name") String name);
}
```
 
 **4.7 添加Controller**
 
 在Web层的controller层，对外暴露一个”/hi”的API接口，通过上面定义的Feign客户端SchedualServiceHi 来消费服务。代码如下
 
 ```java
/**
 * description:
 *
 * @author sunjiamin
 * @date 2018-05-14 09:59
 */
@RestController
public class HiController {

    @Autowired
    SchedualServiceHi schedualServiceHi;

    @RequestMapping(value = "/hi",method = RequestMethod.GET)
    public String sayHi(@RequestParam String name){
        return schedualServiceHi.sayHiFromClientOne(name);
    }
}

```

启动程序，多次访问http://localhost:7076/hi?name=jojo,浏览器交替显示：

    hi jojo,i am from port:7071
    
    hi jojo,i am from port:7072
    
    
  
  ----------------------------------华丽的分割线--------------------------------------------------------------------------
  
    
    
    
 **第五篇: 断路器（Hystrix）**
 
 在微服务架构中，根据业务来拆分成一个个的服务，服务与服务之间可以相互调用（RPC），在Spring Cloud可以用RestTemplate+Ribbon和Feign来调用。
 为了保证其高可用，单个服务通常会集群部署。由于网络原因或者自身的原因，服务并不能保证100%可用，如果单个服务出现问题，调用这个服务就会出现线程阻塞，
 此时若有大量的请求涌入，Servlet容器的线程资源会被消耗完毕，导致服务瘫痪。服务与服务之间的依赖性，故障会传播，会对整个微服务系统造成灾难性的严重后果，
 这就是服务故障的“雪崩”效应。
 
 为了解决这个问题，业界提出了断路器模型。
 
 **5.1 在ribbon使用断路器**
 
 5.1.1 改造spring-cloud-service-ribbon 工程的代码，首先在pox.xml文件中加入spring-cloud-starter-hystrix的起步依赖:
 
                <!-- 添加熔断器-->
        		<dependency>
        			<groupId>org.springframework.cloud</groupId>
        			<artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
        		</dependency>
 
 
 5.1.2 在程序的启动类ServiceRibbonApplication 加@EnableHystrix注解开启Hystrix：
 
 ```java
@EnableDiscoveryClient
@SpringBootApplication
@EnableHystrix
public class RibbonApplication {

	@Bean
	@LoadBalanced
	RestTemplate restTemplate() {
		return new RestTemplate();
	}
	public static void main(String[] args) {
		SpringApplication.run(RibbonApplication.class, args);
	}
}

```
 
 
 5.1.3 改造HelloService类，在hiService方法上加上@HystrixCommand注解。该注解对该方法创建了熔断器的功能，并指定了fallbackMethod熔断方法，
 熔断方法直接返回了一个字符串，字符串为”hi,”+name+”,sorry,error!”，代码如下：
 
 
 ```java
@Service
public class HelloService {
    @Autowired
    RestTemplate restTemplate;


    @HystrixCommand(fallbackMethod = "hiError")
    public String hiService(String name) {
        //return restTemplate.getForObject("http://localhost:7071/hi?name="+name,String.class);
        return restTemplate.getForObject("http://service-hi/hi?name="+name,String.class);
    }

    public String hiError(String name) {
        return "hi,"+name+",sorry,error!";
    }
}
```
 
 至此，Robbin 添加熔断已完成，
 
 启动：service-ribbon 工程，当我们访问http://localhost:7075/hi?name=jojo,浏览器显示：
 
    hi jojo,i am from port:7071
 
 此时关闭 service-hi 工程，当我们再访问http://localhost:7075/hi?name=jojo，浏览器会显示：
 
    hi ,jojo,sorry,error!
 
 这就说明当 service-hi 工程不可用的时候，service-ribbon调用 service-a的API接口时，会执行快速失败，直接返回一组字符串，而不是等待响应超时，
 这很好的控制了容器的线程阻塞。
 

 
 
 
 **5.2 Feign中使用断路器**
 
 **5.2.1** Feign是自带断路器的， 它没有默认打开。需要在配置文件中配置打开它，在配置文件加以下代码：
        
    feign:
       hystrix:
         enabled: true
 
 **5.2.2** 基于spring-cloud-feign工程进行改造，只需要在FeignClient的SchedualServiceHi接口的注解中加上fallback的指定类就行了：
 

```java
@FeignClient(value = "service-hi",fallback = SchedualServiceHiHystric.class)
public interface SchedualServiceHi {
    @RequestMapping(value = "/hi",method = RequestMethod.GET)
    String sayHiFromClientOne(@RequestParam(value = "name") String name);
}
```

**5.2.3** SchedualServiceHiHystric需要实现SchedualServiceHi 接口，并注入到Ioc容器中，代码如下：

```java
@Component
public class SchedualServiceHiHystric implements SchedualServiceHi {
    @Override
    public String sayHiFromClientOne(String name) {
        return "sorry "+name;
    }
}
``` 
  至此，Feign 添加熔断已完成，
  
  测试同上
  
  
  
  ----------------------------------华丽的分割线--------------------------------------------------------------------------
  
  
  **第六篇: 路由网关(zuul)**
  
        module:spring-cloud-service-zuul
  
  在Spring Cloud微服务系统中，一种常见的负载均衡方式是，客户端的请求首先经过负载均衡（zuul、Ngnix），再到达服务网关（zuul集群），
  然后再到具体的服。，服务统一注册到高可用的服务注册中心集群，服务的所有的配置文件由配置服务管理，配置服务的配置文件放在git仓库，方便开发人员随时改配置。
  
  
  6.1 Zuul简介
  
  Zuul的主要功能是路由转发和过滤器。路由功能是微服务的一部分，比如／api/user转发到到user服务，/api/shop转发到到shop服务。zuul默认和Ribbon结合实现了负载均衡的功能。
  
  zuul有以下功能：
  
      Authentication
      Insights
      Stress Testing
      Canary Testing
      Dynamic Routing
      Service Migration
      Load Shedding
      Security
      Static Response handling
      Active/Active traffic management
  
  **6.2 准备工作**
  
  继续使用上一节的工程。在原有的工程上，创建一个新的工程。
  
  **6.3 创建spring-cloud-service-zuul工程**
  
  POM 引用：
  
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
        			<artifactId>spring-cloud-starter-netflix-zuul</artifactId>
        		</dependency>
        
        		<dependency>
        			<groupId>org.springframework.boot</groupId>
        			<artifactId>spring-boot-starter-test</artifactId>
        			<scope>test</scope>
        		</dependency>
        	</dependencies>
        	
  
  **6.4 修改启动类**
  
  在其入口applicaton类加上注解@EnableZuulProxy，开启zuul的功能：
  

  ```java
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
```
  
  **6.5 修改配置文件**
  
   加上配置文件application.yml加上以下的配置代码：
   
    eureka:
      client:
        serviceUrl:
          defaultZone: http://localhost:7070/eureka/
    server:
      port: 7077
    spring:
      application:
        name: service-zuul
    zuul:
      routes:
        api-a:
          path: /api-a/**
          #以/api-a/ 开头的请求都转发给service-ribbon服务；
          serviceId: service-ribbon
        api-b:
          path: /api-b/**
          #以/api-b/开头的请求都转发给service-feign服务；
          serviceId: service-feign
  
  依次运行这五个工程;打开浏览器访问：http://localhost:7077/api-a/hi?name=jojo ;浏览器显示：
 
    hi jojo,i am from port:7072
 
  打开浏览器访问：http://localhost:7077/api-b/hi?name=jojo ;浏览器显示：
  
    hi jojo,i am from port:7072
 
 
  **6.6 服务过滤**
  
  zuul不仅只是路由，并且还能过滤，做一些安全验证。继续改造工程；添加MyFilter 继承自ZuulFilter：
  
  ```java
 package com.sun.jojo.servicezuul.config;
 
 import com.netflix.zuul.ZuulFilter;
 import com.netflix.zuul.context.RequestContext;
 import com.netflix.zuul.exception.ZuulException;
 import org.slf4j.Logger;
 import org.slf4j.LoggerFactory;
 import org.springframework.stereotype.Component;
 
 
 import javax.servlet.http.HttpServletRequest;
 
 /**
  * description:
  *
  * @author sunjiamin
  * @date 2018-05-14 15:24
  */
 @Component
 public class MyFilter  extends ZuulFilter{
 
     private static Logger log = LoggerFactory.getLogger(MyFilter.class);
 
     /**
      * filterType：返回一个字符串代表过滤器的类型，在zuul中定义了四种不同生命周期的过滤器类型，具体如下：
      * pre：路由之前
      * routing：路由之时
      * post： 路由之后
      * error：发送错误调用
      * @return
      */
     @Override
     public String filterType() {
         return "pre";
     }
 
     /**
      * filterOrder：过滤的顺序
      * @return
      */
     @Override
     public int filterOrder() {
         return 0;
     }
 
     /**
      * shouldFilter：这里可以写逻辑判断，是否要过滤，本文true,永远过滤。
      * @return
      */
     @Override
     public boolean shouldFilter() {
         return true;
     }
 
     /**
      * run：过滤器的具体逻辑。可用很复杂，包括查sql，nosql去判断该请求到底有没有权限访问。
      * @return
      * @throws ZuulException
      */
     @Override
     public Object run() throws ZuulException {
 
         RequestContext ctx = RequestContext.getCurrentContext();
         HttpServletRequest request = ctx.getRequest();
         log.info(String.format("%s >>> %s", request.getMethod(), request.getRequestURL().toString()));
         Object accessToken = request.getParameter("token");
         if(accessToken == null) {
             log.warn("token is empty");
             ctx.setSendZuulResponse(false);
             ctx.setResponseStatusCode(401);
             try {
                 ctx.getResponse().getWriter().write("token is empty");
             }catch (Exception e){}
 
             return null;
         }
         log.info("ok");
         return null;
     }
 }


```
  
 这时访问：http://localhost:7077/api-a/hi?name=jojo ；网页显示：
 
    token is empty
 
 访问 http://localhost:7077/api-a/hi?name=jojo&token=22 ； 
 网页显示：
 
    hi joj0,i am from port:7072

 
 ----------------------------------华丽的分割线--------------------------------------------------------------------------
   
   
 
 **第七篇：高可用分布式配置中心(Spring Cloud Config)**
 
 **7.1 简介**
 
 在分布式系统中，由于服务数量巨多，为了方便服务配置文件统一管理，实时更新，所以需要分布式配置中心组件。在Spring Cloud中，
 有分布式配置中心组件spring cloud config ，它支持配置服务放在配置服务的内存中（即本地），也支持放在远程Git仓库中。在spring cloud config 
 组件中，分两个角色，一是config server，二是config client。
 
 **7.2 新建spring-cloud-config-server** 
  
   pom文件：
   
    	<dependencies>
    		<dependency>
    			<groupId>org.springframework.cloud</groupId>
    			<artifactId>spring-cloud-config-server</artifactId>
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
    	
    	
 **7.3 修改启动类**
 
 在程序的入口Application类加上@EnableConfigServer注解开启配置服务器的功能,
 @EnableEurekaClient 开启注册功能，将配置作为一个服务注册到注册中心
 
 ```java
/**
 * @author sunjiamin
 */
@SpringBootApplication
@EnableConfigServer
@EnableEurekaClient
public class SpringCloudConfigServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudConfigServerApplication.class, args);
	}
}
```

7.4 修改配置中心

        spring.application.name=config-server
        server.port=7078
        
        #指定服务注册地址
        eureka.client.service-url.defaultZone = http://localhost:7070/eureka/
        eureka.instance.hostname = localhost
        
        #配置git仓库地址
        spring.cloud.config.server.git.uri=https://github.com/sunjiamin/SpringcloudConfig/ 
        #配置仓库路径
        spring.cloud.config.server.git.searchPaths=respo
        #配置仓库的分支
        spring.cloud.config.label=master
        
        #如果Git仓库为公开仓库，可以不填写用户名和密码，如果是私有仓库需要填写
        #访问git仓库的用户名
        #spring.cloud.config.server.git.username=your username
        #访问git仓库的用户密码
        #spring.cloud.config.server.git.password=your password
        
        
   访问 http://localhost:7078/config-client-dev.properties ，得到结果：
    
            democonfigclient.message: hello spring io
            foo: foo version 2
            
   
   证明配置服务中心可以从远程程序获取配置信息。
   http请求地址和资源文件映射如下:
       
       /{application}/{profile}[/{label}]
       /{application}-{profile}.yml
       /{label}/{application}-{profile}.yml
       /{application}-{profile}.properties
       /{label}/{application}-{profile}.properties  
 
 
 
 **7.5 构建一个config client** 
 
  新建项目： spring-cloud-config-client
  
  pom文件：
  
        	<dependencies>
        		<dependency>
        			<groupId>org.springframework.boot</groupId>
        			<artifactId>spring-boot-starter-web</artifactId>
        		</dependency>
        		<dependency>
        			<groupId>org.springframework.cloud</groupId>
        			<artifactId>spring-cloud-starter-config</artifactId>
        		</dependency>
        		<dependency>
        			<groupId>org.springframework.cloud</groupId>
        			<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        		</dependency>
                <!--<dependency>-->
                    <!--<groupId>org.springframework.cloud</groupId>-->
                    <!--<artifactId>spring-cloud-starter-bus-amqp</artifactId>-->
                <!--</dependency>-->
        
                <!--<dependency>-->
                    <!--<groupId>org.springframework.cloud</groupId>-->
                    <!--<artifactId>spring-cloud-starter-bus-kafka</artifactId>-->
                <!--</dependency>-->
        
        		<dependency>
        			<groupId>org.springframework.boot</groupId>
        			<artifactId>spring-boot-starter-test</artifactId>
        			<scope>test</scope>
        		</dependency>
        	</dependencies>
        	
        	
        	
  **7.6 配置文件bootstrap.properties**
  
  注意 这里是  bootstrap.properties 文件，不是application.properties .bootstrap.properties文件是系统级配置，
  加载时间比application.properties早
  
    spring.application.name=config-client
    #指明远程仓库的分支
    spring.cloud.config.label=master
    spring.cloud.config.profile=dev
    #指明配置服务中心的网址
    #spring.cloud.config.uri= http://localhost:7078/
    server.port=7079
    
    #指定服务注册地址
    eureka.client.service-url.defaultZone = http://localhost:7070/eureka/
    eureka.instance.hostname = localhost
    
    #从配置中心读取文
    spring.cloud.config.discovery.enabled=true
    #配置中心的servieId，即服务名
    spring.cloud.config.discovery.serviceId=config-server
    
  #spring.cloud.config.uri= http://localhost:7078/ 
  这里是注释掉的，如果不是服务注册模式，单机情况下，可直接使用这个就可以，不需要配置 spring.cloud.config.discovery.serviceId=config-server
  在读取配置文件不再写ip地址，而是服务名，这时如果配置服务部署多份，通过负载均衡，从而高可用。
  
  **7.7 修改启动类**
  
  @EnableEurekaClient 开启服务注册功能。
  
  ```java
/**
 * @author sunjiamin
 */
@SpringBootApplication
@EnableEurekaClient
public class SpringCloudConfigClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudConfigClientApplication.class, args);
	}
}

```
  
  **7.8 新建controller 访问**
  
  ```java
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
```
  
  
  浏览器 访问 http://127.0.0.1:7079/hi
  
    foo version 2
  
  
  
  这就说明，config-client从config-server获取了foo的属性，而config-server是从git仓库读取的,
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  