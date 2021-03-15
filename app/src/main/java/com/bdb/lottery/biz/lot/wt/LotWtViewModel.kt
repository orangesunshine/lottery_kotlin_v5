package com.bdb.lottery.biz.lot.wt

import androidx.hilt.lifecycle.ViewModelInject
import com.bdb.lottery.biz.base.BaseViewModel
import com.bdb.lottery.datasource.app.AppRemoteDs
import com.bdb.lottery.datasource.game.GameRemoteDs
import javax.inject.Inject


class LotWtViewModel @ViewModelInject @Inject constructor(
    private val gameRemoteDs: GameRemoteDs,
    private val appRemoteDs: AppRemoteDs
) : BaseViewModel() {
}