package com.example.android_auth.http;

import com.example.android_auth.util.IOUtil;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

public class Api {
    private int lastResponseCode;
    private SSLContext sslContext;

    public int getLastResponseCode() {
        return this.lastResponseCode;
    }

    public Api(AuthenticationParameters authParams) throws Exception {
        this.sslContext = SSLContextFactory.getInstance().makeContext(authParams.getClientCertificate(), authParams.getClientCertificatePassword(), authParams.getCaCertificate());
        CookieHandler.setDefault(new CookieManager());
    }

    public String doGet(String url) throws Exception {
        String result;
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) new URL(url).openConnection();
            if (urlConnection instanceof HttpsURLConnection) {
                ((HttpsURLConnection) urlConnection).setSSLSocketFactory(this.sslContext.getSocketFactory());
            }
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(1500);
            urlConnection.setReadTimeout(1500);
            this.lastResponseCode = urlConnection.getResponseCode();
            result = IOUtil.readFully(urlConnection.getInputStream());
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        } catch (Exception ex) {
            result = ex.toString();
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        } catch (Throwable th) {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return result;
    }

    public String doPost(String url, String query) throws Exception {
        String result;
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) new URL(url).openConnection();
            if (urlConnection instanceof HttpsURLConnection) {
                ((HttpsURLConnection) urlConnection).setSSLSocketFactory(this.sslContext.getSocketFactory());
            }
            urlConnection.setRequestMethod("POST");
            urlConnection.setConnectTimeout(1500);
            urlConnection.setReadTimeout(1500);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();
            urlConnection.connect();
            this.lastResponseCode = urlConnection.getResponseCode();
            result = IOUtil.readFully(urlConnection.getInputStream());
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        } catch (Exception ex) {
            result = ex.toString();
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        } catch (Throwable th) {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return result;
    }
}
