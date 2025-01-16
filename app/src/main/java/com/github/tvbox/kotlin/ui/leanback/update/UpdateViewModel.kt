package com.github.tvbox.kotlin.ui.leanback.update

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.github.tvbox.kotlin.data.entities.UpdateRelease
import com.github.tvbox.kotlin.data.repositories.UpgradeRepository
import com.github.tvbox.kotlin.ui.leanback.toast.LeanbackToastProperty
import com.github.tvbox.kotlin.ui.leanback.toast.LeanbackToastState
import com.github.tvbox.kotlin.utils.Downloader
import com.github.tvbox.kotlin.utils.Logger
import java.io.File

class LeanBackUpdateViewModel : ViewModel() {
    private val log = Logger.create(javaClass.simpleName)

    private var _isChecking = false
    private var _isUpdating = false

    private var _isUpdateAvailable by mutableStateOf(false)
    val isUpdateAvailable get() = _isUpdateAvailable

    private var _updateDownloaded by mutableStateOf(false)
    val updateDownloaded get() = _updateDownloaded

    private var _latestRelease by mutableStateOf(UpdateRelease())
    val latestRelease get() = _latestRelease

    var showDialog by mutableStateOf(false)

    suspend fun checkUpdate(currentVersion: String) {
        if (_isChecking) return
        if (_isUpdateAvailable) return

        try {
            _isChecking = true
            _latestRelease = UpgradeRepository().latestRelease()
            _isUpdateAvailable = _latestRelease.buildHaveNewVersion
        } catch (e: Exception) {
            log.e("检查更新失败", e)
        } finally {
            _isChecking = false
        }
    }

    suspend fun downloadAndUpdate(latestFile: File) {
        if (!_isUpdateAvailable) return
        if (_isUpdating) return

        _isUpdating = true
        _updateDownloaded = false
        LeanbackToastState.Companion.I.showToast(
            "开始下载更新",
            LeanbackToastProperty.Duration.Custom(10_000),
        )

        try {
            Downloader.downloadTo(_latestRelease.downloadURL, latestFile.path,
                callback = object : Downloader.Callback {
                    override fun onProgress(progress: Int) {
                        LeanbackToastState.Companion.I.showToast(
                            "正在下载更新: $progress%",
                            LeanbackToastProperty.Duration.Custom(10_000),
                            "downloadProcess"
                        )
                    }
                })

            _updateDownloaded = true
            LeanbackToastState.Companion.I.showToast("下载更新成功")
        } catch (ex: Exception) {
            LeanbackToastState.Companion.I.showToast("下载更新失败")
        } finally {
            _isUpdating = false
        }
    }
}
