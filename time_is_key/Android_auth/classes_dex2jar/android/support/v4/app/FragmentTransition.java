package android.support.v4.app;

import android.graphics.Rect;
import android.os.Build.VERSION;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewCompat;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

class FragmentTransition
{
  private static final int[] INVERSE_OPS = { 0, 3, 0, 1, 5, 4, 7, 6 };
  
  FragmentTransition() {}
  
  private static void addSharedElementsWithMatchingNames(ArrayList<View> paramArrayList, ArrayMap<String, View> paramArrayMap, Collection<String> paramCollection)
  {
    for (int i = -1 + paramArrayMap.size(); i >= 0; i--)
    {
      View localView = (View)paramArrayMap.valueAt(i);
      if (paramCollection.contains(ViewCompat.getTransitionName(localView))) {
        paramArrayList.add(localView);
      }
    }
  }
  
  private static void addToFirstInLastOut(BackStackRecord paramBackStackRecord, BackStackRecord.Op paramOp, SparseArray<FragmentContainerTransition> paramSparseArray, boolean paramBoolean1, boolean paramBoolean2)
  {
    Fragment localFragment = paramOp.fragment;
    int i = localFragment.mContainerId;
    if (i == 0) {
      return;
    }
    int j;
    label33:
    boolean bool;
    int m;
    int n;
    if (paramBoolean1)
    {
      j = INVERSE_OPS[paramOp.cmd];
      k = 0;
      bool = false;
      m = 0;
      n = 0;
    }
    switch (j)
    {
    case 2: 
    default: 
    case 5: 
    case 1: 
    case 7: 
      for (;;)
      {
        FragmentContainerTransition localFragmentContainerTransition = (FragmentContainerTransition)paramSparseArray.get(i);
        if (bool)
        {
          localFragmentContainerTransition = ensureContainer(localFragmentContainerTransition, paramSparseArray, i);
          localFragmentContainerTransition.lastIn = localFragment;
          localFragmentContainerTransition.lastInIsPop = paramBoolean1;
          localFragmentContainerTransition.lastInTransaction = paramBackStackRecord;
        }
        if ((!paramBoolean2) && (m != 0))
        {
          if ((localFragmentContainerTransition != null) && (localFragmentContainerTransition.firstOut == localFragment)) {
            localFragmentContainerTransition.firstOut = null;
          }
          FragmentManagerImpl localFragmentManagerImpl = paramBackStackRecord.mManager;
          if ((localFragment.mState < 1) && (localFragmentManagerImpl.mCurState >= 1) && (!paramBackStackRecord.mAllowOptimization))
          {
            localFragmentManagerImpl.makeActive(localFragment);
            localFragmentManagerImpl.moveToState(localFragment, 1, 0, 0, false);
          }
        }
        if ((k != 0) && ((localFragmentContainerTransition == null) || (localFragmentContainerTransition.firstOut == null)))
        {
          localFragmentContainerTransition = ensureContainer(localFragmentContainerTransition, paramSparseArray, i);
          localFragmentContainerTransition.firstOut = localFragment;
          localFragmentContainerTransition.firstOutIsPop = paramBoolean1;
          localFragmentContainerTransition.firstOutTransaction = paramBackStackRecord;
        }
        if ((paramBoolean2) || (n == 0) || (localFragmentContainerTransition == null) || (localFragmentContainerTransition.lastIn != localFragment)) {
          break;
        }
        localFragmentContainerTransition.lastIn = null;
        return;
        j = paramOp.cmd;
        break label33;
        if (paramBoolean2) {
          if ((localFragment.mHiddenChanged) && (!localFragment.mHidden) && (localFragment.mAdded)) {
            bool = true;
          }
        }
        for (;;)
        {
          m = 1;
          k = 0;
          n = 0;
          break;
          bool = false;
          continue;
          bool = localFragment.mHidden;
        }
        if (!paramBoolean2) {
          break label385;
        }
        bool = localFragment.mIsNewlyAdded;
        m = 1;
        k = 0;
        n = 0;
      }
      if ((!localFragment.mAdded) && (!localFragment.mHidden)) {}
      for (bool = true;; bool = false) {
        break;
      }
    case 4: 
      label385:
      if (paramBoolean2)
      {
        if ((localFragment.mHiddenChanged) && (localFragment.mAdded) && (localFragment.mHidden)) {}
        for (k = 1;; k = 0)
        {
          n = 1;
          bool = false;
          m = 0;
          break;
        }
      }
      if ((localFragment.mAdded) && (!localFragment.mHidden)) {}
      for (k = 1;; k = 0) {
        break;
      }
    }
    if (paramBoolean2)
    {
      if ((!localFragment.mAdded) && (localFragment.mView != null) && (localFragment.mView.getVisibility() == 0) && (localFragment.mPostponedAlpha >= 0.0F)) {}
      for (k = 1;; k = 0)
      {
        n = 1;
        bool = false;
        m = 0;
        break;
      }
    }
    if ((localFragment.mAdded) && (!localFragment.mHidden)) {}
    for (int k = 1;; k = 0) {
      break;
    }
  }
  
