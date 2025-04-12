package com.srap.nga.ui.component

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jvziyaoyao.scale.image.previewer.TransformImageView
import com.jvziyaoyao.scale.zoomable.previewer.rememberPreviewerState
import kotlinx.coroutines.launch
import coil3.compose.rememberAsyncImagePainter
import com.srap.nga.ui.component.state.ImagePreview

/**
 * 预览图片列表组件
 */
@Composable
fun ImagesPreviewer(
    images: List<Pair<String, String>>,
) {
    val previewerState = rememberPreviewerState(
        pageCount = { images.size },
        getKey = { images[it].second }
    )
    val scope = rememberCoroutineScope()

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        images.forEachIndexed { index, image ->
            TransformImageView(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .weight(0.1f)
                    .height(130.dp)
                    .clickable {
                        scope.launch {
                            ImagePreview.openImage(images, previewerState)

                            // 点击事件触发动效
                            withFrameMillis {
                                scope.launch {
                                    previewerState.enterTransform(index)
                                }
                            }
                        }
                    },
                imageLoader = {
                    val key = images[index].second
                    val imageUrl = images[index].first
                    // 缩略图
                    val painter = rememberAsyncImagePainter(imageUrl)
                    // 必须依次返回key、图片数据、图片的尺寸
                    Triple(key, painter, painter.intrinsicSize)
                },
                transformState = previewerState,
            )
        }
    }
}

/**
 * 预览图片组件,指定打开的图片
 * @param image 需要预览的图片 Pair(url, id)
 * @param images 所有图片 [Pair(url, id)]
 */
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun ImagePreviewer(
    image: Pair<String, String>,
    images: List<Pair<String, String>>,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit
) {
    val dpSaver = Saver<Dp, Float>(
        save = { it.value },
        restore = { it.dp }
    )
    var index = -1
    var newImage = Pair(image.first, image.second.replace(".medium.jpg", ""))
    val newImages = if (images.any { it.second == newImage.second }) {
        images.mapIndexed { itIndex, item ->
            // 避免images有多个key一样的图片
            if (item.second == newImage.second && index == -1) {
                index = itIndex
                newImage = Pair(newImage.first, "${newImage.second}${itIndex}")
            }
            Pair(item.first, "${item.second}${itIndex}")
        }
    } else {
        index = 0
        listOf(newImage)
    }

    val previewerState = rememberPreviewerState(
        pageCount = { newImages.size },
        getKey = { newImages[it].second }
    )
    val scope = rememberCoroutineScope()
    val imageHeight = rememberSaveable(stateSaver = dpSaver) { mutableStateOf(0.dp) }
    val current = LocalDensity.current

    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth()
    ) {
        val maxWidth = maxWidth

        TransformImageView(
            modifier = modifier
                .fillMaxSize()
//                .shadow(4.dp, shape = RoundedCornerShape(14.dp))
                .clip(RoundedCornerShape(14.dp))
                .height(imageHeight.value)
                .width(maxWidth)
                .clickable {
                    scope.launch {
                        ImagePreview.openImage(newImages, previewerState)

                        // 点击事件触发动效
                        withFrameMillis {
                            scope.launch {
                                previewerState.enterTransform(index)
                            }
                        }
                    }
                },
            imageLoader = {
                // 缩略图
                val painter = rememberAsyncImagePainter(
                    newImage.first,
                    onSuccess = { state ->
                        if (state.painter.intrinsicSize != Size.Unspecified) {
                            // 计算图片的高度,让图片完整显示
                            val curImageWidth = with(current) { state.painter.intrinsicSize.width.toDp() }
                            val imageScale = curImageWidth / maxWidth
                            val curImageHeight = with(current) { state.painter.intrinsicSize.height.toDp() }
                            val newHeight = curImageHeight / imageScale
                            if (imageHeight.value == 0.dp) {
                                imageHeight.value = newHeight
                            }
                        }
                    },
                    contentScale = contentScale,
                )
                // 必须依次返回key、图片数据、图片的尺寸
                Triple(newImage.second, painter, painter.intrinsicSize)
            },
            transformState = previewerState,
        )
    }

}