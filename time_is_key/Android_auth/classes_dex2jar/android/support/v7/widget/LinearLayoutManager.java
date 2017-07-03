package android.support.v7.widget;

import android.content.Context;
import android.graphics.PointF;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.RestrictTo;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.support.v4.view.accessibility.AccessibilityRecordCompat;
import android.support.v7.widget.helper.ItemTouchHelper.ViewDropHandler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import java.util.List;

public class LinearLayoutManager
  extends RecyclerView.LayoutManager
  implements ItemTouchHelper.ViewDropHandler, RecyclerView.SmoothScroller.ScrollVectorProvider
{
  static final boolean DEBUG = false;
  public static final int HORIZONTAL = 0;
  public static final int INVALID_OFFSET = Integer.MIN_VALUE;
  private static final float MAX_SCROLL_FACTOR = 0.33333334F;
  private static final String TAG = "LinearLayoutManager";
  public static final int VERTICAL = 1;
  final AnchorInfo mAnchorInfo = new AnchorInfo();
  private int mInitialPrefetchItemCount = 2;
  private boolean mLastStackFromEnd;
  private final LayoutChunkResult mLayoutChunkResult = new LayoutChunkResult();
  private LayoutState mLayoutState;
  int mOrientation;
  OrientationHelper mOrientationHelper;
  SavedState mPendingSavedState = null;
  int mPendingScrollPosition = -1;
  int mPendingScrollPositionOffset = Integer.MIN_VALUE;
  private boolean mRecycleChildrenOnDetach;
  private boolean mReverseLayout = false;
  boolean mShouldReverseLayout = false;
  private boolean mSmoothScrollbarEnabled = true;
  private boolean mStackFromEnd = false;
  
  public LinearLayoutManager(Context paramContext)
  {
    this(paramContext, 1, false);
  }
  
  public LinearLayoutManager(Context paramContext, int paramInt, boolean paramBoolean)
  {
    setOrientation(paramInt);
    setReverseLayout(paramBoolean);
    setAutoMeasureEnabled(true);
  }
  
  public LinearLayoutManager(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    RecyclerView.LayoutManager.Properties localProperties = getProperties(paramContext, paramAttributeSet, paramInt1, paramInt2);
    setOrientation(localProperties.orientation);
    setReverseLayout(localProperties.reverseLayout);
    setStackFromEnd(localProperties.stackFromEnd);
    setAutoMeasureEnabled(true);
  }
  
  private int computeScrollExtent(RecyclerView.State paramState)
  {
    if (getChildCount() == 0) {
      return 0;
    }
    ensureLayoutState();
    OrientationHelper localOrientationHelper = this.mOrientationHelper;
    if (!this.mSmoothScrollbarEnabled) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      View localView = findFirstVisibleChildClosestToStart(bool1, true);
      boolean bool2 = this.mSmoothScrollbarEnabled;
      boolean bool3 = false;
      if (!bool2) {
        bool3 = true;
      }
      return ScrollbarHelper.computeScrollExtent(paramState, localOrientationHelper, localView, findFirstVisibleChildClosestToEnd(bool3, true), this, this.mSmoothScrollbarEnabled);
    }
  }
  
  private int computeScrollOffset(RecyclerView.State paramState)
  {
    if (getChildCount() == 0) {
      return 0;
    }
    ensureLayoutState();
    OrientationHelper localOrientationHelper = this.mOrientationHelper;
    if (!this.mSmoothScrollbarEnabled) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      View localView = findFirstVisibleChildClosestToStart(bool1, true);
      boolean bool2 = this.mSmoothScrollbarEnabled;
      boolean bool3 = false;
      if (!bool2) {
        bool3 = true;
      }
      return ScrollbarHelper.computeScrollOffset(paramState, localOrientationHelper, localView, findFirstVisibleChildClosestToEnd(bool3, true), this, this.mSmoothScrollbarEnabled, this.mShouldReverseLayout);
    }
  }
  
  private int computeScrollRange(RecyclerView.State paramState)
  {
    if (getChildCount() == 0) {
      return 0;
    }
    ensureLayoutState();
    OrientationHelper localOrientationHelper = this.mOrientationHelper;
    if (!this.mSmoothScrollbarEnabled) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      View localView = findFirstVisibleChildClosestToStart(bool1, true);
      boolean bool2 = this.mSmoothScrollbarEnabled;
      boolean bool3 = false;
      if (!bool2) {
        bool3 = true;
      }
      return ScrollbarHelper.computeScrollRange(paramState, localOrientationHelper, localView, findFirstVisibleChildClosestToEnd(bool3, true), this, this.mSmoothScrollbarEnabled);
    }
  }
  
  private View findFirstPartiallyOrCompletelyInvisibleChild(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    return findOnePartiallyOrCompletelyInvisibleChild(0, getChildCount());
  }
  
  private View findFirstReferenceChild(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    return findReferenceChild(paramRecycler, paramState, 0, getChildCount(), paramState.getItemCount());
  }
  
  private View findFirstVisibleChildClosestToEnd(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.mShouldReverseLayout) {
      return findOneVisibleChild(0, getChildCount(), paramBoolean1, paramBoolean2);
    }
    return findOneVisibleChild(-1 + getChildCount(), -1, paramBoolean1, paramBoolean2);
  }
  
  private View findFirstVisibleChildClosestToStart(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.mShouldReverseLayout) {
      return findOneVisibleChild(-1 + getChildCount(), -1, paramBoolean1, paramBoolean2);
    }
    return findOneVisibleChild(0, getChildCount(), paramBoolean1, paramBoolean2);
  }
  
  private View findLastPartiallyOrCompletelyInvisibleChild(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    return findOnePartiallyOrCompletelyInvisibleChild(-1 + getChildCount(), -1);
  }
  
  private View findLastReferenceChild(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    return findReferenceChild(paramRecycler, paramState, -1 + getChildCount(), -1, paramState.getItemCount());
  }
  
  private View findPartiallyOrCompletelyInvisibleChildClosestToEnd(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    if (this.mShouldReverseLayout) {
      return findFirstPartiallyOrCompletelyInvisibleChild(paramRecycler, paramState);
    }
    return findLastPartiallyOrCompletelyInvisibleChild(paramRecycler, paramState);
  }
  
  private View findPartiallyOrCompletelyInvisibleChildClosestToStart(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    if (this.mShouldReverseLayout) {
      return findLastPartiallyOrCompletelyInvisibleChild(paramRecycler, paramState);
    }
    return findFirstPartiallyOrCompletelyInvisibleChild(paramRecycler, paramState);
  }
  
  private View findReferenceChildClosestToEnd(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    if (this.mShouldReverseLayout) {
      return findFirstReferenceChild(paramRecycler, paramState);
    }
    return findLastReferenceChild(paramRecycler, paramState);
  }
  
  private View findReferenceChildClosestToStart(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    if (this.mShouldReverseLayout) {
      return findLastReferenceChild(paramRecycler, paramState);
    }
    return findFirstReferenceChild(paramRecycler, paramState);
  }
  
  private int fixLayoutEndGap(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, boolean paramBoolean)
  {
    int i = this.mOrientationHelper.getEndAfterPadding() - paramInt;
    int j;
    if (i > 0)
    {
      j = -scrollBy(-i, paramRecycler, paramState);
      int k = paramInt + j;
      if (paramBoolean)
      {
        int m = this.mOrientationHelper.getEndAfterPadding() - k;
        if (m > 0)
        {
          this.mOrientationHelper.offsetChildren(m);
          return m + j;
        }
      }
    }
    else
    {
      return 0;
    }
    return j;
  }
  
  private int fixLayoutStartGap(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, boolean paramBoolean)
  {
    int i = paramInt - this.mOrientationHelper.getStartAfterPadding();
    int j;
    if (i > 0)
    {
      j = -scrollBy(i, paramRecycler, paramState);
      int k = paramInt + j;
      if (paramBoolean)
      {
        int m = k - this.mOrientationHelper.getStartAfterPadding();
        if (m > 0)
        {
          this.mOrientationHelper.offsetChildren(-m);
          return j - m;
        }
      }
    }
    else
    {
      return 0;
    }
    return j;
  }
  
  private View getChildClosestToEnd()
  {
    if (this.mShouldReverseLayout) {}
    for (int i = 0;; i = -1 + getChildCount()) {
      return getChildAt(i);
    }
  }
  
  private View getChildClosestToStart()
  {
    if (this.mShouldReverseLayout) {}
    for (int i = -1 + getChildCount();; i = 0) {
      return getChildAt(i);
    }
  }
  
  private void layoutForPredictiveAnimations(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, int paramInt1, int paramInt2)
  {
    if ((!paramState.willRunPredictiveAnimations()) || (getChildCount() == 0) || (paramState.isPreLayout()) || (!supportsPredictiveItemAnimations())) {
      return;
    }
    int i = 0;
    int j = 0;
    List localList = paramRecycler.getScrapList();
    int k = localList.size();
    int m = getPosition(getChildAt(0));
    int n = 0;
    if (n < k)
    {
      RecyclerView.ViewHolder localViewHolder = (RecyclerView.ViewHolder)localList.get(n);
      if (localViewHolder.isRemoved()) {}
      for (;;)
      {
        n++;
        break;
        int i1;
        if (localViewHolder.getLayoutPosition() < m)
        {
          i1 = 1;
          label112:
          if (i1 == this.mShouldReverseLayout) {
            break label156;
          }
        }
        label156:
        for (int i2 = -1;; i2 = 1)
        {
          if (i2 != -1) {
            break label162;
          }
          i += this.mOrientationHelper.getDecoratedMeasurement(localViewHolder.itemView);
          break;
          i1 = 0;
          break label112;
        }
        label162:
        j += this.mOrientationHelper.getDecoratedMeasurement(localViewHolder.itemView);
      }
    }
    this.mLayoutState.mScrapList = localList;
    if (i > 0)
    {
      updateLayoutStateToFillStart(getPosition(getChildClosestToStart()), paramInt1);
      this.mLayoutState.mExtra = i;
      this.mLayoutState.mAvailable = 0;
      this.mLayoutState.assignPositionFromScrapList();
      fill(paramRecycler, this.mLayoutState, paramState, false);
    }
    if (j > 0)
    {
      updateLayoutStateToFillEnd(getPosition(getChildClosestToEnd()), paramInt2);
      this.mLayoutState.mExtra = j;
      this.mLayoutState.mAvailable = 0;
      this.mLayoutState.assignPositionFromScrapList();
      fill(paramRecycler, this.mLayoutState, paramState, false);
    }
    this.mLayoutState.mScrapList = null;
  }
  
  private void logChildren()
  {
    Log.d("LinearLayoutManager", "internal representation of views on the screen");
    for (int i = 0; i < getChildCount(); i++)
    {
      View localView = getChildAt(i);
      Log.d("LinearLayoutManager", "item " + getPosition(localView) + ", coord:" + this.mOrientationHelper.getDecoratedStart(localView));
    }
    Log.d("LinearLayoutManager", "==============");
  }
  
  private void recycleByLayoutState(RecyclerView.Recycler paramRecycler, LayoutState paramLayoutState)
  {
    if ((!paramLayoutState.mRecycle) || (paramLayoutState.mInfinite)) {
      return;
    }
    if (paramLayoutState.mLayoutDirection == -1)
    {
      recycleViewsFromEnd(paramRecycler, paramLayoutState.mScrollingOffset);
      return;
    }
    recycleViewsFromStart(paramRecycler, paramLayoutState.mScrollingOffset);
  }
  
  private void recycleChildren(RecyclerView.Recycler paramRecycler, int paramInt1, int paramInt2)
  {
    if (paramInt1 == paramInt2) {}
    for (;;)
    {
      return;
      if (paramInt2 > paramInt1) {
        for (int j = paramInt2 - 1; j >= paramInt1; j--) {
          removeAndRecycleViewAt(j, paramRecycler);
        }
      } else {
        for (int i = paramInt1; i > paramInt2; i--) {
          removeAndRecycleViewAt(i, paramRecycler);
        }
      }
    }
  }
  
  private void recycleViewsFromEnd(RecyclerView.Recycler paramRecycler, int paramInt)
  {
    int i = getChildCount();
    if (paramInt < 0) {}
    for (;;)
    {
      return;
      int j = this.mOrientationHelper.getEnd() - paramInt;
      if (this.mShouldReverseLayout) {
        for (int m = 0; m < i; m++)
        {
          View localView2 = getChildAt(m);
          if ((this.mOrientationHelper.getDecoratedStart(localView2) < j) || (this.mOrientationHelper.getTransformedStartWithDecoration(localView2) < j))
          {
            recycleChildren(paramRecycler, 0, m);
            return;
          }
        }
      } else {
        for (int k = i - 1; k >= 0; k--)
        {
          View localView1 = getChildAt(k);
          if ((this.mOrientationHelper.getDecoratedStart(localView1) < j) || (this.mOrientationHelper.getTransformedStartWithDecoration(localView1) < j))
          {
            recycleChildren(paramRecycler, i - 1, k);
            return;
          }
        }
      }
    }
  }
  
  private void recycleViewsFromStart(RecyclerView.Recycler paramRecycler, int paramInt)
  {
    if (paramInt < 0) {}
    for (;;)
    {
      return;
      int i = getChildCount();
      if (this.mShouldReverseLayout) {
        for (int k = i - 1; k >= 0; k--)
        {
          View localView2 = getChildAt(k);
          if ((this.mOrientationHelper.getDecoratedEnd(localView2) > paramInt) || (this.mOrientationHelper.getTransformedEndWithDecoration(localView2) > paramInt))
          {
            recycleChildren(paramRecycler, i - 1, k);
            return;
          }
        }
      } else {
        for (int j = 0; j < i; j++)
        {
          View localView1 = getChildAt(j);
          if ((this.mOrientationHelper.getDecoratedEnd(localView1) > paramInt) || (this.mOrientationHelper.getTransformedEndWithDecoration(localView1) > paramInt))
          {
            recycleChildren(paramRecycler, 0, j);
            return;
          }
        }
      }
    }
  }
  
  private void resolveShouldLayoutReverse()
  {
    int i = 1;
    if ((this.mOrientation == i) || (!isLayoutRTL()))
    {
      this.mShouldReverseLayout = this.mReverseLayout;
      return;
    }
    if (!this.mReverseLayout) {}
    for (;;)
    {
      this.mShouldReverseLayout = i;
      return;
      i = 0;
    }
  }
  
  private boolean updateAnchorFromChildren(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, AnchorInfo paramAnchorInfo)
  {
    if (getChildCount() == 0) {}
    do
    {
      return false;
      View localView1 = getFocusedChild();
      if ((localView1 != null) && (paramAnchorInfo.isViewValidAsAnchor(localView1, paramState)))
      {
        paramAnchorInfo.assignFromViewAndKeepVisibleRect(localView1);
        return true;
      }
    } while (this.mLastStackFromEnd != this.mStackFromEnd);
    View localView2;
    label64:
    int i;
    if (paramAnchorInfo.mLayoutFromEnd)
    {
      localView2 = findReferenceChildClosestToEnd(paramRecycler, paramState);
      if (localView2 == null) {
        break label168;
      }
      paramAnchorInfo.assignFromView(localView2);
      if ((!paramState.isPreLayout()) && (supportsPredictiveItemAnimations()))
      {
        if ((this.mOrientationHelper.getDecoratedStart(localView2) < this.mOrientationHelper.getEndAfterPadding()) && (this.mOrientationHelper.getDecoratedEnd(localView2) >= this.mOrientationHelper.getStartAfterPadding())) {
          break label170;
        }
        i = 1;
        label130:
        if (i != 0) {
          if (!paramAnchorInfo.mLayoutFromEnd) {
            break label176;
          }
        }
      }
    }
    label168:
    label170:
    label176:
    for (int j = this.mOrientationHelper.getEndAfterPadding();; j = this.mOrientationHelper.getStartAfterPadding())
    {
      paramAnchorInfo.mCoordinate = j;
      return true;
      localView2 = findReferenceChildClosestToStart(paramRecycler, paramState);
      break label64;
      break;
      i = 0;
      break label130;
    }
  }
  
  private boolean updateAnchorFromPendingData(RecyclerView.State paramState, AnchorInfo paramAnchorInfo)
  {
    if ((paramState.isPreLayout()) || (this.mPendingScrollPosition == -1)) {
      return false;
    }
    if ((this.mPendingScrollPosition < 0) || (this.mPendingScrollPosition >= paramState.getItemCount()))
    {
      this.mPendingScrollPosition = -1;
      this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
      return false;
    }
    paramAnchorInfo.mPosition = this.mPendingScrollPosition;
    if ((this.mPendingSavedState != null) && (this.mPendingSavedState.hasValidAnchor()))
    {
      paramAnchorInfo.mLayoutFromEnd = this.mPendingSavedState.mAnchorLayoutFromEnd;
      if (paramAnchorInfo.mLayoutFromEnd)
      {
        paramAnchorInfo.mCoordinate = (this.mOrientationHelper.getEndAfterPadding() - this.mPendingSavedState.mAnchorOffset);
        return true;
      }
      paramAnchorInfo.mCoordinate = (this.mOrientationHelper.getStartAfterPadding() + this.mPendingSavedState.mAnchorOffset);
      return true;
    }
    if (this.mPendingScrollPositionOffset == Integer.MIN_VALUE)
    {
      View localView = findViewByPosition(this.mPendingScrollPosition);
      if (localView != null)
      {
        if (this.mOrientationHelper.getDecoratedMeasurement(localView) > this.mOrientationHelper.getTotalSpace())
        {
          paramAnchorInfo.assignCoordinateFromPadding();
          return true;
        }
        if (this.mOrientationHelper.getDecoratedStart(localView) - this.mOrientationHelper.getStartAfterPadding() < 0)
        {
          paramAnchorInfo.mCoordinate = this.mOrientationHelper.getStartAfterPadding();
          paramAnchorInfo.mLayoutFromEnd = false;
          return true;
        }
        if (this.mOrientationHelper.getEndAfterPadding() - this.mOrientationHelper.getDecoratedEnd(localView) < 0)
        {
          paramAnchorInfo.mCoordinate = this.mOrientationHelper.getEndAfterPadding();
          paramAnchorInfo.mLayoutFromEnd = true;
          return true;
        }
        if (paramAnchorInfo.mLayoutFromEnd) {}
        for (int j = this.mOrientationHelper.getDecoratedEnd(localView) + this.mOrientationHelper.getTotalSpaceChange();; j = this.mOrientationHelper.getDecoratedStart(localView))
        {
          paramAnchorInfo.mCoordinate = j;
          return true;
        }
      }
      if (getChildCount() > 0)
      {
        int i = getPosition(getChildAt(0));
        if (this.mPendingScrollPosition >= i) {
          break label360;
        }
      }
      label360:
      for (boolean bool1 = true;; bool1 = false)
      {
        boolean bool2 = this.mShouldReverseLayout;
        boolean bool3 = false;
        if (bool1 == bool2) {
          bool3 = true;
        }
        paramAnchorInfo.mLayoutFromEnd = bool3;
        paramAnchorInfo.assignCoordinateFromPadding();
        return true;
      }
    }
    paramAnchorInfo.mLayoutFromEnd = this.mShouldReverseLayout;
    if (this.mShouldReverseLayout)
    {
      paramAnchorInfo.mCoordinate = (this.mOrientationHelper.getEndAfterPadding() - this.mPendingScrollPositionOffset);
      return true;
    }
    paramAnchorInfo.mCoordinate = (this.mOrientationHelper.getStartAfterPadding() + this.mPendingScrollPositionOffset);
    return true;
  }
  
  private void updateAnchorInfoForLayout(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, AnchorInfo paramAnchorInfo)
  {
    if (updateAnchorFromPendingData(paramState, paramAnchorInfo)) {}
    while (updateAnchorFromChildren(paramRecycler, paramState, paramAnchorInfo)) {
      return;
    }
    paramAnchorInfo.assignCoordinateFromPadding();
    if (this.mStackFromEnd) {}
    for (int i = -1 + paramState.getItemCount();; i = 0)
    {
      paramAnchorInfo.mPosition = i;
      return;
    }
  }
  
  private void updateLayoutState(int paramInt1, int paramInt2, boolean paramBoolean, RecyclerView.State paramState)
  {
    int i = -1;
    int j = 1;
    this.mLayoutState.mInfinite = resolveIsInfinite();
    this.mLayoutState.mExtra = getExtraLayoutSpace(paramState);
    this.mLayoutState.mLayoutDirection = paramInt1;
    int k;
    if (paramInt1 == j)
    {
      LayoutState localLayoutState4 = this.mLayoutState;
      localLayoutState4.mExtra += this.mOrientationHelper.getEndPadding();
      View localView2 = getChildClosestToEnd();
      LayoutState localLayoutState5 = this.mLayoutState;
      if (this.mShouldReverseLayout) {}
      for (;;)
      {
        localLayoutState5.mItemDirection = i;
        this.mLayoutState.mCurrentPosition = (getPosition(localView2) + this.mLayoutState.mItemDirection);
        this.mLayoutState.mOffset = this.mOrientationHelper.getDecoratedEnd(localView2);
        k = this.mOrientationHelper.getDecoratedEnd(localView2) - this.mOrientationHelper.getEndAfterPadding();
        this.mLayoutState.mAvailable = paramInt2;
        if (paramBoolean)
        {
          LayoutState localLayoutState3 = this.mLayoutState;
          localLayoutState3.mAvailable -= k;
        }
        this.mLayoutState.mScrollingOffset = k;
        return;
        i = j;
      }
    }
    View localView1 = getChildClosestToStart();
    LayoutState localLayoutState1 = this.mLayoutState;
    localLayoutState1.mExtra += this.mOrientationHelper.getStartAfterPadding();
    LayoutState localLayoutState2 = this.mLayoutState;
    if (this.mShouldReverseLayout) {}
    for (;;)
    {
      localLayoutState2.mItemDirection = j;
      this.mLayoutState.mCurrentPosition = (getPosition(localView1) + this.mLayoutState.mItemDirection);
      this.mLayoutState.mOffset = this.mOrientationHelper.getDecoratedStart(localView1);
      k = -this.mOrientationHelper.getDecoratedStart(localView1) + this.mOrientationHelper.getStartAfterPadding();
      break;
      j = i;
    }
  }
  
  private void updateLayoutStateToFillEnd(int paramInt1, int paramInt2)
  {
    this.mLayoutState.mAvailable = (this.mOrientationHelper.getEndAfterPadding() - paramInt2);
    LayoutState localLayoutState = this.mLayoutState;
    if (this.mShouldReverseLayout) {}
    for (int i = -1;; i = 1)
    {
      localLayoutState.mItemDirection = i;
      this.mLayoutState.mCurrentPosition = paramInt1;
      this.mLayoutState.mLayoutDirection = 1;
      this.mLayoutState.mOffset = paramInt2;
      this.mLayoutState.mScrollingOffset = Integer.MIN_VALUE;
      return;
    }
  }
  
  private void updateLayoutStateToFillEnd(AnchorInfo paramAnchorInfo)
  {
    updateLayoutStateToFillEnd(paramAnchorInfo.mPosition, paramAnchorInfo.mCoordinate);
  }
  
  private void updateLayoutStateToFillStart(int paramInt1, int paramInt2)
  {
    this.mLayoutState.mAvailable = (paramInt2 - this.mOrientationHelper.getStartAfterPadding());
    this.mLayoutState.mCurrentPosition = paramInt1;
    LayoutState localLayoutState = this.mLayoutState;
    if (this.mShouldReverseLayout) {}
    for (int i = 1;; i = -1)
    {
      localLayoutState.mItemDirection = i;
      this.mLayoutState.mLayoutDirection = -1;
      this.mLayoutState.mOffset = paramInt2;
      this.mLayoutState.mScrollingOffset = Integer.MIN_VALUE;
      return;
    }
  }
  
  private void updateLayoutStateToFillStart(AnchorInfo paramAnchorInfo)
  {
    updateLayoutStateToFillStart(paramAnchorInfo.mPosition, paramAnchorInfo.mCoordinate);
  }
  
  public void assertNotInLayoutOrScroll(String paramString)
  {
    if (this.mPendingSavedState == null) {
      super.assertNotInLayoutOrScroll(paramString);
    }
  }
  
  public boolean canScrollHorizontally()
  {
    return this.mOrientation == 0;
  }
  
  public boolean canScrollVertically()
  {
    return this.mOrientation == 1;
  }
  
  public void collectAdjacentPrefetchPositions(int paramInt1, int paramInt2, RecyclerView.State paramState, RecyclerView.LayoutManager.LayoutPrefetchRegistry paramLayoutPrefetchRegistry)
  {
    if (this.mOrientation == 0) {}
    for (int i = paramInt1; (getChildCount() == 0) || (i == 0); i = paramInt2) {
      return;
    }
    if (i > 0) {}
    for (int j = 1;; j = -1)
    {
      updateLayoutState(j, Math.abs(i), true, paramState);
      collectPrefetchPositionsForLayoutState(paramState, this.mLayoutState, paramLayoutPrefetchRegistry);
      return;
    }
  }
  
  public void collectInitialPrefetchPositions(int paramInt, RecyclerView.LayoutManager.LayoutPrefetchRegistry paramLayoutPrefetchRegistry)
  {
    int i = -1;
    boolean bool;
    int j;
    if ((this.mPendingSavedState != null) && (this.mPendingSavedState.hasValidAnchor()))
    {
      bool = this.mPendingSavedState.mAnchorLayoutFromEnd;
      j = this.mPendingSavedState.mAnchorPosition;
      if (!bool) {
        break label136;
      }
    }
    for (;;)
    {
      int k = j;
      for (int m = 0; (m < this.mInitialPrefetchItemCount) && (k >= 0) && (k < paramInt); m++)
      {
        paramLayoutPrefetchRegistry.addPosition(k, 0);
        k += i;
      }
      resolveShouldLayoutReverse();
      bool = this.mShouldReverseLayout;
      if (this.mPendingScrollPosition == i)
      {
        if (bool) {}
        for (j = paramInt - 1;; j = 0) {
          break;
        }
      }
      j = this.mPendingScrollPosition;
      break;
      label136:
      i = 1;
    }
  }
  
  void collectPrefetchPositionsForLayoutState(RecyclerView.State paramState, LayoutState paramLayoutState, RecyclerView.LayoutManager.LayoutPrefetchRegistry paramLayoutPrefetchRegistry)
  {
    int i = paramLayoutState.mCurrentPosition;
    if ((i >= 0) && (i < paramState.getItemCount())) {
      paramLayoutPrefetchRegistry.addPosition(i, Math.max(0, paramLayoutState.mScrollingOffset));
    }
  }
  
  public int computeHorizontalScrollExtent(RecyclerView.State paramState)
  {
    return computeScrollExtent(paramState);
  }
  
  public int computeHorizontalScrollOffset(RecyclerView.State paramState)
  {
    return computeScrollOffset(paramState);
  }
  
  public int computeHorizontalScrollRange(RecyclerView.State paramState)
  {
    return computeScrollRange(paramState);
  }
  
  public PointF computeScrollVectorForPosition(int paramInt)
  {
    if (getChildCount() == 0) {
      return null;
    }
    int i = getPosition(getChildAt(0));
    int j = 0;
    if (paramInt < i) {
      j = 1;
    }
    if (j != this.mShouldReverseLayout) {}
    for (int k = -1; this.mOrientation == 0; k = 1) {
      return new PointF(k, 0.0F);
    }
    return new PointF(0.0F, k);
  }
  
  public int computeVerticalScrollExtent(RecyclerView.State paramState)
  {
    return computeScrollExtent(paramState);
  }
  
  public int computeVerticalScrollOffset(RecyclerView.State paramState)
  {
    return computeScrollOffset(paramState);
  }
  
  public int computeVerticalScrollRange(RecyclerView.State paramState)
  {
    return computeScrollRange(paramState);
  }
  
  int convertFocusDirectionToLayoutDirection(int paramInt)
  {
    int i = -1;
    int j = Integer.MIN_VALUE;
    int k = 1;
    switch (paramInt)
    {
    default: 
      i = j;
    case 1: 
    case 2: 
    case 33: 
    case 130: 
    case 17: 
      do
      {
        do
        {
          do
          {
            do
            {
              return i;
            } while ((this.mOrientation == k) || (!isLayoutRTL()));
            return k;
            if (this.mOrientation == k) {
              return k;
            }
          } while (isLayoutRTL());
          return k;
        } while (this.mOrientation == k);
        return j;
        if (this.mOrientation == k) {
          j = k;
        }
        return j;
      } while (this.mOrientation == 0);
      return j;
    }
    if (this.mOrientation == 0) {}
    for (;;)
    {
      return k;
      k = j;
    }
  }
  
  LayoutState createLayoutState()
  {
    return new LayoutState();
  }
  
  void ensureLayoutState()
  {
    if (this.mLayoutState == null) {
      this.mLayoutState = createLayoutState();
    }
    if (this.mOrientationHelper == null) {
      this.mOrientationHelper = OrientationHelper.createOrientationHelper(this, this.mOrientation);
    }
  }
  
  int fill(RecyclerView.Recycler paramRecycler, LayoutState paramLayoutState, RecyclerView.State paramState, boolean paramBoolean)
  {
    int i = paramLayoutState.mAvailable;
    if (paramLayoutState.mScrollingOffset != Integer.MIN_VALUE)
    {
      if (paramLayoutState.mAvailable < 0) {
        paramLayoutState.mScrollingOffset += paramLayoutState.mAvailable;
      }
      recycleByLayoutState(paramRecycler, paramLayoutState);
    }
    int j = paramLayoutState.mAvailable + paramLayoutState.mExtra;
    LayoutChunkResult localLayoutChunkResult = this.mLayoutChunkResult;
    if (((paramLayoutState.mInfinite) || (j > 0)) && (paramLayoutState.hasMore(paramState)))
    {
      localLayoutChunkResult.resetInternal();
      layoutChunk(paramRecycler, paramState, paramLayoutState, localLayoutChunkResult);
      if (!localLayoutChunkResult.mFinished) {
        break label108;
      }
    }
    for (;;)
    {
      return i - paramLayoutState.mAvailable;
      label108:
      paramLayoutState.mOffset += localLayoutChunkResult.mConsumed * paramLayoutState.mLayoutDirection;
      if ((!localLayoutChunkResult.mIgnoreConsumed) || (this.mLayoutState.mScrapList != null) || (!paramState.isPreLayout()))
      {
        paramLayoutState.mAvailable -= localLayoutChunkResult.mConsumed;
        j -= localLayoutChunkResult.mConsumed;
      }
      if (paramLayoutState.mScrollingOffset != Integer.MIN_VALUE)
      {
        paramLayoutState.mScrollingOffset += localLayoutChunkResult.mConsumed;
        if (paramLayoutState.mAvailable < 0) {
          paramLayoutState.mScrollingOffset += paramLayoutState.mAvailable;
        }
        recycleByLayoutState(paramRecycler, paramLayoutState);
      }
      if ((!paramBoolean) || (!localLayoutChunkResult.mFocusable)) {
        break;
      }
    }
  }
  
  public int findFirstCompletelyVisibleItemPosition()
  {
    View localView = findOneVisibleChild(0, getChildCount(), true, false);
    if (localView == null) {
      return -1;
    }
    return getPosition(localView);
  }
  
  public int findFirstVisibleItemPosition()
  {
    View localView = findOneVisibleChild(0, getChildCount(), false, true);
    if (localView == null) {
      return -1;
    }
    return getPosition(localView);
  }
  
  public int findLastCompletelyVisibleItemPosition()
  {
    View localView = findOneVisibleChild(-1 + getChildCount(), -1, true, false);
    if (localView == null) {
      return -1;
    }
    return getPosition(localView);
  }
  
  public int findLastVisibleItemPosition()
  {
    View localView = findOneVisibleChild(-1 + getChildCount(), -1, false, true);
    if (localView == null) {
      return -1;
    }
    return getPosition(localView);
  }
  
  View findOnePartiallyOrCompletelyInvisibleChild(int paramInt1, int paramInt2)
  {
    ensureLayoutState();
    int i;
    if (paramInt2 > paramInt1) {
      i = 1;
    }
    while (i == 0)
    {
      return getChildAt(paramInt1);
      if (paramInt2 < paramInt1) {
        i = -1;
      } else {
        i = 0;
      }
    }
    int j;
    if (this.mOrientationHelper.getDecoratedStart(getChildAt(paramInt1)) < this.mOrientationHelper.getStartAfterPadding()) {
      j = 16644;
    }
    for (int k = 16388; this.mOrientation == 0; k = 4097)
    {
      return this.mHorizontalBoundCheck.findOneViewWithinBoundFlags(paramInt1, paramInt2, j, k);
      j = 4161;
    }
    return this.mVerticalBoundCheck.findOneViewWithinBoundFlags(paramInt1, paramInt2, j, k);
  }
  
  View findOneVisibleChild(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2)
  {
    ensureLayoutState();
    if (paramBoolean1) {}
    int j;
    for (int i = 24579;; i = 320)
    {
      j = 0;
      if (paramBoolean2) {
        j = 320;
      }
      if (this.mOrientation != 0) {
        break;
      }
      return this.mHorizontalBoundCheck.findOneViewWithinBoundFlags(paramInt1, paramInt2, i, j);
    }
    return this.mVerticalBoundCheck.findOneViewWithinBoundFlags(paramInt1, paramInt2, i, j);
  }
  
  View findReferenceChild(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, int paramInt1, int paramInt2, int paramInt3)
  {
    ensureLayoutState();
    Object localObject1 = null;
    Object localObject2 = null;
    int i = this.mOrientationHelper.getStartAfterPadding();
    int j = this.mOrientationHelper.getEndAfterPadding();
    int k;
    int m;
    label40:
    Object localObject3;
    if (paramInt2 > paramInt1)
    {
      k = 1;
      m = paramInt1;
      if (m == paramInt2) {
        break label154;
      }
      localObject3 = getChildAt(m);
      int n = getPosition((View)localObject3);
      if ((n >= 0) && (n < paramInt3))
      {
        if (!((RecyclerView.LayoutParams)((View)localObject3).getLayoutParams()).isItemRemoved()) {
          break label114;
        }
        if (localObject1 == null) {
          localObject1 = localObject3;
        }
      }
    }
    for (;;)
    {
      m += k;
      break label40;
      k = -1;
      break;
      label114:
      if ((this.mOrientationHelper.getDecoratedStart((View)localObject3) < j) && (this.mOrientationHelper.getDecoratedEnd((View)localObject3) >= i)) {
        break label163;
      }
      if (localObject2 == null) {
        localObject2 = localObject3;
      }
    }
    label154:
    if (localObject2 != null) {}
    for (;;)
    {
      localObject3 = localObject2;
      label163:
      return localObject3;
      localObject2 = localObject1;
    }
  }
  
  public View findViewByPosition(int paramInt)
  {
    int i = getChildCount();
    View localView;
    if (i == 0) {
      localView = null;
    }
    do
    {
      return localView;
      int j = paramInt - getPosition(getChildAt(0));
      if ((j < 0) || (j >= i)) {
        break;
      }
      localView = getChildAt(j);
    } while (getPosition(localView) == paramInt);
    return super.findViewByPosition(paramInt);
  }
  
  public RecyclerView.LayoutParams generateDefaultLayoutParams()
  {
    return new RecyclerView.LayoutParams(-2, -2);
  }
  
  protected int getExtraLayoutSpace(RecyclerView.State paramState)
  {
    if (paramState.hasTargetScrollPosition()) {
      return this.mOrientationHelper.getTotalSpace();
    }
    return 0;
  }
  
  @Deprecated
  public int getInitialItemPrefetchCount()
  {
    return getInitialPrefetchItemCount();
  }
  
  public int getInitialPrefetchItemCount()
  {
    return this.mInitialPrefetchItemCount;
  }
  
  public int getOrientation()
  {
    return this.mOrientation;
  }
  
  public boolean getRecycleChildrenOnDetach()
  {
    return this.mRecycleChildrenOnDetach;
  }
  
  public boolean getReverseLayout()
  {
    return this.mReverseLayout;
  }
  
  public boolean getStackFromEnd()
  {
    return this.mStackFromEnd;
  }
  
  protected boolean isLayoutRTL()
  {
    return getLayoutDirection() == 1;
  }
  
  public boolean isSmoothScrollbarEnabled()
  {
    return this.mSmoothScrollbarEnabled;
  }
  
  void layoutChunk(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, LayoutState paramLayoutState, LayoutChunkResult paramLayoutChunkResult)
  {
    View localView = paramLayoutState.next(paramRecycler);
    if (localView == null)
    {
      paramLayoutChunkResult.mFinished = true;
      return;
    }
    RecyclerView.LayoutParams localLayoutParams = (RecyclerView.LayoutParams)localView.getLayoutParams();
    boolean bool4;
    label66:
    int m;
    int k;
    label128:
    int j;
    int i;
    if (paramLayoutState.mScrapList == null)
    {
      boolean bool3 = this.mShouldReverseLayout;
      if (paramLayoutState.mLayoutDirection == -1)
      {
        bool4 = true;
        if (bool3 != bool4) {
          break label207;
        }
        addView(localView);
        measureChildWithMargins(localView, 0, 0);
        paramLayoutChunkResult.mConsumed = this.mOrientationHelper.getDecoratedMeasurement(localView);
        if (this.mOrientation != 1) {
          break label310;
        }
        if (!isLayoutRTL()) {
          break label266;
        }
        m = getWidth() - getPaddingRight();
        k = m - this.mOrientationHelper.getDecoratedMeasurementInOther(localView);
        if (paramLayoutState.mLayoutDirection != -1) {
          break label289;
        }
        j = paramLayoutState.mOffset;
        i = paramLayoutState.mOffset - paramLayoutChunkResult.mConsumed;
      }
    }
    for (;;)
    {
      layoutDecoratedWithMargins(localView, k, i, m, j);
      if ((localLayoutParams.isItemRemoved()) || (localLayoutParams.isItemChanged())) {
        paramLayoutChunkResult.mIgnoreConsumed = true;
      }
      paramLayoutChunkResult.mFocusable = localView.hasFocusable();
      return;
      bool4 = false;
      break;
      label207:
      addView(localView, 0);
      break label66;
      boolean bool1 = this.mShouldReverseLayout;
      if (paramLayoutState.mLayoutDirection == -1) {}
      for (boolean bool2 = true;; bool2 = false)
      {
        if (bool1 != bool2) {
          break label256;
        }
        addDisappearingView(localView);
        break;
      }
      label256:
      addDisappearingView(localView, 0);
      break label66;
      label266:
      k = getPaddingLeft();
      m = k + this.mOrientationHelper.getDecoratedMeasurementInOther(localView);
      break label128;
      label289:
      i = paramLayoutState.mOffset;
      j = paramLayoutState.mOffset + paramLayoutChunkResult.mConsumed;
      continue;
      label310:
      i = getPaddingTop();
      j = i + this.mOrientationHelper.getDecoratedMeasurementInOther(localView);
      if (paramLayoutState.mLayoutDirection == -1)
      {
        m = paramLayoutState.mOffset;
        k = paramLayoutState.mOffset - paramLayoutChunkResult.mConsumed;
      }
      else
      {
        k = paramLayoutState.mOffset;
        m = paramLayoutState.mOffset + paramLayoutChunkResult.mConsumed;
      }
    }
  }
  
  void onAnchorReady(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, AnchorInfo paramAnchorInfo, int paramInt) {}
  
  public void onDetachedFromWindow(RecyclerView paramRecyclerView, RecyclerView.Recycler paramRecycler)
  {
    super.onDetachedFromWindow(paramRecyclerView, paramRecycler);
    if (this.mRecycleChildrenOnDetach)
    {
      removeAndRecycleAllViews(paramRecycler);
      paramRecycler.clear();
    }
  }
  
  public View onFocusSearchFailed(View paramView, int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    resolveShouldLayoutReverse();
    if (getChildCount() == 0)
    {
      localView2 = null;
      return localView2;
    }
    int i = convertFocusDirectionToLayoutDirection(paramInt);
    if (i == Integer.MIN_VALUE) {
      return null;
    }
    ensureLayoutState();
    ensureLayoutState();
    updateLayoutState(i, (int)(0.33333334F * this.mOrientationHelper.getTotalSpace()), false, paramState);
    this.mLayoutState.mScrollingOffset = Integer.MIN_VALUE;
    this.mLayoutState.mRecycle = false;
    fill(paramRecycler, this.mLayoutState, paramState, true);
    View localView1;
    if (i == -1)
    {
      localView1 = findPartiallyOrCompletelyInvisibleChildClosestToStart(paramRecycler, paramState);
      label107:
      if (i != -1) {
        break label146;
      }
    }
    label146:
    for (View localView2 = getChildClosestToStart();; localView2 = getChildClosestToEnd())
    {
      if (!localView2.hasFocusable()) {
        break label155;
      }
      if (localView1 != null) {
        break;
      }
      return null;
      localView1 = findPartiallyOrCompletelyInvisibleChildClosestToEnd(paramRecycler, paramState);
      break label107;
    }
    label155:
    return localView1;
  }
  
  public void onInitializeAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    super.onInitializeAccessibilityEvent(paramAccessibilityEvent);
    if (getChildCount() > 0)
    {
      AccessibilityRecordCompat localAccessibilityRecordCompat = AccessibilityEventCompat.asRecord(paramAccessibilityEvent);
      localAccessibilityRecordCompat.setFromIndex(findFirstVisibleItemPosition());
      localAccessibilityRecordCompat.setToIndex(findLastVisibleItemPosition());
    }
  }
  
  public void onLayoutChildren(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    if (((this.mPendingSavedState != null) || (this.mPendingScrollPosition != -1)) && (paramState.getItemCount() == 0))
    {
      removeAndRecycleAllViews(paramRecycler);
      return;
    }
    if ((this.mPendingSavedState != null) && (this.mPendingSavedState.hasValidAnchor())) {
      this.mPendingScrollPosition = this.mPendingSavedState.mAnchorPosition;
    }
    ensureLayoutState();
    this.mLayoutState.mRecycle = false;
    resolveShouldLayoutReverse();
    if ((!this.mAnchorInfo.mValid) || (this.mPendingScrollPosition != -1) || (this.mPendingSavedState != null))
    {
      this.mAnchorInfo.reset();
      this.mAnchorInfo.mLayoutFromEnd = (this.mShouldReverseLayout ^ this.mStackFromEnd);
      updateAnchorInfoForLayout(paramRecycler, paramState, this.mAnchorInfo);
      this.mAnchorInfo.mValid = true;
    }
    int i = getExtraLayoutSpace(paramState);
    int k;
    int j;
    int m;
    int n;
    View localView;
    int i17;
    label254:
    label266:
    int i1;
    label286:
    int i4;
    int i2;
    if (this.mLayoutState.mLastScrollDelta >= 0)
    {
      k = i;
      j = 0;
      m = j + this.mOrientationHelper.getStartAfterPadding();
      n = k + this.mOrientationHelper.getEndPadding();
      if ((paramState.isPreLayout()) && (this.mPendingScrollPosition != -1) && (this.mPendingScrollPositionOffset != Integer.MIN_VALUE))
      {
        localView = findViewByPosition(this.mPendingScrollPosition);
        if (localView != null)
        {
          if (!this.mShouldReverseLayout) {
            break label634;
          }
          i17 = this.mOrientationHelper.getEndAfterPadding() - this.mOrientationHelper.getDecoratedEnd(localView) - this.mPendingScrollPositionOffset;
          if (i17 <= 0) {
            break label665;
          }
          m += i17;
        }
      }
      if (!this.mAnchorInfo.mLayoutFromEnd) {
        break label681;
      }
      if (!this.mShouldReverseLayout) {
        break label675;
      }
      i1 = 1;
      onAnchorReady(paramRecycler, paramState, this.mAnchorInfo, i1);
      detachAndScrapAttachedViews(paramRecycler);
      this.mLayoutState.mInfinite = resolveIsInfinite();
      this.mLayoutState.mIsPreLayout = paramState.isPreLayout();
      if (!this.mAnchorInfo.mLayoutFromEnd) {
        break label700;
      }
      updateLayoutStateToFillStart(this.mAnchorInfo);
      this.mLayoutState.mExtra = m;
      fill(paramRecycler, this.mLayoutState, paramState, false);
      i4 = this.mLayoutState.mOffset;
      int i14 = this.mLayoutState.mCurrentPosition;
      if (this.mLayoutState.mAvailable > 0) {
        n += this.mLayoutState.mAvailable;
      }
      updateLayoutStateToFillEnd(this.mAnchorInfo);
      this.mLayoutState.mExtra = n;
      LayoutState localLayoutState2 = this.mLayoutState;
      localLayoutState2.mCurrentPosition += this.mLayoutState.mItemDirection;
      fill(paramRecycler, this.mLayoutState, paramState, false);
      i2 = this.mLayoutState.mOffset;
      if (this.mLayoutState.mAvailable > 0)
      {
        int i15 = this.mLayoutState.mAvailable;
        updateLayoutStateToFillStart(i14, i4);
        this.mLayoutState.mExtra = i15;
        fill(paramRecycler, this.mLayoutState, paramState, false);
        i4 = this.mLayoutState.mOffset;
      }
      label523:
      if (getChildCount() > 0)
      {
        if (!(this.mShouldReverseLayout ^ this.mStackFromEnd)) {
          break label891;
        }
        int i10 = fixLayoutEndGap(i2, paramRecycler, paramState, true);
        int i11 = i4 + i10;
        int i12 = i2 + i10;
        int i13 = fixLayoutStartGap(i11, paramRecycler, paramState, false);
        i4 = i11 + i13;
        i2 = i12 + i13;
      }
      label592:
      layoutForPredictiveAnimations(paramRecycler, paramState, i4, i2);
      if (paramState.isPreLayout()) {
        break label944;
      }
      this.mOrientationHelper.onLayoutComplete();
    }
    for (;;)
    {
      this.mLastStackFromEnd = this.mStackFromEnd;
      return;
      j = i;
      k = 0;
      break;
      label634:
      int i16 = this.mOrientationHelper.getDecoratedStart(localView) - this.mOrientationHelper.getStartAfterPadding();
      i17 = this.mPendingScrollPositionOffset - i16;
      break label254;
      label665:
      n -= i17;
      break label266;
      label675:
      i1 = -1;
      break label286;
      label681:
      if (this.mShouldReverseLayout) {}
      for (i1 = -1;; i1 = 1) {
        break;
      }
      label700:
      updateLayoutStateToFillEnd(this.mAnchorInfo);
      this.mLayoutState.mExtra = n;
      fill(paramRecycler, this.mLayoutState, paramState, false);
      i2 = this.mLayoutState.mOffset;
      int i3 = this.mLayoutState.mCurrentPosition;
      if (this.mLayoutState.mAvailable > 0) {
        m += this.mLayoutState.mAvailable;
      }
      updateLayoutStateToFillStart(this.mAnchorInfo);
      this.mLayoutState.mExtra = m;
      LayoutState localLayoutState1 = this.mLayoutState;
      localLayoutState1.mCurrentPosition += this.mLayoutState.mItemDirection;
      fill(paramRecycler, this.mLayoutState, paramState, false);
      i4 = this.mLayoutState.mOffset;
      if (this.mLayoutState.mAvailable <= 0) {
        break label523;
      }
      int i5 = this.mLayoutState.mAvailable;
      updateLayoutStateToFillEnd(i3, i2);
      this.mLayoutState.mExtra = i5;
      fill(paramRecycler, this.mLayoutState, paramState, false);
      i2 = this.mLayoutState.mOffset;
      break label523;
      label891:
      int i6 = fixLayoutStartGap(i4, paramRecycler, paramState, true);
      int i7 = i4 + i6;
      int i8 = i2 + i6;
      int i9 = fixLayoutEndGap(i8, paramRecycler, paramState, false);
      i4 = i7 + i9;
      i2 = i8 + i9;
      break label592;
      label944:
      this.mAnchorInfo.reset();
    }
  }
  
  public void onLayoutCompleted(RecyclerView.State paramState)
  {
    super.onLayoutCompleted(paramState);
    this.mPendingSavedState = null;
    this.mPendingScrollPosition = -1;
    this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
    this.mAnchorInfo.reset();
  }
  
  public void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if ((paramParcelable instanceof SavedState))
    {
      this.mPendingSavedState = ((SavedState)paramParcelable);
      requestLayout();
    }
  }
  
  public Parcelable onSaveInstanceState()
  {
    if (this.mPendingSavedState != null) {
      return new SavedState(this.mPendingSavedState);
    }
    SavedState localSavedState = new SavedState();
    if (getChildCount() > 0)
    {
      ensureLayoutState();
      boolean bool = this.mLastStackFromEnd ^ this.mShouldReverseLayout;
      localSavedState.mAnchorLayoutFromEnd = bool;
      if (bool)
      {
        View localView2 = getChildClosestToEnd();
        localSavedState.mAnchorOffset = (this.mOrientationHelper.getEndAfterPadding() - this.mOrientationHelper.getDecoratedEnd(localView2));
        localSavedState.mAnchorPosition = getPosition(localView2);
        return localSavedState;
      }
      View localView1 = getChildClosestToStart();
      localSavedState.mAnchorPosition = getPosition(localView1);
      localSavedState.mAnchorOffset = (this.mOrientationHelper.getDecoratedStart(localView1) - this.mOrientationHelper.getStartAfterPadding());
      return localSavedState;
    }
    localSavedState.invalidateAnchor();
    return localSavedState;
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public void prepareForDrop(View paramView1, View paramView2, int paramInt1, int paramInt2)
  {
    assertNotInLayoutOrScroll("Cannot drop a view during a scroll or layout calculation");
    ensureLayoutState();
    resolveShouldLayoutReverse();
    int i = getPosition(paramView1);
    int j = getPosition(paramView2);
    int k;
    if (i < j) {
      k = 1;
    }
    while (this.mShouldReverseLayout) {
      if (k == 1)
      {
        scrollToPositionWithOffset(j, this.mOrientationHelper.getEndAfterPadding() - (this.mOrientationHelper.getDecoratedStart(paramView2) + this.mOrientationHelper.getDecoratedMeasurement(paramView1)));
        return;
        k = -1;
      }
      else
      {
        scrollToPositionWithOffset(j, this.mOrientationHelper.getEndAfterPadding() - this.mOrientationHelper.getDecoratedEnd(paramView2));
        return;
      }
    }
    if (k == -1)
    {
      scrollToPositionWithOffset(j, this.mOrientationHelper.getDecoratedStart(paramView2));
      return;
    }
    scrollToPositionWithOffset(j, this.mOrientationHelper.getDecoratedEnd(paramView2) - this.mOrientationHelper.getDecoratedMeasurement(paramView1));
  }
  
  boolean resolveIsInfinite()
  {
    return (this.mOrientationHelper.getMode() == 0) && (this.mOrientationHelper.getEnd() == 0);
  }
  
  int scrollBy(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    if ((getChildCount() == 0) || (paramInt == 0)) {}
    int i;
    int j;
    int k;
    do
    {
      return 0;
      this.mLayoutState.mRecycle = true;
      ensureLayoutState();
      if (paramInt <= 0) {
        break;
      }
      i = 1;
      j = Math.abs(paramInt);
      updateLayoutState(i, j, true, paramState);
      k = this.mLayoutState.mScrollingOffset + fill(paramRecycler, this.mLayoutState, paramState, false);
    } while (k < 0);
    if (j > k) {}
    for (int m = i * k;; m = paramInt)
    {
      this.mOrientationHelper.offsetChildren(-m);
      this.mLayoutState.mLastScrollDelta = m;
      return m;
      i = -1;
      break;
    }
  }
  
  public int scrollHorizontallyBy(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    if (this.mOrientation == 1) {
      return 0;
    }
    return scrollBy(paramInt, paramRecycler, paramState);
  }
  
  public void scrollToPosition(int paramInt)
  {
    this.mPendingScrollPosition = paramInt;
    this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
    if (this.mPendingSavedState != null) {
      this.mPendingSavedState.invalidateAnchor();
    }
    requestLayout();
  }
  
  public void scrollToPositionWithOffset(int paramInt1, int paramInt2)
  {
    this.mPendingScrollPosition = paramInt1;
    this.mPendingScrollPositionOffset = paramInt2;
    if (this.mPendingSavedState != null) {
      this.mPendingSavedState.invalidateAnchor();
    }
    requestLayout();
  }
  
  public int scrollVerticallyBy(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    if (this.mOrientation == 0) {
      return 0;
    }
    return scrollBy(paramInt, paramRecycler, paramState);
  }
  
  public void setInitialPrefetchItemCount(int paramInt)
  {
    this.mInitialPrefetchItemCount = paramInt;
  }
  
  public void setOrientation(int paramInt)
  {
    if ((paramInt != 0) && (paramInt != 1)) {
      throw new IllegalArgumentException("invalid orientation:" + paramInt);
    }
    assertNotInLayoutOrScroll(null);
    if (paramInt == this.mOrientation) {
      return;
    }
    this.mOrientation = paramInt;
    this.mOrientationHelper = null;
    requestLayout();
  }
  
  public void setRecycleChildrenOnDetach(boolean paramBoolean)
  {
    this.mRecycleChildrenOnDetach = paramBoolean;
  }
  
  public void setReverseLayout(boolean paramBoolean)
  {
    assertNotInLayoutOrScroll(null);
    if (paramBoolean == this.mReverseLayout) {
      return;
    }
    this.mReverseLayout = paramBoolean;
    requestLayout();
  }
  
  public void setSmoothScrollbarEnabled(boolean paramBoolean)
  {
    this.mSmoothScrollbarEnabled = paramBoolean;
  }
  
  public void setStackFromEnd(boolean paramBoolean)
  {
    assertNotInLayoutOrScroll(null);
    if (this.mStackFromEnd == paramBoolean) {
      return;
    }
    this.mStackFromEnd = paramBoolean;
    requestLayout();
  }
  
  boolean shouldMeasureTwice()
  {
    return (getHeightMode() != 1073741824) && (getWidthMode() != 1073741824) && (hasFlexibleChildInBothOrientations());
  }
  
  public void smoothScrollToPosition(RecyclerView paramRecyclerView, RecyclerView.State paramState, int paramInt)
  {
    LinearSmoothScroller localLinearSmoothScroller = new LinearSmoothScroller(paramRecyclerView.getContext());
    localLinearSmoothScroller.setTargetPosition(paramInt);
    startSmoothScroll(localLinearSmoothScroller);
  }
  
  public boolean supportsPredictiveItemAnimations()
  {
    return (this.mPendingSavedState == null) && (this.mLastStackFromEnd == this.mStackFromEnd);
  }
  
  void validateChildOrder()
  {
    int i = 1;
    Log.d("LinearLayoutManager", "validating child count " + getChildCount());
    if (getChildCount() < i) {}
    for (;;)
    {
      return;
      int j = getPosition(getChildAt(0));
      int k = this.mOrientationHelper.getDecoratedStart(getChildAt(0));
      boolean bool;
      if (this.mShouldReverseLayout) {
        for (int i2 = 1; i2 < getChildCount(); i2++)
        {
          View localView2 = getChildAt(i2);
          int i3 = getPosition(localView2);
          int i4 = this.mOrientationHelper.getDecoratedStart(localView2);
          if (i3 < j)
          {
            logChildren();
            StringBuilder localStringBuilder2 = new StringBuilder().append("detected invalid position. loc invalid? ");
            if (i4 < k) {}
            for (;;)
            {
              throw new RuntimeException(i);
              bool = false;
            }
          }
          if (i4 > k)
          {
            logChildren();
            throw new RuntimeException("detected invalid location");
          }
        }
      } else {
        for (int m = 1; m < getChildCount(); m++)
        {
          View localView1 = getChildAt(m);
          int n = getPosition(localView1);
          int i1 = this.mOrientationHelper.getDecoratedStart(localView1);
          if (n < j)
          {
            logChildren();
            StringBuilder localStringBuilder1 = new StringBuilder().append("detected invalid position. loc invalid? ");
            if (i1 < k) {}
            for (;;)
            {
              throw new RuntimeException(bool);
              bool = false;
            }
          }
          if (i1 < k)
          {
            logChildren();
            throw new RuntimeException("detected invalid location");
          }
        }
      }
    }
  }
  
  class AnchorInfo
  {
    int mCoordinate;
    boolean mLayoutFromEnd;
    int mPosition;
    boolean mValid;
    
    AnchorInfo()
    {
      reset();
    }
    
    void assignCoordinateFromPadding()
    {
      if (this.mLayoutFromEnd) {}
      for (int i = LinearLayoutManager.this.mOrientationHelper.getEndAfterPadding();; i = LinearLayoutManager.this.mOrientationHelper.getStartAfterPadding())
      {
        this.mCoordinate = i;
        return;
      }
    }
    
    public void assignFromView(View paramView)
    {
      if (this.mLayoutFromEnd) {}
      for (this.mCoordinate = (LinearLayoutManager.this.mOrientationHelper.getDecoratedEnd(paramView) + LinearLayoutManager.this.mOrientationHelper.getTotalSpaceChange());; this.mCoordinate = LinearLayoutManager.this.mOrientationHelper.getDecoratedStart(paramView))
      {
        this.mPosition = LinearLayoutManager.this.getPosition(paramView);
        return;
      }
    }
    
    public void assignFromViewAndKeepVisibleRect(View paramView)
    {
      int i = LinearLayoutManager.this.mOrientationHelper.getTotalSpaceChange();
      if (i >= 0) {
        assignFromView(paramView);
      }
      int k;
      int i1;
      do
      {
        int j;
        do
        {
          int i2;
          int i6;
          do
          {
            do
            {
              return;
              this.mPosition = LinearLayoutManager.this.getPosition(paramView);
              if (!this.mLayoutFromEnd) {
                break;
              }
              i2 = LinearLayoutManager.this.mOrientationHelper.getEndAfterPadding() - i - LinearLayoutManager.this.mOrientationHelper.getDecoratedEnd(paramView);
              this.mCoordinate = (LinearLayoutManager.this.mOrientationHelper.getEndAfterPadding() - i2);
            } while (i2 <= 0);
            int i3 = LinearLayoutManager.this.mOrientationHelper.getDecoratedMeasurement(paramView);
            int i4 = this.mCoordinate - i3;
            int i5 = LinearLayoutManager.this.mOrientationHelper.getStartAfterPadding();
            i6 = i4 - (i5 + Math.min(LinearLayoutManager.this.mOrientationHelper.getDecoratedStart(paramView) - i5, 0));
          } while (i6 >= 0);
          this.mCoordinate += Math.min(i2, -i6);
          return;
          j = LinearLayoutManager.this.mOrientationHelper.getDecoratedStart(paramView);
          k = j - LinearLayoutManager.this.mOrientationHelper.getStartAfterPadding();
          this.mCoordinate = j;
        } while (k <= 0);
        int m = j + LinearLayoutManager.this.mOrientationHelper.getDecoratedMeasurement(paramView);
        int n = LinearLayoutManager.this.mOrientationHelper.getEndAfterPadding() - i - LinearLayoutManager.this.mOrientationHelper.getDecoratedEnd(paramView);
        i1 = LinearLayoutManager.this.mOrientationHelper.getEndAfterPadding() - Math.min(0, n) - m;
      } while (i1 >= 0);
      this.mCoordinate -= Math.min(k, -i1);
    }
    
    boolean isViewValidAsAnchor(View paramView, RecyclerView.State paramState)
    {
      RecyclerView.LayoutParams localLayoutParams = (RecyclerView.LayoutParams)paramView.getLayoutParams();
      return (!localLayoutParams.isItemRemoved()) && (localLayoutParams.getViewLayoutPosition() >= 0) && (localLayoutParams.getViewLayoutPosition() < paramState.getItemCount());
    }
    
    void reset()
    {
      this.mPosition = -1;
      this.mCoordinate = Integer.MIN_VALUE;
      this.mLayoutFromEnd = false;
      this.mValid = false;
    }
    
    public String toString()
    {
      return "AnchorInfo{mPosition=" + this.mPosition + ", mCoordinate=" + this.mCoordinate + ", mLayoutFromEnd=" + this.mLayoutFromEnd + ", mValid=" + this.mValid + '}';
    }
  }
  
  protected static class LayoutChunkResult
  {
    public int mConsumed;
    public boolean mFinished;
    public boolean mFocusable;
    public boolean mIgnoreConsumed;
    
    protected LayoutChunkResult() {}
    
    void resetInternal()
    {
      this.mConsumed = 0;
      this.mFinished = false;
      this.mIgnoreConsumed = false;
      this.mFocusable = false;
    }
  }
  
  static class LayoutState
  {
    static final int INVALID_LAYOUT = Integer.MIN_VALUE;
    static final int ITEM_DIRECTION_HEAD = -1;
    static final int ITEM_DIRECTION_TAIL = 1;
    static final int LAYOUT_END = 1;
    static final int LAYOUT_START = -1;
    static final int SCROLLING_OFFSET_NaN = Integer.MIN_VALUE;
    static final String TAG = "LLM#LayoutState";
    int mAvailable;
    int mCurrentPosition;
    int mExtra = 0;
    boolean mInfinite;
    boolean mIsPreLayout = false;
    int mItemDirection;
    int mLastScrollDelta;
    int mLayoutDirection;
    int mOffset;
    boolean mRecycle = true;
    List<RecyclerView.ViewHolder> mScrapList = null;
    int mScrollingOffset;
    
    LayoutState() {}
    
    private View nextViewFromScrapList()
    {
      int i = this.mScrapList.size();
      int j = 0;
      if (j < i)
      {
        View localView = ((RecyclerView.ViewHolder)this.mScrapList.get(j)).itemView;
        RecyclerView.LayoutParams localLayoutParams = (RecyclerView.LayoutParams)localView.getLayoutParams();
        if (localLayoutParams.isItemRemoved()) {}
        while (this.mCurrentPosition != localLayoutParams.getViewLayoutPosition())
        {
          j++;
          break;
        }
        assignPositionFromScrapList(localView);
        return localView;
      }
      return null;
    }
    
    public void assignPositionFromScrapList()
    {
      assignPositionFromScrapList(null);
    }
    
    public void assignPositionFromScrapList(View paramView)
    {
      View localView = nextViewInLimitedList(paramView);
      if (localView == null)
      {
        this.mCurrentPosition = -1;
        return;
      }
      this.mCurrentPosition = ((RecyclerView.LayoutParams)localView.getLayoutParams()).getViewLayoutPosition();
    }
    
    boolean hasMore(RecyclerView.State paramState)
    {
      return (this.mCurrentPosition >= 0) && (this.mCurrentPosition < paramState.getItemCount());
    }
    
    void log()
    {
      Log.d("LLM#LayoutState", "avail:" + this.mAvailable + ", ind:" + this.mCurrentPosition + ", dir:" + this.mItemDirection + ", offset:" + this.mOffset + ", layoutDir:" + this.mLayoutDirection);
    }
    
    View next(RecyclerView.Recycler paramRecycler)
    {
      if (this.mScrapList != null) {
        return nextViewFromScrapList();
      }
      View localView = paramRecycler.getViewForPosition(this.mCurrentPosition);
      this.mCurrentPosition += this.mItemDirection;
      return localView;
    }
    
    public View nextViewInLimitedList(View paramView)
    {
      int i = this.mScrapList.size();
      Object localObject = null;
      int j = Integer.MAX_VALUE;
      int k = 0;
      if (k < i)
      {
        View localView = ((RecyclerView.ViewHolder)this.mScrapList.get(k)).itemView;
        RecyclerView.LayoutParams localLayoutParams = (RecyclerView.LayoutParams)localView.getLayoutParams();
        if ((localView == paramView) || (localLayoutParams.isItemRemoved())) {}
        int m;
        do
        {
          do
          {
            k++;
            break;
            m = (localLayoutParams.getViewLayoutPosition() - this.mCurrentPosition) * this.mItemDirection;
          } while ((m < 0) || (m >= j));
          localObject = localView;
          j = m;
        } while (m != 0);
      }
      return localObject;
    }
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public static class SavedState
    implements Parcelable
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public LinearLayoutManager.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new LinearLayoutManager.SavedState(paramAnonymousParcel);
      }
      
      public LinearLayoutManager.SavedState[] newArray(int paramAnonymousInt)
      {
        return new LinearLayoutManager.SavedState[paramAnonymousInt];
      }
    };
    boolean mAnchorLayoutFromEnd;
    int mAnchorOffset;
    int mAnchorPosition;
    
    public SavedState() {}
    
    SavedState(Parcel paramParcel)
    {
      this.mAnchorPosition = paramParcel.readInt();
      this.mAnchorOffset = paramParcel.readInt();
      if (paramParcel.readInt() == i) {}
      for (;;)
      {
        this.mAnchorLayoutFromEnd = i;
        return;
        i = 0;
      }
    }
    
    public SavedState(SavedState paramSavedState)
    {
      this.mAnchorPosition = paramSavedState.mAnchorPosition;
      this.mAnchorOffset = paramSavedState.mAnchorOffset;
      this.mAnchorLayoutFromEnd = paramSavedState.mAnchorLayoutFromEnd;
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    boolean hasValidAnchor()
    {
      return this.mAnchorPosition >= 0;
    }
    
    void invalidateAnchor()
    {
      this.mAnchorPosition = -1;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      paramParcel.writeInt(this.mAnchorPosition);
      paramParcel.writeInt(this.mAnchorOffset);
      if (this.mAnchorLayoutFromEnd) {}
      for (int i = 1;; i = 0)
      {
        paramParcel.writeInt(i);
        return;
      }
    }
  }
}
