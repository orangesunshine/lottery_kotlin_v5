package com.bdb.lottery.biz.lot.activity

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.os.IBinder
import androidx.hilt.lifecycle.ViewModelInject
import com.bdb.lottery.R
import com.bdb.lottery.biz.base.BaseViewModel
import com.bdb.lottery.biz.lot.BallAdapter
import com.bdb.lottery.biz.lot.jd.LotJdFragment
import com.bdb.lottery.biz.lot.tr.LotTrFragment
import com.bdb.lottery.biz.lot.wt.LotWtFragment
import com.bdb.lottery.const.EXTRA
import com.bdb.lottery.const.GAME
import com.bdb.lottery.datasource.cocos.CocosRemoteDs
import com.bdb.lottery.datasource.cocos.TCocos
import com.bdb.lottery.datasource.common.LiveDataWrapper
import com.bdb.lottery.datasource.lot.LotRemoteDs
import com.bdb.lottery.datasource.lot.data.HistoryData
import com.bdb.lottery.datasource.lot.data.countdown.CountDownData
import com.bdb.lottery.datasource.lot.data.jd.BetItem
import com.bdb.lottery.datasource.lot.data.jd.GameBetTypeData
import com.bdb.lottery.event.TEventManager
import com.bdb.lottery.extension.isSpace
import com.bdb.lottery.service.CountDownCallback
import com.bdb.lottery.service.CountDownService
import com.bdb.lottery.service.CountDownService.CountDownSub
import com.bdb.lottery.utils.game.Games
import com.bdb.lottery.utils.game.TGame
import com.bdb.lottery.utils.thread.TThread
import timber.log.Timber
import javax.inject.Inject

