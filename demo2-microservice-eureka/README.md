## Eureka服务发现

- 服务注册与发现

    ![服务注册与发现](服务注册与发现.png)

    - 服务发现方式 [^2]
        - 客户端发现：Eureka、Zk
        - 服务端发现：Consul + nginx
    - 服务注册表是一个记录当前可用服务实例的网络信息的数据库，是服务发现机制的核心。服务注册表提供查询API和管理API，使用查询API获得可用的服务实例，使用管理API实现注册和注销

- 简介：Eureka是`Netflix`开发的服务发现框架，本身是一个基于REST的服务，主要用于定位运行在AWS域中的中间层服务，以达到负载均衡和中间层服务故障转移的目的。Spring Cloud将它集成在其子项目`spring-cloud-netflix`中，以实现Spring Cloud的服务发现功能
- 架构图

    ![eureka](eureka.png)

    - AWS概念：us-east-1c、us-east-1d等是zone，它们都属于us-east-1这个region
    - 在应用启动后，将会向Eureka Server发送心跳（默认周期为30秒）。如果Eureka Server在多个心跳周期内没有接收到某个节点的心跳，Eureka Server将会从服务注册表中把这个服务节点移除（默认90秒）
    - Eureka还提供了客户端缓存的机制，即使所有的Eureka Server都挂掉，客户端依然可以利用缓存中的信息消费其他服务的API
- eureka server
    - 引入依赖

        ```xml
        <dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-eureka-server</artifactId>
		</dependency>

        <!-- 用于注册中心访问账号认证，非必须 -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-security</artifactId>
		</dependency>
        ```
    - 在Application.java中加注解`@EnableEurekaServer`
    - application.yml配置

        ```yml
        server:
          port: 8761

        # 引入了spring-boot-starter-security则会默认开启认证
        security:
          basic:
            enabled: true #开启eureka后台登录认证
          # 不配置user，则默认的用户名为user，密码为自动生成(在控制台可查看)
          user:
            name: smalle
            password: smalle

        eureka:
          instance:
            hostname: localhost
          client:
            # eureka server默认也是一个eureka client.以下两行仅将此App当成eureka server，不当成eureka client(由于是单点测试)
            register-with-eureka: false
            fetch-registry: false
            # 将eureka注册到哪个url
            serviceUrl:
              defaultZone: http://user:password@${eureka.instance.hostname}:${server.port}/eureka/
        ```
    - 后台地址：`http://localhost:8761`
- eureka client
    - 引入依赖

        ```xml
        <dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-eureka</artifactId>
		</dependency>
        ```
    - 在Application.java中加注解`@EnableEurekaClient`
    - application.yml配置

        ```yml
        # eureka客户端配置
        eureka:
          client:
            serviceUrl:
              defaultZone: http://smalle:smalle@localhost:8761/eureka/
          instance:
            # 启用ip访问eureka server(默认是使用主机名进行访问)
            prefer-ip-address: true
            # 实例id
            instanceId: ${spring.application.name}:${spring.application.instance_id:${server.port}}
        ```
    - 示例请看源码
        - 示例中使用H2数据库，IDEA连接方式：path:`mem:testdb`, user:`sa`, password:空, url:`jdbc:h2:mem:testdb`, 使用`Embedded`或`In-memory`方式连接