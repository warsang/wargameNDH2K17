package android.support.transition;

import android.annotation.TargetApi;
import android.support.annotation.RequiresApi;
import android.support.annotation.RestrictTo;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.View.OnAttachStateChangeListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnPreDrawListener;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;

@TargetApi(14)
@RequiresApi(14)
class TransitionManagerPort
{
  private static final String[] EMPTY_STRINGS = new String[0];
  private static String LOG_TAG = "TransitionManager";
  private static TransitionPort sDefaultTransition = new AutoTransitionPort();
  static ArrayList<ViewGroup> sPendingTransitions = new ArrayList();
  private static ThreadLocal<WeakReference<ArrayMap<ViewGroup, ArrayList<TransitionPort>>>> sRunningTransitions = new ThreadLocal();
  ArrayMap<String, ArrayMap<ScenePort, TransitionPort>> mNameSceneTransitions = new ArrayMap();
  ArrayMap<ScenePort, ArrayMap<String, TransitionPort>> mSceneNameTransitions = new ArrayMap();
  ArrayMap<ScenePort, ArrayMap<ScenePort, TransitionPort>> mScenePairTransitions = new ArrayMap();
  ArrayMap<ScenePort, TransitionPort> mSceneTransitions = new ArrayMap();
  
  TransitionManagerPort() {}
  
  public static void beginDelayedTransition(ViewGroup paramViewGroup)
  {
    beginDelayedTransition(paramViewGroup, null);
  }
  
  public static void beginDelayedTransition(ViewGroup paramViewGroup, TransitionPort paramTransitionPort)
  {
    if ((!sPendingTransitions.contains(paramViewGroup)) && (ViewCompat.isLaidOut(paramViewGroup)))
    {
      sPendingTransitions.add(paramViewGroup);
      if (paramTransitionPort == null) {
        paramTransitionPort = sDefaultTransition;
      }
      TransitionPort localTransitionPort = paramTransitionPort.clone();
      sceneChangeSetup(paramViewGroup, localTransitionPort);
      ScenePort.setCurrentScene(paramViewGroup, null);
      sceneChangeRunTransition(paramViewGroup, localTransitionPort);
    }
  }
  
