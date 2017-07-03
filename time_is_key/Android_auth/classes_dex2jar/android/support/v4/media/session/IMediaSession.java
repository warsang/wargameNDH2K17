/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  android.app.PendingIntent
 *  android.net.Uri
 *  android.os.Binder
 *  android.os.Bundle
 *  android.os.IBinder
 *  android.os.IInterface
 *  android.os.Parcel
 *  android.os.Parcelable
 *  android.os.Parcelable$Creator
 *  android.os.RemoteException
 *  android.text.TextUtils
 *  android.view.KeyEvent
 */
package android.support.v4.media.session;

import android.app.PendingIntent;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.RatingCompat;
import android.support.v4.media.session.IMediaControllerCallback;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.ParcelableVolumeInfo;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public interface IMediaSession
extends IInterface {
    public void addQueueItem(MediaDescriptionCompat var1) throws RemoteException;

    public void addQueueItemAt(MediaDescriptionCompat var1, int var2) throws RemoteException;

    public void adjustVolume(int var1, int var2, String var3) throws RemoteException;

    public void fastForward() throws RemoteException;

    public Bundle getExtras() throws RemoteException;

    public long getFlags() throws RemoteException;

    public PendingIntent getLaunchPendingIntent() throws RemoteException;

    public MediaMetadataCompat getMetadata() throws RemoteException;

    public String getPackageName() throws RemoteException;

    public PlaybackStateCompat getPlaybackState() throws RemoteException;

    public List<MediaSessionCompat.QueueItem> getQueue() throws RemoteException;

    public CharSequence getQueueTitle() throws RemoteException;

    public int getRatingType() throws RemoteException;

    public int getRepeatMode() throws RemoteException;

    public String getTag() throws RemoteException;

    public ParcelableVolumeInfo getVolumeAttributes() throws RemoteException;

    public boolean isShuffleModeEnabled() throws RemoteException;

    public boolean isTransportControlEnabled() throws RemoteException;

    public void next() throws RemoteException;

    public void pause() throws RemoteException;

    public void play() throws RemoteException;

    public void playFromMediaId(String var1, Bundle var2) throws RemoteException;

    public void playFromSearch(String var1, Bundle var2) throws RemoteException;

    public void playFromUri(Uri var1, Bundle var2) throws RemoteException;

    public void prepare() throws RemoteException;

    public void prepareFromMediaId(String var1, Bundle var2) throws RemoteException;

    public void prepareFromSearch(String var1, Bundle var2) throws RemoteException;

    public void prepareFromUri(Uri var1, Bundle var2) throws RemoteException;

    public void previous() throws RemoteException;

    public void rate(RatingCompat var1) throws RemoteException;

    public void registerCallbackListener(IMediaControllerCallback var1) throws RemoteException;

    public void removeQueueItem(MediaDescriptionCompat var1) throws RemoteException;

    public void removeQueueItemAt(int var1) throws RemoteException;

    public void rewind() throws RemoteException;

    public void seekTo(long var1) throws RemoteException;

    public void sendCommand(String var1, Bundle var2, MediaSessionCompat.ResultReceiverWrapper var3) throws RemoteException;

    public void sendCustomAction(String var1, Bundle var2) throws RemoteException;

    public boolean sendMediaButton(KeyEvent var1) throws RemoteException;

    public void setRepeatMode(int var1) throws RemoteException;

    public void setShuffleModeEnabled(boolean var1) throws RemoteException;

    public void setVolumeTo(int var1, int var2, String var3) throws RemoteException;

    public void skipToQueueItem(long var1) throws RemoteException;

    public void stop() throws RemoteException;

    public void unregisterCallbackListener(IMediaControllerCallback var1) throws RemoteException;

    public static abstract class Stub
    extends Binder
    implements IMediaSession {
        private static final String DESCRIPTOR = "android.support.v4.media.session.IMediaSession";
        static final int TRANSACTION_addQueueItem = 41;
        static final int TRANSACTION_addQueueItemAt = 42;
        static final int TRANSACTION_adjustVolume = 11;
        static final int TRANSACTION_fastForward = 22;
        static final int TRANSACTION_getExtras = 31;
        static final int TRANSACTION_getFlags = 9;
        static final int TRANSACTION_getLaunchPendingIntent = 8;
        static final int TRANSACTION_getMetadata = 27;
        static final int TRANSACTION_getPackageName = 6;
        static final int TRANSACTION_getPlaybackState = 28;
        static final int TRANSACTION_getQueue = 29;
        static final int TRANSACTION_getQueueTitle = 30;
        static final int TRANSACTION_getRatingType = 32;
        static final int TRANSACTION_getRepeatMode = 37;
        static final int TRANSACTION_getTag = 7;
        static final int TRANSACTION_getVolumeAttributes = 10;
        static final int TRANSACTION_isShuffleModeEnabled = 38;
        static final int TRANSACTION_isTransportControlEnabled = 5;
        static final int TRANSACTION_next = 20;
        static final int TRANSACTION_pause = 18;
        static final int TRANSACTION_play = 13;
        static final int TRANSACTION_playFromMediaId = 14;
        static final int TRANSACTION_playFromSearch = 15;
        static final int TRANSACTION_playFromUri = 16;
        static final int TRANSACTION_prepare = 33;
        static final int TRANSACTION_prepareFromMediaId = 34;
        static final int TRANSACTION_prepareFromSearch = 35;
        static final int TRANSACTION_prepareFromUri = 36;
        static final int TRANSACTION_previous = 21;
        static final int TRANSACTION_rate = 25;
        static final int TRANSACTION_registerCallbackListener = 3;
        static final int TRANSACTION_removeQueueItem = 43;
        static final int TRANSACTION_removeQueueItemAt = 44;
        static final int TRANSACTION_rewind = 23;
        static final int TRANSACTION_seekTo = 24;
        static final int TRANSACTION_sendCommand = 1;
        static final int TRANSACTION_sendCustomAction = 26;
        static final int TRANSACTION_sendMediaButton = 2;
        static final int TRANSACTION_setRepeatMode = 39;
        static final int TRANSACTION_setShuffleModeEnabled = 40;
        static final int TRANSACTION_setVolumeTo = 12;
        static final int TRANSACTION_skipToQueueItem = 17;
        static final int TRANSACTION_stop = 19;
        static final int TRANSACTION_unregisterCallbackListener = 4;

        public Stub() {
            this.attachInterface((IInterface)this, "android.support.v4.media.session.IMediaSession");
        }

        public static IMediaSession asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterface = iBinder.queryLocalInterface("android.support.v4.media.session.IMediaSession");
            if (iInterface != null && iInterface instanceof IMediaSession) {
                return (IMediaSession)iInterface;
            }
            return new Proxy(iBinder);
        }

        public IBinder asBinder() {
            return this;
        }

        /*
         * Enabled aggressive block sorting
         */
        public boolean onTransact(int n, Parcel parcel, Parcel parcel2, int n2) throws RemoteException {
            switch (n) {
                default: {
                    return super.onTransact(n, parcel, parcel2, n2);
                }
                case 1598968902: {
                    parcel2.writeString("android.support.v4.media.session.IMediaSession");
                    return true;
                }
                case 1: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    String string2 = parcel.readString();
                    Bundle bundle = parcel.readInt() != 0 ? (Bundle)Bundle.CREATOR.createFromParcel(parcel) : null;
                    MediaSessionCompat.ResultReceiverWrapper resultReceiverWrapper = parcel.readInt() != 0 ? (MediaSessionCompat.ResultReceiverWrapper)MediaSessionCompat.ResultReceiverWrapper.CREATOR.createFromParcel(parcel) : null;
                    this.sendCommand(string2, bundle, resultReceiverWrapper);
                    parcel2.writeNoException();
                    return true;
                }
                case 2: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    KeyEvent keyEvent = parcel.readInt() != 0 ? (KeyEvent)KeyEvent.CREATOR.createFromParcel(parcel) : null;
                    boolean bl = this.sendMediaButton(keyEvent);
                    parcel2.writeNoException();
                    int n3 = 0;
                    if (bl) {
                        n3 = 1;
                    }
                    parcel2.writeInt(n3);
                    return true;
                }
                case 3: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    this.registerCallbackListener(IMediaControllerCallback.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                }
                case 4: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    this.unregisterCallbackListener(IMediaControllerCallback.Stub.asInterface(parcel.readStrongBinder()));
                    parcel2.writeNoException();
                    return true;
                }
                case 5: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    boolean bl = this.isTransportControlEnabled();
                    parcel2.writeNoException();
                    int n4 = 0;
                    if (bl) {
                        n4 = 1;
                    }
                    parcel2.writeInt(n4);
                    return true;
                }
                case 6: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    String string3 = this.getPackageName();
                    parcel2.writeNoException();
                    parcel2.writeString(string3);
                    return true;
                }
                case 7: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    String string4 = this.getTag();
                    parcel2.writeNoException();
                    parcel2.writeString(string4);
                    return true;
                }
                case 8: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    PendingIntent pendingIntent = this.getLaunchPendingIntent();
                    parcel2.writeNoException();
                    if (pendingIntent != null) {
                        parcel2.writeInt(1);
                        pendingIntent.writeToParcel(parcel2, 1);
                        return true;
                    }
                    parcel2.writeInt(0);
                    return true;
                }
                case 9: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    long l = this.getFlags();
                    parcel2.writeNoException();
                    parcel2.writeLong(l);
                    return true;
                }
                case 10: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    ParcelableVolumeInfo parcelableVolumeInfo = this.getVolumeAttributes();
                    parcel2.writeNoException();
                    if (parcelableVolumeInfo != null) {
                        parcel2.writeInt(1);
                        parcelableVolumeInfo.writeToParcel(parcel2, 1);
                        return true;
                    }
                    parcel2.writeInt(0);
                    return true;
                }
                case 11: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    this.adjustVolume(parcel.readInt(), parcel.readInt(), parcel.readString());
                    parcel2.writeNoException();
                    return true;
                }
                case 12: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    this.setVolumeTo(parcel.readInt(), parcel.readInt(), parcel.readString());
                    parcel2.writeNoException();
                    return true;
                }
                case 27: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    MediaMetadataCompat mediaMetadataCompat = this.getMetadata();
                    parcel2.writeNoException();
                    if (mediaMetadataCompat != null) {
                        parcel2.writeInt(1);
                        mediaMetadataCompat.writeToParcel(parcel2, 1);
                        return true;
                    }
                    parcel2.writeInt(0);
                    return true;
                }
                case 28: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    PlaybackStateCompat playbackStateCompat = this.getPlaybackState();
                    parcel2.writeNoException();
                    if (playbackStateCompat != null) {
                        parcel2.writeInt(1);
                        playbackStateCompat.writeToParcel(parcel2, 1);
                        return true;
                    }
                    parcel2.writeInt(0);
                    return true;
                }
                case 29: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    List list = this.getQueue();
                    parcel2.writeNoException();
                    parcel2.writeTypedList(list);
                    return true;
                }
                case 30: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    CharSequence charSequence = this.getQueueTitle();
                    parcel2.writeNoException();
                    if (charSequence != null) {
                        parcel2.writeInt(1);
                        TextUtils.writeToParcel((CharSequence)charSequence, (Parcel)parcel2, (int)1);
                        return true;
                    }
                    parcel2.writeInt(0);
                    return true;
                }
                case 31: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    Bundle bundle = this.getExtras();
                    parcel2.writeNoException();
                    if (bundle != null) {
                        parcel2.writeInt(1);
                        bundle.writeToParcel(parcel2, 1);
                        return true;
                    }
                    parcel2.writeInt(0);
                    return true;
                }
                case 32: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    int n5 = this.getRatingType();
                    parcel2.writeNoException();
                    parcel2.writeInt(n5);
                    return true;
                }
                case 37: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    int n6 = this.getRepeatMode();
                    parcel2.writeNoException();
                    parcel2.writeInt(n6);
                    return true;
                }
                case 38: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    boolean bl = this.isShuffleModeEnabled();
                    parcel2.writeNoException();
                    int n7 = 0;
                    if (bl) {
                        n7 = 1;
                    }
                    parcel2.writeInt(n7);
                    return true;
                }
                case 41: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    MediaDescriptionCompat mediaDescriptionCompat = parcel.readInt() != 0 ? (MediaDescriptionCompat)MediaDescriptionCompat.CREATOR.createFromParcel(parcel) : null;
                    this.addQueueItem(mediaDescriptionCompat);
                    parcel2.writeNoException();
                    return true;
                }
                case 42: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    MediaDescriptionCompat mediaDescriptionCompat = parcel.readInt() != 0 ? (MediaDescriptionCompat)MediaDescriptionCompat.CREATOR.createFromParcel(parcel) : null;
                    this.addQueueItemAt(mediaDescriptionCompat, parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                }
                case 43: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    MediaDescriptionCompat mediaDescriptionCompat = parcel.readInt() != 0 ? (MediaDescriptionCompat)MediaDescriptionCompat.CREATOR.createFromParcel(parcel) : null;
                    this.removeQueueItem(mediaDescriptionCompat);
                    parcel2.writeNoException();
                    return true;
                }
                case 44: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    this.removeQueueItemAt(parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                }
                case 33: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    this.prepare();
                    parcel2.writeNoException();
                    return true;
                }
                case 34: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    String string5 = parcel.readString();
                    Bundle bundle = parcel.readInt() != 0 ? (Bundle)Bundle.CREATOR.createFromParcel(parcel) : null;
                    this.prepareFromMediaId(string5, bundle);
                    parcel2.writeNoException();
                    return true;
                }
                case 35: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    String string6 = parcel.readString();
                    Bundle bundle = parcel.readInt() != 0 ? (Bundle)Bundle.CREATOR.createFromParcel(parcel) : null;
                    this.prepareFromSearch(string6, bundle);
                    parcel2.writeNoException();
                    return true;
                }
                case 36: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    Uri uri = parcel.readInt() != 0 ? (Uri)Uri.CREATOR.createFromParcel(parcel) : null;
                    Bundle bundle = parcel.readInt() != 0 ? (Bundle)Bundle.CREATOR.createFromParcel(parcel) : null;
                    this.prepareFromUri(uri, bundle);
                    parcel2.writeNoException();
                    return true;
                }
                case 13: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    this.play();
                    parcel2.writeNoException();
                    return true;
                }
                case 14: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    String string7 = parcel.readString();
                    Bundle bundle = parcel.readInt() != 0 ? (Bundle)Bundle.CREATOR.createFromParcel(parcel) : null;
                    this.playFromMediaId(string7, bundle);
                    parcel2.writeNoException();
                    return true;
                }
                case 15: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    String string8 = parcel.readString();
                    Bundle bundle = parcel.readInt() != 0 ? (Bundle)Bundle.CREATOR.createFromParcel(parcel) : null;
                    this.playFromSearch(string8, bundle);
                    parcel2.writeNoException();
                    return true;
                }
                case 16: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    Uri uri = parcel.readInt() != 0 ? (Uri)Uri.CREATOR.createFromParcel(parcel) : null;
                    Bundle bundle = parcel.readInt() != 0 ? (Bundle)Bundle.CREATOR.createFromParcel(parcel) : null;
                    this.playFromUri(uri, bundle);
                    parcel2.writeNoException();
                    return true;
                }
                case 17: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    this.skipToQueueItem(parcel.readLong());
                    parcel2.writeNoException();
                    return true;
                }
                case 18: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    this.pause();
                    parcel2.writeNoException();
                    return true;
                }
                case 19: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    this.stop();
                    parcel2.writeNoException();
                    return true;
                }
                case 20: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    this.next();
                    parcel2.writeNoException();
                    return true;
                }
                case 21: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    this.previous();
                    parcel2.writeNoException();
                    return true;
                }
                case 22: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    this.fastForward();
                    parcel2.writeNoException();
                    return true;
                }
                case 23: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    this.rewind();
                    parcel2.writeNoException();
                    return true;
                }
                case 24: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    this.seekTo(parcel.readLong());
                    parcel2.writeNoException();
                    return true;
                }
                case 25: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    RatingCompat ratingCompat = parcel.readInt() != 0 ? (RatingCompat)RatingCompat.CREATOR.createFromParcel(parcel) : null;
                    this.rate(ratingCompat);
                    parcel2.writeNoException();
                    return true;
                }
                case 39: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    this.setRepeatMode(parcel.readInt());
                    parcel2.writeNoException();
                    return true;
                }
                case 40: {
                    parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
                    boolean bl = parcel.readInt() != 0;
                    this.setShuffleModeEnabled(bl);
                    parcel2.writeNoException();
                    return true;
                }
                case 26: 
            }
            parcel.enforceInterface("android.support.v4.media.session.IMediaSession");
            String string9 = parcel.readString();
            Bundle bundle = parcel.readInt() != 0 ? (Bundle)Bundle.CREATOR.createFromParcel(parcel) : null;
            this.sendCustomAction(string9, bundle);
            parcel2.writeNoException();
            return true;
        }

        private static class Proxy
        implements IMediaSession {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            /*
             * Enabled aggressive block sorting
             * Enabled unnecessary exception pruning
             * Enabled aggressive exception aggregation
             */
            @Override
            public void addQueueItem(MediaDescriptionCompat mediaDescriptionCompat) throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    if (mediaDescriptionCompat != null) {
                        parcel.writeInt(1);
                        mediaDescriptionCompat.writeToParcel(parcel, 0);
                    } else {
                        parcel.writeInt(0);
                    }
                    this.mRemote.transact(41, parcel, parcel2, 0);
                    parcel2.readException();
                    return;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            /*
             * Enabled aggressive block sorting
             * Enabled unnecessary exception pruning
             * Enabled aggressive exception aggregation
             */
            @Override
            public void addQueueItemAt(MediaDescriptionCompat mediaDescriptionCompat, int n) throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    if (mediaDescriptionCompat != null) {
                        parcel.writeInt(1);
                        mediaDescriptionCompat.writeToParcel(parcel, 0);
                    } else {
                        parcel.writeInt(0);
                    }
                    parcel.writeInt(n);
                    this.mRemote.transact(42, parcel, parcel2, 0);
                    parcel2.readException();
                    return;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            @Override
            public void adjustVolume(int n, int n2, String string2) throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    parcel.writeInt(n);
                    parcel.writeInt(n2);
                    parcel.writeString(string2);
                    this.mRemote.transact(11, parcel, parcel2, 0);
                    parcel2.readException();
                    return;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override
            public void fastForward() throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    this.mRemote.transact(22, parcel, parcel2, 0);
                    parcel2.readException();
                    return;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            /*
             * Enabled aggressive block sorting
             * Enabled unnecessary exception pruning
             * Enabled aggressive exception aggregation
             */
            @Override
            public Bundle getExtras() throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    this.mRemote.transact(31, parcel, parcel2, 0);
                    parcel2.readException();
                    Bundle bundle = parcel2.readInt() != 0 ? (Bundle)Bundle.CREATOR.createFromParcel(parcel2) : null;
                    return bundle;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            @Override
            public long getFlags() throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    this.mRemote.transact(9, parcel, parcel2, 0);
                    parcel2.readException();
                    long l = parcel2.readLong();
                    return l;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return "android.support.v4.media.session.IMediaSession";
            }

            /*
             * Enabled aggressive block sorting
             * Enabled unnecessary exception pruning
             * Enabled aggressive exception aggregation
             */
            @Override
            public PendingIntent getLaunchPendingIntent() throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    this.mRemote.transact(8, parcel, parcel2, 0);
                    parcel2.readException();
                    PendingIntent pendingIntent = parcel2.readInt() != 0 ? (PendingIntent)PendingIntent.CREATOR.createFromParcel(parcel2) : null;
                    return pendingIntent;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            /*
             * Enabled aggressive block sorting
             * Enabled unnecessary exception pruning
             * Enabled aggressive exception aggregation
             */
            @Override
            public MediaMetadataCompat getMetadata() throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    this.mRemote.transact(27, parcel, parcel2, 0);
                    parcel2.readException();
                    MediaMetadataCompat mediaMetadataCompat = parcel2.readInt() != 0 ? (MediaMetadataCompat)MediaMetadataCompat.CREATOR.createFromParcel(parcel2) : null;
                    return mediaMetadataCompat;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            @Override
            public String getPackageName() throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    this.mRemote.transact(6, parcel, parcel2, 0);
                    parcel2.readException();
                    String string2 = parcel2.readString();
                    return string2;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            /*
             * Enabled aggressive block sorting
             * Enabled unnecessary exception pruning
             * Enabled aggressive exception aggregation
             */
            @Override
            public PlaybackStateCompat getPlaybackState() throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    this.mRemote.transact(28, parcel, parcel2, 0);
                    parcel2.readException();
                    PlaybackStateCompat playbackStateCompat = parcel2.readInt() != 0 ? (PlaybackStateCompat)PlaybackStateCompat.CREATOR.createFromParcel(parcel2) : null;
                    return playbackStateCompat;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            @Override
            public List<MediaSessionCompat.QueueItem> getQueue() throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    this.mRemote.transact(29, parcel, parcel2, 0);
                    parcel2.readException();
                    ArrayList arrayList = parcel2.createTypedArrayList(MediaSessionCompat.QueueItem.CREATOR);
                    return arrayList;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            /*
             * Enabled aggressive block sorting
             * Enabled unnecessary exception pruning
             * Enabled aggressive exception aggregation
             */
            @Override
            public CharSequence getQueueTitle() throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    this.mRemote.transact(30, parcel, parcel2, 0);
                    parcel2.readException();
                    CharSequence charSequence = parcel2.readInt() != 0 ? (CharSequence)TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel2) : null;
                    return charSequence;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            @Override
            public int getRatingType() throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    this.mRemote.transact(32, parcel, parcel2, 0);
                    parcel2.readException();
                    int n = parcel2.readInt();
                    return n;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            @Override
            public int getRepeatMode() throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    this.mRemote.transact(37, parcel, parcel2, 0);
                    parcel2.readException();
                    int n = parcel2.readInt();
                    return n;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            @Override
            public String getTag() throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    this.mRemote.transact(7, parcel, parcel2, 0);
                    parcel2.readException();
                    String string2 = parcel2.readString();
                    return string2;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            /*
             * Enabled aggressive block sorting
             * Enabled unnecessary exception pruning
             * Enabled aggressive exception aggregation
             */
            @Override
            public ParcelableVolumeInfo getVolumeAttributes() throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    this.mRemote.transact(10, parcel, parcel2, 0);
                    parcel2.readException();
                    ParcelableVolumeInfo parcelableVolumeInfo = parcel2.readInt() != 0 ? (ParcelableVolumeInfo)ParcelableVolumeInfo.CREATOR.createFromParcel(parcel2) : null;
                    return parcelableVolumeInfo;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            @Override
            public boolean isShuffleModeEnabled() throws RemoteException {
                boolean bl;
                Parcel parcel;
                Parcel parcel2;
                block2 : {
                    parcel2 = Parcel.obtain();
                    parcel = Parcel.obtain();
                    try {
                        parcel2.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                        this.mRemote.transact(38, parcel2, parcel, 0);
                        parcel.readException();
                        int n = parcel.readInt();
                        bl = false;
                        if (n == 0) break block2;
                        bl = true;
                    }
                    catch (Throwable var3_5) {
                        parcel.recycle();
                        parcel2.recycle();
                        throw var3_5;
                    }
                }
                parcel.recycle();
                parcel2.recycle();
                return bl;
            }

            @Override
            public boolean isTransportControlEnabled() throws RemoteException {
                boolean bl;
                Parcel parcel;
                Parcel parcel2;
                block2 : {
                    parcel2 = Parcel.obtain();
                    parcel = Parcel.obtain();
                    try {
                        parcel2.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                        this.mRemote.transact(5, parcel2, parcel, 0);
                        parcel.readException();
                        int n = parcel.readInt();
                        bl = false;
                        if (n == 0) break block2;
                        bl = true;
                    }
                    catch (Throwable var3_5) {
                        parcel.recycle();
                        parcel2.recycle();
                        throw var3_5;
                    }
                }
                parcel.recycle();
                parcel2.recycle();
                return bl;
            }

            @Override
            public void next() throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    this.mRemote.transact(20, parcel, parcel2, 0);
                    parcel2.readException();
                    return;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            @Override
            public void pause() throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    this.mRemote.transact(18, parcel, parcel2, 0);
                    parcel2.readException();
                    return;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            @Override
            public void play() throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    this.mRemote.transact(13, parcel, parcel2, 0);
                    parcel2.readException();
                    return;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            /*
             * Enabled aggressive block sorting
             * Enabled unnecessary exception pruning
             * Enabled aggressive exception aggregation
             */
            @Override
            public void playFromMediaId(String string2, Bundle bundle) throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    parcel.writeString(string2);
                    if (bundle != null) {
                        parcel.writeInt(1);
                        bundle.writeToParcel(parcel, 0);
                    } else {
                        parcel.writeInt(0);
                    }
                    this.mRemote.transact(14, parcel, parcel2, 0);
                    parcel2.readException();
                    return;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            /*
             * Enabled aggressive block sorting
             * Enabled unnecessary exception pruning
             * Enabled aggressive exception aggregation
             */
            @Override
            public void playFromSearch(String string2, Bundle bundle) throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    parcel.writeString(string2);
                    if (bundle != null) {
                        parcel.writeInt(1);
                        bundle.writeToParcel(parcel, 0);
                    } else {
                        parcel.writeInt(0);
                    }
                    this.mRemote.transact(15, parcel, parcel2, 0);
                    parcel2.readException();
                    return;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            /*
             * Enabled aggressive block sorting
             * Enabled unnecessary exception pruning
             * Enabled aggressive exception aggregation
             */
            @Override
            public void playFromUri(Uri uri, Bundle bundle) throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    if (uri != null) {
                        parcel.writeInt(1);
                        uri.writeToParcel(parcel, 0);
                    } else {
                        parcel.writeInt(0);
                    }
                    if (bundle != null) {
                        parcel.writeInt(1);
                        bundle.writeToParcel(parcel, 0);
                    } else {
                        parcel.writeInt(0);
                    }
                    this.mRemote.transact(16, parcel, parcel2, 0);
                    parcel2.readException();
                    return;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            @Override
            public void prepare() throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    this.mRemote.transact(33, parcel, parcel2, 0);
                    parcel2.readException();
                    return;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            /*
             * Enabled aggressive block sorting
             * Enabled unnecessary exception pruning
             * Enabled aggressive exception aggregation
             */
            @Override
            public void prepareFromMediaId(String string2, Bundle bundle) throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    parcel.writeString(string2);
                    if (bundle != null) {
                        parcel.writeInt(1);
                        bundle.writeToParcel(parcel, 0);
                    } else {
                        parcel.writeInt(0);
                    }
                    this.mRemote.transact(34, parcel, parcel2, 0);
                    parcel2.readException();
                    return;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            /*
             * Enabled aggressive block sorting
             * Enabled unnecessary exception pruning
             * Enabled aggressive exception aggregation
             */
            @Override
            public void prepareFromSearch(String string2, Bundle bundle) throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    parcel.writeString(string2);
                    if (bundle != null) {
                        parcel.writeInt(1);
                        bundle.writeToParcel(parcel, 0);
                    } else {
                        parcel.writeInt(0);
                    }
                    this.mRemote.transact(35, parcel, parcel2, 0);
                    parcel2.readException();
                    return;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            /*
             * Enabled aggressive block sorting
             * Enabled unnecessary exception pruning
             * Enabled aggressive exception aggregation
             */
            @Override
            public void prepareFromUri(Uri uri, Bundle bundle) throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    if (uri != null) {
                        parcel.writeInt(1);
                        uri.writeToParcel(parcel, 0);
                    } else {
                        parcel.writeInt(0);
                    }
                    if (bundle != null) {
                        parcel.writeInt(1);
                        bundle.writeToParcel(parcel, 0);
                    } else {
                        parcel.writeInt(0);
                    }
                    this.mRemote.transact(36, parcel, parcel2, 0);
                    parcel2.readException();
                    return;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            @Override
            public void previous() throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    this.mRemote.transact(21, parcel, parcel2, 0);
                    parcel2.readException();
                    return;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            /*
             * Enabled aggressive block sorting
             * Enabled unnecessary exception pruning
             * Enabled aggressive exception aggregation
             */
            @Override
            public void rate(RatingCompat ratingCompat) throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    if (ratingCompat != null) {
                        parcel.writeInt(1);
                        ratingCompat.writeToParcel(parcel, 0);
                    } else {
                        parcel.writeInt(0);
                    }
                    this.mRemote.transact(25, parcel, parcel2, 0);
                    parcel2.readException();
                    return;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            /*
             * Enabled aggressive block sorting
             * Enabled unnecessary exception pruning
             * Enabled aggressive exception aggregation
             */
            @Override
            public void registerCallbackListener(IMediaControllerCallback iMediaControllerCallback) throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    IBinder iBinder = iMediaControllerCallback != null ? iMediaControllerCallback.asBinder() : null;
                    parcel.writeStrongBinder(iBinder);
                    this.mRemote.transact(3, parcel, parcel2, 0);
                    parcel2.readException();
                    return;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            /*
             * Enabled aggressive block sorting
             * Enabled unnecessary exception pruning
             * Enabled aggressive exception aggregation
             */
            @Override
            public void removeQueueItem(MediaDescriptionCompat mediaDescriptionCompat) throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    if (mediaDescriptionCompat != null) {
                        parcel.writeInt(1);
                        mediaDescriptionCompat.writeToParcel(parcel, 0);
                    } else {
                        parcel.writeInt(0);
                    }
                    this.mRemote.transact(43, parcel, parcel2, 0);
                    parcel2.readException();
                    return;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            @Override
            public void removeQueueItemAt(int n) throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    parcel.writeInt(n);
                    this.mRemote.transact(44, parcel, parcel2, 0);
                    parcel2.readException();
                    return;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            @Override
            public void rewind() throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    this.mRemote.transact(23, parcel, parcel2, 0);
                    parcel2.readException();
                    return;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            @Override
            public void seekTo(long l) throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    parcel.writeLong(l);
                    this.mRemote.transact(24, parcel, parcel2, 0);
                    parcel2.readException();
                    return;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            /*
             * Enabled aggressive block sorting
             * Enabled unnecessary exception pruning
             * Enabled aggressive exception aggregation
             */
            @Override
            public void sendCommand(String string2, Bundle bundle, MediaSessionCompat.ResultReceiverWrapper resultReceiverWrapper) throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    parcel.writeString(string2);
                    if (bundle != null) {
                        parcel.writeInt(1);
                        bundle.writeToParcel(parcel, 0);
                    } else {
                        parcel.writeInt(0);
                    }
                    if (resultReceiverWrapper != null) {
                        parcel.writeInt(1);
                        resultReceiverWrapper.writeToParcel(parcel, 0);
                    } else {
                        parcel.writeInt(0);
                    }
                    this.mRemote.transact(1, parcel, parcel2, 0);
                    parcel2.readException();
                    return;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            /*
             * Enabled aggressive block sorting
             * Enabled unnecessary exception pruning
             * Enabled aggressive exception aggregation
             */
            @Override
            public void sendCustomAction(String string2, Bundle bundle) throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    parcel.writeString(string2);
                    if (bundle != null) {
                        parcel.writeInt(1);
                        bundle.writeToParcel(parcel, 0);
                    } else {
                        parcel.writeInt(0);
                    }
                    this.mRemote.transact(26, parcel, parcel2, 0);
                    parcel2.readException();
                    return;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            /*
             * Enabled aggressive block sorting
             * Enabled unnecessary exception pruning
             * Enabled aggressive exception aggregation
             */
            @Override
            public boolean sendMediaButton(KeyEvent keyEvent) throws RemoteException {
                boolean bl;
                Parcel parcel;
                Parcel parcel2;
                block6 : {
                    block5 : {
                        bl = true;
                        parcel2 = Parcel.obtain();
                        parcel = Parcel.obtain();
                        try {
                            parcel2.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                            if (keyEvent != null) {
                                parcel2.writeInt(1);
                                keyEvent.writeToParcel(parcel2, 0);
                            } else {
                                parcel2.writeInt(0);
                            }
                            this.mRemote.transact(2, parcel2, parcel, 0);
                            parcel.readException();
                            int n = parcel.readInt();
                            if (n == 0) break block5;
                            break block6;
                        }
                        catch (Throwable var5_6) {
                            parcel.recycle();
                            parcel2.recycle();
                            throw var5_6;
                        }
                    }
                    bl = false;
                }
                parcel.recycle();
                parcel2.recycle();
                return bl;
            }

            @Override
            public void setRepeatMode(int n) throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    parcel.writeInt(n);
                    this.mRemote.transact(39, parcel, parcel2, 0);
                    parcel2.readException();
                    return;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            @Override
            public void setShuffleModeEnabled(boolean bl) throws RemoteException {
                int n;
                Parcel parcel;
                Parcel parcel2;
                block4 : {
                    parcel = Parcel.obtain();
                    parcel2 = Parcel.obtain();
                    parcel.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    n = 0;
                    if (!bl) break block4;
                    n = 1;
                }
                try {
                    parcel.writeInt(n);
                    this.mRemote.transact(40, parcel, parcel2, 0);
                    parcel2.readException();
                    return;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            @Override
            public void setVolumeTo(int n, int n2, String string2) throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    parcel.writeInt(n);
                    parcel.writeInt(n2);
                    parcel.writeString(string2);
                    this.mRemote.transact(12, parcel, parcel2, 0);
                    parcel2.readException();
                    return;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            @Override
            public void skipToQueueItem(long l) throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    parcel.writeLong(l);
                    this.mRemote.transact(17, parcel, parcel2, 0);
                    parcel2.readException();
                    return;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            @Override
            public void stop() throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    this.mRemote.transact(19, parcel, parcel2, 0);
                    parcel2.readException();
                    return;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }

            /*
             * Enabled aggressive block sorting
             * Enabled unnecessary exception pruning
             * Enabled aggressive exception aggregation
             */
            @Override
            public void unregisterCallbackListener(IMediaControllerCallback iMediaControllerCallback) throws RemoteException {
                Parcel parcel = Parcel.obtain();
                Parcel parcel2 = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken("android.support.v4.media.session.IMediaSession");
                    IBinder iBinder = iMediaControllerCallback != null ? iMediaControllerCallback.asBinder() : null;
                    parcel.writeStrongBinder(iBinder);
                    this.mRemote.transact(4, parcel, parcel2, 0);
                    parcel2.readException();
                    return;
                }
                finally {
                    parcel2.recycle();
                    parcel.recycle();
                }
            }
        }

    }

}

