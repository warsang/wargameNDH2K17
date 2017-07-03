.class public Lcom/example/android_auth/http/CustomTrustManager;
.super Ljava/lang/Object;
.source "CustomTrustManager.java"

# interfaces
.implements Ljavax/net/ssl/X509TrustManager;


# instance fields
.field private final originalX509TrustManager:Ljavax/net/ssl/X509TrustManager;

.field private final trustStore:Ljava/security/KeyStore;


# direct methods
.method public constructor <init>(Ljava/security/KeyStore;)V
    .locals 3
    .param p1, "trustStore"    # Ljava/security/KeyStore;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/security/NoSuchAlgorithmException;,
            Ljava/security/KeyStoreException;
        }
    .end annotation

    .prologue
    .line 29
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 30
    iput-object p1, p0, Lcom/example/android_auth/http/CustomTrustManager;->trustStore:Ljava/security/KeyStore;

    .line 32
    const-string v2, "X509"

    invoke-static {v2}, Ljavax/net/ssl/TrustManagerFactory;->getInstance(Ljava/lang/String;)Ljavax/net/ssl/TrustManagerFactory;

    move-result-object v0

    .line 33
    .local v0, "originalTrustManagerFactory":Ljavax/net/ssl/TrustManagerFactory;
    const/4 v2, 0x0

    check-cast v2, Ljava/security/KeyStore;

    invoke-virtual {v0, v2}, Ljavax/net/ssl/TrustManagerFactory;->init(Ljava/security/KeyStore;)V

    .line 35
    invoke-virtual {v0}, Ljavax/net/ssl/TrustManagerFactory;->getTrustManagers()[Ljavax/net/ssl/TrustManager;

    move-result-object v1

    .line 36
    .local v1, "originalTrustManagers":[Ljavax/net/ssl/TrustManager;
    const/4 v2, 0x0

    aget-object v2, v1, v2

    check-cast v2, Ljavax/net/ssl/X509TrustManager;

    iput-object v2, p0, Lcom/example/android_auth/http/CustomTrustManager;->originalX509TrustManager:Ljavax/net/ssl/X509TrustManager;

    .line 37
    return-void
.end method

.method private findRootCert(Ljava/util/List;)Ljava/security/cert/X509Certificate;
    .locals 5
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/util/List",
            "<",
            "Ljava/security/cert/X509Certificate;",
            ">;)",
            "Ljava/security/cert/X509Certificate;"
        }
    .end annotation

    .prologue
    .line 115
    .local p1, "certificates":Ljava/util/List;, "Ljava/util/List<Ljava/security/cert/X509Certificate;>;"
    const/4 v1, 0x0

    .line 117
    .local v1, "rootCert":Ljava/security/cert/X509Certificate;
    invoke-interface {p1}, Ljava/util/List;->iterator()Ljava/util/Iterator;

    move-result-object v3

    :cond_0
    invoke-interface {v3}, Ljava/util/Iterator;->hasNext()Z

    move-result v4

    if-eqz v4, :cond_2

    invoke-interface {v3}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/security/cert/X509Certificate;

    .line 118
    .local v0, "cert":Ljava/security/cert/X509Certificate;
    invoke-direct {p0, v0, p1}, Lcom/example/android_auth/http/CustomTrustManager;->findSigner(Ljava/security/cert/X509Certificate;Ljava/util/List;)Ljava/security/cert/X509Certificate;

    move-result-object v2

    .line 119
    .local v2, "signer":Ljava/security/cert/X509Certificate;
    if-eqz v2, :cond_1

    invoke-virtual {v2, v0}, Ljava/security/cert/X509Certificate;->equals(Ljava/lang/Object;)Z

    move-result v4

    if-eqz v4, :cond_0

    .line 120
    :cond_1
    move-object v1, v0

    .line 125
    .end local v0    # "cert":Ljava/security/cert/X509Certificate;
    .end local v2    # "signer":Ljava/security/cert/X509Certificate;
    :cond_2
    return-object v1
.end method

.method private findSignedCert(Ljava/security/cert/X509Certificate;Ljava/util/List;)Ljava/security/cert/X509Certificate;
    .locals 6
    .param p1, "signingCert"    # Ljava/security/cert/X509Certificate;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/security/cert/X509Certificate;",
            "Ljava/util/List",
            "<",
            "Ljava/security/cert/X509Certificate;",
            ">;)",
            "Ljava/security/cert/X509Certificate;"
        }
    .end annotation

    .prologue
    .line 133
    .local p2, "certificates":Ljava/util/List;, "Ljava/util/List<Ljava/security/cert/X509Certificate;>;"
    const/4 v2, 0x0

    .line 135
    .local v2, "signed":Ljava/security/cert/X509Certificate;
    invoke-interface {p2}, Ljava/util/List;->iterator()Ljava/util/Iterator;

    move-result-object v4

    :cond_0
    invoke-interface {v4}, Ljava/util/Iterator;->hasNext()Z

    move-result v5

    if-eqz v5, :cond_1

    invoke-interface {v4}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/security/cert/X509Certificate;

    .line 136
    .local v0, "cert":Ljava/security/cert/X509Certificate;
    invoke-virtual {p1}, Ljava/security/cert/X509Certificate;->getSubjectDN()Ljava/security/Principal;

    move-result-object v3

    .line 137
    .local v3, "signingCertSubjectDN":Ljava/security/Principal;
    invoke-virtual {v0}, Ljava/security/cert/X509Certificate;->getIssuerDN()Ljava/security/Principal;

    move-result-object v1

    .line 138
    .local v1, "certIssuerDN":Ljava/security/Principal;
    invoke-interface {v1, v3}, Ljava/security/Principal;->equals(Ljava/lang/Object;)Z

    move-result v5

    if-eqz v5, :cond_0

    invoke-virtual {v0, p1}, Ljava/security/cert/X509Certificate;->equals(Ljava/lang/Object;)Z

    move-result v5

    if-nez v5, :cond_0

    .line 139
    move-object v2, v0

    .line 144
    .end local v0    # "cert":Ljava/security/cert/X509Certificate;
    .end local v1    # "certIssuerDN":Ljava/security/Principal;
    .end local v3    # "signingCertSubjectDN":Ljava/security/Principal;
    :cond_1
    return-object v2
