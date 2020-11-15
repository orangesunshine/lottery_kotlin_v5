package com.bdb.lottery.biz.main

import android.os.Bundle
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
import com.bdb.lottery.const.IConst
import com.bdb.lottery.const.ITag
import com.bdb.lottery.extension.statusbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.main_activity.*

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    val HOME_FRAGMENT_TAG: String = "HOME_FRAGMENT_TAG"
    val PROMOTION_FRAGMENT_TAG: String = "PROMOTION_FRAGMENT_TAG"
    val FIND_FRAGMENT_TAG: String = "FIND_FRAGMENT_TAG"
    val USER_FRAGMENT_TAG: String = "USER_FRAGMENT_TAG"

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
            IConst.HEIGHT_STATUS_BAR
        statusbar(true)
        fragments = if (null == savedInstanceState) {
            arrayOf(HomeFragment(), PromotionFragment(), FindFragment(), UserFragment())
        } else {
            val home = supportFragmentManager.findFragmentByTag(tags[0])
            val promotion = supportFragmentManager.findFragmentByTag(tags[1])
            val find = supportFragmentManager.findFragmentByTag(tags[2])
            val user = supportFragmentManager.findFragmentByTag(tags[3])
            arrayOf(
                if (null == home) HomeFragment() else home,
                if (null == promotion) PromotionFragment() else promotion,
                if (null == find) FindFragment() else find,
                if (null == user) UserFragment() else user
            )
        }
        rbs = arrayOf(main_home_rb, main_promotion_rb, main_find_rb, main_user_rb)
        selectNavTab(0)

        main_home_rb.setOnClickListener { selectNavTab(0) }
        main_promotion_rb.setOnClickListener { selectNavTab(1) }
        main_find_rb.setOnClickListener { selectNavTab(2) }
        main_user_rb.setOnClickListener { selectNavTab(3) }
    }

    //底部导航（彩票大厅、优惠、发现、我的）
    fun selectNavTab(index: Int) {
        if (mIndex == index || index < 0 || index >= fragments.size) return
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
}