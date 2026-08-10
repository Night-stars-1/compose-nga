package com.srap.nga.logic.model

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.srap.nga.logic.model.base.BaseResponse
import com.srap.nga.logic.network.deserializer.PostVoteResultAdapter

data class PostVoteResponse(
    @SerializedName("result")
    @JsonAdapter(PostVoteResultAdapter::class)
    val result: Result? = null,
) : BaseResponse<PostVoteResponse>() {
    val lastState: Int?
        get() = result?.lastState

    data class Result(
        @SerializedName("last_state")
        val lastState: Int,
    )
}
