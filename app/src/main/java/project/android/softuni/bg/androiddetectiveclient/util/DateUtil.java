package project.android.softuni.bg.androiddetectiveclient.util;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Milko on 30.9.2016 г..
 */

public class DateUtil {

  private static final String TAG = DateUtil.class.getSimpleName();

  public static Date convertStringToGMTDate(String dateString) {
    DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy");
    Date inputDate = null;
    try {
      inputDate = dateFormat.parse(dateString);
    } catch (ParseException e) {
      Log.e(TAG, "convertStringToGMTDate: cannot convert String to date: " + e);
    }
    return inputDate;
  }

  public static String convertDateToShortString(Date date) {
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
    return dateFormat.format(date);
  }
}
