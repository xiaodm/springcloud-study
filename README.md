## Spring Cloud  

* [Spring Cloud 介绍](./docs/introduce.md)
* Quick Start    
	- [服务注册Server - Eureka](./docs/quickstart-eureka-server.md)
	- [服务注册及发现Client - Eureka](./docs/quickstart-eureka-client.md)
	- [服务调用 - Feign](./docs/quickstart-feign.md)
	- [熔断 - hystrix](./docs/quickstart-hystric.md)
	- [路由服务 - Zuul](./docs/quickstart-zuul.md)
	- [配置管理中心 - Spring Cloud Config](./docs/quickstart-config.md)  
	- [使用远端配置](./docs/quickstart-service-use-config.md)
* 高级使用  
	- [分布式Eureka注册中心](./docs/quickstart-eureka-server-cluster.md)
	- Spring Cloud Config 集群 
		- 使用注册中心能力即可，待续
	- [熔断监控 - hystrix dashboard](./docs/quickstart-hystric-dashboard.md)   
		* 新版本集成dashboard stream有问题,后续补充
	- 熔断监控 - hystrix dashboard-Turbine
		* 新版本集成dashboard stream有问题,后续补充
	- [链路跟踪 - Sleuth](./docs/quickstart-sleuth.md)  
	- [链路跟踪 - Sleuth - Zipkin整合](./docs/quickstart-sleuth-zipkin.md)
	- Spring Cloud Stream
	- [监控 - Spring Boot Admin]()
	- [监控 - ELK/EFK的集成](./docs/quickstart-elk-inte.md) 
* 与docker集成  
* 与Kubernetes集成
* 其他    
	- 配置参考  
	- Spring Cloud Turbine   
	- Spring Cloud Stream  
	- Spring Cloud Security
	- _Spring Cloud Contract_  
	- _Spring Cloud Gateway_
	- _Spring Cloud Cloud Foundry_  
	
* 后续跟踪问题  
	- [graphql 集成使用](./docs/quickstart-graphql.md)
	- spring cloud 组件自己的集群，zuul    
		* 直接注册多个即可（同一台宿主不同端口注册，或不同宿主InstanceId注册）
	- euraka集群
		* 参加上方实现 - 分布式Eureka注册中心
	- 服务联调
	- 时间同步 - sleuth
		* 经测试时间不同步没影响，以traceId起点时间进行的链路记录。（但使用zipkin-UI查询时要注意时间选择）

* 参考