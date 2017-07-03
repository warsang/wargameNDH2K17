package android.support.design.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build.VERSION;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.VisibleForTesting;
import android.support.design.R.dimen;
import android.support.design.R.style;
import android.support.design.R.styleable;
import android.support.v4.content.res.ConfigurationHelper;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatImageHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

@CoordinatorLayout.DefaultBehavior("Landroid/support/design/widget/FloatingActionButton$Behavior;")
public class FloatingActionButton
  extends VisibilityAwareImageButton
{
  private static final int AUTO_MINI_LARGEST_SCREEN_WIDTH = 470;
  private static final String LOG_TAG = "FloatingActionButton";
  public static final int SIZE_AUTO = -1;
  public static final int SIZE_MINI = 1;
  public static final int SIZE_NORMAL;
  private ColorStateList mBackgroundTint;
  private PorterDuff.Mode mBackgroundTintMode;
  private int mBorderWidth;
  boolean mCompatPadding;
  private AppCompatImageHelper mImageHelper;
  int mImagePadding;
  private FloatingActionButtonImpl mImpl;
  private int mMaxImageSize;
  private int mRippleColor;
  final Rect mShadowPadding = new Rect();
  private int mSize;
  private final Rect mTouchArea = new Rect();
  
  public FloatingActionButton(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public FloatingActionButton(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public FloatingActionButton(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    ThemeUtils.checkAppCompatTheme(paramContext);
    TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.FloatingActionButton, paramInt, R.style.Widget_Design_FloatingActionButton);
    this.mBackgroundTint = localTypedArray.getColorStateList(R.styleable.FloatingActionButton_backgroundTint);
    this.mBackgroundTintMode = ViewUtils.parseTintMode(localTypedArray.getInt(R.styleable.FloatingActionButton_backgroundTintMode, -1), null);
    this.mRippleColor = localTypedArray.getColor(R.styleable.FloatingActionButton_rippleColor, 0);
    this.mSize = localTypedArray.getInt(R.styleable.FloatingActionButton_fabSize, -1);
    this.mBorderWidth = localTypedArray.getDimensionPixelSize(R.styleable.FloatingActionButton_borderWidth, 0);
    float f1 = localTypedArray.getDimension(R.styleable.FloatingActionButton_elevation, 0.0F);
    float f2 = localTypedArray.getDimension(R.styleable.FloatingActionButton_pressedTranslationZ, 0.0F);
    this.mCompatPadding = localTypedArray.getBoolean(R.styleable.FloatingActionButton_useCompatPadding, false);
    localTypedArray.recycle();
    this.mImageHelper = new AppCompatImageHelper(this);
    this.mImageHelper.loadFromAttributes(paramAttributeSet, paramInt);
    this.mMaxImageSize = ((int)getResources().getDimension(R.dimen.design_fab_image_size));
    getImpl().setBackgroundDrawable(this.mBackgroundTint, this.mBackgroundTintMode, this.mRippleColor, this.mBorderWidth);
    getImpl().setElevation(f1);
    getImpl().setPressedTranslationZ(f2);
  }
  
  private FloatingActionButtonImpl createImpl()
  {
    int i = Build.VERSION.SDK_INT;
    if (i >= 21) {
      return new FloatingActionButtonLollipop(this, new ShadowDelegateImpl(), ViewUtils.DEFAULT_ANIMATOR_CREATOR);
    }
    if (i >= 14) {
      return new FloatingActionButtonIcs(this, new ShadowDelegateImpl(), ViewUtils.DEFAULT_ANIMATOR_CREATOR);
    }
    return new FloatingActionButtonGingerbread(this, new ShadowDelegateImpl(), ViewUtils.DEFAULT_ANIMATOR_CREATOR);
  }
  
  private FloatingActionButtonImpl getImpl()
  {
    if (this.mImpl == null) {
      this.mImpl = createImpl();
    }
    return this.mImpl;
  }
  
  private int getSizeDimension(int paramInt)
  {
    Resources localResources = getResources();
    switch (paramInt)
    {
    case 0: 
    default: 
      return localResources.getDimensionPixelSize(R.dimen.design_fab_size_normal);
    case -1: 
      if (Math.max(ConfigurationHelper.getScreenWidthDp(localResources), ConfigurationHelper.getScreenHeightDp(localResources)) < 470) {
        return getSizeDimension(1);
      }
      return getSizeDimension(0);
    }
    return localResources.getDimensionPixelSize(R.dimen.design_fab_size_mini);
  }
  
  private static int resolveAdjustedSize(int paramInt1, int paramInt2)
  {
    int i = View.MeasureSpec.getMode(paramInt2);
    int j = View.MeasureSpec.getSize(paramInt2);
    switch (i)
    {
    default: 
      return paramInt1;
    case 0: 
      return paramInt1;
    case -2147483648: 
      return Math.min(paramInt1, j);
    }
    return j;
  }
  
  @Nullable
  private FloatingActionButtonImpl.InternalVisibilityChangedListener wrapOnVisibilityChangedListener(@Nullable final OnVisibilityChangedListener paramOnVisibilityChangedListener)
  {
    if (paramOnVisibilityChangedListener == null) {
      return null;
    }
    new FloatingActionButtonImpl.InternalVisibilityChangedListener()
    {
      public void onHidden()
      {
        paramOnVisibilityChangedListener.onHidden(FloatingActionButton.this);
      }
      
      public void onShown()
      {
        paramOnVisibilityChangedListener.onShown(FloatingActionButton.this);
      }
    };
  }
  
  protected void drawableStateChanged()
  {
    super.drawableStateChanged();
    getImpl().onDrawableStateChanged(getDrawableState());
  }
  
  @Nullable
  public ColorStateList getBackgroundTintList()
  {
    return this.mBackgroundTint;
  }
  
  @Nullable
  public PorterDuff.Mode getBackgroundTintMode()
  {
    return this.mBackgroundTintMode;
  }
  
  public float getCompatElevation()
  {
    return getImpl().getElevation();
  }
  
  @NonNull
  public Drawable getContentBackground()
  {
    return getImpl().getContentBackground();
  }
  
  public boolean getContentRect(@NonNull Rect paramRect)
  {
    boolean bool1 = ViewCompat.isLaidOut(this);
    boolean bool2 = false;
    if (bool1)
    {
      paramRect.set(0, 0, getWidth(), getHeight());
      paramRect.left += this.mShadowPadding.left;
      paramRect.top += this.mShadowPadding.top;
      paramRect.right -= this.mShadowPadding.right;
      paramRect.bottom -= this.mShadowPadding.bottom;
      bool2 = true;
    }
    return bool2;
  }
  
  @ColorInt
  public int getRippleColor()
  {
    return this.mRippleColor;
  }
  
  public int getSize()
  {
    return this.mSize;
  }
  
  int getSizeDimension()
  {
    return getSizeDimension(this.mSize);
  }
  
  public boolean getUseCompatPadding()
  {
    return this.mCompatPadding;
  }
  
  public void hide()
  {
    hide(null);
  }
  
  public void hide(@Nullable OnVisibilityChangedListener paramOnVisibilityChangedListener)
  {
    hide(paramOnVisibilityChangedListener, true);
  }
  
  void hide(@Nullable OnVisibilityChangedListener paramOnVisibilityChangedListener, boolean paramBoolean)
  {
    getImpl().hide(wrapOnVisibilityChangedListener(paramOnVisibilityChangedListener), paramBoolean);
  }
  
  @TargetApi(11)
  public void jumpDrawablesToCurrentState()
  {
    super.jumpDrawablesToCurrentState();
    getImpl().jumpDrawableToCurrentState();
  }
  
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    getImpl().onAttachedToWindow();
  }
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    getImpl().onDetachedFromWindow();
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    int i = getSizeDimension();
    this.mImagePadding = ((i - this.mMaxImageSize) / 2);
    getImpl().updatePadding();
    int j = Math.min(resolveAdjustedSize(i, paramInt1), resolveAdjustedSize(i, paramInt2));
    setMeasuredDimension(j + this.mShadowPadding.left + this.mShadowPadding.right, j + this.mShadowPadding.top + this.mShadowPadding.bottom);
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    switch (paramMotionEvent.getAction())
    {
    }
    do
    {
      return super.onTouchEvent(paramMotionEvent);
    } while ((!getContentRect(this.mTouchArea)) || (this.mTouchArea.contains((int)paramMotionEvent.getX(), (int)paramMotionEvent.getY())));
    return false;
  }
  
  public void setBackgroundColor(int paramInt)
  {
    Log.i("FloatingActionButton", "Setting a custom background is not supported.");
  }
  
  public void setBackgroundDrawable(Drawable paramDrawable)
  {
    Log.i("FloatingActionButton", "Setting a custom background is not supported.");
  }
  
  public void setBackgroundResource(int paramInt)
  {
    Log.i("FloatingActionButton", "Setting a custom background is not supported.");
  }
  
  public void setBackgroundTintList(@Nullable ColorStateList paramColorStateList)
  {
    if (this.mBackgroundTint != paramColorStateList)
    {
      this.mBackgroundTint = paramColorStateList;
      getImpl().setBackgroundTintList(paramColorStateList);
    }
  }
  
  public void setBackgroundTintMode(@Nullable PorterDuff.Mode paramMode)
  {
    if (this.mBackgroundTintMode != paramMode)
    {
      this.mBackgroundTintMode = paramMode;
      getImpl().setBackgroundTintMode(paramMode);
    }
  }
  
  public void setCompatElevation(float paramFloat)
  {
    getImpl().setElevation(paramFloat);
  }
  
  public void setImageResource(@DrawableRes int paramInt)
  {
    this.mImageHelper.setImageResource(paramInt);
  }
  
  public void setRippleColor(@ColorInt int paramInt)
  {
    if (this.mRippleColor != paramInt)
    {
      this.mRippleColor = paramInt;
      getImpl().setRippleColor(paramInt);
    }
  }
  
  public void setSize(int paramInt)
  {
    if (paramInt != this.mSize)
    {
      this.mSize = paramInt;
      requestLayout();
    }
  }
  
  public void setUseCompatPadding(boolean paramBoolean)
  {
    if (this.mCompatPadding != paramBoolean)
    {
      this.mCompatPadding = paramBoolean;
      getImpl().onCompatShadowChanged();
    }
  }
  
  public void show()
  {
    show(null);
  }
  
  public void show(@Nullable OnVisibilityChangedListener paramOnVisibilityChangedListener)
  {
    show(paramOnVisibilityChangedListener, true);
  }
  
  void show(OnVisibilityChangedListener paramOnVisibilityChangedListener, boolean paramBoolean)
  {
    getImpl().show(wrapOnVisibilityChangedListener(paramOnVisibilityChangedListener), paramBoolean);
  }
  
  public static class Behavior
    extends CoordinatorLayout.Behavior<FloatingActionButton>
  {
    private static final boolean AUTO_HIDE_DEFAULT = true;
    private boolean mAutoHideEnabled;
    private FloatingActionButton.OnVisibilityChangedListener mInternalAutoHideListener;
    private Rect mTmpRect;
    
    public Behavior()
    {
      this.mAutoHideEnabled = true;
    }
    
    public Behavior(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
      TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.FloatingActionButton_Behavior_Layout);
      this.mAutoHideEnabled = localTypedArray.getBoolean(R.styleable.FloatingActionButton_Behavior_Layout_behavior_autoHide, true);
      localTypedArray.recycle();
    }
    
    private static boolean isBottomSheet(@NonNull View paramView)
    {
      ViewGroup.LayoutParams localLayoutParams = paramView.getLayoutParams();
      if ((localLayoutParams instanceof CoordinatorLayout.LayoutParams)) {
        return ((CoordinatorLayout.LayoutParams)localLayoutParams).getBehavior() instanceof BottomSheetBehavior;
      }
      return false;
    }
    
    private void offsetIfNeeded(CoordinatorLayout paramCoordinatorLayout, FloatingActionButton paramFloatingActionButton)
    {
      Rect localRect = paramFloatingActionButton.mShadowPadding;
      CoordinatorLayout.LayoutParams localLayoutParams;
      int k;
      int i1;
      if ((localRect != null) && (localRect.centerX() > 0) && (localRect.centerY() > 0))
      {
        localLayoutParams = (CoordinatorLayout.LayoutParams)paramFloatingActionButton.getLayoutParams();
        if (paramFloatingActionButton.getRight() < paramCoordinatorLayout.getWidth() - localLayoutParams.rightMargin) {
          break label101;
        }
        k = localRect.right;
        if (paramFloatingActionButton.getBottom() < paramCoordinatorLayout.getHeight() - localLayoutParams.bottomMargin) {
          break label134;
        }
        i1 = localRect.bottom;
      }
      for (;;)
      {
        if (i1 != 0) {
          ViewCompat.offsetTopAndBottom(paramFloatingActionButton, i1);
        }
        if (k != 0) {
          ViewCompat.offsetLeftAndRight(paramFloatingActionButton, k);
        }
        return;
        label101:
        int i = paramFloatingActionButton.getLeft();
        int j = localLayoutParams.leftMargin;
        k = 0;
        if (i > j) {
          break;
        }
        k = -localRect.left;
        break;
        label134:
        int m = paramFloatingActionButton.getTop();
        int n = localLayoutParams.topMargin;
        i1 = 0;
        if (m <= n) {
          i1 = -localRect.top;
        }
      }
    }
    
    private boolean shouldUpdateVisibility(View paramView, FloatingActionButton paramFloatingActionButton)
    {
      CoordinatorLayout.LayoutParams localLayoutParams = (CoordinatorLayout.LayoutParams)paramFloatingActionButton.getLayoutParams();
      if (!this.mAutoHideEnabled) {}
      while ((localLayoutParams.getAnchorId() != paramView.getId()) || (paramFloatingActionButton.getUserSetVisibility() != 0)) {
        return false;
      }
      return true;
    }
    
    private boolean updateFabVisibilityForAppBarLayout(CoordinatorLayout paramCoordinatorLayout, AppBarLayout paramAppBarLayout, FloatingActionButton paramFloatingActionButton)
    {
      if (!shouldUpdateVisibility(paramAppBarLayout, paramFloatingActionButton)) {
        return false;
      }
      if (this.mTmpRect == null) {
        this.mTmpRect = new Rect();
      }
      Rect localRect = this.mTmpRect;
      ViewGroupUtils.getDescendantRect(paramCoordinatorLayout, paramAppBarLayout, localRect);
      if (localRect.bottom <= paramAppBarLayout.getMinimumHeightForVisibleOverlappingContent()) {
        paramFloatingActionButton.hide(this.mInternalAutoHideListener, false);
      }
      for (;;)
      {
        return true;
        paramFloatingActionButton.show(this.mInternalAutoHideListener, false);
      }
    }
    
    private boolean updateFabVisibilityForBottomSheet(View paramView, FloatingActionButton paramFloatingActionButton)
    {
      if (!shouldUpdateVisibility(paramView, paramFloatingActionButton)) {
        return false;
      }
      CoordinatorLayout.LayoutParams localLayoutParams = (CoordinatorLayout.LayoutParams)paramFloatingActionButton.getLayoutParams();
      if (paramView.getTop() < paramFloatingActionButton.getHeight() / 2 + localLayoutParams.topMargin) {
        paramFloatingActionButton.hide(this.mInternalAutoHideListener, false);
      }
      for (;;)
      {
        return true;
        paramFloatingActionButton.show(this.mInternalAutoHideListener, false);
      }
    }
    
    public boolean getInsetDodgeRect(@NonNull CoordinatorLayout paramCoordinatorLayout, @NonNull FloatingActionButton paramFloatingActionButton, @NonNull Rect paramRect)
    {
      Rect localRect = paramFloatingActionButton.mShadowPadding;
      paramRect.set(paramFloatingActionButton.getLeft() + localRect.left, paramFloatingActionButton.getTop() + localRect.top, paramFloatingActionButton.getRight() - localRect.right, paramFloatingActionButton.getBottom() - localRect.bottom);
      return true;
    }
    
    public boolean isAutoHideEnabled()
    {
      return this.mAutoHideEnabled;
    }
    
    public void onAttachedToLayoutParams(@NonNull CoordinatorLayout.LayoutParams paramLayoutParams)
    {
      if (paramLayoutParams.dodgeInsetEdges == 0) {
        paramLayoutParams.dodgeInsetEdges = 80;
      }
    }
    
    public boolean onDependentViewChanged(CoordinatorLayout paramCoordinatorLayout, FloatingActionButton paramFloatingActionButton, View paramView)
    {
      if ((paramView instanceof AppBarLayout)) {
        updateFabVisibilityForAppBarLayout(paramCoordinatorLayout, (AppBarLayout)paramView, paramFloatingActionButton);
      }
      for (;;)
      {
        return false;
        if (isBottomSheet(paramView)) {
          updateFabVisibilityForBottomSheet(paramView, paramFloatingActionButton);
        }
      }
    }
    
    public boolean onLayoutChild(CoordinatorLayout paramCoordinatorLayout, FloatingActionButton paramFloatingActionButton, int paramInt)
    {
      List localList = paramCoordinatorLayout.getDependencies(paramFloatingActionButton);
      int i = 0;
      int j = localList.size();
      for (;;)
      {
        View localView;
        if (i < j)
        {
          localView = (View)localList.get(i);
          if (!(localView instanceof AppBarLayout)) {
            break label76;
          }
          if (!updateFabVisibilityForAppBarLayout(paramCoordinatorLayout, (AppBarLayout)localView, paramFloatingActionButton)) {
            break label94;
          }
        }
        label76:
        while ((isBottomSheet(localView)) && (updateFabVisibilityForBottomSheet(localView, paramFloatingActionButton)))
        {
          paramCoordinatorLayout.onLayoutChild(paramFloatingActionButton, paramInt);
          offsetIfNeeded(paramCoordinatorLayout, paramFloatingActionButton);
          return true;
        }
        label94:
        i++;
      }
    }
    
    public void setAutoHideEnabled(boolean paramBoolean)
    {
      this.mAutoHideEnabled = paramBoolean;
    }
    
    @VisibleForTesting
    void setInternalAutoHideListener(FloatingActionButton.OnVisibilityChangedListener paramOnVisibilityChangedListener)
    {
      this.mInternalAutoHideListener = paramOnVisibilityChangedListener;
    }
  }
  
  public static abstract class OnVisibilityChangedListener
  {
    public OnVisibilityChangedListener() {}
    
    public void onHidden(FloatingActionButton paramFloatingActionButton) {}
    
    public void onShown(FloatingActionButton paramFloatingActionButton) {}
  }
  
  private class ShadowDelegateImpl
    implements ShadowViewDelegate
  {
    ShadowDelegateImpl() {}
    
    public float getRadius()
    {
      return FloatingActionButton.this.getSizeDimension() / 2.0F;
    }
    
    public boolean isCompatPaddingEnabled()
    {
      return FloatingActionButton.this.mCompatPadding;
    }
    
    public void setBackgroundDrawable(Drawable paramDrawable)
    {
      FloatingActionButton.this.setBackgroundDrawable(paramDrawable);
    }
    
    public void setShadowPadding(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      FloatingActionButton.this.mShadowPadding.set(paramInt1, paramInt2, paramInt3, paramInt4);
      FloatingActionButton.this.setPadding(paramInt1 + FloatingActionButton.this.mImagePadding, paramInt2 + FloatingActionButton.this.mImagePadding, paramInt3 + FloatingActionButton.this.mImagePadding, paramInt4 + FloatingActionButton.this.mImagePadding);
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public static @interface Size {}
}
