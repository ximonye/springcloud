## Feign声明式服务调用

- 简介
- 基本使用(服务消费者)
    - 引入依赖

        ```xml
        <!--Feign声明式服务调用-->
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-feign</artifactId>
		</dependency>
        ```
    - 启动类加注解`@EnableFeignClients`
    - 定义FeignClient接口Bean

        ```java
        // 此服务消费者需要调用的服务声明
        @FeignClient("provider-user")
        public interface UserFeignClient {
            // Feign不支持@GetMapping, @PathVariable必须指明参数值
            @RequestMapping(method = RequestMethod.GET, value = "/simple/{id}")
            User findById(@PathVariable("id") Long id);

            @RequestMapping(method = RequestMethod.POST, value = "/feign-post")
            User postFeignUser(@RequestBody User user);
        }
        ```
    - 在controller中直接调用接口中方法(此时不直接调用restTemplate)


