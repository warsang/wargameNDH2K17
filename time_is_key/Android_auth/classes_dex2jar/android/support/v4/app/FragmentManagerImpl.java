// 
// Decompiled by Procyon v0.5.30
// 

package android.support.v4.app;

import android.support.annotation.CallSuper;
import android.support.v4.os.BuildCompat;
import android.support.v4.util.DebugUtils;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.content.res.Resources$NotFoundException;
import android.view.animation.AnimationUtils;
import java.util.Arrays;
import java.util.Iterator;
import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.content.res.Configuration;
import java.io.FileDescriptor;
import java.io.Writer;
import java.io.PrintWriter;
import android.support.v4.util.LogWriter;
import android.util.Log;
import android.graphics.Paint;
import android.support.v4.view.ViewCompat;
import android.view.animation.Animation$AnimationListener;
import java.util.List;
import android.view.animation.ScaleAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.AlphaAnimation;
import android.content.Context;
import android.view.ViewGroup;
import java.util.Collection;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.View;
import android.support.v4.util.ArraySet;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.os.Build$VERSION;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseArray;
import android.support.v4.util.Pair;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.ArrayList;
import java.lang.reflect.Field;
import android.view.animation.Interpolator;
import android.support.v4.view.LayoutInflaterFactory;

final class FragmentManagerImpl extends FragmentManager implements LayoutInflaterFactory
{
    static final Interpolator ACCELERATE_CUBIC;
    static final Interpolator ACCELERATE_QUINT;
    static final int ANIM_DUR = 220;
    public static final int ANIM_STYLE_CLOSE_ENTER = 3;
    public static final int ANIM_STYLE_CLOSE_EXIT = 4;
    public static final int ANIM_STYLE_FADE_ENTER = 5;
    public static final int ANIM_STYLE_FADE_EXIT = 6;
    public static final int ANIM_STYLE_OPEN_ENTER = 1;
    public static final int ANIM_STYLE_OPEN_EXIT = 2;
    static boolean DEBUG = false;
    static final Interpolator DECELERATE_CUBIC;
    static final Interpolator DECELERATE_QUINT;
    static final boolean HONEYCOMB = false;
    static final String TAG = "FragmentManager";
    static final String TARGET_REQUEST_CODE_STATE_TAG = "android:target_req_state";
    static final String TARGET_STATE_TAG = "android:target_state";
    static final String USER_VISIBLE_HINT_TAG = "android:user_visible_hint";
    static final String VIEW_STATE_TAG = "android:view_state";
    static Field sAnimationListenerField;
    ArrayList<Fragment> mActive;
    ArrayList<Fragment> mAdded;
    ArrayList<Integer> mAvailBackStackIndices;
    ArrayList<Integer> mAvailIndices;
    ArrayList<BackStackRecord> mBackStack;
    ArrayList<OnBackStackChangedListener> mBackStackChangeListeners;
    ArrayList<BackStackRecord> mBackStackIndices;
    FragmentContainer mContainer;
    ArrayList<Fragment> mCreatedMenus;
    int mCurState;
    boolean mDestroyed;
    Runnable mExecCommit;
    boolean mExecutingActions;
    boolean mHavePendingDeferredStart;
    FragmentHostCallback mHost;
    private CopyOnWriteArrayList<Pair<FragmentLifecycleCallbacks, Boolean>> mLifecycleCallbacks;
    boolean mNeedMenuInvalidate;
    String mNoTransactionsBecause;
    Fragment mParent;
    ArrayList<OpGenerator> mPendingActions;
    ArrayList<StartEnterTransitionListener> mPostponedTransactions;
    SparseArray<Parcelable> mStateArray;
    Bundle mStateBundle;
    boolean mStateSaved;
    Runnable[] mTmpActions;
    ArrayList<Fragment> mTmpAddedFragments;
    ArrayList<Boolean> mTmpIsPop;
    ArrayList<BackStackRecord> mTmpRecords;
    
    static {
        FragmentManagerImpl.DEBUG = false;
        final int sdk_INT = Build$VERSION.SDK_INT;
        boolean honeycomb = false;
        if (sdk_INT >= 11) {
            honeycomb = true;
        }
        FragmentManagerImpl.sAnimationListenerField = null;
        DECELERATE_QUINT = (Interpolator)new DecelerateInterpolator(2.5f);
        DECELERATE_CUBIC = (Interpolator)new DecelerateInterpolator(1.5f);
        ACCELERATE_QUINT = (Interpolator)new AccelerateInterpolator(2.5f);
        ACCELERATE_CUBIC = (Interpolator)new AccelerateInterpolator(1.5f);
    }
    
    FragmentManagerImpl() {
        this.mCurState = 0;
        this.mStateBundle = null;
        this.mStateArray = null;
        this.mExecCommit = new Runnable() {
            @Override
            public void run() {
                FragmentManagerImpl.this.execPendingActions();
            }
        };
    }
    
    private void addAddedFragments(final ArraySet<Fragment> set) {
        if (this.mCurState >= 1) {
            final int min = Math.min(this.mCurState, 4);
            int size;
            if (this.mAdded == null) {
                size = 0;
            }
            else {
                size = this.mAdded.size();
            }
            for (int i = 0; i < size; ++i) {
                final Fragment fragment = this.mAdded.get(i);
                if (fragment.mState < min) {
                    this.moveToState(fragment, min, fragment.getNextAnim(), fragment.getNextTransition(), false);
                    if (fragment.mView != null && !fragment.mHidden && fragment.mIsNewlyAdded) {
                        set.add(fragment);
                    }
                }
            }
        }
    }
    
    private void checkStateLoss() {
        if (this.mStateSaved) {
            throw new IllegalStateException("Can not perform this action after onSaveInstanceState");
        }
        if (this.mNoTransactionsBecause != null) {
            throw new IllegalStateException("Can not perform this action inside of " + this.mNoTransactionsBecause);
        }
    }
    
    private void cleanupExec() {
        this.mExecutingActions = false;
        this.mTmpIsPop.clear();
        this.mTmpRecords.clear();
    }
    
    private void completeExecute(final BackStackRecord backStackRecord, final boolean b, final boolean b2, final boolean b3) {
        final ArrayList<BackStackRecord> list = new ArrayList<BackStackRecord>(1);
        final ArrayList<Boolean> list2 = new ArrayList<Boolean>(1);
        list.add(backStackRecord);
        list2.add(b);
        executeOps(list, list2, 0, 1);
        if (b2) {
            FragmentTransition.startTransitions(this, list, list2, 0, 1, true);
        }
        if (b3) {
            this.moveToState(this.mCurState, true);
        }
        if (this.mActive != null) {
            for (int size = this.mActive.size(), i = 0; i < size; ++i) {
                final Fragment fragment = this.mActive.get(i);
                if (fragment != null && fragment.mView != null && fragment.mIsNewlyAdded && backStackRecord.interactsWith(fragment.mContainerId)) {
                    if (Build$VERSION.SDK_INT >= 11 && fragment.mPostponedAlpha > 0.0f) {
                        fragment.mView.setAlpha(fragment.mPostponedAlpha);
                    }
                    if (b3) {
                        fragment.mPostponedAlpha = 0.0f;
                    }
                    else {
                        fragment.mPostponedAlpha = -1.0f;
                        fragment.mIsNewlyAdded = false;
                    }
                }
            }
        }
    }
    
    private void endAnimatingAwayFragments() {
        int size;
        if (this.mActive == null) {
            size = 0;
        }
        else {
            size = this.mActive.size();
        }
        for (int i = 0; i < size; ++i) {
            final Fragment fragment = this.mActive.get(i);
            if (fragment != null && fragment.getAnimatingAway() != null) {
                final int stateAfterAnimating = fragment.getStateAfterAnimating();
                final View animatingAway = fragment.getAnimatingAway();
                fragment.setAnimatingAway(null);
                final Animation animation = animatingAway.getAnimation();
                if (animation != null) {
                    animation.cancel();
                }
                this.moveToState(fragment, stateAfterAnimating, 0, 0, false);
            }
        }
    }
    
    private void ensureExecReady(final boolean b) {
        if (this.mExecutingActions) {
            throw new IllegalStateException("FragmentManager is already executing transactions");
        }
        if (Looper.myLooper() != this.mHost.getHandler().getLooper()) {
            throw new IllegalStateException("Must be called from main thread of fragment host");
        }
        if (!b) {
            this.checkStateLoss();
        }
        if (this.mTmpRecords == null) {
            this.mTmpRecords = new ArrayList<BackStackRecord>();
            this.mTmpIsPop = new ArrayList<Boolean>();
        }
        this.mExecutingActions = true;
        try {
            this.executePostponedTransaction(null, null);
        }
        finally {
            this.mExecutingActions = false;
        }
    }
    
    private static void executeOps(final ArrayList<BackStackRecord> list, final ArrayList<Boolean> list2, final int n, final int n2) {
        for (int i = n; i < n2; ++i) {
            final BackStackRecord backStackRecord = list.get(i);
            if (list2.get(i)) {
                backStackRecord.bumpBackStackNesting(-1);
                backStackRecord.executePopOps(i == n2 - 1);
            }
            else {
                backStackRecord.bumpBackStackNesting(1);
                backStackRecord.executeOps();
            }
        }
    }
    
    private void executeOpsTogether(final ArrayList<BackStackRecord> list, final ArrayList<Boolean> list2, final int n, final int n2) {
        final boolean mAllowOptimization = list.get(n).mAllowOptimization;
        int n3 = 0;
        if (this.mTmpAddedFragments == null) {
            this.mTmpAddedFragments = new ArrayList<Fragment>();
        }
        else {
            this.mTmpAddedFragments.clear();
        }
        if (this.mAdded != null) {
            this.mTmpAddedFragments.addAll(this.mAdded);
        }
        for (int i = n; i < n2; ++i) {
            final BackStackRecord backStackRecord = list.get(i);
            if (!list2.get(i)) {
                backStackRecord.expandReplaceOps(this.mTmpAddedFragments);
            }
            else {
                backStackRecord.trackAddedFragmentsInPop(this.mTmpAddedFragments);
            }
            if (n3 != 0 || backStackRecord.mAddToBackStack) {
                n3 = 1;
            }
            else {
                n3 = 0;
            }
        }
        this.mTmpAddedFragments.clear();
        if (!mAllowOptimization) {
            FragmentTransition.startTransitions(this, list, list2, n, n2, false);
        }
        executeOps(list, list2, n, n2);
        int postponePostponableTransactions = n2;
        if (mAllowOptimization) {
            final ArraySet<Fragment> set = new ArraySet<Fragment>();
            this.addAddedFragments(set);
            postponePostponableTransactions = this.postponePostponableTransactions(list, list2, n, n2, set);
            this.makeRemovedFragmentsInvisible(set);
        }
        if (postponePostponableTransactions != n && mAllowOptimization) {
            FragmentTransition.startTransitions(this, list, list2, n, postponePostponableTransactions, true);
            this.moveToState(this.mCurState, true);
        }
        for (int j = n; j < n2; ++j) {
            final BackStackRecord backStackRecord2 = list.get(j);
            if (list2.get(j) && backStackRecord2.mIndex >= 0) {
                this.freeBackStackIndex(backStackRecord2.mIndex);
                backStackRecord2.mIndex = -1;
            }
        }
        if (n3 != 0) {
            this.reportBackStackChanged();
        }
    }
    
    private void executePostponedTransaction(final ArrayList<BackStackRecord> list, final ArrayList<Boolean> list2) {
        int size;
        if (this.mPostponedTransactions == null) {
            size = 0;
        }
        else {
            size = this.mPostponedTransactions.size();
        }
        int i = 0;
    Label_0081_Outer:
        while (i < size) {
            final StartEnterTransitionListener startEnterTransitionListener = this.mPostponedTransactions.get(i);
            while (true) {
                Label_0098: {
                    if (list == null || startEnterTransitionListener.mIsBack) {
                        break Label_0098;
                    }
                    final int index = list.indexOf(startEnterTransitionListener.mRecord);
                    if (index == -1 || !list2.get(index)) {
                        break Label_0098;
                    }
                    startEnterTransitionListener.cancelTransaction();
                    ++i;
                    continue Label_0081_Outer;
                }
                if (startEnterTransitionListener.isReady() || (list != null && startEnterTransitionListener.mRecord.interactsWith(list, 0, list.size()))) {
                    this.mPostponedTransactions.remove(i);
                    --i;
                    --size;
                    if (list != null && !startEnterTransitionListener.mIsBack) {
                        final int index2 = list.indexOf(startEnterTransitionListener.mRecord);
                        if (index2 != -1 && list2.get(index2)) {
                            startEnterTransitionListener.cancelTransaction();
                            continue;
                        }
                    }
                    startEnterTransitionListener.completeTransaction();
                }
                continue;
            }
        }
    }
    
    private Fragment findFragmentUnder(final Fragment fragment) {
        final ViewGroup mContainer = fragment.mContainer;
        final View mView = fragment.mView;
        if (mContainer != null && mView != null) {
            for (int i = -1 + this.mAdded.indexOf(fragment); i >= 0; --i) {
                final Fragment fragment2 = this.mAdded.get(i);
                if (fragment2.mContainer == mContainer && fragment2.mView != null) {
                    return fragment2;
                }
            }
            return null;
        }
        return null;
    }
    
