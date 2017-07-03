.class public Lcom/example/android_auth/MainActivity;
.super Landroid/support/v7/app/ActionBarActivity;
.source "MainActivity.java"


# direct methods
.method public constructor <init>()V
    .locals 0

    .prologue
    .line 8
    invoke-direct {p0}, Landroid/support/v7/app/ActionBarActivity;-><init>()V

    return-void
.end method


# virtual methods
.method protected onCreate(Landroid/os/Bundle;)V
    .locals 2
    .param p1, "savedInstanceState"    # Landroid/os/Bundle;

    .prologue
    .line 12
    invoke-super {p0, p1}, Landroid/support/v7/app/ActionBarActivity;->onCreate(Landroid/os/Bundle;)V

    .line 13
    const v1, 0x7f04001c

    invoke-virtual {p0, v1}, Lcom/example/android_auth/MainActivity;->setContentView(I)V

    .line 16
    new-instance v0, Landroid/content/Intent;

    const-class v1, Lcom/example/android_auth/LoginActivity;

    invoke-direct {v0, p0, v1}, Landroid/content/Intent;-><init>(Landroid/content/Context;Ljava/lang/Class;)V

    .line 17
    .local v0, "intent":Landroid/content/Intent;
    invoke-virtual {p0, v0}, Lcom/example/android_auth/MainActivity;->startActivity(Landroid/content/Intent;)V

    .line 18
    return-void
.end method
