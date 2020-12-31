package com.bdb.lottery.biz.lot.dialog

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import com.bdb.lottery.biz.base.BaseViewModel
import com.bdb.lottery.datasource.cocos.CocosRemoteDs
import com.bdb.lottery.datasource.lot.LotRemoteDs
import com.bdb.lottery.utils.thread.TThread
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class LotDialogViewModel @ViewModelInject @Inject constructor(
    @ActivityContext val context: Context,
    private val tThread: TThread,
    private val lotRemoteDs: LotRemoteDs,
    private val cocosRemoteDs: CocosRemoteDs,
) : BaseViewModel() {

}