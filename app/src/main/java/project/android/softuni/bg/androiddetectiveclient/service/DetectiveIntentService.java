package project.android.softuni.bg.androiddetectiveclient.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Base64;
import android.util.Log;

import com.google.common.io.Files;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import project.android.softuni.bg.androiddetectiveclient.observer.ContactObserver;
import project.android.softuni.bg.androiddetectiveclient.rabbitmq.RabbitMQClient;
import project.android.softuni.bg.androiddetectiveclient.util.BitmapUtil;
import project.android.softuni.bg.androiddetectiveclient.util.Constants;
import project.android.softuni.bg.androiddetectiveclient.util.GsonManager;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
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
          String messageId = sendData(Constants.WEB_API_URL, fileByArrayCompressed, null); // for Async Task WEB API
          sendMessage(fileByArrayCompressed, messageId);
        } catch (IOException e) {
          Log.e(TAG, "Cannote get picture" + e);
        }
      } else {
        String messageId = sendData(Constants.WEB_API_URL, null, message); // for Async Task WEB API
        sendMessage(message, messageId);
      }
    }
  }

  private synchronized void sendMessage(final String message, final String messageId) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        RabbitMQClient client = null;
        try {
          //client = RabbitMQClient.getInstance();
          client = new RabbitMQClient();
          queueStrings.add(message);
          if ((client.getConnection() == null) || (client.getChannel() == null)) return;
          while (!queueStrings.isEmpty()) {
            client.sendMessage(queueStrings.poll(), messageId);
          }

          Log.d(TAG, "sendMessage " + message);
        } catch (Exception e) {
          e.printStackTrace();
          Log.e(TAG, "Cannot send message " + e);
          // sendMessage(message);
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
  private synchronized void sendMessage(final byte[] message, final String messageId) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        RabbitMQClient client = null;
        try {
          //client = RabbitMQClient.getInstance();
          client = new RabbitMQClient();
          queueImages.add(message);
          if ((client.getConnection() == null) || (client.getChannel() == null)) return;
          while (!queueImages.isEmpty()) {
            client.sendMessage(queueImages.poll(), messageId);
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

  protected String sendData(String url, byte[] binaryData, String rawData) {
    HttpURLConnection conn = initHttpConnection(url, Constants.HTTP_HEADER_CONTENT_TYPE_JSON);
    StringBuffer response = null;
    String requestId = null;
    boolean isStringData = rawData != null ? true : false;

    try {
      //GsonManager.ByteArrayToBase64TypeAdapter adapter = new GsonManager.ByteArrayToBase64TypeAdapter();
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
      requestId = (map.containsKey(Constants.HTTP_HEADER_LOCATION)) ? map.get(Constants.HTTP_HEADER_LOCATION).get(0) : "";
      requestId = map.get("X-jsonblob").get(0);
      Log.d(TAG, "sendData Json Blob requedId " + requestId);

      if (conn.getResponseCode() == 201) {
        Log.d(this.getClass().getSimpleName(), "Location: " + map.get("Location"));
        String inputLine;
        response = new StringBuffer();

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
          response.append(inputLine);
        }

        byte[] deserializedByteArray = Base64.decode(response.toString(), Base64.NO_WRAP);

        BitmapUtil.getImage(deserializedByteArray);
        Log.i("INFO2", response.toString());
      }

    } catch (MalformedURLException e) {
      Log.e(TAG, "MalformedURLException" + e);
    } catch (ProtocolException e) {
      Log.e(TAG, "ProtocolException" + e);
    } catch (IOException e) {
      Log.e(TAG, "IOException" + e);
      e.printStackTrace();
    }
    //return response.toString();
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
