package com.bdb.lottery.base.response

class BaseResponse<T> {
    var code: Int = 0
    var msg: String? = null
    var data: T? = null

    //网络请求成功
    fun isSuccess(): Boolean {
        return code >= 200 && code < 300
    }

    //200code 成功获取数据
    fun successData(): Boolean {
        return 200 == code
    }

    override fun toString(): String {
        return "BaseResponse(code=$code, msg=$msg, data=$data)"
    }

}