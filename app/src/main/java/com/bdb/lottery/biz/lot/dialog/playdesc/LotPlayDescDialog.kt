package com.bdb.lottery.biz.lot.dialog.playdesc

import android.os.Bundle
import android.text.Html
import android.text.method.ScrollingMovementMethod
import android.view.View
import androidx.fragment.app.FragmentManager
import com.bdb.lottery.R
import com.bdb.lottery.base.dialog.BaseDialog
import com.bdb.lottery.const.GAME
import com.bdb.lottery.utils.ui.size.Sizes
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.android.synthetic.main.lot_dialog.*
import kotlinx.android.synthetic.main.lot_play_desc_dialog.*
import javax.inject.Inject

@ActivityScoped
@AndroidEntryPoint
class LotPlayDescDialog @Inject constructor() : BaseDialog(R.layout.lot_play_desc_dialog) {
    private val LOT_PLAY_DESC_TAG = "lot_dialog_tag"
    override var mWidth = 280f

    private var mGameType: Int = 0
    private var mPlayDescContent: String? = null

    fun setGameType(gameType: Int) {
        mGameType = gameType
    }

    fun setPlayDescContent(playDescContent: String) {
        mPlayDescContent =
            playDescContent.replace("\n", "<br/>").replace("/n", "<br/>").replace("\n", "<br/>")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lot_play_desc_close_iv.setOnClickListener {
            dismiss()
        }
        lot_play_desc_content_tv.text = Html.fromHtml(mPlayDescContent)
        lot_play_desc_content_tv.movementMethod = ScrollingMovementMethod.getInstance()
        //根据彩种大类设置title颜色
        lot_play_desc_title_view.setBackgroundResource(when (mGameType) {
            GAME.TYPE_GAME_K3 -> R.drawable.lot_play_desc_dialog_green_title_bg
            GAME.TYPE_GAME_PK8, GAME.TYPE_GAME_PK10 -> R.drawable.lot_play_desc_dialog_yellow_title_bg
            else -> R.drawable.lot_play_desc_dialog_blue_title_bg
        })
    }

    fun show(manager: FragmentManager) {
        super.show(manager, LOT_PLAY_DESC_TAG)
    }
}