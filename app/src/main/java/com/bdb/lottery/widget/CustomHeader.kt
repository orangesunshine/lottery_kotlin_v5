package com.bdb.lottery.widget

import android.content.Context
import android.graphics.drawable.Animatable
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.bdb.lottery.R
import com.bdb.lottery.const.ICache
import com.bdb.lottery.extension.visible
import com.bdb.lottery.module.AppEntries
import com.bdb.lottery.utils.cache.TCache
import com.scwang.smart.drawable.ProgressDrawable
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.api.RefreshKernel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.constant.SpinnerStyle
import dagger.hilt.android.EntryPointAccessors
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class CustomHeader @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : RelativeLayout(context, attrs, defStyleAttr), RefreshHeader {
    private var HEADER_LAST_UPDATE_TIME_CACHE: String = ICache.LAST_UPDATE_TIME_CACHE

    private val ivProgress //刷新动画视图
            : ImageView
    private val ivResult //刷新结果
            : ImageView
    private val tvTitle: TextView
    private val tvUpdate: TextView
    private var mProgressDrawable: ProgressDrawable
    protected var mRefreshKernel: RefreshKernel? = null
    protected var mSetPrimaryColor = false
    protected var mBackgroundColor = 0
    protected var mLastTime: Date
    protected var mLastUpdateFormat: DateFormat
    protected var mTextPulling = "下拉更新"
    protected var mTextRefreshing = "加载中"
    protected var mTextRelease = "松开更新"
    protected var mTextFinish = "刷新成功"
    protected var mTextFailed = "刷新失败"
    protected var mTextUpdate = "最后更新： HH:mm"
    var tCache: TCache =
        EntryPointAccessors.fromApplication(context, AppEntries::class.java).provideTCache()

    /**
     * 更新显示最后更新时间
     */
    fun setLastUpdateTime() {
        tvUpdate.text = mLastUpdateFormat.format(mLastTime)
    }

    /**
     * 保存最后更新时间
     */
    private fun saveLastTime() {
        mLastTime = Date()
        if (!isInEditMode) {
            tCache.putLong(HEADER_LAST_UPDATE_TIME_CACHE, mLastTime.time)
        }
    }

    override fun getView(): View {
        return this //真实的视图就是自己，不能返回null
    }

    override fun getSpinnerStyle(): SpinnerStyle {
        return SpinnerStyle.Translate //指定为平移，不能null
    }

    override fun setPrimaryColors(vararg colors: Int) {
        if (colors.size > 0) {
            val thisView: View = this
            if (thisView.background !is BitmapDrawable && !mSetPrimaryColor) {
                setPrimaryColor(colors[0])
                mSetPrimaryColor = false
            }
        }
    }

    override fun onInitialized(kernel: RefreshKernel, height: Int, maxDragHeight: Int) {
        mRefreshKernel = kernel
        kernel.requestDrawBackgroundFor(this, mBackgroundColor)
    }

    override fun onMoving(
        isDragging: Boolean,
        percent: Float,
        offset: Int,
        height: Int,
        maxDragHeight: Int,
    ) {
    }

    override fun onReleased(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {}
    override fun onStartAnimator(layout: RefreshLayout, height: Int, maxDragHeight: Int) {
        ivResult.visible(false)
        ivProgress.visible(true)
        mProgressDrawable.start()
    }

    override fun onFinish(layout: RefreshLayout, success: Boolean): Int {
        mProgressDrawable.stop()
        if (success) {
            saveLastTime()
        }
        ivResult.visible(true)
        ivProgress.visible(false)
        tvTitle.text = if (success) mTextFinish else mTextFailed
        return 500 //延迟500毫秒之后再弹回
    }

    override fun onHorizontalDrag(percentX: Float, offsetX: Int, offsetMax: Int) {}
    override fun isSupportHorizontalDrag(): Boolean {
        return false
    }

    override fun onStateChanged(
        refreshLayout: RefreshLayout,
        oldState: RefreshState,
        newState: RefreshState,
    ) {
        when (newState) {
            RefreshState.None, RefreshState.PullDownToRefresh -> {
                tvUpdate.visible(true)
                ivProgress.visible(false)
                ivResult.visible(false)
                tvTitle.text = mTextPulling
                setLastUpdateTime();
            }
            RefreshState.ReleaseToRefresh -> tvTitle.text = mTextRelease
            RefreshState.Refreshing -> {
                tvUpdate.visible(false)
                ivProgress.visible(true)
                ivResult.visible(false)
                tvTitle.text = mTextRefreshing
            }
        }
    }

    protected fun self(): CustomHeader {
        return this
    }

    fun setPrimaryColor(@ColorInt primaryColor: Int): CustomHeader {
        mSetPrimaryColor = true
        mBackgroundColor = primaryColor
        if (mRefreshKernel != null) {
            mRefreshKernel!!.requestDrawBackgroundFor(this, primaryColor)
        }
        return self()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            ivProgress.animate().cancel()
        }
        val drawable = ivProgress.drawable
        if (drawable is Animatable) {
            if ((drawable as Animatable).isRunning) {
                (drawable as Animatable).stop()
            }
        }
    }

    init {
        inflate(context, R.layout.refresh_header_common_layout, this)
        HEADER_LAST_UPDATE_TIME_CACHE += context.javaClass.name
        mLastTime =
            Date(tCache.getLong(HEADER_LAST_UPDATE_TIME_CACHE, System.currentTimeMillis()))
        mProgressDrawable = ProgressDrawable()
        ivProgress = findViewById(R.id.srl_classics_progress)
        ivResult = findViewById(R.id.iv_refresh_result)
        tvTitle = findViewById(R.id.srl_classics_title)
        tvUpdate = findViewById(R.id.srl_classics_update)
        ivProgress.setImageDrawable(mProgressDrawable)
        ivProgress.visible(false)
        ivResult.visible(false)
        mLastUpdateFormat = SimpleDateFormat(mTextUpdate)
        tvTitle.text = if (isInEditMode) mTextRefreshing else mTextPulling
        var fragments: List<Fragment>? = null
        if (context is FragmentActivity) {
            try {
                fragments = context.supportFragmentManager.fragments
            } catch (e: Exception) {
            }
        }
        if (null == fragments || !fragments.isEmpty()) setLastUpdateTime()
    }
}