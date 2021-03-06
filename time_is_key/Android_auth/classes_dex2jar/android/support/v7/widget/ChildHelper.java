package android.support.v7.widget;

import android.view.View;
import android.view.ViewGroup.LayoutParams;
import java.util.ArrayList;
import java.util.List;

class ChildHelper
{
  private static final boolean DEBUG = false;
  private static final String TAG = "ChildrenHelper";
  final Bucket mBucket;
  final Callback mCallback;
  final List<View> mHiddenViews;
  
  ChildHelper(Callback paramCallback)
  {
    this.mCallback = paramCallback;
    this.mBucket = new Bucket();
    this.mHiddenViews = new ArrayList();
  }
  
  private int getOffset(int paramInt)
  {
    if (paramInt < 0)
    {
      j = -1;
      return j;
    }
    int i = this.mCallback.getChildCount();
    int j = paramInt;
    for (;;)
    {
      if (j >= i) {
        break label69;
      }
      int k = paramInt - (j - this.mBucket.countOnesBefore(j));
      if (k == 0)
      {
        while (this.mBucket.get(j)) {
          j++;
        }
        break;
      }
      j += k;
    }
    label69:
    return -1;
  }
  
  private void hideViewInternal(View paramView)
  {
    this.mHiddenViews.add(paramView);
    this.mCallback.onEnteredHiddenState(paramView);
  }
  
  private boolean unhideViewInternal(View paramView)
  {
    if (this.mHiddenViews.remove(paramView))
    {
      this.mCallback.onLeftHiddenState(paramView);
      return true;
    }
    return false;
  }
  
  void addView(View paramView, int paramInt, boolean paramBoolean)
  {
    if (paramInt < 0) {}
    for (int i = this.mCallback.getChildCount();; i = getOffset(paramInt))
    {
      this.mBucket.insert(i, paramBoolean);
      if (paramBoolean) {
        hideViewInternal(paramView);
      }
      this.mCallback.addView(paramView, i);
      return;
    }
  }
  
  void addView(View paramView, boolean paramBoolean)
  {
    addView(paramView, -1, paramBoolean);
  }
  
  void attachViewToParent(View paramView, int paramInt, ViewGroup.LayoutParams paramLayoutParams, boolean paramBoolean)
  {
    if (paramInt < 0) {}
    for (int i = this.mCallback.getChildCount();; i = getOffset(paramInt))
    {
      this.mBucket.insert(i, paramBoolean);
      if (paramBoolean) {
        hideViewInternal(paramView);
      }
      this.mCallback.attachViewToParent(paramView, i, paramLayoutParams);
      return;
    }
  }
  
  void detachViewFromParent(int paramInt)
  {
    int i = getOffset(paramInt);
    this.mBucket.remove(i);
    this.mCallback.detachViewFromParent(i);
  }
  
  View findHiddenNonRemovedView(int paramInt)
  {
    int i = this.mHiddenViews.size();
    for (int j = 0; j < i; j++)
    {
      View localView = (View)this.mHiddenViews.get(j);
      RecyclerView.ViewHolder localViewHolder = this.mCallback.getChildViewHolder(localView);
      if ((localViewHolder.getLayoutPosition() == paramInt) && (!localViewHolder.isInvalid()) && (!localViewHolder.isRemoved())) {
        return localView;
      }
    }
    return null;
  }
  
  View getChildAt(int paramInt)
  {
    int i = getOffset(paramInt);
    return this.mCallback.getChildAt(i);
  }
  
  int getChildCount()
  {
    return this.mCallback.getChildCount() - this.mHiddenViews.size();
  }
  
  View getUnfilteredChildAt(int paramInt)
  {
    return this.mCallback.getChildAt(paramInt);
  }
  
  int getUnfilteredChildCount()
  {
    return this.mCallback.getChildCount();
  }
  
  void hide(View paramView)
  {
    int i = this.mCallback.indexOfChild(paramView);
    if (i < 0) {
      throw new IllegalArgumentException("view is not a child, cannot hide " + paramView);
    }
    this.mBucket.set(i);
    hideViewInternal(paramView);
  }
  
  int indexOfChild(View paramView)
  {
    int i = this.mCallback.indexOfChild(paramView);
    if (i == -1) {}
    while (this.mBucket.get(i)) {
      return -1;
    }
    return i - this.mBucket.countOnesBefore(i);
  }
  
  boolean isHidden(View paramView)
  {
    return this.mHiddenViews.contains(paramView);
  }
  
  void removeAllViewsUnfiltered()
  {
    this.mBucket.reset();
    for (int i = -1 + this.mHiddenViews.size(); i >= 0; i--)
    {
      this.mCallback.onLeftHiddenState((View)this.mHiddenViews.get(i));
      this.mHiddenViews.remove(i);
    }
    this.mCallback.removeAllViews();
  }
  
  void removeView(View paramView)
  {
    int i = this.mCallback.indexOfChild(paramView);
    if (i < 0) {
      return;
    }
    if (this.mBucket.remove(i)) {
      unhideViewInternal(paramView);
    }
    this.mCallback.removeViewAt(i);
  }
  
  void removeViewAt(int paramInt)
  {
    int i = getOffset(paramInt);
    View localView = this.mCallback.getChildAt(i);
    if (localView == null) {
      return;
    }
    if (this.mBucket.remove(i)) {
      unhideViewInternal(localView);
    }
    this.mCallback.removeViewAt(i);
  }
  
