package com.srap.nga.logic.model

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.srap.nga.logic.model.base.BaseResponse
import com.srap.nga.logic.network.deserializer.FavoriteResultAdapter

data class FavoriteResponse(
    @SerializedName("result")
    @JsonAdapter(FavoriteResultAdapter::class)
    val result: List<Data>,
) : BaseResponse<FavoriteResponse>() {
    data class Data(
        val id: Int,
        val type: Int,
        val name: String,
        val length: Int,
    )
}

data class FavoriteContentResponse(
    @SerializedName("result")
    val result: Result,
    @SerializedName("currentPage")
    val currentPage: Int,
    @SerializedName("perPage")
    val perPage: Int,
    @SerializedName("total")
    val total: Int,
    @SerializedName("totalPage")
    val totalPage: Int
) : BaseResponse<FavoriteContentResponse>() {
    data class Result(
        @SerializedName("data")
        val `data`: List<Data>
    ) {
        data class Data(
            @SerializedName("author")
            val author: String,
            @SerializedName("authorid")
            val authorid: Int,
            @SerializedName("fid")
            val fid: Int,
            @SerializedName("forumname")
            val forumname: String,
            @SerializedName("lastpost")
            val lastpost: Int,
            @SerializedName("lastposter")
            val lastposter: String,
            @SerializedName("postdate")
            val postdate: Int,
            @SerializedName("replies")
            val replies: Int,
            @SerializedName("subject")
            val subject: String,
            @SerializedName("tid")
            val tid: Int,
            @SerializedName("type")
            val type: Int
        )
    }
}
