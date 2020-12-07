package com.bdb.lottery.biz.lot.wt

import android.os.Bundle
import android.view.View
import androidx.core.view.setPadding
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseFragment
import com.bdb.lottery.biz.lot.LotActivity
import com.bdb.lottery.biz.lot.jd.LotJdFragment
import com.bdb.lottery.const.EXTRA
import com.bdb.lottery.datasource.game.data.HomeFavoritesMapper
import com.bdb.lottery.extension.equalsNSpace
import com.bdb.lottery.extension.ob
import com.bdb.lottery.extension.startWithArgs
import com.bdb.lottery.utils.ui.size.Sizes
import com.bdb.lottery.utils.ui.size.TSize
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.recyclerview_single_layout.*
import javax.inject.Inject

@AndroidEntryPoint
class LotWtFragment : BaseFragment(R.layout.recyclerview_single_layout) {
    private val vm by viewModels<LotWtViewModel>()

    //region 传递参数gameType、gameId、gameName
    companion object {
        fun newInstance(gameType: Int, gameId: Int, gameName: String?): LotWtFragment {
            val fragment = LotWtFragment()
            val args = Bundle()
            args.putInt(EXTRA.ID_GAME_EXTRA, gameId)
            args.putInt(EXTRA.TYPE_GAME_EXTRA, gameType)
            args.putString(EXTRA.NAME_GAME_EXTRA, gameName)
            fragment.arguments = args
            return fragment
        }
    }

    private var mGameId: Int = -1
    private var mGameType: Int = -1
    private var mGameName: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mGameId = it.getInt(EXTRA.ID_GAME_EXTRA)
            mGameType = it.getInt(EXTRA.TYPE_GAME_EXTRA)
            mGameName = it.getString(EXTRA.NAME_GAME_EXTRA)
        }
    }
    //endregion
}