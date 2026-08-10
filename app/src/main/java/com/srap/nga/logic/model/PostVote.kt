package com.srap.nga.logic.model

object PostVote {
    const val DISLIKE = -1
    const val NONE = 0
    const val LIKE = 1

    fun normalize(value: Int): Int = when (value) {
        LIKE, DISLIKE -> value
        else -> NONE
    }

    fun nextState(requestedVote: Int, previousVote: Int): Int {
        require(requestedVote == LIKE || requestedVote == DISLIKE)

        val normalizedPreviousVote = normalize(previousVote)
        return if (normalizedPreviousVote == requestedVote) NONE else requestedVote
    }
}

internal val PostResponse.Result.explicitVoteState: Int?
    get() {
        val rawVote = vote?.trim()?.takeIf { it.isNotEmpty() } ?: return null
        return rawVote.toIntOrNull()?.takeIf {
            it == PostVote.LIKE || it == PostVote.DISLIKE || it == PostVote.NONE
        }
    }

internal fun PostResponse.Result.mergePostVoteState(
    persistedVote: Int?,
): PostResponse.Result {
    val mergedVote = explicitVoteState
        ?: PostVote.normalize(persistedVote ?: PostVote.NONE)

    return copy(
        vote = mergedVote.takeUnless { it == PostVote.NONE }?.toString().orEmpty(),
    )
}

internal fun PostResponse.Result.applyPostVoteResult(
    requestedVote: Int,
    lastVote: Int,
): PostResponse.Result {
    require(requestedVote == PostVote.LIKE || requestedVote == PostVote.DISLIKE)

    val previousVote = PostVote.normalize(lastVote)
    val nextVote = PostVote.nextState(requestedVote, previousVote)
    val likeDelta = (if (nextVote == PostVote.LIKE) 1 else 0) -
        (if (previousVote == PostVote.LIKE) 1 else 0)
    val nextLikeCount = (likeCount.toLong() + likeDelta)
        .coerceIn(0, Int.MAX_VALUE.toLong())
        .toInt()

    return copy(
        likeCount = nextLikeCount,
        vote = nextVote.takeUnless { it == PostVote.NONE }?.toString().orEmpty(),
    )
}
