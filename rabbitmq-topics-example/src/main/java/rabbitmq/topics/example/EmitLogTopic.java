package rabbitmq.topics.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import rabbitmq.common.utils.ConnectionUtil;

public class EmitLogTopic {

    private static final String EXCHANGE_NAME = "topic_logs";

    public static void main(String[] argv) throws Exception {
        // 获取到连接
        Connection connection = ConnectionUtil.getConnection();
        // 从连接中创建通道
        Channel channel = connection.createChannel();
        // 声明（创建）交换机，使用direct模式
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");

        String routingKey = "error.kern";
        String message = "Hello World";
        // 通过通道发布消息（通过exchangeName和exchange绑定的routingKey指定发布到的消息队列）
        channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes("UTF-8"));
        System.out.println(" [x] Sent '" + routingKey + "':'" + message + "'");

        //关闭通道和连接
        channel.close();
        connection.close();
    }
    //..
}
