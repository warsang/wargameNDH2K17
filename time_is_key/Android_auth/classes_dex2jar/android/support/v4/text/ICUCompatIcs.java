package android.support.v4.text;

import android.annotation.TargetApi;
import android.support.annotation.RequiresApi;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

@TargetApi(14)
@RequiresApi(14)
class ICUCompatIcs
{
  private static final String TAG = "ICUCompatIcs";
  private static Method sAddLikelySubtagsMethod;
  private static Method sGetScriptMethod;
  
  static
  {
    try
    {
      Class localClass = Class.forName("libcore.icu.ICU");
      if (localClass != null)
      {
        sGetScriptMethod = localClass.getMethod("getScript", new Class[] { String.class });
        sAddLikelySubtagsMethod = localClass.getMethod("addLikelySubtags", new Class[] { String.class });
      }
      return;
    }
    catch (Exception localException)
    {
      sGetScriptMethod = null;
      sAddLikelySubtagsMethod = null;
      Log.w("ICUCompatIcs", localException);
    }
  }
  
  ICUCompatIcs() {}
  
  private static String addLikelySubtags(Locale paramLocale)
  {
    String str1 = paramLocale.toString();
    try
    {
      if (sAddLikelySubtagsMethod != null)
      {
        Object[] arrayOfObject = { str1 };
        String str2 = (String)sAddLikelySubtagsMethod.invoke(null, arrayOfObject);
        return str2;
      }
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      Log.w("ICUCompatIcs", localIllegalAccessException);
      return str1;
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      for (;;)
      {
        Log.w("ICUCompatIcs", localInvocationTargetException);
      }
    }
  }
  
  private static String getScript(String paramString)
  {
    try
    {
      if (sGetScriptMethod != null)
      {
        Object[] arrayOfObject = { paramString };
        String str = (String)sGetScriptMethod.invoke(null, arrayOfObject);
        return str;
      }
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      Log.w("ICUCompatIcs", localIllegalAccessException);
      return null;
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      for (;;)
      {
        Log.w("ICUCompatIcs", localInvocationTargetException);
      }
    }
  }
  
  public static String maximizeAndGetScript(Locale paramLocale)
  {
    String str = addLikelySubtags(paramLocale);
    if (str != null) {
      return getScript(str);
    }
    return null;
  }
}
