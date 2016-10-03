package project.android.softuni.bg.androiddetectiveclient.broadcast.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created by Milko on 2.10.2016 г..
 */

public class CustomResultReceiver extends ResultReceiver {
  private Receiver mReceiver;

  public CustomResultReceiver(Handler handler) {
    super(handler);
  }

  public void setReceiver(Receiver receiver) {
    mReceiver = receiver;
  }

  public interface Receiver {
    public void onReceiveResult(int resultCode, Bundle resultData);
  }

  @Override
  protected void onReceiveResult(int resultCode, Bundle resultData) {
    if (mReceiver != null) {
      mReceiver.onReceiveResult(resultCode, resultData);
    }
  }
}