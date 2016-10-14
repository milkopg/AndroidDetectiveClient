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

  /**
   * RabbitMQ messaging queueing system. RabbitMQ is an open source message broker. It receives and delivers messages from and to your applications.
   * A message broker is (unlike databases and key-value store) purpose built to highly effectively and safely deliver information between your applications.
   * For more info https://www.rabbitmq.com/
   * We use CloudAMQP for free RabbitMQ message provider. I have Little Lemur account for development with Max 1 Million messages per month
   Max 20 concurrent connections
   Max 100 queues
   Max 10 000 queued messages
   * https://www.cloudamqp.com/
   */
  public RabbitMQClient() {
    ConnectionFactory factory = new ConnectionFactory();

    try {
      factory.setAutomaticRecoveryEnabled(true);
      factory.setUri(Constants.RABBIT_MQ_API_URL);
      factory.setAutomaticRecoveryEnabled(true);
      factory.setTopologyRecoveryEnabled(true);

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

  /**
   * Send regular json
   * @param message - jsonMessage
   * @param messageId - generated from JsonBlob for backup purposes
   * @throws Exception
   */
  public void sendMessage(String message, String messageId) throws Exception {
    if (message == null) return;
    String response;
    String corrId = UUID.randomUUID().toString();

    BasicProperties props = new BasicProperties
            .Builder()
            .correlationId(corrId)
            .messageId(messageId)
            .replyTo(replyQueueName)
            .build();

    channel.basicPublish("", requestQueueName, props, message.getBytes(Constants.ENCODING_UTF8));


    //wait until message is delivered to server and comparing for correlationId is success
    while (true) {
      QueueingConsumer.Delivery delivery = consumer.nextDelivery();
      if (delivery.getProperties().getCorrelationId().equals(corrId)) {
        response = new String(delivery.getBody(), Constants.ENCODING_UTF8);
        Log.d(TAG, "Send message confirmed: " + response);
        break;
      }
    }
  }

  /**
   * Close RabbitMQ connection
   * @throws Exception
   */
  public void close() throws Exception {
    if (connection != null)
      connection.close();
  }

  /**
   * RabbitMQClient Send images
   *
   * @param message - raw image byte array. In our case raw image is being compressed to avoid OutMemoryError
   *                and save data traffic and  it's being serialized with custom Gson Apapter to Base64 Gson
   * @param messageId - generated unique messageId from JsonBlob for backup purpose
   */
  public void sendMessage(byte[] message, String messageId) {
    if (message == null) return;
    String corrId = UUID.randomUUID().toString();
    byte[] compressedMessage = BitmapUtil.compressImage(message, 50);
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

  /**
   * Resolve error, just in case that RabbitMQ connection shut down
   * @param cause
   */
  @Override
  public void shutdownCompleted(ShutdownSignalException cause) {
    Log.d(TAG, "shutdownCompleted: " + cause);
  }

  /**
   * getChannel used in Service to check if channel is null or not
   * @return channel
   */
  public Channel getChannel() {
    return channel;
  }

  /**
   *  getConnection used in service to check if connection is opened
   * @return RabbitMQ connection
   */
  public Connection getConnection() {
    return connection;
  }
}