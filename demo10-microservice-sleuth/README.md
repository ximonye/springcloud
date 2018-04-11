## Sleuth 分布式服务跟踪(Spring Cloud Sleuth)

> 服务跟踪以consumer-movie-ribbon和provider-user为例。
> 最好先启动eureka-server、config-server(需要启动rabbitmq)、zipkin-server(需要启动mysql)

- 简介
    - 用来跟踪每个请求在全链路调用的过程，可快速发现每条链路上的性能瓶颈
    - 构建后会自动监控RabbitMQ/Kafka传递的请求、Zuul代理传递的请求、RestTemplate发起的请求
- 入门案例
    - 引入依赖(在生产者和消费者中都引入)

        ```xml
        <!-- 服务跟踪 -->
    	<dependency>
    		<groupId>org.springframework.cloud</groupId>
    		<artifactId>spring-cloud-starter-sleuth</artifactId>
    	</dependency>
        ```
    - 访问生产者`http://localhost:8000/simple/1`，控制台输出类似`TRACE [provider-user,0ec3c3b4ee83efd5,0ec3c3b4ee83efd5,false]`的信息，信息中括号的值分别代表：应用名称、Trace ID(一个请求链路的唯一标识)、Span ID(一个基本工作单元，如一个Http请求)、是否将信息收集到Zipkin等服务中来收集和展示
    - 添加配置`logging.level.org.springframework.web.servlet.DispatcherServlet=DEBUG`可打印更多信息
- 请求头信息：`org.springframework.cloud.sleuth.Span`
- 抽样收集
    - Spring Cloud Sleuth收集策略通过Sampler接口实现(通过isSampled返回boolean判断是否收集)，默认会使用PercentageBasedSampler实现的抽样策略
    - `spring.sleuth.sampler.percentage=0.1` 代表收集10%的请求跟踪信息
    - 可收集请求头信息中包含某个tag的样品

        ```java
        public class TagSampler implements Sampler {
            private String tag;

            public TagSampler(String tag) {
                this.tag = tag;
            }

            @Override
            public boolean isSampled(Span span) {
                return span.tags().get(tag) != null;
            }
        }
        ```
- 与Zipkin整合(推荐)
    - 建立zipkin server
        - 新建服务`zipkin-server`
        - 引入依赖

            ```xml
            <!-- eureka客户端 -->
    		<dependency>
    			<groupId>org.springframework.cloud</groupId>
    			<artifactId>spring-cloud-starter-eureka</artifactId>
    		</dependency>

    		<!-- Zipkin创建sleuth主题的stream -->
    		<dependency>
    			<groupId>org.springframework.cloud</groupId>
    			<artifactId>spring-cloud-starter-stream-rabbit</artifactId>
    		</dependency>

    		<!--包含Zipkin服务的核心依赖(zipkin-server)、消息中间件的核心依赖、扩展数据存依赖等. 不包含Zipkin前端界面依赖-->
    		<dependency>
    			<groupId>org.springframework.cloud</groupId>
    			<artifactId>spring-cloud-sleuth-zipkin-stream</artifactId>
    		</dependency>
    		<!-- Zipkin前端界面依赖 -->
    		<dependency>
    			<groupId>io.zipkin.java</groupId>
    			<artifactId>zipkin-autoconfigure-ui</artifactId>
    			<scope>runtime</scope>
    		</dependency>

    		<!-- 存储Zipkin跟踪信息到mysql(可选. 使用mysql后, Zipkin前端界面显示的数据是通过Restful API从数据库中获取的. 不使用数据存储在Zipkin内部) -->
    		<dependency>
    			<groupId>org.springframework.boot</groupId>
    			<artifactId>spring-boot-starter-data-jpa</artifactId>
    		</dependency>
    		<dependency>
    			<groupId>mysql</groupId>
    			<artifactId>mysql-connector-java</artifactId>
    		</dependency>
            ```
        - 启动类加注解`@EnableEurekaClient`、`@EnableZipkinStreamServer`(用stream方式启动，包含常规启动@EnableZipkinServer和创建sleuth的stream主题)
        - application.yml配置

            ```yml
            server:
              port: 9411

            spring:
              application:
                name: zipkin-server
              datasource:
                # 建表语句, 用来新建zipkin跟踪信息相关表(zipkin_spans、zipkin_annotations、zipkin_dependencies), 文件在Maven:io.zipkin.java:zipkin.storage.mysql目录下
                schema: classpath:/mysql.sql
                url: jdbc:mysql://localhost:3306/test
                username: root
                password: root
                initialize: true
                continue-on-error: true
              # 不对此服务开启跟踪
              sleuth:
                enabled: false

            # 改变zipkin日志跟踪信息存储方式为mysql(测试也可不使用mysql存储)
            zipkin:
              storage:
                type: mysql
            ```
    - 被跟踪的应用(在生产者和消费者中都引入)
        - 引入依赖

            ```xml
            <!--服务跟踪与Zipkin整合(可选)-->
    		<dependency>
    			<groupId>org.springframework.cloud</groupId>
    			<artifactId>spring-cloud-starter-zipkin</artifactId>
    		</dependency>
            ```
        - 如果zipkin没有使用eureka， 则需要在application.yml中添加`spring.zipkin.base-url: http://localhost:9411/`(zipkin server地址)
    - 进入到zipkin server后台界面查看跟踪信息：http://localhost:9411/(跟踪信息可能会有延迟)
- ELK日志分析系统(Logstash)
    - ELK平台包含：ElasticSerch(分布式搜索引擎)、Logstash(日志收集-过滤-存储)、Kibana(界面展现)三个开源工具。(与Zipkin类似，二者不建议同时使用)
    - 引入依赖

        ```xml
        <!--服务跟踪与ELK日志分析平台整合(可选，此包用于Logstash收集日志)-->
		<dependency>
			<groupId>net.logstash.logback</groupId>
			<artifactId>logstash-logback-encoder</artifactId>
			<version>4.6</version>
		</dependency>
        ```
    - 将spring.application.name配置到bootstrap.yml中
    - 在resources目录加logback-spring.xml文件(请看源码)



