package project.android.softuni.bg.androiddetectiveclient.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;

/**
 * Created by Milko on 5.10.2016 г..
 */

public class BitmapUtil {
  private static final String TAG = BitmapUtil.class.getSimpleName();

  public static byte[] getBytes(Bitmap bitmap) {
    ByteArrayOutputStream stream=new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
    return stream.toByteArray();
  }

  public static Bitmap getImage(byte[] image) {
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inSampleSize = 2;
    return BitmapFactory.decodeByteArray(image, 0, image.length, options);
  }

  public static byte [] compressImage(byte [] image) {
    Bitmap bitmap = getImage(image);
    ByteArrayOutputStream bos = null;
    boolean success;
    try {
      bos = new ByteArrayOutputStream(image.length);
      success = bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
    } catch (Exception e) {
      Log.e(TAG, "compressImage cannot open OutputStream");
      return  null;
    }
    Log.d(TAG, "compressImage: " + success);
    return success ? bos.toByteArray() : image;
  }

}
