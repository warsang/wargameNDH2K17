/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  android.util.Base64
 */
package com.example.android_auth.http;

import android.util.Base64;
import com.example.android_auth.http.CustomTrustManager;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.security.auth.x500.X500Principal;

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

    private KeyStore loadPEMTrustStore(String string2) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.loadPemCertificate(new ByteArrayInputStream(string2.getBytes())));
        X509Certificate x509Certificate = (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(byteArrayInputStream);
        String string3 = x509Certificate.getSubjectX500Principal().getName();
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null);
        keyStore.setCertificateEntry(string3, x509Certificate);
        return keyStore;
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    private KeyStore loadPKCS12KeyStore(File var1_1, String var2_2) throws Exception {
        var3_3 = null;
        try {
            var6_4 = KeyStore.getInstance("PKCS12");
            var7_5 = new FileInputStream(var1_1);
        }
        catch (Throwable var4_6) {}
        var6_4.load(var7_5, var2_2.toCharArray());
        if (var7_5 == null) return var6_4;
        try {
            var7_5.close();
            return var6_4;
        }
        catch (IOException var8_9) {
            return var6_4;
        }
        ** GOTO lbl-1000
        catch (Throwable var4_8) {
            var3_3 = var7_5;
        }
lbl-1000: // 2 sources:
        {
            if (var3_3 == null) throw var4_7;
            try {
                var3_3.close();
            }
            catch (IOException var5_10) {
                throw var4_7;
            }
            throw var4_7;
        }
    }

    /*
     * Loose catch block
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     * Lifted jumps to return sites
     */
    byte[] loadPemCertificate(InputStream inputStream) throws IOException {
        byte[] arrby;
        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String string2 = bufferedReader2.readLine();
            while (string2 != null) {
                if (!string2.startsWith("--")) {
                    stringBuilder.append(string2);
                }
                string2 = bufferedReader2.readLine();
            }
            arrby = Base64.decode((String)stringBuilder.toString(), (int)0);
            if (bufferedReader2 == null) return arrby;
        }
        catch (Throwable var5_9) {
            bufferedReader = bufferedReader2;
        }
        bufferedReader2.close();
        return arrby;
        catch (Throwable throwable) {}
        {
            void var5_8;
            if (bufferedReader == null) throw var5_8;
            bufferedReader.close();
            throw var5_8;
        }
    }

    public SSLContext makeContext(File file, String string2, String string3) throws Exception {
        KeyStore keyStore = this.loadPKCS12KeyStore(file, string2);
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("X509");
        keyManagerFactory.init(keyStore, string2.toCharArray());
        KeyManager[] arrkeyManager = keyManagerFactory.getKeyManagers();
        KeyStore keyStore2 = this.loadPEMTrustStore(string3);
        TrustManager[] arrtrustManager = new TrustManager[]{new CustomTrustManager(keyStore2)};
        SSLContext sSLContext = SSLContext.getInstance("TLS");
        sSLContext.init(arrkeyManager, arrtrustManager, null);
        return sSLContext;
    }
}

