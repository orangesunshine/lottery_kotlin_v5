package com.bdb.lottery.service

import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.bdb.lottery.datasource.lot.LotRemoteDs
import com.bdb.lottery.datasource.lot.data.countdown.CountDownData
import com.bdb.lottery.extension.isSpace
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.greenrobot.eventbus.EventBus
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class CountDownService : BaseService() {
    @Inject
    private lateinit var lotRemoteDs: LotRemoteDs
    private val mIsCountDown = AtomicBoolean(false)
    private val mCountDownCallbacks = HashMap<String, MutableList<CountDownCallback>>()

    companion object {
        private val KEY_GAMEIDS = "key_gameids"
        private val KEY_FORCE_REFRESH = "key_force_refresh"

        fun start(context: Context, gameIds: Array<String>) {
            val intent = Intent(context, CountDownService.javaClass)
            intent.putExtra(KEY_GAMEIDS, gameIds)
            context.startService(intent)
        }

        /**
         * forceRefresh：彩种当前期结束时，强制获取服务器倒计时时间矫正本缓存地时间
         */
        fun start(context: Context, gameId: String) {
            val intent = Intent(context, CountDownService.javaClass)
            intent.putExtra(KEY_GAMEIDS, Array(1) { gameId })
            intent.putExtra(KEY_FORCE_REFRESH, true)
            context.startService(intent)
        }
    }

    private val mCache = ConcurrentHashMap<String, CountDownData.CountDownMapper?>()//倒计时缓存
    private val mIsGettingList = ArrayList<String>()//正在获取倒计时接口数据
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val gameIds = intent?.getStringArrayExtra(KEY_GAMEIDS)
        if (!gameIds.isNullOrEmpty()) {
            var isGettingList: MutableList<String> = ArrayList()
            //过滤正在请求倒计时接口的gameid
            isGettingList.addAll(gameIds.filter { !it.isSpace() && !mIsGettingList.contains(it) })
            if (isGettingList.isNotEmpty()) {
                val isForce = intent?.getBooleanExtra(KEY_FORCE_REFRESH, false)
                val buff = StringBuilder()
                if (!isForce) {
                    isGettingList = isGettingList.filter { !isRunning(it) }.toMutableList()
                }
                if (buff.isNotEmpty()) {
                    isGettingList.forEach { buff.append(it).append(",") }
                    buff.deleteCharAt(buff.length - 1)
                    lotRemoteDs.getFutureIssue(buff.toString(), {
                        mIsGettingList.addAll(isGettingList)
                    }, {
                        it?.mapper(mCache)
                        countDown()
                    }, {
                        mIsGettingList.removeAll(isGettingList)
                    })
                }
            }
        }
        return START_REDELIVER_INTENT
    }

    /**
     * 定时倒计时
     */
    fun countDown() {
        if (mIsCountDown.compareAndSet(false, true)) {
            Observable.interval(1, TimeUnit.SECONDS).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation()).subscribe {
                    if (mCache.isNullOrEmpty()) return@subscribe
                    mCache.forEach {
                        val gameIdkey = it.key
                        it.value?.let { mapper: CountDownData.CountDownMapper ->
                            val currentItem = mapper.currentItem
                            currentItem?.let {
                                if (it.isclose) {
                                    //已封盘
                                    it.openSurplusTime -= 1000
                                    if (it.openSurplusTime <= 0) {
                                        //开奖
                                        it.openSurplusTime = 0
                                        it.isFinish = true

                                        mapper.nextIssue()

                                        start(this@CountDownService, gameIdkey)
                                    }
                                } else {
                                    //投注中
                                    it.betSurplusTime -= 1000
                                    if (it.betSurplusTime <= 0) {
                                        //进入封盘
                                        it.isclose = true
                                        it.openSurplusTime = it.closeTotalTime
                                    }
                                }

                                EventBus.getDefault().post(it)
                            }
                        }
                    }
                }
        }
    }

    /**
     * 判断当前期是否正在进行
     */
    private fun isRunning(gameId: String): Boolean {
        if (mCache.isEmpty()) return false
        val mapper = mCache.get(gameId)
        return (mapper?.currentItem?.isclose ?: true).not()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return CountDownSub()
    }

    inner class CountDownSub : Binder() {
        fun registerCountDownCallback(gameId: String, callback: CountDownCallback) {
            var callbacks = mCountDownCallbacks[gameId]
            if (null == callbacks) {
                callbacks = ArrayList<CountDownCallback>()
                mCountDownCallbacks.put(gameId, callbacks)
            }
            if (callbacks.contains(callback)) return
            callbacks.add(callback)
        }

        fun unregisterCountDownCallback(gameId: String, callback: CountDownCallback) {
            var callbacks = mCountDownCallbacks[gameId]
            if (callbacks?.contains(callback) == true) {
                callbacks.remove(callback)
            }
        }
    }
}