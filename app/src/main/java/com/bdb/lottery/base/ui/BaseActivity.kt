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
import com.bdb.lottery.const.ITag
import com.bdb.lottery.extension.load
import com.bdb.lottery.extension.statusbar
import com.bdb.lottery.widget.LoadingLayout
import java.lang.ref.WeakReference
import javax.inject.Inject
import kotlin.properties.Delegates

abstract class BaseActivity(var layoutId: Int) : AppCompatActivity() {
    //vars
    protected var statusbarLight = true;//状态栏是否半透明
    private var live_ = false;
    protected val isAlive
        //act是不是活的
        get() = live_ && !isFinishing

    //uis
    @Inject
    lateinit var loading: LoadingDialog
    protected val loadingLayout: LoadingLayout
        get() = findViewById(R.id.id_common_loadinglayout)
    protected val content: ViewGroup
        get() = findViewById(R.id.id_common_content_layout)
    val statusBar: ViewGroup
        get() = findViewById(R.id.id_common_statusbar_layout)
    protected var mActivity: WeakReference<AppCompatActivity>? = null//当前activity引用

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        //初始化变量
        initVar(savedInstanceState)

        //解析布局文件
        val content: FrameLayout = window.decorView.findViewById(android.R.id.content)
        attachView(content)
        statusbar(statusbarLight)
        loadingLayoutWrap()
    }

    private fun loadingLayoutWrap() {
        content?.let {
            LoadingLayout.wrap(content)
        }
    }

    fun attachView(root: ViewGroup) {
        root.removeAllViews()
        layoutInflater.inflate(layoutId, root, true)
    }

    fun initVar(bundle: Bundle?) {
        live_ = true
        mActivity = WeakReference(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        live_ = false

        mActivity?.clear()
        mActivity = null
    }

    fun show() {
        loading.show(supportFragmentManager, ITag.COMMON_LOADING)
    }

    fun hide() {
        if (loading.isAdded && !loading.isDetached)
            loading.dismissAllowingStateLoss()
    }

    fun error() {
        loadingLayout.showError()
    }

    fun empty() {
        loadingLayout.showEmpty()
    }

    protected fun <D, T : ViewState<D>> observeWithLoading(
        data: LiveData<T>?,
        block: (D?) -> Any
    ) {
        data?.observe(this, Observer {
            load(it.isLoading)
            block(it.data)
        })
    }

    protected fun <D> observe(
        data: LiveData<D>?,
        block: (D?) -> Any
    ) {
        data?.observe(this, Observer {
            block(it)
        })
    }
}