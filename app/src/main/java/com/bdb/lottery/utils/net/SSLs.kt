package com.bdb.lottery.utils.net

import com.bdb.lottery.utils.net.retrofit.SsX509TrustManager
import timber.log.Timber
import java.security.SecureRandom
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory

object SSLs {
    fun allowAllSSL() {
        HttpsURLConnection.setDefaultHostnameVerifier { _, _ -> true }
        HttpsURLConnection.setDefaultSSLSocketFactory(getSSLSocketFactory(SsX509TrustManager))
    }

    fun getSSLSocketFactory(ssX509TrustManager: SsX509TrustManager): SSLSocketFactory {
        val sslContext = SSLContext.getInstance("TLS")
        try {
            sslContext.init(null, arrayOf(ssX509TrustManager), SecureRandom())
        } catch (e: Exception) {
            Timber.e("SSLs: %s", e)
        }
        return sslContext.socketFactory
    }
}