.end method

.method private findSigner(Ljava/security/cert/X509Certificate;Ljava/util/List;)Ljava/security/cert/X509Certificate;
    .locals 6
    .param p1, "signedCert"    # Ljava/security/cert/X509Certificate;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/security/cert/X509Certificate;",
            "Ljava/util/List",
            "<",
            "Ljava/security/cert/X509Certificate;",
            ">;)",
            "Ljava/security/cert/X509Certificate;"
        }
    .end annotation

    .prologue
    .line 152
    .local p2, "certificates":Ljava/util/List;, "Ljava/util/List<Ljava/security/cert/X509Certificate;>;"
    const/4 v3, 0x0

    .line 154
    .local v3, "signer":Ljava/security/cert/X509Certificate;
    invoke-interface {p2}, Ljava/util/List;->iterator()Ljava/util/Iterator;

    move-result-object v4

    :cond_0
    invoke-interface {v4}, Ljava/util/Iterator;->hasNext()Z

    move-result v5

    if-eqz v5, :cond_1

    invoke-interface {v4}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, Ljava/security/cert/X509Certificate;

    .line 155
    .local v0, "cert":Ljava/security/cert/X509Certificate;
    invoke-virtual {v0}, Ljava/security/cert/X509Certificate;->getSubjectDN()Ljava/security/Principal;

    move-result-object v1

    .line 156
    .local v1, "certSubjectDN":Ljava/security/Principal;
    invoke-virtual {p1}, Ljava/security/cert/X509Certificate;->getIssuerDN()Ljava/security/Principal;

    move-result-object v2

    .line 157
    .local v2, "issuerDN":Ljava/security/Principal;
    invoke-interface {v1, v2}, Ljava/security/Principal;->equals(Ljava/lang/Object;)Z

    move-result v5

    if-eqz v5, :cond_0

    .line 158
    move-object v3, v0

    .line 163
    .end local v0    # "cert":Ljava/security/cert/X509Certificate;
    .end local v1    # "certSubjectDN":Ljava/security/Principal;
    .end local v2    # "issuerDN":Ljava/security/Principal;
    :cond_1
    return-object v3
.end method

