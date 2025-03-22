package com.github.tvbox.osc.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.base.BaseActivity;
import com.github.tvbox.osc.bean.DataSourceBean;
import com.github.tvbox.osc.event.RefreshEvent;
import com.github.tvbox.osc.ui.adapter.DataSourceAdapter;
import com.github.tvbox.osc.ui.dialog.DataSourceSelectDialog;
import com.github.tvbox.osc.ui.dialog.QRCodeDialog;
import com.github.tvbox.osc.util.Constant;
import com.github.tvbox.osc.util.HawkConfig;
import com.github.tvbox.osc.util.LOG;
import com.orhanobut.hawk.Hawk;
import com.owen.tvrecyclerview.widget.TvRecyclerView;
import com.owen.tvrecyclerview.widget.V7LinearLayoutManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class DataSourceActivity extends BaseActivity {

    private static final String TAG_TYPE = "TAG_TYPE";

    private int type = 0;

    private DataSourceAdapter adapter;
    private TvRecyclerView recyclerView;

    public static void launchActivity(int type) {
        Bundle bundle = new Bundle();
        bundle.putInt(TAG_TYPE, type);
        ActivityUtils.startActivity(bundle, DataSourceActivity.class);
    }

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_data_source;
    }

    @Override
    protected void init() {

        EventBus.getDefault().register(this);

        Intent intent = getIntent();
        if (intent != null) {
            type = intent.getIntExtra(TAG_TYPE, 0);
        }

        List<DataSourceBean> list = getDataFromLocal();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new V7LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new DataSourceAdapter(list);
        recyclerView.setAdapter(adapter);

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isCurrent()) {
                recyclerView.setSelection(i);
                break;
            }
        }

        // 展示一个弹窗，显示二维码
        findViewById(R.id.tvAdd).setOnClickListener(v -> {
            showQrCodeDialog();
        });

        adapter.setOnItemClickListener((adapter, view, position) -> {
            showSelectDialog(position);
        });
    }

    private void showSelectDialog(int position) {
        DataSourceBean bean = adapter.getItem(position);

        if (bean != null) {
            DataSourceSelectDialog dialog = new DataSourceSelectDialog(this, bean.getName());
            dialog.show();
            dialog.setOnClickBtnListener(new DataSourceSelectDialog.OnClickBtnListener() {
                @Override
                public void onClickBtnSetCurrent() {
                    if (bean.isCurrent()) {
                        return;
                    }

                    for (DataSourceBean sourceBean : adapter.getData()) {
                        sourceBean.setCurrent(Objects.equals(sourceBean.getUrl(), bean.getUrl()));
                    }

                    adapter.notifyDataSetChanged();
                    recyclerView.setSelection(position);

                    saveDataSource(bean.getUrl(), adapter.getData());
                }

                @Override
                public void onClickBtnSetDelete() {
                    if (bean.isDefault()) {
                        ToastUtils.showShort("默认数据源不可删除");
                        return;
                    }
                    adapter.remove(position);

                    List<DataSourceBean> beanList = adapter.getData();
                    // 如果删除的当前正好是选中的，则重新选择第一个
                    if (bean.isCurrent()) {
                        beanList.get(0).setCurrent(true);
                        Hawk.put(HawkConfig.API_URL, beanList.get(0).getUrl());
                        recyclerView.setSelection(0);
                    } else {
                        // 焦点选中之前
                        for (int i = 0; i < beanList.size(); i++) {
                            if (beanList.get(i).isCurrent()) {
                                recyclerView.setSelection(i);
                            }
                        }
                    }

                    adapter.notifyDataSetChanged();
                    Hawk.put(HawkConfig.API_LIST, beanList);
                }
            });
        }


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(RefreshEvent event) {
        if (event.type == RefreshEvent.TYPE_API_URL_CHANGE) {
//            inputApi.setText((String) event.obj);
            LOG.i("测试：收到数据 = " + event.obj);
        }
        if (event.type == RefreshEvent.TYPE_LIVE_URL_CHANGE) {
//            inputLive.setText((String) event.obj);
        }
        if (event.type == RefreshEvent.TYPE_EPG_URL_CHANGE) {
//            inputEPG.setText((String) event.obj);
        }
    }

    private void showQrCodeDialog() {
        new QRCodeDialog(this).show();
    }

    private List<DataSourceBean> getDataFromLocal() {
        TextView tvTitle = findViewById(R.id.tvTitle);

        if (type == 1) { // 直播
            tvTitle.setText("直播源");
            return Hawk.get(HawkConfig.LIVE_LIST);
        } else if (type == 2) { // 节目单
            tvTitle.setText("电子节目单");
            return Hawk.get(HawkConfig.EPG_LIST);
        } else { // 点播
            tvTitle.setText("点播源");
            return Hawk.get(HawkConfig.API_LIST);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void saveDataSource(String url, List<DataSourceBean> list) {
        if (type == 1) { // 直播
            Hawk.put(HawkConfig.LIVE_URL, url);
            Hawk.put(HawkConfig.LIVE_LIST, list);
        } else if (type == 2) { // 节目单
            Hawk.put(HawkConfig.EPG_URL, url);
            Hawk.put(HawkConfig.EPG_LIST, list);
        } else {
            Hawk.put(HawkConfig.API_URL, url);
            Hawk.put(HawkConfig.API_LIST, list);
        }


    }
}
