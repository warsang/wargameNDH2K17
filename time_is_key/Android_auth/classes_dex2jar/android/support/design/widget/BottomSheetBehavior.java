package android.support.design.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.annotation.VisibleForTesting;
import android.support.design.R.dimen;
import android.support.design.R.styleable;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v4.view.AbsSavedState;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;

public class BottomSheetBehavior<V extends View>
  extends CoordinatorLayout.Behavior<V>
{
  private static final float HIDE_FRICTION = 0.1F;
  private static final float HIDE_THRESHOLD = 0.5F;
  public static final int PEEK_HEIGHT_AUTO = -1;
  public static final int STATE_COLLAPSED = 4;
  public static final int STATE_DRAGGING = 1;
  public static final int STATE_EXPANDED = 3;
  public static final int STATE_HIDDEN = 5;
  public static final int STATE_SETTLING = 2;
  int mActivePointerId;
  private BottomSheetCallback mCallback;
  private final ViewDragHelper.Callback mDragCallback = new ViewDragHelper.Callback()
  {
    public int clampViewPositionHorizontal(View paramAnonymousView, int paramAnonymousInt1, int paramAnonymousInt2)
    {
      return paramAnonymousView.getLeft();
    }
    
    public int clampViewPositionVertical(View paramAnonymousView, int paramAnonymousInt1, int paramAnonymousInt2)
    {
      int i = BottomSheetBehavior.this.mMinOffset;
      if (BottomSheetBehavior.this.mHideable) {}
      for (int j = BottomSheetBehavior.this.mParentHeight;; j = BottomSheetBehavior.this.mMaxOffset) {
        return MathUtils.constrain(paramAnonymousInt1, i, j);
      }
    }
    
    public int getViewVerticalDragRange(View paramAnonymousView)
    {
      if (BottomSheetBehavior.this.mHideable) {
        return BottomSheetBehavior.this.mParentHeight - BottomSheetBehavior.this.mMinOffset;
      }
      return BottomSheetBehavior.this.mMaxOffset - BottomSheetBehavior.this.mMinOffset;
    }
    
    public void onViewDragStateChanged(int paramAnonymousInt)
    {
      if (paramAnonymousInt == 1) {
        BottomSheetBehavior.this.setStateInternal(1);
      }
    }
    
    public void onViewPositionChanged(View paramAnonymousView, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4)
    {
      BottomSheetBehavior.this.dispatchOnSlide(paramAnonymousInt2);
    }
    
    public void onViewReleased(View paramAnonymousView, float paramAnonymousFloat1, float paramAnonymousFloat2)
    {
      int i;
      int j;
      if (paramAnonymousFloat2 < 0.0F)
      {
        i = BottomSheetBehavior.this.mMinOffset;
        j = 3;
      }
      while (BottomSheetBehavior.this.mViewDragHelper.settleCapturedViewAt(paramAnonymousView.getLeft(), i))
      {
        BottomSheetBehavior.this.setStateInternal(2);
        ViewCompat.postOnAnimation(paramAnonymousView, new BottomSheetBehavior.SettleRunnable(BottomSheetBehavior.this, paramAnonymousView, j));
        return;
        if ((BottomSheetBehavior.this.mHideable) && (BottomSheetBehavior.this.shouldHide(paramAnonymousView, paramAnonymousFloat2)))
        {
          i = BottomSheetBehavior.this.mParentHeight;
          j = 5;
        }
        else if (paramAnonymousFloat2 == 0.0F)
        {
          int k = paramAnonymousView.getTop();
          if (Math.abs(k - BottomSheetBehavior.this.mMinOffset) < Math.abs(k - BottomSheetBehavior.this.mMaxOffset))
          {
            i = BottomSheetBehavior.this.mMinOffset;
            j = 3;
          }
          else
          {
            i = BottomSheetBehavior.this.mMaxOffset;
            j = 4;
          }
        }
        else
        {
          i = BottomSheetBehavior.this.mMaxOffset;
          j = 4;
        }
      }
      BottomSheetBehavior.this.setStateInternal(j);
    }
    
    public boolean tryCaptureView(View paramAnonymousView, int paramAnonymousInt)
    {
      int i = 1;
      if (BottomSheetBehavior.this.mState == i) {}
      View localView;
      do
      {
        do
        {
          return false;
        } while (BottomSheetBehavior.this.mTouchingScrollingChild);
        if ((BottomSheetBehavior.this.mState != 3) || (BottomSheetBehavior.this.mActivePointerId != paramAnonymousInt)) {
          break;
        }
        localView = (View)BottomSheetBehavior.this.mNestedScrollingChildRef.get();
      } while ((localView != null) && (ViewCompat.canScrollVertically(localView, -1)));
      if ((BottomSheetBehavior.this.mViewRef != null) && (BottomSheetBehavior.this.mViewRef.get() == paramAnonymousView)) {}
      for (;;)
      {
        return i;
        int j = 0;
      }
    }
  };
  boolean mHideable;
  private boolean mIgnoreEvents;
  private int mInitialY;
  private int mLastNestedScrollDy;
  int mMaxOffset;
  private float mMaximumVelocity;
  int mMinOffset;
  private boolean mNestedScrolled;
  WeakReference<View> mNestedScrollingChildRef;
  int mParentHeight;
  private int mPeekHeight;
  private boolean mPeekHeightAuto;
  private int mPeekHeightMin;
  private boolean mSkipCollapsed;
  int mState = 4;
  boolean mTouchingScrollingChild;
  private VelocityTracker mVelocityTracker;
  ViewDragHelper mViewDragHelper;
  WeakReference<V> mViewRef;
  
  public BottomSheetBehavior() {}
  
  public BottomSheetBehavior(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.BottomSheetBehavior_Layout);
    TypedValue localTypedValue = localTypedArray.peekValue(R.styleable.BottomSheetBehavior_Layout_behavior_peekHeight);
    if ((localTypedValue != null) && (localTypedValue.data == -1)) {
      setPeekHeight(localTypedValue.data);
    }
    for (;;)
    {
      setHideable(localTypedArray.getBoolean(R.styleable.BottomSheetBehavior_Layout_behavior_hideable, false));
      setSkipCollapsed(localTypedArray.getBoolean(R.styleable.BottomSheetBehavior_Layout_behavior_skipCollapsed, false));
      localTypedArray.recycle();
      this.mMaximumVelocity = ViewConfiguration.get(paramContext).getScaledMaximumFlingVelocity();
      return;
      setPeekHeight(localTypedArray.getDimensionPixelSize(R.styleable.BottomSheetBehavior_Layout_behavior_peekHeight, -1));
    }
  }
  
  private View findScrollingChild(View paramView)
  {
    if ((paramView instanceof NestedScrollingChild)) {
      return paramView;
    }
    if ((paramView instanceof ViewGroup))
    {
      ViewGroup localViewGroup = (ViewGroup)paramView;
      int i = 0;
      int j = localViewGroup.getChildCount();
      while (i < j)
      {
        View localView = findScrollingChild(localViewGroup.getChildAt(i));
        if (localView != null) {
          return localView;
        }
        i++;
      }
    }
    return null;
  }
  
  public static <V extends View> BottomSheetBehavior<V> from(V paramV)
  {
    ViewGroup.LayoutParams localLayoutParams = paramV.getLayoutParams();
    if (!(localLayoutParams instanceof CoordinatorLayout.LayoutParams)) {
      throw new IllegalArgumentException("The view is not a child of CoordinatorLayout");
    }
    CoordinatorLayout.Behavior localBehavior = ((CoordinatorLayout.LayoutParams)localLayoutParams).getBehavior();
    if (!(localBehavior instanceof BottomSheetBehavior)) {
      throw new IllegalArgumentException("The view is not associated with BottomSheetBehavior");
    }
    return (BottomSheetBehavior)localBehavior;
  }
  
  private float getYVelocity()
  {
    this.mVelocityTracker.computeCurrentVelocity(1000, this.mMaximumVelocity);
    return VelocityTrackerCompat.getYVelocity(this.mVelocityTracker, this.mActivePointerId);
  }
  
  private void reset()
  {
    this.mActivePointerId = -1;
    if (this.mVelocityTracker != null)
    {
      this.mVelocityTracker.recycle();
      this.mVelocityTracker = null;
    }
  }
  
  void dispatchOnSlide(int paramInt)
  {
    View localView = (View)this.mViewRef.get();
    if ((localView != null) && (this.mCallback != null))
    {
      if (paramInt > this.mMaxOffset) {
        this.mCallback.onSlide(localView, (this.mMaxOffset - paramInt) / (this.mParentHeight - this.mMaxOffset));
      }
    }
    else {
      return;
    }
    this.mCallback.onSlide(localView, (this.mMaxOffset - paramInt) / (this.mMaxOffset - this.mMinOffset));
  }
  
  public final int getPeekHeight()
  {
    if (this.mPeekHeightAuto) {
      return -1;
    }
    return this.mPeekHeight;
  }
  
  @VisibleForTesting
  int getPeekHeightMin()
  {
    return this.mPeekHeightMin;
  }
  
  public boolean getSkipCollapsed()
  {
    return this.mSkipCollapsed;
  }
  
  public final int getState()
  {
    return this.mState;
  }
  
  public boolean isHideable()
  {
    return this.mHideable;
  }
  
  public boolean onInterceptTouchEvent(CoordinatorLayout paramCoordinatorLayout, V paramV, MotionEvent paramMotionEvent)
  {
    int i = 1;
    if (!paramV.isShown())
    {
      this.mIgnoreEvents = i;
      return false;
    }
    int k = MotionEventCompat.getActionMasked(paramMotionEvent);
    if (k == 0) {
      reset();
    }
    if (this.mVelocityTracker == null) {
      this.mVelocityTracker = VelocityTracker.obtain();
    }
    this.mVelocityTracker.addMovement(paramMotionEvent);
    switch (k)
    {
    }
    while ((!this.mIgnoreEvents) && (this.mViewDragHelper.shouldInterceptTouchEvent(paramMotionEvent)))
    {
      return i;
      this.mTouchingScrollingChild = false;
      this.mActivePointerId = -1;
      if (this.mIgnoreEvents)
      {
        this.mIgnoreEvents = false;
        return false;
        int m = (int)paramMotionEvent.getX();
        this.mInitialY = ((int)paramMotionEvent.getY());
        View localView1 = (View)this.mNestedScrollingChildRef.get();
        if ((localView1 != null) && (paramCoordinatorLayout.isPointInChildBounds(localView1, m, this.mInitialY)))
        {
          this.mActivePointerId = paramMotionEvent.getPointerId(paramMotionEvent.getActionIndex());
          this.mTouchingScrollingChild = i;
        }
        if ((this.mActivePointerId == -1) && (!paramCoordinatorLayout.isPointInChildBounds(paramV, m, this.mInitialY))) {}
        int i1;
        for (int n = i;; i1 = 0)
        {
          this.mIgnoreEvents = n;
          break;
        }
      }
    }
    View localView2 = (View)this.mNestedScrollingChildRef.get();
    if ((k == 2) && (localView2 != null) && (!this.mIgnoreEvents) && (this.mState != i) && (!paramCoordinatorLayout.isPointInChildBounds(localView2, (int)paramMotionEvent.getX(), (int)paramMotionEvent.getY())) && (Math.abs(this.mInitialY - paramMotionEvent.getY()) > this.mViewDragHelper.getTouchSlop())) {}
    for (;;)
    {
      return i;
      int j = 0;
    }
  }
  
  public boolean onLayoutChild(CoordinatorLayout paramCoordinatorLayout, V paramV, int paramInt)
  {
    if ((ViewCompat.getFitsSystemWindows(paramCoordinatorLayout)) && (!ViewCompat.getFitsSystemWindows(paramV))) {
      ViewCompat.setFitsSystemWindows(paramV, true);
    }
    int i = paramV.getTop();
    paramCoordinatorLayout.onLayoutChild(paramV, paramInt);
    this.mParentHeight = paramCoordinatorLayout.getHeight();
    int j;
    if (this.mPeekHeightAuto)
    {
      if (this.mPeekHeightMin == 0) {
        this.mPeekHeightMin = paramCoordinatorLayout.getResources().getDimensionPixelSize(R.dimen.design_bottom_sheet_peek_height_min);
      }
      j = Math.max(this.mPeekHeightMin, this.mParentHeight - 9 * paramCoordinatorLayout.getWidth() / 16);
      this.mMinOffset = Math.max(0, this.mParentHeight - paramV.getHeight());
      this.mMaxOffset = Math.max(this.mParentHeight - j, this.mMinOffset);
      if (this.mState != 3) {
        break label200;
      }
      ViewCompat.offsetTopAndBottom(paramV, this.mMinOffset);
    }
    for (;;)
    {
      if (this.mViewDragHelper == null) {
        this.mViewDragHelper = ViewDragHelper.create(paramCoordinatorLayout, this.mDragCallback);
      }
      this.mViewRef = new WeakReference(paramV);
      this.mNestedScrollingChildRef = new WeakReference(findScrollingChild(paramV));
      return true;
      j = this.mPeekHeight;
      break;
      label200:
      if ((this.mHideable) && (this.mState == 5)) {
        ViewCompat.offsetTopAndBottom(paramV, this.mParentHeight);
      } else if (this.mState == 4) {
        ViewCompat.offsetTopAndBottom(paramV, this.mMaxOffset);
      } else if ((this.mState == 1) || (this.mState == 2)) {
        ViewCompat.offsetTopAndBottom(paramV, i - paramV.getTop());
      }
    }
  }
  
  public boolean onNestedPreFling(CoordinatorLayout paramCoordinatorLayout, V paramV, View paramView, float paramFloat1, float paramFloat2)
  {
    return (paramView == this.mNestedScrollingChildRef.get()) && ((this.mState != 3) || (super.onNestedPreFling(paramCoordinatorLayout, paramV, paramView, paramFloat1, paramFloat2)));
  }
  
  public void onNestedPreScroll(CoordinatorLayout paramCoordinatorLayout, V paramV, View paramView, int paramInt1, int paramInt2, int[] paramArrayOfInt)
  {
    if (paramView != (View)this.mNestedScrollingChildRef.get()) {
      return;
    }
    int i = paramV.getTop();
    int j = i - paramInt2;
    if (paramInt2 > 0) {
      if (j < this.mMinOffset)
      {
        paramArrayOfInt[1] = (i - this.mMinOffset);
        ViewCompat.offsetTopAndBottom(paramV, -paramArrayOfInt[1]);
        setStateInternal(3);
      }
    }
    for (;;)
    {
      dispatchOnSlide(paramV.getTop());
      this.mLastNestedScrollDy = paramInt2;
      this.mNestedScrolled = true;
      return;
      paramArrayOfInt[1] = paramInt2;
      ViewCompat.offsetTopAndBottom(paramV, -paramInt2);
      setStateInternal(1);
      continue;
      if ((paramInt2 < 0) && (!ViewCompat.canScrollVertically(paramView, -1))) {
        if ((j <= this.mMaxOffset) || (this.mHideable))
        {
          paramArrayOfInt[1] = paramInt2;
          ViewCompat.offsetTopAndBottom(paramV, -paramInt2);
          setStateInternal(1);
        }
        else
        {
          paramArrayOfInt[1] = (i - this.mMaxOffset);
          ViewCompat.offsetTopAndBottom(paramV, -paramArrayOfInt[1]);
          setStateInternal(4);
        }
      }
    }
  }
  
  public void onRestoreInstanceState(CoordinatorLayout paramCoordinatorLayout, V paramV, Parcelable paramParcelable)
  {
    SavedState localSavedState = (SavedState)paramParcelable;
    super.onRestoreInstanceState(paramCoordinatorLayout, paramV, localSavedState.getSuperState());
    if ((localSavedState.state == 1) || (localSavedState.state == 2))
    {
      this.mState = 4;
      return;
    }
    this.mState = localSavedState.state;
  }
  
  public Parcelable onSaveInstanceState(CoordinatorLayout paramCoordinatorLayout, V paramV)
  {
    return new SavedState(super.onSaveInstanceState(paramCoordinatorLayout, paramV), this.mState);
  }
  
  public boolean onStartNestedScroll(CoordinatorLayout paramCoordinatorLayout, V paramV, View paramView1, View paramView2, int paramInt)
  {
    this.mLastNestedScrollDy = 0;
    this.mNestedScrolled = false;
    int i = paramInt & 0x2;
    boolean bool = false;
    if (i != 0) {
      bool = true;
    }
    return bool;
  }
  
  public void onStopNestedScroll(CoordinatorLayout paramCoordinatorLayout, V paramV, View paramView)
  {
    if (paramV.getTop() == this.mMinOffset) {
      setStateInternal(3);
    }
    while ((paramView != this.mNestedScrollingChildRef.get()) || (!this.mNestedScrolled)) {
      return;
    }
    int i;
    int j;
    if (this.mLastNestedScrollDy > 0)
    {
      i = this.mMinOffset;
      j = 3;
      if (!this.mViewDragHelper.smoothSlideViewTo(paramV, paramV.getLeft(), i)) {
        break label197;
      }
      setStateInternal(2);
      ViewCompat.postOnAnimation(paramV, new SettleRunnable(paramV, j));
    }
    for (;;)
    {
      this.mNestedScrolled = false;
      return;
      if ((this.mHideable) && (shouldHide(paramV, getYVelocity())))
      {
        i = this.mParentHeight;
        j = 5;
        break;
      }
      if (this.mLastNestedScrollDy == 0)
      {
        int k = paramV.getTop();
        if (Math.abs(k - this.mMinOffset) < Math.abs(k - this.mMaxOffset))
        {
          i = this.mMinOffset;
          j = 3;
          break;
        }
        i = this.mMaxOffset;
        j = 4;
        break;
      }
      i = this.mMaxOffset;
      j = 4;
      break;
      label197:
      setStateInternal(j);
    }
  }
  
  public boolean onTouchEvent(CoordinatorLayout paramCoordinatorLayout, V paramV, MotionEvent paramMotionEvent)
  {
    int i = 1;
    if (!paramV.isShown()) {
      i = 0;
    }
    do
    {
      int j;
      do
      {
        return i;
        j = MotionEventCompat.getActionMasked(paramMotionEvent);
      } while ((this.mState == i) && (j == 0));
      this.mViewDragHelper.processTouchEvent(paramMotionEvent);
      if (j == 0) {
        reset();
      }
      if (this.mVelocityTracker == null) {
        this.mVelocityTracker = VelocityTracker.obtain();
      }
      this.mVelocityTracker.addMovement(paramMotionEvent);
      if ((j == 2) && (!this.mIgnoreEvents) && (Math.abs(this.mInitialY - paramMotionEvent.getY()) > this.mViewDragHelper.getTouchSlop())) {
        this.mViewDragHelper.captureChildView(paramV, paramMotionEvent.getPointerId(paramMotionEvent.getActionIndex()));
      }
    } while (!this.mIgnoreEvents);
    return false;
  }
  
  public void setBottomSheetCallback(BottomSheetCallback paramBottomSheetCallback)
  {
    this.mCallback = paramBottomSheetCallback;
  }
  
  public void setHideable(boolean paramBoolean)
  {
    this.mHideable = paramBoolean;
  }
  
  public final void setPeekHeight(int paramInt)
  {
    int i;
    if (paramInt == -1)
    {
      boolean bool = this.mPeekHeightAuto;
      i = 0;
      if (!bool)
      {
        this.mPeekHeightAuto = true;
        i = 1;
      }
    }
    for (;;)
    {
      if ((i != 0) && (this.mState == 4) && (this.mViewRef != null))
      {
        View localView = (View)this.mViewRef.get();
        if (localView != null) {
          localView.requestLayout();
        }
      }
      return;
      if (!this.mPeekHeightAuto)
      {
        int j = this.mPeekHeight;
        i = 0;
        if (j == paramInt) {}
      }
      else
      {
        this.mPeekHeightAuto = false;
        this.mPeekHeight = Math.max(0, paramInt);
        this.mMaxOffset = (this.mParentHeight - paramInt);
        i = 1;
      }
    }
  }
  
  public void setSkipCollapsed(boolean paramBoolean)
  {
    this.mSkipCollapsed = paramBoolean;
  }
  
  public final void setState(final int paramInt)
  {
    if (paramInt == this.mState) {}
    final View localView;
    do
    {
      do
      {
        return;
        if (this.mViewRef != null) {
          break;
        }
      } while ((paramInt != 4) && (paramInt != 3) && ((!this.mHideable) || (paramInt != 5)));
      this.mState = paramInt;
      return;
      localView = (View)this.mViewRef.get();
    } while (localView == null);
    ViewParent localViewParent = localView.getParent();
    if ((localViewParent != null) && (localViewParent.isLayoutRequested()) && (ViewCompat.isAttachedToWindow(localView)))
    {
      localView.post(new Runnable()
      {
        public void run()
        {
          BottomSheetBehavior.this.startSettlingAnimation(localView, paramInt);
        }
      });
      return;
    }
    startSettlingAnimation(localView, paramInt);
  }
  
  void setStateInternal(int paramInt)
  {
    if (this.mState == paramInt) {}
    View localView;
    do
    {
      return;
      this.mState = paramInt;
      localView = (View)this.mViewRef.get();
    } while ((localView == null) || (this.mCallback == null));
    this.mCallback.onStateChanged(localView, paramInt);
  }
  
  boolean shouldHide(View paramView, float paramFloat)
  {
    if (this.mSkipCollapsed) {}
    do
    {
      return true;
      if (paramView.getTop() < this.mMaxOffset) {
        return false;
      }
    } while (Math.abs(paramView.getTop() + 0.1F * paramFloat - this.mMaxOffset) / this.mPeekHeight > 0.5F);
    return false;
  }
  
  void startSettlingAnimation(View paramView, int paramInt)
  {
    int i;
    if (paramInt == 4) {
      i = this.mMaxOffset;
    }
    for (;;)
    {
      setStateInternal(2);
      if (this.mViewDragHelper.smoothSlideViewTo(paramView, paramView.getLeft(), i)) {
        ViewCompat.postOnAnimation(paramView, new SettleRunnable(paramView, paramInt));
      }
      return;
      if (paramInt == 3)
      {
        i = this.mMinOffset;
      }
      else
      {
        if ((!this.mHideable) || (paramInt != 5)) {
          break;
        }
        i = this.mParentHeight;
      }
    }
    throw new IllegalArgumentException("Illegal state argument: " + paramInt);
  }
  
  public static abstract class BottomSheetCallback
  {
    public BottomSheetCallback() {}
    
    public abstract void onSlide(@NonNull View paramView, float paramFloat);
    
    public abstract void onStateChanged(@NonNull View paramView, int paramInt);
  }
  
  protected static class SavedState
    extends AbsSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks()
    {
      public BottomSheetBehavior.SavedState createFromParcel(Parcel paramAnonymousParcel, ClassLoader paramAnonymousClassLoader)
      {
        return new BottomSheetBehavior.SavedState(paramAnonymousParcel, paramAnonymousClassLoader);
      }
      
      public BottomSheetBehavior.SavedState[] newArray(int paramAnonymousInt)
      {
        return new BottomSheetBehavior.SavedState[paramAnonymousInt];
      }
    });
    final int state;
    
    public SavedState(Parcel paramParcel)
    {
      this(paramParcel, null);
    }
    
    public SavedState(Parcel paramParcel, ClassLoader paramClassLoader)
    {
      super(paramClassLoader);
      this.state = paramParcel.readInt();
    }
    
    public SavedState(Parcelable paramParcelable, int paramInt)
    {
      super();
      this.state = paramInt;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeInt(this.state);
    }
  }
  
  private class SettleRunnable
    implements Runnable
  {
    private final int mTargetState;
    private final View mView;
    
    SettleRunnable(View paramView, int paramInt)
    {
      this.mView = paramView;
      this.mTargetState = paramInt;
    }
    
    public void run()
    {
      if ((BottomSheetBehavior.this.mViewDragHelper != null) && (BottomSheetBehavior.this.mViewDragHelper.continueSettling(true)))
      {
        ViewCompat.postOnAnimation(this.mView, this);
        return;
      }
      BottomSheetBehavior.this.setStateInternal(this.mTargetState);
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public static @interface State {}
}
