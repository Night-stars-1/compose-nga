package com.srap.nga.ui.image

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import coil3.compose.rememberAsyncImagePainter
import com.jvziyaoyao.scale.zoomable.pager.PagerGestureScope
import com.srap.nga.ui.component.state.ImagePreview
import kotlinx.coroutines.launch
import com.jvziyaoyao.scale.image.previewer.ImagePreviewer as ScaleImagePreviewer

private const val TAG = "ImagePreviewTransformScreen"

@Composable
fun ImagePreviewTransformScreen() {
    var previewerState = ImagePreview.imagePreviewData
    val images = ImagePreview.images
    val scope = rememberCoroutineScope()

    if (previewerState != null) {
        // 只要预览处于活动状态（包括打开/关闭动画进行中）就拦截返回键，
        // 否则动画期间 canClose 为 false 时返回事件会落到 NavController，把下层页面弹掉。
        val previewActive = previewerState.visible ||
            previewerState.animating ||
            previewerState.visibleTarget == true
        BackHandler(enabled = previewActive) {
            // 正在播放关闭动画时不再重复触发
            if (previewerState.visibleTarget == false) return@BackHandler
            scope.launch {
                try {
                    // exitTransform 会先取消未完成的打开动画，再从当前位置收回
                    previewerState.exitTransform()
                } catch (e: IllegalStateException) {
                    Log.e(TAG, "播放退出动画出错: ${e.message}", e)
                    previewerState.close()
                }
            }
        }

        // 这里声明图片预览组件
        ScaleImagePreviewer(
            modifier = Modifier.fillMaxSize(),
            state = previewerState,
            detectGesture = PagerGestureScope(onTap = {
                scope.launch {
                    try {
                        // 点击界面后关闭组件
                        previewerState.exitTransform()
                    } catch (e: IllegalStateException) {
                        Log.e(TAG, "播放退出动画出错: ${e.message}", e)
                        previewerState.close()
                    }
                }
            }),
            imageLoader = {
                val imageUrl = images.getOrNull(it)?.first
                Log.i(TAG, "ImagePreviewTransformScreen: $it $imageUrl")
                val painter = rememberAsyncImagePainter(imageUrl)
                return@ScaleImagePreviewer Pair(painter, painter.intrinsicSize)
            }
        )
    }
}
