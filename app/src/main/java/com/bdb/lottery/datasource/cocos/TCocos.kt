package com.bdb.lottery.datasource.cocos

import android.webkit.JavascriptInterface
import android.webkit.WebView
import com.bdb.lottery.const.GAME
import com.bdb.lottery.datasource.cocos.data.CocosData
import com.bdb.lottery.datasource.cocos.data.LoadCocosParams
import com.bdb.lottery.datasource.lot.data.countdown.CountDownData
import com.bdb.lottery.extension.equalsNSpace
import com.bdb.lottery.utils.file.Files
import com.bdb.lottery.utils.gson.Gsons
import com.bdb.lottery.utils.regex.Regexs
import com.bdb.lottery.utils.sdcard.SDCards
import com.bdb.lottery.utils.webview.WebViews
import org.zeroturnaround.zip.ZipUtil
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TCocos @Inject constructor() {
    //region 批量下载cocos所有文件大小

    //region 单个cocos文件下载
    //单个cocos文件条件1.本地文件校验失败，2.内存空间足够下载
    fun checkCocos(
        cocosDownloadPath: String,
        cocos: CocosData.CocosDataItem,
    ): Boolean {
        val checkCocosLocal = checkCocosLocal(cocosDownloadPath, cocos)
        val checkStorage = checkStorage(cocos)
        return !checkCocosLocal && checkStorage
    }

    //sd内存足够单个下载cocos文件
    private fun checkStorage(cocos: CocosData.CocosDataItem): Boolean {
        return SDCards.getExternalAvailableSize() > cocos.androidDirSize
    }
    //endregion

    //region 验证cocos文件已存在
    //cocos本地文件校验
    private fun checkCocosLocal(
        cocosDownloadPath: String,
        cocos: CocosData.CocosDataItem,
    ): Boolean {
        val url = cocos.androidZipUrl
        if (!Regexs.isURL(url)) {
            Timber.d("cocos下载url格式错误")
            return false//url格式不对
        }
        val size = cocos.androidDirSize//解压后文件大小
        val decompressDir = cocosDecompressDirFullName(cocosDownloadPath, cocos.name)
        //1.校验cocos解压文件夹存在及大小；2.判断cocos压缩存在及大小，并解压压缩包后，校验解压目录大小
        val checkDecompressDir = checkDecompressDir(decompressDir, size)
        return if (checkDecompressDir) true else {
            val md5 = cocos.androidZipMD5//zip文件md5值
            val filePath = cocosZipFileFullName(cocosDownloadPath, cocos.name)
            val checkZipFile = checkZipFile(filePath, md5)
            val decompressZip = decompressZip(filePath, decompressDir, size)
            checkZipFile && decompressZip
        }
    }

    //验证压缩文件md5值
    private fun checkZipFile(filePath: String, md5: String): Boolean {
        val file = Files.getFileByPath(filePath)
        Timber.d("!Files.isFileExists(file): ${!Files.isFileExists(file)}")
        if (!Files.isFileExists(file)) return false//文件不存在、或md5值不等
        Timber.d(
            "!Files.getFileMD5ToString(file).equalsNSpace(md5): ${
                !Files.getFileMD5ToString(file).equalsNSpace(md5)
            }"
        )
        if (!Files.getFileMD5ToString(file).equalsNSpace(md5)) {//文件存在，md5值不相等
            Files.delete(file)
            return false
        }
        return true
    }

    //验证解压目录大小
    private fun checkDecompressDir(decompressDir: String, size: Int): Boolean {
        val dir = Files.getFileByPath(decompressDir)
        Timber.d("!Files.createOrExistsDir(dir): ${!Files.createOrExistsDir(dir)}")
        if (!Files.createOrExistsDir(dir)) return false//文件夹不存或创建失败
        val length = Files.getLength(dir)
        Timber.d("Files.getLength(dir) != size.toLong(): ${length != size.toLong()}")
        if (length != size.toLong()) {//文件夹存在，大小不一致
            Files.deleteAllInDir(dir)
            return false
        }
        return true
    }
    //endregion

    //region 解压cocos文件
    fun decompressZip(cocosDownloadPath: String, cocos: CocosData.CocosDataItem): Boolean {
        return decompressZip(
            cocosZipFileFullName(cocosDownloadPath, cocos.name),
            cocosDecompressDirFullName(cocosDownloadPath, cocos.name),
            cocos.androidDirSize
        )
    }

    private fun decompressZip(filePath: String, decompressDir: String, size: Int): Boolean {
        //1.sd卡内存足够解压
        return if (checkStorageEnough4Decompress(size)) {
            try {
                //2.解压文件
                ZipUtil.unpack(File(filePath), File(decompressDir))
                //3.解压文件夹大小校验
                checkDecompressDir(decompressDir, size)
            } catch (e: Exception) {
                return false
            }
        } else false
    }
    //endregion

    //region sd卡剩余内存

    //验证sd卡剩余内存足够解压cocos文件
    private fun checkStorageEnough4Decompress(size: Int): Boolean {
        return SDCards.getExternalAvailableSize() >= size
    }
    //endregion

    //region cocos压缩文件全路径、解压目录全路径
    fun cocosZipFileFullName(cocosDownloadPath: String, name: String): String {
        return "$cocosDownloadPath$name.zip"
    }

    private fun cocosDecompressDirFullName(
        cocosDownloadPath: String,
        name: String,
    ): String {
        return cocosDownloadPath + "decompress" + File.separator + name
    }

    //获取加载cocos本地路径url
    private fun cocosLoadUrl(cocosDownloadPath: String, name: String): String {
        return "file:///mnt/sdcard/" + cocosDecompressDirFullName(
            cocosDownloadPath,
            name
        ).let { it.substring(it.indexOf("Android")) + File.separator + "index.html" }
    }
    //endregion

    //根据大类、id判断cocos动画存在
    private val cocosGameIds = arrayOf(19, 40, 208, 209, 212)
    fun hasCocosAnim(gameType: Int, gameId: Int): Boolean {
        return GAME.TYPE_GAME_K3 == gameType || GAME.TYPE_GAME_SSC == gameType ||
                (GAME.TYPE_GAME_PK10 == gameType && cocosGameIds.contains(gameId))
    }

    //根据大类、id获取cocos名称
    fun cocosName(gameType: Int, gameId: Int): String {
        return when (gameType) {
            GAME.TYPE_GAME_PK10 -> {
                when (gameId) {
                    19 -> "pk10"
                    40 -> "boat"
                    208 -> "f1"
                    209 -> "motor-boat"
                    212 -> "super-car"
                    else -> ""
                }
            }
            GAME.TYPE_GAME_K3 -> "k3"
            GAME.TYPE_GAME_SSC -> "ssc"
            else -> ""
        }
    }

    //region js调用：cocos初始化完成回调、传递参数
    fun initCocosEngine(
        cocosDownloadPath: String,
        name: String,
        webView: WebView,
        init: (() -> Unit)? = null,
    ) {
        webView.loadUrl(cocosLoadUrl(cocosDownloadPath, name))
        val cocosH5: Cocos2H5 = object : TCocos.Cocos2H5() {
            @JavascriptInterface
            override fun onLoadDataCallback() {
                init?.invoke()
            }
        }
        webView.addJavascriptInterface(cocosH5, "AndroidMessage")
    }

    internal abstract class Cocos2H5 {
        @JavascriptInterface
        abstract fun onLoadDataCallback()
    }

    //cocos动画加载cb参数
    fun cocosLoadData(
        webView: WebView, openNums: String?,
        issue: String?,
        openTime: String?,
        name: String?,
    ) {
        WebViews.loadUrl(
            webView,
            "callFromJava",
            Gsons.toJson(LoadCocosParams(openNums, issue, openTime, name))//生成cocos动画请求参数
        )
    }
    //endregion

    //region cocos倒计时
    fun cocosCountDown(webView: WebView, currentTime: CountDownData.CurrentTime) {
        WebViews.loadUrl(
            webView,
            "callFromJavaShowTime",
            Gsons.toJson(currentTime.genCountDownBean()))
    }
    //endregion
}