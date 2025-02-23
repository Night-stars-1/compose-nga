package com.srap.nga.logic.model.base

import com.google.gson.annotations.SerializedName
import com.srap.nga.logic.state.Code

abstract class BaseResponse<T>() {
    // 非公开字段，防止被序列化
    @SerializedName("code")
    private val _code: Code = Code.CODE_ERROR
    @SerializedName("msg")
    private val _msg: String = ""

    // 提供公共的 getter 方法
    open val code: Code get() = _code
    open val msg: String get() = _msg
}