package com.bdb.lottery.datasource.lot

import com.bdb.lottery.datasource.lot.data.countdown.CountDownData
import com.bdb.lottery.utils.net.retrofit.RetrofitWrapper
import io.reactivex.rxjava3.disposables.Disposable
import javax.inject.Inject

class LotRemoteDs @Inject constructor(
    private val retrofitWrapper: RetrofitWrapper,
    private val lotApi: LotApi
) {

    /**
     * 获取当前期、未来期
     */
    fun getFutureIssue(
        gameIds: String,
        onStart: (Disposable) -> Unit,
        success: (CountDownData?) -> Unit,
        complete: () -> Unit
    ) {
        retrofitWrapper.observe(
            lotApi.getBetting(gameIds),
            success,
            onStart = onStart,
            complete = complete
        )
    }
}