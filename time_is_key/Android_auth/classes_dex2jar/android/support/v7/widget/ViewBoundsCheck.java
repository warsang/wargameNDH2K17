package android.support.v7.widget;

import android.view.View;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

class ViewBoundsCheck
{
  static final int CVE_PVE_POS = 12;
  static final int CVE_PVS_POS = 8;
  static final int CVS_PVE_POS = 4;
  static final int CVS_PVS_POS = 0;
  static final int EQ = 2;
  static final int FLAG_CVE_EQ_PVE = 8192;
  static final int FLAG_CVE_EQ_PVS = 512;
  static final int FLAG_CVE_GT_PVE = 4096;
  static final int FLAG_CVE_GT_PVS = 256;
  static final int FLAG_CVE_LT_PVE = 16384;
  static final int FLAG_CVE_LT_PVS = 1024;
  static final int FLAG_CVS_EQ_PVE = 32;
  static final int FLAG_CVS_EQ_PVS = 2;
  static final int FLAG_CVS_GT_PVE = 16;
  static final int FLAG_CVS_GT_PVS = 1;
  static final int FLAG_CVS_LT_PVE = 64;
  static final int FLAG_CVS_LT_PVS = 4;
  static final int GT = 1;
  static final int LT = 4;
  static final int MASK = 7;
  BoundFlags mBoundFlags;
  final Callback mCallback;
  
  ViewBoundsCheck(Callback paramCallback)
  {
    this.mCallback = paramCallback;
    this.mBoundFlags = new BoundFlags();
  }
  
  View findOneViewWithinBoundFlags(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    int i = this.mCallback.getParentStart();
    int j = this.mCallback.getParentEnd();
    int k;
    Object localObject;
    int m;
    if (paramInt2 > paramInt1)
    {
      k = 1;
      localObject = null;
      m = paramInt1;
    }
    for (;;)
    {
      if (m == paramInt2) {
        break label179;
      }
      View localView = this.mCallback.getChildAt(m);
      int n = this.mCallback.getChildStart(localView);
      int i1 = this.mCallback.getChildEnd(localView);
      this.mBoundFlags.setBounds(i, j, n, i1);
      if (paramInt3 != 0)
      {
        this.mBoundFlags.resetFlags();
        this.mBoundFlags.addFlags(paramInt3);
        if (this.mBoundFlags.boundsMatch())
        {
          return localView;
          k = -1;
          break;
        }
      }
      if (paramInt4 != 0)
      {
        this.mBoundFlags.resetFlags();
        this.mBoundFlags.addFlags(paramInt4);
        if (this.mBoundFlags.boundsMatch()) {
          localObject = localView;
        }
      }
      m += k;
    }
    label179:
    return localObject;
  }
  
  boolean isViewWithinBoundFlags(View paramView, int paramInt)
  {
    this.mBoundFlags.setBounds(this.mCallback.getParentStart(), this.mCallback.getParentEnd(), this.mCallback.getChildStart(paramView), this.mCallback.getChildEnd(paramView));
    if (paramInt != 0)
    {
      this.mBoundFlags.resetFlags();
      this.mBoundFlags.addFlags(paramInt);
      return this.mBoundFlags.boundsMatch();
    }
    return false;
  }
  
  static class BoundFlags
  {
    int mBoundFlags = 0;
    int mChildEnd;
    int mChildStart;
    int mRvEnd;
    int mRvStart;
    
    BoundFlags() {}
    
    void addFlags(int paramInt)
    {
      this.mBoundFlags = (paramInt | this.mBoundFlags);
    }
    
    boolean boundsMatch()
    {
      if (((0x7 & this.mBoundFlags) != 0) && ((this.mBoundFlags & compare(this.mChildStart, this.mRvStart) << 0) == 0)) {}
      while ((((0x70 & this.mBoundFlags) != 0) && ((this.mBoundFlags & compare(this.mChildStart, this.mRvEnd) << 4) == 0)) || (((0x700 & this.mBoundFlags) != 0) && ((this.mBoundFlags & compare(this.mChildEnd, this.mRvStart) << 8) == 0)) || (((0x7000 & this.mBoundFlags) != 0) && ((this.mBoundFlags & compare(this.mChildEnd, this.mRvEnd) << 12) == 0))) {
        return false;
      }
      return true;
    }
    
    int compare(int paramInt1, int paramInt2)
    {
      if (paramInt1 > paramInt2) {
        return 1;
      }
      if (paramInt1 == paramInt2) {
        return 2;
      }
      return 4;
    }
    
    void resetFlags()
    {
      this.mBoundFlags = 0;
    }
    
    void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      this.mRvStart = paramInt1;
      this.mRvEnd = paramInt2;
      this.mChildStart = paramInt3;
      this.mChildEnd = paramInt4;
    }
    
    void setFlags(int paramInt1, int paramInt2)
    {
      this.mBoundFlags = (this.mBoundFlags & (paramInt2 ^ 0xFFFFFFFF) | paramInt1 & paramInt2);
    }
  }
  
  static abstract interface Callback
  {
    public abstract View getChildAt(int paramInt);
    
    public abstract int getChildCount();
    
    public abstract int getChildEnd(View paramView);
    
    public abstract int getChildStart(View paramView);
    
    public abstract View getParent();
    
    public abstract int getParentEnd();
    
    public abstract int getParentStart();
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface ViewBounds {}
}
