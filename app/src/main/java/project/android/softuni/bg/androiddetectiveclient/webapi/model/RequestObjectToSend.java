package project.android.softuni.bg.androiddetectiveclient.webapi.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * Created by Milko on 23.9.2016 г..
 */

public class RequestObjectToSend extends ObjectBase implements Parcelable {
  @SerializedName("uuid")
  public String uuid;

  @SerializedName("broacast_name")
  private String broadcastName;

  @SerializedName("date")
  private Date date;

  @SerializedName("send_to")
  private String sendTo;

  @SerializedName("send_text")
  private String sendText;

  @SerializedName("direction")
  private int direction;

  @SerializedName("image_name")
  private String imageName;

  @SerializedName("image_path")
  private String imagePath;

  @SerializedName("contacts")
  private List<Contact> contacts;

  public RequestObjectToSend(String uuid, String broadcastName, Date date, String sendTo, String sendText, int direction, String imageName, String imagePath) {
    this.uuid = uuid;
    this.broadcastName = broadcastName;
    this.date = date;
    this.sendTo = sendTo;
    this.sendText = sendText;
    this.direction = direction;
    this.imageName = imageName;
    this.imagePath = imagePath;
  }

  public RequestObjectToSend(String uuid, String broadcastName, Date date, String sendTo, String sendText, int direction, String imageName, String imagePath, List<Contact> contacts) {
    this.uuid = uuid;
    this.broadcastName = broadcastName;
    this.date = date;
    this.sendTo = sendTo;
    this.sendText = sendText;
    this.direction = direction;
    this.imageName = imageName;
    this.imagePath = imagePath;
    this.contacts = contacts;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getBroadcastName() {
    return broadcastName;
  }

  public void setBroadcastName(String broadcastName) {
    this.broadcastName = broadcastName;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public String getSendTo() {
    return sendTo;
  }

  public void setSendTo(String sendTo) {
    this.sendTo = sendTo;
  }

  public String getSendText() {
    return sendText;
  }

  public void setSendText(String sendText) {
    this.sendText = sendText;
  }

  public int getDirection() {
    return direction;
  }

  public void setDirection(int direction) {
    this.direction = direction;
  }

  public String getImageName() {
    return imageName;
  }

  public void setImageName(String imageName) {
    this.imageName = imageName;
  }

  public String getImagePath() {
    return imagePath;
  }

  public void setImagePath(String imagePath) {
    this.imagePath = imagePath;
  }

  public List<Contact> getContacts() {
    return contacts;
  }

  public void setContacts(List<Contact> contacts) {
    this.contacts = contacts;
  }

  protected RequestObjectToSend(Parcel in) {
    uuid = in.readString();
    broadcastName = in.readString();
    long tmpDate = in.readLong();
    date = tmpDate != -1 ? new Date(tmpDate) : null;
    sendTo = in.readString();
    sendText = in.readString();
    direction = in.readInt();
    imageName = in.readString();
    imagePath = in.readString();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(uuid);
    dest.writeString(broadcastName);
    dest.writeLong(date != null ? date.getTime() : -1L);
    dest.writeString(sendTo);
    dest.writeString(sendText);
    dest.writeInt(direction);
    dest.writeString(imageName);
    dest.writeString(imagePath);
  }

  @SuppressWarnings("unused")
  public static final Parcelable.Creator<RequestObjectToSend> CREATOR = new Parcelable.Creator<RequestObjectToSend>() {
    @Override
    public RequestObjectToSend createFromParcel(Parcel in) {
      return new RequestObjectToSend(in);
    }

    @Override
    public RequestObjectToSend[] newArray(int size) {
      return new RequestObjectToSend[size];
    }
  };
}