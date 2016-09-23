package project.android.softuni.bg.androiddetectiveclient.broadcast.listener;

/**
 * Created by Milko on 22.9.2016 г..
 */

public interface IServiceCommunicationListener {
  void sendJsonData(String json);
  void sendBinaryData(Byte[] binaryData);
}
