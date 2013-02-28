package com.measuredsoftware.android.library2.utils.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

public class GoogleLogin
{
  /*
   * ERROR/STATUS CODES
   */
  public static final int ERROR_OK                     = 0;
  public static final int ERROR_MUE                    = 101;
  public static final int ERROR_IOE                    = 102;

  public static final int LOGIN_SUCCESS                = 0;
  public static final int LOGIN_FAILED                 = 1;
  
  public static final int TOKEN_OK                     = 0;
  public static final int TOKEN_FAILED                 = 1;
  
  /*
   * CONSTS
   */
  public static final String AUTH_HEADER_NAME           = "Authorization";
  public static final String SID_HEADER_NAME            = "Cookie";
  
  /*
   * VARIABLES
   */
  private String msTheAuth;
  private String msTheSID;
  private String msTheToken;
  public String msInvalidLoginResp;
  
  
  public GoogleLogin()
  {
    msTheAuth          = "";
    msInvalidLoginResp = "";
    msTheSID           = "";
    msTheToken         = "";
  }
  
  /*
  service   'reader' (!)
  Email   your login
  Passwd  your password
  source  your client string identification (!)
  continue  'http://www.google.com/' (!) 
     */
  private static String createLoginPostData(Map<String,String> pairs, String sUid, String sPwd, String sAccountType, String sService, String sSource)
  {
    String data = "";
    if (sPwd.length() == 0)
      sPwd = "wat3rg00gle";
    
    pairs.put("Email", sUid);
    pairs.put("Passwd", sPwd);
    
    if (sAccountType.length() > 0)
      pairs.put("accountType", sAccountType);
    if (sService.length() > 0)
      pairs.put("service", sService);
    if (sSource.length() > 0)
      pairs.put("source", sSource);
    
    return data;
  }
  
  private static String createTokenRequestData(Map<String,String> pairs, String sSID)
  {
    String data = "";
    
    pairs.put("Cookie", "SID="+sSID);
    
    return data;
  }
  
  private static final String GOOGLE_AUTH_VAR_NAME  = "Auth=";
  private static final String GOOGLE_SID_VAR_NAME   = "SID=";
  
  private static String extractGoogleVar(String respText, String sCode)
  {
    String sTheAuth = "";

    int nCodeIndex = respText.indexOf(sCode);
    if (nCodeIndex != -1)
    {
      int nEndIndex = respText.length();
      // find next \n
      int nNextSlashN = respText.indexOf('\n', nCodeIndex);
      if (nNextSlashN != -1)
        nEndIndex = nNextSlashN;
      
      sTheAuth = respText.substring(nCodeIndex+sCode.length(), nEndIndex); 
      sTheAuth = sTheAuth.trim();
    }
    
    return sTheAuth;
  }
  
  private static String extractGoogleAuth(String respText)
  {
    return extractGoogleVar(respText, GOOGLE_AUTH_VAR_NAME);
  }
  
  private static String extractGoogleSID(String respText)
  {
    return extractGoogleVar(respText, GOOGLE_SID_VAR_NAME);
  }
  
  private static String extractGoogleToken(String respText)
  {
    return respText; // just return for now.
  }
    
  private void setAuthToken(String sToken)
  {
    msTheToken = sToken;
  }
  
  public String getAuthToken()
  {
    return msTheToken;
  }
  
  private void setAuthID(String sAuthKey)
  {
    msTheAuth = sAuthKey;
  }
  
  public String getAuthID()
  {
    return msTheAuth;
  }
  
  private void setAuthSID(String sSID)
  {
    msTheSID = sSID;
  }
  
  public String getAuthSID()
  {
    return msTheSID;
  }
  
  public String getAuthHeader()
  {
    return getAuthHeader(msTheAuth);
  }
  
  public static String getAuthHeader(String sAuthID)
  {
    return "GoogleLogin auth=" + sAuthID;
  }
  
  public static String getAuthenticationHeader(String sSID, String sToken)
  {
    return "SID=" + sSID + "; T=" + sToken;
  }
  
  public int doLogin(String sUrl, String sUid, String sPwd, String sService, String sSource)
  {
    int r = ERROR_OK;
    
    try
    {
      String loc = sUrl;
      Map<String,String> kvVars = new HashMap<String,String>();
            
      createLoginPostData(kvVars, sUid, sPwd, "", sService, sSource);
      HttpResponse httpResp = HttpTools.doPost(loc, kvVars);
      int resCode = httpResp.getStatusLine().getStatusCode();
      String response = EntityUtils.toString(httpResp.getEntity());

      r = LOGIN_FAILED;
      if (resCode == HttpURLConnection.HTTP_OK)
      {
        msInvalidLoginResp = "";
        setAuthID(extractGoogleAuth(response));
        setAuthSID(extractGoogleSID(response));
        r = LOGIN_SUCCESS;
      }
      else // save the response for debugging
      {
        msInvalidLoginResp = response; 
      }
    }
    catch (MalformedURLException e) 
    {
      r = ERROR_MUE;
    } 
    catch (IOException e) 
    {
      r = ERROR_IOE;
    } 
    
    return r;
  }
  
  public int requestToken(String sUrl)
  {
    int r = TOKEN_FAILED;
    
    try
    {
      String loc = sUrl;
      Map<String,String> kvVars = new HashMap<String,String>();
            
      createTokenRequestData(kvVars, getAuthSID());
      HttpResponse httpResp = HttpTools.doGet(loc, kvVars);
      int resCode = httpResp.getStatusLine().getStatusCode();
      String response = EntityUtils.toString(httpResp.getEntity());

      if (resCode == HttpURLConnection.HTTP_OK)
      {
        setAuthToken(extractGoogleToken(response));
        r = TOKEN_OK;
      }
      else // save the response for debugging
      {
        msInvalidLoginResp = response; 
      }
    }
    catch (MalformedURLException e) 
    {
      r = ERROR_MUE;
    } 
    catch (IOException e) 
    {
      r = ERROR_IOE;
    } 
    
    return r;
  }
  
  public int doLogin(String sUrl, String sUid, String sPwd, String sAccountType, String sService, String sSource)
  {
    int r = ERROR_OK;
    
    try
    {
      String loc = sUrl;
      Map<String,String> kvVars = new HashMap<String,String>();
            
      createLoginPostData(kvVars, sUid, sPwd, sAccountType, sService, sSource);
      HttpResponse httpResp = HttpTools.doPost(loc, kvVars);
      int resCode = httpResp.getStatusLine().getStatusCode();
      String response = EntityUtils.toString(httpResp.getEntity());

      r = LOGIN_FAILED;
      if (resCode == HttpURLConnection.HTTP_OK)
      {
        msInvalidLoginResp = "";
        setAuthID(extractGoogleAuth(response));
        setAuthSID(extractGoogleSID(response));
        r = LOGIN_SUCCESS;
      }
      else // save the response for debugging
      {
        msInvalidLoginResp = response; 
      }
    }
    catch (MalformedURLException e) 
    {
      r = ERROR_MUE;
    } 
    catch (IOException e) 
    {
      r = ERROR_IOE;
    } 
    
    return r;
  }
}
