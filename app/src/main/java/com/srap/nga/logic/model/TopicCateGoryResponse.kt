package com.srap.nga.logic.model

import com.google.gson.annotations.SerializedName
import com.srap.nga.logic.model.base.BaseResponse
import com.srap.nga.logic.state.Code

data class TopicCateGoryResponse(
    override val code: Code,
    override val msg: String,
    /**
     * 推荐版块
     */
    @SerializedName("forum_recommend")
    val forumRecommend: ForumRecommend,
    /**
     * 版块列表
     */
    @SerializedName("result")
    val result: List<Result>
) : BaseResponse<TopicCateGoryResponse>(code, msg) {
    /**
     * 推荐版块
     */
    data class ForumRecommend(
        @SerializedName("groups")
        val groups: List<Group>,
        @SerializedName("id")
        val uid: Int,
        @SerializedName("_id")
        val id: String,
        @SerializedName("name")
        val name: String
    ) {
        data class Group(
            @SerializedName("forums")
            val forums: List<Forum>,
            @SerializedName("id")
            val id: Int,
            @SerializedName("info")
            val info: String,
            @SerializedName("name")
            val name: String
        ) {
            data class Forum(
                @SerializedName("fid")
                val fid: Int,
                @SerializedName("icon")
                val icon: String,
                @SerializedName("id")
                val id: Int,
                @SerializedName("info")
                val info: String,
                @SerializedName("is_forumlist")
                val isForumlist: Boolean,
                @SerializedName("name")
                val name: String,
                @SerializedName("stid")
                val stid: Int
            )
        }
    }

    /**
     * 版块
     */
    data class Result(
        @SerializedName("groups")
        val groups: List<Group>,
        @SerializedName("id")
        val uid: Int,
        @SerializedName("_id")
        val id: String,
        @SerializedName("name")
        val name: String
    ) {
        data class Group(
            @SerializedName("forums")
            val forums: List<Forum>,
            @SerializedName("id")
            val id: Int,
            @SerializedName("info")
            val info: String,
            @SerializedName("name")
            val name: String
        ) {
            data class Forum(
                @SerializedName("bit")
                val bit: Int,
                @SerializedName("clp")
                val clp: Int,
                @SerializedName("fid")
                val fid: Int,
                @SerializedName("icon")
                val icon: String,
                @SerializedName("id")
                val id: Int,
                @SerializedName("info")
                val info: String,
                @SerializedName("infoL")
                val infoL: String,
                @SerializedName("infoS")
                val infoS: String,
                @SerializedName("is_forumlist")
                val isForumlist: Boolean,
                @SerializedName("name")
                val name: String,
                @SerializedName("nameS")
                val nameS: String,
                @SerializedName("stid")
                val stid: Int
            )
        }
    }
}


