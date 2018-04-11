package cn.aezo.springcloud.providermovie.controller;

import cn.aezo.springcloud.providermovie.entity.User;
import cn.aezo.springcloud.providermovie.repository.UserRepositroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Random;

/**
 * Created by smalle on 2017/7/1.
 */
@RestController
public class UserController {
    @Autowired
    UserRepositroy userRepositroy;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Value("${spring.application.name}")
    private String applicationName;

    @GetMapping(value = "/simple/{id}")
    public User findById(@PathVariable Long id) throws InterruptedException {
        // 随机睡眠0-2秒，测试hystrix
        long sleepTime = new Random().nextInt(2000);
        System.out.println("sleepTime = " + sleepTime);
        Thread.sleep(sleepTime);

        User user = this.userRepositroy.findOne(id);
        return user;
    }

    /**
     * 从服务注册中心(eureka server)获取当前client的信息
     * @return
     */
    @GetMapping("/instance-info")
    public ServiceInstance showInfo() {
        List<ServiceInstance> localServiceInstances = this.discoveryClient.getInstances(applicationName);
        if(localServiceInstances != null && localServiceInstances.size() > 0)
            return localServiceInstances.get(0);
        else
            return null;
    }
}
