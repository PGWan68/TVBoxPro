package com.github.tvbox.kotlin.ui.utils

import android.content.Context
import android.content.SharedPreferences
import com.github.tvbox.kotlin.data.utils.Constants
import com.github.tvbox.kotlin.ui.utils.SP.SharedPreferenceDelegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * 应用配置存储
 */
object SP {
    private const val SP_NAME = "tvboxpro"
    private const val SP_MODE = Context.MODE_PRIVATE
    private lateinit var sp: SharedPreferences

    fun getInstance(context: Context): SharedPreferences =
        context.getSharedPreferences(SP_NAME, SP_MODE)

    fun init(context: Context) {
        sp = getInstance(context)
    }

    /** ==================== 应用 ==================== */
    /** 开机自启 */
    var appBootLaunch by SharedPreferenceDelegates.boolean()

    /** 上一次最新版本 */
    var appLastLatestVersion by SharedPreferenceDelegates.string()

    /** 设备显示类型 */
    private var priAppDeviceDisplayType by SharedPreferenceDelegates.int()
    var appDeviceDisplayType: AppDeviceDisplayType
        get() = AppDeviceDisplayType.fromValue(priAppDeviceDisplayType)
        set(value) {
            priAppDeviceDisplayType = value.value
        }

    /** ==================== 调式 ==================== */
    /** 显示fps */
    var debugShowFps by SharedPreferenceDelegates.boolean()

    /** 播放器详细信息 */
    var debugShowVideoPlayerMetadata by SharedPreferenceDelegates.boolean()

    /** ==================== 直播源 ==================== */
    /** 上一次直播源序号 */
    var iptvLastIptvIdx by SharedPreferenceDelegates.int()

    /** 换台反转 */
    var iptvChannelChangeFlip by SharedPreferenceDelegates.boolean()

    /** 直播源精简 */
    var iptvSourceSimplify by SharedPreferenceDelegates.boolean()

    /** 直播源 url */
    var iptvSourceUrl by SharedPreferenceDelegates.string(defaultValue = Constants.IPTV_SOURCE_URL)

    /** 直播源缓存时间（毫秒） */
    var iptvSourceCacheTime by SharedPreferenceDelegates.long(defaultValue = Constants.IPTV_SOURCE_CACHE_TIME)

    /** 直播源可播放host列表 */
    var iptvPlayableHostList by SharedPreferenceDelegates.stringSet()

    /** 直播源历史列表 */
    var iptvSourceUrlHistoryList by SharedPreferenceDelegates.stringSet()

    /** 是否启用数字选台 */
    var iptvChannelNoSelectEnable by SharedPreferenceDelegates.boolean(defaultValue = true)

    /** 是否启用直播源频道收藏 */
    var iptvChannelFavoriteEnable by SharedPreferenceDelegates.boolean(defaultValue = true)

    /** 显示直播源频道收藏列表 */
    var iptvChannelFavoriteListVisible by SharedPreferenceDelegates.boolean()

    /** 直播源频道收藏列表 */
    var iptvChannelFavoriteList by SharedPreferenceDelegates.stringSet()

    /** ==================== 节目单 ==================== */
    /** 启用节目单 */
    var epgEnable by SharedPreferenceDelegates.boolean(defaultValue = true)

    /** 节目单 xml url */
    var epgXmlUrl by SharedPreferenceDelegates.string(defaultValue = Constants.EPG_XML_URL)

    /** 节目单刷新时间阈值（小时） */
    var epgRefreshTimeThreshold by SharedPreferenceDelegates.int(defaultValue = Constants.EPG_REFRESH_TIME_THRESHOLD)

    /** 节目单历史列表 */
    var epgXmlUrlHistoryList by SharedPreferenceDelegates.stringSet()

    /** ==================== 界面 ==================== */
    /** 显示节目进度 */
    var uiShowEpgProgrammeProgress by SharedPreferenceDelegates.boolean(defaultValue = true)

    /** 使用经典选台界面 */
    var uiUseClassicPanelScreen by SharedPreferenceDelegates.boolean(defaultValue = true)

    /** 界面密度缩放比例 */
    var uiDensityScaleRatio by SharedPreferenceDelegates.float(defaultValue = 1f)

    /** 界面字体缩放比例 */
    var uiFontScaleRatio by SharedPreferenceDelegates.float(defaultValue = 1f)

    /** 时间显示模式 */
    private var priUiTimeShowMode by SharedPreferenceDelegates.int(defaultValue = UiTimeShowMode.HIDDEN.value)
    var uiTimeShowMode: UiTimeShowMode
        get() = UiTimeShowMode.fromValue(priUiTimeShowMode)
        set(value) {
            priUiTimeShowMode = value.value;
        }

    /** 画中画模式 */
    var uiPipMode by SharedPreferenceDelegates.boolean()

    /** ==================== 更新 ==================== */
    /** 更新强提醒（弹窗形式） */
    var updateForceRemind by SharedPreferenceDelegates.boolean()

    /** ==================== 播放器 ==================== */
    /** 播放器 自定义ua */
    var videoPlayerUserAgent by SharedPreferenceDelegates.string(defaultValue = Constants.VIDEO_PLAYER_USER_AGENT)

