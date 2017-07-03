package android.support.transition;

import android.os.Build.VERSION;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

public class TransitionManager
{
  private static TransitionManagerStaticsImpl sImpl = new TransitionManagerStaticsKitKat();
  private TransitionManagerImpl mImpl;
  
  static
  {
    if (Build.VERSION.SDK_INT < 19)
    {
      sImpl = new TransitionManagerStaticsIcs();
      return;
    }
  }
  
  public TransitionManager()
  {
    if (Build.VERSION.SDK_INT < 19)
    {
      this.mImpl = new TransitionManagerIcs();
      return;
    }
    this.mImpl = new TransitionManagerKitKat();
  }
  
  public static void beginDelayedTransition(@NonNull ViewGroup paramViewGroup)
  {
    sImpl.beginDelayedTransition(paramViewGroup);
  }
  
  public static void beginDelayedTransition(@NonNull ViewGroup paramViewGroup, @Nullable Transition paramTransition)
  {
    TransitionManagerStaticsImpl localTransitionManagerStaticsImpl = sImpl;
    if (paramTransition == null) {}
    for (TransitionImpl localTransitionImpl = null;; localTransitionImpl = paramTransition.mImpl)
    {
      localTransitionManagerStaticsImpl.beginDelayedTransition(paramViewGroup, localTransitionImpl);
      return;
    }
  }
  
  public static void go(@NonNull Scene paramScene)
  {
    sImpl.go(paramScene.mImpl);
  }
  
  public static void go(@NonNull Scene paramScene, @Nullable Transition paramTransition)
  {
    TransitionManagerStaticsImpl localTransitionManagerStaticsImpl = sImpl;
    SceneImpl localSceneImpl = paramScene.mImpl;
    if (paramTransition == null) {}
    for (TransitionImpl localTransitionImpl = null;; localTransitionImpl = paramTransition.mImpl)
    {
      localTransitionManagerStaticsImpl.go(localSceneImpl, localTransitionImpl);
      return;
    }
  }
  
  public void setTransition(@NonNull Scene paramScene1, @NonNull Scene paramScene2, @Nullable Transition paramTransition)
  {
    TransitionManagerImpl localTransitionManagerImpl = this.mImpl;
    SceneImpl localSceneImpl1 = paramScene1.mImpl;
    SceneImpl localSceneImpl2 = paramScene2.mImpl;
    if (paramTransition == null) {}
    for (TransitionImpl localTransitionImpl = null;; localTransitionImpl = paramTransition.mImpl)
    {
      localTransitionManagerImpl.setTransition(localSceneImpl1, localSceneImpl2, localTransitionImpl);
      return;
    }
  }
  
  public void setTransition(@NonNull Scene paramScene, @Nullable Transition paramTransition)
  {
    TransitionManagerImpl localTransitionManagerImpl = this.mImpl;
    SceneImpl localSceneImpl = paramScene.mImpl;
    if (paramTransition == null) {}
    for (TransitionImpl localTransitionImpl = null;; localTransitionImpl = paramTransition.mImpl)
    {
      localTransitionManagerImpl.setTransition(localSceneImpl, localTransitionImpl);
      return;
    }
  }
  
  public void transitionTo(@NonNull Scene paramScene)
  {
    this.mImpl.transitionTo(paramScene.mImpl);
  }
}
