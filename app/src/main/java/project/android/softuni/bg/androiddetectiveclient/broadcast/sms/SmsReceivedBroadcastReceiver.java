package project.android.softuni.bg.androiddetectiveclient.broadcast.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.annotation.RequiresApi;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import project.android.softuni.bg.androiddetectiveclient.util.Constants;

public class SmsReceivedBroadcastReceiver extends BroadcastReceiver {
  final SmsManager sms = SmsManager.getDefault();


  @Override
  public void onReceive(Context context, Intent intent) {
    final Bundle bundle = intent.getExtras();
    try {

      if (bundle != null) {

        final Object[] pdusObj = (Object[]) bundle.get(Constants.INTENT_SMS_PDUS);

        for (int i = 0; i < pdusObj.length; i++) {

          SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
          String phoneNumber = currentMessage.getDisplayOriginatingAddress();

          String senderNumber = phoneNumber;
          String message = currentMessage.getDisplayMessageBody();

          Log.i("SmsReceiver", "senderNumber: "+ senderNumber + "; message: " + message);


          // Show alert
          int duration = Toast.LENGTH_LONG;
          Toast toast = Toast.makeText(context, "senderNumber: "+ senderNumber + ", message: " + message, duration);
          toast.show();

        } // end for loop
      } // bundle is null

    } catch (Exception e) {
      Log.e("SmsReceiver", "Exception smsReceiver" +e);

    }

  }
}
