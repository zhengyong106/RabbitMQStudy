package rabbitmq.common.utils;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;

public class ConnectionUtil {
    public static Connection getConnection() throws Exception {
        //定义连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        //设置服务地址
        factory.setHost("localhost");
        //端口
        factory.setPort(5672);
        //设置账号信息，用户名、密码、vhost
        factory.setVirtualHost("/taotao");
        factory.setUsername("taotao");
        factory.setPassword("taotao");
        // 通过工程获取连接
        return factory.newConnection();
    }

    public static void close(Connection connection) {
        try {
            if(null != connection) {
                connection.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void close(Channel channel) {
        try {
            if(null != channel) {
                channel.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
