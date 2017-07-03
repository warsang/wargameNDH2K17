package android.support.transition;

import android.content.Context;
import android.os.Build.VERSION;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

public class Scene
{
  private static SceneStaticsImpl sImpl = new SceneStaticsIcs();
  SceneImpl mImpl;
  
  static
  {
    if (Build.VERSION.SDK_INT >= 21)
    {
      sImpl = new SceneStaticsApi21();
      return;
    }
    if (Build.VERSION.SDK_INT >= 19)
    {
      sImpl = new SceneStaticsKitKat();
      return;
    }
  }
  
  private Scene(SceneImpl paramSceneImpl)
  {
    this.mImpl = paramSceneImpl;
  }
  
  public Scene(@NonNull ViewGroup paramViewGroup)
  {
    this.mImpl = createSceneImpl();
    this.mImpl.init(paramViewGroup);
  }
  
  public Scene(@NonNull ViewGroup paramViewGroup, @NonNull View paramView)
  {
    this.mImpl = createSceneImpl();
    this.mImpl.init(paramViewGroup, paramView);
  }
  
  private SceneImpl createSceneImpl()
  {
    if (Build.VERSION.SDK_INT >= 21) {
      return new SceneApi21();
    }
    if (Build.VERSION.SDK_INT >= 19) {
      return new SceneKitKat();
    }
    return new SceneIcs();
  }
  
  @NonNull
  public static Scene getSceneForLayout(@NonNull ViewGroup paramViewGroup, @LayoutRes int paramInt, @NonNull Context paramContext)
  {
    SparseArray localSparseArray = (SparseArray)paramViewGroup.getTag(R.id.transition_scene_layoutid_cache);
    if (localSparseArray == null)
    {
      localSparseArray = new SparseArray();
      paramViewGroup.setTag(R.id.transition_scene_layoutid_cache, localSparseArray);
    }
    Scene localScene1 = (Scene)localSparseArray.get(paramInt);
    if (localScene1 != null) {
      return localScene1;
    }
    Scene localScene2 = new Scene(sImpl.getSceneForLayout(paramViewGroup, paramInt, paramContext));
    localSparseArray.put(paramInt, localScene2);
    return localScene2;
  }
  
  public void enter()
  {
    this.mImpl.enter();
  }
  
  public void exit()
  {
    this.mImpl.exit();
  }
  
  @NonNull
  public ViewGroup getSceneRoot()
  {
    return this.mImpl.getSceneRoot();
  }
  
  public void setEnterAction(@Nullable Runnable paramRunnable)
  {
    this.mImpl.setEnterAction(paramRunnable);
  }
  
  public void setExitAction(@Nullable Runnable paramRunnable)
  {
    this.mImpl.setExitAction(paramRunnable);
  }
}
