package com.srap.nga.logic.model

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.srap.nga.logic.model.base.BaseResponse
import com.srap.nga.logic.network.deserializer.QRCodeLoginResultAdapter


data class QRCodeLoginResponse(
    @SerializedName("result")
    @JsonAdapter(QRCodeLoginResultAdapter::class)
    val result: Result?,
): BaseResponse<QRCodeLoginResponse>() {
    data class Result(
        @SerializedName("avatar")
        val avatar: Any,
        @SerializedName("bound_mobile")
        val boundMobile: Int,
        @SerializedName("login_type")
        val loginType: Int,
        @SerializedName("token")
        val token: String,
        @SerializedName("uid")
        val uid: Int,
        @SerializedName("username")
        val username: String
    )
}