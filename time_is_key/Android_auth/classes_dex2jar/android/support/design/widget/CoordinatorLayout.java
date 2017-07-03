package android.support.design.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.SystemClock;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.FloatRange;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.VisibleForTesting;
import android.support.design.R.style;
import android.support.design.R.styleable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v4.util.Pools.Pool;
import android.support.v4.util.Pools.SynchronizedPool;
import android.support.v4.view.AbsSavedState;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.BaseSavedState;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewGroup.OnHierarchyChangeListener;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CoordinatorLayout
  extends ViewGroup
  implements NestedScrollingParent
{
  static final Class<?>[] CONSTRUCTOR_PARAMS;
  static final int EVENT_NESTED_SCROLL = 1;
  static final int EVENT_PRE_DRAW = 0;
  static final int EVENT_VIEW_REMOVED = 2;
  static final String TAG = "CoordinatorLayout";
  static final Comparator<View> TOP_SORTED_CHILDREN_COMPARATOR;
  private static final int TYPE_ON_INTERCEPT = 0;
  private static final int TYPE_ON_TOUCH = 1;
  static final String WIDGET_PACKAGE_NAME;
  static final ThreadLocal<Map<String, Constructor<Behavior>>> sConstructors;
  private static final Pools.Pool<Rect> sRectPool;
  private OnApplyWindowInsetsListener mApplyWindowInsetsListener;
  private View mBehaviorTouchView;
  private final DirectedAcyclicGraph<View> mChildDag = new DirectedAcyclicGraph();
  private final List<View> mDependencySortedChildren = new ArrayList();
  private boolean mDisallowInterceptReset;
  private boolean mDrawStatusBarBackground;
  private boolean mIsAttachedToWindow;
  private int[] mKeylines;
  private WindowInsetsCompat mLastInsets;
  private boolean mNeedsPreDrawListener;
  private View mNestedScrollingDirectChild;
  private final NestedScrollingParentHelper mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
  private View mNestedScrollingTarget;
  ViewGroup.OnHierarchyChangeListener mOnHierarchyChangeListener;
  private OnPreDrawListener mOnPreDrawListener;
  private Paint mScrimPaint;
  private Drawable mStatusBarBackground;
  private final List<View> mTempDependenciesList = new ArrayList();
  private final int[] mTempIntPair = new int[2];
  private final List<View> mTempList1 = new ArrayList();
  
  static
  {
    Package localPackage = CoordinatorLayout.class.getPackage();
    String str;
    if (localPackage != null)
    {
      str = localPackage.getName();
      WIDGET_PACKAGE_NAME = str;
      if (Build.VERSION.SDK_INT < 21) {
        break label82;
      }
    }
    label82:
    for (TOP_SORTED_CHILDREN_COMPARATOR = new ViewElevationComparator();; TOP_SORTED_CHILDREN_COMPARATOR = null)
    {
      CONSTRUCTOR_PARAMS = new Class[] { Context.class, AttributeSet.class };
      sConstructors = new ThreadLocal();
      sRectPool = new Pools.SynchronizedPool(12);
      return;
      str = null;
      break;
    }
  }
  
  public CoordinatorLayout(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public CoordinatorLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public CoordinatorLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    ThemeUtils.checkAppCompatTheme(paramContext);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.CoordinatorLayout, paramInt, R.style.Widget_Design_CoordinatorLayout);
    int i = localTypedArray.getResourceId(R.styleable.CoordinatorLayout_keylines, 0);
    if (i != 0)
    {
      Resources localResources = paramContext.getResources();
      this.mKeylines = localResources.getIntArray(i);
      float f = localResources.getDisplayMetrics().density;
      int j = this.mKeylines.length;
      for (int k = 0; k < j; k++)
      {
        int[] arrayOfInt = this.mKeylines;
        arrayOfInt[k] = ((int)(f * arrayOfInt[k]));
      }
    }
    this.mStatusBarBackground = localTypedArray.getDrawable(R.styleable.CoordinatorLayout_statusBarBackground);
    localTypedArray.recycle();
    setupForInsets();
    super.setOnHierarchyChangeListener(new HierarchyChangeListener());
  }
  
  @NonNull
  private static Rect acquireTempRect()
  {
    Rect localRect = (Rect)sRectPool.acquire();
    if (localRect == null) {
      localRect = new Rect();
    }
    return localRect;
  }
  
  private void constrainChildRect(LayoutParams paramLayoutParams, Rect paramRect, int paramInt1, int paramInt2)
  {
    int i = getWidth();
    int j = getHeight();
    int k = Math.max(getPaddingLeft() + paramLayoutParams.leftMargin, Math.min(paramRect.left, i - getPaddingRight() - paramInt1 - paramLayoutParams.rightMargin));
    int m = Math.max(getPaddingTop() + paramLayoutParams.topMargin, Math.min(paramRect.top, j - getPaddingBottom() - paramInt2 - paramLayoutParams.bottomMargin));
    paramRect.set(k, m, k + paramInt1, m + paramInt2);
  }
  
  private WindowInsetsCompat dispatchApplyWindowInsetsToBehaviors(WindowInsetsCompat paramWindowInsetsCompat)
  {
    if (paramWindowInsetsCompat.isConsumed()) {
      return paramWindowInsetsCompat;
    }
    int i = 0;
    int j = getChildCount();
    for (;;)
    {
      if (i < j)
      {
        View localView = getChildAt(i);
        if (ViewCompat.getFitsSystemWindows(localView))
        {
          Behavior localBehavior = ((LayoutParams)localView.getLayoutParams()).getBehavior();
          if (localBehavior != null)
          {
            paramWindowInsetsCompat = localBehavior.onApplyWindowInsets(this, localView, paramWindowInsetsCompat);
            if (!paramWindowInsetsCompat.isConsumed()) {}
          }
        }
      }
      else
      {
        return paramWindowInsetsCompat;
      }
      i++;
    }
  }
  
  private void getDesiredAnchoredChildRectWithoutConstraints(View paramView, int paramInt1, Rect paramRect1, Rect paramRect2, LayoutParams paramLayoutParams, int paramInt2, int paramInt3)
  {
    int i = GravityCompat.getAbsoluteGravity(resolveAnchoredChildGravity(paramLayoutParams.gravity), paramInt1);
    int j = GravityCompat.getAbsoluteGravity(resolveGravity(paramLayoutParams.anchorGravity), paramInt1);
    int k = i & 0x7;
    int m = i & 0x70;
    int n = j & 0x7;
    int i1 = j & 0x70;
    int i2;
    int i3;
    switch (n)
    {
    default: 
      i2 = paramRect1.left;
      switch (i1)
      {
      default: 
        i3 = paramRect1.top;
        switch (k)
        {
        default: 
          i2 -= paramInt2;
        case 5: 
          label126:
          switch (m)
          {
          default: 
            label163:
            i3 -= paramInt3;
          }
          break;
        }
        break;
      }
      break;
    }
    for (;;)
    {
      paramRect2.set(i2, i3, i2 + paramInt2, i3 + paramInt3);
      return;
      i2 = paramRect1.right;
      break;
      i2 = paramRect1.left + paramRect1.width() / 2;
      break;
      i3 = paramRect1.bottom;
      break label126;
      i3 = paramRect1.top + paramRect1.height() / 2;
      break label126;
      i2 -= paramInt2 / 2;
      break label163;
      i3 -= paramInt3 / 2;
    }
  }
  
  private int getKeyline(int paramInt)
  {
    if (this.mKeylines == null)
    {
      Log.e("CoordinatorLayout", "No keylines defined for " + this + " - attempted index lookup " + paramInt);
      return 0;
    }
    if ((paramInt < 0) || (paramInt >= this.mKeylines.length))
    {
      Log.e("CoordinatorLayout", "Keyline index " + paramInt + " out of range for " + this);
      return 0;
    }
    return this.mKeylines[paramInt];
  }
  
  private void getTopSortedChildren(List<View> paramList)
  {
    paramList.clear();
    boolean bool = isChildrenDrawingOrderEnabled();
    int i = getChildCount();
    int j = i - 1;
    if (j >= 0)
    {
      if (bool) {}
      for (int k = getChildDrawingOrder(i, j);; k = j)
      {
        paramList.add(getChildAt(k));
        j--;
        break;
      }
    }
    if (TOP_SORTED_CHILDREN_COMPARATOR != null) {
      Collections.sort(paramList, TOP_SORTED_CHILDREN_COMPARATOR);
    }
  }
  
  private boolean hasDependencies(View paramView)
  {
    return this.mChildDag.hasOutgoingEdges(paramView);
  }
  
  private void layoutChild(View paramView, int paramInt)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    Rect localRect1 = acquireTempRect();
    localRect1.set(getPaddingLeft() + localLayoutParams.leftMargin, getPaddingTop() + localLayoutParams.topMargin, getWidth() - getPaddingRight() - localLayoutParams.rightMargin, getHeight() - getPaddingBottom() - localLayoutParams.bottomMargin);
    if ((this.mLastInsets != null) && (ViewCompat.getFitsSystemWindows(this)) && (!ViewCompat.getFitsSystemWindows(paramView)))
    {
      localRect1.left += this.mLastInsets.getSystemWindowInsetLeft();
      localRect1.top += this.mLastInsets.getSystemWindowInsetTop();
      localRect1.right -= this.mLastInsets.getSystemWindowInsetRight();
      localRect1.bottom -= this.mLastInsets.getSystemWindowInsetBottom();
    }
    Rect localRect2 = acquireTempRect();
    GravityCompat.apply(resolveGravity(localLayoutParams.gravity), paramView.getMeasuredWidth(), paramView.getMeasuredHeight(), localRect1, localRect2, paramInt);
    paramView.layout(localRect2.left, localRect2.top, localRect2.right, localRect2.bottom);
    releaseTempRect(localRect1);
    releaseTempRect(localRect2);
  }
  
  private void layoutChildWithAnchor(View paramView1, View paramView2, int paramInt)
  {
    ((LayoutParams)paramView1.getLayoutParams());
    Rect localRect1 = acquireTempRect();
    Rect localRect2 = acquireTempRect();
    try
    {
      getDescendantRect(paramView2, localRect1);
      getDesiredAnchoredChildRect(paramView1, paramInt, localRect1, localRect2);
      paramView1.layout(localRect2.left, localRect2.top, localRect2.right, localRect2.bottom);
      return;
    }
    finally
    {
      releaseTempRect(localRect1);
      releaseTempRect(localRect2);
    }
  }
  
  private void layoutChildWithKeyline(View paramView, int paramInt1, int paramInt2)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    int i = GravityCompat.getAbsoluteGravity(resolveKeylineGravity(localLayoutParams.gravity), paramInt2);
    int j = i & 0x7;
    int k = i & 0x70;
    int m = getWidth();
    int n = getHeight();
    int i1 = paramView.getMeasuredWidth();
    int i2 = paramView.getMeasuredHeight();
    if (paramInt2 == 1) {
      paramInt1 = m - paramInt1;
    }
    int i3 = getKeyline(paramInt1) - i1;
    int i4;
    switch (j)
    {
    default: 
      i4 = 0;
      switch (k)
      {
      }
      break;
    }
    for (;;)
    {
      int i5 = Math.max(getPaddingLeft() + localLayoutParams.leftMargin, Math.min(i3, m - getPaddingRight() - i1 - localLayoutParams.rightMargin));
      int i6 = Math.max(getPaddingTop() + localLayoutParams.topMargin, Math.min(i4, n - getPaddingBottom() - i2 - localLayoutParams.bottomMargin));
      paramView.layout(i5, i6, i5 + i1, i6 + i2);
      return;
      i3 += i1;
      break;
      i3 += i1 / 2;
      break;
      i4 = 0 + i2;
      continue;
      i4 = 0 + i2 / 2;
    }
  }
  
  private void offsetChildByInset(View paramView, Rect paramRect, int paramInt)
  {
    if (!ViewCompat.isLaidOut(paramView)) {}
    while ((paramView.getWidth() <= 0) || (paramView.getHeight() <= 0)) {
      return;
    }
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    Behavior localBehavior = localLayoutParams.getBehavior();
    Rect localRect1 = acquireTempRect();
    Rect localRect2 = acquireTempRect();
    localRect2.set(paramView.getLeft(), paramView.getTop(), paramView.getRight(), paramView.getBottom());
    if ((localBehavior != null) && (localBehavior.getInsetDodgeRect(this, paramView, localRect1)))
    {
      if (!localRect2.contains(localRect1)) {
        throw new IllegalArgumentException("Rect should be within the child's bounds. Rect:" + localRect1.toShortString() + " | Bounds:" + localRect2.toShortString());
      }
    }
    else {
      localRect1.set(localRect2);
    }
    releaseTempRect(localRect2);
    if (localRect1.isEmpty())
    {
      releaseTempRect(localRect1);
      return;
    }
    int i = GravityCompat.getAbsoluteGravity(localLayoutParams.dodgeInsetEdges, paramInt);
    int j = i & 0x30;
    int k = 0;
    if (j == 48)
    {
      int i5 = localRect1.top - localLayoutParams.topMargin - localLayoutParams.mInsetOffsetY;
      int i6 = paramRect.top;
      k = 0;
      if (i5 < i6)
      {
        setInsetOffsetY(paramView, paramRect.top - i5);
        k = 1;
      }
    }
    if ((i & 0x50) == 80)
    {
      int i4 = getHeight() - localRect1.bottom - localLayoutParams.bottomMargin + localLayoutParams.mInsetOffsetY;
      if (i4 < paramRect.bottom)
      {
        setInsetOffsetY(paramView, i4 - paramRect.bottom);
        k = 1;
      }
    }
    if (k == 0) {
      setInsetOffsetY(paramView, 0);
    }
    int m = i & 0x3;
    int n = 0;
    if (m == 3)
    {
      int i2 = localRect1.left - localLayoutParams.leftMargin - localLayoutParams.mInsetOffsetX;
      int i3 = paramRect.left;
      n = 0;
      if (i2 < i3)
      {
        setInsetOffsetX(paramView, paramRect.left - i2);
        n = 1;
      }
    }
    if ((i & 0x5) == 5)
    {
      int i1 = getWidth() - localRect1.right - localLayoutParams.rightMargin + localLayoutParams.mInsetOffsetX;
      if (i1 < paramRect.right)
      {
        setInsetOffsetX(paramView, i1 - paramRect.right);
        n = 1;
      }
    }
    if (n == 0) {
      setInsetOffsetX(paramView, 0);
    }
    releaseTempRect(localRect1);
  }
  
  static Behavior parseBehavior(Context paramContext, AttributeSet paramAttributeSet, String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      return null;
    }
    if (paramString.startsWith(".")) {}
    for (str = paramContext.getPackageName() + paramString;; str = paramString)
    {
      try
      {
        Object localObject = (Map)sConstructors.get();
        if (localObject == null)
        {
          localObject = new HashMap();
          sConstructors.set(localObject);
        }
        Constructor localConstructor = (Constructor)((Map)localObject).get(str);
        if (localConstructor == null)
        {
          localConstructor = Class.forName(str, true, paramContext.getClassLoader()).getConstructor(CONSTRUCTOR_PARAMS);
          localConstructor.setAccessible(true);
          ((Map)localObject).put(str, localConstructor);
        }
        Behavior localBehavior = (Behavior)localConstructor.newInstance(new Object[] { paramContext, paramAttributeSet });
        return localBehavior;
      }
      catch (Exception localException)
      {
        throw new RuntimeException("Could not inflate Behavior subclass " + str, localException);
      }
      if (paramString.indexOf('.') < 0) {
        break;
      }
    }
    if (!TextUtils.isEmpty(WIDGET_PACKAGE_NAME)) {}
    for (str = WIDGET_PACKAGE_NAME + '.' + paramString;; str = paramString) {
      break;
    }
  }
  
  private boolean performIntercept(MotionEvent paramMotionEvent, int paramInt)
  {
    boolean bool1 = false;
    int i = 0;
    MotionEvent localMotionEvent = null;
    int j = MotionEventCompat.getActionMasked(paramMotionEvent);
    List localList = this.mTempList1;
    getTopSortedChildren(localList);
    int k = localList.size();
    int m = 0;
    View localView;
    Behavior localBehavior;
    label208:
    boolean bool3;
    if (m < k)
    {
      localView = (View)localList.get(m);
      LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
      localBehavior = localLayoutParams.getBehavior();
      if (((bool1) || (i != 0)) && (j != 0))
      {
        if (localBehavior != null)
        {
          if (localMotionEvent == null)
          {
            long l = SystemClock.uptimeMillis();
            localMotionEvent = MotionEvent.obtain(l, l, 3, 0.0F, 0.0F, 0);
          }
          switch (paramInt)
          {
          }
        }
        for (;;)
        {
          m++;
          break;
          localBehavior.onInterceptTouchEvent(this, localView, localMotionEvent);
          continue;
          localBehavior.onTouchEvent(this, localView, localMotionEvent);
        }
      }
      if ((!bool1) && (localBehavior != null)) {}
      switch (paramInt)
      {
      default: 
        if (bool1) {
          this.mBehaviorTouchView = localView;
        }
        boolean bool2 = localLayoutParams.didBlockInteraction();
        bool3 = localLayoutParams.isBlockingInteractionBelow(this, localView);
        if ((!bool3) || (bool2)) {
          break;
        }
      }
    }
    for (i = 1; (bool3) && (i == 0); i = 0)
    {
      localList.clear();
      return bool1;
      bool1 = localBehavior.onInterceptTouchEvent(this, localView, paramMotionEvent);
      break label208;
      bool1 = localBehavior.onTouchEvent(this, localView, paramMotionEvent);
      break label208;
    }
  }
  
  private void prepareChildren()
  {
    this.mDependencySortedChildren.clear();
    this.mChildDag.clear();
    int i = 0;
    int j = getChildCount();
    while (i < j)
    {
      View localView1 = getChildAt(i);
      getResolvedLayoutParams(localView1).findAnchorView(this, localView1);
      this.mChildDag.addNode(localView1);
      int k = 0;
      if (k < j)
      {
        if (k == i) {}
        for (;;)
        {
          k++;
          break;
          View localView2 = getChildAt(k);
          if (getResolvedLayoutParams(localView2).dependsOn(this, localView2, localView1))
          {
            if (!this.mChildDag.contains(localView2)) {
              this.mChildDag.addNode(localView2);
            }
            this.mChildDag.addEdge(localView1, localView2);
          }
        }
      }
      i++;
    }
    this.mDependencySortedChildren.addAll(this.mChildDag.getSortedList());
    Collections.reverse(this.mDependencySortedChildren);
  }
  
  private static void releaseTempRect(@NonNull Rect paramRect)
  {
    paramRect.setEmpty();
    sRectPool.release(paramRect);
  }
  
  private void resetTouchBehaviors()
  {
    if (this.mBehaviorTouchView != null)
    {
      Behavior localBehavior = ((LayoutParams)this.mBehaviorTouchView.getLayoutParams()).getBehavior();
      if (localBehavior != null)
      {
        long l = SystemClock.uptimeMillis();
        MotionEvent localMotionEvent = MotionEvent.obtain(l, l, 3, 0.0F, 0.0F, 0);
        localBehavior.onTouchEvent(this, this.mBehaviorTouchView, localMotionEvent);
        localMotionEvent.recycle();
      }
      this.mBehaviorTouchView = null;
    }
    int i = getChildCount();
    for (int j = 0; j < i; j++) {
      ((LayoutParams)getChildAt(j).getLayoutParams()).resetTouchBehaviorTracking();
    }
    this.mDisallowInterceptReset = false;
  }
  
  private static int resolveAnchoredChildGravity(int paramInt)
  {
    if (paramInt == 0) {
      paramInt = 17;
    }
    return paramInt;
  }
  
  private static int resolveGravity(int paramInt)
  {
    if ((paramInt & 0x7) == 0) {
      paramInt |= 0x800003;
    }
    if ((paramInt & 0x70) == 0) {
      paramInt |= 0x30;
    }
    return paramInt;
  }
  
  private static int resolveKeylineGravity(int paramInt)
  {
    if (paramInt == 0) {
      paramInt = 8388661;
    }
    return paramInt;
  }
  
  private void setInsetOffsetX(View paramView, int paramInt)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    if (localLayoutParams.mInsetOffsetX != paramInt)
    {
      ViewCompat.offsetLeftAndRight(paramView, paramInt - localLayoutParams.mInsetOffsetX);
      localLayoutParams.mInsetOffsetX = paramInt;
    }
  }
  
  private void setInsetOffsetY(View paramView, int paramInt)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    if (localLayoutParams.mInsetOffsetY != paramInt)
    {
      ViewCompat.offsetTopAndBottom(paramView, paramInt - localLayoutParams.mInsetOffsetY);
      localLayoutParams.mInsetOffsetY = paramInt;
    }
  }
  
  private void setupForInsets()
  {
    if (Build.VERSION.SDK_INT < 21) {
      return;
    }
    if (ViewCompat.getFitsSystemWindows(this))
    {
      if (this.mApplyWindowInsetsListener == null) {
        this.mApplyWindowInsetsListener = new OnApplyWindowInsetsListener()
        {
          public WindowInsetsCompat onApplyWindowInsets(View paramAnonymousView, WindowInsetsCompat paramAnonymousWindowInsetsCompat)
          {
            return CoordinatorLayout.this.setWindowInsets(paramAnonymousWindowInsetsCompat);
          }
        };
      }
      ViewCompat.setOnApplyWindowInsetsListener(this, this.mApplyWindowInsetsListener);
      setSystemUiVisibility(1280);
      return;
    }
    ViewCompat.setOnApplyWindowInsetsListener(this, null);
  }
  
  void addPreDrawListener()
  {
    if (this.mIsAttachedToWindow)
    {
      if (this.mOnPreDrawListener == null) {
        this.mOnPreDrawListener = new OnPreDrawListener();
      }
      getViewTreeObserver().addOnPreDrawListener(this.mOnPreDrawListener);
    }
    this.mNeedsPreDrawListener = true;
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    return ((paramLayoutParams instanceof LayoutParams)) && (super.checkLayoutParams(paramLayoutParams));
  }
  
  public void dispatchDependentViewsChanged(View paramView)
  {
    List localList = this.mChildDag.getIncomingEdges(paramView);
    if ((localList != null) && (!localList.isEmpty())) {
      for (int i = 0; i < localList.size(); i++)
      {
        View localView = (View)localList.get(i);
        Behavior localBehavior = ((LayoutParams)localView.getLayoutParams()).getBehavior();
        if (localBehavior != null) {
          localBehavior.onDependentViewChanged(this, localView, paramView);
        }
      }
    }
  }
  
  public boolean doViewsOverlap(View paramView1, View paramView2)
  {
    boolean bool1 = true;
    if ((paramView1.getVisibility() == 0) && (paramView2.getVisibility() == 0))
    {
      Rect localRect1 = acquireTempRect();
      boolean bool2;
      if (paramView1.getParent() != this) {
        bool2 = bool1;
      }
      for (;;)
      {
        getChildRect(paramView1, bool2, localRect1);
        Rect localRect2 = acquireTempRect();
        boolean bool3;
        if (paramView2.getParent() != this)
        {
          bool3 = bool1;
          label57:
          getChildRect(paramView2, bool3, localRect2);
        }
        try
        {
          if ((localRect1.left <= localRect2.right) && (localRect1.top <= localRect2.bottom) && (localRect1.right >= localRect2.left))
          {
            int i = localRect1.bottom;
            int j = localRect2.top;
            if (i >= j)
            {
              return bool1;
              bool2 = false;
              continue;
              bool3 = false;
              break label57;
            }
          }
          return false;
        }
        finally
        {
          releaseTempRect(localRect1);
          releaseTempRect(localRect2);
        }
      }
    }
  }
  
  protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    if (localLayoutParams.mBehavior != null)
    {
      float f = localLayoutParams.mBehavior.getScrimOpacity(this, paramView);
      if (f > 0.0F)
      {
        if (this.mScrimPaint == null) {
          this.mScrimPaint = new Paint();
        }
        this.mScrimPaint.setColor(localLayoutParams.mBehavior.getScrimColor(this, paramView));
        this.mScrimPaint.setAlpha(MathUtils.constrain(Math.round(255.0F * f), 0, 255));
        int i = paramCanvas.save();
        if (paramView.isOpaque()) {
          paramCanvas.clipRect(paramView.getLeft(), paramView.getTop(), paramView.getRight(), paramView.getBottom(), Region.Op.DIFFERENCE);
        }
        paramCanvas.drawRect(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom(), this.mScrimPaint);
        paramCanvas.restoreToCount(i);
      }
    }
    return super.drawChild(paramCanvas, paramView, paramLong);
  }
  
  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    int[] arrayOfInt = getDrawableState();
    Drawable localDrawable = this.mStatusBarBackground;
    boolean bool1 = false;
    if (localDrawable != null)
    {
      boolean bool2 = localDrawable.isStateful();
      bool1 = false;
      if (bool2) {
        bool1 = false | localDrawable.setState(arrayOfInt);
      }
    }
    if (bool1) {
      invalidate();
    }
  }
  
  void ensurePreDrawListener()
  {
    int i = getChildCount();
    for (int j = 0;; j++)
    {
      int k = 0;
      if (j < i)
      {
        if (hasDependencies(getChildAt(j))) {
          k = 1;
        }
      }
      else
      {
        if (k != this.mNeedsPreDrawListener)
        {
          if (k == 0) {
            break;
          }
          addPreDrawListener();
        }
        return;
      }
    }
    removePreDrawListener();
  }
  
  protected LayoutParams generateDefaultLayoutParams()
  {
    return new LayoutParams(-2, -2);
  }
  
  public LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return new LayoutParams(getContext(), paramAttributeSet);
  }
  
  protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    if ((paramLayoutParams instanceof LayoutParams)) {
      return new LayoutParams((LayoutParams)paramLayoutParams);
    }
    if ((paramLayoutParams instanceof ViewGroup.MarginLayoutParams)) {
      return new LayoutParams((ViewGroup.MarginLayoutParams)paramLayoutParams);
    }
    return new LayoutParams(paramLayoutParams);
  }
  
  void getChildRect(View paramView, boolean paramBoolean, Rect paramRect)
  {
    if ((paramView.isLayoutRequested()) || (paramView.getVisibility() == 8))
    {
      paramRect.setEmpty();
      return;
    }
    if (paramBoolean)
    {
      getDescendantRect(paramView, paramRect);
      return;
    }
    paramRect.set(paramView.getLeft(), paramView.getTop(), paramView.getRight(), paramView.getBottom());
  }
  
  @NonNull
  public List<View> getDependencies(@NonNull View paramView)
  {
    List localList = this.mChildDag.getOutgoingEdges(paramView);
    this.mTempDependenciesList.clear();
    if (localList != null) {
      this.mTempDependenciesList.addAll(localList);
    }
    return this.mTempDependenciesList;
  }
  
  @VisibleForTesting
  final List<View> getDependencySortedChildren()
  {
    prepareChildren();
    return Collections.unmodifiableList(this.mDependencySortedChildren);
  }
  
  @NonNull
  public List<View> getDependents(@NonNull View paramView)
  {
    List localList = this.mChildDag.getIncomingEdges(paramView);
    this.mTempDependenciesList.clear();
    if (localList != null) {
      this.mTempDependenciesList.addAll(localList);
    }
    return this.mTempDependenciesList;
  }
  
  void getDescendantRect(View paramView, Rect paramRect)
  {
    ViewGroupUtils.getDescendantRect(this, paramView, paramRect);
  }
  
  void getDesiredAnchoredChildRect(View paramView, int paramInt, Rect paramRect1, Rect paramRect2)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    int i = paramView.getMeasuredWidth();
    int j = paramView.getMeasuredHeight();
    getDesiredAnchoredChildRectWithoutConstraints(paramView, paramInt, paramRect1, paramRect2, localLayoutParams, i, j);
    constrainChildRect(localLayoutParams, paramRect2, i, j);
  }
  
  void getLastChildRect(View paramView, Rect paramRect)
  {
    paramRect.set(((LayoutParams)paramView.getLayoutParams()).getLastChildRect());
  }
  
  final WindowInsetsCompat getLastWindowInsets()
  {
    return this.mLastInsets;
  }
  
  public int getNestedScrollAxes()
  {
    return this.mNestedScrollingParentHelper.getNestedScrollAxes();
  }
  
  LayoutParams getResolvedLayoutParams(View paramView)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    DefaultBehavior localDefaultBehavior;
    if (!localLayoutParams.mBehaviorResolved)
    {
      Class localClass = paramView.getClass();
      localDefaultBehavior = null;
      while (localClass != null)
      {
        localDefaultBehavior = (DefaultBehavior)localClass.getAnnotation(DefaultBehavior.class);
        if (localDefaultBehavior != null) {
          break;
        }
        localClass = localClass.getSuperclass();
      }
      if (localDefaultBehavior == null) {}
    }
    try
    {
      localLayoutParams.setBehavior((Behavior)localDefaultBehavior.value().newInstance());
      localLayoutParams.mBehaviorResolved = true;
      return localLayoutParams;
    }
    catch (Exception localException)
    {
      for (;;)
      {
        Log.e("CoordinatorLayout", "Default behavior class " + localDefaultBehavior.value().getName() + " could not be instantiated. Did you forget a default constructor?", localException);
      }
    }
  }
  
  @Nullable
  public Drawable getStatusBarBackground()
  {
    return this.mStatusBarBackground;
  }
  
  protected int getSuggestedMinimumHeight()
  {
    return Math.max(super.getSuggestedMinimumHeight(), getPaddingTop() + getPaddingBottom());
  }
  
  protected int getSuggestedMinimumWidth()
  {
    return Math.max(super.getSuggestedMinimumWidth(), getPaddingLeft() + getPaddingRight());
  }
  
  public boolean isPointInChildBounds(View paramView, int paramInt1, int paramInt2)
  {
    Rect localRect = acquireTempRect();
    getDescendantRect(paramView, localRect);
    try
    {
      boolean bool = localRect.contains(paramInt1, paramInt2);
      return bool;
    }
    finally
    {
      releaseTempRect(localRect);
    }
  }
  
  void offsetChildToAnchor(View paramView, int paramInt)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    if (localLayoutParams.mAnchorView != null)
    {
      Rect localRect1 = acquireTempRect();
      Rect localRect2 = acquireTempRect();
      Rect localRect3 = acquireTempRect();
      getDescendantRect(localLayoutParams.mAnchorView, localRect1);
      getChildRect(paramView, false, localRect2);
      int i = paramView.getMeasuredWidth();
      int j = paramView.getMeasuredHeight();
      getDesiredAnchoredChildRectWithoutConstraints(paramView, paramInt, localRect1, localRect3, localLayoutParams, i, j);
      int k;
      if (localRect3.left == localRect2.left)
      {
        int i1 = localRect3.top;
        int i2 = localRect2.top;
        k = 0;
        if (i1 == i2) {}
      }
      else
      {
        k = 1;
      }
      constrainChildRect(localLayoutParams, localRect3, i, j);
      int m = localRect3.left - localRect2.left;
      int n = localRect3.top - localRect2.top;
      if (m != 0) {
        ViewCompat.offsetLeftAndRight(paramView, m);
      }
      if (n != 0) {
        ViewCompat.offsetTopAndBottom(paramView, n);
      }
      if (k != 0)
      {
        Behavior localBehavior = localLayoutParams.getBehavior();
        if (localBehavior != null) {
          localBehavior.onDependentViewChanged(this, paramView, localLayoutParams.mAnchorView);
        }
      }
      releaseTempRect(localRect1);
      releaseTempRect(localRect2);
      releaseTempRect(localRect3);
    }
  }
  
  public void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    resetTouchBehaviors();
    if (this.mNeedsPreDrawListener)
    {
      if (this.mOnPreDrawListener == null) {
        this.mOnPreDrawListener = new OnPreDrawListener();
      }
      getViewTreeObserver().addOnPreDrawListener(this.mOnPreDrawListener);
    }
    if ((this.mLastInsets == null) && (ViewCompat.getFitsSystemWindows(this))) {
      ViewCompat.requestApplyInsets(this);
    }
    this.mIsAttachedToWindow = true;
  }
  
  final void onChildViewsChanged(int paramInt)
  {
    int i = ViewCompat.getLayoutDirection(this);
    int j = this.mDependencySortedChildren.size();
    Rect localRect1 = acquireTempRect();
    Rect localRect2 = acquireTempRect();
    Rect localRect3 = acquireTempRect();
    int k = 0;
    while (k < j)
    {
      View localView1 = (View)this.mDependencySortedChildren.get(k);
      LayoutParams localLayoutParams1 = (LayoutParams)localView1.getLayoutParams();
      if ((paramInt == 0) && (localView1.getVisibility() == 8))
      {
        k++;
      }
      else
      {
        for (int m = 0; m < k; m++)
        {
          View localView3 = (View)this.mDependencySortedChildren.get(m);
          if (localLayoutParams1.mAnchorDirectChild == localView3) {
            offsetChildToAnchor(localView1, i);
          }
        }
        getChildRect(localView1, true, localRect2);
        if ((localLayoutParams1.insetEdge != 0) && (!localRect2.isEmpty()))
        {
          int i1 = GravityCompat.getAbsoluteGravity(localLayoutParams1.insetEdge, i);
          switch (i1 & 0x70)
          {
          default: 
            label200:
            switch (i1 & 0x7)
            {
            }
            break;
          }
        }
        View localView2;
        LayoutParams localLayoutParams2;
        Behavior localBehavior;
        for (;;)
        {
          if ((localLayoutParams1.dodgeInsetEdges != 0) && (localView1.getVisibility() == 0)) {
            offsetChildByInset(localView1, localRect1, i);
          }
          if (paramInt != 2)
          {
            getLastChildRect(localView1, localRect3);
            if (localRect3.equals(localRect2)) {
              break;
            }
            recordLastChildRect(localView1, localRect2);
          }
          for (int n = k + 1; n < j; n++)
          {
            localView2 = (View)this.mDependencySortedChildren.get(n);
            localLayoutParams2 = (LayoutParams)localView2.getLayoutParams();
            localBehavior = localLayoutParams2.getBehavior();
            if ((localBehavior != null) && (localBehavior.layoutDependsOn(this, localView2, localView1)))
            {
              if ((paramInt != 0) || (!localLayoutParams2.getChangedAfterNestedScroll())) {
                break label468;
              }
              localLayoutParams2.resetChangedAfterNestedScroll();
            }
          }
          localRect1.top = Math.max(localRect1.top, localRect2.bottom);
          break label200;
          localRect1.bottom = Math.max(localRect1.bottom, getHeight() - localRect2.top);
          break label200;
          localRect1.left = Math.max(localRect1.left, localRect2.right);
          continue;
          localRect1.right = Math.max(localRect1.right, getWidth() - localRect2.left);
        }
        label468:
        switch (paramInt)
        {
        }
        for (boolean bool = localBehavior.onDependentViewChanged(this, localView2, localView1); paramInt == 1; bool = true)
        {
          localLayoutParams2.setChangedAfterNestedScroll(bool);
          break;
          localBehavior.onDependentViewRemoved(this, localView2, localView1);
        }
      }
    }
    releaseTempRect(localRect1);
    releaseTempRect(localRect2);
    releaseTempRect(localRect3);
  }
  
  public void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    resetTouchBehaviors();
    if ((this.mNeedsPreDrawListener) && (this.mOnPreDrawListener != null)) {
      getViewTreeObserver().removeOnPreDrawListener(this.mOnPreDrawListener);
    }
    if (this.mNestedScrollingTarget != null) {
      onStopNestedScroll(this.mNestedScrollingTarget);
    }
    this.mIsAttachedToWindow = false;
  }
  
  public void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    if ((this.mDrawStatusBarBackground) && (this.mStatusBarBackground != null)) {
      if (this.mLastInsets == null) {
        break label61;
      }
    }
    label61:
    for (int i = this.mLastInsets.getSystemWindowInsetTop();; i = 0)
    {
      if (i > 0)
      {
        this.mStatusBarBackground.setBounds(0, 0, getWidth(), i);
        this.mStatusBarBackground.draw(paramCanvas);
      }
      return;
    }
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    int i = MotionEventCompat.getActionMasked(paramMotionEvent);
    if (i == 0) {
      resetTouchBehaviors();
    }
    boolean bool = performIntercept(paramMotionEvent, 0);
    if (0 != 0) {
      null.recycle();
    }
    if ((i == 1) || (i == 3)) {
      resetTouchBehaviors();
    }
    return bool;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = ViewCompat.getLayoutDirection(this);
    int j = this.mDependencySortedChildren.size();
    int k = 0;
    if (k < j)
    {
      View localView = (View)this.mDependencySortedChildren.get(k);
      if (localView.getVisibility() == 8) {}
      for (;;)
      {
        k++;
        break;
        Behavior localBehavior = ((LayoutParams)localView.getLayoutParams()).getBehavior();
        if ((localBehavior == null) || (!localBehavior.onLayoutChild(this, localView, i))) {
          onLayoutChild(localView, i);
        }
      }
    }
  }
  
  public void onLayoutChild(View paramView, int paramInt)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    if (localLayoutParams.checkAnchorChanged()) {
      throw new IllegalStateException("An anchor may not be changed after CoordinatorLayout measurement begins before layout is complete.");
    }
    if (localLayoutParams.mAnchorView != null)
    {
      layoutChildWithAnchor(paramView, localLayoutParams.mAnchorView, paramInt);
      return;
    }
    if (localLayoutParams.keyline >= 0)
    {
      layoutChildWithKeyline(paramView, localLayoutParams.keyline, paramInt);
      return;
    }
    layoutChild(paramView, paramInt);
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    prepareChildren();
    ensurePreDrawListener();
    int i = getPaddingLeft();
    int j = getPaddingTop();
    int k = getPaddingRight();
    int m = getPaddingBottom();
    int n = ViewCompat.getLayoutDirection(this);
    int i1;
    int i2;
    int i3;
    int i4;
    int i5;
    int i6;
    int i7;
    int i8;
    int i9;
    int i10;
    if (n == 1)
    {
      i1 = 1;
      i2 = View.MeasureSpec.getMode(paramInt1);
      i3 = View.MeasureSpec.getSize(paramInt1);
      i4 = View.MeasureSpec.getMode(paramInt2);
      i5 = View.MeasureSpec.getSize(paramInt2);
      i6 = i + k;
      i7 = j + m;
      i8 = getSuggestedMinimumWidth();
      i9 = getSuggestedMinimumHeight();
      i10 = 0;
      if ((this.mLastInsets == null) || (!ViewCompat.getFitsSystemWindows(this))) {
        break label174;
      }
    }
    View localView;
    label174:
    for (int i11 = 1;; i11 = 0)
    {
      int i12 = this.mDependencySortedChildren.size();
      for (int i13 = 0;; i13++)
      {
        if (i13 >= i12) {
          break label516;
        }
        localView = (View)this.mDependencySortedChildren.get(i13);
        if (localView.getVisibility() != 8) {
          break;
        }
      }
      i1 = 0;
      break;
    }
    LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
    int i17 = localLayoutParams.keyline;
    int i18 = 0;
    int i25;
    int i26;
    if (i17 >= 0)
    {
      i18 = 0;
      if (i2 != 0)
      {
        i25 = getKeyline(localLayoutParams.keyline);
        i26 = 0x7 & GravityCompat.getAbsoluteGravity(resolveKeylineGravity(localLayoutParams.gravity), n);
        if (((i26 != 3) || (i1 != 0)) && ((i26 != 5) || (i1 == 0))) {
          break label475;
        }
        i18 = Math.max(0, i3 - k - i25);
      }
    }
    for (;;)
    {
      int i19 = paramInt1;
      int i20 = paramInt2;
      if ((i11 != 0) && (!ViewCompat.getFitsSystemWindows(localView)))
      {
        int i23 = this.mLastInsets.getSystemWindowInsetLeft() + this.mLastInsets.getSystemWindowInsetRight();
        int i24 = this.mLastInsets.getSystemWindowInsetTop() + this.mLastInsets.getSystemWindowInsetBottom();
        i19 = View.MeasureSpec.makeMeasureSpec(i3 - i23, i2);
        i20 = View.MeasureSpec.makeMeasureSpec(i5 - i24, i4);
      }
      Behavior localBehavior = localLayoutParams.getBehavior();
      if ((localBehavior == null) || (!localBehavior.onMeasureChild(this, localView, i19, i18, i20, 0))) {
        onMeasureChild(localView, i19, i18, i20, 0);
      }
      int i21 = i6 + localView.getMeasuredWidth() + localLayoutParams.leftMargin + localLayoutParams.rightMargin;
      i8 = Math.max(i8, i21);
      int i22 = i7 + localView.getMeasuredHeight() + localLayoutParams.topMargin + localLayoutParams.bottomMargin;
      i9 = Math.max(i9, i22);
      i10 = ViewCompat.combineMeasuredStates(i10, ViewCompat.getMeasuredState(localView));
      break;
      label475:
      if ((i26 != 5) || (i1 != 0))
      {
        i18 = 0;
        if (i26 == 3)
        {
          i18 = 0;
          if (i1 == 0) {}
        }
      }
      else
      {
        i18 = Math.max(0, i25 - i);
      }
    }
    label516:
    int i14 = 0xFF000000 & i10;
    int i15 = ViewCompat.resolveSizeAndState(i8, paramInt1, i14);
    int i16 = i10 << 16;
    setMeasuredDimension(i15, ViewCompat.resolveSizeAndState(i9, paramInt2, i16));
  }
  
  public void onMeasureChild(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    measureChildWithMargins(paramView, paramInt1, paramInt2, paramInt3, paramInt4);
  }
  
  public boolean onNestedFling(View paramView, float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    boolean bool = false;
    int i = getChildCount();
    int j = 0;
    if (j < i)
    {
      View localView = getChildAt(j);
      if (localView.getVisibility() == 8) {}
      for (;;)
      {
        j++;
        break;
        LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
        if (localLayoutParams.isNestedScrollAccepted())
        {
          Behavior localBehavior = localLayoutParams.getBehavior();
          if (localBehavior != null) {
            bool |= localBehavior.onNestedFling(this, localView, paramView, paramFloat1, paramFloat2, paramBoolean);
          }
        }
      }
    }
    if (bool) {
      onChildViewsChanged(1);
    }
    return bool;
  }
  
  public boolean onNestedPreFling(View paramView, float paramFloat1, float paramFloat2)
  {
    boolean bool = false;
    int i = getChildCount();
    int j = 0;
    if (j < i)
    {
      View localView = getChildAt(j);
      if (localView.getVisibility() == 8) {}
      for (;;)
      {
        j++;
        break;
        LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
        if (localLayoutParams.isNestedScrollAccepted())
        {
          Behavior localBehavior = localLayoutParams.getBehavior();
          if (localBehavior != null) {
            bool |= localBehavior.onNestedPreFling(this, localView, paramView, paramFloat1, paramFloat2);
          }
        }
      }
    }
    return bool;
  }
  
  public void onNestedPreScroll(View paramView, int paramInt1, int paramInt2, int[] paramArrayOfInt)
  {
    int i = 0;
    int j = 0;
    int k = 0;
    int m = getChildCount();
    int n = 0;
    if (n < m)
    {
      View localView = getChildAt(n);
      if (localView.getVisibility() == 8) {}
      Behavior localBehavior;
      do
      {
        LayoutParams localLayoutParams;
        do
        {
          n++;
          break;
          localLayoutParams = (LayoutParams)localView.getLayoutParams();
        } while (!localLayoutParams.isNestedScrollAccepted());
        localBehavior = localLayoutParams.getBehavior();
      } while (localBehavior == null);
      int[] arrayOfInt = this.mTempIntPair;
      this.mTempIntPair[1] = 0;
      arrayOfInt[0] = 0;
      localBehavior.onNestedPreScroll(this, localView, paramView, paramInt1, paramInt2, this.mTempIntPair);
      if (paramInt1 > 0)
      {
        i = Math.max(i, this.mTempIntPair[0]);
        label129:
        if (paramInt2 <= 0) {
          break label168;
        }
      }
      label168:
      for (j = Math.max(j, this.mTempIntPair[1]);; j = Math.min(j, this.mTempIntPair[1]))
      {
        k = 1;
        break;
        i = Math.min(i, this.mTempIntPair[0]);
        break label129;
      }
    }
    paramArrayOfInt[0] = i;
    paramArrayOfInt[1] = j;
    if (k != 0) {
      onChildViewsChanged(1);
    }
  }
  
  public void onNestedScroll(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = getChildCount();
    int j = 0;
    int k = 0;
    if (k < i)
    {
      View localView = getChildAt(k);
      if (localView.getVisibility() == 8) {}
      for (;;)
      {
        k++;
        break;
        LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
        if (localLayoutParams.isNestedScrollAccepted())
        {
          Behavior localBehavior = localLayoutParams.getBehavior();
          if (localBehavior != null)
          {
            localBehavior.onNestedScroll(this, localView, paramView, paramInt1, paramInt2, paramInt3, paramInt4);
            j = 1;
          }
        }
      }
    }
    if (j != 0) {
      onChildViewsChanged(1);
    }
  }
  
  public void onNestedScrollAccepted(View paramView1, View paramView2, int paramInt)
  {
    this.mNestedScrollingParentHelper.onNestedScrollAccepted(paramView1, paramView2, paramInt);
    this.mNestedScrollingDirectChild = paramView1;
    this.mNestedScrollingTarget = paramView2;
    int i = getChildCount();
    int j = 0;
    if (j < i)
    {
      View localView = getChildAt(j);
      LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
      if (!localLayoutParams.isNestedScrollAccepted()) {}
      for (;;)
      {
        j++;
        break;
        Behavior localBehavior = localLayoutParams.getBehavior();
        if (localBehavior != null) {
          localBehavior.onNestedScrollAccepted(this, localView, paramView1, paramView2, paramInt);
        }
      }
    }
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if (!(paramParcelable instanceof SavedState)) {
      super.onRestoreInstanceState(paramParcelable);
    }
    for (;;)
    {
      return;
      SavedState localSavedState = (SavedState)paramParcelable;
      super.onRestoreInstanceState(localSavedState.getSuperState());
      SparseArray localSparseArray = localSavedState.behaviorStates;
      int i = 0;
      int j = getChildCount();
      while (i < j)
      {
        View localView = getChildAt(i);
        int k = localView.getId();
        Behavior localBehavior = getResolvedLayoutParams(localView).getBehavior();
        if ((k != -1) && (localBehavior != null))
        {
          Parcelable localParcelable = (Parcelable)localSparseArray.get(k);
          if (localParcelable != null) {
            localBehavior.onRestoreInstanceState(this, localView, localParcelable);
          }
        }
        i++;
      }
    }
  }
  
  protected Parcelable onSaveInstanceState()
  {
    SavedState localSavedState = new SavedState(super.onSaveInstanceState());
    SparseArray localSparseArray = new SparseArray();
    int i = 0;
    int j = getChildCount();
    while (i < j)
    {
      View localView = getChildAt(i);
      int k = localView.getId();
      Behavior localBehavior = ((LayoutParams)localView.getLayoutParams()).getBehavior();
      if ((k != -1) && (localBehavior != null))
      {
        Parcelable localParcelable = localBehavior.onSaveInstanceState(this, localView);
        if (localParcelable != null) {
          localSparseArray.append(k, localParcelable);
        }
      }
      i++;
    }
    localSavedState.behaviorStates = localSparseArray;
    return localSavedState;
  }
  
  public boolean onStartNestedScroll(View paramView1, View paramView2, int paramInt)
  {
    boolean bool1 = false;
    int i = getChildCount();
    int j = 0;
    if (j < i)
    {
      View localView = getChildAt(j);
      if (localView.getVisibility() == 8) {}
      for (;;)
      {
        j++;
        break;
        LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
        Behavior localBehavior = localLayoutParams.getBehavior();
        if (localBehavior != null)
        {
          boolean bool2 = localBehavior.onStartNestedScroll(this, localView, paramView1, paramView2, paramInt);
          bool1 |= bool2;
          localLayoutParams.acceptNestedScroll(bool2);
        }
        else
        {
          localLayoutParams.acceptNestedScroll(false);
        }
      }
    }
    return bool1;
  }
  
  public void onStopNestedScroll(View paramView)
  {
    this.mNestedScrollingParentHelper.onStopNestedScroll(paramView);
    int i = getChildCount();
    int j = 0;
    if (j < i)
    {
      View localView = getChildAt(j);
      LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
      if (!localLayoutParams.isNestedScrollAccepted()) {}
      for (;;)
      {
        j++;
        break;
        Behavior localBehavior = localLayoutParams.getBehavior();
        if (localBehavior != null) {
          localBehavior.onStopNestedScroll(this, localView, paramView);
        }
        localLayoutParams.resetNestedScroll();
        localLayoutParams.resetChangedAfterNestedScroll();
      }
    }
    this.mNestedScrollingDirectChild = null;
    this.mNestedScrollingTarget = null;
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    MotionEvent localMotionEvent = null;
    int i = MotionEventCompat.getActionMasked(paramMotionEvent);
    View localView = this.mBehaviorTouchView;
    boolean bool1 = false;
    boolean bool2;
    if (localView == null)
    {
      bool1 = performIntercept(paramMotionEvent, 1);
      bool2 = false;
      if (!bool1) {}
    }
    else
    {
      Behavior localBehavior = ((LayoutParams)this.mBehaviorTouchView.getLayoutParams()).getBehavior();
      bool2 = false;
      if (localBehavior != null) {
        bool2 = localBehavior.onTouchEvent(this, this.mBehaviorTouchView, paramMotionEvent);
      }
    }
    if (this.mBehaviorTouchView == null) {
      bool2 |= super.onTouchEvent(paramMotionEvent);
    }
    for (;;)
    {
      if (((bool2) || (i != 0)) || (localMotionEvent != null)) {
        localMotionEvent.recycle();
      }
      if ((i == 1) || (i == 3)) {
        resetTouchBehaviors();
      }
      return bool2;
      localMotionEvent = null;
      if (bool1)
      {
        localMotionEvent = null;
        if (0 == 0)
        {
          long l = SystemClock.uptimeMillis();
          localMotionEvent = MotionEvent.obtain(l, l, 3, 0.0F, 0.0F, 0);
        }
        super.onTouchEvent(localMotionEvent);
      }
    }
  }
  
  void recordLastChildRect(View paramView, Rect paramRect)
  {
    ((LayoutParams)paramView.getLayoutParams()).setLastChildRect(paramRect);
  }
  
  void removePreDrawListener()
  {
    if ((this.mIsAttachedToWindow) && (this.mOnPreDrawListener != null)) {
      getViewTreeObserver().removeOnPreDrawListener(this.mOnPreDrawListener);
    }
    this.mNeedsPreDrawListener = false;
  }
  
  public boolean requestChildRectangleOnScreen(View paramView, Rect paramRect, boolean paramBoolean)
  {
    Behavior localBehavior = ((LayoutParams)paramView.getLayoutParams()).getBehavior();
    if ((localBehavior != null) && (localBehavior.onRequestChildRectangleOnScreen(this, paramView, paramRect, paramBoolean))) {
      return true;
    }
    return super.requestChildRectangleOnScreen(paramView, paramRect, paramBoolean);
  }
  
  public void requestDisallowInterceptTouchEvent(boolean paramBoolean)
  {
    super.requestDisallowInterceptTouchEvent(paramBoolean);
    if ((paramBoolean) && (!this.mDisallowInterceptReset))
    {
      resetTouchBehaviors();
      this.mDisallowInterceptReset = true;
    }
  }
  
  public void setFitsSystemWindows(boolean paramBoolean)
  {
    super.setFitsSystemWindows(paramBoolean);
    setupForInsets();
  }
  
  public void setOnHierarchyChangeListener(ViewGroup.OnHierarchyChangeListener paramOnHierarchyChangeListener)
  {
    this.mOnHierarchyChangeListener = paramOnHierarchyChangeListener;
  }
  
  public void setStatusBarBackground(@Nullable Drawable paramDrawable)
  {
    Drawable localDrawable2;
    if (this.mStatusBarBackground != paramDrawable)
    {
      if (this.mStatusBarBackground != null) {
        this.mStatusBarBackground.setCallback(null);
      }
      Drawable localDrawable1 = null;
      if (paramDrawable != null) {
        localDrawable1 = paramDrawable.mutate();
      }
      this.mStatusBarBackground = localDrawable1;
      if (this.mStatusBarBackground != null)
      {
        if (this.mStatusBarBackground.isStateful()) {
          this.mStatusBarBackground.setState(getDrawableState());
        }
        DrawableCompat.setLayoutDirection(this.mStatusBarBackground, ViewCompat.getLayoutDirection(this));
        localDrawable2 = this.mStatusBarBackground;
        if (getVisibility() != 0) {
          break label118;
        }
      }
    }
    label118:
    for (boolean bool = true;; bool = false)
    {
      localDrawable2.setVisible(bool, false);
      this.mStatusBarBackground.setCallback(this);
      ViewCompat.postInvalidateOnAnimation(this);
      return;
    }
  }
  
  public void setStatusBarBackgroundColor(@ColorInt int paramInt)
  {
    setStatusBarBackground(new ColorDrawable(paramInt));
  }
  
  public void setStatusBarBackgroundResource(@DrawableRes int paramInt)
  {
    if (paramInt != 0) {}
    for (Drawable localDrawable = ContextCompat.getDrawable(getContext(), paramInt);; localDrawable = null)
    {
      setStatusBarBackground(localDrawable);
      return;
    }
  }
  
  public void setVisibility(int paramInt)
  {
    super.setVisibility(paramInt);
    if (paramInt == 0) {}
    for (boolean bool = true;; bool = false)
    {
      if ((this.mStatusBarBackground != null) && (this.mStatusBarBackground.isVisible() != bool)) {
        this.mStatusBarBackground.setVisible(bool, false);
      }
      return;
    }
  }
  
  final WindowInsetsCompat setWindowInsets(WindowInsetsCompat paramWindowInsetsCompat)
  {
    boolean bool1 = true;
    boolean bool2;
    if (!ViewUtils.objectEquals(this.mLastInsets, paramWindowInsetsCompat))
    {
      this.mLastInsets = paramWindowInsetsCompat;
      if ((paramWindowInsetsCompat == null) || (paramWindowInsetsCompat.getSystemWindowInsetTop() <= 0)) {
        break label67;
      }
      bool2 = bool1;
      this.mDrawStatusBarBackground = bool2;
      if ((this.mDrawStatusBarBackground) || (getBackground() != null)) {
        break label72;
      }
    }
    for (;;)
    {
      setWillNotDraw(bool1);
      paramWindowInsetsCompat = dispatchApplyWindowInsetsToBehaviors(paramWindowInsetsCompat);
      requestLayout();
      return paramWindowInsetsCompat;
      label67:
      bool2 = false;
      break;
      label72:
      bool1 = false;
    }
  }
  
  protected boolean verifyDrawable(Drawable paramDrawable)
  {
    return (super.verifyDrawable(paramDrawable)) || (paramDrawable == this.mStatusBarBackground);
  }
  
  public static abstract class Behavior<V extends View>
  {
    public Behavior() {}
    
    public Behavior(Context paramContext, AttributeSet paramAttributeSet) {}
    
    public static Object getTag(View paramView)
    {
      return ((CoordinatorLayout.LayoutParams)paramView.getLayoutParams()).mBehaviorTag;
    }
    
    public static void setTag(View paramView, Object paramObject)
    {
      ((CoordinatorLayout.LayoutParams)paramView.getLayoutParams()).mBehaviorTag = paramObject;
    }
    
    public boolean blocksInteractionBelow(CoordinatorLayout paramCoordinatorLayout, V paramV)
    {
      return getScrimOpacity(paramCoordinatorLayout, paramV) > 0.0F;
    }
    
    public boolean getInsetDodgeRect(@NonNull CoordinatorLayout paramCoordinatorLayout, @NonNull V paramV, @NonNull Rect paramRect)
    {
      return false;
    }
    
    @ColorInt
    public int getScrimColor(CoordinatorLayout paramCoordinatorLayout, V paramV)
    {
      return -16777216;
    }
    
    @FloatRange(from=0.0D, to=1.0D)
    public float getScrimOpacity(CoordinatorLayout paramCoordinatorLayout, V paramV)
    {
      return 0.0F;
    }
    
    @Deprecated
    public boolean isDirty(CoordinatorLayout paramCoordinatorLayout, V paramV)
    {
      return false;
    }
    
    public boolean layoutDependsOn(CoordinatorLayout paramCoordinatorLayout, V paramV, View paramView)
    {
      return false;
    }
    
    @NonNull
    public WindowInsetsCompat onApplyWindowInsets(CoordinatorLayout paramCoordinatorLayout, V paramV, WindowInsetsCompat paramWindowInsetsCompat)
    {
      return paramWindowInsetsCompat;
    }
    
    public void onAttachedToLayoutParams(@NonNull CoordinatorLayout.LayoutParams paramLayoutParams) {}
    
    public boolean onDependentViewChanged(CoordinatorLayout paramCoordinatorLayout, V paramV, View paramView)
    {
      return false;
    }
    
    public void onDependentViewRemoved(CoordinatorLayout paramCoordinatorLayout, V paramV, View paramView) {}
    
    public void onDetachedFromLayoutParams() {}
    
    public boolean onInterceptTouchEvent(CoordinatorLayout paramCoordinatorLayout, V paramV, MotionEvent paramMotionEvent)
    {
      return false;
    }
    
    public boolean onLayoutChild(CoordinatorLayout paramCoordinatorLayout, V paramV, int paramInt)
    {
      return false;
    }
    
    public boolean onMeasureChild(CoordinatorLayout paramCoordinatorLayout, V paramV, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      return false;
    }
    
    public boolean onNestedFling(CoordinatorLayout paramCoordinatorLayout, V paramV, View paramView, float paramFloat1, float paramFloat2, boolean paramBoolean)
    {
      return false;
    }
    
    public boolean onNestedPreFling(CoordinatorLayout paramCoordinatorLayout, V paramV, View paramView, float paramFloat1, float paramFloat2)
    {
      return false;
    }
    
    public void onNestedPreScroll(CoordinatorLayout paramCoordinatorLayout, V paramV, View paramView, int paramInt1, int paramInt2, int[] paramArrayOfInt) {}
    
    public void onNestedScroll(CoordinatorLayout paramCoordinatorLayout, V paramV, View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {}
    
    public void onNestedScrollAccepted(CoordinatorLayout paramCoordinatorLayout, V paramV, View paramView1, View paramView2, int paramInt) {}
    
    public boolean onRequestChildRectangleOnScreen(CoordinatorLayout paramCoordinatorLayout, V paramV, Rect paramRect, boolean paramBoolean)
    {
      return false;
    }
    
    public void onRestoreInstanceState(CoordinatorLayout paramCoordinatorLayout, V paramV, Parcelable paramParcelable) {}
    
    public Parcelable onSaveInstanceState(CoordinatorLayout paramCoordinatorLayout, V paramV)
    {
      return View.BaseSavedState.EMPTY_STATE;
    }
    
    public boolean onStartNestedScroll(CoordinatorLayout paramCoordinatorLayout, V paramV, View paramView1, View paramView2, int paramInt)
    {
      return false;
    }
    
    public void onStopNestedScroll(CoordinatorLayout paramCoordinatorLayout, V paramV, View paramView) {}
    
    public boolean onTouchEvent(CoordinatorLayout paramCoordinatorLayout, V paramV, MotionEvent paramMotionEvent)
    {
      return false;
    }
  }
  
  @Retention(RetentionPolicy.RUNTIME)
  public static @interface DefaultBehavior
  {
    Class<? extends CoordinatorLayout.Behavior> value();
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public static @interface DispatchChangeEvent {}
  
  private class HierarchyChangeListener
    implements ViewGroup.OnHierarchyChangeListener
  {
    HierarchyChangeListener() {}
    
    public void onChildViewAdded(View paramView1, View paramView2)
    {
      if (CoordinatorLayout.this.mOnHierarchyChangeListener != null) {
        CoordinatorLayout.this.mOnHierarchyChangeListener.onChildViewAdded(paramView1, paramView2);
      }
    }
    
    public void onChildViewRemoved(View paramView1, View paramView2)
    {
      CoordinatorLayout.this.onChildViewsChanged(2);
      if (CoordinatorLayout.this.mOnHierarchyChangeListener != null) {
        CoordinatorLayout.this.mOnHierarchyChangeListener.onChildViewRemoved(paramView1, paramView2);
      }
    }
  }
  
  public static class LayoutParams
    extends ViewGroup.MarginLayoutParams
  {
    public int anchorGravity = 0;
    public int dodgeInsetEdges = 0;
    public int gravity = 0;
    public int insetEdge = 0;
    public int keyline = -1;
    View mAnchorDirectChild;
    int mAnchorId = -1;
    View mAnchorView;
    CoordinatorLayout.Behavior mBehavior;
    boolean mBehaviorResolved = false;
    Object mBehaviorTag;
    private boolean mDidAcceptNestedScroll;
    private boolean mDidBlockInteraction;
    private boolean mDidChangeAfterNestedScroll;
    int mInsetOffsetX;
    int mInsetOffsetY;
    final Rect mLastChildRect = new Rect();
    
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
    }
    
    LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
      TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.CoordinatorLayout_Layout);
      this.gravity = localTypedArray.getInteger(R.styleable.CoordinatorLayout_Layout_android_layout_gravity, 0);
      this.mAnchorId = localTypedArray.getResourceId(R.styleable.CoordinatorLayout_Layout_layout_anchor, -1);
      this.anchorGravity = localTypedArray.getInteger(R.styleable.CoordinatorLayout_Layout_layout_anchorGravity, 0);
      this.keyline = localTypedArray.getInteger(R.styleable.CoordinatorLayout_Layout_layout_keyline, -1);
      this.insetEdge = localTypedArray.getInt(R.styleable.CoordinatorLayout_Layout_layout_insetEdge, 0);
      this.dodgeInsetEdges = localTypedArray.getInt(R.styleable.CoordinatorLayout_Layout_layout_dodgeInsetEdges, 0);
      this.mBehaviorResolved = localTypedArray.hasValue(R.styleable.CoordinatorLayout_Layout_layout_behavior);
      if (this.mBehaviorResolved) {
        this.mBehavior = CoordinatorLayout.parseBehavior(paramContext, paramAttributeSet, localTypedArray.getString(R.styleable.CoordinatorLayout_Layout_layout_behavior));
      }
      localTypedArray.recycle();
      if (this.mBehavior != null) {
        this.mBehavior.onAttachedToLayoutParams(this);
      }
    }
    
    public LayoutParams(LayoutParams paramLayoutParams)
    {
      super();
    }
    
    public LayoutParams(ViewGroup.LayoutParams paramLayoutParams)
    {
      super();
    }
    
    public LayoutParams(ViewGroup.MarginLayoutParams paramMarginLayoutParams)
    {
      super();
    }
    
    private void resolveAnchorView(View paramView, CoordinatorLayout paramCoordinatorLayout)
    {
      this.mAnchorView = paramCoordinatorLayout.findViewById(this.mAnchorId);
      if (this.mAnchorView != null)
      {
        if (this.mAnchorView == paramCoordinatorLayout)
        {
          if (paramCoordinatorLayout.isInEditMode())
          {
            this.mAnchorDirectChild = null;
            this.mAnchorView = null;
            return;
          }
          throw new IllegalStateException("View can not be anchored to the the parent CoordinatorLayout");
        }
        View localView = this.mAnchorView;
        for (ViewParent localViewParent = this.mAnchorView.getParent(); (localViewParent != paramCoordinatorLayout) && (localViewParent != null); localViewParent = localViewParent.getParent())
        {
          if (localViewParent == paramView)
          {
            if (paramCoordinatorLayout.isInEditMode())
            {
              this.mAnchorDirectChild = null;
              this.mAnchorView = null;
              return;
            }
            throw new IllegalStateException("Anchor must not be a descendant of the anchored view");
          }
          if ((localViewParent instanceof View)) {
            localView = (View)localViewParent;
          }
        }
        this.mAnchorDirectChild = localView;
        return;
      }
      if (paramCoordinatorLayout.isInEditMode())
      {
        this.mAnchorDirectChild = null;
        this.mAnchorView = null;
        return;
      }
      throw new IllegalStateException("Could not find CoordinatorLayout descendant view with id " + paramCoordinatorLayout.getResources().getResourceName(this.mAnchorId) + " to anchor view " + paramView);
    }
    
    private boolean shouldDodge(View paramView, int paramInt)
    {
      int i = GravityCompat.getAbsoluteGravity(((LayoutParams)paramView.getLayoutParams()).insetEdge, paramInt);
      return (i != 0) && ((i & GravityCompat.getAbsoluteGravity(this.dodgeInsetEdges, paramInt)) == i);
    }
    
    private boolean verifyAnchorView(View paramView, CoordinatorLayout paramCoordinatorLayout)
    {
      if (this.mAnchorView.getId() != this.mAnchorId) {
        return false;
      }
      View localView = this.mAnchorView;
      for (ViewParent localViewParent = this.mAnchorView.getParent(); localViewParent != paramCoordinatorLayout; localViewParent = localViewParent.getParent())
      {
        if ((localViewParent == null) || (localViewParent == paramView))
        {
          this.mAnchorDirectChild = null;
          this.mAnchorView = null;
          return false;
        }
        if ((localViewParent instanceof View)) {
          localView = (View)localViewParent;
        }
      }
      this.mAnchorDirectChild = localView;
      return true;
    }
    
    void acceptNestedScroll(boolean paramBoolean)
    {
      this.mDidAcceptNestedScroll = paramBoolean;
    }
    
    boolean checkAnchorChanged()
    {
      return (this.mAnchorView == null) && (this.mAnchorId != -1);
    }
    
    boolean dependsOn(CoordinatorLayout paramCoordinatorLayout, View paramView1, View paramView2)
    {
      return (paramView2 == this.mAnchorDirectChild) || (shouldDodge(paramView2, ViewCompat.getLayoutDirection(paramCoordinatorLayout))) || ((this.mBehavior != null) && (this.mBehavior.layoutDependsOn(paramCoordinatorLayout, paramView1, paramView2)));
    }
    
    boolean didBlockInteraction()
    {
      if (this.mBehavior == null) {
        this.mDidBlockInteraction = false;
      }
      return this.mDidBlockInteraction;
    }
    
    View findAnchorView(CoordinatorLayout paramCoordinatorLayout, View paramView)
    {
      if (this.mAnchorId == -1)
      {
        this.mAnchorDirectChild = null;
        this.mAnchorView = null;
        return null;
      }
      if ((this.mAnchorView == null) || (!verifyAnchorView(paramView, paramCoordinatorLayout))) {
        resolveAnchorView(paramView, paramCoordinatorLayout);
      }
      return this.mAnchorView;
    }
    
    @IdRes
    public int getAnchorId()
    {
      return this.mAnchorId;
    }
    
    @Nullable
    public CoordinatorLayout.Behavior getBehavior()
    {
      return this.mBehavior;
    }
    
    boolean getChangedAfterNestedScroll()
    {
      return this.mDidChangeAfterNestedScroll;
    }
    
    Rect getLastChildRect()
    {
      return this.mLastChildRect;
    }
    
    void invalidateAnchor()
    {
      this.mAnchorDirectChild = null;
      this.mAnchorView = null;
    }
    
    boolean isBlockingInteractionBelow(CoordinatorLayout paramCoordinatorLayout, View paramView)
    {
      if (this.mDidBlockInteraction) {
        return true;
      }
      boolean bool1 = this.mDidBlockInteraction;
      if (this.mBehavior != null) {}
      for (boolean bool2 = this.mBehavior.blocksInteractionBelow(paramCoordinatorLayout, paramView);; bool2 = false)
      {
        boolean bool3 = bool2 | bool1;
        this.mDidBlockInteraction = bool3;
        return bool3;
      }
    }
    
    boolean isNestedScrollAccepted()
    {
      return this.mDidAcceptNestedScroll;
    }
    
    void resetChangedAfterNestedScroll()
    {
      this.mDidChangeAfterNestedScroll = false;
    }
    
    void resetNestedScroll()
    {
      this.mDidAcceptNestedScroll = false;
    }
    
    void resetTouchBehaviorTracking()
    {
      this.mDidBlockInteraction = false;
    }
    
    public void setAnchorId(@IdRes int paramInt)
    {
      invalidateAnchor();
      this.mAnchorId = paramInt;
    }
    
    public void setBehavior(@Nullable CoordinatorLayout.Behavior paramBehavior)
    {
      if (this.mBehavior != paramBehavior)
      {
        if (this.mBehavior != null) {
          this.mBehavior.onDetachedFromLayoutParams();
        }
        this.mBehavior = paramBehavior;
        this.mBehaviorTag = null;
        this.mBehaviorResolved = true;
        if (paramBehavior != null) {
          paramBehavior.onAttachedToLayoutParams(this);
        }
      }
    }
    
    void setChangedAfterNestedScroll(boolean paramBoolean)
    {
      this.mDidChangeAfterNestedScroll = paramBoolean;
    }
    
    void setLastChildRect(Rect paramRect)
    {
      this.mLastChildRect.set(paramRect);
    }
  }
  
  class OnPreDrawListener
    implements ViewTreeObserver.OnPreDrawListener
  {
    OnPreDrawListener() {}
    
    public boolean onPreDraw()
    {
      CoordinatorLayout.this.onChildViewsChanged(0);
      return true;
    }
  }
  
  protected static class SavedState
    extends AbsSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks()
    {
      public CoordinatorLayout.SavedState createFromParcel(Parcel paramAnonymousParcel, ClassLoader paramAnonymousClassLoader)
      {
        return new CoordinatorLayout.SavedState(paramAnonymousParcel, paramAnonymousClassLoader);
      }
      
      public CoordinatorLayout.SavedState[] newArray(int paramAnonymousInt)
      {
        return new CoordinatorLayout.SavedState[paramAnonymousInt];
      }
    });
    SparseArray<Parcelable> behaviorStates;
    
    public SavedState(Parcel paramParcel, ClassLoader paramClassLoader)
    {
      super(paramClassLoader);
      int i = paramParcel.readInt();
      int[] arrayOfInt = new int[i];
      paramParcel.readIntArray(arrayOfInt);
      Parcelable[] arrayOfParcelable = paramParcel.readParcelableArray(paramClassLoader);
      this.behaviorStates = new SparseArray(i);
      for (int j = 0; j < i; j++) {
        this.behaviorStates.append(arrayOfInt[j], arrayOfParcelable[j]);
      }
    }
    
    public SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      if (this.behaviorStates != null) {}
      int[] arrayOfInt;
      Parcelable[] arrayOfParcelable;
      for (int i = this.behaviorStates.size();; i = 0)
      {
        paramParcel.writeInt(i);
        arrayOfInt = new int[i];
        arrayOfParcelable = new Parcelable[i];
        for (int j = 0; j < i; j++)
        {
          arrayOfInt[j] = this.behaviorStates.keyAt(j);
          arrayOfParcelable[j] = ((Parcelable)this.behaviorStates.valueAt(j));
        }
      }
      paramParcel.writeIntArray(arrayOfInt);
      paramParcel.writeParcelableArray(arrayOfParcelable, paramInt);
    }
  }
  
  static class ViewElevationComparator
    implements Comparator<View>
  {
    ViewElevationComparator() {}
    
    public int compare(View paramView1, View paramView2)
    {
      float f1 = ViewCompat.getZ(paramView1);
      float f2 = ViewCompat.getZ(paramView2);
      if (f1 > f2) {
        return -1;
      }
      if (f1 < f2) {
        return 1;
      }
      return 0;
    }
  }
}
