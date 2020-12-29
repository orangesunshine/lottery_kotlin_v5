package com.bdb.lottery.widget

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

class DanStyleInputEditView(context: Context, attrs: AttributeSet?) :
    AppCompatEditText(context, attrs) {
    init {
        inputType = InputType.TYPE_CLASS_NUMBER
        isSingleLine = false
        setHorizontallyScrolling(false)
    }
}