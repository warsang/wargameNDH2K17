.class public Lcom/example/android_auth/http/Api;
.super Ljava/lang/Object;
.source "Api.java"


# instance fields
.field private lastResponseCode:I

.field private sslContext:Ljavax/net/ssl/SSLContext;


# direct methods
.method public constructor <init>(Lcom/example/android_auth/http/AuthenticationParameters;)V
    .locals 4
    .param p1, "authParams"    # Lcom/example/android_auth/http/AuthenticationParameters;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Exception;
        }
    .end annotation

    .prologue
    .line 30
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 32
    invoke-virtual {p1}, Lcom/example/android_auth/http/AuthenticationParameters;->getClientCertificate()Ljava/io/File;

    move-result-object v0

    .line 34
    .local v0, "clientCertFile":Ljava/io/File;
    invoke-static {}, Lcom/example/android_auth/http/SSLContextFactory;->getInstance()Lcom/example/android_auth/http/SSLContextFactory;

    move-result-object v1

    invoke-virtual {p1}, Lcom/example/android_auth/http/AuthenticationParameters;->getClientCertificatePassword()Ljava/lang/String;

    move-result-object v2

    invoke-virtual {p1}, Lcom/example/android_auth/http/AuthenticationParameters;->getCaCertificate()Ljava/lang/String;

    move-result-object v3

    invoke-virtual {v1, v0, v2, v3}, Lcom/example/android_auth/http/SSLContextFactory;->makeContext(Ljava/io/File;Ljava/lang/String;Ljava/lang/String;)Ljavax/net/ssl/SSLContext;

    move-result-object v1

    iput-object v1, p0, Lcom/example/android_auth/http/Api;->sslContext:Ljavax/net/ssl/SSLContext;

    .line 36
    new-instance v1, Ljava/net/CookieManager;

    invoke-direct {v1}, Ljava/net/CookieManager;-><init>()V

    invoke-static {v1}, Ljava/net/CookieHandler;->setDefault(Ljava/net/CookieHandler;)V

    .line 37
    return-void
.end method


