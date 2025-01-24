package com.srap.nga.logic.model
import com.google.gson.annotations.SerializedName
import com.srap.nga.logic.model.base.BaseResponse

data class SearchPromptResponse(
    @SerializedName("result")
    val result: List<Result>
) : BaseResponse<SearchPromptResponse>() {
    data class Result(
        @SerializedName("recom_list")
        val recomList: List<String>
    )
}