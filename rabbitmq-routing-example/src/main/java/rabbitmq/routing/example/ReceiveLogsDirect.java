package rabbitmq.routing.example;

import com.rabbitmq.client.*;
import rabbitmq.common.utils.ConnectionUtil;

public class ReceiveLogsDirect {

    private static final String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] argv) throws Exception {
        // 获取到连接
        Connection connection = ConnectionUtil.getConnection();
        // 从连接中创建通道
        Channel channel = connection.createChannel();
        // 声明（创建）交换机，使用direct模式
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");
        // 创建临时列队，并返回列队名字（实际等同于channel.queueDeclare(queue=QUEUE_NAME, durable=false, exclusive=true, autoDelete=true, arguments=null);）
        String queueName = channel.queueDeclare().getQueue();

        // 将临时列队和交换机进行绑定（交换机通过路由键将消息发送到指定队列）
        channel.queueBind(queueName, EXCHANGE_NAME, "#");

        // 声明消息传递后的回调函数
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" [x] Received '" + delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        };
        // 监听队列, 添加接收回调和取消回调
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> { });
    }
}
