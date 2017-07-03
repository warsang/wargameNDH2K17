package android.support.design.widget;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.R.anim;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.animation.Animation;

class FloatingActionButtonGingerbread
  extends FloatingActionButtonImpl
{
  ShadowDrawableWrapper mShadowDrawable;
  private final StateListAnimator mStateListAnimator = new StateListAnimator();
  
  FloatingActionButtonGingerbread(VisibilityAwareImageButton paramVisibilityAwareImageButton, ShadowViewDelegate paramShadowViewDelegate, ValueAnimatorCompat.Creator paramCreator)
  {
    super(paramVisibilityAwareImageButton, paramShadowViewDelegate, paramCreator);
    this.mStateListAnimator.addState(PRESSED_ENABLED_STATE_SET, createAnimator(new ElevateToTranslationZAnimation()));
    this.mStateListAnimator.addState(FOCUSED_ENABLED_STATE_SET, createAnimator(new ElevateToTranslationZAnimation()));
    this.mStateListAnimator.addState(ENABLED_STATE_SET, createAnimator(new ResetElevationAnimation()));
    this.mStateListAnimator.addState(EMPTY_STATE_SET, createAnimator(new DisabledElevationAnimation()));
  }
  
  private ValueAnimatorCompat createAnimator(@NonNull ShadowAnimatorImpl paramShadowAnimatorImpl)
  {
    ValueAnimatorCompat localValueAnimatorCompat = this.mAnimatorCreator.createAnimator();
    localValueAnimatorCompat.setInterpolator(ANIM_INTERPOLATOR);
    localValueAnimatorCompat.setDuration(100L);
    localValueAnimatorCompat.addListener(paramShadowAnimatorImpl);
    localValueAnimatorCompat.addUpdateListener(paramShadowAnimatorImpl);
    localValueAnimatorCompat.setFloatValues(0.0F, 1.0F);
    return localValueAnimatorCompat;
  }
  
  private static ColorStateList createColorStateList(int paramInt)
  {
    int[][] arrayOfInt = new int[3][];
    int[] arrayOfInt1 = new int[3];
    arrayOfInt[0] = FOCUSED_ENABLED_STATE_SET;
    arrayOfInt1[0] = paramInt;
    int i = 0 + 1;
    arrayOfInt[i] = PRESSED_ENABLED_STATE_SET;
    arrayOfInt1[i] = paramInt;
    int j = i + 1;
    arrayOfInt[j] = new int[0];
    arrayOfInt1[j] = 0;
    (j + 1);
    return new ColorStateList(arrayOfInt, arrayOfInt1);
  }
  
  float getElevation()
  {
    return this.mElevation;
  }
  
  void getPadding(Rect paramRect)
  {
    this.mShadowDrawable.getPadding(paramRect);
  }
  
  void hide(@Nullable final FloatingActionButtonImpl.InternalVisibilityChangedListener paramInternalVisibilityChangedListener, final boolean paramBoolean)
  {
    if (isOrWillBeHidden()) {
      return;
    }
    this.mAnimState = 1;
    Animation localAnimation = android.view.animation.AnimationUtils.loadAnimation(this.mView.getContext(), R.anim.design_fab_out);
    localAnimation.setInterpolator(AnimationUtils.FAST_OUT_LINEAR_IN_INTERPOLATOR);
    localAnimation.setDuration(200L);
    localAnimation.setAnimationListener(new AnimationUtils.AnimationListenerAdapter()
    {
      public void onAnimationEnd(Animation paramAnonymousAnimation)
      {
        FloatingActionButtonGingerbread.this.mAnimState = 0;
        VisibilityAwareImageButton localVisibilityAwareImageButton = FloatingActionButtonGingerbread.this.mView;
        if (paramBoolean) {}
        for (int i = 8;; i = 4)
        {
          localVisibilityAwareImageButton.internalSetVisibility(i, paramBoolean);
          if (paramInternalVisibilityChangedListener != null) {
            paramInternalVisibilityChangedListener.onHidden();
          }
          return;
        }
      }
    });
    this.mView.startAnimation(localAnimation);
  }
  
  void jumpDrawableToCurrentState()
  {
    this.mStateListAnimator.jumpToCurrentState();
  }
  
  void onCompatShadowChanged() {}
  
  void onDrawableStateChanged(int[] paramArrayOfInt)
  {
    this.mStateListAnimator.setState(paramArrayOfInt);
  }
  
  void onElevationsChanged(float paramFloat1, float paramFloat2)
  {
    if (this.mShadowDrawable != null)
    {
      this.mShadowDrawable.setShadowSize(paramFloat1, paramFloat1 + this.mPressedTranslationZ);
      updatePadding();
    }
  }
  
  void setBackgroundDrawable(ColorStateList paramColorStateList, PorterDuff.Mode paramMode, int paramInt1, int paramInt2)
  {
    this.mShapeDrawable = DrawableCompat.wrap(createShapeDrawable());
    DrawableCompat.setTintList(this.mShapeDrawable, paramColorStateList);
    if (paramMode != null) {
      DrawableCompat.setTintMode(this.mShapeDrawable, paramMode);
    }
    this.mRippleDrawable = DrawableCompat.wrap(createShapeDrawable());
    DrawableCompat.setTintList(this.mRippleDrawable, createColorStateList(paramInt1));
    Drawable[] arrayOfDrawable;
    if (paramInt2 > 0)
    {
      this.mBorderDrawable = createBorderDrawable(paramInt2, paramColorStateList);
      arrayOfDrawable = new Drawable[3];
      arrayOfDrawable[0] = this.mBorderDrawable;
      arrayOfDrawable[1] = this.mShapeDrawable;
      arrayOfDrawable[2] = this.mRippleDrawable;
    }
    for (;;)
    {
      this.mContentBackground = new LayerDrawable(arrayOfDrawable);
      this.mShadowDrawable = new ShadowDrawableWrapper(this.mView.getContext(), this.mContentBackground, this.mShadowViewDelegate.getRadius(), this.mElevation, this.mElevation + this.mPressedTranslationZ);
      this.mShadowDrawable.setAddPaddingForCorners(false);
      this.mShadowViewDelegate.setBackgroundDrawable(this.mShadowDrawable);
      return;
      this.mBorderDrawable = null;
      arrayOfDrawable = new Drawable[2];
      arrayOfDrawable[0] = this.mShapeDrawable;
      arrayOfDrawable[1] = this.mRippleDrawable;
    }
  }
  
  void setBackgroundTintList(ColorStateList paramColorStateList)
  {
    if (this.mShapeDrawable != null) {
      DrawableCompat.setTintList(this.mShapeDrawable, paramColorStateList);
    }
    if (this.mBorderDrawable != null) {
      this.mBorderDrawable.setBorderTint(paramColorStateList);
    }
  }
  
  void setBackgroundTintMode(PorterDuff.Mode paramMode)
  {
    if (this.mShapeDrawable != null) {
      DrawableCompat.setTintMode(this.mShapeDrawable, paramMode);
    }
  }
  
  void setRippleColor(int paramInt)
  {
    if (this.mRippleDrawable != null) {
      DrawableCompat.setTintList(this.mRippleDrawable, createColorStateList(paramInt));
    }
  }
  
  void show(@Nullable final FloatingActionButtonImpl.InternalVisibilityChangedListener paramInternalVisibilityChangedListener, boolean paramBoolean)
  {
    if (isOrWillBeShown()) {
      return;
    }
    this.mAnimState = 2;
    this.mView.internalSetVisibility(0, paramBoolean);
    Animation localAnimation = android.view.animation.AnimationUtils.loadAnimation(this.mView.getContext(), R.anim.design_fab_in);
    localAnimation.setDuration(200L);
    localAnimation.setInterpolator(AnimationUtils.LINEAR_OUT_SLOW_IN_INTERPOLATOR);
    localAnimation.setAnimationListener(new AnimationUtils.AnimationListenerAdapter()
    {
      public void onAnimationEnd(Animation paramAnonymousAnimation)
      {
        FloatingActionButtonGingerbread.this.mAnimState = 0;
        if (paramInternalVisibilityChangedListener != null) {
          paramInternalVisibilityChangedListener.onShown();
        }
      }
    });
    this.mView.startAnimation(localAnimation);
  }
  
  private class DisabledElevationAnimation
    extends FloatingActionButtonGingerbread.ShadowAnimatorImpl
  {
    DisabledElevationAnimation()
    {
      super(null);
    }
    
    protected float getTargetShadowSize()
    {
      return 0.0F;
    }
  }
  
  private class ElevateToTranslationZAnimation
    extends FloatingActionButtonGingerbread.ShadowAnimatorImpl
  {
    ElevateToTranslationZAnimation()
    {
      super(null);
    }
    
    protected float getTargetShadowSize()
    {
      return FloatingActionButtonGingerbread.this.mElevation + FloatingActionButtonGingerbread.this.mPressedTranslationZ;
    }
  }
  
  private class ResetElevationAnimation
    extends FloatingActionButtonGingerbread.ShadowAnimatorImpl
  {
    ResetElevationAnimation()
    {
      super(null);
    }
    
    protected float getTargetShadowSize()
    {
      return FloatingActionButtonGingerbread.this.mElevation;
    }
  }
  
  private abstract class ShadowAnimatorImpl
    extends ValueAnimatorCompat.AnimatorListenerAdapter
    implements ValueAnimatorCompat.AnimatorUpdateListener
  {
    private float mShadowSizeEnd;
    private float mShadowSizeStart;
    private boolean mValidValues;
    
    private ShadowAnimatorImpl() {}
    
    protected abstract float getTargetShadowSize();
    
    public void onAnimationEnd(ValueAnimatorCompat paramValueAnimatorCompat)
    {
      FloatingActionButtonGingerbread.this.mShadowDrawable.setShadowSize(this.mShadowSizeEnd);
      this.mValidValues = false;
    }
    
    public void onAnimationUpdate(ValueAnimatorCompat paramValueAnimatorCompat)
    {
      if (!this.mValidValues)
      {
        this.mShadowSizeStart = FloatingActionButtonGingerbread.this.mShadowDrawable.getShadowSize();
        this.mShadowSizeEnd = getTargetShadowSize();
        this.mValidValues = true;
      }
      FloatingActionButtonGingerbread.this.mShadowDrawable.setShadowSize(this.mShadowSizeStart + (this.mShadowSizeEnd - this.mShadowSizeStart) * paramValueAnimatorCompat.getAnimatedFraction());
    }
  }
}
