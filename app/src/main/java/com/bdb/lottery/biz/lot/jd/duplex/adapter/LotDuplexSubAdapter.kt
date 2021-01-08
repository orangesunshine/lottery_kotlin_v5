package com.bdb.lottery.biz.lot.jd.duplex.adapter

import android.view.View
import android.view.ViewGroup
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseSelectedQuickAdapter
import com.bdb.lottery.biz.lot.jd.duplex.LotDuplexData
import com.bdb.lottery.const.GAME
import com.bdb.lottery.extension.setItemChildSelected
import com.bdb.lottery.extension.setSize
import com.bdb.lottery.extension.setTextColorStateList
import com.bdb.lottery.extension.setTextSize
import com.bdb.lottery.module.application.AppEntries
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.util.getItemView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import dagger.hilt.android.EntryPointAccessors
import org.apache.commons.lang3.StringUtils
import timber.log.Timber

class LotDuplexSubAdapter(
    private var singleClick: Boolean = false,
    private val gameType: Int,
    private var betTypeId: Int,
    private var spanCount: Int,
    private var lotLotDuplexData: LotDuplexData,
    updateBlock: (list: ArrayList<Int>) -> Unit
) : BaseSelectedQuickAdapter<String, BaseViewHolder>(
    R.layout.lot_duplex_sub_item,
    lotLotDuplexData.genBallDatas(gameType), updateBlock
) {
    private val tSound by lazy {
        EntryPointAccessors.fromApplication(context, AppEntries::class.java).provideTSound()
    }

    fun notifyChange(
        singleClick: Boolean = false,
        betTypeId: Int,
        spanCount: Int,
        lotLotDuplexData: LotDuplexData,
    ) {
        this.singleClick = singleClick
        this.betTypeId = betTypeId
        this.spanCount = spanCount
        this.lotLotDuplexData = lotLotDuplexData
        setNewInstance(lotLotDuplexData.genBallDatas(gameType))
    }

    private fun isLHH(item: String): Boolean {
        return StringUtils.containsAny(item, "龙", "虎", "和")
    }

    private val LONG_HU_HE_VIEW = 0x10000666
    override fun getItemViewType(position: Int): Int {
        return if (isLHH(getItem(position))) {
            //龙虎和
            LONG_HU_HE_VIEW
        } else {
            return super.getItemViewType(position)
        }
    }

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        if (LONG_HU_HE_VIEW == viewType) {
            return createBaseViewHolder(parent.getItemView(R.layout.lot_duplex_sub_item_long_hu))
        } else {
            return super.onCreateDefViewHolder(parent, viewType)
        }
    }

    private val isPk10 = GAME.TYPE_GAME_PK10 == gameType || GAME.TYPE_GAME_PK8 == gameType
    override fun convert(holder: BaseViewHolder, item: String) {
        val itemViewType = holder.itemViewType
        if (itemViewType == LONG_HU_HE_VIEW) {
            holder.setImageResource(
                R.id.lot_duplex_sub_item_iv, when (item) {
                    "龙" -> if (isPk10) R.drawable.lot_duplex_sub_item_pk_long_selected else R.drawable.lot_duplex_sub_item_long_selector
                    "虎" -> if (isPk10) R.drawable.lot_duplex_sub_item_pk_hu_selected else R.drawable.lot_duplex_sub_item_hu_selector
                    "和" -> R.drawable.lot_duplex_sub_item_he_selector
                    else -> -1
                }
            )
            holder.setItemChildSelected(R.id.lot_duplex_sub_item_iv, isItemSelected(holder))
        } else {
            val adapterPosition = holder.adapterPosition
            val ballTextList = lotLotDuplexData.ballTextList
            holder.setText(
                R.id.lot_duplex_sub_item_tv,
                if (null != ballTextList && adapterPosition < ballTextList.size) ballTextList[adapterPosition] else if (lotLotDuplexData.zeroVisible && adapterPosition < 10) "0$adapterPosition" else adapterPosition.toString()
            )
            val isK3 = GAME.TYPE_GAME_K3 == gameType
            val containsDxds = StringUtils.containsAny(item, "大", "小", "单", "双")
            holder.setTextColorStateList(
                context, R.id.lot_duplex_sub_item_tv, when (gameType) {
                    GAME.TYPE_GAME_PK10, GAME.TYPE_GAME_PK8 -> R.color.lot_duplex_sub_item_pk_ball_text_color_selector
                    GAME.TYPE_GAME_K3 -> R.color.lot_duplex_sub_item_k3_ball_text_color_selector
                    else -> R.color.lot_duplex_sub_item_ball_text_color_selector
                }
            )
            holder.setTextSize(
                R.id.lot_duplex_sub_item_tv, when (spanCount) {
                    6 -> {
                        if (isK3) 20f else 14f
                    }
                    120 -> {
                        if (isK3) 16f else 18f
                    }
                    else -> {
                        if (item.length > 1) 15f else 18f
                    }
                }
            )
            holder.setSize(
                R.id.lot_duplex_sub_item_tv, when (spanCount) {
                    6 -> {
                        if (isK3) 40f else 30f
                    }
                    120 -> {
                        if (isK3) 45f else 35f
                    }
                    else -> {
                        if (!isK3 && containsDxds) 44f else 35f
                    }
                }
            )
            holder.setBackgroundResource(
                R.id.lot_duplex_sub_item_tv, when (gameType) {
                    GAME.TYPE_GAME_PK10, GAME.TYPE_GAME_PK8 -> if (containsDxds) R.drawable.lot_duplex_sub_item_pk_circle_selector else R.drawable.lot_duplex_sub_item_pk_round_selector
                    GAME.TYPE_GAME_K3 -> R.drawable.lot_duplex_sub_item_k3_selector
                    else -> R.drawable.lot_duplex_sub_item_selector
                }
            )
            holder.setItemChildSelected(R.id.lot_duplex_sub_item_tv, isItemSelected(holder))
        }
    }

    override fun convert(holder: BaseViewHolder, item: String, payloads: List<Any>) {
        holder.setItemChildSelected(
            if (isLHH(item)) R.id.lot_duplex_sub_item_iv else R.id.lot_duplex_sub_item_tv,
            isItemSelected(holder)
        )
    }

    init {
        setOnItemClickListener { _: BaseQuickAdapter<*, *>, view: View, position: Int ->
            tSound.playBetBallClickSound()
            YoYo.with(Techniques.ZoomIn).duration(100).playOn(view)
            notifySelectedPositionWithPayLoads(position, singleSelected = singleClick)
        }
    }

    //region 大小单双

    fun selectedList2DxdsTag(list: ArrayList<Int>): Int {
        list.sort()
        return if (list.equals(getBigBallSelectedPositions())) {
            0
        } else if (list.equals(getSmallBallSelectedPositions())) {
            1
        } else if (list.equals(getSingleBallSelectedPositions())) {
            2
        } else if (list.equals(getDoubleBallSelectedPositions())) {
            3
        } else {
            -1
        }
    }

    //region 大
    private fun getBigBallSelectedPositions(): MutableList<Int> {
        val list = mutableListOf<Int>()
        for (i in itemCount / 2 until itemCount) {
            list.add(i)
        }
        Timber.d("getBigBallSelectedPositions: $list")
        return list
    }

    fun bigSelectedChange(selected: Boolean) {
        tSound.playBetBallClickSound()
        if (selected)
            notifyMultiSelectedPositionWithPayLoads(getBigBallSelectedPositions())
        else
            notifyUnSelectedAllWithPayLoads()
    }
    //endregion

    //region 选中小
    private fun getSmallBallSelectedPositions(): MutableList<Int> {
        val list = mutableListOf<Int>()
        for (i in 0 until itemCount / 2) {
            list.add(i)
        }
        Timber.d("getSmallBallSelectedPositions: $list")
        return list
    }

    fun smallSelectedChange(selected: Boolean) {
        tSound.playBetBallClickSound()
        if (selected)
            notifyMultiSelectedPositionWithPayLoads(getSmallBallSelectedPositions())
        else
            notifyUnSelectedAllWithPayLoads()
    }
    //endregion

    //region 选中单
    private fun getSingleBallSelectedPositions(): MutableList<Int> {
        val list = mutableListOf<Int>()
        for (i in 0 until itemCount) {
            if (i % 2 != 0)
                list.add(i)
        }
        Timber.d("getSingleBallSelectedPositions: $list")
        return list
    }

    fun singleSelectedChange(selected: Boolean) {
        tSound.playBetBallClickSound()
        if (selected)
            notifyMultiSelectedPositionWithPayLoads(getSingleBallSelectedPositions())
        else
            notifyUnSelectedAllWithPayLoads()
    }
    //endregion

    //region 选中双
    private fun getDoubleBallSelectedPositions(): MutableList<Int> {
        val list = mutableListOf<Int>()
        for (i in 0 until itemCount) {
            if (i % 2 == 0)
                list.add(i)
        }
        Timber.d("getDoubleBallSelectedPositions: $list")
        return list
    }

    fun doubleSelectedChange(selected: Boolean) {
        tSound.playBetBallClickSound()
        if (selected)
            notifyMultiSelectedPositionWithPayLoads(getDoubleBallSelectedPositions())
        else
            notifyUnSelectedAllWithPayLoads()
    }
    //endregion
    //endregion

    //冷热
    fun hotVisible(visible: Boolean) {
    }

    //遗漏
    fun leaveVisible(visible: Boolean) {
    }
}