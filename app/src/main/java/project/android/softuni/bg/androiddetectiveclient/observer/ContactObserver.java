package project.android.softuni.bg.androiddetectiveclient.observer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import project.android.softuni.bg.androiddetectiveclient.R;
import project.android.softuni.bg.androiddetectiveclient.service.DetectiveIntentService;
import project.android.softuni.bg.androiddetectiveclient.util.Constants;
import project.android.softuni.bg.androiddetectiveclient.util.DateUtil;
import project.android.softuni.bg.androiddetectiveclient.util.GsonManager;
import project.android.softuni.bg.androiddetectiveclient.util.ServiceManager;
import project.android.softuni.bg.androiddetectiveclient.webapi.model.Contact;
import project.android.softuni.bg.androiddetectiveclient.webapi.model.RequestObjectToSend;

/**
 * Created by Milko on 7.10.2016 г..
 */

public class ContactObserver extends ContentObserver {
  private static final String TAG = "ContactObserver";
  private Context mContext;
  private List<Contact> mContactList;
  // this map is for avoiding sending multiple times which overflow RabbitMQ connections
  public static ConcurrentHashMap<String, Integer> objectsMap = new ConcurrentHashMap<>();


  public ContactObserver(Handler handler, Context context) {
    super(handler);
    this.mContext = context;
  }

  /**
   * onChange Receiving Contact Changing event from ContentObserver
   * to avoid multiple sending data. Once contact is changed is being sent more than 10 events, so this
   * prefill RabbitMQ connection pool and stop whole process. We keep last number of contact in ConcurrentHashMap
   * and data is sent only if contact number is changed
   * @param selfChange
   */
  @Override
  public void onChange(boolean selfChange) {
    Log.d(TAG, "on change called");

    //
    //
      mContactList = getContactList();
    if (objectsMap.isEmpty()) {
      objectsMap.put(Constants.RECEIVER_CONTACTS, mContactList.size());
      sendContactObserverData();
    }

    if(!objectsMap.isEmpty() && (mContactList.size() != objectsMap.get(Constants.RECEIVER_CONTACTS).intValue())){
      sendContactObserverData();
    }
    super.onChange(selfChange);
  }

  /**
   * Create RequestObject , convert it to Json String and send it to DetectiveIntentService
   */
  private void sendContactObserverData() {
    RequestObjectToSend data = new RequestObjectToSend(UUID.randomUUID().toString(), this.getClass().getSimpleName(), DateUtil.convertDateLongToShortDate(new Date()), "123", mContext.getString(R.string.contact_list_changed), 0, "", "", mContactList);
    String jsonMessage = GsonManager.convertObjectToGsonString(data);
    objectsMap.put(Constants.RECEIVER_CONTACTS, mContactList.size());
    ServiceManager.startService(mContext, jsonMessage);
    Log.d(TAG, "sendContactObserverData: " + jsonMessage);
  }

  /**
   * Get current contact phone list from ContactContentURI.
   * This method create and extract separate cursor for FirstName, Phone number and email and return List of contacts
   * @return List<Contact> off all contacts from phone</>
   */
  public List<Contact> getContactList() {
    List<Contact> contactList = new ArrayList<>();

    String phoneNumber;
    String email;

    Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
    String _ID = ContactsContract.Contacts._ID;
    String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
    String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

    Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
    String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
    String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

    Uri EmailCONTENT_URI = ContactsContract.CommonDataKinds.Email.CONTENT_URI;
    String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
    String DATA = ContactsContract.CommonDataKinds.Email.DATA;

    StringBuffer output = new StringBuffer();

    ContentResolver contentResolver = mContext.getContentResolver();

    Cursor cursor = contentResolver.query(CONTENT_URI, null, null, null, null);

    // Loop for every contact in the phone
    if (cursor.getCount() > 0) {

      while (cursor.moveToNext()) {
        Contact contact = new Contact();

        String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
        contact.setContactId(Integer.parseInt(contact_id));

        String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
        contact.setName(name);

        int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));

        if (hasPhoneNumber > 0) {
          output.append("\n First Name:" + name);
          contact.setName(name);

          // Query and loop for every phone number of the contact
          Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[]{contact_id}, null);

          while (phoneCursor.moveToNext()) {
            phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
            output.append("\n Phone number:" + phoneNumber);
            contact.setPhoneNumber(phoneNumber);
          }

          phoneCursor.close();

          // Query and loop for every email of the contact
          Cursor emailCursor = contentResolver.query(EmailCONTENT_URI, null, EmailCONTACT_ID + " = ?", new String[]{contact_id}, null);

          while (emailCursor.moveToNext()) {
            email = emailCursor.getString(emailCursor.getColumnIndex(DATA));
            output.append("\nEmail:" + email);
            contact.setEmail(email);
          }
          emailCursor.close();
        }
        output.append("\n");
        contactList.add(contact);
      }

      if (cursor != null) {
        cursor.close();
      }
    }
    return contactList;
  }

  @Override
  public boolean deliverSelfNotifications() {
    return false;
  }
}
