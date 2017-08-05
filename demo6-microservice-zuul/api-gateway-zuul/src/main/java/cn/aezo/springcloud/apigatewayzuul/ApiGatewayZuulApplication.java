package cn.aezo.springcloud.apigatewayzuul;

import cn.aezo.springcloud.apigatewayzuul.error.CustomErrorAttributes;
import cn.aezo.springcloud.apigatewayzuul.filter.AccessFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
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

    // 自定义错误信息
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

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayZuulApplication.class, args);
    }
}
