package project.android.softuni.bg.androiddetectiveclient.webapi.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Milko on 23.9.2016 г..
 */

public class Response extends  ObjectBase implements Parcelable {
  @SerializedName("location")
  public String location;

  public RequestObjectToSend body;

  protected Response(Parcel in) {
    location = in.readString();
    body = (RequestObjectToSend) in.readValue(RequestObjectToSend.class.getClassLoader());
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(location);
    dest.writeValue(body);
  }

  @SuppressWarnings("unused")
  public static final Parcelable.Creator<Response> CREATOR = new Parcelable.Creator<Response>() {
    @Override
    public Response createFromParcel(Parcel in) {
      return new Response(in);
    }

    @Override
    public Response[] newArray(int size) {
      return new Response[size];
    }
  };
}