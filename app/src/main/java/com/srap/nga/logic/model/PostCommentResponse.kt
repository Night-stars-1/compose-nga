package com.srap.nga.logic.model

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import com.srap.nga.logic.model.base.BaseResponse

/**
 * 评论提交接口返回结果。
 *
 * NGA 通常返回提示字符串，但保留 JsonElement 以兼容其他客户端版本的结果结构。
 */
data class PostCommentResponse(
    @SerializedName("result")
    val result: JsonElement? = null,
) : BaseResponse<PostCommentResponse>() {
    val resultMessage: String?
        get() = result
            ?.takeIf { it.isJsonPrimitive && it.asJsonPrimitive.isString }
            ?.asString
}
