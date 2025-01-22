package com.srap.nga.ui.image

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import coil3.compose.rememberAsyncImagePainter
import com.jvziyaoyao.scale.zoomable.pager.PagerGestureScope
import com.srap.nga.logic.providable.LocalPreviewerState
import kotlinx.coroutines.launch
import com.jvziyaoyao.scale.image.previewer.ImagePreviewer as ScaleImagePreviewer


@Composable
fun ImagePreviewTransformScreen() {
    val current = LocalPreviewerState.current
    var previewerState = current.previewerState.value
    val images = current.images
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
                    // 点击界面后关闭组件
                    previewerState.exitTransform()
                }
            }),
            imageLoader = {
                val painter = rememberAsyncImagePainter(images[it])
                return@ScaleImagePreviewer Pair(painter, painter.intrinsicSize)
            }
        )
    }
}