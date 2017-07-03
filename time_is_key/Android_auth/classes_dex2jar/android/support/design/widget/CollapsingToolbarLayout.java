package android.support.design.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.support.design.R.id;
import android.support.design.R.styleable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

public class CollapsingToolbarLayout
  extends FrameLayout
{
  private static final int DEFAULT_SCRIM_ANIMATION_DURATION = 600;
  final CollapsingTextHelper mCollapsingTextHelper;
  private boolean mCollapsingTitleEnabled;
  private Drawable mContentScrim;
  int mCurrentOffset;
  private boolean mDrawCollapsingTitle;
  private View mDummyView;
  private int mExpandedMarginBottom;
  private int mExpandedMarginEnd;
  private int mExpandedMarginStart;
  private int mExpandedMarginTop;
  WindowInsetsCompat mLastInsets;
  private AppBarLayout.OnOffsetChangedListener mOnOffsetChangedListener;
  private boolean mRefreshToolbar = true;
  private int mScrimAlpha;
  private long mScrimAnimationDuration;
  private ValueAnimatorCompat mScrimAnimator;
  private int mScrimVisibleHeightTrigger = -1;
  private boolean mScrimsAreShown;
  Drawable mStatusBarScrim;
  private final Rect mTmpRect = new Rect();
  private Toolbar mToolbar;
  private View mToolbarDirectChild;
  private int mToolbarId;
  
  public CollapsingToolbarLayout(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public CollapsingToolbarLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public CollapsingToolbarLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    ThemeUtils.checkAppCompatTheme(paramContext);
    this.mCollapsingTextHelper = new CollapsingTextHelper(this);
    this.mCollapsingTextHelper.setTextSizeInterpolator(AnimationUtils.DECELERATE_INTERPOLATOR);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.CollapsingToolbarLayout, paramInt, android.support.design.R.style.Widget_Design_CollapsingToolbar);
    this.mCollapsingTextHelper.setExpandedTextGravity(localTypedArray.getInt(R.styleable.CollapsingToolbarLayout_expandedTitleGravity, 8388691));
    this.mCollapsingTextHelper.setCollapsedTextGravity(localTypedArray.getInt(R.styleable.CollapsingToolbarLayout_collapsedTitleGravity, 8388627));
    int i = localTypedArray.getDimensionPixelSize(R.styleable.CollapsingToolbarLayout_expandedTitleMargin, 0);
    this.mExpandedMarginBottom = i;
    this.mExpandedMarginEnd = i;
    this.mExpandedMarginTop = i;
    this.mExpandedMarginStart = i;
    if (localTypedArray.hasValue(R.styleable.CollapsingToolbarLayout_expandedTitleMarginStart)) {
      this.mExpandedMarginStart = localTypedArray.getDimensionPixelSize(R.styleable.CollapsingToolbarLayout_expandedTitleMarginStart, 0);
    }
    if (localTypedArray.hasValue(R.styleable.CollapsingToolbarLayout_expandedTitleMarginEnd)) {
      this.mExpandedMarginEnd = localTypedArray.getDimensionPixelSize(R.styleable.CollapsingToolbarLayout_expandedTitleMarginEnd, 0);
    }
    if (localTypedArray.hasValue(R.styleable.CollapsingToolbarLayout_expandedTitleMarginTop)) {
      this.mExpandedMarginTop = localTypedArray.getDimensionPixelSize(R.styleable.CollapsingToolbarLayout_expandedTitleMarginTop, 0);
    }
    if (localTypedArray.hasValue(R.styleable.CollapsingToolbarLayout_expandedTitleMarginBottom)) {
      this.mExpandedMarginBottom = localTypedArray.getDimensionPixelSize(R.styleable.CollapsingToolbarLayout_expandedTitleMarginBottom, 0);
    }
    this.mCollapsingTitleEnabled = localTypedArray.getBoolean(R.styleable.CollapsingToolbarLayout_titleEnabled, true);
    setTitle(localTypedArray.getText(R.styleable.CollapsingToolbarLayout_title));
    this.mCollapsingTextHelper.setExpandedTextAppearance(android.support.design.R.style.TextAppearance_Design_CollapsingToolbar_Expanded);
    this.mCollapsingTextHelper.setCollapsedTextAppearance(android.support.v7.appcompat.R.style.TextAppearance_AppCompat_Widget_ActionBar_Title);
    if (localTypedArray.hasValue(R.styleable.CollapsingToolbarLayout_expandedTitleTextAppearance)) {
      this.mCollapsingTextHelper.setExpandedTextAppearance(localTypedArray.getResourceId(R.styleable.CollapsingToolbarLayout_expandedTitleTextAppearance, 0));
    }
    if (localTypedArray.hasValue(R.styleable.CollapsingToolbarLayout_collapsedTitleTextAppearance)) {
      this.mCollapsingTextHelper.setCollapsedTextAppearance(localTypedArray.getResourceId(R.styleable.CollapsingToolbarLayout_collapsedTitleTextAppearance, 0));
    }
    this.mScrimVisibleHeightTrigger = localTypedArray.getDimensionPixelSize(R.styleable.CollapsingToolbarLayout_scrimVisibleHeightTrigger, -1);
    this.mScrimAnimationDuration = localTypedArray.getInt(R.styleable.CollapsingToolbarLayout_scrimAnimationDuration, 600);
    setContentScrim(localTypedArray.getDrawable(R.styleable.CollapsingToolbarLayout_contentScrim));
    setStatusBarScrim(localTypedArray.getDrawable(R.styleable.CollapsingToolbarLayout_statusBarScrim));
    this.mToolbarId = localTypedArray.getResourceId(R.styleable.CollapsingToolbarLayout_toolbarId, -1);
    localTypedArray.recycle();
    setWillNotDraw(false);
    ViewCompat.setOnApplyWindowInsetsListener(this, new OnApplyWindowInsetsListener()
    {
      public WindowInsetsCompat onApplyWindowInsets(View paramAnonymousView, WindowInsetsCompat paramAnonymousWindowInsetsCompat)
      {
        return CollapsingToolbarLayout.this.onWindowInsetChanged(paramAnonymousWindowInsetsCompat);
      }
    });
  }
  
  private void animateScrim(int paramInt)
  {
    ensureToolbar();
    Interpolator localInterpolator;
    if (this.mScrimAnimator == null)
    {
      this.mScrimAnimator = ViewUtils.createAnimator();
      this.mScrimAnimator.setDuration(this.mScrimAnimationDuration);
      ValueAnimatorCompat localValueAnimatorCompat = this.mScrimAnimator;
      if (paramInt > this.mScrimAlpha)
      {
        localInterpolator = AnimationUtils.FAST_OUT_LINEAR_IN_INTERPOLATOR;
        localValueAnimatorCompat.setInterpolator(localInterpolator);
        this.mScrimAnimator.addUpdateListener(new ValueAnimatorCompat.AnimatorUpdateListener()
        {
          public void onAnimationUpdate(ValueAnimatorCompat paramAnonymousValueAnimatorCompat)
          {
            CollapsingToolbarLayout.this.setScrimAlpha(paramAnonymousValueAnimatorCompat.getAnimatedIntValue());
          }
        });
      }
    }
    for (;;)
    {
      this.mScrimAnimator.setIntValues(this.mScrimAlpha, paramInt);
      this.mScrimAnimator.start();
      return;
      localInterpolator = AnimationUtils.LINEAR_OUT_SLOW_IN_INTERPOLATOR;
      break;
      if (this.mScrimAnimator.isRunning()) {
        this.mScrimAnimator.cancel();
      }
    }
  }
  
  private void ensureToolbar()
  {
    if (!this.mRefreshToolbar) {
      return;
    }
    this.mToolbar = null;
    this.mToolbarDirectChild = null;
    if (this.mToolbarId != -1)
    {
      this.mToolbar = ((Toolbar)findViewById(this.mToolbarId));
      if (this.mToolbar != null) {
        this.mToolbarDirectChild = findDirectChild(this.mToolbar);
      }
    }
    int i;
    int j;
    if (this.mToolbar == null)
    {
      i = 0;
      j = getChildCount();
    }
    for (;;)
    {
      Toolbar localToolbar = null;
      if (i < j)
      {
        View localView = getChildAt(i);
        if ((localView instanceof Toolbar)) {
          localToolbar = (Toolbar)localView;
        }
      }
      else
      {
        this.mToolbar = localToolbar;
        updateDummyView();
        this.mRefreshToolbar = false;
        return;
      }
      i++;
    }
  }
  
  private View findDirectChild(View paramView)
  {
    View localView = paramView;
    for (ViewParent localViewParent = paramView.getParent(); (localViewParent != this) && (localViewParent != null); localViewParent = localViewParent.getParent()) {
      if ((localViewParent instanceof View)) {
        localView = (View)localViewParent;
      }
    }
    return localView;
  }
  
  private static int getHeightWithMargins(@NonNull View paramView)
  {
    ViewGroup.LayoutParams localLayoutParams = paramView.getLayoutParams();
    if ((localLayoutParams instanceof ViewGroup.MarginLayoutParams))
    {
      ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)localLayoutParams;
      return paramView.getHeight() + localMarginLayoutParams.topMargin + localMarginLayoutParams.bottomMargin;
    }
    return paramView.getHeight();
  }
  
  static ViewOffsetHelper getViewOffsetHelper(View paramView)
  {
    ViewOffsetHelper localViewOffsetHelper = (ViewOffsetHelper)paramView.getTag(R.id.view_offset_helper);
    if (localViewOffsetHelper == null)
    {
      localViewOffsetHelper = new ViewOffsetHelper(paramView);
      paramView.setTag(R.id.view_offset_helper, localViewOffsetHelper);
    }
    return localViewOffsetHelper;
  }
  
  private boolean isToolbarChild(View paramView)
  {
    if ((this.mToolbarDirectChild == null) || (this.mToolbarDirectChild == this)) {
      if (paramView != this.mToolbar) {}
    }
    while (paramView == this.mToolbarDirectChild)
    {
      return true;
      return false;
    }
    return false;
  }
  
  private void updateDummyView()
  {
    if ((!this.mCollapsingTitleEnabled) && (this.mDummyView != null))
    {
      ViewParent localViewParent = this.mDummyView.getParent();
      if ((localViewParent instanceof ViewGroup)) {
        ((ViewGroup)localViewParent).removeView(this.mDummyView);
      }
    }
    if ((this.mCollapsingTitleEnabled) && (this.mToolbar != null))
    {
      if (this.mDummyView == null) {
        this.mDummyView = new View(getContext());
      }
      if (this.mDummyView.getParent() == null) {
        this.mToolbar.addView(this.mDummyView, -1, -1);
      }
    }
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    return paramLayoutParams instanceof LayoutParams;
  }
  
  public void draw(Canvas paramCanvas)
  {
    super.draw(paramCanvas);
    ensureToolbar();
    if ((this.mToolbar == null) && (this.mContentScrim != null) && (this.mScrimAlpha > 0))
    {
      this.mContentScrim.mutate().setAlpha(this.mScrimAlpha);
      this.mContentScrim.draw(paramCanvas);
    }
    if ((this.mCollapsingTitleEnabled) && (this.mDrawCollapsingTitle)) {
      this.mCollapsingTextHelper.draw(paramCanvas);
    }
    if ((this.mStatusBarScrim != null) && (this.mScrimAlpha > 0)) {
      if (this.mLastInsets == null) {
        break label153;
      }
    }
    label153:
    for (int i = this.mLastInsets.getSystemWindowInsetTop();; i = 0)
    {
      if (i > 0)
      {
        this.mStatusBarScrim.setBounds(0, -this.mCurrentOffset, getWidth(), i - this.mCurrentOffset);
        this.mStatusBarScrim.mutate().setAlpha(this.mScrimAlpha);
        this.mStatusBarScrim.draw(paramCanvas);
      }
      return;
    }
  }
  
  protected boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
  {
    Drawable localDrawable = this.mContentScrim;
    int i = 0;
    if (localDrawable != null)
    {
      int j = this.mScrimAlpha;
      i = 0;
      if (j > 0)
      {
        boolean bool = isToolbarChild(paramView);
        i = 0;
        if (bool)
        {
          this.mContentScrim.mutate().setAlpha(this.mScrimAlpha);
          this.mContentScrim.draw(paramCanvas);
          i = 1;
        }
      }
    }
    return (super.drawChild(paramCanvas, paramView, paramLong)) || (i != 0);
  }
  
  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    int[] arrayOfInt = getDrawableState();
    Drawable localDrawable1 = this.mStatusBarScrim;
    boolean bool1 = false;
    if (localDrawable1 != null)
    {
      boolean bool2 = localDrawable1.isStateful();
      bool1 = false;
      if (bool2) {
        bool1 = false | localDrawable1.setState(arrayOfInt);
      }
    }
    Drawable localDrawable2 = this.mContentScrim;
    if ((localDrawable2 != null) && (localDrawable2.isStateful())) {
      bool1 |= localDrawable2.setState(arrayOfInt);
    }
    if (this.mCollapsingTextHelper != null) {
      bool1 |= this.mCollapsingTextHelper.setState(arrayOfInt);
    }
    if (bool1) {
      invalidate();
    }
  }
  
  protected LayoutParams generateDefaultLayoutParams()
  {
    return new LayoutParams(-1, -1);
  }
  
  public FrameLayout.LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return new LayoutParams(getContext(), paramAttributeSet);
  }
  
  protected FrameLayout.LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    return new LayoutParams(paramLayoutParams);
  }
  
  public int getCollapsedTitleGravity()
  {
    return this.mCollapsingTextHelper.getCollapsedTextGravity();
  }
  
  @NonNull
  public Typeface getCollapsedTitleTypeface()
  {
    return this.mCollapsingTextHelper.getCollapsedTypeface();
  }
  
  @Nullable
  public Drawable getContentScrim()
  {
    return this.mContentScrim;
  }
  
  public int getExpandedTitleGravity()
  {
    return this.mCollapsingTextHelper.getExpandedTextGravity();
  }
  
  public int getExpandedTitleMarginBottom()
  {
    return this.mExpandedMarginBottom;
  }
  
  public int getExpandedTitleMarginEnd()
  {
    return this.mExpandedMarginEnd;
  }
  
  public int getExpandedTitleMarginStart()
  {
    return this.mExpandedMarginStart;
  }
  
  public int getExpandedTitleMarginTop()
  {
    return this.mExpandedMarginTop;
  }
  
  @NonNull
  public Typeface getExpandedTitleTypeface()
  {
    return this.mCollapsingTextHelper.getExpandedTypeface();
  }
  
  final int getMaxOffsetForPinChild(View paramView)
  {
    ViewOffsetHelper localViewOffsetHelper = getViewOffsetHelper(paramView);
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    return getHeight() - localViewOffsetHelper.getLayoutTop() - paramView.getHeight() - localLayoutParams.bottomMargin;
  }
  
  int getScrimAlpha()
  {
    return this.mScrimAlpha;
  }
  
  public long getScrimAnimationDuration()
  {
    return this.mScrimAnimationDuration;
  }
  
  public int getScrimVisibleHeightTrigger()
  {
    if (this.mScrimVisibleHeightTrigger >= 0) {
      return this.mScrimVisibleHeightTrigger;
    }
    if (this.mLastInsets != null) {}
    for (int i = this.mLastInsets.getSystemWindowInsetTop();; i = 0)
    {
      int j = ViewCompat.getMinimumHeight(this);
      if (j <= 0) {
        break;
      }
      return Math.min(i + j * 2, getHeight());
    }
    return getHeight() / 3;
  }
  
  @Nullable
  public Drawable getStatusBarScrim()
  {
    return this.mStatusBarScrim;
  }
  
  @Nullable
  public CharSequence getTitle()
  {
    if (this.mCollapsingTitleEnabled) {
      return this.mCollapsingTextHelper.getText();
    }
    return null;
  }
  
  public boolean isTitleEnabled()
  {
    return this.mCollapsingTitleEnabled;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    ViewParent localViewParent = getParent();
    if ((localViewParent instanceof AppBarLayout))
    {
      ViewCompat.setFitsSystemWindows(this, ViewCompat.getFitsSystemWindows((View)localViewParent));
      if (this.mOnOffsetChangedListener == null) {
        this.mOnOffsetChangedListener = new OffsetUpdateListener();
      }
      ((AppBarLayout)localViewParent).addOnOffsetChangedListener(this.mOnOffsetChangedListener);
      ViewCompat.requestApplyInsets(this);
    }
  }
  
  protected void onDetachedFromWindow()
  {
    ViewParent localViewParent = getParent();
    if ((this.mOnOffsetChangedListener != null) && ((localViewParent instanceof AppBarLayout))) {
      ((AppBarLayout)localViewParent).removeOnOffsetChangedListener(this.mOnOffsetChangedListener);
    }
    super.onDetachedFromWindow();
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    if (this.mLastInsets != null)
    {
      int i10 = this.mLastInsets.getSystemWindowInsetTop();
      int i11 = 0;
      int i12 = getChildCount();
      while (i11 < i12)
      {
        View localView = getChildAt(i11);
        if ((!ViewCompat.getFitsSystemWindows(localView)) && (localView.getTop() < i10)) {
          ViewCompat.offsetTopAndBottom(localView, i10);
        }
        i11++;
      }
    }
    boolean bool;
    int k;
    label143:
    Object localObject;
    label156:
    int i1;
    label205:
    int i5;
    label255:
    CollapsingTextHelper localCollapsingTextHelper2;
    int i6;
    label304:
    int i7;
    int i8;
    if ((this.mCollapsingTitleEnabled) && (this.mDummyView != null))
    {
      if ((!ViewCompat.isAttachedToWindow(this.mDummyView)) || (this.mDummyView.getVisibility() != 0)) {
        break label399;
      }
      bool = true;
      this.mDrawCollapsingTitle = bool;
      if (this.mDrawCollapsingTitle)
      {
        if (ViewCompat.getLayoutDirection(this) != 1) {
          break label405;
        }
        k = 1;
        if (this.mToolbarDirectChild == null) {
          break label411;
        }
        localObject = this.mToolbarDirectChild;
        int m = getMaxOffsetForPinChild((View)localObject);
        ViewGroupUtils.getDescendantRect(this, this.mDummyView, this.mTmpRect);
        CollapsingTextHelper localCollapsingTextHelper1 = this.mCollapsingTextHelper;
        int n = this.mTmpRect.left;
        if (k == 0) {
          break label420;
        }
        i1 = this.mToolbar.getTitleMarginEnd();
        int i2 = n + i1;
        int i3 = m + this.mTmpRect.top + this.mToolbar.getTitleMarginTop();
        int i4 = this.mTmpRect.right;
        if (k == 0) {
          break label432;
        }
        i5 = this.mToolbar.getTitleMarginStart();
        localCollapsingTextHelper1.setCollapsedBounds(i2, i3, i5 + i4, m + this.mTmpRect.bottom - this.mToolbar.getTitleMarginBottom());
        localCollapsingTextHelper2 = this.mCollapsingTextHelper;
        if (k == 0) {
          break label444;
        }
        i6 = this.mExpandedMarginEnd;
        i7 = this.mTmpRect.top + this.mExpandedMarginTop;
        i8 = paramInt3 - paramInt1;
        if (k == 0) {
          break label453;
        }
      }
    }
    label399:
    label405:
    label411:
    label420:
    label432:
    label444:
    label453:
    for (int i9 = this.mExpandedMarginStart;; i9 = this.mExpandedMarginEnd)
    {
      localCollapsingTextHelper2.setExpandedBounds(i6, i7, i8 - i9, paramInt4 - paramInt2 - this.mExpandedMarginBottom);
      this.mCollapsingTextHelper.recalculate();
      int i = 0;
      int j = getChildCount();
      while (i < j)
      {
        getViewOffsetHelper(getChildAt(i)).onViewLayout();
        i++;
      }
      bool = false;
      break;
      k = 0;
      break label143;
      localObject = this.mToolbar;
      break label156;
      i1 = this.mToolbar.getTitleMarginStart();
      break label205;
      i5 = this.mToolbar.getTitleMarginEnd();
      break label255;
      i6 = this.mExpandedMarginStart;
      break label304;
    }
    if (this.mToolbar != null)
    {
      if ((this.mCollapsingTitleEnabled) && (TextUtils.isEmpty(this.mCollapsingTextHelper.getText()))) {
        this.mCollapsingTextHelper.setText(this.mToolbar.getTitle());
      }
      if ((this.mToolbarDirectChild != null) && (this.mToolbarDirectChild != this)) {
        break label534;
      }
      setMinimumHeight(getHeightWithMargins(this.mToolbar));
    }
    for (;;)
    {
      updateScrimVisibility();
      return;
      label534:
      setMinimumHeight(getHeightWithMargins(this.mToolbarDirectChild));
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    ensureToolbar();
    super.onMeasure(paramInt1, paramInt2);
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    if (this.mContentScrim != null) {
      this.mContentScrim.setBounds(0, 0, paramInt1, paramInt2);
    }
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
      requestLayout();
    }
    return paramWindowInsetsCompat.consumeSystemWindowInsets();
  }
  
  public void setCollapsedTitleGravity(int paramInt)
  {
    this.mCollapsingTextHelper.setCollapsedTextGravity(paramInt);
  }
  
  public void setCollapsedTitleTextAppearance(@StyleRes int paramInt)
  {
    this.mCollapsingTextHelper.setCollapsedTextAppearance(paramInt);
  }
  
  public void setCollapsedTitleTextColor(@ColorInt int paramInt)
  {
    setCollapsedTitleTextColor(ColorStateList.valueOf(paramInt));
  }
  
  public void setCollapsedTitleTextColor(@NonNull ColorStateList paramColorStateList)
  {
    this.mCollapsingTextHelper.setCollapsedTextColor(paramColorStateList);
  }
  
  public void setCollapsedTitleTypeface(@Nullable Typeface paramTypeface)
  {
    this.mCollapsingTextHelper.setCollapsedTypeface(paramTypeface);
  }
  
  public void setContentScrim(@Nullable Drawable paramDrawable)
  {
    if (this.mContentScrim != paramDrawable)
    {
      if (this.mContentScrim != null) {
        this.mContentScrim.setCallback(null);
      }
      Drawable localDrawable = null;
      if (paramDrawable != null) {
        localDrawable = paramDrawable.mutate();
      }
      this.mContentScrim = localDrawable;
      if (this.mContentScrim != null)
      {
        this.mContentScrim.setBounds(0, 0, getWidth(), getHeight());
        this.mContentScrim.setCallback(this);
        this.mContentScrim.setAlpha(this.mScrimAlpha);
      }
      ViewCompat.postInvalidateOnAnimation(this);
    }
  }
  
  public void setContentScrimColor(@ColorInt int paramInt)
  {
    setContentScrim(new ColorDrawable(paramInt));
  }
  
  public void setContentScrimResource(@DrawableRes int paramInt)
  {
    setContentScrim(ContextCompat.getDrawable(getContext(), paramInt));
  }
  
  public void setExpandedTitleColor(@ColorInt int paramInt)
  {
    setExpandedTitleTextColor(ColorStateList.valueOf(paramInt));
  }
  
  public void setExpandedTitleGravity(int paramInt)
  {
    this.mCollapsingTextHelper.setExpandedTextGravity(paramInt);
  }
  
  public void setExpandedTitleMargin(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    this.mExpandedMarginStart = paramInt1;
    this.mExpandedMarginTop = paramInt2;
    this.mExpandedMarginEnd = paramInt3;
    this.mExpandedMarginBottom = paramInt4;
    requestLayout();
  }
  
  public void setExpandedTitleMarginBottom(int paramInt)
  {
    this.mExpandedMarginBottom = paramInt;
    requestLayout();
  }
  
  public void setExpandedTitleMarginEnd(int paramInt)
  {
    this.mExpandedMarginEnd = paramInt;
    requestLayout();
  }
  
  public void setExpandedTitleMarginStart(int paramInt)
  {
    this.mExpandedMarginStart = paramInt;
    requestLayout();
  }
  
  public void setExpandedTitleMarginTop(int paramInt)
  {
    this.mExpandedMarginTop = paramInt;
    requestLayout();
  }
  
  public void setExpandedTitleTextAppearance(@StyleRes int paramInt)
  {
    this.mCollapsingTextHelper.setExpandedTextAppearance(paramInt);
  }
  
  public void setExpandedTitleTextColor(@NonNull ColorStateList paramColorStateList)
  {
    this.mCollapsingTextHelper.setExpandedTextColor(paramColorStateList);
  }
  
  public void setExpandedTitleTypeface(@Nullable Typeface paramTypeface)
  {
    this.mCollapsingTextHelper.setExpandedTypeface(paramTypeface);
  }
  
  void setScrimAlpha(int paramInt)
  {
    if (paramInt != this.mScrimAlpha)
    {
      if ((this.mContentScrim != null) && (this.mToolbar != null)) {
        ViewCompat.postInvalidateOnAnimation(this.mToolbar);
      }
      this.mScrimAlpha = paramInt;
      ViewCompat.postInvalidateOnAnimation(this);
    }
  }
  
  public void setScrimAnimationDuration(@IntRange(from=0L) long paramLong)
  {
    this.mScrimAnimationDuration = paramLong;
  }
  
  public void setScrimVisibleHeightTrigger(@IntRange(from=0L) int paramInt)
  {
    if (this.mScrimVisibleHeightTrigger != paramInt)
    {
      this.mScrimVisibleHeightTrigger = paramInt;
      updateScrimVisibility();
    }
  }
  
  public void setScrimsShown(boolean paramBoolean)
  {
    if ((ViewCompat.isLaidOut(this)) && (!isInEditMode())) {}
    for (boolean bool = true;; bool = false)
    {
      setScrimsShown(paramBoolean, bool);
      return;
    }
  }
  
  public void setScrimsShown(boolean paramBoolean1, boolean paramBoolean2)
  {
    int i = 255;
    if (this.mScrimsAreShown != paramBoolean1)
    {
      if (!paramBoolean2) {
        break label36;
      }
      if (!paramBoolean1) {
        break label31;
      }
    }
    for (;;)
    {
      animateScrim(i);
      this.mScrimsAreShown = paramBoolean1;
      return;
      label31:
      i = 0;
    }
    label36:
    if (paramBoolean1) {}
    for (;;)
    {
      setScrimAlpha(i);
      break;
      i = 0;
    }
  }
  
  public void setStatusBarScrim(@Nullable Drawable paramDrawable)
  {
    Drawable localDrawable2;
    if (this.mStatusBarScrim != paramDrawable)
    {
      if (this.mStatusBarScrim != null) {
        this.mStatusBarScrim.setCallback(null);
      }
      Drawable localDrawable1 = null;
      if (paramDrawable != null) {
        localDrawable1 = paramDrawable.mutate();
      }
      this.mStatusBarScrim = localDrawable1;
      if (this.mStatusBarScrim != null)
      {
        if (this.mStatusBarScrim.isStateful()) {
          this.mStatusBarScrim.setState(getDrawableState());
        }
        DrawableCompat.setLayoutDirection(this.mStatusBarScrim, ViewCompat.getLayoutDirection(this));
        localDrawable2 = this.mStatusBarScrim;
        if (getVisibility() != 0) {
          break label129;
        }
      }
    }
    label129:
    for (boolean bool = true;; bool = false)
    {
      localDrawable2.setVisible(bool, false);
      this.mStatusBarScrim.setCallback(this);
      this.mStatusBarScrim.setAlpha(this.mScrimAlpha);
      ViewCompat.postInvalidateOnAnimation(this);
      return;
    }
  }
  
  public void setStatusBarScrimColor(@ColorInt int paramInt)
  {
    setStatusBarScrim(new ColorDrawable(paramInt));
  }
  
  public void setStatusBarScrimResource(@DrawableRes int paramInt)
  {
    setStatusBarScrim(ContextCompat.getDrawable(getContext(), paramInt));
  }
  
  public void setTitle(@Nullable CharSequence paramCharSequence)
  {
    this.mCollapsingTextHelper.setText(paramCharSequence);
  }
  
  public void setTitleEnabled(boolean paramBoolean)
  {
    if (paramBoolean != this.mCollapsingTitleEnabled)
    {
      this.mCollapsingTitleEnabled = paramBoolean;
      updateDummyView();
      requestLayout();
    }
  }
  
  public void setVisibility(int paramInt)
  {
    super.setVisibility(paramInt);
    if (paramInt == 0) {}
    for (boolean bool = true;; bool = false)
    {
      if ((this.mStatusBarScrim != null) && (this.mStatusBarScrim.isVisible() != bool)) {
        this.mStatusBarScrim.setVisible(bool, false);
      }
      if ((this.mContentScrim != null) && (this.mContentScrim.isVisible() != bool)) {
        this.mContentScrim.setVisible(bool, false);
      }
      return;
    }
  }
  
  final void updateScrimVisibility()
  {
    if ((this.mContentScrim != null) || (this.mStatusBarScrim != null)) {
      if (getHeight() + this.mCurrentOffset >= getScrimVisibleHeightTrigger()) {
        break label38;
      }
    }
    label38:
    for (boolean bool = true;; bool = false)
    {
      setScrimsShown(bool);
      return;
    }
  }
  
  protected boolean verifyDrawable(Drawable paramDrawable)
  {
    return (super.verifyDrawable(paramDrawable)) || (paramDrawable == this.mContentScrim) || (paramDrawable == this.mStatusBarScrim);
  }
  
  public static class LayoutParams
    extends FrameLayout.LayoutParams
  {
    public static final int COLLAPSE_MODE_OFF = 0;
    public static final int COLLAPSE_MODE_PARALLAX = 2;
    public static final int COLLAPSE_MODE_PIN = 1;
    private static final float DEFAULT_PARALLAX_MULTIPLIER = 0.5F;
    int mCollapseMode = 0;
    float mParallaxMult = 0.5F;
    
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
    }
    
    public LayoutParams(int paramInt1, int paramInt2, int paramInt3)
    {
      super(paramInt2, paramInt3);
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
      TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.CollapsingToolbarLayout_Layout);
      this.mCollapseMode = localTypedArray.getInt(R.styleable.CollapsingToolbarLayout_Layout_layout_collapseMode, 0);
      setParallaxMultiplier(localTypedArray.getFloat(R.styleable.CollapsingToolbarLayout_Layout_layout_collapseParallaxMultiplier, 0.5F));
      localTypedArray.recycle();
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
    public LayoutParams(FrameLayout.LayoutParams paramLayoutParams)
    {
      super();
    }
    
    public int getCollapseMode()
    {
      return this.mCollapseMode;
    }
    
    public float getParallaxMultiplier()
    {
      return this.mParallaxMult;
    }
    
    public void setCollapseMode(int paramInt)
    {
      this.mCollapseMode = paramInt;
    }
    
    public void setParallaxMultiplier(float paramFloat)
    {
      this.mParallaxMult = paramFloat;
    }
  }
  
  private class OffsetUpdateListener
    implements AppBarLayout.OnOffsetChangedListener
  {
    OffsetUpdateListener() {}
    
    public void onOffsetChanged(AppBarLayout paramAppBarLayout, int paramInt)
    {
      CollapsingToolbarLayout.this.mCurrentOffset = paramInt;
      int i;
      int j;
      label41:
      View localView;
      CollapsingToolbarLayout.LayoutParams localLayoutParams;
      ViewOffsetHelper localViewOffsetHelper;
      if (CollapsingToolbarLayout.this.mLastInsets != null)
      {
        i = CollapsingToolbarLayout.this.mLastInsets.getSystemWindowInsetTop();
        j = 0;
        int k = CollapsingToolbarLayout.this.getChildCount();
        if (j >= k) {
          break label160;
        }
        localView = CollapsingToolbarLayout.this.getChildAt(j);
        localLayoutParams = (CollapsingToolbarLayout.LayoutParams)localView.getLayoutParams();
        localViewOffsetHelper = CollapsingToolbarLayout.getViewOffsetHelper(localView);
        switch (localLayoutParams.mCollapseMode)
        {
        }
      }
      for (;;)
      {
        j++;
        break label41;
        i = 0;
        break;
        localViewOffsetHelper.setTopAndBottomOffset(MathUtils.constrain(-paramInt, 0, CollapsingToolbarLayout.this.getMaxOffsetForPinChild(localView)));
        continue;
        localViewOffsetHelper.setTopAndBottomOffset(Math.round(-paramInt * localLayoutParams.mParallaxMult));
      }
      label160:
      CollapsingToolbarLayout.this.updateScrimVisibility();
      if ((CollapsingToolbarLayout.this.mStatusBarScrim != null) && (i > 0)) {
        ViewCompat.postInvalidateOnAnimation(CollapsingToolbarLayout.this);
      }
      int m = CollapsingToolbarLayout.this.getHeight() - ViewCompat.getMinimumHeight(CollapsingToolbarLayout.this) - i;
      CollapsingToolbarLayout.this.mCollapsingTextHelper.setExpansionFraction(Math.abs(paramInt) / m);
    }
  }
}