  boolean removeViewIfHidden(View paramView)
  {
    int i = this.mCallback.indexOfChild(paramView);
    if (i == -1)
    {
      if (unhideViewInternal(paramView)) {}
      return true;
    }
    if (this.mBucket.get(i))
    {
      this.mBucket.remove(i);
      if (!unhideViewInternal(paramView)) {}
      this.mCallback.removeViewAt(i);
      return true;
    }
    return false;
  }
  
  public String toString()
  {
    return this.mBucket.toString() + ", hidden list:" + this.mHiddenViews.size();
  }
  
  void unhide(View paramView)
  {
    int i = this.mCallback.indexOfChild(paramView);
    if (i < 0) {
      throw new IllegalArgumentException("view is not a child, cannot hide " + paramView);
    }
    if (!this.mBucket.get(i)) {
      throw new RuntimeException("trying to unhide a view that was not hidden" + paramView);
    }
    this.mBucket.clear(i);
    unhideViewInternal(paramView);
  }
  
  static class Bucket
  {
    static final int BITS_PER_WORD = 64;
    static final long LAST_BIT = Long.MIN_VALUE;
    long mData = 0L;
    Bucket next;
    
    Bucket() {}
    
    private void ensureNext()
    {
      if (this.next == null) {
        this.next = new Bucket();
      }
    }
    
    void clear(int paramInt)
    {
      if (paramInt >= 64)
      {
        if (this.next != null) {
          this.next.clear(paramInt - 64);
        }
        return;
      }
      this.mData &= (0xFFFFFFFFFFFFFFFF ^ 1L << paramInt);
    }
    
    int countOnesBefore(int paramInt)
    {
      if (this.next == null)
      {
        if (paramInt >= 64) {
          return Long.bitCount(this.mData);
        }
        return Long.bitCount(this.mData & (1L << paramInt) - 1L);
      }
      if (paramInt < 64) {
        return Long.bitCount(this.mData & (1L << paramInt) - 1L);
      }
      return this.next.countOnesBefore(paramInt - 64) + Long.bitCount(this.mData);
    }
    
    boolean get(int paramInt)
    {
      if (paramInt >= 64)
      {
        ensureNext();
        return this.next.get(paramInt - 64);
      }
      return (this.mData & 1L << paramInt) != 0L;
    }
    
    void insert(int paramInt, boolean paramBoolean)
    {
      if (paramInt >= 64)
      {
        ensureNext();
        this.next.insert(paramInt - 64, paramBoolean);
      }
      label109:
      label115:
      for (;;)
      {
        return;
        boolean bool;
        if ((0x8000000000000000 & this.mData) != 0L)
        {
          bool = true;
          long l = (1L << paramInt) - 1L;
          this.mData = (l & this.mData | (this.mData & (0xFFFFFFFFFFFFFFFF ^ l)) << 1);
          if (!paramBoolean) {
            break label109;
          }
          set(paramInt);
        }
        for (;;)
        {
          if ((!bool) && (this.next == null)) {
            break label115;
          }
          ensureNext();
          this.next.insert(0, bool);
          return;
          bool = false;
          break;
          clear(paramInt);
        }
      }
    }
    
    boolean remove(int paramInt)
    {
      if (paramInt >= 64)
      {
        ensureNext();
        bool = this.next.remove(paramInt - 64);
        return bool;
      }
      long l1 = 1L << paramInt;
      if ((l1 & this.mData) != 0L) {}
      for (boolean bool = true;; bool = false)
      {
        this.mData &= (0xFFFFFFFFFFFFFFFF ^ l1);
        long l2 = l1 - 1L;
        this.mData = (l2 & this.mData | Long.rotateRight(this.mData & (0xFFFFFFFFFFFFFFFF ^ l2), 1));
        if (this.next == null) {
          break;
        }
        if (this.next.get(0)) {
          set(63);
        }
        this.next.remove(0);
        return bool;
      }
    }
    
    void reset()
    {
      this.mData = 0L;
      if (this.next != null) {
        this.next.reset();
      }
    }
    
    void set(int paramInt)
    {
      if (paramInt >= 64)
      {
        ensureNext();
        this.next.set(paramInt - 64);
        return;
      }
      this.mData |= 1L << paramInt;
    }
    
    public String toString()
    {
      if (this.next == null) {
        return Long.toBinaryString(this.mData);
      }
      return this.next.toString() + "xx" + Long.toBinaryString(this.mData);
    }
  }
  
  static abstract interface Callback
  {
    public abstract void addView(View paramView, int paramInt);
    
    public abstract void attachViewToParent(View paramView, int paramInt, ViewGroup.LayoutParams paramLayoutParams);
    
    public abstract void detachViewFromParent(int paramInt);
    
    public abstract View getChildAt(int paramInt);
    
    public abstract int getChildCount();
    
    public abstract RecyclerView.ViewHolder getChildViewHolder(View paramView);
    
    public abstract int indexOfChild(View paramView);
    
    public abstract void onEnteredHiddenState(View paramView);
    
    public abstract void onLeftHiddenState(View paramView);
    
    public abstract void removeAllViews();
    
    public abstract void removeViewAt(int paramInt);
  }
}
