package com.bdb.lottery.biz.lot.jd

import android.os.Bundle
import android.widget.EditText
import androidx.hilt.lifecycle.ViewModelInject
import com.bdb.lottery.biz.base.BaseViewModel
import com.bdb.lottery.biz.lot.jd.single.SingleTextWatcher
import com.bdb.lottery.const.EXTRA
import com.bdb.lottery.database.lot.entity.SubPlayMethod
import com.bdb.lottery.datasource.common.LiveDataWrapper
import com.bdb.lottery.datasource.lot.LotLocalDs
import com.bdb.lottery.datasource.lot.LotRemoteDs
import com.bdb.lottery.datasource.lot.data.LotParam
import com.bdb.lottery.datasource.lot.data.SingleBet
import com.bdb.lottery.datasource.lot.data.TouZhuHaoMa
import com.bdb.lottery.datasource.lot.data.jd.BetItem
import com.bdb.lottery.datasource.lot.data.jd.GameBetTypeData
import com.bdb.lottery.datasource.lot.data.jd.PlayItem
import com.bdb.lottery.datasource.lot.data.jd.SingledInfo
import com.bdb.lottery.extension.isSpace
import com.bdb.lottery.utils.cache.TCache
import com.bdb.lottery.utils.convert.Converts
import com.bdb.lottery.utils.thread.Threads
import com.bdb.lottery.utils.ui.toast.AbsToast
import javax.inject.Inject

