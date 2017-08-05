package cn.aezo.springcloud.apigatewayzuul;

import cn.aezo.springcloud.apigatewayzuul.error.CustomErrorAttributes;
import cn.aezo.springcloud.apigatewayzuul.filter.AccessFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.discovery.PatternServiceRouteMapper;
import org.springframework.context.annotation.Bean;

/**
 * Created by smalle on 2017/7/1.
 */
@SpringCloudApplication // @SpringCloudApplication包含了@SpringBootApplication、@EnableDiscoveryClient、@EnableCircuitBreaker
@EnableZuulProxy
public class ApiGatewayZuulApplication {
    // 过滤器
    @Bean
    public AccessFilter accessFilter() {
        return new AccessFilter();
    }

    // 自定义错误信息(通过postman等工具访问可查看)
    @Bean
    public CustomErrorAttributes customErrorAttributes() {
        return new CustomErrorAttributes();
    }

    // 自定义路由映射规则
    @Bean
    public PatternServiceRouteMapper serviceRouteMapper() {
        // 将serviceName-v1映射成/v1/serviceName. 未匹配到则按照原始的
        return new PatternServiceRouteMapper(
                "(?<name>^.+)-(?<version>v.+$)",
                "${version}/${name}");
    }

    // 通过spring cloud config动态加载路由配置
    @Bean
    @RefreshScope
    @ConfigurationProperties("zuul")
    public ZuulProperties zuulProperties() {
        return new ZuulProperties();
    }

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayZuulApplication.class, args);
    }
}
