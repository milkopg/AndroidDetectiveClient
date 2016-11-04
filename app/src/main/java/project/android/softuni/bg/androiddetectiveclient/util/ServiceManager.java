package project.android.softuni.bg.androiddetectiveclient.util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import project.android.softuni.bg.androiddetectiveclient.service.DetectiveIntentService;

/**
 * Created by Milko on 14.10.2016 г..
 */

public class ServiceManager {
  private static final String TAG = ServiceManager.class.getSimpleName();


  /**
   * Start DetectiveIntentService from Broadcast Receiver
   * @param context - Content
   * @param data - data for send, usually is JsonMessage converted to String, or imagePath needed for images
   */
  public static void startService(Context context, String data) {
    if (context == null) return;
    Intent service=new Intent(context, DetectiveIntentService.class);
    if (data != null) {
      service.putExtra(Constants.MESSAGE_TO_SEND, data);
      Log.d(TAG, "data: " + data);
    }
    context.startService(service);
  }
}