class LotJdViewModel @ViewModelInject @Inject constructor(
    private val tCache: TCache,
    private val lotLocalDs: LotLocalDs,
    private val lotRemoteDs: LotRemoteDs,
) : BaseViewModel() {
    val gameBetTypeDataLd = LiveDataWrapper<GameBetTypeData?>()
    val eedDigitLd = LiveDataWrapper<Boolean>()
    val isSingleStyleLd = LiveDataWrapper<Boolean>()
    val onLocalBetTypeLd = LiveDataWrapper<Boolean>()
    val atLeastDigitLd = LiveDataWrapper<Int?>()
    val noteCountLd = LiveDataWrapper<Int>()
    var mToken: String? = null
    private var mSubPlayMethod: SubPlayMethod? = null

    //region 初始化彩票
    private var mUserBonus: Double = 0.0
    private var mUserRebate: Double = 0.0
    private var mSingledInfo: SingledInfo? = null
    private var mAlreadyInitGame = false
    fun initGame() {
        lotRemoteDs.initGame(mGameId.toString()) {
            mSingledInfo = it?.singledInfo
            mUserBonus = it?.userBonus ?: 0.0
            mUserRebate = it?.user ?: 0.0
            mToken = it?.token
            if (!mAlreadyInitGame) mAlreadyInitGame = true
        }
    }
    //endregion

    //region 获取彩票玩法
    private var mAlreadyGetBetType = false
    fun getBetType() {
        lotRemoteDs.getBetType(mGameId.toString()) {
            gameBetTypeDataLd.setData(it)
            if (!mAlreadyGetBetType) mAlreadyGetBetType = true
        }
    }
    //endregion

    //region 数据库查找玩法说明
    fun getLocalBetType(playId: Int) {
        lotLocalDs.queryBetTypeByPlayId(playId)
            ?.let {
                mSubPlayMethod = if (it.size > 1) {
                    it.last { !it.belongto.contains("PK10") }
                } else {
                    if (!it.isNullOrEmpty()) it.last() else null
                }
                mSingleNumCount = mSubPlayMethod?.subPlayMethodDesc?.single_num_counts ?: 0
                val needDigit = mSubPlayMethod?.subPlayMethodDesc?.is_need_show_weizhi == true
                eedDigitLd.setData(needDigit)
                if (needDigit)
                    atLeastDigitLd.setData(mSubPlayMethod?.subPlayMethodDesc?.atleast_wei_shu?.toInt())
                isSingleStyleLd.setData(mSubPlayMethod?.subPlayMethodDesc?.isdanshi != false)
                onLocalBetTypeLd.setData(true)
            }
    }
    //endregion

    //region 初始化彩票参数、玩法下标
    private var mGameId: Int = -1
    private var mGameType: Int = -1
    private var mGameName: String? = null
    fun initExtraVar(arguments: Bundle?) {
        arguments?.let {
            mGameId = it.getInt(EXTRA.ID_GAME_EXTRA)
            mGameType = it.getInt(EXTRA.TYPE_GAME_EXTRA)
            mGameName = it.getString(EXTRA.NAME_GAME_EXTRA)
        }
        playCacheByGameId(mGameId)
    }
    //endregion

    //region 经典：玩法下标-缓存初始化
    private var mPlaySelectedPos: Int = 0
    private var mGroupSelectedPos: Int = 0
    private var mBetSelectedPos: Int = 0
    private var mPlayId: Int = 0
    fun renderPlayNdBet(
        play: (playSelectedPos: Int, betSelectedPos: Int) -> Unit,
    ) {
        play.invoke(mPlaySelectedPos, mBetSelectedPos)
    }

    private fun playCacheByGameId(gameId: Int) {
        mPlaySelectedPos = tCache.playCacheByGameId(gameId)?:0
        mGroupSelectedPos = tCache.playGroupCacheByGameId(gameId)?:0
        mBetSelectedPos = tCache.betCacheByGameId(gameId)?:0
        mPlayId = tCache.playIdCacheByGameId(gameId)?:0
    }

    fun cachePlay4GameId() {
        tCache.cachePlay4GameId(
            mGameId,
            mPlaySelectedPos,
            mGroupSelectedPos,
            mBetSelectedPos,
            mPlayId
        )
    }
    //endregion

    //region 判断是不是当前选中的一级玩法
    fun isCurPlayNdGroup(playIndex: Int, groupIndex: Int): Boolean {
        return mPlaySelectedPos == playIndex && mGroupSelectedPos == groupIndex
    }
    //endregion

    //region 判断是不是当前选中二级玩法分组
    fun isCurGroup(groupPosition: Int): Boolean {
        return mGroupSelectedPos == groupPosition
    }
    //endregion

    //region 玩法选中
    fun setSelectedPosOnBetSelected(
        playSelectedPos: Int,
        groupSelectedPos: Int,
        betSelectedPos: Int,
        unSelectPre: (preGroupPosition: Int) -> Unit,
    ) {
        //选中玩法：更新一级玩法下标、二级玩法组下标、二级玩法下标
        if (mPlaySelectedPos == playSelectedPos && mGroupSelectedPos == groupSelectedPos && mBetSelectedPos == betSelectedPos) return
        if (mPlaySelectedPos != playSelectedPos) {
            mPlaySelectedPos = playSelectedPos
        } else {
            if (mGroupSelectedPos != groupSelectedPos) {
                val preGroupPosition = mGroupSelectedPos
                unSelectPre.invoke(preGroupPosition)
            }
        }
        mGroupSelectedPos = groupSelectedPos
        mBetSelectedPos = betSelectedPos
    }

    //初始化玩法选中
    private var mSelectedBetItem: BetItem? = null
    fun setSelectedBetItem(selectedBetItem: BetItem?) {
        mSelectedBetItem = selectedBetItem
        mPlayId = selectedBetItem?.betType ?: 0
    }

    fun getInitBet(item: PlayItem?): BetItem? {
        var betItem: BetItem? = null
        return item?.list?.let {
            if (mGroupSelectedPos < it.size) {
                it[mGroupSelectedPos].list?.let {
                    if (mBetSelectedPos < it.size) {
                        val betItemTmp = it[mBetSelectedPos]
                        betItem = betItemTmp
                    }
                }
            }
            betItem
        }
    }
    //endregion

    //region 金额单位
    fun initAmountUnit(amountUnit: (Int?) -> Unit) {
        amountUnit.invoke(moneyUnitCache())
    }

    fun cacheMoneyUnit(moneyUnit: Int) {
        tCache.cacheMoneyUnit(mGameId, mPlayId, moneyUnit)
    }

    private fun moneyUnitCache(): Int? {
        return tCache.moneyUnitCache(mGameId, mPlayId)
    }
    //endregion

    fun verifyNdGenLotParams(
        betNums: String,//投注号码
        multiple: Int,//倍数
        moneyUnit: Int,//金额单位
        digit: String,//用户选中digit
        needDigit: Boolean?,//最少位置
        verifyDigit: String?,//验证位置
        noteCount: Int,//注数
        toast: AbsToast,
        lot: (lotParam: LotParam, error: (String) -> Unit) -> Unit,
    ) {
        if (!mAlreadyGetBetType) {
            getBetType()
            toast.showWarning("正在获取玩法配置")
            return
        }

        if (!mAlreadyInitGame) {
            initGame()
            toast.showWarning("正在初始化彩票")
            return
        }

        //验证digit，选中位置个数
        if (needDigit == true && !verifyDigit.isSpace()) {
            toast.showError(verifyDigit)
            return
        }

        //验证注数大于0
        if (noteCount < 1) {
            toast.showError("请按玩法规则进行投注")
            return
        }

        val touZhuHaoMa = genTouZhuHaoMa(
            betNums,
            multiple,
            moneyUnit,
            getDigit(needDigit, digit, mSubPlayMethod),
            verifyDigit,
            needDigit,
            noteCount,
            getAmount(noteCount, moneyUnit, multiple),
            getSingleMoney(mUserRebate, mSelectedBetItem?.baseScale ?: 0.0),
            mSelectedBetItem,
            mSingledInfo,
            toast
        ) ?: return
        lot.invoke(
            LotParam(
                mGameId.toString(), mGameName.toString(), null,
                kg = false,
                tingZhiZhuiHao = true,
                token = mToken!!,
                touZhuHaoMa = listOf(touZhuHaoMa),
                zhuiHaoQiHao = null
            )
        ) { mToken = it }
    }

    //总额
    fun getAmount(noteCount: Int, moneyUnit: Int, multiple: Int): Double {
        return 2 * noteCount * multiple * Math.pow(10.0, (1 - moneyUnit).toDouble())
    }

    private fun getDigit(
        needDigit: Boolean?,
        digit: String,
        subPlayMethod: SubPlayMethod?,
    ): String {
        return if (needDigit == true) digit else subPlayMethod?.subPlayMethodDesc?.digit ?: ""
    }

    //理论最高奖金
    fun getSingleMoney(
        userRebate: Double = mUserRebate,
        baseScale: Double = mSelectedBetItem?.baseScale ?: 0.0,
        amountModelValue: Double = 1.0,
    ): Double {
        val scale = userRebate * (mSelectedBetItem?.multiple ?: 0.0)
        return (baseScale + scale) * amountModelValue
    }

    private fun genTouZhuHaoMa(
        betNums: String,
        multiple: Int,
        moneyUnit: Int,
        digit: String,//用户选中digit
        verifyDigit: String?,//验证位置
        needDigit: Boolean?,//最少位置
        noteCount: Int,
        amount: Double,
        singleMoney: Double,
        betItem: BetItem?,
        singledInfo: SingledInfo?,
        toast: AbsToast,
    ): TouZhuHaoMa? {
        //验证digit，选中位置个数
        if (needDigit == true && !verifyDigit.isSpace()) {
            toast.showError(verifyDigit)
            return null
        }
        val isDanTiao = isDanTiao(noteCount, betItem)
        return TouZhuHaoMa(
            baseScale = singleMoney * multiple,
            singleMoney = singleMoney,
            bouse = mUserBonus,
            danZhuJinEDanWei = Converts.unit2Params(moneyUnit),
            digit = digit,
            model = Converts.unit2Enum(moneyUnit).toString(),
            playtypename = mSelectedBetItem?.getPlayTitle(),
            touZhuBeiShu = multiple,
            yongHuSuoTiaoFanDian = 0.0,
            zhuShu = noteCount,
            wanFaID = mPlayId,
            amount = amount,
            touZhuHaoMa = betNums,
            isDanTiao = isDanTiao,
            singleBet = SingleBet(
                isDanTiao,
                if (isDanTiao) getDanTiaoMaxMoney(betItem, singledInfo) else 0.0
            )
        )
    }

    //region 单挑
    //判断是不是单挑
    private fun isDanTiao(
        noteCount: Int,
        betItem: BetItem?,
    ): Boolean {
        val singleMaxBetCount = betItem?.singleMaxBetCount ?: 0
        return singleMaxBetCount > noteCount
    }

    fun danTiaoTips(): String? {
        if (null == mSelectedBetItem || null == mSingledInfo) return null
        return danTiaoTips(mSelectedBetItem!!, mSingledInfo!!)
    }

    private fun danTiaoTips(betItem: BetItem, singledInfo: SingledInfo): String {
        val maxBetCount = betItem.maxBetCount
        var scale = 0.0
        var maxMoney = 0.0
        if (maxBetCount >= 10000) {
            scale = singledInfo.scales
            maxMoney = singledInfo.maxmoney
        } else if (maxBetCount in 1000..9999) {
            scale = singledInfo.scales2
            maxMoney = singledInfo.maxmoney2
        } else if (maxBetCount in 100..999) {
            scale = singledInfo.scales3
            maxMoney = singledInfo.maxmoney3
        } else if (maxBetCount in 10..99) {
            scale = singledInfo.scales4
            maxMoney = singledInfo.maxmoney4
        }
        val sb = StringBuffer()
        sb.append("该玩法下注注数小于最高注数的")
        sb.append(scale)
        sb.append("%即为单挑,最高中奖额为")
        sb.append(maxMoney)
        sb.append("元")
        return sb.toString()
    }

    private fun getDanTiaoMaxMoney(
        betItem: BetItem?,
        singledInfo: SingledInfo?,
    ): Double {
        var maxMoney = 0.0
        if (singledInfo != null && betItem != null) {
            val maxBetCount: Int = betItem.maxBetCount
            if (maxBetCount >= 10000) {
                maxMoney = singledInfo.maxmoney
            } else if (maxBetCount in 1000..9999) {
                maxMoney = singledInfo.maxmoney2
            } else if (maxBetCount in 100..999) {
                maxMoney = singledInfo.maxmoney3
            } else if (maxBetCount in 10..99) {
                maxMoney = singledInfo.maxmoney4
            }
        }
        return maxMoney
    }
    //endregion

    var mSingleNumCount: Int = 0//单注号码数
    fun createSingleTextWatcher(
        singleInputEt: EditText,
        digit: String,
        error: (String?) -> Unit,
    ): SingleTextWatcher {
        return SingleTextWatcher(
            singleInputEt,
            mSingleNumCount,
            mGameType,
            mPlayId,
            getDigit(eedDigitLd.getLiveData().value, digit, mSubPlayMethod),
            { Threads.retrofitUIThread { noteCountLd.setData(it) } }, error
        )
    }
}