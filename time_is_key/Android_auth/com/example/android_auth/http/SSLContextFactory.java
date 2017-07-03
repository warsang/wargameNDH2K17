package com.example.android_auth.http;

import android.util.Base64;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

public class SSLContextFactory {
    private static SSLContextFactory theInstance = null;

    private SSLContextFactory() {
    }

    public static SSLContextFactory getInstance() {
        if (theInstance == null) {
            theInstance = new SSLContextFactory();
        }
        return theInstance;
    }

    public SSLContext makeContext(File clientCertFile, String clientCertPassword, String caCertString) throws Exception {
        KeyStore keyStore = loadPKCS12KeyStore(clientCertFile, clientCertPassword);
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("X509");
        kmf.init(keyStore, clientCertPassword.toCharArray());
        KeyManager[] keyManagers = kmf.getKeyManagers();
        TrustManager[] trustManagers = new TrustManager[]{new CustomTrustManager(loadPEMTrustStore(caCertString))};
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagers, trustManagers, null);
        return sslContext;
    }

    private KeyStore loadPEMTrustStore(String certificateString) throws Exception {
        X509Certificate cert = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(loadPemCertificate(new ByteArrayInputStream(certificateString.getBytes()))));
        String alias = cert.getSubjectX500Principal().getName();
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null);
        trustStore.setCertificateEntry(alias, cert);
        return trustStore;
    }

    private KeyStore loadPKCS12KeyStore(File certificateFile, String clientCertPassword) throws Exception {
        Throwable th;
        FileInputStream fis = null;
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            FileInputStream fis2 = new FileInputStream(certificateFile);
            try {
                keyStore.load(fis2, clientCertPassword.toCharArray());
                if (fis2 != null) {
                    try {
                        fis2.close();
                    } catch (IOException e) {
                    }
                }
                return keyStore;
            } catch (Throwable th2) {
                th = th2;
                fis = fis2;
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e2) {
                    }
                }
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            if (fis != null) {
                fis.close();
            }
            throw th;
        }
    }

    byte[] loadPemCertificate(InputStream certificateStream) throws IOException {
        Throwable th;
        BufferedReader br = null;
        try {
            StringBuilder buf = new StringBuilder();
            BufferedReader br2 = new BufferedReader(new InputStreamReader(certificateStream));
            try {
                for (String line = br2.readLine(); line != null; line = br2.readLine()) {
                    if (!line.startsWith("--")) {
                        buf.append(line);
                    }
                }
                byte[] der = Base64.decode(buf.toString(), 0);
                if (br2 != null) {
                    br2.close();
                }
                return der;
            } catch (Throwable th2) {
                th = th2;
                br = br2;
                if (br != null) {
                    br.close();
                }
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            if (br != null) {
                br.close();
            }
            throw th;
        }
    }
}
