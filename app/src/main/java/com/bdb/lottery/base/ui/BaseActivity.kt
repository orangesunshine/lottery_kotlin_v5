package com.bdb.lottery.base.ui

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.LinearLayout.VERTICAL
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.fragment.app.FragmentActivity
import com.bdb.lottery.R
import com.bdb.lottery.dialog.LoadingDialog
import com.bdb.lottery.biz.base.BaseViewModel
import com.bdb.lottery.const.TAG
import com.bdb.lottery.extension.loading
import com.bdb.lottery.extension.ob
import com.bdb.lottery.extension.statusbar
import com.bdb.lottery.utils.ui.toast.AbsToast
import com.bdb.lottery.widget.LoadingLayout
import timber.log.Timber
import java.lang.ref.WeakReference
import javax.inject.Inject

open class BaseActivity(
    var layoutId: Int
) : FragmentActivity() {

    //vars
    protected open var statusbarLight = true;//状态栏是否半透明

    //uis
    @Inject
    lateinit var loading: LoadingDialog
    @Inject
    lateinit var toast: AbsToast
    private val loadingLayout: LoadingLayout?
        get() = findViewById(R.id.loadinglayout_id)
    protected val content: ViewGroup?
        get() = findViewById(R.id.content_layout_id)
    val statusBar: View?
        get() = findViewById(R.id.statusbar_layout_id)

    //actionbar
    private val actbarLeft: View?
        get() = findViewById(R.id.actionbar_left_id)
    private val actbarRight: View?
        get() = findViewById(R.id.actionbar_right_id)
    private val actbarCenter: View?
        get() = findViewById(R.id.actionbar_center_id)
    protected var mActivity: WeakReference<FragmentActivity>? = null//当前activity引用

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //初始化变量
        initVar(savedInstanceState)
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

    private fun attachView(root: ViewGroup) {
        root.removeAllViews()
        if (attachStatusBar() || attachActionBar()) {
            val useContentLayoutId = attachRoot() == layoutId
            if (!useContentLayoutId) {
                layoutInflater.inflate(layoutId, root, true)
            }
            var parent: ViewGroup?
            var layout: ViewGroup = layoutInflater.inflate(
                if (useContentLayoutId) layoutId else attachRoot(),
                root,
                false
            ) as ViewGroup
            val verticalLinearLayout = layout is LinearLayout && layout.orientation == VERTICAL
            if (verticalLinearLayout) {
                parent = layout
            } else {
                parent = LinearLayout(this)
                parent.orientation = VERTICAL
                parent.addView(layout)
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
                }
            }

        } else {
            layoutInflater.inflate(layoutId, root, true)
        }
    }

    @CallSuper
    open fun initVar(bundle:Bundle?) {
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
        toast.cancel()
        mActivity?.clear()
        mActivity = null
    }

    fun show() {
        loading.show(supportFragmentManager)
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
    protected open fun attachRoot(): Int {
        return layoutId
    }

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