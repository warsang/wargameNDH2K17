package android.support.design.internal;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Build.VERSION;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.design.R.dimen;
import android.support.v4.util.Pools.Pool;
import android.support.v4.util.Pools.SynchronizedPool;
import android.support.v4.view.ViewCompat;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuView;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public class BottomNavigationMenuView
  extends ViewGroup
  implements MenuView
{
  private final int mActiveItemMaxWidth;
  private final BottomNavigationAnimationHelperBase mAnimationHelper;
  private BottomNavigationItemView[] mButtons;
  private final int mInactiveItemMaxWidth;
  private final int mInactiveItemMinWidth;
  private int mItemBackgroundRes;
  private final int mItemHeight;
  private ColorStateList mItemIconTint;
  private final Pools.Pool<BottomNavigationItemView> mItemPool = new Pools.SynchronizedPool(5);
  private ColorStateList mItemTextColor;
  private MenuBuilder mMenu;
  private final View.OnClickListener mOnClickListener;
  private BottomNavigationPresenter mPresenter;
  private int mSelectedItemId = 0;
  private int mSelectedItemPosition = 0;
  private boolean mShiftingMode = true;
  private int[] mTempChildWidths;
  
  public BottomNavigationMenuView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public BottomNavigationMenuView(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
    Resources localResources = getResources();
    this.mInactiveItemMaxWidth = localResources.getDimensionPixelSize(R.dimen.design_bottom_navigation_item_max_width);
    this.mInactiveItemMinWidth = localResources.getDimensionPixelSize(R.dimen.design_bottom_navigation_item_min_width);
    this.mActiveItemMaxWidth = localResources.getDimensionPixelSize(R.dimen.design_bottom_navigation_active_item_max_width);
    this.mItemHeight = localResources.getDimensionPixelSize(R.dimen.design_bottom_navigation_height);
    if (Build.VERSION.SDK_INT >= 14) {}
    for (this.mAnimationHelper = new BottomNavigationAnimationHelperIcs();; this.mAnimationHelper = new BottomNavigationAnimationHelperBase())
    {
      this.mOnClickListener = new View.OnClickListener()
      {
        public void onClick(View paramAnonymousView)
        {
          MenuItemImpl localMenuItemImpl = ((BottomNavigationItemView)paramAnonymousView).getItemData();
          if (!BottomNavigationMenuView.this.mMenu.performItemAction(localMenuItemImpl, BottomNavigationMenuView.this.mPresenter, 0)) {
            localMenuItemImpl.setChecked(true);
          }
        }
      };
      this.mTempChildWidths = new int[5];
      return;
    }
  }
  
  private BottomNavigationItemView getNewItem()
  {
    BottomNavigationItemView localBottomNavigationItemView = (BottomNavigationItemView)this.mItemPool.acquire();
    if (localBottomNavigationItemView == null) {
      localBottomNavigationItemView = new BottomNavigationItemView(getContext());
    }
    return localBottomNavigationItemView;
  }
  
  public void buildMenuView()
  {
    removeAllViews();
    if (this.mButtons != null) {
      for (BottomNavigationItemView localBottomNavigationItemView2 : this.mButtons) {
        this.mItemPool.release(localBottomNavigationItemView2);
      }
    }
    if (this.mMenu.size() == 0)
    {
      this.mSelectedItemId = 0;
      this.mSelectedItemPosition = 0;
      this.mButtons = null;
      return;
    }
    this.mButtons = new BottomNavigationItemView[this.mMenu.size()];
    if (this.mMenu.size() > 3) {}
    for (boolean bool = true;; bool = false)
    {
      this.mShiftingMode = bool;
      for (int i = 0; i < this.mMenu.size(); i++)
      {
        this.mPresenter.setUpdateSuspended(true);
        this.mMenu.getItem(i).setCheckable(true);
        this.mPresenter.setUpdateSuspended(false);
        BottomNavigationItemView localBottomNavigationItemView1 = getNewItem();
        this.mButtons[i] = localBottomNavigationItemView1;
        localBottomNavigationItemView1.setIconTintList(this.mItemIconTint);
        localBottomNavigationItemView1.setTextColor(this.mItemTextColor);
        localBottomNavigationItemView1.setItemBackground(this.mItemBackgroundRes);
        localBottomNavigationItemView1.setShiftingMode(this.mShiftingMode);
        localBottomNavigationItemView1.initialize((MenuItemImpl)this.mMenu.getItem(i), 0);
        localBottomNavigationItemView1.setItemPosition(i);
        localBottomNavigationItemView1.setOnClickListener(this.mOnClickListener);
        addView(localBottomNavigationItemView1);
      }
    }
    this.mSelectedItemPosition = Math.min(-1 + this.mMenu.size(), this.mSelectedItemPosition);
    this.mMenu.getItem(this.mSelectedItemPosition).setChecked(true);
  }
  
  @Nullable
  public ColorStateList getIconTintList()
  {
    return this.mItemIconTint;
  }
  
  public int getItemBackgroundRes()
  {
    return this.mItemBackgroundRes;
  }
  
  public ColorStateList getItemTextColor()
  {
    return this.mItemTextColor;
  }
  
  public int getSelectedItemId()
  {
    return this.mSelectedItemId;
  }
  
  public int getWindowAnimations()
  {
    return 0;
  }
  
  public void initialize(MenuBuilder paramMenuBuilder)
  {
    this.mMenu = paramMenuBuilder;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = getChildCount();
    int j = paramInt3 - paramInt1;
    int k = paramInt4 - paramInt2;
    int m = 0;
    int n = 0;
    while (n < i)
    {
      View localView = getChildAt(n);
      if (localView.getVisibility() == 8)
      {
        n++;
      }
      else
      {
        if (ViewCompat.getLayoutDirection(this) == 1) {
          localView.layout(j - m - localView.getMeasuredWidth(), 0, j - m, k);
        }
        for (;;)
        {
          m += localView.getMeasuredWidth();
          break;
          localView.layout(m, 0, m + localView.getMeasuredWidth(), k);
        }
      }
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = View.MeasureSpec.getSize(paramInt1);
    int j = getChildCount();
    int k = View.MeasureSpec.makeMeasureSpec(this.mItemHeight, 1073741824);
    if (this.mShiftingMode)
    {
      int i6 = j - 1;
      int i7 = Math.min(i - i6 * this.mInactiveItemMinWidth, this.mActiveItemMaxWidth);
      int i8 = Math.min((i - i7) / i6, this.mInactiveItemMaxWidth);
      int i9 = i - i7 - i8 * i6;
      int i10 = 0;
      if (i10 < j)
      {
        int[] arrayOfInt2 = this.mTempChildWidths;
        if (i10 == this.mSelectedItemPosition) {}
        for (int i11 = i7;; i11 = i8)
        {
          arrayOfInt2[i10] = i11;
          if (i9 > 0)
          {
            int[] arrayOfInt3 = this.mTempChildWidths;
            arrayOfInt3[i10] = (1 + arrayOfInt3[i10]);
            i9--;
          }
          i10++;
          break;
        }
      }
    }
    else
    {
      if (j == 0) {}
      for (int m = 1;; m = j)
      {
        int n = Math.min(i / m, this.mActiveItemMaxWidth);
        int i1 = i - n * j;
        for (int i2 = 0; i2 < j; i2++)
        {
          this.mTempChildWidths[i2] = n;
          if (i1 > 0)
          {
            int[] arrayOfInt1 = this.mTempChildWidths;
            arrayOfInt1[i2] = (1 + arrayOfInt1[i2]);
            i1--;
          }
        }
      }
    }
    int i3 = 0;
    int i4 = 0;
    if (i4 < j)
    {
      View localView = getChildAt(i4);
      if (localView.getVisibility() == 8) {}
      for (;;)
      {
        i4++;
        break;
        localView.measure(View.MeasureSpec.makeMeasureSpec(this.mTempChildWidths[i4], 1073741824), k);
        localView.getLayoutParams().width = localView.getMeasuredWidth();
        i3 += localView.getMeasuredWidth();
      }
    }
    int i5 = View.MeasureSpec.makeMeasureSpec(i3, 1073741824);
    setMeasuredDimension(ViewCompat.resolveSizeAndState(i3, i5, 0), ViewCompat.resolveSizeAndState(this.mItemHeight, k, 0));
  }
  
  public void setIconTintList(ColorStateList paramColorStateList)
  {
    this.mItemIconTint = paramColorStateList;
    if (this.mButtons == null) {}
    for (;;)
    {
      return;
      BottomNavigationItemView[] arrayOfBottomNavigationItemView = this.mButtons;
      int i = arrayOfBottomNavigationItemView.length;
      for (int j = 0; j < i; j++) {
        arrayOfBottomNavigationItemView[j].setIconTintList(paramColorStateList);
      }
    }
  }
  
  public void setItemBackgroundRes(int paramInt)
  {
    this.mItemBackgroundRes = paramInt;
    if (this.mButtons == null) {}
    for (;;)
    {
      return;
      BottomNavigationItemView[] arrayOfBottomNavigationItemView = this.mButtons;
      int i = arrayOfBottomNavigationItemView.length;
      for (int j = 0; j < i; j++) {
        arrayOfBottomNavigationItemView[j].setItemBackground(paramInt);
      }
    }
  }
  
  public void setItemTextColor(ColorStateList paramColorStateList)
  {
    this.mItemTextColor = paramColorStateList;
    if (this.mButtons == null) {}
    for (;;)
    {
      return;
      BottomNavigationItemView[] arrayOfBottomNavigationItemView = this.mButtons;
      int i = arrayOfBottomNavigationItemView.length;
      for (int j = 0; j < i; j++) {
        arrayOfBottomNavigationItemView[j].setTextColor(paramColorStateList);
      }
    }
  }
  
  public void setPresenter(BottomNavigationPresenter paramBottomNavigationPresenter)
  {
    this.mPresenter = paramBottomNavigationPresenter;
  }
  
  void tryRestoreSelectedItemId(int paramInt)
  {
    int i = this.mMenu.size();
    for (int j = 0;; j++) {
      if (j < i)
      {
        MenuItem localMenuItem = this.mMenu.getItem(j);
        if (paramInt == localMenuItem.getItemId())
        {
          this.mSelectedItemId = paramInt;
          this.mSelectedItemPosition = j;
          localMenuItem.setChecked(true);
        }
      }
      else
      {
        return;
      }
    }
  }
  
  public void updateMenuView()
  {
    int i = this.mMenu.size();
    if (i != this.mButtons.length) {
      buildMenuView();
    }
    for (;;)
    {
      return;
      int j = this.mSelectedItemId;
      for (int k = 0; k < i; k++)
      {
        MenuItem localMenuItem = this.mMenu.getItem(k);
        if (localMenuItem.isChecked())
        {
          this.mSelectedItemId = localMenuItem.getItemId();
          this.mSelectedItemPosition = k;
        }
      }
      if (j != this.mSelectedItemId) {
        this.mAnimationHelper.beginDelayedTransition(this);
      }
      for (int m = 0; m < i; m++)
      {
        this.mPresenter.setUpdateSuspended(true);
        this.mButtons[m].initialize((MenuItemImpl)this.mMenu.getItem(m), 0);
        this.mPresenter.setUpdateSuspended(false);
      }
    }
  }
}
