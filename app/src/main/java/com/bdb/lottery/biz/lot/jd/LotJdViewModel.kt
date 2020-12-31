package com.bdb.lottery.biz.lot.jd

import android.os.Bundle
import androidx.hilt.lifecycle.ViewModelInject
import com.bdb.lottery.biz.base.BaseViewModel
import com.bdb.lottery.const.EXTRA
import com.bdb.lottery.database.lot.entity.SubPlayMethod
import com.bdb.lottery.datasource.common.LiveDataWraper
import com.bdb.lottery.datasource.lot.LotLocalDs
import com.bdb.lottery.datasource.lot.LotRemoteDs
import com.bdb.lottery.datasource.lot.data.LotParam
import com.bdb.lottery.datasource.lot.data.SingleBet
import com.bdb.lottery.datasource.lot.data.TouZhuHaoMa
import com.bdb.lottery.datasource.lot.data.jd.BetItem
import com.bdb.lottery.datasource.lot.data.jd.GameBetTypeData
import com.bdb.lottery.datasource.lot.data.jd.PlayItem
import com.bdb.lottery.datasource.lot.data.jd.SingledInfo
import com.bdb.lottery.utils.cache.TCache
import com.bdb.lottery.utils.convert.Converts
import com.bdb.lottery.utils.ui.toast.AbsToast
import javax.inject.Inject

class LotJdViewModel @ViewModelInject @Inject constructor(
    private val tCache: TCache,
    private val lotLocalDs: LotLocalDs,
    private val lotRemoteDs: LotRemoteDs,
) : BaseViewModel() {
    var mToken: String? = null
    val mGameBetTypeData = LiveDataWraper<GameBetTypeData?>()
    val mNeedDigit = LiveDataWraper<Boolean>()
    val mIsSingleStyle = LiveDataWraper<Boolean>()
    val mAtLeastDigit = LiveDataWraper<Int?>()
    private var mSubPlayMethod: SubPlayMethod? = null

    //region 初始化彩票
    private var mUserBonus: Double = 0.0
    private var mUserRebate: Double = 0.0
    private var mSingledInfo: SingledInfo? = null
    private var mAlreadyInitGame = false
    fun initGame() {
        lotRemoteDs.initGame(mGameId.toString(), {
            mSingledInfo = it?.singledInfo
            mUserBonus = it?.userBonus ?: 0.0
            mUserRebate = it?.user ?: 0.0
            mToken = it?.token
        }) {
            mAlreadyInitGame = true
        }
    }
    //endregion

    //region 获取彩票玩法
    private var mAlreadyGetBetType = false
    fun getBetType() {
        lotRemoteDs.getBetType(mGameId.toString(), {
            mGameBetTypeData.setData(it)
        }) { mAlreadyGetBetType = true }
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
                mNeedDigit.setData(mSubPlayMethod?.subPlayMethodDesc?.is_need_show_weizhi == true)
                mAtLeastDigit.setData(mSubPlayMethod?.subPlayMethodDesc?.atleast_wei_shu?.toInt())
                mIsSingleStyle.setData(mSubPlayMethod?.subPlayMethodDesc?.isdanshi != false)
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
        mPlaySelectedPos = tCache.playCacheByGameId(gameId)
        mGroupSelectedPos = tCache.playGroupCacheByGameId(gameId)
        mBetSelectedPos = tCache.betCacheByGameId(gameId)
        mPlayId = tCache.playIdCacheByGameId(gameId)
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
    }

    fun initBetSelected(item: PlayItem?, onBetSelected: (item: BetItem?) -> Unit) {
        item?.list?.let {
            if (mGroupSelectedPos < it.size) {
                it.get(mGroupSelectedPos).list?.let {
                    if (mBetSelectedPos < it.size) {
                        val bet = it[mBetSelectedPos]
                        mSelectedBetItem = bet
                        mPlayId = bet.betType
                        onBetSelected.invoke(bet)
                    }
                }
            }
        }
    }
    //endregion

    //region 金额单位
    fun initAmountUnit(amountUnit: (Int) -> Unit) {
        amountUnit.invoke(moneyUnitCache())
    }

    fun cacheMoneyUnit(moneyUnit: Int) {
        tCache.cacheMoneyUnit(mGameId, mPlayId, moneyUnit)
    }

    private fun moneyUnitCache(): Int {
        return tCache.moneyUnitCache(mGameId, mPlayId)
    }
    //endregion

    fun verifyNdGenLotParams(
        betNums: String,//投注号码
        multiple: String,//倍数
        moneyUnit: Int,//金额单位
        digit: String,//用户选中digit
        verifyDigit: Boolean,//验证位置
        atLeastDigit: Int?,//最少位置
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
        if (!verifyDigit) {
            toast.showError("请至少选择${atLeastDigit}个位置")
            return
        }

        //验证注数大于0
        if (noteCount < 1) {
            toast.showError("请按玩法规则进行投注")
            return
        }

        val touZhuHaoMa = genTouZhuHaoMa(
            betNums,
            multiple.toInt(),
            moneyUnit,
            digit,
            verifyDigit,
            atLeastDigit,
            noteCount,
            getAmount(noteCount, moneyUnit, multiple.toInt()),
            getSingleMoney(mUserRebate, mSelectedBetItem?.baseScale ?: 0.0, multiple.toInt()),
            mSelectedBetItem,
            mSingledInfo,
            toast
        ) ?: return
        lot.invoke(
            LotParam(
                mGameId.toString(), mGameName.toString(), null, false,
                true, mToken!!, listOf(touZhuHaoMa), null
            )
        ) { mToken = it }
    }

    //总额
    private fun getAmount(noteCount: Int, moneyUnit: Int, multiple: Int): Double {
        return 2 * noteCount * multiple * Math.pow(10.0, (1 - moneyUnit).toDouble())
    }

    //理论最高奖金
    private fun getSingleMoney(
        userRebate: Double,
        baseScale: Double,
        multiple: Int,
        amountModleValue: Double = 1.0,
    ): Double {
        val scale = multiple * userRebate
        return (baseScale + scale) * amountModleValue
    }

    private fun genTouZhuHaoMa(
        betNums: String,
        multiple: Int,
        moneyUnit: Int,
        digit: String,//用户选中digit
        verifyDigit: Boolean,//验证位置
        atLeastDigit: Int?,//最少位置
        noteCount: Int,
        amount: Double,
        singleMoney: Double,
        betItem: BetItem?,
        singledInfo: SingledInfo?,
        toast: AbsToast,
    ): TouZhuHaoMa? {
        //验证digit，选中位置个数
        if (!verifyDigit) {
            toast.showError("请至少选择${atLeastDigit}个位置")
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

    fun danTiaoTips(betItem: BetItem, singledInfo: SingledInfo): String {
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
}