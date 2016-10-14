package project.android.softuni.bg.androiddetectiveclient.broadcast.boot;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import project.android.softuni.bg.androiddetectiveclient.util.ServiceManager;

public class OnBootCompleteBroadcastReceiver extends WakefulBroadcastReceiver{
private static  final String TAG = OnBootCompleteBroadcastReceiver.class.getSimpleName();

  /**
   * Once phone is restarted start DetentiveIntentService
   * @param context
   * @param intent
   */
  @Override
  public void onReceive(Context context, Intent intent) {
    if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
      ServiceManager.startService(context, null);
      Log.d(TAG, "OnBootCompleteBroadcastReceiver started");
   }
  }
}
