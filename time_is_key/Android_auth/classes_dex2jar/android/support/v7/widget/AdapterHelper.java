package android.support.v7.widget;

import android.support.v4.util.Pools.Pool;
import android.support.v4.util.Pools.SimplePool;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class AdapterHelper
  implements OpReorderer.Callback
{
  private static final boolean DEBUG = false;
  static final int POSITION_TYPE_INVISIBLE = 0;
  static final int POSITION_TYPE_NEW_OR_LAID_OUT = 1;
  private static final String TAG = "AHT";
  final Callback mCallback;
  final boolean mDisableRecycler;
  private int mExistingUpdateTypes = 0;
  Runnable mOnItemProcessedCallback;
  final OpReorderer mOpReorderer;
  final ArrayList<UpdateOp> mPendingUpdates = new ArrayList();
  final ArrayList<UpdateOp> mPostponedList = new ArrayList();
  private Pools.Pool<UpdateOp> mUpdateOpPool = new Pools.SimplePool(30);
  
  AdapterHelper(Callback paramCallback)
  {
    this(paramCallback, false);
  }
  
  AdapterHelper(Callback paramCallback, boolean paramBoolean)
  {
    this.mCallback = paramCallback;
    this.mDisableRecycler = paramBoolean;
    this.mOpReorderer = new OpReorderer(this);
  }
  
  private void applyAdd(UpdateOp paramUpdateOp)
  {
    postponeAndUpdateViewHolders(paramUpdateOp);
  }
  
  private void applyMove(UpdateOp paramUpdateOp)
  {
    postponeAndUpdateViewHolders(paramUpdateOp);
  }
  
  private void applyRemove(UpdateOp paramUpdateOp)
  {
    int i = paramUpdateOp.positionStart;
    int j = 0;
    int k = paramUpdateOp.positionStart + paramUpdateOp.itemCount;
    int m = -1;
    int n = paramUpdateOp.positionStart;
    if (n < k)
    {
      int i1;
      if ((this.mCallback.findViewHolder(n) != null) || (canFindInPreLayout(n)))
      {
        i1 = 0;
        if (m == 0)
        {
          dispatchAndUpdateViewHolders(obtainUpdateOp(2, i, j, null));
          i1 = 1;
        }
        m = 1;
        label83:
        if (i1 == 0) {
          break label138;
        }
        n -= j;
        k -= j;
      }
      label138:
      for (j = 1;; j++)
      {
        n++;
        break;
        i1 = 0;
        if (m == 1)
        {
          postponeAndUpdateViewHolders(obtainUpdateOp(2, i, j, null));
          i1 = 1;
        }
        m = 0;
        break label83;
      }
    }
    if (j != paramUpdateOp.itemCount)
    {
      recycleUpdateOp(paramUpdateOp);
      paramUpdateOp = obtainUpdateOp(2, i, j, null);
    }
    if (m == 0)
    {
      dispatchAndUpdateViewHolders(paramUpdateOp);
      return;
    }
    postponeAndUpdateViewHolders(paramUpdateOp);
  }
  
  private void applyUpdate(UpdateOp paramUpdateOp)
  {
    int i = paramUpdateOp.positionStart;
    int j = 0;
    int k = paramUpdateOp.positionStart + paramUpdateOp.itemCount;
    int m = -1;
    int n = paramUpdateOp.positionStart;
    if (n < k)
    {
      if ((this.mCallback.findViewHolder(n) != null) || (canFindInPreLayout(n))) {
        if (m == 0)
        {
          dispatchAndUpdateViewHolders(obtainUpdateOp(4, i, j, paramUpdateOp.payload));
          j = 0;
          i = n;
        }
      }
      for (m = 1;; m = 0)
      {
        j++;
        n++;
        break;
        if (m == 1)
        {
          postponeAndUpdateViewHolders(obtainUpdateOp(4, i, j, paramUpdateOp.payload));
          j = 0;
          i = n;
        }
      }
    }
    if (j != paramUpdateOp.itemCount)
    {
      Object localObject = paramUpdateOp.payload;
      recycleUpdateOp(paramUpdateOp);
      paramUpdateOp = obtainUpdateOp(4, i, j, localObject);
    }
    if (m == 0)
    {
      dispatchAndUpdateViewHolders(paramUpdateOp);
      return;
    }
    postponeAndUpdateViewHolders(paramUpdateOp);
  }
  
  private boolean canFindInPreLayout(int paramInt)
  {
    int i = this.mPostponedList.size();
    label111:
    for (int j = 0; j < i; j++)
    {
      UpdateOp localUpdateOp = (UpdateOp)this.mPostponedList.get(j);
      if (localUpdateOp.cmd == 8)
      {
        if (findPositionOffset(localUpdateOp.itemCount, j + 1) == paramInt) {
          return true;
        }
      }
      else if (localUpdateOp.cmd == 1)
      {
        int k = localUpdateOp.positionStart + localUpdateOp.itemCount;
        for (int m = localUpdateOp.positionStart;; m++)
        {
          if (m >= k) {
            break label111;
          }
          if (findPositionOffset(m, j + 1) == paramInt) {
            break;
          }
        }
      }
    }
    return false;
  }
  
  private void dispatchAndUpdateViewHolders(UpdateOp paramUpdateOp)
  {
    if ((paramUpdateOp.cmd == 1) || (paramUpdateOp.cmd == 8)) {
      throw new IllegalArgumentException("should not dispatch add or move for pre layout");
    }
    int i = updatePositionWithPostponed(paramUpdateOp.positionStart, paramUpdateOp.cmd);
    int j = 1;
    int k = paramUpdateOp.positionStart;
    int m;
    int n;
    label113:
    int i1;
    int i3;
    switch (paramUpdateOp.cmd)
    {
    case 3: 
    default: 
      throw new IllegalArgumentException("op should be remove or update." + paramUpdateOp);
    case 4: 
      m = 1;
      n = 1;
      if (n >= paramUpdateOp.itemCount) {
        break label290;
      }
      i1 = updatePositionWithPostponed(paramUpdateOp.positionStart + m * n, paramUpdateOp.cmd);
      int i2 = paramUpdateOp.cmd;
      i3 = 0;
      switch (i2)
      {
      case 3: 
      default: 
        if (i3 != 0) {
          j++;
        }
        break;
      }
      break;
    }
    for (;;)
    {
      n++;
      break label113;
      m = 0;
      break;
      if (i1 == i + 1) {}
      for (i3 = 1;; i3 = 0) {
        break;
      }
      if (i1 == i) {}
      for (i3 = 1;; i3 = 0) {
        break;
      }
      UpdateOp localUpdateOp2 = obtainUpdateOp(paramUpdateOp.cmd, i, j, paramUpdateOp.payload);
      dispatchFirstPassAndUpdateViewHolders(localUpdateOp2, k);
      recycleUpdateOp(localUpdateOp2);
      if (paramUpdateOp.cmd == 4) {
        k += j;
      }
      i = i1;
      j = 1;
    }
    label290:
    Object localObject = paramUpdateOp.payload;
    recycleUpdateOp(paramUpdateOp);
    if (j > 0)
    {
      UpdateOp localUpdateOp1 = obtainUpdateOp(paramUpdateOp.cmd, i, j, localObject);
      dispatchFirstPassAndUpdateViewHolders(localUpdateOp1, k);
      recycleUpdateOp(localUpdateOp1);
    }
  }
  
  private void postponeAndUpdateViewHolders(UpdateOp paramUpdateOp)
  {
    this.mPostponedList.add(paramUpdateOp);
    switch (paramUpdateOp.cmd)
    {
    case 3: 
    case 5: 
    case 6: 
    case 7: 
    default: 
      throw new IllegalArgumentException("Unknown update op type for " + paramUpdateOp);
    case 1: 
      this.mCallback.offsetPositionsForAdd(paramUpdateOp.positionStart, paramUpdateOp.itemCount);
      return;
    case 8: 
      this.mCallback.offsetPositionsForMove(paramUpdateOp.positionStart, paramUpdateOp.itemCount);
      return;
    case 2: 
      this.mCallback.offsetPositionsForRemovingLaidOutOrNewView(paramUpdateOp.positionStart, paramUpdateOp.itemCount);
      return;
    }
    this.mCallback.markViewHoldersUpdated(paramUpdateOp.positionStart, paramUpdateOp.itemCount, paramUpdateOp.payload);
  }
  
  private int updatePositionWithPostponed(int paramInt1, int paramInt2)
  {
    int i = -1 + this.mPostponedList.size();
    if (i >= 0)
    {
      UpdateOp localUpdateOp2 = (UpdateOp)this.mPostponedList.get(i);
      int k;
      int m;
      if (localUpdateOp2.cmd == 8) {
        if (localUpdateOp2.positionStart < localUpdateOp2.itemCount)
        {
          k = localUpdateOp2.positionStart;
          m = localUpdateOp2.itemCount;
          label64:
          if ((paramInt1 < k) || (paramInt1 > m)) {
            break label192;
          }
          if (k != localUpdateOp2.positionStart) {
            break label149;
          }
          if (paramInt2 != 1) {
            break label129;
          }
          localUpdateOp2.itemCount = (1 + localUpdateOp2.itemCount);
          label103:
          paramInt1++;
        }
      }
      for (;;)
      {
        i--;
        break;
        k = localUpdateOp2.itemCount;
        m = localUpdateOp2.positionStart;
        break label64;
        label129:
        if (paramInt2 != 2) {
          break label103;
        }
        localUpdateOp2.itemCount = (-1 + localUpdateOp2.itemCount);
        break label103;
        label149:
        if (paramInt2 == 1) {
          localUpdateOp2.positionStart = (1 + localUpdateOp2.positionStart);
        }
        for (;;)
        {
          paramInt1--;
          break;
          if (paramInt2 == 2) {
            localUpdateOp2.positionStart = (-1 + localUpdateOp2.positionStart);
          }
        }
        label192:
        if (paramInt1 < localUpdateOp2.positionStart) {
          if (paramInt2 == 1)
          {
            localUpdateOp2.positionStart = (1 + localUpdateOp2.positionStart);
            localUpdateOp2.itemCount = (1 + localUpdateOp2.itemCount);
          }
          else if (paramInt2 == 2)
          {
            localUpdateOp2.positionStart = (-1 + localUpdateOp2.positionStart);
            localUpdateOp2.itemCount = (-1 + localUpdateOp2.itemCount);
            continue;
            if (localUpdateOp2.positionStart <= paramInt1)
            {
              if (localUpdateOp2.cmd == 1) {
                paramInt1 -= localUpdateOp2.itemCount;
              } else if (localUpdateOp2.cmd == 2) {
                paramInt1 += localUpdateOp2.itemCount;
              }
            }
            else if (paramInt2 == 1) {
              localUpdateOp2.positionStart = (1 + localUpdateOp2.positionStart);
            } else if (paramInt2 == 2) {
              localUpdateOp2.positionStart = (-1 + localUpdateOp2.positionStart);
            }
          }
        }
      }
    }
    int j = -1 + this.mPostponedList.size();
    if (j >= 0)
    {
      UpdateOp localUpdateOp1 = (UpdateOp)this.mPostponedList.get(j);
      if (localUpdateOp1.cmd == 8) {
        if ((localUpdateOp1.itemCount == localUpdateOp1.positionStart) || (localUpdateOp1.itemCount < 0))
        {
          this.mPostponedList.remove(j);
          recycleUpdateOp(localUpdateOp1);
        }
      }
      for (;;)
      {
        j--;
        break;
        if (localUpdateOp1.itemCount <= 0)
        {
          this.mPostponedList.remove(j);
          recycleUpdateOp(localUpdateOp1);
        }
      }
    }
    return paramInt1;
  }
  
  AdapterHelper addUpdateOp(UpdateOp... paramVarArgs)
  {
    Collections.addAll(this.mPendingUpdates, paramVarArgs);
    return this;
  }
  
  public int applyPendingUpdatesToPosition(int paramInt)
  {
    int i = this.mPendingUpdates.size();
    int j = 0;
    UpdateOp localUpdateOp;
    if (j < i)
    {
      localUpdateOp = (UpdateOp)this.mPendingUpdates.get(j);
      switch (localUpdateOp.cmd)
      {
      }
    }
    for (;;)
    {
      j++;
      break;
      if (localUpdateOp.positionStart <= paramInt)
      {
        paramInt += localUpdateOp.itemCount;
        continue;
        if (localUpdateOp.positionStart <= paramInt)
        {
          if (localUpdateOp.positionStart + localUpdateOp.itemCount > paramInt)
          {
            paramInt = -1;
            return paramInt;
          }
          paramInt -= localUpdateOp.itemCount;
          continue;
          if (localUpdateOp.positionStart == paramInt)
          {
            paramInt = localUpdateOp.itemCount;
          }
          else
          {
            if (localUpdateOp.positionStart < paramInt) {
              paramInt--;
            }
            if (localUpdateOp.itemCount <= paramInt) {
              paramInt++;
            }
          }
        }
      }
    }
  }
  
  void consumePostponedUpdates()
  {
    int i = this.mPostponedList.size();
    for (int j = 0; j < i; j++) {
      this.mCallback.onDispatchSecondPass((UpdateOp)this.mPostponedList.get(j));
    }
    recycleUpdateOpsAndClearList(this.mPostponedList);
    this.mExistingUpdateTypes = 0;
  }
  
  void consumeUpdatesInOnePass()
  {
    consumePostponedUpdates();
    int i = this.mPendingUpdates.size();
    int j = 0;
    if (j < i)
    {
      UpdateOp localUpdateOp = (UpdateOp)this.mPendingUpdates.get(j);
      switch (localUpdateOp.cmd)
      {
      }
      for (;;)
      {
        if (this.mOnItemProcessedCallback != null) {
          this.mOnItemProcessedCallback.run();
        }
        j++;
        break;
        this.mCallback.onDispatchSecondPass(localUpdateOp);
        this.mCallback.offsetPositionsForAdd(localUpdateOp.positionStart, localUpdateOp.itemCount);
        continue;
        this.mCallback.onDispatchSecondPass(localUpdateOp);
        this.mCallback.offsetPositionsForRemovingInvisible(localUpdateOp.positionStart, localUpdateOp.itemCount);
        continue;
        this.mCallback.onDispatchSecondPass(localUpdateOp);
        this.mCallback.markViewHoldersUpdated(localUpdateOp.positionStart, localUpdateOp.itemCount, localUpdateOp.payload);
        continue;
        this.mCallback.onDispatchSecondPass(localUpdateOp);
        this.mCallback.offsetPositionsForMove(localUpdateOp.positionStart, localUpdateOp.itemCount);
      }
    }
    recycleUpdateOpsAndClearList(this.mPendingUpdates);
    this.mExistingUpdateTypes = 0;
  }
  
  void dispatchFirstPassAndUpdateViewHolders(UpdateOp paramUpdateOp, int paramInt)
  {
    this.mCallback.onDispatchFirstPass(paramUpdateOp);
    switch (paramUpdateOp.cmd)
    {
    case 3: 
    default: 
      throw new IllegalArgumentException("only remove and update ops can be dispatched in first pass");
    case 2: 
      this.mCallback.offsetPositionsForRemovingInvisible(paramInt, paramUpdateOp.itemCount);
      return;
    }
    this.mCallback.markViewHoldersUpdated(paramInt, paramUpdateOp.itemCount, paramUpdateOp.payload);
  }
  
  int findPositionOffset(int paramInt)
  {
    return findPositionOffset(paramInt, 0);
  }
  
  int findPositionOffset(int paramInt1, int paramInt2)
  {
    int i = this.mPostponedList.size();
    int j = paramInt2;
    UpdateOp localUpdateOp;
    if (j < i)
    {
      localUpdateOp = (UpdateOp)this.mPostponedList.get(j);
      if (localUpdateOp.cmd == 8) {
        if (localUpdateOp.positionStart == paramInt1) {
          paramInt1 = localUpdateOp.itemCount;
        }
      }
    }
    for (;;)
    {
      j++;
      break;
      if (localUpdateOp.positionStart < paramInt1) {
        paramInt1--;
      }
      if (localUpdateOp.itemCount <= paramInt1)
      {
        paramInt1++;
        continue;
        if (localUpdateOp.positionStart <= paramInt1) {
          if (localUpdateOp.cmd == 2)
          {
            if (paramInt1 < localUpdateOp.positionStart + localUpdateOp.itemCount)
            {
              paramInt1 = -1;
              return paramInt1;
            }
            paramInt1 -= localUpdateOp.itemCount;
          }
          else if (localUpdateOp.cmd == 1)
          {
            paramInt1 += localUpdateOp.itemCount;
          }
        }
      }
    }
  }
  
  boolean hasAnyUpdateTypes(int paramInt)
  {
    return (paramInt & this.mExistingUpdateTypes) != 0;
  }
  
  boolean hasPendingUpdates()
  {
    return this.mPendingUpdates.size() > 0;
  }
  
  boolean hasUpdates()
  {
    return (!this.mPostponedList.isEmpty()) && (!this.mPendingUpdates.isEmpty());
  }
  
  public UpdateOp obtainUpdateOp(int paramInt1, int paramInt2, int paramInt3, Object paramObject)
  {
    UpdateOp localUpdateOp = (UpdateOp)this.mUpdateOpPool.acquire();
    if (localUpdateOp == null) {
      return new UpdateOp(paramInt1, paramInt2, paramInt3, paramObject);
    }
    localUpdateOp.cmd = paramInt1;
    localUpdateOp.positionStart = paramInt2;
    localUpdateOp.itemCount = paramInt3;
    localUpdateOp.payload = paramObject;
    return localUpdateOp;
  }
  
  boolean onItemRangeChanged(int paramInt1, int paramInt2, Object paramObject)
  {
    int i = 1;
    if (paramInt2 < i) {
      return false;
    }
    this.mPendingUpdates.add(obtainUpdateOp(4, paramInt1, paramInt2, paramObject));
    this.mExistingUpdateTypes = (0x4 | this.mExistingUpdateTypes);
    if (this.mPendingUpdates.size() == i) {}
    for (;;)
    {
      return i;
      i = 0;
    }
  }
  
  boolean onItemRangeInserted(int paramInt1, int paramInt2)
  {
    int i = 1;
    if (paramInt2 < i) {
      return false;
    }
    this.mPendingUpdates.add(obtainUpdateOp(i, paramInt1, paramInt2, null));
    this.mExistingUpdateTypes = (0x1 | this.mExistingUpdateTypes);
    if (this.mPendingUpdates.size() == i) {}
    for (;;)
    {
      return i;
      i = 0;
    }
  }
  
  boolean onItemRangeMoved(int paramInt1, int paramInt2, int paramInt3)
  {
    int i = 1;
    if (paramInt1 == paramInt2) {
      return false;
    }
    if (paramInt3 != i) {
      throw new IllegalArgumentException("Moving more than 1 item is not supported yet");
    }
    this.mPendingUpdates.add(obtainUpdateOp(8, paramInt1, paramInt2, null));
    this.mExistingUpdateTypes = (0x8 | this.mExistingUpdateTypes);
    if (this.mPendingUpdates.size() == i) {}
    for (;;)
    {
      return i;
      i = 0;
    }
  }
  
  boolean onItemRangeRemoved(int paramInt1, int paramInt2)
  {
    int i = 1;
    if (paramInt2 < i) {
      return false;
    }
    this.mPendingUpdates.add(obtainUpdateOp(2, paramInt1, paramInt2, null));
    this.mExistingUpdateTypes = (0x2 | this.mExistingUpdateTypes);
    if (this.mPendingUpdates.size() == i) {}
    for (;;)
    {
      return i;
      i = 0;
    }
  }
  
  void preProcess()
  {
    this.mOpReorderer.reorderOps(this.mPendingUpdates);
    int i = this.mPendingUpdates.size();
    int j = 0;
    if (j < i)
    {
      UpdateOp localUpdateOp = (UpdateOp)this.mPendingUpdates.get(j);
      switch (localUpdateOp.cmd)
      {
      }
      for (;;)
      {
        if (this.mOnItemProcessedCallback != null) {
          this.mOnItemProcessedCallback.run();
        }
        j++;
        break;
        applyAdd(localUpdateOp);
        continue;
        applyRemove(localUpdateOp);
        continue;
        applyUpdate(localUpdateOp);
        continue;
        applyMove(localUpdateOp);
      }
    }
    this.mPendingUpdates.clear();
  }
  
  public void recycleUpdateOp(UpdateOp paramUpdateOp)
  {
    if (!this.mDisableRecycler)
    {
      paramUpdateOp.payload = null;
      this.mUpdateOpPool.release(paramUpdateOp);
    }
  }
  
  void recycleUpdateOpsAndClearList(List<UpdateOp> paramList)
  {
    int i = paramList.size();
    for (int j = 0; j < i; j++) {
      recycleUpdateOp((UpdateOp)paramList.get(j));
    }
    paramList.clear();
  }
  
  void reset()
  {
    recycleUpdateOpsAndClearList(this.mPendingUpdates);
    recycleUpdateOpsAndClearList(this.mPostponedList);
    this.mExistingUpdateTypes = 0;
  }
  
  static abstract interface Callback
  {
    public abstract RecyclerView.ViewHolder findViewHolder(int paramInt);
    
    public abstract void markViewHoldersUpdated(int paramInt1, int paramInt2, Object paramObject);
    
    public abstract void offsetPositionsForAdd(int paramInt1, int paramInt2);
    
    public abstract void offsetPositionsForMove(int paramInt1, int paramInt2);
    
    public abstract void offsetPositionsForRemovingInvisible(int paramInt1, int paramInt2);
    
    public abstract void offsetPositionsForRemovingLaidOutOrNewView(int paramInt1, int paramInt2);
    
    public abstract void onDispatchFirstPass(AdapterHelper.UpdateOp paramUpdateOp);
    
    public abstract void onDispatchSecondPass(AdapterHelper.UpdateOp paramUpdateOp);
  }
  
  static class UpdateOp
  {
    static final int ADD = 1;
    static final int MOVE = 8;
    static final int POOL_SIZE = 30;
    static final int REMOVE = 2;
    static final int UPDATE = 4;
    int cmd;
    int itemCount;
    Object payload;
    int positionStart;
    
    UpdateOp(int paramInt1, int paramInt2, int paramInt3, Object paramObject)
    {
      this.cmd = paramInt1;
      this.positionStart = paramInt2;
      this.itemCount = paramInt3;
      this.payload = paramObject;
    }
    
    String cmdToString()
    {
      switch (this.cmd)
      {
      case 3: 
      case 5: 
      case 6: 
      case 7: 
      default: 
        return "??";
      case 1: 
        return "add";
      case 2: 
        return "rm";
      case 4: 
        return "up";
      }
      return "mv";
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {}
      UpdateOp localUpdateOp;
      do
      {
        do
        {
          do
          {
            return true;
            if ((paramObject == null) || (getClass() != paramObject.getClass())) {
              return false;
            }
            localUpdateOp = (UpdateOp)paramObject;
            if (this.cmd != localUpdateOp.cmd) {
              return false;
            }
          } while ((this.cmd == 8) && (Math.abs(this.itemCount - this.positionStart) == 1) && (this.itemCount == localUpdateOp.positionStart) && (this.positionStart == localUpdateOp.itemCount));
          if (this.itemCount != localUpdateOp.itemCount) {
            return false;
          }
          if (this.positionStart != localUpdateOp.positionStart) {
            return false;
          }
          if (this.payload == null) {
            break;
          }
        } while (this.payload.equals(localUpdateOp.payload));
        return false;
      } while (localUpdateOp.payload == null);
      return false;
    }
    
    public int hashCode()
    {
      return 31 * (31 * this.cmd + this.positionStart) + this.itemCount;
    }
    
    public String toString()
    {
      return Integer.toHexString(System.identityHashCode(this)) + "[" + cmdToString() + ",s:" + this.positionStart + "c:" + this.itemCount + ",p:" + this.payload + "]";
    }
  }
}
