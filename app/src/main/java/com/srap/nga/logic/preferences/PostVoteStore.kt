package com.srap.nga.logic.preferences

import android.content.Context
import androidx.core.content.edit
import com.srap.nga.logic.model.PostVote
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostVoteStore @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val preferences = context.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE,
    )

    fun getVote(userId: Int, threadId: Int, postId: Int): Int? {
        if (userId <= 0) return null

        return preferences.getInt(key(userId, threadId, postId), MISSING_VOTE)
            .takeIf { it == PostVote.LIKE || it == PostVote.DISLIKE }
    }

    fun setVote(userId: Int, threadId: Int, postId: Int, vote: Int) {
        if (userId <= 0) return

        val key = key(userId, threadId, postId)
        val normalizedVote = PostVote.normalize(vote)
        preferences.edit {
            if (normalizedVote == PostVote.NONE) {
                remove(key)
            } else {
                putInt(key, normalizedVote)
            }
        }
    }

    private fun key(userId: Int, threadId: Int, postId: Int): String =
        "$userId:$threadId:$postId"

    private companion object {
        const val PREFERENCES_NAME = "postVotes"
        const val MISSING_VOTE = Int.MIN_VALUE
    }
}
