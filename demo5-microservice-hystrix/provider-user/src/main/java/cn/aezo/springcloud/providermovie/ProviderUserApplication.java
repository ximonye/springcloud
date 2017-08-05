package cn.aezo.springcloud.providermovie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * Created by smalle on 2017/7/1.
 */
@SpringBootApplication
@EnableEurekaClient // @EnableDiscoveryClient可以用于所有的服务发现框架，@EnableEurekaClient只能用于Eureka
public class ProviderUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProviderUserApplication.class, args);
    }
}
