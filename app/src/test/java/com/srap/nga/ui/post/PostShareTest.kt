package com.srap.nga.ui.post

import org.junit.Assert.assertEquals
import org.junit.Test

class PostShareTest {
    @Test
    fun `share text contains title and canonical post url`() {
        assertEquals(
            "帖子标题\nhttps://bbs.nga.cn/read.php?tid=123",
            buildPostShareText(id = 123, title = " 帖子标题 "),
        )
    }

    @Test
    fun `share text omits blank title`() {
        assertEquals(
            "https://bbs.nga.cn/read.php?tid=123",
            buildPostShareText(id = 123, title = "  "),
        )
    }
}
