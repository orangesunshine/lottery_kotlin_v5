package com.bdb.lottery.utils.webview

import android.webkit.WebView
import timber.log.Timber

object WebViews {

    /**
     * webview 加载webview
     * @param methodName：js方法名称
     * @param paramsJson：js方法参数
     */
    fun loadUrl(webView: WebView, methodName: String, paramsJson: String) {
        webView.loadUrl(String.format("javascript:${methodName}('${paramsJson}')"))
    }
}