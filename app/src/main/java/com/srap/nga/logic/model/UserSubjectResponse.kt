package com.srap.nga.logic.model

import com.google.gson.annotations.SerializedName
import com.srap.nga.logic.model.TopicSubjectResponse.Result.Data
import com.srap.nga.logic.model.base.BaseResponse
import com.srap.nga.logic.state.Code

data class UserSubjectResponse(
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
) : BaseResponse<UserSubjectResponse>(code, msg) {
    data class Result(
        @SerializedName("data")
        val `data`: List<Data>
    )
}