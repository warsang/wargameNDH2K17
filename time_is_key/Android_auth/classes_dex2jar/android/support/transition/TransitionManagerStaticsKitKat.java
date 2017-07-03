package android.support.transition;

import android.annotation.TargetApi;
import android.support.annotation.RequiresApi;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.ViewGroup;

@TargetApi(19)
@RequiresApi(19)
class TransitionManagerStaticsKitKat
  extends TransitionManagerStaticsImpl
{
  TransitionManagerStaticsKitKat() {}
  
  public void beginDelayedTransition(ViewGroup paramViewGroup)
  {
    TransitionManager.beginDelayedTransition(paramViewGroup);
  }
  
  public void beginDelayedTransition(ViewGroup paramViewGroup, TransitionImpl paramTransitionImpl)
  {
    if (paramTransitionImpl == null) {}
    for (Transition localTransition = null;; localTransition = ((TransitionKitKat)paramTransitionImpl).mTransition)
    {
      TransitionManager.beginDelayedTransition(paramViewGroup, localTransition);
      return;
    }
  }
  
  public void go(SceneImpl paramSceneImpl)
  {
    TransitionManager.go(((SceneWrapper)paramSceneImpl).mScene);
  }
  
  public void go(SceneImpl paramSceneImpl, TransitionImpl paramTransitionImpl)
  {
    Scene localScene = ((SceneWrapper)paramSceneImpl).mScene;
    if (paramTransitionImpl == null) {}
    for (Transition localTransition = null;; localTransition = ((TransitionKitKat)paramTransitionImpl).mTransition)
    {
      TransitionManager.go(localScene, localTransition);
      return;
    }
  }
}
