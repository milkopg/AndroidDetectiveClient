package project.android.softuni.bg.androiddetectiveclient.broadcast.boot;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.widget.Toast;

import project.android.softuni.bg.androiddetectiveclient.service.DetectiveIntentService;

public class OnBootCompleteBroadcastReceiver extends WakefulBroadcastReceiver{

  @Override
  public void onReceive(Context context, Intent intent) {
    if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
      Toast.makeText(context, "OnBootCompleteBroadcastReceiver started ", Toast.LENGTH_SHORT).show();
      //Intent service = new Intent(context, DetectiveService.class);
      Intent service= new Intent(context, DetectiveIntentService.class);
      context.startService(service);
      //startWakefulService(context, service);
   }
  }
}
