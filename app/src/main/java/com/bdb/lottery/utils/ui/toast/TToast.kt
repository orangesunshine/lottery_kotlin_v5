package com.bdb.lottery.utils.ui.toast

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.annotation.CallSuper
import androidx.core.view.ViewCompat
import com.bdb.lottery.R
import com.bdb.lottery.module.AppEntries
import com.bdb.lottery.utils.TPermision
import com.bdb.lottery.utils.TThread
import com.bdb.lottery.utils.ui.*
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject

class UtilsMaxWidthRelativeLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : RelativeLayout(context, attrs, defStyleAttr) {

    private val tSize: TSize =
        EntryPointAccessors.fromApplication(context, AppEntries::class.java).provideTSize()
    private val tScreen: TScreen =
        EntryPointAccessors.fromApplication(context, AppEntries::class.java).provideTScreen()
    private val SPACING: Int = tSize.dp2px(80f)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMaxSpec =
            MeasureSpec.makeMeasureSpec(tScreen.screenSize()[0] - SPACING,
                MeasureSpec.AT_MOST)
        super.onMeasure(widthMaxSpec, heightMeasureSpec)
    }
}

class ActivityToast @Inject constructor(
    private val tImage: TImage,
    private val tActivity: TActivity,
    private val tActivityLifecycle: TActivityLifecycle,
    private val tSize: TSize,
    private val tThread: TThread,
    private val tView: TView,
    @ApplicationContext private val context: Context,
) :
    AbsToast(tView, context) {
    private val TAG_TOAST = "TAG_TOAST"
    private var mActivityLifecycleCallbacks: ActivityLifecycleCallbacks? = null
    override fun show(duration: Int) {
        if (mToast == null) return
        if (!tActivityLifecycle.isAppForeground()) {
            // try to use system toast
            showSystemToast(duration)
            return
        }
        var hasAliveActivity = false
        for (activity in tActivityLifecycle.getActivityList()) {
            if (!tActivity.isActivityAlive(activity)) {
                continue
            }
            hasAliveActivity = true
            showWithActivity(activity, sShowingIndex, true)
        }
        if (hasAliveActivity) {
            registerLifecycleCallback()
            tThread.runOnUiThreadDelayed({ cancel() },
                duration.toLong())
            ++sShowingIndex
        } else {
            // try to use system toast
            showSystemToast(duration)
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

    private fun showSystemToast(duration: Int) {
        val systemToast = SystemToast(tView, context)
        systemToast.mToast = mToast
        systemToast.show(duration)
    }

    private fun showWithActivity(activity: Activity, index: Int, useAnim: Boolean) {
        val window = activity.window
        if (window != null) {
            val decorView = window.decorView as ViewGroup
            val lp = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            lp.gravity = mToast!!.gravity
            lp.bottomMargin = mToast!!.yOffset + tSize.getNavBarHeight()
            lp.leftMargin = mToast!!.xOffset
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

    private fun registerLifecycleCallback() {
        val index = sShowingIndex
        mActivityLifecycleCallbacks = object : ActivityLifecycleCallbacks() {
            override fun onActivityCreated(activity: Activity) {
                if (isShowing) {
                    showWithActivity(activity, index, false)
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
        private get() = mActivityLifecycleCallbacks != null

    companion object {
        private var sShowingIndex = 0
    }
}

class SystemToast @Inject constructor(
    tView: TView,
    @ApplicationContext private val context: Context,
) :
    AbsToast(tView, context) {
    override fun show(duration: Int) {
        if (mToast == null) return
        mToast!!.duration = duration
        mToast!!.show()
    }

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
}

class WindowManagerToast @Inject constructor(
    tView: TView,
    private val tThread: TThread,
    tPermision: TPermision,
    @ApplicationContext private val context: Context,
) :
    AbsToast(tView, context) {
    private val type = tPermision.toastType()
    private var mWM: WindowManager? = null
    private val mParams: WindowManager.LayoutParams = WindowManager.LayoutParams()
    override fun show(duration: Int) {
        if (mToast == null) return
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        mParams.format = PixelFormat.TRANSLUCENT
        mParams.windowAnimations = android.R.style.Animation_Toast
        mParams.title = "ToastWithoutNotification"
        mParams.flags = (WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mParams.packageName = context.getPackageName()
        mParams.gravity = mToast!!.gravity
        if (mParams.gravity and Gravity.HORIZONTAL_GRAVITY_MASK == Gravity.FILL_HORIZONTAL) {
            mParams.horizontalWeight = 1.0f
        }
        if (mParams.gravity and Gravity.VERTICAL_GRAVITY_MASK == Gravity.FILL_VERTICAL) {
            mParams.verticalWeight = 1.0f
        }
        mParams.x = mToast!!.xOffset
        mParams.y = mToast!!.yOffset
        mParams.horizontalMargin = mToast!!.horizontalMargin
        mParams.verticalMargin = mToast!!.verticalMargin
        mWM = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
        try {
            if (mWM != null) {
                mWM!!.addView(mToastView, mParams)
            }
        } catch (ignored: Exception) { /**/
        }
        tThread.runOnUiThreadDelayed({ cancel() },
            duration.toLong())
    }

    override fun cancel() {
        try {
            if (mWM != null) {
                mWM!!.removeViewImmediate(mToastView)
                mWM = null
            }
        } catch (ignored: Exception) { /**/
        }
        super.cancel()
    }

    init {
        mParams.type = type
    }
}

abstract class AbsToast constructor(
    private val tView: TView,
    context: Context,
) :
    IToast {
    protected val mGravity = -1
    protected val mXOffset = -1
    protected val mYOffset = -1
    protected val mIcons = arrayOfNulls<Drawable>(4)
    var mToast: Toast?
    protected var mToastView: View? = null

    override fun setToastView(view: View?) {
        mToastView = view
        mToast?.view = mToastView
    }

    fun tryApplyUtilsToastView(text: CharSequence?): View? {
        val toastView: View = tView.layoutId2View(R.layout.toast_text)
        val messageTv = toastView.findViewById<TextView>(R.id.toast_message_tv)
        val bg = toastView.background.mutate() as GradientDrawable
        bg.setColor(Color.parseColor("#BB000000"))
        messageTv.setTextColor(Color.WHITE)
        messageTv.text = text
        if (mIcons.get(0) != null) {
            val leftIconView = toastView.findViewById<View>(R.id.toast_left_icon)
            ViewCompat.setBackground(leftIconView, mIcons.get(0))
            leftIconView.visibility = View.VISIBLE
        }
        if (mIcons.get(1) != null) {
            val topIconView = toastView.findViewById<View>(R.id.toast_top_icon)
            ViewCompat.setBackground(topIconView, mIcons.get(1))
            topIconView.visibility = View.VISIBLE
        }
        if (mIcons.get(2) != null) {
            val rightIconView = toastView.findViewById<View>(R.id.toast_right_icon)
            ViewCompat.setBackground(rightIconView, mIcons.get(2))
            rightIconView.visibility = View.VISIBLE
        }
        if (mIcons.get(3) != null) {
            val bottomIconView = toastView.findViewById<View>(R.id.toast_bottom_icon)
            ViewCompat.setBackground(bottomIconView, mIcons.get(3))
            bottomIconView.visibility = View.VISIBLE
        }
        return toastView
    }

    override fun setToastView(text: CharSequence?) {
        tryApplyUtilsToastView(text)?.let {
            setToastView(it)
        } ?: let {

        }
        mToastView = mToast?.view
        if (mToastView == null || mToastView!!.findViewById<View?>(R.id.toast_message_tv) == null) {
            setToastView(tView.layoutId2View(R.layout.toast_text))
        }
        val messageTv = mToastView!!.findViewById<TextView>(R.id.toast_message_tv)
        messageTv.text = text
    }

    @CallSuper
    override fun cancel() {
        Timber.d("AbsToast: cancel")
        if (mToast != null) {
            mToast!!.cancel()
        }
        mToast = null
        mToastView = null
    }

    init {
        mToast = Toast(context)
        if (mGravity != -1 || mXOffset != -1 || mYOffset != -1) {
            mToast!!.setGravity(mGravity, mXOffset, mYOffset)
        }
    }
}

internal interface IToast {
    fun setToastView(view: View?)
    fun setToastView(text: CharSequence?)
    fun show(duration: Int)
    fun cancel()
}