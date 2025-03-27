package com.srap.nga.logic.model

import com.google.gson.annotations.SerializedName
import com.srap.nga.logic.model.base.BaseResponse

data class RecTopicResponse(
    val result: List<Any>
) : BaseResponse<RecTopicResponse>() {
    data class Result(
        @SerializedName("app_id")
        val appId: Int,
        @SerializedName("authorid")
        val authorId: Int,
        @SerializedName("bit")
        val bit: Int,
        @SerializedName("dateline")
        val dateline: Int,
        @SerializedName("fid")
        val fid: Int,
        @SerializedName("id")
        val id: Int,
        @SerializedName("is_live")
        val isLive: Boolean,
        @SerializedName("is_market")
        val isMarket: Boolean,
        @SerializedName("is_top")
        val isTop: Boolean,
        @SerializedName("order")
        val order: Int,
        @SerializedName("position")
        val position: Int,
        @SerializedName("subject")
        val subject: String,
        @SerializedName("target_id")
        val targetId: Int,
        @SerializedName("thread_abstract")
        val threadAbstract: String,
        @SerializedName("thread_icon")
        val threadIcon: String,
        @SerializedName("tid")
        val tid: Int,
        @SerializedName("topic")
        val topic: Topic,
        @SerializedName("type")
        val type: Int
    )

    data class Topic(
        @SerializedName("attachs")
        val attachs: List<Attach>,
        @SerializedName("author")
        val author: String,
        @SerializedName("authorid")
        val authorid: Int,
        @SerializedName("fid")
        val fid: Int,
        @SerializedName("jdata")
        val jdata: Any,
        @SerializedName("lastmodify")
        val lastmodify: Int,
        @SerializedName("lastpost")
        val lastpost: Int,
        @SerializedName("lastposter")
        val lastposter: String,
        @SerializedName("parent")
        val parent: List<Any>,
        @SerializedName("postdate")
        val postdate: Int,
        @SerializedName("quote_from")
        val quoteFrom: Int,
        @SerializedName("recommend")
        val recommend: Int,
        @SerializedName("replies")
        val replies: Int,
        @SerializedName("subject")
        val subject: String,
        @SerializedName("tid")
        val tid: Int,
        @SerializedName("topic_misc")
        val topicMisc: String,
        @SerializedName("type")
        val type: Int
    )

    data class Attach(
        @SerializedName("attachurl")
        val attachUrl: String,
        @SerializedName("score")
        val score: Int
    )

    data class PageInfo(
        val currentPage: Int,
        val perPage: Int,
    )
}
