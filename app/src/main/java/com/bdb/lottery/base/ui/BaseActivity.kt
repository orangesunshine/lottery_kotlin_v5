package com.bdb.lottery.base.ui

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.LinearLayout.VERTICAL
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.bdb.lottery.R
import com.bdb.lottery.base.dialog.LoadingDialog
import com.bdb.lottery.base.response.ViewState
import com.bdb.lottery.const.ITag
import com.bdb.lottery.extension.loading
import com.bdb.lottery.extension.statusbar
import com.bdb.lottery.widget.LoadingLayout
import java.lang.ref.WeakReference
import javax.inject.Inject

open class BaseActivity(
    var layoutId: Int
) :
    AppCompatActivity() {
    //vars
    protected var statusbarLight = true;//状态栏是否半透明

    //uis
    @Inject
    lateinit var loading: LoadingDialog
    protected val loadingLayout: LoadingLayout
        get() = findViewById(R.id.id_common_loadinglayout)
    protected val content: ViewGroup?
        get() = findViewById(R.id.id_common_content_layout)
    val statusBar: View?
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

    //emptyErrorRoot()/content 添加空布局、网络错误
    private fun loadingLayoutWrap() {
        emptyErrorRoot()?.let {
            LoadingLayout.wrap(it)
        }
    }

    fun attachView(root: ViewGroup) {
        root.removeAllViews()
        if (attachStatusBar() || attachStatusBar()) {
            var parent: ViewGroup? = null
            var layout: ViewGroup = layoutInflater.inflate(layoutId, root, false) as ViewGroup
            val verticalLinearLayout = layout is LinearLayout && layout.orientation == VERTICAL
            if (verticalLinearLayout) {
                parent = layout
            } else {
                parent = LinearLayout(this)
                parent.orientation = VERTICAL
            }
            root.addView(
                parent,
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )

            parent.run {
                if (attachStatusBar()) {
                    //statusbar 嵌套
                    addView(
                        layoutInflater.inflate(
                            R.layout.statusbar_common_layout,
                            parent,
                            false
                        ),
                        0
                    )

                    //actionbar 嵌套
                    if (attachActionBar())
                        addView(
                            layoutInflater.inflate(getActionbarLayout(), parent, false),
                            if (attachStatusBar()) 1 else 0
                        )

                    if (!verticalLinearLayout) addView(layout)
                }
            }

        } else {
            layoutInflater.inflate(layoutId, root, true)
        }
    }

    open fun initVar() {
        mActivity = WeakReference(this)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBack()
            return false
        } else {
            return super.onKeyDown(keyCode, event)
        }
    }

    protected open fun onBack() {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
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
            loading(it.isLoading)
            block(it.data)
        })
    }

    protected fun <D> observe(
        data: LiveData<D>,
        block: (D?) -> Any?
    ) {
        data.observe(this, Observer {
            block(it)
        })
    }

    ///////////////////////////////////////////////////////////////////////////
    // 子类覆盖方法
    ///////////////////////////////////////////////////////////////////////////
    /**
     * observe livedata
     */
    protected open fun observe() {
    }

    //空、网络错误 覆盖根布局
    protected fun emptyErrorRoot(): ViewGroup? {
        return null
    }

    //是否注入actionbar
    protected open fun attachActionBar(): Boolean {
        return true
    }

    //actionbar样式
    protected open fun getActionbarLayout(): Int {
        return R.layout.actionbar_common_layout
    }

    //是否注入statusbar（顶部margin）
    protected open fun attachStatusBar(): Boolean {
        return true
    }
}