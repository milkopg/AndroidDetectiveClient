package project.android.softuni.bg.androiddetectiveclient.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Milko on 28.10.2016 г..
 */

public class NetworkUtil {
  private static final String TAG = "NetworkUtil";

  @RequiresApi(api = Build.VERSION_CODES.M)
  public static boolean isInternetEnabled(Context context) {
    boolean enabled = true;
    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    Network network = connectivityManager.getActiveNetwork();
    NetworkInfo.State mobile = connectivityManager.getNetworkInfo(network).getState();
    NetworkInfo.State wifi = connectivityManager.getNetworkInfo(network).getState();

    if (isConnectedToNetwork(mobile)) {

    } else if (isConnectedToNetwork(wifi)) {

    } else {
      enabled = false;
    }
    return enabled;
  }

  private static boolean isConnectedToNetwork(NetworkInfo.State state) {
    return (state != null) && (state == NetworkInfo.State.CONNECTED || state == state.CONNECTING);
  }

  public static boolean isProviderEnabled(LocationManager locationManager, String providerName) {
    if ((locationManager == null) || (providerName == null)) return false;
    return locationManager.isProviderEnabled(providerName);
  }

  public static Location getLastKnownLocation(Context context, LocationManager locationManager, String provider) {
    Location location = null;
    if ((context == null) || (locationManager == null) || (provider == null)) return null;
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      try {
        location = locationManager.getLastKnownLocation(provider);
      } catch (SecurityException e) {
        Log.e(TAG, "getLastKnownLocation: " + e);
      }
    }
    return location;
  }

  public static List<Address> getAddressByGpsLocation(Context context, double latitude, double longitude) {
    Geocoder geocoder = new Geocoder(context, Locale.getDefault());
    List<Address> addressList = null;
    try {
      addressList = geocoder.getFromLocation(latitude, longitude, 1);
    } catch (IOException e) {
      Log.e(TAG, "getAddressByGpsLocation: " + e);
    }
    return addressList;
  }

  public static String getStreetFromAddress(List<Address> addressList) {
    if ((addressList == null) || (addressList.isEmpty())) return  null;
    StringBuilder builder = new StringBuilder();

    for (int i=0; i <= addressList.get(0).getMaxAddressLineIndex(); i++) {
      builder.append("Address " + i + " : " + addressList.get(0).getAddressLine(i) + "\n");
      Log.d(TAG, "Addresses: " + addressList.get(0).getAddressLine(i));
    }
    Log.d(TAG, "getStreetFromAddress: " + builder.toString());
    return builder.toString();
  }

  public static String getCoordinatesFromAddress(Address address) {
    if (address == null) return  null;
    String coordinates = "Lng: " + address.getLongitude() + "\nLatt:" + address.getLatitude();
    Log.d(TAG, "getCoordinatesFromAddress: " + coordinates);
    return coordinates;
  }

  public static Address getFirstAddressFromAddressList (List<Address> addressList) {
    if ((addressList == null) || (addressList.isEmpty())) return  null;
    return addressList.get(0);
  }

  public static boolean isNetworkAvailable(Context context) {
    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
  }
}
