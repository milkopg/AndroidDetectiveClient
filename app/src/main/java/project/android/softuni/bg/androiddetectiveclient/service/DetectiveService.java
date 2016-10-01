package project.android.softuni.bg.androiddetectiveclient.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import project.android.softuni.bg.androiddetectiveclient.broadcast.listener.IServiceCommunicationListener;
import project.android.softuni.bg.androiddetectiveclient.rabbitmq.RabbitMQClient;
import project.android.softuni.bg.androiddetectiveclient.util.Constants;

public class DetectiveService extends Service {
  private IBinder binder = new DetectiveServiceBinder();
  private IServiceCommunicationListener callback;
  private static final String TAG = DetectiveService.class.getSimpleName();


  @Override
  public void onCreate() {
    super.onCreate();
  }

  public void setServiceCallback(IServiceCommunicationListener listener) {
    this.callback = listener;
  }

  public class DetectiveServiceBinder extends Binder {
    public DetectiveService getService() {
      return DetectiveService.this;
    }
  }

  @Override
  public IBinder onBind(Intent intent) {
    Toast.makeText(getApplicationContext(), "Service Binded: ", Toast.LENGTH_SHORT).show();
    try {

    } catch (Exception e) {
      Log.e(TAG, "Cannot create RabbitMQClient" + e);
    }
    return binder;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Toast.makeText(getApplicationContext(), "Service started: ", Toast.LENGTH_SHORT).show();
    if (intent.hasExtra(Constants.MESSAGE_TO_SEND)) {
      String message = intent.getStringExtra(Constants.MESSAGE_TO_SEND);
      sendMessage(message);
    }
    return super.onStartCommand(intent, flags, startId);
  }

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


  @Override
  public void onDestroy() {
    super.onDestroy();
  }
}