  public static void calculateFragments(BackStackRecord paramBackStackRecord, SparseArray<FragmentContainerTransition> paramSparseArray, boolean paramBoolean)
  {
    int i = paramBackStackRecord.mOps.size();
    for (int j = 0; j < i; j++) {
      addToFirstInLastOut(paramBackStackRecord, (BackStackRecord.Op)paramBackStackRecord.mOps.get(j), paramSparseArray, false, paramBoolean);
    }
  }
  
  private static ArrayMap<String, String> calculateNameOverrides(int paramInt1, ArrayList<BackStackRecord> paramArrayList, ArrayList<Boolean> paramArrayList1, int paramInt2, int paramInt3)
  {
    ArrayMap localArrayMap = new ArrayMap();
    int i = paramInt3 - 1;
    if (i >= paramInt2)
    {
      BackStackRecord localBackStackRecord = (BackStackRecord)paramArrayList.get(i);
      if (!localBackStackRecord.interactsWith(paramInt1)) {}
      boolean bool;
      do
      {
        i--;
        break;
        bool = ((Boolean)paramArrayList1.get(i)).booleanValue();
      } while (localBackStackRecord.mSharedElementSourceNames == null);
      int j = localBackStackRecord.mSharedElementSourceNames.size();
      ArrayList localArrayList2;
      ArrayList localArrayList1;
      label98:
      int k;
      label101:
      String str1;
      String str2;
      if (bool)
      {
        localArrayList2 = localBackStackRecord.mSharedElementSourceNames;
        localArrayList1 = localBackStackRecord.mSharedElementTargetNames;
        k = 0;
        if (k < j)
        {
          str1 = (String)localArrayList1.get(k);
          str2 = (String)localArrayList2.get(k);
          String str3 = (String)localArrayMap.remove(str2);
          if (str3 == null) {
            break label182;
          }
          localArrayMap.put(str1, str3);
        }
      }
      for (;;)
      {
        k++;
        break label101;
        break;
        localArrayList1 = localBackStackRecord.mSharedElementSourceNames;
        localArrayList2 = localBackStackRecord.mSharedElementTargetNames;
        break label98;
        label182:
        localArrayMap.put(str1, str2);
      }
    }
    return localArrayMap;
  }
  
  public static void calculatePopFragments(BackStackRecord paramBackStackRecord, SparseArray<FragmentContainerTransition> paramSparseArray, boolean paramBoolean)
  {
    if (!paramBackStackRecord.mManager.mContainer.onHasView()) {}
    for (;;)
    {
      return;
      for (int i = -1 + paramBackStackRecord.mOps.size(); i >= 0; i--) {
        addToFirstInLastOut(paramBackStackRecord, (BackStackRecord.Op)paramBackStackRecord.mOps.get(i), paramSparseArray, true, paramBoolean);
      }
    }
  }
  
  private static void callSharedElementStartEnd(Fragment paramFragment1, Fragment paramFragment2, boolean paramBoolean1, ArrayMap<String, View> paramArrayMap, boolean paramBoolean2)
  {
    SharedElementCallback localSharedElementCallback;
    ArrayList localArrayList1;
    ArrayList localArrayList2;
    if (paramBoolean1)
    {
      localSharedElementCallback = paramFragment2.getEnterTransitionCallback();
      if (localSharedElementCallback == null) {
        break label113;
      }
      localArrayList1 = new ArrayList();
      localArrayList2 = new ArrayList();
      if (paramArrayMap != null) {
        break label89;
      }
    }
    label89:
    for (int i = 0;; i = paramArrayMap.size())
    {
      for (int j = 0; j < i; j++)
      {
        localArrayList2.add(paramArrayMap.keyAt(j));
        localArrayList1.add(paramArrayMap.valueAt(j));
      }
      localSharedElementCallback = paramFragment1.getEnterTransitionCallback();
      break;
    }
    if (paramBoolean2)
    {
      localSharedElementCallback.onSharedElementStart(localArrayList2, localArrayList1, null);
      label113:
      return;
    }
    localSharedElementCallback.onSharedElementEnd(localArrayList2, localArrayList1, null);
  }
  
