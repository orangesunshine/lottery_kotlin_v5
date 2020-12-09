package com.bdb.lottery.biz.lot.jd

import android.os.Bundle
import android.text.Editable
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import androidx.fragment.app.viewModels
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseFragment
import com.bdb.lottery.biz.lot.LotActivity
import com.bdb.lottery.const.EXTRA
import com.bdb.lottery.datasource.lot.data.jd.GameBetTypeData
import com.bdb.lottery.dialog.ConfirmDialog
import com.bdb.lottery.utils.adapterPattern.TextWatcherAdapter
import com.bdb.lottery.utils.ui.popup.ALIGN_ANCHOR
import com.bdb.lottery.utils.ui.popup.TPopupWindow
import com.bdb.lottery.utils.ui.size.Sizes
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.lot_jd_fragment.*
import javax.inject.Inject

@AndroidEntryPoint
class LotJdFragment : BaseFragment(R.layout.lot_jd_fragment) {
    private val vm by viewModels<LotJdViewModel>()

    @Inject
    lateinit var mConfirmDialog: ConfirmDialog

    @Inject
    lateinit var mTPopupWindow: TPopupWindow

    private val MODE_SINGLE = 0//单式
    private val MODE_DUPLEX = 1//复式
    private var mMode = MODE_SINGLE//模式
    private var mMultiple = 1//默认1倍
    private var mAmountUnit = 1//默认元
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lot_jd_single_input_et.addTextChangedListener(mTextWatcher)//监听单式输入框
        //删除重复、错误号码
        lot_jd_remove_repeat_nums_tv.setOnClickListener {
            mTextWatcher.repeatNdErrorNums(
                lot_jd_single_input_et.text.toString().trim()
                    .replace(",", ""),
                singleNumCount
            )
        }

        //清空号码
        clearNums()

        //投注参数
        //单位
        initAmountUnitPopWin()
        log_jd_money_unit_tv.setOnClickListener {
            mTPopupWindow.showAtScreenLocation(
                log_jd_money_unit_tv,
                Gravity.TOP or Gravity.START, -Sizes.dp2px(16f), -Sizes.dp2px(8f), ALIGN_ANCHOR
            )
        }

        lot_jd_multiple_et.setOnClickListener {}//倍数

        //下注
        lot_jd_direct_betting_tv.setOnClickListener {
            val multiple = lot_jd_multiple_et.text.toString().trim()
            aliveActivity<LotActivity>()?.lotByDialog(vm.mToken!!, multiple, null) {
                vm.mToken = it
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        vm.initGame(mGameId.toString())
        vm.getBetType(mGameId.toString())
    }

    override fun observe() {
        vm.mGameInitData.getLiveData().observe(this) {}
        vm.mGameBetTypeData.getLiveData().observe(this) { updatePlayMenu(it) }
    }

    //玩法菜单
    private fun updatePlayMenu(betTypeData: GameBetTypeData?) {
        if (!betTypeData.isNullOrEmpty()) {
            val lotActivity = aliveActivity<LotActivity>()
            lotActivity?.updatePlayLayer1List(betTypeData)
        }
    }

    //region 单式输入框
    private var singleNumCount: Int = 5//单注号码数
    private val mTextWatcher = object : TextWatcherAdapter() {
        var end = false
        var canPutBasket = true
        var watcher = true
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            end = s?.let { it.length == lot_jd_single_input_et.selectionEnd } ?: false
        }

        override fun afterTextChanged(s: Editable?) {
            if (!watcher) {
                watcher = true
                return
            }
            val oneNote = s?.length ?: 0 >= singleNumCount//一注
            if (canPutBasket != oneNote) {
                //输入或清空
                canPutBasket = oneNote
                lot_jd_add_to_shopping_bar_tv.text =
                    getString(if (oneNote) R.string.lot_jd_put_shopping_bar else R.string.lot_jd_shopping_bar)
                lot_jd_add_to_shopping_bar_tv.setTextSize(
                    TypedValue.COMPLEX_UNIT_DIP,
                    if (oneNote) 12f else 15f
                )
            }

            if (end) {
                s?.toString()?.replace(",", "")?.let {
                    if (it.length <= singleNumCount) return@let
                    repeatNdErrorNums(it, singleNumCount)
                }
            }
        }

        //删除错误重复号码
        fun repeatNdErrorNums(text: String, count: Int) {
            if (text.length <= count) return
            val buff = StringBuilder(text)
            var offset = count
            while (offset < buff.length && offset > 0) {
                buff.insert(offset, ",")
                offset += 1 + count
            }
            watcher = false
            val ret = buff.toString()
            lot_jd_single_input_et.setText(ret)
            lot_jd_single_input_et.setSelection(ret.length)
        }
    }
    //endregion

    //region 传递参数gameType、gameId、gameName
    companion object {
        fun newInstance(gameType: Int, gameId: Int, gameName: String?): LotJdFragment {
            val fragment = LotJdFragment()
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

    //region 金额单位popup
    private fun initAmountUnitPopWin() {
        mTPopupWindow.setPopWinWidth(100, true).content {
            val content = layoutInflater.inflate(R.layout.lot_jd_money_unit, null)
            val listener: (View) -> Unit = { view: View ->
                mTPopupWindow.dismiss()
                when (view.id) {
                    R.id.lot_jd_money_unit_yuan_tv -> mAmountUnit = 1
                    R.id.lot_jd_money_unit_jiao_tv -> mAmountUnit = 2
                    R.id.lot_jd_money_unit_fen_tv -> mAmountUnit = 3
                    R.id.lot_jd_money_unit_li_tv -> mAmountUnit = 4
                }
            }
            content.findViewById<View>(R.id.lot_jd_money_unit_yuan_tv).setOnClickListener(listener)
            content.findViewById<View>(R.id.lot_jd_money_unit_jiao_tv).setOnClickListener(listener)
            content.findViewById<View>(R.id.lot_jd_money_unit_fen_tv).setOnClickListener(listener)
            content.findViewById<View>(R.id.lot_jd_money_unit_li_tv).setOnClickListener(listener)
            content
        }
    }
    //endregion

    //region 清空号码
    private fun clearNums() {
        //清空确认弹窗
        mConfirmDialog.contentText("是否清除已选择号码").onConfirm {
            if (mMode == MODE_SINGLE) {
                //单式
                lot_jd_single_input_et.setText("")

            } else if (mMode == MODE_DUPLEX) {
                //复式
            }
        }
        lot_jd_input_clear_iv.setOnClickListener {
            mConfirmDialog.show(childFragmentManager)
        }
    }

    //endregion

    //region 下注倒计时状态更新
    private var mClosed = false
    fun updateStatus(closed: Boolean) {
        if (mClosed == closed) return
        val can = !closed//能否下注
        lot_jd_direct_betting_tv.text =
            getString(if (can) R.string.lot_jd_direct_betting else R.string.lot_jd_closed)
        lot_jd_direct_betting_tv.isEnabled = can
        mClosed = closed
    }
    //endregion
}