package project.android.softuni.bg.androiddetectiveclient;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.gson.Gson;

import java.util.Date;
import java.util.UUID;

import project.android.softuni.bg.androiddetectiveclient.broadcast.boot.OnBootCompleteBroadcastReceiver;
import project.android.softuni.bg.androiddetectiveclient.broadcast.listener.IServiceCommunicationListener;
import project.android.softuni.bg.androiddetectiveclient.service.DetectiveService;
import project.android.softuni.bg.androiddetectiveclient.util.ServiceConnectionManager;
import project.android.softuni.bg.androiddetectiveclient.webapi.model.RequestObjectToSend;
import project.android.softuni.bg.androiddetectiveclient.webapi.task.RetrieveTask;
import project.android.softuni.bg.androiddetectiveclient.webapi.task.SendDataTask;

public class MainActivity extends AppCompatActivity implements IServiceCommunicationListener{

  private ServiceConnection mConnection;
  private Intent mServiceIntent;
  private static final String TAG = MainActivity.class.getSimpleName();

  @Override
  public void sendJsonData(String json) {

  }

  @Override
  public void sendBinaryData(Byte[] binaryData) {

  }



  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mConnection = ServiceConnectionManager.getInstance(MainActivity.this);

    mServiceIntent = new Intent(this, DetectiveService.class);

    bindService(mServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
    startService(mServiceIntent);
    //new RetrieveTask().execute();

    Gson gson = new Gson();

//   RequestObjectToSend requestObjectToSend = new RequestObjectToSend(UUID.randomUUID().toString(), OnBootCompleteBroadcastReceiver.class.getSimpleName(),
//            new Date(), "Milko", "Sender text", "Notes");

//    new SendDataTask(gson.toJson(requestObjectToSend)).execute();

    if (ContextCompat.checkSelfPermission(this,
            Manifest.permission.READ_SMS)
            != PackageManager.PERMISSION_GRANTED) {

      // Should we show an explanation?
      if (ActivityCompat.shouldShowRequestPermissionRationale(this,
              Manifest.permission.READ_SMS)) {

        // Show an expanation to the user *asynchronously* -- don't block
        // this thread waiting for the user's response! After the user
        // sees the explanation, try again to request the permission.

      } else {

        // No explanation needed, we can request the permission.

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_SMS},
                1);

        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
        // app-defined int constant. The callback method gets the
        // result of the request.
      }
    }

  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }

//  private ServiceConnection createServiceConnection() {
//    mConnection = new ServiceConnection() {
//      @Override
//      public void onServiceConnected(ComponentName componentName, IBinder service) {
//        DetectiveService.DetectiveSericeBinder serviceToOperate = (DetectiveService.DetectiveSericeBinder) service;
//        serviceToOperate.getService().setServiceCallback(MainActivity.this);
//      }
//
//      @Override
//      public void onServiceDisconnected(ComponentName componentName) {
//        Log.d(TAG, "OnServiceDisconnected: " + componentName);
//      }
//    };
//    return  mConnection;
//  };


//  @Override
//  protected void onStop() {
//    if (mConnection != null)
//      unbindService(mConnection);
//    super.onStop();
//  }
//
//  @Override
//  protected void onPause() {
//    super.onPause();
//    if (mConnection != null)
//      unbindService(mConnection);
//  }
//
//  @Override
//  protected void onResume() {
//    super.onResume();
//    Intent intent= new Intent(this, DetectiveService.class);
//    bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
//    startService(intent);
//  }
}
