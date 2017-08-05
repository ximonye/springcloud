## Stream 消息驱动(Spring Cloud Stream)

> 消息驱动以consumer-movie-ribbon和provider-user为例（最好先启动eureka-server和config-server）

- 简介
    - Spring Cloud Stream本质上是整合了Spring Boot和Spring integration，主要包含发布-订阅、消息组、分区三个概念
    - 其功能是为应用程序(Spring Boot)和消息中间件之间添加一个绑定器(Binder)，只对应用程序提供统一的Channel通道，从而应用程序不需要考虑不同消息中间件的实现(调用规则)
    - 暂时只支持RabbitMQ和Kafka的自动化配置
- 入门案例
    - 引入依赖(以服务`consumer-movie-ribbon`为例)

        ```xml
        <!-- 消息驱动 -->
		<!-- 基于rabbitmq(也可以引入spring-cloud-stream-binder-rabbit/kafka/redis) -->
		<dependency>
			<groupId>org.springframework.cloud</groupId>
			<artifactId>spring-cloud-starter-stream-rabbit</artifactId>
		</dependency>
        ```
    - application.yml 部分配置(consumer-movie-ribbon)

        ```yml
        spring:
          application:
            name: consumer-movie-ribbon
          cloud:
            # Spring Cloud Stream配置
            stream:
              bindings:
                # input为定义的通道名称
                input:
                  # 通道数据传输类型
                  # content-type: text/plain # application/json
                  # 将此实例的某个Stream(input)定义为某个消费组(同一个消费组里面的实例只有其中一个对消息进行消费, 否则所有的实例都会消费, 建议定义)
                  group: group-movie
                  # 应用中的监听的input通道对应中间件的主题(rabbitmq的Exchange, kafka的Topic)为xxx(默认是通道名称, 此时即input)
                  # destination: xxx
                # ...此处省略其他通道配置...
        ```
    - 消息接受者(consumer-movie-ribbon)

        ```java
        // 开启绑定，启动消息驱动。
        // @EnableBinding属性value可指定多个关于消息通道的配置(类)，表示需要加载的类，即根据这些类中的注解(@Input、@Output生成bean)
        @EnableBinding(value = {Processor.class, MyChannel.class})
        public class SinkReceiver {

            // 消息消费者监听的通道名称.
            @StreamListener(Processor.INPUT)
            public void receive(Object msg) {
                System.out.println("msg = " + msg);
            }

            // @StreamListener可将收到的消息(json/xml数据格式)转换成具体的对象
            @StreamListener(MyChannel.CHANNEL2_INPUT) // 接受rabbitmq的channel1_output
            @SendTo(MyChannel.CHANNEL2_OUTPUT) // 收到消息后进行反馈(给rabbitmq的channel1_input发送)
            public Object receive2(User user) {
                System.out.println("user.getUsername() ==> " + user.getUsername());
                return "SinkReceiver.receive2 = " + user; // 将此数据返回给消息发送这或者其他服务
            }
        }

        // 定义通道
        public interface MyChannel {
            // 输入输出通道名称最好不要相同
            String CHANNEL2_INPUT = "channel2_input";
            String CHANNEL2_OUTPUT = "channel2_output";

            @Input(MyChannel.CHANNEL2_INPUT)
            SubscribableChannel channel2_input(); // 设置消息通道名称(默认使用方法名作为消息通道名)，表示从该通道发送数据

            @Output(MyChannel.CHANNEL2_OUTPUT)
            MessageChannel channel2_output();
        }
        ```
        - 易错点：
            - 在两个类中分别@EnableBinding绑定Processor，并同时监听@Input则报错 unknown.channel.name.(一个应用中不能绑定多个相同名称的@Input、@Output; 同理, Processor只能被一个类@EnableBinding绑定或者被两个类分别绑定@Input、@Output)
            - 如果一个应用需要监听相同的主题(如：input)，可以重新命名一个@Input("xxx"), 然后通过spring.cloud.stream.bindings.xxx.destination=input来监听input主题。或者将监听程序写在一个类中

    - 消息发送者(provider-user)

        ```java
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
                channel.send(MessageBuilder.withPayload("hello channel [from provider-user]").build());
            }

            // 测试@StreamListener对消息自动转换和消息反馈
            public void msgTransform() {
                channel1_output.send(MessageBuilder.withPayload("{\"id\": 1, \"username\": \"smalle\"}").build());
            }
        }

        // 用于接受反馈消息
        @EnableBinding(value = {MyChannel.class})
        public class ChannelReceiver {
            // 接受反馈的消息
            @StreamListener(MyChannel.CHANNEL1_INPUT)
            public void receiveSendTo(Object msg) {
                System.out.println("ChannelReceiver.receiveSendTo ==> " + msg);
            }
        }

        // 定义通道
        public interface MyChannel {
            String CHANNEL = "input";
            String CHANNEL1_INPUT = "channel1_input";
            String CHANNEL1_OUTPUT = "channel1_output";

            @Input(MyChannel.CHANNEL1_INPUT)
            SubscribableChannel channel1_input();

            @Output(MyChannel.CHANNEL)
            MessageChannel channel();

            @Output(MyChannel.CHANNEL1_OUTPUT)
            MessageChannel channel1_output();
        }
        ```
