package android.support.v7.util;

import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;

public class AsyncListUtil<T>
{
  static final boolean DEBUG = false;
  static final String TAG = "AsyncListUtil";
  boolean mAllowScrollHints;
  private final ThreadUtil.BackgroundCallback<T> mBackgroundCallback = new ThreadUtil.BackgroundCallback()
  {
    private int mFirstRequiredTileStart;
    private int mGeneration;
    private int mItemCount;
    private int mLastRequiredTileStart;
    final SparseBooleanArray mLoadedTiles = new SparseBooleanArray();
    private TileList.Tile<T> mRecycledRoot;
    
    private TileList.Tile<T> acquireTile()
    {
      if (this.mRecycledRoot != null)
      {
        TileList.Tile localTile = this.mRecycledRoot;
        this.mRecycledRoot = this.mRecycledRoot.mNext;
        return localTile;
      }
      return new TileList.Tile(AsyncListUtil.this.mTClass, AsyncListUtil.this.mTileSize);
    }
    
    private void addTile(TileList.Tile<T> paramAnonymousTile)
    {
      this.mLoadedTiles.put(paramAnonymousTile.mStartPosition, true);
      AsyncListUtil.this.mMainThreadProxy.addTile(this.mGeneration, paramAnonymousTile);
    }
    
    private void flushTileCache(int paramAnonymousInt)
    {
      int i = AsyncListUtil.this.mDataCallback.getMaxCachedTiles();
      while (this.mLoadedTiles.size() >= i)
      {
        int j = this.mLoadedTiles.keyAt(0);
        int k = this.mLoadedTiles.keyAt(-1 + this.mLoadedTiles.size());
        int m = this.mFirstRequiredTileStart - j;
        int n = k - this.mLastRequiredTileStart;
        if ((m > 0) && ((m >= n) || (paramAnonymousInt == 2)))
        {
          removeTile(j);
        }
        else
        {
          if ((n <= 0) || ((m >= n) && (paramAnonymousInt != 1))) {
            break;
          }
          removeTile(k);
        }
      }
    }
    
    private int getTileStart(int paramAnonymousInt)
    {
      return paramAnonymousInt - paramAnonymousInt % AsyncListUtil.this.mTileSize;
    }
    
    private boolean isTileLoaded(int paramAnonymousInt)
    {
      return this.mLoadedTiles.get(paramAnonymousInt);
    }
    
    private void log(String paramAnonymousString, Object... paramAnonymousVarArgs)
    {
      Log.d("AsyncListUtil", "[BKGR] " + String.format(paramAnonymousString, paramAnonymousVarArgs));
    }
    
    private void removeTile(int paramAnonymousInt)
    {
      this.mLoadedTiles.delete(paramAnonymousInt);
      AsyncListUtil.this.mMainThreadProxy.removeTile(this.mGeneration, paramAnonymousInt);
    }
    
    private void requestTiles(int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, boolean paramAnonymousBoolean)
    {
      int i = paramAnonymousInt1;
      if (i <= paramAnonymousInt2)
      {
        if (paramAnonymousBoolean) {}
        for (int j = paramAnonymousInt2 + paramAnonymousInt1 - i;; j = i)
        {
          AsyncListUtil.this.mBackgroundProxy.loadTile(j, paramAnonymousInt3);
          i += AsyncListUtil.this.mTileSize;
          break;
        }
      }
    }
    
    public void loadTile(int paramAnonymousInt1, int paramAnonymousInt2)
    {
      if (isTileLoaded(paramAnonymousInt1)) {
        return;
      }
      TileList.Tile localTile = acquireTile();
      localTile.mStartPosition = paramAnonymousInt1;
      localTile.mItemCount = Math.min(AsyncListUtil.this.mTileSize, this.mItemCount - localTile.mStartPosition);
      AsyncListUtil.this.mDataCallback.fillData(localTile.mItems, localTile.mStartPosition, localTile.mItemCount);
      flushTileCache(paramAnonymousInt2);
      addTile(localTile);
    }
    
    public void recycleTile(TileList.Tile<T> paramAnonymousTile)
    {
      AsyncListUtil.this.mDataCallback.recycleData(paramAnonymousTile.mItems, paramAnonymousTile.mItemCount);
      paramAnonymousTile.mNext = this.mRecycledRoot;
      this.mRecycledRoot = paramAnonymousTile;
    }
    
    public void refresh(int paramAnonymousInt)
    {
      this.mGeneration = paramAnonymousInt;
      this.mLoadedTiles.clear();
      this.mItemCount = AsyncListUtil.this.mDataCallback.refreshData();
      AsyncListUtil.this.mMainThreadProxy.updateItemCount(this.mGeneration, this.mItemCount);
    }
    
    public void updateRange(int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3, int paramAnonymousInt4, int paramAnonymousInt5)
    {
      if (paramAnonymousInt1 > paramAnonymousInt2) {
        return;
      }
      int i = getTileStart(paramAnonymousInt1);
      int j = getTileStart(paramAnonymousInt2);
      this.mFirstRequiredTileStart = getTileStart(paramAnonymousInt3);
      this.mLastRequiredTileStart = getTileStart(paramAnonymousInt4);
      if (paramAnonymousInt5 == 1)
      {
        requestTiles(this.mFirstRequiredTileStart, j, paramAnonymousInt5, true);
        requestTiles(j + AsyncListUtil.this.mTileSize, this.mLastRequiredTileStart, paramAnonymousInt5, false);
        return;
      }
      requestTiles(i, this.mLastRequiredTileStart, paramAnonymousInt5, false);
      requestTiles(this.mFirstRequiredTileStart, i - AsyncListUtil.this.mTileSize, paramAnonymousInt5, true);
    }
  };
  final ThreadUtil.BackgroundCallback<T> mBackgroundProxy;
  final DataCallback<T> mDataCallback;
  int mDisplayedGeneration = 0;
  int mItemCount = 0;
  private final ThreadUtil.MainThreadCallback<T> mMainThreadCallback = new ThreadUtil.MainThreadCallback()
  {
    private boolean isRequestedGeneration(int paramAnonymousInt)
    {
      return paramAnonymousInt == AsyncListUtil.this.mRequestedGeneration;
    }
    
    private void recycleAllTiles()
    {
      for (int i = 0; i < AsyncListUtil.this.mTileList.size(); i++) {
        AsyncListUtil.this.mBackgroundProxy.recycleTile(AsyncListUtil.this.mTileList.getAtIndex(i));
      }
      AsyncListUtil.this.mTileList.clear();
    }
    
    public void addTile(int paramAnonymousInt, TileList.Tile<T> paramAnonymousTile)
    {
      if (!isRequestedGeneration(paramAnonymousInt)) {
        AsyncListUtil.this.mBackgroundProxy.recycleTile(paramAnonymousTile);
      }
      for (;;)
      {
        return;
        TileList.Tile localTile = AsyncListUtil.this.mTileList.addOrReplace(paramAnonymousTile);
        if (localTile != null)
        {
          Log.e("AsyncListUtil", "duplicate tile @" + localTile.mStartPosition);
          AsyncListUtil.this.mBackgroundProxy.recycleTile(localTile);
        }
        int i = paramAnonymousTile.mStartPosition + paramAnonymousTile.mItemCount;
        int j = 0;
        while (j < AsyncListUtil.this.mMissingPositions.size())
        {
          int k = AsyncListUtil.this.mMissingPositions.keyAt(j);
          if ((paramAnonymousTile.mStartPosition <= k) && (k < i))
          {
            AsyncListUtil.this.mMissingPositions.removeAt(j);
            AsyncListUtil.this.mViewCallback.onItemLoaded(k);
          }
          else
          {
            j++;
          }
        }
      }
    }
    
    public void removeTile(int paramAnonymousInt1, int paramAnonymousInt2)
    {
      if (!isRequestedGeneration(paramAnonymousInt1)) {
        return;
      }
      TileList.Tile localTile = AsyncListUtil.this.mTileList.removeAtPos(paramAnonymousInt2);
      if (localTile == null)
      {
        Log.e("AsyncListUtil", "tile not found @" + paramAnonymousInt2);
        return;
      }
      AsyncListUtil.this.mBackgroundProxy.recycleTile(localTile);
    }
    
    public void updateItemCount(int paramAnonymousInt1, int paramAnonymousInt2)
    {
      if (!isRequestedGeneration(paramAnonymousInt1)) {
        return;
      }
      AsyncListUtil.this.mItemCount = paramAnonymousInt2;
      AsyncListUtil.this.mViewCallback.onDataRefresh();
      AsyncListUtil.this.mDisplayedGeneration = AsyncListUtil.this.mRequestedGeneration;
      recycleAllTiles();
      AsyncListUtil.this.mAllowScrollHints = false;
      AsyncListUtil.this.updateRange();
    }
  };
  final ThreadUtil.MainThreadCallback<T> mMainThreadProxy;
  final SparseIntArray mMissingPositions = new SparseIntArray();
  final int[] mPrevRange = new int[2];
  int mRequestedGeneration = this.mDisplayedGeneration;
  private int mScrollHint = 0;
  final Class<T> mTClass;
  final TileList<T> mTileList;
  final int mTileSize;
  final int[] mTmpRange = new int[2];
  final int[] mTmpRangeExtended = new int[2];
  final ViewCallback mViewCallback;
  