# virtual methods
.method public doGet(Ljava/lang/String;)Ljava/lang/String;
    .locals 7
    .param p1, "url"    # Ljava/lang/String;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Exception;
        }
    .end annotation

    .prologue
    .line 41
    const/4 v3, 0x0

    .line 43
    .local v3, "result":Ljava/lang/String;
    const/4 v4, 0x0

    .line 45
    .local v4, "urlConnection":Ljava/net/HttpURLConnection;
    :try_start_0
    new-instance v2, Ljava/net/URL;

    invoke-direct {v2, p1}, Ljava/net/URL;-><init>(Ljava/lang/String;)V

    .line 46
    .local v2, "requestedUrl":Ljava/net/URL;
    invoke-virtual {v2}, Ljava/net/URL;->openConnection()Ljava/net/URLConnection;

    move-result-object v5

    move-object v0, v5

    check-cast v0, Ljava/net/HttpURLConnection;

    move-object v4, v0

    .line 47
    instance-of v5, v4, Ljavax/net/ssl/HttpsURLConnection;

    if-eqz v5, :cond_0

    .line 48
    move-object v0, v4

    check-cast v0, Ljavax/net/ssl/HttpsURLConnection;

    move-object v5, v0

    iget-object v6, p0, Lcom/example/android_auth/http/Api;->sslContext:Ljavax/net/ssl/SSLContext;

    invoke-virtual {v6}, Ljavax/net/ssl/SSLContext;->getSocketFactory()Ljavax/net/ssl/SSLSocketFactory;

    move-result-object v6

    invoke-virtual {v5, v6}, Ljavax/net/ssl/HttpsURLConnection;->setSSLSocketFactory(Ljavax/net/ssl/SSLSocketFactory;)V

    .line 50
    :cond_0
    const-string v5, "GET"

    invoke-virtual {v4, v5}, Ljava/net/HttpURLConnection;->setRequestMethod(Ljava/lang/String;)V

    .line 51
    const/16 v5, 0x5dc

    invoke-virtual {v4, v5}, Ljava/net/HttpURLConnection;->setConnectTimeout(I)V

    .line 52
    const/16 v5, 0x5dc

    invoke-virtual {v4, v5}, Ljava/net/HttpURLConnection;->setReadTimeout(I)V

    .line 54
    invoke-virtual {v4}, Ljava/net/HttpURLConnection;->getResponseCode()I

    move-result v5

    iput v5, p0, Lcom/example/android_auth/http/Api;->lastResponseCode:I

    .line 55
    invoke-virtual {v4}, Ljava/net/HttpURLConnection;->getInputStream()Ljava/io/InputStream;

    move-result-object v5

    invoke-static {v5}, Lcom/example/android_auth/util/IOUtil;->readFully(Ljava/io/InputStream;)Ljava/lang/String;
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    move-result-object v3

    .line 60
    if-eqz v4, :cond_1

    .line 61
    invoke-virtual {v4}, Ljava/net/HttpURLConnection;->disconnect()V

    .line 64
    .end local v2    # "requestedUrl":Ljava/net/URL;
    :cond_1
    :goto_0
    return-object v3

    .line 57
    :catch_0
    move-exception v1

    .line 58
    .local v1, "ex":Ljava/lang/Exception;
    :try_start_1
    invoke-virtual {v1}, Ljava/lang/Exception;->toString()Ljava/lang/String;
    :try_end_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    move-result-object v3

    .line 60
    if-eqz v4, :cond_1

    .line 61
    invoke-virtual {v4}, Ljava/net/HttpURLConnection;->disconnect()V

    goto :goto_0

    .line 60
    .end local v1    # "ex":Ljava/lang/Exception;
    :catchall_0
    move-exception v5

    if-eqz v4, :cond_2

    .line 61
    invoke-virtual {v4}, Ljava/net/HttpURLConnection;->disconnect()V

    :cond_2
    throw v5
.end method

