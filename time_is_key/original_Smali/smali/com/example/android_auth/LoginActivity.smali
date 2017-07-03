.class public Lcom/example/android_auth/LoginActivity;
.super Landroid/support/v7/app/AppCompatActivity;
.source "LoginActivity.java"


# static fields
.field private static PERMISSIONS_STORAGE:[Ljava/lang/String; = null

.field private static final REQUEST_EXTERNAL_STORAGE:I = 0x1

.field private static final REQUEST_SIGNUP:I = 0x0

.field private static final TAG:Ljava/lang/String; = "LoginActivity"


# instance fields
.field private _loginButton:Landroid/widget/Button;

.field private _passwordText:Landroid/widget/EditText;

.field private _usernameText:Landroid/widget/EditText;

.field private activity:Landroid/support/v7/app/AppCompatActivity;

.field private apiLib:Lcom/example/android_auth/http/Api;

.field private exampleUrl:Ljava/lang/String;


# direct methods
.method static constructor <clinit>()V
    .locals 3

    .prologue
    .line 38
    const/4 v0, 0x2

    new-array v0, v0, [Ljava/lang/String;

    const/4 v1, 0x0

    const-string v2, "android.permission.READ_EXTERNAL_STORAGE"

    aput-object v2, v0, v1

    const/4 v1, 0x1

    const-string v2, "android.permission.WRITE_EXTERNAL_STORAGE"

    aput-object v2, v0, v1

    sput-object v0, Lcom/example/android_auth/LoginActivity;->PERMISSIONS_STORAGE:[Ljava/lang/String;

    return-void
.end method

.method public constructor <init>()V
    .locals 1

    .prologue
    .line 36
    invoke-direct {p0}, Landroid/support/v7/app/AppCompatActivity;-><init>()V

    .line 50
    iput-object p0, p0, Lcom/example/android_auth/LoginActivity;->activity:Landroid/support/v7/app/AppCompatActivity;

    .line 53
    const-string v0, "https://zxylieipe.wargame.ndh/login"

    iput-object v0, p0, Lcom/example/android_auth/LoginActivity;->exampleUrl:Ljava/lang/String;

    return-void
.end method

.method static synthetic access$000(Lcom/example/android_auth/LoginActivity;)Ljava/lang/String;
    .locals 1
    .param p0, "x0"    # Lcom/example/android_auth/LoginActivity;

    .prologue
    .line 36
    iget-object v0, p0, Lcom/example/android_auth/LoginActivity;->exampleUrl:Ljava/lang/String;

    return-object v0
.end method

.method static synthetic access$100(Lcom/example/android_auth/LoginActivity;)Lcom/example/android_auth/http/Api;
    .locals 1
    .param p0, "x0"    # Lcom/example/android_auth/LoginActivity;

    .prologue
    .line 36
    iget-object v0, p0, Lcom/example/android_auth/LoginActivity;->apiLib:Lcom/example/android_auth/http/Api;

    return-object v0
.end method

.method private doRequest(Ljava/lang/String;Ljava/lang/String;)V
    .locals 6
    .param p1, "username"    # Ljava/lang/String;
    .param p2, "password"    # Ljava/lang/String;

    .prologue
    .line 166
    move-object v3, p1

    .line 167
    .local v3, "innerUsername":Ljava/lang/String;
    move-object v2, p2

    .line 170
    .local v2, "innerPassword":Ljava/lang/String;
    :try_start_0
    new-instance v0, Lcom/example/android_auth/http/AuthenticationParameters;

    invoke-direct {v0}, Lcom/example/android_auth/http/AuthenticationParameters;-><init>()V

    .line 171
    .local v0, "authParams":Lcom/example/android_auth/http/AuthenticationParameters;
    invoke-direct {p0}, Lcom/example/android_auth/LoginActivity;->getClientCertFile()Ljava/io/File;

    move-result-object v4

    invoke-virtual {v0, v4}, Lcom/example/android_auth/http/AuthenticationParameters;->setClientCertificate(Ljava/io/File;)V

    .line 172
    const-string v4, "password"

    invoke-virtual {v0, v4}, Lcom/example/android_auth/http/AuthenticationParameters;->setClientCertificatePassword(Ljava/lang/String;)V

    .line 173
    invoke-direct {p0}, Lcom/example/android_auth/LoginActivity;->readCaCert()Ljava/lang/String;

    move-result-object v4

    invoke-virtual {v0, v4}, Lcom/example/android_auth/http/AuthenticationParameters;->setCaCertificate(Ljava/lang/String;)V

    .line 175
    new-instance v4, Lcom/example/android_auth/http/Api;

    invoke-direct {v4, v0}, Lcom/example/android_auth/http/Api;-><init>(Lcom/example/android_auth/http/AuthenticationParameters;)V

    iput-object v4, p0, Lcom/example/android_auth/LoginActivity;->apiLib:Lcom/example/android_auth/http/Api;

    .line 178
    new-instance v4, Lcom/example/android_auth/LoginActivity$3;

    invoke-direct {v4, p0, v3, v2}, Lcom/example/android_auth/LoginActivity$3;-><init>(Lcom/example/android_auth/LoginActivity;Ljava/lang/String;Ljava/lang/String;)V

    const/4 v5, 0x0

    new-array v5, v5, [Ljava/lang/Object;

    .line 221
    invoke-virtual {v4, v5}, Lcom/example/android_auth/LoginActivity$3;->execute([Ljava/lang/Object;)Landroid/os/AsyncTask;
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    .line 226
    .end local v0    # "authParams":Lcom/example/android_auth/http/AuthenticationParameters;
    :goto_0
    return-void

    .line 223
    :catch_0
    move-exception v1

    .line 224
    .local v1, "ex":Ljava/lang/Exception;
    const-string v4, "LoginActivity"

    const-string v5, "failed to create timeApi"

    invoke-static {v4, v5, v1}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    goto :goto_0
.end method

.method private getClientCertFile()Ljava/io/File;
    .locals 12
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/IOException;
        }
    .end annotation

    .prologue
    .line 231
    invoke-virtual {p0}, Lcom/example/android_auth/LoginActivity;->getResources()Landroid/content/res/Resources;

    move-result-object v7

    .line 232
    invoke-virtual {p0}, Lcom/example/android_auth/LoginActivity;->getResources()Landroid/content/res/Resources;

    move-result-object v8

    const-string v9, "client"

    const-string v10, "raw"

    .line 233
    invoke-virtual {p0}, Lcom/example/android_auth/LoginActivity;->getPackageName()Ljava/lang/String;

    move-result-object v11

    .line 232
    invoke-virtual {v8, v9, v10, v11}, Landroid/content/res/Resources;->getIdentifier(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I

    move-result v8

    .line 231
    invoke-virtual {v7, v8}, Landroid/content/res/Resources;->openRawResource(I)Ljava/io/InputStream;

    move-result-object v5

    .line 235
    .local v5, "is":Ljava/io/InputStream;
    new-instance v4, Ljava/io/File;

    invoke-static {}, Landroid/os/Environment;->getExternalStorageDirectory()Ljava/io/File;

    move-result-object v7

    const-string v8, "client_tmp.pfx"

    invoke-direct {v4, v7, v8}, Ljava/io/File;-><init>(Ljava/io/File;Ljava/lang/String;)V

    .line 236
    .local v4, "f":Ljava/io/File;
    new-instance v6, Ljava/io/FileOutputStream;

    const/4 v7, 0x1

    invoke-direct {v6, v4, v7}, Ljava/io/FileOutputStream;-><init>(Ljava/io/File;Z)V

    .line 238
    .local v6, "os":Ljava/io/OutputStream;
    const/high16 v0, 0x100000

    .line 240
    .local v0, "buffer_size":I
    const/high16 v7, 0x100000

    :try_start_0
    new-array v1, v7, [B

    .line 242
    .local v1, "bytes":[B
    :goto_0
    const/4 v7, 0x0

    const/high16 v8, 0x100000

    invoke-virtual {v5, v1, v7, v8}, Ljava/io/InputStream;->read([BII)I

    move-result v2

    .line 243
    .local v2, "count":I
    const/4 v7, -0x1

    if-ne v2, v7, :cond_0

    .line 247
    invoke-virtual {v5}, Ljava/io/InputStream;->close()V

    .line 248
    invoke-virtual {v6}, Ljava/io/OutputStream;->close()V

    .line 253
    .end local v1    # "bytes":[B
    .end local v2    # "count":I
    :goto_1
    return-object v4

    .line 245
    .restart local v1    # "bytes":[B
    .restart local v2    # "count":I
    :cond_0
    const/4 v7, 0x0

    invoke-virtual {v6, v1, v7, v2}, Ljava/io/OutputStream;->write([BII)V
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    goto :goto_0

    .line 249
    .end local v1    # "bytes":[B
    .end local v2    # "count":I
    :catch_0
    move-exception v3

    .line 250
    .local v3, "ex":Ljava/lang/Exception;
    invoke-virtual {v3}, Ljava/lang/Exception;->printStackTrace()V

    goto :goto_1
.end method

.method private readCaCert()Ljava/lang/String;
    .locals 6
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/lang/Exception;
        }
    .end annotation

    .prologue
    .line 257
    invoke-virtual {p0}, Lcom/example/android_auth/LoginActivity;->getResources()Landroid/content/res/Resources;

    move-result-object v1

    .line 258
    invoke-virtual {p0}, Lcom/example/android_auth/LoginActivity;->getResources()Landroid/content/res/Resources;

    move-result-object v2

    const-string v3, "ca"

    const-string v4, "raw"

    .line 259
    invoke-virtual {p0}, Lcom/example/android_auth/LoginActivity;->getPackageName()Ljava/lang/String;

    move-result-object v5

    .line 258
    invoke-virtual {v2, v3, v4, v5}, Landroid/content/res/Resources;->getIdentifier(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I

    move-result v2

    .line 257
    invoke-virtual {v1, v2}, Landroid/content/res/Resources;->openRawResource(I)Ljava/io/InputStream;

    move-result-object v0

    .line 261
    .local v0, "inputStream":Ljava/io/InputStream;
    invoke-static {v0}, Lcom/example/android_auth/util/IOUtil;->readFully(Ljava/io/InputStream;)Ljava/lang/String;

    move-result-object v1

    return-object v1
.end method

.method public static verifyStoragePermissions(Landroid/app/Activity;)V
    .locals 3
    .param p0, "activity"    # Landroid/app/Activity;

    .prologue
    .line 266
    const-string v1, "android.permission.WRITE_EXTERNAL_STORAGE"

    invoke-static {p0, v1}, Landroid/support/v4/app/ActivityCompat;->checkSelfPermission(Landroid/content/Context;Ljava/lang/String;)I

    move-result v0

    .line 268
    .local v0, "permission":I
    if-eqz v0, :cond_0

    .line 270
    sget-object v1, Lcom/example/android_auth/LoginActivity;->PERMISSIONS_STORAGE:[Ljava/lang/String;

    const/4 v2, 0x1

    invoke-static {p0, v1, v2}, Landroid/support/v4/app/ActivityCompat;->requestPermissions(Landroid/app/Activity;[Ljava/lang/String;I)V

    .line 276
    :cond_0
    return-void
.end method


# virtual methods
.method public login()V
    .locals 8

    .prologue
    .line 79
    const-string v3, "LoginActivity"

    const-string v4, "Login"

    invoke-static {v3, v4}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 81
    invoke-virtual {p0}, Lcom/example/android_auth/LoginActivity;->validate()Z

    move-result v3

    if-nez v3, :cond_0

    .line 82
    invoke-virtual {p0}, Lcom/example/android_auth/LoginActivity;->onLoginFailed()V

    .line 109
    :goto_0
    return-void

    .line 86
    :cond_0
    iget-object v3, p0, Lcom/example/android_auth/LoginActivity;->_loginButton:Landroid/widget/Button;

    const/4 v4, 0x0

    invoke-virtual {v3, v4}, Landroid/widget/Button;->setEnabled(Z)V

    .line 88
    new-instance v1, Landroid/app/ProgressDialog;

    const v3, 0x7f0b00a8

    invoke-direct {v1, p0, v3}, Landroid/app/ProgressDialog;-><init>(Landroid/content/Context;I)V

    .line 90
    .local v1, "progressDialog":Landroid/app/ProgressDialog;
    const/4 v3, 0x1

    invoke-virtual {v1, v3}, Landroid/app/ProgressDialog;->setIndeterminate(Z)V

    .line 91
    const v3, 0x7f090026

    invoke-virtual {p0, v3}, Lcom/example/android_auth/LoginActivity;->getString(I)Ljava/lang/String;

    move-result-object v3

    invoke-virtual {v1, v3}, Landroid/app/ProgressDialog;->setMessage(Ljava/lang/CharSequence;)V

    .line 92
    invoke-virtual {v1}, Landroid/app/ProgressDialog;->show()V

    .line 94
    iget-object v3, p0, Lcom/example/android_auth/LoginActivity;->_usernameText:Landroid/widget/EditText;

    invoke-virtual {v3}, Landroid/widget/EditText;->getText()Landroid/text/Editable;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/Object;->toString()Ljava/lang/String;

    move-result-object v2

    .line 95
    .local v2, "username":Ljava/lang/String;
    iget-object v3, p0, Lcom/example/android_auth/LoginActivity;->_passwordText:Landroid/widget/EditText;

    invoke-virtual {v3}, Landroid/widget/EditText;->getText()Landroid/text/Editable;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/Object;->toString()Ljava/lang/String;

    move-result-object v0

    .line 98
    .local v0, "password":Ljava/lang/String;
    invoke-direct {p0, v2, v0}, Lcom/example/android_auth/LoginActivity;->doRequest(Ljava/lang/String;Ljava/lang/String;)V

    .line 100
    new-instance v3, Landroid/os/Handler;

    invoke-direct {v3}, Landroid/os/Handler;-><init>()V

    new-instance v4, Lcom/example/android_auth/LoginActivity$2;

    invoke-direct {v4, p0, v1}, Lcom/example/android_auth/LoginActivity$2;-><init>(Lcom/example/android_auth/LoginActivity;Landroid/app/ProgressDialog;)V

    const-wide/16 v6, 0xbb8

    invoke-virtual {v3, v4, v6, v7}, Landroid/os/Handler;->postDelayed(Ljava/lang/Runnable;J)Z

    goto :goto_0
.end method

.method protected onActivityResult(IILandroid/content/Intent;)V
    .locals 1
    .param p1, "requestCode"    # I
    .param p2, "resultCode"    # I
    .param p3, "data"    # Landroid/content/Intent;

    .prologue
    .line 114
    if-nez p1, :cond_0

    .line 115
    const/4 v0, -0x1

    if-ne p2, v0, :cond_0

    .line 119
    invoke-virtual {p0}, Lcom/example/android_auth/LoginActivity;->finish()V

    .line 122
    :cond_0
    return-void
.end method

.method public onBackPressed()V
    .locals 1

    .prologue
    .line 127
    const/4 v0, 0x1

    invoke-virtual {p0, v0}, Lcom/example/android_auth/LoginActivity;->moveTaskToBack(Z)Z

    .line 128
    return-void
.end method

.method public onCreate(Landroid/os/Bundle;)V
    .locals 2
    .param p1, "savedInstanceState"    # Landroid/os/Bundle;

    .prologue
    .line 57
    invoke-super {p0, p1}, Landroid/support/v7/app/AppCompatActivity;->onCreate(Landroid/os/Bundle;)V

    .line 58
    const v0, 0x7f04001b

    invoke-virtual {p0, v0}, Lcom/example/android_auth/LoginActivity;->setContentView(I)V

    .line 60
    const v0, 0x7f0f0073

    invoke-virtual {p0, v0}, Lcom/example/android_auth/LoginActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/EditText;

    iput-object v0, p0, Lcom/example/android_auth/LoginActivity;->_usernameText:Landroid/widget/EditText;

    .line 61
    const v0, 0x7f0f0074

    invoke-virtual {p0, v0}, Lcom/example/android_auth/LoginActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/EditText;

    iput-object v0, p0, Lcom/example/android_auth/LoginActivity;->_passwordText:Landroid/widget/EditText;

    .line 62
    const v0, 0x7f0f0075

    invoke-virtual {p0, v0}, Lcom/example/android_auth/LoginActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/Button;

    iput-object v0, p0, Lcom/example/android_auth/LoginActivity;->_loginButton:Landroid/widget/Button;

    .line 64
    iget-object v0, p0, Lcom/example/android_auth/LoginActivity;->_usernameText:Landroid/widget/EditText;

    const v1, 0x7f090029

    invoke-virtual {p0, v1}, Lcom/example/android_auth/LoginActivity;->getString(I)Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v0, v1}, Landroid/widget/EditText;->setText(Ljava/lang/CharSequence;)V

    .line 66
    iget-object v0, p0, Lcom/example/android_auth/LoginActivity;->_loginButton:Landroid/widget/Button;

    new-instance v1, Lcom/example/android_auth/LoginActivity$1;

    invoke-direct {v1, p0}, Lcom/example/android_auth/LoginActivity$1;-><init>(Lcom/example/android_auth/LoginActivity;)V

    invoke-virtual {v0, v1}, Landroid/widget/Button;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 75
    invoke-static {p0}, Lcom/example/android_auth/LoginActivity;->verifyStoragePermissions(Landroid/app/Activity;)V

    .line 76
    return-void
.end method

.method public onLoginFailed()V
    .locals 3

    .prologue
    const/4 v2, 0x1

    .line 136
    invoke-virtual {p0}, Lcom/example/android_auth/LoginActivity;->getBaseContext()Landroid/content/Context;

    move-result-object v0

    const v1, 0x7f090024

    invoke-virtual {p0, v1}, Lcom/example/android_auth/LoginActivity;->getString(I)Ljava/lang/String;

    move-result-object v1

    invoke-static {v0, v1, v2}, Landroid/widget/Toast;->makeText(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;

    move-result-object v0

    invoke-virtual {v0}, Landroid/widget/Toast;->show()V

    .line 138
    iget-object v0, p0, Lcom/example/android_auth/LoginActivity;->_loginButton:Landroid/widget/Button;

    invoke-virtual {v0, v2}, Landroid/widget/Button;->setEnabled(Z)V

    .line 139
    return-void
.end method

.method public onLoginSuccess()V
    .locals 2

    .prologue
    .line 131
    iget-object v0, p0, Lcom/example/android_auth/LoginActivity;->_loginButton:Landroid/widget/Button;

    const/4 v1, 0x1

    invoke-virtual {v0, v1}, Landroid/widget/Button;->setEnabled(Z)V

    .line 132
    invoke-virtual {p0}, Lcom/example/android_auth/LoginActivity;->finish()V

    .line 133
    return-void
.end method

.method public validate()Z
    .locals 6

    .prologue
    const/4 v5, 0x0

    .line 142
    const/4 v2, 0x1

    .line 144
    .local v2, "valid":Z
    iget-object v3, p0, Lcom/example/android_auth/LoginActivity;->_usernameText:Landroid/widget/EditText;

    invoke-virtual {v3}, Landroid/widget/EditText;->getText()Landroid/text/Editable;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/Object;->toString()Ljava/lang/String;

    move-result-object v1

    .line 145
    .local v1, "username":Ljava/lang/String;
    iget-object v3, p0, Lcom/example/android_auth/LoginActivity;->_passwordText:Landroid/widget/EditText;

    invoke-virtual {v3}, Landroid/widget/EditText;->getText()Landroid/text/Editable;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/Object;->toString()Ljava/lang/String;

    move-result-object v0

    .line 147
    .local v0, "password":Ljava/lang/String;
    invoke-virtual {v1}, Ljava/lang/String;->isEmpty()Z

    move-result v3

    if-eqz v3, :cond_1

    .line 148
    iget-object v3, p0, Lcom/example/android_auth/LoginActivity;->_usernameText:Landroid/widget/EditText;

    const v4, 0x7f09002b

    invoke-virtual {p0, v4}, Lcom/example/android_auth/LoginActivity;->getString(I)Ljava/lang/String;

    move-result-object v4

    invoke-virtual {v3, v4}, Landroid/widget/EditText;->setError(Ljava/lang/CharSequence;)V

    .line 149
    const/4 v2, 0x0

    .line 154
    :goto_0
    invoke-virtual {v0}, Ljava/lang/String;->isEmpty()Z

    move-result v3

    if-nez v3, :cond_0

    invoke-virtual {v0}, Ljava/lang/String;->length()I

    move-result v3

    const/16 v4, 0xf

    if-ne v3, v4, :cond_0

    const-string v3, "[a-z]+"

    invoke-virtual {v0, v3}, Ljava/lang/String;->matches(Ljava/lang/String;)Z

    move-result v3

    if-nez v3, :cond_2

    .line 155
    :cond_0
    iget-object v3, p0, Lcom/example/android_auth/LoginActivity;->_passwordText:Landroid/widget/EditText;

    const v4, 0x7f09002a

    invoke-virtual {p0, v4}, Lcom/example/android_auth/LoginActivity;->getString(I)Ljava/lang/String;

    move-result-object v4

    invoke-virtual {v3, v4}, Landroid/widget/EditText;->setError(Ljava/lang/CharSequence;)V

    .line 156
    const/4 v2, 0x0

    .line 161
    :goto_1
    return v2

    .line 151
    :cond_1
    iget-object v3, p0, Lcom/example/android_auth/LoginActivity;->_usernameText:Landroid/widget/EditText;

    invoke-virtual {v3, v5}, Landroid/widget/EditText;->setError(Ljava/lang/CharSequence;)V

    goto :goto_0

    .line 158
    :cond_2
    iget-object v3, p0, Lcom/example/android_auth/LoginActivity;->_passwordText:Landroid/widget/EditText;

    invoke-virtual {v3, v5}, Landroid/widget/EditText;->setError(Ljava/lang/CharSequence;)V

    goto :goto_1
.end method
