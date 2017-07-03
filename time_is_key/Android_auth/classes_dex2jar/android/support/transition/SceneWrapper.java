package android.support.transition;

import android.annotation.TargetApi;
import android.support.annotation.RequiresApi;
import android.transition.Scene;
import android.view.ViewGroup;

@TargetApi(19)
@RequiresApi(19)
abstract class SceneWrapper
  extends SceneImpl
{
  Scene mScene;
  
  SceneWrapper() {}
  
  public void exit()
  {
    this.mScene.exit();
  }
  
  public ViewGroup getSceneRoot()
  {
    return this.mScene.getSceneRoot();
  }
  
  public void setEnterAction(Runnable paramRunnable)
  {
    this.mScene.setEnterAction(paramRunnable);
  }
  
  public void setExitAction(Runnable paramRunnable)
  {
    this.mScene.setExitAction(paramRunnable);
  }
}
