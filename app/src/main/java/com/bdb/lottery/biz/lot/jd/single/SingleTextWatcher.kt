package com.bdb.lottery.biz.lot.jd.single

import android.text.Editable
import android.widget.EditText
import com.bdb.lottery.biz.lot.BetCenter
import com.bdb.lottery.utils.adapterPattern.TextWatcherAdapter
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

class SingleTextWatcher constructor(
    private val mEtText: EditText,
    private var singleNumCount: Int,
    private val lotType: Int,
    private var playId: Int,
    private var digit: String?,
    private val noteCountBlock: (Int) -> Unit,
    private val error: (String?) -> Unit,
) :
    TextWatcherAdapter() {
    private var end = false
    private var watcher = true
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        end = s?.let { it.length == mEtText.selectionEnd } ?: false
    }

    fun setDigit(digit: String?) {
        this.digit = digit
        filterRepeatNdErrorNums(mEtText.text.toString().trim(), true)
    }

    fun onBetChange(singleNumCount: Int, digit: String?) {
        this.singleNumCount = singleNumCount
        this.digit = digit
    }

    fun setPlayId(playId: Int) {
        this.playId = playId
    }

    override fun afterTextChanged(s: Editable?) {
        if (!watcher) {
            watcher = true
            return
        }
        filterRepeatNdErrorNums(s.toString(), true)
    }

    fun filterRepeatNdErrorNums(
        text: String,
        fromInput: Boolean = true,
        lotDialogNumsBlock: ((String) -> Unit)? = null,
    ) {
        if (!end && fromInput) return
        if (text.length < singleNumCount || singleNumCount == 0) {
            noteCountBlock(0)
            return
        }
        Observable.just(text)
            .observeOn(Schedulers.computation())
            .map {
                val buff = StringBuilder(text.replace(",", ""))
                var offset = singleNumCount
                while (offset < buff.length && offset > 0) {
                    buff.insert(offset, ",")
                    offset += 1 + singleNumCount
                }
                val noteCount = try {
                    BetCenter.computeSingleAvailableBetCount(
                        if (!fromInput && buff.isNotEmpty() && buff.length - buff.lastIndexOf(",") <= singleNumCount) {
                            buff.substring(0, buff.lastIndexOf(","))
                        } else buff.toString(), lotType, playId, digit, fromInput
                    )
                } catch (e: Exception) {
                    if (!fromInput) throw e else -1
                }
                if (-1 != noteCount)
                    noteCountBlock(noteCount)
                if (!fromInput) BetCenter.newNums else buff.toString()
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                watcher = false
                if (!fromInput) mEtText.text.clear()
                val length = it?.length ?: 0
                mEtText.setText(it)
                mEtText.setSelection(length)
                lotDialogNumsBlock?.invoke(it?.let {
                    if (length > 200) it.substring(0,
                        200) + "..." else it
                }
                    ?: "")
            }) { if (!fromInput) error(it.message) }
    }
}