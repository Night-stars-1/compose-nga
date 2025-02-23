package com.srap.nga.logic.model

import com.google.gson.annotations.SerializedName
import com.srap.nga.logic.model.base.BaseResponse

data class CommonResponse(
    @SerializedName("result")
    val result: List<String>,
) : BaseResponse<CommonResponse>()
