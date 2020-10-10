package com.bdb.lottery.base.contract

import android.os.Bundle
import android.view.ViewGroup
import androidx.annotation.LayoutRes

/**
 * mvp
 */
interface IContract {
    //视图
    interface IView{
        fun initVar(bundle: Bundle?);//初始化

        fun attachView(root:ViewGroup)//控件

        @LayoutRes fun layoutId(): Int;//布局文件
    }

    interface IPresenter<in V>{
        fun attachView(view:V);
    }
}