package com.srap.nga.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class ExtensionsTest {
    @Test
    fun toNgaImageUrl_replacesDeprecatedImageHost() {
        val source =
            "https://img.nga.178.com/attachments/mon_202607/18/example.jpeg"

        assertEquals(
            "https://img.nga.cn/attachments/mon_202607/18/example.jpeg",
            source.toNgaImageUrl(),
        )
    }

    @Test
    fun toNgaImageUrl_upgradesHttpBeforeReplacingDeprecatedImageHost() {
        val source =
            "http://img.nga.178.com/attachments/mon_202607/18/example.jpeg"

        assertEquals(
            "https://img.nga.cn/attachments/mon_202607/18/example.jpeg",
            source.toNgaImageUrl(),
        )
    }

    @Test
    fun toNgaImageUrl_keepsCurrentImageHost() {
        val source =
            "https://img.nga.cn/attachments/mon_202608/07/example.jpg"

        assertEquals(source, source.toNgaImageUrl())
    }
}