    /** 播放器 加载超时 */
    var videoPlayerLoadTimeout by SharedPreferenceDelegates.long(defaultValue = Constants.VIDEO_PLAYER_LOAD_TIMEOUT)

    /** 播放器 画面比例 */
    private var priVideoPlayerAspectRatio by SharedPreferenceDelegates.int(defaultValue = VideoPlayerAspectRatio.ORIGINAL.value)
    var videoPlayerAspectRatio: VideoPlayerAspectRatio
        get() = VideoPlayerAspectRatio.fromValue(
            priVideoPlayerAspectRatio
        )
        set(value) {
            priVideoPlayerAspectRatio = value.value;
        }


    var pushToAddress by SharedPreferenceDelegates.string()
    var pushToPort by SharedPreferenceDelegates.string()


    /* 弹幕相关 */
    var danmuColor by SharedPreferenceDelegates.boolean()
    var danmuSizesCale by SharedPreferenceDelegates.float(defaultValue = 0.8f)
    var danmuAlpha by SharedPreferenceDelegates.float(defaultValue = 0.9f)
    var danmuSpeed by SharedPreferenceDelegates.float(defaultValue = 1.5f)
    var danmuMaxLine by SharedPreferenceDelegates.int(defaultValue = 3)
    var danmuOpen by SharedPreferenceDelegates.boolean(defaultValue = true)


    /* URL 相关配置 */
    var apiUrl by SharedPreferenceDelegates.string()
    var apiHistory by SharedPreferenceDelegates.stringSet()
    var liveUrl by SharedPreferenceDelegates.string()
    var liveHistory by SharedPreferenceDelegates.stringSet()
    var epgUrl by SharedPreferenceDelegates.string()
    var epgHistory by SharedPreferenceDelegates.stringSet()
    var proxyServer by SharedPreferenceDelegates.string()


    /* 播放器相关设置 */
    var showPreview by SharedPreferenceDelegates.boolean(defaultValue = true)
    var ijkCodec by SharedPreferenceDelegates.string("硬解码")    //IJK解码: 软解码, 硬解码
    var playType by SharedPreferenceDelegates.int()  //播放器: 0=系统, 1=IJK, 2=Exo, 3=MX, 4=Reex, 5=Kodi
    var playRender by SharedPreferenceDelegates.int()  //0 texture 2
    var playScale by SharedPreferenceDelegates.int()  //画面缩放: 0=默认, 1=16:9, 2=4:3, 3=填充, 4=原始, 5=裁剪
    var playTimesStep by SharedPreferenceDelegates.boolean()
    var picInPic by SharedPreferenceDelegates.boolean()  // true = on, false = off
    var videoPurify by SharedPreferenceDelegates.boolean(defaultValue = true)
    var ijkCachePlay by SharedPreferenceDelegates.boolean()
    var exoRenderer by SharedPreferenceDelegates.int()
    var exoRendererMode by SharedPreferenceDelegates.int(defaultValue = 1)
    var vodPlayerPreferred by SharedPreferenceDelegates.int()

    var dohUrl by SharedPreferenceDelegates.int()     //安全DNS: 0=关闭, 1=腾讯, 2=阿里, 3=360, 4=Google, 5=AdGuard, 6=Quad9
    var defaultParse by SharedPreferenceDelegates.string()
    var parseWebView by SharedPreferenceDelegates.boolean()    //嗅探Webview: true=系统自带, false=XWalkView
    var checkedSourcesForSearch by SharedPreferenceDelegates.boolean()
    var storageDriveSort by SharedPreferenceDelegates.int()
    var subtitleTextSize by SharedPreferenceDelegates.int()
    var subtitleTextStyle by SharedPreferenceDelegates.boolean()
    var subtitleTimeDelay by SharedPreferenceDelegates.boolean()
    var backgroundPlayType by SharedPreferenceDelegates.boolean()   //后台：0=关闭, 1=开启, 2=画中画
    var screenDisplay by SharedPreferenceDelegates.boolean()
    var searchFilterKey by SharedPreferenceDelegates.string()

    /* 设置相关 */
    var debugMode by SharedPreferenceDelegates.boolean()
    var homeApi by SharedPreferenceDelegates.string()
    var homeRec by SharedPreferenceDelegates.int()   //推荐: 0=豆瓣热播, 1=站点推荐, 2=观看历史
    var homeRecStyle by SharedPreferenceDelegates.boolean()     //网格展示数据源，true=列表，false=一行
    var homeNum by SharedPreferenceDelegates.int(defaultValue = 4)    //历史条数: 0=20条, 1=40条, 2=60条, 3=80条, 4=100条
    var showSource by SharedPreferenceDelegates.boolean() //数据源显示: true=开启, false=关闭
    var language by SharedPreferenceDelegates.int()        //语言: 0=中文, 1=英文
    var searchPosition by SharedPreferenceDelegates.boolean()
    var menuPosition by SharedPreferenceDelegates.boolean()
    var homeDefaultShow by SharedPreferenceDelegates.boolean()  //启动时直接进直播的开关
    var searchResultWidth by SharedPreferenceDelegates.int(defaultValue = -1) // 搜索结果view宽度
    var hotVodDelete by SharedPreferenceDelegates.boolean()
    var themeSelect by SharedPreferenceDelegates.boolean()            //主题: 0=奈飞, 1=哆啦, 2=百事, 3=鸣人, 4=小黄, 5=八神, 6=樱花
    var fastSearchMode by SharedPreferenceDelegates.boolean()
    var searchView by SharedPreferenceDelegates.int(defaultValue = 1)   //搜索展示: 0=文字列表, 1=缩略图


