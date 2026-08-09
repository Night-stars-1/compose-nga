package com.srap.nga.logic.model

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import com.srap.nga.logic.model.base.BaseResponse

data class FollowResponse(
    @SerializedName("result")
    val result: JsonElement? = null,
) : BaseResponse<FollowResponse>()
