package rabbitmq.work.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import rabbitmq.common.utils.ConnectionUtil;

public class Worker {
    private static final String TASK_QUEUE_NAME = "WorkQueue";

    public static void main(String[] argv) throws Exception {
        // 获取连接
        Connection connection = ConnectionUtil.getConnection();
        // 创建连接通道
        Channel channel = connection.createChannel();
        // 声明（创建）队列 持久化，非独占，非自动删除，不包含参数
        channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);
        // 设置同一时刻服务器只会发送一条消息给消费者（即work模式的能者多劳）
        channel.basicQos(1);

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        // 声明消息交付后的回调函数
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.printf(" [C] Received [%s]%n", message);
            try {
                doWork(message);
            } finally {
                System.out.printf(" [C] Done%n");
                // 手动应答
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            }
        };
        // 监听队列, 取消自动应答并添加交付后的回调函数和取消后的回调函数
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