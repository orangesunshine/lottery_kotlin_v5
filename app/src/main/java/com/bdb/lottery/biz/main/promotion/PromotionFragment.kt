package com.bdb.lottery.biz.main.promotion

import android.os.Bundle
import android.view.View
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseFragment
import com.bdb.lottery.utils.ui.toast.ActivityToast
import com.bdb.lottery.utils.ui.toast.SystemToast
import com.bdb.lottery.utils.ui.toast.WindowManagerToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.main_promotion_fragment.*
import javax.inject.Inject
import kotlin.random.Random

//优惠
@AndroidEntryPoint
class PromotionFragment : BaseFragment(R.layout.main_promotion_fragment) {
    @Inject
    lateinit var toast1: SystemToast

    @Inject
    lateinit var toast2: ActivityToast

    @Inject
    lateinit var toast3: WindowManagerToast

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn1.setOnClickListener {
            toast1.showError("haha")
        }
        btn2.setOnClickListener {
            toast2.showSuccess("haha\nhah")
        }
        btn3.setOnClickListener {
            toast3.showWarning("haha\nhah\nhah")
        }
    }
}