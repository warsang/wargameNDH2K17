package android.support.v4.view;

import android.annotation.TargetApi;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.Interpolator;

@TargetApi(18)
@RequiresApi(18)
class ViewPropertyAnimatorCompatJellybeanMr2
{
  ViewPropertyAnimatorCompatJellybeanMr2() {}
  
  public static Interpolator getInterpolator(View paramView)
  {
    return (Interpolator)paramView.animate().getInterpolator();
  }
}
