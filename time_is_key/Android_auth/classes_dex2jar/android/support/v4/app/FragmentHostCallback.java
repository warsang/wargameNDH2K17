package android.support.v4.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.SimpleArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public abstract class FragmentHostCallback<E>
  extends FragmentContainer
{
  private final Activity mActivity;
  private SimpleArrayMap<String, LoaderManager> mAllLoaderManagers;
  private boolean mCheckedForLoaderManager;
  final Context mContext;
  final FragmentManagerImpl mFragmentManager = new FragmentManagerImpl();
  private final Handler mHandler;
  private LoaderManagerImpl mLoaderManager;
  private boolean mLoadersStarted;
  private boolean mRetainLoaders;
  final int mWindowAnimations;
  
  FragmentHostCallback(Activity paramActivity, Context paramContext, Handler paramHandler, int paramInt)
  {
    this.mActivity = paramActivity;
    this.mContext = paramContext;
    this.mHandler = paramHandler;
    this.mWindowAnimations = paramInt;
  }
  
  public FragmentHostCallback(Context paramContext, Handler paramHandler, int paramInt) {}
  
  FragmentHostCallback(FragmentActivity paramFragmentActivity)
  {
    this(paramFragmentActivity, paramFragmentActivity, paramFragmentActivity.mHandler, 0);
  }
  
  void doLoaderDestroy()
  {
    if (this.mLoaderManager == null) {
      return;
    }
    this.mLoaderManager.doDestroy();
  }
  
  void doLoaderRetain()
  {
    if (this.mLoaderManager == null) {
      return;
    }
    this.mLoaderManager.doRetain();
  }
  
  void doLoaderStart()
  {
    if (this.mLoadersStarted) {
      return;
    }
    this.mLoadersStarted = true;
    if (this.mLoaderManager != null) {
      this.mLoaderManager.doStart();
    }
    for (;;)
    {
      this.mCheckedForLoaderManager = true;
      return;
      if (!this.mCheckedForLoaderManager)
      {
        this.mLoaderManager = getLoaderManager("(root)", this.mLoadersStarted, false);
        if ((this.mLoaderManager != null) && (!this.mLoaderManager.mStarted)) {
          this.mLoaderManager.doStart();
        }
      }
    }
  }
  
  void doLoaderStop(boolean paramBoolean)
  {
    this.mRetainLoaders = paramBoolean;
    if (this.mLoaderManager == null) {}
    while (!this.mLoadersStarted) {
      return;
    }
    this.mLoadersStarted = false;
    if (paramBoolean)
    {
      this.mLoaderManager.doRetain();
      return;
    }
    this.mLoaderManager.doStop();
  }
  
  void dumpLoaders(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString)
  {
    paramPrintWriter.print(paramString);
    paramPrintWriter.print("mLoadersStarted=");
    paramPrintWriter.println(this.mLoadersStarted);
    if (this.mLoaderManager != null)
    {
      paramPrintWriter.print(paramString);
      paramPrintWriter.print("Loader Manager ");
      paramPrintWriter.print(Integer.toHexString(System.identityHashCode(this.mLoaderManager)));
      paramPrintWriter.println(":");
      this.mLoaderManager.dump(paramString + "  ", paramFileDescriptor, paramPrintWriter, paramArrayOfString);
    }
  }
  
  Activity getActivity()
  {
    return this.mActivity;
  }
  
  Context getContext()
  {
    return this.mContext;
  }
  
  FragmentManagerImpl getFragmentManagerImpl()
  {
    return this.mFragmentManager;
  }
  
  Handler getHandler()
  {
    return this.mHandler;
  }
  
  LoaderManagerImpl getLoaderManager(String paramString, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (this.mAllLoaderManagers == null) {
      this.mAllLoaderManagers = new SimpleArrayMap();
    }
    LoaderManagerImpl localLoaderManagerImpl = (LoaderManagerImpl)this.mAllLoaderManagers.get(paramString);
    if ((localLoaderManagerImpl == null) && (paramBoolean2))
    {
      localLoaderManagerImpl = new LoaderManagerImpl(paramString, this, paramBoolean1);
      this.mAllLoaderManagers.put(paramString, localLoaderManagerImpl);
    }
    while ((!paramBoolean1) || (localLoaderManagerImpl == null) || (localLoaderManagerImpl.mStarted)) {
      return localLoaderManagerImpl;
    }
    localLoaderManagerImpl.doStart();
    return localLoaderManagerImpl;
  }
  
  LoaderManagerImpl getLoaderManagerImpl()
  {
    if (this.mLoaderManager != null) {
      return this.mLoaderManager;
    }
    this.mCheckedForLoaderManager = true;
    this.mLoaderManager = getLoaderManager("(root)", this.mLoadersStarted, true);
    return this.mLoaderManager;
  }
  
  boolean getRetainLoaders()
  {
    return this.mRetainLoaders;
  }
  
  void inactivateFragment(String paramString)
  {
    if (this.mAllLoaderManagers != null)
    {
      LoaderManagerImpl localLoaderManagerImpl = (LoaderManagerImpl)this.mAllLoaderManagers.get(paramString);
      if ((localLoaderManagerImpl != null) && (!localLoaderManagerImpl.mRetaining))
      {
        localLoaderManagerImpl.doDestroy();
        this.mAllLoaderManagers.remove(paramString);
      }
    }
  }
  
  void onAttachFragment(Fragment paramFragment) {}
  
  public void onDump(String paramString, FileDescriptor paramFileDescriptor, PrintWriter paramPrintWriter, String[] paramArrayOfString) {}
  
  @Nullable
  public View onFindViewById(int paramInt)
  {
    return null;
  }
  
  @Nullable
  public abstract E onGetHost();
  
  public LayoutInflater onGetLayoutInflater()
  {
    return (LayoutInflater)this.mContext.getSystemService("layout_inflater");
  }
  
  public int onGetWindowAnimations()
  {
    return this.mWindowAnimations;
  }
  
  public boolean onHasView()
  {
    return true;
  }
  
  public boolean onHasWindowAnimations()
  {
    return true;
  }
  
  public void onRequestPermissionsFromFragment(@NonNull Fragment paramFragment, @NonNull String[] paramArrayOfString, int paramInt) {}
  
  public boolean onShouldSaveFragmentState(Fragment paramFragment)
  {
    return true;
  }
  
  public boolean onShouldShowRequestPermissionRationale(@NonNull String paramString)
  {
    return false;
  }
  
  public void onStartActivityFromFragment(Fragment paramFragment, Intent paramIntent, int paramInt)
  {
    onStartActivityFromFragment(paramFragment, paramIntent, paramInt, null);
  }
  
  public void onStartActivityFromFragment(Fragment paramFragment, Intent paramIntent, int paramInt, @Nullable Bundle paramBundle)
  {
    if (paramInt != -1) {
      throw new IllegalStateException("Starting activity with a requestCode requires a FragmentActivity host");
    }
    this.mContext.startActivity(paramIntent);
  }
  
  public void onStartIntentSenderFromFragment(Fragment paramFragment, IntentSender paramIntentSender, int paramInt1, @Nullable Intent paramIntent, int paramInt2, int paramInt3, int paramInt4, Bundle paramBundle)
    throws IntentSender.SendIntentException
  {
    if (paramInt1 != -1) {
      throw new IllegalStateException("Starting intent sender with a requestCode requires a FragmentActivity host");
    }
    ActivityCompat.startIntentSenderForResult(this.mActivity, paramIntentSender, paramInt1, paramIntent, paramInt2, paramInt3, paramInt4, paramBundle);
  }
  
  public void onSupportInvalidateOptionsMenu() {}
  
  void reportLoaderStart()
  {
    if (this.mAllLoaderManagers != null)
    {
      int i = this.mAllLoaderManagers.size();
      LoaderManagerImpl[] arrayOfLoaderManagerImpl = new LoaderManagerImpl[i];
      for (int j = i - 1; j >= 0; j--) {
        arrayOfLoaderManagerImpl[j] = ((LoaderManagerImpl)this.mAllLoaderManagers.valueAt(j));
      }
      for (int k = 0; k < i; k++)
      {
        LoaderManagerImpl localLoaderManagerImpl = arrayOfLoaderManagerImpl[k];
        localLoaderManagerImpl.finishRetain();
        localLoaderManagerImpl.doReportStart();
      }
    }
  }
  
  void restoreLoaderNonConfig(SimpleArrayMap<String, LoaderManager> paramSimpleArrayMap)
  {
    if (paramSimpleArrayMap != null)
    {
      int i = paramSimpleArrayMap.size();
      for (int j = 0; j < i; j++) {
        ((LoaderManagerImpl)paramSimpleArrayMap.valueAt(j)).updateHostController(this);
      }
    }
    this.mAllLoaderManagers = paramSimpleArrayMap;
  }
  
  SimpleArrayMap<String, LoaderManager> retainLoaderNonConfig()
  {
    SimpleArrayMap localSimpleArrayMap = this.mAllLoaderManagers;
    int i = 0;
    if (localSimpleArrayMap != null)
    {
      int j = this.mAllLoaderManagers.size();
      LoaderManagerImpl[] arrayOfLoaderManagerImpl = new LoaderManagerImpl[j];
      for (int k = j - 1; k >= 0; k--) {
        arrayOfLoaderManagerImpl[k] = ((LoaderManagerImpl)this.mAllLoaderManagers.valueAt(k));
      }
      boolean bool = getRetainLoaders();
      int m = 0;
      if (m < j)
      {
        LoaderManagerImpl localLoaderManagerImpl = arrayOfLoaderManagerImpl[m];
        if ((!localLoaderManagerImpl.mRetaining) && (bool))
        {
          if (!localLoaderManagerImpl.mStarted) {
            localLoaderManagerImpl.doStart();
          }
          localLoaderManagerImpl.doRetain();
        }
        if (localLoaderManagerImpl.mRetaining) {
          i = 1;
        }
        for (;;)
        {
          m++;
          break;
          localLoaderManagerImpl.doDestroy();
          this.mAllLoaderManagers.remove(localLoaderManagerImpl.mWho);
        }
      }
    }
    if (i != 0) {
      return this.mAllLoaderManagers;
    }
    return null;
  }
}
