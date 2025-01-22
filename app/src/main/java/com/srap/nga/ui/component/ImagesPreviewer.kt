package com.srap.nga.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.jvziyaoyao.scale.image.previewer.TransformImageView
import com.jvziyaoyao.scale.zoomable.previewer.rememberPreviewerState
import kotlinx.coroutines.launch
import coil3.compose.rememberAsyncImagePainter
import com.srap.nga.logic.providable.LocalPreviewerState

@Composable
fun ImagesPreviewer(
    images: List<String>,
) {
    val current = LocalPreviewerState.current
    val previewerState = rememberPreviewerState(
        pageCount = { images.size },
        getKey = { images[it] }
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
                            current.previewerState.value = previewerState
                            current.images = images

                            // 点击事件触发动效
                            withFrameMillis {
                                scope.launch {
                                    previewerState.enterTransform(index)
                                }
                            }
                        }
                    },
                imageLoader = {
                    val key = images[index]
                    val imageDrawableId = images[index]
                    // 缩略图
                    val painter = rememberAsyncImagePainter(imageDrawableId)
                    // 必须依次返回key、图片数据、图片的尺寸
                    Triple(key, painter, painter.intrinsicSize)
                },
                transformState = previewerState,
            )
        }
    }
}

@Composable
fun ImagePreviewer(
    image: String,
    images: List<String>,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    isTransform: Boolean = false,
) {
    val newImages = if (images.contains(image)) {
        images
    } else {
        listOf(image)
    }

    val current = LocalPreviewerState.current
    val previewerState = rememberPreviewerState(
        pageCount = { newImages.size },
        getKey = { newImages[it] }
    )
    val index = newImages.indexOf(image)
    val scope = rememberCoroutineScope()

    if (isTransform) {
        TransformImageView(
            modifier = modifier
                .clickable {
                    scope.launch {
                        current.previewerState.value = previewerState
                        current.images = newImages

                        // 点击事件触发动效
                        withFrameMillis {
                            scope.launch {
                                previewerState.enterTransform(index)
                            }
                        }
                    }
                },
            imageLoader = {
                val key = newImages[index]
                val imageDrawableId = newImages[index]
                // 缩略图
                val painter = rememberAsyncImagePainter(
                    imageDrawableId,
                    contentScale = contentScale
                )
                // 必须依次返回key、图片数据、图片的尺寸
                Triple(key, painter, painter.intrinsicSize)
            },
            transformState = previewerState,
        )
    } else {
        AsyncImage(
            model = image,
            contentDescription = "图片",
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    scope.launch {
                        current.previewerState.value = previewerState
                        current.images = newImages

                        // 点击事件触发动效
                        withFrameMillis {
                            scope.launch {
                                previewerState.open(index)
                            }
                        }
                    }
                },
            contentScale = ContentScale.FillWidth
        )
    }
}