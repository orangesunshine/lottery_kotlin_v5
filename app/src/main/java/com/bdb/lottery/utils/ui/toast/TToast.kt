package com.bdb.lottery.utils.ui.toast

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import com.bdb.lottery.R
import com.bdb.lottery.extension.isSpace
import com.bdb.lottery.utils.TPermision
import com.bdb.lottery.utils.TThread
import com.bdb.lottery.utils.ui.*
import com.bdb.lottery.widget.CustomToastView
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

class ActivityToast @Inject constructor(
    private val tImage: TImage,
    private val tActivity: TActivity,
    private val tActivityLifecycle: TActivityLifecycle,
    private val tSize: TSize,
    private val tThread: TThread,
    @ApplicationContext private val context: Context,
) :
    AbsToast(context) {
    private val TAG_TOAST = "TAG_TOAST"
    private var mActivityLifecycleCallbacks: ActivityLifecycleCallbacks? = null

    override fun show(text: CharSequence?, duration: Long) {
        if (!tActivityLifecycle.isAppForeground()) {
            // try to use system toast
            showSystemToast(text, duration)
            return
        }
        var hasAliveActivity = false
        for (activity in tActivityLifecycle.getActivityList()) {
            if (!tActivity.isActivityAlive(activity)) {
                continue
            }
            hasAliveActivity = true
            cancel()
            showWithActivity(activity, text, sShowingIndex, true)
        }
        if (hasAliveActivity) {
            registerLifecycleCallback(text)
            tThread.runOnUiThreadDelayed(
                { cancel() },
                duration
            )
            ++sShowingIndex
        } else {
            // try to use system toast
            showSystemToast(text, duration)
        }
    }

    override fun cancel() {
        if (isShowing) {
            unregisterLifecycleCallback()
            for (activity in tActivityLifecycle.getActivityList()) {
                if (!tActivity.isActivityAlive(activity)) {
                    continue
                }
                val window = activity.window
                if (window != null) {
                    val decorView = window.decorView as ViewGroup
                    val toastView =
                        decorView.findViewWithTag<View>(TAG_TOAST + (sShowingIndex - 1))
                    if (toastView != null) {
                        try {
                            decorView.removeView(toastView)
                        } catch (ignored: java.lang.Exception) { /**/
                        }
                    }
                }
            }
        }
        super.cancel()
    }

    private fun showSystemToast(text: CharSequence?, duration: Long) {
        val systemToast = SystemToast(context)
        systemToast.mToast = mToast
        systemToast.show(text, duration)
    }

    private fun showWithActivity(
        activity: Activity,
        text: CharSequence?,
        index: Int,
        useAnim: Boolean
    ) {
        val window = activity.window
        if (window != null) {
            val decorView = window.decorView as ViewGroup
            val lp = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            mToastView.setText(text)
            lp.gravity = mToast.gravity
            lp.bottomMargin = mToast.yOffset + tSize.getNavBarHeight()
            lp.leftMargin = mToast.xOffset
            val toastViewSnapshot = getToastViewSnapshot(index)
            if (useAnim) {
                toastViewSnapshot.alpha = 0f
                toastViewSnapshot.animate().alpha(1f).setDuration(200).start()
            }
            decorView.addView(toastViewSnapshot, lp)
        }
    }

    private fun getToastViewSnapshot(index: Int): View {
        val bitmap: Bitmap? = tImage.view2Bitmap(mToastView)
        val toastIv = ImageView(context)
        toastIv.tag = TAG_TOAST + index
        toastIv.setImageBitmap(bitmap)
        return toastIv
    }

    private fun registerLifecycleCallback(text: CharSequence?) {
        val index = sShowingIndex
        mActivityLifecycleCallbacks = object : ActivityLifecycleCallbacks() {
            override fun onActivityCreated(activity: Activity) {
                if (isShowing) {
                    showWithActivity(activity, text, index, false)
                }
            }

        }
        tActivityLifecycle.addActivityLifecycleCallbacks(mActivityLifecycleCallbacks)
    }

    private fun unregisterLifecycleCallback() {
        tActivityLifecycle.removeActivityLifecycleCallbacks(mActivityLifecycleCallbacks)
        mActivityLifecycleCallbacks = null
    }

    private val isShowing: Boolean
        get() = mActivityLifecycleCallbacks != null

    companion object {
        private var sShowingIndex = 0
    }
}

