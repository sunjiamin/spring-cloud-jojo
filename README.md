
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
 
 
 
 
 
 ----------------------------------华丽的分割线--------------------------------------------------------------------------
 
 
 
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



 ----------------------------------华丽的分割线--------------------------------------------------------------------------
 
 
 **3）服务消费者（rest+ribbon）**
 
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
  
  
 
 
 
 **4）服务消费者（Feign）**
 
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
  
    
    
    
 **5)断路器（Hystrix）**
 
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
  
  
  
  
  
  
  
  
  
 
 
 
 
 
 
 
 
 
 
 
 