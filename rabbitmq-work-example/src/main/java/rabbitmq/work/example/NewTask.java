package rabbitmq.work.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;
import rabbitmq.common.utils.ConnectionUtil;

public class NewTask {

    private static final String TASK_QUEUE_NAME = "WorkQueue";

    public static void main(String[] argv) throws Exception {
        // 获取到连接
        Connection connection = ConnectionUtil.getConnection();
        // 从连接中创建通道
        Channel channel = connection.createChannel();
        // 声明（创建）队列并且设置启用持久化（durable，注意：RabbitMQ不允许使用不同的参数重新定义现有队列）
        channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null);

        for(int i = 0; i < 100; i++){
            String message = "message" + i + "..........";
            // 通过通道发布消息（通过routingKey指定发布到的消息队列）并且使消息设置为持久化消息（PERSISTENT）
            channel.basicPublish("", TASK_QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("UTF-8"));
            System.out.println(" [x] Sent '" + message + "'");
        }

        //关闭通道和连接
        channel.close();
        connection.close();
    }

}