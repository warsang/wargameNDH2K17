package android.support.transition;

import android.annotation.TargetApi;
import android.support.annotation.RequiresApi;
import android.transition.TransitionSet;

@TargetApi(19)
@RequiresApi(19)
class TransitionSetKitKat
  extends TransitionKitKat
  implements TransitionSetImpl
{
  private TransitionSet mTransitionSet = new TransitionSet();
  
  public TransitionSetKitKat(TransitionInterface paramTransitionInterface)
  {
    init(paramTransitionInterface, this.mTransitionSet);
  }
  
  public TransitionSetKitKat addTransition(TransitionImpl paramTransitionImpl)
  {
    this.mTransitionSet.addTransition(((TransitionKitKat)paramTransitionImpl).mTransition);
    return this;
  }
  
  public int getOrdering()
  {
    return this.mTransitionSet.getOrdering();
  }
  
  public TransitionSetKitKat removeTransition(TransitionImpl paramTransitionImpl)
  {
    this.mTransitionSet.removeTransition(((TransitionKitKat)paramTransitionImpl).mTransition);
    return this;
  }
  
  public TransitionSetKitKat setOrdering(int paramInt)
  {
    this.mTransitionSet.setOrdering(paramInt);
    return this;
  }
}
