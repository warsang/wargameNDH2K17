package android.support.design.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.annotation.VisibleForTesting;
import android.support.design.R.attr;
import android.support.design.R.style;
import android.support.design.R.styleable;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v4.view.AbsSavedState;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.Interpolator;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

@CoordinatorLayout.DefaultBehavior("Landroid/support/design/widget/AppBarLayout$Behavior;")
public class AppBarLayout
  extends LinearLayout
{
  private static final int INVALID_SCROLL_RANGE = -1;
  static final int PENDING_ACTION_ANIMATE_ENABLED = 4;
  static final int PENDING_ACTION_COLLAPSED = 2;
  static final int PENDING_ACTION_EXPANDED = 1;
  static final int PENDING_ACTION_FORCE = 8;
  static final int PENDING_ACTION_NONE;
  private boolean mCollapsed;
  private boolean mCollapsible;
  private int mDownPreScrollRange = -1;
  private int mDownScrollRange = -1;
  private boolean mHaveChildWithInterpolator;
  private WindowInsetsCompat mLastInsets;
  private List<OnOffsetChangedListener> mListeners;
  private int mPendingAction = 0;
  private final int[] mTmpStatesArray = new int[2];
  private int mTotalScrollRange = -1;
  
  public AppBarLayout(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public AppBarLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    setOrientation(1);
    ThemeUtils.checkAppCompatTheme(paramContext);
    if (Build.VERSION.SDK_INT >= 21)
    {
      ViewUtilsLollipop.setBoundsViewOutlineProvider(this);
      ViewUtilsLollipop.setStateListAnimatorFromAttrs(this, paramAttributeSet, 0, R.style.Widget_Design_AppBarLayout);
    }
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.AppBarLayout, 0, R.style.Widget_Design_AppBarLayout);
    ViewCompat.setBackground(this, localTypedArray.getDrawable(R.styleable.AppBarLayout_android_background));
    if (localTypedArray.hasValue(R.styleable.AppBarLayout_expanded)) {
      setExpanded(localTypedArray.getBoolean(R.styleable.AppBarLayout_expanded, false), false, false);
    }
    if ((Build.VERSION.SDK_INT >= 21) && (localTypedArray.hasValue(R.styleable.AppBarLayout_elevation))) {
      ViewUtilsLollipop.setDefaultAppBarLayoutStateListAnimator(this, localTypedArray.getDimensionPixelSize(R.styleable.AppBarLayout_elevation, 0));
    }
    localTypedArray.recycle();
    ViewCompat.setOnApplyWindowInsetsListener(this, new OnApplyWindowInsetsListener()
    {
      public WindowInsetsCompat onApplyWindowInsets(View paramAnonymousView, WindowInsetsCompat paramAnonymousWindowInsetsCompat)
      {
        return AppBarLayout.this.onWindowInsetChanged(paramAnonymousWindowInsetsCompat);
      }
    });
  }
  
  private void invalidateScrollRanges()
  {
    this.mTotalScrollRange = -1;
    this.mDownPreScrollRange = -1;
    this.mDownScrollRange = -1;
  }
  
  private boolean setCollapsibleState(boolean paramBoolean)
  {
    if (this.mCollapsible != paramBoolean)
    {
      this.mCollapsible = paramBoolean;
      refreshDrawableState();
      return true;
    }
    return false;
  }
  
  private void setExpanded(boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    int i;
    if (paramBoolean1)
    {
      i = 1;
      if (!paramBoolean2) {
        break label52;
      }
    }
    label52:
    for (int j = 4;; j = 0)
    {
      int k = j | i;
      int m = 0;
      if (paramBoolean3) {
        m = 8;
      }
      this.mPendingAction = (m | k);
      requestLayout();
      return;
      i = 2;
      break;
    }
  }
  
  private void updateCollapsible()
  {
    int i = 0;
    int j = getChildCount();
    for (;;)
    {
      boolean bool = false;
      if (i < j)
      {
        if (((LayoutParams)getChildAt(i).getLayoutParams()).isCollapsible()) {
          bool = true;
        }
      }
      else
      {
        setCollapsibleState(bool);
        return;
      }
      i++;
    }
  }
  
  public void addOnOffsetChangedListener(OnOffsetChangedListener paramOnOffsetChangedListener)
  {
    if (this.mListeners == null) {
      this.mListeners = new ArrayList();
    }
    if ((paramOnOffsetChangedListener != null) && (!this.mListeners.contains(paramOnOffsetChangedListener))) {
      this.mListeners.add(paramOnOffsetChangedListener);
    }
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    return paramLayoutParams instanceof LayoutParams;
  }
  
  void dispatchOffsetUpdates(int paramInt)
  {
    if (this.mListeners != null)
    {
      int i = 0;
      int j = this.mListeners.size();
      while (i < j)
      {
        OnOffsetChangedListener localOnOffsetChangedListener = (OnOffsetChangedListener)this.mListeners.get(i);
        if (localOnOffsetChangedListener != null) {
          localOnOffsetChangedListener.onOffsetChanged(this, paramInt);
        }
        i++;
      }
    }
  }
  
  protected LayoutParams generateDefaultLayoutParams()
  {
    return new LayoutParams(-1, -2);
  }
  
  public LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return new LayoutParams(getContext(), paramAttributeSet);
  }
  
  protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    if ((Build.VERSION.SDK_INT >= 19) && ((paramLayoutParams instanceof LinearLayout.LayoutParams))) {
      return new LayoutParams((LinearLayout.LayoutParams)paramLayoutParams);
    }
    if ((paramLayoutParams instanceof ViewGroup.MarginLayoutParams)) {
      return new LayoutParams((ViewGroup.MarginLayoutParams)paramLayoutParams);
    }
    return new LayoutParams(paramLayoutParams);
  }
  
  int getDownNestedPreScrollRange()
  {
    if (this.mDownPreScrollRange != -1) {
      return this.mDownPreScrollRange;
    }
    int i = 0;
    int j = -1 + getChildCount();
    if (j >= 0)
    {
      View localView = getChildAt(j);
      LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
      int m = localView.getMeasuredHeight();
      int n = localLayoutParams.mScrollFlags;
      if ((n & 0x5) == 5)
      {
        i1 = i + (localLayoutParams.topMargin + localLayoutParams.bottomMargin);
        if ((n & 0x8) != 0) {
          i = i1 + ViewCompat.getMinimumHeight(localView);
        }
      }
      while (i <= 0) {
        for (;;)
        {
          int i1;
          j--;
          break;
          if ((n & 0x2) != 0) {
            i = i1 + (m - ViewCompat.getMinimumHeight(localView));
          } else {
            i = i1 + (m - getTopInset());
          }
        }
      }
    }
    int k = Math.max(0, i);
    this.mDownPreScrollRange = k;
    return k;
  }
  
  int getDownNestedScrollRange()
  {
    if (this.mDownScrollRange != -1) {
      return this.mDownScrollRange;
    }
    int i = 0;
    int j = 0;
    int k = getChildCount();
    for (;;)
    {
      if (j < k)
      {
        View localView = getChildAt(j);
        LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
        int n = localView.getMeasuredHeight() + (localLayoutParams.topMargin + localLayoutParams.bottomMargin);
        int i1 = localLayoutParams.mScrollFlags;
        if ((i1 & 0x1) != 0)
        {
          i += n;
          if ((i1 & 0x2) == 0) {
            break label118;
          }
          i -= ViewCompat.getMinimumHeight(localView) + getTopInset();
        }
      }
      int m = Math.max(0, i);
      this.mDownScrollRange = m;
      return m;
      label118:
      j++;
    }
  }
  
  final int getMinimumHeightForVisibleOverlappingContent()
  {
    int i = getTopInset();
    int j = ViewCompat.getMinimumHeight(this);
    if (j != 0) {
      return i + j * 2;
    }
    int k = getChildCount();
    if (k >= 1) {}
    for (int m = ViewCompat.getMinimumHeight(getChildAt(k - 1)); m != 0; m = 0) {
      return i + m * 2;
    }
    return getHeight() / 3;
  }
  
  int getPendingAction()
  {
    return this.mPendingAction;
  }
  
  @Deprecated
  public float getTargetElevation()
  {
    return 0.0F;
  }
  
  @VisibleForTesting
  final int getTopInset()
  {
    if (this.mLastInsets != null) {
      return this.mLastInsets.getSystemWindowInsetTop();
    }
    return 0;
  }
  
  public final int getTotalScrollRange()
  {
    if (this.mTotalScrollRange != -1) {
      return this.mTotalScrollRange;
    }
    int i = 0;
    int j = 0;
    int k = getChildCount();
    for (;;)
    {
      if (j < k)
      {
        View localView = getChildAt(j);
        LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
        int n = localView.getMeasuredHeight();
        int i1 = localLayoutParams.mScrollFlags;
        if ((i1 & 0x1) != 0)
        {
          i += n + localLayoutParams.topMargin + localLayoutParams.bottomMargin;
          if ((i1 & 0x2) == 0) {
            break label118;
          }
          i -= ViewCompat.getMinimumHeight(localView);
        }
      }
      int m = Math.max(0, i - getTopInset());
      this.mTotalScrollRange = m;
      return m;
      label118:
      j++;
    }
  }
  
  int getUpNestedPreScrollRange()
  {
    return getTotalScrollRange();
  }
  
  boolean hasChildWithInterpolator()
  {
    return this.mHaveChildWithInterpolator;
  }
  
  boolean hasScrollableChildren()
  {
    return getTotalScrollRange() != 0;
  }
  
  protected int[] onCreateDrawableState(int paramInt)
  {
    int[] arrayOfInt1 = this.mTmpStatesArray;
    int[] arrayOfInt2 = super.onCreateDrawableState(paramInt + arrayOfInt1.length);
    int i;
    if (this.mCollapsible)
    {
      i = R.attr.state_collapsible;
      arrayOfInt1[0] = i;
      if ((!this.mCollapsible) || (!this.mCollapsed)) {
        break label70;
      }
    }
    label70:
    for (int j = R.attr.state_collapsed;; j = -R.attr.state_collapsed)
    {
      arrayOfInt1[1] = j;
      return mergeDrawableStates(arrayOfInt2, arrayOfInt1);
      i = -R.attr.state_collapsible;
      break;
    }
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    invalidateScrollRanges();
    this.mHaveChildWithInterpolator = false;
    int i = 0;
    int j = getChildCount();
    for (;;)
    {
      if (i < j)
      {
        if (((LayoutParams)getChildAt(i).getLayoutParams()).getScrollInterpolator() != null) {
          this.mHaveChildWithInterpolator = true;
        }
      }
      else
      {
        updateCollapsible();
        return;
      }
      i++;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    super.onMeasure(paramInt1, paramInt2);
    invalidateScrollRanges();
  }
  
  WindowInsetsCompat onWindowInsetChanged(WindowInsetsCompat paramWindowInsetsCompat)
  {
    boolean bool = ViewCompat.getFitsSystemWindows(this);
    WindowInsetsCompat localWindowInsetsCompat = null;
    if (bool) {
      localWindowInsetsCompat = paramWindowInsetsCompat;
    }
    if (!ViewUtils.objectEquals(this.mLastInsets, localWindowInsetsCompat))
    {
      this.mLastInsets = localWindowInsetsCompat;
      invalidateScrollRanges();
    }
    return paramWindowInsetsCompat;
  }
  
  public void removeOnOffsetChangedListener(OnOffsetChangedListener paramOnOffsetChangedListener)
  {
    if ((this.mListeners != null) && (paramOnOffsetChangedListener != null)) {
      this.mListeners.remove(paramOnOffsetChangedListener);
    }
  }
  
  void resetPendingAction()
  {
    this.mPendingAction = 0;
  }
  
  boolean setCollapsedState(boolean paramBoolean)
  {
    if (this.mCollapsed != paramBoolean)
    {
      this.mCollapsed = paramBoolean;
      refreshDrawableState();
      return true;
    }
    return false;
  }
  
  public void setExpanded(boolean paramBoolean)
  {
    setExpanded(paramBoolean, ViewCompat.isLaidOut(this));
  }
  
  public void setExpanded(boolean paramBoolean1, boolean paramBoolean2)
  {
    setExpanded(paramBoolean1, paramBoolean2, true);
  }
  
  public void setOrientation(int paramInt)
  {
    if (paramInt != 1) {
      throw new IllegalArgumentException("AppBarLayout is always vertical and does not support horizontal orientation");
    }
    super.setOrientation(paramInt);
  }
  
  @Deprecated
  public void setTargetElevation(float paramFloat)
  {
    if (Build.VERSION.SDK_INT >= 21) {
      ViewUtilsLollipop.setDefaultAppBarLayoutStateListAnimator(this, paramFloat);
    }
  }
  
  public static class Behavior
    extends HeaderBehavior<AppBarLayout>
  {
    private static final int INVALID_POSITION = -1;
    private static final int MAX_OFFSET_ANIMATION_DURATION = 600;
    private WeakReference<View> mLastNestedScrollingChildRef;
    private ValueAnimatorCompat mOffsetAnimator;
    private int mOffsetDelta;
    private int mOffsetToChildIndexOnLayout = -1;
    private boolean mOffsetToChildIndexOnLayoutIsMinHeight;
    private float mOffsetToChildIndexOnLayoutPerc;
    private DragCallback mOnDragCallback;
    private boolean mSkipNestedPreScroll;
    private boolean mWasNestedFlung;
    
    public Behavior() {}
    
    public Behavior(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
    }
    
    private void animateOffsetTo(CoordinatorLayout paramCoordinatorLayout, AppBarLayout paramAppBarLayout, int paramInt, float paramFloat)
    {
      int i = Math.abs(getTopBottomOffsetForScrollingSibling() - paramInt);
      float f = Math.abs(paramFloat);
      if (f > 0.0F) {}
      for (int j = 3 * Math.round(1000.0F * (i / f));; j = (int)(150.0F * (1.0F + i / paramAppBarLayout.getHeight())))
      {
        animateOffsetWithDuration(paramCoordinatorLayout, paramAppBarLayout, paramInt, j);
        return;
      }
    }
    
    private void animateOffsetWithDuration(final CoordinatorLayout paramCoordinatorLayout, final AppBarLayout paramAppBarLayout, int paramInt1, int paramInt2)
    {
      int i = getTopBottomOffsetForScrollingSibling();
      if (i == paramInt1)
      {
        if ((this.mOffsetAnimator != null) && (this.mOffsetAnimator.isRunning())) {
          this.mOffsetAnimator.cancel();
        }
        return;
      }
      if (this.mOffsetAnimator == null)
      {
        this.mOffsetAnimator = ViewUtils.createAnimator();
        this.mOffsetAnimator.setInterpolator(AnimationUtils.DECELERATE_INTERPOLATOR);
        this.mOffsetAnimator.addUpdateListener(new ValueAnimatorCompat.AnimatorUpdateListener()
        {
          public void onAnimationUpdate(ValueAnimatorCompat paramAnonymousValueAnimatorCompat)
          {
            AppBarLayout.Behavior.this.setHeaderTopBottomOffset(paramCoordinatorLayout, paramAppBarLayout, paramAnonymousValueAnimatorCompat.getAnimatedIntValue());
          }
        });
      }
      for (;;)
      {
        this.mOffsetAnimator.setDuration(Math.min(paramInt2, 600));
        this.mOffsetAnimator.setIntValues(i, paramInt1);
        this.mOffsetAnimator.start();
        return;
        this.mOffsetAnimator.cancel();
      }
    }
    
    private static boolean checkFlag(int paramInt1, int paramInt2)
    {
      return (paramInt1 & paramInt2) == paramInt2;
    }
    
    private static View getAppBarChildOnOffset(AppBarLayout paramAppBarLayout, int paramInt)
    {
      int i = Math.abs(paramInt);
      int j = 0;
      int k = paramAppBarLayout.getChildCount();
      while (j < k)
      {
        View localView = paramAppBarLayout.getChildAt(j);
        if ((i >= localView.getTop()) && (i <= localView.getBottom())) {
          return localView;
        }
        j++;
      }
      return null;
    }
    
    private int getChildIndexOnOffset(AppBarLayout paramAppBarLayout, int paramInt)
    {
      int i = 0;
      int j = paramAppBarLayout.getChildCount();
      while (i < j)
      {
        View localView = paramAppBarLayout.getChildAt(i);
        if ((localView.getTop() <= -paramInt) && (localView.getBottom() >= -paramInt)) {
          return i;
        }
        i++;
      }
      return -1;
    }
    
    private int interpolateOffset(AppBarLayout paramAppBarLayout, int paramInt)
    {
      int i = Math.abs(paramInt);
      int j = 0;
      int k = paramAppBarLayout.getChildCount();
      for (;;)
      {
        if (j < k)
        {
          View localView = paramAppBarLayout.getChildAt(j);
          AppBarLayout.LayoutParams localLayoutParams = (AppBarLayout.LayoutParams)localView.getLayoutParams();
          Interpolator localInterpolator = localLayoutParams.getScrollInterpolator();
          if ((i < localView.getTop()) || (i > localView.getBottom())) {
            break label198;
          }
          if (localInterpolator != null)
          {
            int m = localLayoutParams.getScrollFlags();
            int n = m & 0x1;
            int i1 = 0;
            if (n != 0)
            {
              i1 = 0 + (localView.getHeight() + localLayoutParams.topMargin + localLayoutParams.bottomMargin);
              if ((m & 0x2) != 0) {
                i1 -= ViewCompat.getMinimumHeight(localView);
              }
            }
            if (ViewCompat.getFitsSystemWindows(localView)) {
              i1 -= paramAppBarLayout.getTopInset();
            }
            if (i1 > 0)
            {
              int i2 = i - localView.getTop();
              int i3 = Math.round(i1 * localInterpolator.getInterpolation(i2 / i1));
              paramInt = Integer.signum(paramInt) * (i3 + localView.getTop());
            }
          }
        }
        return paramInt;
        label198:
        j++;
      }
    }
    
    private boolean shouldJumpElevationState(CoordinatorLayout paramCoordinatorLayout, AppBarLayout paramAppBarLayout)
    {
      List localList = paramCoordinatorLayout.getDependents(paramAppBarLayout);
      int i = 0;
      int j = localList.size();
      for (;;)
      {
        boolean bool = false;
        if (i < j)
        {
          CoordinatorLayout.Behavior localBehavior = ((CoordinatorLayout.LayoutParams)((View)localList.get(i)).getLayoutParams()).getBehavior();
          if (!(localBehavior instanceof AppBarLayout.ScrollingViewBehavior)) {
            break label81;
          }
          int k = ((AppBarLayout.ScrollingViewBehavior)localBehavior).getOverlayTop();
          bool = false;
          if (k != 0) {
            bool = true;
          }
        }
        return bool;
        label81:
        i++;
      }
    }
    
    private void snapToChildIfNeeded(CoordinatorLayout paramCoordinatorLayout, AppBarLayout paramAppBarLayout)
    {
      int i = getTopBottomOffsetForScrollingSibling();
      int j = getChildIndexOnOffset(paramAppBarLayout, i);
      View localView;
      int k;
      int m;
      int n;
      if (j >= 0)
      {
        localView = paramAppBarLayout.getChildAt(j);
        k = ((AppBarLayout.LayoutParams)localView.getLayoutParams()).getScrollFlags();
        if ((k & 0x11) == 17)
        {
          m = -localView.getTop();
          n = -localView.getBottom();
          if (j == -1 + paramAppBarLayout.getChildCount()) {
            n += paramAppBarLayout.getTopInset();
          }
          if (!checkFlag(k, 2)) {
            break label138;
          }
          n += ViewCompat.getMinimumHeight(localView);
          if (i >= (n + m) / 2) {
            break label177;
          }
        }
      }
      label138:
      label177:
      for (int i2 = n;; i2 = m)
      {
        animateOffsetTo(paramCoordinatorLayout, paramAppBarLayout, MathUtils.constrain(i2, -paramAppBarLayout.getTotalScrollRange(), 0), 0.0F);
        return;
        if (!checkFlag(k, 5)) {
          break;
        }
        int i1 = n + ViewCompat.getMinimumHeight(localView);
        if (i < i1)
        {
          m = i1;
          break;
        }
        n = i1;
        break;
      }
    }
    
    private void updateAppBarLayoutDrawableState(CoordinatorLayout paramCoordinatorLayout, AppBarLayout paramAppBarLayout, int paramInt1, int paramInt2, boolean paramBoolean)
    {
      View localView = getAppBarChildOnOffset(paramAppBarLayout, paramInt1);
      int i;
      int k;
      if (localView != null)
      {
        i = ((AppBarLayout.LayoutParams)localView.getLayoutParams()).getScrollFlags();
        int j = i & 0x1;
        bool1 = false;
        if (j != 0)
        {
          k = ViewCompat.getMinimumHeight(localView);
          if ((paramInt2 <= 0) || ((i & 0xC) == 0)) {
            break label126;
          }
          if (-paramInt1 < localView.getBottom() - k - paramAppBarLayout.getTopInset()) {
            break label120;
          }
          bool1 = true;
        }
      }
      label120:
      label126:
      int m;
      do
      {
        for (;;)
        {
          boolean bool2 = paramAppBarLayout.setCollapsedState(bool1);
          if ((Build.VERSION.SDK_INT >= 11) && ((paramBoolean) || ((bool2) && (shouldJumpElevationState(paramCoordinatorLayout, paramAppBarLayout))))) {
            paramAppBarLayout.jumpDrawablesToCurrentState();
          }
          return;
          bool1 = false;
        }
        m = i & 0x2;
        bool1 = false;
      } while (m == 0);
      if (-paramInt1 >= localView.getBottom() - k - paramAppBarLayout.getTopInset()) {}
      for (boolean bool1 = true;; bool1 = false) {
        break;
      }
    }
    
    boolean canDragView(AppBarLayout paramAppBarLayout)
    {
      boolean bool = true;
      if (this.mOnDragCallback != null) {
        bool = this.mOnDragCallback.canDrag(paramAppBarLayout);
      }
      View localView;
      do
      {
        do
        {
          return bool;
        } while (this.mLastNestedScrollingChildRef == null);
        localView = (View)this.mLastNestedScrollingChildRef.get();
      } while ((localView != null) && (localView.isShown()) && (!ViewCompat.canScrollVertically(localView, -1)));
      return false;
    }
    
    int getMaxDragOffset(AppBarLayout paramAppBarLayout)
    {
      return -paramAppBarLayout.getDownNestedScrollRange();
    }
    
    int getScrollRangeForDragFling(AppBarLayout paramAppBarLayout)
    {
      return paramAppBarLayout.getTotalScrollRange();
    }
    
    int getTopBottomOffsetForScrollingSibling()
    {
      return getTopAndBottomOffset() + this.mOffsetDelta;
    }
    
    @VisibleForTesting
    boolean isOffsetAnimatorRunning()
    {
      return (this.mOffsetAnimator != null) && (this.mOffsetAnimator.isRunning());
    }
    
    void onFlingFinished(CoordinatorLayout paramCoordinatorLayout, AppBarLayout paramAppBarLayout)
    {
      snapToChildIfNeeded(paramCoordinatorLayout, paramAppBarLayout);
    }
    
    public boolean onLayoutChild(CoordinatorLayout paramCoordinatorLayout, AppBarLayout paramAppBarLayout, int paramInt)
    {
      boolean bool = super.onLayoutChild(paramCoordinatorLayout, paramAppBarLayout, paramInt);
      int i = paramAppBarLayout.getPendingAction();
      View localView;
      int m;
      int n;
      if ((this.mOffsetToChildIndexOnLayout >= 0) && ((i & 0x8) == 0))
      {
        localView = paramAppBarLayout.getChildAt(this.mOffsetToChildIndexOnLayout);
        m = -localView.getBottom();
        if (this.mOffsetToChildIndexOnLayoutIsMinHeight)
        {
          n = m + (ViewCompat.getMinimumHeight(localView) + paramAppBarLayout.getTopInset());
          setHeaderTopBottomOffset(paramCoordinatorLayout, paramAppBarLayout, n);
        }
      }
      for (;;)
      {
        paramAppBarLayout.resetPendingAction();
        this.mOffsetToChildIndexOnLayout = -1;
        setTopAndBottomOffset(MathUtils.constrain(getTopAndBottomOffset(), -paramAppBarLayout.getTotalScrollRange(), 0));
        updateAppBarLayoutDrawableState(paramCoordinatorLayout, paramAppBarLayout, getTopAndBottomOffset(), 0, true);
        paramAppBarLayout.dispatchOffsetUpdates(getTopAndBottomOffset());
        return bool;
        n = m + Math.round(localView.getHeight() * this.mOffsetToChildIndexOnLayoutPerc);
        break;
        if (i != 0)
        {
          int j;
          if ((i & 0x4) != 0) {
            j = 1;
          }
          for (;;)
          {
            if ((i & 0x2) != 0)
            {
              int k = -paramAppBarLayout.getUpNestedPreScrollRange();
              if (j != 0)
              {
                animateOffsetTo(paramCoordinatorLayout, paramAppBarLayout, k, 0.0F);
                break;
                j = 0;
                continue;
              }
              setHeaderTopBottomOffset(paramCoordinatorLayout, paramAppBarLayout, k);
              break;
            }
          }
          if ((i & 0x1) != 0) {
            if (j != 0) {
              animateOffsetTo(paramCoordinatorLayout, paramAppBarLayout, 0, 0.0F);
            } else {
              setHeaderTopBottomOffset(paramCoordinatorLayout, paramAppBarLayout, 0);
            }
          }
        }
      }
    }
    
    public boolean onMeasureChild(CoordinatorLayout paramCoordinatorLayout, AppBarLayout paramAppBarLayout, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if (((CoordinatorLayout.LayoutParams)paramAppBarLayout.getLayoutParams()).height == -2)
      {
        paramCoordinatorLayout.onMeasureChild(paramAppBarLayout, paramInt1, paramInt2, View.MeasureSpec.makeMeasureSpec(0, 0), paramInt4);
        return true;
      }
      return super.onMeasureChild(paramCoordinatorLayout, paramAppBarLayout, paramInt1, paramInt2, paramInt3, paramInt4);
    }
    
    public boolean onNestedFling(CoordinatorLayout paramCoordinatorLayout, AppBarLayout paramAppBarLayout, View paramView, float paramFloat1, float paramFloat2, boolean paramBoolean)
    {
      boolean bool;
      if (!paramBoolean) {
        bool = fling(paramCoordinatorLayout, paramAppBarLayout, -paramAppBarLayout.getTotalScrollRange(), 0, -paramFloat2);
      }
      for (;;)
      {
        this.mWasNestedFlung = bool;
        return bool;
        if (paramFloat2 < 0.0F)
        {
          int k = -paramAppBarLayout.getTotalScrollRange() + paramAppBarLayout.getDownNestedPreScrollRange();
          int m = getTopBottomOffsetForScrollingSibling();
          bool = false;
          if (m < k)
          {
            animateOffsetTo(paramCoordinatorLayout, paramAppBarLayout, k, paramFloat2);
            bool = true;
          }
        }
        else
        {
          int i = -paramAppBarLayout.getUpNestedPreScrollRange();
          int j = getTopBottomOffsetForScrollingSibling();
          bool = false;
          if (j > i)
          {
            animateOffsetTo(paramCoordinatorLayout, paramAppBarLayout, i, paramFloat2);
            bool = true;
          }
        }
      }
    }
    
    public void onNestedPreScroll(CoordinatorLayout paramCoordinatorLayout, AppBarLayout paramAppBarLayout, View paramView, int paramInt1, int paramInt2, int[] paramArrayOfInt)
    {
      int i;
      if ((paramInt2 != 0) && (!this.mSkipNestedPreScroll))
      {
        if (paramInt2 >= 0) {
          break label50;
        }
        i = -paramAppBarLayout.getTotalScrollRange();
      }
      for (int j = i + paramAppBarLayout.getDownNestedPreScrollRange();; j = 0)
      {
        paramArrayOfInt[1] = scroll(paramCoordinatorLayout, paramAppBarLayout, paramInt2, i, j);
        return;
        label50:
        i = -paramAppBarLayout.getUpNestedPreScrollRange();
      }
    }
    
    public void onNestedScroll(CoordinatorLayout paramCoordinatorLayout, AppBarLayout paramAppBarLayout, View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if (paramInt4 < 0)
      {
        scroll(paramCoordinatorLayout, paramAppBarLayout, paramInt4, -paramAppBarLayout.getDownNestedScrollRange(), 0);
        this.mSkipNestedPreScroll = true;
        return;
      }
      this.mSkipNestedPreScroll = false;
    }
    
    public void onRestoreInstanceState(CoordinatorLayout paramCoordinatorLayout, AppBarLayout paramAppBarLayout, Parcelable paramParcelable)
    {
      if ((paramParcelable instanceof SavedState))
      {
        SavedState localSavedState = (SavedState)paramParcelable;
        super.onRestoreInstanceState(paramCoordinatorLayout, paramAppBarLayout, localSavedState.getSuperState());
        this.mOffsetToChildIndexOnLayout = localSavedState.firstVisibleChildIndex;
        this.mOffsetToChildIndexOnLayoutPerc = localSavedState.firstVisibleChildPercentageShown;
        this.mOffsetToChildIndexOnLayoutIsMinHeight = localSavedState.firstVisibleChildAtMinimumHeight;
        return;
      }
      super.onRestoreInstanceState(paramCoordinatorLayout, paramAppBarLayout, paramParcelable);
      this.mOffsetToChildIndexOnLayout = -1;
    }
    
    public Parcelable onSaveInstanceState(CoordinatorLayout paramCoordinatorLayout, AppBarLayout paramAppBarLayout)
    {
      Parcelable localParcelable = super.onSaveInstanceState(paramCoordinatorLayout, paramAppBarLayout);
      int i = getTopAndBottomOffset();
      int j = 0;
      int k = paramAppBarLayout.getChildCount();
      while (j < k)
      {
        View localView = paramAppBarLayout.getChildAt(j);
        int m = i + localView.getBottom();
        if ((i + localView.getTop() <= 0) && (m >= 0))
        {
          SavedState localSavedState = new SavedState(localParcelable);
          localSavedState.firstVisibleChildIndex = j;
          if (m == ViewCompat.getMinimumHeight(localView) + paramAppBarLayout.getTopInset()) {}
          for (boolean bool = true;; bool = false)
          {
            localSavedState.firstVisibleChildAtMinimumHeight = bool;
            localSavedState.firstVisibleChildPercentageShown = (m / localView.getHeight());
            return localSavedState;
          }
        }
        j++;
      }
      return localParcelable;
    }
    
    public boolean onStartNestedScroll(CoordinatorLayout paramCoordinatorLayout, AppBarLayout paramAppBarLayout, View paramView1, View paramView2, int paramInt)
    {
      if (((paramInt & 0x2) != 0) && (paramAppBarLayout.hasScrollableChildren()) && (paramCoordinatorLayout.getHeight() - paramView1.getHeight() <= paramAppBarLayout.getHeight())) {}
      for (boolean bool = true;; bool = false)
      {
        if ((bool) && (this.mOffsetAnimator != null)) {
          this.mOffsetAnimator.cancel();
        }
        this.mLastNestedScrollingChildRef = null;
        return bool;
      }
    }
    
    public void onStopNestedScroll(CoordinatorLayout paramCoordinatorLayout, AppBarLayout paramAppBarLayout, View paramView)
    {
      if (!this.mWasNestedFlung) {
        snapToChildIfNeeded(paramCoordinatorLayout, paramAppBarLayout);
      }
      this.mSkipNestedPreScroll = false;
      this.mWasNestedFlung = false;
      this.mLastNestedScrollingChildRef = new WeakReference(paramView);
    }
    
    public void setDragCallback(@Nullable DragCallback paramDragCallback)
    {
      this.mOnDragCallback = paramDragCallback;
    }
    
    int setHeaderTopBottomOffset(CoordinatorLayout paramCoordinatorLayout, AppBarLayout paramAppBarLayout, int paramInt1, int paramInt2, int paramInt3)
    {
      int i = getTopBottomOffsetForScrollingSibling();
      if ((paramInt2 != 0) && (i >= paramInt2) && (i <= paramInt3))
      {
        int j = MathUtils.constrain(paramInt1, paramInt2, paramInt3);
        int k = 0;
        int m;
        if (i != j)
        {
          if (!paramAppBarLayout.hasChildWithInterpolator()) {
            break label134;
          }
          m = interpolateOffset(paramAppBarLayout, j);
          boolean bool = setTopAndBottomOffset(m);
          k = i - j;
          this.mOffsetDelta = (j - m);
          if ((!bool) && (paramAppBarLayout.hasChildWithInterpolator())) {
            paramCoordinatorLayout.dispatchDependentViewsChanged(paramAppBarLayout);
          }
          paramAppBarLayout.dispatchOffsetUpdates(getTopAndBottomOffset());
          if (j >= i) {
            break label141;
          }
        }
        label134:
        label141:
        for (int n = -1;; n = 1)
        {
          updateAppBarLayoutDrawableState(paramCoordinatorLayout, paramAppBarLayout, j, n, false);
          return k;
          m = j;
          break;
        }
      }
      this.mOffsetDelta = 0;
      return 0;
    }
    
    public static abstract class DragCallback
    {
      public DragCallback() {}
      
      public abstract boolean canDrag(@NonNull AppBarLayout paramAppBarLayout);
    }
    
    protected static class SavedState
      extends AbsSavedState
    {
      public static final Parcelable.Creator<SavedState> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks()
      {
        public AppBarLayout.Behavior.SavedState createFromParcel(Parcel paramAnonymousParcel, ClassLoader paramAnonymousClassLoader)
        {
          return new AppBarLayout.Behavior.SavedState(paramAnonymousParcel, paramAnonymousClassLoader);
        }
        
        public AppBarLayout.Behavior.SavedState[] newArray(int paramAnonymousInt)
        {
          return new AppBarLayout.Behavior.SavedState[paramAnonymousInt];
        }
      });
      boolean firstVisibleChildAtMinimumHeight;
      int firstVisibleChildIndex;
      float firstVisibleChildPercentageShown;
      
      public SavedState(Parcel paramParcel, ClassLoader paramClassLoader)
      {
        super(paramClassLoader);
        this.firstVisibleChildIndex = paramParcel.readInt();
        this.firstVisibleChildPercentageShown = paramParcel.readFloat();
        if (paramParcel.readByte() != 0) {}
        for (boolean bool = true;; bool = false)
        {
          this.firstVisibleChildAtMinimumHeight = bool;
          return;
        }
      }
      
      public SavedState(Parcelable paramParcelable)
      {
        super();
      }
      
      public void writeToParcel(Parcel paramParcel, int paramInt)
      {
        super.writeToParcel(paramParcel, paramInt);
        paramParcel.writeInt(this.firstVisibleChildIndex);
        paramParcel.writeFloat(this.firstVisibleChildPercentageShown);
        if (this.firstVisibleChildAtMinimumHeight) {}
        for (int i = 1;; i = 0)
        {
          paramParcel.writeByte((byte)i);
          return;
        }
      }
    }
  }
  
  public static class LayoutParams
    extends LinearLayout.LayoutParams
  {
    static final int COLLAPSIBLE_FLAGS = 10;
    static final int FLAG_QUICK_RETURN = 5;
    static final int FLAG_SNAP = 17;
    public static final int SCROLL_FLAG_ENTER_ALWAYS = 4;
    public static final int SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED = 8;
    public static final int SCROLL_FLAG_EXIT_UNTIL_COLLAPSED = 2;
    public static final int SCROLL_FLAG_SCROLL = 1;
    public static final int SCROLL_FLAG_SNAP = 16;
    int mScrollFlags = 1;
    Interpolator mScrollInterpolator;
    
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
    }
    
    public LayoutParams(int paramInt1, int paramInt2, float paramFloat)
    {
      super(paramInt2, paramFloat);
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
      TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.AppBarLayout_Layout);
      this.mScrollFlags = localTypedArray.getInt(R.styleable.AppBarLayout_Layout_layout_scrollFlags, 0);
      if (localTypedArray.hasValue(R.styleable.AppBarLayout_Layout_layout_scrollInterpolator)) {
        this.mScrollInterpolator = android.view.animation.AnimationUtils.loadInterpolator(paramContext, localTypedArray.getResourceId(R.styleable.AppBarLayout_Layout_layout_scrollInterpolator, 0));
      }
      localTypedArray.recycle();
    }
    
    @TargetApi(19)
    @RequiresApi(19)
    public LayoutParams(LayoutParams paramLayoutParams)
    {
      super();
      this.mScrollFlags = paramLayoutParams.mScrollFlags;
      this.mScrollInterpolator = paramLayoutParams.mScrollInterpolator;
    }
    
    public LayoutParams(ViewGroup.LayoutParams paramLayoutParams)
    {
      super();
    }
    
    public LayoutParams(ViewGroup.MarginLayoutParams paramMarginLayoutParams)
    {
      super();
    }
    
    @TargetApi(19)
    @RequiresApi(19)
    public LayoutParams(LinearLayout.LayoutParams paramLayoutParams)
    {
      super();
    }
    
    public int getScrollFlags()
    {
      return this.mScrollFlags;
    }
    
    public Interpolator getScrollInterpolator()
    {
      return this.mScrollInterpolator;
    }
    
    boolean isCollapsible()
    {
      return ((0x1 & this.mScrollFlags) == 1) && ((0xA & this.mScrollFlags) != 0);
    }
    
    public void setScrollFlags(int paramInt)
    {
      this.mScrollFlags = paramInt;
    }
    
    public void setScrollInterpolator(Interpolator paramInterpolator)
    {
      this.mScrollInterpolator = paramInterpolator;
    }
    
    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
    public static @interface ScrollFlags {}
  }
  
  public static abstract interface OnOffsetChangedListener
  {
    public abstract void onOffsetChanged(AppBarLayout paramAppBarLayout, int paramInt);
  }
  
  public static class ScrollingViewBehavior
    extends HeaderScrollingViewBehavior
  {
    public ScrollingViewBehavior() {}
    
    public ScrollingViewBehavior(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
      TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.ScrollingViewBehavior_Layout);
      setOverlayTop(localTypedArray.getDimensionPixelSize(R.styleable.ScrollingViewBehavior_Layout_behavior_overlapTop, 0));
      localTypedArray.recycle();
    }
    
    private static int getAppBarLayoutOffset(AppBarLayout paramAppBarLayout)
    {
      CoordinatorLayout.Behavior localBehavior = ((CoordinatorLayout.LayoutParams)paramAppBarLayout.getLayoutParams()).getBehavior();
      if ((localBehavior instanceof AppBarLayout.Behavior)) {
        return ((AppBarLayout.Behavior)localBehavior).getTopBottomOffsetForScrollingSibling();
      }
      return 0;
    }
    
    private void offsetChildAsNeeded(CoordinatorLayout paramCoordinatorLayout, View paramView1, View paramView2)
    {
      CoordinatorLayout.Behavior localBehavior = ((CoordinatorLayout.LayoutParams)paramView2.getLayoutParams()).getBehavior();
      if ((localBehavior instanceof AppBarLayout.Behavior))
      {
        AppBarLayout.Behavior localBehavior1 = (AppBarLayout.Behavior)localBehavior;
        ViewCompat.offsetTopAndBottom(paramView1, paramView2.getBottom() - paramView1.getTop() + localBehavior1.mOffsetDelta + getVerticalLayoutGap() - getOverlapPixelsForOffset(paramView2));
      }
    }
    
    AppBarLayout findFirstDependency(List<View> paramList)
    {
      int i = 0;
      int j = paramList.size();
      while (i < j)
      {
        View localView = (View)paramList.get(i);
        if ((localView instanceof AppBarLayout)) {
          return (AppBarLayout)localView;
        }
        i++;
      }
      return null;
    }
    
    float getOverlapRatioForOffset(View paramView)
    {
      int i;
      int j;
      int k;
      if ((paramView instanceof AppBarLayout))
      {
        AppBarLayout localAppBarLayout = (AppBarLayout)paramView;
        i = localAppBarLayout.getTotalScrollRange();
        j = localAppBarLayout.getDownNestedPreScrollRange();
        k = getAppBarLayoutOffset(localAppBarLayout);
        if ((j == 0) || (i + k > j)) {
          break label45;
        }
      }
      label45:
      int m;
      do
      {
        return 0.0F;
        m = i - j;
      } while (m == 0);
      return 1.0F + k / m;
    }
    
    int getScrollRange(View paramView)
    {
      if ((paramView instanceof AppBarLayout)) {
        return ((AppBarLayout)paramView).getTotalScrollRange();
      }
      return super.getScrollRange(paramView);
    }
    
    public boolean layoutDependsOn(CoordinatorLayout paramCoordinatorLayout, View paramView1, View paramView2)
    {
      return paramView2 instanceof AppBarLayout;
    }
    
    public boolean onDependentViewChanged(CoordinatorLayout paramCoordinatorLayout, View paramView1, View paramView2)
    {
      offsetChildAsNeeded(paramCoordinatorLayout, paramView1, paramView2);
      return false;
    }
    
    public boolean onRequestChildRectangleOnScreen(CoordinatorLayout paramCoordinatorLayout, View paramView, Rect paramRect, boolean paramBoolean)
    {
      AppBarLayout localAppBarLayout = findFirstDependency(paramCoordinatorLayout.getDependencies(paramView));
      if (localAppBarLayout != null)
      {
        paramRect.offset(paramView.getLeft(), paramView.getTop());
        Rect localRect = this.mTempRect1;
        localRect.set(0, 0, paramCoordinatorLayout.getWidth(), paramCoordinatorLayout.getHeight());
        if (!localRect.contains(paramRect))
        {
          if (!paramBoolean) {}
          for (boolean bool = true;; bool = false)
          {
            localAppBarLayout.setExpanded(false, bool);
            return true;
          }
        }
      }
      return false;
    }
  }
}
