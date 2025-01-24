package com.srap.nga.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
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
        ClipData.newPlainText("复制", it)?.let { clipboardManager.setPrimaryClip(it) }
        if (showToast)
            ToastUtil.show("已复制: $it")
    }
}

/**
 * 去除点击的涟漪(水波纹)效果
 */
inline fun Modifier.noRippleClickable(
    enabled: Boolean = true,
    crossinline onClick: () -> Unit
): Modifier = composed {
    clickable(
        enabled = enabled,
        indication = null, // 去除涟漪效果
        interactionSource = remember { MutableInteractionSource() },
        onClick = throttle {
            onClick()
        }
    )
}

/**
 * 将HTTP转为HTTPS
 */
fun String.toHttps() {
    this.replace("http://", "https://")
}