.class Landroid/support/v4/app/ActivityOptionsCompat$ActivityOptionsImpl21;
.super Landroid/support/v4/app/ActivityOptionsCompat;
.source "ActivityOptionsCompat.java"


# annotations
.annotation build Landroid/annotation/TargetApi;
    value = 0x15
.end annotation

.annotation build Landroid/support/annotation/RequiresApi;
    value = 0x15
.end annotation

.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/app/ActivityOptionsCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0xa
    name = "ActivityOptionsImpl21"
.end annotation


# instance fields
.field private final mImpl:Landroid/support/v4/app/ActivityOptionsCompat21;


# direct methods
.method constructor <init>(Landroid/support/v4/app/ActivityOptionsCompat21;)V
    .locals 0
    .param p1, "impl"    # Landroid/support/v4/app/ActivityOptionsCompat21;

    .prologue
    .line 339
    invoke-direct {p0}, Landroid/support/v4/app/ActivityOptionsCompat;-><init>()V

    .line 340
    iput-object p1, p0, Landroid/support/v4/app/ActivityOptionsCompat$ActivityOptionsImpl21;->mImpl:Landroid/support/v4/app/ActivityOptionsCompat21;

    .line 341
    return-void
.end method


# virtual methods
.method public toBundle()Landroid/os/Bundle;
    .locals 1

    .prologue
    .line 345
    iget-object v0, p0, Landroid/support/v4/app/ActivityOptionsCompat$ActivityOptionsImpl21;->mImpl:Landroid/support/v4/app/ActivityOptionsCompat21;

    invoke-virtual {v0}, Landroid/support/v4/app/ActivityOptionsCompat21;->toBundle()Landroid/os/Bundle;

    move-result-object v0

    return-object v0
.end method

.method public update(Landroid/support/v4/app/ActivityOptionsCompat;)V
    .locals 3
    .param p1, "otherOptions"    # Landroid/support/v4/app/ActivityOptionsCompat;

    .prologue
    .line 350
    instance-of v1, p1, Landroid/support/v4/app/ActivityOptionsCompat$ActivityOptionsImpl21;

    if-eqz v1, :cond_0

    move-object v0, p1

    .line 352
    check-cast v0, Landroid/support/v4/app/ActivityOptionsCompat$ActivityOptionsImpl21;

    .line 353
    .local v0, "otherImpl":Landroid/support/v4/app/ActivityOptionsCompat$ActivityOptionsImpl21;
    iget-object v1, p0, Landroid/support/v4/app/ActivityOptionsCompat$ActivityOptionsImpl21;->mImpl:Landroid/support/v4/app/ActivityOptionsCompat21;

    iget-object v2, v0, Landroid/support/v4/app/ActivityOptionsCompat$ActivityOptionsImpl21;->mImpl:Landroid/support/v4/app/ActivityOptionsCompat21;

    invoke-virtual {v1, v2}, Landroid/support/v4/app/ActivityOptionsCompat21;->update(Landroid/support/v4/app/ActivityOptionsCompat21;)V

    .line 355
    .end local v0    # "otherImpl":Landroid/support/v4/app/ActivityOptionsCompat$ActivityOptionsImpl21;
    :cond_0
    return-void
.end method
