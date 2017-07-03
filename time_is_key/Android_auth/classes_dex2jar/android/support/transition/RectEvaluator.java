package android.support.transition;

import android.animation.TypeEvaluator;
import android.annotation.TargetApi;
import android.graphics.Rect;
import android.support.annotation.RequiresApi;

@TargetApi(14)
@RequiresApi(14)
class RectEvaluator
  implements TypeEvaluator<Rect>
{
  private Rect mRect;
  
  public RectEvaluator() {}
  
  public RectEvaluator(Rect paramRect)
  {
    this.mRect = paramRect;
  }
  
  public Rect evaluate(float paramFloat, Rect paramRect1, Rect paramRect2)
  {
    int i = paramRect1.left + (int)(paramFloat * (paramRect2.left - paramRect1.left));
    int j = paramRect1.top + (int)(paramFloat * (paramRect2.top - paramRect1.top));
    int k = paramRect1.right + (int)(paramFloat * (paramRect2.right - paramRect1.right));
    int m = paramRect1.bottom + (int)(paramFloat * (paramRect2.bottom - paramRect1.bottom));
    if (this.mRect == null) {
      return new Rect(i, j, k, m);
    }
    this.mRect.set(i, j, k, m);
    return this.mRect;
  }
}
