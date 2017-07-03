package com.example.android_auth.http;

import com.example.android_auth.util.IOUtil;
import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

public class Api
{
  private int lastResponseCode;
  private SSLContext sslContext;
  
  public Api(AuthenticationParameters paramAuthenticationParameters)
    throws Exception
  {
    File localFile = paramAuthenticationParameters.getClientCertificate();
    this.sslContext = SSLContextFactory.getInstance().makeContext(localFile, paramAuthenticationParameters.getClientCertificatePassword(), paramAuthenticationParameters.getCaCertificate());
    CookieHandler.setDefault(new CookieManager());
  }
  
  public String doGet(String paramString)
    throws Exception
  {
    localHttpURLConnection = null;
    try
    {
      localHttpURLConnection = (HttpURLConnection)new URL(paramString).openConnection();
      if ((localHttpURLConnection instanceof HttpsURLConnection)) {
        ((HttpsURLConnection)localHttpURLConnection).setSSLSocketFactory(this.sslContext.getSocketFactory());
      }
      localHttpURLConnection.setRequestMethod("GET");
      localHttpURLConnection.setConnectTimeout(1500);
      localHttpURLConnection.setReadTimeout(1500);
      this.lastResponseCode = localHttpURLConnection.getResponseCode();
      String str2 = IOUtil.readFully(localHttpURLConnection.getInputStream());
      localObject2 = str2;
    }
    catch (Exception localException)
    {
      String str1 = localException.toString();
      Object localObject2 = str1;
      return localObject2;
    }
    finally
    {
      if (localHttpURLConnection == null) {
        break label123;
      }
      localHttpURLConnection.disconnect();
    }
    return localObject2;
  }
  
  public String doPost(String paramString1, String paramString2)
    throws Exception
  {
    localHttpURLConnection = null;
    try
    {
      localHttpURLConnection = (HttpURLConnection)new URL(paramString1).openConnection();
      if ((localHttpURLConnection instanceof HttpsURLConnection)) {
        ((HttpsURLConnection)localHttpURLConnection).setSSLSocketFactory(this.sslContext.getSocketFactory());
      }
      localHttpURLConnection.setRequestMethod("POST");
      localHttpURLConnection.setConnectTimeout(1500);
      localHttpURLConnection.setReadTimeout(1500);
      localHttpURLConnection.setDoInput(true);
      localHttpURLConnection.setDoOutput(true);
      OutputStream localOutputStream = localHttpURLConnection.getOutputStream();
      BufferedWriter localBufferedWriter = new BufferedWriter(new OutputStreamWriter(localOutputStream, "UTF-8"));
      localBufferedWriter.write(paramString2);
      localBufferedWriter.flush();
      localBufferedWriter.close();
      localOutputStream.close();
      localHttpURLConnection.connect();
      this.lastResponseCode = localHttpURLConnection.getResponseCode();
      String str2 = IOUtil.readFully(localHttpURLConnection.getInputStream());
      localObject2 = str2;
    }
    catch (Exception localException)
    {
      String str1 = localException.toString();
      Object localObject2 = str1;
      return localObject2;
    }
    finally
    {
      if (localHttpURLConnection == null) {
        break label185;
      }
      localHttpURLConnection.disconnect();
    }
    return localObject2;
  }
  
  public int getLastResponseCode()
  {
    return this.lastResponseCode;
  }
}
