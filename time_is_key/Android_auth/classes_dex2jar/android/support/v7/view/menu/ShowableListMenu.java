package android.support.v7.view.menu;

import android.support.annotation.RestrictTo;
import android.widget.ListView;

@RestrictTo({android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP})
public abstract interface ShowableListMenu
{
  public abstract void dismiss();
  
  public abstract ListView getListView();
  
  public abstract boolean isShowing();
  
  public abstract void show();
}
