package com.bdb.lottery.datasource.cocos

import com.bdb.lottery.const.IUrl
import com.bdb.lottery.datasource.cocos.data.CocosData
import com.bdb.lottery.utils.file.Files
import com.bdb.lottery.utils.file.TPath
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class CocosRemoteDs @Inject constructor(
    private val cocosApi: CocosApi,
    private val tPath: TPath
) {
    fun downAllCocosFile() {
        val cocosDownloadPath = tPath.cocosDownloadPath()
        if (Files.isDir(cocosDownloadPath)) {
            val cocosObservables = mutableListOf<Observable<CocosData>>()
            IUrl.ULR_COCOS_CONFIG.forEach { cocosObservables.add(cocosApi.cocosConfig(it)) }
            Observable.mergeArrayDelayError(*(cocosObservables.toTypedArray()))
                .retry(2)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .flatMap { if (!it.isNullOrEmpty()) Observable.fromIterable(it) else null }
                .observeOn(Schedulers.io())
                .flatMap { cocosApi.downFile(it.androidZipUrl) }
                .observeOn(Schedulers.io())
                .subscribe {

                }
        }
    }
}