## Spring Cloud Quick Start - 统一配置管理服务 

### 实验环境说明  

* Spring Cloud - Finchley.M9
* Spring Boot - 2.0.0.RELEASE     
* Maven(实验环境为了方便，使用maven进行) 

### 服务项目名称
服务注册中心：test-eureka-server  
__服务1：test-eureka-client1(本文)__     
服务2：test-eureka-client2  
路由服务：test-zuul    
__统一配置配置服务：cloud-config-server(本文)__  
Spring Boot管理服务：boot-admin


### 配置中心仓库    
Spring Cloud Config支持的配置存储方式为：  
* 本地文件  
* svn  
* git  
这里我们选择git作为配置信息存储(GitHub/GitLab)

### 配置服务端   

#### 创建服务提供者项目
创建一个Spring Cloud项目：
`cloud-config-server`       

#### 引入config server和eureka client依赖包    
``` xml    
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-config-server</artifactId>
		</dependency>
		
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
		</dependency>
```  

#### 完整`pom`配置信息  
``` xml  
 <?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>

	<groupId>test.example</groupId>
	<artifactId>cloud-config-server</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<packaging>jar</packaging>

	<name>cloud-config-server</name>
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
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-config-server</artifactId>
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
`bootstrap.yml`中配置信息：    
主要定义了服务名称，注册中心地址  

``` yml
eureka:
  client:
    serviceUrl:
      defaultZone: http://192.168.3.181:8761/eureka/
server:
  port: 8801
spring:
  application:
    name: config-server
spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/xxxxxxx.git
          username: xxxxx
          password: xxxxxxxxx
          searchPaths: cloud-config-files
      label: master
```  

#### 启动注解配置    
添加`@EnableConfigServer`  

``` java  
@SpringBootApplication
@EnableConfigServer
public class CloudConfigServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudConfigServerApplication.class, args);
	}
}

  
```       

#### 运行  
URL与配置文件的映射关系如下：

* /{application}/{profile}[/{label}]
* /{application}-{profile}.yml
* /{label}/{application}-{profile}.yml
* /{application}-{profile}.properties
* /{label}/{application}-{profile}.properties
上面的url会映射{application}-{profile}.properties对应的配置文件，{label}对应git上不同的分支，默认为master。

我们可以尝试构造不同的url来访问不同的配置内容，比如：要访问dev分支，service-hi应用的prod环境，可以通过这个url：http://localhost:7001/service-hi/prod/dev




 

