package android.support.v7.util;

import android.util.SparseArray;
import java.lang.reflect.Array;

class TileList<T>
{
  Tile<T> mLastAccessedTile;
  final int mTileSize;
  private final SparseArray<Tile<T>> mTiles = new SparseArray(10);
  
  public TileList(int paramInt)
  {
    this.mTileSize = paramInt;
  }
  
  public Tile<T> addOrReplace(Tile<T> paramTile)
  {
    int i = this.mTiles.indexOfKey(paramTile.mStartPosition);
    Object localObject;
    if (i < 0)
    {
      this.mTiles.put(paramTile.mStartPosition, paramTile);
      localObject = null;
    }
    do
    {
      return localObject;
      localObject = (Tile)this.mTiles.valueAt(i);
      this.mTiles.setValueAt(i, paramTile);
    } while (this.mLastAccessedTile != localObject);
    this.mLastAccessedTile = paramTile;
    return localObject;
  }
  
  public void clear()
  {
    this.mTiles.clear();
  }
  
  public Tile<T> getAtIndex(int paramInt)
  {
    return (Tile)this.mTiles.valueAt(paramInt);
  }
  
  public T getItemAt(int paramInt)
  {
    if ((this.mLastAccessedTile == null) || (!this.mLastAccessedTile.containsPosition(paramInt)))
    {
      int i = paramInt - paramInt % this.mTileSize;
      int j = this.mTiles.indexOfKey(i);
      if (j < 0) {
        return null;
      }
      this.mLastAccessedTile = ((Tile)this.mTiles.valueAt(j));
    }
    return this.mLastAccessedTile.getByPosition(paramInt);
  }
  
  public Tile<T> removeAtPos(int paramInt)
  {
    Tile localTile = (Tile)this.mTiles.get(paramInt);
    if (this.mLastAccessedTile == localTile) {
      this.mLastAccessedTile = null;
    }
    this.mTiles.delete(paramInt);
    return localTile;
  }
  
  public int size()
  {
    return this.mTiles.size();
  }
  
  public static class Tile<T>
  {
    public int mItemCount;
    public final T[] mItems;
    Tile<T> mNext;
    public int mStartPosition;
    
    public Tile(Class<T> paramClass, int paramInt)
    {
      this.mItems = ((Object[])Array.newInstance(paramClass, paramInt));
    }
    
    boolean containsPosition(int paramInt)
    {
      return (this.mStartPosition <= paramInt) && (paramInt < this.mStartPosition + this.mItemCount);
    }
    
    T getByPosition(int paramInt)
    {
      return this.mItems[(paramInt - this.mStartPosition)];
    }
  }
}
