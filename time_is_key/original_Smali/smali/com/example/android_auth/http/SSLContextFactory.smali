.class public Lcom/example/android_auth/http/SSLContextFactory;
.super Ljava/lang/Object;
.source "SSLContextFactory.java"


# static fields
.field private static theInstance:Lcom/example/android_auth/http/SSLContextFactory;


# direct methods
.method static constructor <clinit>()V
    .locals 1

    .prologue
    .line 19
    const/4 v0, 0x0

    sput-object v0, Lcom/example/android_auth/http/SSLContextFactory;->theInstance:Lcom/example/android_auth/http/SSLContextFactory;

    return-void
.end method

.method private constructor <init>()V
    .locals 0

    .prologue
    .line 21
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 22
    return-void
.end method

.method public static getInstance()Lcom/example/android_auth/http/SSLContextFactory;
    .locals 1

    .prologue
    .line 25
    sget-object v0, Lcom/example/android_auth/http/SSLContextFactory;->theInstance:Lcom/example/android_auth/http/SSLContextFactory;

    if-nez v0, :cond_0

    .line 26
    new-instance v0, Lcom/example/android_auth/http/SSLContextFactory;

    invoke-direct {v0}, Lcom/example/android_auth/http/SSLContextFactory;-><init>()V

    sput-object v0, Lcom/example/android_auth/http/SSLContextFactory;->theInstance:Lcom/example/android_auth/http/SSLContextFactory;

    .line 28
    :cond_0
    sget-object v0, Lcom/example/android_auth/http/SSLContextFactory;->theInstance:Lcom/example/android_auth/http/SSLContextFactory;

    return-object v0
.end method

