package com.srap.nga.logic.model

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.srap.nga.logic.model.base.BaseResponse
import com.srap.nga.logic.network.deserializer.AttachListOrStringAdapter
import com.srap.nga.logic.state.Code

data class TopicSubjectResponse(
    @SerializedName("bit_data")
    val bitData: Int,
    @SerializedName("code")
    override val code: Code,
    @SerializedName("currentPage")
    val currentPage: Int,
    @SerializedName("fid")
    val fid: Int,
    @SerializedName("forumname")
    val forumName: String,
    @SerializedName("msg")
    override val msg: String,
    @SerializedName("perPage")
    val perPage: Int,
    @SerializedName("result")
    val result: Result,
    @SerializedName("total")
    val total: Int,
    @SerializedName("totalPage")
    val totalPage: Int
) : BaseResponse<TopicSubjectResponse>(code, msg) {
    data class Result(
        @SerializedName("attachPrefix")
        val attachPrefix: String,
        @SerializedName("data")
        val `data`: List<Data>,
        @SerializedName("header")
        val header: Any,
        @SerializedName("sessionPrivilege")
        val sessionPrivilege: String,
        @SerializedName("subForum")
        val subForum: List<SubForum>,
        @SerializedName("topic_key")
        val topicKey: Any,
        @SerializedName("unionforum_ary_all")
        val unionforumAryAll: List<Any>
    ) {
        data class Data(
            @SerializedName("as_forum_fid")
            val asForumFid: Int,
            @SerializedName("attachs")
            @JsonAdapter(AttachListOrStringAdapter::class)
            val attachs: List<Attach>?,
            /**
             * 作者名称
             */
            @SerializedName("author")
            val author: String,
            /**
             * 作者ID
             */
            @SerializedName("authorid")
            val authorId: Int,
            @SerializedName("fid")
            val fid: Int,
            @SerializedName("forum_as_set")
            val forumAsSet: Int,
            @SerializedName("forumname")
            val forumName: String,
            @SerializedName("is_forum")
            val isForum: Boolean,
            @SerializedName("lastpost")
            val lastPost: Int,
            @SerializedName("lastposter")
            val lastPoster: String,
            @SerializedName("postdate")
            val postdate: Int,
            /**
             * 回复数
             */
            @SerializedName("replies")
            val replies: Int,
            @SerializedName("subject")
            val subject: String,
            @SerializedName("tid")
            val tid: Int,
            @SerializedName("titlefont_api")
            val titlefontApi: TitlefontApi,
            @SerializedName("topic_misc_var_bit1")
            val topicMiscVarBit1: Int,
            @SerializedName("type")
            val type: Int
        ) {
            data class Attach(
                @SerializedName("attachurl")
                val attachUrl: String
            )

            data class TitlefontApi(
                @SerializedName("bold")
                val bold: Boolean,
                @SerializedName("color")
                val color: String,
                @SerializedName("italic")
                val italic: Boolean,
                @SerializedName("underline")
                val underline: Boolean
            )
        }

        data class SubForum(
            @SerializedName("allow_checked")
            val allowChecked: Int,
            @SerializedName("checked")
            val checked: Int,
            @SerializedName("id")
            val id: Int,
            @SerializedName("info")
            val info: Any,
            @SerializedName("name")
            val name: String,
            @SerializedName("sub_type")
            val subType: Int,
            @SerializedName("tid")
            val tid: Int,
        )
    }
}