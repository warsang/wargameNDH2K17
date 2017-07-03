.class public Lcom/example/android_auth/http/AuthenticationParameters;
.super Ljava/lang/Object;
.source "AuthenticationParameters.java"


# instance fields
.field private caCertificate:Ljava/lang/String;

.field private clientCertificate:Ljava/io/File;

.field private clientCertificatePassword:Ljava/lang/String;


# direct methods
.method public constructor <init>()V
    .locals 1

    .prologue
    const/4 v0, 0x0

    .line 8
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 9
    iput-object v0, p0, Lcom/example/android_auth/http/AuthenticationParameters;->clientCertificate:Ljava/io/File;

    .line 10
    iput-object v0, p0, Lcom/example/android_auth/http/AuthenticationParameters;->clientCertificatePassword:Ljava/lang/String;

    .line 11
    iput-object v0, p0, Lcom/example/android_auth/http/AuthenticationParameters;->caCertificate:Ljava/lang/String;

    return-void
.end method


# virtual methods
.method public getCaCertificate()Ljava/lang/String;
    .locals 1

    .prologue
    .line 30
    iget-object v0, p0, Lcom/example/android_auth/http/AuthenticationParameters;->caCertificate:Ljava/lang/String;

    return-object v0
.end method

.method public getClientCertificate()Ljava/io/File;
    .locals 1

    .prologue
    .line 14
    iget-object v0, p0, Lcom/example/android_auth/http/AuthenticationParameters;->clientCertificate:Ljava/io/File;

    return-object v0
.end method

.method public getClientCertificatePassword()Ljava/lang/String;
    .locals 1

    .prologue
    .line 22
    iget-object v0, p0, Lcom/example/android_auth/http/AuthenticationParameters;->clientCertificatePassword:Ljava/lang/String;

    return-object v0
.end method

.method public setCaCertificate(Ljava/lang/String;)V
    .locals 0
    .param p1, "caCertificate"    # Ljava/lang/String;

    .prologue
    .line 34
    iput-object p1, p0, Lcom/example/android_auth/http/AuthenticationParameters;->caCertificate:Ljava/lang/String;

    .line 35
    return-void
.end method

.method public setClientCertificate(Ljava/io/File;)V
    .locals 0
    .param p1, "clientCertificate"    # Ljava/io/File;

    .prologue
    .line 18
    iput-object p1, p0, Lcom/example/android_auth/http/AuthenticationParameters;->clientCertificate:Ljava/io/File;

    .line 19
    return-void
.end method

.method public setClientCertificatePassword(Ljava/lang/String;)V
    .locals 0
    .param p1, "clientCertificatePassword"    # Ljava/lang/String;

    .prologue
    .line 26
    iput-object p1, p0, Lcom/example/android_auth/http/AuthenticationParameters;->clientCertificatePassword:Ljava/lang/String;

    .line 27
    return-void
.end method
