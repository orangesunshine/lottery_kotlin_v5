package com.bdb.lottery.utils.ui

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TActivityLifecycle @Inject constructor(val tActivity: TActivity) :
    Application.ActivityLifecycleCallbacks {

    val mActivityStack = LinkedList<Activity>()
    val mStatusListeners = CopyOnWriteArrayList<OnAppStatusChangedListener>()

    var mForegroundCount = 0
    var mIsBackground = false
    var mConfigCount = 0

    private fun getActivityList(): List<Activity> {
        if (!mActivityStack.isNullOrEmpty()) return mActivityStack
        return tActivity.getActivitiesByReflect() ?: LinkedList<Activity>()
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

    fun getTopActivity(): Activity? {
        for (activity in getActivityList()) {
            if (tActivity.isActivityAlive(activity))
                return activity
        }
        return null
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

    ///////////////////////////////////////////////////////////////////////////
    // Application.ActivityLifecycleCallbacks
    ///////////////////////////////////////////////////////////////////////////
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        tActivity.setAnimatorsEnabled()
        setTopActivity(activity)
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
    }

    override fun onActivityResumed(activity: Activity) {
        setTopActivity(activity)
        if (mIsBackground) {
            mIsBackground = false
            postStatus(activity, true)
        }
        tActivity.processHideSoftInputOnActivityDestroy(activity, false)
    }

    override fun onActivityPaused(activity: Activity) {
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
        tActivity.processHideSoftInputOnActivityDestroy(activity, true)
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
        mActivityStack.remove(activity)
        tActivity.fixSoftInputLeaks(activity.window)
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

interface OnAppStatusChangedListener {
    fun onForeground(activity: Activity?)
    fun onBackground(activity: Activity?)
}