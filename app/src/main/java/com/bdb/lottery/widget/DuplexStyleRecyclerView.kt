package com.bdb.lottery.widget

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import com.bdb.lottery.R

class DuplexStyleRecyclerView @JvmOverloads constructor(
    private var groupCount: Int = 0,
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.styleLoadingLayout
) : RecyclerView(context, attrs, defStyleAttr) {


}