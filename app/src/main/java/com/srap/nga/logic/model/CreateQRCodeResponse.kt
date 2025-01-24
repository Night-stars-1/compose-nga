package com.srap.nga.logic.model

import com.google.gson.annotations.SerializedName
import com.srap.nga.logic.model.base.BaseResponse
import com.srap.nga.logic.state.Code

data class CreateQRCodeResponse(
    @SerializedName("result")
    val result: List<String>,
) : BaseResponse<CreateQRCodeResponse>()
