package com.bdb.lottery.utils

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.FileProvider
import com.bdb.lottery.utils.Threads.runOnUiThreadDelayed
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.lang.reflect.InvocationTargetException
import java.util.*

object Apps {
    private val ACTIVITY_LIFECYCLE = ActivityLifecycleImpl()

    @SuppressLint("StaticFieldLeak")
    private var sApplication: Application? = null

    /**
     * Init utils.
     *
     * Init it in the class of Application.
     *
     * @param context context
     */
    fun init(context: Context?) {
        init(context?.applicationContext ?: getApplicationByReflect())
    }

    /**
     * Init utils.
     *
     * Init it in the class of Application.
     *
     * @param app application
     */
    fun init(app: Application?) {
        sApplication?.run {
            if (null != app && app.javaClass != sApplication?.javaClass) {
                unregisterActivityLifecycleCallbacks(ACTIVITY_LIFECYCLE)
                sApplication = app
                ACTIVITY_LIFECYCLE.mActivityList.clear()
                registerActivityLifecycleCallbacks(ACTIVITY_LIFECYCLE)
            }
            return
        }
        sApplication = getApplicationByReflect()
        sApplication?.registerActivityLifecycleCallbacks(ACTIVITY_LIFECYCLE)
    }

    /**
     * Return the context of Application object.
     *
     * @return the context of Application object
     */
    fun getApp(): Application? {
        return if(null != sApplication) sApplication else getApplicationByReflect()
    }

    fun getTopActivityOrApp(): Context? {
        return if (isAppForeground()) ACTIVITY_LIFECYCLE.topActivity ?: getApp() else getApp()
    }

    fun isAppForeground(): Boolean {
        getApp()?.let {
            it.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        }?.let {
            it.runningAppProcesses
        }?.let {
            if (it.size <= 0) return false
            for (aInfo in it) {
                if (aInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                    && aInfo.processName == getApp()?.packageName
                ) return true
            }
        }
        return false
    }


    fun getCurrentProcessName(): String? {
        var name = getCurrentProcessNameByFile()
        if (!TextUtils.isEmpty(name)) return name
        name = getCurrentProcessNameByAms()
        if (!TextUtils.isEmpty(name)) return name
        name = getCurrentProcessNameByReflect()
        return name
    }

    fun fixSoftInputLeaks(window: Window) {
        val imm =
            getApp()?.let{it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager} ?: return
        val leakViews =
            arrayOf("mLastSrvView", "mCurRootView", "mServedView", "mNextServedView")
        for (leakView in leakViews) {
            try {
                val leakViewField =
                    InputMethodManager::class.java.getDeclaredField(
                        leakView
                    )?: continue
                if (!leakViewField.isAccessible) {
                    leakViewField.isAccessible = true
                }
                val obj = leakViewField[imm] as? View ?: continue
                if (obj.rootView === window.decorView.rootView) {
                    leakViewField[imm] = null
                }
            } catch (ignore: Throwable) { /**/
            }
        }
    }


