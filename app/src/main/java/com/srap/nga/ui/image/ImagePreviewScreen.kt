package com.srap.nga.ui.image

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.rememberAsyncImagePainter
import com.jvziyaoyao.scale.image.pager.ImagePager
import com.jvziyaoyao.scale.zoomable.pager.rememberZoomablePagerState

@Composable
fun ImagePreviewScreen(
    image: String,
    images: List<String>,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit
) {
    val newImages = if (images.contains(image)) {
        images
    } else {
        listOf(image)
    }
    if (image.isNotEmpty()) {
        ImagePager(
            modifier = modifier.fillMaxSize(),
            pagerState = rememberZoomablePagerState(newImages.indexOf(image)) { newImages.size },
            imageLoader = { index ->
                val painter = rememberAsyncImagePainter(newImages[index], contentScale=contentScale)
                return@ImagePager Pair(painter, painter.intrinsicSize)
            },
        )
    }
}