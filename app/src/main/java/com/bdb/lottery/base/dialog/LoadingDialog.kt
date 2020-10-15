package com.bdb.lottery.base.dialog

import com.bdb.lottery.R
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class LoadingDialog @Inject constructor() : BaseDialog(R.layout.dialog_common_loading) {
}