    ///////////////////////////////////////////////////////////////////////////
    // private method
    ///////////////////////////////////////////////////////////////////////////
    private fun getCurrentProcessNameByFile(): String? {
        return try {
            val file =
                File("/proc/" + Process.myPid() + "/" + "cmdline")
            val mBufferedReader =
                BufferedReader(FileReader(file))
            val processName = mBufferedReader.readLine().trim { it <= ' ' }
            mBufferedReader.close()
            processName
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    private fun getCurrentProcessNameByAms(): String? {
        val am = getApp()?.let { it.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager }
            ?: return ""
        val info = am.runningAppProcesses
        if (info == null || info.size == 0) return ""
        val pid = Process.myPid()
        for (aInfo in info) {
            if (aInfo.pid == pid) {
                if (aInfo.processName != null) {
                    return aInfo.processName
                }
            }
        }
        return ""
    }

    private fun getCurrentProcessNameByReflect(): String? {
        var processName = ""
        try {
            val app: Application = getApp() ?: return null
            val loadedApkField = app.javaClass.getField("mLoadedApk")
            loadedApkField.isAccessible = true
            val loadedApk = loadedApkField[app]
            val activityThreadField =
                loadedApk.javaClass.getDeclaredField("mActivityThread")
            activityThreadField.isAccessible = true
            val activityThread = activityThreadField[loadedApk]
            val getProcessName =
                activityThread.javaClass.getDeclaredMethod("getProcessName")
            processName = getProcessName.invoke(activityThread) as String
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return processName
    }

    private fun getApplicationByReflect(): Application? {
        try {
            @SuppressLint("PrivateApi") val activityThread =
                Class.forName("android.app.ActivityThread")
            val thread = activityThread.getMethod("currentActivityThread").invoke(null)
            val app = activityThread.getMethod("getApplication").invoke(thread)
                ?: throw NullPointerException("u should init first")
            return app as Application
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
        throw NullPointerException("u should init first")
    }

    /**
     * Set animators enabled.
     */
    private fun setAnimatorsEnabled() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && ValueAnimator.areAnimatorsEnabled()) {
            return
        }
        try {
            val sDurationScaleField =
                ValueAnimator::class.java.getDeclaredField("sDurationScale")
            sDurationScaleField.isAccessible = true
            val sDurationScale = sDurationScaleField[null] as Float
            if (sDurationScale == 0f) {
                sDurationScaleField[null] = 1f
                Log.i("Utils", "setAnimatorsEnabled: Animators are enabled now!")
            }
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }

    class ActivityLifecycleImpl() : Application.ActivityLifecycleCallbacks {
        val mActivityList = LinkedList<Activity>()
        val mStatusListenerMap: MutableMap<Any, OnAppStatusChangedListener> =
            HashMap()
        val mDestroyedListenerMap: MutableMap<Activity, MutableSet<OnActivityDestroyedListener>?> =
            HashMap()
        private var mForegroundCount = 0
        private var mConfigCount = 0
        private var mIsBackground = false
        override fun onActivityCreated(
            activity: Activity,
            savedInstanceState: Bundle
        ) {
            setAnimatorsEnabled()
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
                postStatus(true)
            }
            processHideSoftInputOnActivityDestroy(activity, false)
        }

        override fun onActivityPaused(activity: Activity) {}
        override fun onActivityStopped(activity: Activity) {
            if (activity.isChangingConfigurations) {
                --mConfigCount
            } else {
                --mForegroundCount
                if (mForegroundCount <= 0) {
                    mIsBackground = true
                    postStatus(false)
                }
            }
            processHideSoftInputOnActivityDestroy(activity, true)
        }

        override fun onActivitySaveInstanceState(
            activity: Activity,
            outState: Bundle
        ) { /**/
        }

        override fun onActivityDestroyed(activity: Activity) {
            mActivityList.remove(activity)
            consumeOnActivityDestroyedListener(activity)
            fixSoftInputLeaks(activity.window)
        }

        val topActivity: Activity?
            get() {
                if (!mActivityList.isEmpty()) {
                    for (i in mActivityList.indices.reversed()) {
                        val activity = mActivityList[i]
                        if (activity == null || activity.isFinishing
                            || Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed
                        ) {
                            continue
                        }
                        return activity
                    }
                }
                val topActivityByReflect = topActivityByReflect
                topActivityByReflect?.let { setTopActivity(it) }
                return topActivityByReflect
            }

        fun addOnAppStatusChangedListener(
            `object`: Any,
            listener: OnAppStatusChangedListener
        ) {
            mStatusListenerMap[`object`] = listener
        }

        fun removeOnAppStatusChangedListener(`object`: Any?) {
            mStatusListenerMap.remove(`object`)
        }

        fun removeOnActivityDestroyedListener(activity: Activity?) {
            if (activity == null) return
            mDestroyedListenerMap.remove(activity)
        }

        fun addOnActivityDestroyedListener(
            activity: Activity?,
            listener: OnActivityDestroyedListener?
        ) {
            if (activity == null || listener == null) return
            val listeners: MutableSet<OnActivityDestroyedListener>?
            if (!mDestroyedListenerMap.containsKey(activity)) {
                listeners = HashSet()
                mDestroyedListenerMap[activity] = listeners
            } else {
                listeners = mDestroyedListenerMap[activity]
                if (listeners!!.contains(listener)) return
            }
            listeners.add(listener)
        }

        /**
         * To solve close keyboard when activity onDestroy.
         * The preActivity set windowSoftInputMode will prevent
         * the keyboard from closing when curActivity onDestroy.
         */
        private fun processHideSoftInputOnActivityDestroy(
            activity: Activity,
            isSave: Boolean
        ) {
            if (isSave) {
                val attrs = activity.window.attributes
                val softInputMode = attrs.softInputMode
                activity.window.decorView.setTag(-123, softInputMode)
                activity.window
                    .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
            } else {
                val tag = activity.window.decorView.getTag(-123) as? Int ?: return
                runOnUiThreadDelayed(Runnable {
                    activity.window.setSoftInputMode(
                        tag
                    )
                }, 100)
            }
        }

        private fun postStatus(isForeground: Boolean) {
            if (mStatusListenerMap.isEmpty()) return
            for (onAppStatusChangedListener in mStatusListenerMap.values) {
                if (onAppStatusChangedListener == null) return
                if (isForeground) {
                    onAppStatusChangedListener.onForeground()
                } else {
                    onAppStatusChangedListener.onBackground()
                }
            }
        }

        private fun setTopActivity(activity: Activity) {
            if (mActivityList.contains(activity)) {
                if (mActivityList.last != activity) {
                    mActivityList.remove(activity)
                    mActivityList.addLast(activity)
                }
            } else {
                mActivityList.addLast(activity)
            }
        }

        private fun consumeOnActivityDestroyedListener(activity: Activity) {
            val iterator: MutableIterator<Map.Entry<Activity, Set<OnActivityDestroyedListener>?>> =
                mDestroyedListenerMap.entries.iterator()
            while (iterator.hasNext()) {
                val entry =
                    iterator.next()
                if (entry.key === activity) {
                    val value = entry.value
                    for (listener in value!!) {
                        listener.onActivityDestroyed(activity)
                    }
                    iterator.remove()
                }
            }
        }

        private val topActivityByReflect: Activity?
            private get() {
                try {
                    @SuppressLint("PrivateApi") val activityThreadClass =
                        Class.forName("android.app.ActivityThread")
                    val currentActivityThreadMethod =
                        activityThreadClass.getMethod("currentActivityThread").invoke(null)
                    val mActivityListField =
                        activityThreadClass.getDeclaredField("mActivityList")
                    mActivityListField.isAccessible = true
                    val activities =
                        mActivityListField[currentActivityThreadMethod] as Map<*, *>
                            ?: return null
                    for (activityRecord in activities.values) {
                        val activityRecordClass: Class<*> = activityRecord?.javaClass ?: continue
                        val pausedField =
                            activityRecordClass.getDeclaredField("paused")
                        pausedField.isAccessible = true
                        if (!pausedField.getBoolean(activityRecord)) {
                            val activityField =
                                activityRecordClass.getDeclaredField("activity")
                            activityField.isAccessible = true
                            return activityField[activityRecord] as Activity
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return null
            }
    }

    class FileProvider4UtilCode : FileProvider() {
        override fun onCreate(): Boolean {
            init(context)
            return true
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // interface
    ///////////////////////////////////////////////////////////////////////////

    interface OnAppStatusChangedListener {
        fun onForeground()
        fun onBackground()
    }

    interface OnActivityDestroyedListener {
        fun onActivityDestroyed(activity: Activity?)
    }
}