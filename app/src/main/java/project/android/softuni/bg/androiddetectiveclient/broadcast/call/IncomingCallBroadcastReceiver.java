package project.android.softuni.bg.androiddetectiveclient.broadcast.call;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Date;
import java.util.UUID;

import project.android.softuni.bg.androiddetectiveclient.service.DetectiveService;
import project.android.softuni.bg.androiddetectiveclient.util.Constants;
import project.android.softuni.bg.androiddetectiveclient.util.GsonManager;
import project.android.softuni.bg.androiddetectiveclient.webapi.model.ObjectBase;
import project.android.softuni.bg.androiddetectiveclient.webapi.model.RequestObjectToSend;

public class IncomingCallBroadcastReceiver extends BroadcastReceiver {
  public static final String TAG = IncomingCallBroadcastReceiver.class.getSimpleName();
  private Context mContext;


  @Override
  public void onReceive(Context context, Intent intent) {
    mContext = context;
    TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    IPhoneStateListener listener = new IPhoneStateListener();
    tMgr.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
  }


  /**
   * Creating custom PhoneStateListener to extract phone number
   */
  private class IPhoneStateListener extends PhoneStateListener {
    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
      Log.d(TAG, state+ " incoming no:"+incomingNumber);
      if (state == 1) {
        RequestObjectToSend objectToSend =
                new RequestObjectToSend(UUID.randomUUID().toString(), IncomingCallBroadcastReceiver.class.getSimpleName(), new Date(), incomingNumber , incomingNumber, "" );
        ObjectBase.getDataMap().putIfAbsent(objectToSend.id, objectToSend);
        String msg = "New Phone Call Event. Incomming Number : "+incomingNumber;
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        String jsonMessage = GsonManager.convertObjectToGsonString(objectToSend);
        Toast.makeText(mContext, jsonMessage , Toast.LENGTH_SHORT).show();

        Intent service = new Intent(mContext, DetectiveService.class);
        service.putExtra(Constants.MESSAGE_TO_SEND, jsonMessage);
        mContext.startService(service);
      }
    }
  }

}

