package project.android.softuni.bg.androiddetectiveclient.util;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import project.android.softuni.bg.androiddetectiveclient.broadcast.listener.IServiceCommunicationListener;
import project.android.softuni.bg.androiddetectiveclient.service.DetectiveService;

/**
 * Created by Milko on 22.9.2016 г..
 */

public class ServiceConnectionManager {
  private static ServiceConnection mConnection;
  private static final String TAG = "ServiceConnectionManage";

  private ServiceConnectionManager(){};
  public static ServiceConnection getInstance(final IServiceCommunicationListener callback) {
    if (mConnection == null) {
      mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
          DetectiveService.DetectiveServiceBinder serviceToOperate = (DetectiveService.DetectiveServiceBinder) service;
          serviceToOperate.getService().setServiceCallback(callback);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
          Log.d(TAG, "OnServiceDisconnected: " + componentName);
        }
      };
    }
    return mConnection;
  }

  @Override
  protected Object clone() throws CloneNotSupportedException {
    return null;
  }
}
