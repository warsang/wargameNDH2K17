package android.support.design.widget;

import android.annotation.TargetApi;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

@TargetApi(11)
@RequiresApi(11)
class ViewGroupUtilsHoneycomb
{
  private static final ThreadLocal<Matrix> sMatrix = new ThreadLocal();
  private static final ThreadLocal<RectF> sRectF = new ThreadLocal();
  
  ViewGroupUtilsHoneycomb() {}
  
  static void offsetDescendantMatrix(ViewParent paramViewParent, View paramView, Matrix paramMatrix)
  {
    ViewParent localViewParent = paramView.getParent();
    if (((localViewParent instanceof View)) && (localViewParent != paramViewParent))
    {
      View localView = (View)localViewParent;
      offsetDescendantMatrix(paramViewParent, localView, paramMatrix);
      paramMatrix.preTranslate(-localView.getScrollX(), -localView.getScrollY());
    }
    paramMatrix.preTranslate(paramView.getLeft(), paramView.getTop());
    if (!paramView.getMatrix().isIdentity()) {
      paramMatrix.preConcat(paramView.getMatrix());
    }
  }
  
  public static void offsetDescendantRect(ViewGroup paramViewGroup, View paramView, Rect paramRect)
  {
    Matrix localMatrix = (Matrix)sMatrix.get();
    if (localMatrix == null)
    {
      localMatrix = new Matrix();
      sMatrix.set(localMatrix);
    }
    for (;;)
    {
      offsetDescendantMatrix(paramViewGroup, paramView, localMatrix);
      RectF localRectF = (RectF)sRectF.get();
      if (localRectF == null)
      {
        localRectF = new RectF();
        sRectF.set(localRectF);
      }
      localRectF.set(paramRect);
      localMatrix.mapRect(localRectF);
      paramRect.set((int)(0.5F + localRectF.left), (int)(0.5F + localRectF.top), (int)(0.5F + localRectF.right), (int)(0.5F + localRectF.bottom));
      return;
      localMatrix.reset();
    }
  }
}
