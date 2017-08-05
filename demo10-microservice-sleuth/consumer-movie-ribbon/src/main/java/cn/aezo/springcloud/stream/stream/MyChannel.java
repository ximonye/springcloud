package cn.aezo.springcloud.stream.stream;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * Created by smalle on 2017/7/24.
 */
// 自定义消息生产者通道(可参考默认输出通道：org.springframework.cloud.stream.messaging.Source)
// 同理可自定义消息消费者通道(案例默认使用的Spring提供的Sink接口)
// Processor接口继承了Source, Sink(这三个接口是spring为了用户方便默认提供的)。也可将自定义的生产者和消费者通道接口写在一个类中
public interface MyChannel {
    // 输入输出通道名称最好不要相同
    String POLLER_INPUT = "poller_input";
    String POLLER_OUTPUT = "poller_output";

    String CHANNEL1_INPUT = "channel1_input";
    String CHANNEL1_OUTPUT = "channel1_output";

    String CHANNEL2_INPUT = "channel2_input";
    String CHANNEL2_OUTPUT = "channel2_output";


    @Input(MyChannel.POLLER_INPUT) // 设置消息通道名称(默认使用方法名作为消息通道名)，表示从该通道发送数据
    SubscribableChannel poller_input();

    @Input(MyChannel.CHANNEL1_INPUT)
    SubscribableChannel channel1_input();

    @Input(MyChannel.CHANNEL2_INPUT)
    SubscribableChannel channel2_input();


    @Output(MyChannel.POLLER_OUTPUT)
    MessageChannel poller_output();

    @Output(MyChannel.CHANNEL1_OUTPUT)
    MessageChannel channel1_output();

    @Output(MyChannel.CHANNEL2_OUTPUT)
    MessageChannel channel2_output();
}
