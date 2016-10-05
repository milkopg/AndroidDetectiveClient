package project.android.softuni.bg.androiddetectiveclient.webapi.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by Milko on 23.9.2016 г..
 */

public class RequestObjectToSend extends  ObjectBase implements Parcelable {
  @SerializedName("uuid")
  public String uuid;

  @SerializedName("broacast_name")
  public String broadcastName;

  @SerializedName("date")
  public Date date;

  @SerializedName("send_to")
  public String sendTo;

  @SerializedName("send_text")
  public String sendText;

  @SerializedName("direction")
  public int direction;


  @SerializedName("image_name")
  private String imageName;

  @SerializedName("image_path")
  private String imagePath;

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