package com.srap.nga.logic.model

import com.google.gson.annotations.SerializedName
import com.srap.nga.logic.model.base.BaseResponse
import com.srap.nga.logic.state.Code

data class PostResponse(
    val result: List<Result>,
    @SerializedName("attachPrefix")
    val attachPrefix: String,
    /**
     * 当前页
     */
    @SerializedName("currentPage")
    val currentPage: Int,
    @SerializedName("fid")
    val fid: Int,
    @SerializedName("forum_bit")
    val forumBit: Int,
    /**
     * 版块名称
     */
    @SerializedName("forum_name")
    val forumName: String,
    @SerializedName("hot_post")
    val hotPost: Any,
    /**
     * 是否是版主 0 为否
     */
    @SerializedName("is_forum_admin")
    val isForumAdmin: Int,
    @SerializedName("perPage")
    val perPage: Int,
    /**
     * 作者名称
     */
    @SerializedName("tauthor")
    val tauthor: String,
    /**
     * 作者id
     */
    @SerializedName("tauthorid")
    val tauthorid: Int,
    @SerializedName("tmisc_bit1")
    val tmiscBit1: Int,
    /**
     * 总页数
     */
    @SerializedName("totalPage")
    val totalPage: Int,
    /**
     * 标题
     */
    @SerializedName("tsubject")
    val tsubject: String,
    /**
     * 帖子+评论数量
     *
     * -1后为评论的数量
     */
    @SerializedName("vrows")
    val vrows: Int
) : BaseResponse<PostResponse>() {
    data class Result(
        val pid: Int,
        val author: Author,
        val content: String,
        @SerializedName("postdate")
        val postDate: String = "",
        val attches: List<Attche>?,
        /** 当前账号是否关注该帖子作者，0 为未关注。 */
        val follow: Int = 0,
        /** 点赞数量。 */
        @SerializedName("vote_good")
        val likeCount: Int = 0,
        /** 点踩数量。 */
        @SerializedName("vote_bad")
        val dislikeCount: Int = 0,
        /** 当前账号的投票状态，接口可能返回空字符串或数字。 */
        @SerializedName("vote")
        val vote: String? = null,
    ) {
        val voteState: Int
            get() = PostVote.normalize(vote?.toIntOrNull() ?: PostVote.NONE)

        data class Author(
            val uid: Int,
            val username: String,
            /**
             * 头像链接
             */
            val avatar: String,
            @SerializedName("member")
            val group: String? = null,
            val rvrc: String? = null,
            @SerializedName("postnum")
            val posts: Int? = null,
            val medal: List<Medal>? = null,
        )

        data class Medal(
            val id: Int = 0,
            val name: String? = null,
            val icon: String? = null,
        )

        data class Attche(
            @SerializedName("attachurl")
            val attachUrl: String,
        )
    }
}
