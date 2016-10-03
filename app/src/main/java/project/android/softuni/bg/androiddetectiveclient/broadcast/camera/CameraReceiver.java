package project.android.softuni.bg.androiddetectiveclient.broadcast.camera;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class CameraReceiver extends BroadcastReceiver {
  public CameraReceiver() {
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    //
    Bundle bundle = intent.getExtras();  //.get("act")
    Toast.makeText(context, "New Photo Clicked + " + intent.getDataString(), Toast.LENGTH_LONG).show();
    // TODO: This method is called when the BroadcastReceiver is receiving
    // an Intent broadcast.
    //throw new UnsupportedOperationException("Not yet implemented");
  }
}