    private void forcePostponedTransactions() {
        if (this.mPostponedTransactions != null) {
            while (!this.mPostponedTransactions.isEmpty()) {
                this.mPostponedTransactions.remove(0).completeTransaction();
            }
        }
    }
    
    private boolean generateOpsForPendingActions(final ArrayList<BackStackRecord> list, final ArrayList<Boolean> list2) {
        synchronized (this) {
            if (this.mPendingActions == null || this.mPendingActions.size() == 0) {
                return false;
            }
            final int size = this.mPendingActions.size();
            for (int i = 0; i < size; ++i) {
                this.mPendingActions.get(i).generateOps(list, list2);
            }
            this.mPendingActions.clear();
            this.mHost.getHandler().removeCallbacks(this.mExecCommit);
            // monitorexit(this)
            if (size > 0) {
                return true;
            }
        }
        return false;
    }
    
    static Animation makeFadeAnimation(final Context context, final float n, final float n2) {
        final AlphaAnimation alphaAnimation = new AlphaAnimation(n, n2);
        alphaAnimation.setInterpolator(FragmentManagerImpl.DECELERATE_CUBIC);
        alphaAnimation.setDuration(220L);
        return (Animation)alphaAnimation;
    }
    
    static Animation makeOpenCloseAnimation(final Context context, final float n, final float n2, final float n3, final float n4) {
        final AnimationSet set = new AnimationSet(false);
        final ScaleAnimation scaleAnimation = new ScaleAnimation(n, n2, n, n2, 1, 0.5f, 1, 0.5f);
        scaleAnimation.setInterpolator(FragmentManagerImpl.DECELERATE_QUINT);
        scaleAnimation.setDuration(220L);
        set.addAnimation((Animation)scaleAnimation);
        final AlphaAnimation alphaAnimation = new AlphaAnimation(n3, n4);
        alphaAnimation.setInterpolator(FragmentManagerImpl.DECELERATE_CUBIC);
        alphaAnimation.setDuration(220L);
        set.addAnimation((Animation)alphaAnimation);
        return (Animation)set;
    }
    
    private void makeRemovedFragmentsInvisible(final ArraySet<Fragment> set) {
        for (int size = set.size(), i = 0; i < size; ++i) {
            final Fragment fragment = set.valueAt(i);
            if (!fragment.mAdded) {
                final View view = fragment.getView();
                if (Build$VERSION.SDK_INT < 11) {
                    fragment.getView().setVisibility(4);
                }
                else {
                    fragment.mPostponedAlpha = view.getAlpha();
                    view.setAlpha(0.0f);
                }
            }
        }
    }
    
    static boolean modifiesAlpha(final Animation animation) {
        if (!(animation instanceof AlphaAnimation)) {
            if (animation instanceof AnimationSet) {
                final List animations = ((AnimationSet)animation).getAnimations();
                for (int i = 0; i < animations.size(); ++i) {
                    if (animations.get(i) instanceof AlphaAnimation) {
                        return true;
                    }
                }
            }
            return false;
        }
        return true;
    }
    
    private void optimizeAndExecuteOps(final ArrayList<BackStackRecord> list, final ArrayList<Boolean> list2) {
        if (list != null && !list.isEmpty()) {
            if (list2 == null || list.size() != list2.size()) {
                throw new IllegalStateException("Internal error with the back stack records");
            }
            this.executePostponedTransaction(list, list2);
            final int size = list.size();
            int n = 0;
            for (int i = 0; i < size; ++i) {
                if (!list.get(i).mAllowOptimization) {
                    if (n != i) {
                        this.executeOpsTogether(list, list2, n, i);
                    }
                    int n2 = i + 1;
                    if (list2.get(i)) {
                        while (n2 < size && list2.get(n2) && !list.get(n2).mAllowOptimization) {
                            ++n2;
                        }
                    }
                    this.executeOpsTogether(list, list2, i, n2);
                    n = n2;
                    i = n2 - 1;
                }
            }
            if (n != size) {
                this.executeOpsTogether(list, list2, n, size);
            }
        }
    }
    
    private boolean popBackStackImmediate(final String s, final int n, final int n2) {
        this.execPendingActions();
        this.ensureExecReady(true);
        final boolean popBackStackState = this.popBackStackState(this.mTmpRecords, this.mTmpIsPop, s, n, n2);
        Label_0053: {
            if (!popBackStackState) {
                break Label_0053;
            }
            this.mExecutingActions = true;
            try {
                this.optimizeAndExecuteOps(this.mTmpRecords, this.mTmpIsPop);
                this.cleanupExec();
                this.doPendingDeferredStart();
                return popBackStackState;
            }
            finally {
                this.cleanupExec();
            }
        }
    }
    
    private int postponePostponableTransactions(final ArrayList<BackStackRecord> list, final ArrayList<Boolean> list2, final int n, final int n2, final ArraySet<Fragment> set) {
        int n3 = n2;
        for (int i = n2 - 1; i >= n; --i) {
            final BackStackRecord backStackRecord = list.get(i);
            final boolean booleanValue = list2.get(i);
            int n4;
            if (backStackRecord.isPostponed() && !backStackRecord.interactsWith(list, i + 1, n2)) {
                n4 = 1;
            }
            else {
                n4 = 0;
            }
            if (n4 != 0) {
                if (this.mPostponedTransactions == null) {
                    this.mPostponedTransactions = new ArrayList<StartEnterTransitionListener>();
                }
                final StartEnterTransitionListener onStartPostponedListener = new StartEnterTransitionListener(backStackRecord, booleanValue);
                this.mPostponedTransactions.add(onStartPostponedListener);
                backStackRecord.setOnStartPostponedListener(onStartPostponedListener);
                if (booleanValue) {
                    backStackRecord.executeOps();
                }
                else {
                    backStackRecord.executePopOps(false);
                }
                --n3;
                if (i != n3) {
                    list.remove(i);
                    list.add(n3, backStackRecord);
                }
                this.addAddedFragments(set);
            }
        }
        return n3;
    }
    
    public static int reverseTransit(final int n) {
        switch (n) {
            default: {
                return 0;
            }
            case 4097: {
                return 8194;
            }
            case 8194: {
                return 4097;
            }
            case 4099: {
                return 4099;
            }
        }
    }
    
    private void scheduleCommit() {
    Label_0081_Outer:
        while (true) {
            int n = 1;
        Label_0081:
            while (true) {
            Label_0097:
                while (true) {
                    Label_0092: {
                        while (true) {
                            final int n2;
                            synchronized (this) {
                                if (this.mPostponedTransactions == null || this.mPostponedTransactions.isEmpty()) {
                                    break Label_0092;
                                }
                                n2 = n;
                                if (this.mPendingActions != null && this.mPendingActions.size() == n) {
                                    break Label_0081;
                                }
                                break Label_0097;
                                this.mHost.getHandler().removeCallbacks(this.mExecCommit);
                                this.mHost.getHandler().post(this.mExecCommit);
                                return;
                            }
                            if (n2 == 0 && n == 0) {
                                return;
                            }
                            continue;
                        }
                    }
                    int n2 = 0;
                    continue Label_0081_Outer;
                }
                n = 0;
                continue Label_0081;
            }
        }
    }
    
    private void setHWLayerAnimListenerIfAlpha(final View view, final Animation animation) {
        if (view == null || animation == null || !shouldRunOnHWLayer(view, animation)) {
            return;
        }
        while (true) {
            try {
                if (FragmentManagerImpl.sAnimationListenerField == null) {
                    (FragmentManagerImpl.sAnimationListenerField = Animation.class.getDeclaredField("mListener")).setAccessible(true);
                }
                final Animation$AnimationListener animation$AnimationListener = (Animation$AnimationListener)FragmentManagerImpl.sAnimationListenerField.get(animation);
                ViewCompat.setLayerType(view, 2, null);
                animation.setAnimationListener((Animation$AnimationListener)new AnimateOnHWLayerIfNeededListener(view, animation, animation$AnimationListener));
            }
            catch (NoSuchFieldException ex) {
                Log.e("FragmentManager", "No field with the name mListener is found in Animation class", (Throwable)ex);
                final Animation$AnimationListener animation$AnimationListener = null;
                continue;
            }
            catch (IllegalAccessException ex2) {
                Log.e("FragmentManager", "Cannot access Animation's mListener field", (Throwable)ex2);
                final Animation$AnimationListener animation$AnimationListener = null;
                continue;
            }
            break;
        }
    }
    
    static boolean shouldRunOnHWLayer(final View view, final Animation animation) {
        return Build$VERSION.SDK_INT >= 19 && ViewCompat.getLayerType(view) == 0 && ViewCompat.hasOverlappingRendering(view) && modifiesAlpha(animation);
    }
    
    private void throwException(final RuntimeException ex) {
        Log.e("FragmentManager", ex.getMessage());
        Log.e("FragmentManager", "Activity state:");
        final PrintWriter printWriter = new PrintWriter(new LogWriter("FragmentManager"));
        while (true) {
            Label_0079: {
                if (this.mHost == null) {
                    break Label_0079;
                }
                try {
                    this.mHost.onDump("  ", null, printWriter, new String[0]);
                    throw ex;
                }
                catch (Exception ex2) {
                    Log.e("FragmentManager", "Failed dumping state", (Throwable)ex2);
                    throw ex;
                }
                try {
                    this.dump("  ", null, printWriter, new String[0]);
                    continue;
                }
                catch (Exception ex3) {
                    Log.e("FragmentManager", "Failed dumping state", (Throwable)ex3);
                    continue;
                }
            }
            continue;
        }
    }
    
    public static int transitToStyleIndex(final int n, final boolean b) {
        switch (n) {
            default: {
                return -1;
            }
            case 4097: {
                if (b) {
                    return 1;
                }
                return 2;
            }
            case 8194: {
                if (b) {
                    return 3;
                }
                return 4;
            }
            case 4099: {
                if (b) {
                    return 5;
                }
                return 6;
            }
        }
    }
    
    void addBackStackState(final BackStackRecord backStackRecord) {
        if (this.mBackStack == null) {
            this.mBackStack = new ArrayList<BackStackRecord>();
        }
        this.mBackStack.add(backStackRecord);
        this.reportBackStackChanged();
    }
    
    public void addFragment(final Fragment fragment, final boolean b) {
        if (this.mAdded == null) {
            this.mAdded = new ArrayList<Fragment>();
        }
        if (FragmentManagerImpl.DEBUG) {
            Log.v("FragmentManager", "add: " + fragment);
        }
        this.makeActive(fragment);
        if (!fragment.mDetached) {
            if (this.mAdded.contains(fragment)) {
                throw new IllegalStateException("Fragment already added: " + fragment);
            }
            this.mAdded.add(fragment);
            fragment.mAdded = true;
            fragment.mRemoving = false;
            if (fragment.mView == null) {
                fragment.mHiddenChanged = false;
            }
            if (fragment.mHasMenu && fragment.mMenuVisible) {
                this.mNeedMenuInvalidate = true;
            }
            if (b) {
                this.moveToState(fragment);
            }
        }
    }
    
    @Override
    public void addOnBackStackChangedListener(final OnBackStackChangedListener onBackStackChangedListener) {
        if (this.mBackStackChangeListeners == null) {
            this.mBackStackChangeListeners = new ArrayList<OnBackStackChangedListener>();
        }
        this.mBackStackChangeListeners.add(onBackStackChangedListener);
    }
    
    public int allocBackStackIndex(final BackStackRecord backStackRecord) {
        synchronized (this) {
            if (this.mAvailBackStackIndices == null || this.mAvailBackStackIndices.size() <= 0) {
                if (this.mBackStackIndices == null) {
                    this.mBackStackIndices = new ArrayList<BackStackRecord>();
                }
                final int size = this.mBackStackIndices.size();
                if (FragmentManagerImpl.DEBUG) {
                    Log.v("FragmentManager", "Setting back stack index " + size + " to " + backStackRecord);
                }
                this.mBackStackIndices.add(backStackRecord);
                return size;
            }
            final int intValue = this.mAvailBackStackIndices.remove(-1 + this.mAvailBackStackIndices.size());
            if (FragmentManagerImpl.DEBUG) {
                Log.v("FragmentManager", "Adding back stack index " + intValue + " with " + backStackRecord);
            }
            this.mBackStackIndices.set(intValue, backStackRecord);
            return intValue;
        }
    }
    
    public void attachController(final FragmentHostCallback mHost, final FragmentContainer mContainer, final Fragment mParent) {
        if (this.mHost != null) {
            throw new IllegalStateException("Already attached");
        }
        this.mHost = mHost;
        this.mContainer = mContainer;
        this.mParent = mParent;
    }
    
    public void attachFragment(final Fragment fragment) {
        if (FragmentManagerImpl.DEBUG) {
            Log.v("FragmentManager", "attach: " + fragment);
        }
        if (fragment.mDetached) {
            fragment.mDetached = false;
            if (!fragment.mAdded) {
                if (this.mAdded == null) {
                    this.mAdded = new ArrayList<Fragment>();
                }
                if (this.mAdded.contains(fragment)) {
                    throw new IllegalStateException("Fragment already added: " + fragment);
                }
                if (FragmentManagerImpl.DEBUG) {
                    Log.v("FragmentManager", "add from attach: " + fragment);
                }
                this.mAdded.add(fragment);
                fragment.mAdded = true;
                if (fragment.mHasMenu && fragment.mMenuVisible) {
                    this.mNeedMenuInvalidate = true;
                }
            }
        }
    }
    
