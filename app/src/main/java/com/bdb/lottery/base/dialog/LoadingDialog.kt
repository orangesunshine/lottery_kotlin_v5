package com.bdb.lottery.base.dialog

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.bdb.lottery.R
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class LoadingDialog @Inject constructor() : BaseDialog(R.layout.dialog_common_loading),
    LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun hideOnPause() {
        dismissAllowingStateLoss()
    }
}