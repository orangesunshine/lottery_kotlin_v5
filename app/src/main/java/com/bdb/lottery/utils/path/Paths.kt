package com.bdb.lottery.utils.path

import android.os.Build
import com.bdb.lottery.app.BdbApp
import java.io.File

object Paths {
    /**
     * Return the path of /data/data/package.
     *
     * @return the path of /data/data/package
     */
    fun getInternalAppDataPath(): String? {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            BdbApp.sApp.applicationInfo.dataDir
        } else getAbsolutePath(BdbApp.sApp.dataDir)
    }

    private fun getAbsolutePath(file: File?): String? {
        return if (file == null) "" else file.absolutePath
    }
}