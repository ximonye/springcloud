package cn.aezo.springcloud.providermovie.stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;

/**
 * Created by smalle on 2017/8/1.
 */
@EnableBinding(MyChannel.class)
public class SinkSender {
    // 法一：注入绑定接口
    @Autowired
    private MyChannel myChannel;

    // 法二：注入消息通道
    @Autowired @Qualifier("input") // 此时有多个MessageChannel(根据SinkSender中@Output注入的), 需要指明
    private MessageChannel channel;

    private MessageChannel channel1_output;

    // 也可以这样注入
    @Autowired
    public SinkSender(@Qualifier("channel1_output") MessageChannel channel) {
        this.channel1_output = channel;
    }

    // 测试基本的消息发送和接受
    public void sendMessage() {
        // 此条消息会在测试程序中打印
        myChannel.channel().send(MessageBuilder.withPayload("hello stream [from provider-user]").build());

        // 此条消息会在消息消费者中显示
        // channel.send(MessageBuilder.withPayload("hello channel [from provider-user]").build());
    }

    // 测试@StreamListener对消息自动转换和消息反馈
    public void msgTransform() {
        channel1_output.send(MessageBuilder.withPayload("{\"id\": 1, \"username\": \"smalle\"}").build());
    }
}
