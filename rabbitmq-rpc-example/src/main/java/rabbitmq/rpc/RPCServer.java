package rabbitmq.rpc;

import com.rabbitmq.client.*;
import rabbitmq.common.utils.ConnectionUtil;

public class RPCServer {

    private static final String RPC_QUEUE_NAME = "rpc_queue";

    private static int fib(int n) {
        if (n == 0) return 0;
        if (n == 1) return 1;
        return fib(n - 1) + fib(n - 2);
    }

    public static void main(String[] argv) throws Exception {
        // 获取到连接
        Connection connection = ConnectionUtil.getConnection();
        // 从连接中创建通道
        Channel channel = connection.createChannel() ;
        // 声明（创建）队列
        channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);
        // 清除列队中的内容，但是不删除列队
        channel.queuePurge(RPC_QUEUE_NAME);
        // 设置同一时刻只能服务器只会发送一条消息给消费者（即work模式的能者多劳）
        channel.basicQos(1);

        Object monitor = new Object();
        // 定义接收回调函数，接收到消息后（Integer类型消息）返回斐波那契数
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(delivery.getProperties().getCorrelationId())
                    .build();

            String response = "";
            try {
                String message = new String(delivery.getBody(), "UTF-8");
                int n = Integer.parseInt(message);
                response = Integer.toString(fib(n));
            } catch (RuntimeException e) {
                System.out.println(" [.] " + e.toString());
            } finally {
                String replyQueueName = delivery.getProperties().getReplyTo();
                long deliveryTag = delivery.getEnvelope().getDeliveryTag();
                channel.basicPublish("", replyQueueName, replyProps, response.getBytes("UTF-8"));
                channel.basicAck(deliveryTag, false);
                // RabbitMq consumer worker thread notifies the RPC server owner thread
                synchronized (monitor) {
                    monitor.notify();
                }
            }
        };

        // 监听队列, 添加接收回调和取消回调，关闭自动确认
        channel.basicConsume(RPC_QUEUE_NAME, false, deliverCallback, (consumerTag -> { }));
        // Wait and be prepared to consume the message from RPC client.
        while (true) {
            synchronized (monitor) {
                try {
                    monitor.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}