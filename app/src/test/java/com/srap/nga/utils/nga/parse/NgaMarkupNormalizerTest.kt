package com.srap.nga.utils.nga.parse

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NgaMarkupNormalizerTest {
    @Test
    fun `normalizes NGA links including reply anchors`() {
        val normalized = NgaMarkupNormalizer.normalizeLinks(
            """
                [tid=123]主题[/tid]
                [pid=456,123,1]Reply[/pid]
                [uid=789]用户[/uid]
                [url=https://example.com]站外[/url]
            """.trimIndent()
        )

        assertTrue(normalized.contains("read.php?tid=123\">主题"))
        assertTrue(normalized.contains("tid=123&amp;pid=456\">Reply"))
        assertTrue(normalized.contains("uid=789\">用户"))
        assertTrue(normalized.contains("href=\"https://example.com\">站外"))
    }

    @Test
    fun `adapts NGA sizes colors titles and centered blocks`() {
        val adapted = NgaMarkupNormalizer.adaptStyles(
            """
                <div style="text-align:center">
                    <h4 class="subtitle">章节标题</h4>
                    <span style="font-size:160%;line-height:183%"><span class="red">重点</span></span>
                    <del class="gray">删除内容</del>
                    [size=180%]<span class="green">大号文字</span>[/size]
                </div>
            """.trimIndent()
        )

        assertTrue(adapted.contains("align=\"center\""))
        assertTrue(adapted.contains("<h4>章节标题</h4>"))
        assertTrue(adapted.contains("<big><big>"))
        assertTrue(adapted.contains("color=\"#D32F2F\""))
        assertTrue(adapted.contains("color=\"#388E3C\""))
        assertTrue(adapted.contains("<del><font color=\"#757575\">删除内容</font></del>"))
        assertFalse(adapted.contains("line-height"))
        assertFalse(adapted.contains("class=\"red\""))
        assertFalse(adapted.contains("[size="))
    }
    @Test
    fun `normalizes size before structural line break splitting`() {
        val normalized = NgaMarkupNormalizer.normalizeSizes(
            """
                [size=180%]问卷链接</span><br/><br/>
                <b>注意事项：</b><br/>
                1.第一项<br/>
                2.第二项[/size]
            """.trimIndent()
        )
        val parser = SplitQuote()
        parser.splitQuote(normalized)
        val textChunks = parser.data.filterIsInstance<NgaContent.Text>()
        assertFalse(textChunks.any { it.content.contains("[size=") })
        assertFalse(textChunks.any { it.content.contains("[/size]") })
        assertTrue(textChunks.any { it.content.contains("<big><big>") })
    }
}
