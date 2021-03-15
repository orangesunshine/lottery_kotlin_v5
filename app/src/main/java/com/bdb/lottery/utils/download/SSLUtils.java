package com.bdb.lottery.utils.download;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;

public class SSLUtils {

    /**
     * filedownload 使用okhttp作为下载引擎忽略掉SSL验证, 添加证书不好使
     *
     * @return
     */
    public static OkHttpClient.Builder createOkHttp() {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(20_000, TimeUnit.SECONDS);
        builder.sslSocketFactory(SSLUtils.createSSLSocketFactory(), new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        });
        builder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });

        return builder;
    }

    public static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory sSLSocketFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {
                    //do nothing，接受任意客户端证书
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType)
                        throws CertificateException {
                    //do nothing，接受任意服务端证书
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
            sSLSocketFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }

        return sSLSocketFactory;
    }
}
