package com.bdb.lottery.biz.main.home.all

import android.util.SparseArray
import android.view.View
import android.view.ViewGroup
import com.bdb.lottery.R
import com.bdb.lottery.datasource.game.data.AllGameDataMapper
import com.bdb.lottery.utils.Games
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import net.cachapa.expandablelayout.ExpandableLayout

class HomeAllGameAdapter(datas: MutableList<AllGameDataMapper>?) :
    BaseQuickAdapter<AllGameDataMapper, BaseViewHolder>(R.layout.home_allgame_item, datas) {
    private var expandIndex = -1
    private var isLeft = true
    private var expandLayouts = SparseArray<ExpandableLayout>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val holder = super.onCreateViewHolder(parent, viewType)
        val adapterPosition = holder.adapterPosition
        expandLayouts.put(adapterPosition, holder.getView(R.id.home_allgame_item_epdl))
        return holder
    }

    override fun convert(holder: BaseViewHolder, item: AllGameDataMapper) {
        val position = holder.adapterPosition
        if (item.leftGameType > 0)
            holder.setImageResource(
                R.id.home_allgame_left_ariv,
                Games.gameTypeDrawable(item.leftGameType)
            )

        if (item.rightGameType > 0)
            holder.setImageResource(
                R.id.home_allgame_right_ariv,
                Games.gameTypeDrawable(item.rightGameType)
            )


    }

    init {
        addChildClickViewIds(R.id.home_allgame_left_ariv,R.id.home_allgame_right_ariv)
        setOnItemChildClickListener { adapter: BaseQuickAdapter<*, *>, view: View, position: Int ->
            val left = view.id == R.id.home_allgame_left_ariv
            val right = view.id == R.id.home_allgame_right_ariv
            if (left || right) {
                //ExpandableLayout
                if (expandIndex == position) {
                    if ((isLeft && left) || (!isLeft && right)) {
                        //关闭
                        expandLayouts[position].collapse(true)
                    } else if (left) {
                        //右侧改左侧
                        isLeft = true
                    } else if (right) {
                        //左侧改右侧
                        isLeft = false
                    }
                } else {
                    isLeft == left
                    //关闭之前expandIndex(expandIndex>=0)
                    if (expandIndex >= 0) {
                        expandLayouts[expandIndex].collapse()
                    }

                    //打开现在position
                    expandLayouts[position].expand()
                    expandIndex = position
                }
            } else {
                //彩种跳转
            }
        }
    }
}