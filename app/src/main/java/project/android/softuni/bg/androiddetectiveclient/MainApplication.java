package project.android.softuni.bg.androiddetectiveclient;

import android.app.Application;
import android.content.Context;
import android.location.Criteria;
import android.location.LocationManager;

/**
 * Created by Milko on 28.10.2016 г..
 */

public class MainApplication extends Application{
  @Override
  public void onCreate() {
    super.onCreate();
  }

  public LocationManager getLocationManager() {
    return (LocationManager) getSystemService(Context.LOCATION_SERVICE);
  }

  public Criteria getLocationManagerCriteria() {
    return new Criteria();
  }
}
