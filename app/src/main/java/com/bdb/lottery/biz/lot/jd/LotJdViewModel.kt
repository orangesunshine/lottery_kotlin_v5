package com.bdb.lottery.biz.lot.jd

import android.widget.EditText
import androidx.hilt.lifecycle.ViewModelInject
import com.bdb.lottery.biz.base.BaseViewModel
import com.bdb.lottery.biz.lot.BetCenter
import com.bdb.lottery.biz.lot.jd.single.SingleTextWatcher
import com.bdb.lottery.const.GAME
import com.bdb.lottery.database.lot.entity.SubPlayMethod
import com.bdb.lottery.datasource.common.LiveDataWrapper
import com.bdb.lottery.datasource.lot.LotLocalDs
import com.bdb.lottery.datasource.lot.LotRemoteDs
import com.bdb.lottery.datasource.lot.data.LotParam
import com.bdb.lottery.datasource.lot.data.SingleBet
import com.bdb.lottery.datasource.lot.data.TouZhuHaoMa
import com.bdb.lottery.datasource.lot.data.jd.BetItem
import com.bdb.lottery.datasource.lot.data.jd.GameBetTypeData
import com.bdb.lottery.datasource.lot.data.jd.OddInfo
import com.bdb.lottery.datasource.lot.data.jd.SingledInfo
import com.bdb.lottery.extension.equalsNSpace
import com.bdb.lottery.extension.isSpace
import com.bdb.lottery.utils.cache.TCache
import com.bdb.lottery.utils.convert.Converts
import com.bdb.lottery.utils.gson.Gsons
import com.bdb.lottery.utils.ui.toast.AbsToast
import org.apache.commons.lang3.StringUtils
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

