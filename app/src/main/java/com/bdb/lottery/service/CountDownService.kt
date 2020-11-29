package com.bdb.lottery.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.bdb.lottery.datasource.lot.LotRemoteDs
import com.bdb.lottery.datasource.lot.data.countdown.CountDownData
import com.bdb.lottery.extension.isNullOrEmpty
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@AndroidEntryPoint
class CountDownService : BaseService() {
    @Inject
    lateinit var lotRemoteDs: LotRemoteDs
    private val mIsCountDown = AtomicBoolean(false)
    private val mCountDownCallbacks = HashMap<Int, MutableList<CountDownCallback>>()

    companion object {
        private const val KEY_GAMEIDS = "key_gameids"
        private const val KEY_FORCE_REFRESH = "key_force_refresh"

        fun start(context: Context, gameIds: Array<Int>) {
            val intent = Intent(context, CountDownService::class.java)
            intent.putExtra(KEY_GAMEIDS, gameIds)
            ContextCompat.startForegroundService(context, intent)
        }

        /**
         * forceRefresh：彩种当前期结束时，强制获取服务器倒计时时间矫正本缓存地时间
         */
        fun start(context: Context, gameId: Int) {
            val intent = Intent(context, CountDownService::class.java)
            intent.putExtra(KEY_GAMEIDS, IntArray(1) { gameId })
            intent.putExtra(KEY_FORCE_REFRESH, true)
            ContextCompat.startForegroundService(context, intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= 26) {
            val pkgName = applicationContext.packageName

            @SuppressLint("WrongConstant")
            val channel =
                NotificationChannel(pkgName, "彩种倒计时", NotificationManager.IMPORTANCE_DEFAULT)
            channel.setShowBadge(false)
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
            val notification = Notification.Builder(this@CountDownService, pkgName).build()
            startForeground(0x11111, notification)
        }
    }

    private val mCache = ConcurrentHashMap<Int, CountDownData.CountDownMapper?>()//倒计时缓存
    private val mIsGettingList = ArrayList<Int>()//正在获取倒计时接口数据
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val gameIds = intent?.getIntArrayExtra(KEY_GAMEIDS)
        if (!gameIds.isNullOrEmpty()) {
            var isGettingList: MutableList<Int> = ArrayList()
            //过滤正在请求倒计时接口的gameid
            isGettingList.addAll(gameIds!!.filter { !mIsGettingList.contains(it) })
            if (isGettingList.isNotEmpty()) {
                val isForce = intent?.getBooleanExtra(KEY_FORCE_REFRESH, false)
                val buff = StringBuilder()
                if (!isForce) {
                    isGettingList = isGettingList.filter { !isRunning(it) }.toMutableList()
                }
                if (isGettingList.isNotEmpty()) {
                    isGettingList.forEach { buff.append(it).append(",") }
                    buff.deleteCharAt(buff.length - 1)
                    lotRemoteDs.getFutureIssue(buff.toString(), {
                        mIsGettingList.addAll(isGettingList)
                    }, {
                        it?.initCurrentTime()
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
    private fun countDown() {
        if (mIsCountDown.compareAndSet(false, true)) {
            Observable.interval(1, TimeUnit.SECONDS).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation()).subscribe({
                    if (mCache.isNullOrEmpty()) return@subscribe
                    mCache.forEach {
                        val gameId = it.key
                        it.value?.let { mapper: CountDownData.CountDownMapper ->
                            val currentItem = mapper.currentTime
                            currentItem?.let { it: CountDownData.CurrentTime ->
                                if (it.isclose) {
                                    //已封盘
                                    it.openSurplusTime -= 1000
                                    if (it.openSurplusTime <= 0) {
                                        //开奖
                                        it.openSurplusTime = 0
                                        it.isFinish = true

                                        if (mapper.futureTime.isNullOrEmpty()) {
                                            start(this@CountDownService, gameId)
                                        } else {
                                            mapper.nextIssue()
                                            if (mapper.futureTime.isNullOrEmpty())
                                                start(this@CountDownService, gameId)
                                        }
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
                                mCountDownCallbacks.get(gameId)
                                    ?.forEach { callback: CountDownCallback ->
                                        callback.countDown(it)
                                    }
                            }
                        }
                    }
                }, {
                    Timber.d(it)
                })
        }
    }

    /**
     * 判断当前期是否正在进行
     */
    private fun isRunning(gameId: Int): Boolean {
        if (mCache.isEmpty()) return false
        val mapper = mCache[gameId]
        return (mapper?.currentTime?.isclose ?: true).not()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return CountDownSub()
    }

    inner class CountDownSub : Binder() {
        fun registerCountDownCallback(gameId: Int, callback: CountDownCallback) {
            if (gameId == -1) return
            var callbacks = mCountDownCallbacks[gameId]
            if (null == callbacks) {
                callbacks = ArrayList()
                mCountDownCallbacks.put(gameId, callbacks)
            }
            if (callbacks.contains(callback)) return
            callbacks.add(callback)
        }

        fun unregisterCountDownCallback(gameId: Int, callback: CountDownCallback) {
            if (gameId == -1) return
            val callbacks = mCountDownCallbacks[gameId]
            if (callbacks?.contains(callback) == true) {
                callbacks.remove(callback)
            }
        }
    }
}