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
import java.util.List;

import project.android.softuni.bg.androiddetectiveclient.webapi.model.Contact;

/**
 * Created by Milko on 7.10.2016 г..
 */

public class CustomContentObserver extends ContentObserver {
  private static final String TAG = "CustomContentObserver";
  private Context mContext;
  private int mContactCount;
  private List<Contact> mContactList;

  public CustomContentObserver(Handler handler, Context context) {
    super(handler);
    this.mContext = context;
  }

  public CustomContentObserver(Handler handler) {
    super(handler);
  }

  @Override
  public void onChange(boolean selfChange) {
    final int currentCount = getContactCount();
    mContactList = getContactList();
    if (currentCount < mContactCount) {
      // DELETE HAPPEN.
    } else if (currentCount == mContactCount) {
      // UPDATE HAPPEN.
    } else {
      // INSERT HAPPEN.
    }
    mContactCount = currentCount;
    super.onChange(selfChange);
  }

  @Override
  public void onChange(boolean selfChange, Uri uri) {
    super.onChange(selfChange, uri);
  }

  @Override
  public boolean deliverSelfNotifications() {
    return true;
  }
  private int getContactCount() {
    Cursor cursor = null;
    try {
      cursor = mContext.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
      if (cursor != null) {
        return cursor.getCount();
      } else {
        return 0;
      }
    } catch (Exception ignore) {
    } finally {
      if (cursor != null) {
        cursor.close();
      }
    }
    return 0;
  }

  public List<Contact> getContactList() {
    List<Contact> contactList = new ArrayList<>();

    String phoneNumber = null;
    String email = null;

    Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
    String _ID = ContactsContract.Contacts._ID;
    String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
    String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

    Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
    String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
    String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

    Uri EmailCONTENT_URI =  ContactsContract.CommonDataKinds.Email.CONTENT_URI;
    String EmailCONTACT_ID = ContactsContract.CommonDataKinds.Email.CONTACT_ID;
    String DATA = ContactsContract.CommonDataKinds.Email.DATA;

    StringBuffer output = new StringBuffer();

    ContentResolver contentResolver = mContext.getContentResolver();

    Cursor cursor = contentResolver.query(CONTENT_URI, null,null, null, null);

    // Loop for every contact in the phone
    if (cursor.getCount() > 0) {

      while (cursor.moveToNext()) {
        Contact contact = new Contact();

        String contact_id = cursor.getString(cursor.getColumnIndex( _ID ));
        contact.setId(contact_id);

        String name = cursor.getString(cursor.getColumnIndex( DISPLAY_NAME ));
        contact.setName(name);

        int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex( HAS_PHONE_NUMBER )));

        if (hasPhoneNumber > 0) {

          output.append("\n First Name:" + name);
          contact.setName(name);

          // Query and loop for every phone number of the contact
          Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] { contact_id }, null);

          while (phoneCursor.moveToNext()) {
            phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
            output.append("\n Phone number:" + phoneNumber);
            contact.setPhoneNumber(phoneNumber);

          }

          phoneCursor.close();

          // Query and loop for every email of the contact
          Cursor emailCursor = contentResolver.query(EmailCONTENT_URI,	null, EmailCONTACT_ID+ " = ?", new String[] { contact_id }, null);

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

      Log.d(TAG, output.toString());
    }
    return contactList;
  }
}
