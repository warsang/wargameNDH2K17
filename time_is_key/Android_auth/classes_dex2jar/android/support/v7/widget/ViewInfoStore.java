/*
 * Decompiled with CFR 0_115.
 */
package android.support.v7.widget;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.LongSparseArray;
import android.support.v4.util.Pools;
import android.support.v7.widget.RecyclerView;

class ViewInfoStore {
    private static final boolean DEBUG;
    @VisibleForTesting
    final ArrayMap<RecyclerView.ViewHolder, InfoRecord> mLayoutHolderMap = new ArrayMap();
    @VisibleForTesting
    final LongSparseArray<RecyclerView.ViewHolder> mOldChangedHolders = new LongSparseArray();

    ViewInfoStore() {
    }

    /*
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    private RecyclerView.ItemAnimator.ItemHolderInfo popFromLayoutStep(RecyclerView.ViewHolder viewHolder, int n) {
        int n2 = this.mLayoutHolderMap.indexOfKey(viewHolder);
        RecyclerView.ItemAnimator.ItemHolderInfo itemHolderInfo = null;
        if (n2 < 0) {
            return itemHolderInfo;
        }
        InfoRecord infoRecord = this.mLayoutHolderMap.valueAt(n2);
        itemHolderInfo = null;
        if (infoRecord == null) return itemHolderInfo;
        int n3 = n & infoRecord.flags;
        itemHolderInfo = null;
        if (n3 == 0) return itemHolderInfo;
        infoRecord.flags &= ~ n;
        if (n == 4) {
            itemHolderInfo = infoRecord.preInfo;
        } else {
            if (n != 8) throw new IllegalArgumentException("Must provide flag PRE or POST");
            itemHolderInfo = infoRecord.postInfo;
        }
        if ((12 & infoRecord.flags) != 0) return itemHolderInfo;
        this.mLayoutHolderMap.removeAt(n2);
        InfoRecord.recycle(infoRecord);
        return itemHolderInfo;
    }

    void addToAppearedInPreLayoutHolders(RecyclerView.ViewHolder viewHolder, RecyclerView.ItemAnimator.ItemHolderInfo itemHolderInfo) {
        InfoRecord infoRecord = this.mLayoutHolderMap.get(viewHolder);
        if (infoRecord == null) {
            infoRecord = InfoRecord.obtain();
            this.mLayoutHolderMap.put(viewHolder, infoRecord);
        }
        infoRecord.flags = 2 | infoRecord.flags;
        infoRecord.preInfo = itemHolderInfo;
    }

    void addToDisappearedInLayout(RecyclerView.ViewHolder viewHolder) {
        InfoRecord infoRecord = this.mLayoutHolderMap.get(viewHolder);
        if (infoRecord == null) {
            infoRecord = InfoRecord.obtain();
            this.mLayoutHolderMap.put(viewHolder, infoRecord);
        }
        infoRecord.flags = 1 | infoRecord.flags;
    }

    void addToOldChangeHolders(long l, RecyclerView.ViewHolder viewHolder) {
        this.mOldChangedHolders.put(l, viewHolder);
    }

    void addToPostLayout(RecyclerView.ViewHolder viewHolder, RecyclerView.ItemAnimator.ItemHolderInfo itemHolderInfo) {
        InfoRecord infoRecord = this.mLayoutHolderMap.get(viewHolder);
        if (infoRecord == null) {
            infoRecord = InfoRecord.obtain();
            this.mLayoutHolderMap.put(viewHolder, infoRecord);
        }
        infoRecord.postInfo = itemHolderInfo;
        infoRecord.flags = 8 | infoRecord.flags;
    }

    void addToPreLayout(RecyclerView.ViewHolder viewHolder, RecyclerView.ItemAnimator.ItemHolderInfo itemHolderInfo) {
        InfoRecord infoRecord = this.mLayoutHolderMap.get(viewHolder);
        if (infoRecord == null) {
            infoRecord = InfoRecord.obtain();
            this.mLayoutHolderMap.put(viewHolder, infoRecord);
        }
        infoRecord.preInfo = itemHolderInfo;
        infoRecord.flags = 4 | infoRecord.flags;
    }

    void clear() {
        this.mLayoutHolderMap.clear();
        this.mOldChangedHolders.clear();
    }

    RecyclerView.ViewHolder getFromOldChangeHolders(long l) {
        return this.mOldChangedHolders.get(l);
    }

    boolean isDisappearing(RecyclerView.ViewHolder viewHolder) {
        InfoRecord infoRecord = this.mLayoutHolderMap.get(viewHolder);
        if (infoRecord != null && (1 & infoRecord.flags) != 0) {
            return true;
        }
        return false;
    }

    boolean isInPreLayout(RecyclerView.ViewHolder viewHolder) {
        InfoRecord infoRecord = this.mLayoutHolderMap.get(viewHolder);
        if (infoRecord != null && (4 & infoRecord.flags) != 0) {
            return true;
        }
        return false;
    }

    void onDetach() {
        InfoRecord.drainCache();
    }

    public void onViewDetached(RecyclerView.ViewHolder viewHolder) {
        this.removeFromDisappearedInLayout(viewHolder);
    }

    @Nullable
    RecyclerView.ItemAnimator.ItemHolderInfo popFromPostLayout(RecyclerView.ViewHolder viewHolder) {
        return this.popFromLayoutStep(viewHolder, 8);
    }

    @Nullable
    RecyclerView.ItemAnimator.ItemHolderInfo popFromPreLayout(RecyclerView.ViewHolder viewHolder) {
        return this.popFromLayoutStep(viewHolder, 4);
    }

    /*
     * Enabled aggressive block sorting
     */
    void process(ProcessCallback processCallback) {
        int n = -1 + this.mLayoutHolderMap.size();
        while (n >= 0) {
            RecyclerView.ViewHolder viewHolder = this.mLayoutHolderMap.keyAt(n);
            InfoRecord infoRecord = this.mLayoutHolderMap.removeAt(n);
            if ((3 & infoRecord.flags) == 3) {
                processCallback.unused(viewHolder);
            } else if ((1 & infoRecord.flags) != 0) {
                if (infoRecord.preInfo == null) {
                    processCallback.unused(viewHolder);
                } else {
                    processCallback.processDisappeared(viewHolder, infoRecord.preInfo, infoRecord.postInfo);
                }
            } else if ((14 & infoRecord.flags) == 14) {
                processCallback.processAppeared(viewHolder, infoRecord.preInfo, infoRecord.postInfo);
            } else if ((12 & infoRecord.flags) == 12) {
                processCallback.processPersistent(viewHolder, infoRecord.preInfo, infoRecord.postInfo);
            } else if ((4 & infoRecord.flags) != 0) {
                processCallback.processDisappeared(viewHolder, infoRecord.preInfo, null);
            } else if ((8 & infoRecord.flags) != 0) {
                processCallback.processAppeared(viewHolder, infoRecord.preInfo, infoRecord.postInfo);
            } else if ((2 & infoRecord.flags) != 0) {
                // empty if block
            }
            InfoRecord.recycle(infoRecord);
            --n;
        }
        return;
    }

