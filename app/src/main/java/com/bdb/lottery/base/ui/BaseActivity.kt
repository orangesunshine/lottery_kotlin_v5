package com.bdb.lottery.base.ui

import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.bdb.lottery.R
import com.bdb.lottery.base.dialog.LoadingDialog
import com.bdb.lottery.base.response.ViewState
import com.bdb.lottery.const.ITAG
import com.bdb.lottery.extension.load
import com.bdb.lottery.extension.statusbar
import com.bdb.lottery.widget.LoadingLayout
import javax.inject.Inject
import kotlin.properties.Delegates

abstract class BaseActivity(var layoutId: Int) : AppCompatActivity() {
    @Inject
    lateinit var loading: LoadingDialog
    protected var statusbarLight = true;//状态栏是否半透明
    private var live_ = false;
    protected val loadingLayout: LoadingLayout
        get() = findViewById(R.id.id_common_loadinglayout)

    val statusBar: ViewGroup
        get() = findViewById(R.id.id_common_statusbar_layout)

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
        statusbar(statusbarLight)

        lifecycle.addObserver(loading)
    }

    fun attachView(root: ViewGroup) {
        root.removeAllViews()
        layoutInflater.inflate(layoutId, root, true)
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

    fun show() {
        loading.show(supportFragmentManager, ITAG.COMMON_LOADING)
    }

    fun hide() {
        loading.dismissAllowingStateLoss()
    }

    fun error() {
        loadingLayout.showError()
    }

    fun empty() {
        loadingLayout.showEmpty()
    }

    protected fun <D, T : ViewState<D>> observe(
        data: LiveData<T>,
        block: (D) -> Any
    ) {
        data.observe(this, Observer {
            load(it.isLoading)
        })
    }
}