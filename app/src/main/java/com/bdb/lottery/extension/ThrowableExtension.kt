package com.bdb.lottery.extension

import com.bdb.lottery.const.CODE
import com.bdb.lottery.utils.net.retrofit.ApiException
import com.google.gson.JsonSyntaxException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

var Throwable?.msg: String?
    get() = this?.let {
        var message: String? = null
        if (it is JsonSyntaxException) {
            message = "数据解析失败"
        } else if (it is HttpException) {
            val code = it.code()
            if (code >= 500)
                message = "服务器错误"
            else if (code >= 400) {
                message = when (code) {
                    400 -> "参数错误"
                    401 -> "身份未授权"
                    403 -> "禁止访问"
                    404 -> "地址未找到"
                    else -> "请求错误"
                }
            }
        } else if (it is UnknownHostException) {
            message = "网络异常"
        } else if (it is ConnectException) {
            message = "网络异常"
        } else if (it is SocketException) {
            message = "服务异常"
        } else if (it is SocketTimeoutException) {
            message = "响应超时"
        } else if (it is ClassCastException) {
            message = "数据异常"
        } else if (it is ApiException) {
            message = it.response.msg
        } else {
            val msg = it.message
            message = if (msg.isSpace()) it.cause?.message else msg
        }
        message
    }
    set(_) {}

//返回code
var Throwable?.code: Int
    get() = this?.let {
        var code: Int = CODE.DEFAULT_ERROR_CODE
        if (it is JsonSyntaxException) {
            code = CODE.JSONSYNTAX_ERROR_CODE
        } else if (it is HttpException) {
            code = it.code()
        } else if (it is ApiException) {
            code = it.response.code
        } else {
            code = CODE.DEFAULT_ERROR_CODE
        }
        code
    }
        ?: CODE.DEFAULT_ERROR_CODE
    set(_) {}