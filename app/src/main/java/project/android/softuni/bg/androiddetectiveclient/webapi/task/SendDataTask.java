package project.android.softuni.bg.androiddetectiveclient.webapi.task;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import project.android.softuni.bg.androiddetectiveclient.util.Constants;
import project.android.softuni.bg.androiddetectiveclient.webapi.model.ObjectBase;
import project.android.softuni.bg.androiddetectiveclient.webapi.model.Response;

/**
 * Created by Milko on 22.9.2016 г..
 */

public class SendDataTask extends AsyncTask <URL, String, String>{
   private String data;
   private ConcurrentHashMap<String, ObjectBase> dataMap;
   private HttpURLConnection conn;
   private String requestId;
   private StringBuffer response;

  public SendDataTask(String data) {
    this.data = data;
  }

  public SendDataTask(ConcurrentHashMap<String, ObjectBase> dataMap) {
    this.dataMap = dataMap;
  }

  @Override
  protected String doInBackground(URL... voids) {

    String rawData = data;
    String encodedData = null;
    try {
      encodedData = URLEncoder.encode( rawData , "UTF-8");
      URL u = null;
      u = new URL(Constants.WEB_API_URL);
      conn = (HttpURLConnection) u.openConnection();
      conn.setRequestMethod( Constants.HTTP_REQUEST_METHOD_POST );
      conn.setDoInput(true);
      conn.setDoOutput(true);

      conn.setRequestProperty( Constants.HTTP_HEADER_CONTENT_TYPE, Constants.HTTP_HEADER_CONTENT_TYPE_JSON );
      conn.setRequestProperty( Constants.HTTP_HEADER_HOST, Constants.HTTP_HEADER_HOST_JSONBLOB);
      conn.setRequestProperty( Constants.HTTP_HEADER_ACCEPT, Constants.HTTP_HEADER_CONTENT_TYPE_JSON);
      conn.setRequestProperty( Constants.HTTP_HEADER_CONTENT_LENGTH, String.valueOf(rawData.length()));

      OutputStream os = conn.getOutputStream();


      os.write(rawData.getBytes());
      os.close();
      conn.connect();

      //get all headers
      Map<String, List<String>> map = conn.getHeaderFields();
      for (Map.Entry<String, List<String>> entry : map.entrySet()) {
        System.out.println("Key : " + entry.getKey() +
                " ,Value : " + entry.getValue());
      }
      conn.getResponseCode();
      requestId =  (map.containsKey(Constants.HTTP_HEADER_LOCATION)) ? map.get(Constants.HTTP_HEADER_LOCATION).get(0) : "";


//
//
//      Toast.makeText(this, response.query, Toast.LENGTH_SHORT).show();
//
//      List<Result> results = response.results;
//
//      for (Result result : results) {
//        Toast.makeText(this, result.fromUser, Toast.LENGTH_SHORT).show();
//      }
      Log.d(this.getClass().getSimpleName(), "Location: " + map.get("Location"));
      BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String inputLine;
      response = new StringBuffer();
      while ((inputLine = in.readLine()) != null) {
        response.append(inputLine);
      }
      Gson gson = new Gson();

      Response staff = gson.fromJson(response.toString(), Response.class);



      Log.i("INFO2", response.toString());

    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (ProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      Log.e("SendDataTask", e.getLocalizedMessage());
      e.printStackTrace();
    }

   return response.toString();
  }

  protected void onPostExecute(String response) {
    if (response == null) {
      response = "THERE WAS AN ERROR";
    }
    try {
      Log.i("INFO", URLDecoder.decode(response, "UTF-8"));
      //Log.i("INFO", conn.getHeaderField("Location"));



    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    //progressBar.setVisibility(View.GONE);

  }

  @Override
  protected void onProgressUpdate(String... values) {
    super.onProgressUpdate(values);
  }
}
