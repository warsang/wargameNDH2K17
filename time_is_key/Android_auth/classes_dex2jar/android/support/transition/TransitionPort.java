package android.support.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.annotation.TargetApi;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.LongSparseArray;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@TargetApi(14)
@RequiresApi(14)
abstract class TransitionPort
  implements Cloneable
{
  static final boolean DBG = false;
  private static final String LOG_TAG = "Transition";
  private static ThreadLocal<ArrayMap<Animator, AnimationInfo>> sRunningAnimators = new ThreadLocal();
  ArrayList<Animator> mAnimators = new ArrayList();
  boolean mCanRemoveViews = false;
  ArrayList<Animator> mCurrentAnimators = new ArrayList();
  long mDuration = -1L;
  private TransitionValuesMaps mEndValues = new TransitionValuesMaps();
  private boolean mEnded = false;
  TimeInterpolator mInterpolator = null;
  ArrayList<TransitionListener> mListeners = null;
  private String mName = getClass().getName();
  int mNumInstances = 0;
  TransitionSetPort mParent = null;
  boolean mPaused = false;
  ViewGroup mSceneRoot = null;
  long mStartDelay = -1L;
  private TransitionValuesMaps mStartValues = new TransitionValuesMaps();
  ArrayList<View> mTargetChildExcludes = null;
  ArrayList<View> mTargetExcludes = null;
  ArrayList<Integer> mTargetIdChildExcludes = null;
  ArrayList<Integer> mTargetIdExcludes = null;
  ArrayList<Integer> mTargetIds = new ArrayList();
  ArrayList<Class> mTargetTypeChildExcludes = null;
  ArrayList<Class> mTargetTypeExcludes = null;
  ArrayList<View> mTargets = new ArrayList();
  
  public TransitionPort() {}
  
  private void captureHierarchy(View paramView, boolean paramBoolean)
  {
    if (paramView == null) {}
    for (;;)
    {
      return;
      boolean bool = paramView.getParent() instanceof ListView;
      int i = 0;
      if (bool) {
        i = 1;
      }
      if ((i == 0) || (((ListView)paramView.getParent()).getAdapter().hasStableIds()))
      {
        int j = -1;
        long l = -1L;
        if (i == 0) {
          j = paramView.getId();
        }
        for (;;)
        {
          if (((this.mTargetIdExcludes != null) && (this.mTargetIdExcludes.contains(Integer.valueOf(j)))) || ((this.mTargetExcludes != null) && (this.mTargetExcludes.contains(paramView)))) {
            break label183;
          }
          if ((this.mTargetTypeExcludes == null) || (paramView == null)) {
            break label185;
          }
          int i1 = this.mTargetTypeExcludes.size();
          for (int i2 = 0;; i2++)
          {
            if (i2 >= i1) {
              break label185;
            }
            if (((Class)this.mTargetTypeExcludes.get(i2)).isInstance(paramView)) {
              break;
            }
          }
          ListView localListView = (ListView)paramView.getParent();
          l = localListView.getItemIdAtPosition(localListView.getPositionForView(paramView));
        }
        label183:
        continue;
        label185:
        TransitionValues localTransitionValues = new TransitionValues();
        localTransitionValues.view = paramView;
        if (paramBoolean)
        {
          captureStartValues(localTransitionValues);
          label210:
          if (!paramBoolean) {
            break label380;
          }
          if (i != 0) {
            break label363;
          }
          this.mStartValues.viewValues.put(paramView, localTransitionValues);
          if (j >= 0) {
            this.mStartValues.idValues.put(j, localTransitionValues);
          }
        }
        for (;;)
        {
          if ((!(paramView instanceof ViewGroup)) || ((this.mTargetIdChildExcludes != null) && (this.mTargetIdChildExcludes.contains(Integer.valueOf(j)))) || ((this.mTargetChildExcludes != null) && (this.mTargetChildExcludes.contains(paramView)))) {
            break label436;
          }
          if ((this.mTargetTypeChildExcludes == null) || (paramView == null)) {
            break label438;
          }
          int m = this.mTargetTypeChildExcludes.size();
          for (int n = 0;; n++)
          {
            if (n >= m) {
              break label438;
            }
            if (((Class)this.mTargetTypeChildExcludes.get(n)).isInstance(paramView)) {
              break;
            }
          }
          captureEndValues(localTransitionValues);
          break label210;
          label363:
          this.mStartValues.itemIdValues.put(l, localTransitionValues);
          continue;
          label380:
          if (i == 0)
          {
            this.mEndValues.viewValues.put(paramView, localTransitionValues);
            if (j >= 0) {
              this.mEndValues.idValues.put(j, localTransitionValues);
            }
          }
          else
          {
            this.mEndValues.itemIdValues.put(l, localTransitionValues);
          }
        }
        label436:
        continue;
        label438:
        ViewGroup localViewGroup = (ViewGroup)paramView;
        for (int k = 0; k < localViewGroup.getChildCount(); k++) {
          captureHierarchy(localViewGroup.getChildAt(k), paramBoolean);
        }
      }
    }
  }
  
  private ArrayList<Integer> excludeId(ArrayList<Integer> paramArrayList, int paramInt, boolean paramBoolean)
  {
    if (paramInt > 0)
    {
      if (paramBoolean) {
        paramArrayList = ArrayListManager.add(paramArrayList, Integer.valueOf(paramInt));
      }
    }
    else {
      return paramArrayList;
    }
    return ArrayListManager.remove(paramArrayList, Integer.valueOf(paramInt));
  }
  
  private ArrayList<Class> excludeType(ArrayList<Class> paramArrayList, Class paramClass, boolean paramBoolean)
  {
    if (paramClass != null)
    {
      if (paramBoolean) {
        paramArrayList = ArrayListManager.add(paramArrayList, paramClass);
      }
    }
    else {
      return paramArrayList;
    }
    return ArrayListManager.remove(paramArrayList, paramClass);
  }
  
  private ArrayList<View> excludeView(ArrayList<View> paramArrayList, View paramView, boolean paramBoolean)
  {
    if (paramView != null)
    {
      if (paramBoolean) {
        paramArrayList = ArrayListManager.add(paramArrayList, paramView);
      }
    }
    else {
      return paramArrayList;
    }
    return ArrayListManager.remove(paramArrayList, paramView);
  }
  
  private static ArrayMap<Animator, AnimationInfo> getRunningAnimators()
  {
    ArrayMap localArrayMap = (ArrayMap)sRunningAnimators.get();
    if (localArrayMap == null)
    {
      localArrayMap = new ArrayMap();
      sRunningAnimators.set(localArrayMap);
    }
    return localArrayMap;
  }
  
  private void runAnimator(Animator paramAnimator, final ArrayMap<Animator, AnimationInfo> paramArrayMap)
  {
    if (paramAnimator != null)
    {
      paramAnimator.addListener(new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          paramArrayMap.remove(paramAnonymousAnimator);
          TransitionPort.this.mCurrentAnimators.remove(paramAnonymousAnimator);
        }
        
        public void onAnimationStart(Animator paramAnonymousAnimator)
        {
          TransitionPort.this.mCurrentAnimators.add(paramAnonymousAnimator);
        }
      });
      animate(paramAnimator);
    }
  }
  
  public TransitionPort addListener(TransitionListener paramTransitionListener)
  {
    if (this.mListeners == null) {
      this.mListeners = new ArrayList();
    }
    this.mListeners.add(paramTransitionListener);
    return this;
  }
  
  public TransitionPort addTarget(int paramInt)
  {
    if (paramInt > 0) {
      this.mTargetIds.add(Integer.valueOf(paramInt));
    }
    return this;
  }
  
  public TransitionPort addTarget(View paramView)
  {
    this.mTargets.add(paramView);
    return this;
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  protected void animate(Animator paramAnimator)
  {
    if (paramAnimator == null)
    {
      end();
      return;
    }
    if (getDuration() >= 0L) {
      paramAnimator.setDuration(getDuration());
    }
    if (getStartDelay() >= 0L) {
      paramAnimator.setStartDelay(getStartDelay());
    }
    if (getInterpolator() != null) {
      paramAnimator.setInterpolator(getInterpolator());
    }
    paramAnimator.addListener(new AnimatorListenerAdapter()
    {
      public void onAnimationEnd(Animator paramAnonymousAnimator)
      {
        TransitionPort.this.end();
        paramAnonymousAnimator.removeListener(this);
      }
    });
    paramAnimator.start();
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  protected void cancel()
  {
    for (int i = -1 + this.mCurrentAnimators.size(); i >= 0; i--) {
      ((Animator)this.mCurrentAnimators.get(i)).cancel();
    }
    if ((this.mListeners != null) && (this.mListeners.size() > 0))
    {
      ArrayList localArrayList = (ArrayList)this.mListeners.clone();
      int j = localArrayList.size();
      for (int k = 0; k < j; k++) {
        ((TransitionListener)localArrayList.get(k)).onTransitionCancel(this);
      }
    }
  }
  
  public abstract void captureEndValues(TransitionValues paramTransitionValues);
  
  public abstract void captureStartValues(TransitionValues paramTransitionValues);
  
  void captureValues(ViewGroup paramViewGroup, boolean paramBoolean)
  {
    clearValues(paramBoolean);
    if ((this.mTargetIds.size() > 0) || (this.mTargets.size() > 0))
    {
      if (this.mTargetIds.size() > 0)
      {
        int j = 0;
        if (j < this.mTargetIds.size())
        {
          int k = ((Integer)this.mTargetIds.get(j)).intValue();
          View localView2 = paramViewGroup.findViewById(k);
          TransitionValues localTransitionValues2;
          if (localView2 != null)
          {
            localTransitionValues2 = new TransitionValues();
            localTransitionValues2.view = localView2;
            if (!paramBoolean) {
              break label150;
            }
            captureStartValues(localTransitionValues2);
            label106:
            if (!paramBoolean) {
              break label159;
            }
            this.mStartValues.viewValues.put(localView2, localTransitionValues2);
            if (k >= 0) {
              this.mStartValues.idValues.put(k, localTransitionValues2);
            }
          }
          for (;;)
          {
            j++;
            break;
            label150:
            captureEndValues(localTransitionValues2);
            break label106;
            label159:
            this.mEndValues.viewValues.put(localView2, localTransitionValues2);
            if (k >= 0) {
              this.mEndValues.idValues.put(k, localTransitionValues2);
            }
          }
        }
      }
      if (this.mTargets.size() > 0)
      {
        int i = 0;
        if (i < this.mTargets.size())
        {
          View localView1 = (View)this.mTargets.get(i);
          TransitionValues localTransitionValues1;
          if (localView1 != null)
          {
            localTransitionValues1 = new TransitionValues();
            localTransitionValues1.view = localView1;
            if (!paramBoolean) {
              break label288;
            }
            captureStartValues(localTransitionValues1);
            label263:
            if (!paramBoolean) {
              break label297;
            }
            this.mStartValues.viewValues.put(localView1, localTransitionValues1);
          }
          for (;;)
          {
            i++;
            break;
            label288:
            captureEndValues(localTransitionValues1);
            break label263;
            label297:
            this.mEndValues.viewValues.put(localView1, localTransitionValues1);
          }
        }
      }
    }
    else
    {
      captureHierarchy(paramViewGroup, paramBoolean);
    }
  }
  
  void clearValues(boolean paramBoolean)
  {
    if (paramBoolean)
    {
      this.mStartValues.viewValues.clear();
      this.mStartValues.idValues.clear();
      this.mStartValues.itemIdValues.clear();
      return;
    }
    this.mEndValues.viewValues.clear();
    this.mEndValues.idValues.clear();
    this.mEndValues.itemIdValues.clear();
  }
  
  public TransitionPort clone()
  {
    TransitionPort localTransitionPort = null;
    try
    {
      localTransitionPort = (TransitionPort)super.clone();
      localTransitionPort.mAnimators = new ArrayList();
      localTransitionPort.mStartValues = new TransitionValuesMaps();
      localTransitionPort.mEndValues = new TransitionValuesMaps();
      return localTransitionPort;
    }
    catch (CloneNotSupportedException localCloneNotSupportedException) {}
    return localTransitionPort;
  }
  
  public Animator createAnimator(ViewGroup paramViewGroup, TransitionValues paramTransitionValues1, TransitionValues paramTransitionValues2)
  {
    return null;
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  protected void createAnimators(ViewGroup paramViewGroup, TransitionValuesMaps paramTransitionValuesMaps1, TransitionValuesMaps paramTransitionValuesMaps2)
  {
    ArrayMap localArrayMap1 = new ArrayMap(paramTransitionValuesMaps2.viewValues);
    SparseArray localSparseArray = new SparseArray(paramTransitionValuesMaps2.idValues.size());
    for (int i = 0; i < paramTransitionValuesMaps2.idValues.size(); i++) {
      localSparseArray.put(paramTransitionValuesMaps2.idValues.keyAt(i), paramTransitionValuesMaps2.idValues.valueAt(i));
    }
    LongSparseArray localLongSparseArray = new LongSparseArray(paramTransitionValuesMaps2.itemIdValues.size());
    for (int j = 0; j < paramTransitionValuesMaps2.itemIdValues.size(); j++) {
      localLongSparseArray.put(paramTransitionValuesMaps2.itemIdValues.keyAt(j), paramTransitionValuesMaps2.itemIdValues.valueAt(j));
    }
    ArrayList localArrayList1 = new ArrayList();
    ArrayList localArrayList2 = new ArrayList();
    Iterator localIterator1 = paramTransitionValuesMaps1.viewValues.keySet().iterator();
    while (localIterator1.hasNext())
    {
      View localView4 = (View)localIterator1.next();
      boolean bool = localView4.getParent() instanceof ListView;
      int i12 = 0;
      if (bool) {
        i12 = 1;
      }
      if (i12 == 0)
      {
        int i13 = localView4.getId();
        TransitionValues localTransitionValues14;
        label246:
        TransitionValues localTransitionValues15;
        if (paramTransitionValuesMaps1.viewValues.get(localView4) != null)
        {
          localTransitionValues14 = (TransitionValues)paramTransitionValuesMaps1.viewValues.get(localView4);
          if (paramTransitionValuesMaps2.viewValues.get(localView4) == null) {
            break label335;
          }
          localTransitionValues15 = (TransitionValues)paramTransitionValuesMaps2.viewValues.get(localView4);
          localArrayMap1.remove(localView4);
        }
        for (;;)
        {
          localSparseArray.remove(i13);
          if (!isValidTarget(localView4, i13)) {
            break;
          }
          localArrayList1.add(localTransitionValues14);
          localArrayList2.add(localTransitionValues15);
          break;
          localTransitionValues14 = (TransitionValues)paramTransitionValuesMaps1.idValues.get(i13);
          break label246;
          label335:
          localTransitionValues15 = null;
          if (i13 != -1)
          {
            localTransitionValues15 = (TransitionValues)paramTransitionValuesMaps2.idValues.get(i13);
            Object localObject = null;
            Iterator localIterator3 = localArrayMap1.keySet().iterator();
            while (localIterator3.hasNext())
            {
              View localView5 = (View)localIterator3.next();
              if (localView5.getId() == i13) {
                localObject = localView5;
              }
            }
            if (localObject != null) {
              localArrayMap1.remove(localObject);
            }
          }
        }
      }
      ListView localListView = (ListView)localView4.getParent();
      if (localListView.getAdapter().hasStableIds())
      {
        long l3 = localListView.getItemIdAtPosition(localListView.getPositionForView(localView4));
        TransitionValues localTransitionValues13 = (TransitionValues)paramTransitionValuesMaps1.itemIdValues.get(l3);
        localLongSparseArray.remove(l3);
        localArrayList1.add(localTransitionValues13);
        localArrayList2.add(null);
      }
    }
    int k = paramTransitionValuesMaps1.itemIdValues.size();
    for (int m = 0; m < k; m++)
    {
      long l2 = paramTransitionValuesMaps1.itemIdValues.keyAt(m);
      if (isValidTarget(null, l2))
      {
        TransitionValues localTransitionValues11 = (TransitionValues)paramTransitionValuesMaps1.itemIdValues.get(l2);
        TransitionValues localTransitionValues12 = (TransitionValues)paramTransitionValuesMaps2.itemIdValues.get(l2);
        localLongSparseArray.remove(l2);
        localArrayList1.add(localTransitionValues11);
        localArrayList2.add(localTransitionValues12);
      }
    }
    Iterator localIterator2 = localArrayMap1.keySet().iterator();
    while (localIterator2.hasNext())
    {
      View localView3 = (View)localIterator2.next();
      int i11 = localView3.getId();
      if (isValidTarget(localView3, i11))
      {
        if (paramTransitionValuesMaps1.viewValues.get(localView3) != null) {}
        for (TransitionValues localTransitionValues9 = (TransitionValues)paramTransitionValuesMaps1.viewValues.get(localView3);; localTransitionValues9 = (TransitionValues)paramTransitionValuesMaps1.idValues.get(i11))
        {
          TransitionValues localTransitionValues10 = (TransitionValues)localArrayMap1.get(localView3);
          localSparseArray.remove(i11);
          localArrayList1.add(localTransitionValues9);
          localArrayList2.add(localTransitionValues10);
          break;
        }
      }
    }
    int n = localSparseArray.size();
    for (int i1 = 0; i1 < n; i1++)
    {
      int i10 = localSparseArray.keyAt(i1);
      if (isValidTarget(null, i10))
      {
        TransitionValues localTransitionValues7 = (TransitionValues)paramTransitionValuesMaps1.idValues.get(i10);
        TransitionValues localTransitionValues8 = (TransitionValues)localSparseArray.get(i10);
        localArrayList1.add(localTransitionValues7);
        localArrayList2.add(localTransitionValues8);
      }
    }
    int i2 = localLongSparseArray.size();
    for (int i3 = 0; i3 < i2; i3++)
    {
      long l1 = localLongSparseArray.keyAt(i3);
      TransitionValues localTransitionValues5 = (TransitionValues)paramTransitionValuesMaps1.itemIdValues.get(l1);
      TransitionValues localTransitionValues6 = (TransitionValues)localLongSparseArray.get(l1);
      localArrayList1.add(localTransitionValues5);
      localArrayList2.add(localTransitionValues6);
    }
    ArrayMap localArrayMap2 = getRunningAnimators();
    int i4 = 0;
    if (i4 < localArrayList1.size())
    {
      TransitionValues localTransitionValues1 = (TransitionValues)localArrayList1.get(i4);
      TransitionValues localTransitionValues2 = (TransitionValues)localArrayList2.get(i4);
      Animator localAnimator;
      View localView1;
      TransitionValues localTransitionValues3;
      int i7;
      if (((localTransitionValues1 != null) || (localTransitionValues2 != null)) && ((localTransitionValues1 == null) || (!localTransitionValues1.equals(localTransitionValues2))))
      {
        localAnimator = createAnimator(paramViewGroup, localTransitionValues1, localTransitionValues2);
        if (localAnimator != null)
        {
          if (localTransitionValues2 == null) {
            break label1282;
          }
          localView1 = localTransitionValues2.view;
          String[] arrayOfString = getTransitionProperties();
          localTransitionValues3 = null;
          if (localView1 != null)
          {
            localTransitionValues3 = null;
            if (arrayOfString != null)
            {
              int i5 = arrayOfString.length;
              localTransitionValues3 = null;
              if (i5 > 0)
              {
                localTransitionValues3 = new TransitionValues();
                View localView2 = localView1;
                localTransitionValues3.view = localView2;
                TransitionValues localTransitionValues4 = (TransitionValues)paramTransitionValuesMaps2.viewValues.get(localView1);
                if (localTransitionValues4 != null) {
                  for (int i8 = 0;; i8++)
                  {
                    int i9 = arrayOfString.length;
                    if (i8 >= i9) {
                      break;
                    }
                    localTransitionValues3.values.put(arrayOfString[i8], localTransitionValues4.values.get(arrayOfString[i8]));
                  }
                }
                int i6 = localArrayMap2.size();
                i7 = 0;
                label1125:
                if (i7 < i6)
                {
                  AnimationInfo localAnimationInfo2 = (AnimationInfo)localArrayMap2.get((Animator)localArrayMap2.keyAt(i7));
                  if ((localAnimationInfo2.values == null) || (localAnimationInfo2.view != localView1) || (((localAnimationInfo2.name != null) || (getName() != null)) && ((!localAnimationInfo2.name.equals(getName())) || (!localAnimationInfo2.values.equals(localTransitionValues3))))) {
                    break label1276;
                  }
                  localAnimator = null;
                }
              }
            }
          }
        }
      }
      for (;;)
      {
        if (localAnimator != null)
        {
          String str = getName();
          WindowIdPort localWindowIdPort = WindowIdPort.getWindowId(paramViewGroup);
          AnimationInfo localAnimationInfo1 = new AnimationInfo(localView1, str, localWindowIdPort, localTransitionValues3);
          localArrayMap2.put(localAnimator, localAnimationInfo1);
          this.mAnimators.add(localAnimator);
        }
        i4++;
        break;
        label1276:
        i7++;
        break label1125;
        label1282:
        localView1 = localTransitionValues1.view;
        localTransitionValues3 = null;
      }
    }
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  protected void end()
  {
    this.mNumInstances = (-1 + this.mNumInstances);
    if (this.mNumInstances == 0)
    {
      if ((this.mListeners != null) && (this.mListeners.size() > 0))
      {
        ArrayList localArrayList = (ArrayList)this.mListeners.clone();
        int k = localArrayList.size();
        for (int m = 0; m < k; m++) {
          ((TransitionListener)localArrayList.get(m)).onTransitionEnd(this);
        }
      }
      for (int i = 0; i < this.mStartValues.itemIdValues.size(); i++) {}
      for (int j = 0; j < this.mEndValues.itemIdValues.size(); j++) {}
      this.mEnded = true;
    }
  }
  
  public TransitionPort excludeChildren(int paramInt, boolean paramBoolean)
  {
    this.mTargetIdChildExcludes = excludeId(this.mTargetIdChildExcludes, paramInt, paramBoolean);
    return this;
  }
  
  public TransitionPort excludeChildren(View paramView, boolean paramBoolean)
  {
    this.mTargetChildExcludes = excludeView(this.mTargetChildExcludes, paramView, paramBoolean);
    return this;
  }
  
  public TransitionPort excludeChildren(Class paramClass, boolean paramBoolean)
  {
    this.mTargetTypeChildExcludes = excludeType(this.mTargetTypeChildExcludes, paramClass, paramBoolean);
    return this;
  }
  
  public TransitionPort excludeTarget(int paramInt, boolean paramBoolean)
  {
    this.mTargetIdExcludes = excludeId(this.mTargetIdExcludes, paramInt, paramBoolean);
    return this;
  }
  
  public TransitionPort excludeTarget(View paramView, boolean paramBoolean)
  {
    this.mTargetExcludes = excludeView(this.mTargetExcludes, paramView, paramBoolean);
    return this;
  }
  
  public TransitionPort excludeTarget(Class paramClass, boolean paramBoolean)
  {
    this.mTargetTypeExcludes = excludeType(this.mTargetTypeExcludes, paramClass, paramBoolean);
    return this;
  }
  
  public long getDuration()
  {
    return this.mDuration;
  }
  
  public TimeInterpolator getInterpolator()
  {
    return this.mInterpolator;
  }
  
  public String getName()
  {
    return this.mName;
  }
  
  public long getStartDelay()
  {
    return this.mStartDelay;
  }
  
  public List<Integer> getTargetIds()
  {
    return this.mTargetIds;
  }
  
  public List<View> getTargets()
  {
    return this.mTargets;
  }
  
  public String[] getTransitionProperties()
  {
    return null;
  }
  
  public TransitionValues getTransitionValues(View paramView, boolean paramBoolean)
  {
    TransitionValues localTransitionValues;
    if (this.mParent != null)
    {
      localTransitionValues = this.mParent.getTransitionValues(paramView, paramBoolean);
      return localTransitionValues;
    }
    if (paramBoolean) {}
    for (TransitionValuesMaps localTransitionValuesMaps = this.mStartValues;; localTransitionValuesMaps = this.mEndValues)
    {
      localTransitionValues = (TransitionValues)localTransitionValuesMaps.viewValues.get(paramView);
      if (localTransitionValues != null) {
        break;
      }
      int i = paramView.getId();
      if (i >= 0) {
        localTransitionValues = (TransitionValues)localTransitionValuesMaps.idValues.get(i);
      }
      if ((localTransitionValues != null) || (!(paramView.getParent() instanceof ListView))) {
        break;
      }
      ListView localListView = (ListView)paramView.getParent();
      long l = localListView.getItemIdAtPosition(localListView.getPositionForView(paramView));
      return (TransitionValues)localTransitionValuesMaps.itemIdValues.get(l);
    }
  }
  
  boolean isValidTarget(View paramView, long paramLong)
  {
    if ((this.mTargetIdExcludes != null) && (this.mTargetIdExcludes.contains(Integer.valueOf((int)paramLong)))) {
      return false;
    }
    if ((this.mTargetExcludes != null) && (this.mTargetExcludes.contains(paramView))) {
      return false;
    }
    if ((this.mTargetTypeExcludes != null) && (paramView != null))
    {
      int k = this.mTargetTypeExcludes.size();
      for (int m = 0; m < k; m++) {
        if (((Class)this.mTargetTypeExcludes.get(m)).isInstance(paramView)) {
          return false;
        }
      }
    }
    if ((this.mTargetIds.size() == 0) && (this.mTargets.size() == 0)) {
      return true;
    }
    if (this.mTargetIds.size() > 0) {
      for (int j = 0; j < this.mTargetIds.size(); j++) {
        if (((Integer)this.mTargetIds.get(j)).intValue() == paramLong) {
          return true;
        }
      }
    }
    if ((paramView != null) && (this.mTargets.size() > 0)) {
      for (int i = 0; i < this.mTargets.size(); i++) {
        if (this.mTargets.get(i) == paramView) {
          return true;
        }
      }
    }
    return false;
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public void pause(View paramView)
  {
    if (!this.mEnded)
    {
      ArrayMap localArrayMap = getRunningAnimators();
      int i = localArrayMap.size();
      WindowIdPort localWindowIdPort = WindowIdPort.getWindowId(paramView);
      for (int j = i - 1; j >= 0; j--)
      {
        AnimationInfo localAnimationInfo = (AnimationInfo)localArrayMap.valueAt(j);
        if ((localAnimationInfo.view != null) && (localWindowIdPort.equals(localAnimationInfo.windowId))) {
          ((Animator)localArrayMap.keyAt(j)).cancel();
        }
      }
      if ((this.mListeners != null) && (this.mListeners.size() > 0))
      {
        ArrayList localArrayList = (ArrayList)this.mListeners.clone();
        int k = localArrayList.size();
        for (int m = 0; m < k; m++) {
          ((TransitionListener)localArrayList.get(m)).onTransitionPause(this);
        }
      }
      this.mPaused = true;
    }
  }
  
  void playTransition(ViewGroup paramViewGroup)
  {
    ArrayMap localArrayMap = getRunningAnimators();
    int i = -1 + localArrayMap.size();
    if (i >= 0)
    {
      Animator localAnimator = (Animator)localArrayMap.keyAt(i);
      TransitionValues localTransitionValues2;
      if (localAnimator != null)
      {
        AnimationInfo localAnimationInfo = (AnimationInfo)localArrayMap.get(localAnimator);
        if ((localAnimationInfo != null) && (localAnimationInfo.view != null) && (localAnimationInfo.view.getContext() == paramViewGroup.getContext()))
        {
          TransitionValues localTransitionValues1 = localAnimationInfo.values;
          View localView = localAnimationInfo.view;
          if (this.mEndValues.viewValues == null) {
            break label280;
          }
          localTransitionValues2 = (TransitionValues)this.mEndValues.viewValues.get(localView);
          label110:
          if (localTransitionValues2 == null) {
            localTransitionValues2 = (TransitionValues)this.mEndValues.idValues.get(localView.getId());
          }
          int j = 0;
          if (localTransitionValues1 != null)
          {
            j = 0;
            if (localTransitionValues2 != null)
            {
              Iterator localIterator = localTransitionValues1.values.keySet().iterator();
              Object localObject1;
              Object localObject2;
              do
              {
                boolean bool = localIterator.hasNext();
                j = 0;
                if (!bool) {
                  break;
                }
                String str = (String)localIterator.next();
                localObject1 = localTransitionValues1.values.get(str);
                localObject2 = localTransitionValues2.values.get(str);
              } while ((localObject1 == null) || (localObject2 == null) || (localObject1.equals(localObject2)));
              j = 1;
            }
          }
          if (j != 0)
          {
            if ((!localAnimator.isRunning()) && (!localAnimator.isStarted())) {
              break label286;
            }
            localAnimator.cancel();
          }
        }
      }
      for (;;)
      {
        i--;
        break;
        label280:
        localTransitionValues2 = null;
        break label110;
        label286:
        localArrayMap.remove(localAnimator);
      }
    }
    createAnimators(paramViewGroup, this.mStartValues, this.mEndValues);
    runAnimators();
  }
  
  public TransitionPort removeListener(TransitionListener paramTransitionListener)
  {
    if (this.mListeners == null) {}
    do
    {
      return this;
      this.mListeners.remove(paramTransitionListener);
    } while (this.mListeners.size() != 0);
    this.mListeners = null;
    return this;
  }
  
  public TransitionPort removeTarget(int paramInt)
  {
    if (paramInt > 0) {
      this.mTargetIds.remove(Integer.valueOf(paramInt));
    }
    return this;
  }
  
  public TransitionPort removeTarget(View paramView)
  {
    if (paramView != null) {
      this.mTargets.remove(paramView);
    }
    return this;
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public void resume(View paramView)
  {
    if (this.mPaused)
    {
      if (!this.mEnded)
      {
        ArrayMap localArrayMap = getRunningAnimators();
        int i = localArrayMap.size();
        WindowIdPort localWindowIdPort = WindowIdPort.getWindowId(paramView);
        for (int j = i - 1; j >= 0; j--)
        {
          AnimationInfo localAnimationInfo = (AnimationInfo)localArrayMap.valueAt(j);
          if ((localAnimationInfo.view != null) && (localWindowIdPort.equals(localAnimationInfo.windowId))) {
            ((Animator)localArrayMap.keyAt(j)).end();
          }
        }
        if ((this.mListeners != null) && (this.mListeners.size() > 0))
        {
          ArrayList localArrayList = (ArrayList)this.mListeners.clone();
          int k = localArrayList.size();
          for (int m = 0; m < k; m++) {
            ((TransitionListener)localArrayList.get(m)).onTransitionResume(this);
          }
        }
      }
      this.mPaused = false;
    }
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  protected void runAnimators()
  {
    start();
    ArrayMap localArrayMap = getRunningAnimators();
    Iterator localIterator = this.mAnimators.iterator();
    while (localIterator.hasNext())
    {
      Animator localAnimator = (Animator)localIterator.next();
      if (localArrayMap.containsKey(localAnimator))
      {
        start();
        runAnimator(localAnimator, localArrayMap);
      }
    }
    this.mAnimators.clear();
    end();
  }
  
  void setCanRemoveViews(boolean paramBoolean)
  {
    this.mCanRemoveViews = paramBoolean;
  }
  
  public TransitionPort setDuration(long paramLong)
  {
    this.mDuration = paramLong;
    return this;
  }
  
  public TransitionPort setInterpolator(TimeInterpolator paramTimeInterpolator)
  {
    this.mInterpolator = paramTimeInterpolator;
    return this;
  }
  
  TransitionPort setSceneRoot(ViewGroup paramViewGroup)
  {
    this.mSceneRoot = paramViewGroup;
    return this;
  }
  
  public TransitionPort setStartDelay(long paramLong)
  {
    this.mStartDelay = paramLong;
    return this;
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  protected void start()
  {
    if (this.mNumInstances == 0)
    {
      if ((this.mListeners != null) && (this.mListeners.size() > 0))
      {
        ArrayList localArrayList = (ArrayList)this.mListeners.clone();
        int i = localArrayList.size();
        for (int j = 0; j < i; j++) {
          ((TransitionListener)localArrayList.get(j)).onTransitionStart(this);
        }
      }
      this.mEnded = false;
    }
    this.mNumInstances = (1 + this.mNumInstances);
  }
  
  public String toString()
  {
    return toString("");
  }
  
  String toString(String paramString)
  {
    String str1 = paramString + getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + ": ";
    if (this.mDuration != -1L) {
      str1 = str1 + "dur(" + this.mDuration + ") ";
    }
    if (this.mStartDelay != -1L) {
      str1 = str1 + "dly(" + this.mStartDelay + ") ";
    }
    if (this.mInterpolator != null) {
      str1 = str1 + "interp(" + this.mInterpolator + ") ";
    }
    if ((this.mTargetIds.size() > 0) || (this.mTargets.size() > 0))
    {
      String str2 = str1 + "tgts(";
      if (this.mTargetIds.size() > 0) {
        for (int j = 0; j < this.mTargetIds.size(); j++)
        {
          if (j > 0) {
            str2 = str2 + ", ";
          }
          str2 = str2 + this.mTargetIds.get(j);
        }
      }
      if (this.mTargets.size() > 0) {
        for (int i = 0; i < this.mTargets.size(); i++)
        {
          if (i > 0) {
            str2 = str2 + ", ";
          }
          str2 = str2 + this.mTargets.get(i);
        }
      }
      str1 = str2 + ")";
    }
    return str1;
  }
  
  private static class AnimationInfo
  {
    String name;
    TransitionValues values;
    View view;
    WindowIdPort windowId;
    
    AnimationInfo(View paramView, String paramString, WindowIdPort paramWindowIdPort, TransitionValues paramTransitionValues)
    {
      this.view = paramView;
      this.name = paramString;
      this.values = paramTransitionValues;
      this.windowId = paramWindowIdPort;
    }
  }
  
  private static class ArrayListManager
  {
    private ArrayListManager() {}
    
    static <T> ArrayList<T> add(ArrayList<T> paramArrayList, T paramT)
    {
      if (paramArrayList == null) {
        paramArrayList = new ArrayList();
      }
      if (!paramArrayList.contains(paramT)) {
        paramArrayList.add(paramT);
      }
      return paramArrayList;
    }
    
    static <T> ArrayList<T> remove(ArrayList<T> paramArrayList, T paramT)
    {
      if (paramArrayList != null)
      {
        paramArrayList.remove(paramT);
        if (paramArrayList.isEmpty()) {
          paramArrayList = null;
        }
      }
      return paramArrayList;
    }
  }
  
  public static abstract interface TransitionListener
  {
    public abstract void onTransitionCancel(TransitionPort paramTransitionPort);
    
    public abstract void onTransitionEnd(TransitionPort paramTransitionPort);
    
    public abstract void onTransitionPause(TransitionPort paramTransitionPort);
    
    public abstract void onTransitionResume(TransitionPort paramTransitionPort);
    
    public abstract void onTransitionStart(TransitionPort paramTransitionPort);
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public static class TransitionListenerAdapter
    implements TransitionPort.TransitionListener
  {
    public TransitionListenerAdapter() {}
    
    public void onTransitionCancel(TransitionPort paramTransitionPort) {}
    
    public void onTransitionEnd(TransitionPort paramTransitionPort) {}
    
    public void onTransitionPause(TransitionPort paramTransitionPort) {}
    
    public void onTransitionResume(TransitionPort paramTransitionPort) {}
    
    public void onTransitionStart(TransitionPort paramTransitionPort) {}
  }
}
