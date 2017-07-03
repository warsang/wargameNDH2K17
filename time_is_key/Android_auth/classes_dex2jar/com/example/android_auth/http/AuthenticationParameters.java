package com.example.android_auth.http;

import java.io.File;

public class AuthenticationParameters
{
  private String caCertificate = null;
  private File clientCertificate = null;
  private String clientCertificatePassword = null;
  
  public AuthenticationParameters() {}
  
  public String getCaCertificate()
  {
    return this.caCertificate;
  }
  
  public File getClientCertificate()
  {
    return this.clientCertificate;
  }
  
  public String getClientCertificatePassword()
  {
    return this.clientCertificatePassword;
  }
  
  public void setCaCertificate(String paramString)
  {
    this.caCertificate = paramString;
  }
  
  public void setClientCertificate(File paramFile)
  {
    this.clientCertificate = paramFile;
  }
  
  public void setClientCertificatePassword(String paramString)
  {
    this.clientCertificatePassword = paramString;
  }
}
