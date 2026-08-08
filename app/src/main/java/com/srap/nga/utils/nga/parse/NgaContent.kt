package com.srap.nga.utils.nga.parse

sealed class NgaContent {
    data class Text(val content: String, val type: String = "text") : NgaContent()
    data class Image(val url: String, val type: String = "image") : NgaContent()
    data class Video(val video: NgaVideo, val type: String = "video") : NgaContent()
    data class Collapse(val name: String, val content: List<NgaContent>, val type: String = "collapse") : NgaContent()
    data class Quote(val content: List<NgaContent>, val type: String = "quote") : NgaContent()
    data class Table(val table: NgaTable, val type: String = "table") : NgaContent()
    data class Poll(val poll: NgaPoll, val type: String = "poll") : NgaContent()
}

data class NgaVideo(
    val url: String,
    val posterUrl: String?,
    val mimeType: String? = null,
    val animationUrl: String? = null,
)

data class NgaPoll(
    val title: String,
    val maxSelect: Int?,
    val endTimestampSeconds: Long?,
    val digest: String,
    val items: List<NgaPollItem>,
)

data class NgaPollItem(
    val id: String?,
    val title: String,
    val voteCount: Int?,
    val percentage: Float?,
)

data class NgaTable(
    val leadingHtml: String,
    val cells: List<NgaTableCell>,
    val rowCount: Int,
    val columnCount: Int,
)

enum class NgaTableCellAlignment {
    START,
    CENTER,
    END,
}

data class NgaTableCell(
    val html: String,
    val row: Int,
    val column: Int,
    val rowSpan: Int,
    val columnSpan: Int,
    val isHeader: Boolean,
    val alignment: NgaTableCellAlignment = NgaTableCellAlignment.START,
)
