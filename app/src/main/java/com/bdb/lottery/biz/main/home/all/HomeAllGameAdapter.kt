package com.bdb.lottery.biz.main.home.all

import android.util.SparseArray
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdb.lottery.R
import com.bdb.lottery.datasource.game.data.AllGameDataMapper
import com.bdb.lottery.datasource.game.data.AllGameItemData
import com.bdb.lottery.module.application.AppEntries
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.chad.library.adapter.base.listener.OnItemClickListener
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import dagger.hilt.android.EntryPointAccessors
import net.cachapa.expandablelayout.ExpandableLayout

class HomeAllGameAdapter(datas: MutableList<AllGameDataMapper>?) :
    BaseQuickAdapter<AllGameDataMapper, BaseViewHolder>(
        R.layout.home_allgame_sub_item,
        datas
    ),
    OnItemChildClickListener {

    //region 二级点击
    private var mOnSubItemClickListener: OnItemClickListener? = null
    fun setOnSubItemClickListener(onSubItemClickListener: OnItemClickListener) {
        mOnSubItemClickListener = onSubItemClickListener
    }
    //endregion

    //region 填充一级列表
    private var expandLayouts = SparseArray<ExpandableLayout>()
    private var imageViews = SparseArray<ImageView>()
    private var recyclerViews = SparseArray<RecyclerView>()
    private val tGame by lazy {
        EntryPointAccessors.fromApplication(context, AppEntries::class.java).provideTGame()
    }

    override fun convert(holder: BaseViewHolder, item: AllGameDataMapper) {
        val position = holder.adapterPosition
        val expandableLayout = holder.getView<ExpandableLayout>(R.id.home_allgame_item_epdl)
        val imageView = holder.getView<ImageView>(R.id.home_allgame_divide_iv)
        val recyclerView = holder.getView<RecyclerView>(R.id.home_allgame_rv)
        expandLayouts.put(position, expandableLayout)
        imageViews.put(position, imageView)
        recyclerViews.put(position, recyclerView)
        item.leftGameType?.let {
            if (it > 0)
                holder.setImageResource(
                    R.id.home_allgame_left_ariv,
                    tGame.gameTypeRoundDr(it)
                )
        }

        item.rightGameType?.let {
            if (it > 0)
                holder.setImageResource(
                    R.id.home_allgame_right_ariv,
                    tGame.gameTypeRoundDr(it)
                )
        }

        if (null == recyclerView.adapter) {
            recyclerView.setHasFixedSize(true)
            val adapter = HomeAllGameSubAdapter(null)
            adapter.setOnItemClickListener(mOnSubItemClickListener)

            recyclerView.adapter = adapter
        }
    }
    //endregion

    init {
        addChildClickViewIds(R.id.home_allgame_left_ariv, R.id.home_allgame_right_ariv)
        setOnItemChildClickListener(this)
    }

    //region 一级点击
    private var expandIndex = -1
    private var isLeft = true

    //当前二级列表是一级左侧、还是右侧展开
    fun isLeft(): Boolean {
        return isLeft
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
                    renderSubRecyclerView(imageView, recyclerView, null, position, isLeft)
                }
            } else {
                isLeft = left
                //关闭之前expandIndex(expandIndex>=0)
                if (expandIndex >= 0) {
                    expandLayouts[expandIndex].collapse()
                }

                //打开现在position
                expandableLayout.clearAnimation()
                renderSubRecyclerView(imageView, recyclerView, expandableLayout, position, isLeft)
                expandableLayout.expand(true)
                expandableLayout.setInterpolator(LinearInterpolator())
                expandIndex = position
            }
        }
    }
    //endregion

    //region 填充二级列表
    private fun renderSubRecyclerView(
        imageView: ImageView,
        recyclerView: RecyclerView,
        expandableLayout: ExpandableLayout?,
        position: Int,
        isLeft: Boolean,
    ) {
        imageView.setImageResource(if (isLeft) R.drawable.home_allgame_divide_left else R.drawable.home_allgame_divide_right)
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        (recyclerView.adapter as BaseQuickAdapter<AllGameItemData, BaseViewHolder>).setList(
            data.get(position).run { if (isLeft) leftData else rightData }
                ?.toMutableList().apply {
                    expandableLayout?.duration = this?.let {
                        val count = (it.size + 1) / 2
                        if (count <= 3) 100 else count * 100 / 3
                    } ?: 100
                })
    }
    //endregion
}