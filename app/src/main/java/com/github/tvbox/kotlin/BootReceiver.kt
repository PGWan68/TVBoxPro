package com.github.tvbox.kotlin

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.github.tvbox.kotlin.activities.LeanbackActivity
import com.github.tvbox.kotlin.ui.utils.SP

/**
 * 开机自启动监听
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            if (SP.appBootLaunch) {
                context.startActivity(Intent(context, LeanbackActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            }
        }
    }
}
