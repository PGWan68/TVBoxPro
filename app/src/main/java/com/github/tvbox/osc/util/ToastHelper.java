package com.github.tvbox.osc.util;

import android.content.Context;
import android.widget.Toast;
import android.os.Looper;

import com.github.tvbox.kotlin.ui.utils.SP;

public class ToastHelper {

    public static void showToast(Context context, String text) {
        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }).start();
    }

    public static void debugToast(Context context, String text) {
        if ( SP.INSTANCE.getDebugMode()) {
            showToast(context, text);
        }
    }
}