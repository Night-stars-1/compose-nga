package com.srap.nga.logic.model
import com.google.gson.annotations.SerializedName
import com.srap.nga.logic.model.base.BaseResponse

data class ForumBySearchResponse(
    /**
     * 当前页码
     */
    @SerializedName("currentPage")
    val currentPage: Int = 0,
    @SerializedName("perPage")
    val perPage: Int = 0,
    @SerializedName("result")
    val result: List<Result>?,
    /**
     * 总页码
     */
    @SerializedName("totalPage")
    val totalPage: Int?
) : BaseResponse<ForumBySearchResponse>() {
    data class Result(
        @SerializedName("fid")
        val fid: Int,
        @SerializedName("id")
        val id: Int,
        @SerializedName("info")
        val info: String,
        @SerializedName("name")
        val name: String,
        @SerializedName("parent")
        val parent: Parent,
        @SerializedName("stid")
        val stid: Int
    ) {
        data class Parent(
            @SerializedName("descrip")
            val descrip: Any,
            @SerializedName("fid")
            val fid: Int,
            @SerializedName("name")
            val name: String
        )
    }
}

