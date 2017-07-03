package android.support.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewGroup;
import java.util.Map;

@TargetApi(14)
@RequiresApi(14)
class ChangeBoundsPort
  extends TransitionPort
{
  private static final String LOG_TAG = "ChangeBounds";
  private static final String PROPNAME_BOUNDS = "android:changeBounds:bounds";
  private static final String PROPNAME_PARENT = "android:changeBounds:parent";
  private static final String PROPNAME_WINDOW_X = "android:changeBounds:windowX";
  private static final String PROPNAME_WINDOW_Y = "android:changeBounds:windowY";
  private static RectEvaluator sRectEvaluator = new RectEvaluator();
  private static final String[] sTransitionProperties = { "android:changeBounds:bounds", "android:changeBounds:parent", "android:changeBounds:windowX", "android:changeBounds:windowY" };
  boolean mReparent = false;
  boolean mResizeClip = false;
  int[] tempLocation = new int[2];
  
  ChangeBoundsPort() {}
  
  private void captureValues(TransitionValues paramTransitionValues)
  {
    View localView = paramTransitionValues.view;
    paramTransitionValues.values.put("android:changeBounds:bounds", new Rect(localView.getLeft(), localView.getTop(), localView.getRight(), localView.getBottom()));
    paramTransitionValues.values.put("android:changeBounds:parent", paramTransitionValues.view.getParent());
    paramTransitionValues.view.getLocationInWindow(this.tempLocation);
    paramTransitionValues.values.put("android:changeBounds:windowX", Integer.valueOf(this.tempLocation[0]));
    paramTransitionValues.values.put("android:changeBounds:windowY", Integer.valueOf(this.tempLocation[1]));
  }
  
  public void captureEndValues(TransitionValues paramTransitionValues)
  {
    captureValues(paramTransitionValues);
  }
  
  public void captureStartValues(TransitionValues paramTransitionValues)
  {
    captureValues(paramTransitionValues);
  }
  
  public Animator createAnimator(final ViewGroup paramViewGroup, TransitionValues paramTransitionValues1, TransitionValues paramTransitionValues2)
  {
    Object localObject;
    if ((paramTransitionValues1 == null) || (paramTransitionValues2 == null))
    {
      localObject = null;
      return localObject;
    }
    Map localMap1 = paramTransitionValues1.values;
    Map localMap2 = paramTransitionValues2.values;
    ViewGroup localViewGroup1 = (ViewGroup)localMap1.get("android:changeBounds:parent");
    ViewGroup localViewGroup2 = (ViewGroup)localMap2.get("android:changeBounds:parent");
    if ((localViewGroup1 == null) || (localViewGroup2 == null)) {
      return null;
    }
    final View localView = paramTransitionValues2.view;
    int i;
    label95:
    int j;
    int k;
    int m;
    int n;
    int i1;
    int i2;
    int i3;
    int i4;
    int i5;
    int i6;
    int i7;
    int i8;
    PropertyValuesHolder[] arrayOfPropertyValuesHolder2;
    int i14;
    if ((localViewGroup1 == localViewGroup2) || (localViewGroup1.getId() == localViewGroup2.getId()))
    {
      i = 1;
      if ((this.mReparent) && (i == 0)) {
        break label923;
      }
      Rect localRect1 = (Rect)paramTransitionValues1.values.get("android:changeBounds:bounds");
      Rect localRect2 = (Rect)paramTransitionValues2.values.get("android:changeBounds:bounds");
      j = localRect1.left;
      k = localRect2.left;
      m = localRect1.top;
      n = localRect2.top;
      i1 = localRect1.right;
      i2 = localRect2.right;
      i3 = localRect1.bottom;
      i4 = localRect2.bottom;
      i5 = i1 - j;
      i6 = i3 - m;
      i7 = i2 - k;
      i8 = i4 - n;
      int i9 = 0;
      if (i5 != 0)
      {
        i9 = 0;
        if (i6 != 0)
        {
          i9 = 0;
          if (i7 != 0)
          {
            i9 = 0;
            if (i8 != 0)
            {
              i9 = 0;
              if (j != k) {
                i9 = 0 + 1;
              }
              if (m != n) {
                i9++;
              }
              if (i1 != i2) {
                i9++;
              }
              if (i3 != i4) {
                i9++;
              }
            }
          }
        }
      }
      if (i9 <= 0) {
        break label1245;
      }
      if (this.mResizeClip) {
        break label579;
      }
      arrayOfPropertyValuesHolder2 = new PropertyValuesHolder[i9];
      if (j != k) {
        localView.setLeft(j);
      }
      if (m != n) {
        localView.setTop(m);
      }
      if (i1 != i2) {
        localView.setRight(i1);
      }
      if (i3 != i4) {
        localView.setBottom(i3);
      }
      if (j == k) {
        break label1265;
      }
      i14 = 0 + 1;
      arrayOfPropertyValuesHolder2[0] = PropertyValuesHolder.ofInt("left", new int[] { j, k });
    }
    for (;;)
    {
      if (m != n)
      {
        int i16 = i14 + 1;
        arrayOfPropertyValuesHolder2[i14] = PropertyValuesHolder.ofInt("top", new int[] { m, n });
        i14 = i16;
      }
      if (i1 != i2)
      {
        int i15 = i14 + 1;
        arrayOfPropertyValuesHolder2[i14] = PropertyValuesHolder.ofInt("right", new int[] { i1, i2 });
        i14 = i15;
      }
      if (i3 != i4)
      {
        (i14 + 1);
        arrayOfPropertyValuesHolder2[i14] = PropertyValuesHolder.ofInt("bottom", new int[] { i3, i4 });
      }
      for (;;)
      {
        localObject = ObjectAnimator.ofPropertyValuesHolder(localView, arrayOfPropertyValuesHolder2);
        if (!(localView.getParent() instanceof ViewGroup)) {
          break;
        }
        ((ViewGroup)localView.getParent());
        TransitionPort.TransitionListenerAdapter local1 = new TransitionPort.TransitionListenerAdapter()
        {
          boolean mCanceled = false;
          
          public void onTransitionCancel(TransitionPort paramAnonymousTransitionPort)
          {
            this.mCanceled = true;
          }
          
          public void onTransitionEnd(TransitionPort paramAnonymousTransitionPort)
          {
            if (!this.mCanceled) {}
          }
          
          public void onTransitionPause(TransitionPort paramAnonymousTransitionPort) {}
          
          public void onTransitionResume(TransitionPort paramAnonymousTransitionPort) {}
        };
        addListener(local1);
        return localObject;
        i = 0;
        break label95;
        label579:
        if (i5 != i7) {
          localView.setRight(k + Math.max(i5, i7));
        }
        if (i6 != i8) {
          localView.setBottom(n + Math.max(i6, i8));
        }
        if (j != k) {
          localView.setTranslationX(j - k);
        }
        if (m != n) {
          localView.setTranslationY(m - n);
        }
        float f1 = k - j;
        float f2 = n - m;
        int i10 = i7 - i5;
        int i11 = i8 - i6;
        boolean bool = f1 < 0.0F;
        int i12 = 0;
        if (bool) {
          i12 = 0 + 1;
        }
        if (f2 != 0.0F) {
          i12++;
        }
        if ((i10 != 0) || (i11 != 0)) {
          i12++;
        }
        PropertyValuesHolder[] arrayOfPropertyValuesHolder1 = new PropertyValuesHolder[i12];
        int i13;
        if (f1 != 0.0F)
        {
          i13 = 0 + 1;
          float[] arrayOfFloat2 = new float[2];
          arrayOfFloat2[0] = localView.getTranslationX();
          arrayOfFloat2[1] = 0.0F;
          arrayOfPropertyValuesHolder1[0] = PropertyValuesHolder.ofFloat("translationX", arrayOfFloat2);
        }
        for (;;)
        {
          if (f2 != 0.0F)
          {
            (i13 + 1);
            float[] arrayOfFloat1 = new float[2];
            arrayOfFloat1[0] = localView.getTranslationY();
            arrayOfFloat1[1] = 0.0F;
            arrayOfPropertyValuesHolder1[i13] = PropertyValuesHolder.ofFloat("translationY", arrayOfFloat1);
          }
          for (;;)
          {
            if ((i10 != 0) || (i11 != 0))
            {
              new Rect(0, 0, i5, i6);
              new Rect(0, 0, i7, i8);
            }
            ObjectAnimator localObjectAnimator1 = ObjectAnimator.ofPropertyValuesHolder(localView, arrayOfPropertyValuesHolder1);
            if ((localView.getParent() instanceof ViewGroup))
            {
              ((ViewGroup)localView.getParent());
              TransitionPort.TransitionListenerAdapter local2 = new TransitionPort.TransitionListenerAdapter()
              {
                boolean mCanceled = false;
                
                public void onTransitionCancel(TransitionPort paramAnonymousTransitionPort)
                {
                  this.mCanceled = true;
                }
                
                public void onTransitionEnd(TransitionPort paramAnonymousTransitionPort)
                {
                  if (!this.mCanceled) {}
                }
                
                public void onTransitionPause(TransitionPort paramAnonymousTransitionPort) {}
                
                public void onTransitionResume(TransitionPort paramAnonymousTransitionPort) {}
              };
              addListener(local2);
            }
            AnimatorListenerAdapter local3 = new AnimatorListenerAdapter()
            {
              public void onAnimationEnd(Animator paramAnonymousAnimator) {}
            };
            localObjectAnimator1.addListener(local3);
            return localObjectAnimator1;
            label923:
            int i17 = ((Integer)paramTransitionValues1.values.get("android:changeBounds:windowX")).intValue();
            int i18 = ((Integer)paramTransitionValues1.values.get("android:changeBounds:windowY")).intValue();
            int i19 = ((Integer)paramTransitionValues2.values.get("android:changeBounds:windowX")).intValue();
            int i20 = ((Integer)paramTransitionValues2.values.get("android:changeBounds:windowY")).intValue();
            if ((i17 != i19) || (i18 != i20))
            {
              paramViewGroup.getLocationInWindow(this.tempLocation);
              Bitmap localBitmap = Bitmap.createBitmap(localView.getWidth(), localView.getHeight(), Bitmap.Config.ARGB_8888);
              localView.draw(new Canvas(localBitmap));
              final BitmapDrawable localBitmapDrawable = new BitmapDrawable(localBitmap);
              localView.setVisibility(4);
              ViewOverlay.createFrom(paramViewGroup).add(localBitmapDrawable);
              Rect localRect3 = new Rect(i17 - this.tempLocation[0], i18 - this.tempLocation[1], i17 - this.tempLocation[0] + localView.getWidth(), i18 - this.tempLocation[1] + localView.getHeight());
              Rect localRect4 = new Rect(i19 - this.tempLocation[0], i20 - this.tempLocation[1], i19 - this.tempLocation[0] + localView.getWidth(), i20 - this.tempLocation[1] + localView.getHeight());
              ObjectAnimator localObjectAnimator2 = ObjectAnimator.ofObject(localBitmapDrawable, "bounds", sRectEvaluator, new Object[] { localRect3, localRect4 });
              AnimatorListenerAdapter local4 = new AnimatorListenerAdapter()
              {
                public void onAnimationEnd(Animator paramAnonymousAnimator)
                {
                  ViewOverlay.createFrom(paramViewGroup).remove(localBitmapDrawable);
                  localView.setVisibility(0);
                }
              };
              localObjectAnimator2.addListener(local4);
              return localObjectAnimator2;
            }
            label1245:
            return null;
          }
          i13 = 0;
        }
      }
      label1265:
      i14 = 0;
    }
  }
  
  public String[] getTransitionProperties()
  {
    return sTransitionProperties;
  }
  
  public void setReparent(boolean paramBoolean)
  {
    this.mReparent = paramBoolean;
  }
  
  public void setResizeClip(boolean paramBoolean)
  {
    this.mResizeClip = paramBoolean;
  }
}
