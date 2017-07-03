package android.support.transition;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Map;

@TargetApi(14)
@RequiresApi(14)
abstract class VisibilityPort
  extends TransitionPort
{
  private static final String PROPNAME_PARENT = "android:visibility:parent";
  private static final String PROPNAME_VISIBILITY = "android:visibility:visibility";
  private static final String[] sTransitionProperties = { "android:visibility:visibility", "android:visibility:parent" };
  
  VisibilityPort() {}
  
  private void captureValues(TransitionValues paramTransitionValues)
  {
    int i = paramTransitionValues.view.getVisibility();
    paramTransitionValues.values.put("android:visibility:visibility", Integer.valueOf(i));
    paramTransitionValues.values.put("android:visibility:parent", paramTransitionValues.view.getParent());
  }
  
  private VisibilityInfo getVisibilityChangeInfo(TransitionValues paramTransitionValues1, TransitionValues paramTransitionValues2)
  {
    VisibilityInfo localVisibilityInfo = new VisibilityInfo();
    localVisibilityInfo.visibilityChange = false;
    localVisibilityInfo.fadeIn = false;
    if (paramTransitionValues1 != null)
    {
      localVisibilityInfo.startVisibility = ((Integer)paramTransitionValues1.values.get("android:visibility:visibility")).intValue();
      localVisibilityInfo.startParent = ((ViewGroup)paramTransitionValues1.values.get("android:visibility:parent"));
      if (paramTransitionValues2 == null) {
        break label149;
      }
      localVisibilityInfo.endVisibility = ((Integer)paramTransitionValues2.values.get("android:visibility:visibility")).intValue();
      localVisibilityInfo.endParent = ((ViewGroup)paramTransitionValues2.values.get("android:visibility:parent"));
      label104:
      if ((paramTransitionValues1 == null) || (paramTransitionValues2 == null)) {
        break label190;
      }
      if ((localVisibilityInfo.startVisibility != localVisibilityInfo.endVisibility) || (localVisibilityInfo.startParent != localVisibilityInfo.endParent)) {
        break label162;
      }
    }
    label149:
    label162:
    label190:
    do
    {
      return localVisibilityInfo;
      localVisibilityInfo.startVisibility = -1;
      localVisibilityInfo.startParent = null;
      break;
      localVisibilityInfo.endVisibility = -1;
      localVisibilityInfo.endParent = null;
      break label104;
      if (localVisibilityInfo.startVisibility != localVisibilityInfo.endVisibility) {
        if (localVisibilityInfo.startVisibility == 0)
        {
          localVisibilityInfo.fadeIn = false;
          localVisibilityInfo.visibilityChange = true;
        }
      }
      while (paramTransitionValues1 == null)
      {
        localVisibilityInfo.fadeIn = true;
        localVisibilityInfo.visibilityChange = true;
        return localVisibilityInfo;
        if (localVisibilityInfo.endVisibility == 0)
        {
          localVisibilityInfo.fadeIn = true;
          localVisibilityInfo.visibilityChange = true;
          continue;
          if (localVisibilityInfo.startParent != localVisibilityInfo.endParent) {
            if (localVisibilityInfo.endParent == null)
            {
              localVisibilityInfo.fadeIn = false;
              localVisibilityInfo.visibilityChange = true;
            }
            else if (localVisibilityInfo.startParent == null)
            {
              localVisibilityInfo.fadeIn = true;
              localVisibilityInfo.visibilityChange = true;
            }
          }
        }
      }
    } while (paramTransitionValues2 != null);
    localVisibilityInfo.fadeIn = false;
    localVisibilityInfo.visibilityChange = true;
    return localVisibilityInfo;
  }
  
  public void captureEndValues(TransitionValues paramTransitionValues)
  {
    captureValues(paramTransitionValues);
  }
  
  public void captureStartValues(TransitionValues paramTransitionValues)
  {
    captureValues(paramTransitionValues);
  }
  
  public Animator createAnimator(ViewGroup paramViewGroup, TransitionValues paramTransitionValues1, TransitionValues paramTransitionValues2)
  {
    int i = -1;
    VisibilityInfo localVisibilityInfo = getVisibilityChangeInfo(paramTransitionValues1, paramTransitionValues2);
    boolean bool = localVisibilityInfo.visibilityChange;
    Animator localAnimator = null;
    View localView1;
    View localView2;
    label73:
    int j;
    if (bool) {
      if (this.mTargets.size() <= 0)
      {
        int m = this.mTargetIds.size();
        k = 0;
        if (m <= 0) {}
      }
      else
      {
        if (paramTransitionValues1 == null) {
          break label182;
        }
        localView1 = paramTransitionValues1.view;
        if (paramTransitionValues2 == null) {
          break label188;
        }
        localView2 = paramTransitionValues2.view;
        if (localView1 == null) {
          break label194;
        }
        j = localView1.getId();
        label85:
        if (localView2 != null) {
          i = localView2.getId();
        }
        if ((!isValidTarget(localView1, j)) && (!isValidTarget(localView2, i))) {
          break label201;
        }
      }
    }
    label182:
    label188:
    label194:
    label201:
    for (int k = 1;; k = 0)
    {
      if ((k == 0) && (localVisibilityInfo.startParent == null))
      {
        ViewGroup localViewGroup = localVisibilityInfo.endParent;
        localAnimator = null;
        if (localViewGroup == null) {}
      }
      else
      {
        if (!localVisibilityInfo.fadeIn) {
          break label207;
        }
        localAnimator = onAppear(paramViewGroup, paramTransitionValues1, localVisibilityInfo.startVisibility, paramTransitionValues2, localVisibilityInfo.endVisibility);
      }
      return localAnimator;
      localView1 = null;
      break;
      localView2 = null;
      break label73;
      j = i;
      break label85;
    }
    label207:
    return onDisappear(paramViewGroup, paramTransitionValues1, localVisibilityInfo.startVisibility, paramTransitionValues2, localVisibilityInfo.endVisibility);
  }
  
  public String[] getTransitionProperties()
  {
    return sTransitionProperties;
  }
  
  public boolean isVisible(TransitionValues paramTransitionValues)
  {
    if (paramTransitionValues == null) {
      return false;
    }
    int i = ((Integer)paramTransitionValues.values.get("android:visibility:visibility")).intValue();
    View localView = (View)paramTransitionValues.values.get("android:visibility:parent");
    if ((i == 0) && (localView != null)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public Animator onAppear(ViewGroup paramViewGroup, TransitionValues paramTransitionValues1, int paramInt1, TransitionValues paramTransitionValues2, int paramInt2)
  {
    return null;
  }
  
  public Animator onDisappear(ViewGroup paramViewGroup, TransitionValues paramTransitionValues1, int paramInt1, TransitionValues paramTransitionValues2, int paramInt2)
  {
    return null;
  }
  
  private static class VisibilityInfo
  {
    ViewGroup endParent;
    int endVisibility;
    boolean fadeIn;
    ViewGroup startParent;
    int startVisibility;
    boolean visibilityChange;
    
    VisibilityInfo() {}
  }
}
