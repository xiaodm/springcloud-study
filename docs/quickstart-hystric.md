## Spring Cloud Quick Start - 服务熔断Hystric
以Feign消费服务为例。

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

#### 服务熔断实验项目
还是基于之前的`test-eureka-client2`     
因为Feign已经内置集成了Hystric ，不需要额外引入Hystric包即可实验熔断器功能。


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
开启 `feign.hystrix.enabled = true `   

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
feign:
  hystrix:
    enabled: true
```  

#### 启动注解配置    
添加`@EnableHystrix` 

``` java  
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableHystrix
public class EurekaClient2Application {

	public static void main(String[] args) {
		SpringApplication.run(EurekaClient2Application.class, args);
	}
}

  
```       

#### 定义Feign - Hystrix - Fallbacks    
``` java  
@Component
public class HiHystric  implements IServiceHi{
    @Override
    public String sayHiFromClientOne(String name) {
        return "sorry,invoke fail";
    }
}
```      
  
#### Feign调用接口 - fallback特性
``` java  
@FeignClient(value = "service-hi",fallback = HiHystric.class)
public interface IServiceHi {
    @RequestMapping(value = "/hi", method = RequestMethod.GET)
    String sayHiFromClientOne(@RequestParam(value = "name") String name);
}
```  
> Hystrix支持Fallbacks能力： 当目标服务因异常致回路打开或调用异常时，HiHystric将会被执行。

#### 运行  
* 启动服务后，在url上浏览注册中心：http://192.168.3.181:8761/      
* 发现服务service-feign1也已注册到Eureka Server。  
* 执行`http://localhost:8764/hic?name=123`访问后，发现能调用到目标服务`service-hi`并返回正确结果。
* 如果关闭service-hi服务，继续访问当前服务上方地址后，发现不会出现异常页面，而是返回HiHystric内定义的fallback方法执行结果 。 



 

