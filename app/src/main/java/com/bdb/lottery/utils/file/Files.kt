package com.bdb.lottery.utils.file

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.StatFs
import android.text.TextUtils
import com.bdb.lottery.app.BdbApp
import com.bdb.lottery.extension.isSpace
import com.bdb.lottery.utils.convert.Converts
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.security.DigestInputStream
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import kotlin.experimental.and

object Files {
    ///////////////////////////////////////////////////////////////////////////
    // interface
    ///////////////////////////////////////////////////////////////////////////
    interface OnReplaceListener {
        fun onReplace(srcFile: File?, destFile: File?): Boolean
    }

    private val LINE_SEP = System.getProperty("line.separator")


    //通过路径获取file
    fun getFileByPath(filePath: String?): File? {
        return if (filePath.isSpace()) null else File(filePath!!)
    }

    //region 判断文件是否存在
    /**
     * Return whether the file exists.
     *
     * @param file The file.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isFileExists(file: File?): Boolean {
        if (file == null) return false
        return if (file.exists()) {
            true
        } else Files.isFileExists(file.absolutePath)
    }

    /**
     * Return whether the file exists.
     *
     * @param filePath The path of file.
     * @return `true`: yes<br></br>`false`: no
     */
    fun isFileExists(filePath: String?): Boolean {
        val file: File =
            getFileByPath(filePath)
                ?: return false
        return if (file.exists()) {
            true
        } else isFileExistsApi29(filePath!!)
    }

    private fun isFileExistsApi29(filePath: String): Boolean {
        if (Build.VERSION.SDK_INT >= 29) {
            try {
                val uri = Uri.parse(filePath)
                val cr: ContentResolver = BdbApp.sApp.getContentResolver()
                val afd = cr.openAssetFileDescriptor(uri, "r") ?: return false
                try {
                    afd.close()
                } catch (ignore: IOException) {
                }
            } catch (e: FileNotFoundException) {
                return false
            }
            return true
        }
        return false
    }
    //endregion

    //region 重命名
    fun rename(filePath: String?, newName: String?): Boolean {
        return rename(
            getFileByPath(
                filePath
            ), newName
        )
    }

    fun rename(file: File?, newName: String?): Boolean {
        // file is null then return false
        if (file == null) return false
        // file doesn't exist then return false
        if (!file.exists()) return false
        // the new name is space then return false
        if (newName.isSpace()) return false
        // the new name equals old name then return true
        if (newName == file.name) return true
        val newFile = File(file.parent + File.separator + newName)
        // the new name of file exists then return false
        return (!newFile.exists()
                && file.renameTo(newFile))
    }
    //endregion

    //region 判断是否是文件夹
    fun isDir(dirPath: String?): Boolean {
        return isDir(
            getFileByPath(
                dirPath
            )
        )
    }

    fun isDir(file: File?): Boolean {
        return file != null && file.exists() && file.isDirectory
    }
    //endregion

    //region 判断文件
    fun isFile(filePath: String?): Boolean {
        return isFile(
            getFileByPath(
                filePath
            )
        )
    }

    fun isFile(file: File?): Boolean {
        return file != null && file.exists() && file.isFile
    }
    //endregion

    //region 创建文件
    fun createOrExistsDir(dirPath: String?): Boolean {
        return createOrExistsDir(
            getFileByPath(
                dirPath
            )
        )
    }

    fun createOrExistsDir(file: File?): Boolean {
        return file != null && if (file.exists()) file.isDirectory else file.mkdirs()
    }

    fun createOrExistsFile(filePath: String?): Boolean {
        return createOrExistsFile(
            getFileByPath(
                filePath
            )
        )
    }

