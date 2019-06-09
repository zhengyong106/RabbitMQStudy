package rabbitmq.simple.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import rabbitmq.common.utils.ConnectionUtil;

public class Sender {
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] argv) throws Exception {
        // 获取到连接
        Connection connection = ConnectionUtil.getConnection();
        // 从连接中创建通道
        Channel channel = connection.createChannel();
        // 声明（创建）队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        String message = "Hello World!";
        // 通过通道发布消息（通过routingKey指定发布到的消息队列）
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
        //关闭通道和连接
        channel.close();
        connection.close();
    }
}
