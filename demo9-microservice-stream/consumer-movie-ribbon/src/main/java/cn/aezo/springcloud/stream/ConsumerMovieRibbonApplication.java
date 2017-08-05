package cn.aezo.springcloud.stream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

// 可用@SpringCloudApplication代替，它包含了@SpringBootApplication、@EnableDiscoveryClient、@EnableCircuitBreaker
@SpringBootApplication
@EnableDiscoveryClient
@EnableCircuitBreaker
@EnableFeignClients
public class ConsumerMovieRibbonApplication {
	// 定义一个Bean供MovieController中使用
	@Bean
	@LoadBalanced // 使用ribbon实现客户端负载均衡
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	public static void main(String[] args) {
		SpringApplication.run(ConsumerMovieRibbonApplication.class, args);
	}
}
