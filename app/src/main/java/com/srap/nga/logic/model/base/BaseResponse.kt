package com.srap.nga.logic.model.base

import com.srap.nga.logic.state.Code

abstract class BaseResponse<T>(code: Code, msg: String) {
    // 非公开字段，防止被序列化
    private val _code: Code = code
    private val _msg: String = msg

    // 提供公共的 getter 方法
    open val code: Code get() = _code
    open val msg: String get() = _msg
}