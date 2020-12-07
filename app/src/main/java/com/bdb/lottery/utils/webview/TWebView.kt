package com.bdb.lottery.utils.webview

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Message
import android.view.ViewGroup
import android.webkit.*
import android.webkit.WebView.WebViewTransport
import com.bdb.lottery.extension.start
import com.bdb.lottery.utils.ui.activity.Activitys
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class TWebView @Inject constructor(@ActivityContext val context: Context) {
    //WebView打开文件请求码
    val REQUEST_CODE_SHOW_FILE_CHOOSE = 1

    fun initConfig(
        webView: WebView,
        onPageFinishedProcess: (() -> Unit)? = null
    ) {
        val webSettings = webView.settings
        webSettings.domStorageEnabled = true
        webSettings.javaScriptEnabled = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.allowFileAccessFromFileURLs = true
            webSettings.allowUniversalAccessFromFileURLs = true
        }
        webSettings.pluginState = WebSettings.PluginState.ON
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH) //提高渲染的优先级

        webSettings.loadWithOverviewMode = true
//            webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        //            webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.allowFileAccess = true // 设置允许访问文件数据

        webSettings.useWideViewPort = true
        webSettings.blockNetworkImage = false //同步请求图片

        webSettings.setNeedInitialFocus(true) //当webview调用requestFocus时为webview设置节点

        webSettings.loadsImagesAutomatically = true //支持自动加载图片

//        webSettings.setSupportMultipleWindows(true);
        //html中的_bank标签就是新建窗口打开，有时会打不开，需要加以下
        //然后 复写 WebChromeClient的onCreateWindow方法
        //        webSettings.setSupportMultipleWindows(true);
        //html中的_bank标签就是新建窗口打开，有时会打不开，需要加以下
        //然后 复写 WebChromeClient的onCreateWindow方法
        webSettings.setSupportMultipleWindows(false)
        webSettings.javaScriptCanOpenWindowsAutomatically = true //支持通过JS打开新窗口

        //设置支持Cookie
        //设置支持Cookie
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(webView, true)
        }
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    view.loadUrl(request.url.toString())
                } else {
                    view.loadUrl(request.toString())
                }
                return true
            }

            override fun onReceivedSslError(
                view: WebView,
                handler: SslErrorHandler,
                error: SslError
            ) {
                handler.proceed()
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                onPageFinishedProcess?.invoke()
            }
        }
    }

    inline fun <reified T : Activity> fileChoose(
        webView: WebView,
        noinline callback: ((ValueCallback<Array<Uri>>) -> Unit)? = null
    ) {
        webView.webChromeClient = object : WebChromeClient() {
            override fun onCreateWindow(
                view: WebView,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message
            ): Boolean {
                val wvNew = WebView(context)
                wvNew.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                        destroyWebView(wvNew)
                        if (context is Activity && Activitys.isActivityAlive(context))
                            context.start<T>()
                        return true
                    }

                    override fun onReceivedSslError(
                        view: WebView,
                        handler: SslErrorHandler,
                        error: SslError
                    ) {
                        handler.proceed()
                    }
                }
                val transport = resultMsg.obj as WebViewTransport
                transport.webView = wvNew
                resultMsg.sendToTarget()
                return true
            }

            override fun onShowFileChooser(
                webView: WebView,
                filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                callback?.invoke(filePathCallback)
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                intent.type = "image/*"
                if (context is Activity && Activitys.isActivityAlive(context)) {
                    context.startActivityForResult(
                        Intent.createChooser(intent, "图片选择"),
                        REQUEST_CODE_SHOW_FILE_CHOOSE
                    )
                }
                return true
            }
        }
    }

    fun destroyWebView(webView: WebView) {
        webView.loadUrl("about:blank")
        val parent = webView.parent
        if (null != parent && parent is ViewGroup) parent.removeView(webView)
        webView.clearHistory()
        webView.removeAllViews()
        webView.clearCache(true)
        webView.destroy()
    }

    fun back(webView: WebView): Boolean {
        return webView.canGoBack().also { if (it) webView.goBack() }
    }

    fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        callback: ValueCallback<Array<Uri>>?
    ) {
        if (requestCode == REQUEST_CODE_SHOW_FILE_CHOOSE) {
            callback?.let {
                val result =
                    if (data == null || resultCode != Activity.RESULT_OK) null else data.data
                if (result != null) {
                    it.onReceiveValue(arrayOf(result))
                } else {
                    it.onReceiveValue(null)
                }
            }
        }
    }
}