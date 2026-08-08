package com.srap.nga.utils.nga.parse

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HtmlTableParserTest {
    @Test
    fun `parses a normal table and keeps rich cell html`() {
        val table = HtmlTableParser.parse(
            """
                <table>
                    <tr><th>职业</th><th>属性</th></tr>
                    <tr><td><b>战士</b></td><td><a href="https://example.com">急速</a></td></tr>
                </table>
            """.trimIndent()
        )!!

        assertEquals(2, table.rowCount)
        assertEquals(2, table.columnCount)
        assertEquals(4, table.cells.size)
        assertTrue(table.cells.first().isHeader)
        assertEquals("<b>战士</b>", table.cells[2].html)
        assertTrue(table.cells[3].html.contains("href=\"https://example.com\""))
    }

    @Test
    fun `calculates positions for NGA rowspan and colspan tables`() {
        val table = HtmlTableParser.parse(
            """
                <table cellspacing='0px'>
                    <tr><td rowspan=2>Lv.6</td><td colspan=2>输出向</td></tr>
                    <tr><td>攻击</td><td>暴击率</td></tr>
                    <tr><td>数值</td><td>100</td><td>20%</td></tr>
                </table>
            """.trimIndent()
        )!!

        assertEquals(3, table.rowCount)
        assertEquals(3, table.columnCount)
        assertEquals(2, table.cells[0].rowSpan)
        assertEquals(2, table.cells[1].columnSpan)
        assertEquals(1, table.cells[2].column)
        assertEquals(2, table.cells[3].column)
    }

    @Test
    fun `splits tables without losing surrounding post content`() {
        val segments = HtmlTableParser.split(
            "前文<div><table><tr><td>内容</td></tr></table></div>后文"
        )

        assertEquals(3, segments.size)
        assertEquals("前文<div>", (segments[0] as HtmlTableSegment.Html).value)
        assertTrue((segments[1] as HtmlTableSegment.Table).value.startsWith("<table>"))
        assertEquals("</div>后文", (segments[2] as HtmlTableSegment.Html).value)
    }

    @Test
    fun `retains text placed directly inside a table by NGA`() {
        val table = HtmlTableParser.parse(
            """
                <table>门票说明<br/>每日五次
                    <tr><td>一星</td><td>2000</td></tr>
                </table>
            """.trimIndent()
        )!!

        assertTrue(table.leadingHtml.contains("门票说明"))
        assertTrue(table.leadingHtml.contains("每日五次"))
        assertEquals(2, table.columnCount)
    }

    @Test
    fun `unwraps an image-only NGA div and preserves its alignment`() {
        val table = HtmlTableParser.parse(
            """
                <table><tr><td colspan=2>
                    <div style="text-align:center">
                        [img]https://img.nga.cn/attachments/aT3cSzk-k0.webp[/img]
                    </div>
                </td></tr></table>
            """.trimIndent()
        )!!

        val cell = table.cells.single()
        assertFalse(cell.html.contains("<div", ignoreCase = true))
        assertTrue(cell.html.contains("[img]https://img.nga.cn/attachments/aT3cSzk-k0.webp[/img]"))
        assertEquals(NgaTableCellAlignment.CENTER, cell.alignment)
    }

    @Test
    fun `keeps a wrapper when it is not the only cell node`() {
        val table = HtmlTableParser.parse(
            "<table><tr><td><div>正文</div><br/>补充</td></tr></table>"
        )!!

        assertTrue(table.cells.single().html.contains("<div>正文</div>"))
    }

    @Test
    fun `repairs NGA tables whose cells are missing a row wrapper`() {
        val content = SplitCollapse().splitCollapse(
            """
                <table cellspacing='0px'>
                    <td>[img]https://img.nga.cn/attachments/left.jpg[/img]</td>
                    <td>[img]https://img.nga.cn/attachments/right.jpg[/img]</td>
                </table>
            """.trimIndent()
        )!!
        val table = (content.single() as NgaContent.Table).table

        assertEquals(1, table.rowCount)
        assertEquals(2, table.columnCount)
        assertEquals(2, table.cells.size)
        assertTrue(table.cells[0].html.contains("left.jpg"))
        assertTrue(table.cells[1].html.contains("right.jpg"))
    }
}
