package com.github.tvbox.kotlin.activities

import android.app.PictureInPictureParams
import android.os.Build
import android.os.Bundle
import android.util.Rational
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.github.tvbox.kotlin.ui.LeanbackApp
import com.github.tvbox.kotlin.ui.screens.leanback.toast.LeanbackToastState
import com.github.tvbox.kotlin.ui.theme.LeanbackTheme
import com.github.tvbox.kotlin.ui.utils.HttpServer
import com.github.tvbox.kotlin.ui.utils.SP

class LeanbackActivity : BaseLiveActivity() {
    override fun onUserLeaveHint() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        if (!SP.uiPipMode) return

        enterPictureInPictureMode(
            PictureInPictureParams.Builder()
                .setAspectRatio(Rational(16, 9))
                .build()
        )
        super.onUserLeaveHint()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // 隐藏状态栏、导航栏
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, window.decorView).let { insetsController ->
                insetsController.hide(WindowInsetsCompat.Type.statusBars())
                insetsController.hide(WindowInsetsCompat.Type.navigationBars())
                insetsController.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }

            LeanbackTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                ) {
                    LeanbackApp(
                        onBackPressed = {
                            finish()
//                            exitProcess(0)
                        },
                    )
                }
            }
        }

        HttpServer.start(applicationContext, showToast = {
            LeanbackToastState.I.showToast(it, id = "httpServer")
        })
    }
}