    @Override
    public FragmentTransaction beginTransaction() {
        return new BackStackRecord(this);
    }
    
    void completeShowHideFragment(final Fragment fragment) {
        if (fragment.mView != null) {
            final Animation loadAnimation = this.loadAnimation(fragment, fragment.getNextTransition(), !fragment.mHidden, fragment.getNextTransitionStyle());
            if (loadAnimation != null) {
                this.setHWLayerAnimListenerIfAlpha(fragment.mView, loadAnimation);
                fragment.mView.startAnimation(loadAnimation);
                this.setHWLayerAnimListenerIfAlpha(fragment.mView, loadAnimation);
                loadAnimation.start();
            }
            int visibility;
            if (fragment.mHidden && !fragment.isHideReplaced()) {
                visibility = 8;
            }
            else {
                visibility = 0;
            }
            fragment.mView.setVisibility(visibility);
            if (fragment.isHideReplaced()) {
                fragment.setHideReplaced(false);
            }
        }
        if (fragment.mAdded && fragment.mHasMenu && fragment.mMenuVisible) {
            this.mNeedMenuInvalidate = true;
        }
        fragment.mHiddenChanged = false;
        fragment.onHiddenChanged(fragment.mHidden);
    }
    
    public void detachFragment(final Fragment fragment) {
        if (FragmentManagerImpl.DEBUG) {
            Log.v("FragmentManager", "detach: " + fragment);
        }
        if (!fragment.mDetached) {
            fragment.mDetached = true;
            if (fragment.mAdded) {
                if (this.mAdded != null) {
                    if (FragmentManagerImpl.DEBUG) {
                        Log.v("FragmentManager", "remove from detach: " + fragment);
                    }
                    this.mAdded.remove(fragment);
                }
                if (fragment.mHasMenu && fragment.mMenuVisible) {
                    this.mNeedMenuInvalidate = true;
                }
                fragment.mAdded = false;
            }
        }
    }
    
    public void dispatchActivityCreated() {
        this.mStateSaved = false;
        this.mExecutingActions = true;
        this.moveToState(2, false);
        this.mExecutingActions = false;
    }
    
    public void dispatchConfigurationChanged(final Configuration configuration) {
        if (this.mAdded != null) {
            for (int i = 0; i < this.mAdded.size(); ++i) {
                final Fragment fragment = this.mAdded.get(i);
                if (fragment != null) {
                    fragment.performConfigurationChanged(configuration);
                }
            }
        }
    }
    
