## Zuul (API GateWay：网关)

- 简介
- 基本使用
    - 引入依赖

        ```xml
        <!-- API网关。包含actuator、hystrix、ribbon -->
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-zuul</artifactId>
		</dependency>
        ```
    - 启动类声明`@EnableZuulProxy`
    - 基础配置application.yml

        ```yml
        zuul:
          # 忽略表达式。当遇到路径中有admin的不进行路由
          # ignored-patterns: /**/admin/**
          # 路由前缀
          # prefix: /api
          # zuul默认会过滤路由前缀(strip-prefix=true)，此处是关闭此过滤
          # strip-prefix: false
          routes:
            # 通配符(ant规范)：? 代表一个任意字符，* 代表多个任意字符，** 代表多个任意字符且支持多级目录
            # 此处路径在配置文件中越靠前的约优先（系统将所有路径放到LinkedHashMap中，当匹配到一个后就终止匹配）
            # 现在可以同时访问http://localhost:5555/consumer-movie-ribbon/movie/1 和 http://localhost:5555/api-movie/movie/1
            # api-movie为规则名, 可通过spring cloud config进行动态加载(覆盖)
            api-movie:
              path: /api-movie/**
              # 从eureka中获取此服务(spring.application.name)的地址(面向服务的路由)
              serviceId: consumer-movie-ribbon
            api-user:
              path: /api-user/**
              serviceId: provider-user
            # 本地跳转(当访问/api-local/**的时候，则会转到当前应用的/local/**的地址)
            # api-local:
            #   path: /api-local/**
            #   url: forward:/local
            # 禁用过滤器：zuul.<FilterClassName>.<filterType>.disable=true
            # AccessFilter:
            #   pre:
            #     disable: true
        ```
- 自定义路由规则

    ```java
    @Bean
    public PatternServiceRouteMapper serviceRouteMapper() {
        // 将serviceName-v1映射成/v1/serviceName. 未匹配到则按照原始的
        return new PatternServiceRouteMapper(
                "(?<name>^.+)-(?<version>v.+$)",
                "${version}/${name}");
    }
    ```
- 过滤器
    - Zuul过滤器核心处理器(`com.netflix.zuul.FilterProcessor`)
    - 核心过滤器处理(对应包`org.springframework.cloud.netflix.zuul.filters`)
    - 自定义过滤器

        ```java
        @Component
        public class AccessFilter extends ZuulFilter {
            private static Logger logger = LoggerFactory.getLogger(AccessFilter.class);

            // 过滤器类型，决定过滤器在请求的哪个生命周期中执行
            // pre：表示请求在路由之前执行
            // routing：在路由请求时被执行(调用真实服务应用时)
            // post：路由完成(服务调用完成)被执行
            // error：出错时执行
            @Override
            public String filterType() {
                return "pre";
            }

            // 多个过滤器时，控制过滤器的执行顺序（数值越小越优先）
            @Override
            public int filterOrder() {
                return 0;
            }

            // 判断该过滤器是否需要被执行(true需要执行)，可根据实际情况进行范围限定
            @Override
            public boolean shouldFilter() {
                return true;
            }

            // 过滤器的具体逻辑
            @Override
            public Object run() {
                RequestContext ctx = RequestContext.getCurrentContext();
                HttpServletRequest request = ctx.getRequest();

                logger.info("send {} request to {}", request.getMethod(), request.getRequestURL().toString()); // send GET request to http://localhost:5555/api-movie/movie/1

                Object accessToken = request.getParameter("accessToken");
                if(accessToken == null) {
                    logger.warn("access token is empty, add parameter like: accessToken=smalle");
                    ctx.setSendZuulResponse(false); // 令zuul过滤此请求，不进行路由
                    ctx.setResponseStatusCode(401);
                    ctx.setResponseBody("zuul filter");
                    return null;
                }

                logger.info("access token ok");

                // 测试异常过滤器（org.springframework.cloud.netflix.zuul.filters.post.SendErrorFilter）
                // doSomteing();

                return null;
            }

            private void doSomteing() {
                throw new RuntimeException("run error");
            }
        }
        ```
- 自定义异常信息：出现异常会forward到`/error`的端点，`/error`端点的实现来源于Spring Boot的`org.springframework.boot.autoconfigure.web.BasicErrorController`

    ```java
    // 最好使用postman等工具测试
    public class CustomErrorAttributes extends DefaultErrorAttributes {
        @Override
        public Map<String, Object> getErrorAttributes(RequestAttributes requestAttributes, boolean includeStackTrace) {
            Map<String, Object> map = super.getErrorAttributes(requestAttributes, includeStackTrace);
            map.remove("exception"); // 移除exception信息，客户端将看不到此信息
            map.put("myAttr", "hello");
            return map;
        }
    }
    ```
- 动态路由：请见分布式配置中心(Config)部分

