package com.bdb.lottery.extension

import com.bdb.lottery.const.ICode
import com.bdb.lottery.utils.net.retrofit.ApiException
import com.google.gson.JsonSyntaxException
import retrofit2.HttpException

var Throwable?.msg: String?
    get() = this?.let {
        var message: String? = null
        if (it is JsonSyntaxException) {
            message = "数据解析失败"
        } else if (it is HttpException) {
            if (it.code() >= 500)
                message = "服务器错误"
            else if (it.code() >= 400)
                message = "请求错误"
        } else if (it is ApiException) {
            message = it.response.msg
        } else if (it.message.isSpace()) {
            message = it.cause?.message
        }
        message
    }
    set(_) {}

//返回code
var Throwable?.code: Int
    get() = this?.let {
        var code: Int = ICode.DEFAULT_ERROR_CODE
        if (it is JsonSyntaxException) {
            code = ICode.JSONSYNTAX_ERROR_CODE
        } else if (it is HttpException) {
            code = it.code()
        } else if (it is ApiException) {
            code = it.response.code
        } else {
            code = ICode.DEFAULT_ERROR_CODE
        }
        code
    }
        ?: ICode.DEFAULT_ERROR_CODE
    set(_) {}