  private static void changeScene(ScenePort paramScenePort, TransitionPort paramTransitionPort)
  {
    ViewGroup localViewGroup = paramScenePort.getSceneRoot();
    TransitionPort localTransitionPort = null;
    if (paramTransitionPort != null)
    {
      localTransitionPort = paramTransitionPort.clone();
      localTransitionPort.setSceneRoot(localViewGroup);
    }
    ScenePort localScenePort = ScenePort.getCurrentScene(localViewGroup);
    if ((localScenePort != null) && (localScenePort.isCreatedFromLayoutResource())) {
      localTransitionPort.setCanRemoveViews(true);
    }
    sceneChangeSetup(localViewGroup, localTransitionPort);
    paramScenePort.enter();
    sceneChangeRunTransition(localViewGroup, localTransitionPort);
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public static TransitionPort getDefaultTransition()
  {
    return sDefaultTransition;
  }
  
  static ArrayMap<ViewGroup, ArrayList<TransitionPort>> getRunningTransitions()
  {
    WeakReference localWeakReference = (WeakReference)sRunningTransitions.get();
    if ((localWeakReference == null) || (localWeakReference.get() == null))
    {
      localWeakReference = new WeakReference(new ArrayMap());
      sRunningTransitions.set(localWeakReference);
    }
    return (ArrayMap)localWeakReference.get();
  }
  
  private TransitionPort getTransition(ScenePort paramScenePort)
  {
    ViewGroup localViewGroup = paramScenePort.getSceneRoot();
    if (localViewGroup != null)
    {
      ScenePort localScenePort = ScenePort.getCurrentScene(localViewGroup);
      if (localScenePort != null)
      {
        ArrayMap localArrayMap = (ArrayMap)this.mScenePairTransitions.get(paramScenePort);
        if (localArrayMap != null)
        {
          TransitionPort localTransitionPort2 = (TransitionPort)localArrayMap.get(localScenePort);
          if (localTransitionPort2 != null) {
            return localTransitionPort2;
          }
        }
      }
    }
    TransitionPort localTransitionPort1 = (TransitionPort)this.mSceneTransitions.get(paramScenePort);
    if (localTransitionPort1 != null) {
      return localTransitionPort1;
    }
    return sDefaultTransition;
  }
  
  public static void go(ScenePort paramScenePort)
  {
    changeScene(paramScenePort, sDefaultTransition);
  }
  
  public static void go(ScenePort paramScenePort, TransitionPort paramTransitionPort)
  {
    changeScene(paramScenePort, paramTransitionPort);
  }
  
  private static void sceneChangeRunTransition(ViewGroup paramViewGroup, TransitionPort paramTransitionPort)
  {
    if ((paramTransitionPort != null) && (paramViewGroup != null))
    {
      MultiListener localMultiListener = new MultiListener(paramTransitionPort, paramViewGroup);
      paramViewGroup.addOnAttachStateChangeListener(localMultiListener);
      paramViewGroup.getViewTreeObserver().addOnPreDrawListener(localMultiListener);
    }
  }
  
  private static void sceneChangeSetup(ViewGroup paramViewGroup, TransitionPort paramTransitionPort)
  {
    ArrayList localArrayList = (ArrayList)getRunningTransitions().get(paramViewGroup);
    if ((localArrayList != null) && (localArrayList.size() > 0))
    {
      Iterator localIterator = localArrayList.iterator();
      while (localIterator.hasNext()) {
        ((TransitionPort)localIterator.next()).pause(paramViewGroup);
      }
    }
    if (paramTransitionPort != null) {
      paramTransitionPort.captureValues(paramViewGroup, true);
    }
    ScenePort localScenePort = ScenePort.getCurrentScene(paramViewGroup);
    if (localScenePort != null) {
      localScenePort.exit();
    }
  }
  
  public TransitionPort getNamedTransition(ScenePort paramScenePort, String paramString)
  {
    ArrayMap localArrayMap = (ArrayMap)this.mSceneNameTransitions.get(paramScenePort);
    if (localArrayMap != null) {
      return (TransitionPort)localArrayMap.get(paramString);
    }
    return null;
  }
  
  public TransitionPort getNamedTransition(String paramString, ScenePort paramScenePort)
  {
    ArrayMap localArrayMap = (ArrayMap)this.mNameSceneTransitions.get(paramString);
    if (localArrayMap != null) {
      return (TransitionPort)localArrayMap.get(paramScenePort);
    }
    return null;
  }
  
  public String[] getTargetSceneNames(ScenePort paramScenePort)
  {
    ArrayMap localArrayMap = (ArrayMap)this.mSceneNameTransitions.get(paramScenePort);
    String[] arrayOfString;
    if (localArrayMap == null) {
      arrayOfString = EMPTY_STRINGS;
    }
    for (;;)
    {
      return arrayOfString;
      int i = localArrayMap.size();
      arrayOfString = new String[i];
      for (int j = 0; j < i; j++) {
        arrayOfString[j] = ((String)localArrayMap.keyAt(j));
      }
    }
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public void setDefaultTransition(TransitionPort paramTransitionPort)
  {
    sDefaultTransition = paramTransitionPort;
  }
  
  public void setTransition(ScenePort paramScenePort1, ScenePort paramScenePort2, TransitionPort paramTransitionPort)
  {
    ArrayMap localArrayMap = (ArrayMap)this.mScenePairTransitions.get(paramScenePort2);
    if (localArrayMap == null)
    {
      localArrayMap = new ArrayMap();
      this.mScenePairTransitions.put(paramScenePort2, localArrayMap);
    }
    localArrayMap.put(paramScenePort1, paramTransitionPort);
  }
  
  public void setTransition(ScenePort paramScenePort, TransitionPort paramTransitionPort)
  {
    this.mSceneTransitions.put(paramScenePort, paramTransitionPort);
  }
  
  public void setTransition(ScenePort paramScenePort, String paramString, TransitionPort paramTransitionPort)
  {
    ArrayMap localArrayMap = (ArrayMap)this.mSceneNameTransitions.get(paramScenePort);
    if (localArrayMap == null)
    {
      localArrayMap = new ArrayMap();
      this.mSceneNameTransitions.put(paramScenePort, localArrayMap);
    }
    localArrayMap.put(paramString, paramTransitionPort);
  }
  
  public void setTransition(String paramString, ScenePort paramScenePort, TransitionPort paramTransitionPort)
  {
    ArrayMap localArrayMap = (ArrayMap)this.mNameSceneTransitions.get(paramString);
    if (localArrayMap == null)
    {
      localArrayMap = new ArrayMap();
      this.mNameSceneTransitions.put(paramString, localArrayMap);
    }
    localArrayMap.put(paramScenePort, paramTransitionPort);
  }
  
  public void transitionTo(ScenePort paramScenePort)
  {
    changeScene(paramScenePort, getTransition(paramScenePort));
  }
  
  private static class MultiListener
    implements ViewTreeObserver.OnPreDrawListener, View.OnAttachStateChangeListener
  {
    ViewGroup mSceneRoot;
    TransitionPort mTransition;
    
    MultiListener(TransitionPort paramTransitionPort, ViewGroup paramViewGroup)
    {
      this.mTransition = paramTransitionPort;
      this.mSceneRoot = paramViewGroup;
    }
    
    private void removeListeners()
    {
      this.mSceneRoot.getViewTreeObserver().removeOnPreDrawListener(this);
      this.mSceneRoot.removeOnAttachStateChangeListener(this);
    }
    
    public boolean onPreDraw()
    {
      removeListeners();
      TransitionManagerPort.sPendingTransitions.remove(this.mSceneRoot);
      final ArrayMap localArrayMap = TransitionManagerPort.getRunningTransitions();
      ArrayList localArrayList1 = (ArrayList)localArrayMap.get(this.mSceneRoot);
      ArrayList localArrayList2 = null;
      if (localArrayList1 == null)
      {
        localArrayList1 = new ArrayList();
        localArrayMap.put(this.mSceneRoot, localArrayList1);
      }
      for (;;)
      {
        localArrayList1.add(this.mTransition);
        this.mTransition.addListener(new TransitionPort.TransitionListenerAdapter()
        {
          public void onTransitionEnd(TransitionPort paramAnonymousTransitionPort)
          {
            ((ArrayList)localArrayMap.get(TransitionManagerPort.MultiListener.this.mSceneRoot)).remove(paramAnonymousTransitionPort);
          }
        });
        this.mTransition.captureValues(this.mSceneRoot, false);
        if (localArrayList2 == null) {
          break;
        }
        Iterator localIterator = localArrayList2.iterator();
        while (localIterator.hasNext()) {
          ((TransitionPort)localIterator.next()).resume(this.mSceneRoot);
        }
        int i = localArrayList1.size();
        localArrayList2 = null;
        if (i > 0) {
          localArrayList2 = new ArrayList(localArrayList1);
        }
      }
      this.mTransition.playTransition(this.mSceneRoot);
      return true;
    }
    
    public void onViewAttachedToWindow(View paramView) {}
    
    public void onViewDetachedFromWindow(View paramView)
    {
      removeListeners();
      TransitionManagerPort.sPendingTransitions.remove(this.mSceneRoot);
      ArrayList localArrayList = (ArrayList)TransitionManagerPort.getRunningTransitions().get(this.mSceneRoot);
      if ((localArrayList != null) && (localArrayList.size() > 0))
      {
        Iterator localIterator = localArrayList.iterator();
        while (localIterator.hasNext()) {
          ((TransitionPort)localIterator.next()).resume(this.mSceneRoot);
        }
      }
      this.mTransition.clearValues(true);
    }
  }
}
