package cn.aezo.springcloud.consumermovie.controller;

import cn.aezo.springcloud.consumermovie.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/movie/{id}")
    public User findById(@PathVariable Long id) {
        return this.restTemplate.getForObject("http://localhost:7900/simple/" + id, User.class);
    }
}
