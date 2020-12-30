package com.bdb.lottery.biz.lot.jd

import android.os.Bundle
import androidx.hilt.lifecycle.ViewModelInject
import com.bdb.lottery.biz.base.BaseViewModel
import com.bdb.lottery.const.EXTRA
import com.bdb.lottery.database.lot.entity.SubPlayMethod
import com.bdb.lottery.datasource.common.LiveDataWraper
import com.bdb.lottery.datasource.lot.LotLocalDs
import com.bdb.lottery.datasource.lot.LotRemoteDs
import com.bdb.lottery.datasource.lot.data.jd.BetItem
import com.bdb.lottery.datasource.lot.data.jd.GameBetTypeData
import com.bdb.lottery.datasource.lot.data.jd.PlayItem
import com.bdb.lottery.utils.cache.TCache
import com.bdb.lottery.utils.convert.Converts
import javax.inject.Inject

class LotJdViewModel @ViewModelInject @Inject constructor(
    private val tCache: TCache,
    private val lotLocalDs: LotLocalDs,
    private val lotRemoteDs: LotRemoteDs,
) : BaseViewModel() {
    var mToken: String? = null
    val mGameBetTypeData = LiveDataWraper<GameBetTypeData?>()

    //region 初始化彩票
    private var mUserBonus: Double? = 0.0
    fun initGame() {
        lotRemoteDs.initGame(mGameId.toString()) {
            mUserBonus = it?.userBonus
            mToken = it?.token
        }
    }
    //endregion

    //region 获取彩票玩法
    fun getBetType() {
        lotRemoteDs.getBetType(mGameId.toString()) {
            mGameBetTypeData.setData(it)
        }
    }
    //endregion

    //region 数据库查找玩法说明
    private var mSubPlayMethod: SubPlayMethod? = null
    fun getLocalBetType(playId: Int) {
        lotLocalDs.queryBetTypeByPlayId(playId)
            ?.let {
                mSubPlayMethod = if (it.size > 1) {
                    it.last { !it.belongto.contains("PK10") }
                } else {
                    if (!it.isNullOrEmpty()) it.last() else null
                }
                switchDanFuStyle(mSubPlayMethod?.subPlayMethodDesc?.isdanshi ?: true)
            }
    }
    //endregion

    //region 删除重复号码
    var mSingleNumCount: Int = 5//单注号码数

    var mNoteCount: Int = 0//投注注数

    fun repeatNdErrorNums(text: String): String? {
        mNoteCount = 0
        if (text.length <= mSingleNumCount) return null
        val buff = StringBuilder(text)
        var offset = mSingleNumCount
        while (offset < buff.length && offset > 0) {
            buff.insert(offset, ",")
            offset += 1 + mSingleNumCount
            mNoteCount++
        }
        return buff.toString()
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
        play: (playSelectedPos: Int, betSelectedPos: Int) -> Unit
    ) {
        play.invoke(mPlaySelectedPos, mBetSelectedPos)
    }

    private fun playCacheByGameId(gameId: Int) {
        mPlaySelectedPos = tCache.playLayer1Cache4GameId(gameId)
        mGroupSelectedPos = tCache.playGroupCache4GameId(gameId)
        mBetSelectedPos = tCache.playLayer2Cache4GameId(gameId)
        mPlayId = tCache.playIdCache4GameId(gameId)
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
        unSelectPre: (preGroupPosition: Int) -> Unit
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
                        val item = it[mBetSelectedPos]
                        mSelectedBetItem = item
                        mPlayId = item.betType
                        onBetSelected.invoke(item)
                    }
                }
            }
        }
    }
    //endregion

    //region 金额单位
    var mAmountUnit: Int = 1//默认元
    fun setAmountUnit(unit: Int) {
        mAmountUnit = unit
    }
    //endregion

    //region 切换单式、复式
    private val MODE_SINGLE = 0//单式
    private val MODE_DUPLEX = 1//复式
    private var mMode = MODE_DUPLEX//模式
    private fun switchDanFuStyle(isSingleStyle: Boolean) {
        mMode = if (isSingleStyle) MODE_SINGLE else MODE_DUPLEX
    }

    fun isSingleStyle(): Boolean {
        return MODE_SINGLE == mMode
    }
    //endregion

    fun checkNdGenLotParams(
        betNums: String,//投注号码
        multiple: String,//倍数
        toast: (String) -> Unit,
        lot: (params: LotParams?, error: (String) -> Unit) -> Unit
    ) {
        if (null == mSubPlayMethod) {
            getBetType()
            toast.invoke("正在获取玩法配置")
            return
        }
        //验证digit，选中位置个数

        //验证注数大于0
        repeatNdErrorNums(betNums)
        if (mNoteCount < 1) {
            toast.invoke("请按玩法规则进行投注")
            return
        }
        lot.invoke(
            LotParams.genLotParamsByPlayConfig(
                mNoteCount, multiple = multiple.toInt(), betNums,
                Converts.unit2Enum(mAmountUnit), mSelectedBetItem, mSubPlayMethod
            )
        ) { mToken = it }
    }
}