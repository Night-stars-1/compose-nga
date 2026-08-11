package com.srap.nga.ui.component.webview

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NgaPostLinkTest {
    @Test
    fun `parses pid-only NGA links`() {
        assertEquals(
            NgaPostLink(tid = null, pid = 865494843),
            parseNgaPostLink("https://bbs.nga.cn/read.php?pid=865494843&opt=128"),
        )
    }

    @Test
    fun `parses tid and pid links with html query separators`() {
        assertEquals(
            NgaPostLink(tid = 47332975, pid = 865494843),
            parseNgaPostLink("https://bbs.nga.cn/read.php?tid=47332975&amp;pid=865494843"),
        )
    }

    @Test
    fun `ignores non NGA links`() {
        assertNull(parseNgaPostLink("https://example.com/read.php?pid=865494843"))
    }
}
