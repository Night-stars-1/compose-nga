package com.srap.nga.utils.nga.parse

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SplitCollapseTest {
    @Test
    fun `does not preserve Jsoup pretty print whitespace in nested blocks`() {
        val content = SplitCollapse().splitCollapse(
            "<div><span>\u7b2c\u4e00\u6bb5</span><div>\u7b2c\u4e8c\u6bb5</div></div>"
        )!!

        val text = (content.single() as NgaContent.Text).content
        assertEquals("<div><span>\u7b2c\u4e00\u6bb5</span><div>\u7b2c\u4e8c\u6bb5</div></div>", text)
        assertFalse(text.contains('\n'))
    }

    @Test
    fun `keeps centered content after a leading image without blank image spacing`() {
        val content = SplitCollapse().splitCollapse(
            """
                <div style="text-align:center"><br/>[img]https://img.nga.cn/header.jpg[/img]<br/><a href="https://example.com"><b>Bilibili Moe 动画角色人气大赏 2026</b></a><br/></div>
            """.trimIndent()
        )!!

        assertEquals(2, content.size)
        assertEquals("https://img.nga.cn/header.jpg", (content[0] as NgaContent.Image).url)
        val links = (content[1] as NgaContent.Text).content
        assertTrue(links.startsWith("<div style=\"text-align:center\"><a"))
        assertFalse(links.contains("text-align:center\"><br>"))
        assertTrue(NgaMarkupNormalizer.adaptStyles(links).contains("align=\"center\""))
    }
}
