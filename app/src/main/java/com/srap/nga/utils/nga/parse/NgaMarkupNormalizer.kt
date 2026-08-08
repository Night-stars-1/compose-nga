package com.srap.nga.utils.nga.parse

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.LinkedHashMap

/** Normalizes NGA-specific BBCode and CSS classes into markup supported by HtmlCompat. */
internal object NgaMarkupNormalizer {
    private const val ADAPTED_STYLE_CACHE_LIMIT = 256
    private val adaptedStyleCache = object :
        LinkedHashMap<String, String>(ADAPTED_STYLE_CACHE_LIMIT, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, String>?): Boolean =
            size > ADAPTED_STYLE_CACHE_LIMIT
    }

    private val multilineOptions = setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)
    private val fontSizePattern = Regex(
        """font-size\s*:\s*(\d+(?:\.\d+)?)\s*%""",
        RegexOption.IGNORE_CASE,
    )
    private val lineHeightProperty = Regex("""^\s*line-height\s*:""", RegexOption.IGNORE_CASE)
    private val fontSizeProperty = Regex("""^\s*font-size\s*:""", RegexOption.IGNORE_CASE)

    private val classColors = linkedMapOf(
        "red" to "#D32F2F",
        "crimson" to "#C2185B",
        "orangered" to "#E64A19",
        "orange" to "#F57C00",
        "yellow" to "#F9A825",
        "green" to "#388E3C",
        "darkgreen" to "#2E7D32",
        "blue" to "#1976D2",
        "darkblue" to "#1565C0",
        "purple" to "#7B1FA2",
        "gray" to "#757575",
        "grey" to "#757575",
        "silver" to "#757575",
    )

    fun normalizeLinks(html: String): String {
        var result = html
        result = result.replace(
            Regex("""\[url=(https?://[^]]+)](.*?)\[/url]""", multilineOptions),
            """<a href="$1">$2</a>""",
        )
        result = result.replace(
            Regex("""\[url](https?://.*?)\[/url]""", multilineOptions),
            """<a href="$1">$1</a>""",
        )
        result = result.replace(
            Regex("""\[tid=(\d+)](.*?)\[/tid]""", multilineOptions),
            """<a href="https://bbs.nga.cn/read.php?tid=$1">$2</a>""",
        )
        result = result.replace(
            Regex("""\[pid=(\d+),(\d+)(?:,\d+)?](.*?)\[/pid]""", multilineOptions),
            """<a href="https://bbs.nga.cn/read.php?tid=$2&amp;pid=$1">$3</a>""",
        )
        result = result.replace(
            Regex("""\[uid=(\d+)](.*?)\[/uid]""", multilineOptions),
            """<a href="https://bbs.nga.cn/nuke.php?func=ucp&amp;uid=$1">$2</a>""",
        )
        result = result.replace(
            Regex("""\[flash](https?://.*?)\[/flash]""", multilineOptions),
            """<a href="$1">点击查看视频</a>""",
        )
        return result
    }

    fun adaptStyles(html: String): String {
        synchronized(adaptedStyleCache) {
            adaptedStyleCache[html]?.let { return it }
        }
        val adapted = parseStyledBody(html).html()
        synchronized(adaptedStyleCache) {
            adaptedStyleCache[html] = adapted
        }
        return adapted
    }

    /**
     * Returns a styled body so table parsing can reuse the same DOM instead of reparsing every cell.
     */
    fun parseStyledBody(html: String): Element {
        var result = html
        result = replaceSimpleBbCode(result, "b", "b")
        result = replaceSimpleBbCode(result, "i", "i")
        result = replaceSimpleBbCode(result, "u", "u")
        result = replaceSimpleBbCode(result, "del", "del")
        result = result.replace(
            Regex("""\[color=([#a-zA-Z0-9]+)](.*?)\[/color]""", multilineOptions),
            """<font color="$1">$2</font>""",
        )
        result = Regex("""\[size=(\d+(?:\.\d+)?)%](.*?)\[/size]""", multilineOptions)
            .replace(result) { match ->
                wrapForPercentage(
                    html = match.groupValues[2],
                    percentage = match.groupValues[1].toFloatOrNull() ?: 100f,
                )
            }

        val document = Jsoup.parseBodyFragment(result)
        document.outputSettings().prettyPrint(false)
        val body = document.body()
        adaptElementStyles(body)
        return body
    }

    private fun adaptElementStyles(body: Element) {
        body.select("[style]").toList().forEach { element ->
            val style = element.attr("style")
            fontSizePattern.find(style)?.groupValues?.get(1)?.toFloatOrNull()?.let { percentage ->
                element.html(wrapForPercentage(element.html(), percentage))
            }
            val remainingStyle = style.split(';')
                .map { it.trim() }
                .filter { it.isNotBlank() }
                .filterNot {
                    fontSizeProperty.containsMatchIn(it) || lineHeightProperty.containsMatchIn(it)
                }
            if (remainingStyle.isEmpty()) {
                element.removeAttr("style")
            } else {
                element.attr("style", remainingStyle.joinToString(";"))
            }
            if (style.contains("text-align:center", ignoreCase = true) ||
                style.contains("text-align: center", ignoreCase = true)
            ) {
                element.attr("align", "center")
                element.attr("style", "text-align:center")
            }
        }

        classColors.forEach { (className, color) ->
            body.getElementsByClass(className).toList().forEach { element ->
                applyColor(element, color)
                element.removeClass(className)
                if (element.classNames().isEmpty()) element.removeAttr("class")
            }
        }

        body.select("h4.subtitle").toList().forEach { title ->
            title.removeClass("subtitle")
            if (title.classNames().isEmpty()) title.removeAttr("class")
        }
    }

    private fun replaceSimpleBbCode(html: String, bbCode: String, htmlTag: String): String =
        Regex("""\[$bbCode](.*?)\[/$bbCode]""", multilineOptions)
            .replace(html, "<$htmlTag>$1</$htmlTag>")

    private fun wrapForPercentage(html: String, percentage: Float): String {
        val (opening, closing) = when {
            percentage < 80f -> "<small><small>" to "</small></small>"
            percentage < 100f -> "<small>" to "</small>"
            percentage < 110f -> "" to ""
            percentage < 145f -> "<big>" to "</big>"
            else -> "<big><big>" to "</big></big>"
        }
        return "$opening$html$closing"
    }

    private fun applyColor(element: Element, color: String) {
        if (element.normalName() == "span") {
            element.tagName("font")
            element.attr("color", color)
        } else {
            element.html("<font color=\"$color\">${element.html()}</font>")
        }
    }
}
