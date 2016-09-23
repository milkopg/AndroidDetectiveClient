package project.android.softuni.bg.androiddetectiveclient.webapi.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by Milko on 23.9.2016 г..
 */

public class RequestObjectToSend {
  @SerializedName("id")
  public String id;

  @SerializedName("broacast_name")
  public String broadcastName;

  @SerializedName("date")
  public Date date;

  @SerializedName("send_to")
  public String sendTo;

  @SerializedName("send_text")
  public String sendText;

  @SerializedName("notes")
  public String notes;

  public RequestObjectToSend(String id, String broadcastName, Date date, String sendTo, String sendText, String notes) {
    this.id = id;
    this.broadcastName = broadcastName;
    this.date = date;
    this.sendTo = sendTo;
    this.sendText = sendText;
    this.notes = notes;
  }
}
