package rabbitmq.rpc;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import rabbitmq.common.utils.ConnectionUtil;

import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class RPCClient {

    private Connection connection;
    private Channel channel;
    private String requestQueueName = "rpc_queue";

    public RPCClient() throws Exception {
        connection = ConnectionUtil.getConnection();
        channel = connection.createChannel();
    }

    public static void main(String[] argv) throws Exception {
        RPCClient fibonacciRpc = null;
        try {
            fibonacciRpc = new RPCClient();
            for (int i = 0; i < 32; i++) {
                String i_str = Integer.toString(i);
                System.out.println(" [x] Requesting fib(" + i_str + ")");
                String response = fibonacciRpc.call(i_str);
                System.out.println(" [.] Got '" + response + "'");
            }
        }  catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(fibonacciRpc != null){
                //关闭通道和连接
                fibonacciRpc.channel.close();
                fibonacciRpc.connection.close();
            }
        }
    }

    public String call(String message) throws Exception {
        // 创建一个唯一识别码
        final String correlationId = UUID.randomUUID().toString();
        // 创建临时列队，用于接收RPC回调，并返回列队名字
        String replyQueueName = channel.queueDeclare().getQueue();
        // 定义messageProperties（使用 AMQP Advanced Message Queuing Protocol 预定义属性）
        // 其中correlationId用于将RPC响应与请求相关联，replyTo用于命名RPC请求回调队列
        AMQP.BasicProperties props = new AMQP.BasicProperties
                .Builder()
                .correlationId(correlationId)
                .replyTo(replyQueueName)
                .build();

        // 通过通道发布消息到RPC消息队列
        channel.basicPublish("", requestQueueName, props, message.getBytes("UTF-8"));
        // 创建阻塞队列
        final BlockingQueue<String> response = new ArrayBlockingQueue<>(1);
        // 监听队列, 添加接收回调和取消回调
        String ctag = channel.basicConsume(replyQueueName, true, (consumerTag, delivery) -> {
            if (delivery.getProperties().getCorrelationId().equals(correlationId)) {
                response.offer(new String(delivery.getBody(), "UTF-8"));
            }
        }, consumerTag -> {
        });
        // 阻塞队列（于RPC回调完成后取消阻塞）
        String result = response.take();
        // 取消订阅
        channel.basicCancel(ctag);
        return result;
    }
}

