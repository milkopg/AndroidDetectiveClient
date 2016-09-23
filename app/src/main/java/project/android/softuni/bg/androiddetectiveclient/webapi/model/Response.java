package project.android.softuni.bg.androiddetectiveclient.webapi.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Milko on 23.9.2016 г..
 */

public class Response {
  @SerializedName("location")
  public String location;

  public RequestObjectToSend body;
}
