package project.android.softuni.bg.androiddetectiveclient.broadcast.camera;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;

import project.android.softuni.bg.androiddetectiveclient.service.DetectiveIntentService;
import project.android.softuni.bg.androiddetectiveclient.util.Constants;

public class CameraReceiver extends BroadcastReceiver {
  private static final String TAG = CameraReceiver.class.getSimpleName();

  public CameraReceiver() {
  }

  /**
   * Get image path from Camera content resolver and send this info to IntentService
   * @param context
   * @param intent
   */
  @Override
  public void onReceive(Context context, Intent intent) {
    Toast.makeText(context, "New Photo Clicked + " + intent.getDataString(), Toast.LENGTH_LONG).show();

    Cursor cursor = context.getContentResolver().query(intent.getData(), null, null, null, null);
    if (cursor == null) return;
    cursor.moveToFirst();
    String imagePath = cursor.getString(cursor.getColumnIndex(Constants.CAMERA_RECEIVER_COLUMN_NAME_DATA));

    if (cursor != null)
      cursor.close();

    Log.d(TAG, "imagePath: " + imagePath);
    Intent service = new Intent(context, DetectiveIntentService.class);
    service.putExtra(Constants.MESSAGE_TO_SEND, imagePath);
    context.startService(service);

    Toast.makeText(context, "New Photo is Saved as : -" + imagePath, Toast.LENGTH_SHORT).show();
  }
}
