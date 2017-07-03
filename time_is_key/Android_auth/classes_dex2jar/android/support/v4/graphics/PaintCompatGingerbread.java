package android.support.v4.graphics;

import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.util.Pair;

@RequiresApi(9)
class PaintCompatGingerbread
{
  private static final String TOFU_STRING = "󟿽";
  private static final ThreadLocal<Pair<Rect, Rect>> sRectThreadLocal = new ThreadLocal();
  
  PaintCompatGingerbread() {}
  
  static boolean hasGlyph(@NonNull Paint paramPaint, @NonNull String paramString)
  {
    int i = paramString.length();
    boolean bool2;
    if ((i == 1) && (Character.isWhitespace(paramString.charAt(0)))) {
      bool2 = true;
    }
    float f1;
    float f2;
    boolean bool5;
    do
    {
      boolean bool4;
      do
      {
        boolean bool1;
        do
        {
          return bool2;
          f1 = paramPaint.measureText("󟿽");
          f2 = paramPaint.measureText(paramString);
          bool1 = f2 < 0.0F;
          bool2 = false;
        } while (!bool1);
        if (paramString.codePointCount(0, paramString.length()) <= 1) {
          break;
        }
        bool4 = f2 < 2.0F * f1;
        bool2 = false;
      } while (bool4);
      float f3 = 0.0F;
      int j = 0;
      while (j < i)
      {
        int k = Character.charCount(paramString.codePointAt(j));
        f3 += paramPaint.measureText(paramString, j, j + k);
        j += k;
      }
      bool5 = f2 < f3;
      bool2 = false;
    } while (!bool5);
    if (f2 != f1) {
      return true;
    }
    Pair localPair = obtainEmptyRects();
    paramPaint.getTextBounds("󟿽", 0, "󟿽".length(), (Rect)localPair.first);
    paramPaint.getTextBounds(paramString, 0, i, (Rect)localPair.second);
    if (!((Rect)localPair.first).equals(localPair.second)) {}
    for (boolean bool3 = true;; bool3 = false) {
      return bool3;
    }
  }
  
  private static Pair<Rect, Rect> obtainEmptyRects()
  {
    Pair localPair1 = (Pair)sRectThreadLocal.get();
    if (localPair1 == null)
    {
      Pair localPair2 = new Pair(new Rect(), new Rect());
      sRectThreadLocal.set(localPair2);
      return localPair2;
    }
    ((Rect)localPair1.first).setEmpty();
    ((Rect)localPair1.second).setEmpty();
    return localPair1;
  }
}
