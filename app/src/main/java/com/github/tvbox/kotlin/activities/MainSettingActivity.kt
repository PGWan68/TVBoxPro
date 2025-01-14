package com.github.tvbox.kotlin.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import com.github.tvbox.kotlin.ui.leanback.settings.LeanbackSettingsScreenPreview

class MainSettingActivity : BaseLiveActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            LeanbackSettingsScreenPreview();

        };
    }

}