  private static ArrayMap<String, View> captureInSharedElements(ArrayMap<String, String> paramArrayMap, Object paramObject, FragmentContainerTransition paramFragmentContainerTransition)
  {
    Fragment localFragment = paramFragmentContainerTransition.lastIn;
    View localView1 = localFragment.getView();
    if ((paramArrayMap.isEmpty()) || (paramObject == null) || (localView1 == null))
    {
      paramArrayMap.clear();
      localObject = null;
      return localObject;
    }
    Object localObject = new ArrayMap();
    FragmentTransitionCompat21.findNamedViews((Map)localObject, localView1);
    BackStackRecord localBackStackRecord = paramFragmentContainerTransition.lastInTransaction;
    SharedElementCallback localSharedElementCallback;
    ArrayList localArrayList;
    label79:
    int i;
    label115:
    String str1;
    View localView2;
    if (paramFragmentContainerTransition.lastInIsPop)
    {
      localSharedElementCallback = localFragment.getExitTransitionCallback();
      localArrayList = localBackStackRecord.mSharedElementSourceNames;
      if (localArrayList != null) {
        ((ArrayMap)localObject).retainAll(localArrayList);
      }
      if (localSharedElementCallback == null) {
        break label232;
      }
      localSharedElementCallback.onMapSharedElements(localArrayList, (Map)localObject);
      i = -1 + localArrayList.size();
      if (i >= 0)
      {
        str1 = (String)localArrayList.get(i);
        localView2 = (View)((ArrayMap)localObject).get(str1);
        if (localView2 != null) {
          break label191;
        }
        String str3 = findKeyForValue(paramArrayMap, str1);
        if (str3 != null) {
          paramArrayMap.remove(str3);
        }
      }
    }
    for (;;)
    {
      i--;
      break label115;
      break;
      localSharedElementCallback = localFragment.getEnterTransitionCallback();
      localArrayList = localBackStackRecord.mSharedElementTargetNames;
      break label79;
      label191:
      if (!str1.equals(ViewCompat.getTransitionName(localView2)))
      {
        String str2 = findKeyForValue(paramArrayMap, str1);
        if (str2 != null) {
          paramArrayMap.put(str2, ViewCompat.getTransitionName(localView2));
        }
      }
    }
    label232:
    retainValues(paramArrayMap, (ArrayMap)localObject);
    return localObject;
  }
  
  private static ArrayMap<String, View> captureOutSharedElements(ArrayMap<String, String> paramArrayMap, Object paramObject, FragmentContainerTransition paramFragmentContainerTransition)
  {
    if ((paramArrayMap.isEmpty()) || (paramObject == null))
    {
      paramArrayMap.clear();
      localObject = null;
      return localObject;
    }
    Fragment localFragment = paramFragmentContainerTransition.firstOut;
    Object localObject = new ArrayMap();
    FragmentTransitionCompat21.findNamedViews((Map)localObject, localFragment.getView());
    BackStackRecord localBackStackRecord = paramFragmentContainerTransition.firstOutTransaction;
    SharedElementCallback localSharedElementCallback;
    ArrayList localArrayList;
    label69:
    int i;
    label98:
    String str1;
    View localView;
    if (paramFragmentContainerTransition.firstOutIsPop)
    {
      localSharedElementCallback = localFragment.getEnterTransitionCallback();
      localArrayList = localBackStackRecord.mSharedElementTargetNames;
      ((ArrayMap)localObject).retainAll(localArrayList);
      if (localSharedElementCallback == null) {
        break label200;
      }
      localSharedElementCallback.onMapSharedElements(localArrayList, (Map)localObject);
      i = -1 + localArrayList.size();
      if (i >= 0)
      {
        str1 = (String)localArrayList.get(i);
        localView = (View)((ArrayMap)localObject).get(str1);
        if (localView != null) {
          break label161;
        }
        paramArrayMap.remove(str1);
      }
    }
    for (;;)
    {
      i--;
      break label98;
      break;
      localSharedElementCallback = localFragment.getExitTransitionCallback();
      localArrayList = localBackStackRecord.mSharedElementSourceNames;
      break label69;
      label161:
      if (!str1.equals(ViewCompat.getTransitionName(localView)))
      {
        String str2 = (String)paramArrayMap.remove(str1);
        paramArrayMap.put(ViewCompat.getTransitionName(localView), str2);
      }
    }
    label200:
    paramArrayMap.retainAll(((ArrayMap)localObject).keySet());
    return localObject;
  }
  
