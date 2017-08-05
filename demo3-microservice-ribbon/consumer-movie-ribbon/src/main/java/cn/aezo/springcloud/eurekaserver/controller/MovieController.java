package cn.aezo.springcloud.eurekaserver.controller;

import cn.aezo.springcloud.eurekaserver.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Created by smalle on 2017/7/1.
 */
@RestController
public class MovieController {
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @GetMapping("/movie/{id}")
    public User findById(@PathVariable Long id) {
        // 访问provider-user时获取此次请求的负载平衡实例
        ServiceInstance serviceInstance = this.loadBalancerClient.choose("provider-user");
        System.out.println(serviceInstance.getServiceId() + ":" + serviceInstance.getHost() + ":" + serviceInstance.getPort());

        // virtual ip: 服务的spring.application.name
        return this.restTemplate.getForObject("http://provider-user/simple/" + id, User.class);
    }
}
