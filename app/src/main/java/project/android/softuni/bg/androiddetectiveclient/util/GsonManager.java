package project.android.softuni.bg.androiddetectiveclient.util;

import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;

import project.android.softuni.bg.androiddetectiveclient.webapi.model.ObjectBase;
import project.android.softuni.bg.androiddetectiveclient.webapi.model.RequestObjectToSend;

/**
 * Created by Milko on 24.9.2016 г..
 */

public class GsonManager {
  private static final String TAG = GsonManager.class.getSimpleName();

  public static String convertObjectToGsonString(ObjectBase data) {
    Gson gson = new GsonBuilder().setDateFormat(Constants.DATE_FORMAT_SHORT_DATE_TIME).create();
    return gson.toJson(data);
  }

  /**
   * ConvertGsonString to Object.
   * @param json
   * @return serialized GsonObject
   */
  public static RequestObjectToSend convertGsonStringToObject(String json) {
    Gson gson = new GsonBuilder().setDateFormat(Constants.DATE_FORMAT_SHORT_DATE_TIME).create();
    RequestObjectToSend data = null;
    try {
      data = gson.fromJson(json, RequestObjectToSend.class);
    } catch (JsonSyntaxException e) {
      Log.e(TAG, "convertGsonStringToObject: " + e);
    }
    return data;
  }

  /**
   *  create customGsonAdapter for serializing Images like Base64 String. This is required for Sending data in JsonBlob
   */
  public static final Gson customGson = new GsonBuilder()
          .registerTypeAdapter(Byte.class, new JsonDeserializer<Byte>() {
            @Override
            public Byte deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
              return json.getAsByte();
            }
          })
          .registerTypeHierarchyAdapter(byte[].class,
                  new ByteArrayToBase64TypeAdapter()).create();

  /**
   * Custom ByteArrayToBase64 Adapter, with serializer and deserializer methods
   */
  public static class ByteArrayToBase64TypeAdapter implements JsonSerializer<byte[]>, JsonDeserializer<byte[]> {
    /**
     * Deseralize Json to byteArray
     * @param json JsonElement in JsonFormat
     * @param typeOfT
     * @param context - JsonDeserializationContext
     * @return byte array of deserialized JsonElement
     * @throws JsonParseException
     */
    public byte[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      return Base64.decode(json.getAsString(), Base64.NO_WRAP);
    }

    /**
     * Serialized byteArray Json to JsonElement
     * @param src - byteArray of deserialized Json
     * @param typeOfSrc
     * @param context - JsonSerializationContext
     * @return serialized JsonElement
     */
    public JsonElement serialize(byte[] src, Type typeOfSrc, JsonSerializationContext context) {
      return new JsonPrimitive(Base64.encodeToString(src, Base64.NO_WRAP));
    }
  }
}
