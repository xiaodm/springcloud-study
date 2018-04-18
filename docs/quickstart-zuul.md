## Spring Cloud Quick Start - 服务路由Zuul  
Netflix使用Zuul做了以下事情 :  

* Authentication
* Insights
* Stress Testing
* Canary Testing
* Dynamic Routing
* Service Migration
* Load Shedding
* Security
* Static Response handling
* Active/Active traffic management    

这里我们只是简单介绍下`Zuul`的入门-路由及过滤器使用

### 实验环境说明  

* Spring Cloud - Finchley.M9
* Spring Boot - 2.0.0.RELEASE     
* Maven(实验环境为了方便，使用maven进行) 

### 服务项目名称
服务注册中心：test-eureka-server  
服务1：test-eureka-client1   
服务2：test-eureka-client2   
__路由服务：test-zuul(本文)__    
统一配置配置服务：cloud-config-server  
Spring Boot管理服务：boot-admin

### 服务消费者

#### 创建服务路由项目
还是基于之前的`test-zuul`       

#### 引入zuul依赖包    
``` xml    
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-zuul</artifactId>
		</dependency>
```  
 
#### 完整`pom`配置信息  
``` xml  
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>

	<groupId>test.example</groupId>
	<artifactId>cloud-zuul</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<packaging>jar</packaging>

	<name>cloud-zuul</name>
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
		<spring-cloud.version>Finchley.M8</spring-cloud.version>
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
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-zuul</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
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
`bootstrap.yml`中配置信息，主要进行路由的配置：     
 
``` yml
eureka:
  client:
    serviceUrl:
      defaultZone: http://192.168.3.181:8761/eureka/
server:
  port: 8770
spring:
  application:
    name: service-zuul

zuul:
  routes:
    api-a:
      path: /api1/**
      serviceId: service-hi
    api-b:
      path: /api2/**
      serviceId: service-feign1
```    
> 如上配置：  
以`/api1/**`为前缀的请求将路由到`service-hi ` 
以`/api2/**`为前缀的请求将路由到`service-feign1 `  

#### 启动注解配置    
添加`@EnableZuulProxy` 

``` java  
@SpringBootApplication
@EnableZuulProxy
@EnableEurekaClient
public class CloudZuulApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudZuulApplication.class, args);
	}
} 
```       
    

#### 运行  
* 启动服务后，在url上浏览注册中心：http://192.168.3.181:8761/      
* 发现服务service-zuul也已注册到Eureka Server。  
* 执行`http://localhost:8770/api2/hic?name=123`访问后，发现能调用到目标服务并返回正确结果。
  
#### 问题注意点      
如果关闭service-hi服务，继续访问当前服务上方地址后，发现会出现异常页。即使service-feign1配置了熔断机制。  
此问题与Zuul的超时机制有关系，添加超时配置：  
```  
ribbon:
  ReadTimeout: 5000
  ConnectTimeout: 5000
```     

###  Zuul过滤器用法  
如，这里我们建立一个过滤器，验证url必须有token字段  
``` java  
package test.example;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class ZuulFilterTest extends ZuulFilter {
    private static Logger log = LoggerFactory.getLogger(ZuulFilterTest.class);
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

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

> 运行后，如果url没有token参数，会返回401错误信息。






 

