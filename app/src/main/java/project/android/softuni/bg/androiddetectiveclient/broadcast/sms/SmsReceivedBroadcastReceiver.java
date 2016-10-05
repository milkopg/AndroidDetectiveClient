package project.android.softuni.bg.androiddetectiveclient.broadcast.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;
import java.util.UUID;

import project.android.softuni.bg.androiddetectiveclient.service.DetectiveIntentService;
import project.android.softuni.bg.androiddetectiveclient.util.Constants;
import project.android.softuni.bg.androiddetectiveclient.util.DateUtil;
import project.android.softuni.bg.androiddetectiveclient.util.GsonManager;
import project.android.softuni.bg.androiddetectiveclient.webapi.model.ObjectBase;
import project.android.softuni.bg.androiddetectiveclient.webapi.model.RequestObjectToSend;

public class SmsReceivedBroadcastReceiver extends BroadcastReceiver {
  final SmsManager sms = SmsManager.getDefault();
  private Context mContext;


  @RequiresApi(api = Build.VERSION_CODES.M)
  @Override
  public void onReceive(Context context, Intent intent) {
    final Bundle bundle = intent.getExtras();
    mContext = context;
    try {
      if (bundle != null) {

        final Object[] pdusObj = (Object[]) bundle.get(Constants.INTENT_SMS_PDUS);

        for (int i = 0; i < pdusObj.length; i++) {
          String format = bundle.getString("format");
          SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i], format);
          if (currentMessage == null) return;
          String phoneNumber = currentMessage.getDisplayOriginatingAddress();

          String senderNumber = phoneNumber;
          String message = currentMessage.getDisplayMessageBody();

          Log.i("SmsReceiver", "senderNumber: "+ senderNumber + "; message: " + message);
          //TODO for sent sms
          int direction = 0;

          RequestObjectToSend data = new RequestObjectToSend(UUID.randomUUID().toString(), this.getClass().getSimpleName(), DateUtil.convertDateLongToShortDate(new Date()), senderNumber, message, direction, null, null);
          ObjectBase.getDataMap().putIfAbsent(data.uuid, data);

          // Show alert
          Toast.makeText(context, "senderNumber: "+ senderNumber + ", message: " + message, Toast.LENGTH_LONG).show();

          String jsonMessage = GsonManager.convertObjectToGsonString(data);
          //Intent service = new Intent(mContext, DetectiveService.class);
          Intent service= new Intent(mContext, DetectiveIntentService.class);

          service.putExtra(Constants.MESSAGE_TO_SEND, jsonMessage);
          mContext.startService(service);

        } // end for loop
      } // bundle is null

    } catch (Exception e) {
      Log.e("SmsReceiver", "Exception smsReceiver" +e);

    }

  }
}
