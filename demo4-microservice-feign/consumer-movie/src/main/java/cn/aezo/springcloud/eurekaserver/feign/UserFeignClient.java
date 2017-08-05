package cn.aezo.springcloud.eurekaserver.feign;

import cn.aezo.springcloud.eurekaserver.entity.User;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by smalle on 2017/7/2.
 */
// 此服务消费者需要调用的服务声明
@FeignClient("provider-user")
public interface UserFeignClient {
    // Feign不支持@GetMapping, @PathVariable必须指明参数值
    @RequestMapping(method = RequestMethod.GET, value = "/simple/{id}")
    User findById(@PathVariable("id") Long id);

    @RequestMapping(method = RequestMethod.POST, value = "/feign-post")
    User postFeignUser(@RequestBody User user);
}
