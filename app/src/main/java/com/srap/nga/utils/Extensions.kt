package com.srap.nga.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.*
import com.srap.nga.myApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
inline fun debounce(
    time: Long = 500L,
    crossinline callback: () -> Unit
): () -> Unit {
    var debounceJob by remember { mutableStateOf<Job?>(null) }

    return {
        debounceJob?.cancel()
        debounceJob = CoroutineScope(Dispatchers.Main).launch {
            delay(time)
            callback()
        }
    }
}

@Composable
inline fun throttle(
    time: Long = 500L,
    crossinline callback: () -> Unit
): () -> Unit {
    var lastExecutedTime by remember { mutableLongStateOf(0L) }

    return {
        val currentTime = System.currentTimeMillis()

        if (currentTime - lastExecutedTime >= time) {
            lastExecutedTime = currentTime
            callback()
        }
    }
}

fun isDarkTheme(): Boolean {
    val uiMode = myApplication.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
    return uiMode == Configuration.UI_MODE_NIGHT_YES
}


fun Context.copyText(text: String?, showToast: Boolean = true) {
    text?.let {
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        ClipData.newPlainText("copy text", it)?.let { clipboardManager.setPrimaryClip(it) }
        if (showToast)
            ToastUtil.show("已复制: $it")
    }
}
