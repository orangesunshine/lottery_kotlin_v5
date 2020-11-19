package com.bdb.lottery.base.ui

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.LinearLayout.VERTICAL
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.bdb.lottery.R
import com.bdb.lottery.base.dialog.LoadingDialog
import com.bdb.lottery.biz.base.BaseViewModel
import com.bdb.lottery.const.ITag
import com.bdb.lottery.extension.loading
import com.bdb.lottery.extension.ob
import com.bdb.lottery.extension.statusbar
import com.bdb.lottery.widget.LoadingLayout
import java.lang.ref.WeakReference
import javax.inject.Inject

open class BaseActivity(
    var layoutId: Int
) : FragmentActivity() {

    //vars
    protected var statusbarLight = true;//状态栏是否半透明

    //uis
    @Inject
    lateinit var loading: LoadingDialog
    protected val loadingLayout: LoadingLayout?
        get() = findViewById(R.id.id_common_loadinglayout)
    protected val content: ViewGroup?
        get() = findViewById(R.id.id_common_content_layout)
    val statusBar: View?
        get() = findViewById(R.id.id_common_statusbar_layout)

    //actionbar
    val actbarLeft: ViewGroup?
        get() = findViewById(R.id.id_common_actionbar_left)
    val actbarRight: ViewGroup?
        get() = findViewById(R.id.id_common_actionbar_right)
    val actbarCenter: View?
        get() = findViewById(R.id.id_common_actionbar_center)
    protected var mActivity: WeakReference<FragmentActivity>? = null//当前activity引用

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //初始化变量
        initVar()
        //解析布局文件
        val content: FrameLayout = window.decorView.findViewById(android.R.id.content)
        attachView(content)
        statusbar(statusbarLight)
        actbar()
        loadingLayoutWrap()
        obLoadingLayout()
        observe()
    }

    private fun actbar() {
        actbarLeft?.let { actbarLeft(it) }
        actbarCenter?.let { actbarCenter(it) }
        actbarRight?.let { actbarRight(it) }
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
            var parent: ViewGroup?
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
        loading.show(supportFragmentManager, ITag.COMMON_LOADING_TAG)
    }

    fun hide() {
        if (loading.isAdded && !loading.isDetached)
            loading.dismissAllowingStateLoss()
    }

    private fun obLoadingLayout() {
        ob(getVm()?.viewStatus?.getLiveData()) {
            it?.let {
                loading(it.isLoading)
                if (it.isError) {
                    loadingLayout?.showError()
                } else if (it.isEmpty) {
                    loadingLayout?.showEmpty()
                } else {
                    loadingLayout?.showContent()
                }
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // 子类覆盖方法
    ///////////////////////////////////////////////////////////////////////////
    /**
     * observe livedata
     */
    protected open fun observe() {
    }

    //需要loading时候
    protected open fun getVm(): BaseViewModel? {
        return null
    }

    //空、网络错误 覆盖根布局
    protected open fun emptyErrorRoot(): ViewGroup? {
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

    //获取title(actionbar)
    protected open fun actbarTitle(): String {
        return ""
    }

    protected open fun actbarCenter(center: View) {
        if (center is TextView)
            center.text = actbarTitle()
    }

    protected open fun actbarLeft(left: View) {
        left.setOnClickListener { onBack() }
    }

    protected open fun actbarRight(right: View) {

    }
}