  private static ArrayList<View> configureEnteringExitingViews(Object paramObject, Fragment paramFragment, ArrayList<View> paramArrayList, View paramView)
  {
    ArrayList localArrayList = null;
    if (paramObject != null)
    {
      localArrayList = new ArrayList();
      View localView = paramFragment.getView();
      if (localView != null) {
        FragmentTransitionCompat21.captureTransitioningViews(localArrayList, localView);
      }
      if (paramArrayList != null) {
        localArrayList.removeAll(paramArrayList);
      }
      if (!localArrayList.isEmpty())
      {
        localArrayList.add(paramView);
        FragmentTransitionCompat21.addTargets(paramObject, localArrayList);
      }
    }
    return localArrayList;
  }
  
  private static Object configureSharedElementsOptimized(ViewGroup paramViewGroup, View paramView, ArrayMap<String, String> paramArrayMap, FragmentContainerTransition paramFragmentContainerTransition, ArrayList<View> paramArrayList1, ArrayList<View> paramArrayList2, Object paramObject1, Object paramObject2)
  {
    Fragment localFragment1 = paramFragmentContainerTransition.lastIn;
    final Fragment localFragment2 = paramFragmentContainerTransition.firstOut;
    if (localFragment1 != null) {
      localFragment1.getView().setVisibility(0);
    }
    if ((localFragment1 == null) || (localFragment2 == null)) {
      return null;
    }
    final boolean bool = paramFragmentContainerTransition.lastInIsPop;
    Object localObject;
    ArrayMap localArrayMap1;
    final ArrayMap localArrayMap2;
    if (paramArrayMap.isEmpty())
    {
      localObject = null;
      localArrayMap1 = captureOutSharedElements(paramArrayMap, localObject, paramFragmentContainerTransition);
      localArrayMap2 = captureInSharedElements(paramArrayMap, localObject, paramFragmentContainerTransition);
      if (!paramArrayMap.isEmpty()) {
        break label133;
      }
      if (localArrayMap1 != null) {
        localArrayMap1.clear();
      }
      localObject = null;
      if (localArrayMap2 != null) {
        localArrayMap2.clear();
      }
    }
    for (;;)
    {
      if ((paramObject1 != null) || (paramObject2 != null) || (localObject != null)) {
        break label158;
      }
      return null;
      localObject = getSharedElementTransition(localFragment1, localFragment2, bool);
      break;
      label133:
      addSharedElementsWithMatchingNames(paramArrayList1, localArrayMap1, paramArrayMap.keySet());
      addSharedElementsWithMatchingNames(paramArrayList2, localArrayMap2, paramArrayMap.values());
    }
    label158:
    callSharedElementStartEnd(localFragment1, localFragment2, bool, localArrayMap1, true);
    final Rect localRect;
    final View localView;
    if (localObject != null)
    {
      paramArrayList2.add(paramView);
      FragmentTransitionCompat21.setSharedElementTargets(localObject, paramView, paramArrayList1);
      setOutEpicenter(localObject, paramObject2, localArrayMap1, paramFragmentContainerTransition.firstOutIsPop, paramFragmentContainerTransition.firstOutTransaction);
      localRect = new Rect();
      localView = getInEpicenterView(localArrayMap2, paramFragmentContainerTransition, paramObject1, bool);
      if (localView != null) {
        FragmentTransitionCompat21.setEpicenter(paramObject1, localRect);
      }
    }
    for (;;)
    {
      OneShotPreDrawListener.add(paramViewGroup, new Runnable()
      {
        public void run()
        {
          FragmentTransition.callSharedElementStartEnd(this.val$inFragment, localFragment2, bool, localArrayMap2, false);
          if (localView != null) {
            FragmentTransitionCompat21.getBoundsOnScreen(localView, localRect);
          }
        }
      });
      return localObject;
      localView = null;
      localRect = null;
    }
  }
  