.method private loadPEMTrustStore(Ljava/lang/String;)Ljava/security/KeyStore;
    .locals 8
    .param p1, "certificateString"    # Ljava/lang/String;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Exception;
        }
    .end annotation

    .prologue
    .line 62
    new-instance v6, Ljava/io/ByteArrayInputStream;

    invoke-virtual {p1}, Ljava/lang/String;->getBytes()[B

    move-result-object v7

    invoke-direct {v6, v7}, Ljava/io/ByteArrayInputStream;-><init>([B)V

    invoke-virtual {p0, v6}, Lcom/example/android_auth/http/SSLContextFactory;->loadPemCertificate(Ljava/io/InputStream;)[B

    move-result-object v3

    .line 63
    .local v3, "der":[B
    new-instance v4, Ljava/io/ByteArrayInputStream;

    invoke-direct {v4, v3}, Ljava/io/ByteArrayInputStream;-><init>([B)V

    .line 64
    .local v4, "derInputStream":Ljava/io/ByteArrayInputStream;
    const-string v6, "X.509"

    invoke-static {v6}, Ljava/security/cert/CertificateFactory;->getInstance(Ljava/lang/String;)Ljava/security/cert/CertificateFactory;

    move-result-object v2

    .line 65
    .local v2, "certificateFactory":Ljava/security/cert/CertificateFactory;
    invoke-virtual {v2, v4}, Ljava/security/cert/CertificateFactory;->generateCertificate(Ljava/io/InputStream;)Ljava/security/cert/Certificate;

    move-result-object v1

    check-cast v1, Ljava/security/cert/X509Certificate;

    .line 66
    .local v1, "cert":Ljava/security/cert/X509Certificate;
    invoke-virtual {v1}, Ljava/security/cert/X509Certificate;->getSubjectX500Principal()Ljavax/security/auth/x500/X500Principal;

    move-result-object v6

    invoke-virtual {v6}, Ljavax/security/auth/x500/X500Principal;->getName()Ljava/lang/String;

    move-result-object v0

    .line 68
    .local v0, "alias":Ljava/lang/String;
    invoke-static {}, Ljava/security/KeyStore;->getDefaultType()Ljava/lang/String;

    move-result-object v6

    invoke-static {v6}, Ljava/security/KeyStore;->getInstance(Ljava/lang/String;)Ljava/security/KeyStore;

    move-result-object v5

    .line 69
    .local v5, "trustStore":Ljava/security/KeyStore;
    const/4 v6, 0x0

    invoke-virtual {v5, v6}, Ljava/security/KeyStore;->load(Ljava/security/KeyStore$LoadStoreParameter;)V

    .line 70
    invoke-virtual {v5, v0, v1}, Ljava/security/KeyStore;->setCertificateEntry(Ljava/lang/String;Ljava/security/cert/Certificate;)V

    .line 72
    return-object v5
.end method

.method private loadPKCS12KeyStore(Ljava/io/File;Ljava/lang/String;)Ljava/security/KeyStore;
    .locals 5
    .param p1, "certificateFile"    # Ljava/io/File;
    .param p2, "clientCertPassword"    # Ljava/lang/String;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Exception;
        }
    .end annotation

    .prologue
    .line 83
    const/4 v2, 0x0

    .line 84
    .local v2, "keyStore":Ljava/security/KeyStore;
    const/4 v0, 0x0

    .line 86
    .local v0, "fis":Ljava/io/FileInputStream;
    :try_start_0
    const-string v3, "PKCS12"

    invoke-static {v3}, Ljava/security/KeyStore;->getInstance(Ljava/lang/String;)Ljava/security/KeyStore;

    move-result-object v2

    .line 87
    new-instance v1, Ljava/io/FileInputStream;

    invoke-direct {v1, p1}, Ljava/io/FileInputStream;-><init>(Ljava/io/File;)V
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    .line 88
    .end local v0    # "fis":Ljava/io/FileInputStream;
    .local v1, "fis":Ljava/io/FileInputStream;
    :try_start_1
    invoke-virtual {p2}, Ljava/lang/String;->toCharArray()[C

    move-result-object v3

    invoke-virtual {v2, v1, v3}, Ljava/security/KeyStore;->load(Ljava/io/InputStream;[C)V
    :try_end_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_1

    .line 91
    if-eqz v1, :cond_0

    .line 92
    :try_start_2
    invoke-virtual {v1}, Ljava/io/FileInputStream;->close()V
    :try_end_2
    .catch Ljava/io/IOException; {:try_start_2 .. :try_end_2} :catch_0

    .line 98
    :cond_0
    :goto_0
    return-object v2

    .line 90
    .end local v1    # "fis":Ljava/io/FileInputStream;
    .restart local v0    # "fis":Ljava/io/FileInputStream;
    :catchall_0
    move-exception v3

    .line 91
    :goto_1
    if-eqz v0, :cond_1

    .line 92
    :try_start_3
    invoke-virtual {v0}, Ljava/io/FileInputStream;->close()V
    :try_end_3
    .catch Ljava/io/IOException; {:try_start_3 .. :try_end_3} :catch_1

    .line 96
    :cond_1
    :goto_2
    throw v3

    .line 94
    .end local v0    # "fis":Ljava/io/FileInputStream;
    .restart local v1    # "fis":Ljava/io/FileInputStream;
    :catch_0
    move-exception v3

    goto :goto_0

    .end local v1    # "fis":Ljava/io/FileInputStream;
    .restart local v0    # "fis":Ljava/io/FileInputStream;
    :catch_1
    move-exception v4

    goto :goto_2

    .line 90
    .end local v0    # "fis":Ljava/io/FileInputStream;
    .restart local v1    # "fis":Ljava/io/FileInputStream;
    :catchall_1
    move-exception v3

    move-object v0, v1

    .end local v1    # "fis":Ljava/io/FileInputStream;
    .restart local v0    # "fis":Ljava/io/FileInputStream;
    goto :goto_1
.end method


# virtual methods
.method loadPemCertificate(Ljava/io/InputStream;)[B
    .locals 7
    .param p1, "certificateStream"    # Ljava/io/InputStream;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    .prologue
    .line 109
    const/4 v3, 0x0

    .line 110
    .local v3, "der":[B
    const/4 v0, 0x0

    .line 113
    .local v0, "br":Ljava/io/BufferedReader;
    :try_start_0
    new-instance v2, Ljava/lang/StringBuilder;

    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    .line 114
    .local v2, "buf":Ljava/lang/StringBuilder;
    new-instance v1, Ljava/io/BufferedReader;

    new-instance v6, Ljava/io/InputStreamReader;

    invoke-direct {v6, p1}, Ljava/io/InputStreamReader;-><init>(Ljava/io/InputStream;)V

    invoke-direct {v1, v6}, Ljava/io/BufferedReader;-><init>(Ljava/io/Reader;)V
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    .line 116
    .end local v0    # "br":Ljava/io/BufferedReader;
    .local v1, "br":Ljava/io/BufferedReader;
    :try_start_1
    invoke-virtual {v1}, Ljava/io/BufferedReader;->readLine()Ljava/lang/String;

    move-result-object v4

    .line 117
    .local v4, "line":Ljava/lang/String;
    :goto_0
    if-eqz v4, :cond_1

    .line 118
    const-string v6, "--"

    invoke-virtual {v4, v6}, Ljava/lang/String;->startsWith(Ljava/lang/String;)Z

    move-result v6

    if-nez v6, :cond_0

    .line 119
    invoke-virtual {v2, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    .line 121
    :cond_0
    invoke-virtual {v1}, Ljava/io/BufferedReader;->readLine()Ljava/lang/String;

    move-result-object v4

    goto :goto_0

    .line 124
    :cond_1
    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v5

    .line 125
    .local v5, "pem":Ljava/lang/String;
    const/4 v6, 0x0

    invoke-static {v5, v6}, Landroid/util/Base64;->decode(Ljava/lang/String;I)[B
    :try_end_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_1

    move-result-object v3

    .line 128
    if-eqz v1, :cond_2

    .line 129
    invoke-virtual {v1}, Ljava/io/BufferedReader;->close()V

    .line 133
    :cond_2
    return-object v3

    .line 128
    .end local v1    # "br":Ljava/io/BufferedReader;
    .end local v2    # "buf":Ljava/lang/StringBuilder;
    .end local v4    # "line":Ljava/lang/String;
    .end local v5    # "pem":Ljava/lang/String;
    .restart local v0    # "br":Ljava/io/BufferedReader;
    :catchall_0
    move-exception v6

    :goto_1
    if-eqz v0, :cond_3

    .line 129
    invoke-virtual {v0}, Ljava/io/BufferedReader;->close()V

    :cond_3
    throw v6

    .line 128
    .end local v0    # "br":Ljava/io/BufferedReader;
    .restart local v1    # "br":Ljava/io/BufferedReader;
    .restart local v2    # "buf":Ljava/lang/StringBuilder;
    :catchall_1
    move-exception v6

    move-object v0, v1

    .end local v1    # "br":Ljava/io/BufferedReader;
    .restart local v0    # "br":Ljava/io/BufferedReader;
    goto :goto_1
.end method

.method public makeContext(Ljava/io/File;Ljava/lang/String;Ljava/lang/String;)Ljavax/net/ssl/SSLContext;
    .locals 8
    .param p1, "clientCertFile"    # Ljava/io/File;
    .param p2, "clientCertPassword"    # Ljava/lang/String;
    .param p3, "caCertString"    # Ljava/lang/String;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Exception;
        }
    .end annotation

    .prologue
    .line 40
    invoke-direct {p0, p1, p2}, Lcom/example/android_auth/http/SSLContextFactory;->loadPKCS12KeyStore(Ljava/io/File;Ljava/lang/String;)Ljava/security/KeyStore;

    move-result-object v1

    .line 41
    .local v1, "keyStore":Ljava/security/KeyStore;
    const-string v6, "X509"

    invoke-static {v6}, Ljavax/net/ssl/KeyManagerFactory;->getInstance(Ljava/lang/String;)Ljavax/net/ssl/KeyManagerFactory;

    move-result-object v2

    .line 42
    .local v2, "kmf":Ljavax/net/ssl/KeyManagerFactory;
    invoke-virtual {p2}, Ljava/lang/String;->toCharArray()[C

    move-result-object v6

    invoke-virtual {v2, v1, v6}, Ljavax/net/ssl/KeyManagerFactory;->init(Ljava/security/KeyStore;[C)V

    .line 43
    invoke-virtual {v2}, Ljavax/net/ssl/KeyManagerFactory;->getKeyManagers()[Ljavax/net/ssl/KeyManager;

    move-result-object v0

    .line 45
    .local v0, "keyManagers":[Ljavax/net/ssl/KeyManager;
    invoke-direct {p0, p3}, Lcom/example/android_auth/http/SSLContextFactory;->loadPEMTrustStore(Ljava/lang/String;)Ljava/security/KeyStore;

    move-result-object v5

    .line 46
    .local v5, "trustStore":Ljava/security/KeyStore;
    const/4 v6, 0x1

    new-array v4, v6, [Ljavax/net/ssl/TrustManager;

    const/4 v6, 0x0

    new-instance v7, Lcom/example/android_auth/http/CustomTrustManager;

    invoke-direct {v7, v5}, Lcom/example/android_auth/http/CustomTrustManager;-><init>(Ljava/security/KeyStore;)V

    aput-object v7, v4, v6

    .line 48
    .local v4, "trustManagers":[Ljavax/net/ssl/TrustManager;
    const-string v6, "TLS"

    invoke-static {v6}, Ljavax/net/ssl/SSLContext;->getInstance(Ljava/lang/String;)Ljavax/net/ssl/SSLContext;

    move-result-object v3

    .line 49
    .local v3, "sslContext":Ljavax/net/ssl/SSLContext;
    const/4 v6, 0x0

    invoke-virtual {v3, v0, v4, v6}, Ljavax/net/ssl/SSLContext;->init([Ljavax/net/ssl/KeyManager;[Ljavax/net/ssl/TrustManager;Ljava/security/SecureRandom;)V

    .line 51
    return-object v3
.end method
