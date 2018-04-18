## Spring Cloud Quick Start - 服务使用远程配置 

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

### 新建远程配置文件
以服务`service-hi`为例,将service-hi相关的配置信息写入`service-hi-dev.yml`  
``` yml 
testfoo: testfoo123
db-config:
  spring:
    jpa:
      pro1: value1
      pro2: value2
eureka:
  client:
    serviceUrl:
      defaultZone: http://192.168.3.181:8761/eureka/
server:
  port: 8762  
```    
建立完成后，将`service-hi-dev.yml` 提交到远程git仓库：`https://github.com/xxxxxxx.git`的`cloud-config-files`目录内

### 服务本地配置   
`service-hi`服务本地的`bootstrap.yml`配置信息为：  
主要配置`config-server`的url地址即可。
``` yml  
spring:
  application:
    name: service-hi
  cloud:
    config:
      uri: http://192.168.3.181:8801/
      profile: dev
      label: master
```

#### 运行    
启动`service-hi`服务后，发现能正常获取配置信息并启动应用。





 

