package com.bdb.lottery.base.response

data class ViewState<T>(var data: T?) {
    var isLoading = false
    var isError = false
}