.method private reorderCertificateChain([Ljava/security/cert/X509Certificate;)[Ljava/security/cert/X509Certificate;
    .locals 6
    .param p1, "chain"    # [Ljava/security/cert/X509Certificate;

    .prologue
    .line 93
    array-length v5, p1

    new-array v3, v5, [Ljava/security/cert/X509Certificate;

    .line 94
    .local v3, "reorderedChain":[Ljava/security/cert/X509Certificate;
    invoke-static {p1}, Ljava/util/Arrays;->asList([Ljava/lang/Object;)Ljava/util/List;

    move-result-object v1

    .line 96
    .local v1, "certificates":Ljava/util/List;, "Ljava/util/List<Ljava/security/cert/X509Certificate;>;"
    array-length v5, p1

    add-int/lit8 v2, v5, -0x1

    .line 97
    .local v2, "position":I
    invoke-direct {p0, v1}, Lcom/example/android_auth/http/CustomTrustManager;->findRootCert(Ljava/util/List;)Ljava/security/cert/X509Certificate;

    move-result-object v4

    .line 98
    .local v4, "rootCert":Ljava/security/cert/X509Certificate;
    aput-object v4, v3, v2

    .line 100
    move-object v0, v4

    .line 101
    .local v0, "cert":Ljava/security/cert/X509Certificate;
    :goto_0
    invoke-direct {p0, v0, v1}, Lcom/example/android_auth/http/CustomTrustManager;->findSignedCert(Ljava/security/cert/X509Certificate;Ljava/util/List;)Ljava/security/cert/X509Certificate;

    move-result-object v0

    if-eqz v0, :cond_0

    if-lez v2, :cond_0

    .line 102
    add-int/lit8 v2, v2, -0x1

    aput-object v0, v3, v2

    goto :goto_0

    .line 105
    :cond_0
    return-object v3
.end method


# virtual methods
.method public checkClientTrusted([Ljava/security/cert/X509Certificate;Ljava/lang/String;)V
    .locals 0
    .param p1, "chain"    # [Ljava/security/cert/X509Certificate;
    .param p2, "authType"    # Ljava/lang/String;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/security/cert/CertificateException;
        }
    .end annotation

    .prologue
    .line 52
    return-void
.end method

.method public checkServerTrusted([Ljava/security/cert/X509Certificate;Ljava/lang/String;)V
    .locals 8
    .param p1, "chain"    # [Ljava/security/cert/X509Certificate;
    .param p2, "authType"    # Ljava/lang/String;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/security/cert/CertificateException;
        }
    .end annotation

    .prologue
    .line 68
    :try_start_0
    iget-object v7, p0, Lcom/example/android_auth/http/CustomTrustManager;->originalX509TrustManager:Ljavax/net/ssl/X509TrustManager;

    invoke-interface {v7, p1, p2}, Ljavax/net/ssl/X509TrustManager;->checkServerTrusted([Ljava/security/cert/X509Certificate;Ljava/lang/String;)V
    :try_end_0
    .catch Ljava/security/cert/CertificateException; {:try_start_0 .. :try_end_0} :catch_0

    .line 83
    :goto_0
    return-void

    .line 69
    :catch_0
    move-exception v3

    .line 71
    .local v3, "originalException":Ljava/security/cert/CertificateException;
    :try_start_1
    invoke-direct {p0, p1}, Lcom/example/android_auth/http/CustomTrustManager;->reorderCertificateChain([Ljava/security/cert/X509Certificate;)[Ljava/security/cert/X509Certificate;

    move-result-object v5

    .line 72
    .local v5, "reorderedChain":[Ljava/security/cert/X509Certificate;
    const-string v7, "PKIX"

    invoke-static {v7}, Ljava/security/cert/CertPathValidator;->getInstance(Ljava/lang/String;)Ljava/security/cert/CertPathValidator;

    move-result-object v6

    .line 73
    .local v6, "validator":Ljava/security/cert/CertPathValidator;
    const-string v7, "X509"

    invoke-static {v7}, Ljava/security/cert/CertificateFactory;->getInstance(Ljava/lang/String;)Ljava/security/cert/CertificateFactory;

    move-result-object v2

    .line 74
    .local v2, "factory":Ljava/security/cert/CertificateFactory;
    invoke-static {v5}, Ljava/util/Arrays;->asList([Ljava/lang/Object;)Ljava/util/List;

    move-result-object v7

    invoke-virtual {v2, v7}, Ljava/security/cert/CertificateFactory;->generateCertPath(Ljava/util/List;)Ljava/security/cert/CertPath;

    move-result-object v0

    .line 75
    .local v0, "certPath":Ljava/security/cert/CertPath;
    new-instance v4, Ljava/security/cert/PKIXParameters;

    iget-object v7, p0, Lcom/example/android_auth/http/CustomTrustManager;->trustStore:Ljava/security/KeyStore;

    invoke-direct {v4, v7}, Ljava/security/cert/PKIXParameters;-><init>(Ljava/security/KeyStore;)V

    .line 76
    .local v4, "params":Ljava/security/cert/PKIXParameters;
    const/4 v7, 0x0

    invoke-virtual {v4, v7}, Ljava/security/cert/PKIXParameters;->setRevocationEnabled(Z)V

    .line 77
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_1

    goto :goto_0

    .line 78
    .end local v0    # "certPath":Ljava/security/cert/CertPath;
    .end local v2    # "factory":Ljava/security/cert/CertificateFactory;
    .end local v4    # "params":Ljava/security/cert/PKIXParameters;
    .end local v5    # "reorderedChain":[Ljava/security/cert/X509Certificate;
    .end local v6    # "validator":Ljava/security/cert/CertPathValidator;
    :catch_1
    move-exception v1

    .line 79
    .local v1, "ex":Ljava/lang/Exception;
    throw v3
.end method

.method public getAcceptedIssuers()[Ljava/security/cert/X509Certificate;
    .locals 1

    .prologue
    .line 44
    const/4 v0, 0x0

    new-array v0, v0, [Ljava/security/cert/X509Certificate;

    return-object v0
.end method
