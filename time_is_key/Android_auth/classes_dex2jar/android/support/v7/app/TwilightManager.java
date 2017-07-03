package android.support.v7.app;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import java.util.Calendar;

class TwilightManager
{
  private static final int SUNRISE = 6;
  private static final int SUNSET = 22;
  private static final String TAG = "TwilightManager";
  private static TwilightManager sInstance;
  private final Context mContext;
  private final LocationManager mLocationManager;
  private final TwilightState mTwilightState = new TwilightState();
  
  @VisibleForTesting
  TwilightManager(@NonNull Context paramContext, @NonNull LocationManager paramLocationManager)
  {
    this.mContext = paramContext;
    this.mLocationManager = paramLocationManager;
  }
  
  static TwilightManager getInstance(@NonNull Context paramContext)
  {
    if (sInstance == null)
    {
      Context localContext = paramContext.getApplicationContext();
      sInstance = new TwilightManager(localContext, (LocationManager)localContext.getSystemService("location"));
    }
    return sInstance;
  }
  
  private Location getLastKnownLocation()
  {
    int i = PermissionChecker.checkSelfPermission(this.mContext, "android.permission.ACCESS_COARSE_LOCATION");
    Location localLocation1 = null;
    if (i == 0) {
      localLocation1 = getLastKnownLocationForProvider("network");
    }
    int j = PermissionChecker.checkSelfPermission(this.mContext, "android.permission.ACCESS_FINE_LOCATION");
    Location localLocation2 = null;
    if (j == 0) {
      localLocation2 = getLastKnownLocationForProvider("gps");
    }
    if ((localLocation2 != null) && (localLocation1 != null)) {
      if (localLocation2.getTime() <= localLocation1.getTime()) {}
    }
    while (localLocation2 != null)
    {
      return localLocation2;
      return localLocation1;
    }
    return localLocation1;
  }
  
  private Location getLastKnownLocationForProvider(String paramString)
  {
    if (this.mLocationManager != null) {
      try
      {
        if (this.mLocationManager.isProviderEnabled(paramString))
        {
          Location localLocation = this.mLocationManager.getLastKnownLocation(paramString);
          return localLocation;
        }
      }
      catch (Exception localException)
      {
        Log.d("TwilightManager", "Failed to get last known location", localException);
      }
    }
    return null;
  }
  
  private boolean isStateValid()
  {
    return (this.mTwilightState != null) && (this.mTwilightState.nextUpdate > System.currentTimeMillis());
  }
  
  @VisibleForTesting
  static void setInstance(TwilightManager paramTwilightManager)
  {
    sInstance = paramTwilightManager;
  }
  
  private void updateState(@NonNull Location paramLocation)
  {
    TwilightState localTwilightState = this.mTwilightState;
    long l1 = System.currentTimeMillis();
    TwilightCalculator localTwilightCalculator = TwilightCalculator.getInstance();
    localTwilightCalculator.calculateTwilight(l1 - 86400000L, paramLocation.getLatitude(), paramLocation.getLongitude());
    long l2 = localTwilightCalculator.sunset;
    localTwilightCalculator.calculateTwilight(l1, paramLocation.getLatitude(), paramLocation.getLongitude());
    if (localTwilightCalculator.state == 1) {}
    long l3;
    long l4;
    long l5;
    long l6;
    for (boolean bool = true;; bool = false)
    {
      l3 = localTwilightCalculator.sunrise;
      l4 = localTwilightCalculator.sunset;
      localTwilightCalculator.calculateTwilight(86400000L + l1, paramLocation.getLatitude(), paramLocation.getLongitude());
      l5 = localTwilightCalculator.sunrise;
      if ((l3 != -1L) && (l4 != -1L)) {
        break;
      }
      l6 = l1 + 43200000L;
      localTwilightState.isNight = bool;
      localTwilightState.yesterdaySunset = l2;
      localTwilightState.todaySunrise = l3;
      localTwilightState.todaySunset = l4;
      localTwilightState.tomorrowSunrise = l5;
      localTwilightState.nextUpdate = l6;
      return;
    }
    long l7;
    if (l1 > l4) {
      l7 = 0L + l5;
    }
    for (;;)
    {
      l6 = l7 + 60000L;
      break;
      if (l1 > l3) {
        l7 = 0L + l4;
      } else {
        l7 = 0L + l3;
      }
    }
  }
  
  boolean isNight()
  {
    TwilightState localTwilightState = this.mTwilightState;
    if (isStateValid()) {
      return localTwilightState.isNight;
    }
    Location localLocation = getLastKnownLocation();
    if (localLocation != null)
    {
      updateState(localLocation);
      return localTwilightState.isNight;
    }
    Log.i("TwilightManager", "Could not get last known location. This is probably because the app does not have any location permissions. Falling back to hardcoded sunrise/sunset values.");
    int i = Calendar.getInstance().get(11);
    return (i < 6) || (i >= 22);
  }
  
  private static class TwilightState
  {
    boolean isNight;
    long nextUpdate;
    long todaySunrise;
    long todaySunset;
    long tomorrowSunrise;
    long yesterdaySunset;
    
    TwilightState() {}
  }
}
