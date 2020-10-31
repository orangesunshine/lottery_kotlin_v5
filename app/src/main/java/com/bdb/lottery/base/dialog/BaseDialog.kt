package com.bdb.lottery.base.dialog

import android.os.Bundle
import android.view.*
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.bdb.lottery.R
import com.bdb.lottery.utils.Screens
import com.bdb.lottery.utils.Sizes
import timber.log.Timber

open class BaseDialog(@LayoutRes var layoutId: Int) : DialogFragment() {
    private var rootView: View? = null
    var mDimAmount = 0.5f //背景昏暗度
    var mShowBottomEnable = false//是否底部显示
    var mMargin = 0f //左右边距
    var mAnimStyle = 0 //进入退出动画
    var mOutCancel = true //点击外部取消
    var mWidth = 0f
    var mHeight = 0f

    //vars
    protected var statusbarLight = true;//状态栏是否半透明

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.dialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = super.onCreateView(inflater, container, savedInstanceState)
        return if (null != rootView) rootView else inflater.inflate(layoutId, container, false)
    }

    override fun onStart() {
        super.onStart()
        initParams()
    }

    private fun initParams() {
        val window = dialog!!.window
        if (window != null) {
            val params = window.attributes
            params.dimAmount = mDimAmount

            //设置dialog显示位置
            if (mShowBottomEnable) {
                params.gravity = Gravity.BOTTOM
            }

            //设置dialog宽度
            if (mWidth == 0f) {
                params.width = Screens.screenWidth() - 2 * Sizes.dp2px(mMargin)
            } else {
                params.width = Sizes.dp2px(mWidth)
            }

            //设置dialog高度
            if (mHeight == 0f) {
                params.height = WindowManager.LayoutParams.WRAP_CONTENT
            } else {
                params.height = Sizes.dp2px(mHeight)
            }

            //设置dialog动画
            if (mAnimStyle != 0) {
                window.setWindowAnimations(mAnimStyle)
            }
            window.attributes = params
        }
        isCancelable = mOutCancel
    }

    /**
     * 检查当前页面是否处于活跃状态
     * @param manager 当前Activity对应的fragment管理者
     * @return
     */
    protected fun checkActivityIsActive(manager: FragmentManager): Boolean {
        return !manager.isStateSaved()
    }


    override fun show(manager: FragmentManager, tag: String?) {
        if (!checkActivityIsActive(manager)) {
            return
        }
        if (!isAdded) {
            super.show(manager, tag)
        } else {
            val ft = manager.beginTransaction()
            ft.show(this)
            ft.commit()
        }
    }

    override fun dismiss() {
        Timber.d("dismiss")
        dismissAllowingStateLoss()
    }
}