    void removeFromDisappearedInLayout(RecyclerView.ViewHolder viewHolder) {
        InfoRecord infoRecord = this.mLayoutHolderMap.get(viewHolder);
        if (infoRecord == null) {
            return;
        }
        infoRecord.flags = -2 & infoRecord.flags;
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    void removeViewHolder(RecyclerView.ViewHolder var1_1) {
        var2_2 = -1 + this.mOldChangedHolders.size();
        do {
            if (var2_2 < 0) ** GOTO lbl6
            if (var1_1 == this.mOldChangedHolders.valueAt(var2_2)) {
                this.mOldChangedHolders.removeAt(var2_2);
lbl6: // 2 sources:
                if ((var3_3 = this.mLayoutHolderMap.remove(var1_1)) == null) return;
                InfoRecord.recycle(var3_3);
                return;
            }
            --var2_2;
        } while (true);
    }

    static class InfoRecord {
        static final int FLAG_APPEAR = 2;
        static final int FLAG_APPEAR_AND_DISAPPEAR = 3;
        static final int FLAG_APPEAR_PRE_AND_POST = 14;
        static final int FLAG_DISAPPEARED = 1;
        static final int FLAG_POST = 8;
        static final int FLAG_PRE = 4;
        static final int FLAG_PRE_AND_POST = 12;
        static Pools.Pool<InfoRecord> sPool = new Pools.SimplePool<InfoRecord>(20);
        int flags;
        @Nullable
        RecyclerView.ItemAnimator.ItemHolderInfo postInfo;
        @Nullable
        RecyclerView.ItemAnimator.ItemHolderInfo preInfo;

        private InfoRecord() {
        }

        static void drainCache() {
            while (sPool.acquire() != null) {
            }
        }

        static InfoRecord obtain() {
            InfoRecord infoRecord = sPool.acquire();
            if (infoRecord == null) {
                infoRecord = new InfoRecord();
            }
            return infoRecord;
        }

        static void recycle(InfoRecord infoRecord) {
            infoRecord.flags = 0;
            infoRecord.preInfo = null;
            infoRecord.postInfo = null;
            sPool.release(infoRecord);
        }
    }

    static interface ProcessCallback {
        public void processAppeared(RecyclerView.ViewHolder var1, @Nullable RecyclerView.ItemAnimator.ItemHolderInfo var2, RecyclerView.ItemAnimator.ItemHolderInfo var3);

        public void processDisappeared(RecyclerView.ViewHolder var1, @NonNull RecyclerView.ItemAnimator.ItemHolderInfo var2, @Nullable RecyclerView.ItemAnimator.ItemHolderInfo var3);

        public void processPersistent(RecyclerView.ViewHolder var1, @NonNull RecyclerView.ItemAnimator.ItemHolderInfo var2, @NonNull RecyclerView.ItemAnimator.ItemHolderInfo var3);

        public void unused(RecyclerView.ViewHolder var1);
    }

}

