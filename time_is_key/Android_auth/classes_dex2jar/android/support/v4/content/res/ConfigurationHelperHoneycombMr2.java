package android.support.v4.content.res;

import android.annotation.TargetApi;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

@TargetApi(13)
@RequiresApi(13)
class ConfigurationHelperHoneycombMr2
{
  ConfigurationHelperHoneycombMr2() {}
  
  static int getScreenHeightDp(@NonNull Resources paramResources)
  {
    return paramResources.getConfiguration().screenHeightDp;
  }
  
  static int getScreenWidthDp(@NonNull Resources paramResources)
  {
    return paramResources.getConfiguration().screenWidthDp;
  }
  
  static int getSmallestScreenWidthDp(@NonNull Resources paramResources)
  {
    return paramResources.getConfiguration().smallestScreenWidthDp;
  }
}
