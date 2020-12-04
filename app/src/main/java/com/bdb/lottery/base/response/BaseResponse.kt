package com.bdb.lottery.base.response

import com.bdb.lottery.const.CODE
import com.google.gson.GsonBuilder

open class BaseResponse<T>(
    var code: Int = CODE.DEFAULT_ERROR_CODE,
    var msg: String? = null,
    var data: T? = null
) {
    //网络请求成功
    fun isSuccess(): Boolean {
        return code == CODE.NET_SUCCESSFUL_CODE
    }

    //T类型Response转String
    fun mappStringResponse(): BaseResponse<String> {
        return BaseResponse(code, msg, data?.let { GsonBuilder().create().toJson(data) })
    }

    override fun toString(): String {
        return "BaseResponse(code=$code, msg=$msg, data=$data)"
    }
}

inline fun <reified T> BaseResponse<*>.errorData(): T? {
    try {
        val data = this.data
        data?.let {
            if (it is T) return it
        }
    } catch (e: Exception) {
    }
    return null
}