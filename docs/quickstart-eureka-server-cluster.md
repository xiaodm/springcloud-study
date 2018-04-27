## Spring Cloud Quick Start - 服务注册中心-集群    
这里以建立2个Eureka server，组成集群为例：  
* 192.168.3.181:8781  
* 192.168.3.181:8782

### 实验环境说明  

* Spring Cloud - Finchley.M9
* Spring Boot - 2.0.0.RELEASE     
* Maven

### 服务项目名称
__服务注册中心：test-eureka-server(本文)__  
服务1：test-eureka-client1  
服务2：test-eureka-client2  

### 服务注册中心    

#### 创建服务注册中心项目
创建一个Spring Cloud项目：
`test-eureka-server `       

#### 引入eureka server依赖包    
``` xml    
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
		</dependency>
```  

#### 完整`pom`配置信息  
``` xml  
  <?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>

	<groupId>test.example</groupId>
	<artifactId>eureka-server</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<packaging>jar</packaging>

	<name>eureka-server</name>
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
			<artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
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
建立2个配置文件`application-s1.yml`和`application-s2.yml`    
核心主要是：  
* 2个服务相互注册，`eureka.client.serviceUrl.defaultZone`分别指向对方。   
* `registerWithEureka: true、fetchRegistry: true`


`application-s1.yml`中配置信息：  
```   yml
spring:
  application:
    name: eureka-server

server:
  port: 8781

eureka:
  client:
    registerWithEureka: true
    fetchRegistry: true
    serviceUrl:
        defaultZone: http://192.168.3.181:8782/eureka/
  instance:
    preferIpAddress: true
    hostname: eureka-peer1
  server:
    waitTimeInMsWhenSyncEmpty: 0
    enableSelfPreservation: false

```    

`application-s2.yml`中配置信息：  
```   yml
spring:
  application:
    name: eureka-server

server:
  port: 8782

eureka:
  client:
    registerWithEureka: true
    fetchRegistry: true
    serviceUrl:
        defaultZone: http://192.168.3.181:8781/eureka/
  instance:
    preferIpAddress: true
    hostname: eureka-peer2
  server:
    waitTimeInMsWhenSyncEmpty: 0
    enableSelfPreservation: false

```  

#### 启动注解配置    
添加`@EnableEurekaServer`  

``` java  
@EnableEurekaServer
@SpringBootApplication
public class EurekaServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaServerApplication.class, args);
	}
}  
  
```         

#### 运行    
比如示例部署在192.168.3.181上。  
```  
java -jar .\eureka-server-0.0.1-SNAPSHOT.jar --spring.profiles.active=s1  
java -jar .\eureka-server-0.0.1-SNAPSHOT.jar --spring.profiles.active=s2  
```  
启动后，会发现192.168.3.181:8781或8782上能相互看到对方的注册信息。  


#### 客户端配置    
以之前的2个服务service2，service1为例。  
  
注册服务的配置文件中，将eureka-server的两个地址都写上即可(或使用nginx负载均衡)。   

```  
  eureka:
  client:
    serviceUrl:
#      defaultZone: http://192.168.3.181:8761/eureka/
      defaultZone: http://192.168.3.181:8781/eureka/,http://192.168.3.181:8782/eureka/
```
停止其中一个eureka-server后，发现service2，service1均能正常注册，并且可以调用。 
  


 

