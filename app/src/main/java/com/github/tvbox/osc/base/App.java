package com.github.tvbox.osc.base;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import androidx.core.os.HandlerCompat;
import androidx.multidex.MultiDexApplication;

import com.github.catvod.crawler.JsLoader;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.bean.DataSourceBean;
import com.github.tvbox.osc.callback.EmptyCallback;
import com.github.tvbox.osc.callback.LoadingCallback;
import com.github.tvbox.osc.data.AppDataManager;
import com.github.tvbox.osc.server.ControlManager;
import com.github.tvbox.osc.util.Constant;
import com.github.tvbox.osc.util.EpgUtil;
import com.github.tvbox.osc.util.FileUtils;
import com.github.tvbox.osc.util.HawkConfig;
import com.github.tvbox.osc.util.LOG;
import com.github.tvbox.osc.util.LocaleHelper;
import com.github.tvbox.osc.util.OkGoHelper;
import com.github.tvbox.osc.util.PlayerHelper;
import com.github.tvbox.osc.util.SubtitleHelper;
import com.hjq.permissions.XXPermissions;
import com.kingja.loadsir.core.LoadSir;
import com.orhanobut.hawk.Hawk;
import com.p2p.P2PClass;
import com.tencent.bugly.crashreport.CrashReport;
import com.whl.quickjs.android.QuickJSLoader;
import com.yanzhenjie.andserver.AndServer;
import com.yanzhenjie.andserver.Server;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import me.jessyan.autosize.AutoSizeConfig;
import me.jessyan.autosize.unit.Subunits;

/**
 * @author pj567
 * @date :2020/12/17
 * @description:
 */
public class App extends MultiDexApplication {
    private static App instance;
    private static P2PClass p;
    public static String burl;
    private static String dashData;
    public static ViewPump viewPump = null;
    private static Server server = null;
    private final Handler handler;

    public App() {
        instance = this;
        handler = HandlerCompat.createAsync(Looper.getMainLooper());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SubtitleHelper.initSubtitleColor(this);
        initParams();
        // takagen99 : Initialize Locale
        initLocale();
        // OKGo
        OkGoHelper.init(this);
        // 闭关检查模式
        XXPermissions.setCheckMode(false);
        // Get EPG Info
        EpgUtil.init();
        // 初始化Web服务器
        ControlManager.init(this);
        //初始化数据库
        AppDataManager.init();
        LoadSir.beginBuilder()
                .addCallback(new EmptyCallback())
                .addCallback(new LoadingCallback())
                .commit();
        AutoSizeConfig.getInstance().setCustomFragment(true).getUnitsManager()
                .setSupportDP(false)
                .setSupportSP(false)
                .setSupportSubunits(Subunits.MM);
        PlayerHelper.init();

        // Add JS support
        QuickJSLoader.init();

        updateCacheFile();

        initBugly();
    }

    private void initBugly() {
        CrashReport.initCrashReport(getApplicationContext(), "1efbca1553", true);
    }

    private void updateCacheFile() {

        // Delete Cache
        /*File dir = getCacheDir();
        FileUtils.recursiveDelete(dir);
        dir = getExternalCacheDir();
        FileUtils.recursiveDelete(dir);*/
        FileUtils.cleanPlayerCache();

        // add font support, my tv embed font not include emoji
        String extStorageDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        File fontFile = new File(extStorageDir + "/tvbox.ttf");
        if (fontFile.exists()) {
            viewPump = ViewPump.builder()
                    .addInterceptor(new CalligraphyInterceptor(
                            new CalligraphyConfig.Builder()
                                    .setDefaultFontPath(fontFile.getAbsolutePath())
                                    .setFontAttrId(R.attr.fontPath)
                                    .build()))
                    .build();
        }
    }


    public static P2PClass getp2p() {
        try {
            if (p == null) {
                p = new P2PClass(FileUtils.getExternalCachePath());
            }
            return p;
        } catch (Exception e) {
            LOG.e(e.toString());
            return null;
        }
    }


