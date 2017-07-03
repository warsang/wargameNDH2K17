package android.support.v7.util;

import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v7.widget.RecyclerView.Adapter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class DiffUtil
{
  private static final Comparator<Snake> SNAKE_COMPARATOR = new Comparator()
  {
    public int compare(DiffUtil.Snake paramAnonymousSnake1, DiffUtil.Snake paramAnonymousSnake2)
    {
      int i = paramAnonymousSnake1.x - paramAnonymousSnake2.x;
      if (i == 0) {
        i = paramAnonymousSnake1.y - paramAnonymousSnake2.y;
      }
      return i;
    }
  };
  
  private DiffUtil() {}
  
  public static DiffResult calculateDiff(Callback paramCallback)
  {
    return calculateDiff(paramCallback, true);
  }
  
  public static DiffResult calculateDiff(Callback paramCallback, boolean paramBoolean)
  {
    int i = paramCallback.getOldListSize();
    int j = paramCallback.getNewListSize();
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    localArrayList2.add(new Range(0, i, 0, j));
    int k = i + j + Math.abs(i - j);
    int[] arrayOfInt1 = new int[k * 2];
    int[] arrayOfInt2 = new int[k * 2];
    ArrayList localArrayList3 = new ArrayList();
    while (!localArrayList2.isEmpty())
    {
      Range localRange1 = (Range)localArrayList2.remove(-1 + localArrayList2.size());
      Snake localSnake = diffPartial(paramCallback, localRange1.oldListStart, localRange1.oldListEnd, localRange1.newListStart, localRange1.newListEnd, arrayOfInt1, arrayOfInt2, k);
      if (localSnake != null)
      {
        if (localSnake.size > 0) {
          localArrayList1.add(localSnake);
        }
        localSnake.x += localRange1.oldListStart;
        localSnake.y += localRange1.newListStart;
        Range localRange2;
        if (localArrayList3.isEmpty())
        {
          localRange2 = new Range();
          label221:
          int m = localRange1.oldListStart;
          localRange2.oldListStart = m;
          int n = localRange1.newListStart;
          localRange2.newListStart = n;
          if (!localSnake.reverse) {
            break label382;
          }
          int i5 = localSnake.x;
          localRange2.oldListEnd = i5;
          int i6 = localSnake.y;
          localRange2.newListEnd = i6;
          label285:
          localArrayList2.add(localRange2);
          if (!localSnake.reverse) {
            break label493;
          }
          if (!localSnake.removal) {
            break label456;
          }
          localRange1.oldListStart = (1 + (localSnake.x + localSnake.size));
          localRange1.newListStart = (localSnake.y + localSnake.size);
        }
        for (;;)
        {
          localArrayList2.add(localRange1);
          break;
          localRange2 = (Range)localArrayList3.remove(-1 + localArrayList3.size());
          break label221;
          label382:
          if (localSnake.removal)
          {
            int i3 = -1 + localSnake.x;
            localRange2.oldListEnd = i3;
            int i4 = localSnake.y;
            localRange2.newListEnd = i4;
            break label285;
          }
          int i1 = localSnake.x;
          localRange2.oldListEnd = i1;
          int i2 = -1 + localSnake.y;
          localRange2.newListEnd = i2;
          break label285;
          label456:
          localRange1.oldListStart = (localSnake.x + localSnake.size);
          localRange1.newListStart = (1 + (localSnake.y + localSnake.size));
          continue;
          label493:
          localRange1.oldListStart = (localSnake.x + localSnake.size);
          localRange1.newListStart = (localSnake.y + localSnake.size);
        }
      }
      localArrayList3.add(localRange1);
    }
    Collections.sort(localArrayList1, SNAKE_COMPARATOR);
    return new DiffResult(paramCallback, localArrayList1, arrayOfInt1, arrayOfInt2, paramBoolean);
  }
  
  private static Snake diffPartial(Callback paramCallback, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt5)
  {
    int i = paramInt2 - paramInt1;
    int j = paramInt4 - paramInt3;
    if ((paramInt2 - paramInt1 < 1) || (paramInt4 - paramInt3 < 1)) {
      return null;
    }
    int k = i - j;
    int m = (1 + (i + j)) / 2;
    Arrays.fill(paramArrayOfInt1, -1 + (paramInt5 - m), 1 + (paramInt5 + m), 0);
    Arrays.fill(paramArrayOfInt2, k + (-1 + (paramInt5 - m)), k + (1 + (paramInt5 + m)), i);
    int n;
    if (k % 2 != 0) {
      n = 1;
    }
    for (int i1 = 0;; i1++)
    {
      if (i1 > m) {
        break label669;
      }
      for (int i2 = -i1;; i2 += 2)
      {
        if (i2 > i1) {
          break label386;
        }
        int i7;
        if ((i2 == -i1) || ((i2 != i1) && (paramArrayOfInt1[(-1 + (paramInt5 + i2))] < paramArrayOfInt1[(1 + (paramInt5 + i2))]))) {
          i7 = paramArrayOfInt1[(1 + (paramInt5 + i2))];
        }
        for (boolean bool2 = false;; bool2 = true)
        {
          for (int i8 = i7 - i2; (i7 < i) && (i8 < j) && (paramCallback.areItemsTheSame(paramInt1 + i7, paramInt3 + i8)); i8++) {
            i7++;
          }
          n = 0;
          break;
          i7 = 1 + paramArrayOfInt1[(-1 + (paramInt5 + i2))];
        }
        paramArrayOfInt1[(paramInt5 + i2)] = i7;
        if ((n != 0) && (i2 >= 1 + (k - i1)) && (i2 <= -1 + (k + i1)) && (paramArrayOfInt1[(paramInt5 + i2)] >= paramArrayOfInt2[(paramInt5 + i2)]))
        {
          Snake localSnake2 = new Snake();
          localSnake2.x = paramArrayOfInt2[(paramInt5 + i2)];
          localSnake2.y = (localSnake2.x - i2);
          localSnake2.size = (paramArrayOfInt1[(paramInt5 + i2)] - paramArrayOfInt2[(paramInt5 + i2)]);
          localSnake2.removal = bool2;
          localSnake2.reverse = false;
          return localSnake2;
        }
      }
      label386:
      for (int i3 = -i1; i3 <= i1; i3 += 2)
      {
        int i4 = i3 + k;
        int i5;
        if ((i4 == i1 + k) || ((i4 != k + -i1) && (paramArrayOfInt2[(-1 + (paramInt5 + i4))] < paramArrayOfInt2[(1 + (paramInt5 + i4))]))) {
          i5 = paramArrayOfInt2[(-1 + (paramInt5 + i4))];
        }
        for (boolean bool1 = false;; bool1 = true)
        {
          for (int i6 = i5 - i4; (i5 > 0) && (i6 > 0) && (paramCallback.areItemsTheSame(-1 + (paramInt1 + i5), -1 + (paramInt3 + i6))); i6--) {
            i5--;
          }
          i5 = -1 + paramArrayOfInt2[(1 + (paramInt5 + i4))];
        }
        paramArrayOfInt2[(paramInt5 + i4)] = i5;
        if ((n == 0) && (i3 + k >= -i1) && (i3 + k <= i1) && (paramArrayOfInt1[(paramInt5 + i4)] >= paramArrayOfInt2[(paramInt5 + i4)]))
        {
          Snake localSnake1 = new Snake();
          localSnake1.x = paramArrayOfInt2[(paramInt5 + i4)];
          localSnake1.y = (localSnake1.x - i4);
          localSnake1.size = (paramArrayOfInt1[(paramInt5 + i4)] - paramArrayOfInt2[(paramInt5 + i4)]);
          localSnake1.removal = bool1;
          localSnake1.reverse = true;
          return localSnake1;
        }
      }
    }
    label669:
    throw new IllegalStateException("DiffUtil hit an unexpected case while trying to calculate the optimal path. Please make sure your data is not changing during the diff calculation.");
  }
  
  public static abstract class Callback
  {
    public Callback() {}
    
    public abstract boolean areContentsTheSame(int paramInt1, int paramInt2);
    
    public abstract boolean areItemsTheSame(int paramInt1, int paramInt2);
    
    @Nullable
    public Object getChangePayload(int paramInt1, int paramInt2)
    {
      return null;
    }
    
    public abstract int getNewListSize();
    
    public abstract int getOldListSize();
  }
  
  public static class DiffResult
  {
    private static final int FLAG_CHANGED = 2;
    private static final int FLAG_IGNORE = 16;
    private static final int FLAG_MASK = 31;
    private static final int FLAG_MOVED_CHANGED = 4;
    private static final int FLAG_MOVED_NOT_CHANGED = 8;
    private static final int FLAG_NOT_CHANGED = 1;
    private static final int FLAG_OFFSET = 5;
    private final DiffUtil.Callback mCallback;
    private final boolean mDetectMoves;
    private final int[] mNewItemStatuses;
    private final int mNewListSize;
    private final int[] mOldItemStatuses;
    private final int mOldListSize;
    private final List<DiffUtil.Snake> mSnakes;
    
    DiffResult(DiffUtil.Callback paramCallback, List<DiffUtil.Snake> paramList, int[] paramArrayOfInt1, int[] paramArrayOfInt2, boolean paramBoolean)
    {
      this.mSnakes = paramList;
      this.mOldItemStatuses = paramArrayOfInt1;
      this.mNewItemStatuses = paramArrayOfInt2;
      Arrays.fill(this.mOldItemStatuses, 0);
      Arrays.fill(this.mNewItemStatuses, 0);
      this.mCallback = paramCallback;
      this.mOldListSize = paramCallback.getOldListSize();
      this.mNewListSize = paramCallback.getNewListSize();
      this.mDetectMoves = paramBoolean;
      addRootSnake();
      findMatchingItems();
    }
    
    private void addRootSnake()
    {
      if (this.mSnakes.isEmpty()) {}
      for (DiffUtil.Snake localSnake1 = null;; localSnake1 = (DiffUtil.Snake)this.mSnakes.get(0))
      {
        if ((localSnake1 == null) || (localSnake1.x != 0) || (localSnake1.y != 0))
        {
          DiffUtil.Snake localSnake2 = new DiffUtil.Snake();
          localSnake2.x = 0;
          localSnake2.y = 0;
          localSnake2.removal = false;
          localSnake2.size = 0;
          localSnake2.reverse = false;
          this.mSnakes.add(0, localSnake2);
        }
        return;
      }
    }
    
    private void dispatchAdditions(List<DiffUtil.PostponedUpdate> paramList, ListUpdateCallback paramListUpdateCallback, int paramInt1, int paramInt2, int paramInt3)
    {
      if (!this.mDetectMoves) {
        paramListUpdateCallback.onInserted(paramInt1, paramInt2);
      }
      int i;
      do
      {
        return;
        i = paramInt2 - 1;
      } while (i < 0);
      int j = 0x1F & this.mNewItemStatuses[(paramInt3 + i)];
      Iterator localIterator;
      switch (j)
      {
      default: 
        throw new IllegalStateException("unknown flag for pos " + (paramInt3 + i) + " " + Long.toBinaryString(j));
      case 0: 
        paramListUpdateCallback.onInserted(paramInt1, 1);
        localIterator = paramList.iterator();
      case 4: 
      case 8: 
        while (localIterator.hasNext())
        {
          DiffUtil.PostponedUpdate localPostponedUpdate = (DiffUtil.PostponedUpdate)localIterator.next();
          localPostponedUpdate.currentPos = (1 + localPostponedUpdate.currentPos);
          continue;
          int k = this.mNewItemStatuses[(paramInt3 + i)] >> 5;
          paramListUpdateCallback.onMoved(removePostponedUpdate(paramList, k, true).currentPos, paramInt1);
          if (j == 4) {
            paramListUpdateCallback.onChanged(paramInt1, 1, this.mCallback.getChangePayload(k, paramInt3 + i));
          }
        }
      }
      for (;;)
      {
        i--;
        break;
        paramList.add(new DiffUtil.PostponedUpdate(paramInt3 + i, paramInt1, false));
      }
    }
    
    private void dispatchRemovals(List<DiffUtil.PostponedUpdate> paramList, ListUpdateCallback paramListUpdateCallback, int paramInt1, int paramInt2, int paramInt3)
    {
      if (!this.mDetectMoves) {
        paramListUpdateCallback.onRemoved(paramInt1, paramInt2);
      }
      int i;
      do
      {
        return;
        i = paramInt2 - 1;
      } while (i < 0);
      int j = 0x1F & this.mOldItemStatuses[(paramInt3 + i)];
      Iterator localIterator;
      switch (j)
      {
      default: 
        throw new IllegalStateException("unknown flag for pos " + (paramInt3 + i) + " " + Long.toBinaryString(j));
      case 0: 
        paramListUpdateCallback.onRemoved(paramInt1 + i, 1);
        localIterator = paramList.iterator();
      case 4: 
      case 8: 
        while (localIterator.hasNext())
        {
          DiffUtil.PostponedUpdate localPostponedUpdate2 = (DiffUtil.PostponedUpdate)localIterator.next();
          localPostponedUpdate2.currentPos = (-1 + localPostponedUpdate2.currentPos);
          continue;
          int k = this.mOldItemStatuses[(paramInt3 + i)] >> 5;
          DiffUtil.PostponedUpdate localPostponedUpdate1 = removePostponedUpdate(paramList, k, false);
          paramListUpdateCallback.onMoved(paramInt1 + i, -1 + localPostponedUpdate1.currentPos);
          if (j == 4) {
            paramListUpdateCallback.onChanged(-1 + localPostponedUpdate1.currentPos, 1, this.mCallback.getChangePayload(paramInt3 + i, k));
          }
        }
      }
      for (;;)
      {
        i--;
        break;
        paramList.add(new DiffUtil.PostponedUpdate(paramInt3 + i, paramInt1 + i, true));
      }
    }
    
    private void findAddition(int paramInt1, int paramInt2, int paramInt3)
    {
      if (this.mOldItemStatuses[(paramInt1 - 1)] != 0) {
        return;
      }
      findMatchingItem(paramInt1, paramInt2, paramInt3, false);
    }
    
    private boolean findMatchingItem(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
    {
      int i;
      int j;
      int k;
      if (paramBoolean)
      {
        i = paramInt2 - 1;
        j = paramInt1;
        k = paramInt2 - 1;
      }
      for (int m = paramInt3;; m--)
      {
        if (m < 0) {
          break label285;
        }
        DiffUtil.Snake localSnake = (DiffUtil.Snake)this.mSnakes.get(m);
        int n = localSnake.x + localSnake.size;
        int i1 = localSnake.y + localSnake.size;
        if (paramBoolean) {
          for (int i4 = j - 1;; i4--)
          {
            if (i4 < n) {
              break label265;
            }
            if (this.mCallback.areItemsTheSame(i4, i))
            {
              if (this.mCallback.areContentsTheSame(i4, i)) {}
              for (int i5 = 8;; i5 = 4)
              {
                this.mNewItemStatuses[i] = (0x10 | i4 << 5);
                this.mOldItemStatuses[i4] = (i5 | i << 5);
                return true;
                i = paramInt1 - 1;
                j = paramInt1 - 1;
                k = paramInt2;
                break;
              }
            }
          }
        }
        for (int i2 = k - 1; i2 >= i1; i2--) {
          if (this.mCallback.areItemsTheSame(i, i2))
          {
            if (this.mCallback.areContentsTheSame(i, i2)) {}
            for (int i3 = 8;; i3 = 4)
            {
              this.mOldItemStatuses[(paramInt1 - 1)] = (0x10 | i2 << 5);
              this.mNewItemStatuses[i2] = (i3 | paramInt1 - 1 << 5);
              return true;
            }
          }
        }
        label265:
        j = localSnake.x;
        k = localSnake.y;
      }
      label285:
      return false;
    }
    
    private void findMatchingItems()
    {
      int i = this.mOldListSize;
      int j = this.mNewListSize;
      for (int k = -1 + this.mSnakes.size(); k >= 0; k--)
      {
        DiffUtil.Snake localSnake = (DiffUtil.Snake)this.mSnakes.get(k);
        int m = localSnake.x + localSnake.size;
        int n = localSnake.y + localSnake.size;
        if (this.mDetectMoves)
        {
          while (i > m)
          {
            findAddition(i, j, k);
            i--;
          }
          while (j > n)
          {
            findRemoval(i, j, k);
            j--;
          }
        }
        int i1 = 0;
        if (i1 < localSnake.size)
        {
          int i2 = i1 + localSnake.x;
          int i3 = i1 + localSnake.y;
          if (this.mCallback.areContentsTheSame(i2, i3)) {}
          for (int i4 = 1;; i4 = 2)
          {
            this.mOldItemStatuses[i2] = (i4 | i3 << 5);
            this.mNewItemStatuses[i3] = (i4 | i2 << 5);
            i1++;
            break;
          }
        }
        i = localSnake.x;
        j = localSnake.y;
      }
    }
    
    private void findRemoval(int paramInt1, int paramInt2, int paramInt3)
    {
      if (this.mNewItemStatuses[(paramInt2 - 1)] != 0) {
        return;
      }
      findMatchingItem(paramInt1, paramInt2, paramInt3, true);
    }
    
    private static DiffUtil.PostponedUpdate removePostponedUpdate(List<DiffUtil.PostponedUpdate> paramList, int paramInt, boolean paramBoolean)
    {
      for (int i = -1 + paramList.size(); i >= 0; i--)
      {
        localPostponedUpdate1 = (DiffUtil.PostponedUpdate)paramList.get(i);
        if ((localPostponedUpdate1.posInOwnerList == paramInt) && (localPostponedUpdate1.removal == paramBoolean))
        {
          paramList.remove(i);
          int j = i;
          if (j >= paramList.size()) {
            break label123;
          }
          DiffUtil.PostponedUpdate localPostponedUpdate2 = (DiffUtil.PostponedUpdate)paramList.get(j);
          int k = localPostponedUpdate2.currentPos;
          if (paramBoolean) {}
          for (int m = 1;; m = -1)
          {
            localPostponedUpdate2.currentPos = (m + k);
            j++;
            break;
          }
        }
      }
      DiffUtil.PostponedUpdate localPostponedUpdate1 = null;
      label123:
      return localPostponedUpdate1;
    }
    
    public void dispatchUpdatesTo(ListUpdateCallback paramListUpdateCallback)
    {
      BatchingListUpdateCallback localBatchingListUpdateCallback;
      ArrayList localArrayList;
      int i;
      int j;
      if ((paramListUpdateCallback instanceof BatchingListUpdateCallback))
      {
        localBatchingListUpdateCallback = (BatchingListUpdateCallback)paramListUpdateCallback;
        localArrayList = new ArrayList();
        i = this.mOldListSize;
        j = this.mNewListSize;
      }
      for (int k = -1 + this.mSnakes.size();; k--)
      {
        if (k < 0) {
          break label247;
        }
        DiffUtil.Snake localSnake = (DiffUtil.Snake)this.mSnakes.get(k);
        int m = localSnake.size;
        int n = m + localSnake.x;
        int i1 = m + localSnake.y;
        if (n < i) {
          dispatchRemovals(localArrayList, localBatchingListUpdateCallback, n, i - n, n);
        }
        if (i1 < j) {
          dispatchAdditions(localArrayList, localBatchingListUpdateCallback, n, j - i1, i1);
        }
        int i2 = m - 1;
        for (;;)
        {
          if (i2 >= 0)
          {
            if ((0x1F & this.mOldItemStatuses[(i2 + localSnake.x)]) == 2) {
              localBatchingListUpdateCallback.onChanged(i2 + localSnake.x, 1, this.mCallback.getChangePayload(i2 + localSnake.x, i2 + localSnake.y));
            }
            i2--;
            continue;
            localBatchingListUpdateCallback = new BatchingListUpdateCallback(paramListUpdateCallback);
            break;
          }
        }
        i = localSnake.x;
        j = localSnake.y;
      }
      label247:
      localBatchingListUpdateCallback.dispatchLastEvent();
    }
    
    public void dispatchUpdatesTo(final RecyclerView.Adapter paramAdapter)
    {
      dispatchUpdatesTo(new ListUpdateCallback()
      {
        public void onChanged(int paramAnonymousInt1, int paramAnonymousInt2, Object paramAnonymousObject)
        {
          paramAdapter.notifyItemRangeChanged(paramAnonymousInt1, paramAnonymousInt2, paramAnonymousObject);
        }
        
        public void onInserted(int paramAnonymousInt1, int paramAnonymousInt2)
        {
          paramAdapter.notifyItemRangeInserted(paramAnonymousInt1, paramAnonymousInt2);
        }
        
        public void onMoved(int paramAnonymousInt1, int paramAnonymousInt2)
        {
          paramAdapter.notifyItemMoved(paramAnonymousInt1, paramAnonymousInt2);
        }
        
        public void onRemoved(int paramAnonymousInt1, int paramAnonymousInt2)
        {
          paramAdapter.notifyItemRangeRemoved(paramAnonymousInt1, paramAnonymousInt2);
        }
      });
    }
    
    @VisibleForTesting
    List<DiffUtil.Snake> getSnakes()
    {
      return this.mSnakes;
    }
  }
  
  private static class PostponedUpdate
  {
    int currentPos;
    int posInOwnerList;
    boolean removal;
    
    public PostponedUpdate(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      this.posInOwnerList = paramInt1;
      this.currentPos = paramInt2;
      this.removal = paramBoolean;
    }
  }
  
  static class Range
  {
    int newListEnd;
    int newListStart;
    int oldListEnd;
    int oldListStart;
    
    public Range() {}
    
    public Range(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      this.oldListStart = paramInt1;
      this.oldListEnd = paramInt2;
      this.newListStart = paramInt3;
      this.newListEnd = paramInt4;
    }
  }
  
  static class Snake
  {
    boolean removal;
    boolean reverse;
    int size;
    int x;
    int y;
    
    Snake() {}
  }
}
