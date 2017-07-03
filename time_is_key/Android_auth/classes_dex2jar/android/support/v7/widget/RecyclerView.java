package android.support.v7.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.TypedArray;
import android.database.Observable;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.SystemClock;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.VisibleForTesting;
import android.support.v4.os.ParcelableCompat;
import android.support.v4.os.ParcelableCompatCreatorCallbacks;
import android.support.v4.os.TraceCompat;
import android.support.v4.view.AbsSavedState;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.ScrollingView;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.CollectionInfoCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat.CollectionItemInfoCompat;
import android.support.v4.view.accessibility.AccessibilityRecordCompat;
import android.support.v4.widget.EdgeEffectCompat;
import android.support.v4.widget.ScrollerCompat;
import android.support.v7.recyclerview.R.styleable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Display;
import android.view.FocusFinder;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.Interpolator;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecyclerView
  extends ViewGroup
  implements ScrollingView, NestedScrollingChild
{
  static final boolean ALLOW_SIZE_IN_UNSPECIFIED_SPEC = false;
  private static final boolean ALLOW_THREAD_GAP_WORK = false;
  private static final int[] CLIP_TO_PADDING_ATTR;
  static final boolean DEBUG = false;
  static final boolean DISPATCH_TEMP_DETACH = false;
  private static final boolean FORCE_ABS_FOCUS_SEARCH_DIRECTION = false;
  static final boolean FORCE_INVALIDATE_DISPLAY_LIST = false;
  static final long FOREVER_NS = Long.MAX_VALUE;
  public static final int HORIZONTAL = 0;
  private static final boolean IGNORE_DETACHED_FOCUSED_CHILD = false;
  private static final int INVALID_POINTER = -1;
  public static final int INVALID_TYPE = -1;
  private static final Class<?>[] LAYOUT_MANAGER_CONSTRUCTOR_SIGNATURE;
  static final int MAX_SCROLL_DURATION = 2000;
  private static final int[] NESTED_SCROLLING_ATTRS = { 16843830 };
  public static final long NO_ID = -1L;
  public static final int NO_POSITION = -1;
  static final boolean POST_UPDATES_ON_ANIMATION = false;
  public static final int SCROLL_STATE_DRAGGING = 1;
  public static final int SCROLL_STATE_IDLE = 0;
  public static final int SCROLL_STATE_SETTLING = 2;
  static final String TAG = "RecyclerView";
  public static final int TOUCH_SLOP_DEFAULT = 0;
  public static final int TOUCH_SLOP_PAGING = 1;
  static final String TRACE_BIND_VIEW_TAG = "RV OnBindView";
  static final String TRACE_CREATE_VIEW_TAG = "RV CreateView";
  private static final String TRACE_HANDLE_ADAPTER_UPDATES_TAG = "RV PartialInvalidate";
  static final String TRACE_NESTED_PREFETCH_TAG = "RV Nested Prefetch";
  private static final String TRACE_ON_DATA_SET_CHANGE_LAYOUT_TAG = "RV FullInvalidate";
  private static final String TRACE_ON_LAYOUT_TAG = "RV OnLayout";
  static final String TRACE_PREFETCH_TAG = "RV Prefetch";
  static final String TRACE_SCROLL_TAG = "RV Scroll";
  static final boolean VERBOSE_TRACING = false;
  public static final int VERTICAL = 1;
  static final Interpolator sQuinticInterpolator;
  RecyclerViewAccessibilityDelegate mAccessibilityDelegate;
  private final AccessibilityManager mAccessibilityManager;
  private OnItemTouchListener mActiveOnItemTouchListener;
  Adapter mAdapter;
  AdapterHelper mAdapterHelper;
  boolean mAdapterUpdateDuringMeasure;
  private EdgeEffectCompat mBottomGlow;
  private ChildDrawingOrderCallback mChildDrawingOrderCallback;
  ChildHelper mChildHelper;
  boolean mClipToPadding;
  boolean mDataSetHasChangedAfterLayout = false;
  private int mDispatchScrollCounter = 0;
  private int mEatRequestLayout = 0;
  private int mEatenAccessibilityChangeFlags;
  @VisibleForTesting
  boolean mFirstLayoutComplete;
  GapWorker mGapWorker;
  boolean mHasFixedSize;
  private boolean mIgnoreMotionEventTillDown;
  private int mInitialTouchX;
  private int mInitialTouchY;
  boolean mIsAttached;
  ItemAnimator mItemAnimator = new DefaultItemAnimator();
  private RecyclerView.ItemAnimator.ItemAnimatorListener mItemAnimatorListener;
  private Runnable mItemAnimatorRunner;
  final ArrayList<ItemDecoration> mItemDecorations = new ArrayList();
  boolean mItemsAddedOrRemoved;
  boolean mItemsChanged;
  private int mLastTouchX;
  private int mLastTouchY;
  @VisibleForTesting
  LayoutManager mLayout;
  boolean mLayoutFrozen;
  private int mLayoutOrScrollCounter = 0;
  boolean mLayoutRequestEaten;
  private EdgeEffectCompat mLeftGlow;
  private final int mMaxFlingVelocity;
  private final int mMinFlingVelocity;
  private final int[] mMinMaxLayoutPositions;
  private final int[] mNestedOffsets;
  private final RecyclerViewDataObserver mObserver = new RecyclerViewDataObserver();
  private List<OnChildAttachStateChangeListener> mOnChildAttachStateListeners;
  private OnFlingListener mOnFlingListener;
  private final ArrayList<OnItemTouchListener> mOnItemTouchListeners = new ArrayList();
  @VisibleForTesting
  final List<ViewHolder> mPendingAccessibilityImportanceChange;
  private SavedState mPendingSavedState;
  boolean mPostedAnimatorRunner;
  GapWorker.LayoutPrefetchRegistryImpl mPrefetchRegistry;
  private boolean mPreserveFocusAfterLayout = true;
  final Recycler mRecycler = new Recycler();
  RecyclerListener mRecyclerListener;
  private EdgeEffectCompat mRightGlow;
  private final int[] mScrollConsumed;
  private float mScrollFactor = Float.MIN_VALUE;
  private OnScrollListener mScrollListener;
  private List<OnScrollListener> mScrollListeners;
  private final int[] mScrollOffset;
  private int mScrollPointerId = -1;
  private int mScrollState = 0;
  private NestedScrollingChildHelper mScrollingChildHelper;
  final State mState;
  final Rect mTempRect = new Rect();
  private final Rect mTempRect2 = new Rect();
  final RectF mTempRectF = new RectF();
  private EdgeEffectCompat mTopGlow;
  private int mTouchSlop;
  final Runnable mUpdateChildViewsRunnable = new Runnable()
  {
    public void run()
    {
      if ((!RecyclerView.this.mFirstLayoutComplete) || (RecyclerView.this.isLayoutRequested())) {
        return;
      }
      if (!RecyclerView.this.mIsAttached)
      {
        RecyclerView.this.requestLayout();
        return;
      }
      if (RecyclerView.this.mLayoutFrozen)
      {
        RecyclerView.this.mLayoutRequestEaten = true;
        return;
      }
      RecyclerView.this.consumePendingUpdateOperations();
    }
  };
  private VelocityTracker mVelocityTracker;
  final ViewFlinger mViewFlinger = new ViewFlinger();
  private final ViewInfoStore.ProcessCallback mViewInfoProcessCallback;
  final ViewInfoStore mViewInfoStore = new ViewInfoStore();
  
  static
  {
    CLIP_TO_PADDING_ATTR = new int[] { 16842987 };
    boolean bool1;
    boolean bool2;
    label62:
    boolean bool3;
    label76:
    boolean bool4;
    label90:
    boolean bool5;
    if ((Build.VERSION.SDK_INT == 18) || (Build.VERSION.SDK_INT == 19) || (Build.VERSION.SDK_INT == 20))
    {
      bool1 = true;
      FORCE_INVALIDATE_DISPLAY_LIST = bool1;
      if (Build.VERSION.SDK_INT < 23) {
        break label179;
      }
      bool2 = true;
      ALLOW_SIZE_IN_UNSPECIFIED_SPEC = bool2;
      if (Build.VERSION.SDK_INT < 16) {
        break label184;
      }
      bool3 = true;
      POST_UPDATES_ON_ANIMATION = bool3;
      if (Build.VERSION.SDK_INT < 21) {
        break label189;
      }
      bool4 = true;
      ALLOW_THREAD_GAP_WORK = bool4;
      if (Build.VERSION.SDK_INT > 15) {
        break label194;
      }
      bool5 = true;
      label105:
      FORCE_ABS_FOCUS_SEARCH_DIRECTION = bool5;
      if (Build.VERSION.SDK_INT > 15) {
        break label200;
      }
    }
    label179:
    label184:
    label189:
    label194:
    label200:
    for (boolean bool6 = true;; bool6 = false)
    {
      IGNORE_DETACHED_FOCUSED_CHILD = bool6;
      Class[] arrayOfClass = new Class[4];
      arrayOfClass[0] = Context.class;
      arrayOfClass[1] = AttributeSet.class;
      arrayOfClass[2] = Integer.TYPE;
      arrayOfClass[3] = Integer.TYPE;
      LAYOUT_MANAGER_CONSTRUCTOR_SIGNATURE = arrayOfClass;
      sQuinticInterpolator = new Interpolator()
      {
        public float getInterpolation(float paramAnonymousFloat)
        {
          float f = paramAnonymousFloat - 1.0F;
          return 1.0F + f * (f * (f * (f * f)));
        }
      };
      return;
      bool1 = false;
      break;
      bool2 = false;
      break label62;
      bool3 = false;
      break label76;
      bool4 = false;
      break label90;
      bool5 = false;
      break label105;
    }
  }
  
  public RecyclerView(Context paramContext)
  {
    this(paramContext, null);
  }
  
  public RecyclerView(Context paramContext, @Nullable AttributeSet paramAttributeSet)
  {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public RecyclerView(Context paramContext, @Nullable AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
    GapWorker.LayoutPrefetchRegistryImpl localLayoutPrefetchRegistryImpl;
    label328:
    boolean bool1;
    label382:
    boolean bool2;
    if (ALLOW_THREAD_GAP_WORK)
    {
      localLayoutPrefetchRegistryImpl = new GapWorker.LayoutPrefetchRegistryImpl();
      this.mPrefetchRegistry = localLayoutPrefetchRegistryImpl;
      this.mState = new State();
      this.mItemsAddedOrRemoved = false;
      this.mItemsChanged = false;
      this.mItemAnimatorListener = new ItemAnimatorRestoreListener();
      this.mPostedAnimatorRunner = false;
      this.mMinMaxLayoutPositions = new int[2];
      this.mScrollOffset = new int[2];
      this.mScrollConsumed = new int[2];
      this.mNestedOffsets = new int[2];
      this.mPendingAccessibilityImportanceChange = new ArrayList();
      this.mItemAnimatorRunner = new Runnable()
      {
        public void run()
        {
          if (RecyclerView.this.mItemAnimator != null) {
            RecyclerView.this.mItemAnimator.runPendingAnimations();
          }
          RecyclerView.this.mPostedAnimatorRunner = false;
        }
      };
      this.mViewInfoProcessCallback = new ViewInfoStore.ProcessCallback()
      {
        public void processAppeared(RecyclerView.ViewHolder paramAnonymousViewHolder, RecyclerView.ItemAnimator.ItemHolderInfo paramAnonymousItemHolderInfo1, RecyclerView.ItemAnimator.ItemHolderInfo paramAnonymousItemHolderInfo2)
        {
          RecyclerView.this.animateAppearance(paramAnonymousViewHolder, paramAnonymousItemHolderInfo1, paramAnonymousItemHolderInfo2);
        }
        
        public void processDisappeared(RecyclerView.ViewHolder paramAnonymousViewHolder, @NonNull RecyclerView.ItemAnimator.ItemHolderInfo paramAnonymousItemHolderInfo1, @Nullable RecyclerView.ItemAnimator.ItemHolderInfo paramAnonymousItemHolderInfo2)
        {
          RecyclerView.this.mRecycler.unscrapView(paramAnonymousViewHolder);
          RecyclerView.this.animateDisappearance(paramAnonymousViewHolder, paramAnonymousItemHolderInfo1, paramAnonymousItemHolderInfo2);
        }
        
        public void processPersistent(RecyclerView.ViewHolder paramAnonymousViewHolder, @NonNull RecyclerView.ItemAnimator.ItemHolderInfo paramAnonymousItemHolderInfo1, @NonNull RecyclerView.ItemAnimator.ItemHolderInfo paramAnonymousItemHolderInfo2)
        {
          paramAnonymousViewHolder.setIsRecyclable(false);
          if (RecyclerView.this.mDataSetHasChangedAfterLayout) {
            if (RecyclerView.this.mItemAnimator.animateChange(paramAnonymousViewHolder, paramAnonymousViewHolder, paramAnonymousItemHolderInfo1, paramAnonymousItemHolderInfo2)) {
              RecyclerView.this.postAnimationRunner();
            }
          }
          while (!RecyclerView.this.mItemAnimator.animatePersistence(paramAnonymousViewHolder, paramAnonymousItemHolderInfo1, paramAnonymousItemHolderInfo2)) {
            return;
          }
          RecyclerView.this.postAnimationRunner();
        }
        
        public void unused(RecyclerView.ViewHolder paramAnonymousViewHolder)
        {
          RecyclerView.this.mLayout.removeAndRecycleView(paramAnonymousViewHolder.itemView, RecyclerView.this.mRecycler);
        }
      };
      if (paramAttributeSet == null) {
        break label559;
      }
      TypedArray localTypedArray3 = paramContext.obtainStyledAttributes(paramAttributeSet, CLIP_TO_PADDING_ATTR, paramInt, 0);
      this.mClipToPadding = localTypedArray3.getBoolean(0, true);
      localTypedArray3.recycle();
      setScrollContainer(true);
      setFocusableInTouchMode(true);
      ViewConfiguration localViewConfiguration = ViewConfiguration.get(paramContext);
      this.mTouchSlop = localViewConfiguration.getScaledTouchSlop();
      this.mMinFlingVelocity = localViewConfiguration.getScaledMinimumFlingVelocity();
      this.mMaxFlingVelocity = localViewConfiguration.getScaledMaximumFlingVelocity();
      if (getOverScrollMode() != 2) {
        break label567;
      }
      bool1 = true;
      setWillNotDraw(bool1);
      this.mItemAnimator.setListener(this.mItemAnimatorListener);
      initAdapterManager();
      initChildrenHelper();
      if (ViewCompat.getImportantForAccessibility(this) == 0) {
        ViewCompat.setImportantForAccessibility(this, 1);
      }
      this.mAccessibilityManager = ((AccessibilityManager)getContext().getSystemService("accessibility"));
      setAccessibilityDelegateCompat(new RecyclerViewAccessibilityDelegate(this));
      bool2 = true;
      if (paramAttributeSet == null) {
        break label573;
      }
      TypedArray localTypedArray1 = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.RecyclerView, paramInt, 0);
      String str = localTypedArray1.getString(R.styleable.RecyclerView_layoutManager);
      if (localTypedArray1.getInt(R.styleable.RecyclerView_android_descendantFocusability, -1) == -1) {
        setDescendantFocusability(262144);
      }
      localTypedArray1.recycle();
      createLayoutManager(paramContext, str, paramAttributeSet, paramInt, 0);
      if (Build.VERSION.SDK_INT >= 21)
      {
        TypedArray localTypedArray2 = paramContext.obtainStyledAttributes(paramAttributeSet, NESTED_SCROLLING_ATTRS, paramInt, 0);
        bool2 = localTypedArray2.getBoolean(0, true);
        localTypedArray2.recycle();
      }
    }
    for (;;)
    {
      setNestedScrollingEnabled(bool2);
      return;
      localLayoutPrefetchRegistryImpl = null;
      break;
      label559:
      this.mClipToPadding = true;
      break label328;
      label567:
      bool1 = false;
      break label382;
      label573:
      setDescendantFocusability(262144);
    }
  }
  
  private void addAnimatingView(ViewHolder paramViewHolder)
  {
    View localView = paramViewHolder.itemView;
    if (localView.getParent() == this) {}
    for (int i = 1;; i = 0)
    {
      this.mRecycler.unscrapView(getChildViewHolder(localView));
      if (!paramViewHolder.isTmpDetached()) {
        break;
      }
      this.mChildHelper.attachViewToParent(localView, -1, localView.getLayoutParams(), true);
      return;
    }
    if (i == 0)
    {
      this.mChildHelper.addView(localView, true);
      return;
    }
    this.mChildHelper.hide(localView);
  }
  
  private void animateChange(@NonNull ViewHolder paramViewHolder1, @NonNull ViewHolder paramViewHolder2, @NonNull RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo1, @NonNull RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo2, boolean paramBoolean1, boolean paramBoolean2)
  {
    paramViewHolder1.setIsRecyclable(false);
    if (paramBoolean1) {
      addAnimatingView(paramViewHolder1);
    }
    if (paramViewHolder1 != paramViewHolder2)
    {
      if (paramBoolean2) {
        addAnimatingView(paramViewHolder2);
      }
      paramViewHolder1.mShadowedHolder = paramViewHolder2;
      addAnimatingView(paramViewHolder1);
      this.mRecycler.unscrapView(paramViewHolder1);
      paramViewHolder2.setIsRecyclable(false);
      paramViewHolder2.mShadowingHolder = paramViewHolder1;
    }
    if (this.mItemAnimator.animateChange(paramViewHolder1, paramViewHolder2, paramItemHolderInfo1, paramItemHolderInfo2)) {
      postAnimationRunner();
    }
  }
  
  private void cancelTouch()
  {
    resetTouch();
    setScrollState(0);
  }
  
  static void clearNestedRecyclerViewIfNotNested(@NonNull ViewHolder paramViewHolder)
  {
    View localView;
    if (paramViewHolder.mNestedRecyclerView != null) {
      localView = (View)paramViewHolder.mNestedRecyclerView.get();
    }
    while (localView != null)
    {
      if (localView == paramViewHolder.itemView) {
        return;
      }
      ViewParent localViewParent = localView.getParent();
      if ((localViewParent instanceof View)) {
        localView = (View)localViewParent;
      } else {
        localView = null;
      }
    }
    paramViewHolder.mNestedRecyclerView = null;
  }
  
  private void createLayoutManager(Context paramContext, String paramString, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
  {
    if (paramString != null)
    {
      String str1 = paramString.trim();
      if (str1.length() != 0)
      {
        String str2 = getFullClassName(paramContext, str1);
        try
        {
          if (isInEditMode()) {}
          Class localClass;
          for (Object localObject1 = getClass().getClassLoader();; localObject1 = localClassLoader)
          {
            localClass = ((ClassLoader)localObject1).loadClass(str2).asSubclass(LayoutManager.class);
            try
            {
              localObject2 = localClass.getConstructor(LAYOUT_MANAGER_CONSTRUCTOR_SIGNATURE);
              Object[] arrayOfObject2 = new Object[4];
              arrayOfObject2[0] = paramContext;
              arrayOfObject2[1] = paramAttributeSet;
              arrayOfObject2[2] = Integer.valueOf(paramInt1);
              arrayOfObject2[3] = Integer.valueOf(paramInt2);
              arrayOfObject1 = arrayOfObject2;
            }
            catch (NoSuchMethodException localNoSuchMethodException1)
            {
              try
              {
                ClassLoader localClassLoader;
                Constructor localConstructor = localClass.getConstructor(new Class[0]);
                Object localObject2 = localConstructor;
                Object[] arrayOfObject1 = null;
              }
              catch (NoSuchMethodException localNoSuchMethodException2)
              {
                localNoSuchMethodException2.initCause(localNoSuchMethodException1);
                throw new IllegalStateException(paramAttributeSet.getPositionDescription() + ": Error creating LayoutManager " + str2, localNoSuchMethodException2);
              }
            }
            ((Constructor)localObject2).setAccessible(true);
            setLayoutManager((LayoutManager)((Constructor)localObject2).newInstance(arrayOfObject1));
            return;
            localClassLoader = paramContext.getClassLoader();
          }
          return;
        }
        catch (ClassNotFoundException localClassNotFoundException)
        {
          throw new IllegalStateException(paramAttributeSet.getPositionDescription() + ": Unable to find LayoutManager " + str2, localClassNotFoundException);
        }
        catch (InvocationTargetException localInvocationTargetException)
        {
          throw new IllegalStateException(paramAttributeSet.getPositionDescription() + ": Could not instantiate the LayoutManager: " + str2, localInvocationTargetException);
        }
        catch (InstantiationException localInstantiationException)
        {
          throw new IllegalStateException(paramAttributeSet.getPositionDescription() + ": Could not instantiate the LayoutManager: " + str2, localInstantiationException);
        }
        catch (IllegalAccessException localIllegalAccessException)
        {
          throw new IllegalStateException(paramAttributeSet.getPositionDescription() + ": Cannot access non-public constructor " + str2, localIllegalAccessException);
        }
        catch (ClassCastException localClassCastException)
        {
          throw new IllegalStateException(paramAttributeSet.getPositionDescription() + ": Class is not a LayoutManager " + str2, localClassCastException);
        }
      }
    }
  }
  
  private boolean didChildRangeChange(int paramInt1, int paramInt2)
  {
    findMinMaxChildLayoutPositions(this.mMinMaxLayoutPositions);
    boolean bool;
    if (this.mMinMaxLayoutPositions[0] == paramInt1)
    {
      int i = this.mMinMaxLayoutPositions[1];
      bool = false;
      if (i == paramInt2) {}
    }
    else
    {
      bool = true;
    }
    return bool;
  }
  
  private void dispatchContentChangedIfNecessary()
  {
    int i = this.mEatenAccessibilityChangeFlags;
    this.mEatenAccessibilityChangeFlags = 0;
    if ((i != 0) && (isAccessibilityEnabled()))
    {
      AccessibilityEvent localAccessibilityEvent = AccessibilityEvent.obtain();
      localAccessibilityEvent.setEventType(2048);
      AccessibilityEventCompat.setContentChangeTypes(localAccessibilityEvent, i);
      sendAccessibilityEventUnchecked(localAccessibilityEvent);
    }
  }
  
  private void dispatchLayoutStep1()
  {
    this.mState.assertLayoutStep(1);
    this.mState.mIsMeasuring = false;
    eatRequestLayout();
    this.mViewInfoStore.clear();
    onEnterLayoutOrScroll();
    processAdapterUpdatesAndSetAnimationFlags();
    saveFocusInfo();
    State localState = this.mState;
    boolean bool1;
    int m;
    label136:
    ViewHolder localViewHolder2;
    if ((this.mState.mRunSimpleAnimations) && (this.mItemsChanged))
    {
      bool1 = true;
      localState.mTrackOldChangeHolders = bool1;
      this.mItemsChanged = false;
      this.mItemsAddedOrRemoved = false;
      this.mState.mInPreLayout = this.mState.mRunPredictiveAnimations;
      this.mState.mItemCount = this.mAdapter.getItemCount();
      findMinMaxChildLayoutPositions(this.mMinMaxLayoutPositions);
      if (!this.mState.mRunSimpleAnimations) {
        break label294;
      }
      int k = this.mChildHelper.getChildCount();
      m = 0;
      if (m >= k) {
        break label294;
      }
      localViewHolder2 = getChildViewHolderInt(this.mChildHelper.getChildAt(m));
      if ((!localViewHolder2.shouldIgnore()) && ((!localViewHolder2.isInvalid()) || (this.mAdapter.hasStableIds()))) {
        break label194;
      }
    }
    for (;;)
    {
      m++;
      break label136;
      bool1 = false;
      break;
      label194:
      RecyclerView.ItemAnimator.ItemHolderInfo localItemHolderInfo2 = this.mItemAnimator.recordPreLayoutInformation(this.mState, localViewHolder2, ItemAnimator.buildAdapterChangeFlagsForAnimations(localViewHolder2), localViewHolder2.getUnmodifiedPayloads());
      this.mViewInfoStore.addToPreLayout(localViewHolder2, localItemHolderInfo2);
      if ((this.mState.mTrackOldChangeHolders) && (localViewHolder2.isUpdated()) && (!localViewHolder2.isRemoved()) && (!localViewHolder2.shouldIgnore()) && (!localViewHolder2.isInvalid()))
      {
        long l = getChangedHolderKey(localViewHolder2);
        this.mViewInfoStore.addToOldChangeHolders(l, localViewHolder2);
      }
    }
    label294:
    if (this.mState.mRunPredictiveAnimations)
    {
      saveOldPositions();
      boolean bool2 = this.mState.mStructureChanged;
      this.mState.mStructureChanged = false;
      this.mLayout.onLayoutChildren(this.mRecycler, this.mState);
      this.mState.mStructureChanged = bool2;
      int i = 0;
      if (i < this.mChildHelper.getChildCount())
      {
        ViewHolder localViewHolder1 = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
        if (localViewHolder1.shouldIgnore()) {}
        for (;;)
        {
          i++;
          break;
          if (!this.mViewInfoStore.isInPreLayout(localViewHolder1))
          {
            int j = ItemAnimator.buildAdapterChangeFlagsForAnimations(localViewHolder1);
            boolean bool3 = localViewHolder1.hasAnyOfTheFlags(8192);
            if (!bool3) {
              j |= 0x1000;
            }
            RecyclerView.ItemAnimator.ItemHolderInfo localItemHolderInfo1 = this.mItemAnimator.recordPreLayoutInformation(this.mState, localViewHolder1, j, localViewHolder1.getUnmodifiedPayloads());
            if (bool3) {
              recordAnimationInfoIfBouncedHiddenView(localViewHolder1, localItemHolderInfo1);
            } else {
              this.mViewInfoStore.addToAppearedInPreLayoutHolders(localViewHolder1, localItemHolderInfo1);
            }
          }
        }
      }
      clearOldPositions();
    }
    for (;;)
    {
      onExitLayoutOrScroll();
      resumeRequestLayout(false);
      this.mState.mLayoutStep = 2;
      return;
      clearOldPositions();
    }
  }
  
  private void dispatchLayoutStep2()
  {
    eatRequestLayout();
    onEnterLayoutOrScroll();
    this.mState.assertLayoutStep(6);
    this.mAdapterHelper.consumeUpdatesInOnePass();
    this.mState.mItemCount = this.mAdapter.getItemCount();
    this.mState.mDeletedInvisibleItemCountSincePreviousLayout = 0;
    this.mState.mInPreLayout = false;
    this.mLayout.onLayoutChildren(this.mRecycler, this.mState);
    this.mState.mStructureChanged = false;
    this.mPendingSavedState = null;
    State localState = this.mState;
    if ((this.mState.mRunSimpleAnimations) && (this.mItemAnimator != null)) {}
    for (boolean bool = true;; bool = false)
    {
      localState.mRunSimpleAnimations = bool;
      this.mState.mLayoutStep = 4;
      onExitLayoutOrScroll();
      resumeRequestLayout(false);
      return;
    }
  }
  
  private void dispatchLayoutStep3()
  {
    this.mState.assertLayoutStep(4);
    eatRequestLayout();
    onEnterLayoutOrScroll();
    this.mState.mLayoutStep = 1;
    if (this.mState.mRunSimpleAnimations)
    {
      int i = -1 + this.mChildHelper.getChildCount();
      if (i >= 0)
      {
        ViewHolder localViewHolder1 = getChildViewHolderInt(this.mChildHelper.getChildAt(i));
        if (localViewHolder1.shouldIgnore()) {}
        for (;;)
        {
          i--;
          break;
          long l = getChangedHolderKey(localViewHolder1);
          RecyclerView.ItemAnimator.ItemHolderInfo localItemHolderInfo1 = this.mItemAnimator.recordPostLayoutInformation(this.mState, localViewHolder1);
          ViewHolder localViewHolder2 = this.mViewInfoStore.getFromOldChangeHolders(l);
          if ((localViewHolder2 != null) && (!localViewHolder2.shouldIgnore()))
          {
            boolean bool1 = this.mViewInfoStore.isDisappearing(localViewHolder2);
            boolean bool2 = this.mViewInfoStore.isDisappearing(localViewHolder1);
            if ((bool1) && (localViewHolder2 == localViewHolder1))
            {
              this.mViewInfoStore.addToPostLayout(localViewHolder1, localItemHolderInfo1);
            }
            else
            {
              RecyclerView.ItemAnimator.ItemHolderInfo localItemHolderInfo2 = this.mViewInfoStore.popFromPreLayout(localViewHolder2);
              this.mViewInfoStore.addToPostLayout(localViewHolder1, localItemHolderInfo1);
              RecyclerView.ItemAnimator.ItemHolderInfo localItemHolderInfo3 = this.mViewInfoStore.popFromPostLayout(localViewHolder1);
              if (localItemHolderInfo2 == null) {
                handleMissingPreInfoForChangeError(l, localViewHolder1, localViewHolder2);
              } else {
                animateChange(localViewHolder2, localViewHolder1, localItemHolderInfo2, localItemHolderInfo3, bool1, bool2);
              }
            }
          }
          else
          {
            this.mViewInfoStore.addToPostLayout(localViewHolder1, localItemHolderInfo1);
          }
        }
      }
      this.mViewInfoStore.process(this.mViewInfoProcessCallback);
    }
    this.mLayout.removeAndRecycleScrapInt(this.mRecycler);
    this.mState.mPreviousLayoutItemCount = this.mState.mItemCount;
    this.mDataSetHasChangedAfterLayout = false;
    this.mState.mRunSimpleAnimations = false;
    this.mState.mRunPredictiveAnimations = false;
    this.mLayout.mRequestedSimpleAnimations = false;
    if (this.mRecycler.mChangedScrap != null) {
      this.mRecycler.mChangedScrap.clear();
    }
    if (this.mLayout.mPrefetchMaxObservedInInitialPrefetch)
    {
      this.mLayout.mPrefetchMaxCountObserved = 0;
      this.mLayout.mPrefetchMaxObservedInInitialPrefetch = false;
      this.mRecycler.updateViewCacheSize();
    }
    this.mLayout.onLayoutCompleted(this.mState);
    onExitLayoutOrScroll();
    resumeRequestLayout(false);
    this.mViewInfoStore.clear();
    if (didChildRangeChange(this.mMinMaxLayoutPositions[0], this.mMinMaxLayoutPositions[1])) {
      dispatchOnScrolled(0, 0);
    }
    recoverFocusFromState();
    resetFocusInfo();
  }
  
  private boolean dispatchOnItemTouch(MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getAction();
    int j;
    if (this.mActiveOnItemTouchListener != null)
    {
      if (i == 0) {
        this.mActiveOnItemTouchListener = null;
      }
    }
    else
    {
      if (i == 0) {
        break label110;
      }
      j = this.mOnItemTouchListeners.size();
    }
    for (int k = 0; k < j; k++)
    {
      OnItemTouchListener localOnItemTouchListener = (OnItemTouchListener)this.mOnItemTouchListeners.get(k);
      if (localOnItemTouchListener.onInterceptTouchEvent(this, paramMotionEvent))
      {
        this.mActiveOnItemTouchListener = localOnItemTouchListener;
        do
        {
          return true;
          this.mActiveOnItemTouchListener.onTouchEvent(this, paramMotionEvent);
        } while ((i != 3) && (i != 1));
        this.mActiveOnItemTouchListener = null;
        return true;
      }
    }
    label110:
    return false;
  }
  
  private boolean dispatchOnItemTouchIntercept(MotionEvent paramMotionEvent)
  {
    int i = paramMotionEvent.getAction();
    if ((i == 3) || (i == 0)) {
      this.mActiveOnItemTouchListener = null;
    }
    int j = this.mOnItemTouchListeners.size();
    for (int k = 0; k < j; k++)
    {
      OnItemTouchListener localOnItemTouchListener = (OnItemTouchListener)this.mOnItemTouchListeners.get(k);
      if ((localOnItemTouchListener.onInterceptTouchEvent(this, paramMotionEvent)) && (i != 3))
      {
        this.mActiveOnItemTouchListener = localOnItemTouchListener;
        return true;
      }
    }
    return false;
  }
  
  private void findMinMaxChildLayoutPositions(int[] paramArrayOfInt)
  {
    int i = this.mChildHelper.getChildCount();
    if (i == 0)
    {
      paramArrayOfInt[0] = -1;
      paramArrayOfInt[1] = -1;
      return;
    }
    int j = Integer.MAX_VALUE;
    int k = Integer.MIN_VALUE;
    int m = 0;
    if (m < i)
    {
      ViewHolder localViewHolder = getChildViewHolderInt(this.mChildHelper.getChildAt(m));
      if (localViewHolder.shouldIgnore()) {}
      for (;;)
      {
        m++;
        break;
        int n = localViewHolder.getLayoutPosition();
        if (n < j) {
          j = n;
        }
        if (n > k) {
          k = n;
        }
      }
    }
    paramArrayOfInt[0] = j;
    paramArrayOfInt[1] = k;
  }
  
  @Nullable
  static RecyclerView findNestedRecyclerView(@NonNull View paramView)
  {
    if (!(paramView instanceof ViewGroup)) {
      return null;
    }
    if ((paramView instanceof RecyclerView)) {
      return (RecyclerView)paramView;
    }
    ViewGroup localViewGroup = (ViewGroup)paramView;
    int i = localViewGroup.getChildCount();
    for (int j = 0; j < i; j++)
    {
      RecyclerView localRecyclerView = findNestedRecyclerView(localViewGroup.getChildAt(j));
      if (localRecyclerView != null) {
        return localRecyclerView;
      }
    }
    return null;
  }
  
  @Nullable
  private View findNextViewToFocus()
  {
    int i;
    int j;
    int k;
    label29:
    ViewHolder localViewHolder2;
    if (this.mState.mFocusedItemPosition != -1)
    {
      i = this.mState.mFocusedItemPosition;
      j = this.mState.getItemCount();
      k = i;
      if (k < j)
      {
        localViewHolder2 = findViewHolderForAdapterPosition(k);
        if (localViewHolder2 != null) {
          break label80;
        }
      }
    }
    for (int m = -1 + Math.min(j, i);; m--)
    {
      ViewHolder localViewHolder1;
      if (m >= 0)
      {
        localViewHolder1 = findViewHolderForAdapterPosition(m);
        if (localViewHolder1 != null) {}
      }
      else
      {
        return null;
        i = 0;
        break;
        label80:
        if (localViewHolder2.itemView.hasFocusable()) {
          return localViewHolder2.itemView;
        }
        k++;
        break label29;
      }
      if (localViewHolder1.itemView.hasFocusable()) {
        return localViewHolder1.itemView;
      }
    }
  }
  
  static ViewHolder getChildViewHolderInt(View paramView)
  {
    if (paramView == null) {
      return null;
    }
    return ((LayoutParams)paramView.getLayoutParams()).mViewHolder;
  }
  
  static void getDecoratedBoundsWithMarginsInt(View paramView, Rect paramRect)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    Rect localRect = localLayoutParams.mDecorInsets;
    paramRect.set(paramView.getLeft() - localRect.left - localLayoutParams.leftMargin, paramView.getTop() - localRect.top - localLayoutParams.topMargin, paramView.getRight() + localRect.right + localLayoutParams.rightMargin, paramView.getBottom() + localRect.bottom + localLayoutParams.bottomMargin);
  }
  
  private int getDeepestFocusedViewWithId(View paramView)
  {
    int i = paramView.getId();
    while ((!paramView.isFocused()) && ((paramView instanceof ViewGroup)) && (paramView.hasFocus()))
    {
      paramView = ((ViewGroup)paramView).getFocusedChild();
      if (paramView.getId() != -1) {
        i = paramView.getId();
      }
    }
    return i;
  }
  
  private String getFullClassName(Context paramContext, String paramString)
  {
    if (paramString.charAt(0) == '.') {
      paramString = paramContext.getPackageName() + paramString;
    }
    while (paramString.contains(".")) {
      return paramString;
    }
    return RecyclerView.class.getPackage().getName() + '.' + paramString;
  }
  
  private float getScrollFactor()
  {
    if (this.mScrollFactor == Float.MIN_VALUE)
    {
      TypedValue localTypedValue = new TypedValue();
      if (getContext().getTheme().resolveAttribute(16842829, localTypedValue, true)) {
        this.mScrollFactor = localTypedValue.getDimension(getContext().getResources().getDisplayMetrics());
      }
    }
    else
    {
      return this.mScrollFactor;
    }
    return 0.0F;
  }
  
  private NestedScrollingChildHelper getScrollingChildHelper()
  {
    if (this.mScrollingChildHelper == null) {
      this.mScrollingChildHelper = new NestedScrollingChildHelper(this);
    }
    return this.mScrollingChildHelper;
  }
  
  private void handleMissingPreInfoForChangeError(long paramLong, ViewHolder paramViewHolder1, ViewHolder paramViewHolder2)
  {
    int i = this.mChildHelper.getChildCount();
    int j = 0;
    if (j < i)
    {
      ViewHolder localViewHolder = getChildViewHolderInt(this.mChildHelper.getChildAt(j));
      if (localViewHolder == paramViewHolder1) {}
      while (getChangedHolderKey(localViewHolder) != paramLong)
      {
        j++;
        break;
      }
      if ((this.mAdapter != null) && (this.mAdapter.hasStableIds())) {
        throw new IllegalStateException("Two different ViewHolders have the same stable ID. Stable IDs in your adapter MUST BE unique and SHOULD NOT change.\n ViewHolder 1:" + localViewHolder + " \n View Holder 2:" + paramViewHolder1);
      }
      throw new IllegalStateException("Two different ViewHolders have the same change ID. This might happen due to inconsistent Adapter update events or if the LayoutManager lays out the same View multiple times.\n ViewHolder 1:" + localViewHolder + " \n View Holder 2:" + paramViewHolder1);
    }
    Log.e("RecyclerView", "Problem while matching changed view holders with the newones. The pre-layout information for the change holder " + paramViewHolder2 + " cannot be found but it is necessary for " + paramViewHolder1);
  }
  
  private boolean hasUpdatedView()
  {
    int i = this.mChildHelper.getChildCount();
    int j = 0;
    if (j < i)
    {
      ViewHolder localViewHolder = getChildViewHolderInt(this.mChildHelper.getChildAt(j));
      if ((localViewHolder == null) || (localViewHolder.shouldIgnore())) {}
      while (!localViewHolder.isUpdated())
      {
        j++;
        break;
      }
      return true;
    }
    return false;
  }
  
  private void initChildrenHelper()
  {
    this.mChildHelper = new ChildHelper(new ChildHelper.Callback()
    {
      public void addView(View paramAnonymousView, int paramAnonymousInt)
      {
        RecyclerView.this.addView(paramAnonymousView, paramAnonymousInt);
        RecyclerView.this.dispatchChildAttached(paramAnonymousView);
      }
      
      public void attachViewToParent(View paramAnonymousView, int paramAnonymousInt, ViewGroup.LayoutParams paramAnonymousLayoutParams)
      {
        RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(paramAnonymousView);
        if (localViewHolder != null)
        {
          if ((!localViewHolder.isTmpDetached()) && (!localViewHolder.shouldIgnore())) {
            throw new IllegalArgumentException("Called attach on a child which is not detached: " + localViewHolder);
          }
          localViewHolder.clearTmpDetachFlag();
        }
        RecyclerView.this.attachViewToParent(paramAnonymousView, paramAnonymousInt, paramAnonymousLayoutParams);
      }
      
      public void detachViewFromParent(int paramAnonymousInt)
      {
        View localView = getChildAt(paramAnonymousInt);
        if (localView != null)
        {
          RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(localView);
          if (localViewHolder != null)
          {
            if ((localViewHolder.isTmpDetached()) && (!localViewHolder.shouldIgnore())) {
              throw new IllegalArgumentException("called detach on an already detached child " + localViewHolder);
            }
            localViewHolder.addFlags(256);
          }
        }
        RecyclerView.this.detachViewFromParent(paramAnonymousInt);
      }
      
      public View getChildAt(int paramAnonymousInt)
      {
        return RecyclerView.this.getChildAt(paramAnonymousInt);
      }
      
      public int getChildCount()
      {
        return RecyclerView.this.getChildCount();
      }
      
      public RecyclerView.ViewHolder getChildViewHolder(View paramAnonymousView)
      {
        return RecyclerView.getChildViewHolderInt(paramAnonymousView);
      }
      
      public int indexOfChild(View paramAnonymousView)
      {
        return RecyclerView.this.indexOfChild(paramAnonymousView);
      }
      
      public void onEnteredHiddenState(View paramAnonymousView)
      {
        RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(paramAnonymousView);
        if (localViewHolder != null) {
          RecyclerView.ViewHolder.access$200(localViewHolder, RecyclerView.this);
        }
      }
      
      public void onLeftHiddenState(View paramAnonymousView)
      {
        RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(paramAnonymousView);
        if (localViewHolder != null) {
          RecyclerView.ViewHolder.access$300(localViewHolder, RecyclerView.this);
        }
      }
      
      public void removeAllViews()
      {
        int i = getChildCount();
        for (int j = 0; j < i; j++) {
          RecyclerView.this.dispatchChildDetached(getChildAt(j));
        }
        RecyclerView.this.removeAllViews();
      }
      
      public void removeViewAt(int paramAnonymousInt)
      {
        View localView = RecyclerView.this.getChildAt(paramAnonymousInt);
        if (localView != null) {
          RecyclerView.this.dispatchChildDetached(localView);
        }
        RecyclerView.this.removeViewAt(paramAnonymousInt);
      }
    });
  }
  
  private boolean isPreferredNextFocus(View paramView1, View paramView2, int paramInt)
  {
    int i = 1;
    if ((paramView2 == null) || (paramView2 == this)) {
      i = 0;
    }
    label105:
    label110:
    for (;;)
    {
      return i;
      if (paramView1 != null)
      {
        if ((paramInt != 2) && (paramInt != i)) {
          break label121;
        }
        int j;
        if (this.mLayout.getLayoutDirection() == i)
        {
          j = i;
          int k = 0;
          if (paramInt == 2) {
            k = i;
          }
          if ((k ^ j) == 0) {
            break label105;
          }
        }
        for (int m = 66;; m = 17)
        {
          if (isPreferredNextFocusAbsolute(paramView1, paramView2, m)) {
            break label110;
          }
          if (paramInt != 2) {
            break label112;
          }
          return isPreferredNextFocusAbsolute(paramView1, paramView2, 130);
          j = 0;
          break;
        }
      }
    }
    label112:
    return isPreferredNextFocusAbsolute(paramView1, paramView2, 33);
    label121:
    return isPreferredNextFocusAbsolute(paramView1, paramView2, paramInt);
  }
  
  private boolean isPreferredNextFocusAbsolute(View paramView1, View paramView2, int paramInt)
  {
    this.mTempRect.set(0, 0, paramView1.getWidth(), paramView1.getHeight());
    this.mTempRect2.set(0, 0, paramView2.getWidth(), paramView2.getHeight());
    offsetDescendantRectToMyCoords(paramView1, this.mTempRect);
    offsetDescendantRectToMyCoords(paramView2, this.mTempRect2);
    switch (paramInt)
    {
    default: 
      throw new IllegalArgumentException("direction must be absolute. received:" + paramInt);
    case 17: 
      if (((this.mTempRect.right <= this.mTempRect2.right) && (this.mTempRect.left < this.mTempRect2.right)) || (this.mTempRect.left <= this.mTempRect2.left)) {
        break;
      }
    }
    do
    {
      do
      {
        do
        {
          return true;
          return false;
        } while (((this.mTempRect.left < this.mTempRect2.left) || (this.mTempRect.right <= this.mTempRect2.left)) && (this.mTempRect.right < this.mTempRect2.right));
        return false;
      } while (((this.mTempRect.bottom > this.mTempRect2.bottom) || (this.mTempRect.top >= this.mTempRect2.bottom)) && (this.mTempRect.top > this.mTempRect2.top));
      return false;
    } while (((this.mTempRect.top < this.mTempRect2.top) || (this.mTempRect.bottom <= this.mTempRect2.top)) && (this.mTempRect.bottom < this.mTempRect2.bottom));
    return false;
  }
  
  private void onPointerUp(MotionEvent paramMotionEvent)
  {
    int i = MotionEventCompat.getActionIndex(paramMotionEvent);
    if (paramMotionEvent.getPointerId(i) == this.mScrollPointerId) {
      if (i != 0) {
        break label81;
      }
    }
    label81:
    for (int j = 1;; j = 0)
    {
      this.mScrollPointerId = paramMotionEvent.getPointerId(j);
      int k = (int)(0.5F + paramMotionEvent.getX(j));
      this.mLastTouchX = k;
      this.mInitialTouchX = k;
      int m = (int)(0.5F + paramMotionEvent.getY(j));
      this.mLastTouchY = m;
      this.mInitialTouchY = m;
      return;
    }
  }
  
  private boolean predictiveItemAnimationsEnabled()
  {
    return (this.mItemAnimator != null) && (this.mLayout.supportsPredictiveItemAnimations());
  }
  
  private void processAdapterUpdatesAndSetAnimationFlags()
  {
    boolean bool1 = true;
    if (this.mDataSetHasChangedAfterLayout)
    {
      this.mAdapterHelper.reset();
      this.mLayout.onItemsChanged(this);
    }
    int i;
    label54:
    boolean bool2;
    label114:
    State localState2;
    if (predictiveItemAnimationsEnabled())
    {
      this.mAdapterHelper.preProcess();
      if ((!this.mItemsAddedOrRemoved) && (!this.mItemsChanged)) {
        break label171;
      }
      i = bool1;
      State localState1 = this.mState;
      if ((!this.mFirstLayoutComplete) || (this.mItemAnimator == null) || ((!this.mDataSetHasChangedAfterLayout) && (i == 0) && (!this.mLayout.mRequestedSimpleAnimations)) || ((this.mDataSetHasChangedAfterLayout) && (!this.mAdapter.hasStableIds()))) {
        break label176;
      }
      bool2 = bool1;
      localState1.mRunSimpleAnimations = bool2;
      localState2 = this.mState;
      if ((!this.mState.mRunSimpleAnimations) || (i == 0) || (this.mDataSetHasChangedAfterLayout) || (!predictiveItemAnimationsEnabled())) {
        break label182;
      }
    }
    for (;;)
    {
      localState2.mRunPredictiveAnimations = bool1;
      return;
      this.mAdapterHelper.consumeUpdatesInOnePass();
      break;
      label171:
      i = 0;
      break label54;
      label176:
      bool2 = false;
      break label114;
      label182:
      bool1 = false;
    }
  }
  
  private void pullGlows(float paramFloat1, float paramFloat2, float paramFloat3, float paramFloat4)
  {
    int i;
    if (paramFloat2 < 0.0F)
    {
      ensureLeftGlow();
      boolean bool3 = this.mLeftGlow.onPull(-paramFloat2 / getWidth(), 1.0F - paramFloat3 / getHeight());
      i = 0;
      if (bool3) {
        i = 1;
      }
      if (paramFloat4 >= 0.0F) {
        break label164;
      }
      ensureTopGlow();
      if (this.mTopGlow.onPull(-paramFloat4 / getHeight(), paramFloat1 / getWidth())) {
        i = 1;
      }
    }
    for (;;)
    {
      if ((i != 0) || (paramFloat2 != 0.0F) || (paramFloat4 != 0.0F)) {
        ViewCompat.postInvalidateOnAnimation(this);
      }
      return;
      boolean bool1 = paramFloat2 < 0.0F;
      i = 0;
      if (!bool1) {
        break;
      }
      ensureRightGlow();
      boolean bool2 = this.mRightGlow.onPull(paramFloat2 / getWidth(), paramFloat3 / getHeight());
      i = 0;
      if (!bool2) {
        break;
      }
      i = 1;
      break;
      label164:
      if (paramFloat4 > 0.0F)
      {
        ensureBottomGlow();
        if (this.mBottomGlow.onPull(paramFloat4 / getHeight(), 1.0F - paramFloat1 / getWidth())) {
          i = 1;
        }
      }
    }
  }
  
  private void recoverFocusFromState()
  {
    if ((!this.mPreserveFocusAfterLayout) || (this.mAdapter == null) || (!hasFocus()) || (getDescendantFocusability() == 393216) || ((getDescendantFocusability() == 131072) && (isFocused()))) {}
    for (;;)
    {
      return;
      if (!isFocused())
      {
        View localView2 = getFocusedChild();
        if ((IGNORE_DETACHED_FOCUSED_CHILD) && ((localView2.getParent() == null) || (!localView2.hasFocus())))
        {
          if (this.mChildHelper.getChildCount() == 0) {
            requestFocus();
          }
        }
        else {
          if (!this.mChildHelper.isHidden(localView2)) {
            continue;
          }
        }
      }
      boolean bool1 = this.mState.mFocusedItemId < -1L;
      ViewHolder localViewHolder = null;
      if (bool1)
      {
        boolean bool2 = this.mAdapter.hasStableIds();
        localViewHolder = null;
        if (bool2) {
          localViewHolder = findViewHolderForItemId(this.mState.mFocusedItemId);
        }
      }
      if ((localViewHolder == null) || (this.mChildHelper.isHidden(localViewHolder.itemView)) || (!localViewHolder.itemView.hasFocusable()))
      {
        int i = this.mChildHelper.getChildCount();
        localObject = null;
        if (i <= 0) {}
      }
      for (Object localObject = findNextViewToFocus(); localObject != null; localObject = localViewHolder.itemView)
      {
        if (this.mState.mFocusedSubChildId != -1L)
        {
          View localView1 = ((View)localObject).findViewById(this.mState.mFocusedSubChildId);
          if ((localView1 != null) && (localView1.isFocusable())) {
            localObject = localView1;
          }
        }
        ((View)localObject).requestFocus();
        return;
      }
    }
  }
  
  private void releaseGlows()
  {
    EdgeEffectCompat localEdgeEffectCompat = this.mLeftGlow;
    boolean bool = false;
    if (localEdgeEffectCompat != null) {
      bool = this.mLeftGlow.onRelease();
    }
    if (this.mTopGlow != null) {
      bool |= this.mTopGlow.onRelease();
    }
    if (this.mRightGlow != null) {
      bool |= this.mRightGlow.onRelease();
    }
    if (this.mBottomGlow != null) {
      bool |= this.mBottomGlow.onRelease();
    }
    if (bool) {
      ViewCompat.postInvalidateOnAnimation(this);
    }
  }
  
  private void requestChildOnScreen(@NonNull View paramView1, @Nullable View paramView2)
  {
    boolean bool1 = true;
    View localView;
    LayoutManager localLayoutManager;
    Rect localRect1;
    boolean bool2;
    if (paramView2 != null)
    {
      localView = paramView2;
      this.mTempRect.set(0, 0, localView.getWidth(), localView.getHeight());
      ViewGroup.LayoutParams localLayoutParams = localView.getLayoutParams();
      if ((localLayoutParams instanceof LayoutParams))
      {
        LayoutParams localLayoutParams1 = (LayoutParams)localLayoutParams;
        if (!localLayoutParams1.mInsetsDirty)
        {
          Rect localRect2 = localLayoutParams1.mDecorInsets;
          Rect localRect3 = this.mTempRect;
          localRect3.left -= localRect2.left;
          Rect localRect4 = this.mTempRect;
          localRect4.right += localRect2.right;
          Rect localRect5 = this.mTempRect;
          localRect5.top -= localRect2.top;
          Rect localRect6 = this.mTempRect;
          localRect6.bottom += localRect2.bottom;
        }
      }
      if (paramView2 != null)
      {
        offsetDescendantRectToMyCoords(paramView2, this.mTempRect);
        offsetRectIntoDescendantCoords(paramView1, this.mTempRect);
      }
      localLayoutManager = this.mLayout;
      localRect1 = this.mTempRect;
      if (this.mFirstLayoutComplete) {
        break label221;
      }
      bool2 = bool1;
      label197:
      if (paramView2 != null) {
        break label227;
      }
    }
    for (;;)
    {
      localLayoutManager.requestChildRectangleOnScreen(this, paramView1, localRect1, bool2, bool1);
      return;
      localView = paramView1;
      break;
      label221:
      bool2 = false;
      break label197;
      label227:
      bool1 = false;
    }
  }
  
  private void resetFocusInfo()
  {
    this.mState.mFocusedItemId = -1L;
    this.mState.mFocusedItemPosition = -1;
    this.mState.mFocusedSubChildId = -1;
  }
  
  private void resetTouch()
  {
    if (this.mVelocityTracker != null) {
      this.mVelocityTracker.clear();
    }
    stopNestedScroll();
    releaseGlows();
  }
  
  private void saveFocusInfo()
  {
    boolean bool1 = this.mPreserveFocusAfterLayout;
    View localView = null;
    if (bool1)
    {
      boolean bool2 = hasFocus();
      localView = null;
      if (bool2)
      {
        Adapter localAdapter = this.mAdapter;
        localView = null;
        if (localAdapter != null) {
          localView = getFocusedChild();
        }
      }
    }
    if (localView == null) {}
    for (ViewHolder localViewHolder = null; localViewHolder == null; localViewHolder = findContainingViewHolder(localView))
    {
      resetFocusInfo();
      return;
    }
    State localState1 = this.mState;
    long l;
    State localState2;
    int i;
    if (this.mAdapter.hasStableIds())
    {
      l = localViewHolder.getItemId();
      localState1.mFocusedItemId = l;
      localState2 = this.mState;
      if (!this.mDataSetHasChangedAfterLayout) {
        break label142;
      }
      i = -1;
    }
    for (;;)
    {
      localState2.mFocusedItemPosition = i;
      this.mState.mFocusedSubChildId = getDeepestFocusedViewWithId(localViewHolder.itemView);
      return;
      l = -1L;
      break;
      label142:
      if (localViewHolder.isRemoved()) {
        i = localViewHolder.mOldPosition;
      } else {
        i = localViewHolder.getAdapterPosition();
      }
    }
  }
  
  private void setAdapterInternal(Adapter paramAdapter, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.mAdapter != null)
    {
      this.mAdapter.unregisterAdapterDataObserver(this.mObserver);
      this.mAdapter.onDetachedFromRecyclerView(this);
    }
    if ((!paramBoolean1) || (paramBoolean2)) {
      removeAndRecycleViews();
    }
    this.mAdapterHelper.reset();
    Adapter localAdapter = this.mAdapter;
    this.mAdapter = paramAdapter;
    if (paramAdapter != null)
    {
      paramAdapter.registerAdapterDataObserver(this.mObserver);
      paramAdapter.onAttachedToRecyclerView(this);
    }
    if (this.mLayout != null) {
      this.mLayout.onAdapterChanged(localAdapter, this.mAdapter);
    }
    this.mRecycler.onAdapterChanged(localAdapter, this.mAdapter, paramBoolean1);
    this.mState.mStructureChanged = true;
    markKnownViewsInvalid();
  }
  
  private void stopScrollersInternal()
  {
    this.mViewFlinger.stop();
    if (this.mLayout != null) {
      this.mLayout.stopSmoothScroller();
    }
  }
  
  void absorbGlows(int paramInt1, int paramInt2)
  {
    if (paramInt1 < 0)
    {
      ensureLeftGlow();
      this.mLeftGlow.onAbsorb(-paramInt1);
      if (paramInt2 >= 0) {
        break label69;
      }
      ensureTopGlow();
      this.mTopGlow.onAbsorb(-paramInt2);
    }
    for (;;)
    {
      if ((paramInt1 != 0) || (paramInt2 != 0)) {
        ViewCompat.postInvalidateOnAnimation(this);
      }
      return;
      if (paramInt1 <= 0) {
        break;
      }
      ensureRightGlow();
      this.mRightGlow.onAbsorb(paramInt1);
      break;
      label69:
      if (paramInt2 > 0)
      {
        ensureBottomGlow();
        this.mBottomGlow.onAbsorb(paramInt2);
      }
    }
  }
  
  public void addFocusables(ArrayList<View> paramArrayList, int paramInt1, int paramInt2)
  {
    if ((this.mLayout == null) || (!this.mLayout.onAddFocusables(this, paramArrayList, paramInt1, paramInt2))) {
      super.addFocusables(paramArrayList, paramInt1, paramInt2);
    }
  }
  
  public void addItemDecoration(ItemDecoration paramItemDecoration)
  {
    addItemDecoration(paramItemDecoration, -1);
  }
  
  public void addItemDecoration(ItemDecoration paramItemDecoration, int paramInt)
  {
    if (this.mLayout != null) {
      this.mLayout.assertNotInLayoutOrScroll("Cannot add item decoration during a scroll  or layout");
    }
    if (this.mItemDecorations.isEmpty()) {
      setWillNotDraw(false);
    }
    if (paramInt < 0) {
      this.mItemDecorations.add(paramItemDecoration);
    }
    for (;;)
    {
      markItemDecorInsetsDirty();
      requestLayout();
      return;
      this.mItemDecorations.add(paramInt, paramItemDecoration);
    }
  }
  
  public void addOnChildAttachStateChangeListener(OnChildAttachStateChangeListener paramOnChildAttachStateChangeListener)
  {
    if (this.mOnChildAttachStateListeners == null) {
      this.mOnChildAttachStateListeners = new ArrayList();
    }
    this.mOnChildAttachStateListeners.add(paramOnChildAttachStateChangeListener);
  }
  
  public void addOnItemTouchListener(OnItemTouchListener paramOnItemTouchListener)
  {
    this.mOnItemTouchListeners.add(paramOnItemTouchListener);
  }
  
  public void addOnScrollListener(OnScrollListener paramOnScrollListener)
  {
    if (this.mScrollListeners == null) {
      this.mScrollListeners = new ArrayList();
    }
    this.mScrollListeners.add(paramOnScrollListener);
  }
  
  void animateAppearance(@NonNull ViewHolder paramViewHolder, @Nullable RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo1, @NonNull RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo2)
  {
    paramViewHolder.setIsRecyclable(false);
    if (this.mItemAnimator.animateAppearance(paramViewHolder, paramItemHolderInfo1, paramItemHolderInfo2)) {
      postAnimationRunner();
    }
  }
  
  void animateDisappearance(@NonNull ViewHolder paramViewHolder, @NonNull RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo1, @Nullable RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo2)
  {
    addAnimatingView(paramViewHolder);
    paramViewHolder.setIsRecyclable(false);
    if (this.mItemAnimator.animateDisappearance(paramViewHolder, paramItemHolderInfo1, paramItemHolderInfo2)) {
      postAnimationRunner();
    }
  }
  
  void assertInLayoutOrScroll(String paramString)
  {
    if (!isComputingLayout())
    {
      if (paramString == null) {
        throw new IllegalStateException("Cannot call this method unless RecyclerView is computing a layout or scrolling");
      }
      throw new IllegalStateException(paramString);
    }
  }
  
  void assertNotInLayoutOrScroll(String paramString)
  {
    if (isComputingLayout())
    {
      if (paramString == null) {
        throw new IllegalStateException("Cannot call this method while RecyclerView is computing a layout or scrolling");
      }
      throw new IllegalStateException(paramString);
    }
    if (this.mDispatchScrollCounter > 0) {
      Log.w("RecyclerView", "Cannot call this method in a scroll callback. Scroll callbacks mightbe run during a measure & layout pass where you cannot change theRecyclerView data. Any method call that might change the structureof the RecyclerView or the adapter contents should be postponed tothe next frame.", new IllegalStateException(""));
    }
  }
  
  boolean canReuseUpdatedViewHolder(ViewHolder paramViewHolder)
  {
    return (this.mItemAnimator == null) || (this.mItemAnimator.canReuseUpdatedViewHolder(paramViewHolder, paramViewHolder.getUnmodifiedPayloads()));
  }
  
  protected boolean checkLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    return ((paramLayoutParams instanceof LayoutParams)) && (this.mLayout.checkLayoutParams((LayoutParams)paramLayoutParams));
  }
  
  void clearOldPositions()
  {
    int i = this.mChildHelper.getUnfilteredChildCount();
    for (int j = 0; j < i; j++)
    {
      ViewHolder localViewHolder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(j));
      if (!localViewHolder.shouldIgnore()) {
        localViewHolder.clearOldPosition();
      }
    }
    this.mRecycler.clearOldPositions();
  }
  
  public void clearOnChildAttachStateChangeListeners()
  {
    if (this.mOnChildAttachStateListeners != null) {
      this.mOnChildAttachStateListeners.clear();
    }
  }
  
  public void clearOnScrollListeners()
  {
    if (this.mScrollListeners != null) {
      this.mScrollListeners.clear();
    }
  }
  
  public int computeHorizontalScrollExtent()
  {
    if (this.mLayout == null) {}
    while (!this.mLayout.canScrollHorizontally()) {
      return 0;
    }
    return this.mLayout.computeHorizontalScrollExtent(this.mState);
  }
  
  public int computeHorizontalScrollOffset()
  {
    if (this.mLayout == null) {}
    while (!this.mLayout.canScrollHorizontally()) {
      return 0;
    }
    return this.mLayout.computeHorizontalScrollOffset(this.mState);
  }
  
  public int computeHorizontalScrollRange()
  {
    if (this.mLayout == null) {}
    while (!this.mLayout.canScrollHorizontally()) {
      return 0;
    }
    return this.mLayout.computeHorizontalScrollRange(this.mState);
  }
  
  public int computeVerticalScrollExtent()
  {
    if (this.mLayout == null) {}
    while (!this.mLayout.canScrollVertically()) {
      return 0;
    }
    return this.mLayout.computeVerticalScrollExtent(this.mState);
  }
  
  public int computeVerticalScrollOffset()
  {
    if (this.mLayout == null) {}
    while (!this.mLayout.canScrollVertically()) {
      return 0;
    }
    return this.mLayout.computeVerticalScrollOffset(this.mState);
  }
  
  public int computeVerticalScrollRange()
  {
    if (this.mLayout == null) {}
    while (!this.mLayout.canScrollVertically()) {
      return 0;
    }
    return this.mLayout.computeVerticalScrollRange(this.mState);
  }
  
  void considerReleasingGlowsOnScroll(int paramInt1, int paramInt2)
  {
    EdgeEffectCompat localEdgeEffectCompat = this.mLeftGlow;
    boolean bool1 = false;
    if (localEdgeEffectCompat != null)
    {
      boolean bool2 = this.mLeftGlow.isFinished();
      bool1 = false;
      if (!bool2)
      {
        bool1 = false;
        if (paramInt1 > 0) {
          bool1 = this.mLeftGlow.onRelease();
        }
      }
    }
    if ((this.mRightGlow != null) && (!this.mRightGlow.isFinished()) && (paramInt1 < 0)) {
      bool1 |= this.mRightGlow.onRelease();
    }
    if ((this.mTopGlow != null) && (!this.mTopGlow.isFinished()) && (paramInt2 > 0)) {
      bool1 |= this.mTopGlow.onRelease();
    }
    if ((this.mBottomGlow != null) && (!this.mBottomGlow.isFinished()) && (paramInt2 < 0)) {
      bool1 |= this.mBottomGlow.onRelease();
    }
    if (bool1) {
      ViewCompat.postInvalidateOnAnimation(this);
    }
  }
  
  void consumePendingUpdateOperations()
  {
    if ((!this.mFirstLayoutComplete) || (this.mDataSetHasChangedAfterLayout))
    {
      TraceCompat.beginSection("RV FullInvalidate");
      dispatchLayout();
      TraceCompat.endSection();
    }
    label111:
    do
    {
      do
      {
        return;
      } while (!this.mAdapterHelper.hasPendingUpdates());
      if ((this.mAdapterHelper.hasAnyUpdateTypes(4)) && (!this.mAdapterHelper.hasAnyUpdateTypes(11)))
      {
        TraceCompat.beginSection("RV PartialInvalidate");
        eatRequestLayout();
        onEnterLayoutOrScroll();
        this.mAdapterHelper.preProcess();
        if (!this.mLayoutRequestEaten)
        {
          if (!hasUpdatedView()) {
            break label111;
          }
          dispatchLayout();
        }
        for (;;)
        {
          resumeRequestLayout(true);
          onExitLayoutOrScroll();
          TraceCompat.endSection();
          return;
          this.mAdapterHelper.consumePostponedUpdates();
        }
      }
    } while (!this.mAdapterHelper.hasPendingUpdates());
    TraceCompat.beginSection("RV FullInvalidate");
    dispatchLayout();
    TraceCompat.endSection();
  }
  
  void defaultOnMeasure(int paramInt1, int paramInt2)
  {
    setMeasuredDimension(LayoutManager.chooseSize(paramInt1, getPaddingLeft() + getPaddingRight(), ViewCompat.getMinimumWidth(this)), LayoutManager.chooseSize(paramInt2, getPaddingTop() + getPaddingBottom(), ViewCompat.getMinimumHeight(this)));
  }
  
  void dispatchChildAttached(View paramView)
  {
    ViewHolder localViewHolder = getChildViewHolderInt(paramView);
    onChildAttachedToWindow(paramView);
    if ((this.mAdapter != null) && (localViewHolder != null)) {
      this.mAdapter.onViewAttachedToWindow(localViewHolder);
    }
    if (this.mOnChildAttachStateListeners != null) {
      for (int i = -1 + this.mOnChildAttachStateListeners.size(); i >= 0; i--) {
        ((OnChildAttachStateChangeListener)this.mOnChildAttachStateListeners.get(i)).onChildViewAttachedToWindow(paramView);
      }
    }
  }
  
  void dispatchChildDetached(View paramView)
  {
    ViewHolder localViewHolder = getChildViewHolderInt(paramView);
    onChildDetachedFromWindow(paramView);
    if ((this.mAdapter != null) && (localViewHolder != null)) {
      this.mAdapter.onViewDetachedFromWindow(localViewHolder);
    }
    if (this.mOnChildAttachStateListeners != null) {
      for (int i = -1 + this.mOnChildAttachStateListeners.size(); i >= 0; i--) {
        ((OnChildAttachStateChangeListener)this.mOnChildAttachStateListeners.get(i)).onChildViewDetachedFromWindow(paramView);
      }
    }
  }
  
  void dispatchLayout()
  {
    if (this.mAdapter == null)
    {
      Log.e("RecyclerView", "No adapter attached; skipping layout");
      return;
    }
    if (this.mLayout == null)
    {
      Log.e("RecyclerView", "No layout manager attached; skipping layout");
      return;
    }
    this.mState.mIsMeasuring = false;
    if (this.mState.mLayoutStep == 1)
    {
      dispatchLayoutStep1();
      this.mLayout.setExactMeasureSpecsFrom(this);
      dispatchLayoutStep2();
    }
    for (;;)
    {
      dispatchLayoutStep3();
      return;
      if ((this.mAdapterHelper.hasUpdates()) || (this.mLayout.getWidth() != getWidth()) || (this.mLayout.getHeight() != getHeight()))
      {
        this.mLayout.setExactMeasureSpecsFrom(this);
        dispatchLayoutStep2();
      }
      else
      {
        this.mLayout.setExactMeasureSpecsFrom(this);
      }
    }
  }
  
  public boolean dispatchNestedFling(float paramFloat1, float paramFloat2, boolean paramBoolean)
  {
    return getScrollingChildHelper().dispatchNestedFling(paramFloat1, paramFloat2, paramBoolean);
  }
  
  public boolean dispatchNestedPreFling(float paramFloat1, float paramFloat2)
  {
    return getScrollingChildHelper().dispatchNestedPreFling(paramFloat1, paramFloat2);
  }
  
  public boolean dispatchNestedPreScroll(int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2)
  {
    return getScrollingChildHelper().dispatchNestedPreScroll(paramInt1, paramInt2, paramArrayOfInt1, paramArrayOfInt2);
  }
  
  public boolean dispatchNestedScroll(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfInt)
  {
    return getScrollingChildHelper().dispatchNestedScroll(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfInt);
  }
  
  void dispatchOnScrollStateChanged(int paramInt)
  {
    if (this.mLayout != null) {
      this.mLayout.onScrollStateChanged(paramInt);
    }
    onScrollStateChanged(paramInt);
    if (this.mScrollListener != null) {
      this.mScrollListener.onScrollStateChanged(this, paramInt);
    }
    if (this.mScrollListeners != null) {
      for (int i = -1 + this.mScrollListeners.size(); i >= 0; i--) {
        ((OnScrollListener)this.mScrollListeners.get(i)).onScrollStateChanged(this, paramInt);
      }
    }
  }
  
  void dispatchOnScrolled(int paramInt1, int paramInt2)
  {
    this.mDispatchScrollCounter = (1 + this.mDispatchScrollCounter);
    int i = getScrollX();
    int j = getScrollY();
    onScrollChanged(i, j, i, j);
    onScrolled(paramInt1, paramInt2);
    if (this.mScrollListener != null) {
      this.mScrollListener.onScrolled(this, paramInt1, paramInt2);
    }
    if (this.mScrollListeners != null) {
      for (int k = -1 + this.mScrollListeners.size(); k >= 0; k--) {
        ((OnScrollListener)this.mScrollListeners.get(k)).onScrolled(this, paramInt1, paramInt2);
      }
    }
    this.mDispatchScrollCounter = (-1 + this.mDispatchScrollCounter);
  }
  
  void dispatchPendingImportantForAccessibilityChanges()
  {
    int i = -1 + this.mPendingAccessibilityImportanceChange.size();
    if (i >= 0)
    {
      ViewHolder localViewHolder = (ViewHolder)this.mPendingAccessibilityImportanceChange.get(i);
      if ((localViewHolder.itemView.getParent() != this) || (localViewHolder.shouldIgnore())) {}
      for (;;)
      {
        i--;
        break;
        int j = localViewHolder.mPendingAccessibilityState;
        if (j != -1)
        {
          ViewCompat.setImportantForAccessibility(localViewHolder.itemView, j);
          localViewHolder.mPendingAccessibilityState = -1;
        }
      }
    }
    this.mPendingAccessibilityImportanceChange.clear();
  }
  
  protected void dispatchRestoreInstanceState(SparseArray<Parcelable> paramSparseArray)
  {
    dispatchThawSelfOnly(paramSparseArray);
  }
  
  protected void dispatchSaveInstanceState(SparseArray<Parcelable> paramSparseArray)
  {
    dispatchFreezeSelfOnly(paramSparseArray);
  }
  
  public void draw(Canvas paramCanvas)
  {
    int i = 1;
    super.draw(paramCanvas);
    int j = this.mItemDecorations.size();
    for (int k = 0; k < j; k++) {
      ((ItemDecoration)this.mItemDecorations.get(k)).onDrawOver(paramCanvas, this, this.mState);
    }
    EdgeEffectCompat localEdgeEffectCompat = this.mLeftGlow;
    int m = 0;
    int i8;
    if (localEdgeEffectCompat != null)
    {
      boolean bool = this.mLeftGlow.isFinished();
      m = 0;
      if (!bool)
      {
        int i7 = paramCanvas.save();
        if (!this.mClipToPadding) {
          break label460;
        }
        i8 = getPaddingBottom();
        paramCanvas.rotate(270.0F);
        paramCanvas.translate(i8 + -getHeight(), 0.0F);
        if ((this.mLeftGlow == null) || (!this.mLeftGlow.draw(paramCanvas))) {
          break label466;
        }
        m = i;
        label143:
        paramCanvas.restoreToCount(i7);
      }
    }
    int i6;
    label214:
    int i3;
    label269:
    int i4;
    label309:
    int n;
    if ((this.mTopGlow != null) && (!this.mTopGlow.isFinished()))
    {
      int i5 = paramCanvas.save();
      if (this.mClipToPadding) {
        paramCanvas.translate(getPaddingLeft(), getPaddingTop());
      }
      if ((this.mTopGlow != null) && (this.mTopGlow.draw(paramCanvas)))
      {
        i6 = i;
        m |= i6;
        paramCanvas.restoreToCount(i5);
      }
    }
    else
    {
      if ((this.mRightGlow != null) && (!this.mRightGlow.isFinished()))
      {
        int i1 = paramCanvas.save();
        int i2 = getWidth();
        if (!this.mClipToPadding) {
          break label478;
        }
        i3 = getPaddingTop();
        paramCanvas.rotate(90.0F);
        paramCanvas.translate(-i3, -i2);
        if ((this.mRightGlow == null) || (!this.mRightGlow.draw(paramCanvas))) {
          break label484;
        }
        i4 = i;
        m |= i4;
        paramCanvas.restoreToCount(i1);
      }
      if ((this.mBottomGlow != null) && (!this.mBottomGlow.isFinished()))
      {
        n = paramCanvas.save();
        paramCanvas.rotate(180.0F);
        if (!this.mClipToPadding) {
          break label490;
        }
        paramCanvas.translate(-getWidth() + getPaddingRight(), -getHeight() + getPaddingBottom());
        label385:
        if ((this.mBottomGlow == null) || (!this.mBottomGlow.draw(paramCanvas))) {
          break label509;
        }
      }
    }
    for (;;)
    {
      m |= i;
      paramCanvas.restoreToCount(n);
      if ((m == 0) && (this.mItemAnimator != null) && (this.mItemDecorations.size() > 0) && (this.mItemAnimator.isRunning())) {
        m = 1;
      }
      if (m != 0) {
        ViewCompat.postInvalidateOnAnimation(this);
      }
      return;
      label460:
      i8 = 0;
      break;
      label466:
      m = 0;
      break label143;
      i6 = 0;
      break label214;
      label478:
      i3 = 0;
      break label269;
      label484:
      i4 = 0;
      break label309;
      label490:
      paramCanvas.translate(-getWidth(), -getHeight());
      break label385;
      label509:
      i = 0;
    }
  }
  
  public boolean drawChild(Canvas paramCanvas, View paramView, long paramLong)
  {
    return super.drawChild(paramCanvas, paramView, paramLong);
  }
  
  void eatRequestLayout()
  {
    this.mEatRequestLayout = (1 + this.mEatRequestLayout);
    if ((this.mEatRequestLayout == 1) && (!this.mLayoutFrozen)) {
      this.mLayoutRequestEaten = false;
    }
  }
  
  void ensureBottomGlow()
  {
    if (this.mBottomGlow != null) {
      return;
    }
    this.mBottomGlow = new EdgeEffectCompat(getContext());
    if (this.mClipToPadding)
    {
      this.mBottomGlow.setSize(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), getMeasuredHeight() - getPaddingTop() - getPaddingBottom());
      return;
    }
    this.mBottomGlow.setSize(getMeasuredWidth(), getMeasuredHeight());
  }
  
  void ensureLeftGlow()
  {
    if (this.mLeftGlow != null) {
      return;
    }
    this.mLeftGlow = new EdgeEffectCompat(getContext());
    if (this.mClipToPadding)
    {
      this.mLeftGlow.setSize(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), getMeasuredWidth() - getPaddingLeft() - getPaddingRight());
      return;
    }
    this.mLeftGlow.setSize(getMeasuredHeight(), getMeasuredWidth());
  }
  
  void ensureRightGlow()
  {
    if (this.mRightGlow != null) {
      return;
    }
    this.mRightGlow = new EdgeEffectCompat(getContext());
    if (this.mClipToPadding)
    {
      this.mRightGlow.setSize(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), getMeasuredWidth() - getPaddingLeft() - getPaddingRight());
      return;
    }
    this.mRightGlow.setSize(getMeasuredHeight(), getMeasuredWidth());
  }
  
  void ensureTopGlow()
  {
    if (this.mTopGlow != null) {
      return;
    }
    this.mTopGlow = new EdgeEffectCompat(getContext());
    if (this.mClipToPadding)
    {
      this.mTopGlow.setSize(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), getMeasuredHeight() - getPaddingTop() - getPaddingBottom());
      return;
    }
    this.mTopGlow.setSize(getMeasuredWidth(), getMeasuredHeight());
  }
  
  public View findChildViewUnder(float paramFloat1, float paramFloat2)
  {
    for (int i = -1 + this.mChildHelper.getChildCount(); i >= 0; i--)
    {
      View localView = this.mChildHelper.getChildAt(i);
      float f1 = ViewCompat.getTranslationX(localView);
      float f2 = ViewCompat.getTranslationY(localView);
      if ((paramFloat1 >= f1 + localView.getLeft()) && (paramFloat1 <= f1 + localView.getRight()) && (paramFloat2 >= f2 + localView.getTop()) && (paramFloat2 <= f2 + localView.getBottom())) {
        return localView;
      }
    }
    return null;
  }
  
  @Nullable
  public View findContainingItemView(View paramView)
  {
    for (ViewParent localViewParent = paramView.getParent(); (localViewParent != null) && (localViewParent != this) && ((localViewParent instanceof View)); localViewParent = paramView.getParent()) {
      paramView = (View)localViewParent;
    }
    if (localViewParent == this) {
      return paramView;
    }
    return null;
  }
  
  @Nullable
  public ViewHolder findContainingViewHolder(View paramView)
  {
    View localView = findContainingItemView(paramView);
    if (localView == null) {
      return null;
    }
    return getChildViewHolder(localView);
  }
  
  public ViewHolder findViewHolderForAdapterPosition(int paramInt)
  {
    ViewHolder localViewHolder2;
    if (this.mDataSetHasChangedAfterLayout)
    {
      localViewHolder2 = null;
      return localViewHolder2;
    }
    int i = this.mChildHelper.getUnfilteredChildCount();
    ViewHolder localViewHolder1 = null;
    for (int j = 0;; j++)
    {
      if (j >= i) {
        return localViewHolder1;
      }
      localViewHolder2 = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(j));
      if ((localViewHolder2 != null) && (!localViewHolder2.isRemoved()) && (getAdapterPositionFor(localViewHolder2) == paramInt))
      {
        if (!this.mChildHelper.isHidden(localViewHolder2.itemView)) {
          break;
        }
        localViewHolder1 = localViewHolder2;
      }
    }
    return localViewHolder1;
  }
  
  public ViewHolder findViewHolderForItemId(long paramLong)
  {
    ViewHolder localViewHolder1;
    if ((this.mAdapter == null) || (!this.mAdapter.hasStableIds()))
    {
      localViewHolder1 = null;
      return localViewHolder1;
    }
    int i = this.mChildHelper.getUnfilteredChildCount();
    ViewHolder localViewHolder2 = null;
    for (int j = 0;; j++)
    {
      if (j >= i) {
        break label99;
      }
      localViewHolder1 = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(j));
      if ((localViewHolder1 != null) && (!localViewHolder1.isRemoved()) && (localViewHolder1.getItemId() == paramLong))
      {
        if (!this.mChildHelper.isHidden(localViewHolder1.itemView)) {
          break;
        }
        localViewHolder2 = localViewHolder1;
      }
    }
    label99:
    return localViewHolder2;
  }
  
  public ViewHolder findViewHolderForLayoutPosition(int paramInt)
  {
    return findViewHolderForPosition(paramInt, false);
  }
  
  @Deprecated
  public ViewHolder findViewHolderForPosition(int paramInt)
  {
    return findViewHolderForPosition(paramInt, false);
  }
  
  ViewHolder findViewHolderForPosition(int paramInt, boolean paramBoolean)
  {
    int i = this.mChildHelper.getUnfilteredChildCount();
    Object localObject1 = null;
    int j = 0;
    if (j < i)
    {
      localObject2 = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(j));
      if ((localObject2 != null) && (!((ViewHolder)localObject2).isRemoved()))
      {
        if (!paramBoolean) {
          break label66;
        }
        if (((ViewHolder)localObject2).mPosition == paramInt) {
          break label75;
        }
      }
      for (;;)
      {
        j++;
        break;
        label66:
        if (((ViewHolder)localObject2).getLayoutPosition() == paramInt)
        {
          label75:
          if (!this.mChildHelper.isHidden(((ViewHolder)localObject2).itemView)) {
            break label101;
          }
          localObject1 = localObject2;
        }
      }
    }
    Object localObject2 = localObject1;
    label101:
    return localObject2;
  }
  
  public boolean fling(int paramInt1, int paramInt2)
  {
    if (this.mLayout == null) {
      Log.e("RecyclerView", "Cannot fling without a LayoutManager set. Call setLayoutManager with a non-null argument.");
    }
    boolean bool3;
    do
    {
      boolean bool1;
      boolean bool2;
      do
      {
        do
        {
          return false;
        } while (this.mLayoutFrozen);
        bool1 = this.mLayout.canScrollHorizontally();
        bool2 = this.mLayout.canScrollVertically();
        if ((!bool1) || (Math.abs(paramInt1) < this.mMinFlingVelocity)) {
          paramInt1 = 0;
        }
        if ((!bool2) || (Math.abs(paramInt2) < this.mMinFlingVelocity)) {
          paramInt2 = 0;
        }
      } while (((paramInt1 == 0) && (paramInt2 == 0)) || (dispatchNestedPreFling(paramInt1, paramInt2)));
      if ((bool1) || (bool2)) {}
      for (bool3 = true;; bool3 = false)
      {
        dispatchNestedFling(paramInt1, paramInt2, bool3);
        if ((this.mOnFlingListener == null) || (!this.mOnFlingListener.onFling(paramInt1, paramInt2))) {
          break;
        }
        return true;
      }
    } while (!bool3);
    int i = Math.max(-this.mMaxFlingVelocity, Math.min(paramInt1, this.mMaxFlingVelocity));
    int j = Math.max(-this.mMaxFlingVelocity, Math.min(paramInt2, this.mMaxFlingVelocity));
    this.mViewFlinger.fling(i, j);
    return true;
  }
  
  public View focusSearch(View paramView, int paramInt)
  {
    View localView1 = this.mLayout.onInterceptFocusSearch(paramView, paramInt);
    if (localView1 != null) {
      return localView1;
    }
    int i;
    FocusFinder localFocusFinder;
    int i1;
    label94:
    label109:
    int k;
    label147:
    int m;
    label155:
    int n;
    if ((this.mAdapter != null) && (this.mLayout != null) && (!isComputingLayout()) && (!this.mLayoutFrozen))
    {
      i = 1;
      localFocusFinder = FocusFinder.getInstance();
      if ((i == 0) || ((paramInt != 2) && (paramInt != 1))) {
        break label318;
      }
      boolean bool = this.mLayout.canScrollVertically();
      j = 0;
      if (bool)
      {
        if (paramInt != 2) {
          break label216;
        }
        i1 = 130;
        if (localFocusFinder.findNextFocus(this, paramView, i1) != null) {
          break label223;
        }
        j = 1;
        if (FORCE_ABS_FOCUS_SEARCH_DIRECTION) {
          paramInt = i1;
        }
      }
      if ((j == 0) && (this.mLayout.canScrollHorizontally()))
      {
        if (this.mLayout.getLayoutDirection() != 1) {
          break label229;
        }
        k = 1;
        if (paramInt != 2) {
          break label235;
        }
        m = 1;
        if ((m ^ k) == 0) {
          break label241;
        }
        n = 66;
        label167:
        if (localFocusFinder.findNextFocus(this, paramView, n) != null) {
          break label248;
        }
      }
    }
    label216:
    label223:
    label229:
    label235:
    label241:
    label248:
    for (int j = 1;; j = 0)
    {
      if (FORCE_ABS_FOCUS_SEARCH_DIRECTION) {
        paramInt = n;
      }
      if (j == 0) {
        break label281;
      }
      consumePendingUpdateOperations();
      if (findContainingItemView(paramView) != null) {
        break label254;
      }
      return null;
      i = 0;
      break;
      i1 = 33;
      break label94;
      j = 0;
      break label109;
      k = 0;
      break label147;
      m = 0;
      break label155;
      n = 17;
      break label167;
    }
    label254:
    eatRequestLayout();
    this.mLayout.onFocusSearchFailed(paramView, paramInt, this.mRecycler, this.mState);
    resumeRequestLayout(false);
    label281:
    View localView2 = localFocusFinder.findNextFocus(this, paramView, paramInt);
    while ((localView2 != null) && (!localView2.hasFocusable())) {
      if (getFocusedChild() == null)
      {
        return super.focusSearch(paramView, paramInt);
        label318:
        localView2 = localFocusFinder.findNextFocus(this, paramView, paramInt);
        if ((localView2 == null) && (i != 0))
        {
          consumePendingUpdateOperations();
          if (findContainingItemView(paramView) == null) {
            return null;
          }
          eatRequestLayout();
          localView2 = this.mLayout.onFocusSearchFailed(paramView, paramInt, this.mRecycler, this.mState);
          resumeRequestLayout(false);
        }
      }
      else
      {
        requestChildOnScreen(localView2, null);
        return paramView;
      }
    }
    if (isPreferredNextFocus(paramView, localView2, paramInt)) {}
    for (View localView3 = localView2;; localView3 = super.focusSearch(paramView, paramInt)) {
      return localView3;
    }
  }
  
  protected ViewGroup.LayoutParams generateDefaultLayoutParams()
  {
    if (this.mLayout == null) {
      throw new IllegalStateException("RecyclerView has no LayoutManager");
    }
    return this.mLayout.generateDefaultLayoutParams();
  }
  
  public ViewGroup.LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
    if (this.mLayout == null) {
      throw new IllegalStateException("RecyclerView has no LayoutManager");
    }
    return this.mLayout.generateLayoutParams(getContext(), paramAttributeSet);
  }
  
  protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
  {
    if (this.mLayout == null) {
      throw new IllegalStateException("RecyclerView has no LayoutManager");
    }
    return this.mLayout.generateLayoutParams(paramLayoutParams);
  }
  
  public Adapter getAdapter()
  {
    return this.mAdapter;
  }
  
  int getAdapterPositionFor(ViewHolder paramViewHolder)
  {
    if ((paramViewHolder.hasAnyOfTheFlags(524)) || (!paramViewHolder.isBound())) {
      return -1;
    }
    return this.mAdapterHelper.applyPendingUpdatesToPosition(paramViewHolder.mPosition);
  }
  
  public int getBaseline()
  {
    if (this.mLayout != null) {
      return this.mLayout.getBaseline();
    }
    return super.getBaseline();
  }
  
  long getChangedHolderKey(ViewHolder paramViewHolder)
  {
    if (this.mAdapter.hasStableIds()) {
      return paramViewHolder.getItemId();
    }
    return paramViewHolder.mPosition;
  }
  
  public int getChildAdapterPosition(View paramView)
  {
    ViewHolder localViewHolder = getChildViewHolderInt(paramView);
    if (localViewHolder != null) {
      return localViewHolder.getAdapterPosition();
    }
    return -1;
  }
  
  protected int getChildDrawingOrder(int paramInt1, int paramInt2)
  {
    if (this.mChildDrawingOrderCallback == null) {
      return super.getChildDrawingOrder(paramInt1, paramInt2);
    }
    return this.mChildDrawingOrderCallback.onGetChildDrawingOrder(paramInt1, paramInt2);
  }
  
  public long getChildItemId(View paramView)
  {
    if ((this.mAdapter == null) || (!this.mAdapter.hasStableIds())) {}
    ViewHolder localViewHolder;
    do
    {
      return -1L;
      localViewHolder = getChildViewHolderInt(paramView);
    } while (localViewHolder == null);
    return localViewHolder.getItemId();
  }
  
  public int getChildLayoutPosition(View paramView)
  {
    ViewHolder localViewHolder = getChildViewHolderInt(paramView);
    if (localViewHolder != null) {
      return localViewHolder.getLayoutPosition();
    }
    return -1;
  }
  
  @Deprecated
  public int getChildPosition(View paramView)
  {
    return getChildAdapterPosition(paramView);
  }
  
  public ViewHolder getChildViewHolder(View paramView)
  {
    ViewParent localViewParent = paramView.getParent();
    if ((localViewParent != null) && (localViewParent != this)) {
      throw new IllegalArgumentException("View " + paramView + " is not a direct child of " + this);
    }
    return getChildViewHolderInt(paramView);
  }
  
  public boolean getClipToPadding()
  {
    return this.mClipToPadding;
  }
  
  public RecyclerViewAccessibilityDelegate getCompatAccessibilityDelegate()
  {
    return this.mAccessibilityDelegate;
  }
  
  public void getDecoratedBoundsWithMargins(View paramView, Rect paramRect)
  {
    getDecoratedBoundsWithMarginsInt(paramView, paramRect);
  }
  
  public ItemAnimator getItemAnimator()
  {
    return this.mItemAnimator;
  }
  
  Rect getItemDecorInsetsForChild(View paramView)
  {
    LayoutParams localLayoutParams = (LayoutParams)paramView.getLayoutParams();
    if (!localLayoutParams.mInsetsDirty) {
      return localLayoutParams.mDecorInsets;
    }
    if ((this.mState.isPreLayout()) && ((localLayoutParams.isItemChanged()) || (localLayoutParams.isViewInvalid()))) {
      return localLayoutParams.mDecorInsets;
    }
    Rect localRect = localLayoutParams.mDecorInsets;
    localRect.set(0, 0, 0, 0);
    int i = this.mItemDecorations.size();
    for (int j = 0; j < i; j++)
    {
      this.mTempRect.set(0, 0, 0, 0);
      ((ItemDecoration)this.mItemDecorations.get(j)).getItemOffsets(this.mTempRect, paramView, this, this.mState);
      localRect.left += this.mTempRect.left;
      localRect.top += this.mTempRect.top;
      localRect.right += this.mTempRect.right;
      localRect.bottom += this.mTempRect.bottom;
    }
    localLayoutParams.mInsetsDirty = false;
    return localRect;
  }
  
  public LayoutManager getLayoutManager()
  {
    return this.mLayout;
  }
  
  public int getMaxFlingVelocity()
  {
    return this.mMaxFlingVelocity;
  }
  
  public int getMinFlingVelocity()
  {
    return this.mMinFlingVelocity;
  }
  
  long getNanoTime()
  {
    if (ALLOW_THREAD_GAP_WORK) {
      return System.nanoTime();
    }
    return 0L;
  }
  
  @Nullable
  public OnFlingListener getOnFlingListener()
  {
    return this.mOnFlingListener;
  }
  
  public boolean getPreserveFocusAfterLayout()
  {
    return this.mPreserveFocusAfterLayout;
  }
  
  public RecycledViewPool getRecycledViewPool()
  {
    return this.mRecycler.getRecycledViewPool();
  }
  
  public int getScrollState()
  {
    return this.mScrollState;
  }
  
  public boolean hasFixedSize()
  {
    return this.mHasFixedSize;
  }
  
  public boolean hasNestedScrollingParent()
  {
    return getScrollingChildHelper().hasNestedScrollingParent();
  }
  
  public boolean hasPendingAdapterUpdates()
  {
    return (!this.mFirstLayoutComplete) || (this.mDataSetHasChangedAfterLayout) || (this.mAdapterHelper.hasPendingUpdates());
  }
  
  void initAdapterManager()
  {
    this.mAdapterHelper = new AdapterHelper(new AdapterHelper.Callback()
    {
      void dispatchUpdate(AdapterHelper.UpdateOp paramAnonymousUpdateOp)
      {
        switch (paramAnonymousUpdateOp.cmd)
        {
        case 3: 
        case 5: 
        case 6: 
        case 7: 
        default: 
          return;
        case 1: 
          RecyclerView.this.mLayout.onItemsAdded(RecyclerView.this, paramAnonymousUpdateOp.positionStart, paramAnonymousUpdateOp.itemCount);
          return;
        case 2: 
          RecyclerView.this.mLayout.onItemsRemoved(RecyclerView.this, paramAnonymousUpdateOp.positionStart, paramAnonymousUpdateOp.itemCount);
          return;
        case 4: 
          RecyclerView.this.mLayout.onItemsUpdated(RecyclerView.this, paramAnonymousUpdateOp.positionStart, paramAnonymousUpdateOp.itemCount, paramAnonymousUpdateOp.payload);
          return;
        }
        RecyclerView.this.mLayout.onItemsMoved(RecyclerView.this, paramAnonymousUpdateOp.positionStart, paramAnonymousUpdateOp.itemCount, 1);
      }
      
      public RecyclerView.ViewHolder findViewHolder(int paramAnonymousInt)
      {
        RecyclerView.ViewHolder localViewHolder = RecyclerView.this.findViewHolderForPosition(paramAnonymousInt, true);
        if (localViewHolder == null) {
          localViewHolder = null;
        }
        while (!RecyclerView.this.mChildHelper.isHidden(localViewHolder.itemView)) {
          return localViewHolder;
        }
        return null;
      }
      
      public void markViewHoldersUpdated(int paramAnonymousInt1, int paramAnonymousInt2, Object paramAnonymousObject)
      {
        RecyclerView.this.viewRangeUpdate(paramAnonymousInt1, paramAnonymousInt2, paramAnonymousObject);
        RecyclerView.this.mItemsChanged = true;
      }
      
      public void offsetPositionsForAdd(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        RecyclerView.this.offsetPositionRecordsForInsert(paramAnonymousInt1, paramAnonymousInt2);
        RecyclerView.this.mItemsAddedOrRemoved = true;
      }
      
      public void offsetPositionsForMove(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        RecyclerView.this.offsetPositionRecordsForMove(paramAnonymousInt1, paramAnonymousInt2);
        RecyclerView.this.mItemsAddedOrRemoved = true;
      }
      
      public void offsetPositionsForRemovingInvisible(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        RecyclerView.this.offsetPositionRecordsForRemove(paramAnonymousInt1, paramAnonymousInt2, true);
        RecyclerView.this.mItemsAddedOrRemoved = true;
        RecyclerView.State localState = RecyclerView.this.mState;
        localState.mDeletedInvisibleItemCountSincePreviousLayout = (paramAnonymousInt2 + localState.mDeletedInvisibleItemCountSincePreviousLayout);
      }
      
      public void offsetPositionsForRemovingLaidOutOrNewView(int paramAnonymousInt1, int paramAnonymousInt2)
      {
        RecyclerView.this.offsetPositionRecordsForRemove(paramAnonymousInt1, paramAnonymousInt2, false);
        RecyclerView.this.mItemsAddedOrRemoved = true;
      }
      
      public void onDispatchFirstPass(AdapterHelper.UpdateOp paramAnonymousUpdateOp)
      {
        dispatchUpdate(paramAnonymousUpdateOp);
      }
      
      public void onDispatchSecondPass(AdapterHelper.UpdateOp paramAnonymousUpdateOp)
      {
        dispatchUpdate(paramAnonymousUpdateOp);
      }
    });
  }
  
  void invalidateGlows()
  {
    this.mBottomGlow = null;
    this.mTopGlow = null;
    this.mRightGlow = null;
    this.mLeftGlow = null;
  }
  
  public void invalidateItemDecorations()
  {
    if (this.mItemDecorations.size() == 0) {
      return;
    }
    if (this.mLayout != null) {
      this.mLayout.assertNotInLayoutOrScroll("Cannot invalidate item decorations during a scroll or layout");
    }
    markItemDecorInsetsDirty();
    requestLayout();
  }
  
  boolean isAccessibilityEnabled()
  {
    return (this.mAccessibilityManager != null) && (this.mAccessibilityManager.isEnabled());
  }
  
  public boolean isAnimating()
  {
    return (this.mItemAnimator != null) && (this.mItemAnimator.isRunning());
  }
  
  public boolean isAttachedToWindow()
  {
    return this.mIsAttached;
  }
  
  public boolean isComputingLayout()
  {
    return this.mLayoutOrScrollCounter > 0;
  }
  
  public boolean isLayoutFrozen()
  {
    return this.mLayoutFrozen;
  }
  
  public boolean isNestedScrollingEnabled()
  {
    return getScrollingChildHelper().isNestedScrollingEnabled();
  }
  
  void jumpToPositionForSmoothScroller(int paramInt)
  {
    if (this.mLayout == null) {
      return;
    }
    this.mLayout.scrollToPosition(paramInt);
    awakenScrollBars();
  }
  
  void markItemDecorInsetsDirty()
  {
    int i = this.mChildHelper.getUnfilteredChildCount();
    for (int j = 0; j < i; j++) {
      ((LayoutParams)this.mChildHelper.getUnfilteredChildAt(j).getLayoutParams()).mInsetsDirty = true;
    }
    this.mRecycler.markItemDecorInsetsDirty();
  }
  
  void markKnownViewsInvalid()
  {
    int i = this.mChildHelper.getUnfilteredChildCount();
    for (int j = 0; j < i; j++)
    {
      ViewHolder localViewHolder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(j));
      if ((localViewHolder != null) && (!localViewHolder.shouldIgnore())) {
        localViewHolder.addFlags(6);
      }
    }
    markItemDecorInsetsDirty();
    this.mRecycler.markKnownViewsInvalid();
  }
  
  public void offsetChildrenHorizontal(int paramInt)
  {
    int i = this.mChildHelper.getChildCount();
    for (int j = 0; j < i; j++) {
      this.mChildHelper.getChildAt(j).offsetLeftAndRight(paramInt);
    }
  }
  
  public void offsetChildrenVertical(int paramInt)
  {
    int i = this.mChildHelper.getChildCount();
    for (int j = 0; j < i; j++) {
      this.mChildHelper.getChildAt(j).offsetTopAndBottom(paramInt);
    }
  }
  
  void offsetPositionRecordsForInsert(int paramInt1, int paramInt2)
  {
    int i = this.mChildHelper.getUnfilteredChildCount();
    for (int j = 0; j < i; j++)
    {
      ViewHolder localViewHolder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(j));
      if ((localViewHolder != null) && (!localViewHolder.shouldIgnore()) && (localViewHolder.mPosition >= paramInt1))
      {
        localViewHolder.offsetPosition(paramInt2, false);
        this.mState.mStructureChanged = true;
      }
    }
    this.mRecycler.offsetPositionRecordsForInsert(paramInt1, paramInt2);
    requestLayout();
  }
  
  void offsetPositionRecordsForMove(int paramInt1, int paramInt2)
  {
    int i = this.mChildHelper.getUnfilteredChildCount();
    int j;
    int k;
    if (paramInt1 < paramInt2)
    {
      j = paramInt1;
      k = paramInt2;
    }
    ViewHolder localViewHolder;
    for (int m = -1;; m = 1)
    {
      for (int n = 0;; n++)
      {
        if (n >= i) {
          break label128;
        }
        localViewHolder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(n));
        if ((localViewHolder != null) && (localViewHolder.mPosition >= j) && (localViewHolder.mPosition <= k)) {
          break;
        }
      }
      j = paramInt2;
      k = paramInt1;
    }
    if (localViewHolder.mPosition == paramInt1) {
      localViewHolder.offsetPosition(paramInt2 - paramInt1, false);
    }
    for (;;)
    {
      this.mState.mStructureChanged = true;
      break;
      localViewHolder.offsetPosition(m, false);
    }
    label128:
    this.mRecycler.offsetPositionRecordsForMove(paramInt1, paramInt2);
    requestLayout();
  }
  
  void offsetPositionRecordsForRemove(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int i = paramInt1 + paramInt2;
    int j = this.mChildHelper.getUnfilteredChildCount();
    int k = 0;
    if (k < j)
    {
      ViewHolder localViewHolder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(k));
      if ((localViewHolder != null) && (!localViewHolder.shouldIgnore()))
      {
        if (localViewHolder.mPosition < i) {
          break label83;
        }
        localViewHolder.offsetPosition(-paramInt2, paramBoolean);
        this.mState.mStructureChanged = true;
      }
      for (;;)
      {
        k++;
        break;
        label83:
        if (localViewHolder.mPosition >= paramInt1)
        {
          localViewHolder.flagRemovedAndOffsetPosition(paramInt1 - 1, -paramInt2, paramBoolean);
          this.mState.mStructureChanged = true;
        }
      }
    }
    this.mRecycler.offsetPositionRecordsForRemove(paramInt1, paramInt2, paramBoolean);
    requestLayout();
  }
  
  protected void onAttachedToWindow()
  {
    boolean bool = true;
    super.onAttachedToWindow();
    this.mLayoutOrScrollCounter = 0;
    this.mIsAttached = bool;
    if ((this.mFirstLayoutComplete) && (!isLayoutRequested())) {}
    for (;;)
    {
      this.mFirstLayoutComplete = bool;
      if (this.mLayout != null) {
        this.mLayout.dispatchAttachedToWindow(this);
      }
      this.mPostedAnimatorRunner = false;
      if (ALLOW_THREAD_GAP_WORK)
      {
        this.mGapWorker = ((GapWorker)GapWorker.sGapWorker.get());
        if (this.mGapWorker == null)
        {
          this.mGapWorker = new GapWorker();
          Display localDisplay = ViewCompat.getDisplay(this);
          float f1 = 60.0F;
          if ((!isInEditMode()) && (localDisplay != null))
          {
            float f2 = localDisplay.getRefreshRate();
            if (f2 >= 30.0F) {
              f1 = f2;
            }
          }
          this.mGapWorker.mFrameIntervalNs = ((1.0E9F / f1));
          GapWorker.sGapWorker.set(this.mGapWorker);
        }
        this.mGapWorker.add(this);
      }
      return;
      bool = false;
    }
  }
  
  public void onChildAttachedToWindow(View paramView) {}
  
  public void onChildDetachedFromWindow(View paramView) {}
  
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
    if (this.mItemAnimator != null) {
      this.mItemAnimator.endAnimations();
    }
    stopScroll();
    this.mIsAttached = false;
    if (this.mLayout != null) {
      this.mLayout.dispatchDetachedFromWindow(this, this.mRecycler);
    }
    this.mPendingAccessibilityImportanceChange.clear();
    removeCallbacks(this.mItemAnimatorRunner);
    this.mViewInfoStore.onDetach();
    if (ALLOW_THREAD_GAP_WORK)
    {
      this.mGapWorker.remove(this);
      this.mGapWorker = null;
    }
  }
  
  public void onDraw(Canvas paramCanvas)
  {
    super.onDraw(paramCanvas);
    int i = this.mItemDecorations.size();
    for (int j = 0; j < i; j++) {
      ((ItemDecoration)this.mItemDecorations.get(j)).onDraw(paramCanvas, this, this.mState);
    }
  }
  
  void onEnterLayoutOrScroll()
  {
    this.mLayoutOrScrollCounter = (1 + this.mLayoutOrScrollCounter);
  }
  
  void onExitLayoutOrScroll()
  {
    this.mLayoutOrScrollCounter = (-1 + this.mLayoutOrScrollCounter);
    if (this.mLayoutOrScrollCounter < 1)
    {
      this.mLayoutOrScrollCounter = 0;
      dispatchContentChangedIfNecessary();
      dispatchPendingImportantForAccessibilityChanges();
    }
  }
  
  public boolean onGenericMotionEvent(MotionEvent paramMotionEvent)
  {
    if (this.mLayout == null) {}
    label110:
    label113:
    for (;;)
    {
      return false;
      if ((!this.mLayoutFrozen) && ((0x2 & paramMotionEvent.getSource()) != 0) && (paramMotionEvent.getAction() == 8))
      {
        float f1;
        if (this.mLayout.canScrollVertically())
        {
          f1 = -MotionEventCompat.getAxisValue(paramMotionEvent, 9);
          if (!this.mLayout.canScrollHorizontally()) {
            break label110;
          }
        }
        for (float f2 = MotionEventCompat.getAxisValue(paramMotionEvent, 10);; f2 = 0.0F)
        {
          if ((f1 == 0.0F) && (f2 == 0.0F)) {
            break label113;
          }
          float f3 = getScrollFactor();
          scrollByInternal((int)(f2 * f3), (int)(f1 * f3), paramMotionEvent);
          return false;
          f1 = 0.0F;
          break;
        }
      }
    }
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent)
  {
    if (this.mLayoutFrozen) {
      return false;
    }
    if (dispatchOnItemTouchIntercept(paramMotionEvent))
    {
      cancelTouch();
      return true;
    }
    if (this.mLayout == null) {
      return false;
    }
    boolean bool1 = this.mLayout.canScrollHorizontally();
    boolean bool2 = this.mLayout.canScrollVertically();
    if (this.mVelocityTracker == null) {
      this.mVelocityTracker = VelocityTracker.obtain();
    }
    this.mVelocityTracker.addMovement(paramMotionEvent);
    int i = MotionEventCompat.getActionMasked(paramMotionEvent);
    int j = MotionEventCompat.getActionIndex(paramMotionEvent);
    switch (i)
    {
    }
    while (this.mScrollState == 1)
    {
      return true;
      if (this.mIgnoreMotionEventTillDown) {
        this.mIgnoreMotionEventTillDown = false;
      }
      this.mScrollPointerId = paramMotionEvent.getPointerId(0);
      int i14 = (int)(0.5F + paramMotionEvent.getX());
      this.mLastTouchX = i14;
      this.mInitialTouchX = i14;
      int i15 = (int)(0.5F + paramMotionEvent.getY());
      this.mLastTouchY = i15;
      this.mInitialTouchY = i15;
      if (this.mScrollState == 2)
      {
        getParent().requestDisallowInterceptTouchEvent(true);
        setScrollState(1);
      }
      int[] arrayOfInt = this.mNestedOffsets;
      this.mNestedOffsets[1] = 0;
      arrayOfInt[0] = 0;
      int i16 = 0;
      if (bool1) {
        i16 = 0x0 | 0x1;
      }
      if (bool2) {
        i16 |= 0x2;
      }
      startNestedScroll(i16);
      continue;
      this.mScrollPointerId = paramMotionEvent.getPointerId(j);
      int i12 = (int)(0.5F + paramMotionEvent.getX(j));
      this.mLastTouchX = i12;
      this.mInitialTouchX = i12;
      int i13 = (int)(0.5F + paramMotionEvent.getY(j));
      this.mLastTouchY = i13;
      this.mInitialTouchY = i13;
      continue;
      int k = paramMotionEvent.findPointerIndex(this.mScrollPointerId);
      if (k < 0)
      {
        Log.e("RecyclerView", "Error processing scroll; pointer index for id " + this.mScrollPointerId + " not found. Did any MotionEvents get skipped?");
        return false;
      }
      int m = (int)(0.5F + paramMotionEvent.getX(k));
      int n = (int)(0.5F + paramMotionEvent.getY(k));
      if (this.mScrollState != 1)
      {
        int i1 = m - this.mInitialTouchX;
        int i2 = n - this.mInitialTouchY;
        int i3 = 0;
        int i11;
        if (bool1)
        {
          int i7 = Math.abs(i1);
          int i8 = this.mTouchSlop;
          i3 = 0;
          if (i7 > i8)
          {
            int i9 = this.mInitialTouchX;
            int i10 = this.mTouchSlop;
            if (i1 >= 0) {
              break label574;
            }
            i11 = -1;
            label495:
            this.mLastTouchX = (i9 + i11 * i10);
            i3 = 1;
          }
        }
        int i4;
        int i5;
        if ((bool2) && (Math.abs(i2) > this.mTouchSlop))
        {
          i4 = this.mInitialTouchY;
          i5 = this.mTouchSlop;
          if (i2 >= 0) {
            break label580;
          }
        }
        label574:
        label580:
        for (int i6 = -1;; i6 = 1)
        {
          this.mLastTouchY = (i4 + i6 * i5);
          i3 = 1;
          if (i3 == 0) {
            break;
          }
          setScrollState(1);
          break;
          i11 = 1;
          break label495;
        }
        onPointerUp(paramMotionEvent);
        continue;
        this.mVelocityTracker.clear();
        stopNestedScroll();
        continue;
        cancelTouch();
      }
    }
    return false;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    TraceCompat.beginSection("RV OnLayout");
    dispatchLayout();
    TraceCompat.endSection();
    this.mFirstLayoutComplete = true;
  }
  
  protected void onMeasure(int paramInt1, int paramInt2)
  {
    if (this.mLayout == null) {
      defaultOnMeasure(paramInt1, paramInt2);
    }
    do
    {
      int k;
      do
      {
        return;
        if (!this.mLayout.mAutoMeasure) {
          break;
        }
        int i = View.MeasureSpec.getMode(paramInt1);
        int j = View.MeasureSpec.getMode(paramInt2);
        k = 0;
        if (i == 1073741824)
        {
          k = 0;
          if (j == 1073741824) {
            k = 1;
          }
        }
        this.mLayout.onMeasure(this.mRecycler, this.mState, paramInt1, paramInt2);
      } while ((k != 0) || (this.mAdapter == null));
      if (this.mState.mLayoutStep == 1) {
        dispatchLayoutStep1();
      }
      this.mLayout.setMeasureSpecs(paramInt1, paramInt2);
      this.mState.mIsMeasuring = true;
      dispatchLayoutStep2();
      this.mLayout.setMeasuredDimensionFromChildren(paramInt1, paramInt2);
    } while (!this.mLayout.shouldMeasureTwice());
    this.mLayout.setMeasureSpecs(View.MeasureSpec.makeMeasureSpec(getMeasuredWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(getMeasuredHeight(), 1073741824));
    this.mState.mIsMeasuring = true;
    dispatchLayoutStep2();
    this.mLayout.setMeasuredDimensionFromChildren(paramInt1, paramInt2);
    return;
    if (this.mHasFixedSize)
    {
      this.mLayout.onMeasure(this.mRecycler, this.mState, paramInt1, paramInt2);
      return;
    }
    if (this.mAdapterUpdateDuringMeasure)
    {
      eatRequestLayout();
      onEnterLayoutOrScroll();
      processAdapterUpdatesAndSetAnimationFlags();
      onExitLayoutOrScroll();
      if (this.mState.mRunPredictiveAnimations)
      {
        this.mState.mInPreLayout = true;
        this.mAdapterUpdateDuringMeasure = false;
        resumeRequestLayout(false);
      }
    }
    else
    {
      if (this.mAdapter == null) {
        break label342;
      }
    }
    label342:
    for (this.mState.mItemCount = this.mAdapter.getItemCount();; this.mState.mItemCount = 0)
    {
      eatRequestLayout();
      this.mLayout.onMeasure(this.mRecycler, this.mState, paramInt1, paramInt2);
      resumeRequestLayout(false);
      this.mState.mInPreLayout = false;
      return;
      this.mAdapterHelper.consumeUpdatesInOnePass();
      this.mState.mInPreLayout = false;
      break;
    }
  }
  
  protected boolean onRequestFocusInDescendants(int paramInt, Rect paramRect)
  {
    if (isComputingLayout()) {
      return false;
    }
    return super.onRequestFocusInDescendants(paramInt, paramRect);
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable)
  {
    if (!(paramParcelable instanceof SavedState)) {
      super.onRestoreInstanceState(paramParcelable);
    }
    do
    {
      return;
      this.mPendingSavedState = ((SavedState)paramParcelable);
      super.onRestoreInstanceState(this.mPendingSavedState.getSuperState());
    } while ((this.mLayout == null) || (this.mPendingSavedState.mLayoutState == null));
    this.mLayout.onRestoreInstanceState(this.mPendingSavedState.mLayoutState);
  }
  
  protected Parcelable onSaveInstanceState()
  {
    SavedState localSavedState = new SavedState(super.onSaveInstanceState());
    if (this.mPendingSavedState != null)
    {
      localSavedState.copyFrom(this.mPendingSavedState);
      return localSavedState;
    }
    if (this.mLayout != null)
    {
      localSavedState.mLayoutState = this.mLayout.onSaveInstanceState();
      return localSavedState;
    }
    localSavedState.mLayoutState = null;
    return localSavedState;
  }
  
  public void onScrollStateChanged(int paramInt) {}
  
  public void onScrolled(int paramInt1, int paramInt2) {}
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    if ((paramInt1 != paramInt3) || (paramInt2 != paramInt4)) {
      invalidateGlows();
    }
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent)
  {
    if ((this.mLayoutFrozen) || (this.mIgnoreMotionEventTillDown)) {
      return false;
    }
    if (dispatchOnItemTouch(paramMotionEvent))
    {
      cancelTouch();
      return true;
    }
    if (this.mLayout == null) {
      return false;
    }
    boolean bool1 = this.mLayout.canScrollHorizontally();
    boolean bool2 = this.mLayout.canScrollVertically();
    if (this.mVelocityTracker == null) {
      this.mVelocityTracker = VelocityTracker.obtain();
    }
    MotionEvent localMotionEvent = MotionEvent.obtain(paramMotionEvent);
    int i = MotionEventCompat.getActionMasked(paramMotionEvent);
    int j = MotionEventCompat.getActionIndex(paramMotionEvent);
    if (i == 0)
    {
      int[] arrayOfInt3 = this.mNestedOffsets;
      this.mNestedOffsets[1] = 0;
      arrayOfInt3[0] = 0;
    }
    localMotionEvent.offsetLocation(this.mNestedOffsets[0], this.mNestedOffsets[1]);
    int k = 0;
    switch (i)
    {
    }
    for (;;)
    {
      if (k == 0) {
        this.mVelocityTracker.addMovement(localMotionEvent);
      }
      localMotionEvent.recycle();
      return true;
      this.mScrollPointerId = paramMotionEvent.getPointerId(0);
      int i12 = (int)(0.5F + paramMotionEvent.getX());
      this.mLastTouchX = i12;
      this.mInitialTouchX = i12;
      int i13 = (int)(0.5F + paramMotionEvent.getY());
      this.mLastTouchY = i13;
      this.mInitialTouchY = i13;
      int i14 = 0;
      if (bool1) {
        i14 = 0x0 | 0x1;
      }
      if (bool2) {
        i14 |= 0x2;
      }
      startNestedScroll(i14);
      k = 0;
      continue;
      this.mScrollPointerId = paramMotionEvent.getPointerId(j);
      int i10 = (int)(0.5F + paramMotionEvent.getX(j));
      this.mLastTouchX = i10;
      this.mInitialTouchX = i10;
      int i11 = (int)(0.5F + paramMotionEvent.getY(j));
      this.mLastTouchY = i11;
      this.mInitialTouchY = i11;
      k = 0;
      continue;
      int m = paramMotionEvent.findPointerIndex(this.mScrollPointerId);
      if (m < 0)
      {
        Log.e("RecyclerView", "Error processing scroll; pointer index for id " + this.mScrollPointerId + " not found. Did any MotionEvents get skipped?");
        return false;
      }
      int n = (int)(0.5F + paramMotionEvent.getX(m));
      int i1 = (int)(0.5F + paramMotionEvent.getY(m));
      int i2 = this.mLastTouchX - n;
      int i3 = this.mLastTouchY - i1;
      if (dispatchNestedPreScroll(i2, i3, this.mScrollConsumed, this.mScrollOffset))
      {
        i2 -= this.mScrollConsumed[0];
        i3 -= this.mScrollConsumed[1];
        localMotionEvent.offsetLocation(this.mScrollOffset[0], this.mScrollOffset[1]);
        int[] arrayOfInt1 = this.mNestedOffsets;
        arrayOfInt1[0] += this.mScrollOffset[0];
        int[] arrayOfInt2 = this.mNestedOffsets;
        arrayOfInt2[1] += this.mScrollOffset[1];
      }
      if (this.mScrollState != 1)
      {
        int i4 = 0;
        if (bool1)
        {
          int i8 = Math.abs(i2);
          int i9 = this.mTouchSlop;
          i4 = 0;
          if (i8 > i9)
          {
            if (i2 <= 0) {
              break label774;
            }
            i2 -= this.mTouchSlop;
            label603:
            i4 = 1;
          }
        }
        if ((bool2) && (Math.abs(i3) > this.mTouchSlop))
        {
          if (i3 <= 0) {
            break label786;
          }
          i3 -= this.mTouchSlop;
          label636:
          i4 = 1;
        }
        if (i4 != 0) {
          setScrollState(1);
        }
      }
      int i5 = this.mScrollState;
      k = 0;
      if (i5 == 1)
      {
        this.mLastTouchX = (n - this.mScrollOffset[0]);
        this.mLastTouchY = (i1 - this.mScrollOffset[1]);
        int i6;
        if (bool1)
        {
          i6 = i2;
          label698:
          if (!bool2) {
            break label804;
          }
        }
        label774:
        label786:
        label804:
        for (int i7 = i3;; i7 = 0)
        {
          if (scrollByInternal(i6, i7, localMotionEvent)) {
            getParent().requestDisallowInterceptTouchEvent(true);
          }
          GapWorker localGapWorker = this.mGapWorker;
          k = 0;
          if (localGapWorker == null) {
            break;
          }
          if (i2 == 0)
          {
            k = 0;
            if (i3 == 0) {
              break;
            }
          }
          this.mGapWorker.postFromTraversal(this, i2, i3);
          k = 0;
          break;
          i2 += this.mTouchSlop;
          break label603;
          i3 += this.mTouchSlop;
          break label636;
          i6 = 0;
          break label698;
        }
        onPointerUp(paramMotionEvent);
        k = 0;
        continue;
        this.mVelocityTracker.addMovement(localMotionEvent);
        k = 1;
        this.mVelocityTracker.computeCurrentVelocity(1000, this.mMaxFlingVelocity);
        float f1;
        if (bool1)
        {
          f1 = -VelocityTrackerCompat.getXVelocity(this.mVelocityTracker, this.mScrollPointerId);
          label866:
          if (!bool2) {
            break label929;
          }
        }
        label929:
        for (float f2 = -VelocityTrackerCompat.getYVelocity(this.mVelocityTracker, this.mScrollPointerId);; f2 = 0.0F)
        {
          if (((f1 == 0.0F) && (f2 == 0.0F)) || (!fling((int)f1, (int)f2))) {
            setScrollState(0);
          }
          resetTouch();
          break;
          f1 = 0.0F;
          break label866;
        }
        cancelTouch();
        k = 0;
      }
    }
  }
  
  void postAnimationRunner()
  {
    if ((!this.mPostedAnimatorRunner) && (this.mIsAttached))
    {
      ViewCompat.postOnAnimation(this, this.mItemAnimatorRunner);
      this.mPostedAnimatorRunner = true;
    }
  }
  
  void recordAnimationInfoIfBouncedHiddenView(ViewHolder paramViewHolder, RecyclerView.ItemAnimator.ItemHolderInfo paramItemHolderInfo)
  {
    paramViewHolder.setFlags(0, 8192);
    if ((this.mState.mTrackOldChangeHolders) && (paramViewHolder.isUpdated()) && (!paramViewHolder.isRemoved()) && (!paramViewHolder.shouldIgnore()))
    {
      long l = getChangedHolderKey(paramViewHolder);
      this.mViewInfoStore.addToOldChangeHolders(l, paramViewHolder);
    }
    this.mViewInfoStore.addToPreLayout(paramViewHolder, paramItemHolderInfo);
  }
  
  void removeAndRecycleViews()
  {
    if (this.mItemAnimator != null) {
      this.mItemAnimator.endAnimations();
    }
    if (this.mLayout != null)
    {
      this.mLayout.removeAndRecycleAllViews(this.mRecycler);
      this.mLayout.removeAndRecycleScrapInt(this.mRecycler);
    }
    this.mRecycler.clear();
  }
  
  boolean removeAnimatingView(View paramView)
  {
    eatRequestLayout();
    boolean bool1 = this.mChildHelper.removeViewIfHidden(paramView);
    if (bool1)
    {
      ViewHolder localViewHolder = getChildViewHolderInt(paramView);
      this.mRecycler.unscrapView(localViewHolder);
      this.mRecycler.recycleViewHolderInternal(localViewHolder);
    }
    if (!bool1) {}
    for (boolean bool2 = true;; bool2 = false)
    {
      resumeRequestLayout(bool2);
      return bool1;
    }
  }
  
  protected void removeDetachedView(View paramView, boolean paramBoolean)
  {
    ViewHolder localViewHolder = getChildViewHolderInt(paramView);
    if (localViewHolder != null)
    {
      if (!localViewHolder.isTmpDetached()) {
        break label32;
      }
      localViewHolder.clearTmpDetachFlag();
    }
    label32:
    while (localViewHolder.shouldIgnore())
    {
      dispatchChildDetached(paramView);
      super.removeDetachedView(paramView, paramBoolean);
      return;
    }
    throw new IllegalArgumentException("Called removeDetachedView with a view which is not flagged as tmp detached." + localViewHolder);
  }
  
  public void removeItemDecoration(ItemDecoration paramItemDecoration)
  {
    if (this.mLayout != null) {
      this.mLayout.assertNotInLayoutOrScroll("Cannot remove item decoration during a scroll  or layout");
    }
    this.mItemDecorations.remove(paramItemDecoration);
    if (this.mItemDecorations.isEmpty()) {
      if (getOverScrollMode() != 2) {
        break label60;
      }
    }
    label60:
    for (boolean bool = true;; bool = false)
    {
      setWillNotDraw(bool);
      markItemDecorInsetsDirty();
      requestLayout();
      return;
    }
  }
  
  public void removeOnChildAttachStateChangeListener(OnChildAttachStateChangeListener paramOnChildAttachStateChangeListener)
  {
    if (this.mOnChildAttachStateListeners == null) {
      return;
    }
    this.mOnChildAttachStateListeners.remove(paramOnChildAttachStateChangeListener);
  }
  
  public void removeOnItemTouchListener(OnItemTouchListener paramOnItemTouchListener)
  {
    this.mOnItemTouchListeners.remove(paramOnItemTouchListener);
    if (this.mActiveOnItemTouchListener == paramOnItemTouchListener) {
      this.mActiveOnItemTouchListener = null;
    }
  }
  
  public void removeOnScrollListener(OnScrollListener paramOnScrollListener)
  {
    if (this.mScrollListeners != null) {
      this.mScrollListeners.remove(paramOnScrollListener);
    }
  }
  
  void repositionShadowingViews()
  {
    int i = this.mChildHelper.getChildCount();
    for (int j = 0; j < i; j++)
    {
      View localView1 = this.mChildHelper.getChildAt(j);
      ViewHolder localViewHolder = getChildViewHolder(localView1);
      if ((localViewHolder != null) && (localViewHolder.mShadowingHolder != null))
      {
        View localView2 = localViewHolder.mShadowingHolder.itemView;
        int k = localView1.getLeft();
        int m = localView1.getTop();
        if ((k != localView2.getLeft()) || (m != localView2.getTop())) {
          localView2.layout(k, m, k + localView2.getWidth(), m + localView2.getHeight());
        }
      }
    }
  }
  
  public void requestChildFocus(View paramView1, View paramView2)
  {
    if ((!this.mLayout.onRequestChildFocus(this, this.mState, paramView1, paramView2)) && (paramView2 != null)) {
      requestChildOnScreen(paramView1, paramView2);
    }
    super.requestChildFocus(paramView1, paramView2);
  }
  
  public boolean requestChildRectangleOnScreen(View paramView, Rect paramRect, boolean paramBoolean)
  {
    return this.mLayout.requestChildRectangleOnScreen(this, paramView, paramRect, paramBoolean);
  }
  
  public void requestDisallowInterceptTouchEvent(boolean paramBoolean)
  {
    int i = this.mOnItemTouchListeners.size();
    for (int j = 0; j < i; j++) {
      ((OnItemTouchListener)this.mOnItemTouchListeners.get(j)).onRequestDisallowInterceptTouchEvent(paramBoolean);
    }
    super.requestDisallowInterceptTouchEvent(paramBoolean);
  }
  
  public void requestLayout()
  {
    if ((this.mEatRequestLayout == 0) && (!this.mLayoutFrozen))
    {
      super.requestLayout();
      return;
    }
    this.mLayoutRequestEaten = true;
  }
  
  void resumeRequestLayout(boolean paramBoolean)
  {
    if (this.mEatRequestLayout < 1) {
      this.mEatRequestLayout = 1;
    }
    if (!paramBoolean) {
      this.mLayoutRequestEaten = false;
    }
    if (this.mEatRequestLayout == 1)
    {
      if ((paramBoolean) && (this.mLayoutRequestEaten) && (!this.mLayoutFrozen) && (this.mLayout != null) && (this.mAdapter != null)) {
        dispatchLayout();
      }
      if (!this.mLayoutFrozen) {
        this.mLayoutRequestEaten = false;
      }
    }
    this.mEatRequestLayout = (-1 + this.mEatRequestLayout);
  }
  
  void saveOldPositions()
  {
    int i = this.mChildHelper.getUnfilteredChildCount();
    for (int j = 0; j < i; j++)
    {
      ViewHolder localViewHolder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(j));
      if (!localViewHolder.shouldIgnore()) {
        localViewHolder.saveOldPosition();
      }
    }
  }
  
  public void scrollBy(int paramInt1, int paramInt2)
  {
    if (this.mLayout == null) {}
    boolean bool1;
    boolean bool2;
    do
    {
      Log.e("RecyclerView", "Cannot scroll without a LayoutManager set. Call setLayoutManager with a non-null argument.");
      do
      {
        return;
      } while (this.mLayoutFrozen);
      bool1 = this.mLayout.canScrollHorizontally();
      bool2 = this.mLayout.canScrollVertically();
    } while ((!bool1) && (!bool2));
    if (bool1) {
      if (!bool2) {
        break label73;
      }
    }
    for (;;)
    {
      scrollByInternal(paramInt1, paramInt2, null);
      return;
      paramInt1 = 0;
      break;
      label73:
      paramInt2 = 0;
    }
  }
  
  boolean scrollByInternal(int paramInt1, int paramInt2, MotionEvent paramMotionEvent)
  {
    consumePendingUpdateOperations();
    Adapter localAdapter = this.mAdapter;
    int i = 0;
    int j = 0;
    int k = 0;
    int m = 0;
    if (localAdapter != null)
    {
      eatRequestLayout();
      onEnterLayoutOrScroll();
      TraceCompat.beginSection("RV Scroll");
      i = 0;
      k = 0;
      if (paramInt1 != 0)
      {
        i = this.mLayout.scrollHorizontallyBy(paramInt1, this.mRecycler, this.mState);
        k = paramInt1 - i;
      }
      j = 0;
      m = 0;
      if (paramInt2 != 0)
      {
        j = this.mLayout.scrollVerticallyBy(paramInt2, this.mRecycler, this.mState);
        m = paramInt2 - j;
      }
      TraceCompat.endSection();
      repositionShadowingViews();
      onExitLayoutOrScroll();
      resumeRequestLayout(false);
    }
    if (!this.mItemDecorations.isEmpty()) {
      invalidate();
    }
    if (dispatchNestedScroll(i, j, k, m, this.mScrollOffset))
    {
      this.mLastTouchX -= this.mScrollOffset[0];
      this.mLastTouchY -= this.mScrollOffset[1];
      if (paramMotionEvent != null) {
        paramMotionEvent.offsetLocation(this.mScrollOffset[0], this.mScrollOffset[1]);
      }
      int[] arrayOfInt1 = this.mNestedOffsets;
      arrayOfInt1[0] += this.mScrollOffset[0];
      int[] arrayOfInt2 = this.mNestedOffsets;
      arrayOfInt2[1] += this.mScrollOffset[1];
    }
    for (;;)
    {
      if ((i != 0) || (j != 0)) {
        dispatchOnScrolled(i, j);
      }
      if (!awakenScrollBars()) {
        invalidate();
      }
      if ((i == 0) && (j == 0)) {
        break;
      }
      return true;
      if (getOverScrollMode() != 2)
      {
        if (paramMotionEvent != null) {
          pullGlows(paramMotionEvent.getX(), k, paramMotionEvent.getY(), m);
        }
        considerReleasingGlowsOnScroll(paramInt1, paramInt2);
      }
    }
    return false;
  }
  
  public void scrollTo(int paramInt1, int paramInt2)
  {
    Log.w("RecyclerView", "RecyclerView does not support scrolling to an absolute position. Use scrollToPosition instead");
  }
  
  public void scrollToPosition(int paramInt)
  {
    if (this.mLayoutFrozen) {
      return;
    }
    stopScroll();
    if (this.mLayout == null)
    {
      Log.e("RecyclerView", "Cannot scroll to position a LayoutManager set. Call setLayoutManager with a non-null argument.");
      return;
    }
    this.mLayout.scrollToPosition(paramInt);
    awakenScrollBars();
  }
  
  public void sendAccessibilityEventUnchecked(AccessibilityEvent paramAccessibilityEvent)
  {
    if (shouldDeferAccessibilityEvent(paramAccessibilityEvent)) {
      return;
    }
    super.sendAccessibilityEventUnchecked(paramAccessibilityEvent);
  }
  
  public void setAccessibilityDelegateCompat(RecyclerViewAccessibilityDelegate paramRecyclerViewAccessibilityDelegate)
  {
    this.mAccessibilityDelegate = paramRecyclerViewAccessibilityDelegate;
    ViewCompat.setAccessibilityDelegate(this, this.mAccessibilityDelegate);
  }
  
  public void setAdapter(Adapter paramAdapter)
  {
    setLayoutFrozen(false);
    setAdapterInternal(paramAdapter, false, true);
    requestLayout();
  }
  
  public void setChildDrawingOrderCallback(ChildDrawingOrderCallback paramChildDrawingOrderCallback)
  {
    if (paramChildDrawingOrderCallback == this.mChildDrawingOrderCallback) {
      return;
    }
    this.mChildDrawingOrderCallback = paramChildDrawingOrderCallback;
    if (this.mChildDrawingOrderCallback != null) {}
    for (boolean bool = true;; bool = false)
    {
      setChildrenDrawingOrderEnabled(bool);
      return;
    }
  }
  
  @VisibleForTesting
  boolean setChildImportantForAccessibilityInternal(ViewHolder paramViewHolder, int paramInt)
  {
    if (isComputingLayout())
    {
      paramViewHolder.mPendingAccessibilityState = paramInt;
      this.mPendingAccessibilityImportanceChange.add(paramViewHolder);
      return false;
    }
    ViewCompat.setImportantForAccessibility(paramViewHolder.itemView, paramInt);
    return true;
  }
  
  public void setClipToPadding(boolean paramBoolean)
  {
    if (paramBoolean != this.mClipToPadding) {
      invalidateGlows();
    }
    this.mClipToPadding = paramBoolean;
    super.setClipToPadding(paramBoolean);
    if (this.mFirstLayoutComplete) {
      requestLayout();
    }
  }
  
  void setDataSetChangedAfterLayout()
  {
    if (this.mDataSetHasChangedAfterLayout) {
      return;
    }
    this.mDataSetHasChangedAfterLayout = true;
    int i = this.mChildHelper.getUnfilteredChildCount();
    for (int j = 0; j < i; j++)
    {
      ViewHolder localViewHolder = getChildViewHolderInt(this.mChildHelper.getUnfilteredChildAt(j));
      if ((localViewHolder != null) && (!localViewHolder.shouldIgnore())) {
        localViewHolder.addFlags(512);
      }
    }
    this.mRecycler.setAdapterPositionsAsUnknown();
    markKnownViewsInvalid();
  }
  
  public void setHasFixedSize(boolean paramBoolean)
  {
    this.mHasFixedSize = paramBoolean;
  }
  
  public void setItemAnimator(ItemAnimator paramItemAnimator)
  {
    if (this.mItemAnimator != null)
    {
      this.mItemAnimator.endAnimations();
      this.mItemAnimator.setListener(null);
    }
    this.mItemAnimator = paramItemAnimator;
    if (this.mItemAnimator != null) {
      this.mItemAnimator.setListener(this.mItemAnimatorListener);
    }
  }
  
  public void setItemViewCacheSize(int paramInt)
  {
    this.mRecycler.setViewCacheSize(paramInt);
  }
  
  public void setLayoutFrozen(boolean paramBoolean)
  {
    if (paramBoolean != this.mLayoutFrozen)
    {
      assertNotInLayoutOrScroll("Do not setLayoutFrozen in layout or scroll");
      if (!paramBoolean)
      {
        this.mLayoutFrozen = false;
        if ((this.mLayoutRequestEaten) && (this.mLayout != null) && (this.mAdapter != null)) {
          requestLayout();
        }
        this.mLayoutRequestEaten = false;
      }
    }
    else
    {
      return;
    }
    long l = SystemClock.uptimeMillis();
    onTouchEvent(MotionEvent.obtain(l, l, 3, 0.0F, 0.0F, 0));
    this.mLayoutFrozen = true;
    this.mIgnoreMotionEventTillDown = true;
    stopScroll();
  }
  
  public void setLayoutManager(LayoutManager paramLayoutManager)
  {
    if (paramLayoutManager == this.mLayout) {
      return;
    }
    stopScroll();
    if (this.mLayout != null)
    {
      if (this.mItemAnimator != null) {
        this.mItemAnimator.endAnimations();
      }
      this.mLayout.removeAndRecycleAllViews(this.mRecycler);
      this.mLayout.removeAndRecycleScrapInt(this.mRecycler);
      this.mRecycler.clear();
      if (this.mIsAttached) {
        this.mLayout.dispatchDetachedFromWindow(this, this.mRecycler);
      }
      this.mLayout.setRecyclerView(null);
      this.mLayout = null;
    }
    for (;;)
    {
      this.mChildHelper.removeAllViewsUnfiltered();
      this.mLayout = paramLayoutManager;
      if (paramLayoutManager == null) {
        break label192;
      }
      if (paramLayoutManager.mRecyclerView == null) {
        break;
      }
      throw new IllegalArgumentException("LayoutManager " + paramLayoutManager + " is already attached to a RecyclerView: " + paramLayoutManager.mRecyclerView);
      this.mRecycler.clear();
    }
    this.mLayout.setRecyclerView(this);
    if (this.mIsAttached) {
      this.mLayout.dispatchAttachedToWindow(this);
    }
    label192:
    this.mRecycler.updateViewCacheSize();
    requestLayout();
  }
  
  public void setNestedScrollingEnabled(boolean paramBoolean)
  {
    getScrollingChildHelper().setNestedScrollingEnabled(paramBoolean);
  }
  
  public void setOnFlingListener(@Nullable OnFlingListener paramOnFlingListener)
  {
    this.mOnFlingListener = paramOnFlingListener;
  }
  
  @Deprecated
  public void setOnScrollListener(OnScrollListener paramOnScrollListener)
  {
    this.mScrollListener = paramOnScrollListener;
  }
  
  public void setPreserveFocusAfterLayout(boolean paramBoolean)
  {
    this.mPreserveFocusAfterLayout = paramBoolean;
  }
  
  public void setRecycledViewPool(RecycledViewPool paramRecycledViewPool)
  {
    this.mRecycler.setRecycledViewPool(paramRecycledViewPool);
  }
  
  public void setRecyclerListener(RecyclerListener paramRecyclerListener)
  {
    this.mRecyclerListener = paramRecyclerListener;
  }
  
  void setScrollState(int paramInt)
  {
    if (paramInt == this.mScrollState) {
      return;
    }
    this.mScrollState = paramInt;
    if (paramInt != 2) {
      stopScrollersInternal();
    }
    dispatchOnScrollStateChanged(paramInt);
  }
  
  public void setScrollingTouchSlop(int paramInt)
  {
    ViewConfiguration localViewConfiguration = ViewConfiguration.get(getContext());
    switch (paramInt)
    {
    default: 
      Log.w("RecyclerView", "setScrollingTouchSlop(): bad argument constant " + paramInt + "; using default value");
    case 0: 
      this.mTouchSlop = localViewConfiguration.getScaledTouchSlop();
      return;
    }
    this.mTouchSlop = localViewConfiguration.getScaledPagingTouchSlop();
  }
  
  public void setViewCacheExtension(ViewCacheExtension paramViewCacheExtension)
  {
    this.mRecycler.setViewCacheExtension(paramViewCacheExtension);
  }
  
  boolean shouldDeferAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
  {
    if (isComputingLayout())
    {
      int i = 0;
      if (paramAccessibilityEvent != null) {
        i = AccessibilityEventCompat.getContentChangeTypes(paramAccessibilityEvent);
      }
      if (i == 0) {
        i = 0;
      }
      this.mEatenAccessibilityChangeFlags = (i | this.mEatenAccessibilityChangeFlags);
      return true;
    }
    return false;
  }
  
  public void smoothScrollBy(int paramInt1, int paramInt2)
  {
    smoothScrollBy(paramInt1, paramInt2, null);
  }
  
  public void smoothScrollBy(int paramInt1, int paramInt2, Interpolator paramInterpolator)
  {
    if (this.mLayout == null) {}
    do
    {
      Log.e("RecyclerView", "Cannot smooth scroll without a LayoutManager set. Call setLayoutManager with a non-null argument.");
      do
      {
        return;
      } while (this.mLayoutFrozen);
      if (!this.mLayout.canScrollHorizontally()) {
        paramInt1 = 0;
      }
      if (!this.mLayout.canScrollVertically()) {
        paramInt2 = 0;
      }
    } while ((paramInt1 == 0) && (paramInt2 == 0));
    this.mViewFlinger.smoothScrollBy(paramInt1, paramInt2, paramInterpolator);
  }
  
  public void smoothScrollToPosition(int paramInt)
  {
    if (this.mLayoutFrozen) {
      return;
    }
    if (this.mLayout == null)
    {
      Log.e("RecyclerView", "Cannot smooth scroll without a LayoutManager set. Call setLayoutManager with a non-null argument.");
      return;
    }
    this.mLayout.smoothScrollToPosition(this, this.mState, paramInt);
  }
  
  public boolean startNestedScroll(int paramInt)
  {
    return getScrollingChildHelper().startNestedScroll(paramInt);
  }
  
  public void stopNestedScroll()
  {
    getScrollingChildHelper().stopNestedScroll();
  }
  
  public void stopScroll()
  {
    setScrollState(0);
    stopScrollersInternal();
  }
  
  public void swapAdapter(Adapter paramAdapter, boolean paramBoolean)
  {
    setLayoutFrozen(false);
    setAdapterInternal(paramAdapter, true, paramBoolean);
    setDataSetChangedAfterLayout();
    requestLayout();
  }
  
  void viewRangeUpdate(int paramInt1, int paramInt2, Object paramObject)
  {
    int i = this.mChildHelper.getUnfilteredChildCount();
    int j = paramInt1 + paramInt2;
    int k = 0;
    if (k < i)
    {
      View localView = this.mChildHelper.getUnfilteredChildAt(k);
      ViewHolder localViewHolder = getChildViewHolderInt(localView);
      if ((localViewHolder == null) || (localViewHolder.shouldIgnore())) {}
      for (;;)
      {
        k++;
        break;
        if ((localViewHolder.mPosition >= paramInt1) && (localViewHolder.mPosition < j))
        {
          localViewHolder.addFlags(2);
          localViewHolder.addChangePayload(paramObject);
          ((LayoutParams)localView.getLayoutParams()).mInsetsDirty = true;
        }
      }
    }
    this.mRecycler.viewRangeUpdate(paramInt1, paramInt2);
  }
  
  public static abstract class Adapter<VH extends RecyclerView.ViewHolder>
  {
    private boolean mHasStableIds = false;
    private final RecyclerView.AdapterDataObservable mObservable = new RecyclerView.AdapterDataObservable();
    
    public Adapter() {}
    
    public final void bindViewHolder(VH paramVH, int paramInt)
    {
      paramVH.mPosition = paramInt;
      if (hasStableIds()) {
        paramVH.mItemId = getItemId(paramInt);
      }
      paramVH.setFlags(1, 519);
      TraceCompat.beginSection("RV OnBindView");
      onBindViewHolder(paramVH, paramInt, paramVH.getUnmodifiedPayloads());
      paramVH.clearPayload();
      ViewGroup.LayoutParams localLayoutParams = paramVH.itemView.getLayoutParams();
      if ((localLayoutParams instanceof RecyclerView.LayoutParams)) {
        ((RecyclerView.LayoutParams)localLayoutParams).mInsetsDirty = true;
      }
      TraceCompat.endSection();
    }
    
    public final VH createViewHolder(ViewGroup paramViewGroup, int paramInt)
    {
      TraceCompat.beginSection("RV CreateView");
      RecyclerView.ViewHolder localViewHolder = onCreateViewHolder(paramViewGroup, paramInt);
      localViewHolder.mItemViewType = paramInt;
      TraceCompat.endSection();
      return localViewHolder;
    }
    
    public abstract int getItemCount();
    
    public long getItemId(int paramInt)
    {
      return -1L;
    }
    
    public int getItemViewType(int paramInt)
    {
      return 0;
    }
    
    public final boolean hasObservers()
    {
      return this.mObservable.hasObservers();
    }
    
    public final boolean hasStableIds()
    {
      return this.mHasStableIds;
    }
    
    public final void notifyDataSetChanged()
    {
      this.mObservable.notifyChanged();
    }
    
    public final void notifyItemChanged(int paramInt)
    {
      this.mObservable.notifyItemRangeChanged(paramInt, 1);
    }
    
    public final void notifyItemChanged(int paramInt, Object paramObject)
    {
      this.mObservable.notifyItemRangeChanged(paramInt, 1, paramObject);
    }
    
    public final void notifyItemInserted(int paramInt)
    {
      this.mObservable.notifyItemRangeInserted(paramInt, 1);
    }
    
    public final void notifyItemMoved(int paramInt1, int paramInt2)
    {
      this.mObservable.notifyItemMoved(paramInt1, paramInt2);
    }
    
    public final void notifyItemRangeChanged(int paramInt1, int paramInt2)
    {
      this.mObservable.notifyItemRangeChanged(paramInt1, paramInt2);
    }
    
    public final void notifyItemRangeChanged(int paramInt1, int paramInt2, Object paramObject)
    {
      this.mObservable.notifyItemRangeChanged(paramInt1, paramInt2, paramObject);
    }
    
    public final void notifyItemRangeInserted(int paramInt1, int paramInt2)
    {
      this.mObservable.notifyItemRangeInserted(paramInt1, paramInt2);
    }
    
    public final void notifyItemRangeRemoved(int paramInt1, int paramInt2)
    {
      this.mObservable.notifyItemRangeRemoved(paramInt1, paramInt2);
    }
    
    public final void notifyItemRemoved(int paramInt)
    {
      this.mObservable.notifyItemRangeRemoved(paramInt, 1);
    }
    
    public void onAttachedToRecyclerView(RecyclerView paramRecyclerView) {}
    
    public abstract void onBindViewHolder(VH paramVH, int paramInt);
    
    public void onBindViewHolder(VH paramVH, int paramInt, List<Object> paramList)
    {
      onBindViewHolder(paramVH, paramInt);
    }
    
    public abstract VH onCreateViewHolder(ViewGroup paramViewGroup, int paramInt);
    
    public void onDetachedFromRecyclerView(RecyclerView paramRecyclerView) {}
    
    public boolean onFailedToRecycleView(VH paramVH)
    {
      return false;
    }
    
    public void onViewAttachedToWindow(VH paramVH) {}
    
    public void onViewDetachedFromWindow(VH paramVH) {}
    
    public void onViewRecycled(VH paramVH) {}
    
    public void registerAdapterDataObserver(RecyclerView.AdapterDataObserver paramAdapterDataObserver)
    {
      this.mObservable.registerObserver(paramAdapterDataObserver);
    }
    
    public void setHasStableIds(boolean paramBoolean)
    {
      if (hasObservers()) {
        throw new IllegalStateException("Cannot change whether this adapter has stable IDs while the adapter has registered observers.");
      }
      this.mHasStableIds = paramBoolean;
    }
    
    public void unregisterAdapterDataObserver(RecyclerView.AdapterDataObserver paramAdapterDataObserver)
    {
      this.mObservable.unregisterObserver(paramAdapterDataObserver);
    }
  }
  
  static class AdapterDataObservable
    extends Observable<RecyclerView.AdapterDataObserver>
  {
    AdapterDataObservable() {}
    
    public boolean hasObservers()
    {
      return !this.mObservers.isEmpty();
    }
    
    public void notifyChanged()
    {
      for (int i = -1 + this.mObservers.size(); i >= 0; i--) {
        ((RecyclerView.AdapterDataObserver)this.mObservers.get(i)).onChanged();
      }
    }
    
    public void notifyItemMoved(int paramInt1, int paramInt2)
    {
      for (int i = -1 + this.mObservers.size(); i >= 0; i--) {
        ((RecyclerView.AdapterDataObserver)this.mObservers.get(i)).onItemRangeMoved(paramInt1, paramInt2, 1);
      }
    }
    
    public void notifyItemRangeChanged(int paramInt1, int paramInt2)
    {
      notifyItemRangeChanged(paramInt1, paramInt2, null);
    }
    
    public void notifyItemRangeChanged(int paramInt1, int paramInt2, Object paramObject)
    {
      for (int i = -1 + this.mObservers.size(); i >= 0; i--) {
        ((RecyclerView.AdapterDataObserver)this.mObservers.get(i)).onItemRangeChanged(paramInt1, paramInt2, paramObject);
      }
    }
    
    public void notifyItemRangeInserted(int paramInt1, int paramInt2)
    {
      for (int i = -1 + this.mObservers.size(); i >= 0; i--) {
        ((RecyclerView.AdapterDataObserver)this.mObservers.get(i)).onItemRangeInserted(paramInt1, paramInt2);
      }
    }
    
    public void notifyItemRangeRemoved(int paramInt1, int paramInt2)
    {
      for (int i = -1 + this.mObservers.size(); i >= 0; i--) {
        ((RecyclerView.AdapterDataObserver)this.mObservers.get(i)).onItemRangeRemoved(paramInt1, paramInt2);
      }
    }
  }
  
  public static abstract class AdapterDataObserver
  {
    public AdapterDataObserver() {}
    
    public void onChanged() {}
    
    public void onItemRangeChanged(int paramInt1, int paramInt2) {}
    
    public void onItemRangeChanged(int paramInt1, int paramInt2, Object paramObject)
    {
      onItemRangeChanged(paramInt1, paramInt2);
    }
    
    public void onItemRangeInserted(int paramInt1, int paramInt2) {}
    
    public void onItemRangeMoved(int paramInt1, int paramInt2, int paramInt3) {}
    
    public void onItemRangeRemoved(int paramInt1, int paramInt2) {}
  }
  
  public static abstract interface ChildDrawingOrderCallback
  {
    public abstract int onGetChildDrawingOrder(int paramInt1, int paramInt2);
  }
  
  public static abstract class ItemAnimator
  {
    public static final int FLAG_APPEARED_IN_PRE_LAYOUT = 4096;
    public static final int FLAG_CHANGED = 2;
    public static final int FLAG_INVALIDATED = 4;
    public static final int FLAG_MOVED = 2048;
    public static final int FLAG_REMOVED = 8;
    private long mAddDuration = 120L;
    private long mChangeDuration = 250L;
    private ArrayList<ItemAnimatorFinishedListener> mFinishedListeners = new ArrayList();
    private ItemAnimatorListener mListener = null;
    private long mMoveDuration = 250L;
    private long mRemoveDuration = 120L;
    
    public ItemAnimator() {}
    
    static int buildAdapterChangeFlagsForAnimations(RecyclerView.ViewHolder paramViewHolder)
    {
      int i = 0xE & RecyclerView.ViewHolder.access$1400(paramViewHolder);
      if (paramViewHolder.isInvalid()) {
        return 4;
      }
      if ((i & 0x4) == 0)
      {
        int j = paramViewHolder.getOldPosition();
        int k = paramViewHolder.getAdapterPosition();
        if ((j != -1) && (k != -1) && (j != k)) {
          i |= 0x800;
        }
      }
      return i;
    }
    
    public abstract boolean animateAppearance(@NonNull RecyclerView.ViewHolder paramViewHolder, @Nullable ItemHolderInfo paramItemHolderInfo1, @NonNull ItemHolderInfo paramItemHolderInfo2);
    
    public abstract boolean animateChange(@NonNull RecyclerView.ViewHolder paramViewHolder1, @NonNull RecyclerView.ViewHolder paramViewHolder2, @NonNull ItemHolderInfo paramItemHolderInfo1, @NonNull ItemHolderInfo paramItemHolderInfo2);
    
    public abstract boolean animateDisappearance(@NonNull RecyclerView.ViewHolder paramViewHolder, @NonNull ItemHolderInfo paramItemHolderInfo1, @Nullable ItemHolderInfo paramItemHolderInfo2);
    
    public abstract boolean animatePersistence(@NonNull RecyclerView.ViewHolder paramViewHolder, @NonNull ItemHolderInfo paramItemHolderInfo1, @NonNull ItemHolderInfo paramItemHolderInfo2);
    
    public boolean canReuseUpdatedViewHolder(@NonNull RecyclerView.ViewHolder paramViewHolder)
    {
      return true;
    }
    
    public boolean canReuseUpdatedViewHolder(@NonNull RecyclerView.ViewHolder paramViewHolder, @NonNull List<Object> paramList)
    {
      return canReuseUpdatedViewHolder(paramViewHolder);
    }
    
    public final void dispatchAnimationFinished(RecyclerView.ViewHolder paramViewHolder)
    {
      onAnimationFinished(paramViewHolder);
      if (this.mListener != null) {
        this.mListener.onAnimationFinished(paramViewHolder);
      }
    }
    
    public final void dispatchAnimationStarted(RecyclerView.ViewHolder paramViewHolder)
    {
      onAnimationStarted(paramViewHolder);
    }
    
    public final void dispatchAnimationsFinished()
    {
      int i = this.mFinishedListeners.size();
      for (int j = 0; j < i; j++) {
        ((ItemAnimatorFinishedListener)this.mFinishedListeners.get(j)).onAnimationsFinished();
      }
      this.mFinishedListeners.clear();
    }
    
    public abstract void endAnimation(RecyclerView.ViewHolder paramViewHolder);
    
    public abstract void endAnimations();
    
    public long getAddDuration()
    {
      return this.mAddDuration;
    }
    
    public long getChangeDuration()
    {
      return this.mChangeDuration;
    }
    
    public long getMoveDuration()
    {
      return this.mMoveDuration;
    }
    
    public long getRemoveDuration()
    {
      return this.mRemoveDuration;
    }
    
    public abstract boolean isRunning();
    
    public final boolean isRunning(ItemAnimatorFinishedListener paramItemAnimatorFinishedListener)
    {
      boolean bool = isRunning();
      if (paramItemAnimatorFinishedListener != null)
      {
        if (!bool) {
          paramItemAnimatorFinishedListener.onAnimationsFinished();
        }
      }
      else {
        return bool;
      }
      this.mFinishedListeners.add(paramItemAnimatorFinishedListener);
      return bool;
    }
    
    public ItemHolderInfo obtainHolderInfo()
    {
      return new ItemHolderInfo();
    }
    
    public void onAnimationFinished(RecyclerView.ViewHolder paramViewHolder) {}
    
    public void onAnimationStarted(RecyclerView.ViewHolder paramViewHolder) {}
    
    @NonNull
    public ItemHolderInfo recordPostLayoutInformation(@NonNull RecyclerView.State paramState, @NonNull RecyclerView.ViewHolder paramViewHolder)
    {
      return obtainHolderInfo().setFrom(paramViewHolder);
    }
    
    @NonNull
    public ItemHolderInfo recordPreLayoutInformation(@NonNull RecyclerView.State paramState, @NonNull RecyclerView.ViewHolder paramViewHolder, int paramInt, @NonNull List<Object> paramList)
    {
      return obtainHolderInfo().setFrom(paramViewHolder);
    }
    
    public abstract void runPendingAnimations();
    
    public void setAddDuration(long paramLong)
    {
      this.mAddDuration = paramLong;
    }
    
    public void setChangeDuration(long paramLong)
    {
      this.mChangeDuration = paramLong;
    }
    
    void setListener(ItemAnimatorListener paramItemAnimatorListener)
    {
      this.mListener = paramItemAnimatorListener;
    }
    
    public void setMoveDuration(long paramLong)
    {
      this.mMoveDuration = paramLong;
    }
    
    public void setRemoveDuration(long paramLong)
    {
      this.mRemoveDuration = paramLong;
    }
    
    @Retention(RetentionPolicy.SOURCE)
    public static @interface AdapterChanges {}
    
    public static abstract interface ItemAnimatorFinishedListener
    {
      public abstract void onAnimationsFinished();
    }
    
    static abstract interface ItemAnimatorListener
    {
      public abstract void onAnimationFinished(RecyclerView.ViewHolder paramViewHolder);
    }
    
    public static class ItemHolderInfo
    {
      public int bottom;
      public int changeFlags;
      public int left;
      public int right;
      public int top;
      
      public ItemHolderInfo() {}
      
      public ItemHolderInfo setFrom(RecyclerView.ViewHolder paramViewHolder)
      {
        return setFrom(paramViewHolder, 0);
      }
      
      public ItemHolderInfo setFrom(RecyclerView.ViewHolder paramViewHolder, int paramInt)
      {
        View localView = paramViewHolder.itemView;
        this.left = localView.getLeft();
        this.top = localView.getTop();
        this.right = localView.getRight();
        this.bottom = localView.getBottom();
        return this;
      }
    }
  }
  
  private class ItemAnimatorRestoreListener
    implements RecyclerView.ItemAnimator.ItemAnimatorListener
  {
    ItemAnimatorRestoreListener() {}
    
    public void onAnimationFinished(RecyclerView.ViewHolder paramViewHolder)
    {
      paramViewHolder.setIsRecyclable(true);
      if ((paramViewHolder.mShadowedHolder != null) && (paramViewHolder.mShadowingHolder == null)) {
        paramViewHolder.mShadowedHolder = null;
      }
      paramViewHolder.mShadowingHolder = null;
      if ((!RecyclerView.ViewHolder.access$1300(paramViewHolder)) && (!RecyclerView.this.removeAnimatingView(paramViewHolder.itemView)) && (paramViewHolder.isTmpDetached())) {
        RecyclerView.this.removeDetachedView(paramViewHolder.itemView, false);
      }
    }
  }
  
  public static abstract class ItemDecoration
  {
    public ItemDecoration() {}
    
    @Deprecated
    public void getItemOffsets(Rect paramRect, int paramInt, RecyclerView paramRecyclerView)
    {
      paramRect.set(0, 0, 0, 0);
    }
    
    public void getItemOffsets(Rect paramRect, View paramView, RecyclerView paramRecyclerView, RecyclerView.State paramState)
    {
      getItemOffsets(paramRect, ((RecyclerView.LayoutParams)paramView.getLayoutParams()).getViewLayoutPosition(), paramRecyclerView);
    }
    
    @Deprecated
    public void onDraw(Canvas paramCanvas, RecyclerView paramRecyclerView) {}
    
    public void onDraw(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.State paramState)
    {
      onDraw(paramCanvas, paramRecyclerView);
    }
    
    @Deprecated
    public void onDrawOver(Canvas paramCanvas, RecyclerView paramRecyclerView) {}
    
    public void onDrawOver(Canvas paramCanvas, RecyclerView paramRecyclerView, RecyclerView.State paramState)
    {
      onDrawOver(paramCanvas, paramRecyclerView);
    }
  }
  
  public static abstract class LayoutManager
  {
    boolean mAutoMeasure = false;
    ChildHelper mChildHelper;
    private int mHeight;
    private int mHeightMode;
    ViewBoundsCheck mHorizontalBoundCheck = new ViewBoundsCheck(this.mHorizontalBoundCheckCallback);
    private final ViewBoundsCheck.Callback mHorizontalBoundCheckCallback = new ViewBoundsCheck.Callback()
    {
      public View getChildAt(int paramAnonymousInt)
      {
        return RecyclerView.LayoutManager.this.getChildAt(paramAnonymousInt);
      }
      
      public int getChildCount()
      {
        return RecyclerView.LayoutManager.this.getChildCount();
      }
      
      public int getChildEnd(View paramAnonymousView)
      {
        RecyclerView.LayoutParams localLayoutParams = (RecyclerView.LayoutParams)paramAnonymousView.getLayoutParams();
        return RecyclerView.LayoutManager.this.getDecoratedRight(paramAnonymousView) + localLayoutParams.rightMargin;
      }
      
      public int getChildStart(View paramAnonymousView)
      {
        RecyclerView.LayoutParams localLayoutParams = (RecyclerView.LayoutParams)paramAnonymousView.getLayoutParams();
        return RecyclerView.LayoutManager.this.getDecoratedLeft(paramAnonymousView) - localLayoutParams.leftMargin;
      }
      
      public View getParent()
      {
        return RecyclerView.LayoutManager.this.mRecyclerView;
      }
      
      public int getParentEnd()
      {
        return RecyclerView.LayoutManager.this.getWidth() - RecyclerView.LayoutManager.this.getPaddingRight();
      }
      
      public int getParentStart()
      {
        return RecyclerView.LayoutManager.this.getPaddingLeft();
      }
    };
    boolean mIsAttachedToWindow = false;
    private boolean mItemPrefetchEnabled = true;
    private boolean mMeasurementCacheEnabled = true;
    int mPrefetchMaxCountObserved;
    boolean mPrefetchMaxObservedInInitialPrefetch;
    RecyclerView mRecyclerView;
    boolean mRequestedSimpleAnimations = false;
    @Nullable
    RecyclerView.SmoothScroller mSmoothScroller;
    ViewBoundsCheck mVerticalBoundCheck = new ViewBoundsCheck(this.mVerticalBoundCheckCallback);
    private final ViewBoundsCheck.Callback mVerticalBoundCheckCallback = new ViewBoundsCheck.Callback()
    {
      public View getChildAt(int paramAnonymousInt)
      {
        return RecyclerView.LayoutManager.this.getChildAt(paramAnonymousInt);
      }
      
      public int getChildCount()
      {
        return RecyclerView.LayoutManager.this.getChildCount();
      }
      
      public int getChildEnd(View paramAnonymousView)
      {
        RecyclerView.LayoutParams localLayoutParams = (RecyclerView.LayoutParams)paramAnonymousView.getLayoutParams();
        return RecyclerView.LayoutManager.this.getDecoratedBottom(paramAnonymousView) + localLayoutParams.bottomMargin;
      }
      
      public int getChildStart(View paramAnonymousView)
      {
        RecyclerView.LayoutParams localLayoutParams = (RecyclerView.LayoutParams)paramAnonymousView.getLayoutParams();
        return RecyclerView.LayoutManager.this.getDecoratedTop(paramAnonymousView) - localLayoutParams.topMargin;
      }
      
      public View getParent()
      {
        return RecyclerView.LayoutManager.this.mRecyclerView;
      }
      
      public int getParentEnd()
      {
        return RecyclerView.LayoutManager.this.getHeight() - RecyclerView.LayoutManager.this.getPaddingBottom();
      }
      
      public int getParentStart()
      {
        return RecyclerView.LayoutManager.this.getPaddingTop();
      }
    };
    private int mWidth;
    private int mWidthMode;
    
    public LayoutManager() {}
    
    private void addViewInt(View paramView, int paramInt, boolean paramBoolean)
    {
      RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(paramView);
      RecyclerView.LayoutParams localLayoutParams;
      if ((paramBoolean) || (localViewHolder.isRemoved()))
      {
        this.mRecyclerView.mViewInfoStore.addToDisappearedInLayout(localViewHolder);
        localLayoutParams = (RecyclerView.LayoutParams)paramView.getLayoutParams();
        if ((!localViewHolder.wasReturnedFromScrap()) && (!localViewHolder.isScrap())) {
          break label128;
        }
        if (!localViewHolder.isScrap()) {
          break label120;
        }
        localViewHolder.unScrap();
        label68:
        this.mChildHelper.attachViewToParent(paramView, paramInt, paramView.getLayoutParams(), false);
      }
      for (;;)
      {
        if (localLayoutParams.mPendingInvalidate)
        {
          localViewHolder.itemView.invalidate();
          localLayoutParams.mPendingInvalidate = false;
        }
        return;
        this.mRecyclerView.mViewInfoStore.removeFromDisappearedInLayout(localViewHolder);
        break;
        label120:
        localViewHolder.clearReturnedFromScrapFlag();
        break label68;
        label128:
        if (paramView.getParent() == this.mRecyclerView)
        {
          int i = this.mChildHelper.indexOfChild(paramView);
          if (paramInt == -1) {
            paramInt = this.mChildHelper.getChildCount();
          }
          if (i == -1) {
            throw new IllegalStateException("Added View has RecyclerView as parent but view is not a real child. Unfiltered index:" + this.mRecyclerView.indexOfChild(paramView));
          }
          if (i != paramInt) {
            this.mRecyclerView.mLayout.moveView(i, paramInt);
          }
        }
        else
        {
          this.mChildHelper.addView(paramView, paramInt, false);
          localLayoutParams.mInsetsDirty = true;
          if ((this.mSmoothScroller != null) && (this.mSmoothScroller.isRunning())) {
            this.mSmoothScroller.onChildAttachedToWindow(paramView);
          }
        }
      }
    }
    
    public static int chooseSize(int paramInt1, int paramInt2, int paramInt3)
    {
      int i = View.MeasureSpec.getMode(paramInt1);
      int j = View.MeasureSpec.getSize(paramInt1);
      switch (i)
      {
      default: 
        j = Math.max(paramInt2, paramInt3);
      case 1073741824: 
        return j;
      }
      return Math.min(j, Math.max(paramInt2, paramInt3));
    }
    
    private void detachViewInternal(int paramInt, View paramView)
    {
      this.mChildHelper.detachViewFromParent(paramInt);
    }
    
    public static int getChildMeasureSpec(int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean)
    {
      int i = Math.max(0, paramInt1 - paramInt3);
      int k;
      int j;
      if (paramBoolean) {
        if (paramInt4 >= 0)
        {
          k = paramInt4;
          j = 1073741824;
        }
      }
      for (;;)
      {
        return View.MeasureSpec.makeMeasureSpec(k, j);
        if (paramInt4 == -1)
        {
          switch (paramInt2)
          {
          default: 
            j = 0;
            k = 0;
            break;
          case 1073741824: 
          case -2147483648: 
            k = i;
            j = paramInt2;
            break;
          case 0: 
            j = 0;
            k = 0;
            break;
          }
        }
        else
        {
          j = 0;
          k = 0;
          if (paramInt4 == -2)
          {
            j = 0;
            k = 0;
            continue;
            if (paramInt4 >= 0)
            {
              k = paramInt4;
              j = 1073741824;
            }
            else if (paramInt4 == -1)
            {
              k = i;
              j = paramInt2;
            }
            else
            {
              j = 0;
              k = 0;
              if (paramInt4 == -2)
              {
                k = i;
                if ((paramInt2 == Integer.MIN_VALUE) || (paramInt2 == 1073741824)) {
                  j = Integer.MIN_VALUE;
                } else {
                  j = 0;
                }
              }
            }
          }
        }
      }
    }
    
    @Deprecated
    public static int getChildMeasureSpec(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean)
    {
      int i = Math.max(0, paramInt1 - paramInt2);
      int k;
      int j;
      if (paramBoolean) {
        if (paramInt3 >= 0)
        {
          k = paramInt3;
          j = 1073741824;
        }
      }
      for (;;)
      {
        return View.MeasureSpec.makeMeasureSpec(k, j);
        j = 0;
        k = 0;
        continue;
        if (paramInt3 >= 0)
        {
          k = paramInt3;
          j = 1073741824;
        }
        else if (paramInt3 == -1)
        {
          k = i;
          j = 1073741824;
        }
        else
        {
          j = 0;
          k = 0;
          if (paramInt3 == -2)
          {
            k = i;
            j = Integer.MIN_VALUE;
          }
        }
      }
    }
    
    private int[] getChildRectangleOnScreenScrollAmount(RecyclerView paramRecyclerView, View paramView, Rect paramRect, boolean paramBoolean)
    {
      int[] arrayOfInt = new int[2];
      int i = getPaddingLeft();
      int j = getPaddingTop();
      int k = getWidth() - getPaddingRight();
      int m = getHeight() - getPaddingBottom();
      int n = paramView.getLeft() + paramRect.left - paramView.getScrollX();
      int i1 = paramView.getTop() + paramRect.top - paramView.getScrollY();
      int i2 = n + paramRect.width();
      int i3 = i1 + paramRect.height();
      int i4 = Math.min(0, n - i);
      int i5 = Math.min(0, i1 - j);
      int i6 = Math.max(0, i2 - k);
      int i7 = Math.max(0, i3 - m);
      int i8;
      if (getLayoutDirection() == 1) {
        if (i6 != 0)
        {
          i8 = i6;
          if (i5 == 0) {
            break label216;
          }
        }
      }
      label216:
      for (int i9 = i5;; i9 = Math.min(i1 - j, i7))
      {
        arrayOfInt[0] = i8;
        arrayOfInt[1] = i9;
        return arrayOfInt;
        i8 = Math.max(i4, i2 - k);
        break;
        if (i4 != 0) {}
        for (i8 = i4;; i8 = Math.min(n - i, i6)) {
          break;
        }
      }
    }
    
    public static Properties getProperties(Context paramContext, AttributeSet paramAttributeSet, int paramInt1, int paramInt2)
    {
      Properties localProperties = new Properties();
      TypedArray localTypedArray = paramContext.obtainStyledAttributes(paramAttributeSet, R.styleable.RecyclerView, paramInt1, paramInt2);
      localProperties.orientation = localTypedArray.getInt(R.styleable.RecyclerView_android_orientation, 1);
      localProperties.spanCount = localTypedArray.getInt(R.styleable.RecyclerView_spanCount, 1);
      localProperties.reverseLayout = localTypedArray.getBoolean(R.styleable.RecyclerView_reverseLayout, false);
      localProperties.stackFromEnd = localTypedArray.getBoolean(R.styleable.RecyclerView_stackFromEnd, false);
      localTypedArray.recycle();
      return localProperties;
    }
    
    private boolean isFocusedChildVisibleAfterScrolling(RecyclerView paramRecyclerView, int paramInt1, int paramInt2)
    {
      View localView = paramRecyclerView.getFocusedChild();
      if (localView == null) {}
      int i;
      int j;
      int k;
      int m;
      Rect localRect;
      do
      {
        return false;
        i = getPaddingLeft();
        j = getPaddingTop();
        k = getWidth() - getPaddingRight();
        m = getHeight() - getPaddingBottom();
        localRect = this.mRecyclerView.mTempRect;
        getDecoratedBoundsWithMargins(localView, localRect);
      } while ((localRect.left - paramInt1 >= k) || (localRect.right - paramInt1 <= i) || (localRect.top - paramInt2 >= m) || (localRect.bottom - paramInt2 <= j));
      return true;
    }
    
    private static boolean isMeasurementUpToDate(int paramInt1, int paramInt2, int paramInt3)
    {
      boolean bool = true;
      int i = View.MeasureSpec.getMode(paramInt2);
      int j = View.MeasureSpec.getSize(paramInt2);
      if ((paramInt3 > 0) && (paramInt1 != paramInt3)) {
        bool = false;
      }
      do
      {
        do
        {
          return bool;
          switch (i)
          {
          case 0: 
          default: 
            return false;
          }
        } while (j >= paramInt1);
        return false;
      } while (j == paramInt1);
      return false;
    }
    
    private void onSmoothScrollerStopped(RecyclerView.SmoothScroller paramSmoothScroller)
    {
      if (this.mSmoothScroller == paramSmoothScroller) {
        this.mSmoothScroller = null;
      }
    }
    
    private void scrapOrRecycleView(RecyclerView.Recycler paramRecycler, int paramInt, View paramView)
    {
      RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(paramView);
      if (localViewHolder.shouldIgnore()) {
        return;
      }
      if ((localViewHolder.isInvalid()) && (!localViewHolder.isRemoved()) && (!this.mRecyclerView.mAdapter.hasStableIds()))
      {
        removeViewAt(paramInt);
        paramRecycler.recycleViewHolderInternal(localViewHolder);
        return;
      }
      detachViewAt(paramInt);
      paramRecycler.scrapView(paramView);
      this.mRecyclerView.mViewInfoStore.onViewDetached(localViewHolder);
    }
    
    public void addDisappearingView(View paramView)
    {
      addDisappearingView(paramView, -1);
    }
    
    public void addDisappearingView(View paramView, int paramInt)
    {
      addViewInt(paramView, paramInt, true);
    }
    
    public void addView(View paramView)
    {
      addView(paramView, -1);
    }
    
    public void addView(View paramView, int paramInt)
    {
      addViewInt(paramView, paramInt, false);
    }
    
    public void assertInLayoutOrScroll(String paramString)
    {
      if (this.mRecyclerView != null) {
        this.mRecyclerView.assertInLayoutOrScroll(paramString);
      }
    }
    
    public void assertNotInLayoutOrScroll(String paramString)
    {
      if (this.mRecyclerView != null) {
        this.mRecyclerView.assertNotInLayoutOrScroll(paramString);
      }
    }
    
    public void attachView(View paramView)
    {
      attachView(paramView, -1);
    }
    
    public void attachView(View paramView, int paramInt)
    {
      attachView(paramView, paramInt, (RecyclerView.LayoutParams)paramView.getLayoutParams());
    }
    
    public void attachView(View paramView, int paramInt, RecyclerView.LayoutParams paramLayoutParams)
    {
      RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(paramView);
      if (localViewHolder.isRemoved()) {
        this.mRecyclerView.mViewInfoStore.addToDisappearedInLayout(localViewHolder);
      }
      for (;;)
      {
        this.mChildHelper.attachViewToParent(paramView, paramInt, paramLayoutParams, localViewHolder.isRemoved());
        return;
        this.mRecyclerView.mViewInfoStore.removeFromDisappearedInLayout(localViewHolder);
      }
    }
    
    public void calculateItemDecorationsForChild(View paramView, Rect paramRect)
    {
      if (this.mRecyclerView == null)
      {
        paramRect.set(0, 0, 0, 0);
        return;
      }
      paramRect.set(this.mRecyclerView.getItemDecorInsetsForChild(paramView));
    }
    
    public boolean canScrollHorizontally()
    {
      return false;
    }
    
    public boolean canScrollVertically()
    {
      return false;
    }
    
    public boolean checkLayoutParams(RecyclerView.LayoutParams paramLayoutParams)
    {
      return paramLayoutParams != null;
    }
    
    public void collectAdjacentPrefetchPositions(int paramInt1, int paramInt2, RecyclerView.State paramState, LayoutPrefetchRegistry paramLayoutPrefetchRegistry) {}
    
    public void collectInitialPrefetchPositions(int paramInt, LayoutPrefetchRegistry paramLayoutPrefetchRegistry) {}
    
    public int computeHorizontalScrollExtent(RecyclerView.State paramState)
    {
      return 0;
    }
    
    public int computeHorizontalScrollOffset(RecyclerView.State paramState)
    {
      return 0;
    }
    
    public int computeHorizontalScrollRange(RecyclerView.State paramState)
    {
      return 0;
    }
    
    public int computeVerticalScrollExtent(RecyclerView.State paramState)
    {
      return 0;
    }
    
    public int computeVerticalScrollOffset(RecyclerView.State paramState)
    {
      return 0;
    }
    
    public int computeVerticalScrollRange(RecyclerView.State paramState)
    {
      return 0;
    }
    
    public void detachAndScrapAttachedViews(RecyclerView.Recycler paramRecycler)
    {
      for (int i = -1 + getChildCount(); i >= 0; i--) {
        scrapOrRecycleView(paramRecycler, i, getChildAt(i));
      }
    }
    
    public void detachAndScrapView(View paramView, RecyclerView.Recycler paramRecycler)
    {
      scrapOrRecycleView(paramRecycler, this.mChildHelper.indexOfChild(paramView), paramView);
    }
    
    public void detachAndScrapViewAt(int paramInt, RecyclerView.Recycler paramRecycler)
    {
      scrapOrRecycleView(paramRecycler, paramInt, getChildAt(paramInt));
    }
    
    public void detachView(View paramView)
    {
      int i = this.mChildHelper.indexOfChild(paramView);
      if (i >= 0) {
        detachViewInternal(i, paramView);
      }
    }
    
    public void detachViewAt(int paramInt)
    {
      detachViewInternal(paramInt, getChildAt(paramInt));
    }
    
    void dispatchAttachedToWindow(RecyclerView paramRecyclerView)
    {
      this.mIsAttachedToWindow = true;
      onAttachedToWindow(paramRecyclerView);
    }
    
    void dispatchDetachedFromWindow(RecyclerView paramRecyclerView, RecyclerView.Recycler paramRecycler)
    {
      this.mIsAttachedToWindow = false;
      onDetachedFromWindow(paramRecyclerView, paramRecycler);
    }
    
    public void endAnimation(View paramView)
    {
      if (this.mRecyclerView.mItemAnimator != null) {
        this.mRecyclerView.mItemAnimator.endAnimation(RecyclerView.getChildViewHolderInt(paramView));
      }
    }
    
    @Nullable
    public View findContainingItemView(View paramView)
    {
      View localView;
      if (this.mRecyclerView == null) {
        localView = null;
      }
      do
      {
        return localView;
        localView = this.mRecyclerView.findContainingItemView(paramView);
        if (localView == null) {
          return null;
        }
      } while (!this.mChildHelper.isHidden(localView));
      return null;
    }
    
    public View findViewByPosition(int paramInt)
    {
      int i = getChildCount();
      int j = 0;
      if (j < i)
      {
        View localView = getChildAt(j);
        RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(localView);
        if (localViewHolder == null) {}
        while ((localViewHolder.getLayoutPosition() != paramInt) || (localViewHolder.shouldIgnore()) || ((!this.mRecyclerView.mState.isPreLayout()) && (localViewHolder.isRemoved())))
        {
          j++;
          break;
        }
        return localView;
      }
      return null;
    }
    
    public abstract RecyclerView.LayoutParams generateDefaultLayoutParams();
    
    public RecyclerView.LayoutParams generateLayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      return new RecyclerView.LayoutParams(paramContext, paramAttributeSet);
    }
    
    public RecyclerView.LayoutParams generateLayoutParams(ViewGroup.LayoutParams paramLayoutParams)
    {
      if ((paramLayoutParams instanceof RecyclerView.LayoutParams)) {
        return new RecyclerView.LayoutParams((RecyclerView.LayoutParams)paramLayoutParams);
      }
      if ((paramLayoutParams instanceof ViewGroup.MarginLayoutParams)) {
        return new RecyclerView.LayoutParams((ViewGroup.MarginLayoutParams)paramLayoutParams);
      }
      return new RecyclerView.LayoutParams(paramLayoutParams);
    }
    
    public int getBaseline()
    {
      return -1;
    }
    
    public int getBottomDecorationHeight(View paramView)
    {
      return ((RecyclerView.LayoutParams)paramView.getLayoutParams()).mDecorInsets.bottom;
    }
    
    public View getChildAt(int paramInt)
    {
      if (this.mChildHelper != null) {
        return this.mChildHelper.getChildAt(paramInt);
      }
      return null;
    }
    
    public int getChildCount()
    {
      if (this.mChildHelper != null) {
        return this.mChildHelper.getChildCount();
      }
      return 0;
    }
    
    public boolean getClipToPadding()
    {
      return (this.mRecyclerView != null) && (this.mRecyclerView.mClipToPadding);
    }
    
    public int getColumnCountForAccessibility(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
    {
      if ((this.mRecyclerView == null) || (this.mRecyclerView.mAdapter == null)) {}
      while (!canScrollHorizontally()) {
        return 1;
      }
      return this.mRecyclerView.mAdapter.getItemCount();
    }
    
    public int getDecoratedBottom(View paramView)
    {
      return paramView.getBottom() + getBottomDecorationHeight(paramView);
    }
    
    public void getDecoratedBoundsWithMargins(View paramView, Rect paramRect)
    {
      RecyclerView.getDecoratedBoundsWithMarginsInt(paramView, paramRect);
    }
    
    public int getDecoratedLeft(View paramView)
    {
      return paramView.getLeft() - getLeftDecorationWidth(paramView);
    }
    
    public int getDecoratedMeasuredHeight(View paramView)
    {
      Rect localRect = ((RecyclerView.LayoutParams)paramView.getLayoutParams()).mDecorInsets;
      return paramView.getMeasuredHeight() + localRect.top + localRect.bottom;
    }
    
    public int getDecoratedMeasuredWidth(View paramView)
    {
      Rect localRect = ((RecyclerView.LayoutParams)paramView.getLayoutParams()).mDecorInsets;
      return paramView.getMeasuredWidth() + localRect.left + localRect.right;
    }
    
    public int getDecoratedRight(View paramView)
    {
      return paramView.getRight() + getRightDecorationWidth(paramView);
    }
    
    public int getDecoratedTop(View paramView)
    {
      return paramView.getTop() - getTopDecorationHeight(paramView);
    }
    
    public View getFocusedChild()
    {
      View localView;
      if (this.mRecyclerView == null) {
        localView = null;
      }
      do
      {
        return localView;
        localView = this.mRecyclerView.getFocusedChild();
      } while ((localView != null) && (!this.mChildHelper.isHidden(localView)));
      return null;
    }
    
    public int getHeight()
    {
      return this.mHeight;
    }
    
    public int getHeightMode()
    {
      return this.mHeightMode;
    }
    
    public int getItemCount()
    {
      if (this.mRecyclerView != null) {}
      for (RecyclerView.Adapter localAdapter = this.mRecyclerView.getAdapter(); localAdapter != null; localAdapter = null) {
        return localAdapter.getItemCount();
      }
      return 0;
    }
    
    public int getItemViewType(View paramView)
    {
      return RecyclerView.getChildViewHolderInt(paramView).getItemViewType();
    }
    
    public int getLayoutDirection()
    {
      return ViewCompat.getLayoutDirection(this.mRecyclerView);
    }
    
    public int getLeftDecorationWidth(View paramView)
    {
      return ((RecyclerView.LayoutParams)paramView.getLayoutParams()).mDecorInsets.left;
    }
    
    public int getMinimumHeight()
    {
      return ViewCompat.getMinimumHeight(this.mRecyclerView);
    }
    
    public int getMinimumWidth()
    {
      return ViewCompat.getMinimumWidth(this.mRecyclerView);
    }
    
    public int getPaddingBottom()
    {
      if (this.mRecyclerView != null) {
        return this.mRecyclerView.getPaddingBottom();
      }
      return 0;
    }
    
    public int getPaddingEnd()
    {
      if (this.mRecyclerView != null) {
        return ViewCompat.getPaddingEnd(this.mRecyclerView);
      }
      return 0;
    }
    
    public int getPaddingLeft()
    {
      if (this.mRecyclerView != null) {
        return this.mRecyclerView.getPaddingLeft();
      }
      return 0;
    }
    
    public int getPaddingRight()
    {
      if (this.mRecyclerView != null) {
        return this.mRecyclerView.getPaddingRight();
      }
      return 0;
    }
    
    public int getPaddingStart()
    {
      if (this.mRecyclerView != null) {
        return ViewCompat.getPaddingStart(this.mRecyclerView);
      }
      return 0;
    }
    
    public int getPaddingTop()
    {
      if (this.mRecyclerView != null) {
        return this.mRecyclerView.getPaddingTop();
      }
      return 0;
    }
    
    public int getPosition(View paramView)
    {
      return ((RecyclerView.LayoutParams)paramView.getLayoutParams()).getViewLayoutPosition();
    }
    
    public int getRightDecorationWidth(View paramView)
    {
      return ((RecyclerView.LayoutParams)paramView.getLayoutParams()).mDecorInsets.right;
    }
    
    public int getRowCountForAccessibility(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
    {
      if ((this.mRecyclerView == null) || (this.mRecyclerView.mAdapter == null)) {}
      while (!canScrollVertically()) {
        return 1;
      }
      return this.mRecyclerView.mAdapter.getItemCount();
    }
    
    public int getSelectionModeForAccessibility(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
    {
      return 0;
    }
    
    public int getTopDecorationHeight(View paramView)
    {
      return ((RecyclerView.LayoutParams)paramView.getLayoutParams()).mDecorInsets.top;
    }
    
    public void getTransformedBoundingBox(View paramView, boolean paramBoolean, Rect paramRect)
    {
      if (paramBoolean)
      {
        Rect localRect = ((RecyclerView.LayoutParams)paramView.getLayoutParams()).mDecorInsets;
        paramRect.set(-localRect.left, -localRect.top, paramView.getWidth() + localRect.right, paramView.getHeight() + localRect.bottom);
      }
      for (;;)
      {
        if (this.mRecyclerView != null)
        {
          Matrix localMatrix = ViewCompat.getMatrix(paramView);
          if ((localMatrix != null) && (!localMatrix.isIdentity()))
          {
            RectF localRectF = this.mRecyclerView.mTempRectF;
            localRectF.set(paramRect);
            localMatrix.mapRect(localRectF);
            paramRect.set((int)Math.floor(localRectF.left), (int)Math.floor(localRectF.top), (int)Math.ceil(localRectF.right), (int)Math.ceil(localRectF.bottom));
          }
        }
        paramRect.offset(paramView.getLeft(), paramView.getTop());
        return;
        paramRect.set(0, 0, paramView.getWidth(), paramView.getHeight());
      }
    }
    
    public int getWidth()
    {
      return this.mWidth;
    }
    
    public int getWidthMode()
    {
      return this.mWidthMode;
    }
    
    boolean hasFlexibleChildInBothOrientations()
    {
      int i = getChildCount();
      for (int j = 0; j < i; j++)
      {
        ViewGroup.LayoutParams localLayoutParams = getChildAt(j).getLayoutParams();
        if ((localLayoutParams.width < 0) && (localLayoutParams.height < 0)) {
          return true;
        }
      }
      return false;
    }
    
    public boolean hasFocus()
    {
      return (this.mRecyclerView != null) && (this.mRecyclerView.hasFocus());
    }
    
    public void ignoreView(View paramView)
    {
      if ((paramView.getParent() != this.mRecyclerView) || (this.mRecyclerView.indexOfChild(paramView) == -1)) {
        throw new IllegalArgumentException("View should be fully attached to be ignored");
      }
      RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(paramView);
      localViewHolder.addFlags(128);
      this.mRecyclerView.mViewInfoStore.removeViewHolder(localViewHolder);
    }
    
    public boolean isAttachedToWindow()
    {
      return this.mIsAttachedToWindow;
    }
    
    public boolean isAutoMeasureEnabled()
    {
      return this.mAutoMeasure;
    }
    
    public boolean isFocused()
    {
      return (this.mRecyclerView != null) && (this.mRecyclerView.isFocused());
    }
    
    public final boolean isItemPrefetchEnabled()
    {
      return this.mItemPrefetchEnabled;
    }
    
    public boolean isLayoutHierarchical(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
    {
      return false;
    }
    
    public boolean isMeasurementCacheEnabled()
    {
      return this.mMeasurementCacheEnabled;
    }
    
    public boolean isSmoothScrolling()
    {
      return (this.mSmoothScroller != null) && (this.mSmoothScroller.isRunning());
    }
    
    public boolean isViewPartiallyVisible(@NonNull View paramView, boolean paramBoolean1, boolean paramBoolean2)
    {
      boolean bool1 = true;
      if ((this.mHorizontalBoundCheck.isViewWithinBoundFlags(paramView, 24579)) && (this.mVerticalBoundCheck.isViewWithinBoundFlags(paramView, 24579))) {}
      for (boolean bool2 = bool1; paramBoolean1; bool2 = false) {
        return bool2;
      }
      if (!bool2) {}
      for (;;)
      {
        return bool1;
        bool1 = false;
      }
    }
    
    public void layoutDecorated(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      Rect localRect = ((RecyclerView.LayoutParams)paramView.getLayoutParams()).mDecorInsets;
      paramView.layout(paramInt1 + localRect.left, paramInt2 + localRect.top, paramInt3 - localRect.right, paramInt4 - localRect.bottom);
    }
    
    public void layoutDecoratedWithMargins(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      RecyclerView.LayoutParams localLayoutParams = (RecyclerView.LayoutParams)paramView.getLayoutParams();
      Rect localRect = localLayoutParams.mDecorInsets;
      paramView.layout(paramInt1 + localRect.left + localLayoutParams.leftMargin, paramInt2 + localRect.top + localLayoutParams.topMargin, paramInt3 - localRect.right - localLayoutParams.rightMargin, paramInt4 - localRect.bottom - localLayoutParams.bottomMargin);
    }
    
    public void measureChild(View paramView, int paramInt1, int paramInt2)
    {
      RecyclerView.LayoutParams localLayoutParams = (RecyclerView.LayoutParams)paramView.getLayoutParams();
      Rect localRect = this.mRecyclerView.getItemDecorInsetsForChild(paramView);
      int i = paramInt1 + (localRect.left + localRect.right);
      int j = paramInt2 + (localRect.top + localRect.bottom);
      int k = getChildMeasureSpec(getWidth(), getWidthMode(), i + (getPaddingLeft() + getPaddingRight()), localLayoutParams.width, canScrollHorizontally());
      int m = getChildMeasureSpec(getHeight(), getHeightMode(), j + (getPaddingTop() + getPaddingBottom()), localLayoutParams.height, canScrollVertically());
      if (shouldMeasureChild(paramView, k, m, localLayoutParams)) {
        paramView.measure(k, m);
      }
    }
    
    public void measureChildWithMargins(View paramView, int paramInt1, int paramInt2)
    {
      RecyclerView.LayoutParams localLayoutParams = (RecyclerView.LayoutParams)paramView.getLayoutParams();
      Rect localRect = this.mRecyclerView.getItemDecorInsetsForChild(paramView);
      int i = paramInt1 + (localRect.left + localRect.right);
      int j = paramInt2 + (localRect.top + localRect.bottom);
      int k = getChildMeasureSpec(getWidth(), getWidthMode(), i + (getPaddingLeft() + getPaddingRight() + localLayoutParams.leftMargin + localLayoutParams.rightMargin), localLayoutParams.width, canScrollHorizontally());
      int m = getChildMeasureSpec(getHeight(), getHeightMode(), j + (getPaddingTop() + getPaddingBottom() + localLayoutParams.topMargin + localLayoutParams.bottomMargin), localLayoutParams.height, canScrollVertically());
      if (shouldMeasureChild(paramView, k, m, localLayoutParams)) {
        paramView.measure(k, m);
      }
    }
    
    public void moveView(int paramInt1, int paramInt2)
    {
      View localView = getChildAt(paramInt1);
      if (localView == null) {
        throw new IllegalArgumentException("Cannot move a child from non-existing index:" + paramInt1);
      }
      detachViewAt(paramInt1);
      attachView(localView, paramInt2);
    }
    
    public void offsetChildrenHorizontal(int paramInt)
    {
      if (this.mRecyclerView != null) {
        this.mRecyclerView.offsetChildrenHorizontal(paramInt);
      }
    }
    
    public void offsetChildrenVertical(int paramInt)
    {
      if (this.mRecyclerView != null) {
        this.mRecyclerView.offsetChildrenVertical(paramInt);
      }
    }
    
    public void onAdapterChanged(RecyclerView.Adapter paramAdapter1, RecyclerView.Adapter paramAdapter2) {}
    
    public boolean onAddFocusables(RecyclerView paramRecyclerView, ArrayList<View> paramArrayList, int paramInt1, int paramInt2)
    {
      return false;
    }
    
    @CallSuper
    public void onAttachedToWindow(RecyclerView paramRecyclerView) {}
    
    @Deprecated
    public void onDetachedFromWindow(RecyclerView paramRecyclerView) {}
    
    @CallSuper
    public void onDetachedFromWindow(RecyclerView paramRecyclerView, RecyclerView.Recycler paramRecycler)
    {
      onDetachedFromWindow(paramRecyclerView);
    }
    
    @Nullable
    public View onFocusSearchFailed(View paramView, int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
    {
      return null;
    }
    
    public void onInitializeAccessibilityEvent(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, AccessibilityEvent paramAccessibilityEvent)
    {
      int i = 1;
      AccessibilityRecordCompat localAccessibilityRecordCompat = AccessibilityEventCompat.asRecord(paramAccessibilityEvent);
      if ((this.mRecyclerView == null) || (localAccessibilityRecordCompat == null)) {
        return;
      }
      if ((ViewCompat.canScrollVertically(this.mRecyclerView, i)) || (ViewCompat.canScrollVertically(this.mRecyclerView, -1)) || (ViewCompat.canScrollHorizontally(this.mRecyclerView, -1)) || (ViewCompat.canScrollHorizontally(this.mRecyclerView, i))) {}
      for (;;)
      {
        localAccessibilityRecordCompat.setScrollable(i);
        if (this.mRecyclerView.mAdapter == null) {
          break;
        }
        localAccessibilityRecordCompat.setItemCount(this.mRecyclerView.mAdapter.getItemCount());
        return;
        int j = 0;
      }
    }
    
    public void onInitializeAccessibilityEvent(AccessibilityEvent paramAccessibilityEvent)
    {
      onInitializeAccessibilityEvent(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, paramAccessibilityEvent);
    }
    
    void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
    {
      onInitializeAccessibilityNodeInfo(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, paramAccessibilityNodeInfoCompat);
    }
    
    public void onInitializeAccessibilityNodeInfo(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
    {
      if ((ViewCompat.canScrollVertically(this.mRecyclerView, -1)) || (ViewCompat.canScrollHorizontally(this.mRecyclerView, -1)))
      {
        paramAccessibilityNodeInfoCompat.addAction(8192);
        paramAccessibilityNodeInfoCompat.setScrollable(true);
      }
      if ((ViewCompat.canScrollVertically(this.mRecyclerView, 1)) || (ViewCompat.canScrollHorizontally(this.mRecyclerView, 1)))
      {
        paramAccessibilityNodeInfoCompat.addAction(4096);
        paramAccessibilityNodeInfoCompat.setScrollable(true);
      }
      paramAccessibilityNodeInfoCompat.setCollectionInfo(AccessibilityNodeInfoCompat.CollectionInfoCompat.obtain(getRowCountForAccessibility(paramRecycler, paramState), getColumnCountForAccessibility(paramRecycler, paramState), isLayoutHierarchical(paramRecycler, paramState), getSelectionModeForAccessibility(paramRecycler, paramState)));
    }
    
    public void onInitializeAccessibilityNodeInfoForItem(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
    {
      int i;
      if (canScrollVertically())
      {
        i = getPosition(paramView);
        if (!canScrollHorizontally()) {
          break label51;
        }
      }
      label51:
      for (int j = getPosition(paramView);; j = 0)
      {
        paramAccessibilityNodeInfoCompat.setCollectionItemInfo(AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(i, 1, j, 1, false, false));
        return;
        i = 0;
        break;
      }
    }
    
    void onInitializeAccessibilityNodeInfoForItem(View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat)
    {
      RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(paramView);
      if ((localViewHolder != null) && (!localViewHolder.isRemoved()) && (!this.mChildHelper.isHidden(localViewHolder.itemView))) {
        onInitializeAccessibilityNodeInfoForItem(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, paramView, paramAccessibilityNodeInfoCompat);
      }
    }
    
    public View onInterceptFocusSearch(View paramView, int paramInt)
    {
      return null;
    }
    
    public void onItemsAdded(RecyclerView paramRecyclerView, int paramInt1, int paramInt2) {}
    
    public void onItemsChanged(RecyclerView paramRecyclerView) {}
    
    public void onItemsMoved(RecyclerView paramRecyclerView, int paramInt1, int paramInt2, int paramInt3) {}
    
    public void onItemsRemoved(RecyclerView paramRecyclerView, int paramInt1, int paramInt2) {}
    
    public void onItemsUpdated(RecyclerView paramRecyclerView, int paramInt1, int paramInt2) {}
    
    public void onItemsUpdated(RecyclerView paramRecyclerView, int paramInt1, int paramInt2, Object paramObject)
    {
      onItemsUpdated(paramRecyclerView, paramInt1, paramInt2);
    }
    
    public void onLayoutChildren(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
    {
      Log.e("RecyclerView", "You must override onLayoutChildren(Recycler recycler, State state) ");
    }
    
    public void onLayoutCompleted(RecyclerView.State paramState) {}
    
    public void onMeasure(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, int paramInt1, int paramInt2)
    {
      this.mRecyclerView.defaultOnMeasure(paramInt1, paramInt2);
    }
    
    public boolean onRequestChildFocus(RecyclerView paramRecyclerView, RecyclerView.State paramState, View paramView1, View paramView2)
    {
      return onRequestChildFocus(paramRecyclerView, paramView1, paramView2);
    }
    
    @Deprecated
    public boolean onRequestChildFocus(RecyclerView paramRecyclerView, View paramView1, View paramView2)
    {
      return (isSmoothScrolling()) || (paramRecyclerView.isComputingLayout());
    }
    
    public void onRestoreInstanceState(Parcelable paramParcelable) {}
    
    public Parcelable onSaveInstanceState()
    {
      return null;
    }
    
    public void onScrollStateChanged(int paramInt) {}
    
    boolean performAccessibilityAction(int paramInt, Bundle paramBundle)
    {
      return performAccessibilityAction(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, paramInt, paramBundle);
    }
    
    public boolean performAccessibilityAction(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, int paramInt, Bundle paramBundle)
    {
      if (this.mRecyclerView == null) {}
      for (;;)
      {
        return false;
        int i = 0;
        int j = 0;
        switch (paramInt)
        {
        }
        while ((j != 0) || (i != 0))
        {
          this.mRecyclerView.scrollBy(i, j);
          return true;
          boolean bool3 = ViewCompat.canScrollVertically(this.mRecyclerView, -1);
          j = 0;
          if (bool3) {
            j = -(getHeight() - getPaddingTop() - getPaddingBottom());
          }
          boolean bool4 = ViewCompat.canScrollHorizontally(this.mRecyclerView, -1);
          i = 0;
          if (bool4)
          {
            i = -(getWidth() - getPaddingLeft() - getPaddingRight());
            continue;
            boolean bool1 = ViewCompat.canScrollVertically(this.mRecyclerView, 1);
            j = 0;
            if (bool1) {
              j = getHeight() - getPaddingTop() - getPaddingBottom();
            }
            boolean bool2 = ViewCompat.canScrollHorizontally(this.mRecyclerView, 1);
            i = 0;
            if (bool2) {
              i = getWidth() - getPaddingLeft() - getPaddingRight();
            }
          }
        }
      }
    }
    
    public boolean performAccessibilityActionForItem(RecyclerView.Recycler paramRecycler, RecyclerView.State paramState, View paramView, int paramInt, Bundle paramBundle)
    {
      return false;
    }
    
    boolean performAccessibilityActionForItem(View paramView, int paramInt, Bundle paramBundle)
    {
      return performAccessibilityActionForItem(this.mRecyclerView.mRecycler, this.mRecyclerView.mState, paramView, paramInt, paramBundle);
    }
    
    public void postOnAnimation(Runnable paramRunnable)
    {
      if (this.mRecyclerView != null) {
        ViewCompat.postOnAnimation(this.mRecyclerView, paramRunnable);
      }
    }
    
    public void removeAllViews()
    {
      for (int i = -1 + getChildCount(); i >= 0; i--) {
        this.mChildHelper.removeViewAt(i);
      }
    }
    
    public void removeAndRecycleAllViews(RecyclerView.Recycler paramRecycler)
    {
      for (int i = -1 + getChildCount(); i >= 0; i--) {
        if (!RecyclerView.getChildViewHolderInt(getChildAt(i)).shouldIgnore()) {
          removeAndRecycleViewAt(i, paramRecycler);
        }
      }
    }
    
    void removeAndRecycleScrapInt(RecyclerView.Recycler paramRecycler)
    {
      int i = paramRecycler.getScrapCount();
      int j = i - 1;
      if (j >= 0)
      {
        View localView = paramRecycler.getScrapViewAt(j);
        RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(localView);
        if (localViewHolder.shouldIgnore()) {}
        for (;;)
        {
          j--;
          break;
          localViewHolder.setIsRecyclable(false);
          if (localViewHolder.isTmpDetached()) {
            this.mRecyclerView.removeDetachedView(localView, false);
          }
          if (this.mRecyclerView.mItemAnimator != null) {
            this.mRecyclerView.mItemAnimator.endAnimation(localViewHolder);
          }
          localViewHolder.setIsRecyclable(true);
          paramRecycler.quickRecycleScrapView(localView);
        }
      }
      paramRecycler.clearScrap();
      if (i > 0) {
        this.mRecyclerView.invalidate();
      }
    }
    
    public void removeAndRecycleView(View paramView, RecyclerView.Recycler paramRecycler)
    {
      removeView(paramView);
      paramRecycler.recycleView(paramView);
    }
    
    public void removeAndRecycleViewAt(int paramInt, RecyclerView.Recycler paramRecycler)
    {
      View localView = getChildAt(paramInt);
      removeViewAt(paramInt);
      paramRecycler.recycleView(localView);
    }
    
    public boolean removeCallbacks(Runnable paramRunnable)
    {
      if (this.mRecyclerView != null) {
        return this.mRecyclerView.removeCallbacks(paramRunnable);
      }
      return false;
    }
    
    public void removeDetachedView(View paramView)
    {
      this.mRecyclerView.removeDetachedView(paramView, false);
    }
    
    public void removeView(View paramView)
    {
      this.mChildHelper.removeView(paramView);
    }
    
    public void removeViewAt(int paramInt)
    {
      if (getChildAt(paramInt) != null) {
        this.mChildHelper.removeViewAt(paramInt);
      }
    }
    
    public boolean requestChildRectangleOnScreen(RecyclerView paramRecyclerView, View paramView, Rect paramRect, boolean paramBoolean)
    {
      return requestChildRectangleOnScreen(paramRecyclerView, paramView, paramRect, paramBoolean, false);
    }
    
    public boolean requestChildRectangleOnScreen(RecyclerView paramRecyclerView, View paramView, Rect paramRect, boolean paramBoolean1, boolean paramBoolean2)
    {
      int[] arrayOfInt = getChildRectangleOnScreenScrollAmount(paramRecyclerView, paramView, paramRect, paramBoolean1);
      int i = arrayOfInt[0];
      int j = arrayOfInt[1];
      boolean bool1;
      if (paramBoolean2)
      {
        boolean bool2 = isFocusedChildVisibleAfterScrolling(paramRecyclerView, i, j);
        bool1 = false;
        if (!bool2) {}
      }
      else if (i == 0)
      {
        bool1 = false;
        if (j == 0) {}
      }
      else
      {
        if (!paramBoolean1) {
          break label79;
        }
        paramRecyclerView.scrollBy(i, j);
      }
      for (;;)
      {
        bool1 = true;
        return bool1;
        label79:
        paramRecyclerView.smoothScrollBy(i, j);
      }
    }
    
    public void requestLayout()
    {
      if (this.mRecyclerView != null) {
        this.mRecyclerView.requestLayout();
      }
    }
    
    public void requestSimpleAnimationsInNextLayout()
    {
      this.mRequestedSimpleAnimations = true;
    }
    
    public int scrollHorizontallyBy(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
    {
      return 0;
    }
    
    public void scrollToPosition(int paramInt) {}
    
    public int scrollVerticallyBy(int paramInt, RecyclerView.Recycler paramRecycler, RecyclerView.State paramState)
    {
      return 0;
    }
    
    public void setAutoMeasureEnabled(boolean paramBoolean)
    {
      this.mAutoMeasure = paramBoolean;
    }
    
    void setExactMeasureSpecsFrom(RecyclerView paramRecyclerView)
    {
      setMeasureSpecs(View.MeasureSpec.makeMeasureSpec(paramRecyclerView.getWidth(), 1073741824), View.MeasureSpec.makeMeasureSpec(paramRecyclerView.getHeight(), 1073741824));
    }
    
    public final void setItemPrefetchEnabled(boolean paramBoolean)
    {
      if (paramBoolean != this.mItemPrefetchEnabled)
      {
        this.mItemPrefetchEnabled = paramBoolean;
        this.mPrefetchMaxCountObserved = 0;
        if (this.mRecyclerView != null) {
          this.mRecyclerView.mRecycler.updateViewCacheSize();
        }
      }
    }
    
    void setMeasureSpecs(int paramInt1, int paramInt2)
    {
      this.mWidth = View.MeasureSpec.getSize(paramInt1);
      this.mWidthMode = View.MeasureSpec.getMode(paramInt1);
      if ((this.mWidthMode == 0) && (!RecyclerView.ALLOW_SIZE_IN_UNSPECIFIED_SPEC)) {
        this.mWidth = 0;
      }
      this.mHeight = View.MeasureSpec.getSize(paramInt2);
      this.mHeightMode = View.MeasureSpec.getMode(paramInt2);
      if ((this.mHeightMode == 0) && (!RecyclerView.ALLOW_SIZE_IN_UNSPECIFIED_SPEC)) {
        this.mHeight = 0;
      }
    }
    
    public void setMeasuredDimension(int paramInt1, int paramInt2)
    {
      this.mRecyclerView.setMeasuredDimension(paramInt1, paramInt2);
    }
    
    public void setMeasuredDimension(Rect paramRect, int paramInt1, int paramInt2)
    {
      int i = paramRect.width() + getPaddingLeft() + getPaddingRight();
      int j = paramRect.height() + getPaddingTop() + getPaddingBottom();
      setMeasuredDimension(chooseSize(paramInt1, i, getMinimumWidth()), chooseSize(paramInt2, j, getMinimumHeight()));
    }
    
    void setMeasuredDimensionFromChildren(int paramInt1, int paramInt2)
    {
      int i = getChildCount();
      if (i == 0)
      {
        this.mRecyclerView.defaultOnMeasure(paramInt1, paramInt2);
        return;
      }
      int j = Integer.MAX_VALUE;
      int k = Integer.MAX_VALUE;
      int m = Integer.MIN_VALUE;
      int n = Integer.MIN_VALUE;
      for (int i1 = 0; i1 < i; i1++)
      {
        View localView = getChildAt(i1);
        Rect localRect = this.mRecyclerView.mTempRect;
        getDecoratedBoundsWithMargins(localView, localRect);
        if (localRect.left < j) {
          j = localRect.left;
        }
        if (localRect.right > m) {
          m = localRect.right;
        }
        if (localRect.top < k) {
          k = localRect.top;
        }
        if (localRect.bottom > n) {
          n = localRect.bottom;
        }
      }
      this.mRecyclerView.mTempRect.set(j, k, m, n);
      setMeasuredDimension(this.mRecyclerView.mTempRect, paramInt1, paramInt2);
    }
    
    public void setMeasurementCacheEnabled(boolean paramBoolean)
    {
      this.mMeasurementCacheEnabled = paramBoolean;
    }
    
    void setRecyclerView(RecyclerView paramRecyclerView)
    {
      if (paramRecyclerView == null)
      {
        this.mRecyclerView = null;
        this.mChildHelper = null;
        this.mWidth = 0;
      }
      for (this.mHeight = 0;; this.mHeight = paramRecyclerView.getHeight())
      {
        this.mWidthMode = 1073741824;
        this.mHeightMode = 1073741824;
        return;
        this.mRecyclerView = paramRecyclerView;
        this.mChildHelper = paramRecyclerView.mChildHelper;
        this.mWidth = paramRecyclerView.getWidth();
      }
    }
    
    boolean shouldMeasureChild(View paramView, int paramInt1, int paramInt2, RecyclerView.LayoutParams paramLayoutParams)
    {
      return (paramView.isLayoutRequested()) || (!this.mMeasurementCacheEnabled) || (!isMeasurementUpToDate(paramView.getWidth(), paramInt1, paramLayoutParams.width)) || (!isMeasurementUpToDate(paramView.getHeight(), paramInt2, paramLayoutParams.height));
    }
    
    boolean shouldMeasureTwice()
    {
      return false;
    }
    
    boolean shouldReMeasureChild(View paramView, int paramInt1, int paramInt2, RecyclerView.LayoutParams paramLayoutParams)
    {
      return (!this.mMeasurementCacheEnabled) || (!isMeasurementUpToDate(paramView.getMeasuredWidth(), paramInt1, paramLayoutParams.width)) || (!isMeasurementUpToDate(paramView.getMeasuredHeight(), paramInt2, paramLayoutParams.height));
    }
    
    public void smoothScrollToPosition(RecyclerView paramRecyclerView, RecyclerView.State paramState, int paramInt)
    {
      Log.e("RecyclerView", "You must override smoothScrollToPosition to support smooth scrolling");
    }
    
    public void startSmoothScroll(RecyclerView.SmoothScroller paramSmoothScroller)
    {
      if ((this.mSmoothScroller != null) && (paramSmoothScroller != this.mSmoothScroller) && (this.mSmoothScroller.isRunning())) {
        this.mSmoothScroller.stop();
      }
      this.mSmoothScroller = paramSmoothScroller;
      this.mSmoothScroller.start(this.mRecyclerView, this);
    }
    
    public void stopIgnoringView(View paramView)
    {
      RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(paramView);
      localViewHolder.stopIgnoring();
      localViewHolder.resetInternal();
      localViewHolder.addFlags(4);
    }
    
    void stopSmoothScroller()
    {
      if (this.mSmoothScroller != null) {
        this.mSmoothScroller.stop();
      }
    }
    
    public boolean supportsPredictiveItemAnimations()
    {
      return false;
    }
    
    public static abstract interface LayoutPrefetchRegistry
    {
      public abstract void addPosition(int paramInt1, int paramInt2);
    }
    
    public static class Properties
    {
      public int orientation;
      public boolean reverseLayout;
      public int spanCount;
      public boolean stackFromEnd;
      
      public Properties() {}
    }
  }
  
  public static class LayoutParams
    extends ViewGroup.MarginLayoutParams
  {
    final Rect mDecorInsets = new Rect();
    boolean mInsetsDirty = true;
    boolean mPendingInvalidate = false;
    RecyclerView.ViewHolder mViewHolder;
    
    public LayoutParams(int paramInt1, int paramInt2)
    {
      super(paramInt2);
    }
    
    public LayoutParams(Context paramContext, AttributeSet paramAttributeSet)
    {
      super(paramAttributeSet);
    }
    
    public LayoutParams(LayoutParams paramLayoutParams)
    {
      super();
    }
    
    public LayoutParams(ViewGroup.LayoutParams paramLayoutParams)
    {
      super();
    }
    
    public LayoutParams(ViewGroup.MarginLayoutParams paramMarginLayoutParams)
    {
      super();
    }
    
    public int getViewAdapterPosition()
    {
      return this.mViewHolder.getAdapterPosition();
    }
    
    public int getViewLayoutPosition()
    {
      return this.mViewHolder.getLayoutPosition();
    }
    
    @Deprecated
    public int getViewPosition()
    {
      return this.mViewHolder.getPosition();
    }
    
    public boolean isItemChanged()
    {
      return this.mViewHolder.isUpdated();
    }
    
    public boolean isItemRemoved()
    {
      return this.mViewHolder.isRemoved();
    }
    
    public boolean isViewInvalid()
    {
      return this.mViewHolder.isInvalid();
    }
    
    public boolean viewNeedsUpdate()
    {
      return this.mViewHolder.needsUpdate();
    }
  }
  
  public static abstract interface OnChildAttachStateChangeListener
  {
    public abstract void onChildViewAttachedToWindow(View paramView);
    
    public abstract void onChildViewDetachedFromWindow(View paramView);
  }
  
  public static abstract class OnFlingListener
  {
    public OnFlingListener() {}
    
    public abstract boolean onFling(int paramInt1, int paramInt2);
  }
  
  public static abstract interface OnItemTouchListener
  {
    public abstract boolean onInterceptTouchEvent(RecyclerView paramRecyclerView, MotionEvent paramMotionEvent);
    
    public abstract void onRequestDisallowInterceptTouchEvent(boolean paramBoolean);
    
    public abstract void onTouchEvent(RecyclerView paramRecyclerView, MotionEvent paramMotionEvent);
  }
  
  public static abstract class OnScrollListener
  {
    public OnScrollListener() {}
    
    public void onScrollStateChanged(RecyclerView paramRecyclerView, int paramInt) {}
    
    public void onScrolled(RecyclerView paramRecyclerView, int paramInt1, int paramInt2) {}
  }
  
  public static class RecycledViewPool
  {
    private static final int DEFAULT_MAX_SCRAP = 5;
    private int mAttachCount = 0;
    SparseArray<ScrapData> mScrap = new SparseArray();
    
    public RecycledViewPool() {}
    
    private ScrapData getScrapDataForType(int paramInt)
    {
      ScrapData localScrapData = (ScrapData)this.mScrap.get(paramInt);
      if (localScrapData == null)
      {
        localScrapData = new ScrapData();
        this.mScrap.put(paramInt, localScrapData);
      }
      return localScrapData;
    }
    
    void attach(RecyclerView.Adapter paramAdapter)
    {
      this.mAttachCount = (1 + this.mAttachCount);
    }
    
    public void clear()
    {
      for (int i = 0; i < this.mScrap.size(); i++) {
        ((ScrapData)this.mScrap.valueAt(i)).mScrapHeap.clear();
      }
    }
    
    void detach()
    {
      this.mAttachCount = (-1 + this.mAttachCount);
    }
    
    void factorInBindTime(int paramInt, long paramLong)
    {
      ScrapData localScrapData = getScrapDataForType(paramInt);
      localScrapData.mBindRunningAverageNs = runningAverage(localScrapData.mBindRunningAverageNs, paramLong);
    }
    
    void factorInCreateTime(int paramInt, long paramLong)
    {
      ScrapData localScrapData = getScrapDataForType(paramInt);
      localScrapData.mCreateRunningAverageNs = runningAverage(localScrapData.mCreateRunningAverageNs, paramLong);
    }
    
    public RecyclerView.ViewHolder getRecycledView(int paramInt)
    {
      ScrapData localScrapData = (ScrapData)this.mScrap.get(paramInt);
      if ((localScrapData != null) && (!localScrapData.mScrapHeap.isEmpty()))
      {
        ArrayList localArrayList = localScrapData.mScrapHeap;
        return (RecyclerView.ViewHolder)localArrayList.remove(-1 + localArrayList.size());
      }
      return null;
    }
    
    public int getRecycledViewCount(int paramInt)
    {
      return getScrapDataForType(paramInt).mScrapHeap.size();
    }
    
    void onAdapterChanged(RecyclerView.Adapter paramAdapter1, RecyclerView.Adapter paramAdapter2, boolean paramBoolean)
    {
      if (paramAdapter1 != null) {
        detach();
      }
      if ((!paramBoolean) && (this.mAttachCount == 0)) {
        clear();
      }
      if (paramAdapter2 != null) {
        attach(paramAdapter2);
      }
    }
    
    public void putRecycledView(RecyclerView.ViewHolder paramViewHolder)
    {
      int i = paramViewHolder.getItemViewType();
      ArrayList localArrayList = getScrapDataForType(i).mScrapHeap;
      if (((ScrapData)this.mScrap.get(i)).mMaxScrap <= localArrayList.size()) {
        return;
      }
      paramViewHolder.resetInternal();
      localArrayList.add(paramViewHolder);
    }
    
    long runningAverage(long paramLong1, long paramLong2)
    {
      if (paramLong1 == 0L) {
        return paramLong2;
      }
      return 3L * (paramLong1 / 4L) + paramLong2 / 4L;
    }
    
    public void setMaxRecycledViews(int paramInt1, int paramInt2)
    {
      ScrapData localScrapData = getScrapDataForType(paramInt1);
      localScrapData.mMaxScrap = paramInt2;
      ArrayList localArrayList = localScrapData.mScrapHeap;
      if (localArrayList != null) {
        while (localArrayList.size() > paramInt2) {
          localArrayList.remove(-1 + localArrayList.size());
        }
      }
    }
    
    int size()
    {
      int i = 0;
      for (int j = 0; j < this.mScrap.size(); j++)
      {
        ArrayList localArrayList = ((ScrapData)this.mScrap.valueAt(j)).mScrapHeap;
        if (localArrayList != null) {
          i += localArrayList.size();
        }
      }
      return i;
    }
    
    boolean willBindInTime(int paramInt, long paramLong1, long paramLong2)
    {
      long l = getScrapDataForType(paramInt).mBindRunningAverageNs;
      return (l == 0L) || (paramLong1 + l < paramLong2);
    }
    
    boolean willCreateInTime(int paramInt, long paramLong1, long paramLong2)
    {
      long l = getScrapDataForType(paramInt).mCreateRunningAverageNs;
      return (l == 0L) || (paramLong1 + l < paramLong2);
    }
    
    static class ScrapData
    {
      long mBindRunningAverageNs = 0L;
      long mCreateRunningAverageNs = 0L;
      int mMaxScrap = 5;
      ArrayList<RecyclerView.ViewHolder> mScrapHeap = new ArrayList();
      
      ScrapData() {}
    }
  }
  
  public final class Recycler
  {
    static final int DEFAULT_CACHE_SIZE = 2;
    final ArrayList<RecyclerView.ViewHolder> mAttachedScrap = new ArrayList();
    final ArrayList<RecyclerView.ViewHolder> mCachedViews = new ArrayList();
    ArrayList<RecyclerView.ViewHolder> mChangedScrap = null;
    RecyclerView.RecycledViewPool mRecyclerPool;
    private int mRequestedCacheMax = 2;
    private final List<RecyclerView.ViewHolder> mUnmodifiableAttachedScrap = Collections.unmodifiableList(this.mAttachedScrap);
    private RecyclerView.ViewCacheExtension mViewCacheExtension;
    int mViewCacheMax = 2;
    
    public Recycler() {}
    
    private void attachAccessibilityDelegate(View paramView)
    {
      if (RecyclerView.this.isAccessibilityEnabled())
      {
        if (ViewCompat.getImportantForAccessibility(paramView) == 0) {
          ViewCompat.setImportantForAccessibility(paramView, 1);
        }
        if (!ViewCompat.hasAccessibilityDelegate(paramView)) {
          ViewCompat.setAccessibilityDelegate(paramView, RecyclerView.this.mAccessibilityDelegate.getItemDelegate());
        }
      }
    }
    
    private void invalidateDisplayListInt(RecyclerView.ViewHolder paramViewHolder)
    {
      if ((paramViewHolder.itemView instanceof ViewGroup)) {
        invalidateDisplayListInt((ViewGroup)paramViewHolder.itemView, false);
      }
    }
    
    private void invalidateDisplayListInt(ViewGroup paramViewGroup, boolean paramBoolean)
    {
      for (int i = -1 + paramViewGroup.getChildCount(); i >= 0; i--)
      {
        View localView = paramViewGroup.getChildAt(i);
        if ((localView instanceof ViewGroup)) {
          invalidateDisplayListInt((ViewGroup)localView, true);
        }
      }
      if (!paramBoolean) {
        return;
      }
      if (paramViewGroup.getVisibility() == 4)
      {
        paramViewGroup.setVisibility(0);
        paramViewGroup.setVisibility(4);
        return;
      }
      int j = paramViewGroup.getVisibility();
      paramViewGroup.setVisibility(4);
      paramViewGroup.setVisibility(j);
    }
    
    private boolean tryBindViewHolderByDeadline(RecyclerView.ViewHolder paramViewHolder, int paramInt1, int paramInt2, long paramLong)
    {
      paramViewHolder.mOwnerRecyclerView = RecyclerView.this;
      int i = paramViewHolder.getItemViewType();
      long l1 = RecyclerView.this.getNanoTime();
      if ((paramLong != Long.MAX_VALUE) && (!this.mRecyclerPool.willBindInTime(i, l1, paramLong))) {
        return false;
      }
      RecyclerView.this.mAdapter.bindViewHolder(paramViewHolder, paramInt1);
      long l2 = RecyclerView.this.getNanoTime();
      this.mRecyclerPool.factorInBindTime(paramViewHolder.getItemViewType(), l2 - l1);
      attachAccessibilityDelegate(paramViewHolder.itemView);
      if (RecyclerView.this.mState.isPreLayout()) {
        paramViewHolder.mPreLayoutPosition = paramInt2;
      }
      return true;
    }
    
    void addViewHolderToRecycledViewPool(RecyclerView.ViewHolder paramViewHolder, boolean paramBoolean)
    {
      RecyclerView.clearNestedRecyclerViewIfNotNested(paramViewHolder);
      ViewCompat.setAccessibilityDelegate(paramViewHolder.itemView, null);
      if (paramBoolean) {
        dispatchViewRecycled(paramViewHolder);
      }
      paramViewHolder.mOwnerRecyclerView = null;
      getRecycledViewPool().putRecycledView(paramViewHolder);
    }
    
    public void bindViewToPosition(View paramView, int paramInt)
    {
      RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(paramView);
      if (localViewHolder == null) {
        throw new IllegalArgumentException("The view does not have a ViewHolder. You cannot pass arbitrary views to this method, they should be created by the Adapter");
      }
      int i = RecyclerView.this.mAdapterHelper.findPositionOffset(paramInt);
      if ((i < 0) || (i >= RecyclerView.this.mAdapter.getItemCount())) {
        throw new IndexOutOfBoundsException("Inconsistency detected. Invalid item position " + paramInt + "(offset:" + i + ")." + "state:" + RecyclerView.this.mState.getItemCount());
      }
      tryBindViewHolderByDeadline(localViewHolder, i, paramInt, Long.MAX_VALUE);
      ViewGroup.LayoutParams localLayoutParams = localViewHolder.itemView.getLayoutParams();
      RecyclerView.LayoutParams localLayoutParams1;
      if (localLayoutParams == null)
      {
        localLayoutParams1 = (RecyclerView.LayoutParams)RecyclerView.this.generateDefaultLayoutParams();
        localViewHolder.itemView.setLayoutParams(localLayoutParams1);
        localLayoutParams1.mInsetsDirty = true;
        localLayoutParams1.mViewHolder = localViewHolder;
        if (localViewHolder.itemView.getParent() != null) {
          break label240;
        }
      }
      label240:
      for (boolean bool = true;; bool = false)
      {
        localLayoutParams1.mPendingInvalidate = bool;
        return;
        if (!RecyclerView.this.checkLayoutParams(localLayoutParams))
        {
          localLayoutParams1 = (RecyclerView.LayoutParams)RecyclerView.this.generateLayoutParams(localLayoutParams);
          localViewHolder.itemView.setLayoutParams(localLayoutParams1);
          break;
        }
        localLayoutParams1 = (RecyclerView.LayoutParams)localLayoutParams;
        break;
      }
    }
    
    public void clear()
    {
      this.mAttachedScrap.clear();
      recycleAndClearCachedViews();
    }
    
    void clearOldPositions()
    {
      int i = this.mCachedViews.size();
      for (int j = 0; j < i; j++) {
        ((RecyclerView.ViewHolder)this.mCachedViews.get(j)).clearOldPosition();
      }
      int k = this.mAttachedScrap.size();
      for (int m = 0; m < k; m++) {
        ((RecyclerView.ViewHolder)this.mAttachedScrap.get(m)).clearOldPosition();
      }
      if (this.mChangedScrap != null)
      {
        int n = this.mChangedScrap.size();
        for (int i1 = 0; i1 < n; i1++) {
          ((RecyclerView.ViewHolder)this.mChangedScrap.get(i1)).clearOldPosition();
        }
      }
    }
    
    void clearScrap()
    {
      this.mAttachedScrap.clear();
      if (this.mChangedScrap != null) {
        this.mChangedScrap.clear();
      }
    }
    
    public int convertPreLayoutPositionToPostLayout(int paramInt)
    {
      if ((paramInt < 0) || (paramInt >= RecyclerView.this.mState.getItemCount())) {
        throw new IndexOutOfBoundsException("invalid position " + paramInt + ". State " + "item count is " + RecyclerView.this.mState.getItemCount());
      }
      if (!RecyclerView.this.mState.isPreLayout()) {
        return paramInt;
      }
      return RecyclerView.this.mAdapterHelper.findPositionOffset(paramInt);
    }
    
    void dispatchViewRecycled(RecyclerView.ViewHolder paramViewHolder)
    {
      if (RecyclerView.this.mRecyclerListener != null) {
        RecyclerView.this.mRecyclerListener.onViewRecycled(paramViewHolder);
      }
      if (RecyclerView.this.mAdapter != null) {
        RecyclerView.this.mAdapter.onViewRecycled(paramViewHolder);
      }
      if (RecyclerView.this.mState != null) {
        RecyclerView.this.mViewInfoStore.removeViewHolder(paramViewHolder);
      }
    }
    
    RecyclerView.ViewHolder getChangedScrapViewForPosition(int paramInt)
    {
      int i;
      if (this.mChangedScrap != null)
      {
        i = this.mChangedScrap.size();
        if (i != 0) {}
      }
      else
      {
        return null;
      }
      for (int j = 0; j < i; j++)
      {
        RecyclerView.ViewHolder localViewHolder2 = (RecyclerView.ViewHolder)this.mChangedScrap.get(j);
        if ((!localViewHolder2.wasReturnedFromScrap()) && (localViewHolder2.getLayoutPosition() == paramInt))
        {
          localViewHolder2.addFlags(32);
          return localViewHolder2;
        }
      }
      if (RecyclerView.this.mAdapter.hasStableIds())
      {
        int k = RecyclerView.this.mAdapterHelper.findPositionOffset(paramInt);
        if ((k > 0) && (k < RecyclerView.this.mAdapter.getItemCount()))
        {
          long l = RecyclerView.this.mAdapter.getItemId(k);
          for (int m = 0; m < i; m++)
          {
            RecyclerView.ViewHolder localViewHolder1 = (RecyclerView.ViewHolder)this.mChangedScrap.get(m);
            if ((!localViewHolder1.wasReturnedFromScrap()) && (localViewHolder1.getItemId() == l))
            {
              localViewHolder1.addFlags(32);
              return localViewHolder1;
            }
          }
        }
      }
      return null;
    }
    
    RecyclerView.RecycledViewPool getRecycledViewPool()
    {
      if (this.mRecyclerPool == null) {
        this.mRecyclerPool = new RecyclerView.RecycledViewPool();
      }
      return this.mRecyclerPool;
    }
    
    int getScrapCount()
    {
      return this.mAttachedScrap.size();
    }
    
    public List<RecyclerView.ViewHolder> getScrapList()
    {
      return this.mUnmodifiableAttachedScrap;
    }
    
    RecyclerView.ViewHolder getScrapOrCachedViewForId(long paramLong, int paramInt, boolean paramBoolean)
    {
      RecyclerView.ViewHolder localViewHolder;
      for (int i = -1 + this.mAttachedScrap.size(); i >= 0; i--)
      {
        localViewHolder = (RecyclerView.ViewHolder)this.mAttachedScrap.get(i);
        if ((localViewHolder.getItemId() == paramLong) && (!localViewHolder.wasReturnedFromScrap()))
        {
          if (paramInt == localViewHolder.getItemViewType())
          {
            localViewHolder.addFlags(32);
            if ((localViewHolder.isRemoved()) && (!RecyclerView.this.mState.isPreLayout())) {
              localViewHolder.setFlags(2, 14);
            }
            return localViewHolder;
          }
          if (!paramBoolean)
          {
            this.mAttachedScrap.remove(i);
            RecyclerView.this.removeDetachedView(localViewHolder.itemView, false);
            quickRecycleScrapView(localViewHolder.itemView);
          }
        }
      }
      for (int j = -1 + this.mCachedViews.size();; j--)
      {
        if (j < 0) {
          break label225;
        }
        localViewHolder = (RecyclerView.ViewHolder)this.mCachedViews.get(j);
        if (localViewHolder.getItemId() == paramLong)
        {
          if (paramInt == localViewHolder.getItemViewType())
          {
            if (paramBoolean) {
              break;
            }
            this.mCachedViews.remove(j);
            return localViewHolder;
          }
          if (!paramBoolean)
          {
            recycleCachedViewAt(j);
            return null;
          }
        }
      }
      label225:
      return null;
    }
    
    RecyclerView.ViewHolder getScrapOrHiddenOrCachedHolderForPosition(int paramInt, boolean paramBoolean)
    {
      int i = this.mAttachedScrap.size();
      RecyclerView.ViewHolder localViewHolder1;
      for (int j = 0; j < i; j++)
      {
        localViewHolder1 = (RecyclerView.ViewHolder)this.mAttachedScrap.get(j);
        if ((!localViewHolder1.wasReturnedFromScrap()) && (localViewHolder1.getLayoutPosition() == paramInt) && (!localViewHolder1.isInvalid()) && ((RecyclerView.this.mState.mInPreLayout) || (!localViewHolder1.isRemoved())))
        {
          localViewHolder1.addFlags(32);
          return localViewHolder1;
        }
      }
      if (!paramBoolean)
      {
        View localView = RecyclerView.this.mChildHelper.findHiddenNonRemovedView(paramInt);
        if (localView != null)
        {
          RecyclerView.ViewHolder localViewHolder2 = RecyclerView.getChildViewHolderInt(localView);
          RecyclerView.this.mChildHelper.unhide(localView);
          int n = RecyclerView.this.mChildHelper.indexOfChild(localView);
          if (n == -1) {
            throw new IllegalStateException("layout index should not be -1 after unhiding a view:" + localViewHolder2);
          }
          RecyclerView.this.mChildHelper.detachViewFromParent(n);
          scrapView(localView);
          localViewHolder2.addFlags(8224);
          return localViewHolder2;
        }
      }
      int k = this.mCachedViews.size();
      for (int m = 0;; m++)
      {
        if (m >= k) {
          break label285;
        }
        localViewHolder1 = (RecyclerView.ViewHolder)this.mCachedViews.get(m);
        if ((!localViewHolder1.isInvalid()) && (localViewHolder1.getLayoutPosition() == paramInt))
        {
          if (paramBoolean) {
            break;
          }
          this.mCachedViews.remove(m);
          return localViewHolder1;
        }
      }
      label285:
      return null;
    }
    
    View getScrapViewAt(int paramInt)
    {
      return ((RecyclerView.ViewHolder)this.mAttachedScrap.get(paramInt)).itemView;
    }
    
    public View getViewForPosition(int paramInt)
    {
      return getViewForPosition(paramInt, false);
    }
    
    View getViewForPosition(int paramInt, boolean paramBoolean)
    {
      return tryGetViewHolderForPositionByDeadline(paramInt, paramBoolean, Long.MAX_VALUE).itemView;
    }
    
    void markItemDecorInsetsDirty()
    {
      int i = this.mCachedViews.size();
      for (int j = 0; j < i; j++)
      {
        RecyclerView.LayoutParams localLayoutParams = (RecyclerView.LayoutParams)((RecyclerView.ViewHolder)this.mCachedViews.get(j)).itemView.getLayoutParams();
        if (localLayoutParams != null) {
          localLayoutParams.mInsetsDirty = true;
        }
      }
    }
    
    void markKnownViewsInvalid()
    {
      int i;
      int j;
      if ((RecyclerView.this.mAdapter != null) && (RecyclerView.this.mAdapter.hasStableIds()))
      {
        i = this.mCachedViews.size();
        j = 0;
      }
      while (j < i)
      {
        RecyclerView.ViewHolder localViewHolder = (RecyclerView.ViewHolder)this.mCachedViews.get(j);
        if (localViewHolder != null)
        {
          localViewHolder.addFlags(6);
          localViewHolder.addChangePayload(null);
        }
        j++;
        continue;
        recycleAndClearCachedViews();
      }
    }
    
    void offsetPositionRecordsForInsert(int paramInt1, int paramInt2)
    {
      int i = this.mCachedViews.size();
      for (int j = 0; j < i; j++)
      {
        RecyclerView.ViewHolder localViewHolder = (RecyclerView.ViewHolder)this.mCachedViews.get(j);
        if ((localViewHolder != null) && (localViewHolder.mPosition >= paramInt1)) {
          localViewHolder.offsetPosition(paramInt2, true);
        }
      }
    }
    
    void offsetPositionRecordsForMove(int paramInt1, int paramInt2)
    {
      int i;
      int j;
      int k;
      int n;
      label25:
      RecyclerView.ViewHolder localViewHolder;
      if (paramInt1 < paramInt2)
      {
        i = paramInt1;
        j = paramInt2;
        k = -1;
        int m = this.mCachedViews.size();
        n = 0;
        if (n >= m) {
          return;
        }
        localViewHolder = (RecyclerView.ViewHolder)this.mCachedViews.get(n);
        if ((localViewHolder != null) && (localViewHolder.mPosition >= i) && (localViewHolder.mPosition <= j)) {
          break label87;
        }
      }
      for (;;)
      {
        n++;
        break label25;
        i = paramInt2;
        j = paramInt1;
        k = 1;
        break;
        label87:
        if (localViewHolder.mPosition == paramInt1) {
          localViewHolder.offsetPosition(paramInt2 - paramInt1, false);
        } else {
          localViewHolder.offsetPosition(k, false);
        }
      }
    }
    
    void offsetPositionRecordsForRemove(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      int i = paramInt1 + paramInt2;
      int j = -1 + this.mCachedViews.size();
      if (j >= 0)
      {
        RecyclerView.ViewHolder localViewHolder = (RecyclerView.ViewHolder)this.mCachedViews.get(j);
        if (localViewHolder != null)
        {
          if (localViewHolder.mPosition < i) {
            break label64;
          }
          localViewHolder.offsetPosition(-paramInt2, paramBoolean);
        }
        for (;;)
        {
          j--;
          break;
          label64:
          if (localViewHolder.mPosition >= paramInt1)
          {
            localViewHolder.addFlags(8);
            recycleCachedViewAt(j);
          }
        }
      }
    }
    
    void onAdapterChanged(RecyclerView.Adapter paramAdapter1, RecyclerView.Adapter paramAdapter2, boolean paramBoolean)
    {
      clear();
      getRecycledViewPool().onAdapterChanged(paramAdapter1, paramAdapter2, paramBoolean);
    }
    
    void quickRecycleScrapView(View paramView)
    {
      RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(paramView);
      RecyclerView.ViewHolder.access$802(localViewHolder, null);
      RecyclerView.ViewHolder.access$902(localViewHolder, false);
      localViewHolder.clearReturnedFromScrapFlag();
      recycleViewHolderInternal(localViewHolder);
    }
    
    void recycleAndClearCachedViews()
    {
      for (int i = -1 + this.mCachedViews.size(); i >= 0; i--) {
        recycleCachedViewAt(i);
      }
      this.mCachedViews.clear();
      if (RecyclerView.ALLOW_THREAD_GAP_WORK) {
        RecyclerView.this.mPrefetchRegistry.clearPrefetchPositions();
      }
    }
    
    void recycleCachedViewAt(int paramInt)
    {
      addViewHolderToRecycledViewPool((RecyclerView.ViewHolder)this.mCachedViews.get(paramInt), true);
      this.mCachedViews.remove(paramInt);
    }
    
    public void recycleView(View paramView)
    {
      RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(paramView);
      if (localViewHolder.isTmpDetached()) {
        RecyclerView.this.removeDetachedView(paramView, false);
      }
      if (localViewHolder.isScrap()) {
        localViewHolder.unScrap();
      }
      for (;;)
      {
        recycleViewHolderInternal(localViewHolder);
        return;
        if (localViewHolder.wasReturnedFromScrap()) {
          localViewHolder.clearReturnedFromScrapFlag();
        }
      }
    }
    
    void recycleViewHolderInternal(RecyclerView.ViewHolder paramViewHolder)
    {
      if ((paramViewHolder.isScrap()) || (paramViewHolder.itemView.getParent() != null))
      {
        StringBuilder localStringBuilder = new StringBuilder().append("Scrapped or attached views may not be recycled. isScrap:").append(paramViewHolder.isScrap()).append(" isAttached:");
        ViewParent localViewParent = paramViewHolder.itemView.getParent();
        boolean bool1 = false;
        if (localViewParent != null) {
          bool1 = true;
        }
        throw new IllegalArgumentException(bool1);
      }
      if (paramViewHolder.isTmpDetached()) {
        throw new IllegalArgumentException("Tmp detached view should be removed from RecyclerView before it can be recycled: " + paramViewHolder);
      }
      if (paramViewHolder.shouldIgnore()) {
        throw new IllegalArgumentException("Trying to recycle an ignored view holder. You should first call stopIgnoringView(view) before calling recycle.");
      }
      boolean bool2 = RecyclerView.ViewHolder.access$700(paramViewHolder);
      int i;
      int k;
      int m;
      int n;
      int i1;
      if ((RecyclerView.this.mAdapter != null) && (bool2) && (RecyclerView.this.mAdapter.onFailedToRecycleView(paramViewHolder)))
      {
        i = 1;
        if (i == 0)
        {
          boolean bool4 = paramViewHolder.isRecyclable();
          k = 0;
          m = 0;
          if (!bool4) {}
        }
        else
        {
          int j = this.mViewCacheMax;
          k = 0;
          if (j > 0)
          {
            boolean bool3 = paramViewHolder.hasAnyOfTheFlags(526);
            k = 0;
            if (!bool3)
            {
              n = this.mCachedViews.size();
              if ((n >= this.mViewCacheMax) && (n > 0))
              {
                recycleCachedViewAt(0);
                n--;
              }
              i1 = n;
              if ((!RecyclerView.ALLOW_THREAD_GAP_WORK) || (n <= 0) || (RecyclerView.this.mPrefetchRegistry.lastPrefetchIncludedPosition(paramViewHolder.mPosition))) {}
            }
          }
        }
      }
      for (int i2 = n - 1;; i2--) {
        if (i2 >= 0)
        {
          int i3 = ((RecyclerView.ViewHolder)this.mCachedViews.get(i2)).mPosition;
          if (RecyclerView.this.mPrefetchRegistry.lastPrefetchIncludedPosition(i3)) {}
        }
        else
        {
          i1 = i2 + 1;
          this.mCachedViews.add(i1, paramViewHolder);
          k = 1;
          m = 0;
          if (k == 0)
          {
            addViewHolderToRecycledViewPool(paramViewHolder, true);
            m = 1;
          }
          RecyclerView.this.mViewInfoStore.removeViewHolder(paramViewHolder);
          if ((k == 0) && (m == 0) && (bool2)) {
            paramViewHolder.mOwnerRecyclerView = null;
          }
          return;
          i = 0;
          break;
        }
      }
    }
    
    void recycleViewInternal(View paramView)
    {
      recycleViewHolderInternal(RecyclerView.getChildViewHolderInt(paramView));
    }
    
    void scrapView(View paramView)
    {
      RecyclerView.ViewHolder localViewHolder = RecyclerView.getChildViewHolderInt(paramView);
      if ((localViewHolder.hasAnyOfTheFlags(12)) || (!localViewHolder.isUpdated()) || (RecyclerView.this.canReuseUpdatedViewHolder(localViewHolder)))
      {
        if ((localViewHolder.isInvalid()) && (!localViewHolder.isRemoved()) && (!RecyclerView.this.mAdapter.hasStableIds())) {
          throw new IllegalArgumentException("Called scrap view with an invalid view. Invalid views cannot be reused from scrap, they should rebound from recycler pool.");
        }
        localViewHolder.setScrapContainer(this, false);
        this.mAttachedScrap.add(localViewHolder);
        return;
      }
      if (this.mChangedScrap == null) {
        this.mChangedScrap = new ArrayList();
      }
      localViewHolder.setScrapContainer(this, true);
      this.mChangedScrap.add(localViewHolder);
    }
    
    void setAdapterPositionsAsUnknown()
    {
      int i = this.mCachedViews.size();
      for (int j = 0; j < i; j++)
      {
        RecyclerView.ViewHolder localViewHolder = (RecyclerView.ViewHolder)this.mCachedViews.get(j);
        if (localViewHolder != null) {
          localViewHolder.addFlags(512);
        }
      }
    }
    
    void setRecycledViewPool(RecyclerView.RecycledViewPool paramRecycledViewPool)
    {
      if (this.mRecyclerPool != null) {
        this.mRecyclerPool.detach();
      }
      this.mRecyclerPool = paramRecycledViewPool;
      if (paramRecycledViewPool != null) {
        this.mRecyclerPool.attach(RecyclerView.this.getAdapter());
      }
    }
    
    void setViewCacheExtension(RecyclerView.ViewCacheExtension paramViewCacheExtension)
    {
      this.mViewCacheExtension = paramViewCacheExtension;
    }
    
    public void setViewCacheSize(int paramInt)
    {
      this.mRequestedCacheMax = paramInt;
      updateViewCacheSize();
    }
    
    @Nullable
    RecyclerView.ViewHolder tryGetViewHolderForPositionByDeadline(int paramInt, boolean paramBoolean, long paramLong)
    {
      if ((paramInt < 0) || (paramInt >= RecyclerView.this.mState.getItemCount())) {
        throw new IndexOutOfBoundsException("Invalid item position " + paramInt + "(" + paramInt + "). Item count:" + RecyclerView.this.mState.getItemCount());
      }
      boolean bool1 = RecyclerView.this.mState.isPreLayout();
      Object localObject1 = null;
      int i = 0;
      if (bool1)
      {
        localObject1 = getChangedScrapViewForPosition(paramInt);
        if (localObject1 != null) {
          i = 1;
        }
      }
      else if (localObject1 == null)
      {
        localObject1 = getScrapOrHiddenOrCachedHolderForPosition(paramInt, paramBoolean);
        if (localObject1 != null)
        {
          if (validateViewHolderForOffsetPosition((RecyclerView.ViewHolder)localObject1)) {
            break label305;
          }
          if (!paramBoolean)
          {
            ((RecyclerView.ViewHolder)localObject1).addFlags(4);
            if (!((RecyclerView.ViewHolder)localObject1).isScrap()) {
              break label289;
            }
            RecyclerView.this.removeDetachedView(((RecyclerView.ViewHolder)localObject1).itemView, false);
            ((RecyclerView.ViewHolder)localObject1).unScrap();
            label176:
            recycleViewHolderInternal((RecyclerView.ViewHolder)localObject1);
          }
          localObject1 = null;
        }
      }
      label289:
      label305:
      Object localObject2;
      for (;;)
      {
        if (localObject1 == null)
        {
          int k = RecyclerView.this.mAdapterHelper.findPositionOffset(paramInt);
          if ((k < 0) || (k >= RecyclerView.this.mAdapter.getItemCount()))
          {
            throw new IndexOutOfBoundsException("Inconsistency detected. Invalid item position " + paramInt + "(offset:" + k + ")." + "state:" + RecyclerView.this.mState.getItemCount());
            i = 0;
            break;
            if (!((RecyclerView.ViewHolder)localObject1).wasReturnedFromScrap()) {
              break label176;
            }
            ((RecyclerView.ViewHolder)localObject1).clearReturnedFromScrapFlag();
            break label176;
            i = 1;
            continue;
          }
          int m = RecyclerView.this.mAdapter.getItemViewType(k);
          if (RecyclerView.this.mAdapter.hasStableIds())
          {
            localObject1 = getScrapOrCachedViewForId(RecyclerView.this.mAdapter.getItemId(k), m, paramBoolean);
            if (localObject1 != null)
            {
              ((RecyclerView.ViewHolder)localObject1).mPosition = k;
              i = 1;
            }
          }
          if ((localObject1 == null) && (this.mViewCacheExtension != null))
          {
            View localView = this.mViewCacheExtension.getViewForPositionAndType(this, paramInt, m);
            if (localView != null)
            {
              localObject1 = RecyclerView.this.getChildViewHolder(localView);
              if (localObject1 == null) {
                throw new IllegalArgumentException("getViewForPositionAndType returned a view which does not have a ViewHolder");
              }
              if (((RecyclerView.ViewHolder)localObject1).shouldIgnore()) {
                throw new IllegalArgumentException("getViewForPositionAndType returned a view that is ignored. You must call stopIgnoring before returning this view.");
              }
            }
          }
          if (localObject1 == null)
          {
            localObject1 = getRecycledViewPool().getRecycledView(m);
            if (localObject1 != null)
            {
              ((RecyclerView.ViewHolder)localObject1).resetInternal();
              if (RecyclerView.FORCE_INVALIDATE_DISPLAY_LIST) {
                invalidateDisplayListInt((RecyclerView.ViewHolder)localObject1);
              }
            }
          }
          localObject2 = localObject1;
          if (localObject2 != null) {
            break label915;
          }
          long l1 = RecyclerView.this.getNanoTime();
          if ((paramLong != Long.MAX_VALUE) && (!this.mRecyclerPool.willCreateInTime(m, l1, paramLong))) {
            return null;
          }
          localObject1 = RecyclerView.this.mAdapter.createViewHolder(RecyclerView.this, m);
          if (RecyclerView.ALLOW_THREAD_GAP_WORK)
          {
            RecyclerView localRecyclerView = RecyclerView.findNestedRecyclerView(((RecyclerView.ViewHolder)localObject1).itemView);
            if (localRecyclerView != null) {
              ((RecyclerView.ViewHolder)localObject1).mNestedRecyclerView = new WeakReference(localRecyclerView);
            }
          }
          long l2 = RecyclerView.this.getNanoTime();
          this.mRecyclerPool.factorInCreateTime(m, l2 - l1);
        }
      }
      for (;;)
      {
        if ((i != 0) && (!RecyclerView.this.mState.isPreLayout()) && (((RecyclerView.ViewHolder)localObject1).hasAnyOfTheFlags(8192)))
        {
          ((RecyclerView.ViewHolder)localObject1).setFlags(0, 8192);
          if (RecyclerView.this.mState.mRunSimpleAnimations)
          {
            int j = 0x1000 | RecyclerView.ItemAnimator.buildAdapterChangeFlagsForAnimations((RecyclerView.ViewHolder)localObject1);
            RecyclerView.ItemAnimator.ItemHolderInfo localItemHolderInfo = RecyclerView.this.mItemAnimator.recordPreLayoutInformation(RecyclerView.this.mState, (RecyclerView.ViewHolder)localObject1, j, ((RecyclerView.ViewHolder)localObject1).getUnmodifiedPayloads());
            RecyclerView.this.recordAnimationInfoIfBouncedHiddenView((RecyclerView.ViewHolder)localObject1, localItemHolderInfo);
          }
        }
        boolean bool2 = false;
        ViewGroup.LayoutParams localLayoutParams;
        RecyclerView.LayoutParams localLayoutParams1;
        if ((RecyclerView.this.mState.isPreLayout()) && (((RecyclerView.ViewHolder)localObject1).isBound()))
        {
          ((RecyclerView.ViewHolder)localObject1).mPreLayoutPosition = paramInt;
          localLayoutParams = ((RecyclerView.ViewHolder)localObject1).itemView.getLayoutParams();
          if (localLayoutParams != null) {
            break label860;
          }
          localLayoutParams1 = (RecyclerView.LayoutParams)RecyclerView.this.generateDefaultLayoutParams();
          ((RecyclerView.ViewHolder)localObject1).itemView.setLayoutParams(localLayoutParams1);
          label775:
          localLayoutParams1.mViewHolder = ((RecyclerView.ViewHolder)localObject1);
          if ((i == 0) || (!bool2)) {
            break label909;
          }
        }
        label860:
        label909:
        for (boolean bool3 = true;; bool3 = false)
        {
          localLayoutParams1.mPendingInvalidate = bool3;
          return localObject1;
          if ((((RecyclerView.ViewHolder)localObject1).isBound()) && (!((RecyclerView.ViewHolder)localObject1).needsUpdate()))
          {
            boolean bool4 = ((RecyclerView.ViewHolder)localObject1).isInvalid();
            bool2 = false;
            if (!bool4) {
              break;
            }
          }
          bool2 = tryBindViewHolderByDeadline((RecyclerView.ViewHolder)localObject1, RecyclerView.this.mAdapterHelper.findPositionOffset(paramInt), paramInt, paramLong);
          break;
          if (!RecyclerView.this.checkLayoutParams(localLayoutParams))
          {
            localLayoutParams1 = (RecyclerView.LayoutParams)RecyclerView.this.generateLayoutParams(localLayoutParams);
            ((RecyclerView.ViewHolder)localObject1).itemView.setLayoutParams(localLayoutParams1);
            break label775;
          }
          localLayoutParams1 = (RecyclerView.LayoutParams)localLayoutParams;
          break label775;
        }
        label915:
        localObject1 = localObject2;
      }
    }
    
    void unscrapView(RecyclerView.ViewHolder paramViewHolder)
    {
      if (RecyclerView.ViewHolder.access$900(paramViewHolder)) {
        this.mChangedScrap.remove(paramViewHolder);
      }
      for (;;)
      {
        RecyclerView.ViewHolder.access$802(paramViewHolder, null);
        RecyclerView.ViewHolder.access$902(paramViewHolder, false);
        paramViewHolder.clearReturnedFromScrapFlag();
        return;
        this.mAttachedScrap.remove(paramViewHolder);
      }
    }
    
    void updateViewCacheSize()
    {
      if (RecyclerView.this.mLayout != null) {}
      for (int i = RecyclerView.this.mLayout.mPrefetchMaxCountObserved;; i = 0)
      {
        this.mViewCacheMax = (i + this.mRequestedCacheMax);
        for (int j = -1 + this.mCachedViews.size(); (j >= 0) && (this.mCachedViews.size() > this.mViewCacheMax); j--) {
          recycleCachedViewAt(j);
        }
      }
    }
    
    boolean validateViewHolderForOffsetPosition(RecyclerView.ViewHolder paramViewHolder)
    {
      boolean bool = true;
      if (paramViewHolder.isRemoved()) {
        bool = RecyclerView.this.mState.isPreLayout();
      }
      do
      {
        return bool;
        if ((paramViewHolder.mPosition < 0) || (paramViewHolder.mPosition >= RecyclerView.this.mAdapter.getItemCount())) {
          throw new IndexOutOfBoundsException("Inconsistency detected. Invalid view holder adapter position" + paramViewHolder);
        }
        if ((!RecyclerView.this.mState.isPreLayout()) && (RecyclerView.this.mAdapter.getItemViewType(paramViewHolder.mPosition) != paramViewHolder.getItemViewType())) {
          return false;
        }
      } while ((!RecyclerView.this.mAdapter.hasStableIds()) || (paramViewHolder.getItemId() == RecyclerView.this.mAdapter.getItemId(paramViewHolder.mPosition)));
      return false;
    }
    
    void viewRangeUpdate(int paramInt1, int paramInt2)
    {
      int i = paramInt1 + paramInt2;
      int j = -1 + this.mCachedViews.size();
      if (j >= 0)
      {
        RecyclerView.ViewHolder localViewHolder = (RecyclerView.ViewHolder)this.mCachedViews.get(j);
        if (localViewHolder == null) {}
        for (;;)
        {
          j--;
          break;
          int k = localViewHolder.mPosition;
          if ((k >= paramInt1) && (k < i))
          {
            localViewHolder.addFlags(2);
            recycleCachedViewAt(j);
          }
        }
      }
    }
  }
  
  public static abstract interface RecyclerListener
  {
    public abstract void onViewRecycled(RecyclerView.ViewHolder paramViewHolder);
  }
  
  private class RecyclerViewDataObserver
    extends RecyclerView.AdapterDataObserver
  {
    RecyclerViewDataObserver() {}
    
    public void onChanged()
    {
      RecyclerView.this.assertNotInLayoutOrScroll(null);
      RecyclerView.this.mState.mStructureChanged = true;
      RecyclerView.this.setDataSetChangedAfterLayout();
      if (!RecyclerView.this.mAdapterHelper.hasPendingUpdates()) {
        RecyclerView.this.requestLayout();
      }
    }
    
    public void onItemRangeChanged(int paramInt1, int paramInt2, Object paramObject)
    {
      RecyclerView.this.assertNotInLayoutOrScroll(null);
      if (RecyclerView.this.mAdapterHelper.onItemRangeChanged(paramInt1, paramInt2, paramObject)) {
        triggerUpdateProcessor();
      }
    }
    
    public void onItemRangeInserted(int paramInt1, int paramInt2)
    {
      RecyclerView.this.assertNotInLayoutOrScroll(null);
      if (RecyclerView.this.mAdapterHelper.onItemRangeInserted(paramInt1, paramInt2)) {
        triggerUpdateProcessor();
      }
    }
    
    public void onItemRangeMoved(int paramInt1, int paramInt2, int paramInt3)
    {
      RecyclerView.this.assertNotInLayoutOrScroll(null);
      if (RecyclerView.this.mAdapterHelper.onItemRangeMoved(paramInt1, paramInt2, paramInt3)) {
        triggerUpdateProcessor();
      }
    }
    
    public void onItemRangeRemoved(int paramInt1, int paramInt2)
    {
      RecyclerView.this.assertNotInLayoutOrScroll(null);
      if (RecyclerView.this.mAdapterHelper.onItemRangeRemoved(paramInt1, paramInt2)) {
        triggerUpdateProcessor();
      }
    }
    
    void triggerUpdateProcessor()
    {
      if ((RecyclerView.POST_UPDATES_ON_ANIMATION) && (RecyclerView.this.mHasFixedSize) && (RecyclerView.this.mIsAttached))
      {
        ViewCompat.postOnAnimation(RecyclerView.this, RecyclerView.this.mUpdateChildViewsRunnable);
        return;
      }
      RecyclerView.this.mAdapterUpdateDuringMeasure = true;
      RecyclerView.this.requestLayout();
    }
  }
  
  @RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
  public static class SavedState
    extends AbsSavedState
  {
    public static final Parcelable.Creator<SavedState> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks()
    {
      public RecyclerView.SavedState createFromParcel(Parcel paramAnonymousParcel, ClassLoader paramAnonymousClassLoader)
      {
        return new RecyclerView.SavedState(paramAnonymousParcel, paramAnonymousClassLoader);
      }
      
      public RecyclerView.SavedState[] newArray(int paramAnonymousInt)
      {
        return new RecyclerView.SavedState[paramAnonymousInt];
      }
    });
    Parcelable mLayoutState;
    
    SavedState(Parcel paramParcel, ClassLoader paramClassLoader)
    {
      super(paramClassLoader);
      if (paramClassLoader != null) {}
      for (;;)
      {
        this.mLayoutState = paramParcel.readParcelable(paramClassLoader);
        return;
        paramClassLoader = RecyclerView.LayoutManager.class.getClassLoader();
      }
    }
    
    SavedState(Parcelable paramParcelable)
    {
      super();
    }
    
    void copyFrom(SavedState paramSavedState)
    {
      this.mLayoutState = paramSavedState.mLayoutState;
    }
    
    public void writeToParcel(Parcel paramParcel, int paramInt)
    {
      super.writeToParcel(paramParcel, paramInt);
      paramParcel.writeParcelable(this.mLayoutState, 0);
    }
  }
  
  public static class SimpleOnItemTouchListener
    implements RecyclerView.OnItemTouchListener
  {
    public SimpleOnItemTouchListener() {}
    
    public boolean onInterceptTouchEvent(RecyclerView paramRecyclerView, MotionEvent paramMotionEvent)
    {
      return false;
    }
    
    public void onRequestDisallowInterceptTouchEvent(boolean paramBoolean) {}
    
    public void onTouchEvent(RecyclerView paramRecyclerView, MotionEvent paramMotionEvent) {}
  }
  
  public static abstract class SmoothScroller
  {
    private RecyclerView.LayoutManager mLayoutManager;
    private boolean mPendingInitialRun;
    private RecyclerView mRecyclerView;
    private final Action mRecyclingAction = new Action(0, 0);
    private boolean mRunning;
    private int mTargetPosition = -1;
    private View mTargetView;
    
    public SmoothScroller() {}
    
    private void onAnimation(int paramInt1, int paramInt2)
    {
      RecyclerView localRecyclerView = this.mRecyclerView;
      if ((!this.mRunning) || (this.mTargetPosition == -1) || (localRecyclerView == null)) {
        stop();
      }
      this.mPendingInitialRun = false;
      if (this.mTargetView != null)
      {
        if (getChildPosition(this.mTargetView) != this.mTargetPosition) {
          break label146;
        }
        onTargetFound(this.mTargetView, localRecyclerView.mState, this.mRecyclingAction);
        this.mRecyclingAction.runIfNecessary(localRecyclerView);
        stop();
      }
      for (;;)
      {
        if (this.mRunning)
        {
          onSeekTargetStep(paramInt1, paramInt2, localRecyclerView.mState, this.mRecyclingAction);
          boolean bool = this.mRecyclingAction.hasJumpTarget();
          this.mRecyclingAction.runIfNecessary(localRecyclerView);
          if (bool)
          {
            if (!this.mRunning) {
              break;
            }
            this.mPendingInitialRun = true;
            localRecyclerView.mViewFlinger.postOnAnimation();
          }
        }
        return;
        label146:
        Log.e("RecyclerView", "Passed over target position while smooth scrolling.");
        this.mTargetView = null;
      }
      stop();
    }
    
    public View findViewByPosition(int paramInt)
    {
      return this.mRecyclerView.mLayout.findViewByPosition(paramInt);
    }
    
    public int getChildCount()
    {
      return this.mRecyclerView.mLayout.getChildCount();
    }
    
    public int getChildPosition(View paramView)
    {
      return this.mRecyclerView.getChildLayoutPosition(paramView);
    }
    
    @Nullable
    public RecyclerView.LayoutManager getLayoutManager()
    {
      return this.mLayoutManager;
    }
    
    public int getTargetPosition()
    {
      return this.mTargetPosition;
    }
    
    @Deprecated
    public void instantScrollToPosition(int paramInt)
    {
      this.mRecyclerView.scrollToPosition(paramInt);
    }
    
    public boolean isPendingInitialRun()
    {
      return this.mPendingInitialRun;
    }
    
    public boolean isRunning()
    {
      return this.mRunning;
    }
    
    protected void normalize(PointF paramPointF)
    {
      double d = Math.sqrt(paramPointF.x * paramPointF.x + paramPointF.y * paramPointF.y);
      paramPointF.x = ((float)(paramPointF.x / d));
      paramPointF.y = ((float)(paramPointF.y / d));
    }
    
    protected void onChildAttachedToWindow(View paramView)
    {
      if (getChildPosition(paramView) == getTargetPosition()) {
        this.mTargetView = paramView;
      }
    }
    
    protected abstract void onSeekTargetStep(int paramInt1, int paramInt2, RecyclerView.State paramState, Action paramAction);
    
    protected abstract void onStart();
    
    protected abstract void onStop();
    
    protected abstract void onTargetFound(View paramView, RecyclerView.State paramState, Action paramAction);
    
    public void setTargetPosition(int paramInt)
    {
      this.mTargetPosition = paramInt;
    }
    
    void start(RecyclerView paramRecyclerView, RecyclerView.LayoutManager paramLayoutManager)
    {
      this.mRecyclerView = paramRecyclerView;
      this.mLayoutManager = paramLayoutManager;
      if (this.mTargetPosition == -1) {
        throw new IllegalArgumentException("Invalid target position");
      }
      RecyclerView.State.access$1102(this.mRecyclerView.mState, this.mTargetPosition);
      this.mRunning = true;
      this.mPendingInitialRun = true;
      this.mTargetView = findViewByPosition(getTargetPosition());
      onStart();
      this.mRecyclerView.mViewFlinger.postOnAnimation();
    }
    
    protected final void stop()
    {
      if (!this.mRunning) {
        return;
      }
      onStop();
      RecyclerView.State.access$1102(this.mRecyclerView.mState, -1);
      this.mTargetView = null;
      this.mTargetPosition = -1;
      this.mPendingInitialRun = false;
      this.mRunning = false;
      this.mLayoutManager.onSmoothScrollerStopped(this);
      this.mLayoutManager = null;
      this.mRecyclerView = null;
    }
    
    public static class Action
    {
      public static final int UNDEFINED_DURATION = Integer.MIN_VALUE;
      private boolean changed = false;
      private int consecutiveUpdates = 0;
      private int mDuration;
      private int mDx;
      private int mDy;
      private Interpolator mInterpolator;
      private int mJumpToPosition = -1;
      
      public Action(int paramInt1, int paramInt2)
      {
        this(paramInt1, paramInt2, Integer.MIN_VALUE, null);
      }
      
      public Action(int paramInt1, int paramInt2, int paramInt3)
      {
        this(paramInt1, paramInt2, paramInt3, null);
      }
      
      public Action(int paramInt1, int paramInt2, int paramInt3, Interpolator paramInterpolator)
      {
        this.mDx = paramInt1;
        this.mDy = paramInt2;
        this.mDuration = paramInt3;
        this.mInterpolator = paramInterpolator;
      }
      
      private void validate()
      {
        if ((this.mInterpolator != null) && (this.mDuration < 1)) {
          throw new IllegalStateException("If you provide an interpolator, you must set a positive duration");
        }
        if (this.mDuration < 1) {
          throw new IllegalStateException("Scroll duration must be a positive number");
        }
      }
      
      public int getDuration()
      {
        return this.mDuration;
      }
      
      public int getDx()
      {
        return this.mDx;
      }
      
      public int getDy()
      {
        return this.mDy;
      }
      
      public Interpolator getInterpolator()
      {
        return this.mInterpolator;
      }
      
      boolean hasJumpTarget()
      {
        return this.mJumpToPosition >= 0;
      }
      
      public void jumpTo(int paramInt)
      {
        this.mJumpToPosition = paramInt;
      }
      
      void runIfNecessary(RecyclerView paramRecyclerView)
      {
        if (this.mJumpToPosition >= 0)
        {
          int i = this.mJumpToPosition;
          this.mJumpToPosition = -1;
          paramRecyclerView.jumpToPositionForSmoothScroller(i);
          this.changed = false;
          return;
        }
        if (this.changed)
        {
          validate();
          if (this.mInterpolator == null) {
            if (this.mDuration == Integer.MIN_VALUE) {
              paramRecyclerView.mViewFlinger.smoothScrollBy(this.mDx, this.mDy);
            }
          }
          for (;;)
          {
            this.consecutiveUpdates = (1 + this.consecutiveUpdates);
            if (this.consecutiveUpdates > 10) {
              Log.e("RecyclerView", "Smooth Scroll action is being updated too frequently. Make sure you are not changing it unless necessary");
            }
            this.changed = false;
            return;
            paramRecyclerView.mViewFlinger.smoothScrollBy(this.mDx, this.mDy, this.mDuration);
            continue;
            paramRecyclerView.mViewFlinger.smoothScrollBy(this.mDx, this.mDy, this.mDuration, this.mInterpolator);
          }
        }
        this.consecutiveUpdates = 0;
      }
      
      public void setDuration(int paramInt)
      {
        this.changed = true;
        this.mDuration = paramInt;
      }
      
      public void setDx(int paramInt)
      {
        this.changed = true;
        this.mDx = paramInt;
      }
      
      public void setDy(int paramInt)
      {
        this.changed = true;
        this.mDy = paramInt;
      }
      
      public void setInterpolator(Interpolator paramInterpolator)
      {
        this.changed = true;
        this.mInterpolator = paramInterpolator;
      }
      
      public void update(int paramInt1, int paramInt2, int paramInt3, Interpolator paramInterpolator)
      {
        this.mDx = paramInt1;
        this.mDy = paramInt2;
        this.mDuration = paramInt3;
        this.mInterpolator = paramInterpolator;
        this.changed = true;
      }
    }
    
    public static abstract interface ScrollVectorProvider
    {
      public abstract PointF computeScrollVectorForPosition(int paramInt);
    }
  }
  
  public static class State
  {
    static final int STEP_ANIMATIONS = 4;
    static final int STEP_LAYOUT = 2;
    static final int STEP_START = 1;
    private SparseArray<Object> mData;
    int mDeletedInvisibleItemCountSincePreviousLayout = 0;
    long mFocusedItemId;
    int mFocusedItemPosition;
    int mFocusedSubChildId;
    boolean mInPreLayout = false;
    boolean mIsMeasuring = false;
    int mItemCount = 0;
    int mLayoutStep = 1;
    int mPreviousLayoutItemCount = 0;
    boolean mRunPredictiveAnimations = false;
    boolean mRunSimpleAnimations = false;
    boolean mStructureChanged = false;
    private int mTargetPosition = -1;
    boolean mTrackOldChangeHolders = false;
    
    public State() {}
    
    void assertLayoutStep(int paramInt)
    {
      if ((paramInt & this.mLayoutStep) == 0) {
        throw new IllegalStateException("Layout state should be one of " + Integer.toBinaryString(paramInt) + " but it is " + Integer.toBinaryString(this.mLayoutStep));
      }
    }
    
    public boolean didStructureChange()
    {
      return this.mStructureChanged;
    }
    
    public <T> T get(int paramInt)
    {
      if (this.mData == null) {
        return null;
      }
      return this.mData.get(paramInt);
    }
    
    public int getItemCount()
    {
      if (this.mInPreLayout) {
        return this.mPreviousLayoutItemCount - this.mDeletedInvisibleItemCountSincePreviousLayout;
      }
      return this.mItemCount;
    }
    
    public int getTargetScrollPosition()
    {
      return this.mTargetPosition;
    }
    
    public boolean hasTargetScrollPosition()
    {
      return this.mTargetPosition != -1;
    }
    
    public boolean isMeasuring()
    {
      return this.mIsMeasuring;
    }
    
    public boolean isPreLayout()
    {
      return this.mInPreLayout;
    }
    
    void prepareForNestedPrefetch(RecyclerView.Adapter paramAdapter)
    {
      this.mLayoutStep = 1;
      this.mItemCount = paramAdapter.getItemCount();
      this.mStructureChanged = false;
      this.mInPreLayout = false;
      this.mTrackOldChangeHolders = false;
      this.mIsMeasuring = false;
    }
    
    public void put(int paramInt, Object paramObject)
    {
      if (this.mData == null) {
        this.mData = new SparseArray();
      }
      this.mData.put(paramInt, paramObject);
    }
    
    public void remove(int paramInt)
    {
      if (this.mData == null) {
        return;
      }
      this.mData.remove(paramInt);
    }
    
    State reset()
    {
      this.mTargetPosition = -1;
      if (this.mData != null) {
        this.mData.clear();
      }
      this.mItemCount = 0;
      this.mStructureChanged = false;
      this.mIsMeasuring = false;
      return this;
    }
    
    public String toString()
    {
      return "State{mTargetPosition=" + this.mTargetPosition + ", mData=" + this.mData + ", mItemCount=" + this.mItemCount + ", mPreviousLayoutItemCount=" + this.mPreviousLayoutItemCount + ", mDeletedInvisibleItemCountSincePreviousLayout=" + this.mDeletedInvisibleItemCountSincePreviousLayout + ", mStructureChanged=" + this.mStructureChanged + ", mInPreLayout=" + this.mInPreLayout + ", mRunSimpleAnimations=" + this.mRunSimpleAnimations + ", mRunPredictiveAnimations=" + this.mRunPredictiveAnimations + '}';
    }
    
    public boolean willRunPredictiveAnimations()
    {
      return this.mRunPredictiveAnimations;
    }
    
    public boolean willRunSimpleAnimations()
    {
      return this.mRunSimpleAnimations;
    }
  }
  
  public static abstract class ViewCacheExtension
  {
    public ViewCacheExtension() {}
    
    public abstract View getViewForPositionAndType(RecyclerView.Recycler paramRecycler, int paramInt1, int paramInt2);
  }
  
  class ViewFlinger
    implements Runnable
  {
    private boolean mEatRunOnAnimationRequest = false;
    Interpolator mInterpolator = RecyclerView.sQuinticInterpolator;
    private int mLastFlingX;
    private int mLastFlingY;
    private boolean mReSchedulePostAnimationCallback = false;
    private ScrollerCompat mScroller = ScrollerCompat.create(RecyclerView.this.getContext(), RecyclerView.sQuinticInterpolator);
    
    public ViewFlinger() {}
    
    private int computeScrollDuration(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      int i = Math.abs(paramInt1);
      int j = Math.abs(paramInt2);
      int k;
      int m;
      int n;
      if (i > j)
      {
        k = 1;
        m = (int)Math.sqrt(paramInt3 * paramInt3 + paramInt4 * paramInt4);
        n = (int)Math.sqrt(paramInt1 * paramInt1 + paramInt2 * paramInt2);
        if (k == 0) {
          break label142;
        }
      }
      int i3;
      label142:
      for (int i1 = RecyclerView.this.getWidth();; i1 = RecyclerView.this.getHeight())
      {
        int i2 = i1 / 2;
        float f1 = Math.min(1.0F, 1.0F * n / i1);
        float f2 = i2 + i2 * distanceInfluenceForSnapDuration(f1);
        if (m <= 0) {
          break label154;
        }
        i3 = 4 * Math.round(1000.0F * Math.abs(f2 / m));
        return Math.min(i3, 2000);
        k = 0;
        break;
      }
      label154:
      if (k != 0) {}
      for (;;)
      {
        i3 = (int)(300.0F * (1.0F + i / i1));
        break;
        i = j;
      }
    }
    
    private void disableRunOnAnimationRequests()
    {
      this.mReSchedulePostAnimationCallback = false;
      this.mEatRunOnAnimationRequest = true;
    }
    
    private float distanceInfluenceForSnapDuration(float paramFloat)
    {
      return (float)Math.sin((float)(0.4712389167638204D * (paramFloat - 0.5F)));
    }
    
    private void enableRunOnAnimationRequests()
    {
      this.mEatRunOnAnimationRequest = false;
      if (this.mReSchedulePostAnimationCallback) {
        postOnAnimation();
      }
    }
    
    public void fling(int paramInt1, int paramInt2)
    {
      RecyclerView.this.setScrollState(2);
      this.mLastFlingY = 0;
      this.mLastFlingX = 0;
      this.mScroller.fling(0, 0, paramInt1, paramInt2, Integer.MIN_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MAX_VALUE);
      postOnAnimation();
    }
    
    void postOnAnimation()
    {
      if (this.mEatRunOnAnimationRequest)
      {
        this.mReSchedulePostAnimationCallback = true;
        return;
      }
      RecyclerView.this.removeCallbacks(this);
      ViewCompat.postOnAnimation(RecyclerView.this, this);
    }
    
    public void run()
    {
      if (RecyclerView.this.mLayout == null)
      {
        stop();
        return;
      }
      disableRunOnAnimationRequests();
      RecyclerView.this.consumePendingUpdateOperations();
      ScrollerCompat localScrollerCompat = this.mScroller;
      RecyclerView.SmoothScroller localSmoothScroller = RecyclerView.this.mLayout.mSmoothScroller;
      int k;
      int m;
      int i1;
      int i2;
      int i10;
      int i4;
      int i5;
      label367:
      int i6;
      label387:
      int i7;
      label516:
      int i8;
      label544:
      int i9;
      if (localScrollerCompat.computeScrollOffset())
      {
        int i = localScrollerCompat.getCurrX();
        int j = localScrollerCompat.getCurrY();
        k = i - this.mLastFlingX;
        m = j - this.mLastFlingY;
        this.mLastFlingX = i;
        this.mLastFlingY = j;
        RecyclerView.Adapter localAdapter = RecyclerView.this.mAdapter;
        int n = 0;
        i1 = 0;
        i2 = 0;
        int i3 = 0;
        if (localAdapter != null)
        {
          RecyclerView.this.eatRequestLayout();
          RecyclerView.this.onEnterLayoutOrScroll();
          TraceCompat.beginSection("RV Scroll");
          n = 0;
          i1 = 0;
          if (k != 0)
          {
            n = RecyclerView.this.mLayout.scrollHorizontallyBy(k, RecyclerView.this.mRecycler, RecyclerView.this.mState);
            i1 = k - n;
          }
          i2 = 0;
          i3 = 0;
          if (m != 0)
          {
            i3 = RecyclerView.this.mLayout.scrollVerticallyBy(m, RecyclerView.this.mRecycler, RecyclerView.this.mState);
            i2 = m - i3;
          }
          TraceCompat.endSection();
          RecyclerView.this.repositionShadowingViews();
          RecyclerView.this.onExitLayoutOrScroll();
          RecyclerView.this.resumeRequestLayout(false);
          if ((localSmoothScroller != null) && (!localSmoothScroller.isPendingInitialRun()) && (localSmoothScroller.isRunning()))
          {
            i10 = RecyclerView.this.mState.getItemCount();
            if (i10 != 0) {
              break label636;
            }
            localSmoothScroller.stop();
          }
        }
        if (!RecyclerView.this.mItemDecorations.isEmpty()) {
          RecyclerView.this.invalidate();
        }
        if (RecyclerView.this.getOverScrollMode() != 2) {
          RecyclerView.this.considerReleasingGlowsOnScroll(k, m);
        }
        if ((i1 != 0) || (i2 != 0))
        {
          i4 = (int)localScrollerCompat.getCurrVelocity();
          i5 = 0;
          if (i1 != i)
          {
            if (i1 >= 0) {
              break label687;
            }
            i5 = -i4;
          }
          i6 = 0;
          if (i2 != j)
          {
            if (i2 >= 0) {
              break label705;
            }
            i6 = -i4;
          }
          if (RecyclerView.this.getOverScrollMode() != 2) {
            RecyclerView.this.absorbGlows(i5, i6);
          }
          if (((i5 != 0) || (i1 == i) || (localScrollerCompat.getFinalX() == 0)) && ((i6 != 0) || (i2 == j) || (localScrollerCompat.getFinalY() == 0))) {
            localScrollerCompat.abortAnimation();
          }
        }
        if ((n != 0) || (i3 != 0)) {
          RecyclerView.this.dispatchOnScrolled(n, i3);
        }
        if (!RecyclerView.this.awakenScrollBars()) {
          RecyclerView.this.invalidate();
        }
        if ((m == 0) || (!RecyclerView.this.mLayout.canScrollVertically()) || (i3 != m)) {
          break label723;
        }
        i7 = 1;
        if ((k == 0) || (!RecyclerView.this.mLayout.canScrollHorizontally()) || (n != k)) {
          break label729;
        }
        i8 = 1;
        if (((k != 0) || (m != 0)) && (i8 == 0) && (i7 == 0)) {
          break label735;
        }
        i9 = 1;
        label567:
        if ((!localScrollerCompat.isFinished()) && (i9 != 0)) {
          break label741;
        }
        RecyclerView.this.setScrollState(0);
        if (RecyclerView.ALLOW_THREAD_GAP_WORK) {
          RecyclerView.this.mPrefetchRegistry.clearPrefetchPositions();
        }
      }
      for (;;)
      {
        if (localSmoothScroller != null)
        {
          if (localSmoothScroller.isPendingInitialRun()) {
            localSmoothScroller.onAnimation(0, 0);
          }
          if (!this.mReSchedulePostAnimationCallback) {
            localSmoothScroller.stop();
          }
        }
        enableRunOnAnimationRequests();
        return;
        label636:
        if (localSmoothScroller.getTargetPosition() >= i10)
        {
          localSmoothScroller.setTargetPosition(i10 - 1);
          localSmoothScroller.onAnimation(k - i1, m - i2);
          break;
        }
        localSmoothScroller.onAnimation(k - i1, m - i2);
        break;
        label687:
        if (i1 > 0)
        {
          i5 = i4;
          break label367;
        }
        i5 = 0;
        break label367;
        label705:
        if (i2 > 0)
        {
          i6 = i4;
          break label387;
        }
        i6 = 0;
        break label387;
        label723:
        i7 = 0;
        break label516;
        label729:
        i8 = 0;
        break label544;
        label735:
        i9 = 0;
        break label567;
        label741:
        postOnAnimation();
        if (RecyclerView.this.mGapWorker != null) {
          RecyclerView.this.mGapWorker.postFromTraversal(RecyclerView.this, k, m);
        }
      }
    }
    
    public void smoothScrollBy(int paramInt1, int paramInt2)
    {
      smoothScrollBy(paramInt1, paramInt2, 0, 0);
    }
    
    public void smoothScrollBy(int paramInt1, int paramInt2, int paramInt3)
    {
      smoothScrollBy(paramInt1, paramInt2, paramInt3, RecyclerView.sQuinticInterpolator);
    }
    
    public void smoothScrollBy(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    {
      smoothScrollBy(paramInt1, paramInt2, computeScrollDuration(paramInt1, paramInt2, paramInt3, paramInt4));
    }
    
    public void smoothScrollBy(int paramInt1, int paramInt2, int paramInt3, Interpolator paramInterpolator)
    {
      if (this.mInterpolator != paramInterpolator)
      {
        this.mInterpolator = paramInterpolator;
        this.mScroller = ScrollerCompat.create(RecyclerView.this.getContext(), paramInterpolator);
      }
      RecyclerView.this.setScrollState(2);
      this.mLastFlingY = 0;
      this.mLastFlingX = 0;
      this.mScroller.startScroll(0, 0, paramInt1, paramInt2, paramInt3);
      postOnAnimation();
    }
    
    public void smoothScrollBy(int paramInt1, int paramInt2, Interpolator paramInterpolator)
    {
      int i = computeScrollDuration(paramInt1, paramInt2, 0, 0);
      if (paramInterpolator == null) {
        paramInterpolator = RecyclerView.sQuinticInterpolator;
      }
      smoothScrollBy(paramInt1, paramInt2, i, paramInterpolator);
    }
    
    public void stop()
    {
      RecyclerView.this.removeCallbacks(this);
      this.mScroller.abortAnimation();
    }
  }
  
  public static abstract class ViewHolder
  {
    static final int FLAG_ADAPTER_FULLUPDATE = 1024;
    static final int FLAG_ADAPTER_POSITION_UNKNOWN = 512;
    static final int FLAG_APPEARED_IN_PRE_LAYOUT = 4096;
    static final int FLAG_BOUNCED_FROM_HIDDEN_LIST = 8192;
    static final int FLAG_BOUND = 1;
    static final int FLAG_IGNORE = 128;
    static final int FLAG_INVALID = 4;
    static final int FLAG_MOVED = 2048;
    static final int FLAG_NOT_RECYCLABLE = 16;
    static final int FLAG_REMOVED = 8;
    static final int FLAG_RETURNED_FROM_SCRAP = 32;
    static final int FLAG_TMP_DETACHED = 256;
    static final int FLAG_UPDATE = 2;
    private static final List<Object> FULLUPDATE_PAYLOADS = Collections.EMPTY_LIST;
    static final int PENDING_ACCESSIBILITY_STATE_NOT_SET = -1;
    public final View itemView;
    private int mFlags;
    private boolean mInChangeScrap = false;
    private int mIsRecyclableCount = 0;
    long mItemId = -1L;
    int mItemViewType = -1;
    WeakReference<RecyclerView> mNestedRecyclerView;
    int mOldPosition = -1;
    RecyclerView mOwnerRecyclerView;
    List<Object> mPayloads = null;
    @VisibleForTesting
    int mPendingAccessibilityState = -1;
    int mPosition = -1;
    int mPreLayoutPosition = -1;
    private RecyclerView.Recycler mScrapContainer = null;
    ViewHolder mShadowedHolder = null;
    ViewHolder mShadowingHolder = null;
    List<Object> mUnmodifiedPayloads = null;
    private int mWasImportantForAccessibilityBeforeHidden = 0;
    
    public ViewHolder(View paramView)
    {
      if (paramView == null) {
        throw new IllegalArgumentException("itemView may not be null");
      }
      this.itemView = paramView;
    }
    
    private void createPayloadsIfNeeded()
    {
      if (this.mPayloads == null)
      {
        this.mPayloads = new ArrayList();
        this.mUnmodifiedPayloads = Collections.unmodifiableList(this.mPayloads);
      }
    }
    
    private boolean doesTransientStatePreventRecycling()
    {
      return ((0x10 & this.mFlags) == 0) && (ViewCompat.hasTransientState(this.itemView));
    }
    
    private void onEnteredHiddenState(RecyclerView paramRecyclerView)
    {
      this.mWasImportantForAccessibilityBeforeHidden = ViewCompat.getImportantForAccessibility(this.itemView);
      paramRecyclerView.setChildImportantForAccessibilityInternal(this, 4);
    }
    
    private void onLeftHiddenState(RecyclerView paramRecyclerView)
    {
      paramRecyclerView.setChildImportantForAccessibilityInternal(this, this.mWasImportantForAccessibilityBeforeHidden);
      this.mWasImportantForAccessibilityBeforeHidden = 0;
    }
    
    private boolean shouldBeKeptAsChild()
    {
      return (0x10 & this.mFlags) != 0;
    }
    
    void addChangePayload(Object paramObject)
    {
      if (paramObject == null) {
        addFlags(1024);
      }
      while ((0x400 & this.mFlags) != 0) {
        return;
      }
      createPayloadsIfNeeded();
      this.mPayloads.add(paramObject);
    }
    
    void addFlags(int paramInt)
    {
      this.mFlags = (paramInt | this.mFlags);
    }
    
    void clearOldPosition()
    {
      this.mOldPosition = -1;
      this.mPreLayoutPosition = -1;
    }
    
    void clearPayload()
    {
      if (this.mPayloads != null) {
        this.mPayloads.clear();
      }
      this.mFlags = (0xFBFF & this.mFlags);
    }
    
    void clearReturnedFromScrapFlag()
    {
      this.mFlags = (0xFFFFFFDF & this.mFlags);
    }
    
    void clearTmpDetachFlag()
    {
      this.mFlags = (0xFEFF & this.mFlags);
    }
    
    void flagRemovedAndOffsetPosition(int paramInt1, int paramInt2, boolean paramBoolean)
    {
      addFlags(8);
      offsetPosition(paramInt2, paramBoolean);
      this.mPosition = paramInt1;
    }
    
    public final int getAdapterPosition()
    {
      if (this.mOwnerRecyclerView == null) {
        return -1;
      }
      return this.mOwnerRecyclerView.getAdapterPositionFor(this);
    }
    
    public final long getItemId()
    {
      return this.mItemId;
    }
    
    public final int getItemViewType()
    {
      return this.mItemViewType;
    }
    
    public final int getLayoutPosition()
    {
      if (this.mPreLayoutPosition == -1) {
        return this.mPosition;
      }
      return this.mPreLayoutPosition;
    }
    
    public final int getOldPosition()
    {
      return this.mOldPosition;
    }
    
    @Deprecated
    public final int getPosition()
    {
      if (this.mPreLayoutPosition == -1) {
        return this.mPosition;
      }
      return this.mPreLayoutPosition;
    }
    
    List<Object> getUnmodifiedPayloads()
    {
      if ((0x400 & this.mFlags) == 0)
      {
        if ((this.mPayloads == null) || (this.mPayloads.size() == 0)) {
          return FULLUPDATE_PAYLOADS;
        }
        return this.mUnmodifiedPayloads;
      }
      return FULLUPDATE_PAYLOADS;
    }
    
    boolean hasAnyOfTheFlags(int paramInt)
    {
      return (paramInt & this.mFlags) != 0;
    }
    
    boolean isAdapterPositionUnknown()
    {
      return ((0x200 & this.mFlags) != 0) || (isInvalid());
    }
    
    boolean isBound()
    {
      return (0x1 & this.mFlags) != 0;
    }
    
    boolean isInvalid()
    {
      return (0x4 & this.mFlags) != 0;
    }
    
    public final boolean isRecyclable()
    {
      return ((0x10 & this.mFlags) == 0) && (!ViewCompat.hasTransientState(this.itemView));
    }
    
    boolean isRemoved()
    {
      return (0x8 & this.mFlags) != 0;
    }
    
    boolean isScrap()
    {
      return this.mScrapContainer != null;
    }
    
    boolean isTmpDetached()
    {
      return (0x100 & this.mFlags) != 0;
    }
    
    boolean isUpdated()
    {
      return (0x2 & this.mFlags) != 0;
    }
    
    boolean needsUpdate()
    {
      return (0x2 & this.mFlags) != 0;
    }
    
    void offsetPosition(int paramInt, boolean paramBoolean)
    {
      if (this.mOldPosition == -1) {
        this.mOldPosition = this.mPosition;
      }
      if (this.mPreLayoutPosition == -1) {
        this.mPreLayoutPosition = this.mPosition;
      }
      if (paramBoolean) {
        this.mPreLayoutPosition = (paramInt + this.mPreLayoutPosition);
      }
      this.mPosition = (paramInt + this.mPosition);
      if (this.itemView.getLayoutParams() != null) {
        ((RecyclerView.LayoutParams)this.itemView.getLayoutParams()).mInsetsDirty = true;
      }
    }
    
    void resetInternal()
    {
      this.mFlags = 0;
      this.mPosition = -1;
      this.mOldPosition = -1;
      this.mItemId = -1L;
      this.mPreLayoutPosition = -1;
      this.mIsRecyclableCount = 0;
      this.mShadowedHolder = null;
      this.mShadowingHolder = null;
      clearPayload();
      this.mWasImportantForAccessibilityBeforeHidden = 0;
      this.mPendingAccessibilityState = -1;
      RecyclerView.clearNestedRecyclerViewIfNotNested(this);
    }
    
    void saveOldPosition()
    {
      if (this.mOldPosition == -1) {
        this.mOldPosition = this.mPosition;
      }
    }
    
    void setFlags(int paramInt1, int paramInt2)
    {
      this.mFlags = (this.mFlags & (paramInt2 ^ 0xFFFFFFFF) | paramInt1 & paramInt2);
    }
    
    public final void setIsRecyclable(boolean paramBoolean)
    {
      int i;
      if (paramBoolean)
      {
        i = -1 + this.mIsRecyclableCount;
        this.mIsRecyclableCount = i;
        if (this.mIsRecyclableCount >= 0) {
          break label65;
        }
        this.mIsRecyclableCount = 0;
        Log.e("View", "isRecyclable decremented below 0: unmatched pair of setIsRecyable() calls for " + this);
      }
      label65:
      do
      {
        return;
        i = 1 + this.mIsRecyclableCount;
        break;
        if ((!paramBoolean) && (this.mIsRecyclableCount == 1))
        {
          this.mFlags = (0x10 | this.mFlags);
          return;
        }
      } while ((!paramBoolean) || (this.mIsRecyclableCount != 0));
      this.mFlags = (0xFFFFFFEF & this.mFlags);
    }
    
    void setScrapContainer(RecyclerView.Recycler paramRecycler, boolean paramBoolean)
    {
      this.mScrapContainer = paramRecycler;
      this.mInChangeScrap = paramBoolean;
    }
    
    boolean shouldIgnore()
    {
      return (0x80 & this.mFlags) != 0;
    }
    
    void stopIgnoring()
    {
      this.mFlags = (0xFF7F & this.mFlags);
    }
    
    public String toString()
    {
      StringBuilder localStringBuilder1 = new StringBuilder("ViewHolder{" + Integer.toHexString(hashCode()) + " position=" + this.mPosition + " id=" + this.mItemId + ", oldPos=" + this.mOldPosition + ", pLpos:" + this.mPreLayoutPosition);
      StringBuilder localStringBuilder2;
      if (isScrap())
      {
        localStringBuilder2 = localStringBuilder1.append(" scrap ");
        if (!this.mInChangeScrap) {
          break label299;
        }
      }
      label299:
      for (String str = "[changeScrap]";; str = "[attachedScrap]")
      {
        localStringBuilder2.append(str);
        if (isInvalid()) {
          localStringBuilder1.append(" invalid");
        }
        if (!isBound()) {
          localStringBuilder1.append(" unbound");
        }
        if (needsUpdate()) {
          localStringBuilder1.append(" update");
        }
        if (isRemoved()) {
          localStringBuilder1.append(" removed");
        }
        if (shouldIgnore()) {
          localStringBuilder1.append(" ignored");
        }
        if (isTmpDetached()) {
          localStringBuilder1.append(" tmpDetached");
        }
        if (!isRecyclable()) {
          localStringBuilder1.append(" not recyclable(" + this.mIsRecyclableCount + ")");
        }
        if (isAdapterPositionUnknown()) {
          localStringBuilder1.append(" undefined adapter position");
        }
        if (this.itemView.getParent() == null) {
          localStringBuilder1.append(" no parent");
        }
        localStringBuilder1.append("}");
        return localStringBuilder1.toString();
      }
    }
    
    void unScrap()
    {
      this.mScrapContainer.unscrapView(this);
    }
    
    boolean wasReturnedFromScrap()
    {
      return (0x20 & this.mFlags) != 0;
    }
  }
}
