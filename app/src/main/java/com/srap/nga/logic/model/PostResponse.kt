package com.srap.nga.logic.model

import com.google.gson.annotations.SerializedName
import com.srap.nga.logic.model.base.BaseResponse
import com.srap.nga.logic.state.Code

data class PostResponse(
    override val code: Code,
    override val msg: String,
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
    @SerializedName("vrows")
    val vrows: Int
) : BaseResponse<PostResponse>(code, msg) {
    data class Result(
        val author: Author,
        val content: String,
        val attches: List<Attche>?,
    ) {
        data class Author(
            val uid: Int,
            val username: String,
            /**
             * 头像链接
             */
            val avatar: String,
        )

        data class Attche(
            @SerializedName("attachurl")
            val attachUrl: String,
        )
    }
}
