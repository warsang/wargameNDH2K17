package android.support.transition;

import android.annotation.TargetApi;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewGroup;

@TargetApi(14)
@RequiresApi(14)
class SceneIcs
  extends SceneImpl
{
  ScenePort mScene;
  
  SceneIcs() {}
  
  public void enter()
  {
    this.mScene.enter();
  }
  
  public void exit()
  {
    this.mScene.exit();
  }
  
  public ViewGroup getSceneRoot()
  {
    return this.mScene.getSceneRoot();
  }
  
  public void init(ViewGroup paramViewGroup)
  {
    this.mScene = new ScenePort(paramViewGroup);
  }
  
  public void init(ViewGroup paramViewGroup, View paramView)
  {
    this.mScene = new ScenePort(paramViewGroup, paramView);
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
