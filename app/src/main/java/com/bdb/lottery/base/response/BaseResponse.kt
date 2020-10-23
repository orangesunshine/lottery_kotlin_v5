package com.bdb.lottery.base.response

class BaseResponse<T> {
    var code: Int = 0
    var msg: String? = null
    var data: T? = null

    fun isSuccess(): Boolean {
        return null != code && code >= 200 && code < 300
    }

    override fun toString(): String {
        return "BaseResponse(code=$code, msg=$msg, data=$data)"
    }

}