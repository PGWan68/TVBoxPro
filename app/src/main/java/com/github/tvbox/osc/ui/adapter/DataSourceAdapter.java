package com.github.tvbox.osc.ui.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.bean.DataSourceBean;

import java.util.List;

public class DataSourceAdapter extends BaseQuickAdapter<DataSourceBean, BaseViewHolder> {

    public DataSourceAdapter(@Nullable List<DataSourceBean> data) {
        super(R.layout.holder_data_source, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, DataSourceBean item) {
        int position = helper.getLayoutPosition();
        helper.setGone(R.id.tvUrl, !(position == 0 || position == getData().size() - 1));
        helper.setGone(R.id.ivChecked, item.isCurrent());


        helper.setText(R.id.tvName, item.getName());
        helper.setText(R.id.tvUrl, item.getUrl());
    }
}


