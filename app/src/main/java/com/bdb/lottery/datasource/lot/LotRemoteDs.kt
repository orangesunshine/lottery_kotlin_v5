package com.bdb.lottery.datasource.lot

import com.bdb.lottery.base.response.errorData
import com.bdb.lottery.datasource.lot.data.HistoryData
import com.bdb.lottery.datasource.lot.data.LotData
import com.bdb.lottery.datasource.lot.data.LotParam
import com.bdb.lottery.datasource.lot.data.countdown.CountDownData
import com.bdb.lottery.datasource.lot.data.jd.GameBetTypeData
import com.bdb.lottery.datasource.lot.data.jd.GameInitData
import com.bdb.lottery.datasource.lot.data.kg.KgBetTypeData
import com.bdb.lottery.datasource.lot.data.kg.KgInitData
import com.bdb.lottery.datasource.lot.data.wt.WtBetTypeData
import com.bdb.lottery.datasource.lot.data.wt.WtInitData
import com.bdb.lottery.utils.gson.Gsons
import com.bdb.lottery.utils.net.retrofit.RetrofitWrapper
import io.reactivex.rxjava3.disposables.Disposable
import timber.log.Timber
import javax.inject.Inject

class LotRemoteDs @Inject constructor(
    private val retrofitWrapper: RetrofitWrapper,
    private val lotApi: LotApi,
) {

    //获取当前期、未来期
    fun getFutureIssue(
        gameIds: String,
        onStart: (Disposable) -> Unit,
        success: (CountDownData?) -> Unit,
        complete: () -> Unit,
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

    //下注
    fun lot(lotParam: LotParam, success: (LotData?) -> Unit, error: (token: String) -> Unit) {
        val json = Gsons.toJson(lotParam)
        Timber.d("json: ${json}")
        retrofitWrapper.observeErrorData(lotApi.lot(json!!), success, {
            it.errorData<Map<String, String>>()
                ?.let { error(it.get("token") ?: "") }
        })
    }

    //region 经典
    fun initGame(gameId: String,success: (GameInitData?) -> Unit) {
        retrofitWrapper.observe(lotApi.initGame(gameId), success)
    }

    fun getBetType(gameId: String,success: (GameBetTypeData?) -> Unit) {
        retrofitWrapper.observe(lotApi.getBetType(gameId), success)
    }
    //endregion

    //region 传统
    fun initKgGame(gameId: String,success: (KgInitData?) -> Unit) {
        retrofitWrapper.observe(lotApi.initKgGame(gameId), success)
    }

    fun getKgBetType(gameId: String,success: (KgBetTypeData?) -> Unit) {
        retrofitWrapper.observe(lotApi.getKgBetType(gameId), success)
    }
    //endregion

    //region 微投
    fun initWtGame(gameId: String,success: (WtInitData?) -> Unit) {
        retrofitWrapper.observe(lotApi.initWtGame(gameId), success)
    }

    fun getWtBetType(gameId: String,success: (WtBetTypeData?) -> Unit) {
        retrofitWrapper.observe(lotApi.getWtBetType(gameId), success)
    }
    //endregion
}