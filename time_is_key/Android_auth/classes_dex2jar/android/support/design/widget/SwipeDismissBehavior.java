package android.support.design.widget;

import android.support.annotation.NonNull;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

public class SwipeDismissBehavior<V extends View>
  extends CoordinatorLayout.Behavior<V>
{
  private static final float DEFAULT_ALPHA_END_DISTANCE = 0.5F;
  private static final float DEFAULT_ALPHA_START_DISTANCE = 0.0F;
  private static final float DEFAULT_DRAG_DISMISS_THRESHOLD = 0.5F;
  public static final int STATE_DRAGGING = 1;
  public static final int STATE_IDLE = 0;
  public static final int STATE_SETTLING = 2;
  public static final int SWIPE_DIRECTION_ANY = 2;
  public static final int SWIPE_DIRECTION_END_TO_START = 1;
  public static final int SWIPE_DIRECTION_START_TO_END;
  float mAlphaEndSwipeDistance = 0.5F;
  float mAlphaStartSwipeDistance = 0.0F;
  private final ViewDragHelper.Callback mDragCallback = new ViewDragHelper.Callback()
  {
    private static final int INVALID_POINTER_ID = -1;
    private int mActivePointerId = -1;
    private int mOriginalCapturedViewLeft;
    
    private boolean shouldDismiss(View paramAnonymousView, float paramAnonymousFloat)
    {
      int k;
      if (paramAnonymousFloat != 0.0F) {
        if (ViewCompat.getLayoutDirection(paramAnonymousView) == 1)
        {
          k = 1;
          if (SwipeDismissBehavior.this.mSwipeDirection != 2) {
            break label36;
          }
        }
      }
      label36:
      label59:
      label67:
      label91:
      int i;
      int j;
      do
      {
        do
        {
          do
          {
            do
            {
              do
              {
                return true;
                k = 0;
                break;
                if (SwipeDismissBehavior.this.mSwipeDirection != 0) {
                  break label67;
                }
                if (k == 0) {
                  break label59;
                }
              } while (paramAnonymousFloat < 0.0F);
              return false;
            } while (paramAnonymousFloat > 0.0F);
            return false;
            if (SwipeDismissBehavior.this.mSwipeDirection != 1) {
              break label138;
            }
            if (k == 0) {
              break label91;
            }
          } while (paramAnonymousFloat > 0.0F);
          return false;
        } while (paramAnonymousFloat < 0.0F);
        return false;
        i = paramAnonymousView.getLeft() - this.mOriginalCapturedViewLeft;
        j = Math.round(paramAnonymousView.getWidth() * SwipeDismissBehavior.this.mDragDismissThreshold);
      } while (Math.abs(i) >= j);
      return false;
      label138:
      return false;
    }
    
    public int clampViewPositionHorizontal(View paramAnonymousView, int paramAnonymousInt1, int paramAnonymousInt2)
    {
      int i;
      int j;
      int k;
      if (ViewCompat.getLayoutDirection(paramAnonymousView) == 1)
      {
        i = 1;
        if (SwipeDismissBehavior.this.mSwipeDirection != 0) {
          break label78;
        }
        if (i == 0) {
          break label58;
        }
        j = this.mOriginalCapturedViewLeft - paramAnonymousView.getWidth();
        k = this.mOriginalCapturedViewLeft;
      }
      for (;;)
      {
        return SwipeDismissBehavior.clamp(j, paramAnonymousInt1, k);
        i = 0;
        break;
        label58:
        j = this.mOriginalCapturedViewLeft;
        k = this.mOriginalCapturedViewLeft + paramAnonymousView.getWidth();
        continue;
        label78:
        if (SwipeDismissBehavior.this.mSwipeDirection == 1)
        {
          if (i != 0)
          {
            j = this.mOriginalCapturedViewLeft;
            k = this.mOriginalCapturedViewLeft + paramAnonymousView.getWidth();
          }
          else
          {
            j = this.mOriginalCapturedViewLeft - paramAnonymousView.getWidth();
            k = this.mOriginalCapturedViewLeft;
          }
        }
        else
        {
          j = this.mOriginalCapturedViewLeft - paramAnonymousView.getWidth();
          k = this.mOriginalCapturedViewLeft + paramAnonymousView.getWidth();
        }
      }
    }
    
    public int clampViewPositionVertical(View paramAnonymousView, int paramAnonymousInt1, int paramAnonymousInt2)
    {
      return paramAnonymousView.getTop();
    }
    
    public int getViewHorizontalDragRange(View paramAnonymousView)
    {
      return paramAnonymousView.getWidth();
    }
    
    public void onViewCaptured(View paramAnonymousView, int paramAnonymousInt)
    {
      this.mActivePointerId = paramAnonymousInt;
      this.mOriginalCapturedViewLeft = paramAnonymousView.getLeft();
      ViewParent localViewParent = paramAnonymousView.getParent();
      if (localViewParent != null) {
        localViewParent.requestDisallowInterceptTouchEvent(true);
      }
    }
    
    public void onViewDragStateChanged(int paramAnonymousInt)
    {
      if (SwipeDismissBehavior.this.mListener != null) {
        SwipeDismissBehavior.this.mListener.onDragStateChanged(paramAnonymousInt);
      }
    }
    
    public void onViewPositionChanged(View paramAnonymousView, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
    {
      float f1 = this.mOriginalCapturedViewLeft + paramAnonymousView.getWidth() * SwipeDismissBehavior.this.mAlphaStartSwipeDistance;
      float f2 = this.mOriginalCapturedViewLeft + paramAnonymousView.getWidth() * SwipeDismissBehavior.this.mAlphaEndSwipeDistance;
      if (paramAnonymousInt1 <= f1)
      {
        ViewCompat.setAlpha(paramAnonymousView, 1.0F);
        return;
      }
      if (paramAnonymousInt1 >= f2)
      {
        ViewCompat.setAlpha(paramAnonymousView, 0.0F);
        return;
      }
      ViewCompat.setAlpha(paramAnonymousView, SwipeDismissBehavior.clamp(0.0F, 1.0F - SwipeDismissBehavior.fraction(f1, f2, paramAnonymousInt1), 1.0F));
    }
    
    public void onViewReleased(View paramAnonymousView, float paramAnonymousFloat1, float paramAnonymousFloat2)
    {
      this.mActivePointerId = -1;
      int i = paramAnonymousView.getWidth();
      int j;
      boolean bool;
      if (shouldDismiss(paramAnonymousView, paramAnonymousFloat1)) {
        if (paramAnonymousView.getLeft() < this.mOriginalCapturedViewLeft)
        {
          j = this.mOriginalCapturedViewLeft - i;
          bool = true;
          label43:
          if (!SwipeDismissBehavior.this.mViewDragHelper.settleCapturedViewAt(j, paramAnonymousView.getTop())) {
            break label105;
          }
          ViewCompat.postOnAnimation(paramAnonymousView, new SwipeDismissBehavior.SettleRunnable(SwipeDismissBehavior.this, paramAnonymousView, bool));
        }
      }
      label105:
      while ((!bool) || (SwipeDismissBehavior.this.mListener == null))
      {
        return;
        j = i + this.mOriginalCapturedViewLeft;
        break;
        j = this.mOriginalCapturedViewLeft;
        bool = false;
        break label43;
      }
      SwipeDismissBehavior.this.mListener.onDismiss(paramAnonymousView);
    }
    
    public boolean tryCaptureView(View paramAnonymousView, int paramAnonymousInt)
    {
      return (this.mActivePointerId == -1) && (SwipeDismissBehavior.this.canSwipeDismissView(paramAnonymousView));
    }
  };
  float mDragDismissThreshold = 0.5F;
  private boolean mInterceptingEvents;
  OnDismissListener mListener;
  private float mSensitivity = 0.0F;
  private boolean mSensitivitySet;
  int mSwipeDirection = 2;
  ViewDragHelper mViewDragHelper;
  
  public SwipeDismissBehavior() {}
  
  static float clamp(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    return Math.min(Math.max(paramFloat1, paramFloat2), paramFloat3);
  }
  
  static int clamp(int paramInt1, int paramInt2, int paramInt3)
  {
    return Math.min(Math.max(paramInt1, paramInt2), paramInt3);
  }
  
  private void ensureViewDragHelper(ViewGroup paramViewGroup)
  {
    if (this.mViewDragHelper == null) {
      if (!this.mSensitivitySet) {
        break label33;
      }
    }
    label33:
    for (ViewDragHelper localViewDragHelper = ViewDragHelper.create(paramViewGroup, this.mSensitivity, this.mDragCallback);; localViewDragHelper = ViewDragHelper.create(paramViewGroup, this.mDragCallback))
    {
      this.mViewDragHelper = localViewDragHelper;
      return;
    }
  }
  
  static float fraction(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    return (paramFloat3 - paramFloat1) / (paramFloat2 - paramFloat1);
  }
  
  public boolean canSwipeDismissView(@NonNull View paramView)
  {
    return true;
  }
  
  public int getDragState()
  {
    if (this.mViewDragHelper != null) {
      return this.mViewDragHelper.getViewDragState();
    }
    return 0;
  }
  
  public boolean onInterceptTouchEvent(CoordinatorLayout paramCoordinatorLayout, V paramV, MotionEvent paramMotionEvent)
  {
    boolean bool1 = this.mInterceptingEvents;
    switch (MotionEventCompat.getActionMasked(paramMotionEvent))
    {
    }
    for (;;)
    {
      boolean bool2 = false;
      if (bool1)
      {
        ensureViewDragHelper(paramCoordinatorLayout);
        bool2 = this.mViewDragHelper.shouldInterceptTouchEvent(paramMotionEvent);
      }
      return bool2;
      this.mInterceptingEvents = paramCoordinatorLayout.isPointInChildBounds(paramV, (int)paramMotionEvent.getX(), (int)paramMotionEvent.getY());
      bool1 = this.mInterceptingEvents;
      continue;
      this.mInterceptingEvents = false;
    }
  }
  
  public boolean onTouchEvent(CoordinatorLayout paramCoordinatorLayout, V paramV, MotionEvent paramMotionEvent)
  {
    if (this.mViewDragHelper != null)
    {
      this.mViewDragHelper.processTouchEvent(paramMotionEvent);
      return true;
    }
    return false;
  }
  
  public void setDragDismissDistance(float paramFloat)
  {
    this.mDragDismissThreshold = clamp(0.0F, paramFloat, 1.0F);
  }
  
  public void setEndAlphaSwipeDistance(float paramFloat)
  {
    this.mAlphaEndSwipeDistance = clamp(0.0F, paramFloat, 1.0F);
  }
  
  public void setListener(OnDismissListener paramOnDismissListener)
  {
    this.mListener = paramOnDismissListener;
  }
  
  public void setSensitivity(float paramFloat)
  {
    this.mSensitivity = paramFloat;
    this.mSensitivitySet = true;
  }
  
  public void setStartAlphaSwipeDistance(float paramFloat)
  {
    this.mAlphaStartSwipeDistance = clamp(0.0F, paramFloat, 1.0F);
  }
  
  public void setSwipeDirection(int paramInt)
  {
    this.mSwipeDirection = paramInt;
  }
  
  public static abstract interface OnDismissListener
  {
    public abstract void onDismiss(View paramView);
    
    public abstract void onDragStateChanged(int paramInt);
  }
  
  private class SettleRunnable
    implements Runnable
  {
    private final boolean mDismiss;
    private final View mView;
    
    SettleRunnable(View paramView, boolean paramBoolean)
    {
      this.mView = paramView;
      this.mDismiss = paramBoolean;
    }
    
    public void run()
    {
      if ((SwipeDismissBehavior.this.mViewDragHelper != null) && (SwipeDismissBehavior.this.mViewDragHelper.continueSettling(true))) {
        ViewCompat.postOnAnimation(this.mView, this);
      }
      while ((!this.mDismiss) || (SwipeDismissBehavior.this.mListener == null)) {
        return;
      }
      SwipeDismissBehavior.this.mListener.onDismiss(this.mView);
    }
  }
}
