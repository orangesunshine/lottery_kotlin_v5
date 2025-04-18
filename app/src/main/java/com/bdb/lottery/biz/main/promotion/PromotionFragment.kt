package com.bdb.lottery.biz.main.promotion

import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseFragment
import com.bdb.lottery.const.GAME
import com.bdb.lottery.extension.isSpace
import com.bdb.lottery.utils.lot.Lots
import com.bdb.lottery.utils.ui.size.Sizes
import com.bdb.lottery.utils.ui.toast.ActivityToast
import com.bdb.lottery.utils.ui.toast.SystemToast
import com.bdb.lottery.utils.ui.toast.WindowManagerToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.main_promotion_fragment.*
import javax.inject.Inject
import kotlin.random.Random

//优惠
@AndroidEntryPoint
class PromotionFragment : BaseFragment(R.layout.main_promotion_fragment) {
    @Inject
    lateinit var toast1: SystemToast

    @Inject
    lateinit var toast2: ActivityToast

    @Inject
    lateinit var toast3: WindowManagerToast

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btn1.setOnClickListener {
            toast1.showError("haha")
        }
        btn2.setOnClickListener {
            toast2.showSuccess("haha\nhah")
        }
        btn3.setOnClickListener {
            toast3.showWarning("haha\nhah\nhah")
        }
//        promotion_root.addView(ball(GAME.TYPE_GAME_SSC,0,"0",10))
    }

    private fun ball(gameType: Int, gameId: Int, num: String?, position: Int): TextView {
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
//        textView.includeFontPadding = false
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize.toFloat())
        textView.setTextColor(Color.parseColor("#333333"))
        val str = if (position + 1 == 10) "*" else (position + 1).toString()
        return textView
    }

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
}