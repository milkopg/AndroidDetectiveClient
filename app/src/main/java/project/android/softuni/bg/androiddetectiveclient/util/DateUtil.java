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

  /**
   * Convert date to short format yyyy-MM-dd HH:mm and return it to String. Necessary for Gson Object parsing
   * @param date - current date
   * @return DateString in shortFormat
   */
  public static String convertDateToShortString(Date date) {
    DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_SHORT_DATE_TIME);

    return dateFormat.format(date);
  }

  /**
   * Convert Long Date to Short Date format yyyy-MM-dd HH:mm
   * @param date Long Date
   * @return Short Date format yyyy-MM-dd HH:mm
   */
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
