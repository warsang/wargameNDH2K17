package android.support.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import java.util.Map;

@TargetApi(14)
@RequiresApi(14)
class FadePort
  extends VisibilityPort
{
  private static boolean DBG = false;
  public static final int IN = 1;
  private static final String LOG_TAG = "Fade";
  public static final int OUT = 2;
  private static final String PROPNAME_SCREEN_X = "android:fade:screenX";
  private static final String PROPNAME_SCREEN_Y = "android:fade:screenY";
  private int mFadingMode;
  
  public FadePort()
  {
    this(3);
  }
  
  public FadePort(int paramInt)
  {
    this.mFadingMode = paramInt;
  }
  
  private void captureValues(TransitionValues paramTransitionValues)
  {
    int[] arrayOfInt = new int[2];
    paramTransitionValues.view.getLocationOnScreen(arrayOfInt);
    paramTransitionValues.values.put("android:fade:screenX", Integer.valueOf(arrayOfInt[0]));
    paramTransitionValues.values.put("android:fade:screenY", Integer.valueOf(arrayOfInt[1]));
  }
  
  private Animator createAnimation(View paramView, float paramFloat1, float paramFloat2, AnimatorListenerAdapter paramAnimatorListenerAdapter)
  {
    Object localObject;
    if (paramFloat1 == paramFloat2)
    {
      localObject = null;
      if (paramAnimatorListenerAdapter != null) {
        paramAnimatorListenerAdapter.onAnimationEnd(null);
      }
    }
    do
    {
      return localObject;
      localObject = ObjectAnimator.ofFloat(paramView, "alpha", new float[] { paramFloat1, paramFloat2 });
      if (DBG) {
        Log.d("Fade", "Created animator " + localObject);
      }
    } while (paramAnimatorListenerAdapter == null);
    ((ObjectAnimator)localObject).addListener(paramAnimatorListenerAdapter);
    return localObject;
  }
  
  public void captureStartValues(TransitionValues paramTransitionValues)
  {
    super.captureStartValues(paramTransitionValues);
    captureValues(paramTransitionValues);
  }
  
  public Animator onAppear(ViewGroup paramViewGroup, TransitionValues paramTransitionValues1, int paramInt1, TransitionValues paramTransitionValues2, int paramInt2)
  {
    if (((0x1 & this.mFadingMode) != 1) || (paramTransitionValues2 == null)) {
      return null;
    }
    final View localView1 = paramTransitionValues2.view;
    if (DBG) {
      if (paramTransitionValues1 == null) {
        break label126;
      }
    }
    label126:
    for (View localView2 = paramTransitionValues1.view;; localView2 = null)
    {
      Log.d("Fade", "Fade.onAppear: startView, startVis, endView, endVis = " + localView2 + ", " + paramInt1 + ", " + localView1 + ", " + paramInt2);
      localView1.setAlpha(0.0F);
      addListener(new TransitionPort.TransitionListenerAdapter()
      {
        boolean mCanceled = false;
        float mPausedAlpha;
        
        public void onTransitionCancel(TransitionPort paramAnonymousTransitionPort)
        {
          localView1.setAlpha(1.0F);
          this.mCanceled = true;
        }
        
        public void onTransitionEnd(TransitionPort paramAnonymousTransitionPort)
        {
          if (!this.mCanceled) {
            localView1.setAlpha(1.0F);
          }
        }
        
        public void onTransitionPause(TransitionPort paramAnonymousTransitionPort)
        {
          this.mPausedAlpha = localView1.getAlpha();
          localView1.setAlpha(1.0F);
        }
        
        public void onTransitionResume(TransitionPort paramAnonymousTransitionPort)
        {
          localView1.setAlpha(this.mPausedAlpha);
        }
      });
      return createAnimation(localView1, 0.0F, 1.0F, null);
    }
  }
  
  public Animator onDisappear(final ViewGroup paramViewGroup, TransitionValues paramTransitionValues1, int paramInt1, TransitionValues paramTransitionValues2, final int paramInt2)
  {
    if ((0x2 & this.mFadingMode) != 2) {
      return null;
    }
    View localView1;
    View localView2;
    label34:
    Object localObject1;
    Object localObject2;
    View localView3;
    if (paramTransitionValues1 != null)
    {
      localView1 = paramTransitionValues1.view;
      if (paramTransitionValues2 == null) {
        break label267;
      }
      localView2 = paramTransitionValues2.view;
      if (DBG) {
        Log.d("Fade", "Fade.onDisappear: startView, startVis, endView, endVis = " + localView1 + ", " + paramInt1 + ", " + localView2 + ", " + paramInt2);
      }
      localObject1 = null;
      if ((localView2 != null) && (localView2.getParent() != null)) {
        break label443;
      }
      if (localView2 == null) {
        break label273;
      }
      localObject2 = localView2;
      localView3 = localView2;
    }
    for (;;)
    {
      if (localObject2 == null) {
        break label498;
      }
      int j = ((Integer)paramTransitionValues1.values.get("android:fade:screenX")).intValue();
      int k = ((Integer)paramTransitionValues1.values.get("android:fade:screenY")).intValue();
      int[] arrayOfInt = new int[2];
      paramViewGroup.getLocationOnScreen(arrayOfInt);
      ViewCompat.offsetLeftAndRight((View)localObject2, j - arrayOfInt[0] - ((View)localObject2).getLeft());
      ViewCompat.offsetTopAndBottom((View)localObject2, k - arrayOfInt[1] - ((View)localObject2).getTop());
      ViewGroupOverlay.createFrom(paramViewGroup).add((View)localObject2);
      final View localView6 = localView3;
      final Object localObject4 = localObject2;
      AnimatorListenerAdapter local2 = new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          localView6.setAlpha(1.0F);
          if (this.val$finalViewToKeep != null) {
            this.val$finalViewToKeep.setVisibility(paramInt2);
          }
          if (localObject4 != null) {
            ViewGroupOverlay.createFrom(paramViewGroup).remove(localObject4);
          }
        }
      };
      return createAnimation(localView3, 1.0F, 0.0F, local2);
      localView1 = null;
      break;
      label267:
      localView2 = null;
      break label34;
      label273:
      localObject2 = null;
      localView3 = null;
      localObject1 = null;
      if (localView1 != null) {
        if (localView1.getParent() == null)
        {
          localObject2 = localView1;
          localView3 = localView1;
          localObject1 = null;
        }
        else
        {
          boolean bool1 = localView1.getParent() instanceof View;
          localObject2 = null;
          localView3 = null;
          localObject1 = null;
          if (bool1)
          {
            ViewParent localViewParent = localView1.getParent().getParent();
            localObject2 = null;
            localView3 = null;
            localObject1 = null;
            if (localViewParent == null)
            {
              int i = ((View)localView1.getParent()).getId();
              localObject2 = null;
              localView3 = null;
              localObject1 = null;
              if (i != -1)
              {
                View localView4 = paramViewGroup.findViewById(i);
                localObject2 = null;
                localView3 = null;
                localObject1 = null;
                if (localView4 != null)
                {
                  boolean bool2 = this.mCanRemoveViews;
                  localObject2 = null;
                  localView3 = null;
                  localObject1 = null;
                  if (bool2)
                  {
                    localObject2 = localView1;
                    localView3 = localView1;
                    localObject1 = null;
                    continue;
                    label443:
                    if (paramInt2 == 4)
                    {
                      localView3 = localView2;
                      localObject1 = localView3;
                      localObject2 = null;
                    }
                    else if (localView1 == localView2)
                    {
                      localView3 = localView2;
                      localObject1 = localView3;
                      localObject2 = null;
                    }
                    else
                    {
                      localView3 = localView1;
                      localObject2 = localView3;
                      localObject1 = null;
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    label498:
    if (localObject1 != null)
    {
      ((View)localObject1).setVisibility(0);
      final View localView5 = localView3;
      final Object localObject3 = localObject2;
      AnimatorListenerAdapter local3 = new AnimatorListenerAdapter()
      {
        boolean mCanceled = false;
        float mPausedAlpha = -1.0F;
        
        public void onAnimationCancel(Animator paramAnonymousAnimator)
        {
          this.mCanceled = true;
          if (this.mPausedAlpha >= 0.0F) {
            localView5.setAlpha(this.mPausedAlpha);
          }
        }
        
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          if (!this.mCanceled) {
            localView5.setAlpha(1.0F);
          }
          if ((this.val$finalViewToKeep != null) && (!this.mCanceled)) {
            this.val$finalViewToKeep.setVisibility(paramInt2);
          }
          if (localObject3 != null) {
            ViewGroupOverlay.createFrom(paramViewGroup).add(localObject3);
          }
        }
      };
      return createAnimation(localView3, 1.0F, 0.0F, local3);
    }
    return null;
  }
}