    public boolean dispatchContextItemSelected(final MenuItem menuItem) {
        if (this.mAdded != null) {
            for (int i = 0; i < this.mAdded.size(); ++i) {
                final Fragment fragment = this.mAdded.get(i);
                if (fragment != null && fragment.performContextItemSelected(menuItem)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void dispatchCreate() {
        this.mStateSaved = false;
        this.mExecutingActions = true;
        this.moveToState(1, false);
        this.mExecutingActions = false;
    }
    
    public boolean dispatchCreateOptionsMenu(final Menu menu, final MenuInflater menuInflater) {
        final ArrayList<Fragment> mAdded = this.mAdded;
        ArrayList<Fragment> mCreatedMenus = null;
        boolean b = false;
        if (mAdded != null) {
            for (int i = 0; i < this.mAdded.size(); ++i) {
                final Fragment fragment = this.mAdded.get(i);
                if (fragment != null && fragment.performCreateOptionsMenu(menu, menuInflater)) {
                    b = true;
                    if (mCreatedMenus == null) {
                        mCreatedMenus = new ArrayList<Fragment>();
                    }
                    mCreatedMenus.add(fragment);
                }
            }
        }
        if (this.mCreatedMenus != null) {
            for (int j = 0; j < this.mCreatedMenus.size(); ++j) {
                final Fragment fragment2 = this.mCreatedMenus.get(j);
                if (mCreatedMenus == null || !mCreatedMenus.contains(fragment2)) {
                    fragment2.onDestroyOptionsMenu();
                }
            }
        }
        this.mCreatedMenus = mCreatedMenus;
        return b;
    }
    
    public void dispatchDestroy() {
        this.mDestroyed = true;
        this.execPendingActions();
        this.mExecutingActions = true;
        this.moveToState(0, false);
        this.mExecutingActions = false;
        this.mHost = null;
        this.mContainer = null;
        this.mParent = null;
    }
    
    public void dispatchDestroyView() {
        this.mExecutingActions = true;
        this.moveToState(1, false);
        this.mExecutingActions = false;
    }
    
    public void dispatchLowMemory() {
        if (this.mAdded != null) {
            for (int i = 0; i < this.mAdded.size(); ++i) {
                final Fragment fragment = this.mAdded.get(i);
                if (fragment != null) {
                    fragment.performLowMemory();
                }
            }
        }
    }
    
    public void dispatchMultiWindowModeChanged(final boolean b) {
        if (this.mAdded != null) {
            for (int i = -1 + this.mAdded.size(); i >= 0; --i) {
                final Fragment fragment = this.mAdded.get(i);
                if (fragment != null) {
                    fragment.performMultiWindowModeChanged(b);
                }
            }
        }
    }
    
    void dispatchOnFragmentActivityCreated(final Fragment fragment, final Bundle bundle, final boolean b) {
        if (this.mParent != null) {
            final FragmentManager fragmentManager = this.mParent.getFragmentManager();
            if (fragmentManager instanceof FragmentManagerImpl) {
                ((FragmentManagerImpl)fragmentManager).dispatchOnFragmentActivityCreated(fragment, bundle, true);
            }
        }
        if (this.mLifecycleCallbacks != null) {
            for (final Pair<FragmentLifecycleCallbacks, Boolean> pair : this.mLifecycleCallbacks) {
                if (!b || pair.second) {
                    pair.first.onFragmentActivityCreated(this, fragment, bundle);
                }
            }
        }
    }
    
    void dispatchOnFragmentAttached(final Fragment fragment, final Context context, final boolean b) {
        if (this.mParent != null) {
            final FragmentManager fragmentManager = this.mParent.getFragmentManager();
            if (fragmentManager instanceof FragmentManagerImpl) {
                ((FragmentManagerImpl)fragmentManager).dispatchOnFragmentAttached(fragment, context, true);
            }
        }
        if (this.mLifecycleCallbacks != null) {
            for (final Pair<FragmentLifecycleCallbacks, Boolean> pair : this.mLifecycleCallbacks) {
                if (!b || pair.second) {
                    pair.first.onFragmentAttached(this, fragment, context);
                }
            }
        }
    }
    
    void dispatchOnFragmentCreated(final Fragment fragment, final Bundle bundle, final boolean b) {
        if (this.mParent != null) {
            final FragmentManager fragmentManager = this.mParent.getFragmentManager();
            if (fragmentManager instanceof FragmentManagerImpl) {
                ((FragmentManagerImpl)fragmentManager).dispatchOnFragmentCreated(fragment, bundle, true);
            }
        }
        if (this.mLifecycleCallbacks != null) {
            for (final Pair<FragmentLifecycleCallbacks, Boolean> pair : this.mLifecycleCallbacks) {
                if (!b || pair.second) {
                    pair.first.onFragmentCreated(this, fragment, bundle);
                }
            }
        }
    }
    
    void dispatchOnFragmentDestroyed(final Fragment fragment, final boolean b) {
        if (this.mParent != null) {
            final FragmentManager fragmentManager = this.mParent.getFragmentManager();
            if (fragmentManager instanceof FragmentManagerImpl) {
                ((FragmentManagerImpl)fragmentManager).dispatchOnFragmentDestroyed(fragment, true);
            }
        }
        if (this.mLifecycleCallbacks != null) {
            for (final Pair<FragmentLifecycleCallbacks, Boolean> pair : this.mLifecycleCallbacks) {
                if (!b || pair.second) {
                    pair.first.onFragmentDestroyed(this, fragment);
                }
            }
        }
    }
    
    void dispatchOnFragmentDetached(final Fragment fragment, final boolean b) {
        if (this.mParent != null) {
            final FragmentManager fragmentManager = this.mParent.getFragmentManager();
            if (fragmentManager instanceof FragmentManagerImpl) {
                ((FragmentManagerImpl)fragmentManager).dispatchOnFragmentDetached(fragment, true);
            }
        }
        if (this.mLifecycleCallbacks != null) {
            for (final Pair<FragmentLifecycleCallbacks, Boolean> pair : this.mLifecycleCallbacks) {
                if (!b || pair.second) {
                    pair.first.onFragmentDetached(this, fragment);
                }
            }
        }
    }
    
    void dispatchOnFragmentPaused(final Fragment fragment, final boolean b) {
        if (this.mParent != null) {
            final FragmentManager fragmentManager = this.mParent.getFragmentManager();
            if (fragmentManager instanceof FragmentManagerImpl) {
                ((FragmentManagerImpl)fragmentManager).dispatchOnFragmentPaused(fragment, true);
            }
        }
        if (this.mLifecycleCallbacks != null) {
            for (final Pair<FragmentLifecycleCallbacks, Boolean> pair : this.mLifecycleCallbacks) {
                if (!b || pair.second) {
                    pair.first.onFragmentPaused(this, fragment);
                }
            }
        }
    }
    
    void dispatchOnFragmentPreAttached(final Fragment fragment, final Context context, final boolean b) {
        if (this.mParent != null) {
            final FragmentManager fragmentManager = this.mParent.getFragmentManager();
            if (fragmentManager instanceof FragmentManagerImpl) {
                ((FragmentManagerImpl)fragmentManager).dispatchOnFragmentPreAttached(fragment, context, true);
            }
        }
        if (this.mLifecycleCallbacks != null) {
            for (final Pair<FragmentLifecycleCallbacks, Boolean> pair : this.mLifecycleCallbacks) {
                if (!b || pair.second) {
                    pair.first.onFragmentPreAttached(this, fragment, context);
                }
            }
        }
    }
    
    void dispatchOnFragmentResumed(final Fragment fragment, final boolean b) {
        if (this.mParent != null) {
            final FragmentManager fragmentManager = this.mParent.getFragmentManager();
            if (fragmentManager instanceof FragmentManagerImpl) {
                ((FragmentManagerImpl)fragmentManager).dispatchOnFragmentResumed(fragment, true);
            }
        }
        if (this.mLifecycleCallbacks != null) {
            for (final Pair<FragmentLifecycleCallbacks, Boolean> pair : this.mLifecycleCallbacks) {
                if (!b || pair.second) {
                    pair.first.onFragmentResumed(this, fragment);
                }
            }
        }
    }
    
    void dispatchOnFragmentSaveInstanceState(final Fragment fragment, final Bundle bundle, final boolean b) {
        if (this.mParent != null) {
            final FragmentManager fragmentManager = this.mParent.getFragmentManager();
            if (fragmentManager instanceof FragmentManagerImpl) {
                ((FragmentManagerImpl)fragmentManager).dispatchOnFragmentSaveInstanceState(fragment, bundle, true);
            }
        }
        if (this.mLifecycleCallbacks != null) {
            for (final Pair<FragmentLifecycleCallbacks, Boolean> pair : this.mLifecycleCallbacks) {
                if (!b || pair.second) {
                    pair.first.onFragmentSaveInstanceState(this, fragment, bundle);
                }
            }
        }
    }
    
    void dispatchOnFragmentStarted(final Fragment fragment, final boolean b) {
        if (this.mParent != null) {
            final FragmentManager fragmentManager = this.mParent.getFragmentManager();
            if (fragmentManager instanceof FragmentManagerImpl) {
                ((FragmentManagerImpl)fragmentManager).dispatchOnFragmentStarted(fragment, true);
            }
        }
        if (this.mLifecycleCallbacks != null) {
            for (final Pair<FragmentLifecycleCallbacks, Boolean> pair : this.mLifecycleCallbacks) {
                if (!b || pair.second) {
                    pair.first.onFragmentStarted(this, fragment);
                }
            }
        }
    }
    
    void dispatchOnFragmentStopped(final Fragment fragment, final boolean b) {
        if (this.mParent != null) {
            final FragmentManager fragmentManager = this.mParent.getFragmentManager();
            if (fragmentManager instanceof FragmentManagerImpl) {
                ((FragmentManagerImpl)fragmentManager).dispatchOnFragmentStopped(fragment, true);
            }
        }
        if (this.mLifecycleCallbacks != null) {
            for (final Pair<FragmentLifecycleCallbacks, Boolean> pair : this.mLifecycleCallbacks) {
                if (!b || pair.second) {
                    pair.first.onFragmentStopped(this, fragment);
                }
            }
        }
    }
    
    void dispatchOnFragmentViewCreated(final Fragment fragment, final View view, final Bundle bundle, final boolean b) {
        if (this.mParent != null) {
            final FragmentManager fragmentManager = this.mParent.getFragmentManager();
            if (fragmentManager instanceof FragmentManagerImpl) {
                ((FragmentManagerImpl)fragmentManager).dispatchOnFragmentViewCreated(fragment, view, bundle, true);
            }
        }
        if (this.mLifecycleCallbacks != null) {
            for (final Pair<FragmentLifecycleCallbacks, Boolean> pair : this.mLifecycleCallbacks) {
                if (!b || pair.second) {
                    pair.first.onFragmentViewCreated(this, fragment, view, bundle);
                }
            }
        }
    }
    
    void dispatchOnFragmentViewDestroyed(final Fragment fragment, final boolean b) {
        if (this.mParent != null) {
            final FragmentManager fragmentManager = this.mParent.getFragmentManager();
            if (fragmentManager instanceof FragmentManagerImpl) {
                ((FragmentManagerImpl)fragmentManager).dispatchOnFragmentViewDestroyed(fragment, true);
            }
        }
        if (this.mLifecycleCallbacks != null) {
            for (final Pair<FragmentLifecycleCallbacks, Boolean> pair : this.mLifecycleCallbacks) {
                if (!b || pair.second) {
                    pair.first.onFragmentViewDestroyed(this, fragment);
                }
            }
        }
    }
    
    public boolean dispatchOptionsItemSelected(final MenuItem menuItem) {
        if (this.mAdded != null) {
            for (int i = 0; i < this.mAdded.size(); ++i) {
                final Fragment fragment = this.mAdded.get(i);
                if (fragment != null && fragment.performOptionsItemSelected(menuItem)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void dispatchOptionsMenuClosed(final Menu menu) {
        if (this.mAdded != null) {
            for (int i = 0; i < this.mAdded.size(); ++i) {
                final Fragment fragment = this.mAdded.get(i);
                if (fragment != null) {
                    fragment.performOptionsMenuClosed(menu);
                }
            }
        }
    }
    
    public void dispatchPause() {
        this.mExecutingActions = true;
        this.moveToState(4, false);
        this.mExecutingActions = false;
    }
    
    public void dispatchPictureInPictureModeChanged(final boolean b) {
        if (this.mAdded != null) {
            for (int i = -1 + this.mAdded.size(); i >= 0; --i) {
                final Fragment fragment = this.mAdded.get(i);
                if (fragment != null) {
                    fragment.performPictureInPictureModeChanged(b);
                }
            }
        }
    }
    
    public boolean dispatchPrepareOptionsMenu(final Menu menu) {
        final ArrayList<Fragment> mAdded = this.mAdded;
        boolean b = false;
        if (mAdded != null) {
            for (int i = 0; i < this.mAdded.size(); ++i) {
                final Fragment fragment = this.mAdded.get(i);
                if (fragment != null && fragment.performPrepareOptionsMenu(menu)) {
                    b = true;
                }
            }
        }
        return b;
    }
    
    public void dispatchReallyStop() {
        this.mExecutingActions = true;
        this.moveToState(2, false);
        this.mExecutingActions = false;
    }
    
    public void dispatchResume() {
        this.mStateSaved = false;
        this.mExecutingActions = true;
        this.moveToState(5, false);
        this.mExecutingActions = false;
    }
    
    public void dispatchStart() {
        this.mStateSaved = false;
        this.mExecutingActions = true;
        this.moveToState(4, false);
        this.mExecutingActions = false;
    }
    
    public void dispatchStop() {
        this.mStateSaved = true;
        this.mExecutingActions = true;
        this.moveToState(3, false);
        this.mExecutingActions = false;
    }
    
    void doPendingDeferredStart() {
        if (this.mHavePendingDeferredStart) {
            boolean b = false;
            for (int i = 0; i < this.mActive.size(); ++i) {
                final Fragment fragment = this.mActive.get(i);
                if (fragment != null && fragment.mLoaderManager != null) {
                    b |= fragment.mLoaderManager.hasRunningLoaders();
                }
            }
            if (!b) {
                this.mHavePendingDeferredStart = false;
                this.startPendingDeferredFragments();
            }
        }
    }
    
    @Override
    public void dump(final String s, final FileDescriptor fileDescriptor, final PrintWriter printWriter, final String[] array) {
        final String string = s + "    ";
        if (this.mActive != null) {
            final int size = this.mActive.size();
            if (size > 0) {
                printWriter.print(s);
                printWriter.print("Active Fragments in ");
                printWriter.print(Integer.toHexString(System.identityHashCode(this)));
                printWriter.println(":");
                for (int i = 0; i < size; ++i) {
                    final Fragment fragment = this.mActive.get(i);
                    printWriter.print(s);
                    printWriter.print("  #");
                    printWriter.print(i);
                    printWriter.print(": ");
                    printWriter.println(fragment);
                    if (fragment != null) {
                        fragment.dump(string, fileDescriptor, printWriter, array);
                    }
                }
            }
        }
        if (this.mAdded != null) {
            final int size2 = this.mAdded.size();
            if (size2 > 0) {
                printWriter.print(s);
                printWriter.println("Added Fragments:");
                for (int j = 0; j < size2; ++j) {
                    final Fragment fragment2 = this.mAdded.get(j);
                    printWriter.print(s);
                    printWriter.print("  #");
                    printWriter.print(j);
                    printWriter.print(": ");
                    printWriter.println(fragment2.toString());
                }
            }
        }
        if (this.mCreatedMenus != null) {
            final int size3 = this.mCreatedMenus.size();
            if (size3 > 0) {
                printWriter.print(s);
                printWriter.println("Fragments Created Menus:");
                for (int k = 0; k < size3; ++k) {
                    final Fragment fragment3 = this.mCreatedMenus.get(k);
                    printWriter.print(s);
                    printWriter.print("  #");
                    printWriter.print(k);
                    printWriter.print(": ");
                    printWriter.println(fragment3.toString());
                }
            }
        }
        if (this.mBackStack != null) {
            final int size4 = this.mBackStack.size();
            if (size4 > 0) {
                printWriter.print(s);
                printWriter.println("Back Stack:");
                for (int l = 0; l < size4; ++l) {
                    final BackStackRecord backStackRecord = this.mBackStack.get(l);
                    printWriter.print(s);
                    printWriter.print("  #");
                    printWriter.print(l);
                    printWriter.print(": ");
                    printWriter.println(backStackRecord.toString());
                    backStackRecord.dump(string, fileDescriptor, printWriter, array);
                }
            }
        }
        synchronized (this) {
            if (this.mBackStackIndices != null) {
                final int size5 = this.mBackStackIndices.size();
                if (size5 > 0) {
                    printWriter.print(s);
                    printWriter.println("Back Stack Indices:");
                    for (int n = 0; n < size5; ++n) {
                        final BackStackRecord backStackRecord2 = this.mBackStackIndices.get(n);
                        printWriter.print(s);
                        printWriter.print("  #");
                        printWriter.print(n);
                        printWriter.print(": ");
                        printWriter.println(backStackRecord2);
                    }
                }
            }
            if (this.mAvailBackStackIndices != null && this.mAvailBackStackIndices.size() > 0) {
                printWriter.print(s);
                printWriter.print("mAvailBackStackIndices: ");
                printWriter.println(Arrays.toString(this.mAvailBackStackIndices.toArray()));
            }
            // monitorexit(this)
            if (this.mPendingActions != null) {
                final int size6 = this.mPendingActions.size();
                if (size6 > 0) {
                    printWriter.print(s);
                    printWriter.println("Pending Actions:");
                    for (int n2 = 0; n2 < size6; ++n2) {
                        final OpGenerator opGenerator = this.mPendingActions.get(n2);
                        printWriter.print(s);
                        printWriter.print("  #");
                        printWriter.print(n2);
                        printWriter.print(": ");
                        printWriter.println(opGenerator);
                    }
                }
            }
        }
        printWriter.print(s);
        printWriter.println("FragmentManager misc state:");
        printWriter.print(s);
        printWriter.print("  mHost=");
        printWriter.println(this.mHost);
        printWriter.print(s);
        printWriter.print("  mContainer=");
        printWriter.println(this.mContainer);
        if (this.mParent != null) {
            printWriter.print(s);
            printWriter.print("  mParent=");
            printWriter.println(this.mParent);
        }
        printWriter.print(s);
        printWriter.print("  mCurState=");
        printWriter.print(this.mCurState);
        printWriter.print(" mStateSaved=");
        printWriter.print(this.mStateSaved);
        printWriter.print(" mDestroyed=");
        printWriter.println(this.mDestroyed);
        if (this.mNeedMenuInvalidate) {
            printWriter.print(s);
            printWriter.print("  mNeedMenuInvalidate=");
            printWriter.println(this.mNeedMenuInvalidate);
        }
        if (this.mNoTransactionsBecause != null) {
            printWriter.print(s);
            printWriter.print("  mNoTransactionsBecause=");
            printWriter.println(this.mNoTransactionsBecause);
        }
        if (this.mAvailIndices != null && this.mAvailIndices.size() > 0) {
            printWriter.print(s);
            printWriter.print("  mAvailIndices: ");
            printWriter.println(Arrays.toString(this.mAvailIndices.toArray()));
        }
    }
    
    public void enqueueAction(final OpGenerator opGenerator, final boolean b) {
        if (!b) {
            this.checkStateLoss();
        }
        synchronized (this) {
            if (this.mDestroyed || this.mHost == null) {
                throw new IllegalStateException("Activity has been destroyed");
            }
        }
        if (this.mPendingActions == null) {
            this.mPendingActions = new ArrayList<OpGenerator>();
        }
        this.mPendingActions.add(opGenerator);
        this.scheduleCommit();
    }
    // monitorexit(this)
    
    public boolean execPendingActions() {
        this.ensureExecReady(true);
        boolean b = false;
        while (this.generateOpsForPendingActions(this.mTmpRecords, this.mTmpIsPop)) {
            this.mExecutingActions = true;
            try {
                this.optimizeAndExecuteOps(this.mTmpRecords, this.mTmpIsPop);
                this.cleanupExec();
                b = true;
                continue;
            }
            finally {
                this.cleanupExec();
            }
            break;
        }
        this.doPendingDeferredStart();
        return b;
    }
    
    public void execSingleAction(final OpGenerator opGenerator, final boolean b) {
        this.ensureExecReady(b);
        Label_0043: {
            if (!opGenerator.generateOps(this.mTmpRecords, this.mTmpIsPop)) {
                break Label_0043;
            }
            this.mExecutingActions = true;
            try {
                this.optimizeAndExecuteOps(this.mTmpRecords, this.mTmpIsPop);
                this.cleanupExec();
                this.doPendingDeferredStart();
            }
            finally {
                this.cleanupExec();
            }
        }
    }
    
    @Override
    public boolean executePendingTransactions() {
        final boolean execPendingActions = this.execPendingActions();
        this.forcePostponedTransactions();
        return execPendingActions;
    }
    
    @Override
    public Fragment findFragmentById(final int n) {
        if (this.mAdded != null) {
            for (int i = -1 + this.mAdded.size(); i >= 0; --i) {
                final Fragment fragment = this.mAdded.get(i);
                if (fragment != null && fragment.mFragmentId == n) {
                    return fragment;
                }
            }
        }
        Label_0056: {
            break Label_0056;
        }
        if (this.mActive != null) {
            for (int j = -1 + this.mActive.size(); j >= 0; --j) {
                final Fragment fragment = this.mActive.get(j);
                if (fragment != null && fragment.mFragmentId == n) {
                    return fragment;
                }
            }
        }
        return null;
    }
    
    @Override
    public Fragment findFragmentByTag(final String s) {
        if (this.mAdded != null && s != null) {
            for (int i = -1 + this.mAdded.size(); i >= 0; --i) {
                final Fragment fragment = this.mAdded.get(i);
                if (fragment != null && s.equals(fragment.mTag)) {
                    return fragment;
                }
            }
        }
        Label_0063: {
            break Label_0063;
        }
        if (this.mActive != null && s != null) {
            for (int j = -1 + this.mActive.size(); j >= 0; --j) {
                final Fragment fragment = this.mActive.get(j);
                if (fragment != null && s.equals(fragment.mTag)) {
                    return fragment;
                }
            }
        }
        return null;
    }
    
    public Fragment findFragmentByWho(final String s) {
        if (this.mActive != null && s != null) {
            for (int i = -1 + this.mActive.size(); i >= 0; --i) {
                final Fragment fragment = this.mActive.get(i);
                if (fragment != null) {
                    final Fragment fragmentByWho = fragment.findFragmentByWho(s);
                    if (fragmentByWho != null) {
                        return fragmentByWho;
                    }
                }
            }
        }
        return null;
    }
    
    public void freeBackStackIndex(final int n) {
        synchronized (this) {
            this.mBackStackIndices.set(n, null);
            if (this.mAvailBackStackIndices == null) {
                this.mAvailBackStackIndices = new ArrayList<Integer>();
            }
            if (FragmentManagerImpl.DEBUG) {
                Log.v("FragmentManager", "Freeing back stack index " + n);
            }
            this.mAvailBackStackIndices.add(n);
        }
    }
    
    @Override
    public BackStackEntry getBackStackEntryAt(final int n) {
        return this.mBackStack.get(n);
    }
    
    @Override
    public int getBackStackEntryCount() {
        if (this.mBackStack != null) {
            return this.mBackStack.size();
        }
        return 0;
    }
    
    @Override
    public Fragment getFragment(final Bundle bundle, final String s) {
        final int int1 = bundle.getInt(s, -1);
        Fragment fragment;
        if (int1 == -1) {
            fragment = null;
        }
        else {
            if (int1 >= this.mActive.size()) {
                this.throwException(new IllegalStateException("Fragment no longer exists for key " + s + ": index " + int1));
            }
            fragment = this.mActive.get(int1);
            if (fragment == null) {
                this.throwException(new IllegalStateException("Fragment no longer exists for key " + s + ": index " + int1));
                return fragment;
            }
        }
        return fragment;
    }
    
    @Override
    public List<Fragment> getFragments() {
        return this.mActive;
    }
    
    LayoutInflaterFactory getLayoutInflaterFactory() {
        return this;
    }
    
    public void hideFragment(final Fragment fragment) {
        boolean b = true;
        if (FragmentManagerImpl.DEBUG) {
            Log.v("FragmentManager", "hide: " + fragment);
        }
        if (!fragment.mHidden) {
            fragment.mHidden = b;
            if (fragment.mHiddenChanged) {
                b = false;
            }
            fragment.mHiddenChanged = b;
        }
    }
    
    @Override
    public boolean isDestroyed() {
        return this.mDestroyed;
    }
    
    boolean isStateAtLeast(final int n) {
        return this.mCurState >= n;
    }
    
    Animation loadAnimation(final Fragment fragment, final int n, final boolean b, int onGetWindowAnimations) {
        final Animation onCreateAnimation = fragment.onCreateAnimation(n, b, fragment.getNextAnim());
        if (onCreateAnimation != null) {
            return onCreateAnimation;
        }
        if (fragment.getNextAnim() != 0) {
            final Animation loadAnimation = AnimationUtils.loadAnimation(this.mHost.getContext(), fragment.getNextAnim());
            if (loadAnimation != null) {
                return loadAnimation;
            }
        }
        if (n == 0) {
            return null;
        }
        final int transitToStyleIndex = transitToStyleIndex(n, b);
        if (transitToStyleIndex < 0) {
            return null;
        }
        switch (transitToStyleIndex) {
            default: {
                if (onGetWindowAnimations == 0 && this.mHost.onHasWindowAnimations()) {
                    onGetWindowAnimations = this.mHost.onGetWindowAnimations();
                }
                if (onGetWindowAnimations == 0) {
                    return null;
                }
                return null;
            }
            case 1: {
                return makeOpenCloseAnimation(this.mHost.getContext(), 1.125f, 1.0f, 0.0f, 1.0f);
            }
            case 2: {
                return makeOpenCloseAnimation(this.mHost.getContext(), 1.0f, 0.975f, 1.0f, 0.0f);
            }
            case 3: {
                return makeOpenCloseAnimation(this.mHost.getContext(), 0.975f, 1.0f, 0.0f, 1.0f);
            }
            case 4: {
                return makeOpenCloseAnimation(this.mHost.getContext(), 1.0f, 1.075f, 1.0f, 0.0f);
            }
            case 5: {
                return makeFadeAnimation(this.mHost.getContext(), 0.0f, 1.0f);
            }
            case 6: {
                return makeFadeAnimation(this.mHost.getContext(), 1.0f, 0.0f);
            }
        }
    }
    
    void makeActive(final Fragment fragment) {
        if (fragment.mIndex < 0) {
            if (this.mAvailIndices == null || this.mAvailIndices.size() <= 0) {
                if (this.mActive == null) {
                    this.mActive = new ArrayList<Fragment>();
                }
                fragment.setIndex(this.mActive.size(), this.mParent);
                this.mActive.add(fragment);
            }
            else {
                fragment.setIndex(this.mAvailIndices.remove(-1 + this.mAvailIndices.size()), this.mParent);
                this.mActive.set(fragment.mIndex, fragment);
            }
            if (FragmentManagerImpl.DEBUG) {
                Log.v("FragmentManager", "Allocated fragment index " + fragment);
            }
        }
    }
    
    void makeInactive(final Fragment fragment) {
        if (fragment.mIndex < 0) {
            return;
        }
        if (FragmentManagerImpl.DEBUG) {
            Log.v("FragmentManager", "Freeing fragment index " + fragment);
        }
        this.mActive.set(fragment.mIndex, null);
        if (this.mAvailIndices == null) {
            this.mAvailIndices = new ArrayList<Integer>();
        }
        this.mAvailIndices.add(fragment.mIndex);
        this.mHost.inactivateFragment(fragment.mWho);
        fragment.initState();
    }
    
    void moveFragmentToExpectedState(final Fragment fragment) {
        if (fragment != null) {
            int n = this.mCurState;
            if (fragment.mRemoving) {
                if (fragment.isInBackStack()) {
                    n = Math.min(n, 1);
                }
                else {
                    n = Math.min(n, 0);
                }
            }
            this.moveToState(fragment, n, fragment.getNextTransition(), fragment.getNextTransitionStyle(), false);
            if (fragment.mView != null) {
                final Fragment fragmentUnder = this.findFragmentUnder(fragment);
                if (fragmentUnder != null) {
                    final View mView = fragmentUnder.mView;
                    final ViewGroup mContainer = fragment.mContainer;
                    final int indexOfChild = mContainer.indexOfChild(mView);
                    final int indexOfChild2 = mContainer.indexOfChild(fragment.mView);
                    if (indexOfChild2 < indexOfChild) {
                        mContainer.removeViewAt(indexOfChild2);
                        mContainer.addView(fragment.mView, indexOfChild);
                    }
                }
                if (fragment.mIsNewlyAdded && fragment.mContainer != null) {
                    if (Build$VERSION.SDK_INT < 11) {
                        fragment.mView.setVisibility(0);
                    }
                    else if (fragment.mPostponedAlpha > 0.0f) {
                        fragment.mView.setAlpha(fragment.mPostponedAlpha);
                    }
                    fragment.mPostponedAlpha = 0.0f;
                    fragment.mIsNewlyAdded = false;
                    final Animation loadAnimation = this.loadAnimation(fragment, fragment.getNextTransition(), true, fragment.getNextTransitionStyle());
                    if (loadAnimation != null) {
                        this.setHWLayerAnimListenerIfAlpha(fragment.mView, loadAnimation);
                        fragment.mView.startAnimation(loadAnimation);
                    }
                }
            }
            if (fragment.mHiddenChanged) {
                this.completeShowHideFragment(fragment);
            }
        }
    }
    
    void moveToState(final int mCurState, final boolean b) {
        if (this.mHost == null && mCurState != 0) {
            throw new IllegalStateException("No activity");
        }
        if (b || mCurState != this.mCurState) {
            this.mCurState = mCurState;
            if (this.mActive != null) {
                final ArrayList<Fragment> mAdded = this.mAdded;
                boolean b2 = false;
                if (mAdded != null) {
                    for (int size = this.mAdded.size(), i = 0; i < size; ++i) {
                        final Fragment fragment = this.mAdded.get(i);
                        this.moveFragmentToExpectedState(fragment);
                        if (fragment.mLoaderManager != null) {
                            b2 |= fragment.mLoaderManager.hasRunningLoaders();
                        }
                    }
                }
                for (int size2 = this.mActive.size(), j = 0; j < size2; ++j) {
                    final Fragment fragment2 = this.mActive.get(j);
                    if (fragment2 != null && (fragment2.mRemoving || fragment2.mDetached) && !fragment2.mIsNewlyAdded) {
                        this.moveFragmentToExpectedState(fragment2);
                        if (fragment2.mLoaderManager != null) {
                            b2 |= fragment2.mLoaderManager.hasRunningLoaders();
                        }
                    }
                }
                if (!b2) {
                    this.startPendingDeferredFragments();
                }
                if (this.mNeedMenuInvalidate && this.mHost != null && this.mCurState == 5) {
                    this.mHost.onSupportInvalidateOptionsMenu();
                    this.mNeedMenuInvalidate = false;
                }
            }
        }
    }
    
    void moveToState(final Fragment fragment) {
        this.moveToState(fragment, this.mCurState, 0, 0, false);
    }
    
    void moveToState(final Fragment fragment, int mState, final int n, final int n2, final boolean b) {
        if ((!fragment.mAdded || fragment.mDetached) && mState > 1) {
            mState = 1;
        }
        if (fragment.mRemoving && mState > fragment.mState) {
            mState = fragment.mState;
        }
        if (fragment.mDeferStart && fragment.mState < 4 && mState > 3) {
            mState = 3;
        }
        Label_0152: {
            if (fragment.mState < mState) {
                if (!fragment.mFromLayout || fragment.mInLayout) {
                    if (fragment.getAnimatingAway() != null) {
                        fragment.setAnimatingAway(null);
                        this.moveToState(fragment, fragment.getStateAfterAnimating(), 0, 0, true);
                    }
                    FragmentManagerImpl mFragmentManager;
                    int mContainerId;
                    ViewGroup mContainer;
                    String resourceName;
                    boolean mIsNewlyAdded;
                    Label_0637:Label_1166_Outer:
                    while (true) {
                        Label_0595: {
                        Label_1180_Outer:
                            while (true) {
                            Label_1150_Outer:
                                while (true) {
                                    Label_0531: {
                                        while (true) {
                                            Label_0984:Label_1032_Outer:
                                            while (true) {
                                            Label_1079_Outer:
                                                while (true) {
                                                    while (true) {
                                                        switch (fragment.mState) {
                                                            case 0: {
                                                                if (FragmentManagerImpl.DEBUG) {
                                                                    Log.v("FragmentManager", "moveto CREATED: " + fragment);
                                                                }
                                                                if (fragment.mSavedFragmentState != null) {
                                                                    fragment.mSavedFragmentState.setClassLoader(this.mHost.getContext().getClassLoader());
                                                                    fragment.mSavedViewState = (SparseArray<Parcelable>)fragment.mSavedFragmentState.getSparseParcelableArray("android:view_state");
                                                                    fragment.mTarget = this.getFragment(fragment.mSavedFragmentState, "android:target_state");
                                                                    if (fragment.mTarget != null) {
                                                                        fragment.mTargetRequestCode = fragment.mSavedFragmentState.getInt("android:target_req_state", 0);
                                                                    }
                                                                    if (!(fragment.mUserVisibleHint = fragment.mSavedFragmentState.getBoolean("android:user_visible_hint", true))) {
                                                                        fragment.mDeferStart = true;
                                                                        if (mState > 3) {
                                                                            mState = 3;
                                                                        }
                                                                    }
                                                                }
                                                                fragment.mHost = this.mHost;
                                                                fragment.mParentFragment = this.mParent;
                                                                if (this.mParent != null) {
                                                                    mFragmentManager = this.mParent.mChildFragmentManager;
                                                                }
                                                                else {
                                                                    mFragmentManager = this.mHost.getFragmentManagerImpl();
                                                                }
                                                                fragment.mFragmentManager = mFragmentManager;
                                                                this.dispatchOnFragmentPreAttached(fragment, this.mHost.getContext(), false);
                                                                fragment.mCalled = false;
                                                                fragment.onAttach(this.mHost.getContext());
                                                                if (!fragment.mCalled) {
                                                                    throw new SuperNotCalledException("Fragment " + fragment + " did not call through to super.onAttach()");
                                                                }
                                                                if (fragment.mParentFragment == null) {
                                                                    this.mHost.onAttachFragment(fragment);
                                                                    break;
                                                                }
                                                                break Label_0984;
                                                            }
                                                            case 1: {
                                                                if (mState <= 1) {
                                                                    break Label_0984;
                                                                }
                                                                if (FragmentManagerImpl.DEBUG) {
                                                                    Log.v("FragmentManager", "moveto ACTIVITY_CREATED: " + fragment);
                                                                }
                                                                if (fragment.mFromLayout) {
                                                                    break Label_0984;
                                                                }
                                                                mContainerId = fragment.mContainerId;
                                                                mContainer = null;
                                                                if (mContainerId == 0) {
                                                                    break Label_1079_Outer;
                                                                }
                                                                if (fragment.mContainerId == -1) {
                                                                    this.throwException(new IllegalArgumentException("Cannot create fragment " + fragment + " for a container view with no id"));
                                                                }
                                                                mContainer = (ViewGroup)this.mContainer.onFindViewById(fragment.mContainerId);
                                                                if (mContainer == null && !fragment.mRestored) {
                                                                    break Label_1079_Outer;
                                                                }
                                                                break Label_1079_Outer;
                                                            }
                                                            case 2: {
                                                                break Label_0984;
                                                                Label_0902_Outer:Label_0978_Outer:
                                                                while (true) {
                                                                    Label_1218: {
                                                                        while (true) {
                                                                        Label_1212:
                                                                            while (true) {
                                                                            Label_1198:
                                                                                while (true) {
                                                                                    try {
                                                                                        resourceName = fragment.getResources().getResourceName(fragment.mContainerId);
                                                                                        this.throwException(new IllegalArgumentException("No view found for id 0x" + Integer.toHexString(fragment.mContainerId) + " (" + resourceName + ") for fragment " + fragment));
                                                                                        fragment.mContainer = mContainer;
                                                                                        fragment.mView = fragment.performCreateView(fragment.getLayoutInflater(fragment.mSavedFragmentState), mContainer, fragment.mSavedFragmentState);
                                                                                        if (fragment.mView == null) {
                                                                                            break Label_1218;
                                                                                        }
                                                                                        fragment.mInnerView = fragment.mView;
                                                                                        if (Build$VERSION.SDK_INT < 11) {
                                                                                            break Label_1198;
                                                                                        }
                                                                                        ViewCompat.setSaveFromParentEnabled(fragment.mView, false);
                                                                                        if (mContainer != null) {
                                                                                            mContainer.addView(fragment.mView);
                                                                                        }
                                                                                        if (fragment.mHidden) {
                                                                                            fragment.mView.setVisibility(8);
                                                                                        }
                                                                                        fragment.onViewCreated(fragment.mView, fragment.mSavedFragmentState);
                                                                                        this.dispatchOnFragmentViewCreated(fragment, fragment.mView, fragment.mSavedFragmentState, false);
                                                                                        if (fragment.mView.getVisibility() != 0 || fragment.mContainer == null) {
                                                                                            break Label_1212;
                                                                                        }
                                                                                        mIsNewlyAdded = true;
                                                                                        fragment.mIsNewlyAdded = mIsNewlyAdded;
                                                                                        fragment.performActivityCreated(fragment.mSavedFragmentState);
                                                                                        this.dispatchOnFragmentActivityCreated(fragment, fragment.mSavedFragmentState, false);
                                                                                        if (fragment.mView != null) {
                                                                                            fragment.restoreViewState(fragment.mSavedFragmentState);
                                                                                        }
                                                                                        fragment.mSavedFragmentState = null;
                                                                                        if (mState > 2) {
                                                                                            fragment.mState = 3;
                                                                                        }
                                                                                        if (mState > 3) {
                                                                                            if (FragmentManagerImpl.DEBUG) {
                                                                                                Log.v("FragmentManager", "moveto STARTED: " + fragment);
                                                                                            }
                                                                                            fragment.performStart();
                                                                                            this.dispatchOnFragmentStarted(fragment, false);
                                                                                        }
                                                                                        if (mState > 4) {
                                                                                            if (FragmentManagerImpl.DEBUG) {
                                                                                                Log.v("FragmentManager", "moveto RESUMED: " + fragment);
                                                                                            }
                                                                                            fragment.performResume();
                                                                                            this.dispatchOnFragmentResumed(fragment, false);
                                                                                            fragment.mSavedFragmentState = null;
                                                                                            fragment.mSavedViewState = null;
                                                                                        }
                                                                                        break Label_0152;
                                                                                        fragment.mView = (View)NoSaveStateFrameLayout.wrap(fragment.mView);
                                                                                        break Label_0595;
                                                                                        fragment.mParentFragment.onAttachFragment(fragment);
                                                                                        break Label_0984;
                                                                                        fragment.mInnerView = null;
                                                                                        continue Label_0637;
                                                                                        fragment.restoreChildFragmentState(fragment.mSavedFragmentState);
                                                                                        fragment.mState = 1;
                                                                                        break Label_0531;
                                                                                    }
                                                                                    catch (Resources$NotFoundException ex) {
                                                                                        resourceName = "unknown";
                                                                                        continue Label_0902_Outer;
                                                                                    }
                                                                                    break;
                                                                                }
                                                                                fragment.mView = (View)NoSaveStateFrameLayout.wrap(fragment.mView);
                                                                                continue Label_0978_Outer;
                                                                            }
                                                                            mIsNewlyAdded = false;
                                                                            continue Label_1032_Outer;
                                                                        }
                                                                    }
                                                                    fragment.mInnerView = null;
                                                                    continue Label_0984;
                                                                }
                                                                break;
                                                            }
                                                            case 3: {
                                                                continue Label_1079_Outer;
                                                            }
                                                            case 4: {
                                                                continue Label_1166_Outer;
                                                            }
                                                        }
                                                        break;
                                                    }
                                                    break;
                                                }
                                                break;
                                            }
                                            this.dispatchOnFragmentAttached(fragment, this.mHost.getContext(), false);
                                            if (fragment.mRetaining) {
                                                continue;
                                            }
                                            break;
                                        }
                                        fragment.performCreate(fragment.mSavedFragmentState);
                                        this.dispatchOnFragmentCreated(fragment, fragment.mSavedFragmentState, false);
                                    }
                                    fragment.mRetaining = false;
                                    if (!fragment.mFromLayout) {
                                        continue Label_0637;
                                    }
                                    fragment.mView = fragment.performCreateView(fragment.getLayoutInflater(fragment.mSavedFragmentState), null, fragment.mSavedFragmentState);
                                    if (fragment.mView == null) {
                                        continue Label_1150_Outer;
                                    }
                                    break;
                                }
                                fragment.mInnerView = fragment.mView;
                                if (Build$VERSION.SDK_INT < 11) {
                                    continue Label_1180_Outer;
                                }
                                break;
                            }
                            ViewCompat.setSaveFromParentEnabled(fragment.mView, false);
                        }
                        if (fragment.mHidden) {
                            fragment.mView.setVisibility(8);
                        }
                        fragment.onViewCreated(fragment.mView, fragment.mSavedFragmentState);
                        this.dispatchOnFragmentViewCreated(fragment, fragment.mView, fragment.mSavedFragmentState, false);
                        continue Label_0637;
                    }
                }
            }
            else {
                if (fragment.mState <= mState) {
                    break Label_0152;
                }
                switch (fragment.mState) {
                    default: {
                        break Label_0152;
                    }
                    case 3: {
                        if (mState < 3) {
                            if (FragmentManagerImpl.DEBUG) {
                                Log.v("FragmentManager", "movefrom STOPPED: " + fragment);
                            }
                            fragment.performReallyStop();
                        }
                    }
                    case 2: {
                        if (mState < 2) {
                            if (FragmentManagerImpl.DEBUG) {
                                Log.v("FragmentManager", "movefrom ACTIVITY_CREATED: " + fragment);
                            }
                            if (fragment.mView != null && this.mHost.onShouldSaveFragmentState(fragment) && fragment.mSavedViewState == null) {
                                this.saveFragmentViewState(fragment);
                            }
                            fragment.performDestroyView();
                            this.dispatchOnFragmentViewDestroyed(fragment, false);
                            if (fragment.mView != null && fragment.mContainer != null) {
                                final int mCurState = this.mCurState;
                                Animation loadAnimation = null;
                                if (mCurState > 0) {
                                    final boolean mDestroyed = this.mDestroyed;
                                    loadAnimation = null;
                                    if (!mDestroyed) {
                                        final int visibility = fragment.mView.getVisibility();
                                        loadAnimation = null;
                                        if (visibility == 0) {
                                            final float n3 = fcmpl(fragment.mPostponedAlpha, 0.0f);
                                            loadAnimation = null;
                                            if (n3 >= 0) {
                                                loadAnimation = this.loadAnimation(fragment, n, false, n2);
                                            }
                                        }
                                    }
                                }
                                fragment.mPostponedAlpha = 0.0f;
                                if (loadAnimation != null) {
                                    fragment.setAnimatingAway(fragment.mView);
                                    fragment.setStateAfterAnimating(mState);
                                    loadAnimation.setAnimationListener((Animation$AnimationListener)new AnimateOnHWLayerIfNeededListener(fragment.mView, loadAnimation) {
                                        @Override
                                        public void onAnimationEnd(final Animation animation) {
                                            super.onAnimationEnd(animation);
                                            if (fragment.getAnimatingAway() != null) {
                                                fragment.setAnimatingAway(null);
                                                FragmentManagerImpl.this.moveToState(fragment, fragment.getStateAfterAnimating(), 0, 0, false);
                                            }
                                        }
                                    });
                                    fragment.mView.startAnimation(loadAnimation);
                                }
                                fragment.mContainer.removeView(fragment.mView);
                            }
                            fragment.mContainer = null;
                            fragment.mView = null;
                            fragment.mInnerView = null;
                        }
                    }
                    case 1: {
                        if (mState >= 1) {
                            break Label_0152;
                        }
                        if (this.mDestroyed && fragment.getAnimatingAway() != null) {
                            final View animatingAway = fragment.getAnimatingAway();
                            fragment.setAnimatingAway(null);
                            animatingAway.clearAnimation();
                        }
                        if (fragment.getAnimatingAway() != null) {
                            fragment.setStateAfterAnimating(mState);
                            mState = 1;
                            break Label_0152;
                        }
                        if (FragmentManagerImpl.DEBUG) {
                            Log.v("FragmentManager", "movefrom CREATED: " + fragment);
                        }
                        if (!fragment.mRetaining) {
                            fragment.performDestroy();
                            this.dispatchOnFragmentDestroyed(fragment, false);
                        }
                        else {
                            fragment.mState = 0;
                        }
                        fragment.performDetach();
                        this.dispatchOnFragmentDetached(fragment, false);
                        if (b) {
                            break Label_0152;
                        }
                        if (!fragment.mRetaining) {
                            this.makeInactive(fragment);
                            break Label_0152;
                        }
                        fragment.mHost = null;
                        fragment.mParentFragment = null;
                        fragment.mFragmentManager = null;
                        break Label_0152;
                    }
                    case 5: {
                        if (mState < 5) {
                            if (FragmentManagerImpl.DEBUG) {
                                Log.v("FragmentManager", "movefrom RESUMED: " + fragment);
                            }
                            fragment.performPause();
                            this.dispatchOnFragmentPaused(fragment, false);
                        }
                    }
                    case 4: {
                        if (mState < 4) {
                            if (FragmentManagerImpl.DEBUG) {
                                Log.v("FragmentManager", "movefrom STARTED: " + fragment);
                            }
                            fragment.performStop();
                            this.dispatchOnFragmentStopped(fragment, false);
                        }
                    }
                }
            }
            return;
        }
        if (fragment.mState != mState) {
            Log.w("FragmentManager", "moveToState: Fragment state for " + fragment + " not updated inline; " + "expected state " + mState + " found " + fragment.mState);
            fragment.mState = mState;
        }
    }
    
    public void noteStateNotSaved() {
        this.mStateSaved = false;
    }
    
    @Override
    public View onCreateView(final View view, final String s, final Context context, final AttributeSet set) {
        if ("fragment".equals(s)) {
            String s2 = set.getAttributeValue((String)null, "class");
            final TypedArray obtainStyledAttributes = context.obtainStyledAttributes(set, FragmentTag.Fragment);
            if (s2 == null) {
                s2 = obtainStyledAttributes.getString(0);
            }
            final int resourceId = obtainStyledAttributes.getResourceId(1, -1);
            final String string = obtainStyledAttributes.getString(2);
            obtainStyledAttributes.recycle();
            if (Fragment.isSupportFragmentClass(this.mHost.getContext(), s2)) {
                int id;
                if (view != null) {
                    id = view.getId();
                }
                else {
                    id = 0;
                }
                if (id == -1 && resourceId == -1 && string == null) {
                    throw new IllegalArgumentException(set.getPositionDescription() + ": Must specify unique android:id, android:tag, or have a parent with an id for " + s2);
                }
                Fragment fragment;
                if (resourceId != -1) {
                    fragment = this.findFragmentById(resourceId);
                }
                else {
                    fragment = null;
                }
                if (fragment == null && string != null) {
                    fragment = this.findFragmentByTag(string);
                }
                if (fragment == null && id != -1) {
                    fragment = this.findFragmentById(id);
                }
                if (FragmentManagerImpl.DEBUG) {
                    Log.v("FragmentManager", "onCreateView: id=0x" + Integer.toHexString(resourceId) + " fname=" + s2 + " existing=" + fragment);
                }
                if (fragment == null) {
                    fragment = Fragment.instantiate(context, s2);
                    fragment.mFromLayout = true;
                    int mFragmentId;
                    if (resourceId != 0) {
                        mFragmentId = resourceId;
                    }
                    else {
                        mFragmentId = id;
                    }
                    fragment.mFragmentId = mFragmentId;
                    fragment.mContainerId = id;
                    fragment.mTag = string;
                    fragment.mInLayout = true;
                    fragment.mFragmentManager = this;
                    fragment.mHost = this.mHost;
                    fragment.onInflate(this.mHost.getContext(), set, fragment.mSavedFragmentState);
                    this.addFragment(fragment, true);
                }
                else {
                    if (fragment.mInLayout) {
                        throw new IllegalArgumentException(set.getPositionDescription() + ": Duplicate id 0x" + Integer.toHexString(resourceId) + ", tag " + string + ", or parent id 0x" + Integer.toHexString(id) + " with another fragment for " + s2);
                    }
                    fragment.mInLayout = true;
                    fragment.mHost = this.mHost;
                    if (!fragment.mRetaining) {
                        fragment.onInflate(this.mHost.getContext(), set, fragment.mSavedFragmentState);
                    }
                }
                if (this.mCurState < 1 && fragment.mFromLayout) {
                    this.moveToState(fragment, 1, 0, 0, false);
                }
                else {
                    this.moveToState(fragment);
                }
                if (fragment.mView == null) {
                    throw new IllegalStateException("Fragment " + s2 + " did not create a view.");
                }
                if (resourceId != 0) {
                    fragment.mView.setId(resourceId);
                }
                if (fragment.mView.getTag() == null) {
                    fragment.mView.setTag((Object)string);
                }
                return fragment.mView;
            }
        }
        return null;
    }
    
    public void performPendingDeferredStart(final Fragment fragment) {
        if (fragment.mDeferStart) {
            if (!this.mExecutingActions) {
                fragment.mDeferStart = false;
                this.moveToState(fragment, this.mCurState, 0, 0, false);
                return;
            }
            this.mHavePendingDeferredStart = true;
        }
    }
    
    @Override
    public void popBackStack() {
        this.enqueueAction((OpGenerator)new PopBackStackState(null, -1, 0), false);
    }
    
    @Override
    public void popBackStack(final int n, final int n2) {
        if (n < 0) {
            throw new IllegalArgumentException("Bad id: " + n);
        }
        this.enqueueAction((OpGenerator)new PopBackStackState(null, n, n2), false);
    }
    
    @Override
    public void popBackStack(final String s, final int n) {
        this.enqueueAction((OpGenerator)new PopBackStackState(s, -1, n), false);
    }
    
    @Override
    public boolean popBackStackImmediate() {
        this.checkStateLoss();
        return this.popBackStackImmediate(null, -1, 0);
    }
    
    @Override
    public boolean popBackStackImmediate(final int n, final int n2) {
        this.checkStateLoss();
        this.execPendingActions();
        if (n < 0) {
            throw new IllegalArgumentException("Bad id: " + n);
        }
        return this.popBackStackImmediate(null, n, n2);
    }
    
    @Override
    public boolean popBackStackImmediate(final String s, final int n) {
        this.checkStateLoss();
        return this.popBackStackImmediate(s, -1, n);
    }
    
    boolean popBackStackState(final ArrayList<BackStackRecord> list, final ArrayList<Boolean> list2, final String s, final int n, final int n2) {
        if (this.mBackStack != null) {
            if (s == null && n < 0 && (n2 & 0x1) == 0x0) {
                final int n3 = -1 + this.mBackStack.size();
                if (n3 < 0) {
                    return false;
                }
                list.add(this.mBackStack.remove(n3));
                list2.add(true);
            }
            else {
                int i = -1;
                if (s != null || n >= 0) {
                    for (i = -1 + this.mBackStack.size(); i >= 0; --i) {
                        final BackStackRecord backStackRecord = this.mBackStack.get(i);
                        if ((s != null && s.equals(backStackRecord.getName())) || (n >= 0 && n == backStackRecord.mIndex)) {
                            break;
                        }
                    }
                    if (i < 0) {
                        return false;
                    }
                    if ((n2 & 0x1) != 0x0) {
                        --i;
                        while (i >= 0) {
                            final BackStackRecord backStackRecord2 = this.mBackStack.get(i);
                            if ((s == null || !s.equals(backStackRecord2.getName())) && (n < 0 || n != backStackRecord2.mIndex)) {
                                break;
                            }
                            --i;
                        }
                    }
                }
                if (i == -1 + this.mBackStack.size()) {
                    return false;
                }
                for (int j = -1 + this.mBackStack.size(); j > i; --j) {
                    list.add(this.mBackStack.remove(j));
                    list2.add(true);
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public void putFragment(final Bundle bundle, final String s, final Fragment fragment) {
        if (fragment.mIndex < 0) {
            this.throwException(new IllegalStateException("Fragment " + fragment + " is not currently in the FragmentManager"));
        }
        bundle.putInt(s, fragment.mIndex);
    }
    
    @Override
    public void registerFragmentLifecycleCallbacks(final FragmentLifecycleCallbacks fragmentLifecycleCallbacks, final boolean b) {
        if (this.mLifecycleCallbacks == null) {
            this.mLifecycleCallbacks = new CopyOnWriteArrayList<Pair<FragmentLifecycleCallbacks, Boolean>>();
        }
        this.mLifecycleCallbacks.add(new Pair<FragmentLifecycleCallbacks, Boolean>(fragmentLifecycleCallbacks, b));
    }
    
    public void removeFragment(final Fragment fragment) {
        if (FragmentManagerImpl.DEBUG) {
            Log.v("FragmentManager", "remove: " + fragment + " nesting=" + fragment.mBackStackNesting);
        }
        boolean b;
        if (!fragment.isInBackStack()) {
            b = true;
        }
        else {
            b = false;
        }
        if (!fragment.mDetached || b) {
            if (this.mAdded != null) {
                this.mAdded.remove(fragment);
            }
            if (fragment.mHasMenu && fragment.mMenuVisible) {
                this.mNeedMenuInvalidate = true;
            }
            fragment.mAdded = false;
            fragment.mRemoving = true;
        }
    }
    
    @Override
    public void removeOnBackStackChangedListener(final OnBackStackChangedListener onBackStackChangedListener) {
        if (this.mBackStackChangeListeners != null) {
            this.mBackStackChangeListeners.remove(onBackStackChangedListener);
        }
    }
    
    void reportBackStackChanged() {
        if (this.mBackStackChangeListeners != null) {
            for (int i = 0; i < this.mBackStackChangeListeners.size(); ++i) {
                this.mBackStackChangeListeners.get(i).onBackStackChanged();
            }
        }
    }
    
    void restoreAllState(final Parcelable parcelable, final FragmentManagerNonConfig fragmentManagerNonConfig) {
        if (parcelable != null) {
            final FragmentManagerState fragmentManagerState = (FragmentManagerState)parcelable;
            if (fragmentManagerState.mActive != null) {
                List<FragmentManagerNonConfig> childNonConfigs = null;
                if (fragmentManagerNonConfig != null) {
                    final List<Fragment> fragments = fragmentManagerNonConfig.getFragments();
                    childNonConfigs = fragmentManagerNonConfig.getChildNonConfigs();
                    int size;
                    if (fragments != null) {
                        size = fragments.size();
                    }
                    else {
                        size = 0;
                    }
                    for (int i = 0; i < size; ++i) {
                        final Fragment mInstance = fragments.get(i);
                        if (FragmentManagerImpl.DEBUG) {
                            Log.v("FragmentManager", "restoreAllState: re-attaching retained " + mInstance);
                        }
                        final FragmentState fragmentState = fragmentManagerState.mActive[mInstance.mIndex];
                        fragmentState.mInstance = mInstance;
                        mInstance.mSavedViewState = null;
                        mInstance.mBackStackNesting = 0;
                        mInstance.mInLayout = false;
                        mInstance.mAdded = false;
                        mInstance.mTarget = null;
                        if (fragmentState.mSavedFragmentState != null) {
                            fragmentState.mSavedFragmentState.setClassLoader(this.mHost.getContext().getClassLoader());
                            mInstance.mSavedViewState = (SparseArray<Parcelable>)fragmentState.mSavedFragmentState.getSparseParcelableArray("android:view_state");
                            mInstance.mSavedFragmentState = fragmentState.mSavedFragmentState;
                        }
                    }
                }
                this.mActive = new ArrayList<Fragment>(fragmentManagerState.mActive.length);
                if (this.mAvailIndices != null) {
                    this.mAvailIndices.clear();
                }
                for (int j = 0; j < fragmentManagerState.mActive.length; ++j) {
                    final FragmentState fragmentState2 = fragmentManagerState.mActive[j];
                    if (fragmentState2 != null) {
                        FragmentManagerNonConfig fragmentManagerNonConfig2 = null;
                        if (childNonConfigs != null) {
                            final int size2 = childNonConfigs.size();
                            fragmentManagerNonConfig2 = null;
                            if (j < size2) {
                                fragmentManagerNonConfig2 = childNonConfigs.get(j);
                            }
                        }
                        final Fragment instantiate = fragmentState2.instantiate(this.mHost, this.mParent, fragmentManagerNonConfig2);
                        if (FragmentManagerImpl.DEBUG) {
                            Log.v("FragmentManager", "restoreAllState: active #" + j + ": " + instantiate);
                        }
                        this.mActive.add(instantiate);
                        fragmentState2.mInstance = null;
                    }
                    else {
                        this.mActive.add(null);
                        if (this.mAvailIndices == null) {
                            this.mAvailIndices = new ArrayList<Integer>();
                        }
                        if (FragmentManagerImpl.DEBUG) {
                            Log.v("FragmentManager", "restoreAllState: avail #" + j);
                        }
                        this.mAvailIndices.add(j);
                    }
                }
                if (fragmentManagerNonConfig != null) {
                    final List<Fragment> fragments2 = fragmentManagerNonConfig.getFragments();
                    int size3;
                    if (fragments2 != null) {
                        size3 = fragments2.size();
                    }
                    else {
                        size3 = 0;
                    }
                    for (int k = 0; k < size3; ++k) {
                        final Fragment fragment = fragments2.get(k);
                        if (fragment.mTargetIndex >= 0) {
                            if (fragment.mTargetIndex < this.mActive.size()) {
                                fragment.mTarget = this.mActive.get(fragment.mTargetIndex);
                            }
                            else {
                                Log.w("FragmentManager", "Re-attaching retained fragment " + fragment + " target no longer exists: " + fragment.mTargetIndex);
                                fragment.mTarget = null;
                            }
                        }
                    }
                }
                if (fragmentManagerState.mAdded != null) {
                    this.mAdded = new ArrayList<Fragment>(fragmentManagerState.mAdded.length);
                    for (int l = 0; l < fragmentManagerState.mAdded.length; ++l) {
                        final Fragment fragment2 = this.mActive.get(fragmentManagerState.mAdded[l]);
                        if (fragment2 == null) {
                            this.throwException(new IllegalStateException("No instantiated fragment for index #" + fragmentManagerState.mAdded[l]));
                        }
                        fragment2.mAdded = true;
                        if (FragmentManagerImpl.DEBUG) {
                            Log.v("FragmentManager", "restoreAllState: added #" + l + ": " + fragment2);
                        }
                        if (this.mAdded.contains(fragment2)) {
                            throw new IllegalStateException("Already added!");
                        }
                        this.mAdded.add(fragment2);
                    }
                }
                else {
                    this.mAdded = null;
                }
                if (fragmentManagerState.mBackStack == null) {
                    this.mBackStack = null;
                    return;
                }
                this.mBackStack = new ArrayList<BackStackRecord>(fragmentManagerState.mBackStack.length);
                for (int n = 0; n < fragmentManagerState.mBackStack.length; ++n) {
                    final BackStackRecord instantiate2 = fragmentManagerState.mBackStack[n].instantiate(this);
                    if (FragmentManagerImpl.DEBUG) {
                        Log.v("FragmentManager", "restoreAllState: back stack #" + n + " (index " + instantiate2.mIndex + "): " + instantiate2);
                        final PrintWriter printWriter = new PrintWriter(new LogWriter("FragmentManager"));
                        instantiate2.dump("  ", printWriter, false);
                        printWriter.close();
                    }
                    this.mBackStack.add(instantiate2);
                    if (instantiate2.mIndex >= 0) {
                        this.setBackStackIndex(instantiate2.mIndex, instantiate2);
                    }
                }
            }
        }
    }
    
    FragmentManagerNonConfig retainNonConfig() {
        final ArrayList<Fragment> mActive = this.mActive;
        ArrayList<FragmentManagerNonConfig> list = null;
        ArrayList<Fragment> list2 = null;
        if (mActive != null) {
            for (int i = 0; i < this.mActive.size(); ++i) {
                final Fragment fragment = this.mActive.get(i);
                if (fragment != null) {
                    if (fragment.mRetainInstance) {
                        if (list2 == null) {
                            list2 = new ArrayList<Fragment>();
                        }
                        list2.add(fragment);
                        fragment.mRetaining = true;
                        int mIndex;
                        if (fragment.mTarget != null) {
                            mIndex = fragment.mTarget.mIndex;
                        }
                        else {
                            mIndex = -1;
                        }
                        fragment.mTargetIndex = mIndex;
                        if (FragmentManagerImpl.DEBUG) {
                            Log.v("FragmentManager", "retainNonConfig: keeping retained " + fragment);
                        }
                    }
                    final FragmentManagerImpl mChildFragmentManager = fragment.mChildFragmentManager;
                    boolean b = false;
                    if (mChildFragmentManager != null) {
                        final FragmentManagerNonConfig retainNonConfig = fragment.mChildFragmentManager.retainNonConfig();
                        b = false;
                        if (retainNonConfig != null) {
                            if (list == null) {
                                list = new ArrayList<FragmentManagerNonConfig>();
                                for (int j = 0; j < i; ++j) {
                                    list.add(null);
                                }
                            }
                            list.add(retainNonConfig);
                            b = true;
                        }
                    }
                    if (list != null && !b) {
                        list.add(null);
                    }
                }
            }
        }
        if (list2 == null && list == null) {
            return null;
        }
        return new FragmentManagerNonConfig(list2, list);
    }
    
    Parcelable saveAllState() {
        this.forcePostponedTransactions();
        this.endAnimatingAwayFragments();
        this.execPendingActions();
        if (FragmentManagerImpl.HONEYCOMB) {
            this.mStateSaved = true;
        }
        if (this.mActive != null && this.mActive.size() > 0) {
            final int size = this.mActive.size();
            final FragmentState[] mActive = new FragmentState[size];
            boolean b = false;
            for (int i = 0; i < size; ++i) {
                final Fragment fragment = this.mActive.get(i);
                if (fragment != null) {
                    if (fragment.mIndex < 0) {
                        this.throwException(new IllegalStateException("Failure saving state: active " + fragment + " has cleared index: " + fragment.mIndex));
                    }
                    b = true;
                    final FragmentState fragmentState = new FragmentState(fragment);
                    mActive[i] = fragmentState;
                    if (fragment.mState > 0 && fragmentState.mSavedFragmentState == null) {
                        fragmentState.mSavedFragmentState = this.saveFragmentBasicState(fragment);
                        if (fragment.mTarget != null) {
                            if (fragment.mTarget.mIndex < 0) {
                                this.throwException(new IllegalStateException("Failure saving state: " + fragment + " has target not in fragment manager: " + fragment.mTarget));
                            }
                            if (fragmentState.mSavedFragmentState == null) {
                                fragmentState.mSavedFragmentState = new Bundle();
                            }
                            this.putFragment(fragmentState.mSavedFragmentState, "android:target_state", fragment.mTarget);
                            if (fragment.mTargetRequestCode != 0) {
                                fragmentState.mSavedFragmentState.putInt("android:target_req_state", fragment.mTargetRequestCode);
                            }
                        }
                    }
                    else {
                        fragmentState.mSavedFragmentState = fragment.mSavedFragmentState;
                    }
                    if (FragmentManagerImpl.DEBUG) {
                        Log.v("FragmentManager", "Saved state of " + fragment + ": " + fragmentState.mSavedFragmentState);
                    }
                }
            }
            if (b) {
                final ArrayList<Fragment> mAdded = this.mAdded;
                int[] mAdded2 = null;
                if (mAdded != null) {
                    final int size2 = this.mAdded.size();
                    mAdded2 = null;
                    if (size2 > 0) {
                        mAdded2 = new int[size2];
                        for (int j = 0; j < size2; ++j) {
                            mAdded2[j] = this.mAdded.get(j).mIndex;
                            if (mAdded2[j] < 0) {
                                this.throwException(new IllegalStateException("Failure saving state: active " + this.mAdded.get(j) + " has cleared index: " + mAdded2[j]));
                            }
                            if (FragmentManagerImpl.DEBUG) {
                                Log.v("FragmentManager", "saveAllState: adding fragment #" + j + ": " + this.mAdded.get(j));
                            }
                        }
                    }
                }
                final ArrayList<BackStackRecord> mBackStack = this.mBackStack;
                BackStackState[] mBackStack2 = null;
                if (mBackStack != null) {
                    final int size3 = this.mBackStack.size();
                    mBackStack2 = null;
                    if (size3 > 0) {
                        mBackStack2 = new BackStackState[size3];
                        for (int k = 0; k < size3; ++k) {
                            mBackStack2[k] = new BackStackState(this.mBackStack.get(k));
                            if (FragmentManagerImpl.DEBUG) {
                                Log.v("FragmentManager", "saveAllState: adding back stack #" + k + ": " + this.mBackStack.get(k));
                            }
                        }
                    }
                }
                final FragmentManagerState fragmentManagerState = new FragmentManagerState();
                fragmentManagerState.mActive = mActive;
                fragmentManagerState.mAdded = mAdded2;
                fragmentManagerState.mBackStack = mBackStack2;
                return (Parcelable)fragmentManagerState;
            }
            if (FragmentManagerImpl.DEBUG) {
                Log.v("FragmentManager", "saveAllState: no fragments!");
                return null;
            }
        }
        return null;
    }
    
    Bundle saveFragmentBasicState(final Fragment fragment) {
        if (this.mStateBundle == null) {
            this.mStateBundle = new Bundle();
        }
        fragment.performSaveInstanceState(this.mStateBundle);
        this.dispatchOnFragmentSaveInstanceState(fragment, this.mStateBundle, false);
        final boolean empty = this.mStateBundle.isEmpty();
        Bundle mStateBundle = null;
        if (!empty) {
            mStateBundle = this.mStateBundle;
            this.mStateBundle = null;
        }
        if (fragment.mView != null) {
            this.saveFragmentViewState(fragment);
        }
        if (fragment.mSavedViewState != null) {
            if (mStateBundle == null) {
                mStateBundle = new Bundle();
            }
            mStateBundle.putSparseParcelableArray("android:view_state", (SparseArray)fragment.mSavedViewState);
        }
        if (!fragment.mUserVisibleHint) {
            if (mStateBundle == null) {
                mStateBundle = new Bundle();
            }
            mStateBundle.putBoolean("android:user_visible_hint", fragment.mUserVisibleHint);
        }
        return mStateBundle;
    }
    
    @Override
    public Fragment.SavedState saveFragmentInstanceState(final Fragment fragment) {
        if (fragment.mIndex < 0) {
            this.throwException(new IllegalStateException("Fragment " + fragment + " is not currently in the FragmentManager"));
        }
        final int mState = fragment.mState;
        Fragment.SavedState savedState = null;
        if (mState > 0) {
            final Bundle saveFragmentBasicState = this.saveFragmentBasicState(fragment);
            savedState = null;
            if (saveFragmentBasicState != null) {
                savedState = new Fragment.SavedState(saveFragmentBasicState);
            }
        }
        return savedState;
    }
    
    void saveFragmentViewState(final Fragment fragment) {
        if (fragment.mInnerView != null) {
            if (this.mStateArray == null) {
                this.mStateArray = (SparseArray<Parcelable>)new SparseArray();
            }
            else {
                this.mStateArray.clear();
            }
            fragment.mInnerView.saveHierarchyState((SparseArray)this.mStateArray);
            if (this.mStateArray.size() > 0) {
                fragment.mSavedViewState = this.mStateArray;
                this.mStateArray = null;
            }
        }
    }
    
    public void setBackStackIndex(final int n, final BackStackRecord backStackRecord) {
        synchronized (this) {
            if (this.mBackStackIndices == null) {
                this.mBackStackIndices = new ArrayList<BackStackRecord>();
            }
            int i = this.mBackStackIndices.size();
            if (n < i) {
                if (FragmentManagerImpl.DEBUG) {
                    Log.v("FragmentManager", "Setting back stack index " + n + " to " + backStackRecord);
                }
                this.mBackStackIndices.set(n, backStackRecord);
            }
            else {
                while (i < n) {
                    this.mBackStackIndices.add(null);
                    if (this.mAvailBackStackIndices == null) {
                        this.mAvailBackStackIndices = new ArrayList<Integer>();
                    }
                    if (FragmentManagerImpl.DEBUG) {
                        Log.v("FragmentManager", "Adding available back stack index " + i);
                    }
                    this.mAvailBackStackIndices.add(i);
                    ++i;
                }
                if (FragmentManagerImpl.DEBUG) {
                    Log.v("FragmentManager", "Adding back stack index " + n + " with " + backStackRecord);
                }
                this.mBackStackIndices.add(backStackRecord);
            }
        }
    }
    
    public void showFragment(final Fragment fragment) {
        if (FragmentManagerImpl.DEBUG) {
            Log.v("FragmentManager", "show: " + fragment);
        }
        if (fragment.mHidden) {
            fragment.mHidden = false;
            final boolean mHiddenChanged = fragment.mHiddenChanged;
            boolean mHiddenChanged2 = false;
            if (!mHiddenChanged) {
                mHiddenChanged2 = true;
            }
            fragment.mHiddenChanged = mHiddenChanged2;
        }
    }
    
    void startPendingDeferredFragments() {
        if (this.mActive != null) {
            for (int i = 0; i < this.mActive.size(); ++i) {
                final Fragment fragment = this.mActive.get(i);
                if (fragment != null) {
                    this.performPendingDeferredStart(fragment);
                }
            }
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(128);
        sb.append("FragmentManager{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(" in ");
        if (this.mParent != null) {
            DebugUtils.buildShortClassTag(this.mParent, sb);
        }
        else {
            DebugUtils.buildShortClassTag(this.mHost, sb);
        }
        sb.append("}}");
        return sb.toString();
    }
    
    @Override
    public void unregisterFragmentLifecycleCallbacks(final FragmentLifecycleCallbacks fragmentLifecycleCallbacks) {
        if (this.mLifecycleCallbacks == null) {
            return;
        }
        while (true) {
            final CopyOnWriteArrayList<Pair<FragmentLifecycleCallbacks, Boolean>> mLifecycleCallbacks = this.mLifecycleCallbacks;
            // monitorenter(mLifecycleCallbacks)
            int n = 0;
            while (true) {
                Label_0069: {
                    try {
                        final int size = this.mLifecycleCallbacks.size();
                        if (n < size) {
                            if (this.mLifecycleCallbacks.get(n).first != fragmentLifecycleCallbacks) {
                                break Label_0069;
                            }
                            this.mLifecycleCallbacks.remove(n);
                        }
                        return;
                    }
                    finally {
                    }
                    // monitorexit(mLifecycleCallbacks)
                }
                ++n;
                continue;
            }
        }
    }
    
    static class AnimateOnHWLayerIfNeededListener implements Animation$AnimationListener
    {
        private Animation$AnimationListener mOriginalListener;
        private boolean mShouldRunOnHWLayer;
        View mView;
        
        public AnimateOnHWLayerIfNeededListener(final View mView, final Animation animation) {
            if (mView == null || animation == null) {
                return;
            }
            this.mView = mView;
        }
        
        public AnimateOnHWLayerIfNeededListener(final View mView, final Animation animation, final Animation$AnimationListener mOriginalListener) {
            if (mView == null || animation == null) {
                return;
            }
            this.mOriginalListener = mOriginalListener;
            this.mView = mView;
            this.mShouldRunOnHWLayer = true;
        }
        
        @CallSuper
        public void onAnimationEnd(final Animation animation) {
            if (this.mView != null && this.mShouldRunOnHWLayer) {
                if (ViewCompat.isAttachedToWindow(this.mView) || BuildCompat.isAtLeastN()) {
                    this.mView.post((Runnable)new Runnable() {
                        @Override
                        public void run() {
                            ViewCompat.setLayerType(AnimateOnHWLayerIfNeededListener.this.mView, 0, null);
                        }
                    });
                }
                else {
                    ViewCompat.setLayerType(this.mView, 0, null);
                }
            }
            if (this.mOriginalListener != null) {
                this.mOriginalListener.onAnimationEnd(animation);
            }
        }
        
        public void onAnimationRepeat(final Animation animation) {
            if (this.mOriginalListener != null) {
                this.mOriginalListener.onAnimationRepeat(animation);
            }
        }
        
        @CallSuper
        public void onAnimationStart(final Animation animation) {
            if (this.mOriginalListener != null) {
                this.mOriginalListener.onAnimationStart(animation);
            }
        }
    }
    
    static class FragmentTag
    {
        public static final int[] Fragment;
        public static final int Fragment_id = 1;
        public static final int Fragment_name = 0;
        public static final int Fragment_tag = 2;
        
        static {
            Fragment = new int[] { 16842755, 16842960, 16842961 };
        }
    }
    
    interface OpGenerator
    {
        boolean generateOps(final ArrayList<BackStackRecord> p0, final ArrayList<Boolean> p1);
    }
    
    private class PopBackStackState implements OpGenerator
    {
        final int mFlags;
        final int mId;
        final String mName;
        
        PopBackStackState(final String mName, final int mId, final int mFlags) {
            this.mName = mName;
            this.mId = mId;
            this.mFlags = mFlags;
        }
        
        @Override
        public boolean generateOps(final ArrayList<BackStackRecord> list, final ArrayList<Boolean> list2) {
            return FragmentManagerImpl.this.popBackStackState(list, list2, this.mName, this.mId, this.mFlags);
        }
    }
    
    static class StartEnterTransitionListener implements OnStartEnterTransitionListener
    {
        private final boolean mIsBack;
        private int mNumPostponed;
        private final BackStackRecord mRecord;
        
        StartEnterTransitionListener(final BackStackRecord mRecord, final boolean mIsBack) {
            this.mIsBack = mIsBack;
            this.mRecord = mRecord;
        }
        
        public void cancelTransaction() {
            this.mRecord.mManager.completeExecute(this.mRecord, this.mIsBack, false, false);
        }
        
        public void completeTransaction() {
            boolean b;
            if (this.mNumPostponed > 0) {
                b = true;
            }
            else {
                b = false;
            }
            final FragmentManagerImpl mManager = this.mRecord.mManager;
            for (int size = mManager.mAdded.size(), i = 0; i < size; ++i) {
                final Fragment fragment = mManager.mAdded.get(i);
                fragment.setOnStartEnterTransitionListener(null);
                if (b && fragment.isPostponed()) {
                    fragment.startPostponedEnterTransition();
                }
            }
            final FragmentManagerImpl mManager2 = this.mRecord.mManager;
            final BackStackRecord mRecord = this.mRecord;
            final boolean mIsBack = this.mIsBack;
            boolean b2 = false;
            if (!b) {
                b2 = true;
            }
            mManager2.completeExecute(mRecord, mIsBack, b2, true);
        }
        
        public boolean isReady() {
            return this.mNumPostponed == 0;
        }
        
        @Override
        public void onStartEnterTransition() {
            --this.mNumPostponed;
            if (this.mNumPostponed != 0) {
                return;
            }
            this.mRecord.mManager.scheduleCommit();
        }
        
        @Override
        public void startListening() {
            ++this.mNumPostponed;
        }
    }
}
