package cn.aezo.springcloud.springbootrabbitmq.services;

import cn.aezo.springcloud.springbootrabbitmq.entity.User;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Created by smalle on 2017/7/2.
 */
@Service
public class UserService {
    @Autowired
    RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "findByIdFallBack")
    public User findById(Long id) {
        // virtual ip: 服务的spring.application.name
        return this.restTemplate.getForObject("http://provider-user/simple/" + id, User.class);
    }

    // 当服务调用失败或者超时则回调此函数. 此函数参数和返回值必须和调用函数一致
    public User findByIdFallBack(Long id) {
        System.out.println(id + ", error[hystrix]");
        return new User();
    }
}
