package com.bdb.lottery.biz.lot.jd.single

import android.text.Editable
import android.widget.EditText
import com.bdb.lottery.utils.adapterPattern.TextWatcherAdapter
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

class SingleTextWatcher constructor(
    private val mEtText: EditText,
    private val singleNumCount: Int,
    private val lotType: Int,
    private var playId: Int,
    private val digits: String?,
    private val noteCountBlock: (Int) -> Unit,
    private val loading: () -> Unit,
    private val dismissLoading: () -> Unit,
    private val error: (String?) -> Unit,
) :
    TextWatcherAdapter() {
    private var end = false
    private var watcher = true
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        end = s?.let { it.length == mEtText.selectionEnd } ?: false
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
        overBlock: ((String) -> Unit)? = null,
    ) {
        if (!end && fromInput) return
        if (text.length < singleNumCount || singleNumCount == 0) {
            noteCountBlock(0)
            return
        }
        val needLoading = text.length > 10000
        Observable.just(text)
            .subscribeOn(AndroidSchedulers.mainThread())
            .apply {
                if (needLoading) {
                    doOnSubscribe {
                        loading()
                    }
                }
            }
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
                        } else buff.toString(), lotType, playId, digits, fromInput
                    )
                } catch (e: Exception) {
                    if (!fromInput) throw e else -1
                }
                if (-1 != noteCount)
                    noteCountBlock(noteCount)
                if (needLoading || !fromInput) BetCenter.newNums else buff.toString()
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                watcher = false
                if (needLoading) mEtText.text.clear()
                mEtText.setText(it)
                val length = it?.length ?: 0
                mEtText.setSelection(length)
                overBlock?.invoke(it?.let { if (length > 200) it.substring(0, 200) + "..." else it }
                    ?: "")
            }, { if (!fromInput) error(it.message) }, { if (needLoading) dismissLoading() })
    }

    fun setPlayId(playId: Int) {
        this.playId = playId
    }
}