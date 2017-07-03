/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  android.app.Notification
 *  android.os.Binder
 *  android.os.IBinder
 *  android.os.IInterface
 *  android.os.Parcel
 *  android.os.Parcelable
 *  android.os.Parcelable$Creator
 *  android.os.RemoteException
 */
package android.support.v4.app;

import android.app.Notification;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

public interface INotificationSideChannel
extends IInterface {
    public void cancel(String var1, int var2, String var3) throws RemoteException;

    public void cancelAll(String var1) throws RemoteException;

    public void notify(String var1, int var2, String var3, Notification var4) throws RemoteException;

    public static abstract class Stub
    extends Binder
    implements INotificationSideChannel {
        private static final String DESCRIPTOR = "android.support.v4.app.INotificationSideChannel";
        static final int TRANSACTION_cancel = 2;
        static final int TRANSACTION_cancelAll = 3;
        static final int TRANSACTION_notify = 1;

        public Stub() {
            this.attachInterface((IInterface)this, "android.support.v4.app.INotificationSideChannel");
        }

        public static INotificationSideChannel asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterface = iBinder.queryLocalInterface("android.support.v4.app.INotificationSideChannel");
            if (iInterface != null && iInterface instanceof INotificationSideChannel) {
                return (INotificationSideChannel)iInterface;
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
                    parcel2.writeString("android.support.v4.app.INotificationSideChannel");
                    return true;
                }
                case 1: {
                    parcel.enforceInterface("android.support.v4.app.INotificationSideChannel");
                    String string2 = parcel.readString();
                    int n3 = parcel.readInt();
                    String string3 = parcel.readString();
                    Notification notification = parcel.readInt() != 0 ? (Notification)Notification.CREATOR.createFromParcel(parcel) : null;
                    this.notify(string2, n3, string3, notification);
                    return true;
                }
                case 2: {
                    parcel.enforceInterface("android.support.v4.app.INotificationSideChannel");
                    this.cancel(parcel.readString(), parcel.readInt(), parcel.readString());
                    return true;
                }
                case 3: 
            }
            parcel.enforceInterface("android.support.v4.app.INotificationSideChannel");
            this.cancelAll(parcel.readString());
            return true;
        }

        private static class Proxy
        implements INotificationSideChannel {
            private IBinder mRemote;

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override
            public void cancel(String string2, int n, String string3) throws RemoteException {
                Parcel parcel = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken("android.support.v4.app.INotificationSideChannel");
                    parcel.writeString(string2);
                    parcel.writeInt(n);
                    parcel.writeString(string3);
                    this.mRemote.transact(2, parcel, null, 1);
                    return;
                }
                finally {
                    parcel.recycle();
                }
            }

            @Override
            public void cancelAll(String string2) throws RemoteException {
                Parcel parcel = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken("android.support.v4.app.INotificationSideChannel");
                    parcel.writeString(string2);
                    this.mRemote.transact(3, parcel, null, 1);
                    return;
                }
                finally {
                    parcel.recycle();
                }
            }

            public String getInterfaceDescriptor() {
                return "android.support.v4.app.INotificationSideChannel";
            }

            /*
             * Enabled aggressive block sorting
             * Enabled unnecessary exception pruning
             * Enabled aggressive exception aggregation
             */
            @Override
            public void notify(String string2, int n, String string3, Notification notification) throws RemoteException {
                Parcel parcel = Parcel.obtain();
                try {
                    parcel.writeInterfaceToken("android.support.v4.app.INotificationSideChannel");
                    parcel.writeString(string2);
                    parcel.writeInt(n);
                    parcel.writeString(string3);
                    if (notification != null) {
                        parcel.writeInt(1);
                        notification.writeToParcel(parcel, 0);
                    } else {
                        parcel.writeInt(0);
                    }
                    this.mRemote.transact(1, parcel, null, 1);
                    return;
                }
                finally {
                    parcel.recycle();
                }
            }
        }

    }

}

