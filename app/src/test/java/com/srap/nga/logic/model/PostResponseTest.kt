package com.srap.nga.logic.model

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Test

class PostResponseTest {
    private val gson = Gson()

    @Test
    fun result_mapsNgaVoteFieldsToLikeAndDislikeCounts() {
        val result = gson.fromJson(
            """
            {
              "pid": 42,
              "author": {"uid": 7, "username": "tester", "avatar": ""},
              "content": "reply",
              "attches": [],
              "vote_good": 12,
              "vote_bad": 3,
              "vote": "1"
            }
            """.trimIndent(),
            PostResponse.Result::class.java,
        )

        assertEquals(12, result.likeCount)
        assertEquals(3, result.dislikeCount)
        assertEquals(PostVote.LIKE, result.voteState)
    }

    @Test
    fun result_preservesExplicitZeroDislikeCount() {
        val result = gson.fromJson(
            """
            {
              "pid": 42,
              "author": {"uid": 7, "username": "tester", "avatar": ""},
              "content": "reply",
              "attches": [],
              "vote_good": 2,
              "vote_bad": 0
            }
            """.trimIndent(),
            PostResponse.Result::class.java,
        )

        assertEquals(2, result.likeCount)
        assertEquals(0, result.dislikeCount)
        assertEquals(PostVote.NONE, result.voteState)
    }

    @Test
    fun result_defaultsMissingVoteFieldsToZero() {
        val result = gson.fromJson(
            """
            {
              "pid": 42,
              "author": {"uid": 7, "username": "tester", "avatar": ""},
              "content": "reply",
              "attches": []
            }
            """.trimIndent(),
            PostResponse.Result::class.java,
        )

        assertEquals(0, result.likeCount)
        assertEquals(0, result.dislikeCount)
        assertEquals(PostVote.NONE, result.voteState)
    }

    @Test
    fun result_acceptsNumericVoteState() {
        val result = gson.fromJson(
            """
            {
              "pid": 42,
              "author": {"uid": 7, "username": "tester", "avatar": ""},
              "content": "reply",
              "attches": [],
              "vote": -1
            }
            """.trimIndent(),
            PostResponse.Result::class.java,
        )

        assertEquals(PostVote.DISLIKE, result.voteState)
    }

    @Test
    fun result_mapsContainingThreadId() {
        val result = gson.fromJson(
            """
            {
              "pid": 865494843,
              "tid": 47332975,
              "author": {"uid": 7, "username": "tester", "avatar": ""},
              "content": "reply",
              "attches": []
            }
            """.trimIndent(),
            PostResponse.Result::class.java,
        )

        assertEquals(47332975, result.tid)
    }
}
