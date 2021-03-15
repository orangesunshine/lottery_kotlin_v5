package com.bdb.lottery.datasource.cocos

import android.content.Context
import com.bdb.lottery.const.URL
import com.bdb.lottery.datasource.cocos.data.CocosData
import com.bdb.lottery.extension.equalsNSpace
import com.bdb.lottery.extension.msg
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
    private val tCocos: TCocos,
) {

    //cocos配置优先缓存
    private fun cachePriCocosConfig(success: (CocosData, String) -> Unit) {
        val cocosDownloadPath = tPath.cocosDownloadPath()
        if (Files.createOrExistsDir(cocosDownloadPath)) {
            //存在或创建cocos目录
            val cache = tCache.cocosConfigCache()
            //cocos配置缓存
            if (null != cache) {
                success(cache, cocosDownloadPath!!)
            } else {
                //网络下载cocos配置
                val alreadyConfig = AtomicBoolean(false)
                val cocosObservables = mutableListOf<Observable<CocosData>>()
                URL.ULR_COCOS_CONFIG.forEach { cocosObservables.add(cocosApi.cocosConfig(it)) }
                var dispose: Disposable? = null
                Observable.mergeArrayDelayError(*(cocosObservables.toTypedArray()))
                    .doOnSubscribe { dispose = it }
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.io())
                    .subscribe {
                        if (!it.isEmpty()) {
                            if (alreadyConfig.compareAndSet(false, true)) {
                                Timber.d("alreadyConfig")
                                dispose?.isDisposed
                                tCache.cacheCocosConfig(it)
                                success(it, cocosDownloadPath!!)
                            }
                        }
                    }
            }
        }
    }

    //下载全部cocos文件
    fun downloadAllCocos() {
        cachePriCocosConfig { data: CocosData, path: String ->
            FileDownloader.setup(context)
            val queueSet =
                FileDownloadQueueSet(FileDownloadListenerAdapter())
            val tasks: MutableList<BaseDownloadTask> = ArrayList()
            data.forEach {
                if (tCocos.checkCocos(path, it)) {
                    tasks.add(
                        FileDownloader.getImpl().create(it.androidZipUrl)
                            .setTag(it)
                            .setPath(tCocos.cocosZipFileFullName(path, it.name))
                            .setListener(object : FileDownloadListenerAdapter() {
                                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                                    super.error(task, e)
                                    Timber.d(e.msg)
                                }
                            })
                    )
                }
            }
            if (tasks.isNotEmpty()) {
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
        success: ((String) -> Unit)? = null,
    ) {
        cachePriCocosConfig { data: CocosData, path: String ->
            FileDownloader.setup(context)
            data.forEach {
                if (it.name.equalsNSpace(cocosName)) {
                    if (tCocos.checkCocos(path, it)) {
                        FileDownloader.getImpl()
                            .create(it.androidZipUrl)
                            .setTag(it)
                            .setPath(tCocos.cocosZipFileFullName(path, it.name))
                            .setCallbackProgressMinInterval(200)
                            .setListener(object : FileDownloadListenerAdapter() {
                                override fun progress(
                                    task: BaseDownloadTask?,
                                    soFarBytes: Int,
                                    totalBytes: Int,
                                ) {
                                    Timber.d("downloadSingleCocos--progress")
                                    progress?.invoke(soFarBytes, totalBytes, task?.speed ?: -1)
                                }

                                override fun error(task: BaseDownloadTask?, e: Throwable?) {
                                    super.error(task, e)
                                    Timber.e("downloadSingleCocos--error：" + e.msg)
                                }
                            })
                            .addFinishListener {
                                Timber.e("downloadSingleCocos--finish--status: ${it.status} ,path：${it.path} ,filename：${it.filename}")
                                val cocos = it.tag as CocosData.CocosDataItem
                                if (tCocos.decompressZip(path, cocos)) {
                                    success?.invoke(path)
                                }
                            }.start()
                    } else {
                        success?.invoke(path)
                    }
                    return@forEach
                }
            }
        }
    }
}