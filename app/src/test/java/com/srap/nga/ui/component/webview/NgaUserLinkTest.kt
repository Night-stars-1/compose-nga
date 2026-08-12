package com.srap.nga.ui.component.webview

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NgaUserLinkTest {

    @Test
    fun parseNgaUserLink_ucpLink_returnsUid() {
        assertEquals(
            65096494,
            parseNgaUserLink("https://bbs.nga.cn/nuke.php?func=ucp&uid=65096494"),
        )
    }

    @Test
    fun parseNgaUserLink_ngabbsHostAndEscapedAmp_returnsUid() {
        assertEquals(
            123,
            parseNgaUserLink("https://ngabbs.com/nuke.php?func=ucp&amp;uid=123"),
        )
    }

    @Test
    fun parseNgaUserLink_nonUcpNukeLink_returnsNull() {
        assertNull(parseNgaUserLink("https://bbs.nga.cn/nuke.php?func=likelist&uid=123"))
    }

    @Test
    fun parseNgaUserLink_missingUid_returnsNull() {
        assertNull(parseNgaUserLink("https://bbs.nga.cn/nuke.php?func=ucp&username=abc"))
    }

    @Test
    fun parseNgaUserLink_nonNgaHost_returnsNull() {
        assertNull(parseNgaUserLink("https://example.com/nuke.php?func=ucp&uid=123"))
    }
}
