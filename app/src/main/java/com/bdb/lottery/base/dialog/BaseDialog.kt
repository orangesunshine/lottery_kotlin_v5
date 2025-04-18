package com.bdb.lottery.base.dialog

import android.os.Bundle
import android.view.*
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.bdb.lottery.R
import com.bdb.lottery.utils.ui.screen.TScreen
import com.bdb.lottery.utils.ui.size.Sizes
import com.bdb.lottery.utils.ui.size.TSize
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
open class BaseDialog(@LayoutRes var layoutId: Int) : DialogFragment() {
    private var rootView: View? = null
    var mDimAmount = 0.5f //背景昏暗度
    open var mShowBottomEnable = false//是否底部显示
    open var mMargin = 0f //左右边距
    open var mAnimStyle = 0 //进入退出动画
    open var mOutCancel = true //点击外部取消
    open var mWidth = 0f//dp
    open var mHeight = 0f

    @Inject
    lateinit var tScreen: TScreen;

    @Inject
    lateinit var tSize: TSize

    //vars
    protected var statusbarLight = true;//状态栏是否半透明

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.dialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
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
                params.width = tScreen.screenWidth() - 2 * Sizes.dp2px(mMargin)
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
    private fun checkActivityIsActive(manager: FragmentManager): Boolean {
        return !manager.isStateSaved
    }


    override fun show(manager: FragmentManager, tag: String?) {
        if (!checkActivityIsActive(manager)) {
            return
        }
        val ft = manager.beginTransaction()
        if (!isAdded) {
            ft.add(this, tag)
        } else {
            ft.show(this)
        }
        ft.commit()
    }

    override fun dismiss() {
        if (isAdded)
            dismissAllowingStateLoss()
    }
}