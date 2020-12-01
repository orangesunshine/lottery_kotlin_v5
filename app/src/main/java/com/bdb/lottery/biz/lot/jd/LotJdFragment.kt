package com.bdb.lottery.biz.lot.jd

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.view.View
import androidx.fragment.app.viewModels
import com.bdb.lottery.R
import com.bdb.lottery.base.ui.BaseFragment
import com.bdb.lottery.utils.adapterPattern.TextWatcherAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.lot_jd_fragment.*
import timber.log.Timber

@AndroidEntryPoint
class LotJdFragment : BaseFragment(R.layout.lot_jd_fragment) {
    private val vm by viewModels<LotJdViewModel>()

    //vars
    private var singleNumCount: Int = 5//单注号码数
    private val mTextWatcher = object : TextWatcherAdapter() {
        var end = false
        var inputBlank = true
        var watcher = true
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            end = s?.let { it.length == lot_jd_single_input_et.selectionEnd } ?: false
        }

        override fun afterTextChanged(s: Editable?) {
            if (!watcher) {
                watcher = true
                return
            }
            val blank = TextUtils.isEmpty(s)
            if (inputBlank != blank) {
                //输入或清空
                inputBlank = blank
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lot_jd_remove_repeat_nums_tv.setOnClickListener {
            mTextWatcher.repeatNdErrorNums(lot_jd_single_input_et.text.toString().trim().replace(",", ""),
                singleNumCount)
        }

        lot_jd_single_input_et.addTextChangedListener(mTextWatcher)
    }
}