class SystemToast @Inject constructor(
    @ApplicationContext private val context: Context,
) :
    AbsToast(context) {

    internal class SafeHandler(private val impl: Handler) : Handler() {
        override fun handleMessage(msg: Message) {
            impl.handleMessage(msg)
        }

        override fun dispatchMessage(msg: Message) {
            try {
                impl.dispatchMessage(msg)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    init {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            try {
                val mTNField = Toast::class.java.getDeclaredField("mTN")
                mTNField.isAccessible = true
                val mTN = mTNField[mToast]
                val mTNmHandlerField = mTNField.type.getDeclaredField("mHandler")
                mTNmHandlerField.isAccessible = true
                val tnHandler = mTNmHandlerField[mTN] as Handler
                mTNmHandlerField[mTN] = SafeHandler(tnHandler)
            } catch (ignored: java.lang.Exception) { /**/
            }
        }
    }

    override fun show(text: CharSequence?, duration: Long) {
        mToast.duration = duration.toInt()
        mToastView.setText(text)
        mToast.show()
    }
}

@ActivityScoped
class WindowManagerToast @Inject constructor(
    private val tThread: TThread,
    tPermision: TPermision,
    @ActivityContext private val context: Context,
) :
    AbsToast(context) {
    private var mWM: WindowManager? = null
    private val mParams: WindowManager.LayoutParams = WindowManager.LayoutParams()

    override fun show(text: CharSequence?, duration: Long) {
        cancel()
        mToastView.setText(text)
        mWM = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
        try {
            mWM?.addView(mToastView, mParams)
        } catch (ignored: Exception) { /**/
            Log.e("younger", "ignored: " + ignored + "\n context" + context.javaClass.canonicalName)
        }
        tThread.runOnUiThreadDelayed(
            { cancel() },
            duration
        )
    }

    private fun buildParams() {
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        mParams.format = PixelFormat.TRANSLUCENT
        mParams.windowAnimations = android.R.style.Animation_Toast
        mParams.title = "ToastWithoutNotification"
        mParams.flags = (WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mParams.packageName = context.getPackageName()
        mParams.gravity = mToast.gravity
        if (mParams.gravity and Gravity.HORIZONTAL_GRAVITY_MASK == Gravity.FILL_HORIZONTAL) {
            mParams.horizontalWeight = 1.0f
        }
        if (mParams.gravity and Gravity.VERTICAL_GRAVITY_MASK == Gravity.FILL_VERTICAL) {
            mParams.verticalWeight = 1.0f
        }
        mParams.x = mToast.xOffset
        mParams.y = mToast.yOffset
        mParams.horizontalMargin = mToast.horizontalMargin
        mParams.verticalMargin = mToast.verticalMargin
    }

    override fun cancel() {
        try {
            mWM?.removeViewImmediate(mToastView)
            mWM = null
        } catch (ignored: Exception) { /**/
        }
        super.cancel()
    }

    init {
        mParams.type =
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) WindowManager.LayoutParams.TYPE_TOAST else WindowManager.LayoutParams.LAST_APPLICATION_WINDOW
    }

    init {
        buildParams()
    }
}

abstract class AbsToast constructor(
    context: Context,
) :
    IToast {
    var mToast: Toast = Toast(context)
    protected val mToastView: CustomToastView = CustomToastView(context)

    init {
        mToast.view = mToastView
    }

    override fun cancel() {
        mToast.cancel()
    }

    fun show(text: CharSequence?) {
        show(text, autoDuration(text))
    }

    fun showWarning(text: CharSequence?) {
        mToastView.setToastDrawable(R.drawable.toast_warning)
        show(text, autoDuration(text))
    }

    fun showError(text: CharSequence?) {
        mToastView.setToastDrawable(R.drawable.toast_error)
        show(text, autoDuration(text))
    }

    fun showSuccess(text: CharSequence?) {
        mToastView.setToastDrawable(R.drawable.toast_succes)
        show(text, autoDuration(text))
    }

    private fun autoDuration(text: CharSequence?): Long {
        val length = if (text.isSpace()) 0 else text!!.length
        return if (length < 20) 1500 else if (length < 40) 3000 else 5000
    }
}

internal interface IToast {
    fun show(text: CharSequence?, duration: Long)
    fun cancel()
}