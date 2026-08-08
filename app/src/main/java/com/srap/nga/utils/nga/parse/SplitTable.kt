package com.srap.nga.utils.nga.parse

/** Splits HTML tables out before the remaining text is handed to Android's HtmlCompat parser. */
class SplitTable {
    val imageList = mutableListOf<String>()

    fun splitTable(text: String): List<NgaContent>? {
        val result = mutableListOf<NgaContent>()

        HtmlTableParser.split(text).forEach { segment ->
            when (segment) {
                is HtmlTableSegment.Html -> result.addHtml(segment.value)
                is HtmlTableSegment.Table -> {
                    val table = HtmlTableParser.parse(segment.value)
                    if (table != null) {
                        result.add(NgaContent.Table(table))
                    } else {
                        // Gracefully retain malformed/unsupported markup as ordinary post text.
                        result.addHtml(segment.value)
                    }
                }
            }
        }

        return result.takeIf { it.isNotEmpty() }
    }

    private fun MutableList<NgaContent>.addHtml(html: String) {
        val content = SplitImage().splitImage(html) ?: return
        content.filterIsInstance<NgaContent.Image>().forEach { imageList.add(it.url) }
        addAll(content)
    }
}
