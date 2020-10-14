package com.bdb.lottery.base.response

class BaseResponse<T> {
    var code: Int? = null
    var msg: String? = null
    var data: T? = null
}