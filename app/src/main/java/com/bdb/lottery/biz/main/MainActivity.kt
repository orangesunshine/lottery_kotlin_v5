package com.bdb.lottery.biz.main

import android.os.Bundle
import android.os.SystemClock
import android.view.KeyEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.bdb.lottery.R
import com.bdb.lottery.biz.main.find.FindFragment
import com.bdb.lottery.biz.main.home.HomeFragment
import com.bdb.lottery.biz.main.promotion.PromotionFragment
import com.bdb.lottery.biz.main.user.UserFragment
import com.bdb.lottery.const.CONST
import com.bdb.lottery.extension.statusbar
import com.bdb.lottery.extension.validIndex
import com.bdb.lottery.utils.ui.app.Apps
import com.bdb.lottery.utils.ui.toast.AbsToast
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.main_activity.*
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    private val HOME_FRAGMENT_TAG: String = "HOME_FRAGMENT_TAG"
    private val PROMOTION_FRAGMENT_TAG: String = "PROMOTION_FRAGMENT_TAG"
    private val FIND_FRAGMENT_TAG: String = "FIND_FRAGMENT_TAG"
    private val USER_FRAGMENT_TAG: String = "USER_FRAGMENT_TAG"

    private var mIndex = -1
    private lateinit var fragments: Array<Fragment>
    private lateinit var rbs: Array<RadioButton>
    private val tags: Array<String> = arrayOf(
        HOME_FRAGMENT_TAG,
        PROMOTION_FRAGMENT_TAG,
        FIND_FRAGMENT_TAG,
        USER_FRAGMENT_TAG
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val content = window.decorView.findViewById<FrameLayout>(android.R.id.content)
        content.removeAllViews()
        layoutInflater.inflate(R.layout.main_activity, content, true)
        (main_content_fl.layoutParams as ViewGroup.MarginLayoutParams).topMargin =
            CONST.HEIGHT_STATUS_BAR
        statusbar(true)
        fragments = if (null == savedInstanceState) {
            arrayOf(HomeFragment(), PromotionFragment(), FindFragment(), UserFragment())
        } else {
            arrayOf(
                supportFragmentManager.findFragmentByTag(tags[0]) ?: HomeFragment(),
                supportFragmentManager.findFragmentByTag(tags[1]) ?: PromotionFragment(),
                supportFragmentManager.findFragmentByTag(tags[2]) ?: FindFragment(),
                supportFragmentManager.findFragmentByTag(tags[3]) ?: UserFragment()
            )
        }
        rbs = arrayOf(mainHomeRb, mainPromotionRb, mainFindRb, mainUserRb)
        selectNavTab(0)

        mainHomeRb.setOnClickListener { selectNavTab(0) }
        mainPromotionRb.setOnClickListener { selectNavTab(1) }
        mainFindRb.setOnClickListener { selectNavTab(2) }
        mainUserRb.setOnClickListener { selectNavTab(3) }
    }

    //底部导航（彩票大厅、优惠、发现、我的）
    private fun selectNavTab(index: Int) {
        if (mIndex == index || !fragments.validIndex(index)) return
        //处理fragment
        supportFragmentManager.beginTransaction().apply {
            if (mIndex >= 0) {
                val pre = fragments[mIndex]
                if (pre.isAdded) hide(pre)
            }
            val cur = fragments[index]
            val tag = tags[index]
            if (!cur.isAdded) add(R.id.main_content_fl, cur, tag) else show(cur)
        }.commitAllowingStateLoss()

        //处理nav tab
        if (mIndex >= 0)
            rbs[mIndex].isChecked = false
        rbs[index].isChecked = true
        mIndex = index
    }

    //region 2秒内连续2次，退出应用
    @Inject
    lateinit var toast: AbsToast
    private var mLastBackTime: Long = 0
    private val mTerminateInternal = 2000L
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val elapsedRealtime = SystemClock.elapsedRealtime()
        return if (elapsedRealtime - mLastBackTime > mTerminateInternal) {
            mLastBackTime = elapsedRealtime
            toast.showWarning("再次按下退出")
            true
        } else {
            Apps.killApp()
            super.onKeyDown(keyCode, event)
        }
    }
    //endregion
}