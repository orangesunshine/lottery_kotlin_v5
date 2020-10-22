package com.bdb.lottery.utils.net.retrofit

import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * 动态指定baseurl exmaple
 */
class BaseUrlInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        //获取原始的originalRequest
        val originalRequest: Request = chain.request()
        //获取老的url
        val oldUrl: HttpUrl = originalRequest.url
        //获取originalRequest的创建者builder
        val builder: Request.Builder = originalRequest.newBuilder()
        //获取头信息的集合如：manage,mdffx
        val urlnameList: List<String> = originalRequest.headers("urlname")
        return if (urlnameList != null && urlnameList.size > 0) {
            //删除原有配置中的值,就是namesAndValues集合里的值
            builder.removeHeader("urlname")
            //获取头信息中配置的value,如：manage或者mdffx
            val urlname = urlnameList[0]
            var baseURL: HttpUrl? = null
            //根据头信息中配置的value,来匹配新的base_url地址
            // FIXME: 2019/5/30 此处可以根据实际需求 进行 baseURL的切换
            if ("common_base_url_header" == urlname) {
                baseURL = "host1".toHttpUrlOrNull()
            } else if ("old_php_base_url_header" == urlname) {
                //baseURL = HttpUrl.parse(App.getInstance().setBaseUrlOldPhp());
            } else if ("java" == urlname) {
                baseURL = "host2".toHttpUrlOrNull()
            }


            //重建新的HttpUrl，需要重新设置的url部分
            val newHttpUrl = oldUrl.newBuilder()
                .scheme(baseURL!!.scheme) //http协议如：http或者https
                .host(baseURL.host) //主机地址
                .port(baseURL.port) //端口
                .build()
            originalRequest.header("NoToken") ?: let {
                builder.addHeader("Cookie", "token")
                    .addHeader("Content-Type", "application/x-www-form-urlencoded")
                    .addHeader("X-Requested-With", "XMLHttpRequest")
            }

            //获取处理后的新newRequest
            val newRequest: Request = builder.url(newHttpUrl).build()
            chain.proceed(newRequest)
        } else {
            chain.proceed(originalRequest)
        }
    }
}