  private static Object configureSharedElementsUnoptimized(ViewGroup paramViewGroup, final View paramView, ArrayMap<String, String> paramArrayMap, final FragmentContainerTransition paramFragmentContainerTransition, final ArrayList<View> paramArrayList1, final ArrayList<View> paramArrayList2, final Object paramObject1, Object paramObject2)
  {
    final Fragment localFragment1 = paramFragmentContainerTransition.lastIn;
    final Fragment localFragment2 = paramFragmentContainerTransition.firstOut;
    if ((localFragment1 == null) || (localFragment2 == null)) {
      return null;
    }
    final boolean bool = paramFragmentContainerTransition.lastInIsPop;
    Object localObject1;
    ArrayMap localArrayMap;
    if (paramArrayMap.isEmpty())
    {
      localObject1 = null;
      localArrayMap = captureOutSharedElements(paramArrayMap, localObject1, paramFragmentContainerTransition);
      if (!paramArrayMap.isEmpty()) {
        break label90;
      }
    }
    for (final Object localObject2 = null;; localObject2 = localObject1)
    {
      if ((paramObject1 != null) || (paramObject2 != null) || (localObject2 != null)) {
        break label108;
      }
      return null;
      localObject1 = getSharedElementTransition(localFragment1, localFragment2, bool);
      break;
      label90:
      paramArrayList1.addAll(localArrayMap.values());
    }
    label108:
    callSharedElementStartEnd(localFragment1, localFragment2, bool, localArrayMap, true);
    final Rect localRect;
    if (localObject2 != null)
    {
      localRect = new Rect();
      FragmentTransitionCompat21.setSharedElementTargets(localObject2, paramView, paramArrayList1);
      setOutEpicenter(localObject2, paramObject2, localArrayMap, paramFragmentContainerTransition.firstOutIsPop, paramFragmentContainerTransition.firstOutTransaction);
      if (paramObject1 != null) {
        FragmentTransitionCompat21.setEpicenter(paramObject1, localRect);
      }
    }
    for (;;)
    {
      OneShotPreDrawListener.add(paramViewGroup, new Runnable()
      {
        public void run()
        {
          ArrayMap localArrayMap = FragmentTransition.captureInSharedElements(this.val$nameOverrides, localObject2, paramFragmentContainerTransition);
          if (localArrayMap != null)
          {
            paramArrayList2.addAll(localArrayMap.values());
            paramArrayList2.add(paramView);
          }
          FragmentTransition.callSharedElementStartEnd(localFragment1, localFragment2, bool, localArrayMap, false);
          if (localObject2 != null)
          {
            FragmentTransitionCompat21.swapSharedElementTargets(localObject2, paramArrayList1, paramArrayList2);
            View localView = FragmentTransition.getInEpicenterView(localArrayMap, paramFragmentContainerTransition, paramObject1, bool);
            if (localView != null) {
              FragmentTransitionCompat21.getBoundsOnScreen(localView, localRect);
            }
          }
        }
      });
      return localObject2;
      localRect = null;
    }
  }
  
  private static void configureTransitionsOptimized(FragmentManagerImpl paramFragmentManagerImpl, int paramInt, FragmentContainerTransition paramFragmentContainerTransition, View paramView, ArrayMap<String, String> paramArrayMap)
  {
    boolean bool1 = paramFragmentManagerImpl.mContainer.onHasView();
    ViewGroup localViewGroup = null;
    if (bool1) {
      localViewGroup = (ViewGroup)paramFragmentManagerImpl.mContainer.onFindViewById(paramInt);
    }
    if (localViewGroup == null) {}
    Fragment localFragment2;
    ArrayList localArrayList1;
    ArrayList localArrayList2;
    Object localObject1;
    Object localObject2;
    Object localObject3;
    ArrayList localArrayList3;
    ArrayList localArrayList4;
    Object localObject4;
    do
    {
      Fragment localFragment1;
      boolean bool2;
      do
      {
        return;
        localFragment1 = paramFragmentContainerTransition.lastIn;
        localFragment2 = paramFragmentContainerTransition.firstOut;
        bool2 = paramFragmentContainerTransition.lastInIsPop;
        boolean bool3 = paramFragmentContainerTransition.firstOutIsPop;
        localArrayList1 = new ArrayList();
        localArrayList2 = new ArrayList();
        localObject1 = getEnterTransition(localFragment1, bool2);
        localObject2 = getExitTransition(localFragment2, bool3);
        localObject3 = configureSharedElementsOptimized(localViewGroup, paramView, paramArrayMap, paramFragmentContainerTransition, localArrayList2, localArrayList1, localObject1, localObject2);
      } while ((localObject1 == null) && (localObject3 == null) && (localObject2 == null));
      localArrayList3 = configureEnteringExitingViews(localObject2, localFragment2, localArrayList2, paramView);
      localArrayList4 = configureEnteringExitingViews(localObject1, localFragment1, localArrayList1, paramView);
      setViewVisibility(localArrayList4, 4);
      localObject4 = mergeTransitions(localObject1, localObject2, localObject3, localFragment1, bool2);
    } while (localObject4 == null);
    replaceHide(localObject2, localFragment2, localArrayList3);
    ArrayList localArrayList5 = FragmentTransitionCompat21.prepareSetNameOverridesOptimized(localArrayList1);
    FragmentTransitionCompat21.scheduleRemoveTargets(localObject4, localObject1, localArrayList4, localObject2, localArrayList3, localObject3, localArrayList1);
    FragmentTransitionCompat21.beginDelayedTransition(localViewGroup, localObject4);
    FragmentTransitionCompat21.setNameOverridesOptimized(localViewGroup, localArrayList2, localArrayList1, localArrayList5, paramArrayMap);
    setViewVisibility(localArrayList4, 0);
    FragmentTransitionCompat21.swapSharedElementTargets(localObject3, localArrayList2, localArrayList1);
  }
  
