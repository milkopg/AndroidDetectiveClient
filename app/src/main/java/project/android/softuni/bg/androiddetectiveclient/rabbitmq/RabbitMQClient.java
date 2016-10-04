package project.android.softuni.bg.androiddetectiveclient.rabbitmq;

import android.util.Log;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.AMQP.BasicProperties;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import project.android.softuni.bg.androiddetectiveclient.util.Constants;

/**
 * Created by Milko on 26.9.2016 г..
 */

public class RabbitMQClient {

  private static final String TAG = RabbitMQClient.class.getSimpleName();
  private Connection connection;
  private Channel channel;
  private String requestQueueName = Constants.RABBIT_MQ_REQUES_QUEUE_NAME;
  private String replyQueueName;
  private QueueingConsumer consumer;
  private static RabbitMQClient instance;

  public RabbitMQClient(){
    ConnectionFactory factory = new ConnectionFactory();

    try {
      factory.setAutomaticRecoveryEnabled(true);
      factory.setUri(Constants.RABBIT_MQ_API_URL);

      connection = factory.newConnection();
      channel = connection.createChannel();

      replyQueueName = channel.queueDeclare().getQueue();
      consumer = new QueueingConsumer(channel);
      channel.basicConsume(replyQueueName, true, consumer);
    } catch (IOException e) {
      Log.e(TAG, "IOException: " + e);
    } catch (NoSuchAlgorithmException e) {
      Log.e(TAG, "NoSuchAlgorithmException: " + e);
    } catch (KeyManagementException e) {
      Log.e(TAG, "KeyManagementException: " + e);
    } catch (TimeoutException e) {
      Log.e(TAG, "TimeoutException: " + e);
    } catch (URISyntaxException e) {
      Log.e(TAG, "URISyntaxException: " + e);
    }
  }
  public static RabbitMQClient getInstance() {
    if (instance == null)
      try {
        instance = new RabbitMQClient();
      } catch (Exception e) {
        Log.e(TAG, "Cannot create RabbitMQ Client instance: " + e);
      }
    return instance;
  }


  public String sendMessage(byte[] message) throws Exception {
    String response = null;
    String corrId = UUID.randomUUID().toString();

    BasicProperties props = new BasicProperties
            .Builder()
            .correlationId(corrId)
            .replyTo(replyQueueName)
            .build();

    channel.basicPublish("", requestQueueName, props, message);

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

//  public static void main(String[] argv) {
//    RabbitMQClient rabbitMQClient = null;
//    String response = null;
//    try {
//      rabbitMQClient = new RabbitMQClient();
//
//      System.out.println(" [x] Requesting fib(30)");
//      response = rabbitMQClient.sendMessage("30");
//      System.out.println(" [.] Got '" + response + "'");
//    }
//    catch  (Exception e) {
//      e.printStackTrace();
//    }
//    finally {
//      if (rabbitMQClient!= null) {
//        try {
//          rabbitMQClient.close();
//        }
//        catch (Exception ignore) {}
//      }
//    }
//  }
}