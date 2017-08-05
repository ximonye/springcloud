## Config 分布式配置中心(Spring Cloud Config)

- 配置中心(Config服务器端)
    - 引入依赖

        ```xml
        <!-- 配置中心 -->
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-config-server</artifactId>
		</dependency>

        <!-- 用于配置中心访问账号认证 -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-security</artifactId>
		</dependency>

        <!--向eureka注册，服务化配置中心-->
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-eureka</artifactId>
		</dependency>
        ```
    - 启动类添加`@EnableConfigServer`，开启服务发现则还要加`@EnableDiscoveryClient`
    - 配置文件

        ```yml
        spring:
          cloud:
            config:
              server:
                git:
                  # 可以使用占位符{application}、{profile}、{label}
                  uri: https://git.oschina.net/smalle/spring-cloud-config-test.git
                  # 搜索此git仓库的配置文件目录
                  search-paths: config-repo
                  username: smalle
                  password: aezocn

          server:
            port: 7000

          security:
            basic:
              enabled: true # 开启权限验证(默认是false)
            user:
              name: smalle
              password: smalle

          # eureka客户端配置
          eureka:
            client:
              serviceUrl:
                defaultZone: http://smalle:smalle@localhost:8761/eureka/
            instance:
              # 启用ip访问
              prefer-ip-address: true
              instanceId: ${spring.application.name}:${spring.application.instance_id:${server.port}}
        ```
    - 在git仓库的config-repo目录下添加配置文件: `consumer-movie-ribbon.yml`(写如配置如：from: git-default-1.0. 下同)、`consumer-movie-ribbon-dev.yml`、`consumer-movie-ribbon-test.yml`、`consumer-movie-ribbon-prod.yml`，并写入参数
    - 访问：`http://localhost:7000/consumer-movie-ribbon/prod/master`即可获取应用为`consumer-movie-ribbon`，profile为`prod`，git分支为`master`的配置数据(`/{application}/{profile}/{label}`)
        - 某application对应的配置命名必须为`{application}-{profile}.yml`，其中`{profile}`和`{label}`可在对应的application的`bootstrap.yml`中指定
        - 访问配置路径后，程序默认会将配置数据下载到本地，当git仓库不可用时则获取本地的缓存数据
        - 支持git/svn/本地文件等
- 客户端配置映射
    - 引入依赖

        ```xml
        <!-- 配置中心客户端 -->
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-config</artifactId>
		</dependency>
        ```
    - 添加`bootstrap.yml`配置文件(不能放在application.yml中)

        ```yml
        # bootstrap.yml其优先级高于application.yml
        spring:
          # application:
          #  name: consumer-movie-ribbon
          cloud:
            config:
              # (1) config server地址
              # uri: http://localhost:7000/
              # (2) 配置中心实行服务化(向eureka注册了自己)，此处要开启服务发现，并指明配置中心服务id
              discovery:
                enabled: true
                service-id: config-server
              profile: prod
              label: master
              # 如果配置中心开启了权限验证，此处填写相应的用户名和密码
              username: smalle
              password: smalle

        # eureka客户端配置(使用了spring cloud config, 则eureka的配置必须写在bootstrap.yml中，否则报找不到config server )
        eureka:
          client:
            serviceUrl:
              defaultZone: http://smalle:smalle@localhost:8761/eureka/
          instance:
            # 启用ip访问
            prefer-ip-address: true
            instanceId: ${spring.application.name}:${spring.application.instance_id:${server.port}}
        ```
    - 测试程序

        ```java
        // @RefreshScope // 之后刷新config后可重新注入值
        @RestController
        public class ConfigController {
            @Value("${from:none}")
            private String from;

            // 测试从配置中心获取配置数据，访问http://localhost:9000/from
            @RequestMapping("/from")
            public String from() {
                return this.from; // 会从git仓库中读取配置数据
            }
        }
        ```
- 动态刷新配置(可获取最新配置信息的git提交)
    - config客户端重启会刷新配置(重新注入配置信息)
    - 动态刷新
        - 在需要动态加载配置的Bean上加注解`@RefreshScope`
        - 给 **config client** 加入权限验证依赖(`org.springframework.boot/spring-boot-starter-security`)，并在对应的application.yml中开启验证
            - 否则访问`/refresh`端点会失败，报错：`Consider adding Spring Security or set 'management.security.enabled' to false.`(需要加入Spring Security或者关闭端点验证)
        - 对应的需要注入配置的类加`@RefreshScope`
        - `POST`请求`http://localhost:9000/refresh`(将Postman的Authorization选择Basic Auth和输入用户名/密码)
        - 再次访问config client的 http://localhost:9000/from 即可获取最新git提交的数据(由于开启了验证，所有端点都需要输入用户名密码)
            - 得到如`["from"]`的结果(from配置文件中改变的key)
- 动态加载网关配置
    - 在`api-gateway-zuul`服务中同上述一样加`bootstrap.yml`，并对eureka和config server进行配置
    - 在`application.yml`对

        ```yml
        zuul:
          routes:
            api-movie:
              path: /api-movie/**
              serviceId: consumer-movie-ribbon
              # 如果consumer-movie-ribbon服务开启了权限验证，则需要防止zuul将头信息(Cookie/Set-Cookie/Authorization)过滤掉了.(多用于API网关下的权限验证等服务)
              # 此方法是对指定规则开启自定义敏感头. 还有一中解决方法是设置路由敏感头为空(则不会过滤任何头信息)：zuul.routes.<route>.sensitiveHeaders=
              customSensitiveHeaders: true

        # 为了动态刷新配置(spring cloud config)，执行/refresh端点(此端点需要加入Spring Security或者关闭端点验证)
        security:
          basic:
            enabled: true
          user:
            name: smalle
            password: smalle
        ```
    - 在git仓库中加入`api-gateway-zuul-prod.yml`等配置文件，并加入配置

        ```yml
        zuul:
          routes:
            api-movie:
              path: /api-movie-config/**
              serviceId: consumer-movie-ribbon
        ```
    - `POST`请求`http://localhost:5555/refresh`即可刷新`api-gateway-zuul`的配置，因此动态加载了路由规则zuul.routes.api-movie

