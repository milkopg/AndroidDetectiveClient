package project.android.softuni.bg.androiddetectiveclient.broadcast.call;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import project.android.softuni.bg.androiddetectiveclient.service.DetectiveIntentService;
import project.android.softuni.bg.androiddetectiveclient.service.DetectiveService;

public class OutgoingCallBroadcastReceiver extends BroadcastReceiver {
  public OutgoingCallBroadcastReceiver() {
  }

  @Override
  public void onReceive(Context context, Intent intent) {
    if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
// If it is to call (outgoing)
     // Intent i = new Intent(context, DetectiveIntentService.class);
      Intent i = new Intent(context, DetectiveService.class);
      i.putExtras(intent);
      context.startActivity(i);
    }
  }
}
