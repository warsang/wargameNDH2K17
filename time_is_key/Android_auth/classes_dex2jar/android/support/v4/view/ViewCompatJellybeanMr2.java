package android.support.v4.view;

import android.annotation.TargetApi;
import android.graphics.Rect;
import android.support.annotation.RequiresApi;
import android.view.View;

@TargetApi(18)
@RequiresApi(18)
class ViewCompatJellybeanMr2
{
  ViewCompatJellybeanMr2() {}
  
  public static Rect getClipBounds(View paramView)
  {
    return paramView.getClipBounds();
  }
  
  public static boolean isInLayout(View paramView)
  {
    return paramView.isInLayout();
  }
  
  public static void setClipBounds(View paramView, Rect paramRect)
  {
    paramView.setClipBounds(paramRect);
  }
}
