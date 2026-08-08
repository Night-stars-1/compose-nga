package com.srap.nga.utils.nga.parse

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SplitQuoteTest {
    @Test
    fun `keeps text between consecutive nested quotes`() {
        val parser = SplitQuote()
        parser.splitQuote(
            """[quote]<ul><li>3、第三点<br/>[quote]第三点示例[/quote]第三点结尾</li><li>4、第四点<br/>[quote]第四点示例[/quote]第四点结尾</li><li>5、第五点</li></ul>[/quote]"""
        )

        val outerQuote = parser.data.single() as NgaContent.Quote
        assertEquals(2, outerQuote.content.count { it is NgaContent.Quote })

        val pointFourIndex = outerQuote.content.indexOfFirst {
            it is NgaContent.Text && it.content.contains("4、第四点")
        }
        val firstQuoteIndex = outerQuote.content.indexOfFirst { it is NgaContent.Quote }
        val secondQuoteIndex = outerQuote.content.indexOfLast { it is NgaContent.Quote }

        assertTrue(pointFourIndex > firstQuoteIndex)
        assertTrue(pointFourIndex < secondQuoteIndex)
        assertTrue(
            outerQuote.content.filterIsInstance<NgaContent.Text>()
                .joinToString { it.content }
                .contains("第四点结尾")
        )
    }
}
