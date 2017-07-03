package android.support.transition;

import android.annotation.TargetApi;
import android.support.annotation.RequiresApi;
import android.view.ViewGroup;

@TargetApi(14)
@RequiresApi(14)
class TransitionManagerStaticsIcs
  extends TransitionManagerStaticsImpl
{
  TransitionManagerStaticsIcs() {}
  
  public void beginDelayedTransition(ViewGroup paramViewGroup)
  {
    TransitionManagerPort.beginDelayedTransition(paramViewGroup);
  }
  
  public void beginDelayedTransition(ViewGroup paramViewGroup, TransitionImpl paramTransitionImpl)
  {
    if (paramTransitionImpl == null) {}
    for (TransitionPort localTransitionPort = null;; localTransitionPort = ((TransitionIcs)paramTransitionImpl).mTransition)
    {
      TransitionManagerPort.beginDelayedTransition(paramViewGroup, localTransitionPort);
      return;
    }
  }
  
  public void go(SceneImpl paramSceneImpl)
  {
    TransitionManagerPort.go(((SceneIcs)paramSceneImpl).mScene);
  }
  
  public void go(SceneImpl paramSceneImpl, TransitionImpl paramTransitionImpl)
  {
    ScenePort localScenePort = ((SceneIcs)paramSceneImpl).mScene;
    if (paramTransitionImpl == null) {}
    for (TransitionPort localTransitionPort = null;; localTransitionPort = ((TransitionIcs)paramTransitionImpl).mTransition)
    {
      TransitionManagerPort.go(localScenePort, localTransitionPort);
      return;
    }
  }
}
