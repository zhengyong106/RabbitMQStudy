package rabbitmq.work.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import rabbitmq.common.utils.ConnectionUtil;

import java.util.concurrent.atomic.AtomicInteger;

public class Worker {
    private static final AtomicInteger receivedCount = new AtomicInteger();

    private static final String TASK_QUEUE_NAME = "WorkQueue";

    public static void main(String[] argv) throws Exception {
        // 获取到连接
        Connection connection = ConnectionUtil.getConnection();
        // 从连接中创建通道
        Channel channel = connection.createChannel();
        // 声明（创建）队列
        channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);

        // 设置同一时刻只能服务器只会发送一条消息给消费者（即work模式的能者多劳）
        channel.basicQos(1);

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        // 声明消息传递后的回调函数
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");

            int _receivedCount = receivedCount.incrementAndGet();
            System.out.printf(" [%s] Received [%s]%n", _receivedCount, message);
            try {
                doWork(message);
            } finally {
                System.out.printf(" [%s] Done%n", _receivedCount);
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
        };
        // 监听队列, 添加接收回调和取消回调
        channel.basicConsume(TASK_QUEUE_NAME, false, deliverCallback, consumerTag -> { });
    }

    private static void doWork(String task) {
        for (char ch : task.toCharArray()) {
            if (ch == '.') {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException _ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}