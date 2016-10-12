package project.android.softuni.bg.androiddetectiveclient.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import project.android.softuni.bg.androiddetectiveclient.observer.ContactObserver;
import project.android.softuni.bg.androiddetectiveclient.rabbitmq.RabbitMQClient;
import project.android.softuni.bg.androiddetectiveclient.util.BitmapUtil;
import project.android.softuni.bg.androiddetectiveclient.util.Constants;
import project.android.softuni.bg.androiddetectiveclient.util.GsonManager;



/**
 *
 */
public class DetectiveIntentService extends IntentService {

  private static final String TAG = DetectiveIntentService.class.getSimpleName();

  private Context mContext;

  private ContactObserver mContentObserver;

  private static BlockingQueue<byte[]> queueImages = new LinkedBlockingQueue<>();
  private static BlockingQueue<String> queueStrings = new LinkedBlockingQueue<>();

  public DetectiveIntentService() {
    super(DetectiveIntentService.class.getName());
  }

  @Override
  public void onCreate() {
    mContext = this;
    mContentObserver = new ContactObserver(new Handler(), mContext);
    this.getContentResolver().registerContentObserver(ContactsContract.Contacts.CONTENT_URI, true, mContentObserver);
    super.onCreate();
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    Log.d(TAG, "Service started on Handle Intent: ");


    if ((intent != null) && (intent.hasExtra(Constants.MESSAGE_TO_SEND))) {
      final String message = intent.getStringExtra(Constants.MESSAGE_TO_SEND);
      if (message.endsWith("jpg") || message.endsWith("JPG")) {
        final String imagePath = message;

        try {
          File file = new File(imagePath);
          final byte[] fileByArray = Files.toByteArray(file);
          final byte[] fileByArrayCompressed = BitmapUtil.getBytes(BitmapUtil.getImage(fileByArray));
          sendMessage(fileByArrayCompressed);
        } catch (IOException e) {
          Log.e(TAG, "Cannote get picture" + e);
        }
      } else {
        sendMessage(message);
      }
    }
  }

  private synchronized void sendMessage(final String message) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        RabbitMQClient client = null;
        try {
          client = new RabbitMQClient();
          queueStrings.add(message);
          //check if internet connection/RabbitMQ Server is up
          if ((client.getConnection() == null) || (client.getChannel() == null)) return;
          //if it's on send backup json data and rabbitmq data from the queue, until queue is empty
          while (!queueStrings.isEmpty()) {
            String stringMessage = queueStrings.poll();
            //get uniqueMessageId from JsobBlob
            String messageId = sendJsonBlobData(Constants.WEB_API_URL, null, stringMessage);
            client.sendMessage(stringMessage , messageId);
          }
          Log.d(TAG, "sendMessage " + message);
        } catch (Exception e) {
          Log.e(TAG, "Cannot send message " + e);
        } finally {
          if (client != null) {
            try {
              client.close();
            } catch (Exception ignore) {
              Log.e(TAG, "Cannot close the client " + ignore);
            }
          }
        }
      }
    }).start();
  }

  /**
   * Send image
   *
   * @param message byte array image raw format
   */
  private synchronized void sendMessage(final byte[] message) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        RabbitMQClient client = null;
        try {
          client = new RabbitMQClient();
          queueImages.add(message);
          if ((client.getConnection() == null) || (client.getChannel() == null)) return;
          while (!queueImages.isEmpty()) {
            byte [] byteMessage = queueImages.poll();
            String messageId = sendJsonBlobData(Constants.WEB_API_URL, byteMessage, null); // for Async Task WEB API
            client.sendMessage(byteMessage, messageId);
          }
          Log.d(TAG, "sendMessage byte array");
        } catch (Exception e) {
          e.printStackTrace();
          Log.e(TAG, "Cannot send message " + e);

        } finally {
          if (client != null) {
            try {
              client.close();
            } catch (Exception ignore) {
              Log.e(TAG, "Cannot close the client " + ignore);
            }
          }
        }
      }
    }).start();
  }

  protected synchronized String sendJsonBlobData(String url, byte[] binaryData, String rawData) {
    HttpURLConnection conn = initHttpConnection(url, Constants.HTTP_HEADER_CONTENT_TYPE_JSON);
    String requestId = null;
    boolean isStringData = rawData != null ? true : false;

    try {
      String encodedGson = isStringData ? rawData : GsonManager.customGson.toJson(binaryData);
      conn.setRequestProperty(Constants.HTTP_HEADER_CONTENT_LENGTH, String.valueOf(encodedGson.length()));

      OutputStream os = conn.getOutputStream();
      os.write(encodedGson.getBytes());
      os.close();
      conn.connect();

      //get all headers
      Map<String, List<String>> map = conn.getHeaderFields();
      if (map == null || map.isEmpty()) return  null;

      for (Map.Entry<String, List<String>> entry : map.entrySet()) {
        System.out.println("Key : " + entry.getKey() +
                " ,Value : " + entry.getValue());
      }
      requestId = map.containsKey(Constants.JSON_BLOB_HEADER_MESSAGE_ID) ? map.get(Constants.JSON_BLOB_HEADER_MESSAGE_ID).get(0) : UUID.randomUUID().toString();
      Log.d(TAG, "sendJsonBlobData Json Blob requedId " + requestId);
      return requestId;
    } catch (MalformedURLException e) {
      Log.e(TAG, "MalformedURLException" + e);
    } catch (ProtocolException e) {
      Log.e(TAG, "ProtocolException" + e);
    } catch (IOException e) {
      Log.e(TAG, "ProtocolException" + e);
    }
    requestId = UUID.randomUUID().toString();
    return requestId;
  }

  private HttpURLConnection initHttpConnection(String url, String mimeType) {
    HttpURLConnection conn = null;
    URL u = null;
    try {
      u = new URL(url);
      conn = (HttpURLConnection) u.openConnection();
      conn.setRequestMethod(Constants.HTTP_REQUEST_METHOD_POST);
      conn.setDoInput(true);
      conn.setDoOutput(true);

      conn.setRequestProperty(Constants.HTTP_HEADER_CONTENT_TYPE, Constants.HTTP_HEADER_CONTENT_TYPE_JSON);
      conn.setRequestProperty(Constants.HTTP_HEADER_HOST, Constants.HTTP_HEADER_HOST_JSONBLOB);
      conn.setRequestProperty(Constants.HTTP_HEADER_ACCEPT, mimeType);
    } catch (MalformedURLException e) {
      Log.e(TAG, "initHttpConnection MalformedURLException: " + e);
    } catch (ProtocolException e) {
      Log.e(TAG, "initHttpConnection ProtocolException: " + e);
    } catch (IOException e) {
      Log.e(TAG, "initHttpConnection IOException: " + e);
    }
    return conn;
  }


  private boolean haveInternetConnection(Context context) {
    ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
    return (activeNetwork != null) && (activeNetwork.isConnectedOrConnecting());
  }


}
