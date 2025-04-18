package com.bdb.lottery.utils.ui.activity

import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import com.bdb.lottery.app.BdbApp
import com.bdb.lottery.utils.thread.Threads
import timber.log.Timber
import java.util.*

object Activitys {
    /**
     * 判断activity是否是活的
     */
    fun isActivityAlive(context: Context?): Boolean {
        return (context != null && context is Activity && !context.isFinishing
                && (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1 || !context.isDestroyed))
    }

    /**
     * @return the activities which topActivity is first position
     */
    fun getActivitiesByReflect(): List<Activity> {
        val list = LinkedList<Activity>()
        var topActivity: Activity? = null
        try {
            val activityThread = getActivityThread()
            activityThread?.let {
                val mActivitiesField = activityThread.javaClass.getDeclaredField("mActivities")
                mActivitiesField.isAccessible = true
                val mActivities = mActivitiesField[activityThread] as? Map<*, *>
                    ?: return list
                val binder_activityClientRecord_map = mActivities as Map<Any, Any>
                for (activityRecord in binder_activityClientRecord_map.values) {
                    val activityClientRecordClass: Class<*> = activityRecord.javaClass
                    val activityField = activityClientRecordClass.getDeclaredField("activity")
                    activityField.isAccessible = true
                    val activity = activityField[activityRecord] as Activity
                    if (topActivity == null) {
                        val pausedField = activityClientRecordClass.getDeclaredField("paused")
                        pausedField.isAccessible = true
                        if (!pausedField.getBoolean(activityRecord)) {
                            topActivity = activity
                        } else {
                            list.add(activity)
                        }
                    } else {
                        list.add(activity)
                    }
                }
            }
        } catch (e: Exception) {
            Timber.d("getActivitiesByReflect: ${e.message}")
        }
        if (topActivity != null) {
            list.addFirst(topActivity)
        }
        return list
    }

    private fun getActivityThread(): Any? {
        val activityThread = getActivityThreadInActivityThreadStaticField()
        return activityThread ?: getActivityThreadInActivityThreadStaticMethod()
    }

    private fun getActivityThreadInActivityThreadStaticField(): Any? {
        return try {
            val activityThreadClass = Class.forName("android.app.ActivityThread")
            val sCurrentActivityThreadField =
                activityThreadClass.getDeclaredField("sCurrentActivityThread")
            sCurrentActivityThreadField.isAccessible = true
            sCurrentActivityThreadField[null]
        } catch (e: java.lang.Exception) {
            Timber.d("getActivityThreadInActivityThreadStaticField: ${e.message}")
            null
        }
    }

    private fun getActivityThreadInActivityThreadStaticMethod(): Any? {
        return try {
            val activityThreadClass = Class.forName("android.app.ActivityThread")
            activityThreadClass.getMethod("currentActivityThread").invoke(null)
        } catch (e: Exception) {
            Timber.d("getActivityThreadInActivityThreadStaticMethod: ${e.message}")
            null
        }
    }

    /**
     * Set animators enabled.
     */
    fun setAnimatorsEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && ValueAnimator.areAnimatorsEnabled()) {
            return
        }
        try {
            val sDurationScaleField = ValueAnimator::class.java.getDeclaredField("sDurationScale")
            sDurationScaleField.isAccessible = true
            val sDurationScale = sDurationScaleField[null] as Float
            if (sDurationScale == 0f) {
                sDurationScaleField[null] = 1f
                Timber.d("setAnimatorsEnabled: Animators are enabled now!")
            }
        } catch (e: Exception) {
            Timber.d("setAnimatorsEnabled: ${e.message}")
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // lifecycle end
    ///////////////////////////////////////////////////////////////////////////
    /**
     * To solve close keyboard when activity onDestroy.
     * The preActivity set windowSoftInputMode will prevent
     * the keyboard from closing when curActivity onDestroy.
     */
    fun processHideSoftInputOnActivityDestroy(activity: Activity, isSave: Boolean) {
        try {
            if (isSave) {
                val window = activity.window
                val attrs = window.attributes
                val softInputMode = attrs.softInputMode
                window.decorView.setTag(-123, softInputMode)
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            } else {
                val tag = activity.window.decorView.getTag(-123) as? Int ?: return
                Threads.retrofitUIThreadDelayed({
                    try {
                        val window = activity.window
                        window?.setSoftInputMode(tag)
                    } catch (ignore: java.lang.Exception) {
                    }
                }, 100)
            }
        } catch (ignore: java.lang.Exception) {
        }
    }
}