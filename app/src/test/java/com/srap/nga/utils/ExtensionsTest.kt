package com.srap.nga.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ExtensionsTest {

    @Test
    fun toHttpsOrNull_normalizesRemoteUrls() {
        assertEquals(
            "https://img4.nga.cn/image.png",
            "http://img4.nga.cn/image.png".toHttpsOrNull(),
        )
        assertEquals(
            "https://img4.nga.cn/image.png",
            "//img4.nga.cn/image.png".toHttpsOrNull(),
        )
        assertEquals(
            "https://img4.nga.cn/image.png",
            "https://img4.nga.cn/image.png".toHttpsOrNull(),
        )
    }

    @Test
    fun toHttpsOrNull_returnsNullForBlankValues() {
        assertNull(null.toHttpsOrNull())
        assertNull("   ".toHttpsOrNull())
    }
}
