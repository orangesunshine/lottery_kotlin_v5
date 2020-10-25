package com.bdb.lottery.base.ui

import android.os.Bundle
import android.view.KeyEvent
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

abstract class BaseActivity(var layoutId: Int, var wrapStatusbar: Boolean = true) :
    AppCompatActivity() {
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
    protected val content: ViewGroup?
        get() = findViewById(R.id.id_common_content_layout)
    val statusBar: ViewGroup?
        get() = findViewById(R.id.id_common_statusbar_layout)
    protected var mActivity: WeakReference<AppCompatActivity>? = null//当前activity引用

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        //初始化变量
        initVar()
        //解析布局文件
        val content: FrameLayout = window.decorView.findViewById(android.R.id.content)
        attachView(content)
        statusbar(statusbarLight)
        loadingLayoutWrap()
        observe()
    }

    /**
     * observe livedata
     */
    protected open fun observe(){}

    private fun loadingLayoutWrap() {
        content?.let {
            LoadingLayout.wrap(content)
        }
    }

    fun attachView(root: ViewGroup) {
        root.removeAllViews()
        if (wrapStatusbar) {
            //statusbar 嵌套
            val wrapL: ViewGroup =
                layoutInflater.inflate(
                    R.layout.statusbar_wrap_common_layout,
                    root,
                    false
                ) as ViewGroup

            root.addView(wrapL)
            //添加布局文件
            layoutInflater.inflate(layoutId, wrapL, true)
        } else {

            //添加布局文件
            layoutInflater.inflate(layoutId, root, true)
        }
    }

    open fun initVar() {
        live_ = true
        mActivity = WeakReference(this)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            onBack()
            return false
        }else{
            return super.onKeyDown(keyCode, event)
        }
    }

    protected open fun onBack() {
        finish()
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
        data: LiveData<D>,
        block: (D?) -> Any
    ) {
        data.observe(this, Observer {
            block(it)
        })
    }
}