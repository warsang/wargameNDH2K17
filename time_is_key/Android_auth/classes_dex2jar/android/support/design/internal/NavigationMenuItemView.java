package android.support.design.internal;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.RestrictTo;
import android.support.design.R.dimen;
import android.support.design.R.drawable;
import android.support.design.R.id;
import android.support.design.R.layout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.appcompat.R.attr;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuView.ItemView;
import android.support.v7.widget.LinearLayoutCompat.LayoutParams;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.CheckedTextView;
import android.widget.FrameLayout;

@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public class NavigationMenuItemView
  extends ForegroundLinearLayout
  implements MenuView.ItemView
{
  private static final int[] CHECKED_STATE_SET = { 16842912 };
  private final AccessibilityDelegateCompat mAccessibilityDelegate = new AccessibilityDelegateCompat()
  {
    public void onInitializeAccessibilityNodeInfo(View paramAnonymousView, AccessibilityNodeInfoCompat paramAnonymousAccessibilityNodeInfoCompat)
    {
      super.onInitializeAccessibilityNodeInfo(paramAnonymousView, paramAnonymousAccessibilityNodeInfoCompat);
      paramAnonymousAccessibilityNodeInfoCompat.setCheckable(NavigationMenuItemView.this.mCheckable);
    }
  };
  private FrameLayout mActionArea;
  boolean mCheckable;
  private Drawable mEmptyDrawable;
  private boolean mHasIconTintList;
  private final int mIconSize;
  private ColorStateList mIconTintList;
  private MenuItemImpl mItemData;
  private boolean mNeedsEmptyIcon;
  private final CheckedTextView mTextView;
  
  public NavigationMenuItemView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public NavigationMenuItemView(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public NavigationMenuItemView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    setOrientation(0);
    LayoutInflater.from(paramContext).inflate(R.layout.design_navigation_menu_item, this, true);
    this.mIconSize = paramContext.getResources().getDimensionPixelSize(R.dimen.design_navigation_icon_size);
    this.mTextView = ((CheckedTextView)findViewById(R.id.design_menu_item_text));
    this.mTextView.setDuplicateParentStateEnabled(true);
    ViewCompat.setAccessibilityDelegate(this.mTextView, this.mAccessibilityDelegate);
  }
  
  private void adjustAppearance()
  {
    if (shouldExpandActionArea())
    {
      this.mTextView.setVisibility(8);
      if (this.mActionArea != null)
      {
        LinearLayoutCompat.LayoutParams localLayoutParams2 = (LinearLayoutCompat.LayoutParams)this.mActionArea.getLayoutParams();
        localLayoutParams2.width = -1;
        this.mActionArea.setLayoutParams(localLayoutParams2);
      }
    }
    do
    {
      return;
      this.mTextView.setVisibility(0);
    } while (this.mActionArea == null);
    LinearLayoutCompat.LayoutParams localLayoutParams1 = (LinearLayoutCompat.LayoutParams)this.mActionArea.getLayoutParams();
    localLayoutParams1.width = -2;
    this.mActionArea.setLayoutParams(localLayoutParams1);
  }
  
  private StateListDrawable createDefaultBackground()
  {
    TypedValue localTypedValue = new TypedValue();
    if (getContext().getTheme().resolveAttribute(R.attr.colorControlHighlight, localTypedValue, true))
    {
      StateListDrawable localStateListDrawable = new StateListDrawable();
      localStateListDrawable.addState(CHECKED_STATE_SET, new ColorDrawable(localTypedValue.data));
      localStateListDrawable.addState(EMPTY_STATE_SET, new ColorDrawable(0));
      return localStateListDrawable;
    }
    return null;
  }
  
  private void setActionView(View paramView)
  {
    if (paramView != null)
    {
      if (this.mActionArea == null) {
        this.mActionArea = ((FrameLayout)((ViewStub)findViewById(R.id.design_menu_item_action_area_stub)).inflate());
      }
      this.mActionArea.removeAllViews();
      this.mActionArea.addView(paramView);
    }
  }
  
  private boolean shouldExpandActionArea()
  {
    return (this.mItemData.getTitle() == null) && (this.mItemData.getIcon() == null) && (this.mItemData.getActionView() != null);
  }
  
  public MenuItemImpl getItemData()
  {
    return this.mItemData;
  }
  
  public void initialize(MenuItemImpl paramMenuItemImpl, int paramInt)
  {
    this.mItemData = paramMenuItemImpl;
    if (paramMenuItemImpl.isVisible()) {}
    for (int i = 0;; i = 8)
    {
      setVisibility(i);
      if (getBackground() == null) {
        ViewCompat.setBackground(this, createDefaultBackground());
      }
      setCheckable(paramMenuItemImpl.isCheckable());
      setChecked(paramMenuItemImpl.isChecked());
      setEnabled(paramMenuItemImpl.isEnabled());
      setTitle(paramMenuItemImpl.getTitle());
      setIcon(paramMenuItemImpl.getIcon());
      setActionView(paramMenuItemImpl.getActionView());
      adjustAppearance();
      return;
    }
  }
  
  protected int[] onCreateDrawableState(int paramInt)
  {
    int[] arrayOfInt = super.onCreateDrawableState(paramInt + 1);
    if ((this.mItemData != null) && (this.mItemData.isCheckable()) && (this.mItemData.isChecked())) {
      mergeDrawableStates(arrayOfInt, CHECKED_STATE_SET);
    }
    return arrayOfInt;
  }
  
  public boolean prefersCondensedTitle()
  {
    return false;
  }
  
  public void recycle()
  {
    if (this.mActionArea != null) {
      this.mActionArea.removeAllViews();
    }
    this.mTextView.setCompoundDrawables(null, null, null, null);
  }
  
  public void setCheckable(boolean paramBoolean)
  {
    refreshDrawableState();
    if (this.mCheckable != paramBoolean)
    {
      this.mCheckable = paramBoolean;
      this.mAccessibilityDelegate.sendAccessibilityEvent(this.mTextView, 2048);
    }
  }
  
  public void setChecked(boolean paramBoolean)
  {
    refreshDrawableState();
    this.mTextView.setChecked(paramBoolean);
  }
  
  public void setIcon(Drawable paramDrawable)
  {
    Drawable.ConstantState localConstantState;
    if (paramDrawable != null) {
      if (this.mHasIconTintList)
      {
        localConstantState = paramDrawable.getConstantState();
        if (localConstantState == null)
        {
          paramDrawable = DrawableCompat.wrap(paramDrawable).mutate();
          DrawableCompat.setTintList(paramDrawable, this.mIconTintList);
        }
      }
      else
      {
        paramDrawable.setBounds(0, 0, this.mIconSize, this.mIconSize);
      }
    }
    for (;;)
    {
      TextViewCompat.setCompoundDrawablesRelative(this.mTextView, paramDrawable, null, null, null);
      return;
      paramDrawable = localConstantState.newDrawable();
      break;
      if (this.mNeedsEmptyIcon)
      {
        if (this.mEmptyDrawable == null)
        {
          this.mEmptyDrawable = ResourcesCompat.getDrawable(getResources(), R.drawable.navigation_empty_icon, getContext().getTheme());
          if (this.mEmptyDrawable != null) {
            this.mEmptyDrawable.setBounds(0, 0, this.mIconSize, this.mIconSize);
          }
        }
        paramDrawable = this.mEmptyDrawable;
      }
    }
  }
  
  void setIconTintList(ColorStateList paramColorStateList)
  {
    this.mIconTintList = paramColorStateList;
    if (this.mIconTintList != null) {}
    for (boolean bool = true;; bool = false)
    {
      this.mHasIconTintList = bool;
      if (this.mItemData != null) {
        setIcon(this.mItemData.getIcon());
      }
      return;
    }
  }
  
  public void setNeedsEmptyIcon(boolean paramBoolean)
  {
    this.mNeedsEmptyIcon = paramBoolean;
  }
  
  public void setShortcut(boolean paramBoolean, char paramChar) {}
  
  public void setTextAppearance(int paramInt)
  {
    TextViewCompat.setTextAppearance(this.mTextView, paramInt);
  }
  
  public void setTextColor(ColorStateList paramColorStateList)
  {
    this.mTextView.setTextColor(paramColorStateList);
  }
  
  public void setTitle(CharSequence paramCharSequence)
  {
    this.mTextView.setText(paramCharSequence);
  }
  
  public boolean showsIcon()
  {
    return true;
  }
}
