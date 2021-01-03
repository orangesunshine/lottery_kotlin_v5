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
import com.bdb.lottery.utils.gson.Gsons
import com.bdb.lottery.utils.ui.toast.AbsToast
import javax.inject.Inject

class LotJdViewModel @ViewModelInject @Inject constructor(
    private val tCache: TCache,
    private val lotLocalDs: LotLocalDs,
    private val lotRemoteDs: LotRemoteDs,
) : BaseViewModel() {
    val gameBetTypeDataLd = LiveDataWrapper<GameBetTypeData?>()//玩法接口
    var subPlayMethodLd = LiveDataWrapper<SubPlayMethod?>()

    private var mToken: String? = null

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
                subPlayMethodLd.setData(if (it.size > 1) {
                    it.last { !it.belongto.contains("PK10") }
                } else {
                    if (!it.isNullOrEmpty()) it.last() else null
                })
            }
    }
    //endregion

    private var lotteryHowToPlayMap: Map<String, Map<String, String>>? = null
    fun cachePreHowToPlay(dialogBlock: ((gameType: Int, desc: String?) -> Unit)? = null) {
        if (null != lotteryHowToPlayMap) {
            dialogBlock?.invoke(mGameType, lotteryHowToPlayMap2Desc(lotteryHowToPlayMap))
        } else {
            lotRemoteDs.cachePreHowToPlay {
                it?.let {
                    lotteryHowToPlayMap =
                        Gsons.fromJsonByTokeType<Map<String, Map<String, String>>>(it)
                    dialogBlock?.invoke(mGameType, lotteryHowToPlayMap2Desc(lotteryHowToPlayMap))
                }
            }
        }
    }

    fun lotteryHowToPlayMap2Desc(lotteryHowToPlayMap: Map<String, Map<String, String>>?): String? {
        if (lotteryHowToPlayMap.isNullOrEmpty()) return null
        val ctMap = lotteryHowToPlayMap.get("ct")
        if (ctMap.isNullOrEmpty()) return null
        val key = "gameKind${mGameType}_$mPlayId"
        return ctMap[key] ?: ctMap[mPlayId.toString()]
    }

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
    }
    //endregion

    //region 经典：玩法下标-缓存初始化
    private var mPlayId: Int = 0
    fun playCacheByGameId(cacheBlock: (gameId: Int, TCache) -> Unit) {
        cacheBlock.invoke(mGameId, tCache)
        mPlayId = tCache.playIdCacheByGameId(mGameId) ?: 0
    }

    fun cachePlay4GameId(playSelectedPos: Int, groupSelectedPos: Int, betSelectedPos: Int) {
        tCache.cachePlay4GameId(
            mGameId,
            playSelectedPos,
            groupSelectedPos,
            betSelectedPos,
            mPlayId
        )
    }
    //endregion

    //初始化玩法选中
    fun setCurPlayId(playId: Int) {
        mPlayId = playId
    }

    fun play2BetByPos(item: PlayItem?, groupSelectedPos: Int, betSelectedPos: Int): BetItem? {
        var betItem: BetItem? = null
        return item?.list?.let {
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

    //region 根据玩法配置获取当前位置
    fun getDigit(needDigit: Boolean?, digit: String): String {
        return if (needDigit == true) digit else subPlayMethodLd.getLiveData().value?.subPlayMethodDesc?.digit
            ?: ""
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
        betItem: BetItem?,
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
            digit,
            verifyDigit,
            needDigit,
            noteCount,
            getAmount(noteCount, moneyUnit, multiple),
            getSingleMoney(betItem),
            betItem,
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

    //理论最高奖金
    fun getSingleMoney(
        betItem: BetItem?,
        userRebate: Double = mUserRebate,
        amountModelValue: Double = 1.0,
    ): Double {
        val baseScale = betItem?.baseScale ?: 0.0
        val scale = userRebate * (betItem?.multiple ?: 0.0)
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
            playtypename = betItem?.getPlayTitle(),
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

    fun danTiaoTips(betItem: BetItem?): String? {
        if (null == betItem || null == mSingledInfo) return null
        return danTiaoTips(betItem, mSingledInfo!!)
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

    fun createSingleTextWatcher(
        singleInputEt: EditText,
        singleNumCount: Int,
        digit: String,
        noteCountBlock: (Int) -> Unit,
        error: (String?) -> Unit,
    ): SingleTextWatcher {
        return SingleTextWatcher(
            singleInputEt,
            singleNumCount,
            mGameType,
            mPlayId,
            digit,
            noteCountBlock, error
        )
    }
}