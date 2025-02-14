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
        if (previewerState.canClose) {
            BackHandler {
                scope.launch {
                    // 返回键按下时退出变换
                    previewerState.exitTransform()
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
                val painter = rememberAsyncImagePainter(images[it].first)
                return@ScaleImagePreviewer Pair(painter, painter.intrinsicSize)
            }
        )
    }
}