.method public doPost(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    .locals 9
    .param p1, "url"    # Ljava/lang/String;
    .param p2, "query"    # Ljava/lang/String;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Exception;
        }
    .end annotation

    .prologue
    .line 68
    const/4 v4, 0x0

    .line 70
    .local v4, "result":Ljava/lang/String;
    const/4 v5, 0x0

    .line 72
    .local v5, "urlConnection":Ljava/net/HttpURLConnection;
    :try_start_0
    new-instance v3, Ljava/net/URL;

    invoke-direct {v3, p1}, Ljava/net/URL;-><init>(Ljava/lang/String;)V

    .line 73
    .local v3, "requestedUrl":Ljava/net/URL;
    invoke-virtual {v3}, Ljava/net/URL;->openConnection()Ljava/net/URLConnection;

    move-result-object v7

    move-object v0, v7

    check-cast v0, Ljava/net/HttpURLConnection;

    move-object v5, v0

    .line 74
    instance-of v7, v5, Ljavax/net/ssl/HttpsURLConnection;

    if-eqz v7, :cond_0

    .line 75
    move-object v0, v5

    check-cast v0, Ljavax/net/ssl/HttpsURLConnection;

    move-object v7, v0

    iget-object v8, p0, Lcom/example/android_auth/http/Api;->sslContext:Ljavax/net/ssl/SSLContext;

    invoke-virtual {v8}, Ljavax/net/ssl/SSLContext;->getSocketFactory()Ljavax/net/ssl/SSLSocketFactory;

    move-result-object v8

    invoke-virtual {v7, v8}, Ljavax/net/ssl/HttpsURLConnection;->setSSLSocketFactory(Ljavax/net/ssl/SSLSocketFactory;)V

    .line 78
    :cond_0
    const-string v7, "POST"

    invoke-virtual {v5, v7}, Ljava/net/HttpURLConnection;->setRequestMethod(Ljava/lang/String;)V

    .line 79
    const/16 v7, 0x5dc

    invoke-virtual {v5, v7}, Ljava/net/HttpURLConnection;->setConnectTimeout(I)V

    .line 80
    const/16 v7, 0x5dc

    invoke-virtual {v5, v7}, Ljava/net/HttpURLConnection;->setReadTimeout(I)V

    .line 81
    const/4 v7, 0x1

    invoke-virtual {v5, v7}, Ljava/net/HttpURLConnection;->setDoInput(Z)V

    .line 82
    const/4 v7, 0x1

    invoke-virtual {v5, v7}, Ljava/net/HttpURLConnection;->setDoOutput(Z)V

    .line 84
    invoke-virtual {v5}, Ljava/net/HttpURLConnection;->getOutputStream()Ljava/io/OutputStream;

    move-result-object v2

    .line 85
    .local v2, "os":Ljava/io/OutputStream;
    new-instance v6, Ljava/io/BufferedWriter;

    new-instance v7, Ljava/io/OutputStreamWriter;

    const-string v8, "UTF-8"

    invoke-direct {v7, v2, v8}, Ljava/io/OutputStreamWriter;-><init>(Ljava/io/OutputStream;Ljava/lang/String;)V

    invoke-direct {v6, v7}, Ljava/io/BufferedWriter;-><init>(Ljava/io/Writer;)V

    .line 86
    .local v6, "writer":Ljava/io/BufferedWriter;
    invoke-virtual {v6, p2}, Ljava/io/BufferedWriter;->write(Ljava/lang/String;)V

    .line 87
    invoke-virtual {v6}, Ljava/io/BufferedWriter;->flush()V

    .line 88
    invoke-virtual {v6}, Ljava/io/BufferedWriter;->close()V

    .line 89
    invoke-virtual {v2}, Ljava/io/OutputStream;->close()V

    .line 91
    invoke-virtual {v5}, Ljava/net/HttpURLConnection;->connect()V

    .line 93
    invoke-virtual {v5}, Ljava/net/HttpURLConnection;->getResponseCode()I

    move-result v7

    iput v7, p0, Lcom/example/android_auth/http/Api;->lastResponseCode:I

    .line 94
    invoke-virtual {v5}, Ljava/net/HttpURLConnection;->getInputStream()Ljava/io/InputStream;

    move-result-object v7

    invoke-static {v7}, Lcom/example/android_auth/util/IOUtil;->readFully(Ljava/io/InputStream;)Ljava/lang/String;
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    move-result-object v4

    .line 99
    if-eqz v5, :cond_1

    .line 100
    invoke-virtual {v5}, Ljava/net/HttpURLConnection;->disconnect()V

    .line 103
    .end local v2    # "os":Ljava/io/OutputStream;
    .end local v3    # "requestedUrl":Ljava/net/URL;
    .end local v6    # "writer":Ljava/io/BufferedWriter;
    :cond_1
    :goto_0
    return-object v4

    .line 96
    :catch_0
    move-exception v1

    .line 97
    .local v1, "ex":Ljava/lang/Exception;
    :try_start_1
    invoke-virtual {v1}, Ljava/lang/Exception;->toString()Ljava/lang/String;
    :try_end_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    move-result-object v4

    .line 99
    if-eqz v5, :cond_1

    .line 100
    invoke-virtual {v5}, Ljava/net/HttpURLConnection;->disconnect()V

    goto :goto_0

    .line 99
    .end local v1    # "ex":Ljava/lang/Exception;
    :catchall_0
    move-exception v7

    if-eqz v5, :cond_2

    .line 100
    invoke-virtual {v5}, Ljava/net/HttpURLConnection;->disconnect()V

    :cond_2
    throw v7
.end method

.method public getLastResponseCode()I
    .locals 1

    .prologue
    .line 27
    iget v0, p0, Lcom/example/android_auth/http/Api;->lastResponseCode:I

    return v0
.end method
