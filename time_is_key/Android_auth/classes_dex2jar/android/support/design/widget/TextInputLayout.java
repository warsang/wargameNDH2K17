package android.support.design.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.ConstantState;
import android.graphics.drawable.DrawableContainer;
import android.os.Build.VERSION;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.support.annotation.VisibleForTesting;
import android.support.design.R.color;
import android.support.design.R.id;
import android.support.design.R.layout;
import android.support.design.R.string;
import android.support.design.R.styleable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v4.view.AbsSavedState;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.widget.Space;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.AppCompatDrawableManager;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.TintTypedArray;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.AccelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import java.util.List;

public class TextInputLayout
  extends LinearLayout
{
  private static final int ANIMATION_DURATION = 200;
  private static final int INVALID_MAX_LENGTH = -1;
  private static final String LOG_TAG = "TextInputLayout";
  private ValueAnimatorCompat mAnimator;
  final CollapsingTextHelper mCollapsingTextHelper = new CollapsingTextHelper(this);
  boolean mCounterEnabled;
  private int mCounterMaxLength;
  private int mCounterOverflowTextAppearance;
  private boolean mCounterOverflowed;
  private int mCounterTextAppearance;
  private TextView mCounterView;
  private ColorStateList mDefaultTextColor;
  EditText mEditText;
  private CharSequence mError;
  private boolean mErrorEnabled;
  private boolean mErrorShown;
  private int mErrorTextAppearance;
  TextView mErrorView;
  private ColorStateList mFocusedTextColor;
  private boolean mHasPasswordToggleTintList;
  private boolean mHasPasswordToggleTintMode;
  private boolean mHasReconstructedEditTextBackground;
  private CharSequence mHint;
  private boolean mHintAnimationEnabled;
  private boolean mHintEnabled;
  private boolean mHintExpanded;
  private boolean mInDrawableStateChanged;
  private LinearLayout mIndicatorArea;
  private int mIndicatorsAdded;
  private final FrameLayout mInputFrame;
  private Drawable mOriginalEditTextEndDrawable;
  private CharSequence mPasswordToggleContentDesc;
  private Drawable mPasswordToggleDrawable;
  private Drawable mPasswordToggleDummyDrawable;
  private boolean mPasswordToggleEnabled;
  private ColorStateList mPasswordToggleTintList;
  private PorterDuff.Mode mPasswordToggleTintMode;
  private CheckableImageButton mPasswordToggleView;
  private boolean mPasswordToggledVisible;
  private boolean mRestoringSavedState;
  private Paint mTmpPaint;
  private final Rect mTmpRect = new Rect();
  private Typeface mTypeface;
  
  public TextInputLayout(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public TextInputLayout(Context paramContext, AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public TextInputLayout(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet);
    ThemeUtils.checkAppCompatTheme(paramContext);
    setOrientation(1);
    setWillNotDraw(false);
    setAddStatesFromChildren(true);
    this.mInputFrame = new FrameLayout(paramContext);
    this.mInputFrame.setAddStatesFromChildren(true);
    addView(this.mInputFrame);
    this.mCollapsingTextHelper.setTextSizeInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
    this.mCollapsingTextHelper.setPositionInterpolator(new AccelerateInterpolator());
    this.mCollapsingTextHelper.setCollapsedTextGravity(8388659);
    TintTypedArray localTintTypedArray = TintTypedArray.obtainStyledAttributes(paramContext, paramAttributeSet, R.styleable.TextInputLayout, paramInt, android.support.design.R.style.Widget_Design_TextInputLayout);
    this.mHintEnabled = localTintTypedArray.getBoolean(R.styleable.TextInputLayout_hintEnabled, true);
    setHint(localTintTypedArray.getText(R.styleable.TextInputLayout_android_hint));
    this.mHintAnimationEnabled = localTintTypedArray.getBoolean(R.styleable.TextInputLayout_hintAnimationEnabled, true);
    if (localTintTypedArray.hasValue(R.styleable.TextInputLayout_android_textColorHint))
    {
      ColorStateList localColorStateList = localTintTypedArray.getColorStateList(R.styleable.TextInputLayout_android_textColorHint);
      this.mFocusedTextColor = localColorStateList;
      this.mDefaultTextColor = localColorStateList;
    }
    if (localTintTypedArray.getResourceId(R.styleable.TextInputLayout_hintTextAppearance, -1) != -1) {
      setHintTextAppearance(localTintTypedArray.getResourceId(R.styleable.TextInputLayout_hintTextAppearance, 0));
    }
    this.mErrorTextAppearance = localTintTypedArray.getResourceId(R.styleable.TextInputLayout_errorTextAppearance, 0);
    boolean bool1 = localTintTypedArray.getBoolean(R.styleable.TextInputLayout_errorEnabled, false);
    boolean bool2 = localTintTypedArray.getBoolean(R.styleable.TextInputLayout_counterEnabled, false);
    setCounterMaxLength(localTintTypedArray.getInt(R.styleable.TextInputLayout_counterMaxLength, -1));
    this.mCounterTextAppearance = localTintTypedArray.getResourceId(R.styleable.TextInputLayout_counterTextAppearance, 0);
    this.mCounterOverflowTextAppearance = localTintTypedArray.getResourceId(R.styleable.TextInputLayout_counterOverflowTextAppearance, 0);
    this.mPasswordToggleEnabled = localTintTypedArray.getBoolean(R.styleable.TextInputLayout_passwordToggleEnabled, false);
    this.mPasswordToggleDrawable = localTintTypedArray.getDrawable(R.styleable.TextInputLayout_passwordToggleDrawable);
    this.mPasswordToggleContentDesc = localTintTypedArray.getText(R.styleable.TextInputLayout_passwordToggleContentDescription);
    if (localTintTypedArray.hasValue(R.styleable.TextInputLayout_passwordToggleTint))
    {
      this.mHasPasswordToggleTintList = true;
      this.mPasswordToggleTintList = localTintTypedArray.getColorStateList(R.styleable.TextInputLayout_passwordToggleTint);
    }
    if (localTintTypedArray.hasValue(R.styleable.TextInputLayout_passwordToggleTintMode))
    {
      this.mHasPasswordToggleTintMode = true;
      this.mPasswordToggleTintMode = ViewUtils.parseTintMode(localTintTypedArray.getInt(R.styleable.TextInputLayout_passwordToggleTintMode, -1), null);
    }
    localTintTypedArray.recycle();
    setErrorEnabled(bool1);
    setCounterEnabled(bool2);
    applyPasswordToggleTint();
    if (ViewCompat.getImportantForAccessibility(this) == 0) {
      ViewCompat.setImportantForAccessibility(this, 1);
    }
    ViewCompat.setAccessibilityDelegate(this, new TextInputAccessibilityDelegate());
  }
  
  private void addIndicator(TextView paramTextView, int paramInt)
  {
    if (this.mIndicatorArea == null)
    {
      this.mIndicatorArea = new LinearLayout(getContext());
      this.mIndicatorArea.setOrientation(0);
      addView(this.mIndicatorArea, -1, -2);
      Space localSpace = new Space(getContext());
      LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(0, 0, 1.0F);
      this.mIndicatorArea.addView(localSpace, localLayoutParams);
      if (this.mEditText != null) {
        adjustIndicatorPadding();
      }
    }
    this.mIndicatorArea.setVisibility(0);
    this.mIndicatorArea.addView(paramTextView, paramInt);
    this.mIndicatorsAdded = (1 + this.mIndicatorsAdded);
  }
  
  private void adjustIndicatorPadding()
  {
    ViewCompat.setPaddingRelative(this.mIndicatorArea, ViewCompat.getPaddingStart(this.mEditText), 0, ViewCompat.getPaddingEnd(this.mEditText), this.mEditText.getPaddingBottom());
  }
  
  private void applyPasswordToggleTint()
  {
    if ((this.mPasswordToggleDrawable != null) && ((this.mHasPasswordToggleTintList) || (this.mHasPasswordToggleTintMode)))
    {
      this.mPasswordToggleDrawable = DrawableCompat.wrap(this.mPasswordToggleDrawable).mutate();
      if (this.mHasPasswordToggleTintList) {
        DrawableCompat.setTintList(this.mPasswordToggleDrawable, this.mPasswordToggleTintList);
      }
      if (this.mHasPasswordToggleTintMode) {
        DrawableCompat.setTintMode(this.mPasswordToggleDrawable, this.mPasswordToggleTintMode);
      }
      if ((this.mPasswordToggleView != null) && (this.mPasswordToggleView.getDrawable() != this.mPasswordToggleDrawable)) {
        this.mPasswordToggleView.setImageDrawable(this.mPasswordToggleDrawable);
      }
    }
  }
  
  private static boolean arrayContains(int[] paramArrayOfInt, int paramInt)
  {
    int i = paramArrayOfInt.length;
    for (int j = 0;; j++)
    {
      boolean bool = false;
      if (j < i)
      {
        if (paramArrayOfInt[j] == paramInt) {
          bool = true;
        }
      }
      else {
        return bool;
      }
    }
  }
  
  private void collapseHint(boolean paramBoolean)
  {
    if ((this.mAnimator != null) && (this.mAnimator.isRunning())) {
      this.mAnimator.cancel();
    }
    if ((paramBoolean) && (this.mHintAnimationEnabled)) {
      animateToExpansionFraction(1.0F);
    }
    for (;;)
    {
      this.mHintExpanded = false;
      return;
      this.mCollapsingTextHelper.setExpansionFraction(1.0F);
    }
  }
  
  private void ensureBackgroundDrawableStateWorkaround()
  {
    int i = Build.VERSION.SDK_INT;
    if ((i != 21) && (i != 22)) {}
    Drawable localDrawable2;
    do
    {
      Drawable localDrawable1;
      do
      {
        return;
        localDrawable1 = this.mEditText.getBackground();
      } while ((localDrawable1 == null) || (this.mHasReconstructedEditTextBackground));
      localDrawable2 = localDrawable1.getConstantState().newDrawable();
      if ((localDrawable1 instanceof DrawableContainer)) {
        this.mHasReconstructedEditTextBackground = DrawableUtils.setContainerConstantState((DrawableContainer)localDrawable1, localDrawable2.getConstantState());
      }
    } while (this.mHasReconstructedEditTextBackground);
    ViewCompat.setBackground(this.mEditText, localDrawable2);
    this.mHasReconstructedEditTextBackground = true;
  }
  
  private void expandHint(boolean paramBoolean)
  {
    if ((this.mAnimator != null) && (this.mAnimator.isRunning())) {
      this.mAnimator.cancel();
    }
    if ((paramBoolean) && (this.mHintAnimationEnabled)) {
      animateToExpansionFraction(0.0F);
    }
    for (;;)
    {
      this.mHintExpanded = true;
      return;
      this.mCollapsingTextHelper.setExpansionFraction(0.0F);
    }
  }
  
  private boolean hasPasswordTransformation()
  {
    return (this.mEditText != null) && ((this.mEditText.getTransformationMethod() instanceof PasswordTransformationMethod));
  }
  
  private static void recursiveSetEnabled(ViewGroup paramViewGroup, boolean paramBoolean)
  {
    int i = 0;
    int j = paramViewGroup.getChildCount();
    while (i < j)
    {
      View localView = paramViewGroup.getChildAt(i);
      localView.setEnabled(paramBoolean);
      if ((localView instanceof ViewGroup)) {
        recursiveSetEnabled((ViewGroup)localView, paramBoolean);
      }
      i++;
    }
  }
  
  private void removeIndicator(TextView paramTextView)
  {
    if (this.mIndicatorArea != null)
    {
      this.mIndicatorArea.removeView(paramTextView);
      int i = -1 + this.mIndicatorsAdded;
      this.mIndicatorsAdded = i;
      if (i == 0) {
        this.mIndicatorArea.setVisibility(8);
      }
    }
  }
  
  private void setEditText(EditText paramEditText)
  {
    if (this.mEditText != null) {
      throw new IllegalArgumentException("We already have an EditText, can only have one");
    }
    if (!(paramEditText instanceof TextInputEditText)) {
      Log.i("TextInputLayout", "EditText added is not a TextInputEditText. Please switch to using that class instead.");
    }
    this.mEditText = paramEditText;
    if (!hasPasswordTransformation()) {
      this.mCollapsingTextHelper.setTypefaces(this.mEditText.getTypeface());
    }
    this.mCollapsingTextHelper.setExpandedTextSize(this.mEditText.getTextSize());
    int i = this.mEditText.getGravity();
    this.mCollapsingTextHelper.setCollapsedTextGravity(0x30 | i & 0xFFFFFF8F);
    this.mCollapsingTextHelper.setExpandedTextGravity(i);
    this.mEditText.addTextChangedListener(new TextWatcher()
    {
      public void afterTextChanged(Editable paramAnonymousEditable)
      {
        TextInputLayout localTextInputLayout = TextInputLayout.this;
        if (!TextInputLayout.this.mRestoringSavedState) {}
        for (boolean bool = true;; bool = false)
        {
          localTextInputLayout.updateLabelState(bool);
          if (TextInputLayout.this.mCounterEnabled) {
            TextInputLayout.this.updateCounter(paramAnonymousEditable.length());
          }
          return;
        }
      }
      
      public void beforeTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
      
      public void onTextChanged(CharSequence paramAnonymousCharSequence, int paramAnonymousInt1, int paramAnonymousInt2, int paramAnonymousInt3) {}
    });
    if (this.mDefaultTextColor == null) {
      this.mDefaultTextColor = this.mEditText.getHintTextColors();
    }
    if ((this.mHintEnabled) && (TextUtils.isEmpty(this.mHint)))
    {
      setHint(this.mEditText.getHint());
      this.mEditText.setHint(null);
    }
    if (this.mCounterView != null) {
      updateCounter(this.mEditText.getText().length());
    }
    if (this.mIndicatorArea != null) {
      adjustIndicatorPadding();
    }
    updatePasswordToggleView();
    updateLabelState(false, true);
  }
  
  private void setError(@Nullable final CharSequence paramCharSequence, boolean paramBoolean)
  {
    boolean bool = true;
    this.mError = paramCharSequence;
    if (!this.mErrorEnabled)
    {
      if (TextUtils.isEmpty(paramCharSequence)) {
        return;
      }
      setErrorEnabled(bool);
    }
    if (!TextUtils.isEmpty(paramCharSequence))
    {
      this.mErrorShown = bool;
      ViewCompat.animate(this.mErrorView).cancel();
      if (!this.mErrorShown) {
        break label159;
      }
      this.mErrorView.setText(paramCharSequence);
      this.mErrorView.setVisibility(0);
      if (!paramBoolean) {
        break label148;
      }
      if (ViewCompat.getAlpha(this.mErrorView) == 1.0F) {
        ViewCompat.setAlpha(this.mErrorView, 0.0F);
      }
      ViewCompat.animate(this.mErrorView).alpha(1.0F).setDuration(200L).setInterpolator(AnimationUtils.LINEAR_OUT_SLOW_IN_INTERPOLATOR).setListener(new ViewPropertyAnimatorListenerAdapter()
      {
        public void onAnimationStart(View paramAnonymousView)
        {
          paramAnonymousView.setVisibility(0);
        }
      }).start();
    }
    for (;;)
    {
      updateEditTextBackground();
      updateLabelState(paramBoolean);
      return;
      bool = false;
      break;
      label148:
      ViewCompat.setAlpha(this.mErrorView, 1.0F);
      continue;
      label159:
      if (this.mErrorView.getVisibility() == 0) {
        if (paramBoolean)
        {
          ViewCompat.animate(this.mErrorView).alpha(0.0F).setDuration(200L).setInterpolator(AnimationUtils.FAST_OUT_LINEAR_IN_INTERPOLATOR).setListener(new ViewPropertyAnimatorListenerAdapter()
          {
            public void onAnimationEnd(View paramAnonymousView)
            {
              TextInputLayout.this.mErrorView.setText(paramCharSequence);
              paramAnonymousView.setVisibility(4);
            }
          }).start();
        }
        else
        {
          this.mErrorView.setText(paramCharSequence);
          this.mErrorView.setVisibility(4);
        }
      }
    }
  }
  
  private void setHintInternal(CharSequence paramCharSequence)
  {
    this.mHint = paramCharSequence;
    this.mCollapsingTextHelper.setText(paramCharSequence);
  }
  
  private boolean shouldShowPasswordIcon()
  {
    return (this.mPasswordToggleEnabled) && ((hasPasswordTransformation()) || (this.mPasswordToggledVisible));
  }
  
  private void updateEditTextBackground()
  {
    if (this.mEditText == null) {}
    Drawable localDrawable;
    do
    {
      return;
      localDrawable = this.mEditText.getBackground();
    } while (localDrawable == null);
    ensureBackgroundDrawableStateWorkaround();
    if (android.support.v7.widget.DrawableUtils.canSafelyMutateDrawable(localDrawable)) {
      localDrawable = localDrawable.mutate();
    }
    if ((this.mErrorShown) && (this.mErrorView != null))
    {
      localDrawable.setColorFilter(AppCompatDrawableManager.getPorterDuffColorFilter(this.mErrorView.getCurrentTextColor(), PorterDuff.Mode.SRC_IN));
      return;
    }
    if ((this.mCounterOverflowed) && (this.mCounterView != null))
    {
      localDrawable.setColorFilter(AppCompatDrawableManager.getPorterDuffColorFilter(this.mCounterView.getCurrentTextColor(), PorterDuff.Mode.SRC_IN));
      return;
    }
    DrawableCompat.clearColorFilter(localDrawable);
    this.mEditText.refreshDrawableState();
  }
  
  private void updateInputLayoutMargins()
  {
    LinearLayout.LayoutParams localLayoutParams = (LinearLayout.LayoutParams)this.mInputFrame.getLayoutParams();
    if (this.mHintEnabled)
    {
      if (this.mTmpPaint == null) {
        this.mTmpPaint = new Paint();
      }
      this.mTmpPaint.setTypeface(this.mCollapsingTextHelper.getCollapsedTypeface());
      this.mTmpPaint.setTextSize(this.mCollapsingTextHelper.getCollapsedTextSize());
    }
    for (int i = (int)-this.mTmpPaint.ascent();; i = 0)
    {
      if (i != localLayoutParams.topMargin)
      {
        localLayoutParams.topMargin = i;
        this.mInputFrame.requestLayout();
      }
      return;
    }
  }
  
  private void updatePasswordToggleView()
  {
    if (this.mEditText == null) {}
    Drawable[] arrayOfDrawable1;
    do
    {
      do
      {
        return;
        if (shouldShowPasswordIcon())
        {
          if (this.mPasswordToggleView == null)
          {
            this.mPasswordToggleView = ((CheckableImageButton)LayoutInflater.from(getContext()).inflate(R.layout.design_text_input_password_icon, this.mInputFrame, false));
            this.mPasswordToggleView.setImageDrawable(this.mPasswordToggleDrawable);
            this.mPasswordToggleView.setContentDescription(this.mPasswordToggleContentDesc);
            this.mInputFrame.addView(this.mPasswordToggleView);
            this.mPasswordToggleView.setOnClickListener(new View.OnClickListener()
            {
              public void onClick(View paramAnonymousView)
              {
                TextInputLayout.this.passwordVisibilityToggleRequested();
              }
            });
          }
          if ((this.mEditText != null) && (ViewCompat.getMinimumHeight(this.mEditText) <= 0)) {
            this.mEditText.setMinimumHeight(ViewCompat.getMinimumHeight(this.mPasswordToggleView));
          }
          this.mPasswordToggleView.setVisibility(0);
          this.mPasswordToggleView.setChecked(this.mPasswordToggledVisible);
          if (this.mPasswordToggleDummyDrawable == null) {
            this.mPasswordToggleDummyDrawable = new ColorDrawable();
          }
          this.mPasswordToggleDummyDrawable.setBounds(0, 0, this.mPasswordToggleView.getMeasuredWidth(), 1);
          Drawable[] arrayOfDrawable2 = TextViewCompat.getCompoundDrawablesRelative(this.mEditText);
          if (arrayOfDrawable2[2] != this.mPasswordToggleDummyDrawable) {
            this.mOriginalEditTextEndDrawable = arrayOfDrawable2[2];
          }
          TextViewCompat.setCompoundDrawablesRelative(this.mEditText, arrayOfDrawable2[0], arrayOfDrawable2[1], this.mPasswordToggleDummyDrawable, arrayOfDrawable2[3]);
          this.mPasswordToggleView.setPadding(this.mEditText.getPaddingLeft(), this.mEditText.getPaddingTop(), this.mEditText.getPaddingRight(), this.mEditText.getPaddingBottom());
          return;
        }
        if ((this.mPasswordToggleView != null) && (this.mPasswordToggleView.getVisibility() == 0)) {
          this.mPasswordToggleView.setVisibility(8);
        }
      } while (this.mPasswordToggleDummyDrawable == null);
      arrayOfDrawable1 = TextViewCompat.getCompoundDrawablesRelative(this.mEditText);
    } while (arrayOfDrawable1[2] != this.mPasswordToggleDummyDrawable);
    TextViewCompat.setCompoundDrawablesRelative(this.mEditText, arrayOfDrawable1[0], arrayOfDrawable1[1], this.mOriginalEditTextEndDrawable, arrayOfDrawable1[3]);
    this.mPasswordToggleDummyDrawable = null;
  }
  
  public void addView(View paramView, int paramInt, ViewGroup.LayoutParams paramLayoutParams)
  {
    if ((paramView instanceof EditText))
    {
      FrameLayout.LayoutParams localLayoutParams = new FrameLayout.LayoutParams(paramLayoutParams);
      localLayoutParams.gravity = (0x10 | 0xFFFFFF8F & localLayoutParams.gravity);
      this.mInputFrame.addView(paramView, localLayoutParams);
      this.mInputFrame.setLayoutParams(paramLayoutParams);
      updateInputLayoutMargins();
      setEditText((EditText)paramView);
      return;
    }
    super.addView(paramView, paramInt, paramLayoutParams);
  }
  
  @VisibleForTesting
  void animateToExpansionFraction(float paramFloat)
  {
    if (this.mCollapsingTextHelper.getExpansionFraction() == paramFloat) {
      return;
    }
    if (this.mAnimator == null)
    {
      this.mAnimator = ViewUtils.createAnimator();
      this.mAnimator.setInterpolator(AnimationUtils.LINEAR_INTERPOLATOR);
      this.mAnimator.setDuration(200L);
      this.mAnimator.addUpdateListener(new ValueAnimatorCompat.AnimatorUpdateListener()
      {
        public void onAnimationUpdate(ValueAnimatorCompat paramAnonymousValueAnimatorCompat)
        {
          TextInputLayout.this.mCollapsingTextHelper.setExpansionFraction(paramAnonymousValueAnimatorCompat.getAnimatedFloatValue());
        }
      });
    }
    this.mAnimator.setFloatValues(this.mCollapsingTextHelper.getExpansionFraction(), paramFloat);
    this.mAnimator.start();
  }
  
  protected void dispatchRestoreInstanceState(SparseArray<Parcelable> paramSparseArray)
  {
    this.mRestoringSavedState = true;
    super.dispatchRestoreInstanceState(paramSparseArray);
    this.mRestoringSavedState = false;
  }
  
  public void draw(Canvas paramCanvas)
  {
    super.draw(paramCanvas);
    if (this.mHintEnabled) {
      this.mCollapsingTextHelper.draw(paramCanvas);
    }
  }
  
  protected void drawableStateChanged()
  {
    boolean bool1 = true;
    if (this.mInDrawableStateChanged) {
      return;
    }
    this.mInDrawableStateChanged = bool1;
    super.drawableStateChanged();
    int[] arrayOfInt = getDrawableState();
    if ((ViewCompat.isLaidOut(this)) && (isEnabled())) {}
    for (;;)
    {
      updateLabelState(bool1);
      updateEditTextBackground();
      CollapsingTextHelper localCollapsingTextHelper = this.mCollapsingTextHelper;
      boolean bool2 = false;
      if (localCollapsingTextHelper != null) {
        bool2 = false | this.mCollapsingTextHelper.setState(arrayOfInt);
      }
      if (bool2) {
        invalidate();
      }
      this.mInDrawableStateChanged = false;
      return;
      bool1 = false;
    }
  }
  
  public int getCounterMaxLength()
  {
    return this.mCounterMaxLength;
  }
  
  @Nullable
  public EditText getEditText()
  {
    return this.mEditText;
  }
  
  @Nullable
  public CharSequence getError()
  {
    if (this.mErrorEnabled) {
      return this.mError;
    }
    return null;
  }
  
  @Nullable
  public CharSequence getHint()
  {
    if (this.mHintEnabled) {
      return this.mHint;
    }
    return null;
  }
  
  @Nullable
  public CharSequence getPasswordVisibilityToggleContentDescription()
  {
    return this.mPasswordToggleContentDesc;
  }
  
  @Nullable
  public Drawable getPasswordVisibilityToggleDrawable()
  {
    return this.mPasswordToggleDrawable;
  }
  
  @NonNull
  public Typeface getTypeface()
  {
    return this.mTypeface;
  }
  
  public boolean isCounterEnabled()
  {
    return this.mCounterEnabled;
  }
  
  public boolean isErrorEnabled()
  {
    return this.mErrorEnabled;
  }
  
  public boolean isHintAnimationEnabled()
  {
    return this.mHintAnimationEnabled;
  }
  
  public boolean isHintEnabled()
  {
    return this.mHintEnabled;
  }
  
  @VisibleForTesting
  final boolean isHintExpanded()
  {
    return this.mHintExpanded;
  }
  
  public boolean isPasswordVisibilityToggleEnabled()
  {
    return this.mPasswordToggleEnabled;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    if ((this.mHintEnabled) && (this.mEditText != null))
    {
      Rect localRect = this.mTmpRect;
      ViewGroupUtils.getDescendantRect(this, this.mEditText, localRect);
      int i = localRect.left + this.mEditText.getCompoundPaddingLeft();
      int j = localRect.right - this.mEditText.getCompoundPaddingRight();
      this.mCollapsingTextHelper.setExpandedBounds(i, localRect.top + this.mEditText.getCompoundPaddingTop(), j, localRect.bottom - this.mEditText.getCompoundPaddingBottom());
      this.mCollapsingTextHelper.setCollapsedBounds(i, getPaddingTop(), j, paramInt4 - paramInt2 - getPaddingBottom());
      this.mCollapsingTextHelper.recalculate();
    }
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    updatePasswordToggleView();
    super.onMeasure(paramInt1, paramInt2);
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if (!(paramParcelable instanceof SavedState))
    {
      super.onRestoreInstanceState(paramParcelable);
      return;
    }
    SavedState localSavedState = (SavedState)paramParcelable;
    super.onRestoreInstanceState(localSavedState.getSuperState());
    setError(localSavedState.error);
    requestLayout();
  }
  
  public Parcelable onSaveInstanceState()
  {
    SavedState localSavedState = new SavedState(super.onSaveInstanceState());
    if (this.mErrorShown) {
      localSavedState.error = getError();
    }
    return localSavedState;
  }
  
  void passwordVisibilityToggleRequested()
  {
    int i;
    if (this.mPasswordToggleEnabled)
    {
      i = this.mEditText.getSelectionEnd();
      if (!hasPasswordTransformation()) {
        break label55;
      }
      this.mEditText.setTransformationMethod(null);
    }
    for (this.mPasswordToggledVisible = true;; this.mPasswordToggledVisible = false)
    {
      this.mPasswordToggleView.setChecked(this.mPasswordToggledVisible);
      this.mEditText.setSelection(i);
      return;
      label55:
      this.mEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }
  }
  
  public void setCounterEnabled(boolean paramBoolean)
  {
    if (this.mCounterEnabled != paramBoolean)
    {
      if (!paramBoolean) {
        break label151;
      }
      this.mCounterView = new AppCompatTextView(getContext());
      this.mCounterView.setId(R.id.textinput_counter);
      if (this.mTypeface != null) {
        this.mCounterView.setTypeface(this.mTypeface);
      }
      this.mCounterView.setMaxLines(1);
    }
    for (;;)
    {
      try
      {
        TextViewCompat.setTextAppearance(this.mCounterView, this.mCounterTextAppearance);
        addIndicator(this.mCounterView, -1);
        if (this.mEditText == null)
        {
          updateCounter(0);
          this.mCounterEnabled = paramBoolean;
          return;
        }
      }
      catch (Exception localException)
      {
        TextViewCompat.setTextAppearance(this.mCounterView, android.support.v7.appcompat.R.style.TextAppearance_AppCompat_Caption);
        this.mCounterView.setTextColor(ContextCompat.getColor(getContext(), R.color.design_textinput_error_color_light));
        continue;
        updateCounter(this.mEditText.getText().length());
        continue;
      }
      label151:
      removeIndicator(this.mCounterView);
      this.mCounterView = null;
    }
  }
  
  public void setCounterMaxLength(int paramInt)
  {
    if (this.mCounterMaxLength != paramInt)
    {
      if (paramInt <= 0) {
        break label39;
      }
      this.mCounterMaxLength = paramInt;
      if (this.mCounterEnabled) {
        if (this.mEditText != null) {
          break label47;
        }
      }
    }
    label39:
    label47:
    for (int i = 0;; i = this.mEditText.getText().length())
    {
      updateCounter(i);
      return;
      this.mCounterMaxLength = -1;
      break;
    }
  }
  
  public void setEnabled(boolean paramBoolean)
  {
    recursiveSetEnabled(this, paramBoolean);
    super.setEnabled(paramBoolean);
  }
  
  public void setError(@Nullable CharSequence paramCharSequence)
  {
    if ((ViewCompat.isLaidOut(this)) && (isEnabled()) && ((this.mErrorView == null) || (!TextUtils.equals(this.mErrorView.getText(), paramCharSequence)))) {}
    for (boolean bool = true;; bool = false)
    {
      setError(paramCharSequence, bool);
      return;
    }
  }
  
  public void setErrorEnabled(boolean paramBoolean)
  {
    if (this.mErrorEnabled != paramBoolean)
    {
      if (this.mErrorView != null) {
        ViewCompat.animate(this.mErrorView).cancel();
      }
      if (!paramBoolean) {
        break label189;
      }
      this.mErrorView = new AppCompatTextView(getContext());
      this.mErrorView.setId(R.id.textinput_error);
      if (this.mTypeface != null) {
        this.mErrorView.setTypeface(this.mTypeface);
      }
    }
    try
    {
      TextViewCompat.setTextAppearance(this.mErrorView, this.mErrorTextAppearance);
      int j = Build.VERSION.SDK_INT;
      i = 0;
      if (j >= 23)
      {
        int k = this.mErrorView.getTextColors().getDefaultColor();
        i = 0;
        if (k == -65281) {
          i = 1;
        }
      }
    }
    catch (Exception localException)
    {
      for (;;)
      {
        int i = 1;
      }
    }
    if (i != 0)
    {
      TextViewCompat.setTextAppearance(this.mErrorView, android.support.v7.appcompat.R.style.TextAppearance_AppCompat_Caption);
      this.mErrorView.setTextColor(ContextCompat.getColor(getContext(), R.color.design_textinput_error_color_light));
    }
    this.mErrorView.setVisibility(4);
    ViewCompat.setAccessibilityLiveRegion(this.mErrorView, 1);
    addIndicator(this.mErrorView, 0);
    for (;;)
    {
      this.mErrorEnabled = paramBoolean;
      return;
      label189:
      this.mErrorShown = false;
      updateEditTextBackground();
      removeIndicator(this.mErrorView);
      this.mErrorView = null;
    }
  }
  
  public void setErrorTextAppearance(@StyleRes int paramInt)
  {
    this.mErrorTextAppearance = paramInt;
    if (this.mErrorView != null) {
      TextViewCompat.setTextAppearance(this.mErrorView, paramInt);
    }
  }
  
  public void setHint(@Nullable CharSequence paramCharSequence)
  {
    if (this.mHintEnabled)
    {
      setHintInternal(paramCharSequence);
      sendAccessibilityEvent(2048);
    }
  }
  
  public void setHintAnimationEnabled(boolean paramBoolean)
  {
    this.mHintAnimationEnabled = paramBoolean;
  }
  
  public void setHintEnabled(boolean paramBoolean)
  {
    CharSequence localCharSequence;
    if (paramBoolean != this.mHintEnabled)
    {
      this.mHintEnabled = paramBoolean;
      localCharSequence = this.mEditText.getHint();
      if (this.mHintEnabled) {
        break label73;
      }
      if ((!TextUtils.isEmpty(this.mHint)) && (TextUtils.isEmpty(localCharSequence))) {
        this.mEditText.setHint(this.mHint);
      }
      setHintInternal(null);
    }
    for (;;)
    {
      if (this.mEditText != null) {
        updateInputLayoutMargins();
      }
      return;
      label73:
      if (!TextUtils.isEmpty(localCharSequence))
      {
        if (TextUtils.isEmpty(this.mHint)) {
          setHint(localCharSequence);
        }
        this.mEditText.setHint(null);
      }
    }
  }
  
  public void setHintTextAppearance(@StyleRes int paramInt)
  {
    this.mCollapsingTextHelper.setCollapsedTextAppearance(paramInt);
    this.mFocusedTextColor = this.mCollapsingTextHelper.getCollapsedTextColor();
    if (this.mEditText != null)
    {
      updateLabelState(false);
      updateInputLayoutMargins();
    }
  }
  
  public void setPasswordVisibilityToggleContentDescription(@StringRes int paramInt)
  {
    if (paramInt != 0) {}
    for (CharSequence localCharSequence = getResources().getText(paramInt);; localCharSequence = null)
    {
      setPasswordVisibilityToggleContentDescription(localCharSequence);
      return;
    }
  }
  
  public void setPasswordVisibilityToggleContentDescription(@Nullable CharSequence paramCharSequence)
  {
    this.mPasswordToggleContentDesc = paramCharSequence;
    if (this.mPasswordToggleView != null) {
      this.mPasswordToggleView.setContentDescription(paramCharSequence);
    }
  }
  
  public void setPasswordVisibilityToggleDrawable(@DrawableRes int paramInt)
  {
    if (paramInt != 0) {}
    for (Drawable localDrawable = AppCompatResources.getDrawable(getContext(), paramInt);; localDrawable = null)
    {
      setPasswordVisibilityToggleDrawable(localDrawable);
      return;
    }
  }
  
  public void setPasswordVisibilityToggleDrawable(@Nullable Drawable paramDrawable)
  {
    this.mPasswordToggleDrawable = paramDrawable;
    if (this.mPasswordToggleView != null) {
      this.mPasswordToggleView.setImageDrawable(paramDrawable);
    }
  }
  
  public void setPasswordVisibilityToggleEnabled(boolean paramBoolean)
  {
    if (this.mPasswordToggleEnabled != paramBoolean)
    {
      this.mPasswordToggleEnabled = paramBoolean;
      if ((!paramBoolean) && (this.mPasswordToggledVisible) && (this.mEditText != null)) {
        this.mEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
      }
      this.mPasswordToggledVisible = false;
      updatePasswordToggleView();
    }
  }
  
  public void setPasswordVisibilityToggleTintList(@Nullable ColorStateList paramColorStateList)
  {
    this.mPasswordToggleTintList = paramColorStateList;
    this.mHasPasswordToggleTintList = true;
    applyPasswordToggleTint();
  }
  
  public void setPasswordVisibilityToggleTintMode(@Nullable PorterDuff.Mode paramMode)
  {
    this.mPasswordToggleTintMode = paramMode;
    this.mHasPasswordToggleTintMode = true;
    applyPasswordToggleTint();
  }
  
  public void setTypeface(@Nullable Typeface paramTypeface)
  {
    if (paramTypeface != this.mTypeface)
    {
      this.mTypeface = paramTypeface;
      this.mCollapsingTextHelper.setTypefaces(paramTypeface);
      if (this.mCounterView != null) {
        this.mCounterView.setTypeface(paramTypeface);
      }
      if (this.mErrorView != null) {
        this.mErrorView.setTypeface(paramTypeface);
      }
    }
  }
  
  void updateCounter(int paramInt)
  {
    boolean bool1 = this.mCounterOverflowed;
    if (this.mCounterMaxLength == -1)
    {
      this.mCounterView.setText(String.valueOf(paramInt));
      this.mCounterOverflowed = false;
      if ((this.mEditText != null) && (bool1 != this.mCounterOverflowed))
      {
        updateLabelState(false);
        updateEditTextBackground();
      }
      return;
    }
    boolean bool2;
    label64:
    TextView localTextView2;
    if (paramInt > this.mCounterMaxLength)
    {
      bool2 = true;
      this.mCounterOverflowed = bool2;
      if (bool1 != this.mCounterOverflowed)
      {
        localTextView2 = this.mCounterView;
        if (!this.mCounterOverflowed) {
          break label167;
        }
      }
    }
    label167:
    for (int j = this.mCounterOverflowTextAppearance;; j = this.mCounterTextAppearance)
    {
      TextViewCompat.setTextAppearance(localTextView2, j);
      TextView localTextView1 = this.mCounterView;
      Context localContext = getContext();
      int i = R.string.character_counter_pattern;
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = Integer.valueOf(paramInt);
      arrayOfObject[1] = Integer.valueOf(this.mCounterMaxLength);
      localTextView1.setText(localContext.getString(i, arrayOfObject));
      break;
      bool2 = false;
      break label64;
    }
  }
  
  void updateLabelState(boolean paramBoolean)
  {
    updateLabelState(paramBoolean, false);
  }
  
  void updateLabelState(boolean paramBoolean1, boolean paramBoolean2)
  {
    boolean bool1 = isEnabled();
    int i;
    boolean bool2;
    int j;
    if ((this.mEditText != null) && (!TextUtils.isEmpty(this.mEditText.getText())))
    {
      i = 1;
      bool2 = arrayContains(getDrawableState(), 16842908);
      if (TextUtils.isEmpty(getError())) {
        break label148;
      }
      j = 1;
      label53:
      if (this.mDefaultTextColor != null) {
        this.mCollapsingTextHelper.setExpandedTextColor(this.mDefaultTextColor);
      }
      if ((!bool1) || (!this.mCounterOverflowed) || (this.mCounterView == null)) {
        break label154;
      }
      this.mCollapsingTextHelper.setCollapsedTextColor(this.mCounterView.getTextColors());
      label103:
      if ((i == 0) && ((!isEnabled()) || ((!bool2) && (j == 0)))) {
        break label205;
      }
      if ((paramBoolean2) || (this.mHintExpanded)) {
        collapseHint(paramBoolean1);
      }
    }
    label148:
    label154:
    label205:
    while ((!paramBoolean2) && (this.mHintExpanded))
    {
      return;
      i = 0;
      break;
      j = 0;
      break label53;
      if ((bool1) && (bool2) && (this.mFocusedTextColor != null))
      {
        this.mCollapsingTextHelper.setCollapsedTextColor(this.mFocusedTextColor);
        break label103;
      }
      if (this.mDefaultTextColor == null) {
        break label103;
      }
      this.mCollapsingTextHelper.setCollapsedTextColor(this.mDefaultTextColor);
      break label103;
    }
    expandHint(paramBoolean1);
  }
  
  static class SavedState
    extends AbsSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks()
    {
      public TextInputLayout.SavedState createFromParcel(Parcel paramAnonymousParcel, ClassLoader paramAnonymousClassLoader)
      {
        return new TextInputLayout.SavedState(paramAnonymousParcel, paramAnonymousClassLoader);
      }
      
      public TextInputLayout.SavedState[] newArray(int paramAnonymousInt)
      {
        return new TextInputLayout.SavedState[paramAnonymousInt];
      }
    });
    CharSequence error;
    
    SavedState(Parcel paramParcel, ClassLoader paramClassLoader)
    {
      super(paramClassLoader);
      this.error = ((CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(paramParcel));
    }
    
    SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    public String toString()
    {
      return "TextInputLayout.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " error=" + this.error + "}";
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      TextUtils.writeToParcel(this.error, paramParcel, paramInt);
    }
  }
  
  private class TextInputAccessibilityDelegate
    extends AccessibilityDelegateCompat
  {
    TextInputAccessibilityDelegate() {}
    
    public void onInitializeAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
    {
      super.onInitializeAccessibilityEvent(paramView, paramAccessibilityEvent);
      paramAccessibilityEvent.setClassName(TextInputLayout.class.getSimpleName());
    }
    
    public void onInitializeAccessibilityNodeInfo(View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
    {
      super.onInitializeAccessibilityNodeInfo(paramView, paramAccessibilityNodeInfoCompat);
      paramAccessibilityNodeInfoCompat.setClassName(TextInputLayout.class.getSimpleName());
      CharSequence localCharSequence1 = TextInputLayout.this.mCollapsingTextHelper.getText();
      if (!TextUtils.isEmpty(localCharSequence1)) {
        paramAccessibilityNodeInfoCompat.setText(localCharSequence1);
      }
      if (TextInputLayout.this.mEditText != null) {
        paramAccessibilityNodeInfoCompat.setLabelFor(TextInputLayout.this.mEditText);
      }
      if (TextInputLayout.this.mErrorView != null) {}
      for (CharSequence localCharSequence2 = TextInputLayout.this.mErrorView.getText();; localCharSequence2 = null)
      {
        if (!TextUtils.isEmpty(localCharSequence2))
        {
          paramAccessibilityNodeInfoCompat.setContentInvalid(true);
          paramAccessibilityNodeInfoCompat.setError(localCharSequence2);
        }
        return;
      }
    }
    
    public void onPopulateAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent)
    {
      super.onPopulateAccessibilityEvent(paramView, paramAccessibilityEvent);
      CharSequence localCharSequence = TextInputLayout.this.mCollapsingTextHelper.getText();
      if (!TextUtils.isEmpty(localCharSequence)) {
        paramAccessibilityEvent.getText().add(localCharSequence);
      }
    }
  }
}
