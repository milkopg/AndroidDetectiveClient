package project.android.softuni.bg.androiddetectiveclient.rabbitmq;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.AMQP.BasicProperties;
import java.util.UUID;

import project.android.softuni.bg.androiddetectiveclient.util.Constants;

/**
 * Created by Milko on 26.9.2016 г..
 */

public class RabbitMQClient {

  private Connection connection;
  private Channel channel;
  private String requestQueueName = Constants.RABBIT_MQ_REQUES_QUEUE_NAME;
  private String replyQueueName;
  private QueueingConsumer consumer;

  public RabbitMQClient() throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setAutomaticRecoveryEnabled(true);
    factory.setUri(Constants.RABBIT_MQ_API_URL);

    connection = factory.newConnection();
    channel = connection.createChannel();

    replyQueueName = channel.queueDeclare().getQueue();
    consumer = new QueueingConsumer(channel);
    channel.basicConsume(replyQueueName, true, consumer);
  }

  public String sendMessage(String message) throws Exception {
    String response = null;
    String corrId = UUID.randomUUID().toString();

    BasicProperties props = new BasicProperties
            .Builder()
            .correlationId(corrId)
            .replyTo(replyQueueName)
            .build();

    channel.basicPublish("", requestQueueName, props, message.getBytes("UTF-8"));

    while (true) {
      QueueingConsumer.Delivery delivery = consumer.nextDelivery();
      if (delivery.getProperties().getCorrelationId().equals(corrId)) {
        response = new String(delivery.getBody(),"UTF-8");
        break;
      }
    }

    return response;
  }

  public void close() throws Exception {
    connection.close();
  }

  public static void main(String[] argv) {
    RabbitMQClient rabbitMQClient = null;
    String response = null;
    try {
      rabbitMQClient = new RabbitMQClient();

      System.out.println(" [x] Requesting fib(30)");
      response = rabbitMQClient.sendMessage("30");
      System.out.println(" [.] Got '" + response + "'");
    }
    catch  (Exception e) {
      e.printStackTrace();
    }
    finally {
      if (rabbitMQClient!= null) {
        try {
          rabbitMQClient.close();
        }
        catch (Exception ignore) {}
      }
    }
  }
}