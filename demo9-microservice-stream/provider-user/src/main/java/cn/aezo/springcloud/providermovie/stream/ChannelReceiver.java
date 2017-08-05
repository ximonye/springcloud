package cn.aezo.springcloud.providermovie.stream;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

/**
 * Created by smalle on 2017/7/24.
 */
// 开启绑定，启动消息驱动。
// @EnableBinding属性value可指定多个关于消息通道的配置(类)，表示需要加载的类，即根据这些类中的注解(@Input、@Output生成bean)
@EnableBinding(value = {MyChannel.class})
public class ChannelReceiver {
    // 接受反馈的消息
    @StreamListener(MyChannel.CHANNEL1_INPUT)
    public void receiveSendTo(Object msg) {
        System.out.println("ChannelReceiver.receiveSendTo ==> " + msg);
    }
}
