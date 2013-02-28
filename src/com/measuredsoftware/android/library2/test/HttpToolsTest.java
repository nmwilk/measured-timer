package com.measuredsoftware.android.library2.test;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.measuredsoftware.android.library2.utils.http.HttpTools;
import com.measuredsoftware.android.library2.utils.http.HttpTools.BinaryPostResult;

public class HttpToolsTest extends TestCase {

  public void testPost1() {
    List<NameValuePair> pairs = new ArrayList<NameValuePair>(1);
    pairs.add(new BasicNameValuePair("field1","!A Variable!"));
    try {
      HttpResponse httpResp = HttpTools.doPost("http://192.168.0.136/phppost.php", pairs);
      int resCode = httpResp.getStatusLine().getStatusCode();
      assertEquals(resCode, 200);
      String content = EntityUtils.toString(httpResp.getEntity());
      assertEquals(content,"!A Variable!\n");
    } catch (ClientProtocolException e) {
      e.printStackTrace();
      assertNotNull(null);
    } catch (IOException e) {
      e.printStackTrace();
      assertNotNull(null);
    }
  }
  
  public void testPost2() {
    List<NameValuePair> pairs = new ArrayList<NameValuePair>(1);
    pairs.add(new BasicNameValuePair("field1","!A Variable!"));
    try {
      HttpResponse httpResp = HttpTools.doPost("http://192.168.0.136/phppost.php", pairs);
      int resCode = httpResp.getStatusLine().getStatusCode();
      assertEquals(resCode, 200);
      String content = EntityUtils.toString(httpResp.getEntity());
      assertEquals(content,"!A Variable!\n");
    } catch (ClientProtocolException e) {
      e.printStackTrace();
      assertNotNull(null);
    } catch (IOException e) {
      e.printStackTrace();
      assertNotNull(null);
    }
  }
  
  /**
   * doesn't work. the getContent call is not interrupted, and just causes a null pointer exception.
   */
  public void testCancellation() {
    BinaryPostResult res = new BinaryPostResult();
    
    byte[] ba = new byte[] { 0x30, 0x31, 0x32, 0x33, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3A, 0x3B, 0x3C, 0x3D, 0x3E, 0x3f };
    
    res.mConn = null;
    res.mResult = HttpTools.HTTPTOOLS_ERROR_SUCCESS;
    try {
      URL url = new URL("http://www.measuredsoftware.co.uk/testserver/post.php");
      res.mConn = (HttpURLConnection)url.openConnection();
      
      res.mConn.setDoInput(true);
      res.mConn.setDoOutput(true);
      res.mConn.setUseCaches(false);
      res.mConn.setRequestMethod("POST");
      res.mConn.setConnectTimeout(8000);
      res.mConn.setReadTimeout(8000);
      
      res.mConn.setRequestProperty("Connection", "Keep-Alive");
      res.mConn.setRequestProperty("Content-Type", "application/octet-stream");

      new CancelThread(res.mConn).start();
      
      // write the bytes
      DataOutputStream ds = new DataOutputStream(res.mConn.getOutputStream());
      ds.write(ba);
      
      ds.flush();
      ds.close();
      
      Object o = res.mConn.getContent();
      
      res.mResult = HttpTools.HTTPTOOLS_ERROR_SUCCESS;
    } catch (MalformedURLException e) {
      res.mConn = null;
      res.mResult = HttpTools.HTTPTOOLS_ERROR_MALF_URL;
    } catch (SocketTimeoutException e) {
      res.mConn = null;
      res.mResult = HttpTools.HTTPTOOLS_ERROR_NO_ROUTE_TO_HOST;
    } catch (IOException e) {
      res.mConn = null;
      res.mResult = HttpTools.HTTPTOOLS_ERROR_NO_ROUTE_TO_HOST;
    }

    assertEquals(HttpTools.HTTPTOOLS_CANCELLED, res.mResult);
  }
  
  private class CancelThread  extends Thread {
    private HttpURLConnection mConn;
    public CancelThread(HttpURLConnection conn) {
      mConn = conn;
    }
    
    @Override
    public void run() {
      mConn.disconnect();
    }
  }  
  /**
   * 
   */
  
  
  /**
   * doesn't work either. the doPost request isn't cancelled, an illegalstationexception is just thrown
   */  
  public void testHttpPostCancel() {
    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
    nameValuePairs.add(new BasicNameValuePair("testvar","testvalue"));
    HttpPost httppost = new HttpPost("http://www.measuredsoftware.co.uk/testserver/post.php");
    HttpClient httpclient = HttpTools.setupPost(httppost, nameValuePairs);
    
    new CancelHttpClientThread(httpclient).start();
    HttpResponse res = HttpTools.doPost(httpclient, httppost);
    
    assertEquals(1, 0);
    
  }
 
  private class CancelHttpClientThread  extends Thread {
    private HttpClient mHttpclient;
    public CancelHttpClientThread(HttpClient httpclient) {
      mHttpclient = httpclient;
    }
    
    @Override
    public void run() {
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e) {
      }
      mHttpclient.getConnectionManager().shutdown();
    }
  }  
  
}
