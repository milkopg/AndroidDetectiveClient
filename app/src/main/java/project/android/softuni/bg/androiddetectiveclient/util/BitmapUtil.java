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

  /**
   * Compress bitmap to byte array
   * @param bitmap for compress
   * @param quality of compress. Integer range 0-100
   * @return compressed byte array
   */
  public static byte[] getBytes(Bitmap bitmap, int quality) {
    ByteArrayOutputStream stream=new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
    return stream.toByteArray();
  }

  /**
   * getImage from byte array
   * @param image
   * @return BitMap image
   */
  public static Bitmap getImage(byte[] image) {
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inSampleSize = 2;
    return BitmapFactory.decodeByteArray(image, 0, image.length, options);
  }

  /**
   * compress image byte array with quality
   * @param  image - byte array of raw image
   * @param quality of image. Range (0-100)
   * @return comressed image byte array
   */
  public static byte [] compressImage(byte[] image, int quality) {
    Bitmap bitmap = getImage(image);
    ByteArrayOutputStream bos = null;
    boolean success;
    try {
      bos = new ByteArrayOutputStream(image.length);
      success = bitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos);
    } catch (Exception e) {
      Log.e(TAG, "compressImage cannot open OutputStream");
      return  null;
    }
    Log.d(TAG, "compressImage: " + success);
    return success ? bos.toByteArray() : image;
  }
}
