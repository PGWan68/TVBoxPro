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
import java.util.List;

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

        list.add(new DataSourceBean("添加更多数据源", ""));

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new V7LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new DataSourceAdapter(list);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener((adapter, view, position) -> {
            // 最后一个条目
            if (position == list.size() - 1) {
                // 展示一个弹窗，显示二维码
                showQrCodeDialog();
            } else {
                showSelectDialog(position, view);
            }
        });
    }

    private void showSelectDialog(int position, View view) {
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
                        sourceBean.setCurrent(sourceBean.equals(bean));
                    }

                    Hawk.put(HawkConfig.API_URL, bean.getUrl());
                    final List<DataSourceBean> lastData = adapter.getData();
                    lastData.remove(lastData.size() - 1);
                    Hawk.put(HawkConfig.API_LIST, lastData);

                    // TODO 会失去焦点
                    view.requestFocus();
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onClickBtnSetDelete() {
                    if (position == 0) {
                        ToastUtils.showShort("默认数据源不可删除");
                        return;
                    }

                    // 重新选择第一个
                    List<DataSourceBean> bean = adapter.getData();
                    bean.get(0).setCurrent(true);
                    adapter.remove(position);

                    Hawk.put(HawkConfig.API_URL, bean.get(0).getUrl());
                    // TODO 会崩溃
//                    final List<DataSourceBean> lastData = bean;
//                    lastData.remove(lastData.size() - 1);
//                    Hawk.put(HawkConfig.API_LIST, lastData);
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
        QRCodeDialog dialog = new QRCodeDialog(this);
        dialog.show();
    }

    private List<DataSourceBean> getDataFromLocal() {
        List<DataSourceBean> data = new ArrayList<>();

        TextView tvTitle = findViewById(R.id.tvTitle);

        if (type == 1) { // 直播
            tvTitle.setText("直播源");
            data.add(new DataSourceBean("IPTV加强版直播源", Constant.DEFAULT_LIVE_URL));
            return data;
        } else if (type == 2) { // 节目单
            tvTitle.setText("电子节目单");
            data.add(new DataSourceBean("范明明电子节目单", Constant.DEFAULT_EPG_URL));
            return data;
        } else { // 点播
            tvTitle.setText("点播源");

            List<DataSourceBean> list = Hawk.get(HawkConfig.API_LIST);

            list.add(new DataSourceBean("饭太硬二号", Constant.DEFAULT_VOD_URL));
            list.add(new DataSourceBean("饭太硬3号", Constant.DEFAULT_VOD_URL));
            list.add(new DataSourceBean("饭太硬4号", Constant.DEFAULT_VOD_URL));


            return list;

//            return Hawk.get(HawkConfig.API_LIST);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
