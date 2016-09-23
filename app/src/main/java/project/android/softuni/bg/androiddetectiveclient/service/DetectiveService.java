package project.android.softuni.bg.androiddetectiveclient.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import project.android.softuni.bg.androiddetectiveclient.broadcast.listener.IServiceCommunicationListener;

public class DetectiveService extends Service {
  private DetectiveSericeBinder binder;
  private IServiceCommunicationListener callback;

  @Override
  public void onCreate() {
    super.onCreate();
  }

  public void setServiceCallback(IServiceCommunicationListener listener) {
    this.callback = listener;
  }

  public class DetectiveSericeBinder extends Binder {
    public DetectiveService getService() {
      return DetectiveService.this;
    }
  }

  @Override
  public IBinder onBind(Intent intent) {
    Toast.makeText(getApplicationContext(), "Service Binded: ", Toast.LENGTH_SHORT).show();
    return binder;
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Toast.makeText(getApplicationContext(), "Service started: ", Toast.LENGTH_SHORT).show();
    return super.onStartCommand(intent, flags, startId);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }
}
