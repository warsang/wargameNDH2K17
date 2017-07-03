package android.support.transition;

import android.annotation.TargetApi;
import android.support.annotation.RequiresApi;

@TargetApi(14)
@RequiresApi(14)
class TransitionManagerIcs
  extends TransitionManagerImpl
{
  private final TransitionManagerPort mTransitionManager = new TransitionManagerPort();
  
  TransitionManagerIcs() {}
  
  public void setTransition(SceneImpl paramSceneImpl1, SceneImpl paramSceneImpl2, TransitionImpl paramTransitionImpl)
  {
    TransitionManagerPort localTransitionManagerPort = this.mTransitionManager;
    ScenePort localScenePort1 = ((SceneIcs)paramSceneImpl1).mScene;
    ScenePort localScenePort2 = ((SceneIcs)paramSceneImpl2).mScene;
    if (paramTransitionImpl == null) {}
    for (TransitionPort localTransitionPort = null;; localTransitionPort = ((TransitionIcs)paramTransitionImpl).mTransition)
    {
      localTransitionManagerPort.setTransition(localScenePort1, localScenePort2, localTransitionPort);
      return;
    }
  }
  
  public void setTransition(SceneImpl paramSceneImpl, TransitionImpl paramTransitionImpl)
  {
    TransitionManagerPort localTransitionManagerPort = this.mTransitionManager;
    ScenePort localScenePort = ((SceneIcs)paramSceneImpl).mScene;
    if (paramTransitionImpl == null) {}
    for (TransitionPort localTransitionPort = null;; localTransitionPort = ((TransitionIcs)paramTransitionImpl).mTransition)
    {
      localTransitionManagerPort.setTransition(localScenePort, localTransitionPort);
      return;
    }
  }
  
  public void transitionTo(SceneImpl paramSceneImpl)
  {
    this.mTransitionManager.transitionTo(((SceneIcs)paramSceneImpl).mScene);
  }
}