  public AsyncListUtil(Class<T> paramClass, int paramInt, DataCallback<T> paramDataCallback, ViewCallback paramViewCallback)
  {
    this.mTClass = paramClass;
    this.mTileSize = paramInt;
    this.mDataCallback = paramDataCallback;
    this.mViewCallback = paramViewCallback;
    this.mTileList = new TileList(this.mTileSize);
    MessageThreadUtil localMessageThreadUtil = new MessageThreadUtil();
    this.mMainThreadProxy = localMessageThreadUtil.getMainThreadProxy(this.mMainThreadCallback);
    this.mBackgroundProxy = localMessageThreadUtil.getBackgroundProxy(this.mBackgroundCallback);
    refresh();
  }
  
  private boolean isRefreshPending()
  {
    return this.mRequestedGeneration != this.mDisplayedGeneration;
  }
  
  public T getItem(int paramInt)
  {
    if ((paramInt < 0) || (paramInt >= this.mItemCount)) {
      throw new IndexOutOfBoundsException(paramInt + " is not within 0 and " + this.mItemCount);
    }
    Object localObject = this.mTileList.getItemAt(paramInt);
    if ((localObject == null) && (!isRefreshPending())) {
      this.mMissingPositions.put(paramInt, 0);
    }
    return localObject;
  }
  
