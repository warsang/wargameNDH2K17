package android.support.v7.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.RestrictTo;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.graphics.drawable.DrawableWrapper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import java.lang.reflect.Field;

@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public class ListViewCompat
  extends ListView
{
  public static final int INVALID_POSITION = -1;
  public static final int NO_POSITION = -1;
  private static final int[] STATE_SET_NOTHING = { 0 };
  private Field mIsChildViewEnabled;
  protected int mMotionPosition;
  int mSelectionBottomPadding = 0;
  int mSelectionLeftPadding = 0;
  int mSelectionRightPadding = 0;
  int mSelectionTopPadding = 0;
  private GateKeeperDrawable mSelector;
  final Rect mSelectorRect = new Rect();
  
  public ListViewCompat(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public ListViewCompat(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public ListViewCompat(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    try
    {
      this.mIsChildViewEnabled = AbsListView.class.getDeclaredField("mIsChildViewEnabled");
      this.mIsChildViewEnabled.setAccessible(true);
      return;
    }
    catch (NoSuchFieldException localNoSuchFieldException)
    {
      localNoSuchFieldException.printStackTrace();
    }
  }
  
  protected void dispatchDraw(Canvas paramCanvas)
  {
    drawSelectorCompat(paramCanvas);
    super.dispatchDraw(paramCanvas);
  }
  
  protected void drawSelectorCompat(Canvas paramCanvas)
  {
    if (!this.mSelectorRect.isEmpty())
    {
      Drawable localDrawable = getSelector();
      if (localDrawable != null)
      {
        localDrawable.setBounds(this.mSelectorRect);
        localDrawable.draw(paramCanvas);
      }
    }
  }
  
  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    setSelectorEnabled(true);
    updateSelectorStateCompat();
  }
  
  public int lookForSelectablePosition(int paramInt, boolean paramBoolean)
  {
    ListAdapter localListAdapter = getAdapter();
    if ((localListAdapter == null) || (isInTouchMode())) {}
    int i;
    do
    {
      int j;
      do
      {
        return -1;
        i = localListAdapter.getCount();
        if (getAdapter().areAllItemsEnabled()) {
          break;
        }
        if (paramBoolean) {
          for (j = Math.max(0, paramInt); (j < i) && (!localListAdapter.isEnabled(j)); j++) {}
        }
        for (j = Math.min(paramInt, i - 1); (j >= 0) && (!localListAdapter.isEnabled(j)); j--) {}
      } while ((j < 0) || (j >= i));
      return j;
    } while ((paramInt < 0) || (paramInt >= i));
    return paramInt;
  }
  
  public int measureHeightOfChildrenCompat(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5)
  {
    int i = getListPaddingTop();
    int j = getListPaddingBottom();
    getListPaddingLeft();
    getListPaddingRight();
    int k = getDividerHeight();
    Drawable localDrawable = getDivider();
    ListAdapter localListAdapter = getAdapter();
    int i1;
    if (localListAdapter == null)
    {
      i1 = i + j;
      return i1;
    }
    int m = i + j;
    int n;
    label76:
    View localView;
    int i2;
    int i3;
    if ((k > 0) && (localDrawable != null))
    {
      n = k;
      i1 = 0;
      localView = null;
      i2 = 0;
      i3 = localListAdapter.getCount();
    }
    for (int i4 = 0;; i4++)
    {
      if (i4 >= i3) {
        break label295;
      }
      int i5 = localListAdapter.getItemViewType(i4);
      if (i5 != i2)
      {
        localView = null;
        i2 = i5;
      }
      localView = localListAdapter.getView(i4, localView, this);
      ViewGroup.LayoutParams localLayoutParams = localView.getLayoutParams();
      if (localLayoutParams == null)
      {
        localLayoutParams = generateDefaultLayoutParams();
        localView.setLayoutParams(localLayoutParams);
      }
      if (localLayoutParams.height > 0) {}
      for (int i6 = View.MeasureSpec.makeMeasureSpec(localLayoutParams.height, 1073741824);; i6 = View.MeasureSpec.makeMeasureSpec(0, 0))
      {
        localView.measure(paramInt1, i6);
        localView.forceLayout();
        if (i4 > 0) {
          m += n;
        }
        m += localView.getMeasuredHeight();
        if (m < paramInt4) {
          break label273;
        }
        if ((paramInt5 >= 0) && (i4 > paramInt5) && (i1 > 0) && (m != paramInt4)) {
          break;
        }
        return paramInt4;
        n = 0;
        break label76;
      }
      label273:
      if ((paramInt5 >= 0) && (i4 >= paramInt5)) {
        i1 = m;
      }
    }
    label295:
    return m;
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    switch (paramMotionEvent.getAction())
    {
    }
    for (;;)
    {
      return super.onTouchEvent(paramMotionEvent);
      this.mMotionPosition = pointToPosition((int)paramMotionEvent.getX(), (int)paramMotionEvent.getY());
    }
  }
  
  protected void positionSelectorCompat(int paramInt, View paramView)
  {
    Rect localRect = this.mSelectorRect;
    localRect.set(paramView.getLeft(), paramView.getTop(), paramView.getRight(), paramView.getBottom());
    localRect.left -= this.mSelectionLeftPadding;
    localRect.top -= this.mSelectionTopPadding;
    localRect.right += this.mSelectionRightPadding;
    localRect.bottom += this.mSelectionBottomPadding;
    try
    {
      boolean bool1 = this.mIsChildViewEnabled.getBoolean(this);
      Field localField;
      if (paramView.isEnabled() != bool1)
      {
        localField = this.mIsChildViewEnabled;
        if (bool1) {
          break label131;
        }
      }
      label131:
      for (boolean bool2 = true;; bool2 = false)
      {
        localField.set(this, Boolean.valueOf(bool2));
        if (paramInt != -1) {
          refreshDrawableState();
        }
        return;
      }
      return;
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      localIllegalAccessException.printStackTrace();
    }
  }
  
  protected void positionSelectorLikeFocusCompat(int paramInt, View paramView)
  {
    boolean bool1 = true;
    Drawable localDrawable = getSelector();
    boolean bool2;
    float f1;
    float f2;
    if ((localDrawable != null) && (paramInt != -1))
    {
      bool2 = bool1;
      if (bool2) {
        localDrawable.setVisible(false, false);
      }
      positionSelectorCompat(paramInt, paramView);
      if (bool2)
      {
        Rect localRect = this.mSelectorRect;
        f1 = localRect.exactCenterX();
        f2 = localRect.exactCenterY();
        if (getVisibility() != 0) {
          break label96;
        }
      }
    }
    for (;;)
    {
      localDrawable.setVisible(bool1, false);
      DrawableCompat.setHotspot(localDrawable, f1, f2);
      return;
      bool2 = false;
      break;
      label96:
      bool1 = false;
    }
  }
  
  protected void positionSelectorLikeTouchCompat(int paramInt, View paramView, float paramFloat1, float paramFloat2)
  {
    positionSelectorLikeFocusCompat(paramInt, paramView);
    Drawable localDrawable = getSelector();
    if ((localDrawable != null) && (paramInt != -1)) {
      DrawableCompat.setHotspot(localDrawable, paramFloat1, paramFloat2);
    }
  }
  
  public void setSelector(Drawable paramDrawable)
  {
    if (paramDrawable != null) {}
    for (GateKeeperDrawable localGateKeeperDrawable = new GateKeeperDrawable(paramDrawable);; localGateKeeperDrawable = null)
    {
      this.mSelector = localGateKeeperDrawable;
      super.setSelector(this.mSelector);
      Rect localRect = new Rect();
      if (paramDrawable != null) {
        paramDrawable.getPadding(localRect);
      }
      this.mSelectionLeftPadding = localRect.left;
      this.mSelectionTopPadding = localRect.top;
      this.mSelectionRightPadding = localRect.right;
      this.mSelectionBottomPadding = localRect.bottom;
      return;
    }
  }
  
  protected void setSelectorEnabled(boolean paramBoolean)
  {
    if (this.mSelector != null) {
      this.mSelector.setEnabled(paramBoolean);
    }
  }
  
  protected boolean shouldShowSelectorCompat()
  {
    return (touchModeDrawsInPressedStateCompat()) && (isPressed());
  }
  
  protected boolean touchModeDrawsInPressedStateCompat()
  {
    return false;
  }
  
  protected void updateSelectorStateCompat()
  {
    Drawable localDrawable = getSelector();
    if ((localDrawable != null) && (shouldShowSelectorCompat())) {
      localDrawable.setState(getDrawableState());
    }
  }
  
  private static class GateKeeperDrawable
    extends DrawableWrapper
  {
    private boolean mEnabled = true;
    
    public GateKeeperDrawable(Drawable paramDrawable)
    {
      super();
    }
    
    public void draw(Canvas paramCanvas)
    {
      if (this.mEnabled) {
        super.draw(paramCanvas);
      }
    }
    
    void setEnabled(boolean paramBoolean)
    {
      this.mEnabled = paramBoolean;
    }
    
    public void setHotspot(float paramFloat1, float paramFloat2)
    {
      if (this.mEnabled) {
        super.setHotspot(paramFloat1, paramFloat2);
      }
    }
    
    public void setHotspotBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      if (this.mEnabled) {
        super.setHotspotBounds(paramInt1, paramInt2, paramInt3, paramInt4);
      }
    }
    
    public boolean setState(int[] paramArrayOfInt)
    {
      if (this.mEnabled) {
        return super.setState(paramArrayOfInt);
      }
      return false;
    }
    
    public boolean setVisible(boolean paramBoolean1, boolean paramBoolean2)
    {
      if (this.mEnabled) {
        return super.setVisible(paramBoolean1, paramBoolean2);
      }
      return false;
    }
  }
}
