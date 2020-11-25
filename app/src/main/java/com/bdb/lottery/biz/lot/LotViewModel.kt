package com.bdb.lottery.biz.lot

import android.app.Activity
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.hilt.lifecycle.ViewModelInject
import com.bdb.lottery.biz.base.BaseViewModel
import com.bdb.lottery.const.IExtra
import com.bdb.lottery.datasource.common.LiveDataWraper
import com.bdb.lottery.datasource.lot.LotRemoteDs
import com.bdb.lottery.datasource.lot.data.HistoryData
import com.bdb.lottery.datasource.lot.data.countdown.CountDownData
import com.bdb.lottery.service.CountDownCallback
import com.bdb.lottery.service.CountDownService
import com.bdb.lottery.service.CountDownService.CountDownSub
import com.bdb.lottery.utils.TGame
import com.bdb.lottery.utils.TThread
import dagger.hilt.android.qualifiers.ActivityContext
import java.lang.StringBuilder
import javax.inject.Inject

class LotViewModel @ViewModelInject @Inject constructor(
    @ActivityContext val context: Context,
    private val tGame: TGame,
    private val tThread: TThread,
    private val lotRemoteDs: LotRemoteDs
) : BaseViewModel() {
    val countDown = LiveDataWraper<CountDownData.CurrentTime?>()
    val curIssue = LiveDataWraper<HistoryData.HistoryItem?>()
    val historyIssue = LiveDataWraper<List<HistoryData.HistoryItem?>?>()
    private lateinit var mGameId: String
    private lateinit var mGameType: String
    private lateinit var mGameName: String

    init {
        if (context is Activity) {
            mGameId = context.intent.getStringExtra(IExtra.GAMEID_EXTRA)
            mGameType = context.intent.getStringExtra(IExtra.GAMETYPE_EXTRA)
            mGameName = context.intent.getStringExtra(IExtra.GAMETYPE_EXTRA)
        }
    }

    //region 开奖历史记录
    fun getHistoryByGameId() {
        lotRemoteDs.getHistoryByGameId(mGameId) {
            historyIssue.setData(it?.filterNotNull())
            curIssue.setData(if (!it.isNullOrEmpty()) it[0] else null)
        }
    }
    //endregion

    //region 倒计时
    private val mCountDownCallback = object : CountDownCallback {
        override fun countDown(currentTime: CountDownData.CurrentTime) {
            tThread.runOnUiThread {
                countDown.setData(currentTime)
            }
        }
    }

    private var mSub: CountDownSub? = null
    val conn = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (service is CountDownSub) {
                mSub = service
                service.registerCountDownCallback(mGameId, mCountDownCallback)
            }

        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mSub?.unregisterCountDownCallback(mGameId, mCountDownCallback)
            mSub = null
        }

    }

    fun bindService() {
        CountDownService.start(context, mGameId)
        context.bindService(
            Intent(context, CountDownService::class.java),
            conn,
            Service.BIND_AUTO_CREATE
        )
    }

    fun unBindService() {
        context.unbindService(conn)
        mSub?.unregisterCountDownCallback(mGameId, mCountDownCallback)
        mSub = null
    }
    //endregion

    //region 处理期号
    fun shortIssue(issue: String): String {
        return tGame.shortIssueText(issue, mGameType)
    }

    //开奖历史左侧彩种名称、期号
    fun gameNameNdShortIssue(issue: String): String {
        return StringBuilder().append(mGameName).append(shortIssue(issue)).toString()
    }
    //endregion

    //region 获取彩种对应玩法关注号码位置（高亮显示）
    fun getBrightIndexs(playTypeName: String): MutableList<String> {
        return tGame.lotAlphaList(playTypeName, mGameType)
    }
    //endregion
}