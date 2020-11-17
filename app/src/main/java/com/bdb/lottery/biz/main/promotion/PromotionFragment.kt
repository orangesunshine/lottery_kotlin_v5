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
    lateinit var toast1: ActivityToast


    @Inject
    lateinit var toast2: WindowManagerToast


    @Inject
    lateinit var toast3: SystemToast

    private var count = 0;

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        promotion_marquee_tv.setOnClickListener {
            (++count).let { when(it){
                1-> toast1.also { it.setToastView("toast 1") }.show(15000)
                2-> toast2.also { it.setToastView("toast 2") }.show(15000)
                3-> toast3.also { it.setToastView("toast 3") }.show(15000)
            } }
        }
    }
}