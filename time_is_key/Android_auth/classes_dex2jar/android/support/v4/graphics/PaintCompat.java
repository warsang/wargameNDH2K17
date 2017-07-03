package android.support.v4.graphics;

import android.graphics.Paint;
import android.os.Build.VERSION;
import android.support.annotation.NonNull;

public final class PaintCompat
{
  private PaintCompat() {}
  
  public static boolean hasGlyph(@NonNull Paint paramPaint, @NonNull String paramString)
  {
    if (Build.VERSION.SDK_INT >= 23) {
      return PaintCompatApi23.hasGlyph(paramPaint, paramString);
    }
    return PaintCompatGingerbread.hasGlyph(paramPaint, paramString);
  }
}
