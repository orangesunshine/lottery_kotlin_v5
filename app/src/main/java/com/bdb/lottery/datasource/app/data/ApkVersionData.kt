package com.bdb.lottery.datasource.app.data

data class ApkVersionData(
    var apkUrl: String,
    var isForce: Boolean,
    var updateContent: String,
    var isNeedUpdate: Boolean,
    var versionName: String
)