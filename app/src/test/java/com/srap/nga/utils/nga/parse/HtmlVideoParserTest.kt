package com.srap.nga.utils.nga.parse

import org.junit.Assert.assertEquals
import org.junit.Test

class HtmlVideoParserTest {
    @Test
    fun `parses NGA video source and poster`() {
        val video = HtmlVideoParser.parse(
            """
                <span class="video">
                    <video src="https://img.nga.cn/demo.mp4" poster="https://img.nga.cn/demo.jpg">浏览器不支持</video>
                </span>
            """.trimIndent()
        )!!

        assertEquals("https://img.nga.cn/demo.mp4", video.url)
        assertEquals("https://img.nga.cn/demo.jpg", video.posterUrl)
    }

    @Test
    fun `split collapse keeps text around a video in source order`() {
        val content = SplitCollapse().splitCollapse(
            """
                前文<span class="video"><video src="https://img.nga.cn/demo.mp4" poster="https://img.nga.cn/demo.jpg">浏览器不支持</video></span>后文
            """.trimIndent()
        )!!

        assertEquals(3, content.size)
        assertEquals("前文", (content[0] as NgaContent.Text).content.trim())
        assertEquals("https://img.nga.cn/demo.mp4", (content[1] as NgaContent.Video).video.url)
        assertEquals("后文", (content[2] as NgaContent.Text).content.trim())
    }

    @Test
    fun `split collapse treats gif in a video tag as an image`() {
        val parser = SplitCollapse()
        val content = parser.splitCollapse(
            """
                前文<span class="video"><video src="https://img.nga.cn/demo.GIF?download=1"></video></span>后文
            """.trimIndent()
        )!!

        assertEquals(3, content.size)
        assertEquals("前文", (content[0] as NgaContent.Text).content.trim())
        assertEquals(
            "https://img.nga.cn/demo.GIF?download=1",
            (content[1] as NgaContent.Image).url,
        )
        assertEquals("后文", (content[2] as NgaContent.Text).content.trim())
        assertEquals(listOf("https://img.nga.cn/demo.GIF?download=1"), parser.imageList)
    }

    @Test
    fun `gif thumbnail poster resolves to original animated image`() {
        val content = SplitCollapse().splitCollapse(
            """
                <span class="video">
                    <video
                        src="https://img.nga.cn/demo.mp4"
                        poster="https://img.nga.cn/demo.gif.medium.jpg"
                    ></video>
                </span>
            """.trimIndent()
        )!!

        assertEquals(1, content.size)
        assertEquals(
            "https://img.nga.cn/demo.gif",
            (content.single() as NgaContent.Image).url,
        )
    }

    @Test
    fun `gif filename in proxy query remains an animated image`() {
        val proxyUrl = "https://img.nga.cn/proxy?id=7&filename=demo.gif"
        val content = SplitCollapse().splitCollapse(
            """<span class="video"><video src="$proxyUrl"></video></span>"""
        )!!

        assertEquals(proxyUrl, (content.single() as NgaContent.Image).url)
    }

    @Test
    fun `image gif source without extension remains an animated image`() {
        val content = SplitCollapse().splitCollapse(
            """
                <span class="video">
                    <video>
                        <source src="https://img.nga.cn/attachment?id=7" type="image/gif">
                    </video>
                </span>
            """.trimIndent()
        )!!

        assertEquals(
            "https://img.nga.cn/attachment?id=7",
            (content.single() as NgaContent.Image).url,
        )
    }

    @Test
    fun `mp4 with a regular poster remains a video`() {
        val content = SplitCollapse().splitCollapse(
            """
                <span class="video">
                    <video src="https://img.nga.cn/demo.mp4" poster="https://img.nga.cn/demo.jpg">
                    </video>
                </span>
            """.trimIndent()
        )!!

        assertEquals(
            "https://img.nga.cn/demo.mp4",
            (content.single() as NgaContent.Video).video.url,
        )
    }
}
