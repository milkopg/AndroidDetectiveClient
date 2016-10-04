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
    DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_SHORT_DATE_TIME);

    return dateFormat.format(date);
  }

  public static Date convertDateLongToShortDate(Date date) {
    DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_SHORT_DATE_TIME);
    String dateString = convertDateToShortString(date);
    try {
      return dateFormat.parse(dateString);
    } catch (ParseException e) {
      Log.e(TAG, "convertDateLongToShortDate: "  + e);
    }
    return date;
  }
}
