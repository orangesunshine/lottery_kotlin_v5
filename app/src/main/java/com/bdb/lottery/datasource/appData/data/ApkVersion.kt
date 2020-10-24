package com.bdb.lottery.datasource.appData.data

data class ApkVersion(
    var apkUrl: String,
    var isForce: Boolean,
    var updateContent: String,
    var isNeedUpdate: Boolean,
    var versionName: String
)