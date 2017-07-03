package android.support.design.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.StringRes;
import android.support.design.R.dimen;
import android.support.design.R.layout;
import android.support.design.R.style;
import android.support.v4.util.Pools.Pool;
import android.support.v4.util.Pools.SimplePool;
import android.support.v4.util.Pools.SynchronizedPool;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PointerIconCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.DecorView;
import android.support.v4.view.ViewPager.OnAdapterChangeListener;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.content.res.AppCompatResources;
import android.text.Layout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

@ViewPager.DecorView
public class TabLayout
  extends HorizontalScrollView
{
  private static final int ANIMATION_DURATION = 300;
  static final int DEFAULT_GAP_TEXT_ICON = 8;
  private static final int DEFAULT_HEIGHT = 48;
  private static final int DEFAULT_HEIGHT_WITH_TEXT_ICON = 72;
  static final int FIXED_WRAP_GUTTER_MIN = 16;
  public static final int GRAVITY_CENTER = 1;
  public static final int GRAVITY_FILL = 0;
  private static final int INVALID_WIDTH = -1;
  public static final int MODE_FIXED = 1;
  public static final int MODE_SCROLLABLE = 0;
  static final int MOTION_NON_ADJACENT_OFFSET = 24;
  private static final int TAB_MIN_WIDTH_MARGIN = 56;
  private static final Pools.Pool<Tab> sTabPool = new Pools.SynchronizedPool(16);
  private AdapterChangeListener mAdapterChangeListener;
  private int mContentInsetStart;
  private OnTabSelectedListener mCurrentVpSelectedListener;
  int mMode;
  private TabLayoutOnPageChangeListener mPageChangeListener;
  private PagerAdapter mPagerAdapter;
  private DataSetObserver mPagerAdapterObserver;
  private final int mRequestedTabMaxWidth;
  private final int mRequestedTabMinWidth;
  private ValueAnimatorCompat mScrollAnimator;
  private final int mScrollableTabMinWidth;
  private OnTabSelectedListener mSelectedListener;
  private final ArrayList<OnTabSelectedListener> mSelectedListeners = new ArrayList();
  private Tab mSelectedTab;
  private boolean mSetupViewPagerImplicitly;
  final int mTabBackgroundResId;
  int mTabGravity;
  int mTabMaxWidth = Integer.MAX_VALUE;
  int mTabPaddingBottom;
  int mTabPaddingEnd;
  int mTabPaddingStart;
  int mTabPaddingTop;
  private final SlidingTabStrip mTabStrip;
  int mTabTextAppearance;
  ColorStateList mTabTextColors;
  float mTabTextMultiLineSize;
  float mTabTextSize;
  private final Pools.Pool<TabView> mTabViewPool = new Pools.SimplePool(12);
  private final ArrayList<Tab> mTabs = new ArrayList();
  ViewPager mViewPager;
  
  public TabLayout(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public TabLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public TabLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    ThemeUtils.checkAppCompatTheme(paramContext);
    setHorizontalScrollBarEnabled(false);
    this.mTabStrip = new SlidingTabStrip(paramContext);
    super.addView(this.mTabStrip, 0, new FrameLayout.LayoutParams(-2, -1));
    TypedArray localTypedArray1 = paramContext.obtainStyledAttributes(paramAttributeSet, android.support.design.R.styleable.TabLayout, paramInt, R.style.Widget_Design_TabLayout);
    this.mTabStrip.setSelectedIndicatorHeight(localTypedArray1.getDimensionPixelSize(android.support.design.R.styleable.TabLayout_tabIndicatorHeight, 0));
    this.mTabStrip.setSelectedIndicatorColor(localTypedArray1.getColor(android.support.design.R.styleable.TabLayout_tabIndicatorColor, 0));
    int i = localTypedArray1.getDimensionPixelSize(android.support.design.R.styleable.TabLayout_tabPadding, 0);
    this.mTabPaddingBottom = i;
    this.mTabPaddingEnd = i;
    this.mTabPaddingTop = i;
    this.mTabPaddingStart = i;
    this.mTabPaddingStart = localTypedArray1.getDimensionPixelSize(android.support.design.R.styleable.TabLayout_tabPaddingStart, this.mTabPaddingStart);
    this.mTabPaddingTop = localTypedArray1.getDimensionPixelSize(android.support.design.R.styleable.TabLayout_tabPaddingTop, this.mTabPaddingTop);
    this.mTabPaddingEnd = localTypedArray1.getDimensionPixelSize(android.support.design.R.styleable.TabLayout_tabPaddingEnd, this.mTabPaddingEnd);
    this.mTabPaddingBottom = localTypedArray1.getDimensionPixelSize(android.support.design.R.styleable.TabLayout_tabPaddingBottom, this.mTabPaddingBottom);
    this.mTabTextAppearance = localTypedArray1.getResourceId(android.support.design.R.styleable.TabLayout_tabTextAppearance, R.style.TextAppearance_Design_Tab);
    TypedArray localTypedArray2 = paramContext.obtainStyledAttributes(this.mTabTextAppearance, android.support.v7.appcompat.R.styleable.TextAppearance);
    try
    {
      this.mTabTextSize = localTypedArray2.getDimensionPixelSize(android.support.v7.appcompat.R.styleable.TextAppearance_android_textSize, 0);
      this.mTabTextColors = localTypedArray2.getColorStateList(android.support.v7.appcompat.R.styleable.TextAppearance_android_textColor);
      localTypedArray2.recycle();
      if (localTypedArray1.hasValue(android.support.design.R.styleable.TabLayout_tabTextColor)) {
        this.mTabTextColors = localTypedArray1.getColorStateList(android.support.design.R.styleable.TabLayout_tabTextColor);
      }
      if (localTypedArray1.hasValue(android.support.design.R.styleable.TabLayout_tabSelectedTextColor))
      {
        int j = localTypedArray1.getColor(android.support.design.R.styleable.TabLayout_tabSelectedTextColor, 0);
        this.mTabTextColors = createColorStateList(this.mTabTextColors.getDefaultColor(), j);
      }
      this.mRequestedTabMinWidth = localTypedArray1.getDimensionPixelSize(android.support.design.R.styleable.TabLayout_tabMinWidth, -1);
      this.mRequestedTabMaxWidth = localTypedArray1.getDimensionPixelSize(android.support.design.R.styleable.TabLayout_tabMaxWidth, -1);
      this.mTabBackgroundResId = localTypedArray1.getResourceId(android.support.design.R.styleable.TabLayout_tabBackground, 0);
      this.mContentInsetStart = localTypedArray1.getDimensionPixelSize(android.support.design.R.styleable.TabLayout_tabContentStart, 0);
      this.mMode = localTypedArray1.getInt(android.support.design.R.styleable.TabLayout_tabMode, 1);
      this.mTabGravity = localTypedArray1.getInt(android.support.design.R.styleable.TabLayout_tabGravity, 0);
      localTypedArray1.recycle();
      Resources localResources = getResources();
      this.mTabTextMultiLineSize = localResources.getDimensionPixelSize(R.dimen.design_tab_text_size_2line);
      this.mScrollableTabMinWidth = localResources.getDimensionPixelSize(R.dimen.design_tab_scrollable_min_width);
      applyModeAndGravity();
      return;
    }
    finally
    {
      localTypedArray2.recycle();
    }
  }
  
  private void addTabFromItemView(@NonNull TabItem paramTabItem)
  {
    Tab localTab = newTab();
    if (paramTabItem.mText != null) {
      localTab.setText(paramTabItem.mText);
    }
    if (paramTabItem.mIcon != null) {
      localTab.setIcon(paramTabItem.mIcon);
    }
    if (paramTabItem.mCustomLayout != 0) {
      localTab.setCustomView(paramTabItem.mCustomLayout);
    }
    if (!TextUtils.isEmpty(paramTabItem.getContentDescription())) {
      localTab.setContentDescription(paramTabItem.getContentDescription());
    }
    addTab(localTab);
  }
  
  private void addTabView(Tab paramTab)
  {
    TabView localTabView = paramTab.mView;
    this.mTabStrip.addView(localTabView, paramTab.getPosition(), createLayoutParamsForTabs());
  }
  
  private void addViewInternal(View paramView)
  {
    if ((paramView instanceof TabItem))
    {
      addTabFromItemView((TabItem)paramView);
      return;
    }
    throw new IllegalArgumentException("Only TabItem instances can be added to TabLayout");
  }
  
  private void animateToTab(int paramInt)
  {
    if (paramInt == -1) {
      return;
    }
    if ((getWindowToken() == null) || (!ViewCompat.isLaidOut(this)) || (this.mTabStrip.childrenNeedLayout()))
    {
      setScrollPosition(paramInt, 0.0F, true);
      return;
    }
    int i = getScrollX();
    int j = calculateScrollXForTab(paramInt, 0.0F);
    if (i != j)
    {
      ensureScrollAnimator();
      this.mScrollAnimator.setIntValues(i, j);
      this.mScrollAnimator.start();
    }
    this.mTabStrip.animateIndicatorToPosition(paramInt, 300);
  }
  
  private void applyModeAndGravity()
  {
    int i = this.mMode;
    int j = 0;
    if (i == 0) {
      j = Math.max(0, this.mContentInsetStart - this.mTabPaddingStart);
    }
    ViewCompat.setPaddingRelative(this.mTabStrip, j, 0, 0, 0);
    switch (this.mMode)
    {
    }
    for (;;)
    {
      updateTabViews(true);
      return;
      this.mTabStrip.setGravity(1);
      continue;
      this.mTabStrip.setGravity(8388611);
    }
  }
  
  private int calculateScrollXForTab(int paramInt, float paramFloat)
  {
    if (this.mMode == 0)
    {
      View localView1 = this.mTabStrip.getChildAt(paramInt);
      View localView2;
      if (paramInt + 1 < this.mTabStrip.getChildCount())
      {
        localView2 = this.mTabStrip.getChildAt(paramInt + 1);
        if (localView1 == null) {
          break label118;
        }
      }
      int k;
      int m;
      label118:
      for (int i = localView1.getWidth();; i = 0)
      {
        int j = 0;
        if (localView2 != null) {
          j = localView2.getWidth();
        }
        k = localView1.getLeft() + i / 2 - getWidth() / 2;
        m = (int)(paramFloat * (0.5F * (i + j)));
        if (ViewCompat.getLayoutDirection(this) != 0) {
          break label124;
        }
        return k + m;
        localView2 = null;
        break;
      }
      label124:
      return k - m;
    }
    return 0;
  }
  
  private void configureTab(Tab paramTab, int paramInt)
  {
    paramTab.setPosition(paramInt);
    this.mTabs.add(paramInt, paramTab);
    int i = this.mTabs.size();
    for (int j = paramInt + 1; j < i; j++) {
      ((Tab)this.mTabs.get(j)).setPosition(j);
    }
  }
  
  private static ColorStateList createColorStateList(int paramInt1, int paramInt2)
  {
    int[][] arrayOfInt = new int[2][];
    int[] arrayOfInt1 = new int[2];
    arrayOfInt[0] = SELECTED_STATE_SET;
    arrayOfInt1[0] = paramInt2;
    int i = 0 + 1;
    arrayOfInt[i] = EMPTY_STATE_SET;
    arrayOfInt1[i] = paramInt1;
    (i + 1);
    return new ColorStateList(arrayOfInt, arrayOfInt1);
  }
  
  private LinearLayout.LayoutParams createLayoutParamsForTabs()
  {
    LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(-2, -1);
    updateTabViewLayoutParams(localLayoutParams);
    return localLayoutParams;
  }
  
  private TabView createTabView(@NonNull Tab paramTab)
  {
    if (this.mTabViewPool != null) {}
    for (TabView localTabView = (TabView)this.mTabViewPool.acquire();; localTabView = null)
    {
      if (localTabView == null) {
        localTabView = new TabView(getContext());
      }
      localTabView.setTab(paramTab);
      localTabView.setFocusable(true);
      localTabView.setMinimumWidth(getTabMinWidth());
      return localTabView;
    }
  }
  
  private void dispatchTabReselected(@NonNull Tab paramTab)
  {
    for (int i = -1 + this.mSelectedListeners.size(); i >= 0; i--) {
      ((OnTabSelectedListener)this.mSelectedListeners.get(i)).onTabReselected(paramTab);
    }
  }
  
  private void dispatchTabSelected(@NonNull Tab paramTab)
  {
    for (int i = -1 + this.mSelectedListeners.size(); i >= 0; i--) {
      ((OnTabSelectedListener)this.mSelectedListeners.get(i)).onTabSelected(paramTab);
    }
  }
  
  private void dispatchTabUnselected(@NonNull Tab paramTab)
  {
    for (int i = -1 + this.mSelectedListeners.size(); i >= 0; i--) {
      ((OnTabSelectedListener)this.mSelectedListeners.get(i)).onTabUnselected(paramTab);
    }
  }
  
  private void ensureScrollAnimator()
  {
    if (this.mScrollAnimator == null)
    {
      this.mScrollAnimator = ViewUtils.createAnimator();
      this.mScrollAnimator.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
      this.mScrollAnimator.setDuration(300L);
      this.mScrollAnimator.addUpdateListener(new ValueAnimatorCompat.AnimatorUpdateListener()
      {
        public void onAnimationUpdate(ValueAnimatorCompat paramAnonymousValueAnimatorCompat)
        {
          TabLayout.this.scrollTo(paramAnonymousValueAnimatorCompat.getAnimatedIntValue(), 0);
        }
      });
    }
  }
  
  private int getDefaultHeight()
  {
    int i = 0;
    int j = this.mTabs.size();
    for (;;)
    {
      int k = 0;
      if (i < j)
      {
        Tab localTab = (Tab)this.mTabs.get(i);
        if ((localTab != null) && (localTab.getIcon() != null) && (!TextUtils.isEmpty(localTab.getText()))) {
          k = 1;
        }
      }
      else
      {
        if (k == 0) {
          break;
        }
        return 72;
      }
      i++;
    }
    return 48;
  }
  
  private float getScrollPosition()
  {
    return this.mTabStrip.getIndicatorPosition();
  }
  
  private int getTabMinWidth()
  {
    if (this.mRequestedTabMinWidth != -1) {
      return this.mRequestedTabMinWidth;
    }
    if (this.mMode == 0) {
      return this.mScrollableTabMinWidth;
    }
    return 0;
  }
  
  private int getTabScrollRange()
  {
    return Math.max(0, this.mTabStrip.getWidth() - getWidth() - getPaddingLeft() - getPaddingRight());
  }
  
  private void removeTabViewAt(int paramInt)
  {
    TabView localTabView = (TabView)this.mTabStrip.getChildAt(paramInt);
    this.mTabStrip.removeViewAt(paramInt);
    if (localTabView != null)
    {
      localTabView.reset();
      this.mTabViewPool.release(localTabView);
    }
    requestLayout();
  }
  
  private void setSelectedTabView(int paramInt)
  {
    int i = this.mTabStrip.getChildCount();
    if (paramInt < i)
    {
      int j = 0;
      if (j < i)
      {
        View localView = this.mTabStrip.getChildAt(j);
        if (j == paramInt) {}
        for (boolean bool = true;; bool = false)
        {
          localView.setSelected(bool);
          j++;
          break;
        }
      }
    }
  }
  
  private void setupWithViewPager(@Nullable ViewPager paramViewPager, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.mViewPager != null)
    {
      if (this.mPageChangeListener != null) {
        this.mViewPager.removeOnPageChangeListener(this.mPageChangeListener);
      }
      if (this.mAdapterChangeListener != null) {
        this.mViewPager.removeOnAdapterChangeListener(this.mAdapterChangeListener);
      }
    }
    if (this.mCurrentVpSelectedListener != null)
    {
      removeOnTabSelectedListener(this.mCurrentVpSelectedListener);
      this.mCurrentVpSelectedListener = null;
    }
    if (paramViewPager != null)
    {
      this.mViewPager = paramViewPager;
      if (this.mPageChangeListener == null) {
        this.mPageChangeListener = new TabLayoutOnPageChangeListener(this);
      }
      this.mPageChangeListener.reset();
      paramViewPager.addOnPageChangeListener(this.mPageChangeListener);
      this.mCurrentVpSelectedListener = new ViewPagerOnTabSelectedListener(paramViewPager);
      addOnTabSelectedListener(this.mCurrentVpSelectedListener);
      PagerAdapter localPagerAdapter = paramViewPager.getAdapter();
      if (localPagerAdapter != null) {
        setPagerAdapter(localPagerAdapter, paramBoolean1);
      }
      if (this.mAdapterChangeListener == null) {
        this.mAdapterChangeListener = new AdapterChangeListener();
      }
      this.mAdapterChangeListener.setAutoRefresh(paramBoolean1);
      paramViewPager.addOnAdapterChangeListener(this.mAdapterChangeListener);
      setScrollPosition(paramViewPager.getCurrentItem(), 0.0F, true);
    }
    for (;;)
    {
      this.mSetupViewPagerImplicitly = paramBoolean2;
      return;
      this.mViewPager = null;
      setPagerAdapter(null, false);
    }
  }
  
  private void updateAllTabs()
  {
    int i = 0;
    int j = this.mTabs.size();
    while (i < j)
    {
      ((Tab)this.mTabs.get(i)).updateView();
      i++;
    }
  }
  
  private void updateTabViewLayoutParams(LinearLayout.LayoutParams paramLayoutParams)
  {
    if ((this.mMode == 1) && (this.mTabGravity == 0))
    {
      paramLayoutParams.width = 0;
      paramLayoutParams.weight = 1.0F;
      return;
    }
    paramLayoutParams.width = -2;
    paramLayoutParams.weight = 0.0F;
  }
  
  public void addOnTabSelectedListener(@NonNull OnTabSelectedListener paramOnTabSelectedListener)
  {
    if (!this.mSelectedListeners.contains(paramOnTabSelectedListener)) {
      this.mSelectedListeners.add(paramOnTabSelectedListener);
    }
  }
  
  public void addTab(@NonNull Tab paramTab)
  {
    addTab(paramTab, this.mTabs.isEmpty());
  }
  
  public void addTab(@NonNull Tab paramTab, int paramInt)
  {
    addTab(paramTab, paramInt, this.mTabs.isEmpty());
  }
  
  public void addTab(@NonNull Tab paramTab, int paramInt, boolean paramBoolean)
  {
    if (paramTab.mParent != this) {
      throw new IllegalArgumentException("Tab belongs to a different TabLayout.");
    }
    configureTab(paramTab, paramInt);
    addTabView(paramTab);
    if (paramBoolean) {
      paramTab.select();
    }
  }
  
  public void addTab(@NonNull Tab paramTab, boolean paramBoolean)
  {
    addTab(paramTab, this.mTabs.size(), paramBoolean);
  }
  
  public void addView(View paramView)
  {
    addViewInternal(paramView);
  }
  
  public void addView(View paramView, int paramInt)
  {
    addViewInternal(paramView);
  }
  
  public void addView(View paramView, int paramInt, ViewGroup.LayoutParams paramLayoutParams)
  {
    addViewInternal(paramView);
  }
  
  public void addView(View paramView, ViewGroup.LayoutParams paramLayoutParams)
  {
    addViewInternal(paramView);
  }
  
  public void clearOnTabSelectedListeners()
  {
    this.mSelectedListeners.clear();
  }
  
  int dpToPx(int paramInt)
  {
    return Math.round(getResources().getDisplayMetrics().density * paramInt);
  }
  
  public FrameLayout.LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    return generateDefaultLayoutParams();
  }
  
  public int getSelectedTabPosition()
  {
    if (this.mSelectedTab != null) {
      return this.mSelectedTab.getPosition();
    }
    return -1;
  }
  
  @Nullable
  public Tab getTabAt(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= getTabCount())) {
      return null;
    }
    return (Tab)this.mTabs.get(paramInt);
  }
  
  public int getTabCount()
  {
    return this.mTabs.size();
  }
  
  public int getTabGravity()
  {
    return this.mTabGravity;
  }
  
  int getTabMaxWidth()
  {
    return this.mTabMaxWidth;
  }
  
  public int getTabMode()
  {
    return this.mMode;
  }
  
  @Nullable
  public ColorStateList getTabTextColors()
  {
    return this.mTabTextColors;
  }
  
  @NonNull
  public Tab newTab()
  {
    Tab localTab = (Tab)sTabPool.acquire();
    if (localTab == null) {
      localTab = new Tab();
    }
    localTab.mParent = this;
    localTab.mView = createTabView(localTab);
    return localTab;
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    if (this.mViewPager == null)
    {
      ViewParent localViewParent = getParent();
      if ((localViewParent instanceof ViewPager)) {
        setupWithViewPager((ViewPager)localViewParent, true, true);
      }
    }
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    if (this.mSetupViewPagerImplicitly)
    {
      setupWithViewPager(null);
      this.mSetupViewPagerImplicitly = false;
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = dpToPx(getDefaultHeight()) + getPaddingTop() + getPaddingBottom();
    label48:
    int j;
    switch (View.MeasureSpec.getMode(paramInt2))
    {
    default: 
      j = View.MeasureSpec.getSize(paramInt1);
      if (View.MeasureSpec.getMode(paramInt1) != 0) {
        if (this.mRequestedTabMaxWidth <= 0) {
          break label211;
        }
      }
      break;
    }
    View localView;
    label211:
    for (int i1 = this.mRequestedTabMaxWidth;; i1 = j - dpToPx(56))
    {
      this.mTabMaxWidth = i1;
      super.onMeasure(paramInt1, paramInt2);
      int k;
      if (getChildCount() == 1)
      {
        localView = getChildAt(0);
        k = this.mMode;
        m = 0;
      }
      switch (k)
      {
      default: 
        if (m != 0)
        {
          int n = getChildMeasureSpec(paramInt2, getPaddingTop() + getPaddingBottom(), localView.getLayoutParams().height);
          localView.measure(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 1073741824), n);
        }
        return;
        paramInt2 = View.MeasureSpec.makeMeasureSpec(Math.min(i, View.MeasureSpec.getSize(paramInt2)), 1073741824);
        break label48;
        paramInt2 = View.MeasureSpec.makeMeasureSpec(i, 1073741824);
        break label48;
      }
    }
    if (localView.getMeasuredWidth() < getMeasuredWidth()) {}
    for (int m = 1;; m = 0) {
      break;
    }
    if (localView.getMeasuredWidth() != getMeasuredWidth()) {}
    for (m = 1;; m = 0) {
      break;
    }
  }
  
  void populateFromPagerAdapter()
  {
    removeAllTabs();
    if (this.mPagerAdapter != null)
    {
      int i = this.mPagerAdapter.getCount();
      for (int j = 0; j < i; j++) {
        addTab(newTab().setText(this.mPagerAdapter.getPageTitle(j)), false);
      }
      if ((this.mViewPager != null) && (i > 0))
      {
        int k = this.mViewPager.getCurrentItem();
        if ((k != getSelectedTabPosition()) && (k < getTabCount())) {
          selectTab(getTabAt(k));
        }
      }
    }
  }
  
  public void removeAllTabs()
  {
    for (int i = -1 + this.mTabStrip.getChildCount(); i >= 0; i--) {
      removeTabViewAt(i);
    }
    Iterator localIterator = this.mTabs.iterator();
    while (localIterator.hasNext())
    {
      Tab localTab = (Tab)localIterator.next();
      localIterator.remove();
      localTab.reset();
      sTabPool.release(localTab);
    }
    this.mSelectedTab = null;
  }
  
  public void removeOnTabSelectedListener(@NonNull OnTabSelectedListener paramOnTabSelectedListener)
  {
    this.mSelectedListeners.remove(paramOnTabSelectedListener);
  }
  
  public void removeTab(Tab paramTab)
  {
    if (paramTab.mParent != this) {
      throw new IllegalArgumentException("Tab does not belong to this TabLayout.");
    }
    removeTabAt(paramTab.getPosition());
  }
  
  public void removeTabAt(int paramInt)
  {
    if (this.mSelectedTab != null) {}
    for (int i = this.mSelectedTab.getPosition();; i = 0)
    {
      removeTabViewAt(paramInt);
      Tab localTab1 = (Tab)this.mTabs.remove(paramInt);
      if (localTab1 != null)
      {
        localTab1.reset();
        sTabPool.release(localTab1);
      }
      int j = this.mTabs.size();
      for (int k = paramInt; k < j; k++) {
        ((Tab)this.mTabs.get(k)).setPosition(k);
      }
    }
    if (i == paramInt) {
      if (!this.mTabs.isEmpty()) {
        break label122;
      }
    }
    label122:
    for (Tab localTab2 = null;; localTab2 = (Tab)this.mTabs.get(Math.max(0, paramInt - 1)))
    {
      selectTab(localTab2);
      return;
    }
  }
  
  void selectTab(Tab paramTab)
  {
    selectTab(paramTab, true);
  }
  
  void selectTab(Tab paramTab, boolean paramBoolean)
  {
    Tab localTab = this.mSelectedTab;
    if (localTab == paramTab)
    {
      if (localTab != null)
      {
        dispatchTabReselected(paramTab);
        animateToTab(paramTab.getPosition());
      }
      return;
    }
    int i;
    if (paramTab != null)
    {
      i = paramTab.getPosition();
      label38:
      if (paramBoolean)
      {
        if (((localTab != null) && (localTab.getPosition() != -1)) || (i == -1)) {
          break label110;
        }
        setScrollPosition(i, 0.0F, true);
      }
    }
    for (;;)
    {
      if (i != -1) {
        setSelectedTabView(i);
      }
      if (localTab != null) {
        dispatchTabUnselected(localTab);
      }
      this.mSelectedTab = paramTab;
      if (paramTab == null) {
        break;
      }
      dispatchTabSelected(paramTab);
      return;
      i = -1;
      break label38;
      label110:
      animateToTab(i);
    }
  }
  
  @Deprecated
  public void setOnTabSelectedListener(@Nullable OnTabSelectedListener paramOnTabSelectedListener)
  {
    if (this.mSelectedListener != null) {
      removeOnTabSelectedListener(this.mSelectedListener);
    }
    this.mSelectedListener = paramOnTabSelectedListener;
    if (paramOnTabSelectedListener != null) {
      addOnTabSelectedListener(paramOnTabSelectedListener);
    }
  }
  
  void setPagerAdapter(@Nullable PagerAdapter paramPagerAdapter, boolean paramBoolean)
  {
    if ((this.mPagerAdapter != null) && (this.mPagerAdapterObserver != null)) {
      this.mPagerAdapter.unregisterDataSetObserver(this.mPagerAdapterObserver);
    }
    this.mPagerAdapter = paramPagerAdapter;
    if ((paramBoolean) && (paramPagerAdapter != null))
    {
      if (this.mPagerAdapterObserver == null) {
        this.mPagerAdapterObserver = new PagerAdapterObserver();
      }
      paramPagerAdapter.registerDataSetObserver(this.mPagerAdapterObserver);
    }
    populateFromPagerAdapter();
  }
  
  void setScrollAnimatorListener(ValueAnimatorCompat.AnimatorListener paramAnimatorListener)
  {
    ensureScrollAnimator();
    this.mScrollAnimator.addListener(paramAnimatorListener);
  }
  
  public void setScrollPosition(int paramInt, float paramFloat, boolean paramBoolean)
  {
    setScrollPosition(paramInt, paramFloat, paramBoolean, true);
  }
  
  void setScrollPosition(int paramInt, float paramFloat, boolean paramBoolean1, boolean paramBoolean2)
  {
    int i = Math.round(paramFloat + paramInt);
    if ((i < 0) || (i >= this.mTabStrip.getChildCount())) {}
    do
    {
      return;
      if (paramBoolean2) {
        this.mTabStrip.setIndicatorPositionFromTabPosition(paramInt, paramFloat);
      }
      if ((this.mScrollAnimator != null) && (this.mScrollAnimator.isRunning())) {
        this.mScrollAnimator.cancel();
      }
      scrollTo(calculateScrollXForTab(paramInt, paramFloat), 0);
    } while (!paramBoolean1);
    setSelectedTabView(i);
  }
  
  public void setSelectedTabIndicatorColor(@ColorInt int paramInt)
  {
    this.mTabStrip.setSelectedIndicatorColor(paramInt);
  }
  
  public void setSelectedTabIndicatorHeight(int paramInt)
  {
    this.mTabStrip.setSelectedIndicatorHeight(paramInt);
  }
  
  public void setTabGravity(int paramInt)
  {
    if (this.mTabGravity != paramInt)
    {
      this.mTabGravity = paramInt;
      applyModeAndGravity();
    }
  }
  
  public void setTabMode(int paramInt)
  {
    if (paramInt != this.mMode)
    {
      this.mMode = paramInt;
      applyModeAndGravity();
    }
  }
  
  public void setTabTextColors(int paramInt1, int paramInt2)
  {
    setTabTextColors(createColorStateList(paramInt1, paramInt2));
  }
  
  public void setTabTextColors(@Nullable ColorStateList paramColorStateList)
  {
    if (this.mTabTextColors != paramColorStateList)
    {
      this.mTabTextColors = paramColorStateList;
      updateAllTabs();
    }
  }
  
  @Deprecated
  public void setTabsFromPagerAdapter(@Nullable PagerAdapter paramPagerAdapter)
  {
    setPagerAdapter(paramPagerAdapter, false);
  }
  
  public void setupWithViewPager(@Nullable ViewPager paramViewPager)
  {
    setupWithViewPager(paramViewPager, true);
  }
  
  public void setupWithViewPager(@Nullable ViewPager paramViewPager, boolean paramBoolean)
  {
    setupWithViewPager(paramViewPager, paramBoolean, false);
  }
  
  public boolean shouldDelayChildPressedState()
  {
    return getTabScrollRange() > 0;
  }
  
  void updateTabViews(boolean paramBoolean)
  {
    for (int i = 0; i < this.mTabStrip.getChildCount(); i++)
    {
      View localView = this.mTabStrip.getChildAt(i);
      localView.setMinimumWidth(getTabMinWidth());
      updateTabViewLayoutParams((LinearLayout.LayoutParams)localView.getLayoutParams());
      if (paramBoolean) {
        localView.requestLayout();
      }
    }
  }
  
  private class AdapterChangeListener
    implements ViewPager.OnAdapterChangeListener
  {
    private boolean mAutoRefresh;
    
    AdapterChangeListener() {}
    
    public void onAdapterChanged(@NonNull ViewPager paramViewPager, @Nullable PagerAdapter paramPagerAdapter1, @Nullable PagerAdapter paramPagerAdapter2)
    {
      if (TabLayout.this.mViewPager == paramViewPager) {
        TabLayout.this.setPagerAdapter(paramPagerAdapter2, this.mAutoRefresh);
      }
    }
    
    void setAutoRefresh(boolean paramBoolean)
    {
      this.mAutoRefresh = paramBoolean;
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public static @interface Mode {}
  
  public static abstract interface OnTabSelectedListener
  {
    public abstract void onTabReselected(TabLayout.Tab paramTab);
    
    public abstract void onTabSelected(TabLayout.Tab paramTab);
    
    public abstract void onTabUnselected(TabLayout.Tab paramTab);
  }
  
  private class PagerAdapterObserver
    extends DataSetObserver
  {
    PagerAdapterObserver() {}
    
    public void onChanged()
    {
      TabLayout.this.populateFromPagerAdapter();
    }
    
    public void onInvalidated()
    {
      TabLayout.this.populateFromPagerAdapter();
    }
  }
  
  private class SlidingTabStrip
    extends LinearLayout
  {
    private ValueAnimatorCompat mIndicatorAnimator;
    private int mIndicatorLeft = -1;
    private int mIndicatorRight = -1;
    private int mSelectedIndicatorHeight;
    private final Paint mSelectedIndicatorPaint;
    int mSelectedPosition = -1;
    float mSelectionOffset;
    
    SlidingTabStrip(Context paramContext)
    {
      super();
      setWillNotDraw(false);
      this.mSelectedIndicatorPaint = new Paint();
    }
    
    private void updateIndicatorPosition()
    {
      View localView1 = getChildAt(this.mSelectedPosition);
      int j;
      int i;
      if ((localView1 != null) && (localView1.getWidth() > 0))
      {
        j = localView1.getLeft();
        i = localView1.getRight();
        if ((this.mSelectionOffset > 0.0F) && (this.mSelectedPosition < -1 + getChildCount()))
        {
          View localView2 = getChildAt(1 + this.mSelectedPosition);
          j = (int)(this.mSelectionOffset * localView2.getLeft() + (1.0F - this.mSelectionOffset) * j);
          i = (int)(this.mSelectionOffset * localView2.getRight() + (1.0F - this.mSelectionOffset) * i);
        }
      }
      for (;;)
      {
        setIndicatorPosition(j, i);
        return;
        i = -1;
        j = i;
      }
    }
    
    void animateIndicatorToPosition(final int paramInt1, int paramInt2)
    {
      if ((this.mIndicatorAnimator != null) && (this.mIndicatorAnimator.isRunning())) {
        this.mIndicatorAnimator.cancel();
      }
      int i;
      View localView;
      if (ViewCompat.getLayoutDirection(this) == 1)
      {
        i = 1;
        localView = getChildAt(paramInt1);
        if (localView != null) {
          break label56;
        }
        updateIndicatorPosition();
      }
      for (;;)
      {
        return;
        i = 0;
        break;
        label56:
        final int j = localView.getLeft();
        final int k = localView.getRight();
        final int i1;
        final int n;
        if (Math.abs(paramInt1 - this.mSelectedPosition) <= 1)
        {
          i1 = this.mIndicatorLeft;
          n = this.mIndicatorRight;
        }
        while ((i1 != j) || (n != k))
        {
          ValueAnimatorCompat localValueAnimatorCompat = ViewUtils.createAnimator();
          this.mIndicatorAnimator = localValueAnimatorCompat;
          localValueAnimatorCompat.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
          localValueAnimatorCompat.setDuration(paramInt2);
          localValueAnimatorCompat.setFloatValues(0.0F, 1.0F);
          localValueAnimatorCompat.addUpdateListener(new ValueAnimatorCompat.AnimatorUpdateListener()
          {
            public void onAnimationUpdate(ValueAnimatorCompat paramAnonymousValueAnimatorCompat)
            {
              float f = paramAnonymousValueAnimatorCompat.getAnimatedFraction();
              TabLayout.SlidingTabStrip.this.setIndicatorPosition(AnimationUtils.lerp(i1, j, f), AnimationUtils.lerp(n, k, f));
            }
          });
          localValueAnimatorCompat.addListener(new ValueAnimatorCompat.AnimatorListenerAdapter()
          {
            public void onAnimationEnd(ValueAnimatorCompat paramAnonymousValueAnimatorCompat)
            {
              TabLayout.SlidingTabStrip.this.mSelectedPosition = paramInt1;
              TabLayout.SlidingTabStrip.this.mSelectionOffset = 0.0F;
            }
          });
          localValueAnimatorCompat.start();
          return;
          int m = TabLayout.this.dpToPx(24);
          if (paramInt1 < this.mSelectedPosition)
          {
            if (i != 0)
            {
              n = j - m;
              i1 = n;
            }
            else
            {
              n = k + m;
              i1 = n;
            }
          }
          else if (i != 0)
          {
            n = k + m;
            i1 = n;
          }
          else
          {
            n = j - m;
            i1 = n;
          }
        }
      }
    }
    
    boolean childrenNeedLayout()
    {
      int i = 0;
      int j = getChildCount();
      while (i < j)
      {
        if (getChildAt(i).getWidth() <= 0) {
          return true;
        }
        i++;
      }
      return false;
    }
    
    public void draw(Canvas paramCanvas)
    {
      super.draw(paramCanvas);
      if ((this.mIndicatorLeft >= 0) && (this.mIndicatorRight > this.mIndicatorLeft)) {
        paramCanvas.drawRect(this.mIndicatorLeft, getHeight() - this.mSelectedIndicatorHeight, this.mIndicatorRight, getHeight(), this.mSelectedIndicatorPaint);
      }
    }
    
    float getIndicatorPosition()
    {
      return this.mSelectedPosition + this.mSelectionOffset;
    }
    
    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
      if ((this.mIndicatorAnimator != null) && (this.mIndicatorAnimator.isRunning()))
      {
        this.mIndicatorAnimator.cancel();
        long l = this.mIndicatorAnimator.getDuration();
        animateIndicatorToPosition(this.mSelectedPosition, Math.round((1.0F - this.mIndicatorAnimator.getAnimatedFraction()) * (float)l));
        return;
      }
      updateIndicatorPosition();
    }
    
    protected void onMeasure(int paramInt1, int paramInt2)
    {
      super.onMeasure(paramInt1, paramInt2);
      if (View.MeasureSpec.getMode(paramInt1) != 1073741824) {}
      int n;
      do
      {
        int i;
        int j;
        do
        {
          do
          {
            return;
          } while ((TabLayout.this.mMode != 1) || (TabLayout.this.mTabGravity != 1));
          i = getChildCount();
          j = 0;
          for (int k = 0; k < i; k++)
          {
            View localView = getChildAt(k);
            if (localView.getVisibility() == 0) {
              j = Math.max(j, localView.getMeasuredWidth());
            }
          }
        } while (j <= 0);
        int m = TabLayout.this.dpToPx(16);
        n = 0;
        if (j * i <= getMeasuredWidth() - m * 2) {
          for (int i1 = 0; i1 < i; i1++)
          {
            LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams)getChildAt(i1).getLayoutParams();
            if ((localLayoutParams.width != j) || (localLayoutParams.weight != 0.0F))
            {
              localLayoutParams.width = j;
              localLayoutParams.weight = 0.0F;
              n = 1;
            }
          }
        }
        TabLayout.this.mTabGravity = 0;
        TabLayout.this.updateTabViews(false);
        n = 1;
      } while (n == 0);
      super.onMeasure(paramInt1, paramInt2);
    }
    
    void setIndicatorPosition(int paramInt1, int paramInt2)
    {
      if ((paramInt1 != this.mIndicatorLeft) || (paramInt2 != this.mIndicatorRight))
      {
        this.mIndicatorLeft = paramInt1;
        this.mIndicatorRight = paramInt2;
        ViewCompat.postInvalidateOnAnimation(this);
      }
    }
    
    void setIndicatorPositionFromTabPosition(int paramInt, float paramFloat)
    {
      if ((this.mIndicatorAnimator != null) && (this.mIndicatorAnimator.isRunning())) {
        this.mIndicatorAnimator.cancel();
      }
      this.mSelectedPosition = paramInt;
      this.mSelectionOffset = paramFloat;
      updateIndicatorPosition();
    }
    
    void setSelectedIndicatorColor(int paramInt)
    {
      if (this.mSelectedIndicatorPaint.getColor() != paramInt)
      {
        this.mSelectedIndicatorPaint.setColor(paramInt);
        ViewCompat.postInvalidateOnAnimation(this);
      }
    }
    
    void setSelectedIndicatorHeight(int paramInt)
    {
      if (this.mSelectedIndicatorHeight != paramInt)
      {
        this.mSelectedIndicatorHeight = paramInt;
        ViewCompat.postInvalidateOnAnimation(this);
      }
    }
  }
  
  public static final class Tab
  {
    public static final int INVALID_POSITION = -1;
    private CharSequence mContentDesc;
    private View mCustomView;
    private Drawable mIcon;
    TabLayout mParent;
    private int mPosition = -1;
    private Object mTag;
    private CharSequence mText;
    TabLayout.TabView mView;
    
    Tab() {}
    
    @Nullable
    public CharSequence getContentDescription()
    {
      return this.mContentDesc;
    }
    
    @Nullable
    public View getCustomView()
    {
      return this.mCustomView;
    }
    
    @Nullable
    public Drawable getIcon()
    {
      return this.mIcon;
    }
    
    public int getPosition()
    {
      return this.mPosition;
    }
    
    @Nullable
    public Object getTag()
    {
      return this.mTag;
    }
    
    @Nullable
    public CharSequence getText()
    {
      return this.mText;
    }
    
    public boolean isSelected()
    {
      if (this.mParent == null) {
        throw new IllegalArgumentException("Tab not attached to a TabLayout");
      }
      return this.mParent.getSelectedTabPosition() == this.mPosition;
    }
    
    void reset()
    {
      this.mParent = null;
      this.mView = null;
      this.mTag = null;
      this.mIcon = null;
      this.mText = null;
      this.mContentDesc = null;
      this.mPosition = -1;
      this.mCustomView = null;
    }
    
    public void select()
    {
      if (this.mParent == null) {
        throw new IllegalArgumentException("Tab not attached to a TabLayout");
      }
      this.mParent.selectTab(this);
    }
    
    @NonNull
    public Tab setContentDescription(@StringRes int paramInt)
    {
      if (this.mParent == null) {
        throw new IllegalArgumentException("Tab not attached to a TabLayout");
      }
      return setContentDescription(this.mParent.getResources().getText(paramInt));
    }
    
    @NonNull
    public Tab setContentDescription(@Nullable CharSequence paramCharSequence)
    {
      this.mContentDesc = paramCharSequence;
      updateView();
      return this;
    }
    
    @NonNull
    public Tab setCustomView(@LayoutRes int paramInt)
    {
      return setCustomView(LayoutInflater.from(this.mView.getContext()).inflate(paramInt, this.mView, false));
    }
    
    @NonNull
    public Tab setCustomView(@Nullable View paramView)
    {
      this.mCustomView = paramView;
      updateView();
      return this;
    }
    
    @NonNull
    public Tab setIcon(@DrawableRes int paramInt)
    {
      if (this.mParent == null) {
        throw new IllegalArgumentException("Tab not attached to a TabLayout");
      }
      return setIcon(AppCompatResources.getDrawable(this.mParent.getContext(), paramInt));
    }
    
    @NonNull
    public Tab setIcon(@Nullable Drawable paramDrawable)
    {
      this.mIcon = paramDrawable;
      updateView();
      return this;
    }
    
    void setPosition(int paramInt)
    {
      this.mPosition = paramInt;
    }
    
    @NonNull
    public Tab setTag(@Nullable Object paramObject)
    {
      this.mTag = paramObject;
      return this;
    }
    
    @NonNull
    public Tab setText(@StringRes int paramInt)
    {
      if (this.mParent == null) {
        throw new IllegalArgumentException("Tab not attached to a TabLayout");
      }
      return setText(this.mParent.getResources().getText(paramInt));
    }
    
    @NonNull
    public Tab setText(@Nullable CharSequence paramCharSequence)
    {
      this.mText = paramCharSequence;
      updateView();
      return this;
    }
    
    void updateView()
    {
      if (this.mView != null) {
        this.mView.update();
      }
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public static @interface TabGravity {}
  
  public static class TabLayoutOnPageChangeListener
    implements ViewPager.OnPageChangeListener
  {
    private int mPreviousScrollState;
    private int mScrollState;
    private final WeakReference<TabLayout> mTabLayoutRef;
    
    public TabLayoutOnPageChangeListener(TabLayout paramTabLayout)
    {
      this.mTabLayoutRef = new WeakReference(paramTabLayout);
    }
    
    public void onPageScrollStateChanged(int paramInt)
    {
      this.mPreviousScrollState = this.mScrollState;
      this.mScrollState = paramInt;
    }
    
    public void onPageScrolled(int paramInt1, float paramFloat, int paramInt2)
    {
      TabLayout localTabLayout = (TabLayout)this.mTabLayoutRef.get();
      boolean bool1;
      if (localTabLayout != null)
      {
        if ((this.mScrollState == 2) && (this.mPreviousScrollState != 1)) {
          break label66;
        }
        bool1 = true;
        if ((this.mScrollState == 2) && (this.mPreviousScrollState == 0)) {
          break label72;
        }
      }
      label66:
      label72:
      for (boolean bool2 = true;; bool2 = false)
      {
        localTabLayout.setScrollPosition(paramInt1, paramFloat, bool1, bool2);
        return;
        bool1 = false;
        break;
      }
    }
    
    public void onPageSelected(int paramInt)
    {
      TabLayout localTabLayout = (TabLayout)this.mTabLayoutRef.get();
      if ((localTabLayout != null) && (localTabLayout.getSelectedTabPosition() != paramInt) && (paramInt < localTabLayout.getTabCount())) {
        if ((this.mScrollState != 0) && ((this.mScrollState != 2) || (this.mPreviousScrollState != 0))) {
          break label66;
        }
      }
      label66:
      for (boolean bool = true;; bool = false)
      {
        localTabLayout.selectTab(localTabLayout.getTabAt(paramInt), bool);
        return;
      }
    }
    
    void reset()
    {
      this.mScrollState = 0;
      this.mPreviousScrollState = 0;
    }
  }
  
  class TabView
    extends LinearLayout
    implements View.OnLongClickListener
  {
    private ImageView mCustomIconView;
    private TextView mCustomTextView;
    private View mCustomView;
    private int mDefaultMaxLines = 2;
    private ImageView mIconView;
    private TabLayout.Tab mTab;
    private TextView mTextView;
    
    public TabView(Context paramContext)
    {
      super();
      if (TabLayout.this.mTabBackgroundResId != 0) {
        ViewCompat.setBackground(this, AppCompatResources.getDrawable(paramContext, TabLayout.this.mTabBackgroundResId));
      }
      ViewCompat.setPaddingRelative(this, TabLayout.this.mTabPaddingStart, TabLayout.this.mTabPaddingTop, TabLayout.this.mTabPaddingEnd, TabLayout.this.mTabPaddingBottom);
      setGravity(17);
      setOrientation(1);
      setClickable(true);
      ViewCompat.setPointerIcon(this, PointerIconCompat.getSystemIcon(getContext(), 1002));
    }
    
    private float approximateLineWidth(Layout paramLayout, int paramInt, float paramFloat)
    {
      return paramLayout.getLineWidth(paramInt) * (paramFloat / paramLayout.getPaint().getTextSize());
    }
    
    private void updateTextAndIcon(@Nullable TextView paramTextView, @Nullable ImageView paramImageView)
    {
      Drawable localDrawable;
      CharSequence localCharSequence1;
      label31:
      CharSequence localCharSequence2;
      label47:
      label70:
      int i;
      if (this.mTab != null)
      {
        localDrawable = this.mTab.getIcon();
        if (this.mTab == null) {
          break label209;
        }
        localCharSequence1 = this.mTab.getText();
        if (this.mTab == null) {
          break label215;
        }
        localCharSequence2 = this.mTab.getContentDescription();
        if (paramImageView != null)
        {
          if (localDrawable == null) {
            break label221;
          }
          paramImageView.setImageDrawable(localDrawable);
          paramImageView.setVisibility(0);
          setVisibility(0);
          paramImageView.setContentDescription(localCharSequence2);
        }
        if (TextUtils.isEmpty(localCharSequence1)) {
          break label235;
        }
        i = 1;
        label87:
        if (paramTextView != null)
        {
          if (i == 0) {
            break label241;
          }
          paramTextView.setText(localCharSequence1);
          paramTextView.setVisibility(0);
          setVisibility(0);
        }
      }
      for (;;)
      {
        paramTextView.setContentDescription(localCharSequence2);
        if (paramImageView != null)
        {
          ViewGroup.MarginLayoutParams localMarginLayoutParams = (ViewGroup.MarginLayoutParams)paramImageView.getLayoutParams();
          int j = 0;
          if (i != 0)
          {
            int k = paramImageView.getVisibility();
            j = 0;
            if (k == 0) {
              j = TabLayout.this.dpToPx(8);
            }
          }
          if (j != localMarginLayoutParams.bottomMargin)
          {
            localMarginLayoutParams.bottomMargin = j;
            paramImageView.requestLayout();
          }
        }
        if ((i != 0) || (TextUtils.isEmpty(localCharSequence2))) {
          break label255;
        }
        setOnLongClickListener(this);
        return;
        localDrawable = null;
        break;
        label209:
        localCharSequence1 = null;
        break label31;
        label215:
        localCharSequence2 = null;
        break label47;
        label221:
        paramImageView.setVisibility(8);
        paramImageView.setImageDrawable(null);
        break label70;
        label235:
        i = 0;
        break label87;
        label241:
        paramTextView.setVisibility(8);
        paramTextView.setText(null);
      }
      label255:
      setOnLongClickListener(null);
      setLongClickable(false);
    }
    
    public TabLayout.Tab getTab()
    {
      return this.mTab;
    }
    
    @TargetApi(14)
    public void onInitializeAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
    {
      super.onInitializeAccessibilityEvent(paramAccessibilityEvent);
      paramAccessibilityEvent.setClassName(ActionBar.Tab.class.getName());
    }
    
    @TargetApi(14)
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo paramAccessibilityNodeInfo)
    {
      super.onInitializeAccessibilityNodeInfo(paramAccessibilityNodeInfo);
      paramAccessibilityNodeInfo.setClassName(ActionBar.Tab.class.getName());
    }
    
    public boolean onLongClick(View paramView)
    {
      int[] arrayOfInt = new int[2];
      Rect localRect = new Rect();
      getLocationOnScreen(arrayOfInt);
      getWindowVisibleDisplayFrame(localRect);
      Context localContext = getContext();
      int i = getWidth();
      int j = getHeight();
      int k = arrayOfInt[1] + j / 2;
      int m = arrayOfInt[0] + i / 2;
      if (ViewCompat.getLayoutDirection(paramView) == 0) {
        m = localContext.getResources().getDisplayMetrics().widthPixels - m;
      }
      Toast localToast = Toast.makeText(localContext, this.mTab.getContentDescription(), 0);
      if (k < localRect.height()) {
        localToast.setGravity(8388661, m, j + arrayOfInt[1] - localRect.top);
      }
      for (;;)
      {
        localToast.show();
        return true;
        localToast.setGravity(81, 0, j);
      }
    }
    
    public void onMeasure(int paramInt1, int paramInt2)
    {
      int i = View.MeasureSpec.getSize(paramInt1);
      int j = View.MeasureSpec.getMode(paramInt1);
      int k = TabLayout.this.getTabMaxWidth();
      int m;
      float f1;
      int n;
      if ((k > 0) && ((j == 0) || (i > k)))
      {
        m = View.MeasureSpec.makeMeasureSpec(TabLayout.this.mTabMaxWidth, Integer.MIN_VALUE);
        super.onMeasure(m, paramInt2);
        if (this.mTextView != null)
        {
          getResources();
          f1 = TabLayout.this.mTabTextSize;
          n = this.mDefaultMaxLines;
          if ((this.mIconView == null) || (this.mIconView.getVisibility() != 0)) {
            break label263;
          }
          n = 1;
        }
      }
      for (;;)
      {
        float f2 = this.mTextView.getTextSize();
        int i1 = this.mTextView.getLineCount();
        int i2 = TextViewCompat.getMaxLines(this.mTextView);
        if ((f1 != f2) || ((i2 >= 0) && (n != i2)))
        {
          int i3 = 1;
          if ((TabLayout.this.mMode == 1) && (f1 > f2) && (i1 == 1))
          {
            Layout localLayout = this.mTextView.getLayout();
            if ((localLayout == null) || (approximateLineWidth(localLayout, 0, f1) > getMeasuredWidth() - getPaddingLeft() - getPaddingRight())) {
              i3 = 0;
            }
          }
          if (i3 != 0)
          {
            this.mTextView.setTextSize(0, f1);
            this.mTextView.setMaxLines(n);
            super.onMeasure(m, paramInt2);
          }
        }
        return;
        m = paramInt1;
        break;
        label263:
        if ((this.mTextView != null) && (this.mTextView.getLineCount() > 1)) {
          f1 = TabLayout.this.mTabTextMultiLineSize;
        }
      }
    }
    
    public boolean performClick()
    {
      boolean bool = super.performClick();
      if (this.mTab != null)
      {
        if (!bool) {
          playSoundEffect(0);
        }
        this.mTab.select();
        bool = true;
      }
      return bool;
    }
    
    void reset()
    {
      setTab(null);
      setSelected(false);
    }
    
    public void setSelected(boolean paramBoolean)
    {
      if (isSelected() != paramBoolean) {}
      for (int i = 1;; i = 0)
      {
        super.setSelected(paramBoolean);
        if ((i != 0) && (paramBoolean) && (Build.VERSION.SDK_INT < 16)) {
          sendAccessibilityEvent(4);
        }
        if (this.mTextView != null) {
          this.mTextView.setSelected(paramBoolean);
        }
        if (this.mIconView != null) {
          this.mIconView.setSelected(paramBoolean);
        }
        if (this.mCustomView != null) {
          this.mCustomView.setSelected(paramBoolean);
        }
        return;
      }
    }
    
    void setTab(@Nullable TabLayout.Tab paramTab)
    {
      if (paramTab != this.mTab)
      {
        this.mTab = paramTab;
        update();
      }
    }
    
    final void update()
    {
      TabLayout.Tab localTab = this.mTab;
      View localView;
      if (localTab != null)
      {
        localView = localTab.getCustomView();
        if (localView == null) {
          break label311;
        }
        ViewParent localViewParent = localView.getParent();
        if (localViewParent != this)
        {
          if (localViewParent != null) {
            ((ViewGroup)localViewParent).removeView(localView);
          }
          addView(localView);
        }
        this.mCustomView = localView;
        if (this.mTextView != null) {
          this.mTextView.setVisibility(8);
        }
        if (this.mIconView != null)
        {
          this.mIconView.setVisibility(8);
          this.mIconView.setImageDrawable(null);
        }
        this.mCustomTextView = ((TextView)localView.findViewById(16908308));
        if (this.mCustomTextView != null) {
          this.mDefaultMaxLines = TextViewCompat.getMaxLines(this.mCustomTextView);
        }
        this.mCustomIconView = ((ImageView)localView.findViewById(16908294));
        label140:
        if (this.mCustomView != null) {
          break label344;
        }
        if (this.mIconView == null)
        {
          ImageView localImageView = (ImageView)LayoutInflater.from(getContext()).inflate(R.layout.design_layout_tab_icon, this, false);
          addView(localImageView, 0);
          this.mIconView = localImageView;
        }
        if (this.mTextView == null)
        {
          TextView localTextView = (TextView)LayoutInflater.from(getContext()).inflate(R.layout.design_layout_tab_text, this, false);
          addView(localTextView);
          this.mTextView = localTextView;
          this.mDefaultMaxLines = TextViewCompat.getMaxLines(this.mTextView);
        }
        TextViewCompat.setTextAppearance(this.mTextView, TabLayout.this.mTabTextAppearance);
        if (TabLayout.this.mTabTextColors != null) {
          this.mTextView.setTextColor(TabLayout.this.mTabTextColors);
        }
        updateTextAndIcon(this.mTextView, this.mIconView);
        label287:
        if ((localTab == null) || (!localTab.isSelected())) {
          break label373;
        }
      }
      label311:
      label344:
      label373:
      for (boolean bool = true;; bool = false)
      {
        setSelected(bool);
        return;
        localView = null;
        break;
        if (this.mCustomView != null)
        {
          removeView(this.mCustomView);
          this.mCustomView = null;
        }
        this.mCustomTextView = null;
        this.mCustomIconView = null;
        break label140;
        if ((this.mCustomTextView == null) && (this.mCustomIconView == null)) {
          break label287;
        }
        updateTextAndIcon(this.mCustomTextView, this.mCustomIconView);
        break label287;
      }
    }
  }
  
  public static class ViewPagerOnTabSelectedListener
    implements TabLayout.OnTabSelectedListener
  {
    private final ViewPager mViewPager;
    
    public ViewPagerOnTabSelectedListener(ViewPager paramViewPager)
    {
      this.mViewPager = paramViewPager;
    }
    
    public void onTabReselected(TabLayout.Tab paramTab) {}
    
    public void onTabSelected(TabLayout.Tab paramTab)
    {
      this.mViewPager.setCurrentItem(paramTab.getPosition());
    }
    
    public void onTabUnselected(TabLayout.Tab paramTab) {}
  }
}
