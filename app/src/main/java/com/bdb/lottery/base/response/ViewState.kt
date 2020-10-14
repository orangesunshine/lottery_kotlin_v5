package com.bdb.lottery.base.response

data class ViewState<T>(var name: String) {
    var isLoading = false
    var isError = false
    var data: T? = null
}