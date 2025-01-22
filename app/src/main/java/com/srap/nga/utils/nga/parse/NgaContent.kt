package com.srap.nga.utils.nga.parse

sealed class NgaContent {
    data class Text(val content: String, val type: String = "text") : NgaContent()
    data class Image(val url: String, val type: String = "image") : NgaContent()
    data class Collapse(val name: String, val content: List<NgaContent>, val type: String = "collapse") : NgaContent()
    data class Quote(val content: List<NgaContent>, val type: String = "quote") : NgaContent()
}