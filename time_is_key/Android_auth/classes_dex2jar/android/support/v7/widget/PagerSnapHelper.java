package android.support.v7.widget;

import android.content.Context;
import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;

public class PagerSnapHelper
  extends SnapHelper
{
  private static final int MAX_SCROLL_ON_FLING_DURATION = 100;
  @Nullable
  private OrientationHelper mHorizontalHelper;
  @Nullable
  private OrientationHelper mVerticalHelper;
  
  public PagerSnapHelper() {}
  
  private int distanceToCenter(@NonNull RecyclerView.LayoutManager paramLayoutManager, @NonNull View paramView, OrientationHelper paramOrientationHelper)
  {
    int i = paramOrientationHelper.getDecoratedStart(paramView) + paramOrientationHelper.getDecoratedMeasurement(paramView) / 2;
    if (paramLayoutManager.getClipToPadding()) {}
    for (int j = paramOrientationHelper.getStartAfterPadding() + paramOrientationHelper.getTotalSpace() / 2;; j = paramOrientationHelper.getEnd() / 2) {
      return i - j;
    }
  }
  
  @Nullable
  private View findCenterView(RecyclerView.LayoutManager paramLayoutManager, OrientationHelper paramOrientationHelper)
  {
    int i = paramLayoutManager.getChildCount();
    if (i == 0)
    {
      localObject = null;
      return localObject;
    }
    Object localObject = null;
    if (paramLayoutManager.getClipToPadding()) {}
    for (int j = paramOrientationHelper.getStartAfterPadding() + paramOrientationHelper.getTotalSpace() / 2;; j = paramOrientationHelper.getEnd() / 2)
    {
      int k = Integer.MAX_VALUE;
      for (int m = 0; m < i; m++)
      {
        View localView = paramLayoutManager.getChildAt(m);
        int n = Math.abs(paramOrientationHelper.getDecoratedStart(localView) + paramOrientationHelper.getDecoratedMeasurement(localView) / 2 - j);
        if (n < k)
        {
          k = n;
          localObject = localView;
        }
      }
      break;
    }
  }
  
  @Nullable
  private View findStartView(RecyclerView.LayoutManager paramLayoutManager, OrientationHelper paramOrientationHelper)
  {
    int i = paramLayoutManager.getChildCount();
    Object localObject;
    if (i == 0) {
      localObject = null;
    }
    for (;;)
    {
      return localObject;
      localObject = null;
      int j = Integer.MAX_VALUE;
      for (int k = 0; k < i; k++)
      {
        View localView = paramLayoutManager.getChildAt(k);
        int m = paramOrientationHelper.getDecoratedStart(localView);
        if (m < j)
        {
          j = m;
          localObject = localView;
        }
      }
    }
  }
  
  @NonNull
  private OrientationHelper getHorizontalHelper(@NonNull RecyclerView.LayoutManager paramLayoutManager)
  {
    if ((this.mHorizontalHelper == null) || (this.mHorizontalHelper.mLayoutManager != paramLayoutManager)) {
      this.mHorizontalHelper = OrientationHelper.createHorizontalHelper(paramLayoutManager);
    }
    return this.mHorizontalHelper;
  }
  
  @NonNull
  private OrientationHelper getVerticalHelper(@NonNull RecyclerView.LayoutManager paramLayoutManager)
  {
    if ((this.mVerticalHelper == null) || (this.mVerticalHelper.mLayoutManager != paramLayoutManager)) {
      this.mVerticalHelper = OrientationHelper.createVerticalHelper(paramLayoutManager);
    }
    return this.mVerticalHelper;
  }
  
  @Nullable
  public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager paramLayoutManager, @NonNull View paramView)
  {
    int[] arrayOfInt = new int[2];
    if (paramLayoutManager.canScrollHorizontally()) {
      arrayOfInt[0] = distanceToCenter(paramLayoutManager, paramView, getHorizontalHelper(paramLayoutManager));
    }
    while (paramLayoutManager.canScrollVertically())
    {
      arrayOfInt[1] = distanceToCenter(paramLayoutManager, paramView, getVerticalHelper(paramLayoutManager));
      return arrayOfInt;
      arrayOfInt[0] = 0;
    }
    arrayOfInt[1] = 0;
    return arrayOfInt;
  }
  
  protected LinearSmoothScroller createSnapScroller(RecyclerView.LayoutManager paramLayoutManager)
  {
    if (!(paramLayoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider)) {
      return null;
    }
    new LinearSmoothScroller(this.mRecyclerView.getContext())
    {
      protected float calculateSpeedPerPixel(DisplayMetrics paramAnonymousDisplayMetrics)
      {
        return 100.0F / paramAnonymousDisplayMetrics.densityDpi;
      }
      
      protected int calculateTimeForScrolling(int paramAnonymousInt)
      {
        return Math.min(100, super.calculateTimeForScrolling(paramAnonymousInt));
      }
      
      protected void onTargetFound(View paramAnonymousView, RecyclerView.State paramAnonymousState, RecyclerView.SmoothScroller.Action paramAnonymousAction)
      {
        int[] arrayOfInt = PagerSnapHelper.this.calculateDistanceToFinalSnap(PagerSnapHelper.this.mRecyclerView.getLayoutManager(), paramAnonymousView);
        int i = arrayOfInt[0];
        int j = arrayOfInt[1];
        int k = calculateTimeForDeceleration(Math.max(Math.abs(i), Math.abs(j)));
        if (k > 0) {
          paramAnonymousAction.update(i, j, k, this.mDecelerateInterpolator);
        }
      }
    };
  }
  
  @Nullable
  public View findSnapView(RecyclerView.LayoutManager paramLayoutManager)
  {
    if (paramLayoutManager.canScrollVertically()) {
      return findCenterView(paramLayoutManager, getVerticalHelper(paramLayoutManager));
    }
    if (paramLayoutManager.canScrollHorizontally()) {
      return findCenterView(paramLayoutManager, getHorizontalHelper(paramLayoutManager));
    }
    return null;
  }
  
  public int findTargetSnapPosition(RecyclerView.LayoutManager paramLayoutManager, int paramInt1, int paramInt2)
  {
    int i = paramLayoutManager.getItemCount();
    int j;
    if (i == 0) {
      j = -1;
    }
    int k;
    label102:
    label199:
    label205:
    do
    {
      return j;
      View localView;
      if (paramLayoutManager.canScrollVertically()) {
        localView = findStartView(paramLayoutManager, getVerticalHelper(paramLayoutManager));
      }
      while (localView == null)
      {
        return -1;
        boolean bool1 = paramLayoutManager.canScrollHorizontally();
        localView = null;
        if (bool1) {
          localView = findStartView(paramLayoutManager, getHorizontalHelper(paramLayoutManager));
        }
      }
      j = paramLayoutManager.getPosition(localView);
      if (j == -1) {
        return -1;
      }
      if (paramLayoutManager.canScrollHorizontally()) {
        if (paramInt1 > 0)
        {
          k = 1;
          boolean bool2 = paramLayoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider;
          m = 0;
          if (bool2)
          {
            PointF localPointF = ((RecyclerView.SmoothScroller.ScrollVectorProvider)paramLayoutManager).computeScrollVectorForPosition(i - 1);
            m = 0;
            if (localPointF != null) {
              if ((localPointF.x >= 0.0F) && (localPointF.y >= 0.0F)) {
                break label199;
              }
            }
          }
        }
      }
      for (int m = 1;; m = 0)
      {
        if (m == 0) {
          break label205;
        }
        if (k == 0) {
          break;
        }
        return j - 1;
        k = 0;
        break label102;
        if (paramInt2 > 0) {}
        for (k = 1;; k = 0) {
          break;
        }
      }
    } while (k == 0);
    return j + 1;
  }
}
