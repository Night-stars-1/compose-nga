package com.srap.nga.logic.providable

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import com.jvziyaoyao.scale.zoomable.previewer.PreviewerState

class LocalState {
    val previewerState = mutableStateOf<PreviewerState?>(null)
    var images: List<String> = listOf()
    var image: String = ""
}

val LocalPreviewerState = compositionLocalOf<LocalState> { error("PreviewerState not provided") }
