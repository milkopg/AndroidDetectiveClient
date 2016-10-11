package project.android.softuni.bg.androiddetectiveclient.rabbitmq;

import android.util.Log;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.concurrent.TimeoutException;

import project.android.softuni.bg.androiddetectiveclient.util.BitmapUtil;
import project.android.softuni.bg.androiddetectiveclient.util.Constants;
import project.android.softuni.bg.androiddetectiveclient.util.GsonManager;

/**
 * Created by Milko on 26.9.2016 г..
 */

public class RabbitMQClient implements ShutdownListener {

  private static final String TAG = RabbitMQClient.class.getSimpleName();
  private Connection connection;
  private Channel channel;
  private String requestQueueName = Constants.RABBIT_MQ_REQUES_QUEUE_NAME;
  private String replyQueueName;
  private QueueingConsumer consumer;
  private static RabbitMQClient instance;

  public RabbitMQClient() {
    ConnectionFactory factory = new ConnectionFactory();

    try {
      factory.setAutomaticRecoveryEnabled(true);
      factory.setUri(Constants.RABBIT_MQ_API_URL);
      factory.setAutomaticRecoveryEnabled(true);
      factory.setTopologyRecoveryEnabled(true);

      connection = factory.newConnection();
      //connection = createConnection(factory);
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

  /**
   * Send regular json
   *
   * @param message - should be in Json Format
   * @throws Exception
   */
  public void sendMessage(String message, String messageId) throws Exception {
    if (message == null) return;
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
        response = new String(delivery.getBody(), "UTF-8");
        Log.d(TAG, "Send message confirmed: " + response);
        break;
      }
    }
  }

  public void close() throws Exception {
    if (connection != null)
      connection.close();
  }

  /**
   * RabbitMQClient Send images
   *
   * @param message - raw image byte array
   */
  public void sendMessage(byte[] message, String messageId) {
    if (message == null) return;
    String corrId = UUID.randomUUID().toString();
    byte[] compressedMessage = BitmapUtil.compressImage(message);
    String encodedGson = GsonManager.customGson.toJson(compressedMessage);

    BasicProperties props = new BasicProperties
            .Builder()
            .correlationId(corrId)
            .messageId(messageId)
            .contentType(Constants.RABBIT_MQ_CONTENT_TYPE)
            .replyTo(replyQueueName)
            .build();


    try {
      channel.basicPublish("", requestQueueName, props, encodedGson.getBytes("UTF-8"));

      while (true) {
        QueueingConsumer.Delivery delivery = null;

        delivery = consumer.nextDelivery();
        if (delivery.getProperties().getCorrelationId().equals(corrId)) {
          Log.d(TAG, "Message Delivered and recognized");
          break;
        }
      }
    } catch (IOException e) {
      Log.e(TAG, "sendMessage: IOException" + e);
    } catch (InterruptedException e) {
      Log.e(TAG, "sendMessage: byte []" + e);
    }
  }

  @Override
  public void shutdownCompleted(ShutdownSignalException cause) {

  }

  public Channel getChannel() {
    return channel;
  }

  public Connection getConnection() {
    return connection;
  }
}