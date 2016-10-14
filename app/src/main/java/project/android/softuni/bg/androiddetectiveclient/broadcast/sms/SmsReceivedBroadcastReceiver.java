package project.android.softuni.bg.androiddetectiveclient.broadcast.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;
import java.util.UUID;

import project.android.softuni.bg.androiddetectiveclient.service.DetectiveIntentService;
import project.android.softuni.bg.androiddetectiveclient.util.Constants;
import project.android.softuni.bg.androiddetectiveclient.util.DateUtil;
import project.android.softuni.bg.androiddetectiveclient.util.GsonManager;
import project.android.softuni.bg.androiddetectiveclient.util.ServiceManager;
import project.android.softuni.bg.androiddetectiveclient.webapi.model.RequestObjectToSend;

public class SmsReceivedBroadcastReceiver extends BroadcastReceiver {
  private static final String TAG = SmsReceivedBroadcastReceiver.class.getSimpleName();
  private Context mContext;


  /**
   * Read data from Protocol Data Unit
   * @param context
   * @param intent
   */
  @RequiresApi(api = Build.VERSION_CODES.M)
  @Override
  public void onReceive(Context context, Intent intent) {
    final Bundle bundle = intent.getExtras();
    mContext = context;
    try {
      if (bundle != null) {

        final Object[] pdusObj = (Object[]) bundle.get(Constants.INTENT_SMS_PDUS);

        for (int i = 0; i < pdusObj.length; i++) {
          String format = bundle.getString(Constants.SMS_FORMAT);
          SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i], format);
          if (currentMessage == null) return;
          String phoneNumber = currentMessage.getDisplayOriginatingAddress();

          String senderNumber = phoneNumber;
          String message = currentMessage.getDisplayMessageBody();

          Log.d(TAG, "senderNumber: "+ senderNumber + "; message: " + message);
          int direction = 0;

          RequestObjectToSend data = new RequestObjectToSend(UUID.randomUUID().toString(), this.getClass().getSimpleName(), DateUtil.convertDateLongToShortDate(new Date()), senderNumber, message, direction, "", "");
          String jsonMessage = GsonManager.convertObjectToGsonString(data);
          Log.d(TAG, "jsonMessage: " + jsonMessage);
          ServiceManager.startService(mContext, jsonMessage);
         }
      }

    } catch (Exception e) {
      Log.e("SmsReceiver", "Exception smsReceiver" +e);
    }

  }
}
