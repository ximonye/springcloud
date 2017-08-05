package cn.aezo.springcloud.stream.stream.integration;

import cn.aezo.springcloud.stream.stream.MyChannel;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by smalle on 2017/7/25.
 */
// 消息消费者（用spring integration实现消息收发）
@EnableBinding(value = {MyChannel.class}) // 收发消息的通道不能使用同一个MessageChannel
public class MyReceiver {

    @ServiceActivator(inputChannel = MyChannel.CHANNEL1_INPUT) // 收发消息的通道不能使用同一个MessageChannel
    public void receiveInput(Object msg) {
        System.out.println("MyReceiver.receiveInput: msg = " + msg);
    }

    @ServiceActivator(inputChannel = MyChannel.POLLER_INPUT) // 收发消息的通道不能使用同一个MessageChannel
    public void receive(Object msg) {
        System.out.println("MyReceiver: msg = " + msg);
    }

    // 消息转换(也可放在MySender中)，@ServiceActivator本身不具备消息转换功能(如：json/xml转成具体的对象)
    @Transformer(inputChannel = MyChannel.POLLER_INPUT, outputChannel = MyChannel.POLLER_OUTPUT)
    public Object transform(Date msg) {
        return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(msg);
    }
}
