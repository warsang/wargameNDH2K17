.class Lcom/example/android_auth/LoginActivity$1;
.super Ljava/lang/Object;
.source "LoginActivity.java"

# interfaces
.implements Landroid/view/View$OnClickListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Lcom/example/android_auth/LoginActivity;->onCreate(Landroid/os/Bundle;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Lcom/example/android_auth/LoginActivity;


# direct methods
.method constructor <init>(Lcom/example/android_auth/LoginActivity;)V
    .locals 0
    .param p1, "this$0"    # Lcom/example/android_auth/LoginActivity;

    .prologue
    .line 66
    iput-object p1, p0, Lcom/example/android_auth/LoginActivity$1;->this$0:Lcom/example/android_auth/LoginActivity;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onClick(Landroid/view/View;)V
    .locals 1
    .param p1, "v"    # Landroid/view/View;

    .prologue
    .line 70
    iget-object v0, p0, Lcom/example/android_auth/LoginActivity$1;->this$0:Lcom/example/android_auth/LoginActivity;

    invoke-virtual {v0}, Lcom/example/android_auth/LoginActivity;->login()V

    .line 71
    return-void
.end method
