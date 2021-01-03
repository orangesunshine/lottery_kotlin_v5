package com.bdb.lottery.biz.lot.wt

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseFragment
import com.bdb.lottery.const.EXTRA
import dagger.hilt.android.AndroidEntryPoint

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