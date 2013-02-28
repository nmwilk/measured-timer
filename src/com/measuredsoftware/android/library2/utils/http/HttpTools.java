package com.measuredsoftware.android.library2.utils.http;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
/**
 * A refactoring of the code provided by Moons on this page
 * http://www.anddev.org/doing_http_post_with_the_current_sdk-t5911.html
 *
 * Allows to send POST requests to a configurable server
 *
 * @author Moons, Wadael
 *
 */
public class HttpTools 
{
  public static final int HTTPTOOLS_ERROR_SUCCESS          = 0;
  public static final int HTTPTOOLS_ERROR_NO_ROUTE_TO_HOST = 1;
  public static final int HTTPTOOLS_ERROR_MALF_URL         = 2;
  public static final int HTTPTOOLS_CANCELLED              = 3;
  
  
  public static class BinaryPostResult {
    public int mResult;
    public HttpURLConnection mConn;
  }
  
  public static BinaryPostResult doBinaryPost(String loc, byte[] ba) {
    BinaryPostResult res = new BinaryPostResult();
    
    res.mConn = null;
    res.mResult = HTTPTOOLS_ERROR_SUCCESS;
    try {
      URL url = new URL(loc);
      res.mConn = (HttpURLConnection)url.openConnection();
      
      res.mConn.setDoInput(true);
      res.mConn.setDoOutput(true);
      res.mConn.setUseCaches(false);
      res.mConn.setRequestMethod("POST");
      res.mConn.setConnectTimeout(8000);
      res.mConn.setReadTimeout(8000);
      
      res.mConn.setRequestProperty("Connection", "Keep-Alive");
      res.mConn.setRequestProperty("Content-Type", "application/octet-stream");
  
      // write the bytes
      DataOutputStream ds = new DataOutputStream(res.mConn.getOutputStream());
      ds.write(ba);
      ds.flush();
      ds.close();
      
      res.mResult = HTTPTOOLS_ERROR_SUCCESS;
    } catch (MalformedURLException e) {
      res.mConn = null;
      res.mResult = HTTPTOOLS_ERROR_MALF_URL;
    } catch (SocketTimeoutException e) {
      res.mConn = null;
      res.mResult = HTTPTOOLS_ERROR_NO_ROUTE_TO_HOST;
    } catch (IOException e) {
      res.mConn = null;
      res.mResult = HTTPTOOLS_ERROR_NO_ROUTE_TO_HOST;
    }
    
    return res;
  }
  
  public static HttpResponse doPost(String url, Map<String, String> kvPairs) throws ClientProtocolException, IOException 
  {
    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(kvPairs.size());
    String k, v;
    Iterator<String> itKeys = kvPairs.keySet().iterator();
    while (itKeys.hasNext()) 
    {
      k = itKeys.next();
      v = kvPairs.get(k);
      nameValuePairs.add(new BasicNameValuePair(k, v));
    }
    
    return doPost(url, nameValuePairs);
  }
  
  public static HttpResponse doPost(String url, List<NameValuePair> nameValuePairs) throws ClientProtocolException, IOException 
  {
    HttpClient httpclient = new DefaultHttpClient();
    HttpPost httppost = new HttpPost(url);
    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    HttpResponse response;
    response = httpclient.execute(httppost);
    return response;
  }
  
  public static HttpClient setupPost(HttpPost httppost, List<NameValuePair> nameValuePairs) {
    HttpClient httpclient = null;
    try {
      httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
      httpclient = new DefaultHttpClient();      
    } catch (UnsupportedEncodingException e) {
    }
    return httpclient;
  }
  
  public static HttpResponse doPost(HttpClient httpclient, HttpPost httppost) {
    HttpResponse response = null;
    try {
      response = httpclient.execute(httppost);
    } catch (ClientProtocolException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return response;
  }  
  
  public static HttpResponse doGet(String url, Map<String, String> kvPairs) throws ClientProtocolException, IOException
  {
    HttpClient httpclient = new DefaultHttpClient();
    HttpGet httpget = new HttpGet(url);

    if (kvPairs != null && kvPairs.isEmpty() == false) 
    {
      String name, value;
      Iterator<String> itKeys = kvPairs.keySet().iterator();
      while (itKeys.hasNext()) 
      {
        name = itKeys.next();
        value = kvPairs.get(name);
        httpget.addHeader(name, value);//.setEntity(new UrlEncodedFormEntity(nameValuePairs));
      }
    }
    HttpResponse response;
    response = httpclient.execute(httpget);
    return response;
  }
} 