  public int getItemCount()
  {
    return this.mItemCount;
  }
  
  void log(String paramString, Object... paramVarArgs)
  {
    Log.d("AsyncListUtil", "[MAIN] " + String.format(paramString, paramVarArgs));
  }
  
  public void onRangeChanged()
  {
    if (isRefreshPending()) {
      return;
    }
    updateRange();
    this.mAllowScrollHints = true;
  }
  
  public void refresh()
  {
    this.mMissingPositions.clear();
    ThreadUtil.BackgroundCallback localBackgroundCallback = this.mBackgroundProxy;
    int i = 1 + this.mRequestedGeneration;
    this.mRequestedGeneration = i;
    localBackgroundCallback.refresh(i);
  }
  
  void updateRange()
  {
    this.mViewCallback.getItemRangeInto(this.mTmpRange);
    if ((this.mTmpRange[0] > this.mTmpRange[1]) || (this.mTmpRange[0] < 0)) {}
    while (this.mTmpRange[1] >= this.mItemCount) {
      return;
    }
    if (!this.mAllowScrollHints) {
      this.mScrollHint = 0;
    }
    for (;;)
    {
      this.mPrevRange[0] = this.mTmpRange[0];
      this.mPrevRange[1] = this.mTmpRange[1];
      this.mViewCallback.extendRangeInto(this.mTmpRange, this.mTmpRangeExtended, this.mScrollHint);
      this.mTmpRangeExtended[0] = Math.min(this.mTmpRange[0], Math.max(this.mTmpRangeExtended[0], 0));
      this.mTmpRangeExtended[1] = Math.max(this.mTmpRange[1], Math.min(this.mTmpRangeExtended[1], -1 + this.mItemCount));
      this.mBackgroundProxy.updateRange(this.mTmpRange[0], this.mTmpRange[1], this.mTmpRangeExtended[0], this.mTmpRangeExtended[1], this.mScrollHint);
      return;
      if ((this.mTmpRange[0] > this.mPrevRange[1]) || (this.mPrevRange[0] > this.mTmpRange[1])) {
        this.mScrollHint = 0;
      } else if (this.mTmpRange[0] < this.mPrevRange[0]) {
        this.mScrollHint = 1;
      } else if (this.mTmpRange[0] > this.mPrevRange[0]) {
        this.mScrollHint = 2;
      }
    }
  }
  
  public static abstract class DataCallback<T>
  {
    public DataCallback() {}
    
    @WorkerThread
    public abstract void fillData(T[] paramArrayOfT, int paramInt1, int paramInt2);
    
    @WorkerThread
    public int getMaxCachedTiles()
    {
      return 10;
    }
    
    @WorkerThread
    public void recycleData(T[] paramArrayOfT, int paramInt) {}
    
    @WorkerThread
    public abstract int refreshData();
  }
  
  public static abstract class ViewCallback
  {
    public static final int HINT_SCROLL_ASC = 2;
    public static final int HINT_SCROLL_DESC = 1;
    public static final int HINT_SCROLL_NONE;
    
    public ViewCallback() {}
    
    @UiThread
    public void extendRangeInto(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt)
    {
      int i = 1 + (paramArrayOfInt1[1] - paramArrayOfInt1[0]);
      int j = i / 2;
      int k = paramArrayOfInt1[0];
      int m;
      int n;
      if (paramInt == 1)
      {
        m = i;
        paramArrayOfInt2[0] = (k - m);
        n = paramArrayOfInt1[1];
        if (paramInt != 2) {
          break label65;
        }
      }
      for (;;)
      {
        paramArrayOfInt2[1] = (n + i);
        return;
        m = j;
        break;
        label65:
        i = j;
      }
    }
    
    @UiThread
    public abstract void getItemRangeInto(int[] paramArrayOfInt);
    
    @UiThread
    public abstract void onDataRefresh();
    
    @UiThread
    public abstract void onItemLoaded(int paramInt);
  }
}
