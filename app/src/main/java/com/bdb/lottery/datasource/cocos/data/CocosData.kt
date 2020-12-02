package com.bdb.lottery.datasource.cocos.data

class CocosData : ArrayList<CocosData.CocosDataItem>() {
    data class CocosDataItem(
        var androidDirSize: Int, // 2353134
        var androidZipMD5: String, // 6C3329DA312A09227293282F3E0E8608
        var androidZipUrl: String, // http://oklznzb.com/lottery/common/animation/draw/zip/android/super-car.zip
        var iOSDirSize: Int, // 2355306
        var iOSZipMD5: String, // 8ab750bf65bb3f87cf8361eb52ff4cef
        var iOSZipUrl: String, // http://oklznzb.com/lottery/common/animation/draw/zip/ios/super-car.zip
        var name: String // super-car
    )
}