  private static void configureTransitionsUnoptimized(FragmentManagerImpl paramFragmentManagerImpl, int paramInt, FragmentContainerTransition paramFragmentContainerTransition, View paramView, ArrayMap<String, String> paramArrayMap)
  {
    boolean bool1 = paramFragmentManagerImpl.mContainer.onHasView();
    ViewGroup localViewGroup = null;
    if (bool1) {
      localViewGroup = (ViewGroup)paramFragmentManagerImpl.mContainer.onFindViewById(paramInt);
    }
    if (localViewGroup == null) {}
    Fragment localFragment1;
    Object localObject1;
    Object localObject2;
    ArrayList localArrayList2;
    Object localObject3;
    ArrayList localArrayList3;
    Object localObject4;
    do
    {
      Fragment localFragment2;
      ArrayList localArrayList1;
      do
      {
        return;
        localFragment1 = paramFragmentContainerTransition.lastIn;
        localFragment2 = paramFragmentContainerTransition.firstOut;
        boolean bool2 = paramFragmentContainerTransition.lastInIsPop;
        boolean bool3 = paramFragmentContainerTransition.firstOutIsPop;
        localObject1 = getEnterTransition(localFragment1, bool2);
        localObject2 = getExitTransition(localFragment2, bool3);
        localArrayList1 = new ArrayList();
        localArrayList2 = new ArrayList();
        localObject3 = configureSharedElementsUnoptimized(localViewGroup, paramView, paramArrayMap, paramFragmentContainerTransition, localArrayList1, localArrayList2, localObject1, localObject2);
      } while ((localObject1 == null) && (localObject3 == null) && (localObject2 == null));
      localArrayList3 = configureEnteringExitingViews(localObject2, localFragment2, localArrayList1, paramView);
      if ((localArrayList3 == null) || (localArrayList3.isEmpty())) {
        localObject2 = null;
      }
      FragmentTransitionCompat21.addTarget(localObject1, paramView);
      localObject4 = mergeTransitions(localObject1, localObject2, localObject3, localFragment1, paramFragmentContainerTransition.lastInIsPop);
    } while (localObject4 == null);
    ArrayList localArrayList4 = new ArrayList();
    FragmentTransitionCompat21.scheduleRemoveTargets(localObject4, localObject1, localArrayList4, localObject2, localArrayList3, localObject3, localArrayList2);
    scheduleTargetChange(localViewGroup, localFragment1, paramView, localArrayList2, localObject1, localArrayList4, localObject2, localArrayList3);
    FragmentTransitionCompat21.setNameOverridesUnoptimized(localViewGroup, localArrayList2, paramArrayMap);
    FragmentTransitionCompat21.beginDelayedTransition(localViewGroup, localObject4);
    FragmentTransitionCompat21.scheduleNameReset(localViewGroup, localArrayList2, paramArrayMap);
  }
  
  private static FragmentContainerTransition ensureContainer(FragmentContainerTransition paramFragmentContainerTransition, SparseArray<FragmentContainerTransition> paramSparseArray, int paramInt)
  {
    if (paramFragmentContainerTransition == null)
    {
      paramFragmentContainerTransition = new FragmentContainerTransition();
      paramSparseArray.put(paramInt, paramFragmentContainerTransition);
    }
    return paramFragmentContainerTransition;
  }
  
