package android.support.v7.widget.helper;

import android.graphics.Canvas;
import android.support.v4.view.ViewCompat;
import android.support.v7.recyclerview.R.id;
import android.support.v7.widget.RecyclerView;
import android.view.View;

class ItemTouchUIUtilImpl
{
  ItemTouchUIUtilImpl() {}
  
  static class Gingerbread
    implements ItemTouchUIUtil
  {
    Gingerbread() {}
    
    private void draw(Canvas paramCanvas, RecyclerView paramRecyclerView, View paramView, float paramFloat1, float paramFloat2)
    {
      paramCanvas.save();
      paramCanvas.translate(paramFloat1, paramFloat2);
      paramRecyclerView.drawChild(paramCanvas, paramView, 0L);
      paramCanvas.restore();
    }
    
    public void clearView(View paramView)
    {
      paramView.setVisibility(0);
    }
    
    public void onDraw(Canvas paramCanvas, RecyclerView paramRecyclerView, View paramView, float paramFloat1, float paramFloat2, int paramInt, boolean paramBoolean)
    {
      if (paramInt != 2) {
        draw(paramCanvas, paramRecyclerView, paramView, paramFloat1, paramFloat2);
      }
    }
    
    public void onDrawOver(Canvas paramCanvas, RecyclerView paramRecyclerView, View paramView, float paramFloat1, float paramFloat2, int paramInt, boolean paramBoolean)
    {
      if (paramInt == 2) {
        draw(paramCanvas, paramRecyclerView, paramView, paramFloat1, paramFloat2);
      }
    }
    
    public void onSelected(View paramView)
    {
      paramView.setVisibility(4);
    }
  }
  
  static class Honeycomb
    implements ItemTouchUIUtil
  {
    Honeycomb() {}
    
    public void clearView(View paramView)
    {
      ViewCompat.setTranslationX(paramView, 0.0F);
      ViewCompat.setTranslationY(paramView, 0.0F);
    }
    
    public void onDraw(Canvas paramCanvas, RecyclerView paramRecyclerView, View paramView, float paramFloat1, float paramFloat2, int paramInt, boolean paramBoolean)
    {
      ViewCompat.setTranslationX(paramView, paramFloat1);
      ViewCompat.setTranslationY(paramView, paramFloat2);
    }
    
    public void onDrawOver(Canvas paramCanvas, RecyclerView paramRecyclerView, View paramView, float paramFloat1, float paramFloat2, int paramInt, boolean paramBoolean) {}
    
    public void onSelected(View paramView) {}
  }
  
  static class Lollipop
    extends ItemTouchUIUtilImpl.Honeycomb
  {
    Lollipop() {}
    
    private float findMaxElevation(RecyclerView paramRecyclerView, View paramView)
    {
      int i = paramRecyclerView.getChildCount();
      float f1 = 0.0F;
      int j = 0;
      if (j < i)
      {
        View localView = paramRecyclerView.getChildAt(j);
        if (localView == paramView) {}
        for (;;)
        {
          j++;
          break;
          float f2 = ViewCompat.getElevation(localView);
          if (f2 > f1) {
            f1 = f2;
          }
        }
      }
      return f1;
    }
    
    public void clearView(View paramView)
    {
      Object localObject = paramView.getTag(R.id.item_touch_helper_previous_elevation);
      if ((localObject != null) && ((localObject instanceof Float))) {
        ViewCompat.setElevation(paramView, ((Float)localObject).floatValue());
      }
      paramView.setTag(R.id.item_touch_helper_previous_elevation, null);
      super.clearView(paramView);
    }
    
    public void onDraw(Canvas paramCanvas, RecyclerView paramRecyclerView, View paramView, float paramFloat1, float paramFloat2, int paramInt, boolean paramBoolean)
    {
      if ((paramBoolean) && (paramView.getTag(R.id.item_touch_helper_previous_elevation) == null))
      {
        Float localFloat = Float.valueOf(ViewCompat.getElevation(paramView));
        ViewCompat.setElevation(paramView, 1.0F + findMaxElevation(paramRecyclerView, paramView));
        paramView.setTag(R.id.item_touch_helper_previous_elevation, localFloat);
      }
      super.onDraw(paramCanvas, paramRecyclerView, paramView, paramFloat1, paramFloat2, paramInt, paramBoolean);
    }
  }
}
