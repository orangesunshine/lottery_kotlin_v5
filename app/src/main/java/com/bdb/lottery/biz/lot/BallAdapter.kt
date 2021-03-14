package com.bdb.lottery.biz.lot

import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bdb.lottery.R
import com.bdb.lottery.const.GAME
import com.bdb.lottery.datasource.lot.data.HistoryData
import com.bdb.lottery.extension.isSpace
import com.bdb.lottery.utils.lot.Lots
import com.bdb.lottery.utils.ui.size.Sizes
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import com.zhy.view.flowlayout.TagFlowLayout

class BallAdapter(
    private val gameType: Int,
    private val gameId: Int,
    private var parentPlayId: Int,
    private val betTypeId: Int,
    private var brightLists: String,
    data: MutableList<HistoryData.HistoryItem>? = null,
) : BaseQuickAdapter<HistoryData.HistoryItem, BaseViewHolder>(R.layout.lot_history_item, data) {
    private val PAYLOAD_LABEL = "PAYLOAD_LABEL"

    //获取期号
    private fun getIssue(item: HistoryData.HistoryItem): String {
        return "第" + item.issueno + "期"
    }

    //形态
    private fun labelText(item: HistoryData.HistoryItem): String {
        return Lots.getLabel(gameType, parentPlayId, betTypeId, item.nums)
    }

    private fun ballSize(gameType: Int): Int {
        return Sizes.dp2px(
            when (gameType) {
                GAME.TYPE_GAME_FREQUENCY_LOW, GAME.TYPE_GAME_K3 -> 20f
                GAME.TYPE_GAME_PK10, GAME.TYPE_GAME_PK8 -> 17f
                else -> 18f
            }
        )
    }

    private fun ballSpace(gameType: Int): Int {
        return Sizes.dp2px(
            when (gameType) {
                GAME.TYPE_GAME_K3, GAME.TYPE_GAME_FREQUENCY_LOW -> 10f
                GAME.TYPE_GAME_LHC, GAME.TYPE_GAME_PK10, GAME.TYPE_GAME_PK8 -> 3f
                else -> 8f
            }
        )
    }

    //球
    fun ball(position: Int, num: String?): TextView {
        var bgRes = -1
        var textColor = "#ffffff"
        var content: String? = null
        val isSymbol = "+" == num || "=" == num
        when (gameType) {
            GAME.TYPE_GAME_SSC, GAME.TYPE_GAME_KL10FEN, GAME.TYPE_GAME_11X5 -> {
                bgRes = R.drawable.lot_open_nums_red_circle_shape
                content = num
            }
            GAME.TYPE_GAME_FREQUENCY_LOW -> {
            }
            GAME.TYPE_GAME_PK10, GAME.TYPE_GAME_PK8 -> {//pk10,pk8
                bgRes = Lots.num2Ball4Pk(num)
            }
            GAME.TYPE_GAME_PC28 -> {//pc28
                textColor = "#333333"
                bgRes = if (isSymbol) -1 else Lots.num2Dr4Pc28(num)
                content = num
            }
            GAME.TYPE_GAME_K3 -> {//快三
                bgRes = Lots.ball2Dr4K3(num)
            }
            GAME.TYPE_GAME_LHC -> {//六合彩
                textColor = "#333333"
                bgRes =
                    if (isSymbol) -1 else Lots.ball2Dr4LHC(Lots.deleteInitZero(num), 74 == gameId)
                content = num
            }
        }
        val textView = TextView(context)
        textView.text = content
        if (-1 == bgRes) {
            textView.background = null
        } else {
            textView.setBackgroundResource(bgRes)
        }
        //球大小
        val size = ballSize(gameType)
        textView.layoutParams = ViewGroup.MarginLayoutParams(size, size)
        //球间距
        (textView.layoutParams as ViewGroup.MarginLayoutParams).leftMargin = ballSpace(gameType)

        textView.gravity = Gravity.CENTER
        textView.setTextColor(Color.parseColor(textColor))
        val str = if (position + 1 == 10) "*" else (position + 1).toString()
        textView.alpha = if (brightLists.isSpace() || brightLists.contains(str)) 1f else 0.4f
        return textView
    }

    override fun convert(holder: BaseViewHolder, item: HistoryData.HistoryItem) {
        if (GAME.TYPE_GAME_K3 == gameType) holder.setBackgroundResource(
            R.id.lot_history_item_horizontal_divide_view,
            R.color.color_skin_k3_line
        )//期号
        holder.setText(R.id.lot_history_item_issue_tv, getIssue(item))//期号
        holder.getView<View>(R.id.lot_history_item_label_tv).layoutParams.width =
            Sizes.dp2px(if (GAME.TYPE_GAME_SSC == gameType) 70f else 40f)
        //形态
        holder.setText(R.id.lot_history_item_label_tv, labelText(item))
        holder.getView<View>(R.id.lot_history_item_label_tv).layoutParams.width =
            Sizes.dp2px(if (GAME.TYPE_GAME_SSC == gameType) 70f else 40f)
        val nums = item.nums
        nums?.let {
            val split = nums.split(" ")
            holder.getView<TagFlowLayout>(R.id.lot_history_item_nums_fl).adapter =
                object : TagAdapter<String>(split) {
                    override fun getView(parent: FlowLayout?, position: Int, num: String?): View {
                        return ball(position, num)
                    }
                }
        }
    }

    override fun convert(
        holder: BaseViewHolder,
        item: HistoryData.HistoryItem,
        payloads: List<Any>,
    ) {
        holder.setText(R.id.lot_history_item_label_tv, labelText(item))
        val nums = item.nums
        nums?.let {
            val split = nums.split(" ")
            holder.getView<TagFlowLayout>(R.id.lot_history_item_nums_fl).adapter =
                object : TagAdapter<String>(split) {
                    override fun getView(parent: FlowLayout?, position: Int, num: String?): View {
                        return ball(position, num)
                    }
                }
        }
    }

    fun onBetChange(parentPlayId: Int, brightLists: String) {
        this.parentPlayId = parentPlayId
        this.brightLists = brightLists
        notifyItemRangeChanged(0, itemCount, PAYLOAD_LABEL)
    }
}