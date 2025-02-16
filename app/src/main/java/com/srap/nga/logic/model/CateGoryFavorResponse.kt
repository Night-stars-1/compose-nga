package com.srap.nga.logic.model

import com.google.gson.annotations.SerializedName
import com.srap.nga.logic.model.base.BaseResponse

data class CateGoryFavorResponse(
    @SerializedName("result")
    val result: List<List<Result>>
) : BaseResponse<CateGoryFavorResponse>() {
    data class Result(
        /**
         * 社区ID
         */
        @SerializedName("fid")
        val fid: Int,
        /**
         * 社区ID
         */
        @SerializedName("id")
        val id: Int,
        /**
         * 社区名称
         */
        @SerializedName("name")
        val name: String
    )
}
