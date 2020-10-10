package com.bdb.lottery.biz.main

import android.os.Bundle
import com.bdb.lottery.R
import com.bdb.lottery.base.contract.IContract
import com.bdb.lottery.base.view.BaseActivity
import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter
import org.sufficientlysecure.htmltextview.HtmlTextView


class MainActivity: BaseActivity<IContract.IPresenter<IContract.IView>>() {
    override fun layoutId(): Int {
        return R.layout.activity_main
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val htmlTextView: HtmlTextView = findViewById(R.id.html_text)
        htmlTextView.setHtml(
            "<h2>Hello wold</h2><img src=\"http://ateststatis.appxianlu1.com//resources/new_kj/promotion/48903bd9502948b88158928ec986678b.png\"/>",
            HtmlHttpImageGetter(htmlTextView)
        );
        println()
    }
}