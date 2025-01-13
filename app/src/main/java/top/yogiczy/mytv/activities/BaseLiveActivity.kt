package top.yogiczy.mytv.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.github.tvbox.osc.util.AppManager

open class BaseLiveActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppManager.getInstance().addActivity(this);
    }

    override fun onDestroy() {
        super.onDestroy()
        AppManager.getInstance().finishActivity(this);
    }
}