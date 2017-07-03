package android.support.v7.widget;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

public abstract class SimpleItemAnimator
  extends RecyclerView.ItemAnimator
{
  private static final boolean DEBUG = false;
  private static final String TAG = "SimpleItemAnimator";
  boolean mSupportsChangeAnimations = true;
  
  public SimpleItemAnimator() {}
  
  public abstract boolean animateAdd(RecyclerView.ViewHolder paramViewHolder);
  
  public boolean animateAppearance(@NonNull RecyclerView.ViewHolder paramViewHolder, @Nullable RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo1, @NonNull RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo2)
  {
    if ((paramItemHolderInfo1 != null) && ((paramItemHolderInfo1.left != paramItemHolderInfo2.left) || (paramItemHolderInfo1.top != paramItemHolderInfo2.top))) {
      return animateMove(paramViewHolder, paramItemHolderInfo1.left, paramItemHolderInfo1.top, paramItemHolderInfo2.left, paramItemHolderInfo2.top);
    }
    return animateAdd(paramViewHolder);
  }
  
  public abstract boolean animateChange(RecyclerView.ViewHolder paramViewHolder1, RecyclerView.ViewHolder paramViewHolder2, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public boolean animateChange(@NonNull RecyclerView.ViewHolder paramViewHolder1, @NonNull RecyclerView.ViewHolder paramViewHolder2, @NonNull RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo1, @NonNull RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo2)
  {
    int i = paramItemHolderInfo1.left;
    int j = paramItemHolderInfo1.top;
    int k;
    if (paramViewHolder2.shouldIgnore()) {
      k = paramItemHolderInfo1.left;
    }
    for (int m = paramItemHolderInfo1.top;; m = paramItemHolderInfo2.top)
    {
      return animateChange(paramViewHolder1, paramViewHolder2, i, j, k, m);
      k = paramItemHolderInfo2.left;
    }
  }
  
  public boolean animateDisappearance(@NonNull RecyclerView.ViewHolder paramViewHolder, @NonNull RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo1, @Nullable RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo2)
  {
    int i = paramItemHolderInfo1.left;
    int j = paramItemHolderInfo1.top;
    View localView = paramViewHolder.itemView;
    int k;
    if (paramItemHolderInfo2 == null)
    {
      k = localView.getLeft();
      if (paramItemHolderInfo2 != null) {
        break label109;
      }
    }
    label109:
    for (int m = localView.getTop();; m = paramItemHolderInfo2.top)
    {
      if ((paramViewHolder.isRemoved()) || ((i == k) && (j == m))) {
        break label118;
      }
      localView.layout(k, m, k + localView.getWidth(), m + localView.getHeight());
      return animateMove(paramViewHolder, i, j, k, m);
      k = paramItemHolderInfo2.left;
      break;
    }
    label118:
    return animateRemove(paramViewHolder);
  }
  
  public abstract boolean animateMove(RecyclerView.ViewHolder paramViewHolder, int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public boolean animatePersistence(@NonNull RecyclerView.ViewHolder paramViewHolder, @NonNull RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo1, @NonNull RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo2)
  {
    if ((paramItemHolderInfo1.left != paramItemHolderInfo2.left) || (paramItemHolderInfo1.top != paramItemHolderInfo2.top)) {
      return animateMove(paramViewHolder, paramItemHolderInfo1.left, paramItemHolderInfo1.top, paramItemHolderInfo2.left, paramItemHolderInfo2.top);
    }
    dispatchMoveFinished(paramViewHolder);
    return false;
  }
  
  public abstract boolean animateRemove(RecyclerView.ViewHolder paramViewHolder);
  
  public boolean canReuseUpdatedViewHolder(@NonNull RecyclerView.ViewHolder paramViewHolder)
  {
    return (!this.mSupportsChangeAnimations) || (paramViewHolder.isInvalid());
  }
  
  public final void dispatchAddFinished(RecyclerView.ViewHolder paramViewHolder)
  {
    onAddFinished(paramViewHolder);
    dispatchAnimationFinished(paramViewHolder);
  }
  
  public final void dispatchAddStarting(RecyclerView.ViewHolder paramViewHolder)
  {
    onAddStarting(paramViewHolder);
  }
  
  public final void dispatchChangeFinished(RecyclerView.ViewHolder paramViewHolder, boolean paramBoolean)
  {
    onChangeFinished(paramViewHolder, paramBoolean);
    dispatchAnimationFinished(paramViewHolder);
  }
  
  public final void dispatchChangeStarting(RecyclerView.ViewHolder paramViewHolder, boolean paramBoolean)
  {
    onChangeStarting(paramViewHolder, paramBoolean);
  }
  
  public final void dispatchMoveFinished(RecyclerView.ViewHolder paramViewHolder)
  {
    onMoveFinished(paramViewHolder);
    dispatchAnimationFinished(paramViewHolder);
  }
  
  public final void dispatchMoveStarting(RecyclerView.ViewHolder paramViewHolder)
  {
    onMoveStarting(paramViewHolder);
  }
  
  public final void dispatchRemoveFinished(RecyclerView.ViewHolder paramViewHolder)
  {
    onRemoveFinished(paramViewHolder);
    dispatchAnimationFinished(paramViewHolder);
  }
  
  public final void dispatchRemoveStarting(RecyclerView.ViewHolder paramViewHolder)
  {
    onRemoveStarting(paramViewHolder);
  }
  
  public boolean getSupportsChangeAnimations()
  {
    return this.mSupportsChangeAnimations;
  }
  
  public void onAddFinished(RecyclerView.ViewHolder paramViewHolder) {}
  
  public void onAddStarting(RecyclerView.ViewHolder paramViewHolder) {}
  
  public void onChangeFinished(RecyclerView.ViewHolder paramViewHolder, boolean paramBoolean) {}
  
  public void onChangeStarting(RecyclerView.ViewHolder paramViewHolder, boolean paramBoolean) {}
  
  public void onMoveFinished(RecyclerView.ViewHolder paramViewHolder) {}
  
  public void onMoveStarting(RecyclerView.ViewHolder paramViewHolder) {}
  
  public void onRemoveFinished(RecyclerView.ViewHolder paramViewHolder) {}
  
  public void onRemoveStarting(RecyclerView.ViewHolder paramViewHolder) {}
  
  public void setSupportsChangeAnimations(boolean paramBoolean)
  {
    this.mSupportsChangeAnimations = paramBoolean;
  }
}
