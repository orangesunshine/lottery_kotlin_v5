package com.bdb.lottery.biz.main.home.all

import android.util.Log
import android.util.SparseArray
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdb.lottery.R
import com.bdb.lottery.datasource.game.data.AllGameDataMapper
import com.bdb.lottery.datasource.game.data.AllGameItemData
import com.bdb.lottery.utils.Games
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import net.cachapa.expandablelayout.ExpandableLayout

class HomeAllGameAdapter(datas: MutableList<AllGameDataMapper>?) :
    BaseQuickAdapter<AllGameDataMapper, BaseViewHolder>(
        R.layout.home_allgame_lotterytype_item,
        datas
    ),
    OnItemChildClickListener, OnItemClickListener {
    private var expandIndex = -1
    private var isLeft = true
    private var expandLayouts = SparseArray<ExpandableLayout>()
    private var imageViews = SparseArray<ImageView>()
    private var recyclerViews = SparseArray<RecyclerView>()

    override fun convert(holder: BaseViewHolder, item: AllGameDataMapper) {
        val position = holder.adapterPosition
        val expandableLayout = holder.getView<ExpandableLayout>(R.id.home_allgame_item_epdl)
        val imageView = holder.getView<ImageView>(R.id.home_allgame_divide_iv)
        val recyclerView = holder.getView<RecyclerView>(R.id.home_allgame_rv)
        expandLayouts.put(position, expandableLayout)
        imageViews.put(position, imageView)
        recyclerViews.put(position, recyclerView)
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

        if (null == recyclerView.adapter) {
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter =
                HomeAllGameItemAdapter(null).apply {
                    setOnItemClickListener(this@HomeAllGameAdapter)
                }
        }
    }

    init {
        addChildClickViewIds(R.id.home_allgame_left_ariv, R.id.home_allgame_right_ariv)
        setOnItemChildClickListener(this)
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        val expandableLayout = expandLayouts[position]
        val imageView = imageViews[position]
        val recyclerView = recyclerViews[position]
        val left = view.id == R.id.home_allgame_left_ariv
        val right = view.id == R.id.home_allgame_right_ariv
        if (left || right) {
            //ExpandableLayout
            if (expandIndex == position) {
                if ((isLeft && left) || (!isLeft && right)) {
                    //关闭
                    expandableLayout.collapse(true)
                    expandIndex = -1
                } else if (left || right) {
                    isLeft = !isLeft
                    renderRecyclerView(imageView, recyclerView, position, isLeft)
                }
            } else {
                isLeft = left
                //关闭之前expandIndex(expandIndex>=0)
                if (expandIndex >= 0) {
                    expandLayouts[expandIndex].collapse()
                }

                //打开现在position
                expandableLayout.clearAnimation()
                expandableLayout.expand(true)
                renderRecyclerView(imageView, recyclerView, position, isLeft)
                expandableLayout.setInterpolator(LinearInterpolator())
                expandIndex = position
            }
        }
    }

    override fun onItemClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        Log.e("younger", "onItemClick")
    }

    fun renderRecyclerView(
        imageView: ImageView,
        recyclerView: RecyclerView,
        position: Int,
        isleft: Boolean
    ) {
        imageView.setImageResource(if (isLeft) R.drawable.home_allgame_divide_left else R.drawable.home_allgame_divide_right)
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        (recyclerView.adapter as BaseQuickAdapter<AllGameItemData, BaseViewHolder>).setList(
            data.get(position).run { if (isleft) leftData else rightData }
                ?.toMutableList())
    }
}