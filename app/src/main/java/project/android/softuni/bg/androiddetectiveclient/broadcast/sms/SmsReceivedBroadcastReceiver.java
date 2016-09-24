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

import java.util.Date;
import java.util.UUID;

import project.android.softuni.bg.androiddetectiveclient.util.Constants;
import project.android.softuni.bg.androiddetectiveclient.webapi.model.ObjectBase;
import project.android.softuni.bg.androiddetectiveclient.webapi.model.RequestObjectToSend;

public class SmsReceivedBroadcastReceiver extends BroadcastReceiver {
  final SmsManager sms = SmsManager.getDefault();


  @RequiresApi(api = Build.VERSION_CODES.M)
  @Override
  public void onReceive(Context context, Intent intent) {
    final Bundle bundle = intent.getExtras();
    try {

      if (bundle != null) {

        final Object[] pdusObj = (Object[]) bundle.get(Constants.INTENT_SMS_PDUS);

        for (int i = 0; i < pdusObj.length; i++) {

          SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i], null);
          String phoneNumber = currentMessage.getDisplayOriginatingAddress();

          String senderNumber = phoneNumber;
          String message = currentMessage.getDisplayMessageBody();

          Log.i("SmsReceiver", "senderNumber: "+ senderNumber + "; message: " + message);

          RequestObjectToSend data = new RequestObjectToSend(UUID.randomUUID().toString(), this.getClass().getSimpleName(), new Date(), senderNumber, message, "");
          ObjectBase.getDataMap().putIfAbsent(data.id, data);

          // Show alert
          int duration = Toast.LENGTH_LONG;
          Toast.makeText(context, "senderNumber: "+ senderNumber + ", message: " + message, Toast.LENGTH_LONG).show();

        } // end for loop
      } // bundle is null

    } catch (Exception e) {
      Log.e("SmsReceiver", "Exception smsReceiver" +e);

    }

  }
}
