package project.android.softuni.bg.androiddetectiveclient.webapi.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Milko on 23.9.2016 г..
 */

public class RequestObjectToSend extends  ObjectBase implements Parcelable {
  @SerializedName("uuid")
  public String uuid;

  @SerializedName("broacast_name")
  public String broadcastName;

  @SerializedName("date")
  public String date;

  @SerializedName("send_to")
  public String sendTo;

  @SerializedName("send_text")
  public String sendText;

  @SerializedName("direction")
  public int direction;

 public RequestObjectToSend(String uuid, String broadcastName, String date, String sendTo, String sendText, int direction) {
    this.uuid = uuid;
    this.broadcastName = broadcastName;
    this.date = date;
    this.sendTo = sendTo;
    this.sendText = sendText;
    this.direction = direction;
  }

  protected RequestObjectToSend(Parcel in) {
    uuid = in.readString();
    broadcastName = in.readString();
    date = in.readString();
    sendTo = in.readString();
    sendText = in.readString();
    direction = in.readInt();
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(uuid);
    dest.writeString(broadcastName);
    dest.writeString(date);
    dest.writeString(sendTo);
    dest.writeString(sendText);
    dest.writeInt(direction);
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
