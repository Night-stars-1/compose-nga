package com.srap.nga.utils.nga.parse

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import kotlin.math.max

internal sealed interface HtmlTableSegment {
    data class Html(val value: String) : HtmlTableSegment
    data class Table(val value: String) : HtmlTableSegment
}

/** Parses the table markup returned by the NGA app API. */
internal object HtmlTableParser {
    private val htmlRegexOptions = setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL)
    private val tableTag = Regex("""<\s*(/?)\s*table\b[^>]*>""", htmlRegexOptions)
    private val tableOpeningTag = Regex("""<\s*table\b[^>]*>""", htmlRegexOptions)
    private val tableClosingTag = Regex("""<\s*/\s*table\s*>""", htmlRegexOptions)
    private val firstRowTag = Regex("""<\s*tr\b[^>]*>""", htmlRegexOptions)
    private val cellOpeningTag = Regex("""<\s*t[dh]\b[^>]*>""", htmlRegexOptions)
    private val cellClosingTag = Regex("""<\s*/\s*t[dh]\s*>""", htmlRegexOptions)
    private val captionTag = Regex("""<\s*caption\b""", htmlRegexOptions)
    private val tableSectionTag = Regex(
        """<\s*/?\s*(?:thead|tbody|tfoot|colgroup|col)\b[^>]*>""",
        htmlRegexOptions,
    )

    fun split(html: String): List<HtmlTableSegment> {
        if (!html.contains("<table", ignoreCase = true)) {
            return listOf(HtmlTableSegment.Html(html))
        }

        val segments = mutableListOf<HtmlTableSegment>()
        var depth = 0
        var tableStart = -1
        var cursor = 0

        tableTag.findAll(html).forEach { match ->
            val isClosingTag = match.groupValues[1].isNotEmpty()
            if (!isClosingTag) {
                if (depth == 0) {
                    if (cursor < match.range.first) {
                        segments.add(HtmlTableSegment.Html(html.substring(cursor, match.range.first)))
                    }
                    tableStart = match.range.first
                    cursor = tableStart
                }
                depth++
            } else if (depth > 0) {
                depth--
                if (depth == 0) {
                    val tableEnd = match.range.last + 1
                    segments.add(HtmlTableSegment.Table(html.substring(tableStart, tableEnd)))
                    cursor = tableEnd
                    tableStart = -1
                }
            }
        }

        if (cursor < html.length) {
            segments.add(HtmlTableSegment.Html(html.substring(cursor)))
        }
        return segments.ifEmpty { listOf(HtmlTableSegment.Html(html)) }
    }

    fun normalizeForHtmlParser(html: String): String =
        split(html).joinToString(separator = "") { segment ->
            when (segment) {
                is HtmlTableSegment.Html -> segment.value
                is HtmlTableSegment.Table -> prepareTableMarkup(segment.value)
            }
        }

    fun parse(tableHtml: String): NgaTable? {
        val body = NgaMarkupNormalizer.parseStyledBody(prepareTableMarkup(tableHtml))
        val table = body.selectFirst("table") ?: return null
        val rows = table.select("tr").filter { row -> row.closestTable() === table }
        if (rows.isEmpty()) return null

        val cells = mutableListOf<NgaTableCell>()
        val occupiedPositions = mutableSetOf<Long>()
        var columnCount = 0
        var rowCount = rows.size

        rows.forEachIndexed { rowIndex, row ->
            var columnIndex = 0
            row.children()
                .filter { it.normalName() == "td" || it.normalName() == "th" }
                .forEach { cell ->
                    val rowSpan = cell.positiveSpan("rowspan")
                    val columnSpan = cell.positiveSpan("colspan")
                    while ((0 until columnSpan).any {
                            occupiedPositions.contains(positionKey(rowIndex, columnIndex + it))
                        }
                    ) {
                        columnIndex++
                    }

                    val markup = TableCellMarkupNormalizer.normalize(cell)
                    cells.add(
                        NgaTableCell(
                            html = markup.html,
                            row = rowIndex,
                            column = columnIndex,
                            rowSpan = rowSpan,
                            columnSpan = columnSpan,
                            isHeader = cell.normalName() == "th",
                            alignment = markup.alignment,
                        )
                    )
                    repeat(rowSpan) { rowOffset ->
                        repeat(columnSpan) { columnOffset ->
                            occupiedPositions.add(
                                positionKey(rowIndex + rowOffset, columnIndex + columnOffset)
                            )
                        }
                    }
                    columnIndex += columnSpan
                    columnCount = max(columnCount, columnIndex)
                    rowCount = max(rowCount, rowIndex + rowSpan)
                }
        }
        if (cells.isEmpty() || columnCount == 0) return null

        val captionHtml = table.children()
            .firstOrNull { it.normalName() == "caption" }
            ?.html()
            .orEmpty()
        val fosteredHtml = body.childNodes()
            .filterNot { it === table }
            .joinToString(separator = "") { it.outerHtml() }
            .trim()
        val leadingHtml = listOf(fosteredHtml, captionHtml)
            .filter { it.isNotBlank() }
            .joinToString("<br/>")

        return NgaTable(
            leadingHtml = leadingHtml,
            cells = cells,
            rowCount = rowCount,
            columnCount = columnCount,
        )
    }

    private fun prepareTableMarkup(tableHtml: String): String =
        protectLeadingText(wrapDirectCellsInRow(tableHtml))

    private fun wrapDirectCellsInRow(tableHtml: String): String {
        val openingTag = tableOpeningTag.find(tableHtml) ?: return tableHtml
        if (firstRowTag.find(tableHtml, openingTag.range.last + 1) != null) return tableHtml
        val closingTag = tableClosingTag.find(tableHtml, openingTag.range.last + 1) ?: return tableHtml
        val firstCell = cellOpeningTag.find(tableHtml, openingTag.range.last + 1)
            ?.takeIf { it.range.first < closingTag.range.first }
            ?: return tableHtml
        val lastCell = cellClosingTag.findAll(tableHtml, firstCell.range.first)
            .takeWhile { it.range.last < closingTag.range.first }
            .lastOrNull()
        val rowEnd = lastCell?.range?.last?.plus(1) ?: closingTag.range.first

        return buildString(tableHtml.length + 9) {
            append(tableHtml, 0, firstCell.range.first)
            append("<tr>")
            append(tableHtml, firstCell.range.first, rowEnd)
            append("</tr>")
            append(tableHtml, rowEnd, tableHtml.length)
        }
    }

    private fun protectLeadingText(tableHtml: String): String {
        val openingTag = tableOpeningTag.find(tableHtml) ?: return tableHtml
        val firstRow = firstRowTag.find(tableHtml, openingTag.range.last + 1) ?: return tableHtml
        val betweenTableAndRow = tableHtml.substring(openingTag.range.last + 1, firstRow.range.first)
        if (captionTag.containsMatchIn(betweenTableAndRow)) return tableHtml

        val leadingHtml = tableSectionTag.replace(betweenTableAndRow, "").trim()
        if (leadingHtml.isBlank() || Jsoup.parseBodyFragment(leadingHtml).text().isBlank()) {
            return tableHtml
        }

        val sectionTags = tableSectionTag.findAll(betweenTableAndRow)
            .joinToString(separator = "") { it.value }
        return buildString(tableHtml.length + 19) {
            append(tableHtml, 0, openingTag.range.last + 1)
            append("<caption>")
            append(leadingHtml)
            append("</caption>")
            append(sectionTags)
            append(tableHtml, firstRow.range.first, tableHtml.length)
        }
    }

    private fun Element.closestTable(): Element? =
        parents().firstOrNull { it.normalName() == "table" }

    private fun Element.positiveSpan(attribute: String): Int =
        attr(attribute).toIntOrNull()?.coerceAtLeast(1) ?: 1

    private fun positionKey(row: Int, column: Int): Long =
        (row.toLong() shl 32) or (column.toLong() and 0xffffffffL)
}
