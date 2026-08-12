package com.srap.nga.ui.settings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.srap.nga.BuildConfig
import com.srap.nga.logic.model.GithubReleaseResponse
import com.srap.nga.logic.repository.NetworkRepo
import com.srap.nga.logic.state.LoadingState
import com.srap.nga.myApplication
import com.srap.nga.ui.base.BaseViewModel
import com.srap.nga.utils.ApkInstaller
import com.srap.nga.utils.ToastUtils
import com.srap.nga.utils.VersionUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppUpdateViewModel @Inject constructor(
    networkRepo: NetworkRepo,
) : BaseViewModel(networkRepo) {

    var isChecking by mutableStateOf(false)
        private set

    /** 检查到的新版本，非空时显示更新对话框 */
    var newRelease by mutableStateOf<GithubReleaseResponse?>(null)
        private set

    fun checkUpdate() {
        if (isChecking) return
        isChecking = true
        viewModelScope.launch {
            networkRepo.getLatestRelease().collect { state ->
                when (state) {
                    is LoadingState.Error -> {
                        ToastUtils.show("检查更新失败: ${state.errMsg}")
                    }
                    is LoadingState.Success -> {
                        val release = state.response
                        if (VersionUtils.isNewerVersion(release.tagName, BuildConfig.VERSION_NAME)) {
                            newRelease = release
                        } else {
                            ToastUtils.show("已是最新版本")
                        }
                    }
                }
                isChecking = false
            }
        }
    }

    var isDownloading by mutableStateOf(false)
        private set

    /** 下载进度 0..100 */
    var downloadProgress by mutableStateOf(0)
        private set

    /** 下载 APK 并调起系统安装器 */
    fun downloadAndInstall() {
        val url = newRelease?.apkDownloadUrl ?: return
        if (isDownloading) return
        isDownloading = true
        downloadProgress = 0
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val file = ApkInstaller.download(myApplication, url) { percent ->
                    downloadProgress = percent
                }
                ApkInstaller.install(myApplication, file)
            } catch (e: Exception) {
                ToastUtils.show("下载失败: ${e.message}")
            } finally {
                isDownloading = false
            }
        }
    }

    fun dismissUpdate() {
        newRelease = null
    }
}
