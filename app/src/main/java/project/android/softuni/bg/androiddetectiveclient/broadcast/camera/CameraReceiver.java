package project.android.softuni.bg.androiddetectiveclient.broadcast.camera;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Path;
import android.os.Bundle;
import android.widget.Toast;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;

import project.android.softuni.bg.androiddetectiveclient.service.DetectiveIntentService;
import project.android.softuni.bg.androiddetectiveclient.util.Constants;

public class CameraReceiver extends BroadcastReceiver {
  public CameraReceiver() {
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    //
    Bundle bundle = intent.getExtras();  //.get("act")
    Toast.makeText(context, "New Photo Clicked + " + intent.getDataString(), Toast.LENGTH_LONG).show();

    Cursor cursor = context.getContentResolver().query(intent.getData(),      null,null, null, null);
    cursor.moveToFirst();
    String image_path = cursor.getString(cursor.getColumnIndex("_data"));
    File file = new File(image_path);
    byte [] fileByArray = new byte[0];
    try {
      fileByArray = Files.toByteArray(file);
    } catch (IOException e) {
      e.printStackTrace();
    }
    Intent service=new Intent(context, DetectiveIntentService.class);
    service.putExtra(Constants.MESSAGE_TO_SEND, fileByArray);
    context.startService(service);

    Toast.makeText(context, "New Photo is Saved as : -" + image_path, Toast.LENGTH_SHORT).show();
  }
    // TODO: This method is called when the BroadcastReceiver is receiving
    // an Intent broadcast.
    //throw new UnsupportedOperationException("Not yet implemented");

}
