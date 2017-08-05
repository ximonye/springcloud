package cn.aezo.springcloud.springbootrabbitmq.feign;

import cn.aezo.springcloud.springbootrabbitmq.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Created by smalle on 2017/7/9.
 */
@Component
public class HystrixClientFallback implements UserFeignClient {

    @Override
    public User findById(@PathVariable("id") Long id) {
        User user = new User();
        user.setId(0L);
        return user;
    }
}
