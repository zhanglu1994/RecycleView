package com.zhangl.recycleview;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class XXRecycleAdapter extends BaseQuickAdapter<UserBean,BaseViewHolder> {


    public XXRecycleAdapter(int layoutResId, @Nullable List<UserBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, UserBean item) {

        helper.setText(R.id.name_tv,item.getName())
                .addOnClickListener(R.id.main_iv)
                .addOnClickListener(R.id.name_tv);

    }




}
