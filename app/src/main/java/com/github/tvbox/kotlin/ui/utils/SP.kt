package com.github.tvbox.kotlin.ui.utils

import android.content.Context
import android.content.SharedPreferences
import com.github.tvbox.kotlin.data.utils.Constants
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

    enum class KEY {
        /** ==================== 应用 ==================== */
        /** 开机自启 */
        APP_BOOT_LAUNCH,

        /** 上一次最新版本 */
        APP_LAST_LATEST_VERSION,

        /** 设备显示类型 */
        APP_DEVICE_DISPLAY_TYPE,

        /** ==================== 调式 ==================== */
        /** 显示fps */
        DEBUG_SHOW_FPS,

        /** 播放器详细信息 */
        DEBUG_SHOW_VIDEO_PLAYER_METADATA,

        /** ==================== 直播源 ==================== */
        /** 上一次直播源序号 */
        IPTV_LAST_IPTV_IDX,

        /** 换台反转 */
        IPTV_CHANNEL_CHANGE_FLIP,

        /** 直播源精简 */
        IPTV_SOURCE_SIMPLIFY,

        /** 直播源url */
        IPTV_SOURCE_URL,

        /** 直播源缓存时间（毫秒） */
        IPTV_SOURCE_CACHE_TIME,

        /** 直播源可播放host列表 */
        IPTV_PLAYABLE_HOST_LIST,

        /** 直播源历史列表 */
        IPTV_SOURCE_URL_HISTORY_LIST,

        /** 是否启用数字选台 */
        IPTV_CHANNEL_NO_SELECT_ENABLE,

        /** 是否启用直播源频道收藏 */
        IPTV_CHANNEL_FAVORITE_ENABLE,

        /** 显示直播源频道收藏列表 */
        IPTV_CHANNEL_FAVORITE_LIST_VISIBLE,

        /** 直播源频道收藏列表 */
        IPTV_CHANNEL_FAVORITE_LIST,

        /** ==================== 节目单 ==================== */
        /** 启用节目单 */
        EPG_ENABLE,

        /** 节目单 xml url */
        EPG_XML_URL,

        /** 节目单刷新时间阈值（小时） */
        EPG_REFRESH_TIME_THRESHOLD,

        /** 节目单历史列表 */
        EPG_XML_URL_HISTORY_LIST,

        /** ==================== 界面 ==================== */
        /** 显示节目进度 */
        UI_SHOW_EPG_PROGRAMME_PROGRESS,

        /** 使用经典选台界面 */
        UI_USE_CLASSIC_PANEL_SCREEN,

        /** 界面密度缩放比例 */
        UI_DENSITY_SCALE_RATIO,

        /** 界面字体缩放比例 */
        UI_FONT_SCALE_RATIO,

        /** 时间显示模式 */
        UI_TIME_SHOW_MODE,

        /** 画中画模式 */
        UI_PIP_MODE,

        /** ==================== 更新 ==================== */
        /** 更新强提醒（弹窗形式） */
        UPDATE_FORCE_REMIND,

        /** ==================== 播放器 ==================== */
        /** 播放器 自定义ua */
        VIDEO_PLAYER_USER_AGENT,

        /** 播放器 加载超时 */
        VIDEO_PLAYER_LOAD_TIMEOUT,

        /** 播放器 画面比例 */
        VIDEO_PLAYER_ASPECT_RATIO,
    }

    /** ==================== 应用 ==================== */
    /** 开机自启 */
    var appBootLaunch by SharedPreferenceDelegates.boolean()

    /** 上一次最新版本 */
    var appLastLatestVersion by SharedPreferenceDelegates.string()

    /** 设备显示类型 */
    var appDeviceDisplayType: AppDeviceDisplayType
        get() = AppDeviceDisplayType.fromValue(sp.getInt(KEY.APP_DEVICE_DISPLAY_TYPE.name, 0))
        set(value) = sp.edit().putInt(KEY.APP_DEVICE_DISPLAY_TYPE.name, value.value).apply()

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
    var uiTimeShowMode: UiTimeShowMode
        get() = UiTimeShowMode.fromValue(sp.getInt(KEY.UI_TIME_SHOW_MODE.name, 0))
        set(value) = sp.edit().putInt(KEY.UI_TIME_SHOW_MODE.name, value.value).apply()

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
    var videoPlayerAspectRatio: VideoPlayerAspectRatio
        get() = VideoPlayerAspectRatio.fromValue(
            sp.getInt(KEY.VIDEO_PLAYER_ASPECT_RATIO.name, VideoPlayerAspectRatio.ORIGINAL.value)
        )
        set(value) = sp.edit().putInt(KEY.VIDEO_PLAYER_ASPECT_RATIO.name, value.value).apply()

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