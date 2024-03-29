package rabbitmq.publish_subscribe.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import rabbitmq.common.utils.ConnectionUtil;

public class ReceiveLogs {
    private static final String EXCHANGE_NAME = "logs";

    public static void main(String[] argv) throws Exception {
        // 获取到连接
        Connection connection = ConnectionUtil.getConnection();
        // 从连接中创建通道
        Channel channel = connection.createChannel();
        // 声明（创建）交换机，使用fanout模式（广播模式）
        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        // 创建临时列队，并返回列队名字（实际等同于channel.queueDeclare(queue=QUEUE_NAME, durable=false, exclusive=true, autoDelete=true, arguments=null);）
        String queueName = channel.queueDeclare().getQueue();
        // 将临时列队和交换机进行绑定（告诉交换机将消息发送到哪个队列），扇形交换机不需要routingKey
        channel.queueBind(queueName, EXCHANGE_NAME, "");

        // 声明消息传递后的回调函数
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + message + "'");
        };
        // 监听队列, 添加接收回调和取消回调
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
    }
}