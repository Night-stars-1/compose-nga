package com.srap.nga.utils.nga.parse

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SplitCollapseTest {
    @Test
    fun `preserves top level line breaks for HtmlCompat`() {
        val content = SplitCollapse().splitCollapse(
            "\u7b2c\u4e00\u6bb5<br/>\u7b2c\u4e8c\u6bb5<br/><br/>\u7b2c\u4e09\u6bb5"
        )!!

        assertEquals(1, content.size)
        assertEquals(
            "\u7b2c\u4e00\u6bb5<br>\u7b2c\u4e8c\u6bb5<br><br>\u7b2c\u4e09\u6bb5",
            (content.single() as NgaContent.Text).content,
        )
    }

    @Test
    fun `keeps paragraph breaks while trimming breaks around images`() {
        val content = SplitCollapse().splitCollapse(
            "\u524d\u6587<br/><br/>[img]https://img.nga.cn/example.jpg[/img]<br/><br/>" +
                "\u56fe\u6ce8<br/><br/>\u540e\u6587"
        )!!

        assertEquals(3, content.size)
        assertEquals("\u524d\u6587", (content[0] as NgaContent.Text).content)
        assertEquals(
            "https://img.nga.cn/example.jpg",
            (content[1] as NgaContent.Image).url,
        )
        assertEquals(
            "\u56fe\u6ce8<br><br>\u540e\u6587",
            (content[2] as NgaContent.Text).content,
        )
    }

    @Test
    fun `preserves paragraph breaks inside collapse content`() {
        val content = SplitCollapse().splitCollapse(
            "<div class=\"foldSnippet\">" +
                "<div class=\"foldTxt\">\u6807\u9898</div>" +
                "<div class=\"foldHidden\">\u7b2c\u4e00\u6bb5<br/><br/>\u7b2c\u4e8c\u6bb5</div>" +
                "</div>"
        )!!

        val collapse = content.single() as NgaContent.Collapse
        assertEquals("\u6807\u9898", collapse.name)
        assertEquals(
            "\u7b2c\u4e00\u6bb5<br><br>\u7b2c\u4e8c\u6bb5",
            (collapse.content.single() as NgaContent.Text).content,
        )
    }

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
