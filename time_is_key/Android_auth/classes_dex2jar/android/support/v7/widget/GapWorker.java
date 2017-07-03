package android.support.v7.widget;

import android.support.annotation.Nullable;
import android.support.v4.os.TraceCompat;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

final class GapWorker
  implements Runnable
{
  static final ThreadLocal<GapWorker> sGapWorker = new ThreadLocal();
  static Comparator<Task> sTaskComparator = new Comparator()
  {
    public int compare(GapWorker.Task paramAnonymousTask1, GapWorker.Task paramAnonymousTask2)
    {
      int i = -1;
      int j;
      int k;
      if (paramAnonymousTask1.view == null)
      {
        j = 1;
        if (paramAnonymousTask2.view != null) {
          break label44;
        }
        k = 1;
      }
      for (;;)
      {
        if (j != k)
        {
          if (paramAnonymousTask1.view == null)
          {
            return 1;
            j = 0;
            break;
            label44:
            k = 0;
            continue;
          }
          return i;
        }
      }
      if (paramAnonymousTask1.immediate != paramAnonymousTask2.immediate)
      {
        if (paramAnonymousTask1.immediate) {}
        for (;;)
        {
          return i;
          i = 1;
        }
      }
      int m = paramAnonymousTask2.viewVelocity - paramAnonymousTask1.viewVelocity;
      if (m != 0) {
        return m;
      }
      int n = paramAnonymousTask1.distanceToItem - paramAnonymousTask2.distanceToItem;
      if (n != 0) {
        return n;
      }
      return 0;
    }
  };
  long mFrameIntervalNs;
  long mPostTimeNs;
  ArrayList<RecyclerView> mRecyclerViews = new ArrayList();
  private ArrayList<Task> mTasks = new ArrayList();
  
  GapWorker() {}
  
  private void buildTaskList()
  {
    int i = this.mRecyclerViews.size();
    int j = 0;
    for (int k = 0; k < i; k++)
    {
      RecyclerView localRecyclerView2 = (RecyclerView)this.mRecyclerViews.get(k);
      if (localRecyclerView2.getWindowVisibility() == 0)
      {
        localRecyclerView2.mPrefetchRegistry.collectPrefetchPositionsFromView(localRecyclerView2, false);
        j += localRecyclerView2.mPrefetchRegistry.mCount;
      }
    }
    this.mTasks.ensureCapacity(j);
    int m = 0;
    int n = 0;
    while (n < i)
    {
      RecyclerView localRecyclerView1 = (RecyclerView)this.mRecyclerViews.get(n);
      if (localRecyclerView1.getWindowVisibility() != 0)
      {
        n++;
      }
      else
      {
        LayoutPrefetchRegistryImpl localLayoutPrefetchRegistryImpl = localRecyclerView1.mPrefetchRegistry;
        int i1 = Math.abs(localLayoutPrefetchRegistryImpl.mPrefetchDx) + Math.abs(localLayoutPrefetchRegistryImpl.mPrefetchDy);
        int i2 = 0;
        label143:
        Task localTask;
        label186:
        int i3;
        if (i2 < 2 * localLayoutPrefetchRegistryImpl.mCount)
        {
          if (m < this.mTasks.size()) {
            break label258;
          }
          localTask = new Task();
          this.mTasks.add(localTask);
          i3 = localLayoutPrefetchRegistryImpl.mPrefetchArray[(i2 + 1)];
          if (i3 > i1) {
            break label275;
          }
        }
        label258:
        label275:
        for (boolean bool = true;; bool = false)
        {
          localTask.immediate = bool;
          localTask.viewVelocity = i1;
          localTask.distanceToItem = i3;
          localTask.view = localRecyclerView1;
          localTask.position = localLayoutPrefetchRegistryImpl.mPrefetchArray[i2];
          m++;
          i2 += 2;
          break label143;
          break;
          localTask = (Task)this.mTasks.get(m);
          break label186;
        }
      }
    }
    Collections.sort(this.mTasks, sTaskComparator);
  }
  
  private void flushTaskWithDeadline(Task paramTask, long paramLong)
  {
    if (paramTask.immediate) {}
    for (long l = Long.MAX_VALUE;; l = paramLong)
    {
      RecyclerView.ViewHolder localViewHolder = prefetchPositionWithDeadline(paramTask.view, paramTask.position, l);
      if ((localViewHolder != null) && (localViewHolder.mNestedRecyclerView != null)) {
        prefetchInnerRecyclerViewWithDeadline((RecyclerView)localViewHolder.mNestedRecyclerView.get(), paramLong);
      }
      return;
    }
  }
  
  private void flushTasksWithDeadline(long paramLong)
  {
    for (int i = 0;; i++)
    {
      Task localTask;
      if (i < this.mTasks.size())
      {
        localTask = (Task)this.mTasks.get(i);
        if (localTask.view != null) {}
      }
      else
      {
        return;
      }
      flushTaskWithDeadline(localTask, paramLong);
      localTask.clear();
    }
  }
  
  static boolean isPrefetchPositionAttached(RecyclerView paramRecyclerView, int paramInt)
  {
    int i = paramRecyclerView.mChildHelper.getUnfilteredChildCount();
    for (int j = 0; j < i; j++)
    {
      RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(paramRecyclerView.mChildHelper.getUnfilteredChildAt(j));
      if ((localViewHolder.mPosition == paramInt) && (!localViewHolder.isInvalid())) {
        return true;
      }
    }
    return false;
  }
  
  private void prefetchInnerRecyclerViewWithDeadline(@Nullable RecyclerView paramRecyclerView, long paramLong)
  {
    if (paramRecyclerView == null) {}
    LayoutPrefetchRegistryImpl localLayoutPrefetchRegistryImpl;
    do
    {
      return;
      if ((paramRecyclerView.mDataSetHasChangedAfterLayout) && (paramRecyclerView.mChildHelper.getUnfilteredChildCount() != 0)) {
        paramRecyclerView.removeAndRecycleViews();
      }
      localLayoutPrefetchRegistryImpl = paramRecyclerView.mPrefetchRegistry;
      localLayoutPrefetchRegistryImpl.collectPrefetchPositionsFromView(paramRecyclerView, true);
    } while (localLayoutPrefetchRegistryImpl.mCount == 0);
    try
    {
      TraceCompat.beginSection("RV Nested Prefetch");
      paramRecyclerView.mState.prepareForNestedPrefetch(paramRecyclerView.mAdapter);
      for (int i = 0; i < 2 * localLayoutPrefetchRegistryImpl.mCount; i += 2) {
        prefetchPositionWithDeadline(paramRecyclerView, localLayoutPrefetchRegistryImpl.mPrefetchArray[i], paramLong);
      }
      return;
    }
    finally
    {
      TraceCompat.endSection();
    }
  }
  
  private RecyclerView.ViewHolder prefetchPositionWithDeadline(RecyclerView paramRecyclerView, int paramInt, long paramLong)
  {
    RecyclerView.ViewHolder localViewHolder;
    if (isPrefetchPositionAttached(paramRecyclerView, paramInt)) {
      localViewHolder = null;
    }
    RecyclerView.Recycler localRecycler;
    do
    {
      return localViewHolder;
      localRecycler = paramRecyclerView.mRecycler;
      localViewHolder = localRecycler.tryGetViewHolderForPositionByDeadline(paramInt, false, paramLong);
    } while (localViewHolder == null);
    if (localViewHolder.isBound())
    {
      localRecycler.recycleView(localViewHolder.itemView);
      return localViewHolder;
    }
    localRecycler.addViewHolderToRecycledViewPool(localViewHolder, false);
    return localViewHolder;
  }
  
  public void add(RecyclerView paramRecyclerView)
  {
    this.mRecyclerViews.add(paramRecyclerView);
  }
  
  void postFromTraversal(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
  {
    if ((paramRecyclerView.isAttachedToWindow()) && (this.mPostTimeNs == 0L))
    {
      this.mPostTimeNs = paramRecyclerView.getNanoTime();
      paramRecyclerView.post(this);
    }
    paramRecyclerView.mPrefetchRegistry.setPrefetchVector(paramInt1, paramInt2);
  }
  
  void prefetch(long paramLong)
  {
    buildTaskList();
    flushTasksWithDeadline(paramLong);
  }
  
  public void remove(RecyclerView paramRecyclerView)
  {
    this.mRecyclerViews.remove(paramRecyclerView);
  }
  
  public void run()
  {
    try
    {
      TraceCompat.beginSection("RV Prefetch");
      boolean bool = this.mRecyclerViews.isEmpty();
      if (bool) {
        return;
      }
      int i = this.mRecyclerViews.size();
      long l1 = 0L;
      for (int j = 0; j < i; j++)
      {
        RecyclerView localRecyclerView = (RecyclerView)this.mRecyclerViews.get(j);
        if (localRecyclerView.getWindowVisibility() == 0)
        {
          long l2 = Math.max(localRecyclerView.getDrawingTime(), l1);
          l1 = l2;
        }
      }
      if (l1 == 0L) {
        return;
      }
      prefetch(TimeUnit.MILLISECONDS.toNanos(l1) + this.mFrameIntervalNs);
      return;
    }
    finally
    {
      this.mPostTimeNs = 0L;
      TraceCompat.endSection();
    }
  }
  
  static class LayoutPrefetchRegistryImpl
    implements RecyclerView.LayoutManager.LayoutPrefetchRegistry
  {
    int mCount;
    int[] mPrefetchArray;
    int mPrefetchDx;
    int mPrefetchDy;
    
    LayoutPrefetchRegistryImpl() {}
    
    public void addPosition(int paramInt1, int paramInt2)
    {
      if (paramInt1 < 0) {
        throw new IllegalArgumentException("Layout positions must be non-negative");
      }
      if (paramInt2 < 0) {
        throw new IllegalArgumentException("Pixel distance must be non-negative");
      }
      int i = 2 * this.mCount;
      if (this.mPrefetchArray == null)
      {
        this.mPrefetchArray = new int[4];
        Arrays.fill(this.mPrefetchArray, -1);
      }
      for (;;)
      {
        this.mPrefetchArray[i] = paramInt1;
        this.mPrefetchArray[(i + 1)] = paramInt2;
        this.mCount = (1 + this.mCount);
        return;
        if (i >= this.mPrefetchArray.length)
        {
          int[] arrayOfInt = this.mPrefetchArray;
          this.mPrefetchArray = new int[i * 2];
          System.arraycopy(arrayOfInt, 0, this.mPrefetchArray, 0, arrayOfInt.length);
        }
      }
    }
    
    void clearPrefetchPositions()
    {
      if (this.mPrefetchArray != null) {
        Arrays.fill(this.mPrefetchArray, -1);
      }
      this.mCount = 0;
    }
    
    void collectPrefetchPositionsFromView(RecyclerView paramRecyclerView, boolean paramBoolean)
    {
      this.mCount = 0;
      if (this.mPrefetchArray != null) {
        Arrays.fill(this.mPrefetchArray, -1);
      }
      RecyclerView.LayoutManager localLayoutManager = paramRecyclerView.mLayout;
      if ((paramRecyclerView.mAdapter != null) && (localLayoutManager != null) && (localLayoutManager.isItemPrefetchEnabled()))
      {
        if (!paramBoolean) {
          break label101;
        }
        if (!paramRecyclerView.mAdapterHelper.hasPendingUpdates()) {
          localLayoutManager.collectInitialPrefetchPositions(paramRecyclerView.mAdapter.getItemCount(), this);
        }
      }
      for (;;)
      {
        if (this.mCount > localLayoutManager.mPrefetchMaxCountObserved)
        {
          localLayoutManager.mPrefetchMaxCountObserved = this.mCount;
          localLayoutManager.mPrefetchMaxObservedInInitialPrefetch = paramBoolean;
          paramRecyclerView.mRecycler.updateViewCacheSize();
        }
        return;
        label101:
        if (!paramRecyclerView.hasPendingAdapterUpdates()) {
          localLayoutManager.collectAdjacentPrefetchPositions(this.mPrefetchDx, this.mPrefetchDy, paramRecyclerView.mState, this);
        }
      }
    }
    
    boolean lastPrefetchIncludedPosition(int paramInt)
    {
      if (this.mPrefetchArray != null)
      {
        int i = 2 * this.mCount;
        for (int j = 0; j < i; j += 2) {
          if (this.mPrefetchArray[j] == paramInt) {
            return true;
          }
        }
      }
      return false;
    }
    
    void setPrefetchVector(int paramInt1, int paramInt2)
    {
      this.mPrefetchDx = paramInt1;
      this.mPrefetchDy = paramInt2;
    }
  }
  
  static class Task
  {
    public int distanceToItem;
    public boolean immediate;
    public int position;
    public RecyclerView view;
    public int viewVelocity;
    
    Task() {}
    
    public void clear()
    {
      this.immediate = false;
      this.viewVelocity = 0;
      this.distanceToItem = 0;
      this.view = null;
      this.position = 0;
    }
  }
}