class LotJdViewModel @ViewModelInject @Inject constructor(
    private val tCache: TCache,
    private val lotLocalDs: LotLocalDs,
    private val lotRemoteDs: LotRemoteDs,
) : BaseViewModel() {

    //region 请求数据

    //region 初始化彩票
    private var mToken: String? = null
    private var mUserBonus: Double = 0.0
    private var mUserRebate: Double = 0.0
    private var mSingledInfo: SingledInfo? = null
    private var mOddInfo: List<OddInfo>? = null
    private var mAlreadyInitGame = AtomicBoolean()
    fun netInitGame(gameId: Int) {
        lotRemoteDs.initGame(gameId.toString()) {
            it?.let {
                mSingledInfo = it.singledInfo
                mOddInfo = it.oddInfo
                mUserBonus = it.userBonus ?: 0.0
                mUserRebate = it.user ?: 0.0
                mToken = it.token
            }
            mAlreadyInitGame.set(true)
        }
    }
    //endregion

    //region 获取彩票玩法
    private var mAlreadyGetBetType = false
    val gameBetTypeDataLd = LiveDataWrapper<GameBetTypeData?>()//玩法接口
    fun netBetType(gameId: Int) {
        lotRemoteDs.getBetType(gameId.toString()) {
            gameBetTypeDataLd.setData(it)
            if (!mAlreadyGetBetType) mAlreadyGetBetType = true
        }
    }
    //endregion

    //region 数据库查找玩法说明
    var subPlayMethodLd = LiveDataWrapper<SubPlayMethod?>()
    fun dbBetType(playId: Int) {
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

    //region 玩法说明
    private var lotteryHowToPlayMap: Map<String, Map<String, String>>? = null
    fun cachePreHowToPlay(
        gameType: Int, playId: Int,
        dialogBlock: ((desc: String?) -> Unit)? = null
    ) {
        if (null != lotteryHowToPlayMap) {
            dialogBlock?.invoke(lotteryHowToPlayMap2Desc(gameType, playId, lotteryHowToPlayMap))
        } else {
            lotRemoteDs.cachePreHowToPlay {
                it?.let {
                    lotteryHowToPlayMap =
                        Gsons.fromJsonByTokeType<Map<String, Map<String, String>>>(it)
                    dialogBlock?.invoke(
                        lotteryHowToPlayMap2Desc(
                            gameType, playId,
                            lotteryHowToPlayMap
                        )
                    )
                }
            }
        }
    }

    //根据游戏大类、玩法id获取玩法说明
    private fun lotteryHowToPlayMap2Desc(
        gameType: Int,
        playId: Int,
        lotteryHowToPlayMap: Map<String, Map<String, String>>?
    ): String? {
        if (lotteryHowToPlayMap.isNullOrEmpty()) return null
        val ctMap = lotteryHowToPlayMap["ct"]
        if (ctMap.isNullOrEmpty()) return null
        val key = "gameKind${gameType}_$playId"
        return ctMap[key] ?: ctMap[playId.toString()]
    }
    //endregion
    //endregion

    //region 工具方法

    //region 金额单位
    fun initAmountUnit(gameId: Int, playId: Int, amountUnit: (Int?) -> Unit) {
        amountUnit.invoke(moneyUnitCache(gameId, playId))
    }

    fun cacheMoneyUnit(
        gameId: Int, playId: Int, moneyUnit: Int
    ) {
        tCache.cacheMoneyUnit(gameId, playId, moneyUnit)
    }

    private fun moneyUnitCache(gameId: Int, playId: Int): Int? {
        return tCache.moneyUnitCache(gameId, playId)
    }
    //endregion

    //region 根据玩法配置获取当前位置
    fun getDigit(needDigit: Boolean?, digit: String): String {
        return if (needDigit == true) digit else subPlayMethodLd.getLiveData().value?.subPlayMethodDesc?.digit
            ?: ""
    }
    //endregion

    //region 校验下注参数
    fun verifyNdGenLotParams(
        mode: Int,
        gameType: Int,
        gameId: Int,
        playId: Int,
        gameName: String?,
        betNums: String,//投注号码
        multiple: Int,//倍数
        moneyUnit: Int,//金额单位
        digit: String,//用户选中digit
        needDigit: Boolean?,//最少位置
        verifyDigit: String?,//验证位置
        noteCount: Int,//注数
        betItem: BetItem?,
        toast: AbsToast,
        lot: (lotParam: LotParam, refreshToken: (String) -> Unit) -> Unit,
    ) {
        if (!mAlreadyGetBetType) {
            netBetType(gameId)
            toast.showWarning("正在获取玩法配置")
            return
        }

        if (mAlreadyInitGame.get()) {
            netInitGame(gameId)
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
            playId,
            betNums,
            multiple,
            moneyUnit,
            digit,
            verifyDigit,
            needDigit,
            noteCount,
            getAmount(noteCount, moneyUnit, multiple),
            getSingleMoney(mode, gameType, betNums, betItem),
            betItem,
            mSingledInfo,
            toast
        ) ?: return
        lot.invoke(
            LotParam(
                gameId.toString(), gameName, null,
                kg = false,
                tingZhiZhuiHao = true,
                token = mToken!!,
                touZhuHaoMa = listOf(touZhuHaoMa),
                zhuiHaoQiHao = ArrayList()
            )
        ) { mToken = it }
    }

    /**
     * 根据参数生成投注参数：touZhuHaoMa
     */
    private fun genTouZhuHaoMa(
        playId: Int,
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
            wanFaID = playId,
            amount = amount,
            touZhuHaoMa = betNums,
            isDanTiao = isDanTiao,
            singleBet = SingleBet(
                isDanTiao,
                if (isDanTiao) getDanTiaoMaxMoney(betItem, singledInfo) else 0.0
            )
        )
    }
    //endregion

    //region 总额
    fun getAmount(noteCount: Int, moneyUnit: Int, multiple: Int): Double {
        return 2 * noteCount * multiple * Math.pow(10.0, (1 - moneyUnit).toDouble())
    }
    //endregion

    //region 理论最高奖金
    fun getSingleMoney(
        mode: Int, gameType: Int,
        betNums: String?,
        betItem: BetItem?,
        userRebate: Double = mUserRebate,
        amountModelValue: Double = 1.0,
    ): Double {
        if (isLHH(mode, gameType, betNums)) {
            mOddInfo?.let {
                for (oddInfo in it) {
                    if (oddInfo.special_number.equalsNSpace(betNums)) return BetCenter.computeBonusText(
                        oddInfo,
                        mUserBonus
                    )
                }
            }
            return 0.0
        } else {
            val baseScale = betItem?.baseScale ?: 0.0
            val scale = userRebate * (betItem?.multiple ?: 0.0)
            return (baseScale + scale) * amountModelValue
        }
    }

    /**
     * 根据彩种大类，投注号码判断是否使用龙虎和奖金(initGame->oddInfo)
     */
    private fun isLHH(mode: Int, gameType: Int, betNums: String?): Boolean {
        if (mode == 0) return false//单式
        return when (gameType) {
            GAME.TYPE_GAME_SSC -> return StringUtils.equalsAny(betNums, "龙", "虎", "和")
            GAME.TYPE_GAME_PK10, GAME.TYPE_GAME_PK8 -> return StringUtils.equalsAny(
                betNums,
                "大小单双",
                "和值"
            )
            GAME.TYPE_GAME_K3 -> return StringUtils.equals(betNums, "和值")
            else -> false
        }
    }
    //endregion

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

    //region 单式：监听输入框
    fun createSingleTextWatcher(
        gameType: Int,
        playId: Int,
        singleInputEt: EditText,
        singleNumCount: Int,
        digit: String,
        noteCountBlock: (Int) -> Unit,
        error: (String?) -> Unit,
    ): SingleTextWatcher {
        return SingleTextWatcher(
            singleInputEt,
            singleNumCount,
            gameType,
            playId,
            digit,
            noteCountBlock, error
        )
    }
    //endregion
    //endregion
}