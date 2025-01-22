package com.srap.nga.logic.model

import com.google.gson.annotations.SerializedName
import com.srap.nga.logic.model.base.BaseResponse
import com.srap.nga.logic.state.Code

data class CreateQRCodeResponse(
    @SerializedName("code")
    override val code: Code,
    @SerializedName("result")
    val result: List<String>,
    @SerializedName("msg")
    override val msg: String,
) : BaseResponse<CreateQRCodeResponse>(code, msg)
