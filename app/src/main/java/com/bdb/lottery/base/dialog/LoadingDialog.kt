package com.bdb.lottery.base.dialog

import android.os.Bundle
import android.view.View
import com.bdb.lottery.R
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.android.synthetic.main.loading_common_dialog.*
import javax.inject.Inject

@ActivityScoped
class LoadingDialog @Inject constructor() : BaseDialog(R.layout.loading_common_dialog) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mDimAmount = 0f
        loadingLav.playAnimation()
    }

}