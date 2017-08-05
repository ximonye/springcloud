package cn.aezo.springcloud.providermovie.stream.integration;

import cn.aezo.springcloud.providermovie.stream.MyChannel;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.core.MessageSource;
import org.springframework.messaging.support.GenericMessage;

import java.util.Date;

/**
 * Created by smalle on 2017/7/25.
 */
// 消息生产者（使用spring integration实现消息收发）
@EnableBinding(value = {MyChannel.class})
public class MySender {

    @Bean // 项目启动后便会执行
    @InboundChannelAdapter(value = MyChannel.POLLER_OUTPUT, poller = @Poller(fixedDelay = "5000")) // 对MyChannel.POLLER_OUTPUT通道进行输出. poller表示轮询，此时为每5秒执行一次方法
    public MessageSource<Date> timeMsgSource() {
        return () -> new GenericMessage<>(new Date());
    }
}
