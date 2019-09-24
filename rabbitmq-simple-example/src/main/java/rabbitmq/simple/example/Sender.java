package rabbitmq.simple.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import rabbitmq.common.utils.ConnectionUtil;

public class Sender {
    private final static String QUEUE_NAME = "HelloWorldQueue";

    public static void main(String[] argv) throws Exception {
        // 获取连接
        Connection connection = ConnectionUtil.getConnection();
        // 创建连接通道
        Channel channel = connection.createChannel();
        // 声明（创建）队列 非持久，非独占，非自动删除，不包含参数
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        String message = "Hello World!";
        // 发布消息（通过 routingKey 指定发布到的消息队列）
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
        //关闭通道和连接
        ConnectionUtil.close(channel);
        ConnectionUtil.close(connection);
    }
}
