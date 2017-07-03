.class public Lcom/example/android_auth/util/IOUtil;
.super Ljava/lang/Object;
.source "IOUtil.java"


# direct methods
.method public constructor <init>()V
    .locals 0

    .prologue
    .line 8
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public static readFully(Ljava/io/InputStream;)Ljava/lang/String;
    .locals 7
    .param p0, "inputStream"    # Ljava/io/InputStream;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    .prologue
    .line 11
    if-nez p0, :cond_1

    .line 12
    const-string v6, ""

    .line 29
    :cond_0
    :goto_0
    return-object v6

    .line 15
    :cond_1
    const/4 v2, 0x0

    .line 16
    .local v2, "bufferedInputStream":Ljava/io/BufferedInputStream;
    const/4 v4, 0x0

    .line 19
    .local v4, "byteArrayOutputStream":Ljava/io/ByteArrayOutputStream;
    :try_start_0
    new-instance v3, Ljava/io/BufferedInputStream;

    invoke-direct {v3, p0}, Ljava/io/BufferedInputStream;-><init>(Ljava/io/InputStream;)V
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_1

    .line 20
    .end local v2    # "bufferedInputStream":Ljava/io/BufferedInputStream;
    .local v3, "bufferedInputStream":Ljava/io/BufferedInputStream;
    :try_start_1
    new-instance v5, Ljava/io/ByteArrayOutputStream;

    invoke-direct {v5}, Ljava/io/ByteArrayOutputStream;-><init>()V
    :try_end_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_2

    .line 22
    .end local v4    # "byteArrayOutputStream":Ljava/io/ByteArrayOutputStream;
    .local v5, "byteArrayOutputStream":Ljava/io/ByteArrayOutputStream;
    const/16 v6, 0x400

    :try_start_2
    new-array v1, v6, [B

    .line 23
    .local v1, "buffer":[B
    const/4 v0, 0x0

    .line 25
    .local v0, "available":I
    :goto_1
    invoke-virtual {v3, v1}, Ljava/io/BufferedInputStream;->read([B)I

    move-result v0

    if-ltz v0, :cond_3

    .line 26
    const/4 v6, 0x0

    invoke-virtual {v5, v1, v6, v0}, Ljava/io/ByteArrayOutputStream;->write([BII)V
    :try_end_2
    .catchall {:try_start_2 .. :try_end_2} :catchall_0

    goto :goto_1

    .line 32
    .end local v0    # "available":I
    .end local v1    # "buffer":[B
    :catchall_0
    move-exception v6

    move-object v4, v5

    .end local v5    # "byteArrayOutputStream":Ljava/io/ByteArrayOutputStream;
    .restart local v4    # "byteArrayOutputStream":Ljava/io/ByteArrayOutputStream;
    move-object v2, v3

    .end local v3    # "bufferedInputStream":Ljava/io/BufferedInputStream;
    .restart local v2    # "bufferedInputStream":Ljava/io/BufferedInputStream;
    :goto_2
    if-eqz v2, :cond_2

    .line 33
    invoke-virtual {v2}, Ljava/io/BufferedInputStream;->close()V

    :cond_2
    throw v6

    .line 29
    .end local v2    # "bufferedInputStream":Ljava/io/BufferedInputStream;
    .end local v4    # "byteArrayOutputStream":Ljava/io/ByteArrayOutputStream;
    .restart local v0    # "available":I
    .restart local v1    # "buffer":[B
    .restart local v3    # "bufferedInputStream":Ljava/io/BufferedInputStream;
    .restart local v5    # "byteArrayOutputStream":Ljava/io/ByteArrayOutputStream;
    :cond_3
    :try_start_3
    invoke-virtual {v5}, Ljava/io/ByteArrayOutputStream;->toString()Ljava/lang/String;
    :try_end_3
    .catchall {:try_start_3 .. :try_end_3} :catchall_0

    move-result-object v6

    .line 32
    if-eqz v3, :cond_0

    .line 33
    invoke-virtual {v3}, Ljava/io/BufferedInputStream;->close()V

    goto :goto_0

    .line 32
    .end local v0    # "available":I
    .end local v1    # "buffer":[B
    .end local v3    # "bufferedInputStream":Ljava/io/BufferedInputStream;
    .end local v5    # "byteArrayOutputStream":Ljava/io/ByteArrayOutputStream;
    .restart local v2    # "bufferedInputStream":Ljava/io/BufferedInputStream;
    .restart local v4    # "byteArrayOutputStream":Ljava/io/ByteArrayOutputStream;
    :catchall_1
    move-exception v6

    goto :goto_2

    .end local v2    # "bufferedInputStream":Ljava/io/BufferedInputStream;
    .restart local v3    # "bufferedInputStream":Ljava/io/BufferedInputStream;
    :catchall_2
    move-exception v6

    move-object v2, v3

    .end local v3    # "bufferedInputStream":Ljava/io/BufferedInputStream;
    .restart local v2    # "bufferedInputStream":Ljava/io/BufferedInputStream;
    goto :goto_2
.end method
