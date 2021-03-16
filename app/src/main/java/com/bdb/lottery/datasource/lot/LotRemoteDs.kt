package com.bdb.lottery.datasource.lot

import com.bdb.lottery.base.response.errorData
import com.bdb.lottery.datasource.app.AppRemoteDs
import com.bdb.lottery.datasource.lot.data.HistoryData
import com.bdb.lottery.datasource.lot.data.LotData
import com.bdb.lottery.datasource.lot.data.LotParam
import com.bdb.lottery.datasource.lot.data.countdown.CountDownData
import com.bdb.lottery.datasource.lot.data.jd.GameBetTypeData
import com.bdb.lottery.datasource.lot.data.jd.GameInitData
import com.bdb.lottery.datasource.lot.data.jd.HotData
import com.bdb.lottery.datasource.lot.data.jd.LeaveData
import com.bdb.lottery.datasource.lot.data.tr.TrBetTypeData
import com.bdb.lottery.datasource.lot.data.tr.TrInitGameData
import com.bdb.lottery.datasource.lot.data.wt.WtBetTypeData
import com.bdb.lottery.datasource.lot.data.wt.WtInitData
import com.bdb.lottery.extension.isSpace
import com.bdb.lottery.utils.cache.Caches
import com.bdb.lottery.utils.gson.Gsons
import com.bdb.lottery.utils.net.retrofit.RetrofitWrapper
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

class LotRemoteDs @Inject constructor(
    private val retrofitWrapper: RetrofitWrapper,
    private val appRemoteDs: AppRemoteDs,
    private val lotApi: LotApi,
) {

    //获取当前期、未来期
    fun getFutureIssue(
        gameIds: String,
        onStart: (Disposable) -> Unit,
        success: (CountDownData) -> Unit,
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
    fun getHistoryByGameId(gameId: String, success: (HistoryData) -> Unit) {
        retrofitWrapper.observe(lotApi.getHistoryByGameId(gameId), success)
    }

    //下注
    fun lot(
        lotParam: LotParam,
        success: (LotData) -> Unit,
        error: (token: String) -> Unit,
        onStart: ((Disposable) -> Unit)? = null,
        complete: (() -> Unit)? = null,
    ) {
        val json = Gsons.toJson(lotParam)
        retrofitWrapper.observeErrorData(lotApi.lot(json), success, {
            it.errorData<Map<String, String>>()
                ?.let { error(it["token"] ?: "") }
        }, onStart, complete)
    }

    //region 经典
    fun initGame(gameId: String, success: (GameInitData) -> Unit) {
        retrofitWrapper.observe(lotApi.initGame(gameId), success)
    }

    fun getBetType(gameId: String, success: (GameBetTypeData) -> Unit) {
        retrofitWrapper.observe(lotApi.getBetType(gameId), success)
    }

    fun getLeave(gameType:String, success: (LeaveData) -> Unit){
        retrofitWrapper.observe(lotApi.getLeave(gameType), success)
    }

    fun getHot(gameType:String, success: (HotData) -> Unit){
        retrofitWrapper.observe(lotApi.getHot(gameType), success)
    }
    //endregion

    //region 传统
    fun initTrGame(gameId: String, success: (TrInitGameData) -> Unit) {
        retrofitWrapper.observe(lotApi.initTrGame(gameId), success)
    }

    fun getTrBetType(gameId: String, success: (TrBetTypeData) -> Unit) {
        retrofitWrapper.observe(lotApi.getTrBetType(gameId), success)
    }
    //endregion

    //region 微投
    fun initWtGame(gameId: String, success: (WtInitData) -> Unit) {
        retrofitWrapper.observe(lotApi.initWtGame(gameId), success)
    }

    fun getWtBetType(gameId: String, success: (WtBetTypeData) -> Unit) {
        retrofitWrapper.observe(lotApi.getWtBetType(gameId), success)
    }
    //endregion

    //缓存玩法说明
    fun refreshHowToPlayCache() {
        appRemoteDs.cachePrePlatformParams {
            val howToPlayUrl = it.getHowToPlayUrl()
            Timber.d("howToPlayUrl:$howToPlayUrl")
            lotApi.getJs(howToPlayUrl).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe {
                    Caches.putString(
                        howToPlayUrl,
                        it.replace("var _wfsm=", "").replace("\r\n\t", "").replace("\r\n", "")
                    )
                }
        }
    }

    //缓存优先：获取玩法说明数据
    fun cachePreHowToPlay(
        success: (String) -> Unit,
    ) {
        appRemoteDs.cachePrePlatformParams {
            val howToPlayUrl = it.getHowToPlayUrl()
            val cache = Caches.getString(howToPlayUrl)
            if (!cache.isSpace()) {
                success.invoke(cache!!)
            } else {
                lotApi.getJs(howToPlayUrl).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe {
                        val ret = it.replace("var _wfsm=", "").replace("\r\n\t", "")
                        Caches.putString(howToPlayUrl, ret)
                        success.invoke(ret)
                    }
            }
        }
    }

    //缓存官方说明
    fun refreshOfficialDescCache() {
        appRemoteDs.cachePrePlatformParams {
            val gameOfficialDescUrl = it.getGameOfficialDescUrl()
            lotApi.getJs(gameOfficialDescUrl).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe {
                    Caches.putString(
                        gameOfficialDescUrl,
                        it.replace("var _Gameshows =", "").replace("\r\n\t", "")
                            .replace("\r\n", "")
                    )
                }
        }
    }

    //缓存优先：获取官方说明数据
    fun cachePreOfficialDesc(success: (String) -> Unit) {
        appRemoteDs.cachePrePlatformParams {
            val gameOfficialDescUrl = it.getGameOfficialDescUrl()
            val cache = Caches.getString(gameOfficialDescUrl)
            if (!cache.isSpace()) {
                success.invoke(cache!!)
            } else {
                lotApi.getJs(gameOfficialDescUrl).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe {
                        Caches.putString(
                            gameOfficialDescUrl,
                            it.replace("var _Gameshows =", "").replace("\r\n\t", "")
                                .replace("\r\n", "")
                        )
                        success.invoke(it)
                    }
            }
        }
    }
}