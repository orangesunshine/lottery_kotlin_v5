package com.bdb.lottery.biz.lot

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.hilt.lifecycle.ViewModelInject
import com.bdb.lottery.biz.base.BaseViewModel
import com.bdb.lottery.const.EXTRA
import com.bdb.lottery.datasource.cocos.CocosRemoteDs
import com.bdb.lottery.datasource.common.LiveDataWraper
import com.bdb.lottery.datasource.lot.LotRemoteDs
import com.bdb.lottery.datasource.lot.data.HistoryData
import com.bdb.lottery.datasource.lot.data.countdown.CountDownData
import com.bdb.lottery.extension.isSpace
import com.bdb.lottery.service.CountDownCallback
import com.bdb.lottery.service.CountDownService
import com.bdb.lottery.service.CountDownService.CountDownSub
import com.bdb.lottery.utils.thread.TThread
import dagger.hilt.android.qualifiers.ActivityContext
import timber.log.Timber
import javax.inject.Inject

class LotViewModel @ViewModelInject @Inject constructor(
    @ActivityContext val context: Context,
    private val tThread: TThread,
    private val lotRemoteDs: LotRemoteDs,
    private val cocosRemoteDs: CocosRemoteDs,
) : BaseViewModel() {
    val countDown = LiveDataWraper<CountDownData.CurrentTime?>()
    val curIssue = LiveDataWraper<HistoryData.HistoryItem?>()
    val historyIssue = LiveDataWraper<List<HistoryData.HistoryItem>?>()
    private var mGameId = -1
    fun setGameId(gameId: Int) {
        mGameId = gameId
    }

    //region 开奖历史记录
    fun getHistoryByGameId(gameId: String) {
        lotRemoteDs.getHistoryByGameId(gameId) {
            Timber.d(it.toString())
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
    private val conn = object : ServiceConnection {
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

    fun bindService(gameId: Int) {
        CountDownService.start(context, gameId)
        context.bindService(
            Intent(context, CountDownService::class.java),
            conn,
            Service.BIND_AUTO_CREATE
        )
    }

    fun unBindService(gameId: Int) {
        context.unbindService(conn)
        mSub?.unregisterCountDownCallback(gameId, mCountDownCallback)
        mSub = null
    }
    //endregion

    //region 下载cocos动画文件
    fun downloadCocos(cocosName:String){
        cocosRemoteDs.downloadSingleCocos(cocosName)
    }
    //endregion

    //region 跳转彩票页面
    companion object {
        fun start(context: Context, gameId: String, gameType: String, gameName: String?) {
            context.startActivity(Intent(context, LotActivity::class.java).apply {
                putExtra(EXTRA.ID_GAME_EXTRA, gameId)
                putExtra(EXTRA.TYPE_GAME_EXTRA, gameType)
                if (!gameName.isSpace())
                    putExtra(EXTRA.NAME_GAME_EXTRA, gameName)
            })
        }
    }
    //endregion
}