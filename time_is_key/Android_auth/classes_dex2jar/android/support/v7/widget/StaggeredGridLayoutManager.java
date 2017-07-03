package android.support.v7.widget;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.CollectionItemInfoCompat;
import android.support.v4.view.accessibility.AccessibilityRecordCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.accessibility.AccessibilityEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

public class StaggeredGridLayoutManager
  extends RecyclerView.LayoutManager
  implements RecyclerView.SmoothScroller.ScrollVectorProvider
{
  static final boolean DEBUG = false;
  @Deprecated
  public static final int GAP_HANDLING_LAZY = 1;
  public static final int GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS = 2;
  public static final int GAP_HANDLING_NONE = 0;
  public static final int HORIZONTAL = 0;
  static final int INVALID_OFFSET = Integer.MIN_VALUE;
  private static final float MAX_SCROLL_FACTOR = 0.33333334F;
  private static final String TAG = "StaggeredGridLayoutManager";
  public static final int VERTICAL = 1;
  private final AnchorInfo mAnchorInfo = new AnchorInfo();
  private final Runnable mCheckForGapsRunnable;
  private int mFullSizeSpec;
  private int mGapStrategy = 2;
  private boolean mLaidOutInvalidFullSpan = false;
  private boolean mLastLayoutFromEnd;
  private boolean mLastLayoutRTL;
  @NonNull
  private final LayoutState mLayoutState;
  LazySpanLookup mLazySpanLookup = new LazySpanLookup();
  private int mOrientation;
  private SavedState mPendingSavedState;
  int mPendingScrollPosition = -1;
  int mPendingScrollPositionOffset = Integer.MIN_VALUE;
  private int[] mPrefetchDistances;
  @NonNull
  OrientationHelper mPrimaryOrientation;
  private BitSet mRemainingSpans;
  boolean mReverseLayout = false;
  @NonNull
  OrientationHelper mSecondaryOrientation;
  boolean mShouldReverseLayout = false;
  private int mSizePerSpan;
  private boolean mSmoothScrollbarEnabled;
  private int mSpanCount = -1;
  Span[] mSpans;
  private final Rect mTmpRect = new Rect();
  
  public StaggeredGridLayoutManager(int paramInt1, int paramInt2)
  {
    this.mSmoothScrollbarEnabled = bool;
    this.mCheckForGapsRunnable = new Runnable()
    {
      public void run()
      {
        StaggeredGridLayoutManager.this.checkForGaps();
      }
    };
    this.mOrientation = paramInt2;
    setSpanCount(paramInt1);
    if (this.mGapStrategy != 0) {}
    for (;;)
    {
      setAutoMeasureEnabled(bool);
      this.mLayoutState = new LayoutState();
      createOrientationHelpers();
      return;
      bool = false;
    }
  }
  
  public StaggeredGridLayoutManager(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    this.mSmoothScrollbarEnabled = bool;
    this.mCheckForGapsRunnable = new Runnable()
    {
      public void run()
      {
        StaggeredGridLayoutManager.this.checkForGaps();
      }
    };
    RecyclerView.LayoutManager.Properties localProperties = getProperties(paramContext, paramAttributeSet, paramInt1, paramInt2);
    setOrientation(localProperties.orientation);
    setSpanCount(localProperties.spanCount);
    setReverseLayout(localProperties.reverseLayout);
    if (this.mGapStrategy != 0) {}
    for (;;)
    {
      setAutoMeasureEnabled(bool);
      this.mLayoutState = new LayoutState();
      createOrientationHelpers();
      return;
      bool = false;
    }
  }
  
  private void appendViewToAllSpans(View paramView)
  {
    for (int i = -1 + this.mSpanCount; i >= 0; i--) {
      this.mSpans[i].appendToSpan(paramView);
    }
  }
  
  private void applyPendingSavedState(AnchorInfo paramAnchorInfo)
  {
    if (this.mPendingSavedState.mSpanOffsetsSize > 0) {
      if (this.mPendingSavedState.mSpanOffsetsSize == this.mSpanCount)
      {
        int i = 0;
        if (i < this.mSpanCount)
        {
          this.mSpans[i].clear();
          int j = this.mPendingSavedState.mSpanOffsets[i];
          if (j != Integer.MIN_VALUE)
          {
            if (!this.mPendingSavedState.mAnchorLayoutFromEnd) {
              break label95;
            }
            j += this.mPrimaryOrientation.getEndAfterPadding();
          }
          for (;;)
          {
            this.mSpans[i].setLine(j);
            i++;
            break;
            label95:
            j += this.mPrimaryOrientation.getStartAfterPadding();
          }
        }
      }
      else
      {
        this.mPendingSavedState.invalidateSpanInfo();
        this.mPendingSavedState.mAnchorPosition = this.mPendingSavedState.mVisibleAnchorPosition;
      }
    }
    this.mLastLayoutRTL = this.mPendingSavedState.mLastLayoutRTL;
    setReverseLayout(this.mPendingSavedState.mReverseLayout);
    resolveShouldLayoutReverse();
    if (this.mPendingSavedState.mAnchorPosition != -1) {
      this.mPendingScrollPosition = this.mPendingSavedState.mAnchorPosition;
    }
    for (paramAnchorInfo.mLayoutFromEnd = this.mPendingSavedState.mAnchorLayoutFromEnd;; paramAnchorInfo.mLayoutFromEnd = this.mShouldReverseLayout)
    {
      if (this.mPendingSavedState.mSpanLookupSize > 1)
      {
        this.mLazySpanLookup.mData = this.mPendingSavedState.mSpanLookup;
        this.mLazySpanLookup.mFullSpanItems = this.mPendingSavedState.mFullSpanItems;
      }
      return;
    }
  }
  
  private void attachViewToSpans(View paramView, LayoutParams paramLayoutParams, LayoutState paramLayoutState)
  {
    if (paramLayoutState.mLayoutDirection == 1)
    {
      if (paramLayoutParams.mFullSpan)
      {
        appendViewToAllSpans(paramView);
        return;
      }
      paramLayoutParams.mSpan.appendToSpan(paramView);
      return;
    }
    if (paramLayoutParams.mFullSpan)
    {
      prependViewToAllSpans(paramView);
      return;
    }
    paramLayoutParams.mSpan.prependToSpan(paramView);
  }
  
  private int calculateScrollDirectionForPosition(int paramInt)
  {
    int i = -1;
    if (getChildCount() == 0)
    {
      if (this.mShouldReverseLayout) {
        return 1;
      }
      return i;
    }
    int j;
    if (paramInt < getFirstChildPosition())
    {
      j = 1;
      if (j == this.mShouldReverseLayout) {
        break label45;
      }
    }
    for (;;)
    {
      return i;
      j = 0;
      break;
      label45:
      i = 1;
    }
  }
  
  private boolean checkSpanForGap(Span paramSpan)
  {
    if (this.mShouldReverseLayout)
    {
      if (paramSpan.getEndLine() >= this.mPrimaryOrientation.getEndAfterPadding()) {
        break label91;
      }
      if (paramSpan.getLayoutParams((View)paramSpan.mViews.get(-1 + paramSpan.mViews.size())).mFullSpan) {}
    }
    do
    {
      return true;
      return false;
      if (paramSpan.getStartLine() <= this.mPrimaryOrientation.getStartAfterPadding()) {
        break;
      }
    } while (!paramSpan.getLayoutParams((View)paramSpan.mViews.get(0)).mFullSpan);
    return false;
    label91:
    return false;
  }
  
  private int computeScrollExtent(RecyclerView.State paramState)
  {
    boolean bool1 = true;
    if (getChildCount() == 0) {
      return 0;
    }
    OrientationHelper localOrientationHelper = this.mPrimaryOrientation;
    boolean bool2;
    View localView;
    if (!this.mSmoothScrollbarEnabled)
    {
      bool2 = bool1;
      localView = findFirstVisibleItemClosestToStart(bool2);
      if (this.mSmoothScrollbarEnabled) {
        break label65;
      }
    }
    for (;;)
    {
      return ScrollbarHelper.computeScrollExtent(paramState, localOrientationHelper, localView, findFirstVisibleItemClosestToEnd(bool1), this, this.mSmoothScrollbarEnabled);
      bool2 = false;
      break;
      label65:
      bool1 = false;
    }
  }
  
  private int computeScrollOffset(RecyclerView.State paramState)
  {
    boolean bool1 = true;
    if (getChildCount() == 0) {
      return 0;
    }
    OrientationHelper localOrientationHelper = this.mPrimaryOrientation;
    boolean bool2;
    View localView;
    if (!this.mSmoothScrollbarEnabled)
    {
      bool2 = bool1;
      localView = findFirstVisibleItemClosestToStart(bool2);
      if (this.mSmoothScrollbarEnabled) {
        break label69;
      }
    }
    for (;;)
    {
      return ScrollbarHelper.computeScrollOffset(paramState, localOrientationHelper, localView, findFirstVisibleItemClosestToEnd(bool1), this, this.mSmoothScrollbarEnabled, this.mShouldReverseLayout);
      bool2 = false;
      break;
      label69:
      bool1 = false;
    }
  }
  
  private int computeScrollRange(RecyclerView.State paramState)
  {
    boolean bool1 = true;
    if (getChildCount() == 0) {
      return 0;
    }
    OrientationHelper localOrientationHelper = this.mPrimaryOrientation;
    boolean bool2;
    View localView;
    if (!this.mSmoothScrollbarEnabled)
    {
      bool2 = bool1;
      localView = findFirstVisibleItemClosestToStart(bool2);
      if (this.mSmoothScrollbarEnabled) {
        break label65;
      }
    }
    for (;;)
    {
      return ScrollbarHelper.computeScrollRange(paramState, localOrientationHelper, localView, findFirstVisibleItemClosestToEnd(bool1), this, this.mSmoothScrollbarEnabled);
      bool2 = false;
      break;
      label65:
      bool1 = false;
    }
  }
  
  private int convertFocusDirectionToLayoutDirection(int paramInt)
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
  
  private StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem createFullSpanItemFromEnd(int paramInt)
  {
    StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem localFullSpanItem = new StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem();
    localFullSpanItem.mGapPerSpan = new int[this.mSpanCount];
    for (int i = 0; i < this.mSpanCount; i++) {
      localFullSpanItem.mGapPerSpan[i] = (paramInt - this.mSpans[i].getEndLine(paramInt));
    }
    return localFullSpanItem;
  }
  
  private StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem createFullSpanItemFromStart(int paramInt)
  {
    StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem localFullSpanItem = new StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem();
    localFullSpanItem.mGapPerSpan = new int[this.mSpanCount];
    for (int i = 0; i < this.mSpanCount; i++) {
      localFullSpanItem.mGapPerSpan[i] = (this.mSpans[i].getStartLine(paramInt) - paramInt);
    }
    return localFullSpanItem;
  }
  
  private void createOrientationHelpers()
  {
    this.mPrimaryOrientation = OrientationHelper.createOrientationHelper(this, this.mOrientation);
    this.mSecondaryOrientation = OrientationHelper.createOrientationHelper(this, 1 - this.mOrientation);
  }
  
  private int fill(RecyclerView.Recycler paramRecycler, LayoutState paramLayoutState, RecyclerView.State paramState)
  {
    this.mRemainingSpans.set(0, this.mSpanCount, true);
    int i;
    int j;
    label62:
    int k;
    label65:
    View localView;
    LayoutParams localLayoutParams;
    int i1;
    int i2;
    int i3;
    label137:
    Span localSpan;
    label158:
    label169:
    label190:
    int i5;
    label223:
    int i4;
    int i7;
    label347:
    int i6;
    if (this.mLayoutState.mInfinite) {
      if (paramLayoutState.mLayoutDirection == 1)
      {
        i = Integer.MAX_VALUE;
        updateAllRemainingSpans(paramLayoutState.mLayoutDirection, i);
        if (!this.mShouldReverseLayout) {
          break label495;
        }
        j = this.mPrimaryOrientation.getEndAfterPadding();
        k = 0;
        if ((!paramLayoutState.hasMore(paramState)) || ((!this.mLayoutState.mInfinite) && (this.mRemainingSpans.isEmpty()))) {
          break label865;
        }
        localView = paramLayoutState.next(paramRecycler);
        localLayoutParams = (LayoutParams)localView.getLayoutParams();
        i1 = localLayoutParams.getViewLayoutPosition();
        i2 = this.mLazySpanLookup.getSpan(i1);
        if (i2 != -1) {
          break label507;
        }
        i3 = 1;
        if (i3 == 0) {
          break label523;
        }
        if (!localLayoutParams.mFullSpan) {
          break label513;
        }
        localSpan = this.mSpans[0];
        this.mLazySpanLookup.setSpan(i1, localSpan);
        localLayoutParams.mSpan = localSpan;
        if (paramLayoutState.mLayoutDirection != 1) {
          break label535;
        }
        addView(localView);
        measureChildWithDecorationsAndMargin(localView, localLayoutParams, false);
        if (paramLayoutState.mLayoutDirection != 1) {
          break label557;
        }
        if (!localLayoutParams.mFullSpan) {
          break label545;
        }
        i5 = getMaxEnd(j);
        i4 = i5 + this.mPrimaryOrientation.getDecoratedMeasurement(localView);
        if ((i3 != 0) && (localLayoutParams.mFullSpan))
        {
          StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem localFullSpanItem3 = createFullSpanItemFromEnd(i5);
          localFullSpanItem3.mGapDir = -1;
          localFullSpanItem3.mPosition = i1;
          this.mLazySpanLookup.addFullSpanItem(localFullSpanItem3);
        }
        if ((localLayoutParams.mFullSpan) && (paramLayoutState.mItemDirection == -1))
        {
          if (i3 == 0) {
            break label645;
          }
          this.mLaidOutInvalidFullSpan = true;
        }
        attachViewToSpans(localView, localLayoutParams, paramLayoutState);
        if ((!isLayoutRTL()) || (this.mOrientation != 1)) {
          break label753;
        }
        if (!localLayoutParams.mFullSpan) {
          break label723;
        }
        i7 = this.mSecondaryOrientation.getEndAfterPadding();
        i6 = i7 - this.mSecondaryOrientation.getDecoratedMeasurement(localView);
        if (this.mOrientation != 1) {
          break label810;
        }
        layoutDecoratedWithMargins(localView, i6, i5, i7, i4);
        label383:
        if (!localLayoutParams.mFullSpan) {
          break label827;
        }
        updateAllRemainingSpans(this.mLayoutState.mLayoutDirection, i);
        label404:
        recycle(paramRecycler, this.mLayoutState);
        if ((this.mLayoutState.mStopInFocusable) && (localView.hasFocusable()))
        {
          if (!localLayoutParams.mFullSpan) {
            break label849;
          }
          this.mRemainingSpans.clear();
        }
      }
    }
    for (;;)
    {
      k = 1;
      break label65;
      i = Integer.MIN_VALUE;
      break;
      if (paramLayoutState.mLayoutDirection == 1)
      {
        i = paramLayoutState.mEndLine + paramLayoutState.mAvailable;
        break;
      }
      i = paramLayoutState.mStartLine - paramLayoutState.mAvailable;
      break;
      label495:
      j = this.mPrimaryOrientation.getStartAfterPadding();
      break label62;
      label507:
      i3 = 0;
      break label137;
      label513:
      localSpan = getNextSpan(paramLayoutState);
      break label158;
      label523:
      localSpan = this.mSpans[i2];
      break label169;
      label535:
      addView(localView, 0);
      break label190;
      label545:
      i5 = localSpan.getEndLine(j);
      break label223;
      label557:
      if (localLayoutParams.mFullSpan) {}
      for (i4 = getMinStart(j);; i4 = localSpan.getStartLine(j))
      {
        i5 = i4 - this.mPrimaryOrientation.getDecoratedMeasurement(localView);
        if ((i3 == 0) || (!localLayoutParams.mFullSpan)) {
          break;
        }
        StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem localFullSpanItem1 = createFullSpanItemFromStart(i4);
        localFullSpanItem1.mGapDir = 1;
        localFullSpanItem1.mPosition = i1;
        this.mLazySpanLookup.addFullSpanItem(localFullSpanItem1);
        break;
      }
      label645:
      if (paramLayoutState.mLayoutDirection == 1)
      {
        if (!areAllEndsEqual()) {}
        for (i9 = 1;; i9 = 0)
        {
          label663:
          if (i9 == 0) {
            break label715;
          }
          StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem localFullSpanItem2 = this.mLazySpanLookup.getFullSpanItem(i1);
          if (localFullSpanItem2 != null) {
            localFullSpanItem2.mHasUnwantedGapAfter = true;
          }
          this.mLaidOutInvalidFullSpan = true;
          break;
        }
      }
      if (!areAllStartsEqual()) {}
      for (int i9 = 1;; i9 = 0)
      {
        break label663;
        label715:
        break;
      }
      label723:
      i7 = this.mSecondaryOrientation.getEndAfterPadding() - (-1 + this.mSpanCount - localSpan.mIndex) * this.mSizePerSpan;
      break label347;
      label753:
      if (localLayoutParams.mFullSpan) {}
      for (i6 = this.mSecondaryOrientation.getStartAfterPadding();; i6 = localSpan.mIndex * this.mSizePerSpan + this.mSecondaryOrientation.getStartAfterPadding())
      {
        i7 = i6 + this.mSecondaryOrientation.getDecoratedMeasurement(localView);
        break;
      }
      label810:
      layoutDecoratedWithMargins(localView, i5, i6, i4, i7);
      break label383;
      label827:
      int i8 = this.mLayoutState.mLayoutDirection;
      updateRemainingSpans(localSpan, i8, i);
      break label404;
      label849:
      this.mRemainingSpans.set(localSpan.mIndex, false);
    }
    label865:
    if (k == 0) {
      recycle(paramRecycler, this.mLayoutState);
    }
    int n;
    if (this.mLayoutState.mLayoutDirection == -1) {
      n = getMinStart(this.mPrimaryOrientation.getStartAfterPadding());
    }
    for (int m = this.mPrimaryOrientation.getStartAfterPadding() - n; m > 0; m = getMaxEnd(this.mPrimaryOrientation.getEndAfterPadding()) - this.mPrimaryOrientation.getEndAfterPadding()) {
      return Math.min(paramLayoutState.mAvailable, m);
    }
    return 0;
  }
  
  private int findFirstReferenceChildPosition(int paramInt)
  {
    int i = getChildCount();
    for (int j = 0; j < i; j++)
    {
      int k = getPosition(getChildAt(j));
      if ((k >= 0) && (k < paramInt)) {
        return k;
      }
    }
    return 0;
  }
  
  private int findLastReferenceChildPosition(int paramInt)
  {
    for (int i = -1 + getChildCount(); i >= 0; i--)
    {
      int j = getPosition(getChildAt(i));
      if ((j >= 0) && (j < paramInt)) {
        return j;
      }
    }
    return 0;
  }
  
  private void fixEndGap(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, boolean paramBoolean)
  {
    int i = getMaxEnd(Integer.MIN_VALUE);
    if (i == Integer.MIN_VALUE) {}
    int k;
    do
    {
      int j;
      do
      {
        return;
        j = this.mPrimaryOrientation.getEndAfterPadding() - i;
      } while (j <= 0);
      k = j - -scrollBy(-j, paramRecycler, paramState);
    } while ((!paramBoolean) || (k <= 0));
    this.mPrimaryOrientation.offsetChildren(k);
  }
  
  private void fixStartGap(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, boolean paramBoolean)
  {
    int i = getMinStart(Integer.MAX_VALUE);
    if (i == Integer.MAX_VALUE) {}
    int k;
    do
    {
      int j;
      do
      {
        return;
        j = i - this.mPrimaryOrientation.getStartAfterPadding();
      } while (j <= 0);
      k = j - scrollBy(j, paramRecycler, paramState);
    } while ((!paramBoolean) || (k <= 0));
    this.mPrimaryOrientation.offsetChildren(-k);
  }
  
  private int getMaxEnd(int paramInt)
  {
    int i = this.mSpans[0].getEndLine(paramInt);
    for (int j = 1; j < this.mSpanCount; j++)
    {
      int k = this.mSpans[j].getEndLine(paramInt);
      if (k > i) {
        i = k;
      }
    }
    return i;
  }
  
  private int getMaxStart(int paramInt)
  {
    int i = this.mSpans[0].getStartLine(paramInt);
    for (int j = 1; j < this.mSpanCount; j++)
    {
      int k = this.mSpans[j].getStartLine(paramInt);
      if (k > i) {
        i = k;
      }
    }
    return i;
  }
  
  private int getMinEnd(int paramInt)
  {
    int i = this.mSpans[0].getEndLine(paramInt);
    for (int j = 1; j < this.mSpanCount; j++)
    {
      int k = this.mSpans[j].getEndLine(paramInt);
      if (k < i) {
        i = k;
      }
    }
    return i;
  }
  
  private int getMinStart(int paramInt)
  {
    int i = this.mSpans[0].getStartLine(paramInt);
    for (int j = 1; j < this.mSpanCount; j++)
    {
      int k = this.mSpans[j].getStartLine(paramInt);
      if (k < i) {
        i = k;
      }
    }
    return i;
  }
  
  private Span getNextSpan(LayoutState paramLayoutState)
  {
    int k;
    int i;
    int j;
    if (preferLastSpan(paramLayoutState.mLayoutDirection))
    {
      k = -1 + this.mSpanCount;
      i = -1;
      j = -1;
    }
    while (paramLayoutState.mLayoutDirection == 1)
    {
      localObject2 = null;
      int i3 = Integer.MAX_VALUE;
      int i4 = this.mPrimaryOrientation.getStartAfterPadding();
      int i5 = k;
      while (i5 != i)
      {
        Span localSpan2 = this.mSpans[i5];
        int i6 = localSpan2.getEndLine(i4);
        if (i6 < i3)
        {
          localObject2 = localSpan2;
          i3 = i6;
        }
        i5 += j;
      }
      i = this.mSpanCount;
      j = 1;
      k = 0;
    }
    Object localObject1 = null;
    int m = Integer.MIN_VALUE;
    int n = this.mPrimaryOrientation.getEndAfterPadding();
    int i1 = k;
    while (i1 != i)
    {
      Span localSpan1 = this.mSpans[i1];
      int i2 = localSpan1.getStartLine(n);
      if (i2 > m)
      {
        localObject1 = localSpan1;
        m = i2;
      }
      i1 += j;
    }
    Object localObject2 = localObject1;
    return localObject2;
  }
  
  private void handleUpdate(int paramInt1, int paramInt2, int paramInt3)
  {
    int i;
    int k;
    int j;
    if (this.mShouldReverseLayout)
    {
      i = getLastChildPosition();
      if (paramInt3 != 8) {
        break label104;
      }
      if (paramInt1 >= paramInt2) {
        break label93;
      }
      k = paramInt2 + 1;
      j = paramInt1;
      label32:
      this.mLazySpanLookup.invalidateAfter(j);
      switch (paramInt3)
      {
      default: 
        label76:
        if (k > i) {
          break;
        }
      }
    }
    for (;;)
    {
      return;
      i = getFirstChildPosition();
      break;
      label93:
      k = paramInt1 + 1;
      j = paramInt2;
      break label32;
      label104:
      j = paramInt1;
      k = paramInt1 + paramInt2;
      break label32;
      this.mLazySpanLookup.offsetForAddition(paramInt1, paramInt2);
      break label76;
      this.mLazySpanLookup.offsetForRemoval(paramInt1, paramInt2);
      break label76;
      this.mLazySpanLookup.offsetForRemoval(paramInt1, 1);
      this.mLazySpanLookup.offsetForAddition(paramInt2, 1);
      break label76;
      if (this.mShouldReverseLayout) {}
      for (int m = getFirstChildPosition(); j <= m; m = getLastChildPosition())
      {
        requestLayout();
        return;
      }
    }
  }
  
  private void measureChildWithDecorationsAndMargin(View paramView, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    calculateItemDecorationsForChild(paramView, this.mTmpRect);
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    int i = updateSpecWithExtra(paramInt1, localLayoutParams.leftMargin + this.mTmpRect.left, localLayoutParams.rightMargin + this.mTmpRect.right);
    int j = updateSpecWithExtra(paramInt2, localLayoutParams.topMargin + this.mTmpRect.top, localLayoutParams.bottomMargin + this.mTmpRect.bottom);
    if (paramBoolean) {}
    for (boolean bool = shouldReMeasureChild(paramView, i, j, localLayoutParams);; bool = shouldMeasureChild(paramView, i, j, localLayoutParams))
    {
      if (bool) {
        paramView.measure(i, j);
      }
      return;
    }
  }
  
  private void measureChildWithDecorationsAndMargin(View paramView, LayoutParams paramLayoutParams, boolean paramBoolean)
  {
    if (paramLayoutParams.mFullSpan)
    {
      if (this.mOrientation == 1)
      {
        measureChildWithDecorationsAndMargin(paramView, this.mFullSizeSpec, getChildMeasureSpec(getHeight(), getHeightMode(), 0, paramLayoutParams.height, true), paramBoolean);
        return;
      }
      measureChildWithDecorationsAndMargin(paramView, getChildMeasureSpec(getWidth(), getWidthMode(), 0, paramLayoutParams.width, true), this.mFullSizeSpec, paramBoolean);
      return;
    }
    if (this.mOrientation == 1)
    {
      measureChildWithDecorationsAndMargin(paramView, getChildMeasureSpec(this.mSizePerSpan, getWidthMode(), 0, paramLayoutParams.width, false), getChildMeasureSpec(getHeight(), getHeightMode(), 0, paramLayoutParams.height, true), paramBoolean);
      return;
    }
    measureChildWithDecorationsAndMargin(paramView, getChildMeasureSpec(getWidth(), getWidthMode(), 0, paramLayoutParams.width, true), getChildMeasureSpec(this.mSizePerSpan, getHeightMode(), 0, paramLayoutParams.height, false), paramBoolean);
  }
  
  private void onLayoutChildren(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, boolean paramBoolean)
  {
    int i = 1;
    AnchorInfo localAnchorInfo = this.mAnchorInfo;
    if (((this.mPendingSavedState != null) || (this.mPendingScrollPosition != -1)) && (paramState.getItemCount() == 0))
    {
      removeAndRecycleAllViews(paramRecycler);
      localAnchorInfo.reset();
      return;
    }
    if ((!localAnchorInfo.mValid) || (this.mPendingScrollPosition != -1) || (this.mPendingSavedState != null))
    {
      int k = i;
      if (k != 0)
      {
        localAnchorInfo.reset();
        if (this.mPendingSavedState == null) {
          break label251;
        }
        applyPendingSavedState(localAnchorInfo);
      }
    }
    int m;
    for (;;)
    {
      updateAnchorInfoForLayout(paramState, localAnchorInfo);
      localAnchorInfo.mValid = i;
      if ((this.mPendingSavedState == null) && (this.mPendingScrollPosition == -1) && ((localAnchorInfo.mLayoutFromEnd != this.mLastLayoutFromEnd) || (isLayoutRTL() != this.mLastLayoutRTL)))
      {
        this.mLazySpanLookup.clear();
        localAnchorInfo.mInvalidateOffsets = i;
      }
      if ((getChildCount() <= 0) || ((this.mPendingSavedState != null) && (this.mPendingSavedState.mSpanOffsetsSize >= i))) {
        break label330;
      }
      if (!localAnchorInfo.mInvalidateOffsets) {
        break label267;
      }
      for (int i3 = 0; i3 < this.mSpanCount; i3++)
      {
        this.mSpans[i3].clear();
        if (localAnchorInfo.mOffset != Integer.MIN_VALUE) {
          this.mSpans[i3].setLine(localAnchorInfo.mOffset);
        }
      }
      m = 0;
      break;
      label251:
      resolveShouldLayoutReverse();
      localAnchorInfo.mLayoutFromEnd = this.mShouldReverseLayout;
    }
    label267:
    label330:
    label430:
    label463:
    int n;
    if ((m != 0) || (this.mAnchorInfo.mSpanReferenceLines == null))
    {
      for (int i1 = 0; i1 < this.mSpanCount; i1++) {
        this.mSpans[i1].cacheReferenceLineAndClear(this.mShouldReverseLayout, localAnchorInfo.mOffset);
      }
      this.mAnchorInfo.saveSpanReferenceLines(this.mSpans);
      detachAndScrapAttachedViews(paramRecycler);
      this.mLayoutState.mRecycle = false;
      this.mLaidOutInvalidFullSpan = false;
      updateMeasureSpecs(this.mSecondaryOrientation.getTotalSpace());
      updateLayoutState(localAnchorInfo.mPosition, paramState);
      if (!localAnchorInfo.mLayoutFromEnd) {
        break label644;
      }
      setLayoutStateDirection(-1);
      fill(paramRecycler, this.mLayoutState, paramState);
      setLayoutStateDirection(i);
      this.mLayoutState.mCurrentPosition = (localAnchorInfo.mPosition + this.mLayoutState.mItemDirection);
      fill(paramRecycler, this.mLayoutState, paramState);
      repositionToWrapContentIfNecessary();
      if (getChildCount() > 0)
      {
        if (!this.mShouldReverseLayout) {
          break label700;
        }
        fixEndGap(paramRecycler, paramState, i);
        fixStartGap(paramRecycler, paramState, false);
      }
      n = 0;
      if (paramBoolean)
      {
        boolean bool1 = paramState.isPreLayout();
        n = 0;
        if (!bool1) {
          if ((this.mGapStrategy == 0) || (getChildCount() <= 0) || ((!this.mLaidOutInvalidFullSpan) && (hasGapsToFix() == null))) {
            break label718;
          }
        }
      }
    }
    for (;;)
    {
      n = 0;
      if (i != 0)
      {
        removeCallbacks(this.mCheckForGapsRunnable);
        boolean bool2 = checkForGaps();
        n = 0;
        if (bool2) {
          n = 1;
        }
      }
      if (paramState.isPreLayout()) {
        this.mAnchorInfo.reset();
      }
      this.mLastLayoutFromEnd = localAnchorInfo.mLayoutFromEnd;
      this.mLastLayoutRTL = isLayoutRTL();
      if (n == 0) {
        break;
      }
      this.mAnchorInfo.reset();
      onLayoutChildren(paramRecycler, paramState, false);
      return;
      for (int i2 = 0; i2 < this.mSpanCount; i2++)
      {
        Span localSpan = this.mSpans[i2];
        localSpan.clear();
        localSpan.setLine(this.mAnchorInfo.mSpanReferenceLines[i2]);
      }
      break label330;
      label644:
      setLayoutStateDirection(i);
      fill(paramRecycler, this.mLayoutState, paramState);
      setLayoutStateDirection(-1);
      this.mLayoutState.mCurrentPosition = (localAnchorInfo.mPosition + this.mLayoutState.mItemDirection);
      fill(paramRecycler, this.mLayoutState, paramState);
      break label430;
      label700:
      fixStartGap(paramRecycler, paramState, i);
      fixEndGap(paramRecycler, paramState, false);
      break label463;
      label718:
      int j = 0;
    }
  }
  
  private boolean preferLastSpan(int paramInt)
  {
    int k;
    if (this.mOrientation == 0) {
      if (paramInt == -1)
      {
        k = 1;
        if (k == this.mShouldReverseLayout) {
          break label32;
        }
      }
    }
    label32:
    label66:
    label69:
    for (;;)
    {
      return true;
      k = 0;
      break;
      return false;
      int i;
      if (paramInt == -1)
      {
        i = 1;
        if (i != this.mShouldReverseLayout) {
          break label66;
        }
      }
      for (int j = 1;; j = 0)
      {
        if (j == isLayoutRTL()) {
          break label69;
        }
        return false;
        i = 0;
        break;
      }
    }
  }
  
  private void prependViewToAllSpans(View paramView)
  {
    for (int i = -1 + this.mSpanCount; i >= 0; i--) {
      this.mSpans[i].prependToSpan(paramView);
    }
  }
  
  private void recycle(RecyclerView.Recycler paramRecycler, LayoutState paramLayoutState)
  {
    if ((!paramLayoutState.mRecycle) || (paramLayoutState.mInfinite)) {
      return;
    }
    if (paramLayoutState.mAvailable == 0)
    {
      if (paramLayoutState.mLayoutDirection == -1)
      {
        recycleFromEnd(paramRecycler, paramLayoutState.mEndLine);
        return;
      }
      recycleFromStart(paramRecycler, paramLayoutState.mStartLine);
      return;
    }
    if (paramLayoutState.mLayoutDirection == -1)
    {
      int k = paramLayoutState.mStartLine - getMaxStart(paramLayoutState.mStartLine);
      if (k < 0) {}
      for (int m = paramLayoutState.mEndLine;; m = paramLayoutState.mEndLine - Math.min(k, paramLayoutState.mAvailable))
      {
        recycleFromEnd(paramRecycler, m);
        return;
      }
    }
    int i = getMinEnd(paramLayoutState.mEndLine) - paramLayoutState.mEndLine;
    if (i < 0) {}
    for (int j = paramLayoutState.mStartLine;; j = paramLayoutState.mStartLine + Math.min(i, paramLayoutState.mAvailable))
    {
      recycleFromStart(paramRecycler, j);
      return;
    }
  }
  
  private void recycleFromEnd(RecyclerView.Recycler paramRecycler, int paramInt)
  {
    for (int i = -1 + getChildCount();; i--)
    {
      View localView;
      LayoutParams localLayoutParams;
      if (i >= 0)
      {
        localView = getChildAt(i);
        if ((this.mPrimaryOrientation.getDecoratedStart(localView) >= paramInt) && (this.mPrimaryOrientation.getTransformedStartWithDecoration(localView) >= paramInt))
        {
          localLayoutParams = (LayoutParams)localView.getLayoutParams();
          if (!localLayoutParams.mFullSpan) {
            break label126;
          }
          j = 0;
          if (j >= this.mSpanCount) {
            break label98;
          }
          if (this.mSpans[j].mViews.size() != 1) {
            break label92;
          }
        }
      }
      label92:
      label98:
      label126:
      while (localLayoutParams.mSpan.mViews.size() == 1)
      {
        for (;;)
        {
          int j;
          return;
          j++;
        }
        for (int k = 0; k < this.mSpanCount; k++) {
          this.mSpans[k].popEnd();
        }
      }
      localLayoutParams.mSpan.popEnd();
      removeAndRecycleView(localView, paramRecycler);
    }
  }
  
  private void recycleFromStart(RecyclerView.Recycler paramRecycler, int paramInt)
  {
    for (;;)
    {
      View localView;
      LayoutParams localLayoutParams;
      if (getChildCount() > 0)
      {
        localView = getChildAt(0);
        if ((this.mPrimaryOrientation.getDecoratedEnd(localView) <= paramInt) && (this.mPrimaryOrientation.getTransformedEndWithDecoration(localView) <= paramInt))
        {
          localLayoutParams = (LayoutParams)localView.getLayoutParams();
          if (!localLayoutParams.mFullSpan) {
            break label118;
          }
          i = 0;
          if (i >= this.mSpanCount) {
            break label90;
          }
          if (this.mSpans[i].mViews.size() != 1) {
            break label84;
          }
        }
      }
      label84:
      label90:
      label118:
      while (localLayoutParams.mSpan.mViews.size() == 1)
      {
        for (;;)
        {
          int i;
          return;
          i++;
        }
        for (int j = 0; j < this.mSpanCount; j++) {
          this.mSpans[j].popStart();
        }
      }
      localLayoutParams.mSpan.popStart();
      removeAndRecycleView(localView, paramRecycler);
    }
  }
  
  private void repositionToWrapContentIfNecessary()
  {
    if (this.mSecondaryOrientation.getMode() == 1073741824) {}
    int i;
    int k;
    do
    {
      return;
      float f1 = 0.0F;
      i = getChildCount();
      int j = 0;
      if (j < i)
      {
        View localView2 = getChildAt(j);
        float f2 = this.mSecondaryOrientation.getDecoratedMeasurement(localView2);
        if (f2 < f1) {}
        for (;;)
        {
          j++;
          break;
          if (((LayoutParams)localView2.getLayoutParams()).isFullSpan()) {
            f2 = 1.0F * f2 / this.mSpanCount;
          }
          f1 = Math.max(f1, f2);
        }
      }
      k = this.mSizePerSpan;
      int m = Math.round(f1 * this.mSpanCount);
      if (this.mSecondaryOrientation.getMode() == Integer.MIN_VALUE) {
        m = Math.min(m, this.mSecondaryOrientation.getTotalSpace());
      }
      updateMeasureSpecs(m);
    } while (this.mSizePerSpan == k);
    int n = 0;
    label158:
    View localView1;
    LayoutParams localLayoutParams;
    if (n < i)
    {
      localView1 = getChildAt(n);
      localLayoutParams = (LayoutParams)localView1.getLayoutParams();
      if (!localLayoutParams.mFullSpan) {
        break label196;
      }
    }
    for (;;)
    {
      n++;
      break label158;
      break;
      label196:
      if ((isLayoutRTL()) && (this.mOrientation == 1))
      {
        localView1.offsetLeftAndRight(-(-1 + this.mSpanCount - localLayoutParams.mSpan.mIndex) * this.mSizePerSpan - k * -(-1 + this.mSpanCount - localLayoutParams.mSpan.mIndex));
      }
      else
      {
        int i1 = localLayoutParams.mSpan.mIndex * this.mSizePerSpan;
        int i2 = k * localLayoutParams.mSpan.mIndex;
        if (this.mOrientation == 1) {
          localView1.offsetLeftAndRight(i1 - i2);
        } else {
          localView1.offsetTopAndBottom(i1 - i2);
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
  
  private void setLayoutStateDirection(int paramInt)
  {
    int i = 1;
    this.mLayoutState.mLayoutDirection = paramInt;
    LayoutState localLayoutState = this.mLayoutState;
    int j = this.mShouldReverseLayout;
    if (paramInt == -1)
    {
      int k = i;
      if (j != k) {
        break label48;
      }
    }
    for (;;)
    {
      localLayoutState.mItemDirection = i;
      return;
      int m = 0;
      break;
      label48:
      i = -1;
    }
  }
  
  private void updateAllRemainingSpans(int paramInt1, int paramInt2)
  {
    int i = 0;
    if (i < this.mSpanCount)
    {
      if (this.mSpans[i].mViews.isEmpty()) {}
      for (;;)
      {
        i++;
        break;
        updateRemainingSpans(this.mSpans[i], paramInt1, paramInt2);
      }
    }
  }
  
  private boolean updateAnchorFromChildren(RecyclerView.State paramState, AnchorInfo paramAnchorInfo)
  {
    if (this.mLastLayoutFromEnd) {}
    for (int i = findLastReferenceChildPosition(paramState.getItemCount());; i = findFirstReferenceChildPosition(paramState.getItemCount()))
    {
      paramAnchorInfo.mPosition = i;
      paramAnchorInfo.mOffset = Integer.MIN_VALUE;
      return true;
    }
  }
  
  private void updateLayoutState(int paramInt, RecyclerView.State paramState)
  {
    boolean bool1 = true;
    this.mLayoutState.mAvailable = 0;
    this.mLayoutState.mCurrentPosition = paramInt;
    boolean bool2 = isSmoothScrolling();
    int i = 0;
    int j = 0;
    boolean bool4;
    if (bool2)
    {
      int k = paramState.getTargetScrollPosition();
      i = 0;
      j = 0;
      if (k != -1)
      {
        boolean bool3 = this.mShouldReverseLayout;
        if (k >= paramInt) {
          break label174;
        }
        bool4 = bool1;
        if (bool3 != bool4) {
          break label180;
        }
        i = this.mPrimaryOrientation.getTotalSpace();
      }
    }
    label84:
    label125:
    LayoutState localLayoutState;
    if (getClipToPadding())
    {
      this.mLayoutState.mStartLine = (this.mPrimaryOrientation.getStartAfterPadding() - j);
      this.mLayoutState.mEndLine = (i + this.mPrimaryOrientation.getEndAfterPadding());
      this.mLayoutState.mStopInFocusable = false;
      this.mLayoutState.mRecycle = bool1;
      localLayoutState = this.mLayoutState;
      if ((this.mPrimaryOrientation.getMode() != 0) || (this.mPrimaryOrientation.getEnd() != 0)) {
        break label225;
      }
    }
    for (;;)
    {
      localLayoutState.mInfinite = bool1;
      return;
      label174:
      bool4 = false;
      break;
      label180:
      j = this.mPrimaryOrientation.getTotalSpace();
      i = 0;
      break label84;
      this.mLayoutState.mEndLine = (i + this.mPrimaryOrientation.getEnd());
      this.mLayoutState.mStartLine = (-j);
      break label125;
      label225:
      bool1 = false;
    }
  }
  
  private void updateRemainingSpans(Span paramSpan, int paramInt1, int paramInt2)
  {
    int i = paramSpan.getDeletedSize();
    if (paramInt1 == -1) {
      if (i + paramSpan.getStartLine() <= paramInt2) {
        this.mRemainingSpans.set(paramSpan.mIndex, false);
      }
    }
    while (paramSpan.getEndLine() - i < paramInt2) {
      return;
    }
    this.mRemainingSpans.set(paramSpan.mIndex, false);
  }
  
  private int updateSpecWithExtra(int paramInt1, int paramInt2, int paramInt3)
  {
    if ((paramInt2 == 0) && (paramInt3 == 0)) {}
    int i;
    do
    {
      return paramInt1;
      i = View.MeasureSpec.getMode(paramInt1);
    } while ((i != Integer.MIN_VALUE) && (i != 1073741824));
    return View.MeasureSpec.makeMeasureSpec(Math.max(0, View.MeasureSpec.getSize(paramInt1) - paramInt2 - paramInt3), i);
  }
  
  boolean areAllEndsEqual()
  {
    int i = this.mSpans[0].getEndLine(Integer.MIN_VALUE);
    for (int j = 1; j < this.mSpanCount; j++) {
      if (this.mSpans[j].getEndLine(Integer.MIN_VALUE) != i) {
        return false;
      }
    }
    return true;
  }
  
  boolean areAllStartsEqual()
  {
    int i = this.mSpans[0].getStartLine(Integer.MIN_VALUE);
    for (int j = 1; j < this.mSpanCount; j++) {
      if (this.mSpans[j].getStartLine(Integer.MIN_VALUE) != i) {
        return false;
      }
    }
    return true;
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
  
  boolean checkForGaps()
  {
    if ((getChildCount() == 0) || (this.mGapStrategy == 0) || (!isAttachedToWindow())) {
      return false;
    }
    int i;
    if (this.mShouldReverseLayout) {
      i = getLastChildPosition();
    }
    for (int j = getFirstChildPosition(); (i == 0) && (hasGapsToFix() != null); j = getLastChildPosition())
    {
      this.mLazySpanLookup.clear();
      requestSimpleAnimationsInNextLayout();
      requestLayout();
      return true;
      i = getFirstChildPosition();
    }
    if (!this.mLaidOutInvalidFullSpan) {
      return false;
    }
    if (this.mShouldReverseLayout) {}
    StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem localFullSpanItem1;
    for (int k = -1;; k = 1)
    {
      localFullSpanItem1 = this.mLazySpanLookup.getFirstFullSpanItemInRange(i, j + 1, k, true);
      if (localFullSpanItem1 != null) {
        break;
      }
      this.mLaidOutInvalidFullSpan = false;
      this.mLazySpanLookup.forceInvalidateAfter(j + 1);
      return false;
    }
    StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem localFullSpanItem2 = this.mLazySpanLookup.getFirstFullSpanItemInRange(i, localFullSpanItem1.mPosition, k * -1, true);
    if (localFullSpanItem2 == null) {
      this.mLazySpanLookup.forceInvalidateAfter(localFullSpanItem1.mPosition);
    }
    for (;;)
    {
      requestSimpleAnimationsInNextLayout();
      requestLayout();
      return true;
      this.mLazySpanLookup.forceInvalidateAfter(1 + localFullSpanItem2.mPosition);
    }
  }
  
  public boolean checkLayoutParams(RecyclerView.LayoutParams paramLayoutParams)
  {
    return paramLayoutParams instanceof LayoutParams;
  }
  
  public void collectAdjacentPrefetchPositions(int paramInt1, int paramInt2, RecyclerView.State paramState, RecyclerView.LayoutManager.LayoutPrefetchRegistry paramLayoutPrefetchRegistry)
  {
    int i;
    if (this.mOrientation == 0)
    {
      i = paramInt1;
      if ((getChildCount() != 0) && (i != 0)) {
        break label29;
      }
    }
    for (;;)
    {
      return;
      i = paramInt2;
      break;
      label29:
      prepareLayoutStateForDelta(i, paramState);
      if ((this.mPrefetchDistances == null) || (this.mPrefetchDistances.length < this.mSpanCount)) {
        this.mPrefetchDistances = new int[this.mSpanCount];
      }
      int j = 0;
      int k = 0;
      if (k < this.mSpanCount)
      {
        if (this.mLayoutState.mItemDirection == -1) {}
        for (int n = this.mLayoutState.mStartLine - this.mSpans[k].getStartLine(this.mLayoutState.mStartLine);; n = this.mSpans[k].getEndLine(this.mLayoutState.mEndLine) - this.mLayoutState.mEndLine)
        {
          if (n >= 0)
          {
            this.mPrefetchDistances[j] = n;
            j++;
          }
          k++;
          break;
        }
      }
      Arrays.sort(this.mPrefetchDistances, 0, j);
      for (int m = 0; (m < j) && (this.mLayoutState.hasMore(paramState)); m++)
      {
        paramLayoutPrefetchRegistry.addPosition(this.mLayoutState.mCurrentPosition, this.mPrefetchDistances[m]);
        LayoutState localLayoutState = this.mLayoutState;
        localLayoutState.mCurrentPosition += this.mLayoutState.mItemDirection;
      }
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
    int i = calculateScrollDirectionForPosition(paramInt);
    PointF localPointF = new PointF();
    if (i == 0) {
      return null;
    }
    if (this.mOrientation == 0)
    {
      localPointF.x = i;
      localPointF.y = 0.0F;
      return localPointF;
    }
    localPointF.x = 0.0F;
    localPointF.y = i;
    return localPointF;
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
  
  public int[] findFirstCompletelyVisibleItemPositions(int[] paramArrayOfInt)
  {
    if (paramArrayOfInt == null) {
      paramArrayOfInt = new int[this.mSpanCount];
    }
    while (paramArrayOfInt.length >= this.mSpanCount) {
      for (int i = 0; i < this.mSpanCount; i++) {
        paramArrayOfInt[i] = this.mSpans[i].findFirstCompletelyVisibleItemPosition();
      }
    }
    throw new IllegalArgumentException("Provided int[]'s size must be more than or equal to span count. Expected:" + this.mSpanCount + ", array size:" + paramArrayOfInt.length);
    return paramArrayOfInt;
  }
  
  View findFirstVisibleItemClosestToEnd(boolean paramBoolean)
  {
    int i = this.mPrimaryOrientation.getStartAfterPadding();
    int j = this.mPrimaryOrientation.getEndAfterPadding();
    Object localObject = null;
    int k = -1 + getChildCount();
    if (k >= 0)
    {
      View localView = getChildAt(k);
      int m = this.mPrimaryOrientation.getDecoratedStart(localView);
      int n = this.mPrimaryOrientation.getDecoratedEnd(localView);
      if ((n <= i) || (m >= j)) {}
      for (;;)
      {
        k--;
        break;
        if ((n <= j) || (!paramBoolean)) {
          return localView;
        }
        if (localObject == null) {
          localObject = localView;
        }
      }
    }
    return localObject;
  }
  
  View findFirstVisibleItemClosestToStart(boolean paramBoolean)
  {
    int i = this.mPrimaryOrientation.getStartAfterPadding();
    int j = this.mPrimaryOrientation.getEndAfterPadding();
    int k = getChildCount();
    Object localObject = null;
    int m = 0;
    if (m < k)
    {
      View localView = getChildAt(m);
      int n = this.mPrimaryOrientation.getDecoratedStart(localView);
      if ((this.mPrimaryOrientation.getDecoratedEnd(localView) <= i) || (n >= j)) {}
      for (;;)
      {
        m++;
        break;
        if ((n >= i) || (!paramBoolean)) {
          return localView;
        }
        if (localObject == null) {
          localObject = localView;
        }
      }
    }
    return localObject;
  }
  
  int findFirstVisibleItemPositionInt()
  {
    if (this.mShouldReverseLayout) {}
    for (View localView = findFirstVisibleItemClosestToEnd(true); localView == null; localView = findFirstVisibleItemClosestToStart(true)) {
      return -1;
    }
    return getPosition(localView);
  }
  
  public int[] findFirstVisibleItemPositions(int[] paramArrayOfInt)
  {
    if (paramArrayOfInt == null) {
      paramArrayOfInt = new int[this.mSpanCount];
    }
    while (paramArrayOfInt.length >= this.mSpanCount) {
      for (int i = 0; i < this.mSpanCount; i++) {
        paramArrayOfInt[i] = this.mSpans[i].findFirstVisibleItemPosition();
      }
    }
    throw new IllegalArgumentException("Provided int[]'s size must be more than or equal to span count. Expected:" + this.mSpanCount + ", array size:" + paramArrayOfInt.length);
    return paramArrayOfInt;
  }
  
  public int[] findLastCompletelyVisibleItemPositions(int[] paramArrayOfInt)
  {
    if (paramArrayOfInt == null) {
      paramArrayOfInt = new int[this.mSpanCount];
    }
    while (paramArrayOfInt.length >= this.mSpanCount) {
      for (int i = 0; i < this.mSpanCount; i++) {
        paramArrayOfInt[i] = this.mSpans[i].findLastCompletelyVisibleItemPosition();
      }
    }
    throw new IllegalArgumentException("Provided int[]'s size must be more than or equal to span count. Expected:" + this.mSpanCount + ", array size:" + paramArrayOfInt.length);
    return paramArrayOfInt;
  }
  
  public int[] findLastVisibleItemPositions(int[] paramArrayOfInt)
  {
    if (paramArrayOfInt == null) {
      paramArrayOfInt = new int[this.mSpanCount];
    }
    while (paramArrayOfInt.length >= this.mSpanCount) {
      for (int i = 0; i < this.mSpanCount; i++) {
        paramArrayOfInt[i] = this.mSpans[i].findLastVisibleItemPosition();
      }
    }
    throw new IllegalArgumentException("Provided int[]'s size must be more than or equal to span count. Expected:" + this.mSpanCount + ", array size:" + paramArrayOfInt.length);
    return paramArrayOfInt;
  }
  
  public RecyclerView.LayoutParams generateDefaultLayoutParams()
  {
    if (this.mOrientation == 0) {
      return new LayoutParams(-2, -1);
    }
    return new LayoutParams(-1, -2);
  }
  
  public RecyclerView.LayoutParams generateLayoutParams(Context paramContext, AttributeSet paramAttributeSet)
  {
    return new LayoutParams(paramContext, paramAttributeSet);
  }
  
  public RecyclerView.LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    if ((paramLayoutParams instanceof ViewGroup.MarginLayoutParams)) {
      return new LayoutParams((ViewGroup.MarginLayoutParams)paramLayoutParams);
    }
    return new LayoutParams(paramLayoutParams);
  }
  
  public int getColumnCountForAccessibility(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    if (this.mOrientation == 1) {
      return this.mSpanCount;
    }
    return super.getColumnCountForAccessibility(paramRecycler, paramState);
  }
  
  int getFirstChildPosition()
  {
    if (getChildCount() == 0) {
      return 0;
    }
    return getPosition(getChildAt(0));
  }
  
  public int getGapStrategy()
  {
    return this.mGapStrategy;
  }
  
  int getLastChildPosition()
  {
    int i = getChildCount();
    if (i == 0) {
      return 0;
    }
    return getPosition(getChildAt(i - 1));
  }
  
  public int getOrientation()
  {
    return this.mOrientation;
  }
  
  public boolean getReverseLayout()
  {
    return this.mReverseLayout;
  }
  
  public int getRowCountForAccessibility(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    if (this.mOrientation == 0) {
      return this.mSpanCount;
    }
    return super.getRowCountForAccessibility(paramRecycler, paramState);
  }
  
  public int getSpanCount()
  {
    return this.mSpanCount;
  }
  
  View hasGapsToFix()
  {
    int i = -1 + getChildCount();
    BitSet localBitSet = new BitSet(this.mSpanCount);
    localBitSet.set(0, this.mSpanCount, true);
    int j;
    int m;
    int k;
    if ((this.mOrientation == 1) && (isLayoutRTL()))
    {
      j = 1;
      if (!this.mShouldReverseLayout) {
        break label135;
      }
      m = i;
      k = 0 - 1;
      label61:
      if (m >= k) {
        break label146;
      }
    }
    int i1;
    View localView1;
    LayoutParams localLayoutParams1;
    label127:
    label135:
    label146:
    for (int n = 1;; n = -1)
    {
      i1 = m;
      if (i1 == k) {
        break label364;
      }
      localView1 = getChildAt(i1);
      localLayoutParams1 = (LayoutParams)localView1.getLayoutParams();
      if (!localBitSet.get(localLayoutParams1.mSpan.mIndex)) {
        break label164;
      }
      if (!checkSpanForGap(localLayoutParams1.mSpan)) {
        break label152;
      }
      return localView1;
      j = -1;
      break;
      k = i + 1;
      m = 0;
      break label61;
    }
    label152:
    localBitSet.clear(localLayoutParams1.mSpan.mIndex);
    label164:
    if (localLayoutParams1.mFullSpan) {}
    label172:
    label252:
    label290:
    label350:
    label352:
    label358:
    label362:
    for (;;)
    {
      i1 += n;
      break;
      if (i1 + n != k)
      {
        View localView2 = getChildAt(i1 + n);
        int i4;
        int i5;
        if (this.mShouldReverseLayout)
        {
          int i7 = this.mPrimaryOrientation.getDecoratedEnd(localView1);
          int i8 = this.mPrimaryOrientation.getDecoratedEnd(localView2);
          if (i7 < i8) {
            break label127;
          }
          i4 = 0;
          if (i7 == i8) {
            i4 = 1;
          }
          if (i4 == 0) {
            break label350;
          }
          LayoutParams localLayoutParams2 = (LayoutParams)localView2.getLayoutParams();
          if (localLayoutParams1.mSpan.mIndex - localLayoutParams2.mSpan.mIndex >= 0) {
            break label352;
          }
          i5 = 1;
          if (j >= 0) {
            break label358;
          }
        }
        for (int i6 = 1;; i6 = 0)
        {
          if (i5 == i6) {
            break label362;
          }
          return localView1;
          int i2 = this.mPrimaryOrientation.getDecoratedStart(localView1);
          int i3 = this.mPrimaryOrientation.getDecoratedStart(localView2);
          if (i2 > i3) {
            break;
          }
          i4 = 0;
          if (i2 != i3) {
            break label252;
          }
          i4 = 1;
          break label252;
          break label172;
          i5 = 0;
          break label290;
        }
      }
    }
    label364:
    return null;
  }
  
  public void invalidateSpanAssignments()
  {
    this.mLazySpanLookup.clear();
    requestLayout();
  }
  
  boolean isLayoutRTL()
  {
    return getLayoutDirection() == 1;
  }
  
  public void offsetChildrenHorizontal(int paramInt)
  {
    super.offsetChildrenHorizontal(paramInt);
    for (int i = 0; i < this.mSpanCount; i++) {
      this.mSpans[i].onOffset(paramInt);
    }
  }
  
  public void offsetChildrenVertical(int paramInt)
  {
    super.offsetChildrenVertical(paramInt);
    for (int i = 0; i < this.mSpanCount; i++) {
      this.mSpans[i].onOffset(paramInt);
    }
  }
  
  public void onDetachedFromWindow(RecyclerView paramRecyclerView, RecyclerView.Recycler paramRecycler)
  {
    removeCallbacks(this.mCheckForGapsRunnable);
    for (int i = 0; i < this.mSpanCount; i++) {
      this.mSpans[i].clear();
    }
    paramRecyclerView.requestLayout();
  }
  
  @Nullable
  public View onFocusSearchFailed(View paramView, int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    View localView5;
    if (getChildCount() == 0)
    {
      localView5 = null;
      return localView5;
    }
    View localView1 = findContainingItemView(paramView);
    if (localView1 == null) {
      return null;
    }
    resolveShouldLayoutReverse();
    int i = convertFocusDirectionToLayoutDirection(paramInt);
    if (i == Integer.MIN_VALUE) {
      return null;
    }
    LayoutParams localLayoutParams = (LayoutParams)localView1.getLayoutParams();
    boolean bool = localLayoutParams.mFullSpan;
    Span localSpan = localLayoutParams.mSpan;
    if (i == 1) {}
    for (int j = getLastChildPosition();; j = getFirstChildPosition())
    {
      updateLayoutState(j, paramState);
      setLayoutStateDirection(i);
      this.mLayoutState.mCurrentPosition = (j + this.mLayoutState.mItemDirection);
      this.mLayoutState.mAvailable = ((int)(0.33333334F * this.mPrimaryOrientation.getTotalSpace()));
      this.mLayoutState.mStopInFocusable = true;
      this.mLayoutState.mRecycle = false;
      fill(paramRecycler, this.mLayoutState, paramState);
      this.mLastLayoutFromEnd = this.mShouldReverseLayout;
      if (!bool)
      {
        localView5 = localSpan.getFocusableViewAfter(j, i);
        if ((localView5 != null) && (localView5 != localView1)) {
          break;
        }
      }
      if (!preferLastSpan(i)) {
        break label262;
      }
      for (int i7 = -1 + this.mSpanCount;; i7--)
      {
        if (i7 < 0) {
          break label308;
        }
        localView5 = this.mSpans[i7].getFocusableViewAfter(j, i);
        if ((localView5 != null) && (localView5 != localView1)) {
          break;
        }
      }
    }
    label262:
    for (int k = 0;; k++)
    {
      if (k >= this.mSpanCount) {
        break label308;
      }
      localView5 = this.mSpans[k].getFocusableViewAfter(j, i);
      if ((localView5 != null) && (localView5 != localView1)) {
        break;
      }
    }
    label308:
    int m;
    int n;
    label327:
    int i1;
    if (!this.mReverseLayout)
    {
      m = 1;
      if (i != -1) {
        break label383;
      }
      n = 1;
      if (m != n) {
        break label389;
      }
      i1 = 1;
      label337:
      if (bool) {
        break label405;
      }
      if (i1 == 0) {
        break label395;
      }
    }
    label383:
    label389:
    label395:
    for (int i6 = localSpan.findFirstPartiallyVisibleItemPosition();; i6 = localSpan.findLastPartiallyVisibleItemPosition())
    {
      View localView4 = findViewByPosition(i6);
      if ((localView4 == null) || (localView4 == localView1)) {
        break label405;
      }
      return localView4;
      m = 0;
      break;
      n = 0;
      break label327;
      i1 = 0;
      break label337;
    }
    label405:
    if (preferLastSpan(i))
    {
      for (int i4 = -1 + this.mSpanCount;; i4--)
      {
        if (i4 < 0) {
          break label571;
        }
        if (i4 != localSpan.mIndex) {
          break;
        }
      }
      if (i1 != 0) {}
      for (int i5 = this.mSpans[i4].findFirstPartiallyVisibleItemPosition();; i5 = this.mSpans[i4].findLastPartiallyVisibleItemPosition())
      {
        View localView3 = findViewByPosition(i5);
        if ((localView3 == null) || (localView3 == localView1)) {
          break;
        }
        return localView3;
      }
    }
    for (int i2 = 0; i2 < this.mSpanCount; i2++)
    {
      if (i1 != 0) {}
      for (int i3 = this.mSpans[i2].findFirstPartiallyVisibleItemPosition();; i3 = this.mSpans[i2].findLastPartiallyVisibleItemPosition())
      {
        View localView2 = findViewByPosition(i3);
        if ((localView2 == null) || (localView2 == localView1)) {
          break;
        }
        return localView2;
      }
    }
    label571:
    return null;
  }
  
  public void onInitializeAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    super.onInitializeAccessibilityEvent(paramAccessibilityEvent);
    AccessibilityRecordCompat localAccessibilityRecordCompat;
    View localView1;
    View localView2;
    if (getChildCount() > 0)
    {
      localAccessibilityRecordCompat = AccessibilityEventCompat.asRecord(paramAccessibilityEvent);
      localView1 = findFirstVisibleItemClosestToStart(false);
      localView2 = findFirstVisibleItemClosestToEnd(false);
      if ((localView1 != null) && (localView2 != null)) {}
    }
    else
    {
      return;
    }
    int i = getPosition(localView1);
    int j = getPosition(localView2);
    if (i < j)
    {
      localAccessibilityRecordCompat.setFromIndex(i);
      localAccessibilityRecordCompat.setToIndex(j);
      return;
    }
    localAccessibilityRecordCompat.setFromIndex(j);
    localAccessibilityRecordCompat.setToIndex(i);
  }
  
  public void onInitializeAccessibilityNodeInfoForItem(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
  {
    ViewGroup.LayoutParams localLayoutParams = paramView.getLayoutParams();
    if (!(localLayoutParams instanceof LayoutParams))
    {
      super.onInitializeAccessibilityNodeInfoForItem(paramView, paramAccessibilityNodeInfoCompat);
      return;
    }
    LayoutParams localLayoutParams1 = (LayoutParams)localLayoutParams;
    if (this.mOrientation == 0)
    {
      int k = localLayoutParams1.getSpanIndex();
      if (localLayoutParams1.mFullSpan) {}
      for (int m = this.mSpanCount;; m = 1)
      {
        paramAccessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(k, m, -1, -1, localLayoutParams1.mFullSpan, false));
        return;
      }
    }
    int i = localLayoutParams1.getSpanIndex();
    if (localLayoutParams1.mFullSpan) {}
    for (int j = this.mSpanCount;; j = 1)
    {
      paramAccessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(-1, -1, i, j, localLayoutParams1.mFullSpan, false));
      return;
    }
  }
  
  public void onItemsAdded(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
  {
    handleUpdate(paramInt1, paramInt2, 1);
  }
  
  public void onItemsChanged(RecyclerView paramRecyclerView)
  {
    this.mLazySpanLookup.clear();
    requestLayout();
  }
  
  public void onItemsMoved(RecyclerView paramRecyclerView, int paramInt1, int paramInt2, int paramInt3)
  {
    handleUpdate(paramInt1, paramInt2, 8);
  }
  
  public void onItemsRemoved(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
  {
    handleUpdate(paramInt1, paramInt2, 2);
  }
  
  public void onItemsUpdated(RecyclerView paramRecyclerView, int paramInt1, int paramInt2, Object paramObject)
  {
    handleUpdate(paramInt1, paramInt2, 4);
  }
  
  public void onLayoutChildren(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    onLayoutChildren(paramRecycler, paramState, true);
  }
  
  public void onLayoutCompleted(RecyclerView.State paramState)
  {
    super.onLayoutCompleted(paramState);
    this.mPendingScrollPosition = -1;
    this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
    this.mPendingSavedState = null;
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
    if (this.mPendingSavedState != null)
    {
      localSavedState = new SavedState(this.mPendingSavedState);
      return localSavedState;
    }
    SavedState localSavedState = new SavedState();
    localSavedState.mReverseLayout = this.mReverseLayout;
    localSavedState.mAnchorLayoutFromEnd = this.mLastLayoutFromEnd;
    localSavedState.mLastLayoutRTL = this.mLastLayoutRTL;
    label101:
    int i;
    label120:
    int j;
    label153:
    int k;
    if ((this.mLazySpanLookup != null) && (this.mLazySpanLookup.mData != null))
    {
      localSavedState.mSpanLookup = this.mLazySpanLookup.mData;
      localSavedState.mSpanLookupSize = localSavedState.mSpanLookup.length;
      localSavedState.mFullSpanItems = this.mLazySpanLookup.mFullSpanItems;
      if (getChildCount() <= 0) {
        break label265;
      }
      if (!this.mLastLayoutFromEnd) {
        break label222;
      }
      i = getLastChildPosition();
      localSavedState.mAnchorPosition = i;
      localSavedState.mVisibleAnchorPosition = findFirstVisibleItemPositionInt();
      localSavedState.mSpanOffsetsSize = this.mSpanCount;
      localSavedState.mSpanOffsets = new int[this.mSpanCount];
      j = 0;
      if (j < this.mSpanCount)
      {
        if (!this.mLastLayoutFromEnd) {
          break label230;
        }
        k = this.mSpans[j].getEndLine(Integer.MIN_VALUE);
        if (k != Integer.MIN_VALUE) {
          k -= this.mPrimaryOrientation.getEndAfterPadding();
        }
      }
    }
    for (;;)
    {
      localSavedState.mSpanOffsets[j] = k;
      j++;
      break label153;
      break;
      localSavedState.mSpanLookupSize = 0;
      break label101;
      label222:
      i = getFirstChildPosition();
      break label120;
      label230:
      k = this.mSpans[j].getStartLine(Integer.MIN_VALUE);
      if (k != Integer.MIN_VALUE) {
        k -= this.mPrimaryOrientation.getStartAfterPadding();
      }
    }
    label265:
    localSavedState.mAnchorPosition = -1;
    localSavedState.mVisibleAnchorPosition = -1;
    localSavedState.mSpanOffsetsSize = 0;
    return localSavedState;
  }
  
  public void onScrollStateChanged(int paramInt)
  {
    if (paramInt == 0) {
      checkForGaps();
    }
  }
  
  void prepareLayoutStateForDelta(int paramInt, RecyclerView.State paramState)
  {
    int i;
    if (paramInt > 0) {
      i = 1;
    }
    for (int j = getLastChildPosition();; j = getFirstChildPosition())
    {
      this.mLayoutState.mRecycle = true;
      updateLayoutState(j, paramState);
      setLayoutStateDirection(i);
      this.mLayoutState.mCurrentPosition = (j + this.mLayoutState.mItemDirection);
      this.mLayoutState.mAvailable = Math.abs(paramInt);
      return;
      i = -1;
    }
  }
  
  int scrollBy(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    if ((getChildCount() == 0) || (paramInt == 0)) {
      return 0;
    }
    prepareLayoutStateForDelta(paramInt, paramState);
    int i = fill(paramRecycler, this.mLayoutState, paramState);
    int j;
    if (this.mLayoutState.mAvailable < i) {
      j = paramInt;
    }
    for (;;)
    {
      this.mPrimaryOrientation.offsetChildren(-j);
      this.mLastLayoutFromEnd = this.mShouldReverseLayout;
      this.mLayoutState.mAvailable = 0;
      recycle(paramRecycler, this.mLayoutState);
      return j;
      if (paramInt < 0) {
        j = -i;
      } else {
        j = i;
      }
    }
  }
  
  public int scrollHorizontallyBy(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    return scrollBy(paramInt, paramRecycler, paramState);
  }
  
  public void scrollToPosition(int paramInt)
  {
    if ((this.mPendingSavedState != null) && (this.mPendingSavedState.mAnchorPosition != paramInt)) {
      this.mPendingSavedState.invalidateAnchorPositionInfo();
    }
    this.mPendingScrollPosition = paramInt;
    this.mPendingScrollPositionOffset = Integer.MIN_VALUE;
    requestLayout();
  }
  
  public void scrollToPositionWithOffset(int paramInt1, int paramInt2)
  {
    if (this.mPendingSavedState != null) {
      this.mPendingSavedState.invalidateAnchorPositionInfo();
    }
    this.mPendingScrollPosition = paramInt1;
    this.mPendingScrollPositionOffset = paramInt2;
    requestLayout();
  }
  
  public int scrollVerticallyBy(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    return scrollBy(paramInt, paramRecycler, paramState);
  }
  
  public void setGapStrategy(int paramInt)
  {
    assertNotInLayoutOrScroll(null);
    if (paramInt == this.mGapStrategy) {
      return;
    }
    if ((paramInt != 0) && (paramInt != 2)) {
      throw new IllegalArgumentException("invalid gap strategy. Must be GAP_HANDLING_NONE or GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS");
    }
    this.mGapStrategy = paramInt;
    if (this.mGapStrategy != 0) {}
    for (boolean bool = true;; bool = false)
    {
      setAutoMeasureEnabled(bool);
      requestLayout();
      return;
    }
  }
  
  public void setMeasuredDimension(Rect paramRect, int paramInt1, int paramInt2)
  {
    int i = getPaddingLeft() + getPaddingRight();
    int j = getPaddingTop() + getPaddingBottom();
    int m;
    int k;
    if (this.mOrientation == 1)
    {
      m = chooseSize(paramInt2, j + paramRect.height(), getMinimumHeight());
      k = chooseSize(paramInt1, i + this.mSizePerSpan * this.mSpanCount, getMinimumWidth());
    }
    for (;;)
    {
      setMeasuredDimension(k, m);
      return;
      k = chooseSize(paramInt1, i + paramRect.width(), getMinimumWidth());
      m = chooseSize(paramInt2, j + this.mSizePerSpan * this.mSpanCount, getMinimumHeight());
    }
  }
  
  public void setOrientation(int paramInt)
  {
    if ((paramInt != 0) && (paramInt != 1)) {
      throw new IllegalArgumentException("invalid orientation.");
    }
    assertNotInLayoutOrScroll(null);
    if (paramInt == this.mOrientation) {
      return;
    }
    this.mOrientation = paramInt;
    OrientationHelper localOrientationHelper = this.mPrimaryOrientation;
    this.mPrimaryOrientation = this.mSecondaryOrientation;
    this.mSecondaryOrientation = localOrientationHelper;
    requestLayout();
  }
  
  public void setReverseLayout(boolean paramBoolean)
  {
    assertNotInLayoutOrScroll(null);
    if ((this.mPendingSavedState != null) && (this.mPendingSavedState.mReverseLayout != paramBoolean)) {
      this.mPendingSavedState.mReverseLayout = paramBoolean;
    }
    this.mReverseLayout = paramBoolean;
    requestLayout();
  }
  
  public void setSpanCount(int paramInt)
  {
    assertNotInLayoutOrScroll(null);
    if (paramInt != this.mSpanCount)
    {
      invalidateSpanAssignments();
      this.mSpanCount = paramInt;
      this.mRemainingSpans = new BitSet(this.mSpanCount);
      this.mSpans = new Span[this.mSpanCount];
      for (int i = 0; i < this.mSpanCount; i++) {
        this.mSpans[i] = new Span(i);
      }
      requestLayout();
    }
  }
  
  public void smoothScrollToPosition(RecyclerView paramRecyclerView, RecyclerView.State paramState, int paramInt)
  {
    LinearSmoothScroller localLinearSmoothScroller = new LinearSmoothScroller(paramRecyclerView.getContext());
    localLinearSmoothScroller.setTargetPosition(paramInt);
    startSmoothScroll(localLinearSmoothScroller);
  }
  
  public boolean supportsPredictiveItemAnimations()
  {
    return this.mPendingSavedState == null;
  }
  
  boolean updateAnchorFromPendingData(RecyclerView.State paramState, AnchorInfo paramAnchorInfo)
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
    if ((this.mPendingSavedState == null) || (this.mPendingSavedState.mAnchorPosition == -1) || (this.mPendingSavedState.mSpanOffsetsSize < 1))
    {
      View localView = findViewByPosition(this.mPendingScrollPosition);
      if (localView != null)
      {
        if (this.mShouldReverseLayout) {}
        for (int j = getLastChildPosition();; j = getFirstChildPosition())
        {
          paramAnchorInfo.mPosition = j;
          if (this.mPendingScrollPositionOffset == Integer.MIN_VALUE) {
            break label188;
          }
          if (!paramAnchorInfo.mLayoutFromEnd) {
            break;
          }
          paramAnchorInfo.mOffset = (this.mPrimaryOrientation.getEndAfterPadding() - this.mPendingScrollPositionOffset - this.mPrimaryOrientation.getDecoratedEnd(localView));
          return true;
        }
        paramAnchorInfo.mOffset = (this.mPrimaryOrientation.getStartAfterPadding() + this.mPendingScrollPositionOffset - this.mPrimaryOrientation.getDecoratedStart(localView));
        return true;
        label188:
        if (this.mPrimaryOrientation.getDecoratedMeasurement(localView) > this.mPrimaryOrientation.getTotalSpace())
        {
          if (paramAnchorInfo.mLayoutFromEnd) {}
          for (int n = this.mPrimaryOrientation.getEndAfterPadding();; n = this.mPrimaryOrientation.getStartAfterPadding())
          {
            paramAnchorInfo.mOffset = n;
            return true;
          }
        }
        int k = this.mPrimaryOrientation.getDecoratedStart(localView) - this.mPrimaryOrientation.getStartAfterPadding();
        if (k < 0)
        {
          paramAnchorInfo.mOffset = (-k);
          return true;
        }
        int m = this.mPrimaryOrientation.getEndAfterPadding() - this.mPrimaryOrientation.getDecoratedEnd(localView);
        if (m < 0)
        {
          paramAnchorInfo.mOffset = m;
          return true;
        }
        paramAnchorInfo.mOffset = Integer.MIN_VALUE;
        return true;
      }
      paramAnchorInfo.mPosition = this.mPendingScrollPosition;
      if (this.mPendingScrollPositionOffset == Integer.MIN_VALUE)
      {
        int i = calculateScrollDirectionForPosition(paramAnchorInfo.mPosition);
        boolean bool = false;
        if (i == 1) {
          bool = true;
        }
        paramAnchorInfo.mLayoutFromEnd = bool;
        paramAnchorInfo.assignCoordinateFromPadding();
      }
      for (;;)
      {
        paramAnchorInfo.mInvalidateOffsets = true;
        return true;
        paramAnchorInfo.assignCoordinateFromPadding(this.mPendingScrollPositionOffset);
      }
    }
    paramAnchorInfo.mOffset = Integer.MIN_VALUE;
    paramAnchorInfo.mPosition = this.mPendingScrollPosition;
    return true;
  }
  
  void updateAnchorInfoForLayout(RecyclerView.State paramState, AnchorInfo paramAnchorInfo)
  {
    if (updateAnchorFromPendingData(paramState, paramAnchorInfo)) {}
    while (updateAnchorFromChildren(paramState, paramAnchorInfo)) {
      return;
    }
    paramAnchorInfo.assignCoordinateFromPadding();
    paramAnchorInfo.mPosition = 0;
  }
  
  void updateMeasureSpecs(int paramInt)
  {
    this.mSizePerSpan = (paramInt / this.mSpanCount);
    this.mFullSizeSpec = View.MeasureSpec.makeMeasureSpec(paramInt, this.mSecondaryOrientation.getMode());
  }
  
  class AnchorInfo
  {
    boolean mInvalidateOffsets;
    boolean mLayoutFromEnd;
    int mOffset;
    int mPosition;
    int[] mSpanReferenceLines;
    boolean mValid;
    
    public AnchorInfo()
    {
      reset();
    }
    
    void assignCoordinateFromPadding()
    {
      if (this.mLayoutFromEnd) {}
      for (int i = StaggeredGridLayoutManager.this.mPrimaryOrientation.getEndAfterPadding();; i = StaggeredGridLayoutManager.this.mPrimaryOrientation.getStartAfterPadding())
      {
        this.mOffset = i;
        return;
      }
    }
    
    void assignCoordinateFromPadding(int paramInt)
    {
      if (this.mLayoutFromEnd)
      {
        this.mOffset = (StaggeredGridLayoutManager.this.mPrimaryOrientation.getEndAfterPadding() - paramInt);
        return;
      }
      this.mOffset = (paramInt + StaggeredGridLayoutManager.this.mPrimaryOrientation.getStartAfterPadding());
    }
    
    void reset()
    {
      this.mPosition = -1;
      this.mOffset = Integer.MIN_VALUE;
      this.mLayoutFromEnd = false;
      this.mInvalidateOffsets = false;
      this.mValid = false;
      if (this.mSpanReferenceLines != null) {
        Arrays.fill(this.mSpanReferenceLines, -1);
      }
    }
    
    void saveSpanReferenceLines(StaggeredGridLayoutManager.Span[] paramArrayOfSpan)
    {
      int i = paramArrayOfSpan.length;
      if ((this.mSpanReferenceLines == null) || (this.mSpanReferenceLines.length < i)) {
        this.mSpanReferenceLines = new int[StaggeredGridLayoutManager.this.mSpans.length];
      }
      for (int j = 0; j < i; j++) {
        this.mSpanReferenceLines[j] = paramArrayOfSpan[j].getStartLine(Integer.MIN_VALUE);
      }
    }
  }
  
  public static class LayoutParams
    extends RecyclerView.LayoutParams
  {
    public static final int INVALID_SPAN_ID = -1;
    boolean mFullSpan;
    StaggeredGridLayoutManager.Span mSpan;
    
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
    }
    
    public LayoutParams(RecyclerView.LayoutParams paramLayoutParams)
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
    
    public final int getSpanIndex()
    {
      if (this.mSpan == null) {
        return -1;
      }
      return this.mSpan.mIndex;
    }
    
    public boolean isFullSpan()
    {
      return this.mFullSpan;
    }
    
    public void setFullSpan(boolean paramBoolean)
    {
      this.mFullSpan = paramBoolean;
    }
  }
  
  static class LazySpanLookup
  {
    private static final int MIN_SIZE = 10;
    int[] mData;
    List<FullSpanItem> mFullSpanItems;
    
    LazySpanLookup() {}
    
    private int invalidateFullSpansAfter(int paramInt)
    {
      if (this.mFullSpanItems == null) {
        return -1;
      }
      FullSpanItem localFullSpanItem1 = getFullSpanItem(paramInt);
      if (localFullSpanItem1 != null) {
        this.mFullSpanItems.remove(localFullSpanItem1);
      }
      int i = -1;
      int j = this.mFullSpanItems.size();
      for (int k = 0;; k++) {
        if (k < j)
        {
          if (((FullSpanItem)this.mFullSpanItems.get(k)).mPosition >= paramInt) {
            i = k;
          }
        }
        else
        {
          if (i == -1) {
            break;
          }
          FullSpanItem localFullSpanItem2 = (FullSpanItem)this.mFullSpanItems.get(i);
          this.mFullSpanItems.remove(i);
          return localFullSpanItem2.mPosition;
        }
      }
    }
    
    private void offsetFullSpansForAddition(int paramInt1, int paramInt2)
    {
      if (this.mFullSpanItems == null) {
        return;
      }
      int i = -1 + this.mFullSpanItems.size();
      label20:
      FullSpanItem localFullSpanItem;
      if (i >= 0)
      {
        localFullSpanItem = (FullSpanItem)this.mFullSpanItems.get(i);
        if (localFullSpanItem.mPosition >= paramInt1) {
          break label54;
        }
      }
      for (;;)
      {
        i--;
        break label20;
        break;
        label54:
        localFullSpanItem.mPosition = (paramInt2 + localFullSpanItem.mPosition);
      }
    }
    
    private void offsetFullSpansForRemoval(int paramInt1, int paramInt2)
    {
      if (this.mFullSpanItems == null) {
        return;
      }
      int i = paramInt1 + paramInt2;
      int j = -1 + this.mFullSpanItems.size();
      label25:
      FullSpanItem localFullSpanItem;
      if (j >= 0)
      {
        localFullSpanItem = (FullSpanItem)this.mFullSpanItems.get(j);
        if (localFullSpanItem.mPosition >= paramInt1) {
          break label61;
        }
      }
      for (;;)
      {
        j--;
        break label25;
        break;
        label61:
        if (localFullSpanItem.mPosition < i) {
          this.mFullSpanItems.remove(j);
        } else {
          localFullSpanItem.mPosition -= paramInt2;
        }
      }
    }
    
    public void addFullSpanItem(FullSpanItem paramFullSpanItem)
    {
      if (this.mFullSpanItems == null) {
        this.mFullSpanItems = new ArrayList();
      }
      int i = this.mFullSpanItems.size();
      for (int j = 0; j < i; j++)
      {
        FullSpanItem localFullSpanItem = (FullSpanItem)this.mFullSpanItems.get(j);
        if (localFullSpanItem.mPosition == paramFullSpanItem.mPosition) {
          this.mFullSpanItems.remove(j);
        }
        if (localFullSpanItem.mPosition >= paramFullSpanItem.mPosition)
        {
          this.mFullSpanItems.add(j, paramFullSpanItem);
          return;
        }
      }
      this.mFullSpanItems.add(paramFullSpanItem);
    }
    
    void clear()
    {
      if (this.mData != null) {
        Arrays.fill(this.mData, -1);
      }
      this.mFullSpanItems = null;
    }
    
    void ensureSize(int paramInt)
    {
      if (this.mData == null)
      {
        this.mData = new int[1 + Math.max(paramInt, 10)];
        Arrays.fill(this.mData, -1);
      }
      while (paramInt < this.mData.length) {
        return;
      }
      int[] arrayOfInt = this.mData;
      this.mData = new int[sizeForPosition(paramInt)];
      System.arraycopy(arrayOfInt, 0, this.mData, 0, arrayOfInt.length);
      Arrays.fill(this.mData, arrayOfInt.length, this.mData.length, -1);
    }
    
    int forceInvalidateAfter(int paramInt)
    {
      if (this.mFullSpanItems != null) {
        for (int i = -1 + this.mFullSpanItems.size(); i >= 0; i--) {
          if (((FullSpanItem)this.mFullSpanItems.get(i)).mPosition >= paramInt) {
            this.mFullSpanItems.remove(i);
          }
        }
      }
      return invalidateAfter(paramInt);
    }
    
    public FullSpanItem getFirstFullSpanItemInRange(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
    {
      FullSpanItem localFullSpanItem;
      if (this.mFullSpanItems == null)
      {
        localFullSpanItem = null;
        return localFullSpanItem;
      }
      int i = this.mFullSpanItems.size();
      for (int j = 0;; j++)
      {
        if (j >= i) {
          break label102;
        }
        localFullSpanItem = (FullSpanItem)this.mFullSpanItems.get(j);
        if (localFullSpanItem.mPosition >= paramInt2) {
          return null;
        }
        if ((localFullSpanItem.mPosition >= paramInt1) && ((paramInt3 == 0) || (localFullSpanItem.mGapDir == paramInt3) || ((paramBoolean) && (localFullSpanItem.mHasUnwantedGapAfter)))) {
          break;
        }
      }
      label102:
      return null;
    }
    
    public FullSpanItem getFullSpanItem(int paramInt)
    {
      FullSpanItem localFullSpanItem;
      if (this.mFullSpanItems == null)
      {
        localFullSpanItem = null;
        return localFullSpanItem;
      }
      for (int i = -1 + this.mFullSpanItems.size();; i--)
      {
        if (i < 0) {
          break label55;
        }
        localFullSpanItem = (FullSpanItem)this.mFullSpanItems.get(i);
        if (localFullSpanItem.mPosition == paramInt) {
          break;
        }
      }
      label55:
      return null;
    }
    
    int getSpan(int paramInt)
    {
      if ((this.mData == null) || (paramInt >= this.mData.length)) {
        return -1;
      }
      return this.mData[paramInt];
    }
    
    int invalidateAfter(int paramInt)
    {
      if (this.mData == null) {}
      while (paramInt >= this.mData.length) {
        return -1;
      }
      int i = invalidateFullSpansAfter(paramInt);
      if (i == -1)
      {
        Arrays.fill(this.mData, paramInt, this.mData.length, -1);
        return this.mData.length;
      }
      Arrays.fill(this.mData, paramInt, i + 1, -1);
      return i + 1;
    }
    
    void offsetForAddition(int paramInt1, int paramInt2)
    {
      if ((this.mData == null) || (paramInt1 >= this.mData.length)) {
        return;
      }
      ensureSize(paramInt1 + paramInt2);
      System.arraycopy(this.mData, paramInt1, this.mData, paramInt1 + paramInt2, this.mData.length - paramInt1 - paramInt2);
      Arrays.fill(this.mData, paramInt1, paramInt1 + paramInt2, -1);
      offsetFullSpansForAddition(paramInt1, paramInt2);
    }
    
    void offsetForRemoval(int paramInt1, int paramInt2)
    {
      if ((this.mData == null) || (paramInt1 >= this.mData.length)) {
        return;
      }
      ensureSize(paramInt1 + paramInt2);
      System.arraycopy(this.mData, paramInt1 + paramInt2, this.mData, paramInt1, this.mData.length - paramInt1 - paramInt2);
      Arrays.fill(this.mData, this.mData.length - paramInt2, this.mData.length, -1);
      offsetFullSpansForRemoval(paramInt1, paramInt2);
    }
    
    void setSpan(int paramInt, StaggeredGridLayoutManager.Span paramSpan)
    {
      ensureSize(paramInt);
      this.mData[paramInt] = paramSpan.mIndex;
    }
    
    int sizeForPosition(int paramInt)
    {
      int i = this.mData.length;
      while (i <= paramInt) {
        i *= 2;
      }
      return i;
    }
    
    static class FullSpanItem
      implements Parcelable
    {
      public static final Parcelable.Creator<FullSpanItem> CREATOR = new Parcelable.Creator()
      {
        public StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem createFromParcel(Parcel paramAnonymousParcel)
        {
          return new StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem(paramAnonymousParcel);
        }
        
        public StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem[] newArray(int paramAnonymousInt)
        {
          return new StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem[paramAnonymousInt];
        }
      };
      int mGapDir;
      int[] mGapPerSpan;
      boolean mHasUnwantedGapAfter;
      int mPosition;
      
      public FullSpanItem() {}
      
      public FullSpanItem(Parcel paramParcel)
      {
        this.mPosition = paramParcel.readInt();
        this.mGapDir = paramParcel.readInt();
        if (paramParcel.readInt() == i) {}
        for (;;)
        {
          this.mHasUnwantedGapAfter = i;
          int j = paramParcel.readInt();
          if (j > 0)
          {
            this.mGapPerSpan = new int[j];
            paramParcel.readIntArray(this.mGapPerSpan);
          }
          return;
          i = 0;
        }
      }
      
      public int describeContents()
      {
        return 0;
      }
      
      int getGapForSpan(int paramInt)
      {
        if (this.mGapPerSpan == null) {
          return 0;
        }
        return this.mGapPerSpan[paramInt];
      }
      
      public String toString()
      {
        return "FullSpanItem{mPosition=" + this.mPosition + ", mGapDir=" + this.mGapDir + ", mHasUnwantedGapAfter=" + this.mHasUnwantedGapAfter + ", mGapPerSpan=" + Arrays.toString(this.mGapPerSpan) + '}';
      }
      
      public void writeToParcel(Parcel paramParcel, int paramInt)
      {
        paramParcel.writeInt(this.mPosition);
        paramParcel.writeInt(this.mGapDir);
        if (this.mHasUnwantedGapAfter) {}
        for (int i = 1;; i = 0)
        {
          paramParcel.writeInt(i);
          if ((this.mGapPerSpan == null) || (this.mGapPerSpan.length <= 0)) {
            break;
          }
          paramParcel.writeInt(this.mGapPerSpan.length);
          paramParcel.writeIntArray(this.mGapPerSpan);
          return;
        }
        paramParcel.writeInt(0);
      }
    }
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public static class SavedState
    implements Parcelable
  {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator()
    {
      public StaggeredGridLayoutManager.SavedState createFromParcel(Parcel paramAnonymousParcel)
      {
        return new StaggeredGridLayoutManager.SavedState(paramAnonymousParcel);
      }
      
      public StaggeredGridLayoutManager.SavedState[] newArray(int paramAnonymousInt)
      {
        return new StaggeredGridLayoutManager.SavedState[paramAnonymousInt];
      }
    };
    boolean mAnchorLayoutFromEnd;
    int mAnchorPosition;
    List<StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem> mFullSpanItems;
    boolean mLastLayoutRTL;
    boolean mReverseLayout;
    int[] mSpanLookup;
    int mSpanLookupSize;
    int[] mSpanOffsets;
    int mSpanOffsetsSize;
    int mVisibleAnchorPosition;
    
    public SavedState() {}
    
    SavedState(Parcel paramParcel)
    {
      this.mAnchorPosition = paramParcel.readInt();
      this.mVisibleAnchorPosition = paramParcel.readInt();
      this.mSpanOffsetsSize = paramParcel.readInt();
      if (this.mSpanOffsetsSize > 0)
      {
        this.mSpanOffsets = new int[this.mSpanOffsetsSize];
        paramParcel.readIntArray(this.mSpanOffsets);
      }
      this.mSpanLookupSize = paramParcel.readInt();
      if (this.mSpanLookupSize > 0)
      {
        this.mSpanLookup = new int[this.mSpanLookupSize];
        paramParcel.readIntArray(this.mSpanLookup);
      }
      int j;
      int k;
      if (paramParcel.readInt() == i)
      {
        j = i;
        this.mReverseLayout = j;
        if (paramParcel.readInt() != i) {
          break label152;
        }
        k = i;
        label114:
        this.mAnchorLayoutFromEnd = k;
        if (paramParcel.readInt() != i) {
          break label158;
        }
      }
      for (;;)
      {
        this.mLastLayoutRTL = i;
        this.mFullSpanItems = paramParcel.readArrayList(StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem.class.getClassLoader());
        return;
        j = 0;
        break;
        label152:
        k = 0;
        break label114;
        label158:
        i = 0;
      }
    }
    
    public SavedState(SavedState paramSavedState)
    {
      this.mSpanOffsetsSize = paramSavedState.mSpanOffsetsSize;
      this.mAnchorPosition = paramSavedState.mAnchorPosition;
      this.mVisibleAnchorPosition = paramSavedState.mVisibleAnchorPosition;
      this.mSpanOffsets = paramSavedState.mSpanOffsets;
      this.mSpanLookupSize = paramSavedState.mSpanLookupSize;
      this.mSpanLookup = paramSavedState.mSpanLookup;
      this.mReverseLayout = paramSavedState.mReverseLayout;
      this.mAnchorLayoutFromEnd = paramSavedState.mAnchorLayoutFromEnd;
      this.mLastLayoutRTL = paramSavedState.mLastLayoutRTL;
      this.mFullSpanItems = paramSavedState.mFullSpanItems;
    }
    
    public int describeContents()
    {
      return 0;
    }
    
    void invalidateAnchorPositionInfo()
    {
      this.mSpanOffsets = null;
      this.mSpanOffsetsSize = 0;
      this.mAnchorPosition = -1;
      this.mVisibleAnchorPosition = -1;
    }
    
    void invalidateSpanInfo()
    {
      this.mSpanOffsets = null;
      this.mSpanOffsetsSize = 0;
      this.mSpanLookupSize = 0;
      this.mSpanLookup = null;
      this.mFullSpanItems = null;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      int i = 1;
      paramParcel.writeInt(this.mAnchorPosition);
      paramParcel.writeInt(this.mVisibleAnchorPosition);
      paramParcel.writeInt(this.mSpanOffsetsSize);
      if (this.mSpanOffsetsSize > 0) {
        paramParcel.writeIntArray(this.mSpanOffsets);
      }
      paramParcel.writeInt(this.mSpanLookupSize);
      if (this.mSpanLookupSize > 0) {
        paramParcel.writeIntArray(this.mSpanLookup);
      }
      int j;
      int k;
      if (this.mReverseLayout)
      {
        j = i;
        paramParcel.writeInt(j);
        if (!this.mAnchorLayoutFromEnd) {
          break label123;
        }
        k = i;
        label90:
        paramParcel.writeInt(k);
        if (!this.mLastLayoutRTL) {
          break label129;
        }
      }
      for (;;)
      {
        paramParcel.writeInt(i);
        paramParcel.writeList(this.mFullSpanItems);
        return;
        j = 0;
        break;
        label123:
        k = 0;
        break label90;
        label129:
        i = 0;
      }
    }
  }
  
  class Span
  {
    static final int INVALID_LINE = Integer.MIN_VALUE;
    int mCachedEnd = Integer.MIN_VALUE;
    int mCachedStart = Integer.MIN_VALUE;
    int mDeletedSize = 0;
    final int mIndex;
    ArrayList<View> mViews = new ArrayList();
    
    Span(int paramInt)
    {
      this.mIndex = paramInt;
    }
    
    void appendToSpan(View paramView)
    {
      StaggeredGridLayoutManager.LayoutParams localLayoutParams = getLayoutParams(paramView);
      localLayoutParams.mSpan = this;
      this.mViews.add(paramView);
      this.mCachedEnd = Integer.MIN_VALUE;
      if (this.mViews.size() == 1) {
        this.mCachedStart = Integer.MIN_VALUE;
      }
      if ((localLayoutParams.isItemRemoved()) || (localLayoutParams.isItemChanged())) {
        this.mDeletedSize += StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedMeasurement(paramView);
      }
    }
    
    void cacheReferenceLineAndClear(boolean paramBoolean, int paramInt)
    {
      int i;
      if (paramBoolean)
      {
        i = getEndLine(Integer.MIN_VALUE);
        clear();
        if (i != Integer.MIN_VALUE) {
          break label32;
        }
      }
      label32:
      while (((paramBoolean) && (i < StaggeredGridLayoutManager.this.mPrimaryOrientation.getEndAfterPadding())) || ((!paramBoolean) && (i > StaggeredGridLayoutManager.this.mPrimaryOrientation.getStartAfterPadding())))
      {
        return;
        i = getStartLine(Integer.MIN_VALUE);
        break;
      }
      if (paramInt != Integer.MIN_VALUE) {
        i += paramInt;
      }
      this.mCachedEnd = i;
      this.mCachedStart = i;
    }
    
    void calculateCachedEnd()
    {
      View localView = (View)this.mViews.get(-1 + this.mViews.size());
      StaggeredGridLayoutManager.LayoutParams localLayoutParams = getLayoutParams(localView);
      this.mCachedEnd = StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedEnd(localView);
      if (localLayoutParams.mFullSpan)
      {
        StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem localFullSpanItem = StaggeredGridLayoutManager.this.mLazySpanLookup.getFullSpanItem(localLayoutParams.getViewLayoutPosition());
        if ((localFullSpanItem != null) && (localFullSpanItem.mGapDir == 1)) {
          this.mCachedEnd += localFullSpanItem.getGapForSpan(this.mIndex);
        }
      }
    }
    
    void calculateCachedStart()
    {
      View localView = (View)this.mViews.get(0);
      StaggeredGridLayoutManager.LayoutParams localLayoutParams = getLayoutParams(localView);
      this.mCachedStart = StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedStart(localView);
      if (localLayoutParams.mFullSpan)
      {
        StaggeredGridLayoutManager.LazySpanLookup.FullSpanItem localFullSpanItem = StaggeredGridLayoutManager.this.mLazySpanLookup.getFullSpanItem(localLayoutParams.getViewLayoutPosition());
        if ((localFullSpanItem != null) && (localFullSpanItem.mGapDir == -1)) {
          this.mCachedStart -= localFullSpanItem.getGapForSpan(this.mIndex);
        }
      }
    }
    
    void clear()
    {
      this.mViews.clear();
      invalidateCache();
      this.mDeletedSize = 0;
    }
    
    public int findFirstCompletelyVisibleItemPosition()
    {
      if (StaggeredGridLayoutManager.this.mReverseLayout) {
        return findOneVisibleChild(-1 + this.mViews.size(), -1, true);
      }
      return findOneVisibleChild(0, this.mViews.size(), true);
    }
    
    public int findFirstPartiallyVisibleItemPosition()
    {
      if (StaggeredGridLayoutManager.this.mReverseLayout) {
        return findOnePartiallyVisibleChild(-1 + this.mViews.size(), -1, true);
      }
      return findOnePartiallyVisibleChild(0, this.mViews.size(), true);
    }
    
    public int findFirstVisibleItemPosition()
    {
      if (StaggeredGridLayoutManager.this.mReverseLayout) {
        return findOneVisibleChild(-1 + this.mViews.size(), -1, false);
      }
      return findOneVisibleChild(0, this.mViews.size(), false);
    }
    
    public int findLastCompletelyVisibleItemPosition()
    {
      if (StaggeredGridLayoutManager.this.mReverseLayout) {
        return findOneVisibleChild(0, this.mViews.size(), true);
      }
      return findOneVisibleChild(-1 + this.mViews.size(), -1, true);
    }
    
    public int findLastPartiallyVisibleItemPosition()
    {
      if (StaggeredGridLayoutManager.this.mReverseLayout) {
        return findOnePartiallyVisibleChild(0, this.mViews.size(), true);
      }
      return findOnePartiallyVisibleChild(-1 + this.mViews.size(), -1, true);
    }
    
    public int findLastVisibleItemPosition()
    {
      if (StaggeredGridLayoutManager.this.mReverseLayout) {
        return findOneVisibleChild(0, this.mViews.size(), false);
      }
      return findOneVisibleChild(-1 + this.mViews.size(), -1, false);
    }
    
    int findOnePartiallyOrCompletelyVisibleChild(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
    {
      int i = StaggeredGridLayoutManager.this.mPrimaryOrientation.getStartAfterPadding();
      int j = StaggeredGridLayoutManager.this.mPrimaryOrientation.getEndAfterPadding();
      int k;
      int m;
      if (paramInt2 > paramInt1)
      {
        k = 1;
        m = paramInt1;
      }
      for (;;)
      {
        if (m == paramInt2) {
          break label261;
        }
        View localView = (View)this.mViews.get(m);
        int n = StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedStart(localView);
        int i1 = StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedEnd(localView);
        int i2;
        label98:
        int i3;
        if (paramBoolean3) {
          if (n <= j)
          {
            i2 = 1;
            if (!paramBoolean3) {
              break label193;
            }
            if (i1 < i) {
              break label187;
            }
            i3 = 1;
          }
        }
        for (;;)
        {
          if ((i2 != 0) && (i3 != 0))
          {
            if ((paramBoolean1) && (paramBoolean2))
            {
              if ((n < i) || (i1 > j)) {
                break label251;
              }
              return StaggeredGridLayoutManager.this.getPosition(localView);
              k = -1;
              break;
              i2 = 0;
              break label98;
              if (n < j)
              {
                i2 = 1;
                break label98;
              }
              i2 = 0;
              break label98;
              label187:
              i3 = 0;
              continue;
              label193:
              if (i1 > i)
              {
                i3 = 1;
                continue;
              }
              i3 = 0;
              continue;
            }
            if (paramBoolean2) {
              return StaggeredGridLayoutManager.this.getPosition(localView);
            }
            if ((n < i) || (i1 > j)) {
              return StaggeredGridLayoutManager.this.getPosition(localView);
            }
          }
        }
        label251:
        m += k;
      }
      label261:
      return -1;
    }
    
    int findOnePartiallyVisibleChild(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      return findOnePartiallyOrCompletelyVisibleChild(paramInt1, paramInt2, false, false, paramBoolean);
    }
    
    int findOneVisibleChild(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      return findOnePartiallyOrCompletelyVisibleChild(paramInt1, paramInt2, paramBoolean, true, false);
    }
    
    public int getDeletedSize()
    {
      return this.mDeletedSize;
    }
    
    int getEndLine()
    {
      if (this.mCachedEnd != Integer.MIN_VALUE) {
        return this.mCachedEnd;
      }
      calculateCachedEnd();
      return this.mCachedEnd;
    }
    
    int getEndLine(int paramInt)
    {
      if (this.mCachedEnd != Integer.MIN_VALUE) {
        paramInt = this.mCachedEnd;
      }
      while (this.mViews.size() == 0) {
        return paramInt;
      }
      calculateCachedEnd();
      return this.mCachedEnd;
    }
    
    public View getFocusableViewAfter(int paramInt1, int paramInt2)
    {
      Object localObject = null;
      int k;
      View localView2;
      if (paramInt2 == -1)
      {
        int j = this.mViews.size();
        k = 0;
        if (k < j)
        {
          localView2 = (View)this.mViews.get(k);
          if (((!StaggeredGridLayoutManager.this.mReverseLayout) || (StaggeredGridLayoutManager.this.getPosition(localView2) > paramInt1)) && ((StaggeredGridLayoutManager.this.mReverseLayout) || (StaggeredGridLayoutManager.this.getPosition(localView2) < paramInt1))) {
            break label88;
          }
        }
      }
      label88:
      label196:
      for (;;)
      {
        return localObject;
        if (localView2.hasFocusable())
        {
          localObject = localView2;
          k++;
          break;
          for (int i = -1 + this.mViews.size();; i--)
          {
            if (i < 0) {
              break label196;
            }
            View localView1 = (View)this.mViews.get(i);
            if (((StaggeredGridLayoutManager.this.mReverseLayout) && (StaggeredGridLayoutManager.this.getPosition(localView1) >= paramInt1)) || ((!StaggeredGridLayoutManager.this.mReverseLayout) && (StaggeredGridLayoutManager.this.getPosition(localView1) <= paramInt1)) || (!localView1.hasFocusable())) {
              break;
            }
            localObject = localView1;
          }
        }
      }
    }
    
    StaggeredGridLayoutManager.LayoutParams getLayoutParams(View paramView)
    {
      return (StaggeredGridLayoutManager.LayoutParams)paramView.getLayoutParams();
    }
    
    int getStartLine()
    {
      if (this.mCachedStart != Integer.MIN_VALUE) {
        return this.mCachedStart;
      }
      calculateCachedStart();
      return this.mCachedStart;
    }
    
    int getStartLine(int paramInt)
    {
      if (this.mCachedStart != Integer.MIN_VALUE) {
        paramInt = this.mCachedStart;
      }
      while (this.mViews.size() == 0) {
        return paramInt;
      }
      calculateCachedStart();
      return this.mCachedStart;
    }
    
    void invalidateCache()
    {
      this.mCachedStart = Integer.MIN_VALUE;
      this.mCachedEnd = Integer.MIN_VALUE;
    }
    
    void onOffset(int paramInt)
    {
      if (this.mCachedStart != Integer.MIN_VALUE) {
        this.mCachedStart = (paramInt + this.mCachedStart);
      }
      if (this.mCachedEnd != Integer.MIN_VALUE) {
        this.mCachedEnd = (paramInt + this.mCachedEnd);
      }
    }
    
    void popEnd()
    {
      int i = this.mViews.size();
      View localView = (View)this.mViews.remove(i - 1);
      StaggeredGridLayoutManager.LayoutParams localLayoutParams = getLayoutParams(localView);
      localLayoutParams.mSpan = null;
      if ((localLayoutParams.isItemRemoved()) || (localLayoutParams.isItemChanged())) {
        this.mDeletedSize -= StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedMeasurement(localView);
      }
      if (i == 1) {
        this.mCachedStart = Integer.MIN_VALUE;
      }
      this.mCachedEnd = Integer.MIN_VALUE;
    }
    
    void popStart()
    {
      View localView = (View)this.mViews.remove(0);
      StaggeredGridLayoutManager.LayoutParams localLayoutParams = getLayoutParams(localView);
      localLayoutParams.mSpan = null;
      if (this.mViews.size() == 0) {
        this.mCachedEnd = Integer.MIN_VALUE;
      }
      if ((localLayoutParams.isItemRemoved()) || (localLayoutParams.isItemChanged())) {
        this.mDeletedSize -= StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedMeasurement(localView);
      }
      this.mCachedStart = Integer.MIN_VALUE;
    }
    
    void prependToSpan(View paramView)
    {
      StaggeredGridLayoutManager.LayoutParams localLayoutParams = getLayoutParams(paramView);
      localLayoutParams.mSpan = this;
      this.mViews.add(0, paramView);
      this.mCachedStart = Integer.MIN_VALUE;
      if (this.mViews.size() == 1) {
        this.mCachedEnd = Integer.MIN_VALUE;
      }
      if ((localLayoutParams.isItemRemoved()) || (localLayoutParams.isItemChanged())) {
        this.mDeletedSize += StaggeredGridLayoutManager.this.mPrimaryOrientation.getDecoratedMeasurement(paramView);
      }
    }
    
    void setLine(int paramInt)
    {
      this.mCachedStart = paramInt;
      this.mCachedEnd = paramInt;
    }
  }
}
