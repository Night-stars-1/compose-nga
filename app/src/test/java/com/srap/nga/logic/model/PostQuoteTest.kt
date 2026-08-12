package com.srap.nga.logic.model

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Test

class PostQuoteTest {
    private val gson = Gson()

    @Test
    fun quoteContent_matchesNgaReplyFormat() {
        val post = gson.fromJson(
            """
            {
              "pid": 802076529,
              "tid": 42845977,
              "lou": 1,
              "postdate": "2024-12-28 14:22",
              "content": "还好吧其实，用很久了都没事",
              "author": {
                "uid": 123456,
                "username": "test_user",
                "avatar": ""
              },
              "attches": []
            }
            """.trimIndent(),
            PostResponse.Result::class.java,
        )

        assertEquals(
            "[quote][pid=802076529,42845977,1]Reply[/pid] " +
                "[b]Post by [uid=123456]test_user[/uid] " +
                "(2024-12-28 14:22):[/b]\n\n" +
                "还好吧其实，用很久了都没事[/quote]\n\n[s:ac:茶]",
            buildNgaQuoteContent(
                post = post,
                threadId = 42845977,
                replyContent = "  [s:ac:茶]  ",
            ),
        )
    }

    @Test
    fun quoteContent_usesExplicitThreadIdWhenReplyItemOmitsTid() {
        val post = gson.fromJson(
            """
            {
              "pid": 9,
              "lou": 3,
              "postdate": "2026-08-12 10:00",
              "content": "原评论",
              "author": {"uid": 8, "username": "user", "avatar": ""},
              "attches": []
            }
            """.trimIndent(),
            PostResponse.Result::class.java,
        )

        assertEquals(
            "[quote][pid=9,77,3]Reply[/pid] " +
                "[b]Post by [uid=8]user[/uid] (2026-08-12 10:00):[/b]\n\n" +
                "原评论[/quote]\n\n回复",
            buildNgaQuoteContent(post, threadId = 77, replyContent = "回复"),
        )
    }
}
