package android.support.v4.os;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.UserManager;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;

@TargetApi(24)
@RequiresApi(24)
@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public class UserManagerCompatApi24
{
  public UserManagerCompatApi24() {}
  
  public static boolean isUserUnlocked(Context paramContext)
  {
    return ((UserManager)paramContext.getSystemService(UserManager.class)).isUserUnlocked();
  }
}
