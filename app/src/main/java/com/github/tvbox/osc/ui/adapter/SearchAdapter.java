package com.github.tvbox.osc.ui.adapter;

import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.recyclerview.widget.GridLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.tvbox.kotlin.ui.utils.SP;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.api.ApiConfig;
import com.github.tvbox.osc.base.App;
import com.github.tvbox.osc.bean.Movie;
import com.github.tvbox.osc.util.ImgUtil;

import java.util.ArrayList;

public class SearchAdapter extends BaseQuickAdapter<Movie.Video, BaseViewHolder> {
    private int searchWidth;

    public SearchAdapter() {
        super(SP.INSTANCE.getSearchView() == 0 ? R.layout.item_search_lite : R.layout.item_search, new ArrayList<>());
        searchWidth = SP.INSTANCE.getSearchResultWidth();
    }

    @Override
    protected void convert(BaseViewHolder helper, Movie.Video item) {
        // lite
        if (SP.INSTANCE.getSearchView() == 0) {
            helper.setText(R.id.tvName, String.format("%s  %s %s %s", ApiConfig.get().getSource(item.sourceKey).getName(), item.name, item.type == null ? "" : item.type, item.note == null ? "" : item.note));
        } else {// with preview\
            helper.setText(R.id.tvName, item.name);
            helper.setText(R.id.tvSite, ApiConfig.get().getSource(item.sourceKey).getName());
            helper.setVisible(R.id.tvNote, item.note != null && !item.note.isEmpty());
            if (item.note != null && !item.note.isEmpty()) {
                helper.setText(R.id.tvNote, item.note);
            }
            FrameLayout flRootView = helper.getView(R.id.fl_root_view);
            // 动态设置item宽度
            if (searchWidth == -1) {
                searchWidth = SP.INSTANCE.getSearchResultWidth();
                if (searchWidth != -1) {
                    setItemWidth(flRootView);
                }
            } else {
                setItemWidth(flRootView);
            }
            ImageView ivThumb = helper.getView(R.id.ivThumb);
            if (!TextUtils.isEmpty(item.pic)) {
                // takagen99 : Use Glide instead
                ImgUtil.load(item.pic, ivThumb, (int) App.getInstance().getResources().getDimension(R.dimen.vs_5));
            } else {
                ivThumb.setImageResource(R.drawable.img_loading_placeholder);
            }
        }
    }

    private void setItemWidth(FrameLayout view) {
        GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        layoutParams.width = searchWidth;
        view.setLayoutParams(layoutParams);
    }
}