package com.bdb.lottery.biz.lot

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.bdb.lottery.R
import com.bdb.lottery.biz.lot.jd.LotJdFragment
import com.bdb.lottery.biz.lot.tr.LotTrFragment
import com.bdb.lottery.biz.lot.wt.LotWtFragment
import com.bdb.lottery.datasource.lot.data.countdown.CountDownData
import com.bdb.lottery.extension.isSpace
import com.bdb.lottery.extension.visible
import com.bdb.lottery.utils.game.Games
import com.bdb.lottery.utils.game.TGame
import com.bdb.lottery.utils.time.TTime
import com.bdb.lottery.utils.time.Times
import com.bdb.lottery.utils.ui.keyboard.KeyBoards
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.android.synthetic.main.lot_activity.*
import javax.inject.Inject

@ActivityScoped
class TLot @Inject constructor(
    @ActivityContext private val context: Context,
    private val tGame: TGame,
    private val tTime: TTime
) {
    private val JD_FRAGMENT_TAG: String = "JD_FRAGMENT_TAG"
    private val TR_FRAGMENT_TAG: String = "TR_FRAGMENT_TAG"
    private val WT_FRAGMENT_TAG: String = "WT_FRAGMENT_TAG"

    private val tags: Array<String> = arrayOf(
        JD_FRAGMENT_TAG,
        TR_FRAGMENT_TAG,
        WT_FRAGMENT_TAG
    )

    //初始化fragment
    fun initFragment(
        gameType: Int,
        gameId: Int,
        gameName: String?,
        bundle: Bundle?
    ): Array<Fragment> {
        val saveStates = null != bundle
        val fm = (context as FragmentActivity).supportFragmentManager
        val jd = fm.findFragmentByTag(tags[0])
        val tr = fm.findFragmentByTag(tags[1])
        val wt = fm.findFragmentByTag(tags[2])
        return arrayOf(
            if (saveStates && null != jd) jd else LotJdFragment.newInstance(
                gameType,
                gameId,
                gameName
            ),
            if (saveStates && null != tr) tr else LotTrFragment.newInstance(
                gameType,
                gameId,
                gameName
            ),
            if (saveStates && null != wt) wt else LotWtFragment.newInstance(
                gameType,
                gameId,
                gameName
            )
        )
    }

    //region 切换fragment
    private var mFragmentIndex = -1// 0经典 1传统 2微投
    fun switchFragment(
        index: Int,
        fragments: Array<Fragment>,
        afterSwitch: ((Int) -> Unit)? = null
    ) {
        if (mFragmentIndex == index) return
        //传统显示label
        (context as FragmentActivity).supportFragmentManager.beginTransaction().let {
            //上一个页面
            if (mFragmentIndex < fragments.size && mFragmentIndex >= 0) {
                val preFragment = fragments[mFragmentIndex]
                if (preFragment.isAdded) {
                    it.hide(preFragment)
                }
            }

            //当前的页面
            if (index < fragments.size && index >= 0) {
                val fragment = fragments[index]
                val tag = tags[index]
                if (fragment.isAdded) {
                    it.show(fragment)
                } else {
                    it.add(R.id.lot_rect_content_fl, fragment, tag)
                }
            }
            it.commitAllowingStateLoss()
        }
        afterSwitch?.invoke(index)
        mFragmentIndex = index
    }
    //endregion

    //region 经典：单式输入法适配
    fun adjustSoftInput(listener: KeyBoards.OnSoftInputChangedListener) {
        val window = (context as Activity).window
        KeyBoards.fixAndroidBug5497(window)
        KeyBoards.registerSoftInputChangedListener(window, listener)
    }
    //endregion

    //region 开奖号码列表
    fun openNumsAdapter(gameType: Int, nums: String): TagAdapter<String> {
        val split = nums.split(" ")
        val brightIndexs = tGame.brightIndexs("1", gameType)
        return object : TagAdapter<String>(split) {
            override fun getView(parent: FlowLayout?, position: Int, num: String?): View {
                val textView = TextView(context)
                textView.gravity = Gravity.CENTER
                textView.text = num
                textView.setBackgroundResource(R.drawable.lot_open_nums_white_circle_shape)
                textView.alpha =
                    if (brightIndexs.contains(position + 1)) 1f else 0.6f
                return textView
            }
        }
    }
    //endregion

    //region 倒计时返回：状态、时分秒
    fun countDownData(
        currentTime: CountDownData.CurrentTime,
        lotJdFragment: LotJdFragment?
    ): Array<String> {
        val isClosed = currentTime.isclose
        val showHour = currentTime.betTotalTime / 1000 / 60 / 60 > 0//是否显示小时
        val surplusTime = tTime.surplusTime2String(
            showHour,
            if (isClosed) currentTime.openSurplusTime else currentTime.betSurplusTime
        )
        val split = surplusTime.split(":")
        val showHourReal = showHour || (split.size > 2)
        val hour = if (split.size > 2) split[0] else "00"
        val minute = if (split.size > 2) split[1] else if (split.size > 1) split[0] else "00"
        val second =
            if (split.size > 2) split[2] else if (split.size > 1) split[1] else if (split.isNotEmpty()) split[0] else "00"

        //更新经典状态
        lotJdFragment?.updateStatus(isClosed)
        return if (showHourReal) {
            arrayOf(hour, minute, second)
        } else {
            arrayOf(minute, second)
        }
    }
    //endregion

    //jd切换单复式
    fun updateJdParams(lotJdFragment: LotJdFragment?, isSingleStyle: Boolean) {
        //更新经典状态
        lotJdFragment?.switchDanFuStyle(isSingleStyle)
    }

    //获取jdFragment
    fun getJdFragment(fragments: Array<Fragment>, sign: String): LotJdFragment? {
        if (!sign.contains("1")) return null;
        return if (!fragments.isNullOrEmpty()) fragments[0] as LotJdFragment else null
    }
}