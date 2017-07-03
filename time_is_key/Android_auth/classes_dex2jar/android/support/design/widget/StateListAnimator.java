package android.support.design.widget;

import android.util.StateSet;
import java.util.ArrayList;

final class StateListAnimator
{
  private final ValueAnimatorCompat.AnimatorListener mAnimationListener = new ValueAnimatorCompat.AnimatorListenerAdapter()
  {
    public void onAnimationEnd(ValueAnimatorCompat paramAnonymousValueAnimatorCompat)
    {
      if (StateListAnimator.this.mRunningAnimator == paramAnonymousValueAnimatorCompat) {
        StateListAnimator.this.mRunningAnimator = null;
      }
    }
  };
  private Tuple mLastMatch = null;
  ValueAnimatorCompat mRunningAnimator = null;
  private final ArrayList<Tuple> mTuples = new ArrayList();
  
  StateListAnimator() {}
  
  private void cancel()
  {
    if (this.mRunningAnimator != null)
    {
      this.mRunningAnimator.cancel();
      this.mRunningAnimator = null;
    }
  }
  
  private void start(Tuple paramTuple)
  {
    this.mRunningAnimator = paramTuple.mAnimator;
    this.mRunningAnimator.start();
  }
  
  public void addState(int[] paramArrayOfInt, ValueAnimatorCompat paramValueAnimatorCompat)
  {
    Tuple localTuple = new Tuple(paramArrayOfInt, paramValueAnimatorCompat);
    paramValueAnimatorCompat.addListener(this.mAnimationListener);
    this.mTuples.add(localTuple);
  }
  
  public void jumpToCurrentState()
  {
    if (this.mRunningAnimator != null)
    {
      this.mRunningAnimator.end();
      this.mRunningAnimator = null;
    }
  }
  
  void setState(int[] paramArrayOfInt)
  {
    int i = this.mTuples.size();
    int j = 0;
    Object localObject = null;
    if (j < i)
    {
      Tuple localTuple = (Tuple)this.mTuples.get(j);
      if (StateSet.stateSetMatches(localTuple.mSpecs, paramArrayOfInt)) {
        localObject = localTuple;
      }
    }
    else
    {
      if (localObject != this.mLastMatch) {
        break label63;
      }
    }
    label63:
    do
    {
      return;
      j++;
      break;
      if (this.mLastMatch != null) {
        cancel();
      }
      this.mLastMatch = localObject;
    } while (localObject == null);
    start(localObject);
  }
  
  static class Tuple
  {
    final ValueAnimatorCompat mAnimator;
    final int[] mSpecs;
    
    Tuple(int[] paramArrayOfInt, ValueAnimatorCompat paramValueAnimatorCompat)
    {
      this.mSpecs = paramArrayOfInt;
      this.mAnimator = paramValueAnimatorCompat;
    }
  }
}