  private static String findKeyForValue(ArrayMap<String, String> paramArrayMap, String paramString)
  {
    int i = paramArrayMap.size();
    for (int j = 0; j < i; j++) {
      if (paramString.equals(paramArrayMap.valueAt(j))) {
        return (String)paramArrayMap.keyAt(j);
      }
    }
    return null;
  }
  
  private static Object getEnterTransition(Fragment paramFragment, boolean paramBoolean)
  {
    if (paramFragment == null) {
      return null;
    }
    if (paramBoolean) {}
    for (Object localObject = paramFragment.getReenterTransition();; localObject = paramFragment.getEnterTransition()) {
      return FragmentTransitionCompat21.cloneTransition(localObject);
    }
  }
  
  private static Object getExitTransition(Fragment paramFragment, boolean paramBoolean)
  {
    if (paramFragment == null) {
      return null;
    }
    if (paramBoolean) {}
    for (Object localObject = paramFragment.getReturnTransition();; localObject = paramFragment.getExitTransition()) {
      return FragmentTransitionCompat21.cloneTransition(localObject);
    }
  }
  
  private static View getInEpicenterView(ArrayMap<String, View> paramArrayMap, FragmentContainerTransition paramFragmentContainerTransition, Object paramObject, boolean paramBoolean)
  {
    BackStackRecord localBackStackRecord = paramFragmentContainerTransition.lastInTransaction;
    if ((paramObject != null) && (paramArrayMap != null) && (localBackStackRecord.mSharedElementSourceNames != null) && (!localBackStackRecord.mSharedElementSourceNames.isEmpty()))
    {
      if (paramBoolean) {}
      for (String str = (String)localBackStackRecord.mSharedElementSourceNames.get(0);; str = (String)localBackStackRecord.mSharedElementTargetNames.get(0)) {
        return (View)paramArrayMap.get(str);
      }
    }
    return null;
  }
  
  private static Object getSharedElementTransition(Fragment paramFragment1, Fragment paramFragment2, boolean paramBoolean)
  {
    if ((paramFragment1 == null) || (paramFragment2 == null)) {
      return null;
    }
    if (paramBoolean) {}
    for (Object localObject = paramFragment2.getSharedElementReturnTransition();; localObject = paramFragment1.getSharedElementEnterTransition()) {
      return FragmentTransitionCompat21.wrapTransitionInSet(FragmentTransitionCompat21.cloneTransition(localObject));
    }
  }
  
  private static Object mergeTransitions(Object paramObject1, Object paramObject2, Object paramObject3, Fragment paramFragment, boolean paramBoolean)
  {
    boolean bool = true;
    if ((paramObject1 != null) && (paramObject2 != null) && (paramFragment != null)) {
      if (!paramBoolean) {
        break label38;
      }
    }
    label38:
    for (bool = paramFragment.getAllowReturnTransitionOverlap(); bool; bool = paramFragment.getAllowEnterTransitionOverlap()) {
      return FragmentTransitionCompat21.mergeTransitionsTogether(paramObject2, paramObject1, paramObject3);
    }
    return FragmentTransitionCompat21.mergeTransitionsInSequence(paramObject2, paramObject1, paramObject3);
  }
  
  private static void replaceHide(Object paramObject, Fragment paramFragment, ArrayList<View> paramArrayList)
  {
    if ((paramFragment != null) && (paramObject != null) && (paramFragment.mAdded) && (paramFragment.mHidden) && (paramFragment.mHiddenChanged))
    {
      paramFragment.setHideReplaced(true);
      FragmentTransitionCompat21.scheduleHideFragmentView(paramObject, paramFragment.getView(), paramArrayList);
      OneShotPreDrawListener.add(paramFragment.mContainer, new Runnable()
      {
        public void run()
        {
          FragmentTransition.setViewVisibility(this.val$exitingViews, 4);
        }
      });
    }
  }
  
  private static void retainValues(ArrayMap<String, String> paramArrayMap, ArrayMap<String, View> paramArrayMap1)
  {
    for (int i = -1 + paramArrayMap.size(); i >= 0; i--) {
      if (!paramArrayMap1.containsKey((String)paramArrayMap.valueAt(i))) {
        paramArrayMap.removeAt(i);
      }
    }
  }
  
