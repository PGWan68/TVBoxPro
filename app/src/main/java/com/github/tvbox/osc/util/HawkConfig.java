package com.github.tvbox.osc.util;

import com.orhanobut.hawk.Hawk;

/**
 * @author pj567
 * @date :2020/12/23
 * @description:
 */
public class HawkConfig {


    // Settings
    public static final String HOME_REC = "home_rec";                    // 0 豆瓣 1 推荐 2 历史


    public static final String HOME_SEARCH_POSITION = "search_position"; // true=Up, false=Down
    public static final String HOME_MENU_POSITION = "menu_position";     // true=Up, false=Down

    // Player Settings
    public static final String IJK_CODEC = "ijk_codec";
    public static final String PLAY_TYPE = "play_type";     //0 系统 1 ijk 2 exo 10 MXPlayer
    public static final String PLAY_RENDER = "play_render"; //0 texture 2
    public static final String PLAY_SCALE = "play_scale";   //
    public static final String PLAY_TIME_STEP = "play_time_step";

    // Other Settings
    public static final String DOH_URL = "doh_url";         // DNS
    public static final String PARSE_WEBVIEW = "parse_webview"; // true 系统 false xwalk
    public static final String SEARCH_VIEW = "search_view";     // 0 文字列表 1 缩略图
    public static final String SOURCES_FOR_SEARCH = "checked_sources_for_search";
    public static final String SUBTITLE_TEXT_SIZE = "subtitle_text_size";
    public static final String SUBTITLE_TEXT_STYLE = "subtitle_text_style";
    public static final String SUBTITLE_TIME_DELAY = "subtitle_time_delay";
    public static final String THEME_SELECT = "theme_select";
    public static final String BACKGROUND_PLAY_TYPE = "background_play_type";
    public static final String FAST_SEARCH_MODE = "fast_search_mode";

}
