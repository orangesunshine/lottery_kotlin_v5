package com.bdb.lottery.utils.net

abstract class NetAdapter<Data> : NetCallback<Data> {
    override fun onError(code: Int, msg: String?) {
    }

    override fun onSuccess(data: Data?) {
    }

    override fun onComplete() {
    }
}