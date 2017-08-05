package cn.aezo.springcloud.eurekaserverpeer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer // 开启Eureka Server
public class EurekaServerPeerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EurekaServerPeerApplication.class, args);
	}
}
