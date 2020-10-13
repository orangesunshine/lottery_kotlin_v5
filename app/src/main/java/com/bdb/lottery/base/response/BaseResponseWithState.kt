package com.bdb.lottery.base.response

data class BaseResponseWithState<T>(var name: String) {
    var isLoading = false
    var isError = false
    var isNetConnected = true
    var data: T? = null
}