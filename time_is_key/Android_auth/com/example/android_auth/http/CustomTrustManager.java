package com.example.android_auth.http;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class CustomTrustManager implements X509TrustManager {
    private final X509TrustManager originalX509TrustManager;
    private final KeyStore trustStore;

    public CustomTrustManager(KeyStore trustStore) throws NoSuchAlgorithmException, KeyStoreException {
        this.trustStore = trustStore;
        TrustManagerFactory originalTrustManagerFactory = TrustManagerFactory.getInstance("X509");
        originalTrustManagerFactory.init((KeyStore) null);
        this.originalX509TrustManager = (X509TrustManager) originalTrustManagerFactory.getTrustManagers()[0];
    }

    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }

    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
    }

    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        try {
            this.originalX509TrustManager.checkServerTrusted(chain, authType);
        } catch (CertificateException originalException) {
            try {
                X509Certificate[] reorderedChain = reorderCertificateChain(chain);
                CertPathValidator validator = CertPathValidator.getInstance("PKIX");
                CertPath certPath = CertificateFactory.getInstance("X509").generateCertPath(Arrays.asList(reorderedChain));
                PKIXParameters params = new PKIXParameters(this.trustStore);
                params.setRevocationEnabled(false);
                validator.validate(certPath, params);
            } catch (Exception e) {
                throw originalException;
            }
        }
    }

    private X509Certificate[] reorderCertificateChain(X509Certificate[] chain) {
        X509Certificate[] reorderedChain = new X509Certificate[chain.length];
        List<X509Certificate> certificates = Arrays.asList(chain);
        int position = chain.length - 1;
        X509Certificate rootCert = findRootCert(certificates);
        reorderedChain[position] = rootCert;
        X509Certificate cert = rootCert;
        while (true) {
            cert = findSignedCert(cert, certificates);
            if (cert == null || position <= 0) {
                return reorderedChain;
            }
            position--;
            reorderedChain[position] = cert;
        }
        return reorderedChain;
    }

    private X509Certificate findRootCert(List<X509Certificate> certificates) {
        for (X509Certificate cert : certificates) {
            X509Certificate signer = findSigner(cert, certificates);
            if (signer != null) {
                if (signer.equals(cert)) {
                }
            }
            return cert;
        }
        return null;
    }

    private X509Certificate findSignedCert(X509Certificate signingCert, List<X509Certificate> certificates) {
        for (X509Certificate cert : certificates) {
            if (cert.getIssuerDN().equals(signingCert.getSubjectDN()) && !cert.equals(signingCert)) {
                return cert;
            }
        }
        return null;
    }

    private X509Certificate findSigner(X509Certificate signedCert, List<X509Certificate> certificates) {
        for (X509Certificate cert : certificates) {
            if (cert.getSubjectDN().equals(signedCert.getIssuerDN())) {
                return cert;
            }
        }
        return null;
    }
}
