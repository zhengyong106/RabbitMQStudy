package rabbitmq.routing.example;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import rabbitmq.common.utils.ConnectionUtil;

public class EmitLogDirect {

    private static final String EXCHANGE_NAME = "direct_logs";

    public static void main(String[] argv) throws Exception {
        // 获取到连接
        Connection connection = ConnectionUtil.getConnection();
        // 从连接中创建通道
        Channel channel = connection.createChannel();
        // 声明（创建）交换机，使用direct模式
        channel.exchangeDeclare(EXCHANGE_NAME, "direct");
        // 定义四种不同的日志级别
        String[] severities = new String[]{"info", "warning", "error", "debug"};

        for(String severity: severities){
            String message = severity + " message";
            // 通过通道发布消息（通过exchange和exchange绑定的routingKey指定发布到的消息队列）
            channel.basicPublish(EXCHANGE_NAME, severity, null, message.getBytes("UTF-8"));
            System.out.println(" [x] Sent '" + severity + "':'" + message + "'");
        }

        ConnectionUtil.close(channel);
        ConnectionUtil.close(connection);
    }
}
