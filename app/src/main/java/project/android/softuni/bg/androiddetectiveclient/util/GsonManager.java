package project.android.softuni.bg.androiddetectiveclient.util;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.concurrent.ConcurrentHashMap;

import project.android.softuni.bg.androiddetectiveclient.webapi.model.ObjectBase;

/**
 * Created by Milko on 24.9.2016 г..
 */

public class GsonManager {
  private static final String TAG = GsonManager.class.getSimpleName();

  public static String convertObjectToGsonString(ObjectBase data) {
    Gson gson = new Gson();
    return gson.toJson(data);
  }

  public static String convertObjectMapToGsonString (ConcurrentHashMap<String, ObjectBase> objectBaseMap) {
    Gson gson = new Gson();
    return gson.toJson(objectBaseMap);
  }

  public static ObjectBase convertGsonStringToObject(String json) {
    Gson gson = new Gson();
    ObjectBase data = null;
    try {
      data = gson.fromJson(json, ObjectBase.class);
    } catch (JsonSyntaxException e) {
      Log.e(TAG, "convertGsonStringToObject: " + e.getLocalizedMessage());
    }
     return data;
  }

  public static ConcurrentHashMap<String, ObjectBase> convertGsonStringToObjectMap(String json) {
    Gson gson = new Gson();
    ConcurrentHashMap<String, ObjectBase>  objectMap = new ConcurrentHashMap<>();
    try {
      objectMap = gson.fromJson(json, objectMap.getClass());
    } catch (JsonSyntaxException e) {
      Log.e(TAG, "convertGsonStringToObjectMap: " + e.getLocalizedMessage());
    }
    return objectMap;
  }
}
