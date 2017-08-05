package cn.aezo.springcloud.providermovie.controller;

import cn.aezo.springcloud.providermovie.repository.UserRepositroy;
import cn.aezo.springcloud.providermovie.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by smalle on 2017/7/1.
 */
@RestController
public class UserController {
    @Autowired
    UserRepositroy userRepositroy;

    @GetMapping(value = "/simple/{id}")
    public User findByiD(@PathVariable Long id) {
        User user = userRepositroy.findOne(id);
        return user;
    }
}
