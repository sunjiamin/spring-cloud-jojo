
#1）eureka 服务注册

 module:spring-cloud-eureka-server
 
 ##1.添加Pom引用
 
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
 
 
 ##2.启动一个服务注册中心
   只需要一个注解@EnableEurekaServer，这个注解需要在springboot工程的启动application类上加
 
    @EnableEurekaServer
    @SpringBootApplication
    public class SpringCloudEurekaServerApplication {
 	  public static void main(String[] args) {
 	    	SpringApplication.run(SpringCloudEurekaServerApplication.class, args);
 	  }
    }
  

 ##3.添加配置
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
 
 

   