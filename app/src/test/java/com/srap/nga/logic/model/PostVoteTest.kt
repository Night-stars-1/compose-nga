package com.srap.nga.logic.model

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PostVoteTest {
    @Test
    fun response_mapsLastVoteState() {
        val response = Gson().fromJson(
            """
            {
              "code": 0,
              "msg": "操作成功",
              "result": {"last_state": -1}
            }
            """.trimIndent(),
            PostVoteResponse::class.java,
        )

        assertEquals(PostVote.DISLIKE, response.lastState)
    }

    @Test
    fun response_mapsLastVoteStateFromArrayResult() {
        val response = Gson().fromJson(
            """
            {
              "code": 0,
              "msg": "操作成功",
              "result": [{"last_state": 1}]
            }
            """.trimIndent(),
            PostVoteResponse::class.java,
        )

        assertEquals(PostVote.LIKE, response.lastState)
    }

    @Test
    fun response_allowsEmptyArrayResult() {
        val response = Gson().fromJson(
            """
            {
              "code": 1,
              "msg": "操作失败",
              "result": []
            }
            """.trimIndent(),
            PostVoteResponse::class.java,
        )

        assertNull(response.lastState)
    }

    @Test
    fun response_rejectsMissingOrInvalidLastVoteState() {
        val missingState = Gson().fromJson(
            """{"code":0,"msg":"操作成功","result":[{}]}""",
            PostVoteResponse::class.java,
        )
        val invalidState = Gson().fromJson(
            """{"code":0,"msg":"操作成功","result":{"last_state":2}}""",
            PostVoteResponse::class.java,
        )

        assertNull(missingState.lastState)
        assertNull(invalidState.lastState)
    }

    @Test
    fun applyResult_handlesLikeTransitions() {
        val liked = reply(likeCount = 2).applyPostVoteResult(
            requestedVote = PostVote.LIKE,
            lastVote = PostVote.NONE,
        )
        val cancelled = liked.applyPostVoteResult(
            requestedVote = PostVote.LIKE,
            lastVote = PostVote.LIKE,
        )

        assertEquals(PostVote.LIKE, liked.voteState)
        assertEquals(3, liked.likeCount)
        assertEquals(PostVote.NONE, cancelled.voteState)
        assertEquals(2, cancelled.likeCount)
    }

    @Test
    fun applyResult_handlesDislikeTransitions_withoutChangingApiDislikeCount() {
        val disliked = reply(dislikeCount = 0).applyPostVoteResult(
            requestedVote = PostVote.DISLIKE,
            lastVote = PostVote.NONE,
        )
        val cancelled = disliked.applyPostVoteResult(
            requestedVote = PostVote.DISLIKE,
            lastVote = PostVote.DISLIKE,
        )

        assertEquals(PostVote.DISLIKE, disliked.voteState)
        assertEquals(0, disliked.dislikeCount)
        assertEquals(PostVote.NONE, cancelled.voteState)
        assertEquals(0, cancelled.dislikeCount)
    }

    @Test
    fun applyResult_switchesBetweenLikeAndDislike() {
        val switchedToLike = reply(likeCount = 4, dislikeCount = 0)
            .applyPostVoteResult(
                requestedVote = PostVote.LIKE,
                lastVote = PostVote.DISLIKE,
            )
        val switchedToDislike = reply(likeCount = 4, dislikeCount = 0)
            .applyPostVoteResult(
                requestedVote = PostVote.DISLIKE,
                lastVote = PostVote.LIKE,
            )

        assertEquals(PostVote.LIKE, switchedToLike.voteState)
        assertEquals(5, switchedToLike.likeCount)
        assertEquals(0, switchedToLike.dislikeCount)
        assertEquals(PostVote.DISLIKE, switchedToDislike.voteState)
        assertEquals(3, switchedToDislike.likeCount)
        assertEquals(0, switchedToDislike.dislikeCount)
    }

    @Test
    fun applyResult_neverMakesLikeCountNegative() {
        val result = reply(likeCount = 0).applyPostVoteResult(
            requestedVote = PostVote.DISLIKE,
            lastVote = PostVote.LIKE,
        )

        assertEquals(0, result.likeCount)
    }

    @Test
    fun mergeState_usesPersistedVoteWhenApiVoteIsBlank() {
        val refreshedReply = reply(likeCount = 8).copy(vote = "")

        val result = refreshedReply.mergePostVoteState(PostVote.LIKE)

        assertEquals(PostVote.LIKE, result.voteState)
        assertEquals(8, result.likeCount)
    }

    @Test
    fun mergeState_prefersExplicitApiVote() {
        val explicitDislike = reply(likeCount = 8).copy(vote = "-1")
        val explicitNone = reply(likeCount = 8).copy(vote = "0")

        assertEquals(
            PostVote.DISLIKE,
            explicitDislike.mergePostVoteState(PostVote.LIKE).voteState,
        )
        assertEquals(
            PostVote.NONE,
            explicitNone.mergePostVoteState(PostVote.LIKE).voteState,
        )
    }

    private fun reply(
        likeCount: Int = 0,
        dislikeCount: Int = 0,
    ) = PostResponse.Result(
        pid = 42,
        author = PostResponse.Result.Author(
            uid = 7,
            username = "tester",
            avatar = "",
        ),
        content = "reply",
        attches = null,
        likeCount = likeCount,
        dislikeCount = dislikeCount,
    )
}
