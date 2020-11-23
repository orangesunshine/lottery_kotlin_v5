package com.bdb.lottery.biz.lot

import androidx.activity.viewModels
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseActivity
import com.bdb.lottery.utils.TTime
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LotActivity : BaseActivity(R.layout.lot_activity) {
    private val vm by viewModels<LotViewModel>()

    @Inject
    private lateinit var tTime: TTime

    override fun observe() {
        vm.mCountDown.getLiveData().observe(this, {
            it?.let {
                val isclose = it.isclose
                val surplusTime = tTime.surplusTime2String(
                    false,
                    if (isclose) it.openSurplusTime else it.betSurplusTime
                )
                val split = surplusTime.split(":")
                if (split.size > 2) {

                } else {
                    val first = split[0]
                    first.toCharArray().let {
                        if(it.size>1){
                            
                        }

                        if(it.size>0){

                        }
                    }

                }
            }
        })
    }

    override fun attachActionBar(): Boolean {
        return false
    }

    override fun attachStatusBar(): Boolean {
        return false
    }
}