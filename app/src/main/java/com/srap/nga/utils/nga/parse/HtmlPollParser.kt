package com.srap.nga.utils.nga.parse

import org.jsoup.Jsoup
import org.jsoup.nodes.Element

/** Parses the voteBlock markup returned by the NGA app API. */
internal object HtmlPollParser {
    private val maxSelectPattern = Regex(""""max_select"\s*:\s*(\d+)""")
    private val endTimestampPattern = Regex(""""end"\s*:\s*(\d+)""")
    private val percentagePattern = Regex(
        """width\s*:\s*([0-9]+(?:\.[0-9]+)?)\s*%""",
        RegexOption.IGNORE_CASE,
    )

    fun parse(html: String): NgaPoll? {
        val block = Jsoup.parseBodyFragment(html).selectFirst(".voteBlock") ?: return null
        return parse(block)
    }

    fun parse(block: Element): NgaPoll? {
        if (!block.hasClass("voteBlock")) return null

        val metadata = block.attr("data-vote")
        val items = block.select(".voteSubTitle").mapNotNull { subtitle ->
            val title = subtitle.selectFirst("h2")?.text()?.trim()
                ?: subtitle.selectFirst("label")?.text()?.trim()
                ?: subtitle.text().trim()
            if (title.isBlank()) return@mapNotNull null

            val percentItem = subtitle.nextPercentItem()
            val voteCount = percentItem
                ?.selectFirst(".percentNum")
                ?.text()
                ?.replace(",", "")
                ?.trim()
                ?.toIntOrNull()
            val percentage = percentItem
                ?.selectFirst(".percentScroll")
                ?.attr("style")
                ?.let { style -> percentagePattern.find(style)?.groupValues?.get(1)?.toFloatOrNull() }

            NgaPollItem(
                id = subtitle.selectFirst("input[data-vid]")?.attr("data-vid")?.takeIf { it.isNotBlank() },
                title = title,
                voteCount = voteCount,
                percentage = percentage?.coerceIn(0f, 100f),
            )
        }

        return NgaPoll(
            title = block.selectFirst(".voteTotalTitle h2")?.text()?.trim()
                ?.takeIf { it.isNotBlank() }
                ?: "投票",
            maxSelect = maxSelectPattern.find(metadata)?.groupValues?.get(1)?.toIntOrNull(),
            endTimestampSeconds = endTimestampPattern.find(metadata)?.groupValues?.get(1)?.toLongOrNull(),
            digest = block.selectFirst(".voteDigest")
                ?.wholeText()
                ?.trim()
                ?.replace(Regex("""[ \t]+"""), " ")
                .orEmpty(),
            items = items,
        )
    }

    private fun Element.nextPercentItem(): Element? {
        var sibling = nextElementSibling()
        while (sibling != null && !sibling.hasClass("voteSubTitle")) {
            if (sibling.hasClass("percentItem")) return sibling
            sibling = sibling.nextElementSibling()
        }
        return null
    }
}
