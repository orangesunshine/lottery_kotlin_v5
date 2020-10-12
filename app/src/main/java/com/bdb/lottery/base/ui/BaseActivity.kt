package com.bdb.lottery.base.ui

import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.bdb.lottery.base.contract.IContract
import com.bdb.lottery.utils.UIs

abstract class BaseActivity<T : IContract.IPresenter<IContract.IView>> : AppCompatActivity(),
    IContract.IView {
    var mTransluctentStatusBar = false;//状态栏是否半透明
    private var mAlive = false;//act是不是活的
    var mActivity: AppCompatActivity? = null//当前activity引用

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //初始化变量
        initVar(savedInstanceState)

        //解析布局文件
        val content: FrameLayout = window.decorView.findViewById(android.R.id.content)
        attachView(content)
    }

    override fun attachView(root: ViewGroup) {
        root.removeAllViews()
        layoutInflater.inflate(layoutId(), root, true)

        attachStatusBar()
    }

    //状态栏
    fun attachStatusBar() {
        if (mTransluctentStatusBar) UIs.statusBarTranslucent(mActivity!!) else UIs.statusBarColor(
            mActivity!!,
            Color.BLACK
        )
    }

    override fun initVar(bundle: Bundle?) {
        mAlive = true
        mActivity = this
    }

    override fun onDestroy() {
        super.onDestroy()
        mAlive = false
        mActivity = null
    }
}