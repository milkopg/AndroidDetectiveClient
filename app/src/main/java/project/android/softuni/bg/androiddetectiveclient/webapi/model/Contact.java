package project.android.softuni.bg.androiddetectiveclient.webapi.model;

/**
 * Created by Milko on 7.10.2016 г..
 */

public class Contact {
  private String id;
  private String name;
  private String phoneNumber;
  private String email;

  public Contact() {
  }

  public Contact(String email, String id, String name, String phoneNumber) {
    this.email = email;
    this.id = id;
    this.name = name;
    this.phoneNumber = phoneNumber;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
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
}
