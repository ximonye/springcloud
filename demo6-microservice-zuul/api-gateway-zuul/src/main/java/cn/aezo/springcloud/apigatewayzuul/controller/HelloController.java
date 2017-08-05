package cn.aezo.springcloud.apigatewayzuul.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by smalle on 2017/7/11.
 */
@RestController
public class HelloController {

    // 测试zuul本地跳转。访问 http://localhost:5555/api-local/hello?accessToken=smalle
    @RequestMapping("/local/hello")
    public String hello() {
        return "hello local";
    }
}
