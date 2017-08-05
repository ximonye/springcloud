package cn.aezo.springcloud.apigatewayzuul.error;

import org.springframework.boot.autoconfigure.web.DefaultErrorAttributes;
import org.springframework.web.context.request.RequestAttributes;

import java.util.Map;

/**
 * Created by smalle on 2017/7/15.
 */
// org.springframework.boot.autoconfigure.web.BasicErrorController 默认错误处理
public class CustomErrorAttributes extends DefaultErrorAttributes {
    @Override
    public Map<String, Object> getErrorAttributes(RequestAttributes requestAttributes, boolean includeStackTrace) {
        Map<String, Object> map = super.getErrorAttributes(requestAttributes, includeStackTrace);
        map.remove("exception"); // 移除exception信息，客户端将看不到此信息
        map.put("myAttr", "hello");
        return map;
    }
}
