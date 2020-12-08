package com.bdb.lottery.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.bdb.lottery.R
import com.bdb.lottery.base.dialog.BaseDialog
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.android.synthetic.main.loading_common_dialog.*
import javax.inject.Inject

/**
 *
 */
@ActivityScoped
class LoadingDialog @Inject constructor() : BaseDialog(R.layout.loading_common_dialog) {
    private val LOADING_DIALOG_TAG: String = "loading_dialog_tag"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mDimAmount = 0f
        loadingLav.playAnimation()
    }

    fun show(manager: FragmentManager) {
        super.show(manager, LOADING_DIALOG_TAG)
    }
}