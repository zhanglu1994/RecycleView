package com.zhangl.recycleview;

import com.chad.library.adapter.base.loadmore.LoadMoreView;

public class RecycleLoadingView extends LoadMoreView {


    @Override
    public int getLayoutId() {
        return R.layout.recycle_loadview;
    }

    @Override
    protected int getLoadingViewId() {
        return R.id.load_more_loading_view;
    }

    @Override
    protected int getLoadFailViewId() {
        return R.id.load_more_load_fail_view;
    }

    @Override
    protected int getLoadEndViewId() {
        return R.id.load_more_load_end_view;
    }
}
