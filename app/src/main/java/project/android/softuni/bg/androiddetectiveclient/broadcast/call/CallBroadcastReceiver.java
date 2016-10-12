package project.android.softuni.bg.androiddetectiveclient.broadcast.call;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import project.android.softuni.bg.androiddetectiveclient.service.DetectiveIntentService;
import project.android.softuni.bg.androiddetectiveclient.util.Constants;
import project.android.softuni.bg.androiddetectiveclient.util.DateUtil;
import project.android.softuni.bg.androiddetectiveclient.util.GsonManager;
import project.android.softuni.bg.androiddetectiveclient.webapi.model.RequestObjectToSend;

public class CallBroadcastReceiver extends BroadcastReceiver {
  public static final String TAG = CallBroadcastReceiver.class.getSimpleName();
  private String previousState;
  private Context mContext;

  @Override
  public void onReceive(Context context, Intent intent) {
    mContext = context;

    String callDuration = "";
    String phNumber = "";
    previousState = intent.getStringExtra(Constants.INTENT_STATE);

    String action = intent.getAction();
    if (action.equalsIgnoreCase(Constants.INTENT_PHONE_STATE)) {

      if (intent.getStringExtra(TelephonyManager.EXTRA_STATE).equals(TelephonyManager.EXTRA_STATE_IDLE)) {

        Uri contacts = CallLog.Calls.CONTENT_URI;
        //check for if user give READ_CALL_LOG permission
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
          Cursor managedCursor = mContext.getContentResolver().query(contacts, null, null, null, android.provider.CallLog.Calls.DATE + " DESC");
          if (managedCursor == null) return;
          int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
          int duration1 = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

          // movetoFirst() gives last ended call
          if (managedCursor.moveToFirst()) {
            phNumber = managedCursor.getString(number);
            callDuration = managedCursor.getString(duration1);
          }
          managedCursor.close();

          String callDurationText = String.format(Locale.getDefault(), "Duration: %s seconds", callDuration);
          String broadcastName = CallBroadcastReceiver.class.getSimpleName();

          int direction = (previousState.equals(TelephonyManager.CALL_STATE_RINGING) || previousState.equals(TelephonyManager.CALL_STATE_IDLE)) ? 0 : 1;
          RequestObjectToSend objectToSend = new RequestObjectToSend(UUID.randomUUID().toString(), broadcastName, DateUtil.convertDateLongToShortDate(new Date()), phNumber, callDurationText, direction, null, null);

          String jsonMessage = GsonManager.convertObjectToGsonString(objectToSend);
          Log.d(TAG, "CallBroadcastReceiver: " + jsonMessage);

          Intent service = new Intent(mContext, DetectiveIntentService.class);
          service.putExtra(Constants.MESSAGE_TO_SEND, jsonMessage);
          mContext.startService(service);
        }
      }
    }
  }
}


