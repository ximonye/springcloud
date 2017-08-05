package cn.aezo.springcloud.stream.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by smalle on 2017/7/15.
 */
@RefreshScope // 之后刷新config后可重新注入值
@RestController
public class ConfigController {
    @Value("${from:none}")
    private String from;

    // 测试从配置中心获取配置数据
    @RequestMapping("/from")
    public String from() {
        return this.from; // 会从git仓库中读取配置数据
    }
}
