package rabbitmq.publish_subscribe.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import rabbitmq.common.utils.ConnectionUtil;

public class EmitLog {

    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] argv) throws Exception {
        // 获取到连接
        Connection connection = ConnectionUtil.getConnection();
        // 从连接中创建通道
        Channel channel = connection.createChannel();
        // 声明（创建）交换机，使用fanout模式（广播模式）
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

        String message = "Hello World!";
        // 通过通道发布消息（通过exchange指定发布到的消息队列）
        channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("UTF-8"));
        System.out.println(" [x] Sent '" + message + "'");

        //关闭通道和连接
        channel.close();
        connection.close();
    }
}
