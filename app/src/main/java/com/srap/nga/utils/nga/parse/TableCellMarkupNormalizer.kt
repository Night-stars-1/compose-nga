package com.srap.nga.utils.nga.parse

import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode

internal data class NormalizedTableCellMarkup(
    val html: String,
    val alignment: NgaTableCellAlignment,
)

/**
 * Removes NGA's presentation-only cell wrapper. HtmlCompat treats div/p as block elements and
 * appends a trailing line break, which otherwise leaves a large gap below image-only cells.
 */
internal object TableCellMarkupNormalizer {
    private val textAlignPattern = Regex(
        """(?:^|;)\s*text-align\s*:\s*(left|start|center|right|end)\b""",
        RegexOption.IGNORE_CASE,
    )

    fun normalize(cell: Element): NormalizedTableCellMarkup {
        val wrapper = cell.childNodes()
            .filterNot { node -> node is TextNode && node.text().isBlank() }
            .singleOrNull()
            .let { it as? Element }
            ?.takeIf { element ->
                element.normalName() == "div" || element.normalName() == "p"
            }
        val content = wrapper ?: cell
        return NormalizedTableCellMarkup(
            html = content.html(),
            alignment = wrapper.readAlignment()
                ?: cell.readAlignment()
                ?: NgaTableCellAlignment.START,
        )
    }

    private fun Element?.readAlignment(): NgaTableCellAlignment? {
        this ?: return null
        val value = attr("align").takeIf { it.isNotBlank() }
            ?: textAlignPattern.find(attr("style"))?.groupValues?.get(1)
            ?: return null
        return when (value.lowercase()) {
            "center" -> NgaTableCellAlignment.CENTER
            "right", "end" -> NgaTableCellAlignment.END
            "left", "start" -> NgaTableCellAlignment.START
            else -> null
        }
    }
}