    /* 直播相关设置 */
    var liveChannelName by SharedPreferenceDelegates.string()
    var liveChannelGroupName by SharedPreferenceDelegates.string()
    var liveChannelReverse by SharedPreferenceDelegates.boolean()
    var liveCrossGroup by SharedPreferenceDelegates.boolean()
    var liveConnectTimeout by SharedPreferenceDelegates.int(defaultValue = 2)
    var liveShowNetSpeed by SharedPreferenceDelegates.boolean()
    var liveShowTime by SharedPreferenceDelegates.boolean()
    var liveSkipPassword by SharedPreferenceDelegates.boolean()
    var livePlayerType by SharedPreferenceDelegates.int(defaultValue = 1)   // 0 系统 1 ijk 2 exo


    enum class UiTimeShowMode(val value: Int) {
        /** 隐藏 */
        HIDDEN(0),

        /** 常显 */
        ALWAYS(1),

        /** 整点 */
        EVERY_HOUR(2),

        /** 半点 */
        HALF_HOUR(3);

        companion object {
            fun fromValue(value: Int): UiTimeShowMode {
                return entries.firstOrNull { it.value == value } ?: ALWAYS
            }
        }
    }

    enum class AppDeviceDisplayType(val value: Int) {
        /** tv端 */
        LEANBACK(0),

        /** 手机端 */
        MOBILE(1),

        /** 平板端 */
        PAD(2);

        companion object {
            fun fromValue(value: Int): AppDeviceDisplayType {
                return entries.firstOrNull { it.value == value } ?: LEANBACK
            }
        }
    }

    enum class VideoPlayerAspectRatio(val value: Int) {
        /** 原始 */
        ORIGINAL(0),

        /** 16:9 */
        SIXTEEN_NINE(1),

        /** 4:3 */
        FOUR_THREE(2),

        /** 自动拉伸 */
        AUTO(3);

        companion object {
            fun fromValue(value: Int): VideoPlayerAspectRatio {
                return entries.firstOrNull { it.value == value } ?: ORIGINAL
            }
        }
    }

    private object SharedPreferenceDelegates {

        fun int(defaultValue: Int = 0) = object : ReadWriteProperty<SP, Int> {

            override fun getValue(thisRef: SP, property: KProperty<*>): Int {
                return sp.getInt(property.name, defaultValue)
            }

            override fun setValue(thisRef: SP, property: KProperty<*>, value: Int) {
                sp.edit().putInt(property.name, value).apply()
            }
        }

        fun long(defaultValue: Long = 0L) = object : ReadWriteProperty<SP, Long> {

            override fun getValue(thisRef: SP, property: KProperty<*>): Long {
                return sp.getLong(property.name, defaultValue)
            }

            override fun setValue(thisRef: SP, property: KProperty<*>, value: Long) {
                sp.edit().putLong(property.name, value).apply()
            }
        }

        fun boolean(defaultValue: Boolean = false) = object : ReadWriteProperty<SP, Boolean> {
            override fun getValue(thisRef: SP, property: KProperty<*>): Boolean {
                return sp.getBoolean(property.name, defaultValue)
            }

            override fun setValue(thisRef: SP, property: KProperty<*>, value: Boolean) {
                sp.edit().putBoolean(property.name, value).apply()
            }
        }

        fun float(defaultValue: Float = 0.0f) = object : ReadWriteProperty<SP, Float> {
            override fun getValue(thisRef: SP, property: KProperty<*>): Float {
                return sp.getFloat(property.name, defaultValue)
            }

            override fun setValue(thisRef: SP, property: KProperty<*>, value: Float) {
                sp.edit().putFloat(property.name, value).apply()
            }
        }

        fun string(defaultValue: String? = "") = object : ReadWriteProperty<SP, String> {
            override fun getValue(thisRef: SP, property: KProperty<*>): String {
                return sp.getString(property.name, defaultValue) ?: ""
            }

            override fun setValue(thisRef: SP, property: KProperty<*>, value: String) {
                sp.edit().putString(property.name, value).apply()
            }
        }

        fun stringSet(defaultValue: Set<String> = emptySet()) =
            object : ReadWriteProperty<SP, Set<String>> {
                override fun getValue(thisRef: SP, property: KProperty<*>): Set<String> {
                    return sp.getStringSet(property.name, defaultValue) ?: emptySet();
                }

                override fun setValue(thisRef: SP, property: KProperty<*>, value: Set<String>) {
                    sp.edit().putStringSet(property.name, value).apply()
                }
            }
    }


}