package com.srap.nga.utils.nga.handler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NgaImageSizeParserTest {
    @Test
    fun `decodes NGA base36 image dimensions`() {
        assertEquals(
            NgaImageSize(width = 1280, height = 720),
            NgaImageSizeParser.parse(
                "https://img.nga.cn/attachments/mon_202606/aT3cSzk-k0.webp"
            ),
        )
        assertEquals(
            NgaImageSize(width = 200, height = 31),
            NgaImageSizeParser.parse("https://img.nga.cn/aK6ToS5k-v.png"),
        )
    }

    @Test
    fun `ignores URLs without valid encoded dimensions`() {
        assertNull(NgaImageSizeParser.parse("https://example.com/image.png"))
        assertNull(NgaImageSizeParser.parse("https://img.nga.cn/aS0-k0.webp"))
    }
}
