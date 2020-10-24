package com.bdb.lottery.utils.net

interface NetCallback<Data> {
    fun onError(code: Int, msg: String?)

    fun onSuccess(data: Data?)

    fun onComplete()
}