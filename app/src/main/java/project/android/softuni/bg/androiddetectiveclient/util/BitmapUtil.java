package project.android.softuni.bg.androiddetectiveclient.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

/**
 * Created by Milko on 5.10.2016 г..
 */

public class BitmapUtil {
  public static byte[] getBytes(Bitmap bitmap) {
    ByteArrayOutputStream stream=new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.PNG,100, stream);
    return stream.toByteArray();
  }

  public static Bitmap getImage(byte[] image) {
    return BitmapFactory.decodeByteArray(image, 0, image.length);
  }

}
