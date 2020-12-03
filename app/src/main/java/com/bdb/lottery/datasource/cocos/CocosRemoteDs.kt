package com.bdb.lottery.datasource.cocos

import android.content.Context
import com.bdb.lottery.const.IUrl
import com.bdb.lottery.datasource.cocos.data.CocosData
import com.bdb.lottery.utils.file.Files
import com.bdb.lottery.utils.file.TPath
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadQueueSet
import com.liulishuo.filedownloader.FileDownloader
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject


class CocosRemoteDs @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cocosApi: CocosApi,
    private val tPath: TPath
) {
    fun downAllCocosFile() {
        val alreadyConfig = AtomicBoolean(false)
        val cocosDownloadPath = tPath.cocosDownloadPath()
        if (Files.createOrExistsDir(cocosDownloadPath)) {
            val cocosObservables = mutableListOf<Observable<CocosData>>()
            IUrl.ULR_COCOS_CONFIG.forEach { cocosObservables.add(cocosApi.cocosConfig(it)) }
            var dispose: Disposable? = null
            Observable.mergeArrayDelayError(*(cocosObservables.toTypedArray()))
                .doOnSubscribe { dispose = it }
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe {
                    if (alreadyConfig.compareAndSet(false, true)) {
                        dispose?.isDisposed
                        if (!it.isEmpty()) {
                            FileDownloader.setup(context)
                            val queueSet =
                                FileDownloadQueueSet(FileDownloadListenerAdapter())
                            val tasks: MutableList<BaseDownloadTask> = ArrayList()
                            it.forEach {
                                val fileName =
                                    cocosDownloadPath + File.separator + it.name + ".zip";
                                tasks.add(
                                    FileDownloader.getImpl().create(it.androidZipUrl)
                                        .setPath(fileName)
                                )
                            }
                            queueSet.disableCallbackProgressTimes().setAutoRetryTimes(1)
                                .downloadTogether(tasks).start()
                        }
                    }
                }
        }
    }
}