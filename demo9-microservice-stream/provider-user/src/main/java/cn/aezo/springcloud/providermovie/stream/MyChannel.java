package cn.aezo.springcloud.providermovie.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * Created by smalle on 2017/7/24.
 */
// 自定义消息生产者通道(可参考默认输出通道：org.springframework.cloud.stream.messaging.Source)
// 同理可自定义消息消费者通道(案例默认使用的Spring提供的Sink接口)
// Processor接口继承了Source, Sink。也可将自定义的生产者和消费者通道接口写在一个类中
public interface MyChannel {
    String CHANNEL = "input";
    String POLLER_OUTPUT = "poller_output";
    String CHANNEL1_INPUT = "channel1_input";
    String CHANNEL1_OUTPUT = "channel1_output";


    @Input(MyChannel.CHANNEL1_INPUT)
    SubscribableChannel channel1_input();

    @Output(MyChannel.POLLER_OUTPUT)
    MessageChannel poller_output();

    @Output(MyChannel.CHANNEL)
    MessageChannel channel();

    @Output(MyChannel.CHANNEL1_OUTPUT)
    MessageChannel channel1_output();
}
