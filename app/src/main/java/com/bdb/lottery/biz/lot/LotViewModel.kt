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
import com.bdb.lottery.datasource.account.AccountRemoteDs
import com.bdb.lottery.datasource.app.AppRemoteDs
import com.bdb.lottery.datasource.common.LiveDataWraper
import com.bdb.lottery.datasource.lot.LotRemoteDs
import com.bdb.lottery.datasource.lot.data.countdown.CountDownData
import com.bdb.lottery.service.CountDownCallback
import com.bdb.lottery.service.CountDownService
import com.bdb.lottery.service.CountDownService.CountDownSub
import com.bdb.lottery.utils.cache.TCache
import dagger.hilt.android.qualifiers.ActivityContext
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class LotViewModel @ViewModelInject @Inject constructor(
    @ActivityContext val context: Context,
    private val lotRemoteDs: LotRemoteDs
) : BaseViewModel() {
    val mCountDown = LiveDataWraper<CountDownData.CurrentItem?>()
    private lateinit var mGameId: String

    private val mCountDownCallback = object : CountDownCallback {
        override fun countDown(mapper: CountDownData.CountDownMapper?) {
            mCountDown.setData(mapper?.currentItem)
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
            mSub?.registerCountDownCallback(mGameId, mCountDownCallback)
            mSub = null
        }

    }

    init {
        if (context is Activity) {
            mGameId = context.intent.getStringExtra(IExtra.GAMEID_EXTRA)
            mGameId.let { CountDownService.start(context, it) }

            context.bindService(
                Intent(context, CountDownService.javaClass),
                conn,
                Service.BIND_AUTO_CREATE
            )
        }
    }
}