class LotViewModel @ViewModelInject @Inject constructor(
    private val tThread: TThread,
    private val tGame: TGame,
    private val tCocos: TCocos,
    private val lotRemoteDs: LotRemoteDs,
    private val cocosRemoteDs: CocosRemoteDs,
) : BaseViewModel() {

    //region 初始化参数：倍数、彩种id、彩种大类，彩种名称，玩法id
    private var mGameId: Int = -1
    private var mGameType: Int = -1
    private var mGameName: String? = null
    fun initExtraArgs(intent: Intent): Boolean {
        mGameType = intent.getIntExtra(EXTRA.TYPE_GAME_EXTRA, -1)
        mGameId = intent.getIntExtra(EXTRA.ID_GAME_EXTRA, -1)
        mGameName = intent.getStringExtra(EXTRA.NAME_GAME_EXTRA)
        return -1 != mGameId && -1 != mGameType
    }

    fun getGameId(): Int {
        return mGameId
    }
    //endregion

    //region 网络请求：开奖历史、倒计时、cocos动画

    //region 开奖历史记录
    val curIssue = LiveDataWrapper<HistoryData.HistoryItem?>()
    val historyIssue = LiveDataWrapper<List<HistoryData.HistoryItem>?>()
    fun getHistoryByGameId() {
        lotRemoteDs.getHistoryByGameId(mGameId.toString()) {
            Timber.d(it.toString())
            historyIssue.setData(it.filterNotNull())
            curIssue.setData(if (!it.isNullOrEmpty()) it[0] else null)
        }
    }
    //endregion

    //region 倒计时
    val countDown = LiveDataWrapper<CountDownData.CurrentTime?>()
    private val mCountDownCallback = object : CountDownCallback {
        override fun countDown(currentTime: CountDownData.CurrentTime) {
            tThread.runOnUiThread {
                countDown.setData(currentTime)
            }
        }
    }

    private var mSub: CountDownSub? = null
    private val conn = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (service is CountDownSub) {
                mSub = service
                service.registerCountDownCallback(mGameId, mCountDownCallback)
            }

        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mSub?.unregisterCountDownCallback(mGameId, mCountDownCallback)
            mSub = null
        }

    }

    fun bindService(context: Context) {
        CountDownService.start(context, mGameId)
        context.bindService(
            Intent(context, CountDownService::class.java),
            conn,
            Service.BIND_AUTO_CREATE
        )
    }

    fun unBindService(context: Context) {
        context.unbindService(conn)
        mSub?.unregisterCountDownCallback(mGameId, mCountDownCallback)
        mSub = null
    }
    //endregion

    //region 下载cocos动画文件
    fun downloadCocos(cocosName: String, success: ((String) -> Unit)? = null) {
        cocosRemoteDs.downloadSingleCocos(cocosName, success = success)
    }
    //endregion

    //region cocos名称：用于匹配下载
    fun cocosName(): String {
        return tCocos.cocosName(mGameType, mGameId)
    }
    //endregion
    //endregion

    //region 创建fragment

    //region 创建：经典fragment
    fun createJdFragment(betTypeId: Int): LotJdFragment {
        return LotJdFragment.newInstance(mGameType, mGameId, betTypeId, mGameName)
    }
    //endregion

    //region 创建：传统fragment
    fun createTrFragment(): LotTrFragment {
        return LotTrFragment.newInstance(mGameType, mGameId, mGameName)
    }
    //endregion

    //region 创建：微投fragment
    fun createWtFragment(): LotWtFragment {
        return LotWtFragment.newInstance(mGameType, mGameId, mGameName)
    }
    //endregion
    //endregion

    //region 适配器：开奖号码
    fun createOpenBallAdapter(context: Context, nums: String, playName: String?): OpenBallAdapter {
        return OpenBallAdapter(
            context, mGameType, mGameId,
            nums, lotPlace(playName)
        )
    }
    //endregion

    //region 适配器：往期开奖
    fun createBallAdapter(
        parentPlayId: Int,
        betTypeId: Int,
        playName: String?,
        data: MutableList<HistoryData.HistoryItem>?
    ): BallAdapter {
        return BallAdapter(mGameType, mGameId, parentPlayId, betTypeId, lotPlace(playName), data)
    }
    //endregion

    //region 换肤

    //region 换肤-标题栏背景
    fun titleSkinSkin(): Int {
        return when (mGameType) {
            GAME.TYPE_GAME_K3 -> R.drawable.lot_actionbar_bg_k3
            GAME.TYPE_GAME_PK10, GAME.TYPE_GAME_PK8 -> R.drawable.lot_actionbar_bg_pk
            else -> R.drawable.lot_actionbar_bg_default
        }
    }
    //endregion

    //region 换肤-倒计时栏背景
    fun countDownBannerSkin(): Int {
        return when (mGameType) {
            GAME.TYPE_GAME_K3 -> R.drawable.lot_top_rect_bg_k3
            GAME.TYPE_GAME_PK10, GAME.TYPE_GAME_PK8 -> R.drawable.lot_top_rect_bg_pk
            else -> R.drawable.lot_top_rect_bg_default
        }
    }
    //endregion

    //region 换肤-倒计时背景
    fun countDownSkin(isWt: Boolean): Int {
        return when (mGameType) {
            GAME.TYPE_GAME_K3 -> R.drawable.lot_countdown_shape_k3
            GAME.TYPE_GAME_PK10, GAME.TYPE_GAME_PK8 -> if (isWt) R.drawable.lot_countdown_shape_wt_pk else R.drawable.lot_countdown_shape_pk
            else -> R.drawable.lot_countdown_shape_default
        }
    }
    //endregion

    //region 换肤-倒计时栏文字颜色（历史期号、往期开奖、当前期号、受注状态）
    fun openHistoryTextColor(): Int {
        return Color.parseColor(
            when (mGameType) {
                GAME.TYPE_GAME_PK10, GAME.TYPE_GAME_PK8 -> "#E1CE99"
                GAME.TYPE_GAME_K3 -> "#82DFBC"
                else -> "#C8E2F9"
            }
        )
    }
    //endregion

    //region 换肤-倒计时栏文字颜色（历史期号、往期开奖、当前期号、受注状态）
    fun countDownTextColor(): Int {
        return when (mGameType) {
            GAME.TYPE_GAME_K3 -> R.color.color_text_skin_k3
            GAME.TYPE_GAME_PK10, GAME.TYPE_GAME_PK8 -> R.color.color_skin_pk_text_color
            else -> R.color.white
        }
    }
    //endregion

    //region 换肤-往期开奖按钮背景
    fun openHistoryBtnSkin(): Int {
        return when (mGameType) {
            GAME.TYPE_GAME_PK10, GAME.TYPE_GAME_PK8 -> R.drawable.lot_top_rect_history_pk_bg_shape
            GAME.TYPE_GAME_K3 -> R.drawable.lot_top_rect_history_k3_bg_shape
            else -> R.drawable.lot_top_rect_history_bg_shape
        }
    }
    //endregion

    //region 换肤-往期开奖箭头
    fun openHistoryBtnArrowSkin(): Int {
        return if (GAME.TYPE_GAME_PK10 == mGameType || GAME.TYPE_GAME_PK8 == mGameType) R.drawable.lot_top_rect_history_pk_arrow else R.drawable.lot_top_rect_history_arrow
    }
    //endregion

    //region 换肤-倒计时栏分割线
    fun countDownDivideSkin(): Int {
        return when (mGameType) {
            GAME.TYPE_GAME_K3 -> R.drawable.lot_top_rect_horizontal_divide_k3
            GAME.TYPE_GAME_PK10, GAME.TYPE_GAME_PK8 -> R.drawable.lot_top_rect_horizontal_divide_pk
            else -> R.drawable.lot_top_rect_horizontal_divide_default
        }
    }
    //endregion
    //endregion

    //region 彩种大类判断：快三
    fun isK3(): Boolean {
        return Games.isK3(mGameType)
    }
    //endregion

    // region 彩种大类判断：时时彩
    fun isSSC(): Boolean {
        return Games.isSSC(mGameType)
    }
    //endregion

    // region 彩种大类判断：PK10、PK8
    fun isPK(): Boolean {
        return GAME.TYPE_GAME_PK10 == mGameType || GAME.TYPE_GAME_PK8 == mGameType
    }
    //endregion

    //region 可见：奖杯（cocos动画、直播）
    fun cupVisible(): Boolean {
        return hasCocosAnim() || hasLive()
    }
    //endregion

    //region 可见：cocos动画
    fun hasCocosAnim(): Boolean {
        return tCocos.hasCocosAnim(mGameType, mGameId)
    }
    //endregion

    //region 可见：直播
    private fun hasLive(): Boolean {
        return false
    }
    //endregion

    //region 可见：标题栏底部分割线
    fun titleBottomDivideVisible(): Boolean {
        return mGameType !in arrayOf(
            GAME.TYPE_GAME_K3,
            GAME.TYPE_GAME_PK10,
            GAME.TYPE_GAME_PK8
        )
    }
    //endregion


    //region 期号简化：期号文字过长，根据彩种处理成适当长度期号，拼接gameName
    fun shortIssueTextWithGameName(issue: String): String {
        return StringBuilder().append(mGameName ?: "").append(" ")
            .append(Games.shortIssueText(issue, mGameType)).append("期")
            .toString()
    }
    //endregion

    //region 期号简化：期号文字过长，根据彩种处理成适当长度期号
    fun shortIssueText(issue: String): String {
        return Games.shortIssueText(issue, mGameType)
    }
    //endregion

    //region 跑马灯文字：标题栏
    fun marqueeTextList(playName: String?): List<String?> {
        return if (playName.isSpace()) listOf(mGameName, playName)
        else listOf(playName, mGameName)
    }
    //endregion

    //region 投注范围
    fun lotPlace(playName: String?): String {
        return when (val gameTypeStr = mGameType.toString()) {
            "1", "2", "3", "9" -> tGame.getShowLotteryPlaceSSC(playName, gameTypeStr)
            "5", "11" -> tGame.getShowLotteryPlacePK10(playName, gameTypeStr)
            else -> ""
        }
    }
    //endregion

    //region 玩法选中：gameId对应玩法下标缓存
    fun play2BetByPos(
        playSelectedPos: Int,
        groupSelectedPos: Int,
        betSelectedPos: Int,
        data: GameBetTypeData?
    ): BetItem? {
        var betItem: BetItem? = null
        return data?.get(playSelectedPos)?.list?.let {
            if (groupSelectedPos < it.size) {
                it[groupSelectedPos].list?.let {
                    if (betSelectedPos < it.size) {
                        val betItemTmp = it[betSelectedPos]
                        betItem = betItemTmp
                    }
                }
            }
            betItem
        }
    }
    //endregion

    //region 刷新余额：发送全局eventbus事件
    fun refreshBalanceByPostGlobalEvent() {
        TEventManager.postBalanceEvent()
    }
    //endregion
}