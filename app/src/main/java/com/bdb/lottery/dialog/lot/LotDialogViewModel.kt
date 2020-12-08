package com.bdb.lottery.dialog.lot

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.hilt.lifecycle.ViewModelInject
import com.bdb.lottery.biz.base.BaseViewModel
import com.bdb.lottery.datasource.cocos.CocosRemoteDs
import com.bdb.lottery.datasource.common.LiveDataWraper
import com.bdb.lottery.datasource.lot.LotRemoteDs
import com.bdb.lottery.datasource.lot.data.countdown.CountDownData
import com.bdb.lottery.service.CountDownCallback
import com.bdb.lottery.service.CountDownService
import com.bdb.lottery.utils.thread.TThread
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class LotDialogViewModel @ViewModelInject @Inject constructor(
    @ActivityContext val context: Context,
    private val tThread: TThread,
    private val lotRemoteDs: LotRemoteDs
) : BaseViewModel() {

    private var mGameId = -1
    fun setGameId(gameId: Int) {
        mGameId = gameId
    }

    val countDown = LiveDataWraper<CountDownData.CurrentTime?>()

    //region 倒计时
    private val mCountDownCallback = object : CountDownCallback {
        override fun countDown(currentTime: CountDownData.CurrentTime) {
            tThread.runOnUiThread {
                countDown.setData(currentTime)
            }
        }
    }

    private var mSub: CountDownService.CountDownSub? = null
    private val conn = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (service is CountDownService.CountDownSub) {
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
}