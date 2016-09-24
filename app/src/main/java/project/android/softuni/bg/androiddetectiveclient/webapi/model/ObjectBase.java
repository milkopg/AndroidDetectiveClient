package project.android.softuni.bg.androiddetectiveclient.webapi.model;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Milko on 24.9.2016 г..
 */

public class ObjectBase {
  private static ConcurrentHashMap<String, ObjectBase> dataMap = new ConcurrentHashMap<>();

  public static ConcurrentHashMap<String, ObjectBase> getDataMap() {
    return dataMap;
  }

  public static ObjectBase putIfAbsent(String key, ObjectBase value) {
    if (key != null) return null;
    return dataMap.putIfAbsent(key, value);
  }

  public static  ObjectBase getObject(String key) {
    if (key != null) return null;
    return dataMap.get(key) ;
  }

}
