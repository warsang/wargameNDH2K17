/*
 * Decompiled with CFR 0_115.
 * 
 * Could not load the following classes:
 *  android.os.Handler
 *  android.os.Looper
 *  android.util.Log
 */
package android.support.v7.util;

import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ParallelExecutorCompat;
import android.support.v7.util.ThreadUtil;
import android.support.v7.util.TileList;
import android.util.Log;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

class MessageThreadUtil<T>
implements ThreadUtil<T> {
    MessageThreadUtil() {
    }

    @Override
    public ThreadUtil.BackgroundCallback<T> getBackgroundProxy(final ThreadUtil.BackgroundCallback<T> backgroundCallback) {
        return new ThreadUtil.BackgroundCallback<T>(){
            static final int LOAD_TILE = 3;
            static final int RECYCLE_TILE = 4;
            static final int REFRESH = 1;
            static final int UPDATE_RANGE = 2;
            private Runnable mBackgroundRunnable;
            AtomicBoolean mBackgroundRunning;
            private final Executor mExecutor;
            final MessageQueue mQueue;

            private void maybeExecuteBackgroundRunnable() {
                if (this.mBackgroundRunning.compareAndSet(false, true)) {
                    this.mExecutor.execute(this.mBackgroundRunnable);
                }
            }

            private void sendMessage(SyncQueueItem syncQueueItem) {
                this.mQueue.sendMessage(syncQueueItem);
                this.maybeExecuteBackgroundRunnable();
            }

            private void sendMessageAtFrontOfQueue(SyncQueueItem syncQueueItem) {
                this.mQueue.sendMessageAtFrontOfQueue(syncQueueItem);
                this.maybeExecuteBackgroundRunnable();
            }

            @Override
            public void loadTile(int n, int n2) {
                this.sendMessage(SyncQueueItem.obtainMessage(3, n, n2));
            }

            @Override
            public void recycleTile(TileList.Tile<T> tile) {
                this.sendMessage(SyncQueueItem.obtainMessage(4, 0, tile));
            }

            @Override
            public void refresh(int n) {
                this.sendMessageAtFrontOfQueue(SyncQueueItem.obtainMessage(1, n, null));
            }

            @Override
            public void updateRange(int n, int n2, int n3, int n4, int n5) {
                this.sendMessageAtFrontOfQueue(SyncQueueItem.obtainMessage(2, n, n2, n3, n4, n5, null));
            }

        };
    }

    @Override
    public ThreadUtil.MainThreadCallback<T> getMainThreadProxy(final ThreadUtil.MainThreadCallback<T> mainThreadCallback) {
        return new ThreadUtil.MainThreadCallback<T>(){
            static final int ADD_TILE = 2;
            static final int REMOVE_TILE = 3;
            static final int UPDATE_ITEM_COUNT = 1;
            private final Handler mMainThreadHandler;
            private Runnable mMainThreadRunnable;
            final MessageQueue mQueue;

            private void sendMessage(SyncQueueItem syncQueueItem) {
                this.mQueue.sendMessage(syncQueueItem);
                this.mMainThreadHandler.post(this.mMainThreadRunnable);
            }

            @Override
            public void addTile(int n, TileList.Tile<T> tile) {
                this.sendMessage(SyncQueueItem.obtainMessage(2, n, tile));
            }

            @Override
            public void removeTile(int n, int n2) {
                this.sendMessage(SyncQueueItem.obtainMessage(3, n, n2));
            }

            @Override
            public void updateItemCount(int n, int n2) {
                this.sendMessage(SyncQueueItem.obtainMessage(1, n, n2));
            }

        };
    }

    static class MessageQueue {
        private SyncQueueItem mRoot;

        MessageQueue() {
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        SyncQueueItem next() {
            synchronized (this) {
                block6 : {
                    SyncQueueItem syncQueueItem = this.mRoot;
                    if (syncQueueItem != null) break block6;
                    return null;
                }
                SyncQueueItem syncQueueItem = this.mRoot;
                this.mRoot = this.mRoot.next;
                return syncQueueItem;
            }
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        void removeMessages(int n) {
            synchronized (this) {
                while (this.mRoot != null && this.mRoot.what == n) {
                    SyncQueueItem syncQueueItem = this.mRoot;
                    this.mRoot = this.mRoot.next;
                    syncQueueItem.recycle();
                }
                if (this.mRoot != null) {
                    SyncQueueItem syncQueueItem = this.mRoot;
                    SyncQueueItem syncQueueItem2 = syncQueueItem.next;
                    while (syncQueueItem2 != null) {
                        SyncQueueItem syncQueueItem3 = syncQueueItem2.next;
                        if (syncQueueItem2.what == n) {
                            syncQueueItem.next = syncQueueItem3;
                            syncQueueItem2.recycle();
                        } else {
                            syncQueueItem = syncQueueItem2;
                        }
                        syncQueueItem2 = syncQueueItem3;
                    }
                }
                return;
            }
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        void sendMessage(SyncQueueItem syncQueueItem) {
            synchronized (this) {
                if (this.mRoot == null) {
                    this.mRoot = syncQueueItem;
                } else {
                    SyncQueueItem syncQueueItem2 = this.mRoot;
                    while (syncQueueItem2.next != null) {
                        syncQueueItem2 = syncQueueItem2.next;
                    }
                    syncQueueItem2.next = syncQueueItem;
                }
                return;
            }
        }

        void sendMessageAtFrontOfQueue(SyncQueueItem syncQueueItem) {
            synchronized (this) {
                syncQueueItem.next = this.mRoot;
                this.mRoot = syncQueueItem;
                return;
            }
        }
    }

    static class SyncQueueItem {
        private static SyncQueueItem sPool;
        private static final Object sPoolLock;
        public int arg1;
        public int arg2;
        public int arg3;
        public int arg4;
        public int arg5;
        public Object data;
        private SyncQueueItem next;
        public int what;

        static {
            sPoolLock = new Object();
        }

        SyncQueueItem() {
        }

        static SyncQueueItem obtainMessage(int n, int n2, int n3) {
            return SyncQueueItem.obtainMessage(n, n2, n3, 0, 0, 0, null);
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        static SyncQueueItem obtainMessage(int n, int n2, int n3, int n4, int n5, int n6, Object object) {
            Object object2 = sPoolLock;
            synchronized (object2) {
                SyncQueueItem syncQueueItem;
                if (sPool == null) {
                    syncQueueItem = new SyncQueueItem();
                } else {
                    syncQueueItem = sPool;
                    sPool = SyncQueueItem.sPool.next;
                    syncQueueItem.next = null;
                }
                syncQueueItem.what = n;
                syncQueueItem.arg1 = n2;
                syncQueueItem.arg2 = n3;
                syncQueueItem.arg3 = n4;
                syncQueueItem.arg4 = n5;
                syncQueueItem.arg5 = n6;
                syncQueueItem.data = object;
                return syncQueueItem;
            }
        }

        static SyncQueueItem obtainMessage(int n, int n2, Object object) {
            return SyncQueueItem.obtainMessage(n, n2, 0, 0, 0, 0, object);
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        void recycle() {
            this.next = null;
            this.arg5 = 0;
            this.arg4 = 0;
            this.arg3 = 0;
            this.arg2 = 0;
            this.arg1 = 0;
            this.what = 0;
            this.data = null;
            Object object = sPoolLock;
            synchronized (object) {
                if (sPool != null) {
                    this.next = sPool;
                }
                sPool = this;
                return;
            }
        }
    }

}

