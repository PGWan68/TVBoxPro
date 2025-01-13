package top.yogiczy.mytv.activities

import android.content.Intent
import android.os.Bundle
import top.yogiczy.mytv.ui.utils.SP

class LiveMainActivity : BaseLiveActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activityClass = when (SP.appDeviceDisplayType) {
            SP.AppDeviceDisplayType.LEANBACK -> LeanbackActivity::class.java
            SP.AppDeviceDisplayType.MOBILE -> LiveMobileActivity::class.java
            SP.AppDeviceDisplayType.PAD -> PadActivity::class.java
        }

        // TODO 切换时变化生硬
        startActivity(Intent(this, activityClass))

        finish()
    }
}
