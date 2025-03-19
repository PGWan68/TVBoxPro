package com.github.tvbox.osc.ui.adapter;

import android.os.Build;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.ui.activity.SettingActivity;

import java.util.ArrayList;

/**
 * @author pj567
 * @date :2020/12/23
 * @description:
 */
public class SettingMenuAdapter extends BaseQuickAdapter<SettingActivity.SettingMenu, BaseViewHolder> {
    public SettingMenuAdapter() {
        super(R.layout.item_setting_menu, new ArrayList<>());
    }

    @Override
    protected void convert(BaseViewHolder helper, SettingActivity.SettingMenu item) {
        helper.setText(R.id.tvName, item.name);
        ImageView tvIcon = helper.itemView.findViewById(R.id.tvIcon);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tvIcon.setForeground(item.drawable);
        } else {
            tvIcon.setBackground(item.drawable);
        }
    }
}