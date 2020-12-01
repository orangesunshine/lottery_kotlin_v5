package com.bdb.lottery.utils.ui.activity

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import com.bdb.lottery.app.BdbApp
import com.bdb.lottery.base.response.BaseResponse
import com.bdb.lottery.biz.login.LoginActivity
import com.bdb.lottery.const.ICode
import com.bdb.lottery.extension.start
import com.bdb.lottery.utils.thread.TThread
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TActivityLifecycle @Inject constructor(
    private val tThread: TThread,
) :
    Application.ActivityLifecycleCallbacks {

    private val mActivityStack = LinkedList<Activity>()
    private val mStatusListeners = CopyOnWriteArrayList<OnAppStatusChangedListener>()
    private val mActivityLifecycleCallbacksMap =
        ConcurrentHashMap<Activity, CopyOnWriteArrayList<ActivityLifecycleCallbacks>>()
    private val STUB = Activity()
    var mForegroundCount = 0
    var mIsBackground = false
    var mConfigCount = 0

    fun addActivityLifecycleCallbacks(listener: ActivityLifecycleCallbacks?) {
        addActivityLifecycleCallbacks(STUB,
            listener)
    }

    fun addActivityLifecycleCallbacks(
        activity: Activity?,
        listener: ActivityLifecycleCallbacks?,
    ) {
        if (activity == null || listener == null) return
        tThread.runOnUiThread {
            addActivityLifecycleCallbacksInner(activity,
                listener)
        }
    }

    private fun addActivityLifecycleCallbacksInner(
        activity: Activity,
        callbacks: ActivityLifecycleCallbacks,
    ) {
        var callbacksList: MutableList<ActivityLifecycleCallbacks>? =
            mActivityLifecycleCallbacksMap[activity]?.toMutableList()
        if (callbacksList == null) {
            callbacksList = CopyOnWriteArrayList()
            mActivityLifecycleCallbacksMap[activity] = callbacksList
        } else {
            if (callbacksList.contains(callbacks)) return
        }
        callbacksList.add(callbacks)
    }

    fun removeActivityLifecycleCallbacks(activity: Activity?) {
        if (activity == null) return
        tThread.runOnUiThread { mActivityLifecycleCallbacksMap.remove(activity) }
    }

    fun removeActivityLifecycleCallbacks(callbacks: ActivityLifecycleCallbacks?) {
        removeActivityLifecycleCallbacks(STUB,
            callbacks)
    }

    private fun removeActivityLifecycleCallbacks(
        activity: Activity?,
        callbacks: ActivityLifecycleCallbacks?,
    ) {
        if (activity == null || callbacks == null) return
        tThread.runOnUiThread {
            removeActivityLifecycleCallbacksInner(activity,
                callbacks)
        }
    }

    private fun removeActivityLifecycleCallbacksInner(
        activity: Activity,
        callbacks: ActivityLifecycleCallbacks,
    ) {
        val callbacksList: MutableList<ActivityLifecycleCallbacks>? =
            mActivityLifecycleCallbacksMap[activity]
        if (callbacksList != null && !callbacksList.isEmpty()) {
            callbacksList.remove(callbacks)
        }
    }

    private fun consumeActivityLifecycleCallbacks(activity: Activity, event: Lifecycle.Event) {
        consumeLifecycle(activity, event, mActivityLifecycleCallbacksMap[activity])
        consumeLifecycle(activity, event,
            mActivityLifecycleCallbacksMap[STUB])
    }

    private fun consumeLifecycle(
        activity: Activity,
        event: Lifecycle.Event,
        listeners: List<ActivityLifecycleCallbacks>?,
    ) {
        if (listeners == null) return
        for (listener in listeners) {
            listener.onLifecycleChanged(activity, event)
            if (event == Lifecycle.Event.ON_CREATE) {
                listener.onActivityCreated(activity)
            } else if (event == Lifecycle.Event.ON_START) {
                listener.onActivityStarted(activity)
            } else if (event == Lifecycle.Event.ON_RESUME) {
                listener.onActivityResumed(activity)
            } else if (event == Lifecycle.Event.ON_PAUSE) {
                listener.onActivityPaused(activity)
            } else if (event == Lifecycle.Event.ON_STOP) {
                listener.onActivityStopped(activity)
            } else if (event == Lifecycle.Event.ON_DESTROY) {
                listener.onActivityDestroyed(activity)
            }
        }
        if (event == Lifecycle.Event.ON_DESTROY) {
            mActivityLifecycleCallbacksMap.remove(activity)
        }
    }

    fun getActivityList(): List<Activity> {
        if (!mActivityStack.isNullOrEmpty()) return mActivityStack
        return Activitys.getActivitiesByReflect() ?: LinkedList<Activity>()
    }

    private fun setTopActivity(activity: Activity) {
        if (mActivityStack.contains(activity)) {
            if (!mActivityStack.first.equals(activity)) {
                mActivityStack.remove(activity)
                mActivityStack.addFirst(activity)
            }
        } else {
            mActivityStack.addFirst(activity)
        }
    }

    fun addOnAppStatusChangedListener(listener: OnAppStatusChangedListener) {
        mStatusListeners.add(listener)
    }

    fun removeOnAppStatusChangedListener(listener: OnAppStatusChangedListener) {
        mStatusListeners.remove(listener)
    }

    fun isAppForeground(): Boolean {
        return !mIsBackground
    }

    //获取顶部栈context
    fun getTopActivityOrApp(): Context {
        return getTopActivity() ?: BdbApp.sApp
    }

    private fun getTopActivity(): Activity? {
        for (activity in getActivityList()) {
            if (Activitys.isActivityAlive(activity))
                return activity
        }
        return null
    }

    //顶层activity打开页面
    private inline fun <reified T : Activity> topStartActivity() {
        val topActivity = getTopActivity()
        if (isAppForeground() && topActivity !is T) {
            topActivity?.start<T>()
            popAllActivity()
        }
    }

    private fun popAllActivity() {
        var pop = mActivityStack.pop()
        while (null != pop) {
            pop.finish()
            pop = mActivityStack.pop()
        }
    }

    //根据后端code跳转登录
    fun topLogin(response: BaseResponse<*>) {
        if (ICode.LIST_TOLOGIN.contains(response.code))
            topStartActivity<LoginActivity>()
    }

    ///////////////////////////////////////////////////////////////////////////
    // Application.ActivityLifecycleCallbacks
    ///////////////////////////////////////////////////////////////////////////
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        Activitys.setAnimatorsEnabled()
        setTopActivity(activity)
        consumeActivityLifecycleCallbacks(activity, Lifecycle.Event.ON_CREATE)
    }

    override fun onActivityStarted(activity: Activity) {
        if (!mIsBackground) {
            setTopActivity(activity)
        }
        if (mConfigCount < 0) {
            ++mConfigCount
        } else {
            ++mForegroundCount
        }
        consumeActivityLifecycleCallbacks(activity, Lifecycle.Event.ON_START)
    }

    override fun onActivityResumed(activity: Activity) {
        setTopActivity(activity)
        if (mIsBackground) {
            mIsBackground = false
            postStatus(activity, true)
        }
        Activitys.processHideSoftInputOnActivityDestroy(activity, false)
        consumeActivityLifecycleCallbacks(activity, Lifecycle.Event.ON_RESUME)
    }

    override fun onActivityPaused(activity: Activity) {
        consumeActivityLifecycleCallbacks(activity, Lifecycle.Event.ON_PAUSE)
    }

    override fun onActivityStopped(activity: Activity) {
        if (activity.isChangingConfigurations) {
            --mConfigCount
        } else {
            --mForegroundCount
            if (mForegroundCount <= 0) {
                mIsBackground = true
                postStatus(activity, false)
            }
        }
        Activitys.processHideSoftInputOnActivityDestroy(activity, true)
        consumeActivityLifecycleCallbacks(activity, Lifecycle.Event.ON_STOP)
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        mActivityStack.remove(activity)
        Activitys.fixSoftInputLeaks(activity.window)
        consumeActivityLifecycleCallbacks(activity, Lifecycle.Event.ON_DESTROY)
    }

    private fun postStatus(activity: Activity, isForeground: Boolean) {
        if (mStatusListeners.isEmpty()) return
        for (statusListener in mStatusListeners) {
            if (isForeground) {
                statusListener.onForeground(activity)
            } else {
                statusListener.onBackground(activity)
            }
        }
    }
}

open class ActivityLifecycleCallbacks {
    open fun onActivityCreated(activity: Activity) {/**/
    }

    fun onActivityStarted(activity: Activity) { /**/
    }

    fun onActivityResumed(activity: Activity) { /**/
    }

    fun onActivityPaused(activity: Activity) { /**/
    }

    fun onActivityStopped(activity: Activity) { /**/
    }

    fun onActivityDestroyed(activity: Activity) { /**/
    }

    fun onLifecycleChanged(activity: Activity, event: Lifecycle.Event) { /**/
    }
}

interface OnAppStatusChangedListener {
    fun onForeground(activity: Activity?)
    fun onBackground(activity: Activity?)
}