package com.srap.nga.ui.component.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.jvziyaoyao.scale.zoomable.previewer.PreviewerState

object ImagePreview {
    var imagePreviewData by mutableStateOf<PreviewerState?>(null)
        private set

    var images: List<Pair<String, String>> = listOf()
        private set

    fun openImage(images: List<Pair<String, String>>, imagePreviewData: PreviewerState) {
        this.images = images
        this.imagePreviewData = imagePreviewData
    }
}