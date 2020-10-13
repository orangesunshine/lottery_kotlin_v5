package com.bdb.lottery.base.ui

import android.graphics.Color
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.bdb.lottery.base.dialog.LoadingDialog
import com.bdb.lottery.utils.UIs
import javax.inject.Inject

abstract class BaseActivity(var layoutId: Int) : AppCompatActivity() {
    @Inject
    lateinit var loading: LoadingDialog
    protected var mTransluctentStatusBar = false;//状态栏是否半透明
    private var live_ = false;

    protected val isAlive
        //act是不是活的
        get() = live_ && !isFinishing

    protected var mActivity: AppCompatActivity? = null//当前activity引用

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        //初始化变量
        initVar(savedInstanceState)

        //解析布局文件
        val content: FrameLayout = window.decorView.findViewById(android.R.id.content)
        attachView(content)
    }

    fun attachView(root: ViewGroup) {
        root.removeAllViews()
        layoutInflater.inflate(layoutId, root, true)

        attachStatusBar()
    }

    //状态栏
    fun attachStatusBar() {
        mActivity?.let {
            if (mTransluctentStatusBar) UIs.statusBarTranslucent(it)
            else UIs.statusBarColor(it, Color.BLACK)
        }
    }

    fun initVar(bundle: Bundle?) {
        live_ = true
        mActivity = this
    }

    override fun onDestroy() {
        super.onDestroy()
        live_ = false
        mActivity = null
    }
}