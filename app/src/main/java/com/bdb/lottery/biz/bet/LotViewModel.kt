package com.bdb.lottery.biz.bet

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import com.bdb.lottery.biz.base.BaseViewModel
import com.bdb.lottery.datasource.account.AccountRemoteDs
import com.bdb.lottery.datasource.app.AppRemoteDs
import com.bdb.lottery.utils.cache.TCache
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class LotViewModel @ViewModelInject @Inject constructor(
    @ActivityContext val context: Context,
    val tCache: TCache,
    private val accountRemoteDs: AccountRemoteDs,
    private val appRemoteDs: AppRemoteDs
) : BaseViewModel() {


}