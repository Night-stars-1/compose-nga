package com.srap.nga.utils.nga.parse

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node

class SplitImage {
    private companion object {
        const val IMAGE_PLACEHOLDER_TAG = "nga-image-placeholder"

        val imagePattern = Regex(
            """\[img](.*?)\[/img]""",
            setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL),
        )
        val leadingBreakPattern = Regex(
            """^(?:\s*<br\s*/?>\s*)+""",
            RegexOption.IGNORE_CASE,
        )
        val trailingBreakPattern = Regex(
            """(?:\s*<br\s*/?>\s*)+$""",
            RegexOption.IGNORE_CASE,
        )
    }

    fun splitImage(text: String, start: Int = 0): List<NgaContent>? {
        val source = if (start == 0) text else text.substring(start)
        val matches = imagePattern.findAll(source).toList()
        if (matches.isEmpty()) {
            return source.takeIf { it.isNotEmpty() }?.let { listOf(NgaContent.Text(it)) }
        }

        val imageUrls = matches.map { it.groupValues[1] }
        val markedHtml = buildString(source.length) {
            var cursor = 0
            matches.forEachIndexed { index, match ->
                append(source, cursor, match.range.first)
                append("<$IMAGE_PLACEHOLDER_TAG data-index=\"")
                append(index)
                append("\"></$IMAGE_PLACEHOLDER_TAG>")
                cursor = match.range.last + 1
            }
            append(source, cursor, source.length)
        }

        val document = Jsoup.parseBodyFragment(markedHtml)
        document.outputSettings().prettyPrint(false)
        val content = document.body().childNodes()
            .flatMap { splitNode(it, imageUrls) }
            .mergeAndTrimImageBoundaries()
        return content.takeIf { it.isNotEmpty() }
    }

    private fun splitNode(node: Node, imageUrls: List<String>): List<NgaContent> {
        if (node is Element && node.normalName() == IMAGE_PLACEHOLDER_TAG) {
            val index = node.attr("data-index").toIntOrNull() ?: return emptyList()
            return imageUrls.getOrNull(index)?.let { listOf(NgaContent.Image(it)) }.orEmpty()
        }
        if (node !is Element || node.getElementsByTag(IMAGE_PLACEHOLDER_TAG).isEmpty()) {
            return listOf(NgaContent.Text(node.outerHtml()))
        }

        return node.childNodes()
            .flatMap { splitNode(it, imageUrls) }
            .mergeAndTrimImageBoundaries()
            .map { content ->
                if (content is NgaContent.Text) {
                    NgaContent.Text(wrapWithElement(node, content.content))
                } else {
                    content
                }
            }
    }

    private fun wrapWithElement(element: Element, html: String): String =
        element.clone().apply {
            empty()
            this.html(html)
        }.outerHtml()

    private fun List<NgaContent>.mergeAndTrimImageBoundaries(): List<NgaContent> {
        val merged = mutableListOf<NgaContent>()
        forEach { content ->
            if (content is NgaContent.Text && merged.lastOrNull() is NgaContent.Text) {
                val previous = merged.removeAt(merged.lastIndex) as NgaContent.Text
                merged.add(NgaContent.Text(previous.content + content.content))
            } else {
                merged.add(content)
            }
        }

        val result = mutableListOf<NgaContent>()
        var followsImage = false
        merged.forEach { content ->
            when (content) {
                is NgaContent.Image -> {
                    val previous = result.lastOrNull()
                    if (previous is NgaContent.Text) {
                        result.removeAt(result.lastIndex)
                        previous.content.replace(trailingBreakPattern, "")
                            .takeIf { it.isNotBlank() }
                            ?.let { result.add(NgaContent.Text(it)) }
                    }
                    result.add(content)
                    followsImage = true
                }
                is NgaContent.Text -> {
                    val html = if (followsImage) {
                        content.content.replace(leadingBreakPattern, "")
                    } else {
                        content.content
                    }
                    if (html.isNotBlank()) result.add(NgaContent.Text(html))
                    followsImage = false
                }
                else -> {
                    result.add(content)
                    followsImage = false
                }
            }
        }
        return result
    }
}
