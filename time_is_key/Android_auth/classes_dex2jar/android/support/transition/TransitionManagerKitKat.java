package android.support.transition;

import android.annotation.TargetApi;
import android.support.annotation.RequiresApi;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionManager;

@TargetApi(19)
@RequiresApi(19)
class TransitionManagerKitKat
  extends TransitionManagerImpl
{
  private final TransitionManager mTransitionManager = new TransitionManager();
  
  TransitionManagerKitKat() {}
  
  public void setTransition(SceneImpl paramSceneImpl1, SceneImpl paramSceneImpl2, TransitionImpl paramTransitionImpl)
  {
    TransitionManager localTransitionManager = this.mTransitionManager;
    Scene localScene1 = ((SceneWrapper)paramSceneImpl1).mScene;
    Scene localScene2 = ((SceneWrapper)paramSceneImpl2).mScene;
    if (paramTransitionImpl == null) {}
    for (Transition localTransition = null;; localTransition = ((TransitionKitKat)paramTransitionImpl).mTransition)
    {
      localTransitionManager.setTransition(localScene1, localScene2, localTransition);
      return;
    }
  }
  
  public void setTransition(SceneImpl paramSceneImpl, TransitionImpl paramTransitionImpl)
  {
    TransitionManager localTransitionManager = this.mTransitionManager;
    Scene localScene = ((SceneWrapper)paramSceneImpl).mScene;
    if (paramTransitionImpl == null) {}
    for (Transition localTransition = null;; localTransition = ((TransitionKitKat)paramTransitionImpl).mTransition)
    {
      localTransitionManager.setTransition(localScene, localTransition);
      return;
    }
  }
  
  public void transitionTo(SceneImpl paramSceneImpl)
  {
    this.mTransitionManager.transitionTo(((SceneWrapper)paramSceneImpl).mScene);
  }
}
