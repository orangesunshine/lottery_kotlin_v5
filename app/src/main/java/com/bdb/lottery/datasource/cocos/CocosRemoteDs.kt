package com.bdb.lottery.datasource.cocos

import android.content.Context
import com.bdb.lottery.const.URL
import com.bdb.lottery.datasource.cocos.data.CocosData
import com.bdb.lottery.extension.equalsNSpace
import com.bdb.lottery.utils.cache.TCache
import com.bdb.lottery.utils.file.Files
import com.bdb.lottery.utils.file.TPath
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadQueueSet
import com.liulishuo.filedownloader.FileDownloader
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject


class CocosRemoteDs @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cocosApi: CocosApi,
    private val tPath: TPath,
    private val tCache: TCache,
    private val tCocos: TCocos
) {

    //cocos配置优先缓存
    private fun cachePriCocosConfig(sucess: (CocosData?, String) -> Unit) {
        val cocosDownloadPath = tPath.cocosDownloadPath()
        if (Files.createOrExistsDir(cocosDownloadPath)) {
            //存在或创建cocos目录
            val cache = tCache.cocosConfigCache()
            //cocos配置缓存
            if (null != cache) {
                sucess(cache, cocosDownloadPath!!)
            } else {
                //网络下载cocos配置
                val alreadyConfig = AtomicBoolean(false)
                val cocosObservables = mutableListOf<Observable<CocosData?>>()
                URL.ULR_COCOS_CONFIG.forEach { cocosObservables.add(cocosApi.cocosConfig(it)) }
                var dispose: Disposable? = null
                Observable.mergeArrayDelayError(*(cocosObservables.toTypedArray()))
                    .doOnSubscribe { dispose = it }
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe {
                        it?.let {
                            if (!it.isEmpty()) {
                                if (alreadyConfig.compareAndSet(false, true)) {
                                    Timber.d("alreadyConfig")
                                    dispose?.isDisposed
                                    sucess(it, cocosDownloadPath!!)
                                }
                            }
                        }
                    }
            }
        }
    }

    //下载全部cocos文件
    fun downloadAllCocos() {
        Timber.d("downloadAllCocos")
        cachePriCocosConfig { data: CocosData?, path: String ->
            data?.let {
                FileDownloader.setup(context)
                val queueSet =
                    FileDownloadQueueSet(FileDownloadListenerAdapter())
                val tasks: MutableList<BaseDownloadTask> = ArrayList()
                it.forEach {
                    tCocos.clearSize()
                    if (tCocos.checkCocosBatchDownload(path, it)) {
                        tasks.add(
                            FileDownloader.getImpl().create(it.androidZipUrl)
                                .setTag(it)
                                .setPath(tCocos.cocosZipFileFullName(path, it))
                        )
                    }
                }
                queueSet.disableCallbackProgressTimes().setAutoRetryTimes(1)
                    .downloadTogether(tasks)
                    .addTaskFinishListener {
                        val cocos = it.tag as CocosData.CocosDataItem
                        tCocos.decompressZip(path, cocos)
                    }.start()
            }
        }
    }

    /**
     * @param cocosName 单个cocos文件名
     * @param progress 下载进度
     */
    fun downloadSingleCocos(
        cocosName: String,
        progress: ((soFarBytes: Int, totalBytes: Int, speed: Int) -> Unit)? = null,
        success: (() -> Unit)? = null
    ) {
        Timber.d("downloadCocos")
        cachePriCocosConfig { data: CocosData?, path: String ->
            data?.let {
                FileDownloader.setup(context)
                it.forEach {
                    if (it.name.equalsNSpace(cocosName)) {
                        if (tCocos.checkCocosSingleDownload(path, it)) {
                            FileDownloader.getImpl()
                                .create(it.androidZipUrl)
                                .setPath(path)
                                .setCallbackProgressMinInterval(200)
                                .setListener(object : FileDownloadListenerAdapter() {
                                    override fun progress(
                                        task: BaseDownloadTask?,
                                        soFarBytes: Int,
                                        totalBytes: Int
                                    ) {
                                        progress?.invoke(soFarBytes, totalBytes, task?.speed ?: -1)
                                    }
                                })
                                .addFinishListener {
                                    val cocos = it.tag as CocosData.CocosDataItem
                                    if (tCocos.decompressZip(path, cocos)) {
                                        success?.invoke()
                                    }
                                }.start()
                        }
                        return@forEach
                    }
                }
            }
        }
    }
}