- Spring integration原生支持(了解，Spring Cloud Stream是基于它实现的)
    - 消息消费者(consumer-movie-ribbon)

        ```java
        @EnableBinding(value = {MyChannel.class}) // 收发消息的通道不能使用同一个MessageChannel
        public class MyReceiver {
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
        ```
    - 消息生产者(provider-user)

        ```java
        @EnableBinding(value = {MyChannel.class})
        public class MySender {

            @Bean // 项目启动后便会执行
            @InboundChannelAdapter(value = MyChannel.POLLER_OUTPUT, poller = @Poller(fixedDelay = "5000")) // 对MyChannel.POLLER_OUTPUT通道进行输出. poller表示轮询，此时为每5秒执行一次方法
            public MessageSource<Date> timeMsgSource() {
                return () -> new GenericMessage<>(new Date());
            }
        }
        ```
- 消息分区(未测试)

    ```java
    # 消费者配置
    # 当前消费者的总实例数量(消息分区需要设置)
    spring.cloud.stream.instanceCount=2
    # 当前实例的索引号(消息分区需要设置，最大为instance-count - 1)
    spring.cloud.stream.instanceIndex=0
    # 开启消费者分区功能
    spring.cloud.stream.bindings.input.consumer.partitioned=true

    # 生成者配置
    spring.cloud.stream.bindings.output.destination=input
    # 可根据实际消息规则配置SpEL表达式生成分区键用于分配出站数据, 用于消息分区
    spring.cloud.stream.bindings.output.producer.partitionKeyExpression=payload
    # 分区数量
    spring.cloud.stream.bindings.output.producer.partitionCount=2
    ```
- 绑定器SPI
    - 绑定器是将程序(SpringBoot)中的输入/输出通道和消息中间件的输入输出做绑定
    - Spring Cloud Stream暂时只实现了RabbitMQ和Kafka的绑定其，因此只支持此二者的自动化配置
    - 可自己实现其他消息中间件的绑定器
        - 一个实现Binder接口的类
        - 一个Spring配置加载类，用来连接中间件
        - 一个或多个能够在classpath下找到META-INF/spring.binders定义绑定器定的文件。如：

            ```java
            rabbit:\
            org.springframework.cloud.stream.binder.rabbit.config.RabbitServiceAutoConfiguration
            ```
    - 绑定器配置

        ```java
        # 默认的绑定器为rabbit(名字是META-INF/spring.binders中定义的)
        spring.cloud.stream.defaultBinder=rabbit
        # 定义某个通道(input)的绑定器
        spring.cloud.stream.bindings.input.binder=kafka

        # 为不同通道定义同一类型不同环境的绑定器
        spring.cloud.stream.bindings.input.binder=rabbit1
        spring.cloud.stream.bindings.output.binder=rabbit2
        # 定义rabbit1的类型和环境(此处省略rabbit2的配置)
        spring.cloud.stream.binders.rabbit1.type=rabbit1
        spring.cloud.stream.binders.rabbit1.environment.spring.rabbitmq.host=127.0.0.1
        spring.cloud.stream.binders.rabbit1.environment.spring.rabbitmq.port=5672
        spring.cloud.stream.binders.rabbit1.environment.spring.rabbitmq.username=guest
        spring.cloud.stream.binders.rabbit1.environment.spring.rabbitmq.password=guest
        ```