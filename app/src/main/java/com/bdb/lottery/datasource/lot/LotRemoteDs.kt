package com.bdb.lottery.datasource.lot

import com.bdb.lottery.const.IUrl
import com.bdb.lottery.datasource.cocos.CocosApi
import com.bdb.lottery.datasource.cocos.data.CocosData
import com.bdb.lottery.datasource.lot.data.HistoryData
import com.bdb.lottery.datasource.lot.data.countdown.CountDownData
import com.bdb.lottery.utils.net.retrofit.RetrofitWrapper
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class LotRemoteDs @Inject constructor(
    private val retrofitWrapper: RetrofitWrapper,
    private val lotApi: LotApi
) {

    //获取当前期、未来期
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

    //根据ID获取该彩种历史开奖
    fun getHistoryByGameId(gameId: String, success: (HistoryData?) -> Unit) {
        retrofitWrapper.observe(lotApi.getHistoryByGameId(gameId), success)
    }
}