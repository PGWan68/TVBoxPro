package com.github.tvbox.osc.ui.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.ViewPager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.api.ApiConfig;
import com.github.tvbox.osc.base.BaseActivity;
import com.github.tvbox.osc.base.BaseLazyFragment;
import com.github.tvbox.osc.ui.adapter.SettingMenuAdapter;
import com.github.tvbox.osc.ui.adapter.SettingPageAdapter;
import com.github.tvbox.osc.ui.fragment.ModelSettingFragment;
import com.github.tvbox.osc.ui.fragment.UserFragment;
import com.github.tvbox.osc.util.AppManager;
import com.github.tvbox.osc.util.HawkConfig;
import com.orhanobut.hawk.Hawk;
import com.owen.tvrecyclerview.widget.TvRecyclerView;
import com.owen.tvrecyclerview.widget.V7LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pj567
 * @date :2020/12/23
 * @description:
 */
public class SettingActivity extends BaseActivity {
    private TvRecyclerView mGridView;
    private ViewPager mViewPager;
    private SettingMenuAdapter sortAdapter;
    private SettingPageAdapter pageAdapter;
    private List<BaseLazyFragment> fragments = new ArrayList<>();
    private boolean sortChange = false;
    private int defaultSelected = 0;
    private int sortFocused = 0;
    private Handler mHandler = new Handler();
    private String homeSourceKey;
    private String currentApi;
    private String currentLive;
    private int homeRec;
    private int dnsOpt;

    private List<SettingMenu> sortList = new ArrayList<>();

    @Override
    protected int getLayoutResID() {
        return R.layout.activity_setting;
    }

    @Override
    protected void init() {
        initView();
        initData();
    }

    private void initView() {
        mGridView = findViewById(R.id.mGridView);
        mViewPager = findViewById(R.id.mViewPager);
        sortAdapter = new SettingMenuAdapter();
        mGridView.setAdapter(sortAdapter);
        mGridView.setLayoutManager(new V7LinearLayoutManager(this.mContext, 1, false));
        sortAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.tvName) {
                    if (view.getParent() != null) {
                        ((ViewGroup) view.getParent()).requestFocus();
                        sortFocused = position;
                        if (sortFocused != defaultSelected) {
                            defaultSelected = sortFocused;
                            mViewPager.setCurrentItem(sortFocused, false);
                        }
                    }
                }
            }
        });
        mGridView.setOnItemListener(new TvRecyclerView.OnItemListener() {
            @Override
            public void onItemPreSelected(TvRecyclerView parent, View itemView, int position) {
                if (itemView != null) {
//                    TextView tvName = itemView.findViewById(R.id.tvName);
//                    tvName.setTextColor(getResources().getColor(R.color.color_FFFFFF_70));
                }
            }

            @Override
            public void onItemSelected(TvRecyclerView parent, View itemView, int position) {
//                if (itemView != null) {
//                    sortChange = true;
//                    sortFocused = position;
//                    TextView tvName = itemView.findViewById(R.id.tvName);
//                    tvName.setTextColor(Color.WHITE);
//                }
                mViewPager.setCurrentItem(position, false);
            }

            @Override
            public void onItemClick(TvRecyclerView parent, View itemView, int position) {
                mViewPager.setCurrentItem(position, false);
            }
        });
    }

    private void initData() {
        currentApi = Hawk.get(HawkConfig.API_URL, "");
        currentLive = Hawk.get(HawkConfig.LIVE_URL, "");
        homeSourceKey = ApiConfig.get().getHomeSourceBean().getKey();
        homeRec = Hawk.get(HawkConfig.HOME_REC, 0);
        dnsOpt = Hawk.get(HawkConfig.DOH_URL, 0);


        sortList.add(new SettingMenu("应用", getDrawable(R.drawable.hm_drawer)));
        sortList.add(new SettingMenu("数据源", getDrawable(R.drawable.ic_data_24dp)));
        sortList.add(new SettingMenu("播放器", getDrawable(R.drawable.set_play)));
        sortList.add(new SettingMenu("系统", getDrawable(R.drawable.hm_settings)));
        sortList.add(new SettingMenu("关于", getDrawable(R.drawable.ic_info_24dp)));
        sortAdapter.setNewData(sortList);
        initViewPager();
    }

    private void initViewPager() {

        /*
         * 0：应用设置
         * 1: 数据源设置
         * 2: 播放器设置
         * 3：系统设置
         * 4:关于
         */
        for (int i = 0; i < sortList.size(); i++) {
            fragments.add(ModelSettingFragment.newInstance(i));
        }

        pageAdapter = new SettingPageAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(pageAdapter);
        mViewPager.setCurrentItem(0);
    }

    private final Runnable mDataRunnable = new Runnable() {
        @Override
        public void run() {
            if (sortChange) {
                sortChange = false;
                if (sortFocused != defaultSelected) {
                    defaultSelected = sortFocused;
//                    mViewPager.setCurrentItem(sortFocused, false);
                }
            }
        }
    };

    private final Runnable mDevModeRun = new Runnable() {
        @Override
        public void run() {
            devMode = "";
        }
    };


    public interface DevModeCallback {
        void onChange();
    }

    public static DevModeCallback callback = null;

    String devMode = "";

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            mHandler.removeCallbacks(mDataRunnable);
            int keyCode = event.getKeyCode();
            switch (keyCode) {
                case KeyEvent.KEYCODE_0:
                    mHandler.removeCallbacks(mDevModeRun);
                    devMode += "0";
                    mHandler.postDelayed(mDevModeRun, 200);
                    if (devMode.length() >= 4) {
                        if (callback != null) {
                            callback.onChange();
                        }
                    }
                    break;
            }
        } else if (event.getAction() == KeyEvent.ACTION_UP) {
            mHandler.postDelayed(mDataRunnable, 200);
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onBackPressed() {
        if ((homeSourceKey != null && !homeSourceKey.equals(Hawk.get(HawkConfig.HOME_API, ""))) || !currentApi.equals(Hawk.get(HawkConfig.API_URL, "")) || !currentLive.equals(Hawk.get(HawkConfig.LIVE_URL, "")) || homeRec != Hawk.get(HawkConfig.HOME_REC, 0) || dnsOpt != Hawk.get(HawkConfig.DOH_URL, 0)) {
            AppManager.getInstance().finishAllActivity();
            if (currentApi.equals(Hawk.get(HawkConfig.API_URL, "")) & (currentLive.equals(Hawk.get(HawkConfig.LIVE_URL, "")))) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("useCache", true);
                jumpActivity(HomeActivity.class, bundle);
            } else {
                jumpActivity(HomeActivity.class);
            }
        } else {
            super.onBackPressed();
        }
    }

    public class SettingMenu {
        public String name;
        public Drawable drawable;

        public SettingMenu(String name, Drawable drawable) {
            this.drawable = drawable;
            this.name = name;
        }

    }
}