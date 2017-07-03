.class Lcom/example/android_auth/LoginActivity$2;
.super Ljava/lang/Object;
.source "LoginActivity.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/example/android_auth/LoginActivity;->login()V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/example/android_auth/LoginActivity;

.field final synthetic val$progressDialog:Landroid/app/ProgressDialog;


# direct methods
.method constructor <init>(Lcom/example/android_auth/LoginActivity;Landroid/app/ProgressDialog;)V
    .locals 0
    .param p1, "this$0"    # Lcom/example/android_auth/LoginActivity;

    .prologue
    .line 101
    iput-object p1, p0, Lcom/example/android_auth/LoginActivity$2;->this$0:Lcom/example/android_auth/LoginActivity;

    iput-object p2, p0, Lcom/example/android_auth/LoginActivity$2;->val$progressDialog:Landroid/app/ProgressDialog;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .locals 1

    .prologue
    .line 104
    iget-object v0, p0, Lcom/example/android_auth/LoginActivity$2;->this$0:Lcom/example/android_auth/LoginActivity;

    invoke-virtual {v0}, Lcom/example/android_auth/LoginActivity;->onLoginFailed()V

    .line 106
    iget-object v0, p0, Lcom/example/android_auth/LoginActivity$2;->val$progressDialog:Landroid/app/ProgressDialog;

    invoke-virtual {v0}, Landroid/app/ProgressDialog;->dismiss()V

    .line 107
    return-void
.end method
