package android.support.v7.view.menu;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.appcompat.R.dimen;
import android.support.v7.appcompat.R.layout;
import android.support.v7.widget.MenuItemHoverListener;
import android.support.v7.widget.MenuPopupWindow;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

final class CascadingMenuPopup
  extends MenuPopup
  implements MenuPresenter, View.OnKeyListener, PopupWindow.OnDismissListener
{
  static final int HORIZ_POSITION_LEFT = 0;
  static final int HORIZ_POSITION_RIGHT = 1;
  static final int SUBMENU_TIMEOUT_MS = 200;
  private View mAnchorView;
  private final Context mContext;
  private int mDropDownGravity = 0;
  private boolean mForceShowIcon;
  private final ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener()
  {
    public void onGlobalLayout()
    {
      if ((CascadingMenuPopup.this.isShowing()) && (CascadingMenuPopup.this.mShowingMenus.size() > 0) && (!((CascadingMenuPopup.CascadingMenuInfo)CascadingMenuPopup.this.mShowingMenus.get(0)).window.isModal()))
      {
        View localView = CascadingMenuPopup.this.mShownAnchorView;
        if ((localView != null) && (localView.isShown())) {
          break label77;
        }
        CascadingMenuPopup.this.dismiss();
      }
      for (;;)
      {
        return;
        label77:
        Iterator localIterator = CascadingMenuPopup.this.mShowingMenus.iterator();
        while (localIterator.hasNext()) {
          ((CascadingMenuPopup.CascadingMenuInfo)localIterator.next()).window.show();
        }
      }
    }
  };
  private boolean mHasXOffset;
  private boolean mHasYOffset;
  private int mLastPosition;
  private final MenuItemHoverListener mMenuItemHoverListener = new MenuItemHoverListener()
  {
    public void onItemHoverEnter(@NonNull final MenuBuilder paramAnonymousMenuBuilder, @NonNull final MenuItem paramAnonymousMenuItem)
    {
      CascadingMenuPopup.this.mSubMenuHoverHandler.removeCallbacksAndMessages(null);
      int i = -1;
      int j = 0;
      int k = CascadingMenuPopup.this.mShowingMenus.size();
      for (;;)
      {
        if (j < k)
        {
          if (paramAnonymousMenuBuilder == ((CascadingMenuPopup.CascadingMenuInfo)CascadingMenuPopup.this.mShowingMenus.get(j)).menu) {
            i = j;
          }
        }
        else
        {
          if (i != -1) {
            break;
          }
          return;
        }
        j++;
      }
      int m = i + 1;
      if (m < CascadingMenuPopup.this.mShowingMenus.size()) {}
      for (final CascadingMenuPopup.CascadingMenuInfo localCascadingMenuInfo = (CascadingMenuPopup.CascadingMenuInfo)CascadingMenuPopup.this.mShowingMenus.get(m);; localCascadingMenuInfo = null)
      {
        Runnable local1 = new Runnable()
        {
          public void run()
          {
            if (localCascadingMenuInfo != null)
            {
              CascadingMenuPopup.this.mShouldCloseImmediately = true;
              localCascadingMenuInfo.menu.close(false);
              CascadingMenuPopup.this.mShouldCloseImmediately = false;
            }
            if ((paramAnonymousMenuItem.isEnabled()) && (paramAnonymousMenuItem.hasSubMenu())) {
              paramAnonymousMenuBuilder.performItemAction(paramAnonymousMenuItem, 4);
            }
          }
        };
        long l = 200L + SystemClock.uptimeMillis();
        CascadingMenuPopup.this.mSubMenuHoverHandler.postAtTime(local1, paramAnonymousMenuBuilder, l);
        return;
      }
    }
    
    public void onItemHoverExit(@NonNull MenuBuilder paramAnonymousMenuBuilder, @NonNull MenuItem paramAnonymousMenuItem)
    {
      CascadingMenuPopup.this.mSubMenuHoverHandler.removeCallbacksAndMessages(paramAnonymousMenuBuilder);
    }
  };
  private final int mMenuMaxWidth;
  private PopupWindow.OnDismissListener mOnDismissListener;
  private final boolean mOverflowOnly;
  private final List<MenuBuilder> mPendingMenus = new LinkedList();
  private final int mPopupStyleAttr;
  private final int mPopupStyleRes;
  private MenuPresenter.Callback mPresenterCallback;
  private int mRawDropDownGravity = 0;
  boolean mShouldCloseImmediately;
  private boolean mShowTitle;
  final List<CascadingMenuInfo> mShowingMenus = new ArrayList();
  View mShownAnchorView;
  final Handler mSubMenuHoverHandler;
  private ViewTreeObserver mTreeObserver;
  private int mXOffset;
  private int mYOffset;
  
  public CascadingMenuPopup(@NonNull Context paramContext, @NonNull View paramView, @AttrRes int paramInt1, @StyleRes int paramInt2, boolean paramBoolean)
  {
    this.mContext = paramContext;
    this.mAnchorView = paramView;
    this.mPopupStyleAttr = paramInt1;
    this.mPopupStyleRes = paramInt2;
    this.mOverflowOnly = paramBoolean;
    this.mForceShowIcon = false;
    this.mLastPosition = getInitialMenuPosition();
    Resources localResources = paramContext.getResources();
    this.mMenuMaxWidth = Math.max(localResources.getDisplayMetrics().widthPixels / 2, localResources.getDimensionPixelSize(R.dimen.abc_config_prefDialogWidth));
    this.mSubMenuHoverHandler = new Handler();
  }
  
  private MenuPopupWindow createPopupWindow()
  {
    MenuPopupWindow localMenuPopupWindow = new MenuPopupWindow(this.mContext, null, this.mPopupStyleAttr, this.mPopupStyleRes);
    localMenuPopupWindow.setHoverListener(this.mMenuItemHoverListener);
    localMenuPopupWindow.setOnItemClickListener(this);
    localMenuPopupWindow.setOnDismissListener(this);
    localMenuPopupWindow.setAnchorView(this.mAnchorView);
    localMenuPopupWindow.setDropDownGravity(this.mDropDownGravity);
    localMenuPopupWindow.setModal(true);
    return localMenuPopupWindow;
  }
  
  private int findIndexOfAddedMenu(@NonNull MenuBuilder paramMenuBuilder)
  {
    int i = 0;
    int j = this.mShowingMenus.size();
    while (i < j)
    {
      if (paramMenuBuilder == ((CascadingMenuInfo)this.mShowingMenus.get(i)).menu) {
        return i;
      }
      i++;
    }
    return -1;
  }
  
  private MenuItem findMenuItemForSubmenu(@NonNull MenuBuilder paramMenuBuilder1, @NonNull MenuBuilder paramMenuBuilder2)
  {
    int i = 0;
    int j = paramMenuBuilder1.size();
    while (i < j)
    {
      MenuItem localMenuItem = paramMenuBuilder1.getItem(i);
      if ((localMenuItem.hasSubMenu()) && (paramMenuBuilder2 == localMenuItem.getSubMenu())) {
        return localMenuItem;
      }
      i++;
    }
    return null;
  }
  
  @Nullable
  private View findParentViewForSubmenu(@NonNull CascadingMenuInfo paramCascadingMenuInfo, @NonNull MenuBuilder paramMenuBuilder)
  {
    MenuItem localMenuItem = findMenuItemForSubmenu(paramCascadingMenuInfo.menu, paramMenuBuilder);
    if (localMenuItem == null) {
      return null;
    }
    ListView localListView = paramCascadingMenuInfo.getListView();
    ListAdapter localListAdapter = localListView.getAdapter();
    int i;
    MenuAdapter localMenuAdapter;
    label61:
    int j;
    int k;
    int m;
    if ((localListAdapter instanceof HeaderViewListAdapter))
    {
      HeaderViewListAdapter localHeaderViewListAdapter = (HeaderViewListAdapter)localListAdapter;
      i = localHeaderViewListAdapter.getHeadersCount();
      localMenuAdapter = (MenuAdapter)localHeaderViewListAdapter.getWrappedAdapter();
      j = -1;
      k = 0;
      m = localMenuAdapter.getCount();
    }
    for (;;)
    {
      if (k < m)
      {
        if (localMenuItem == localMenuAdapter.getItem(k)) {
          j = k;
        }
      }
      else
      {
        if (j == -1) {
          break;
        }
        int n = j + i - localListView.getFirstVisiblePosition();
        if ((n < 0) || (n >= localListView.getChildCount())) {
          break;
        }
        return localListView.getChildAt(n);
        localMenuAdapter = (MenuAdapter)localListAdapter;
        i = 0;
        break label61;
      }
      k++;
    }
  }
  
  private int getInitialMenuPosition()
  {
    int i = 1;
    if (ViewCompat.getLayoutDirection(this.mAnchorView) == i) {
      i = 0;
    }
    return i;
  }
  
  private int getNextMenuPosition(int paramInt)
  {
    ListView localListView = ((CascadingMenuInfo)this.mShowingMenus.get(-1 + this.mShowingMenus.size())).getListView();
    int[] arrayOfInt = new int[2];
    localListView.getLocationOnScreen(arrayOfInt);
    Rect localRect = new Rect();
    this.mShownAnchorView.getWindowVisibleDisplayFrame(localRect);
    if (this.mLastPosition == 1)
    {
      if (paramInt + (arrayOfInt[0] + localListView.getWidth()) > localRect.right) {
        return 0;
      }
      return 1;
    }
    if (arrayOfInt[0] - paramInt < 0) {
      return 1;
    }
    return 0;
  }
  
  private void showMenu(@NonNull MenuBuilder paramMenuBuilder)
  {
    LayoutInflater localLayoutInflater = LayoutInflater.from(this.mContext);
    MenuAdapter localMenuAdapter = new MenuAdapter(paramMenuBuilder, localLayoutInflater, this.mOverflowOnly);
    int i;
    MenuPopupWindow localMenuPopupWindow;
    CascadingMenuInfo localCascadingMenuInfo1;
    View localView;
    label130:
    int k;
    label164:
    int m;
    int i1;
    if ((!isShowing()) && (this.mForceShowIcon))
    {
      localMenuAdapter.setForceShowIcon(true);
      i = measureIndividualMenuWidth(localMenuAdapter, null, this.mContext, this.mMenuMaxWidth);
      localMenuPopupWindow = createPopupWindow();
      localMenuPopupWindow.setAdapter(localMenuAdapter);
      localMenuPopupWindow.setContentWidth(i);
      localMenuPopupWindow.setDropDownGravity(this.mDropDownGravity);
      if (this.mShowingMenus.size() <= 0) {
        break label383;
      }
      localCascadingMenuInfo1 = (CascadingMenuInfo)this.mShowingMenus.get(-1 + this.mShowingMenus.size());
      localView = findParentViewForSubmenu(localCascadingMenuInfo1, paramMenuBuilder);
      if (localView == null) {
        break label439;
      }
      localMenuPopupWindow.setTouchModal(false);
      localMenuPopupWindow.setEnterTransition(null);
      int j = getNextMenuPosition(i);
      if (j != 1) {
        break label392;
      }
      k = 1;
      this.mLastPosition = j;
      int[] arrayOfInt = new int[2];
      localView.getLocationInWindow(arrayOfInt);
      m = localCascadingMenuInfo1.window.getHorizontalOffset() + arrayOfInt[0];
      int n = localCascadingMenuInfo1.window.getVerticalOffset() + arrayOfInt[1];
      if ((0x5 & this.mDropDownGravity) != 5) {
        break label411;
      }
      if (k == 0) {
        break label398;
      }
      i1 = m + i;
      label234:
      localMenuPopupWindow.setHorizontalOffset(i1);
      localMenuPopupWindow.setVerticalOffset(n);
    }
    for (;;)
    {
      CascadingMenuInfo localCascadingMenuInfo2 = new CascadingMenuInfo(localMenuPopupWindow, paramMenuBuilder, this.mLastPosition);
      this.mShowingMenus.add(localCascadingMenuInfo2);
      localMenuPopupWindow.show();
      if ((localCascadingMenuInfo1 == null) && (this.mShowTitle) && (paramMenuBuilder.getHeaderTitle() != null))
      {
        ListView localListView = localMenuPopupWindow.getListView();
        FrameLayout localFrameLayout = (FrameLayout)localLayoutInflater.inflate(R.layout.abc_popup_menu_header_item_layout, localListView, false);
        TextView localTextView = (TextView)localFrameLayout.findViewById(16908310);
        localFrameLayout.setEnabled(false);
        localTextView.setText(paramMenuBuilder.getHeaderTitle());
        localListView.addHeaderView(localFrameLayout, null, false);
        localMenuPopupWindow.show();
      }
      return;
      if (!isShowing()) {
        break;
      }
      localMenuAdapter.setForceShowIcon(MenuPopup.shouldPreserveIconSpacing(paramMenuBuilder));
      break;
      label383:
      localCascadingMenuInfo1 = null;
      localView = null;
      break label130;
      label392:
      k = 0;
      break label164;
      label398:
      i1 = m - localView.getWidth();
      break label234;
      label411:
      if (k != 0)
      {
        i1 = m + localView.getWidth();
        break label234;
      }
      i1 = m - i;
      break label234;
      label439:
      if (this.mHasXOffset) {
        localMenuPopupWindow.setHorizontalOffset(this.mXOffset);
      }
      if (this.mHasYOffset) {
        localMenuPopupWindow.setVerticalOffset(this.mYOffset);
      }
      localMenuPopupWindow.setEpicenterBounds(getEpicenterBounds());
    }
  }
  
  public void addMenu(MenuBuilder paramMenuBuilder)
  {
    paramMenuBuilder.addMenuPresenter(this, this.mContext);
    if (isShowing())
    {
      showMenu(paramMenuBuilder);
      return;
    }
    this.mPendingMenus.add(paramMenuBuilder);
  }
  
  protected boolean closeMenuOnSubMenuOpened()
  {
    return false;
  }
  
  public void dismiss()
  {
    int i = this.mShowingMenus.size();
    if (i > 0)
    {
      CascadingMenuInfo[] arrayOfCascadingMenuInfo = (CascadingMenuInfo[])this.mShowingMenus.toArray(new CascadingMenuInfo[i]);
      for (int j = i - 1; j >= 0; j--)
      {
        CascadingMenuInfo localCascadingMenuInfo = arrayOfCascadingMenuInfo[j];
        if (localCascadingMenuInfo.window.isShowing()) {
          localCascadingMenuInfo.window.dismiss();
        }
      }
    }
  }
  
  public boolean flagActionItems()
  {
    return false;
  }
  
  public ListView getListView()
  {
    if (this.mShowingMenus.isEmpty()) {
      return null;
    }
    return ((CascadingMenuInfo)this.mShowingMenus.get(-1 + this.mShowingMenus.size())).getListView();
  }
  
  public boolean isShowing()
  {
    return (this.mShowingMenus.size() > 0) && (((CascadingMenuInfo)this.mShowingMenus.get(0)).window.isShowing());
  }
  
  public void onCloseMenu(MenuBuilder paramMenuBuilder, boolean paramBoolean)
  {
    int i = findIndexOfAddedMenu(paramMenuBuilder);
    if (i < 0) {}
    do
    {
      return;
      int j = i + 1;
      if (j < this.mShowingMenus.size()) {
        ((CascadingMenuInfo)this.mShowingMenus.get(j)).menu.close(false);
      }
      CascadingMenuInfo localCascadingMenuInfo = (CascadingMenuInfo)this.mShowingMenus.remove(i);
      localCascadingMenuInfo.menu.removeMenuPresenter(this);
      if (this.mShouldCloseImmediately)
      {
        localCascadingMenuInfo.window.setExitTransition(null);
        localCascadingMenuInfo.window.setAnimationStyle(0);
      }
      localCascadingMenuInfo.window.dismiss();
      int k = this.mShowingMenus.size();
      if (k > 0) {}
      for (this.mLastPosition = ((CascadingMenuInfo)this.mShowingMenus.get(k - 1)).position; k == 0; this.mLastPosition = getInitialMenuPosition())
      {
        dismiss();
        if (this.mPresenterCallback != null) {
          this.mPresenterCallback.onCloseMenu(paramMenuBuilder, true);
        }
        if (this.mTreeObserver != null)
        {
          if (this.mTreeObserver.isAlive()) {
            this.mTreeObserver.removeGlobalOnLayoutListener(this.mGlobalLayoutListener);
          }
          this.mTreeObserver = null;
        }
        this.mOnDismissListener.onDismiss();
        return;
      }
    } while (!paramBoolean);
    ((CascadingMenuInfo)this.mShowingMenus.get(0)).menu.close(false);
  }
  
  public void onDismiss()
  {
    int i = 0;
    int j = this.mShowingMenus.size();
    for (;;)
    {
      Object localObject = null;
      if (i < j)
      {
        CascadingMenuInfo localCascadingMenuInfo = (CascadingMenuInfo)this.mShowingMenus.get(i);
        if (!localCascadingMenuInfo.window.isShowing()) {
          localObject = localCascadingMenuInfo;
        }
      }
      else
      {
        if (localObject != null) {
          localObject.menu.close(false);
        }
        return;
      }
      i++;
    }
  }
  
  public boolean onKey(View paramView, int paramInt, KeyEvent paramKeyEvent)
  {
    if ((paramKeyEvent.getAction() == 1) && (paramInt == 82))
    {
      dismiss();
      return true;
    }
    return false;
  }
  
  public void onRestoreInstanceState(Parcelable paramParcelable) {}
  
  public Parcelable onSaveInstanceState()
  {
    return null;
  }
  
  public boolean onSubMenuSelected(SubMenuBuilder paramSubMenuBuilder)
  {
    Iterator localIterator = this.mShowingMenus.iterator();
    while (localIterator.hasNext())
    {
      CascadingMenuInfo localCascadingMenuInfo = (CascadingMenuInfo)localIterator.next();
      if (paramSubMenuBuilder == localCascadingMenuInfo.menu) {
        localCascadingMenuInfo.getListView().requestFocus();
      }
    }
    do
    {
      return true;
      if (!paramSubMenuBuilder.hasVisibleItems()) {
        break;
      }
      addMenu(paramSubMenuBuilder);
    } while (this.mPresenterCallback == null);
    this.mPresenterCallback.onOpenSubMenu(paramSubMenuBuilder);
    return true;
    return false;
  }
  
  public void setAnchorView(@NonNull View paramView)
  {
    if (this.mAnchorView != paramView)
    {
      this.mAnchorView = paramView;
      this.mDropDownGravity = GravityCompat.getAbsoluteGravity(this.mRawDropDownGravity, ViewCompat.getLayoutDirection(this.mAnchorView));
    }
  }
  
  public void setCallback(MenuPresenter.Callback paramCallback)
  {
    this.mPresenterCallback = paramCallback;
  }
  
  public void setForceShowIcon(boolean paramBoolean)
  {
    this.mForceShowIcon = paramBoolean;
  }
  
  public void setGravity(int paramInt)
  {
    if (this.mRawDropDownGravity != paramInt)
    {
      this.mRawDropDownGravity = paramInt;
      this.mDropDownGravity = GravityCompat.getAbsoluteGravity(paramInt, ViewCompat.getLayoutDirection(this.mAnchorView));
    }
  }
  
  public void setHorizontalOffset(int paramInt)
  {
    this.mHasXOffset = true;
    this.mXOffset = paramInt;
  }
  
  public void setOnDismissListener(PopupWindow.OnDismissListener paramOnDismissListener)
  {
    this.mOnDismissListener = paramOnDismissListener;
  }
  
  public void setShowTitle(boolean paramBoolean)
  {
    this.mShowTitle = paramBoolean;
  }
  
  public void setVerticalOffset(int paramInt)
  {
    this.mHasYOffset = true;
    this.mYOffset = paramInt;
  }
  
  public void show()
  {
    if (isShowing()) {}
    do
    {
      return;
      Iterator localIterator = this.mPendingMenus.iterator();
      while (localIterator.hasNext()) {
        showMenu((MenuBuilder)localIterator.next());
      }
      this.mPendingMenus.clear();
      this.mShownAnchorView = this.mAnchorView;
    } while (this.mShownAnchorView == null);
    if (this.mTreeObserver == null) {}
    for (int i = 1;; i = 0)
    {
      this.mTreeObserver = this.mShownAnchorView.getViewTreeObserver();
      if (i == 0) {
        break;
      }
      this.mTreeObserver.addOnGlobalLayoutListener(this.mGlobalLayoutListener);
      return;
    }
  }
  
  public void updateMenuView(boolean paramBoolean)
  {
    Iterator localIterator = this.mShowingMenus.iterator();
    while (localIterator.hasNext()) {
      toMenuAdapter(((CascadingMenuInfo)localIterator.next()).getListView().getAdapter()).notifyDataSetChanged();
    }
  }
  
  private static class CascadingMenuInfo
  {
    public final MenuBuilder menu;
    public final int position;
    public final MenuPopupWindow window;
    
    public CascadingMenuInfo(@NonNull MenuPopupWindow paramMenuPopupWindow, @NonNull MenuBuilder paramMenuBuilder, int paramInt)
    {
      this.window = paramMenuPopupWindow;
      this.menu = paramMenuBuilder;
      this.position = paramInt;
    }
    
    public ListView getListView()
    {
      return this.window.getListView();
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface HorizPosition {}
}