  private static void scheduleTargetChange(ViewGroup paramViewGroup, final Fragment paramFragment, final View paramView, final ArrayList<View> paramArrayList1, Object paramObject1, final ArrayList<View> paramArrayList2, final Object paramObject2, final ArrayList<View> paramArrayList3)
  {
    OneShotPreDrawListener.add(paramViewGroup, new Runnable()
    {
      public void run()
      {
        if (this.val$enterTransition != null)
        {
          FragmentTransitionCompat21.removeTarget(this.val$enterTransition, paramView);
          ArrayList localArrayList2 = FragmentTransition.configureEnteringExitingViews(this.val$enterTransition, paramFragment, paramArrayList1, paramView);
          paramArrayList2.addAll(localArrayList2);
        }
        if (paramArrayList3 != null)
        {
          if (paramObject2 != null)
          {
            ArrayList localArrayList1 = new ArrayList();
            localArrayList1.add(paramView);
            FragmentTransitionCompat21.replaceTargets(paramObject2, paramArrayList3, localArrayList1);
          }
          paramArrayList3.clear();
          paramArrayList3.add(paramView);
        }
      }
    });
  }
  
  private static void setOutEpicenter(Object paramObject1, Object paramObject2, ArrayMap<String, View> paramArrayMap, boolean paramBoolean, BackStackRecord paramBackStackRecord)
  {
    if ((paramBackStackRecord.mSharedElementSourceNames != null) && (!paramBackStackRecord.mSharedElementSourceNames.isEmpty())) {
      if (!paramBoolean) {
        break label65;
      }
    }
    label65:
    for (String str = (String)paramBackStackRecord.mSharedElementTargetNames.get(0);; str = (String)paramBackStackRecord.mSharedElementSourceNames.get(0))
    {
      View localView = (View)paramArrayMap.get(str);
      FragmentTransitionCompat21.setEpicenter(paramObject1, localView);
      if (paramObject2 != null) {
        FragmentTransitionCompat21.setEpicenter(paramObject2, localView);
      }
      return;
    }
  }
  
  private static void setViewVisibility(ArrayList<View> paramArrayList, int paramInt)
  {
    if (paramArrayList == null) {}
    for (;;)
    {
      return;
      for (int i = -1 + paramArrayList.size(); i >= 0; i--) {
        ((View)paramArrayList.get(i)).setVisibility(paramInt);
      }
    }
  }
  
  static void startTransitions(FragmentManagerImpl paramFragmentManagerImpl, ArrayList<BackStackRecord> paramArrayList, ArrayList<Boolean> paramArrayList1, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    if ((paramFragmentManagerImpl.mCurState < 1) || (Build.VERSION.SDK_INT < 21)) {}
    SparseArray localSparseArray;
    do
    {
      return;
      localSparseArray = new SparseArray();
      int i = paramInt1;
      if (i < paramInt2)
      {
        BackStackRecord localBackStackRecord = (BackStackRecord)paramArrayList.get(i);
        if (((Boolean)paramArrayList1.get(i)).booleanValue()) {
          calculatePopFragments(localBackStackRecord, localSparseArray, paramBoolean);
        }
        for (;;)
        {
          i++;
          break;
          calculateFragments(localBackStackRecord, localSparseArray, paramBoolean);
        }
      }
    } while (localSparseArray.size() == 0);
    View localView = new View(paramFragmentManagerImpl.mHost.getContext());
    int j = localSparseArray.size();
    int k = 0;
    label123:
    int m;
    ArrayMap localArrayMap;
    FragmentContainerTransition localFragmentContainerTransition;
    if (k < j)
    {
      m = localSparseArray.keyAt(k);
      localArrayMap = calculateNameOverrides(m, paramArrayList, paramArrayList1, paramInt1, paramInt2);
      localFragmentContainerTransition = (FragmentContainerTransition)localSparseArray.valueAt(k);
      if (!paramBoolean) {
        break label186;
      }
      configureTransitionsOptimized(paramFragmentManagerImpl, m, localFragmentContainerTransition, localView, localArrayMap);
    }
    for (;;)
    {
      k++;
      break label123;
      break;
      label186:
      configureTransitionsUnoptimized(paramFragmentManagerImpl, m, localFragmentContainerTransition, localView, localArrayMap);
    }
  }
  
  static class FragmentContainerTransition
  {
    public Fragment firstOut;
    public boolean firstOutIsPop;
    public BackStackRecord firstOutTransaction;
    public Fragment lastIn;
    public boolean lastInIsPop;
    public BackStackRecord lastInTransaction;
    
    FragmentContainerTransition() {}
  }
}
