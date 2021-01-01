package com.bdb.lottery.biz.base

import androidx.lifecycle.ViewModel
import com.bdb.lottery.base.response.ViewState
import com.bdb.lottery.datasource.common.LiveDataWrapper

open class BaseViewModel : ViewModel() {
    val viewStatus = LiveDataWrapper<ViewState?>()
}