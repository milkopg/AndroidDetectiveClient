package project.android.softuni.bg.androiddetectiveclient.webapi.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Milko on 7.10.2016 г..
 */

public class Contact {
  @SerializedName("uuid")
  private String contactId;

  @SerializedName("name")
  private String name;

  @SerializedName("phoneNumber")
  private String phoneNumber;

  @SerializedName("email")
  private String email;

  @SerializedName("request")
  private RequestObjectToSend request;

  public Contact() {
  }

  public Contact(String email, String contactId, String name, String phoneNumber, RequestObjectToSend request) {
    this.email = email;
    this.contactId = contactId;
    this.name = name;
    this.phoneNumber = phoneNumber;
    this.request = request;
  }

  public String getContactId() {
    return contactId;
  }

  public void setContactId(String contactId) {
    this.contactId = contactId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public RequestObjectToSend getRequest() {
    return request;
  }

  public void setRequest(RequestObjectToSend request) {
    this.request = request;
  }
}
