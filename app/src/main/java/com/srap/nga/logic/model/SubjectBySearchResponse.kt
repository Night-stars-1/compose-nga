package com.srap.nga.logic.model
import com.google.gson.annotations.SerializedName
import com.srap.nga.logic.model.base.BaseResponse

data class SubjectBySearchResponse(
    @SerializedName("currentPage")
    val currentPage: Int = 0,
    @SerializedName("perPage")
    val perPage: Int = 0,
    @SerializedName("result")
    val result: Result?,
    @SerializedName("total")
    val total: Int = 0,
    @SerializedName("totalPage")
    val totalPage: Int = 0
) : BaseResponse<SubjectBySearchResponse>() {
    data class Result(
        @SerializedName("data")
        val `data`: List<Data>,
        @SerializedName("msg")
        val msg: String,
        @SerializedName("table")
        val table: Any
    ) {
        data class Data(
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
            /**
             * 社区ID
             */
            @SerializedName("fid")
            val fid: Int,
            /**
             * 社区名称
             */
            @SerializedName("forumname")
            val forumName: String,
            /**
             * 是否是社区
             */
            @SerializedName("is_forum")
            val isForum: Boolean?,
            /**
             * 是否置顶
             */
            @SerializedName("is_set_elm")
            val isSetElm: Boolean,
            /**
             * 标题
             */
            @SerializedName("subject")
            val subject: String,
            /**
             * 帖子ID
             */
            @SerializedName("tid")
            val tid: Int,
            @SerializedName("type")
            val type: Int,
            /**
             * 回复数
             */
            @SerializedName("replies")
            val replies: Int
        )
    }
}