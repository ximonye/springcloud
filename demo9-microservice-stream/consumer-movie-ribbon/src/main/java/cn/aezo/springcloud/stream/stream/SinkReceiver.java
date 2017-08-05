package cn.aezo.springcloud.stream.stream;

import cn.aezo.springcloud.stream.entity.User;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.messaging.handler.annotation.SendTo;

/**
 * Created by smalle on 2017/7/24.
 */
// 开启绑定，启动消息驱动。
// @EnableBinding属性value可指定多个关于消息通道的配置(类)，表示需要加载的类，即根据这些类中的注解(@Input、@Output生成bean)
// 易错点：
// (1) 一个应用中不能绑定多个相同名称的@Input、@Output; 同理, Processor只能被@EnableBinding绑定一次. 否则容易报错 unknown.channel.name.
// (2) 如果一个应用需要监听相同的主题(如：input)，可以重新命名一个@Input("xxx"), 然后通过spring.cloud.stream.bindings.xxx.destination=input来监听input主题。或者将监听程序写在一个类中
@EnableBinding(value = {Processor.class, MyChannel.class})
public class SinkReceiver {

    // 需要@EnableBinding同时绑定输入和输出通道
    @StreamListener(Processor.INPUT) // 消息消费者监听的通道名称.
    public void receive(Object msg) {
        System.out.println("msg = " + msg);
    }

    // 和receive同时收到相同的消息
    @StreamListener(Processor.INPUT)
    public void receive1(Object msg) {
        System.out.println("msg1 = " + msg);
    }

    // @StreamListener可将收到的消息(json/xml数据格式)转换成具体的对象
    @StreamListener(MyChannel.CHANNEL2_INPUT)
    @SendTo(MyChannel.CHANNEL2_OUTPUT) // 收到消息后进行反馈
    public Object receive2(User user) {
        System.out.println("user.getUsername() ==> " + user.getUsername());
        return "SinkReceiver.receive2 = " + user; // 将此数据返回给消息发送这或者其他服务
    }
}
