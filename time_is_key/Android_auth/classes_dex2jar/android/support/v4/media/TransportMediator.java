package android.support.v4.media;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build.VERSION;
import android.view.KeyEvent;
import android.view.KeyEvent.Callback;
import android.view.KeyEvent.DispatcherState;
import android.view.View;
import android.view.Window;
import java.util.ArrayList;

@Deprecated
public class TransportMediator
  extends TransportController
{
  @Deprecated
  public static final int FLAG_KEY_MEDIA_FAST_FORWARD = 64;
  @Deprecated
  public static final int FLAG_KEY_MEDIA_NEXT = 128;
  @Deprecated
  public static final int FLAG_KEY_MEDIA_PAUSE = 16;
  @Deprecated
  public static final int FLAG_KEY_MEDIA_PLAY = 4;
  @Deprecated
  public static final int FLAG_KEY_MEDIA_PLAY_PAUSE = 8;
  @Deprecated
  public static final int FLAG_KEY_MEDIA_PREVIOUS = 1;
  @Deprecated
  public static final int FLAG_KEY_MEDIA_REWIND = 2;
  @Deprecated
  public static final int FLAG_KEY_MEDIA_STOP = 32;
  @Deprecated
  public static final int KEYCODE_MEDIA_PAUSE = 127;
  @Deprecated
  public static final int KEYCODE_MEDIA_PLAY = 126;
  @Deprecated
  public static final int KEYCODE_MEDIA_RECORD = 130;
  final AudioManager mAudioManager;
  final TransportPerformer mCallbacks;
  final Context mContext;
  final TransportMediatorJellybeanMR2 mController;
  final Object mDispatcherState;
  final KeyEvent.Callback mKeyEventCallback = new KeyEvent.Callback()
  {
    public boolean onKeyDown(int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
    {
      if (TransportMediator.isMediaKey(paramAnonymousInt)) {
        return TransportMediator.this.mCallbacks.onMediaButtonDown(paramAnonymousInt, paramAnonymousKeyEvent);
      }
      return false;
    }
    
    public boolean onKeyLongPress(int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
    {
      return false;
    }
    
    public boolean onKeyMultiple(int paramAnonymousInt1, int paramAnonymousInt2, KeyEvent paramAnonymousKeyEvent)
    {
      return false;
    }
    
    public boolean onKeyUp(int paramAnonymousInt, KeyEvent paramAnonymousKeyEvent)
    {
      if (TransportMediator.isMediaKey(paramAnonymousInt)) {
        return TransportMediator.this.mCallbacks.onMediaButtonUp(paramAnonymousInt, paramAnonymousKeyEvent);
      }
      return false;
    }
  };
  final ArrayList<TransportStateListener> mListeners = new ArrayList();
  final TransportMediatorCallback mTransportKeyCallback = new TransportMediatorCallback()
  {
    public long getPlaybackPosition()
    {
      return TransportMediator.this.mCallbacks.onGetCurrentPosition();
    }
    
    public void handleAudioFocusChange(int paramAnonymousInt)
    {
      TransportMediator.this.mCallbacks.onAudioFocusChange(paramAnonymousInt);
    }
    
    public void handleKey(KeyEvent paramAnonymousKeyEvent)
    {
      paramAnonymousKeyEvent.dispatch(TransportMediator.this.mKeyEventCallback);
    }
    
    public void playbackPositionUpdate(long paramAnonymousLong)
    {
      TransportMediator.this.mCallbacks.onSeekTo(paramAnonymousLong);
    }
  };
  final View mView;
  
  @Deprecated
  public TransportMediator(Activity paramActivity, TransportPerformer paramTransportPerformer)
  {
    this(paramActivity, null, paramTransportPerformer);
  }
  
  private TransportMediator(Activity paramActivity, View paramView, TransportPerformer paramTransportPerformer)
  {
    if (paramActivity != null) {}
    for (Object localObject = paramActivity;; localObject = paramView.getContext())
    {
      this.mContext = ((Context)localObject);
      this.mCallbacks = paramTransportPerformer;
      this.mAudioManager = ((AudioManager)this.mContext.getSystemService("audio"));
      if (paramActivity != null) {
        paramView = paramActivity.getWindow().getDecorView();
      }
      this.mView = paramView;
      this.mDispatcherState = this.mView.getKeyDispatcherState();
      if (Build.VERSION.SDK_INT < 18) {
        break;
      }
      this.mController = new TransportMediatorJellybeanMR2(this.mContext, this.mAudioManager, this.mView, this.mTransportKeyCallback);
      return;
    }
    this.mController = null;
  }
  
  @Deprecated
  public TransportMediator(View paramView, TransportPerformer paramTransportPerformer)
  {
    this(null, paramView, paramTransportPerformer);
  }
  
  private TransportStateListener[] getListeners()
  {
    if (this.mListeners.size() <= 0) {
      return null;
    }
    TransportStateListener[] arrayOfTransportStateListener = new TransportStateListener[this.mListeners.size()];
    this.mListeners.toArray(arrayOfTransportStateListener);
    return arrayOfTransportStateListener;
  }
  
  static boolean isMediaKey(int paramInt)
  {
    switch (paramInt)
    {
    default: 
      return false;
    }
    return true;
  }
  
  private void pushControllerState()
  {
    if (this.mController != null) {
      this.mController.refreshState(this.mCallbacks.onIsPlaying(), this.mCallbacks.onGetCurrentPosition(), this.mCallbacks.onGetTransportControlFlags());
    }
  }
  
  private void reportPlayingChanged()
  {
    TransportStateListener[] arrayOfTransportStateListener = getListeners();
    if (arrayOfTransportStateListener != null)
    {
      int i = arrayOfTransportStateListener.length;
      for (int j = 0; j < i; j++) {
        arrayOfTransportStateListener[j].onPlayingChanged(this);
      }
    }
  }
  
  private void reportTransportControlsChanged()
  {
    TransportStateListener[] arrayOfTransportStateListener = getListeners();
    if (arrayOfTransportStateListener != null)
    {
      int i = arrayOfTransportStateListener.length;
      for (int j = 0; j < i; j++) {
        arrayOfTransportStateListener[j].onTransportControlsChanged(this);
      }
    }
  }
  
  @Deprecated
  public void destroy()
  {
    this.mController.destroy();
  }
  
  @Deprecated
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent)
  {
    return paramKeyEvent.dispatch(this.mKeyEventCallback, (KeyEvent.DispatcherState)this.mDispatcherState, this);
  }
  
  @Deprecated
  public int getBufferPercentage()
  {
    return this.mCallbacks.onGetBufferPercentage();
  }
  
  @Deprecated
  public long getCurrentPosition()
  {
    return this.mCallbacks.onGetCurrentPosition();
  }
  
  @Deprecated
  public long getDuration()
  {
    return this.mCallbacks.onGetDuration();
  }
  
  @Deprecated
  public Object getRemoteControlClient()
  {
    if (this.mController != null) {
      return this.mController.getRemoteControlClient();
    }
    return null;
  }
  
  @Deprecated
  public int getTransportControlFlags()
  {
    return this.mCallbacks.onGetTransportControlFlags();
  }
  
  @Deprecated
  public boolean isPlaying()
  {
    return this.mCallbacks.onIsPlaying();
  }
  
  @Deprecated
  public void pausePlaying()
  {
    if (this.mController != null) {
      this.mController.pausePlaying();
    }
    this.mCallbacks.onPause();
    pushControllerState();
    reportPlayingChanged();
  }
  
  @Deprecated
  public void refreshState()
  {
    pushControllerState();
    reportPlayingChanged();
    reportTransportControlsChanged();
  }
  
  @Deprecated
  public void registerStateListener(TransportStateListener paramTransportStateListener)
  {
    this.mListeners.add(paramTransportStateListener);
  }
  
  @Deprecated
  public void seekTo(long paramLong)
  {
    this.mCallbacks.onSeekTo(paramLong);
  }
  
  @Deprecated
  public void startPlaying()
  {
    if (this.mController != null) {
      this.mController.startPlaying();
    }
    this.mCallbacks.onStart();
    pushControllerState();
    reportPlayingChanged();
  }
  
  @Deprecated
  public void stopPlaying()
  {
    if (this.mController != null) {
      this.mController.stopPlaying();
    }
    this.mCallbacks.onStop();
    pushControllerState();
    reportPlayingChanged();
  }
  
  @Deprecated
  public void unregisterStateListener(TransportStateListener paramTransportStateListener)
  {
    this.mListeners.remove(paramTransportStateListener);
  }
}
