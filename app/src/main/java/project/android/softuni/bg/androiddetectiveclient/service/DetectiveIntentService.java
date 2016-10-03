package project.android.softuni.bg.androiddetectiveclient.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;
import java.util.UUID;

import project.android.softuni.bg.androiddetectiveclient.broadcast.sms.SmsDeliverBroadcastReceiver;
import project.android.softuni.bg.androiddetectiveclient.rabbitmq.RabbitMQClient;
import project.android.softuni.bg.androiddetectiveclient.util.Constants;
import project.android.softuni.bg.androiddetectiveclient.util.DateUtil;
import project.android.softuni.bg.androiddetectiveclient.util.GsonManager;
import project.android.softuni.bg.androiddetectiveclient.webapi.model.RequestObjectToSend;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class DetectiveIntentService extends IntentService {

  private static final String TAG = DetectiveIntentService.class.getSimpleName();

  public DetectiveIntentService() {
    super(DetectiveIntentService.class.getName());
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    //Toast.makeText(getApplicationContext(), "Service started: ", Toast.LENGTH_SHORT).show();
    Log.d(TAG, "Service started on Handle Intent: ");


    if ((intent != null) && (intent.hasExtra(Constants.MESSAGE_TO_SEND))) {
     final String message = intent.getStringExtra(Constants.MESSAGE_TO_SEND);
      new Thread(new Runnable() {
        @Override
        public void run() {
          sendMessage(message);
        }
      }).start();
    } else {
      final ResultReceiver receiver = intent.getParcelableExtra("receiver");
      if (receiver != null) {
        Bundle bundle = new Bundle();
        final String message = GsonManager.convertObjectToGsonString(new RequestObjectToSend(UUID.randomUUID().toString(), SmsDeliverBroadcastReceiver.class.getSimpleName(), DateUtil.convertDateLongToShortDate(new Date()),"12314", "ko staa e feis", 1 ));
        bundle.putString(Constants.MESSAGE_TO_SEND, message);
        receiver.send(101, bundle);
        new Thread(new Runnable() {
          @Override
          public void run() {
            sendMessage(message);
          }
        }).start();
      }

    }
  }

//  @Override
//  public int onStartCommand(Intent intent, int flags, int startId) {
//    if ((intent != null) && (intent.hasExtra(Constants.MESSAGE_TO_SEND))) {
//      final String message = intent.getStringExtra(Constants.MESSAGE_TO_SEND);
//      new Thread(new Runnable() {
//        @Override
//        public void run() {
//          sendMessage(message);
//        }
//      }).start();
//    }
//    return super.onStartCommand(intent,flags, startId);
//  }

  private void sendMessage(final String message) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        RabbitMQClient client = null;
        try {
          //client = RabbitMQClient.getInstance();
          client = new RabbitMQClient();
          if (client == null) return;
          client.sendMessage(message);
        } catch (Exception e) {
          e.printStackTrace();
          Log.e(TAG, "Cannot send message " + e);
          sendMessage(message);
        } finally {
          if (client!= null) {
            try {
              client.close();
            }
            catch (Exception ignore) {
              Log.e(TAG, "Cannot close the client " + ignore);
            }
          }
        }
      }
    }).start();
  }
}
