package cn.aezo.springcloud.providermovie.stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by smalle on 2017/7/24.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class StreamTests {
    @Autowired
    SinkSender sinkSender;

    @Test
    public void sendMessage() {
        sinkSender.sendMessage();
    }

    @Test
    public void msgTransform() {
        sinkSender.msgTransform();
    }

}
