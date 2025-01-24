package com.srap.nga.ui.login.qrcode

import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.srap.nga.logic.repository.NetworkRepo
import com.srap.nga.logic.state.LoadingState
import com.srap.nga.ui.base.BaseViewModel
import com.srap.nga.ui.navigateToHome
import com.srap.nga.utils.GlobalObject
import com.srap.nga.utils.StorageUtil
import com.srap.nga.utils.ToastUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

data class Key(
    val qrKey: String,
    val hiddenKey: String
)

@HiltViewModel
class QRCodeLoginViewModel @Inject constructor(
    networkRepo: NetworkRepo,
) : BaseViewModel(networkRepo) {
    private val TAG = javaClass.simpleName

    var result by mutableStateOf<Key?>(null)
    private var checkJob: Job? = null

    init {
        fetchData()
    }

    fun fetchData() {
        viewModelScope.launch {
            networkRepo.createQRCode()
                .collect { state ->
                    when (state) {
                        is LoadingState.Error -> {
                            ToastUtil.show(state.errMsg)
                        }

                        is LoadingState.Success -> {
                            if (state.response.result.isNotEmpty()) {
                                val keys = state.response.result[0].split(",")
                                result = Key(keys[0], keys[1])
                                startQRCodeCheckLoop()
                            }
                        }
                    }
                }
        }
    }

    private fun startQRCodeCheckLoop() {
        stopQRCodeCheckLoop()
        checkJob = viewModelScope.launch {
            val startTime = System.currentTimeMillis()
            while (isActive) {
                checkQRCode()

                if (System.currentTimeMillis() - startTime >= 60 * 1000) {
                    stopQRCodeCheckLoop() // 超时后停止循环
                    break
                }

                delay(3000)
            }
        }
    }

    private fun stopQRCodeCheckLoop() {
        checkJob?.cancel()
        checkJob = null
    }

    fun checkQRCode() {
        if (result != null) {
            viewModelScope.launch {
                networkRepo.qrCodeLogin(result!!.qrKey, result!!.hiddenKey)
                    .collect { state ->
                        when (state) {
                            is LoadingState.Error -> {
                                ToastUtil.show(state.errMsg)
                            }

                            is LoadingState.Success -> {
                                val result = state.response.result
                                if (result != null) {
                                    stopQRCodeCheckLoop()
                                    StorageUtil.Token = result.token
                                    StorageUtil.Uid = result.uid
                                    Handler(Looper.getMainLooper()).post {
                                        GlobalObject.navController?.navigateToHome()
                                    }
                                }
                            }
                        }
                    }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopQRCodeCheckLoop()
    }
}