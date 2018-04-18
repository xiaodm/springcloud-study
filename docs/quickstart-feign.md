## Spring Cloud Quick Start - 服务消费Feign

### 实验环境说明  

* Spring Cloud - Finchley.M9
* Spring Boot - 2.0.0.RELEASE     
* Maven(实验环境为了方便，使用maven进行) 

### 服务项目名称
服务注册中心：test-eureka-server  
服务1：test-eureka-client1   
__服务2：test-eureka-client2(本文)__   
路由服务：test-zuul    
统一配置配置服务：cloud-config-server  
Spring Boot管理服务：boot-admin

### 服务消费者    

#### 服务消费者项目
基于之前创建的Spring Cloud项目：
`test-eureka-client2`       

#### 引入`eureka client` 和 `openfeign`依赖包    
``` xml    
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
		</dependency>  
			<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-openfeign</artifactId>
		</dependency>
```  

#### 完整`pom`配置信息  
``` xml  
 <?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>

	<groupId>test.example</groupId>
	<artifactId>eureka-client2</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<packaging>jar</packaging>

	<name>eureka-client2</name>
	<description>Demo project for Spring Boot</description>

	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>2.0.0.RELEASE</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>

	<properties>
		<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
		<project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
		<java.version>1.8</java.version>
		<spring-cloud.version>Finchley.M9</spring-cloud.version>
	</properties>

	<dependencies>
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>

		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-openfeign</artifactId>
		</dependency>

	</dependencies>

	<dependencyManagement>
		<dependencies>
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-dependencies</artifactId>
				<version>${spring-cloud.version}</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
			<dependency>
				<groupId>org.springframework.cloud</groupId>
				<artifactId>spring-cloud-openfeign</artifactId>
				<version>2.0.0.M1</version>
				<type>pom</type>
				<scope>import</scope>
			</dependency>
		</dependencies>
	</dependencyManagement>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>

	<repositories>
		<repository>
			<id>spring-milestones</id>
			<name>Spring Milestones</name>
			<url>https://repo.spring.io/milestone</url>
			<snapshots>
				<enabled>false</enabled>
			</snapshots>
		</repository>
	</repositories>


</project>



```  

#### 配置文件  
`bootstrap.yml`中配置信息：    
主要定义了服务名称
``` yml
eureka:
  client:
    serviceUrl:
      defaultZone: http://192.168.3.181:8761/eureka/
server:
  port: 8764
spring:
  application:
    name: service-feign1
```  

#### 启动注解配置    
添加`@EnableEurekaClient`  和 `@EnableFeignClients`

``` java  
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class EurekaClient1Application {

	public static void main(String[] args) {
		SpringApplication.run(EurekaClient1Application.class, args);
	}
}
  
```       

#### 定义调用目标服务的接口    
``` java  
@FeignClient(value = "service-hi",fallback = HiHystric.class)
public interface IServiceHi {
    @RequestMapping(value = "/hi", method = RequestMethod.GET)
    String sayHiFromClientOne(@RequestParam(value = "name") String name);
}  
```      

* 使用@FeignClient("service-hi")注解来绑定该接口对应service-hi服务
* 通过Spring MVC的注解来配置service-hi服务下的具体实现。sayHiFromClientOne上的RequestMapping表示要访问的目标服务路由为`/hi`，传递的参数为`name`
  
#### Controller层调用上面定义的接口 
``` java  
public class HiController {
    Logger log = LoggerFactory.getLogger(HiController.class);

    @Autowired
    IServiceHi iServiceHi;
    @RequestMapping(value = "/hic",method = RequestMethod.GET)
    public String sayHi(@RequestParam String name){
        log.info("service2 call sayHi");
        return "service-feign1 get by feign: " + iServiceHi.sayHiFromClientOne(name);
    }
}  
```  

#### 运行  
启动服务后，在url上浏览注册中心：http://192.168.3.181:8761/      
发现服务service-feign1也已注册到Eureka Server。  
执行`http://localhost:8764/hic?name=123`访问后，发现能调用到目标服务`service-hi`并返回正确结果。 




 

