package com.bdb.lottery.biz.lot.dialog.lot

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import com.bdb.lottery.biz.base.BaseViewModel
import com.bdb.lottery.datasource.cocos.CocosRemoteDs
import com.bdb.lottery.datasource.lot.LotRemoteDs
import com.bdb.lottery.datasource.lot.data.LotData
import com.bdb.lottery.datasource.lot.data.LotParam
import com.bdb.lottery.utils.thread.TThread
import dagger.hilt.android.qualifiers.ActivityContext
import io.reactivex.rxjava3.disposables.Disposable
import javax.inject.Inject

class LotDialogViewModel @ViewModelInject @Inject constructor(
    @ActivityContext val context: Context,
    private val lotRemoteDs: LotRemoteDs,
) : BaseViewModel() {

    fun lot(
        lotParam: LotParam, success: (LotData?) -> Unit,
        error: (token: String) -> Unit,
        onStart: ((Disposable) -> Unit)? = null,
        complete: (() -> Unit)? = null,
    ) {
        lotRemoteDs.lot(lotParam, success, error, onStart, complete)
    }
}