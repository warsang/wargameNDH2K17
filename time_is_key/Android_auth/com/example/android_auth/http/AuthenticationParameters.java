package com.example.android_auth.http;

import java.io.File;

public class AuthenticationParameters {
    private String caCertificate = null;
    private File clientCertificate = null;
    private String clientCertificatePassword = null;

    public File getClientCertificate() {
        return this.clientCertificate;
    }

    public void setClientCertificate(File clientCertificate) {
        this.clientCertificate = clientCertificate;
    }

    public String getClientCertificatePassword() {
        return this.clientCertificatePassword;
    }

    public void setClientCertificatePassword(String clientCertificatePassword) {
        this.clientCertificatePassword = clientCertificatePassword;
    }

    public String getCaCertificate() {
        return this.caCertificate;
    }

    public void setCaCertificate(String caCertificate) {
        this.caCertificate = caCertificate;
    }
}
