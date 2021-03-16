package com.bdb.lottery.biz.lot.activity

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bdb.lottery.R
import com.bdb.lottery.const.GAME
import com.bdb.lottery.extension.isSpace
import com.bdb.lottery.utils.lot.Lots
import com.bdb.lottery.utils.ui.size.Sizes
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter

class OpenBallAdapter constructor(
    private val context: Context,
    private val gameType: Int,
    private val gameId: Int,
    nums: String,
    private var lotPlace: String,
) : TagAdapter<String>(nums.split(" ")) {

    private fun ballSize(gameType: Int): Int {
        return Sizes.dp2px(
            when (gameType) {
                GAME.TYPE_GAME_PK10, GAME.TYPE_GAME_PK8 -> 17f
                GAME.TYPE_GAME_PC28, GAME.TYPE_GAME_LHC -> 21f
                else -> 20f
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

    fun onBetChange(lotPlace: String) {
        this.lotPlace = lotPlace
        notifyDataChanged()
    }

    override fun getView(parent: FlowLayout?, position: Int, num: String?): View {
        return ball(num, position)
    }

    private fun ball(num: String?, position: Int): TextView {
        var bgRes = -1
        var textSize = 11
        var content: String? = null
        val isSymbol = "+" == num || "=" == num
        when (gameType) {
            GAME.TYPE_GAME_SSC, GAME.TYPE_GAME_FREQUENCY_LOW, GAME.TYPE_GAME_KL10FEN, GAME.TYPE_GAME_11X5 -> {
                bgRes = R.drawable.lot_open_nums_white_circle_shape
                content = num
            }
            GAME.TYPE_GAME_PK10, GAME.TYPE_GAME_PK8 -> {//pk10,pk8
                textSize = 10
                bgRes = Lots.num2Ball4Pk(num)
            }
            GAME.TYPE_GAME_PC28 -> {//pc28
                textSize = 10
                bgRes = if (isSymbol) -1 else Lots.num2Dr4Pc28(num)
                content = num
            }
            GAME.TYPE_GAME_K3 -> {//快三
                bgRes = Lots.ball2Dr4K3New(num)
            }
            GAME.TYPE_GAME_LHC -> {//六合彩
                bgRes =
                    if (isSymbol) -1 else Lots.ball2Dr4LHC(
                        Lots.deleteInitZero(num),
                        74 == gameId
                    )
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
        (textView.layoutParams as ViewGroup.MarginLayoutParams).leftMargin =
            ballSpace(gameType)

        textView.gravity = Gravity.CENTER
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize.toFloat())
        textView.setTextColor(Color.parseColor("#333333"))
        val str = if (position + 1 == 10) "*" else (position + 1).toString()
        textView.alpha = if (lotPlace.isSpace() || lotPlace.contains(str)) 1f else 0.6f
        return textView
    }
}