package com.bdb.lottery.base.response

data class ViewState(
    var isLoading: Boolean = false,
    var isError: Boolean = false,
    var isEmpty: Boolean = false
)