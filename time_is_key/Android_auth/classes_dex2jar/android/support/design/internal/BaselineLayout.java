package android.support.design.internal;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.ViewUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class BaselineLayout
  extends ViewGroup
{
  private int mBaseline = -1;
  
  public BaselineLayout(Context paramContext)
  {
    super(paramContext, null, 0);
  }
  
  public BaselineLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet, 0);
  }
  
  public BaselineLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }
  
  public int getBaseline()
  {
    return this.mBaseline;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = getChildCount();
    int j = getPaddingLeft();
    int k = paramInt3 - paramInt1 - getPaddingRight() - j;
    int m = getPaddingTop();
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
        int i1 = localView.getMeasuredWidth();
        int i2 = localView.getMeasuredHeight();
        int i3 = j + (k - i1) / 2;
        if ((this.mBaseline != -1) && (localView.getBaseline() != -1)) {}
        for (int i4 = m + this.mBaseline - localView.getBaseline();; i4 = m)
        {
          localView.layout(i3, i4, i3 + i1, i4 + i2);
          break;
        }
      }
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = getChildCount();
    int j = 0;
    int k = 0;
    int m = -1;
    int n = -1;
    int i1 = 0;
    int i2 = 0;
    if (i2 < i)
    {
      View localView = getChildAt(i2);
      if (localView.getVisibility() == 8) {}
      for (;;)
      {
        i2++;
        break;
        measureChild(localView, paramInt1, paramInt2);
        int i4 = localView.getBaseline();
        if (i4 != -1)
        {
          m = Math.max(m, i4);
          n = Math.max(n, localView.getMeasuredHeight() - i4);
        }
        j = Math.max(j, localView.getMeasuredWidth());
        k = Math.max(k, localView.getMeasuredHeight());
        i1 = ViewUtils.combineMeasuredStates(i1, ViewCompat.getMeasuredState(localView));
      }
    }
    if (m != -1)
    {
      k = Math.max(k, m + Math.max(n, getPaddingBottom()));
      this.mBaseline = m;
    }
    int i3 = Math.max(k, getSuggestedMinimumHeight());
    setMeasuredDimension(ViewCompat.resolveSizeAndState(Math.max(j, getSuggestedMinimumWidth()), paramInt1, i1), ViewCompat.resolveSizeAndState(i3, paramInt2, i1 << 16));
  }
}
