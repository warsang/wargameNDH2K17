package android.support.v4.text;

import android.os.Build.VERSION;
import java.util.Locale;

public final class ICUCompat
{
  private static final ICUCompatImpl IMPL = new ICUCompatImplBase();
  
  static
  {
    int i = Build.VERSION.SDK_INT;
    if (i >= 21)
    {
      IMPL = new ICUCompatImplLollipop();
      return;
    }
    if (i >= 14)
    {
      IMPL = new ICUCompatImplIcs();
      return;
    }
  }
  
  private ICUCompat() {}
  
  public static String maximizeAndGetScript(Locale paramLocale)
  {
    return IMPL.maximizeAndGetScript(paramLocale);
  }
  
  static abstract interface ICUCompatImpl
  {
    public abstract String maximizeAndGetScript(Locale paramLocale);
  }
  
  static class ICUCompatImplBase
    implements ICUCompat.ICUCompatImpl
  {
    ICUCompatImplBase() {}
    
    public String maximizeAndGetScript(Locale paramLocale)
    {
      return null;
    }
  }
  
  static class ICUCompatImplIcs
    implements ICUCompat.ICUCompatImpl
  {
    ICUCompatImplIcs() {}
    
    public String maximizeAndGetScript(Locale paramLocale)
    {
      return ICUCompatIcs.maximizeAndGetScript(paramLocale);
    }
  }
  
  static class ICUCompatImplLollipop
    implements ICUCompat.ICUCompatImpl
  {
    ICUCompatImplLollipop() {}
    
    public String maximizeAndGetScript(Locale paramLocale)
    {
      return ICUCompatApi23.maximizeAndGetScript(paramLocale);
    }
  }
}
