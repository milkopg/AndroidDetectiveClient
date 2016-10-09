package project.android.softuni.bg.androiddetectiveclient;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.gson.Gson;

import project.android.softuni.bg.androiddetectiveclient.broadcast.listener.IServiceCommunicationListener;
import project.android.softuni.bg.androiddetectiveclient.broadcast.receiver.CustomResultReceiver;
import project.android.softuni.bg.androiddetectiveclient.service.DetectiveIntentService;
import project.android.softuni.bg.androiddetectiveclient.service.DetectiveService;
import project.android.softuni.bg.androiddetectiveclient.util.Constants;
import project.android.softuni.bg.androiddetectiveclient.util.ServiceConnectionManager;
import project.android.softuni.bg.androiddetectiveclient.webapi.task.SendDataTask;

public class MainActivity extends AppCompatActivity implements IServiceCommunicationListener, CustomResultReceiver.Receiver {

  private ServiceConnection mConnection;
  private Intent mServiceIntent;
  private static final String TAG = MainActivity.class.getSimpleName();
  private CustomResultReceiver mReceiver;

  @Override
  public void sendJsonData(String json) {
    new SendDataTask(json).execute();
  }

  @Override
  public void onReceiveResult(int resultCode, Bundle resultData) {

  }


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mConnection = ServiceConnectionManager.getInstance(MainActivity.this);
    mReceiver = new CustomResultReceiver(new Handler());
    mReceiver.setReceiver(this);

    // mServiceIntent = new Intent(this, DetectiveService.class);

    // mServiceIntent= new Intent(this, DetectiveIntentService.class);
    mServiceIntent = new Intent(Intent.ACTION_SYNC, null, this, DetectiveIntentService.class);
    mServiceIntent.putExtra(Constants.RECEIVER, mReceiver);
    startService(mServiceIntent);

    bindService(mServiceIntent, mConnection, Context.BIND_AUTO_CREATE);

    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

      ActivityCompat.requestPermissions(this,
              new String[]{Manifest.permission.READ_SMS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.SEND_SMS, Manifest.permission.BROADCAST_SMS,
                      Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS},
              1);

//      // Should we show an explanation?
//      if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//              Manifest.permission.READ_SMS)) {
//
//        // Show an expanation to the user *asynchronously* -- don't block
//        // this thread waiting for the user's response! After the user
//        // sees the explanation, try again to request the permission.
//
//      } else {
//
//        // No explanation needed, we can request the permission.
//
//        ActivityCompat.requestPermissions(this,
//                new String[]{Manifest.permission.READ_SMS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.SEND_SMS, Manifest.permission.BROADCAST_SMS,
//                        Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS},
//                1);
//
//        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//        // app-defined int constant. The callback method gets the
//        // result of the request.
//      }
//    } else {
//      ActivityCompat.requestPermissions(this,
//              new String[]{Manifest.permission.READ_SMS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.SEND_SMS, Manifest.permission.BROADCAST_SMS,
//                      Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS},
//              1);
    }
  }

  @Override
  protected void onDestroy() {
//    if (mConnection != null)
//      unbindService(mConnection);
    super.onDestroy();

  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
  }
}
