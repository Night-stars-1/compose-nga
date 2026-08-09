package com.srap.nga.ui.post

import org.junit.Assert.assertEquals
import org.junit.Test

class PostShareTest {
    @Test
    fun `share text only contains canonical post url`() {
        assertEquals(
            "https://bbs.nga.cn/read.php?tid=123",
            buildPostShareText(id = 123),
        )
    }
}