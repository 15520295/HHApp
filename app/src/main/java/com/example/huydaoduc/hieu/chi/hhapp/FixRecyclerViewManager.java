package com.example.huydaoduc.hieu.chi.hhapp;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.LinearLayout;

//public class FixRecyclerViewManager extends GridLayoutManager {
//    public FixRecyclerViewManager(Context mContext) {
//        super();
//    }
//
//    /**
//     * Disable predictive animations. There is a bug in RecyclerView which causes views that
//     * are being reloaded to pull invalid ViewHolders from the internal recycler stack if the
//     * adapter size has decreased since the ViewHolder was recycled.
//     */
//    @Override
//    public boolean supportsPredictiveItemAnimations() {
//        return false;
//    }
//
//    public FixRecyclerViewManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }
//
//    public FixRecyclerViewManager(Context context, int spanCount) {
//        super(context, spanCount);
//    }
//
//    public FixRecyclerViewManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
//        super(context, spanCount, orientation, reverseLayout);
//    }
//}