.class Lcom/example/android_auth/LoginActivity$3;
.super Landroid/os/AsyncTask;
.source "LoginActivity.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/example/android_auth/LoginActivity;->doRequest(Ljava/lang/String;Ljava/lang/String;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/example/android_auth/LoginActivity;

.field final synthetic val$innerPassword:Ljava/lang/String;

.field final synthetic val$innerUsername:Ljava/lang/String;


# direct methods
.method constructor <init>(Lcom/example/android_auth/LoginActivity;Ljava/lang/String;Ljava/lang/String;)V
    .locals 0
    .param p1, "this$0"    # Lcom/example/android_auth/LoginActivity;

    .prologue
    .line 178
    iput-object p1, p0, Lcom/example/android_auth/LoginActivity$3;->this$0:Lcom/example/android_auth/LoginActivity;

    iput-object p2, p0, Lcom/example/android_auth/LoginActivity$3;->val$innerUsername:Ljava/lang/String;

    iput-object p3, p0, Lcom/example/android_auth/LoginActivity$3;->val$innerPassword:Ljava/lang/String;

    invoke-direct {p0}, Landroid/os/AsyncTask;-><init>()V

    return-void
.end method


# virtual methods
.method protected varargs doInBackground([Ljava/lang/Object;)Ljava/lang/Object;
    .locals 13
    .param p1, "objects"    # [Ljava/lang/Object;

    .prologue
    const/4 v12, 0x1

    const/4 v11, 0x0

    .line 183
    :try_start_0
    new-instance v7, Landroid/net/Uri$Builder;

    invoke-direct {v7}, Landroid/net/Uri$Builder;-><init>()V

    const-string v8, "username"

    iget-object v9, p0, Lcom/example/android_auth/LoginActivity$3;->val$innerUsername:Ljava/lang/String;

    .line 184
    invoke-virtual {v7, v8, v9}, Landroid/net/Uri$Builder;->appendQueryParameter(Ljava/lang/String;Ljava/lang/String;)Landroid/net/Uri$Builder;

    move-result-object v7

    const-string v8, "password"

    iget-object v9, p0, Lcom/example/android_auth/LoginActivity$3;->val$innerPassword:Ljava/lang/String;

    .line 185
    invoke-virtual {v7, v8, v9}, Landroid/net/Uri$Builder;->appendQueryParameter(Ljava/lang/String;Ljava/lang/String;)Landroid/net/Uri$Builder;

    move-result-object v1

    .line 186
    .local v1, "builder":Landroid/net/Uri$Builder;
    invoke-virtual {v1}, Landroid/net/Uri$Builder;->build()Landroid/net/Uri;

    move-result-object v7

    invoke-virtual {v7}, Landroid/net/Uri;->getEncodedQuery()Ljava/lang/String;

    move-result-object v3

    .line 188
    .local v3, "query":Ljava/lang/String;
    iget-object v7, p0, Lcom/example/android_auth/LoginActivity$3;->this$0:Lcom/example/android_auth/LoginActivity;

    # getter for: Lcom/example/android_auth/LoginActivity;->apiLib:Lcom/example/android_auth/http/Api;
    invoke-static {v7}, Lcom/example/android_auth/LoginActivity;->access$100(Lcom/example/android_auth/LoginActivity;)Lcom/example/android_auth/http/Api;

    move-result-object v7

    iget-object v8, p0, Lcom/example/android_auth/LoginActivity$3;->this$0:Lcom/example/android_auth/LoginActivity;

    # getter for: Lcom/example/android_auth/LoginActivity;->exampleUrl:Ljava/lang/String;
    invoke-static {v8}, Lcom/example/android_auth/LoginActivity;->access$000(Lcom/example/android_auth/LoginActivity;)Ljava/lang/String;

    move-result-object v8

    invoke-virtual {v7, v8, v3}, Lcom/example/android_auth/http/Api;->doPost(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;

    move-result-object v5

    .line 189
    .local v5, "result":Ljava/lang/String;
    iget-object v7, p0, Lcom/example/android_auth/LoginActivity$3;->this$0:Lcom/example/android_auth/LoginActivity;

    # getter for: Lcom/example/android_auth/LoginActivity;->apiLib:Lcom/example/android_auth/http/Api;
    invoke-static {v7}, Lcom/example/android_auth/LoginActivity;->access$100(Lcom/example/android_auth/LoginActivity;)Lcom/example/android_auth/http/Api;

    move-result-object v7

    invoke-virtual {v7}, Lcom/example/android_auth/http/Api;->getLastResponseCode()I

    move-result v4

    .line 190
    .local v4, "responseCode":I
    const/16 v7, 0xc8

    if-ne v4, v7, :cond_0

    .line 191
    const/4 v7, 0x1

    new-array v7, v7, [Ljava/lang/Object;

    const/4 v8, 0x0

    aput-object v5, v7, v8

    invoke-virtual {p0, v7}, Lcom/example/android_auth/LoginActivity$3;->publishProgress([Ljava/lang/Object;)V

    .line 205
    .end local v1    # "builder":Landroid/net/Uri$Builder;
    .end local v3    # "query":Ljava/lang/String;
    .end local v4    # "responseCode":I
    .end local v5    # "result":Ljava/lang/String;
    :goto_0
    const/4 v7, 0x0

    return-object v7

    .line 193
    .restart local v1    # "builder":Landroid/net/Uri$Builder;
    .restart local v3    # "query":Ljava/lang/String;
    .restart local v4    # "responseCode":I
    .restart local v5    # "result":Ljava/lang/String;
    :cond_0
    const/4 v7, 0x1

    new-array v7, v7, [Ljava/lang/Object;

    const/4 v8, 0x0

    new-instance v9, Ljava/lang/StringBuilder;

    invoke-direct {v9}, Ljava/lang/StringBuilder;-><init>()V

    const-string v10, "HTTP Response Code: "

    invoke-virtual {v9, v10}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v9

    invoke-virtual {v9, v4}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v9

    invoke-virtual {v9}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v9

    aput-object v9, v7, v8

    invoke-virtual {p0, v7}, Lcom/example/android_auth/LoginActivity$3;->publishProgress([Ljava/lang/Object;)V
    :try_end_0
    .catch Ljava/lang/Throwable; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_0

    .line 196
    .end local v1    # "builder":Landroid/net/Uri$Builder;
    .end local v3    # "query":Ljava/lang/String;
    .end local v4    # "responseCode":I
    .end local v5    # "result":Ljava/lang/String;
    :catch_0
    move-exception v2

    .line 197
    .local v2, "ex":Ljava/lang/Throwable;
    new-instance v0, Ljava/io/ByteArrayOutputStream;

    invoke-direct {v0}, Ljava/io/ByteArrayOutputStream;-><init>()V

    .line 198
    .local v0, "baos":Ljava/io/ByteArrayOutputStream;
    new-instance v6, Ljava/io/PrintWriter;

    invoke-direct {v6, v0}, Ljava/io/PrintWriter;-><init>(Ljava/io/OutputStream;)V

    .line 199
    .local v6, "writer":Ljava/io/PrintWriter;
    invoke-virtual {v2, v6}, Ljava/lang/Throwable;->printStackTrace(Ljava/io/PrintWriter;)V

    .line 200
    invoke-virtual {v6}, Ljava/io/PrintWriter;->flush()V

    .line 201
    invoke-virtual {v6}, Ljava/io/PrintWriter;->close()V

    .line 202
    new-array v7, v12, [Ljava/lang/Object;

    new-instance v8, Ljava/lang/StringBuilder;

    invoke-direct {v8}, Ljava/lang/StringBuilder;-><init>()V

    invoke-virtual {v2}, Ljava/lang/Throwable;->toString()Ljava/lang/String;

    move-result-object v9

    invoke-virtual {v8, v9}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v8

    const-string v9, " : "

    invoke-virtual {v8, v9}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v8

    invoke-virtual {v0}, Ljava/io/ByteArrayOutputStream;->toString()Ljava/lang/String;

    move-result-object v9

    invoke-virtual {v8, v9}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v8

    invoke-virtual {v8}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v8

    aput-object v8, v7, v11

    invoke-virtual {p0, v7}, Lcom/example/android_auth/LoginActivity$3;->publishProgress([Ljava/lang/Object;)V

    goto :goto_0
.end method

.method protected onPostExecute(Ljava/lang/Object;)V
    .locals 2
    .param p1, "result"    # Ljava/lang/Object;

    .prologue
    .line 219
    const-string v0, "LoginActivity"

    const-string v1, "Done!"

    invoke-static {v0, v1}, Landroid/util/Log;->i(Ljava/lang/String;Ljava/lang/String;)I

    .line 220
    return-void
.end method

.method protected varargs onProgressUpdate([Ljava/lang/Object;)V
    .locals 5
    .param p1, "values"    # [Ljava/lang/Object;

    .prologue
    .line 210
    new-instance v0, Ljava/lang/StringBuilder;

    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    .line 211
    .local v0, "buf":Ljava/lang/StringBuilder;
    array-length v3, p1

    const/4 v2, 0x0

    :goto_0
    if-ge v2, v3, :cond_0

    aget-object v1, p1, v2

    .line 212
    .local v1, "value":Ljava/lang/Object;
    invoke-virtual {v1}, Ljava/lang/Object;->toString()Ljava/lang/String;

    move-result-object v4

    invoke-virtual {v0, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    .line 211
    add-int/lit8 v2, v2, 0x1

    goto :goto_0

    .line 214
    .end local v1    # "value":Ljava/lang/Object;
    :cond_0
    const-string v2, "LoginActivity"

    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v3

    invoke-static {v2, v3}, Landroid/util/Log;->i(Ljava/lang/String;Ljava/lang/String;)I

    .line 215
    return-void
.end method
