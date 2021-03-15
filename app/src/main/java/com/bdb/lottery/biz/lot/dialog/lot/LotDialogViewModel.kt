package com.bdb.lottery.biz.lot.dialog.lot

import androidx.hilt.lifecycle.ViewModelInject
import com.bdb.lottery.biz.base.BaseViewModel
import com.bdb.lottery.datasource.lot.LotRemoteDs
import com.bdb.lottery.datasource.lot.data.LotData
import com.bdb.lottery.datasource.lot.data.LotParam
import io.reactivex.rxjava3.disposables.Disposable
import javax.inject.Inject

class LotDialogViewModel @ViewModelInject @Inject constructor(
    private val lotRemoteDs: LotRemoteDs,
) : BaseViewModel() {

    fun lot(
        lotParam: LotParam, success: (LotData) -> Unit,
        error: (token: String) -> Unit,
        onStart: ((Disposable) -> Unit)? = null,
        complete: (() -> Unit)? = null,
    ) {
        lotRemoteDs.lot(lotParam, success, error, onStart, complete)
    }
}