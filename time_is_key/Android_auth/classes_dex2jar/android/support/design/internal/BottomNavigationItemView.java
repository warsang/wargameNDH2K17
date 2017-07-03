package android.support.design.internal;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.support.annotation.NonNull;
import android.support.annotation.RestrictTo;
import android.support.design.R.dimen;
import android.support.design.R.drawable;
import android.support.design.R.id;
import android.support.design.R.layout;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.PointerIconCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.view.menu.MenuItemImpl;
import android.support.v7.view.menu.MenuView.ItemView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public class BottomNavigationItemView
  extends FrameLayout
  implements MenuView.ItemView
{
  private static final int[] CHECKED_STATE_SET = { 16842912 };
  public static final int INVALID_ITEM_POSITION = -1;
  private final int mDefaultMargin;
  private ImageView mIcon;
  private ColorStateList mIconTint;
  private MenuItemImpl mItemData;
  private int mItemPosition = -1;
  private final TextView mLargeLabel;
  private final float mScaleDownFactor;
  private final float mScaleUpFactor;
  private final int mShiftAmount;
  private boolean mShiftingMode;
  private final TextView mSmallLabel;
  
  public BottomNavigationItemView(@NonNull Context paramContext)
  {
    this(paramContext, null);
  }
  
  public BottomNavigationItemView(@NonNull Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public BottomNavigationItemView(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    Resources localResources = getResources();
    int i = localResources.getDimensionPixelSize(R.dimen.design_bottom_navigation_text_size);
    int j = localResources.getDimensionPixelSize(R.dimen.design_bottom_navigation_active_text_size);
    this.mDefaultMargin = localResources.getDimensionPixelSize(R.dimen.design_bottom_navigation_margin);
    this.mShiftAmount = (i - j);
    this.mScaleUpFactor = (1.0F * j / i);
    this.mScaleDownFactor = (1.0F * i / j);
    LayoutInflater.from(paramContext).inflate(R.layout.design_bottom_navigation_item, this, true);
    setBackgroundResource(R.drawable.design_bottom_navigation_item_background);
    this.mIcon = ((ImageView)findViewById(R.id.icon));
    this.mSmallLabel = ((TextView)findViewById(R.id.smallLabel));
    this.mLargeLabel = ((TextView)findViewById(R.id.largeLabel));
  }
  
  public MenuItemImpl getItemData()
  {
    return this.mItemData;
  }
  
  public int getItemPosition()
  {
    return this.mItemPosition;
  }
  
  public void initialize(MenuItemImpl paramMenuItemImpl, int paramInt)
  {
    this.mItemData = paramMenuItemImpl;
    setCheckable(paramMenuItemImpl.isCheckable());
    setChecked(paramMenuItemImpl.isChecked());
    setEnabled(paramMenuItemImpl.isEnabled());
    setIcon(paramMenuItemImpl.getIcon());
    setTitle(paramMenuItemImpl.getTitle());
    setId(paramMenuItemImpl.getItemId());
  }
  
  public int[] onCreateDrawableState(int paramInt)
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
  
  public void setCheckable(boolean paramBoolean)
  {
    refreshDrawableState();
  }
  
  public void setChecked(boolean paramBoolean)
  {
    ViewCompat.setPivotX(this.mLargeLabel, this.mLargeLabel.getWidth() / 2);
    ViewCompat.setPivotY(this.mLargeLabel, this.mLargeLabel.getBaseline());
    ViewCompat.setPivotX(this.mSmallLabel, this.mSmallLabel.getWidth() / 2);
    ViewCompat.setPivotY(this.mSmallLabel, this.mSmallLabel.getBaseline());
    if (this.mShiftingMode) {
      if (paramBoolean)
      {
        FrameLayout.LayoutParams localLayoutParams4 = (FrameLayout.LayoutParams)this.mIcon.getLayoutParams();
        localLayoutParams4.gravity = 49;
        localLayoutParams4.topMargin = this.mDefaultMargin;
        this.mIcon.setLayoutParams(localLayoutParams4);
        this.mLargeLabel.setVisibility(0);
        ViewCompat.setScaleX(this.mLargeLabel, 1.0F);
        ViewCompat.setScaleY(this.mLargeLabel, 1.0F);
        this.mSmallLabel.setVisibility(4);
      }
    }
    for (;;)
    {
      refreshDrawableState();
      return;
      FrameLayout.LayoutParams localLayoutParams3 = (FrameLayout.LayoutParams)this.mIcon.getLayoutParams();
      localLayoutParams3.gravity = 17;
      localLayoutParams3.topMargin = this.mDefaultMargin;
      this.mIcon.setLayoutParams(localLayoutParams3);
      this.mLargeLabel.setVisibility(4);
      ViewCompat.setScaleX(this.mLargeLabel, 0.5F);
      ViewCompat.setScaleY(this.mLargeLabel, 0.5F);
      break;
      if (paramBoolean)
      {
        FrameLayout.LayoutParams localLayoutParams2 = (FrameLayout.LayoutParams)this.mIcon.getLayoutParams();
        localLayoutParams2.gravity = 49;
        localLayoutParams2.topMargin = (this.mDefaultMargin + this.mShiftAmount);
        this.mIcon.setLayoutParams(localLayoutParams2);
        this.mLargeLabel.setVisibility(0);
        this.mSmallLabel.setVisibility(4);
        ViewCompat.setScaleX(this.mLargeLabel, 1.0F);
        ViewCompat.setScaleY(this.mLargeLabel, 1.0F);
        ViewCompat.setScaleX(this.mSmallLabel, this.mScaleUpFactor);
        ViewCompat.setScaleY(this.mSmallLabel, this.mScaleUpFactor);
      }
      else
      {
        FrameLayout.LayoutParams localLayoutParams1 = (FrameLayout.LayoutParams)this.mIcon.getLayoutParams();
        localLayoutParams1.gravity = 49;
        localLayoutParams1.topMargin = this.mDefaultMargin;
        this.mIcon.setLayoutParams(localLayoutParams1);
        this.mLargeLabel.setVisibility(4);
        this.mSmallLabel.setVisibility(0);
        ViewCompat.setScaleX(this.mLargeLabel, this.mScaleDownFactor);
        ViewCompat.setScaleY(this.mLargeLabel, this.mScaleDownFactor);
        ViewCompat.setScaleX(this.mSmallLabel, 1.0F);
        ViewCompat.setScaleY(this.mSmallLabel, 1.0F);
      }
    }
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    super.setEnabled(paramBoolean);
    this.mSmallLabel.setEnabled(paramBoolean);
    this.mLargeLabel.setEnabled(paramBoolean);
    this.mIcon.setEnabled(paramBoolean);
    if (paramBoolean)
    {
      ViewCompat.setPointerIcon(this, PointerIconCompat.getSystemIcon(getContext(), 1002));
      return;
    }
    ViewCompat.setPointerIcon(this, null);
  }
  
  public void setIcon(Drawable paramDrawable)
  {
    Drawable.ConstantState localConstantState;
    if (paramDrawable != null)
    {
      localConstantState = paramDrawable.getConstantState();
      if (localConstantState != null) {
        break label38;
      }
    }
    for (;;)
    {
      paramDrawable = DrawableCompat.wrap(paramDrawable).mutate();
      DrawableCompat.setTintList(paramDrawable, this.mIconTint);
      this.mIcon.setImageDrawable(paramDrawable);
      return;
      label38:
      paramDrawable = localConstantState.newDrawable();
    }
  }
  
  public void setIconTintList(ColorStateList paramColorStateList)
  {
    this.mIconTint = paramColorStateList;
    if (this.mItemData != null) {
      setIcon(this.mItemData.getIcon());
    }
  }
  
  public void setItemBackground(int paramInt)
  {
    if (paramInt == 0) {}
    for (Drawable localDrawable = null;; localDrawable = ContextCompat.getDrawable(getContext(), paramInt))
    {
      ViewCompat.setBackground(this, localDrawable);
      return;
    }
  }
  
  public void setItemPosition(int paramInt)
  {
    this.mItemPosition = paramInt;
  }
  
  public void setShiftingMode(boolean paramBoolean)
  {
    this.mShiftingMode = paramBoolean;
  }
  
  public void setShortcut(boolean paramBoolean, char paramChar) {}
  
  public void setTextColor(ColorStateList paramColorStateList)
  {
    this.mSmallLabel.setTextColor(paramColorStateList);
    this.mLargeLabel.setTextColor(paramColorStateList);
  }
  
  public void setTitle(CharSequence paramCharSequence)
  {
    this.mSmallLabel.setText(paramCharSequence);
    this.mLargeLabel.setText(paramCharSequence);
    setContentDescription(paramCharSequence);
  }
  
  public boolean showsIcon()
  {
    return true;
  }
}