    private void initParams() {
        // Hawk
        Hawk.init(this).build();
        Hawk.put(HawkConfig.DEBUG_OPEN, false);

        // 首页选项
        putDefault(HawkConfig.HOME_SHOW_SOURCE, false);       //数据源显示: true=开启, false=关闭
        putDefault(HawkConfig.HOME_SEARCH_POSITION, false);  //按钮位置-搜索: true=上方, false=下方
        putDefault(HawkConfig.HOME_MENU_POSITION, false);     //按钮位置-设置: true=上方, false=下方
        putDefault(HawkConfig.HOME_REC, 0);                  //推荐: 0=豆瓣热播, 1=站点推荐, 2=观看历史
        putDefault(HawkConfig.HOME_NUM, 4);                  //历史条数: 0=20条, 1=40条, 2=60条, 3=80条, 4=100条
        putDefault(HawkConfig.HOME_REC_STYLE, true);          // 默认竖列展示

        // 播放器选项
        putDefault(HawkConfig.SHOW_PREVIEW, true);           //窗口预览: true=开启, false=关闭
        putDefault(HawkConfig.PLAY_SCALE, 0);                //画面缩放: 0=默认, 1=16:9, 2=4:3, 3=填充, 4=原始, 5=裁剪
        putDefault(HawkConfig.BACKGROUND_PLAY_TYPE, 0);      //后台：0=关闭, 1=开启, 2=画中画
        putDefault(HawkConfig.PLAY_TYPE, 1);                 //播放器: 0=系统, 1=IJK, 2=Exo, 3=MX, 4=Reex, 5=Kodi
        putDefault(HawkConfig.IJK_CODEC, "硬解码");           //IJK解码: 软解码, 硬解码
        putDefault(HawkConfig.PLAY_RENDER, 1);               //渲染控件: 0: TextureView, 1: SurfaceView, // 小米电视上使用TextureView会有一些高码视频解码失败

        // 系统选项
        putDefault(HawkConfig.HOME_LOCALE, 0);               //语言: 0=中文, 1=英文
        putDefault(HawkConfig.THEME_SELECT, 2);              //主题: 0=奈飞, 1=哆啦, 2=百事, 3=鸣人, 4=小黄, 5=八神, 6=樱花
        putDefault(HawkConfig.SEARCH_VIEW, 1);               //搜索展示: 0=文字列表, 1=缩略图
        putDefault(HawkConfig.PARSE_WEBVIEW, true);          //嗅探Webview: true=系统自带, false=XWalkView
        putDefault(HawkConfig.DOH_URL, 0);                   //安全DNS: 0=关闭, 1=腾讯, 2=阿里, 3=360, 4=Google, 5=AdGuard, 6=Quad9


        // 数据源
        // 点播源
        List<DataSourceBean> apiList = Hawk.get(HawkConfig.API_LIST);
        if (apiList == null || apiList.isEmpty()) {
            apiList = new ArrayList<>();
            apiList.add(new DataSourceBean("饭太硬加强版", Constant.DEFAULT_VOD_URL, true, true));
            apiList.add(new DataSourceBean("菜妮丝", Constant.DEFAULT_VOD_URL2, false, true));
            apiList.add(new DataSourceBean("老刘备", Constant.DEFAULT_VOD_URL3, false, true));
            Hawk.put(HawkConfig.API_LIST, apiList);
            Hawk.put(HawkConfig.API_URL, apiList.get(0).getUrl());
        }


        // 直播源
        List<DataSourceBean> liveList = Hawk.get(HawkConfig.LIVE_LIST);
        if (liveList == null || liveList.isEmpty()) {
            liveList = new ArrayList<>();
            liveList.add(new DataSourceBean("IPTV加强版直播源", Constant.DEFAULT_LIVE_URL, true, true));
            liveList.add(new DataSourceBean("局域网直播源", Constant.DEFAULT_LIVE_URL2, false, true));
            Hawk.put(HawkConfig.LIVE_LIST, liveList);
            Hawk.put(HawkConfig.LIVE_URL, liveList.get(0).getUrl());
        }

        // EPG源
        List<DataSourceBean> epgList = Hawk.get(HawkConfig.EPG_LIST);
        if (epgList == null || epgList.isEmpty()) {
            epgList = new ArrayList<>();
            epgList.add(new DataSourceBean("范明明电子节目单", Constant.DEFAULT_EPG_URL, true, true));
            Hawk.put(HawkConfig.EPG_LIST, epgList);
            Hawk.put(HawkConfig.EPG_URL, epgList.get(0).getUrl());
        }
    }

    private void initLocale() {
        if (Hawk.get(HawkConfig.HOME_LOCALE, 0) == 0) {
            LocaleHelper.setLocale(App.this, "zh");
        } else {
            LocaleHelper.setLocale(App.this, "");
        }
    }

    public static App getInstance() {
        return instance;
    }

    private void putDefault(String key, Object value) {
        if (!Hawk.contains(key)) {
            Hawk.put(key, value);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        JsLoader.load();
    }

    public void setDashData(String data) {
        dashData = data;
    }

    public String getDashData() {
        return dashData;
    }

    public static void startWebserver() {
        if (server != null) return;
        server = AndServer
                .webServer(instance)
                .port(12345)
                .timeout(60, TimeUnit.SECONDS)
                .listener(new Server.ServerListener() {
                    @Override
                    public void onStarted() {

                    }

                    @Override
                    public void onStopped() {

                    }

                    @Override
                    public void onException(Exception e) {

                    }
                }).build();
        server.startup();
    }

    public static void post(Runnable runnable) {
        getInstance().handler.post(runnable);
    }

    public static void post(Runnable runnable, long delayMillis) {
        getInstance().handler.removeCallbacks(runnable);
        if (delayMillis >= 0) getInstance().handler.postDelayed(runnable, delayMillis);
    }
}
