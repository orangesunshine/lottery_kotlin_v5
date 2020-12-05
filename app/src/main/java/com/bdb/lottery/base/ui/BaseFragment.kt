package com.bdb.lottery.base.ui

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bdb.lottery.R
import com.bdb.lottery.dialog.LoadingDialog
import com.bdb.lottery.biz.base.BaseViewModel
import com.bdb.lottery.const.TAG
import com.bdb.lottery.extension.loading
import com.bdb.lottery.extension.ob
import com.bdb.lottery.utils.ui.toast.AbsToast
import com.bdb.lottery.widget.LoadingLayout
import java.lang.ref.WeakReference
import javax.inject.Inject

open class BaseFragment(
    var layoutId: Int
) : Fragment() {
    //uis
    @Inject
    lateinit var loading: LoadingDialog
    @Inject
    lateinit var toast: AbsToast
    protected var rootView: View? = null
    protected val loadingLayout: LoadingLayout?
        get() = rootView?.findViewById(R.id.loadinglayout_id)
    protected var mActivity: WeakReference<Activity>? = null//当前activity引用

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        mActivity = WeakReference(activity)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = WeakReference(context as Activity)
    }

    override fun onDetach() {
        super.onDetach()
        mActivity?.clear()
        mActivity = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = super.onCreateView(inflater, container, savedInstanceState)
        if (null == rootView) rootView = inflater.inflate(layoutId, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingLayoutWrap()
        obLoadingLayout()
        observe()
    }

    //emptyErrorRoot()/content 添加空布局、网络错误
    private fun loadingLayoutWrap() {
        emptyErrorRoot()?.let {
            LoadingLayout.wrap(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mActivity?.clear()
        mActivity = null
    }

    fun show() {
        loading.show(childFragmentManager, TAG.COMMON_LOADING_TAG)
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
}