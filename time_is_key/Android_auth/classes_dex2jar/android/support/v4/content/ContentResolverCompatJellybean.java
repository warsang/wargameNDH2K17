package android.support.v4.content;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.CancellationSignal;
import android.os.OperationCanceledException;
import android.support.annotation.RequiresApi;

@TargetApi(16)
@RequiresApi(16)
class ContentResolverCompatJellybean
{
  ContentResolverCompatJellybean() {}
  
  static boolean isFrameworkOperationCanceledException(Exception paramException)
  {
    return paramException instanceof OperationCanceledException;
  }
  
  public static Cursor query(ContentResolver paramContentResolver, Uri paramUri, String[] paramArrayOfString1, String paramString1, String[] paramArrayOfString2, String paramString2, Object paramObject)
  {
    return paramContentResolver.query(paramUri, paramArrayOfString1, paramString1, paramArrayOfString2, paramString2, (CancellationSignal)paramObject);
  }
}
