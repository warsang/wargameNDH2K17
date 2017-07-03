package com.example.android_auth.http;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class CustomTrustManager
  implements X509TrustManager
{
  private final X509TrustManager originalX509TrustManager;
  private final KeyStore trustStore;
  
  public CustomTrustManager(KeyStore paramKeyStore)
    throws NoSuchAlgorithmException, KeyStoreException
  {
    this.trustStore = paramKeyStore;
    TrustManagerFactory localTrustManagerFactory = TrustManagerFactory.getInstance("X509");
    localTrustManagerFactory.init((KeyStore)null);
    this.originalX509TrustManager = ((X509TrustManager)localTrustManagerFactory.getTrustManagers()[0]);
  }
  
  private X509Certificate findRootCert(List<X509Certificate> paramList)
  {
    Iterator localIterator = paramList.iterator();
    X509Certificate localX509Certificate1;
    X509Certificate localX509Certificate2;
    do
    {
      boolean bool = localIterator.hasNext();
      localObject = null;
      if (!bool) {
        break;
      }
      localX509Certificate1 = (X509Certificate)localIterator.next();
      localX509Certificate2 = findSigner(localX509Certificate1, paramList);
    } while ((localX509Certificate2 != null) && (!localX509Certificate2.equals(localX509Certificate1)));
    Object localObject = localX509Certificate1;
    return localObject;
  }
  
  private X509Certificate findSignedCert(X509Certificate paramX509Certificate, List<X509Certificate> paramList)
  {
    Iterator localIterator = paramList.iterator();
    X509Certificate localX509Certificate;
    Principal localPrincipal;
    do
    {
      boolean bool = localIterator.hasNext();
      localObject = null;
      if (!bool) {
        break;
      }
      localX509Certificate = (X509Certificate)localIterator.next();
      localPrincipal = paramX509Certificate.getSubjectDN();
    } while ((!localX509Certificate.getIssuerDN().equals(localPrincipal)) || (localX509Certificate.equals(paramX509Certificate)));
    Object localObject = localX509Certificate;
    return localObject;
  }
  
  private X509Certificate findSigner(X509Certificate paramX509Certificate, List<X509Certificate> paramList)
  {
    Iterator localIterator = paramList.iterator();
    X509Certificate localX509Certificate;
    do
    {
      boolean bool = localIterator.hasNext();
      localObject = null;
      if (!bool) {
        break;
      }
      localX509Certificate = (X509Certificate)localIterator.next();
    } while (!localX509Certificate.getSubjectDN().equals(paramX509Certificate.getIssuerDN()));
    Object localObject = localX509Certificate;
    return localObject;
  }
  
  private X509Certificate[] reorderCertificateChain(X509Certificate[] paramArrayOfX509Certificate)
  {
    X509Certificate[] arrayOfX509Certificate = new X509Certificate[paramArrayOfX509Certificate.length];
    List localList = Arrays.asList(paramArrayOfX509Certificate);
    int i = -1 + paramArrayOfX509Certificate.length;
    X509Certificate localX509Certificate1 = findRootCert(localList);
    arrayOfX509Certificate[i] = localX509Certificate1;
    X509Certificate localX509Certificate2 = localX509Certificate1;
    for (;;)
    {
      localX509Certificate2 = findSignedCert(localX509Certificate2, localList);
      if ((localX509Certificate2 == null) || (i <= 0)) {
        break;
      }
      i--;
      arrayOfX509Certificate[i] = localX509Certificate2;
    }
    return arrayOfX509Certificate;
  }
  
  public void checkClientTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString)
    throws CertificateException
  {}
  
  public void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString)
    throws CertificateException
  {
    try
    {
      this.originalX509TrustManager.checkServerTrusted(paramArrayOfX509Certificate, paramString);
      return;
    }
    catch (CertificateException localCertificateException)
    {
      try
      {
        X509Certificate[] arrayOfX509Certificate = reorderCertificateChain(paramArrayOfX509Certificate);
        CertPathValidator localCertPathValidator = CertPathValidator.getInstance("PKIX");
        CertPath localCertPath = CertificateFactory.getInstance("X509").generateCertPath(Arrays.asList(arrayOfX509Certificate));
        PKIXParameters localPKIXParameters = new PKIXParameters(this.trustStore);
        localPKIXParameters.setRevocationEnabled(false);
        localCertPathValidator.validate(localCertPath, localPKIXParameters);
        return;
      }
      catch (Exception localException)
      {
        throw localCertificateException;
      }
    }
  }
  
  public X509Certificate[] getAcceptedIssuers()
  {
    return new X509Certificate[0];
  }
}
