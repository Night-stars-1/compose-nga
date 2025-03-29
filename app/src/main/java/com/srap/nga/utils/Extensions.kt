package com.srap.nga.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.srap.nga.constant.Constants.UTF8
import com.srap.nga.myApplication
import com.srap.nga.ui.component.state.SwipeableState
import com.srap.nga.ui.component.state.nestedScrollConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.URLDecoder
import java.net.URLEncoder

inline val String?.encode: String
    get() = URLEncoder.encode(this?.replace("%", "%25")?.replace("+", "%2B"), UTF8)
inline val String.decode: String
    get() = URLDecoder.decode(this, UTF8)

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
            ToastUtils.show("已复制: $it")
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
fun String.toHttps(): String {
    return this.replace("http://", "https://")
}

@Composable
fun Modifier.swipeable(
    state: SwipeableState,
    orientation: Orientation,
    enabled: Boolean = true,
    reverseDirection: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
) : Modifier {
    return nestedScroll(state.nestedScrollConnection)
        .draggable(
            orientation = orientation,
            enabled = enabled,
            reverseDirection = reverseDirection,
            interactionSource = interactionSource,
            startDragImmediately = !state.isVisible,
            onDragStopped = { state.performFling() },
            state = state.draggableState
        )
}