package android.support.graphics.drawable;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Drawable.Callback;
import android.graphics.drawable.Drawable.ConstantState;
import android.os.Build.VERSION;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Xml;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

@SuppressLint({"NewApi"})
public class AnimatedVectorDrawableCompat
  extends VectorDrawableCommon
  implements Animatable2Compat
{
  private static final String ANIMATED_VECTOR = "animated-vector";
  private static final boolean DBG_ANIMATION_VECTOR_DRAWABLE = false;
  private static final String LOGTAG = "AnimatedVDCompat";
  private static final String TARGET = "target";
  private AnimatedVectorDrawableCompatState mAnimatedVectorState;
  private ArrayList<Animatable2Compat.AnimationCallback> mAnimationCallbacks = null;
  private Animator.AnimatorListener mAnimatorListener = null;
  private ArgbEvaluator mArgbEvaluator = null;
  AnimatedVectorDrawableDelegateState mCachedConstantStateDelegate;
  final Drawable.Callback mCallback = new Drawable.Callback()
  {
    public void invalidateDrawable(Drawable paramAnonymousDrawable)
    {
      AnimatedVectorDrawableCompat.this.invalidateSelf();
    }
    
    public void scheduleDrawable(Drawable paramAnonymousDrawable, Runnable paramAnonymousRunnable, long paramAnonymousLong)
    {
      AnimatedVectorDrawableCompat.this.scheduleSelf(paramAnonymousRunnable, paramAnonymousLong);
    }
    
    public void unscheduleDrawable(Drawable paramAnonymousDrawable, Runnable paramAnonymousRunnable)
    {
      AnimatedVectorDrawableCompat.this.unscheduleSelf(paramAnonymousRunnable);
    }
  };
  private Context mContext;
  
  AnimatedVectorDrawableCompat()
  {
    this(null, null, null);
  }
  
  private AnimatedVectorDrawableCompat(@Nullable Context paramContext)
  {
    this(paramContext, null, null);
  }
  
  private AnimatedVectorDrawableCompat(@Nullable Context paramContext, @Nullable AnimatedVectorDrawableCompatState paramAnimatedVectorDrawableCompatState, @Nullable Resources paramResources)
  {
    this.mContext = paramContext;
    if (paramAnimatedVectorDrawableCompatState != null)
    {
      this.mAnimatedVectorState = paramAnimatedVectorDrawableCompatState;
      return;
    }
    this.mAnimatedVectorState = new AnimatedVectorDrawableCompatState(paramContext, paramAnimatedVectorDrawableCompatState, this.mCallback, paramResources);
  }
  
  public static void clearAnimationCallbacks(Drawable paramDrawable)
  {
    if ((paramDrawable == null) || (!(paramDrawable instanceof Animatable))) {
      return;
    }
    if (Build.VERSION.SDK_INT >= 24)
    {
      ((AnimatedVectorDrawable)paramDrawable).clearAnimationCallbacks();
      return;
    }
    ((AnimatedVectorDrawableCompat)paramDrawable).clearAnimationCallbacks();
  }
  
  @Nullable
  public static AnimatedVectorDrawableCompat create(@NonNull Context paramContext, @DrawableRes int paramInt)
  {
    if (Build.VERSION.SDK_INT >= 24)
    {
      AnimatedVectorDrawableCompat localAnimatedVectorDrawableCompat1 = new AnimatedVectorDrawableCompat(paramContext);
      localAnimatedVectorDrawableCompat1.mDelegateDrawable = ResourcesCompat.getDrawable(paramContext.getResources(), paramInt, paramContext.getTheme());
      localAnimatedVectorDrawableCompat1.mDelegateDrawable.setCallback(localAnimatedVectorDrawableCompat1.mCallback);
      localAnimatedVectorDrawableCompat1.mCachedConstantStateDelegate = new AnimatedVectorDrawableDelegateState(localAnimatedVectorDrawableCompat1.mDelegateDrawable.getConstantState());
      return localAnimatedVectorDrawableCompat1;
    }
    Resources localResources = paramContext.getResources();
    try
    {
      localXmlResourceParser = localResources.getXml(paramInt);
      localAttributeSet = Xml.asAttributeSet(localXmlResourceParser);
      int i;
      do
      {
        i = localXmlResourceParser.next();
      } while ((i != 2) && (i != 1));
      if (i != 2) {
        throw new XmlPullParserException("No start tag found");
      }
    }
    catch (XmlPullParserException localXmlPullParserException)
    {
      XmlResourceParser localXmlResourceParser;
      AttributeSet localAttributeSet;
      Log.e("AnimatedVDCompat", "parser error", localXmlPullParserException);
      return null;
      AnimatedVectorDrawableCompat localAnimatedVectorDrawableCompat2 = createFromXmlInner(paramContext, paramContext.getResources(), localXmlResourceParser, localAttributeSet, paramContext.getTheme());
      return localAnimatedVectorDrawableCompat2;
    }
    catch (IOException localIOException)
    {
      for (;;)
      {
        Log.e("AnimatedVDCompat", "parser error", localIOException);
      }
    }
  }
  
  public static AnimatedVectorDrawableCompat createFromXmlInner(Context paramContext, Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    AnimatedVectorDrawableCompat localAnimatedVectorDrawableCompat = new AnimatedVectorDrawableCompat(paramContext);
    localAnimatedVectorDrawableCompat.inflate(paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
    return localAnimatedVectorDrawableCompat;
  }
  
  public static void registerAnimationCallback(Drawable paramDrawable, Animatable2Compat.AnimationCallback paramAnimationCallback)
  {
    if ((paramDrawable == null) || (paramAnimationCallback == null)) {}
    while (!(paramDrawable instanceof Animatable)) {
      return;
    }
    if (Build.VERSION.SDK_INT >= 24)
    {
      registerPlatformCallback((AnimatedVectorDrawable)paramDrawable, paramAnimationCallback);
      return;
    }
    ((AnimatedVectorDrawableCompat)paramDrawable).registerAnimationCallback(paramAnimationCallback);
  }
  
  private static void registerPlatformCallback(@NonNull AnimatedVectorDrawable paramAnimatedVectorDrawable, @NonNull Animatable2Compat.AnimationCallback paramAnimationCallback)
  {
    paramAnimatedVectorDrawable.registerAnimationCallback(paramAnimationCallback.getPlatformCallback());
  }
  
  private void removeAnimatorSetListener()
  {
    if (this.mAnimatorListener != null)
    {
      this.mAnimatedVectorState.mAnimatorSet.removeListener(this.mAnimatorListener);
      this.mAnimatorListener = null;
    }
  }
  
  private void setupAnimatorsForTarget(String paramString, Animator paramAnimator)
  {
    paramAnimator.setTarget(this.mAnimatedVectorState.mVectorDrawable.getTargetByName(paramString));
    if (Build.VERSION.SDK_INT < 21) {
      setupColorAnimator(paramAnimator);
    }
    if (this.mAnimatedVectorState.mAnimators == null)
    {
      AnimatedVectorDrawableCompatState.access$002(this.mAnimatedVectorState, new ArrayList());
      this.mAnimatedVectorState.mTargetNameMap = new ArrayMap();
    }
    this.mAnimatedVectorState.mAnimators.add(paramAnimator);
    this.mAnimatedVectorState.mTargetNameMap.put(paramAnimator, paramString);
  }
  
  private void setupColorAnimator(Animator paramAnimator)
  {
    if ((paramAnimator instanceof AnimatorSet))
    {
      ArrayList localArrayList = ((AnimatorSet)paramAnimator).getChildAnimations();
      if (localArrayList != null) {
        for (int i = 0; i < localArrayList.size(); i++) {
          setupColorAnimator((Animator)localArrayList.get(i));
        }
      }
    }
    if ((paramAnimator instanceof ObjectAnimator))
    {
      ObjectAnimator localObjectAnimator = (ObjectAnimator)paramAnimator;
      String str = localObjectAnimator.getPropertyName();
      if (("fillColor".equals(str)) || ("strokeColor".equals(str)))
      {
        if (this.mArgbEvaluator == null) {
          this.mArgbEvaluator = new ArgbEvaluator();
        }
        localObjectAnimator.setEvaluator(this.mArgbEvaluator);
      }
    }
  }
  
  public static boolean unregisterAnimationCallback(Drawable paramDrawable, Animatable2Compat.AnimationCallback paramAnimationCallback)
  {
    if ((paramDrawable == null) || (paramAnimationCallback == null)) {}
    while (!(paramDrawable instanceof Animatable)) {
      return false;
    }
    if (Build.VERSION.SDK_INT >= 24) {
      return unregisterPlatformCallback((AnimatedVectorDrawable)paramDrawable, paramAnimationCallback);
    }
    return ((AnimatedVectorDrawableCompat)paramDrawable).unregisterAnimationCallback(paramAnimationCallback);
  }
  
  private static boolean unregisterPlatformCallback(AnimatedVectorDrawable paramAnimatedVectorDrawable, Animatable2Compat.AnimationCallback paramAnimationCallback)
  {
    return paramAnimatedVectorDrawable.unregisterAnimationCallback(paramAnimationCallback.getPlatformCallback());
  }
  
  public void applyTheme(Resources.Theme paramTheme)
  {
    if (this.mDelegateDrawable != null) {
      DrawableCompat.applyTheme(this.mDelegateDrawable, paramTheme);
    }
  }
  
  public boolean canApplyTheme()
  {
    if (this.mDelegateDrawable != null) {
      return DrawableCompat.canApplyTheme(this.mDelegateDrawable);
    }
    return false;
  }
  
  public void clearAnimationCallbacks()
  {
    if (this.mDelegateDrawable != null) {
      ((AnimatedVectorDrawable)this.mDelegateDrawable).clearAnimationCallbacks();
    }
    do
    {
      return;
      removeAnimatorSetListener();
    } while (this.mAnimationCallbacks == null);
    this.mAnimationCallbacks.clear();
  }
  
  public void draw(Canvas paramCanvas)
  {
    if (this.mDelegateDrawable != null) {
      this.mDelegateDrawable.draw(paramCanvas);
    }
    do
    {
      return;
      this.mAnimatedVectorState.mVectorDrawable.draw(paramCanvas);
    } while (!this.mAnimatedVectorState.mAnimatorSet.isStarted());
    invalidateSelf();
  }
  
  public int getAlpha()
  {
    if (this.mDelegateDrawable != null) {
      return DrawableCompat.getAlpha(this.mDelegateDrawable);
    }
    return this.mAnimatedVectorState.mVectorDrawable.getAlpha();
  }
  
  public int getChangingConfigurations()
  {
    if (this.mDelegateDrawable != null) {
      return this.mDelegateDrawable.getChangingConfigurations();
    }
    return super.getChangingConfigurations() | this.mAnimatedVectorState.mChangingConfigurations;
  }
  
  public Drawable.ConstantState getConstantState()
  {
    if (this.mDelegateDrawable != null) {
      return new AnimatedVectorDrawableDelegateState(this.mDelegateDrawable.getConstantState());
    }
    return null;
  }
  
  public int getIntrinsicHeight()
  {
    if (this.mDelegateDrawable != null) {
      return this.mDelegateDrawable.getIntrinsicHeight();
    }
    return this.mAnimatedVectorState.mVectorDrawable.getIntrinsicHeight();
  }
  
  public int getIntrinsicWidth()
  {
    if (this.mDelegateDrawable != null) {
      return this.mDelegateDrawable.getIntrinsicWidth();
    }
    return this.mAnimatedVectorState.mVectorDrawable.getIntrinsicWidth();
  }
  
  public int getOpacity()
  {
    if (this.mDelegateDrawable != null) {
      return this.mDelegateDrawable.getOpacity();
    }
    return this.mAnimatedVectorState.mVectorDrawable.getOpacity();
  }
  
  public void inflate(Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet)
    throws XmlPullParserException, IOException
  {
    inflate(paramResources, paramXmlPullParser, paramAttributeSet, null);
  }
  
  public void inflate(Resources paramResources, XmlPullParser paramXmlPullParser, AttributeSet paramAttributeSet, Resources.Theme paramTheme)
    throws XmlPullParserException, IOException
  {
    if (this.mDelegateDrawable != null)
    {
      DrawableCompat.inflate(this.mDelegateDrawable, paramResources, paramXmlPullParser, paramAttributeSet, paramTheme);
      return;
    }
    int i = paramXmlPullParser.getEventType();
    int j = 1 + paramXmlPullParser.getDepth();
    if ((i != 1) && ((paramXmlPullParser.getDepth() >= j) || (i != 3)))
    {
      String str1;
      if (i == 2)
      {
        str1 = paramXmlPullParser.getName();
        if (!"animated-vector".equals(str1)) {
          break label182;
        }
        TypedArray localTypedArray2 = VectorDrawableCommon.obtainAttributes(paramResources, paramTheme, paramAttributeSet, AndroidResources.styleable_AnimatedVectorDrawable);
        int m = localTypedArray2.getResourceId(0, 0);
        if (m != 0)
        {
          VectorDrawableCompat localVectorDrawableCompat = VectorDrawableCompat.create(paramResources, m, paramTheme);
          localVectorDrawableCompat.setAllowCaching(false);
          localVectorDrawableCompat.setCallback(this.mCallback);
          if (this.mAnimatedVectorState.mVectorDrawable != null) {
            this.mAnimatedVectorState.mVectorDrawable.setCallback(null);
          }
          this.mAnimatedVectorState.mVectorDrawable = localVectorDrawableCompat;
        }
        localTypedArray2.recycle();
      }
      label182:
      TypedArray localTypedArray1;
      for (;;)
      {
        i = paramXmlPullParser.next();
        break;
        if ("target".equals(str1))
        {
          localTypedArray1 = paramResources.obtainAttributes(paramAttributeSet, AndroidResources.styleable_AnimatedVectorDrawableTarget);
          String str2 = localTypedArray1.getString(0);
          int k = localTypedArray1.getResourceId(1, 0);
          if (k != 0)
          {
            if (this.mContext == null) {
              break label254;
            }
            setupAnimatorsForTarget(str2, AnimatorInflater.loadAnimator(this.mContext, k));
          }
          localTypedArray1.recycle();
        }
      }
      label254:
      localTypedArray1.recycle();
      throw new IllegalStateException("Context can't be null when inflating animators");
    }
    this.mAnimatedVectorState.setupAnimatorSet();
  }
  
  public boolean isAutoMirrored()
  {
    if (this.mDelegateDrawable != null) {
      return DrawableCompat.isAutoMirrored(this.mDelegateDrawable);
    }
    return this.mAnimatedVectorState.mVectorDrawable.isAutoMirrored();
  }
  
  public boolean isRunning()
  {
    if (this.mDelegateDrawable != null) {
      return ((AnimatedVectorDrawable)this.mDelegateDrawable).isRunning();
    }
    return this.mAnimatedVectorState.mAnimatorSet.isRunning();
  }
  
  public boolean isStateful()
  {
    if (this.mDelegateDrawable != null) {
      return this.mDelegateDrawable.isStateful();
    }
    return this.mAnimatedVectorState.mVectorDrawable.isStateful();
  }
  
  public Drawable mutate()
  {
    if (this.mDelegateDrawable != null) {
      this.mDelegateDrawable.mutate();
    }
    return this;
  }
  
  protected void onBoundsChange(Rect paramRect)
  {
    if (this.mDelegateDrawable != null)
    {
      this.mDelegateDrawable.setBounds(paramRect);
      return;
    }
    this.mAnimatedVectorState.mVectorDrawable.setBounds(paramRect);
  }
  
  protected boolean onLevelChange(int paramInt)
  {
    if (this.mDelegateDrawable != null) {
      return this.mDelegateDrawable.setLevel(paramInt);
    }
    return this.mAnimatedVectorState.mVectorDrawable.setLevel(paramInt);
  }
  
  protected boolean onStateChange(int[] paramArrayOfInt)
  {
    if (this.mDelegateDrawable != null) {
      return this.mDelegateDrawable.setState(paramArrayOfInt);
    }
    return this.mAnimatedVectorState.mVectorDrawable.setState(paramArrayOfInt);
  }
  
  public void registerAnimationCallback(@NonNull Animatable2Compat.AnimationCallback paramAnimationCallback)
  {
    if (this.mDelegateDrawable != null) {
      registerPlatformCallback((AnimatedVectorDrawable)this.mDelegateDrawable, paramAnimationCallback);
    }
    do
    {
      do
      {
        return;
      } while (paramAnimationCallback == null);
      if (this.mAnimationCallbacks == null) {
        this.mAnimationCallbacks = new ArrayList();
      }
    } while (this.mAnimationCallbacks.contains(paramAnimationCallback));
    this.mAnimationCallbacks.add(paramAnimationCallback);
    if (this.mAnimatorListener == null) {
      this.mAnimatorListener = new AnimatorListenerAdapter()
      {
        public void onAnimationEnd(Animator paramAnonymousAnimator)
        {
          ArrayList localArrayList = new ArrayList(AnimatedVectorDrawableCompat.this.mAnimationCallbacks);
          int i = localArrayList.size();
          for (int j = 0; j < i; j++) {
            ((Animatable2Compat.AnimationCallback)localArrayList.get(j)).onAnimationEnd(AnimatedVectorDrawableCompat.this);
          }
        }
        
        public void onAnimationStart(Animator paramAnonymousAnimator)
        {
          ArrayList localArrayList = new ArrayList(AnimatedVectorDrawableCompat.this.mAnimationCallbacks);
          int i = localArrayList.size();
          for (int j = 0; j < i; j++) {
            ((Animatable2Compat.AnimationCallback)localArrayList.get(j)).onAnimationStart(AnimatedVectorDrawableCompat.this);
          }
        }
      };
    }
    this.mAnimatedVectorState.mAnimatorSet.addListener(this.mAnimatorListener);
  }
  
  public void setAlpha(int paramInt)
  {
    if (this.mDelegateDrawable != null)
    {
      this.mDelegateDrawable.setAlpha(paramInt);
      return;
    }
    this.mAnimatedVectorState.mVectorDrawable.setAlpha(paramInt);
  }
  
  public void setAutoMirrored(boolean paramBoolean)
  {
    if (this.mDelegateDrawable != null)
    {
      this.mDelegateDrawable.setAutoMirrored(paramBoolean);
      return;
    }
    this.mAnimatedVectorState.mVectorDrawable.setAutoMirrored(paramBoolean);
  }
  
  public void setColorFilter(ColorFilter paramColorFilter)
  {
    if (this.mDelegateDrawable != null)
    {
      this.mDelegateDrawable.setColorFilter(paramColorFilter);
      return;
    }
    this.mAnimatedVectorState.mVectorDrawable.setColorFilter(paramColorFilter);
  }
  
  public void setTint(int paramInt)
  {
    if (this.mDelegateDrawable != null)
    {
      DrawableCompat.setTint(this.mDelegateDrawable, paramInt);
      return;
    }
    this.mAnimatedVectorState.mVectorDrawable.setTint(paramInt);
  }
  
  public void setTintList(ColorStateList paramColorStateList)
  {
    if (this.mDelegateDrawable != null)
    {
      DrawableCompat.setTintList(this.mDelegateDrawable, paramColorStateList);
      return;
    }
    this.mAnimatedVectorState.mVectorDrawable.setTintList(paramColorStateList);
  }
  
  public void setTintMode(PorterDuff.Mode paramMode)
  {
    if (this.mDelegateDrawable != null)
    {
      DrawableCompat.setTintMode(this.mDelegateDrawable, paramMode);
      return;
    }
    this.mAnimatedVectorState.mVectorDrawable.setTintMode(paramMode);
  }
  
  public boolean setVisible(boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.mDelegateDrawable != null) {
      return this.mDelegateDrawable.setVisible(paramBoolean1, paramBoolean2);
    }
    this.mAnimatedVectorState.mVectorDrawable.setVisible(paramBoolean1, paramBoolean2);
    return super.setVisible(paramBoolean1, paramBoolean2);
  }
  
  public void start()
  {
    if (this.mDelegateDrawable != null) {
      ((AnimatedVectorDrawable)this.mDelegateDrawable).start();
    }
    while (this.mAnimatedVectorState.mAnimatorSet.isStarted()) {
      return;
    }
    this.mAnimatedVectorState.mAnimatorSet.start();
    invalidateSelf();
  }
  
  public void stop()
  {
    if (this.mDelegateDrawable != null)
    {
      ((AnimatedVectorDrawable)this.mDelegateDrawable).stop();
      return;
    }
    this.mAnimatedVectorState.mAnimatorSet.end();
  }
  
  public boolean unregisterAnimationCallback(@NonNull Animatable2Compat.AnimationCallback paramAnimationCallback)
  {
    if (this.mDelegateDrawable != null) {
      unregisterPlatformCallback((AnimatedVectorDrawable)this.mDelegateDrawable, paramAnimationCallback);
    }
    boolean bool;
    if ((this.mAnimationCallbacks == null) || (paramAnimationCallback == null)) {
      bool = false;
    }
    do
    {
      return bool;
      bool = this.mAnimationCallbacks.remove(paramAnimationCallback);
    } while (this.mAnimationCallbacks.size() != 0);
    removeAnimatorSetListener();
    return bool;
  }
  
  private static class AnimatedVectorDrawableCompatState
    extends Drawable.ConstantState
  {
    AnimatorSet mAnimatorSet;
    private ArrayList<Animator> mAnimators;
    int mChangingConfigurations;
    ArrayMap<Animator, String> mTargetNameMap;
    VectorDrawableCompat mVectorDrawable;
    
    public AnimatedVectorDrawableCompatState(Context paramContext, AnimatedVectorDrawableCompatState paramAnimatedVectorDrawableCompatState, Drawable.Callback paramCallback, Resources paramResources)
    {
      if (paramAnimatedVectorDrawableCompatState != null)
      {
        this.mChangingConfigurations = paramAnimatedVectorDrawableCompatState.mChangingConfigurations;
        Drawable.ConstantState localConstantState;
        if (paramAnimatedVectorDrawableCompatState.mVectorDrawable != null)
        {
          localConstantState = paramAnimatedVectorDrawableCompatState.mVectorDrawable.getConstantState();
          if (paramResources == null) {
            break label224;
          }
        }
        label224:
        for (this.mVectorDrawable = ((VectorDrawableCompat)localConstantState.newDrawable(paramResources));; this.mVectorDrawable = ((VectorDrawableCompat)localConstantState.newDrawable()))
        {
          this.mVectorDrawable = ((VectorDrawableCompat)this.mVectorDrawable.mutate());
          this.mVectorDrawable.setCallback(paramCallback);
          this.mVectorDrawable.setBounds(paramAnimatedVectorDrawableCompatState.mVectorDrawable.getBounds());
          this.mVectorDrawable.setAllowCaching(false);
          if (paramAnimatedVectorDrawableCompatState.mAnimators == null) {
            return;
          }
          int i = paramAnimatedVectorDrawableCompatState.mAnimators.size();
          this.mAnimators = new ArrayList(i);
          this.mTargetNameMap = new ArrayMap(i);
          for (int j = 0; j < i; j++)
          {
            Animator localAnimator1 = (Animator)paramAnimatedVectorDrawableCompatState.mAnimators.get(j);
            Animator localAnimator2 = localAnimator1.clone();
            String str = (String)paramAnimatedVectorDrawableCompatState.mTargetNameMap.get(localAnimator1);
            localAnimator2.setTarget(this.mVectorDrawable.getTargetByName(str));
            this.mAnimators.add(localAnimator2);
            this.mTargetNameMap.put(localAnimator2, str);
          }
        }
        setupAnimatorSet();
      }
    }
    
    public int getChangingConfigurations()
    {
      return this.mChangingConfigurations;
    }
    
    public Drawable newDrawable()
    {
      throw new IllegalStateException("No constant state support for SDK < 24.");
    }
    
    public Drawable newDrawable(Resources paramResources)
    {
      throw new IllegalStateException("No constant state support for SDK < 24.");
    }
    
    public void setupAnimatorSet()
    {
      if (this.mAnimatorSet == null) {
        this.mAnimatorSet = new AnimatorSet();
      }
      this.mAnimatorSet.playTogether(this.mAnimators);
    }
  }
  
  private static class AnimatedVectorDrawableDelegateState
    extends Drawable.ConstantState
  {
    private final Drawable.ConstantState mDelegateState;
    
    public AnimatedVectorDrawableDelegateState(Drawable.ConstantState paramConstantState)
    {
      this.mDelegateState = paramConstantState;
    }
    
    public boolean canApplyTheme()
    {
      return this.mDelegateState.canApplyTheme();
    }
    
    public int getChangingConfigurations()
    {
      return this.mDelegateState.getChangingConfigurations();
    }
    
    public Drawable newDrawable()
    {
      AnimatedVectorDrawableCompat localAnimatedVectorDrawableCompat = new AnimatedVectorDrawableCompat();
      localAnimatedVectorDrawableCompat.mDelegateDrawable = this.mDelegateState.newDrawable();
      localAnimatedVectorDrawableCompat.mDelegateDrawable.setCallback(localAnimatedVectorDrawableCompat.mCallback);
      return localAnimatedVectorDrawableCompat;
    }
    
    public Drawable newDrawable(Resources paramResources)
    {
      AnimatedVectorDrawableCompat localAnimatedVectorDrawableCompat = new AnimatedVectorDrawableCompat();
      localAnimatedVectorDrawableCompat.mDelegateDrawable = this.mDelegateState.newDrawable(paramResources);
      localAnimatedVectorDrawableCompat.mDelegateDrawable.setCallback(localAnimatedVectorDrawableCompat.mCallback);
      return localAnimatedVectorDrawableCompat;
    }
    
    public Drawable newDrawable(Resources paramResources, Resources.Theme paramTheme)
    {
      AnimatedVectorDrawableCompat localAnimatedVectorDrawableCompat = new AnimatedVectorDrawableCompat();
      localAnimatedVectorDrawableCompat.mDelegateDrawable = this.mDelegateState.newDrawable(paramResources, paramTheme);
      localAnimatedVectorDrawableCompat.mDelegateDrawable.setCallback(localAnimatedVectorDrawableCompat.mCallback);
      return localAnimatedVectorDrawableCompat;
    }
  }
}
