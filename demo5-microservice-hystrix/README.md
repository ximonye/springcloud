## Hystrix服务容错保护(断路器)

- 简介
- 基本使用(服务消费者)
    - 引入依赖

        ```xml
        <!--服务容错保护(断路器) Hystrix-->
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-hystrix</artifactId>
		</dependency>
        ```
    - 启动类加注解`@EnableCircuitBreaker`
    - 声明断路后回调函数

        ```java
        @HystrixCommand(fallbackMethod = "findByIdFallBack")
        public User findById(Long id) {
            // virtual ip: 服务的spring.application.name
            return this.restTemplate.getForObject("http://provider-user/simple/" + id, User.class);
        }

        // 当服务调用失败或者超时则回调此函数. 此函数参数和返回值必须和调用函数一致
        public User findByIdFallBack(Long id) {
            System.out.println(id + ", error[hystrix]");
            return null;
        }
        ```
