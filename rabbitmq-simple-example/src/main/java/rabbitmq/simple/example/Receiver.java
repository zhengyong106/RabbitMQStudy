package rabbitmq.simple.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import rabbitmq.common.utils.ConnectionUtil;

public class Receiver {
    private final static String QUEUE_NAME = "hello";

    public static void main(String[] argv) throws Exception {
        // 获取到连接
        Connection connection = ConnectionUtil.getConnection();
        // 从连接中创建通道
        Channel channel = connection.createChannel();
        // 声明（创建）队列 非持久，非独占，非自动删除，不包含参数
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        // 声明消息传递后的回调函数
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");
        };
        // 监听队列, 添加接收回调和取消回调
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });
    }
}