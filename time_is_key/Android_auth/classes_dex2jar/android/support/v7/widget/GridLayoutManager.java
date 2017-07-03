package android.support.v7.widget;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.CollectionItemInfoCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import java.util.Arrays;

public class GridLayoutManager
  extends LinearLayoutManager
{
  private static final boolean DEBUG = false;
  public static final int DEFAULT_SPAN_COUNT = -1;
  private static final String TAG = "GridLayoutManager";
  int[] mCachedBorders;
  final Rect mDecorInsets = new Rect();
  boolean mPendingSpanCountChange = false;
  final SparseIntArray mPreLayoutSpanIndexCache = new SparseIntArray();
  final SparseIntArray mPreLayoutSpanSizeCache = new SparseIntArray();
  View[] mSet;
  int mSpanCount = -1;
  SpanSizeLookup mSpanSizeLookup = new DefaultSpanSizeLookup();
  
  public GridLayoutManager(Context paramContext, int paramInt)
  {
    super(paramContext);
    setSpanCount(paramInt);
  }
  
  public GridLayoutManager(Context paramContext, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    super(paramContext, paramInt2, paramBoolean);
    setSpanCount(paramInt1);
  }
  
  public GridLayoutManager(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    super(paramContext, paramAttributeSet, paramInt1, paramInt2);
    setSpanCount(getProperties(paramContext, paramAttributeSet, paramInt1, paramInt2).spanCount);
  }
  
  private void assignSpans(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int i;
    int j;
    if (paramBoolean)
    {
      i = 0;
      j = paramInt1;
    }
    for (int k = 1;; k = -1)
    {
      int m = 0;
      int n = i;
      while (n != j)
      {
        View localView = this.mSet[n];
        LayoutParams localLayoutParams = (LayoutParams)localView.getLayoutParams();
        localLayoutParams.mSpanSize = getSpanSize(paramRecycler, paramState, getPosition(localView));
        localLayoutParams.mSpanIndex = m;
        m += localLayoutParams.mSpanSize;
        n += k;
      }
      i = paramInt1 - 1;
      j = -1;
    }
  }
  
  private void cachePreLayoutSpanMapping()
  {
    int i = getChildCount();
    for (int j = 0; j < i; j++)
    {
      LayoutParams localLayoutParams = (LayoutParams)getChildAt(j).getLayoutParams();
      int k = localLayoutParams.getViewLayoutPosition();
      this.mPreLayoutSpanSizeCache.put(k, localLayoutParams.getSpanSize());
      this.mPreLayoutSpanIndexCache.put(k, localLayoutParams.getSpanIndex());
    }
  }
  
  private void calculateItemBorders(int paramInt)
  {
    this.mCachedBorders = calculateItemBorders(this.mCachedBorders, this.mSpanCount, paramInt);
  }
  
  static int[] calculateItemBorders(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    if ((paramArrayOfInt == null) || (paramArrayOfInt.length != paramInt1 + 1) || (paramArrayOfInt[(-1 + paramArrayOfInt.length)] != paramInt2)) {
      paramArrayOfInt = new int[paramInt1 + 1];
    }
    paramArrayOfInt[0] = 0;
    int i = paramInt2 / paramInt1;
    int j = paramInt2 % paramInt1;
    int k = 0;
    int m = 0;
    for (int n = 1; n <= paramInt1; n++)
    {
      int i1 = i;
      m += j;
      if ((m > 0) && (paramInt1 - m < j))
      {
        i1++;
        m -= paramInt1;
      }
      k += i1;
      paramArrayOfInt[n] = k;
    }
    return paramArrayOfInt;
  }
  
  private void clearPreLayoutSpanMappingCache()
  {
    this.mPreLayoutSpanSizeCache.clear();
    this.mPreLayoutSpanIndexCache.clear();
  }
  
  private void ensureAnchorIsInCorrectSpan(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, LinearLayoutManager.AnchorInfo paramAnchorInfo, int paramInt)
  {
    int i = 1;
    if (paramInt == i) {}
    int j;
    for (;;)
    {
      j = getSpanIndex(paramRecycler, paramState, paramAnchorInfo.mPosition);
      if (i == 0) {
        break;
      }
      while ((j > 0) && (paramAnchorInfo.mPosition > 0))
      {
        paramAnchorInfo.mPosition = (-1 + paramAnchorInfo.mPosition);
        j = getSpanIndex(paramRecycler, paramState, paramAnchorInfo.mPosition);
      }
      i = 0;
    }
    int k = -1 + paramState.getItemCount();
    int m = paramAnchorInfo.mPosition;
    int i1;
    for (int n = j; m < k; n = i1)
    {
      i1 = getSpanIndex(paramRecycler, paramState, m + 1);
      if (i1 <= n) {
        break;
      }
      m++;
    }
    paramAnchorInfo.mPosition = m;
  }
  
  private void ensureViewSet()
  {
    if ((this.mSet == null) || (this.mSet.length != this.mSpanCount)) {
      this.mSet = new View[this.mSpanCount];
    }
  }
  
  private int getSpanGroupIndex(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, int paramInt)
  {
    if (!paramState.isPreLayout()) {
      return this.mSpanSizeLookup.getSpanGroupIndex(paramInt, this.mSpanCount);
    }
    int i = paramRecycler.convertPreLayoutPositionToPostLayout(paramInt);
    if (i == -1)
    {
      Log.w("GridLayoutManager", "Cannot find span size for pre layout position. " + paramInt);
      return 0;
    }
    return this.mSpanSizeLookup.getSpanGroupIndex(i, this.mSpanCount);
  }
  
  private int getSpanIndex(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, int paramInt)
  {
    int i;
    if (!paramState.isPreLayout()) {
      i = this.mSpanSizeLookup.getCachedSpanIndex(paramInt, this.mSpanCount);
    }
    do
    {
      return i;
      i = this.mPreLayoutSpanIndexCache.get(paramInt, -1);
    } while (i != -1);
    int j = paramRecycler.convertPreLayoutPositionToPostLayout(paramInt);
    if (j == -1)
    {
      Log.w("GridLayoutManager", "Cannot find span size for pre layout position. It is not cached, not in the adapter. Pos:" + paramInt);
      return 0;
    }
    return this.mSpanSizeLookup.getCachedSpanIndex(j, this.mSpanCount);
  }
  
  private int getSpanSize(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, int paramInt)
  {
    int i;
    if (!paramState.isPreLayout()) {
      i = this.mSpanSizeLookup.getSpanSize(paramInt);
    }
    do
    {
      return i;
      i = this.mPreLayoutSpanSizeCache.get(paramInt, -1);
    } while (i != -1);
    int j = paramRecycler.convertPreLayoutPositionToPostLayout(paramInt);
    if (j == -1)
    {
      Log.w("GridLayoutManager", "Cannot find span size for pre layout position. It is not cached, not in the adapter. Pos:" + paramInt);
      return 1;
    }
    return this.mSpanSizeLookup.getSpanSize(j);
  }
  
  private void guessMeasurement(float paramFloat, int paramInt)
  {
    calculateItemBorders(Math.max(Math.round(paramFloat * this.mSpanCount), paramInt));
  }
  
  private void measureChild(View paramView, int paramInt, boolean paramBoolean)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    Rect localRect = localLayoutParams.mDecorInsets;
    int i = localRect.top + localRect.bottom + localLayoutParams.topMargin + localLayoutParams.bottomMargin;
    int j = localRect.left + localRect.right + localLayoutParams.leftMargin + localLayoutParams.rightMargin;
    int k = getSpaceForSpanRange(localLayoutParams.mSpanIndex, localLayoutParams.mSpanSize);
    int n;
    int m;
    if (this.mOrientation == 1)
    {
      n = getChildMeasureSpec(k, paramInt, j, localLayoutParams.width, false);
      m = getChildMeasureSpec(this.mOrientationHelper.getTotalSpace(), getHeightMode(), i, localLayoutParams.height, true);
    }
    for (;;)
    {
      measureChildWithDecorationsAndMargin(paramView, n, m, paramBoolean);
      return;
      m = getChildMeasureSpec(k, paramInt, i, localLayoutParams.height, false);
      n = getChildMeasureSpec(this.mOrientationHelper.getTotalSpace(), getWidthMode(), j, localLayoutParams.width, true);
    }
  }
  
  private void measureChildWithDecorationsAndMargin(View paramView, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    RecyclerView.LayoutParams localLayoutParams = (RecyclerView.LayoutParams)paramView.getLayoutParams();
    if (paramBoolean) {}
    for (boolean bool = shouldReMeasureChild(paramView, paramInt1, paramInt2, localLayoutParams);; bool = shouldMeasureChild(paramView, paramInt1, paramInt2, localLayoutParams))
    {
      if (bool) {
        paramView.measure(paramInt1, paramInt2);
      }
      return;
    }
  }
  
  private void updateMeasurements()
  {
    if (getOrientation() == 1) {}
    for (int i = getWidth() - getPaddingRight() - getPaddingLeft();; i = getHeight() - getPaddingBottom() - getPaddingTop())
    {
      calculateItemBorders(i);
      return;
    }
  }
  
  public boolean checkLayoutParams(RecyclerView.LayoutParams paramLayoutParams)
  {
    return paramLayoutParams instanceof LayoutParams;
  }
  
  void collectPrefetchPositionsForLayoutState(RecyclerView.State paramState, LinearLayoutManager.LayoutState paramLayoutState, RecyclerView.LayoutManager.LayoutPrefetchRegistry paramLayoutPrefetchRegistry)
  {
    int i = this.mSpanCount;
    for (int j = 0; (j < this.mSpanCount) && (paramLayoutState.hasMore(paramState)) && (i > 0); j++)
    {
      int k = paramLayoutState.mCurrentPosition;
      paramLayoutPrefetchRegistry.addPosition(k, Math.max(0, paramLayoutState.mScrollingOffset));
      i -= this.mSpanSizeLookup.getSpanSize(k);
      paramLayoutState.mCurrentPosition += paramLayoutState.mItemDirection;
    }
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
        break label168;
      }
      localObject3 = getChildAt(m);
      int n = getPosition((View)localObject3);
      if ((n >= 0) && (n < paramInt3) && (getSpanIndex(paramRecycler, paramState, n) == 0)) {
        break label102;
      }
    }
    for (;;)
    {
      m += k;
      break label40;
      k = -1;
      break;
      label102:
      if (((RecyclerView.LayoutParams)((View)localObject3).getLayoutParams()).isItemRemoved())
      {
        if (localObject1 == null) {
          localObject1 = localObject3;
        }
      }
      else
      {
        if ((this.mOrientationHelper.getDecoratedStart((View)localObject3) < j) && (this.mOrientationHelper.getDecoratedEnd((View)localObject3) >= i)) {
          break label177;
        }
        if (localObject2 == null) {
          localObject2 = localObject3;
        }
      }
    }
    label168:
    if (localObject2 != null) {}
    for (;;)
    {
      localObject3 = localObject2;
      label177:
      return localObject3;
      localObject2 = localObject1;
    }
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
    if (paramState.getItemCount() < 1) {
      return 0;
    }
    return 1 + getSpanGroupIndex(paramRecycler, paramState, -1 + paramState.getItemCount());
  }
  
  public int getRowCountForAccessibility(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    if (this.mOrientation == 0) {
      return this.mSpanCount;
    }
    if (paramState.getItemCount() < 1) {
      return 0;
    }
    return 1 + getSpanGroupIndex(paramRecycler, paramState, -1 + paramState.getItemCount());
  }
  
  int getSpaceForSpanRange(int paramInt1, int paramInt2)
  {
    if ((this.mOrientation == 1) && (isLayoutRTL())) {
      return this.mCachedBorders[(this.mSpanCount - paramInt1)] - this.mCachedBorders[(this.mSpanCount - paramInt1 - paramInt2)];
    }
    return this.mCachedBorders[(paramInt1 + paramInt2)] - this.mCachedBorders[paramInt1];
  }
  
  public int getSpanCount()
  {
    return this.mSpanCount;
  }
  
  public SpanSizeLookup getSpanSizeLookup()
  {
    return this.mSpanSizeLookup;
  }
  
  void layoutChunk(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, LinearLayoutManager.LayoutState paramLayoutState, LinearLayoutManager.LayoutChunkResult paramLayoutChunkResult)
  {
    int i = this.mOrientationHelper.getModeInOther();
    int j;
    int k;
    label38:
    boolean bool;
    label58:
    int m;
    int n;
    int i1;
    if (i != 1073741824)
    {
      j = 1;
      if (getChildCount() <= 0) {
        break label210;
      }
      k = this.mCachedBorders[this.mSpanCount];
      if (j != 0) {
        updateMeasurements();
      }
      if (paramLayoutState.mItemDirection != 1) {
        break label216;
      }
      bool = true;
      m = this.mSpanCount;
      n = 0;
      i1 = 0;
      if (!bool) {
        m = getSpanIndex(paramRecycler, paramState, paramLayoutState.mCurrentPosition) + getSpanSize(paramRecycler, paramState, paramLayoutState.mCurrentPosition);
      }
    }
    for (;;)
    {
      int i19;
      if ((n < this.mSpanCount) && (paramLayoutState.hasMore(paramState)) && (m > 0))
      {
        int i18 = paramLayoutState.mCurrentPosition;
        i19 = getSpanSize(paramRecycler, paramState, i18);
        if (i19 > this.mSpanCount)
        {
          throw new IllegalArgumentException("Item at position " + i18 + " requires " + i19 + " spans but GridLayoutManager has only " + this.mSpanCount + " spans.");
          j = 0;
          break;
          label210:
          k = 0;
          break label38;
          label216:
          bool = false;
          break label58;
        }
        m -= i19;
        if (m >= 0) {
          break label246;
        }
      }
      label246:
      View localView5;
      do
      {
        if (n != 0) {
          break;
        }
        paramLayoutChunkResult.mFinished = true;
        return;
        localView5 = paramLayoutState.next(paramRecycler);
      } while (localView5 == null);
      i1 += i19;
      this.mSet[n] = localView5;
      n++;
    }
    int i2 = 0;
    float f1 = 0.0F;
    assignSpans(paramRecycler, paramState, n, i1, bool);
    int i3 = 0;
    if (i3 < n)
    {
      View localView4 = this.mSet[i3];
      if (paramLayoutState.mScrapList == null) {
        if (bool) {
          addView(localView4);
        }
      }
      for (;;)
      {
        calculateItemDecorationsForChild(localView4, this.mDecorInsets);
        measureChild(localView4, i, false);
        int i17 = this.mOrientationHelper.getDecoratedMeasurement(localView4);
        if (i17 > i2) {
          i2 = i17;
        }
        LayoutParams localLayoutParams3 = (LayoutParams)localView4.getLayoutParams();
        float f2 = 1.0F * this.mOrientationHelper.getDecoratedMeasurementInOther(localView4) / localLayoutParams3.mSpanSize;
        if (f2 > f1) {
          f1 = f2;
        }
        i3++;
        break;
        addView(localView4, 0);
        continue;
        if (bool) {
          addDisappearingView(localView4);
        } else {
          addDisappearingView(localView4, 0);
        }
      }
    }
    if (j != 0)
    {
      guessMeasurement(f1, k);
      i2 = 0;
      for (int i15 = 0; i15 < n; i15++)
      {
        View localView3 = this.mSet[i15];
        measureChild(localView3, 1073741824, true);
        int i16 = this.mOrientationHelper.getDecoratedMeasurement(localView3);
        if (i16 > i2) {
          i2 = i16;
        }
      }
    }
    int i4 = 0;
    if (i4 < n)
    {
      View localView2 = this.mSet[i4];
      LayoutParams localLayoutParams2;
      int i10;
      int i11;
      int i12;
      int i13;
      if (this.mOrientationHelper.getDecoratedMeasurement(localView2) != i2)
      {
        localLayoutParams2 = (LayoutParams)localView2.getLayoutParams();
        Rect localRect = localLayoutParams2.mDecorInsets;
        i10 = localRect.top + localRect.bottom + localLayoutParams2.topMargin + localLayoutParams2.bottomMargin;
        i11 = localRect.left + localRect.right + localLayoutParams2.leftMargin + localLayoutParams2.rightMargin;
        i12 = getSpaceForSpanRange(localLayoutParams2.mSpanIndex, localLayoutParams2.mSpanSize);
        if (this.mOrientation != 1) {
          break label704;
        }
        i13 = getChildMeasureSpec(i12, 1073741824, i11, localLayoutParams2.width, false);
      }
      for (int i14 = View.MeasureSpec.makeMeasureSpec(i2 - i10, 1073741824);; i14 = getChildMeasureSpec(i12, 1073741824, i10, localLayoutParams2.height, false))
      {
        measureChildWithDecorationsAndMargin(localView2, i13, i14, true);
        i4++;
        break;
        label704:
        i13 = View.MeasureSpec.makeMeasureSpec(i2 - i11, 1073741824);
      }
    }
    paramLayoutChunkResult.mConsumed = i2;
    int i5 = 0;
    int i6 = 0;
    int i8;
    int i7;
    int i9;
    label783:
    View localView1;
    LayoutParams localLayoutParams1;
    if (this.mOrientation == 1) {
      if (paramLayoutState.mLayoutDirection == -1)
      {
        i8 = paramLayoutState.mOffset;
        i7 = i8 - i2;
        i9 = 0;
        if (i9 >= n) {
          break label1060;
        }
        localView1 = this.mSet[i9];
        localLayoutParams1 = (LayoutParams)localView1.getLayoutParams();
        if (this.mOrientation != 1) {
          break label1026;
        }
        if (!isLayoutRTL()) {
          break label992;
        }
        i6 = getPaddingLeft() + this.mCachedBorders[(this.mSpanCount - localLayoutParams1.mSpanIndex)];
        i5 = i6 - this.mOrientationHelper.getDecoratedMeasurementInOther(localView1);
      }
    }
    for (;;)
    {
      layoutDecoratedWithMargins(localView1, i5, i7, i6, i8);
      if ((localLayoutParams1.isItemRemoved()) || (localLayoutParams1.isItemChanged())) {
        paramLayoutChunkResult.mIgnoreConsumed = true;
      }
      paramLayoutChunkResult.mFocusable |= localView1.hasFocusable();
      i9++;
      break label783;
      i7 = paramLayoutState.mOffset;
      i8 = i7 + i2;
      i5 = 0;
      i6 = 0;
      break;
      if (paramLayoutState.mLayoutDirection == -1)
      {
        i6 = paramLayoutState.mOffset;
        i5 = i6 - i2;
        i7 = 0;
        i8 = 0;
        break;
      }
      i5 = paramLayoutState.mOffset;
      i6 = i5 + i2;
      i7 = 0;
      i8 = 0;
      break;
      label992:
      i5 = getPaddingLeft() + this.mCachedBorders[localLayoutParams1.mSpanIndex];
      i6 = i5 + this.mOrientationHelper.getDecoratedMeasurementInOther(localView1);
      continue;
      label1026:
      i7 = getPaddingTop() + this.mCachedBorders[localLayoutParams1.mSpanIndex];
      i8 = i7 + this.mOrientationHelper.getDecoratedMeasurementInOther(localView1);
    }
    label1060:
    Arrays.fill(this.mSet, null);
  }
  
  void onAnchorReady(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, LinearLayoutManager.AnchorInfo paramAnchorInfo, int paramInt)
  {
    super.onAnchorReady(paramRecycler, paramState, paramAnchorInfo, paramInt);
    updateMeasurements();
    if ((paramState.getItemCount() > 0) && (!paramState.isPreLayout())) {
      ensureAnchorIsInCorrectSpan(paramRecycler, paramState, paramAnchorInfo, paramInt);
    }
    ensureViewSet();
  }
  
  public View onFocusSearchFailed(View paramView, int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    View localView1 = findContainingItemView(paramView);
    View localView3;
    if (localView1 == null)
    {
      localView3 = null;
      return localView3;
    }
    LayoutParams localLayoutParams1 = (LayoutParams)localView1.getLayoutParams();
    int i = localLayoutParams1.mSpanIndex;
    int j = localLayoutParams1.mSpanIndex + localLayoutParams1.mSpanSize;
    if (super.onFocusSearchFailed(paramView, paramInt, paramRecycler, paramState) == null) {
      return null;
    }
    boolean bool1;
    label74:
    int k;
    label90:
    int i1;
    int m;
    int n;
    label109:
    int i2;
    label127:
    Object localObject;
    int i3;
    int i4;
    View localView2;
    int i5;
    int i6;
    int i7;
    int i8;
    label160:
    int i9;
    if (convertFocusDirectionToLayoutDirection(paramInt) == 1)
    {
      bool1 = true;
      boolean bool2 = this.mShouldReverseLayout;
      if (bool1 == bool2) {
        break label207;
      }
      k = 1;
      if (k == 0) {
        break label213;
      }
      i1 = -1 + getChildCount();
      m = -1;
      n = -1;
      if ((this.mOrientation != 1) || (!isLayoutRTL())) {
        break label228;
      }
      i2 = 1;
      localObject = null;
      i3 = -1;
      i4 = 0;
      localView2 = null;
      i5 = -1;
      i6 = 0;
      i7 = getSpanGroupIndex(paramRecycler, paramState, i1);
      i8 = i1;
      if (i8 != n)
      {
        i9 = getSpanGroupIndex(paramRecycler, paramState, i8);
        localView3 = getChildAt(i8);
        if (localView3 != localView1) {
          break label234;
        }
      }
      label193:
      if (localObject == null) {
        break label605;
      }
    }
    for (;;)
    {
      return localObject;
      bool1 = false;
      break label74;
      label207:
      k = 0;
      break label90;
      label213:
      m = 1;
      n = getChildCount();
      i1 = 0;
      break label109;
      label228:
      i2 = 0;
      break label127;
      label234:
      if ((localView3.hasFocusable()) && (i9 != i7)) {
        if (localObject != null) {
          break label193;
        }
      }
      for (;;)
      {
        i8 += m;
        break label160;
        LayoutParams localLayoutParams2 = (LayoutParams)localView3.getLayoutParams();
        int i10 = localLayoutParams2.mSpanIndex;
        int i11 = localLayoutParams2.mSpanIndex + localLayoutParams2.mSpanSize;
        if ((localView3.hasFocusable()) && (i10 == i) && (i11 == j)) {
          break;
        }
        int i14;
        if (((localView3.hasFocusable()) && (localObject == null)) || ((!localView3.hasFocusable()) && (localView2 == null))) {
          i14 = 1;
        }
        for (;;)
        {
          label345:
          if (i14 != 0)
          {
            if (!localView3.hasFocusable()) {
              break label574;
            }
            localObject = localView3;
            i3 = localLayoutParams2.mSpanIndex;
            i4 = Math.min(i11, j) - Math.max(i10, i);
            break;
            int i12 = Math.max(i10, i);
            int i13 = Math.min(i11, j) - i12;
            if (localView3.hasFocusable())
            {
              if (i13 > i4)
              {
                i14 = 1;
              }
              else
              {
                i14 = 0;
                if (i13 == i4)
                {
                  if (i10 > i3) {}
                  for (int i19 = 1;; i19 = 0)
                  {
                    int i20 = i2;
                    int i21 = i19;
                    i14 = 0;
                    if (i20 != i21) {
                      break;
                    }
                    i14 = 1;
                    break;
                  }
                }
              }
            }
            else
            {
              i14 = 0;
              if (localObject == null)
              {
                boolean bool3 = isViewPartiallyVisible(localView3, false, true);
                i14 = 0;
                if (bool3) {
                  if (i13 > i6)
                  {
                    i14 = 1;
                  }
                  else
                  {
                    int i15 = i6;
                    i14 = 0;
                    if (i13 == i15) {
                      if (i10 <= i5) {
                        break label568;
                      }
                    }
                  }
                }
              }
            }
          }
        }
        label568:
        for (int i16 = 1;; i16 = 0)
        {
          int i17 = i2;
          int i18 = i16;
          i14 = 0;
          if (i17 != i18) {
            break label345;
          }
          i14 = 1;
          break label345;
          break;
        }
        label574:
        localView2 = localView3;
        i5 = localLayoutParams2.mSpanIndex;
        i6 = Math.min(i11, j) - Math.max(i10, i);
      }
      label605:
      localObject = localView2;
    }
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
    int i = getSpanGroupIndex(paramRecycler, paramState, localLayoutParams1.getViewLayoutPosition());
    if (this.mOrientation == 0)
    {
      int m = localLayoutParams1.getSpanIndex();
      int n = localLayoutParams1.getSpanSize();
      if ((this.mSpanCount > 1) && (localLayoutParams1.getSpanSize() == this.mSpanCount)) {}
      for (boolean bool2 = true;; bool2 = false)
      {
        paramAccessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(m, n, i, 1, bool2, false));
        return;
      }
    }
    int j = localLayoutParams1.getSpanIndex();
    int k = localLayoutParams1.getSpanSize();
    if ((this.mSpanCount > 1) && (localLayoutParams1.getSpanSize() == this.mSpanCount)) {}
    for (boolean bool1 = true;; bool1 = false)
    {
      paramAccessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(i, 1, j, k, bool1, false));
      return;
    }
  }
  
  public void onItemsAdded(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
  {
    this.mSpanSizeLookup.invalidateSpanIndexCache();
  }
  
  public void onItemsChanged(RecyclerView paramRecyclerView)
  {
    this.mSpanSizeLookup.invalidateSpanIndexCache();
  }
  
  public void onItemsMoved(RecyclerView paramRecyclerView, int paramInt1, int paramInt2, int paramInt3)
  {
    this.mSpanSizeLookup.invalidateSpanIndexCache();
  }
  
  public void onItemsRemoved(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
  {
    this.mSpanSizeLookup.invalidateSpanIndexCache();
  }
  
  public void onItemsUpdated(RecyclerView paramRecyclerView, int paramInt1, int paramInt2, Object paramObject)
  {
    this.mSpanSizeLookup.invalidateSpanIndexCache();
  }
  
  public void onLayoutChildren(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    if (paramState.isPreLayout()) {
      cachePreLayoutSpanMapping();
    }
    super.onLayoutChildren(paramRecycler, paramState);
    clearPreLayoutSpanMappingCache();
  }
  
  public void onLayoutCompleted(RecyclerView.State paramState)
  {
    super.onLayoutCompleted(paramState);
    this.mPendingSpanCountChange = false;
  }
  
  public int scrollHorizontallyBy(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    updateMeasurements();
    ensureViewSet();
    return super.scrollHorizontallyBy(paramInt, paramRecycler, paramState);
  }
  
  public int scrollVerticallyBy(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
  {
    updateMeasurements();
    ensureViewSet();
    return super.scrollVerticallyBy(paramInt, paramRecycler, paramState);
  }
  
  public void setMeasuredDimension(Rect paramRect, int paramInt1, int paramInt2)
  {
    if (this.mCachedBorders == null) {
      super.setMeasuredDimension(paramRect, paramInt1, paramInt2);
    }
    int i = getPaddingLeft() + getPaddingRight();
    int j = getPaddingTop() + getPaddingBottom();
    int m;
    int k;
    if (this.mOrientation == 1)
    {
      m = chooseSize(paramInt2, j + paramRect.height(), getMinimumHeight());
      k = chooseSize(paramInt1, i + this.mCachedBorders[(-1 + this.mCachedBorders.length)], getMinimumWidth());
    }
    for (;;)
    {
      setMeasuredDimension(k, m);
      return;
      k = chooseSize(paramInt1, i + paramRect.width(), getMinimumWidth());
      m = chooseSize(paramInt2, j + this.mCachedBorders[(-1 + this.mCachedBorders.length)], getMinimumHeight());
    }
  }
  
  public void setSpanCount(int paramInt)
  {
    if (paramInt == this.mSpanCount) {
      return;
    }
    this.mPendingSpanCountChange = true;
    if (paramInt < 1) {
      throw new IllegalArgumentException("Span count should be at least 1. Provided " + paramInt);
    }
    this.mSpanCount = paramInt;
    this.mSpanSizeLookup.invalidateSpanIndexCache();
    requestLayout();
  }
  
  public void setSpanSizeLookup(SpanSizeLookup paramSpanSizeLookup)
  {
    this.mSpanSizeLookup = paramSpanSizeLookup;
  }
  
  public void setStackFromEnd(boolean paramBoolean)
  {
    if (paramBoolean) {
      throw new UnsupportedOperationException("GridLayoutManager does not support stack from end. Consider using reverse layout");
    }
    super.setStackFromEnd(false);
  }
  
  public boolean supportsPredictiveItemAnimations()
  {
    return (this.mPendingSavedState == null) && (!this.mPendingSpanCountChange);
  }
  
  public static final class DefaultSpanSizeLookup
    extends GridLayoutManager.SpanSizeLookup
  {
    public DefaultSpanSizeLookup() {}
    
    public int getSpanIndex(int paramInt1, int paramInt2)
    {
      return paramInt1 % paramInt2;
    }
    
    public int getSpanSize(int paramInt)
    {
      return 1;
    }
  }
  
  public static class LayoutParams
    extends RecyclerView.LayoutParams
  {
    public static final int INVALID_SPAN_ID = -1;
    int mSpanIndex = -1;
    int mSpanSize = 0;
    
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
    
    public int getSpanIndex()
    {
      return this.mSpanIndex;
    }
    
    public int getSpanSize()
    {
      return this.mSpanSize;
    }
  }
  
  public static abstract class SpanSizeLookup
  {
    private boolean mCacheSpanIndices = false;
    final SparseIntArray mSpanIndexCache = new SparseIntArray();
    
    public SpanSizeLookup() {}
    
    int findReferenceIndexFromCache(int paramInt)
    {
      int i = 0;
      int j = -1 + this.mSpanIndexCache.size();
      while (i <= j)
      {
        int m = i + j >>> 1;
        if (this.mSpanIndexCache.keyAt(m) < paramInt) {
          i = m + 1;
        } else {
          j = m - 1;
        }
      }
      int k = i - 1;
      if ((k >= 0) && (k < this.mSpanIndexCache.size())) {
        return this.mSpanIndexCache.keyAt(k);
      }
      return -1;
    }
    
    int getCachedSpanIndex(int paramInt1, int paramInt2)
    {
      int i;
      if (!this.mCacheSpanIndices) {
        i = getSpanIndex(paramInt1, paramInt2);
      }
      do
      {
        return i;
        i = this.mSpanIndexCache.get(paramInt1, -1);
      } while (i != -1);
      int j = getSpanIndex(paramInt1, paramInt2);
      this.mSpanIndexCache.put(paramInt1, j);
      return j;
    }
    
    public int getSpanGroupIndex(int paramInt1, int paramInt2)
    {
      int i = 0;
      int j = 0;
      int k = getSpanSize(paramInt1);
      int m = 0;
      if (m < paramInt1)
      {
        int n = getSpanSize(m);
        i += n;
        if (i == paramInt2)
        {
          i = 0;
          j++;
        }
        for (;;)
        {
          m++;
          break;
          if (i > paramInt2)
          {
            i = n;
            j++;
          }
        }
      }
      if (i + k > paramInt2) {
        j++;
      }
      return j;
    }
    
    public int getSpanIndex(int paramInt1, int paramInt2)
    {
      int i = getSpanSize(paramInt1);
      int j;
      if (i == paramInt2) {
        j = 0;
      }
      do
      {
        return j;
        boolean bool = this.mCacheSpanIndices;
        j = 0;
        int k = 0;
        if (bool)
        {
          int i1 = this.mSpanIndexCache.size();
          j = 0;
          k = 0;
          if (i1 > 0)
          {
            int i2 = findReferenceIndexFromCache(paramInt1);
            j = 0;
            k = 0;
            if (i2 >= 0)
            {
              j = this.mSpanIndexCache.get(i2) + getSpanSize(i2);
              k = i2 + 1;
            }
          }
        }
        int m = k;
        if (m < paramInt1)
        {
          int n = getSpanSize(m);
          j += n;
          if (j == paramInt2) {
            j = 0;
          }
          for (;;)
          {
            m++;
            break;
            if (j > paramInt2) {
              j = n;
            }
          }
        }
      } while (j + i <= paramInt2);
      return 0;
    }
    
    public abstract int getSpanSize(int paramInt);
    
    public void invalidateSpanIndexCache()
    {
      this.mSpanIndexCache.clear();
    }
    
    public boolean isSpanIndexCacheEnabled()
    {
      return this.mCacheSpanIndices;
    }
    
    public void setSpanIndexCacheEnabled(boolean paramBoolean)
    {
      this.mCacheSpanIndices = paramBoolean;
    }
  }
}