    fun createOrExistsFile(file: File?): Boolean {
        if (file == null) return false
        if (file.exists()) return file.isFile
        return if (!createOrExistsDir(file.parentFile)) false else try {
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    fun createFileByDeleteOldFile(filePath: String?): Boolean {
        return createFileByDeleteOldFile(
            getFileByPath(
                filePath
            )
        )
    }

    fun createFileByDeleteOldFile(file: File?): Boolean {
        if (file == null) return false
        // file exists and unsuccessfully delete then return false
        if (file.exists() && !file.delete()) return false
        return if (!createOrExistsDir(file.parentFile)) false else try {
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }
    //endregion

    //region 复制
    fun copy(
        srcPath: String?,
        destPath: String?,
    ): Boolean {
        return copy(
            getFileByPath(
                srcPath
            ), getFileByPath(destPath), null
        )
    }

    fun copy(
        srcPath: String?,
        destPath: String?,
        listener: OnReplaceListener?,
    ): Boolean {
        return copy(
            getFileByPath(
                srcPath
            ),
            getFileByPath(destPath),
            listener
        )
    }

    @JvmOverloads
    fun copy(
        src: File?,
        dest: File?,
        listener: OnReplaceListener? = null,
    ): Boolean {
        if (src == null) return false
        return if (src.isDirectory) {
            copyDir(src, dest, listener)
        } else copyFile(src, dest, listener)
    }

    private fun copyDir(
        srcDir: File?,
        destDir: File?,
        listener: OnReplaceListener?,
    ): Boolean {
        return copyOrMoveDir(
            srcDir,
            destDir,
            listener,
            false
        )
    }

    private fun copyFile(
        srcFile: File?,
        destFile: File?,
        listener: OnReplaceListener?,
    ): Boolean {
        return copyOrMoveFile(
            srcFile,
            destFile,
            listener,
            false
        )
    }
    //endregion

    //region 移动文件
    fun move(
        srcPath: String?,
        destPath: String?,
    ): Boolean {
        return move(
            getFileByPath(
                srcPath
            ), getFileByPath(destPath), null
        )
    }

    fun move(
        srcPath: String?,
        destPath: String?,
        listener: OnReplaceListener?,
    ): Boolean {
        return move(
            getFileByPath(
                srcPath
            ),
            getFileByPath(destPath),
            listener
        )
    }

    @JvmOverloads
    fun move(
        src: File?,
        dest: File?,
        listener: OnReplaceListener? = null,
    ): Boolean {
        if (src == null) return false
        return if (src.isDirectory) {
            moveDir(src, dest, listener)
        } else moveFile(src, dest, listener)
    }

    fun moveDir(
        srcDir: File?,
        destDir: File?,
        listener: OnReplaceListener?,
    ): Boolean {
        return copyOrMoveDir(
            srcDir,
            destDir,
            listener,
            true
        )
    }

    fun moveFile(
        srcFile: File?,
        destFile: File?,
        listener: OnReplaceListener?,
    ): Boolean {
        return copyOrMoveFile(
            srcFile,
            destFile,
            listener,
            true
        )
    }
    //endregion

    //region 移动、复制
    private fun copyOrMoveDir(
        srcDir: File?,
        destDir: File?,
        listener: OnReplaceListener?,
        isMove: Boolean,
    ): Boolean {
        if (srcDir == null || destDir == null) return false
        // destDir's path locate in srcDir's path then return false
        val srcPath = srcDir.path + File.separator
        val destPath = destDir.path + File.separator
        if (destPath.contains(srcPath)) return false
        if (!srcDir.exists() || !srcDir.isDirectory) return false
        if (!createOrExistsDir(destDir)) return false
        val files = srcDir.listFiles()
        if (files != null && files.size > 0) {
            for (file in files) {
                val oneDestFile = File(destPath + file.name)
                if (file.isFile) {
                    if (!copyOrMoveFile(
                            file,
                            oneDestFile,
                            listener,
                            isMove
                        )
                    ) return false
                } else if (file.isDirectory) {
                    if (!copyOrMoveDir(
                            file,
                            oneDestFile,
                            listener,
                            isMove
                        )
                    ) return false
                }
            }
        }
        return !isMove || deleteDir(srcDir)
    }

    private fun copyOrMoveFile(
        srcFile: File?,
        destFile: File?,
        listener: OnReplaceListener?,
        isMove: Boolean,
    ): Boolean {
        if (srcFile == null || destFile == null) return false
        // srcFile equals destFile then return false
        if (srcFile == destFile) return false
        // srcFile doesn't exist or isn't a file then return false
        if (!srcFile.exists() || !srcFile.isFile) return false
        if (destFile.exists()) {
            if (listener == null || listener.onReplace(
                    srcFile,
                    destFile
                )
            ) { // require delete the old file
                if (!destFile.delete()) { // unsuccessfully delete then return false
                    return false
                }
            } else {
                return true
            }
        }
        return if (!createOrExistsDir(destFile.parentFile)) false else try {
            (FileIOs.writeFileFromIS(destFile.absolutePath, FileInputStream(srcFile))
                    && !(isMove && !deleteFile(
                srcFile
            )))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            false
        }
    }
    //endregion

    //region 删除文件
    fun delete(filePath: String?): Boolean {
        return delete(
            getFileByPath(
                filePath
            )
        )
    }

    fun delete(file: File?): Boolean {
        if (file == null) return false
        return if (file.isDirectory) {
            deleteDir(file)
        } else deleteFile(file)
    }

    fun deleteDir(dir: File?): Boolean {
        if (dir == null) return false
        // dir doesn't exist then return true
        if (!dir.exists()) return true
        // dir isn't a directory then return false
        if (!dir.isDirectory) return false
        val files = dir.listFiles()
        if (files != null && files.size > 0) {
            for (file in files) {
                if (file.isFile) {
                    if (!file.delete()) return false
                } else if (file.isDirectory) {
                    if (!deleteDir(file)) return false
                }
            }
        }
        return dir.delete()
    }

    private fun deleteFile(file: File?): Boolean {
        return file != null && (!file.exists() || file.isFile && file.delete())
    }

    fun deleteAllInDir(dirPath: String?): Boolean {
        return deleteAllInDir(
            getFileByPath(
                dirPath
            )
        )
    }

    fun deleteAllInDir(dir: File?): Boolean {
        return deleteFilesInDirWithFilter(dir,
            FileFilter { true })
    }

    fun deleteFilesInDir(dirPath: String?): Boolean {
        return deleteFilesInDir(
            getFileByPath(
                dirPath
            )
        )
    }

    fun deleteFilesInDir(dir: File?): Boolean {
        return deleteFilesInDirWithFilter(dir,
            FileFilter { pathname -> pathname.isFile })
    }

    fun deleteFilesInDirWithFilter(
        dirPath: String?,
        filter: FileFilter?,
    ): Boolean {
        return deleteFilesInDirWithFilter(
            getFileByPath(
                dirPath
            ), filter
        )
    }

    fun deleteFilesInDirWithFilter(dir: File?, filter: FileFilter?): Boolean {
        if (dir == null || filter == null) return false
        // dir doesn't exist then return true
        if (!dir.exists()) return true
        // dir isn't a directory then return false
        if (!dir.isDirectory) return false
        val files = dir.listFiles()
        if (files != null && files.size != 0) {
            for (file in files) {
                if (filter.accept(file)) {
                    if (file.isFile) {
                        if (!file.delete()) return false
                    } else if (file.isDirectory) {
                        if (!deleteDir(file)) return false
                    }
                }
            }
        }
        return true
    }
    //endregion

    //region 文件列表
    @JvmOverloads
    fun listFilesInDir(
        dirPath: String?,
        comparator: Comparator<File?>? = null,
    ): List<File> {
        return listFilesInDir(
            getFileByPath(
                dirPath
            ), false, comparator
        )
    }

    @JvmOverloads
    fun listFilesInDir(dir: File?, comparator: Comparator<File?>? = null): List<File> {
        return listFilesInDir(
            dir,
            false,
            comparator
        )
    }

    fun listFilesInDir(dirPath: String?, isRecursive: Boolean): List<File> {
        return listFilesInDir(
            getFileByPath(
                dirPath
            ), isRecursive
        )
    }

    fun listFilesInDir(
        dirPath: String?,
        isRecursive: Boolean,
        comparator: Comparator<File?>?,
    ): List<File> {
        return listFilesInDir(
            getFileByPath(
                dirPath
            ), isRecursive, comparator
        )
    }

    @JvmOverloads
    fun listFilesInDir(
        dir: File?,
        isRecursive: Boolean,
        comparator: Comparator<File?>? = null,
    ): List<File> {
        return listFilesInDirWithFilter(
            dir,
            FileFilter { true }, isRecursive, comparator
        )
    }

    fun listFilesInDirWithFilter(
        dirPath: String?,
        filter: FileFilter?,
    ): List<File> {
        return listFilesInDirWithFilter(
            getFileByPath(
                dirPath
            ), filter
        )
    }

    fun listFilesInDirWithFilter(
        dirPath: String?,
        filter: FileFilter?,
        comparator: Comparator<File?>?,
    ): List<File> {
        return listFilesInDirWithFilter(
            getFileByPath(
                dirPath
            ), filter, comparator
        )
    }

    fun listFilesInDirWithFilter(
        dir: File?,
        filter: FileFilter?,
        comparator: Comparator<File?>?,
    ): List<File> {
        return listFilesInDirWithFilter(
            dir,
            filter,
            false,
            comparator
        )
    }

    fun listFilesInDirWithFilter(
        dirPath: String?,
        filter: FileFilter?,
        isRecursive: Boolean,
    ): List<File> {
        return listFilesInDirWithFilter(
            getFileByPath(
                dirPath
            ), filter, isRecursive
        )
    }

    fun listFilesInDirWithFilter(
        dirPath: String?,
        filter: FileFilter?,
        isRecursive: Boolean,
        comparator: Comparator<File?>? = null,
    ): List<File> {
        return listFilesInDirWithFilter(
            getFileByPath(
                dirPath
            ), filter, isRecursive, comparator
        )
    }

    @JvmOverloads
    fun listFilesInDirWithFilter(
        dir: File?,
        filter: FileFilter?,
        isRecursive: Boolean = false,
        comparator: Comparator<File?>? = null,
    ): List<File> {
        val files: List<File> =
            listFilesInDirWithFilterInner(
                dir,
                filter,
                isRecursive
            )
        if (comparator != null) {
            Collections.sort(files, comparator)
        }
        return files
    }

    private fun listFilesInDirWithFilterInner(
        dir: File?,
        filter: FileFilter?,
        isRecursive: Boolean,
    ): List<File> {
        val list: MutableList<File> = ArrayList()
        if (!isDir(dir)) return list
        val files = dir!!.listFiles()
        if (files != null && files.size > 0) {
            for (file in files) {
                if (filter?.accept(file) ?: false) {
                    list.add(file)
                }
                if (isRecursive && file.isDirectory) {
                    list.addAll(
                        listFilesInDirWithFilterInner(
                            file,
                            filter,
                            true
                        )
                    )
                }
            }
        }
        return list
    }
    //endregion

    //region 文件最后修改时间
    fun getFileLastModified(filePath: String?): Long {
        return getFileLastModified(
            getFileByPath(
                filePath
            )
        )
    }

    fun getFileLastModified(file: File?): Long {
        return file?.lastModified() ?: -1
    }
    //endregion

    //region 文件编码格式
    fun getFileCharsetSimple(filePath: String?): String {
        return getFileCharsetSimple(
            getFileByPath(
                filePath
            )
        )
    }

    fun getFileCharsetSimple(file: File?): String {
        if (file == null) return ""
        if (isUtf8(file)) return "UTF-8"
        var p = 0
        var ins: InputStream? = null
        try {
            ins = BufferedInputStream(FileInputStream(file))
            p = (ins.read() shl 8) + ins.read()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                ins?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return when (p) {
            0xfffe -> "Unicode"
            0xfeff -> "UTF-16BE"
            else -> "GBK"
        }
    }
    //endregion

    //region 判断utf8
    fun isUtf8(filePath: String?): Boolean {
        return isUtf8(
            getFileByPath(
                filePath
            )
        )
    }

    fun isUtf8(file: File?): Boolean {
        if (file == null) return false
        var ins: InputStream? = null
        try {
            val bytes = ByteArray(24)
            ins = BufferedInputStream(FileInputStream(file))
            val read = ins.read(bytes)
            return if (read != -1) {
                val readArr = ByteArray(read)
                System.arraycopy(bytes, 0, readArr, 0, read)
                isUtf8(readArr) == 100
            } else {
                false
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                ins?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return false
    }

    /**
     * UTF-8编码方式
     * ----------------------------------------------
     * 0xxxxxxx
     * 110xxxxx 10xxxxxx
     * 1110xxxx 10xxxxxx 10xxxxxx
     * 11110xxx 10xxxxxx 10xxxxxx 10xxxxxx
     */
    private fun isUtf8(raw: ByteArray): Int {
        var i: Int
        val len: Int
        var utf8 = 0
        var ascii = 0
        if (raw.size > 3) {
            if (raw[0] == 0xEF.toByte() && raw[1] == 0xBB.toByte() && raw[2] == 0xBF.toByte()) {
                return 100
            }
        }
        len = raw.size
        var child = 0
        i = 0
        while (i < len) {

            // UTF-8 byte shouldn't be FF and FE
            if (raw[i] and 0xFF.toByte() == 0xFF.toByte() || raw[i] and 0xFE.toByte() == 0xFE.toByte()) {
                return 0
            }
            if (child == 0) {
                // ASCII format is 0x0*******
                if (raw[i] and 0x7F.toByte() == raw[i] && raw[i] != 0.toByte()) {
                    ascii++
                } else if (raw[i] and 0xC0.toByte() == 0xC0.toByte()) {
                    // 0x11****** maybe is UTF-8
                    for (bit in 0..7) {
                        child =
                            if ((0x80 shr bit).toByte() and raw[i] == (0x80 shr bit).toByte()) {
                                bit
                            } else {
                                break
                            }
                    }
                    utf8++
                }
                i++
            } else {
                child = if (raw.size - i > child) child else raw.size - i
                var currentNotUtf8 = false
                for (children in 0 until child) {
                    // format must is 0x10******
                    if (raw[i + children] and 0x80.toByte() != 0x80.toByte()) {
                        if (raw[i + children] and 0x7F.toByte() == raw[i + children] && raw[i] != 0.toByte()) {
                            // ASCII format is 0x0*******
                            ascii++
                        }
                        currentNotUtf8 = true
                    }
                }
                if (currentNotUtf8) {
                    utf8--
                    i++
                } else {
                    utf8 += child
                    i += child
                }
                child = 0
            }
        }
        // UTF-8 contains ASCII
        return if (ascii == len) {
            100
        } else (100 * ((utf8 + ascii).toFloat() / len.toFloat())).toInt()
    }
    //endregion

    //region 文件行数
    fun getFileLines(filePath: String?): Int {
        return getFileLines(
            getFileByPath(
                filePath
            )
        )
    }

    fun getFileLines(file: File?): Int {
        var count = 1
        var ins: InputStream? = null
        try {
            ins = BufferedInputStream(FileInputStream(file))
            val buffer = ByteArray(1024)
            var readChars: Int
            if (LINE_SEP?.endsWith("\n") ?: false) {
                while (ins.read(buffer, 0, 1024).also { readChars = it } != -1) {
                    for (i in 0 until readChars) {
                        if (buffer[i].equals('\n')) ++count
                    }
                }
            } else {
                while (ins.read(buffer, 0, 1024).also { readChars = it } != -1) {
                    for (i in 0 until readChars) {
                        if (buffer[i] == '\r'.toByte()) ++count
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                ins?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return count
    }
    //endregion

    //region 文件、目录大小
    fun getSize(filePath: String?): String {
        return getSize(
            getFileByPath(
                filePath
            )
        )
    }

    fun getSize(file: File?): String {
        if (file == null) return ""
        return if (file.isDirectory) {
            getDirSize(file)
        } else getFileSize(file)
    }

    private fun getDirSize(dir: File): String {
        val len: Long = getDirLength(dir)
        return if (len == -1L) "" else Converts.byte2FitMemorySize(len)
    }

    private fun getFileSize(file: File): String {
        val len: Long = getFileLength(file)
        return if (len == -1L) "" else Converts.byte2FitMemorySize(len)
    }

    fun getLength(filePath: String?): Long {
        return getLength(
            getFileByPath(
                filePath
            )
        )
    }

    fun getLength(file: File?): Long {
        if (file == null) return 0
        return if (file.isDirectory) {
            getDirLength(file)
        } else getFileLength(file)
    }

    private fun getDirLength(dir: File): Long {
        if (!isDir(dir)) return 0
        var len: Long = 0
        val files = dir.listFiles()
        if (files != null && files.isNotEmpty()) {
            for (file in files) {
                if (file.isDirectory) {
                    len += getDirLength(file)
                } else {
                    len += file.length()
                }
            }
        }
        return len
    }

    fun getFileLength(filePath: String): Long {
        val isURL = filePath.matches(Regex("[a-zA-z]+://[^\\s]*"))
        if (isURL) {
            try {
                val conn = URL(filePath).openConnection() as HttpURLConnection
                conn.setRequestProperty("Accept-Encoding", "identity")
                conn.connect()
                return if (conn.responseCode == 200) {
                    conn.contentLength.toLong()
                } else -1
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return getFileLength(
            getFileByPath(
                filePath
            )
        )
    }

    private fun getFileLength(file: File?): Long {
        return if (null != file && isFile(file))
            file.length()
        else -1
    }
    //endregion

    //region md5
    fun getFileMD5ToString(filePath: String?): String {
        val file = if (filePath.isSpace()) null else File(filePath)
        return getFileMD5ToString(file)
    }

    fun getFileMD5ToString(file: File?): String {
        return Converts.bytes2HexString(
            getFileMD5(
                file
            )
        )
    }

    fun getFileMD5(filePath: String?): ByteArray? {
        return getFileMD5(
            getFileByPath(
                filePath
            )
        )
    }

    fun getFileMD5(file: File?): ByteArray? {
        if (file == null) return null
        var dis: DigestInputStream? = null
        try {
            val fis = FileInputStream(file)
            var md = MessageDigest.getInstance("MD5")
            dis = DigestInputStream(fis, md)
            val buffer = ByteArray(1024 * 256)
            while (true) {
                if (dis.read(buffer) <= 0) break
            }
            md = dis.messageDigest
            return md.digest()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                dis?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }
    //endregion

    //region 全路径获取文件、文件夹名称，后缀
    fun getDirName(file: File?): String {
        return if (file == null) "" else getDirName(
            file.absolutePath
        )
    }

    fun getDirName(filePath: String): String {
        if (filePath.isSpace()) return ""
        val lastSep = filePath.lastIndexOf(File.separator)
        return if (lastSep == -1) "" else filePath.substring(0, lastSep + 1)
    }

    fun getFileName(file: File?): String {
        return if (file == null) "" else getFileName(
            file.absolutePath
        )
    }

    fun getFileName(filePath: String): String {
        if (filePath.isSpace()) return ""
        val lastSep = filePath.lastIndexOf(File.separator)
        return if (lastSep == -1) filePath else filePath.substring(lastSep + 1)
    }

    fun getFileNameNoExtension(file: File?): String {
        return if (file == null) "" else getFileNameNoExtension(
            file.path
        )
    }

    fun getFileNameNoExtension(filePath: String): String {
        if (filePath.isSpace()) return ""
        val lastPoi = filePath.lastIndexOf('.')
        val lastSep = filePath.lastIndexOf(File.separator)
        if (lastSep == -1) {
            return if (lastPoi == -1) filePath else filePath.substring(0, lastPoi)
        }
        return if (lastPoi == -1 || lastSep > lastPoi) {
            filePath.substring(lastSep + 1)
        } else filePath.substring(lastSep + 1, lastPoi)
    }

    fun getFileExtension(file: File?): String {
        return if (file == null) "" else getFileExtension(
            file.path
        )
    }

    fun getFileExtension(filePath: String): String {
        if (filePath.isSpace()) return ""
        val lastPoi = filePath.lastIndexOf('.')
        val lastSep = filePath.lastIndexOf(File.separator)
        return if (lastPoi == -1 || lastSep >= lastPoi) "" else filePath.substring(lastPoi + 1)
    }
    //endregion

    //region 系统扫描
    fun notifySystemToScan(filePath: String?) {
        notifySystemToScan(
            getFileByPath(
                filePath
            )
        )
    }

    fun notifySystemToScan(file: File?) {
        if (file == null || !file.exists()) return
        val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        intent.data = Uri.parse("file://" + file.absolutePath)
        BdbApp.sApp.sendBroadcast(intent)
    }
    //endregion

    /**
     * Return the total size of file system.
     *
     * @param anyPathInFs Any path in file system.
     * @return the total size of file system
     */
    fun getFsTotalSize(anyPathInFs: String?): Long {
        if (TextUtils.isEmpty(anyPathInFs)) return 0
        val statFs = StatFs(anyPathInFs)
        val blockSize: Long
        val totalSize: Long
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = statFs.blockSizeLong
            totalSize = statFs.blockCountLong
        } else {
            blockSize = statFs.blockSize.toLong()
            totalSize = statFs.blockCount.toLong()
        }
        return blockSize * totalSize
    }

    /**
     * Return the available size of file system.
     *
     * @param anyPathInFs Any path in file system.
     * @return the available size of file system
     */
    fun getFsAvailableSize(anyPathInFs: String?): Long {
        if (TextUtils.isEmpty(anyPathInFs)) return 0
        val statFs = StatFs(anyPathInFs)
        val blockSize: Long
        val availableSize: Long
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = statFs.blockSizeLong
            availableSize = statFs.availableBlocksLong
        } else {
            blockSize = statFs.blockSize.toLong()
            availableSize = statFs.availableBlocks.toLong()
        }
        return blockSize * availableSize
    }

}