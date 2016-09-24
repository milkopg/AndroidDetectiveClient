package project.android.softuni.bg.androiddetectiveclient.broadcast.listener;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import project.android.softuni.bg.androiddetectiveclient.webapi.model.ObjectBase;

/**
 * Created by Milko on 22.9.2016 г..
 */

public interface IServiceCommunicationListener {
  void sendJsonData(String json);
}
