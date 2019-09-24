package rabbitmq.simple.example;

import com.rabbitmq.client.*;
import rabbitmq.common.utils.ConnectionUtil;

import java.io.IOException;

public class Receiver {
    private final static String QUEUE_NAME = "HelloWorldQueue";

    public static void main(String[] argv) throws Exception {
        // 获取连接
        Connection connection = ConnectionUtil.getConnection();
        // 创建连接通道
        Channel channel = connection.createChannel();
        // 声明（创建）队列 非持久，非独占，非自动删除，不包含参数
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + message + "'");
            }
        };

        // 监听队列, 添加接收回调和取消回调
        channel.basicConsume(QUEUE_NAME, true, consumer);
    }
}