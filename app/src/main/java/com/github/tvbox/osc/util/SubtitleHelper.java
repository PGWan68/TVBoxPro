package com.github.tvbox.osc.util;

import android.app.Activity;
import android.content.Context;

import com.github.tvbox.kotlin.ui.utils.SP;
import com.github.tvbox.osc.R;
import com.github.tvbox.osc.subtitle.widget.SimpleSubtitleView;

public class SubtitleHelper {
    private static int[][] subtitleTextColor = null;

    // 初始化字幕颜色
    public static void initSubtitleColor(Context context) {
        int[] subtitleColor = context.getApplicationContext().getResources().getIntArray(R.array.subtitle_text_color);
        int[] shadowColor = context.getApplicationContext().getResources().getIntArray(R.array.subtitle_text_shadow_color);
        SubtitleHelper.subtitleTextColor = new int[][]{subtitleColor, shadowColor};
    }

    public static int[][] getSubtitleTextColor() {
        return subtitleTextColor;
    }

    public static int getSubtitleTextAutoSize(Activity activity) {
        double screenSqrt = ScreenUtils.getSqrt(activity);
        int subtitleTextSize = 20;
        if (screenSqrt > 7.0 && screenSqrt <= 13.0) {
            subtitleTextSize = 24;
        } else if (screenSqrt > 13.0 && screenSqrt <= 50.0) {
            subtitleTextSize = 36;
        } else if (screenSqrt > 50.0) {
            subtitleTextSize = 46;
        }
        return subtitleTextSize;
    }

    public static int getTextSize(Activity activity) {
        int autoSize = SP.INSTANCE.getSubtitleTextSize();
        if (autoSize == 0) {
            autoSize = getSubtitleTextAutoSize(activity);
            setTextSize(autoSize);
        }
        return autoSize;
    }

    public static void setTextSize(int size) {
        SP.INSTANCE.setSubtitleTextSize(size);
    }

    public static int getTimeDelay() {
        return SP.INSTANCE.getSubtitleTimeDelay();
    }

    public static void setTimeDelay(int delay) {
        SP.INSTANCE.setSubtitleTimeDelay(delay);
    }

    public static void upTextStyle(SimpleSubtitleView mSubtitleView) {
        upTextStyle(mSubtitleView, -1);
    }

    public static void upTextStyle(SimpleSubtitleView mSubtitleView, int style) {
        int colorIndex = style;
        if (style == -1) {
            colorIndex = SP.INSTANCE.getSubtitleTextStyle();
        } else {
            SP.INSTANCE.setSubtitleTextStyle(style);
        }
        int[][] subtitleTextColor = getSubtitleTextColor();
        mSubtitleView.setTextColor(subtitleTextColor[0][colorIndex]);
        mSubtitleView.setShadowLayer(10, 0, 0, subtitleTextColor[1][colorIndex]);
        // mSubtitleView.setBackGroundTextColor(subtitleTextColor[1][colorIndex]);
    }

    public static int getTextStyle() {
        return SP.INSTANCE.getSubtitleTextStyle();
    }

    public static int getTextStyleSize() {
        int[][] subtitleTextColor = getSubtitleTextColor();
        return Math.min(subtitleTextColor[0].length, subtitleTextColor[1].length);
    }
}
