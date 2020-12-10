package com.bdb.lottery.utils.resource

import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.core.content.ContextCompat
import com.bdb.lottery.app.BdbApp
import com.bdb.lottery.extension.isSpace
import com.bdb.lottery.utils.convert.Converts
import com.bdb.lottery.utils.file.FileIOs
import java.io.IOException
import java.io.InputStream
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

object Resources {
    private const val BUFFER_SIZE = 8192

    private fun ResourceUtils() {
        throw UnsupportedOperationException("u can't instantiate me...")
    }

    /**
     * Return the drawable by identifier.
     *
     * @param id The identifier.
     * @return the drawable by identifier
     */
    fun getDrawable(@DrawableRes id: Int): Drawable? {
        return ContextCompat.getDrawable(BdbApp.sApp, id)
    }

    /**
     * Return the id identifier by name.
     *
     * @param name The name of id.
     * @return the id identifier by name
     */
    fun getIdByName(name: String?): Int {
        return BdbApp.sApp.getResources()
            .getIdentifier(name, "id", BdbApp.sApp.getPackageName())
    }

    /**
     * Return the string identifier by name.
     *
     * @param name The name of string.
     * @return the string identifier by name
     */
    fun getStringIdByName(name: String?): Int {
        return BdbApp.sApp.getResources()
            .getIdentifier(name, "string", BdbApp.sApp.getPackageName())
    }

    /**
     * Return the color identifier by name.
     *
     * @param name The name of color.
     * @return the color identifier by name
     */
    fun getColorIdByName(name: String?): Int {
        return BdbApp.sApp.getResources()
            .getIdentifier(name, "color", BdbApp.sApp.getPackageName())
    }

    /**
     * Return the dimen identifier by name.
     *
     * @param name The name of dimen.
     * @return the dimen identifier by name
     */
    fun getDimenIdByName(name: String?): Int {
        return BdbApp.sApp.getResources()
            .getIdentifier(name, "dimen", BdbApp.sApp.getPackageName())
    }

    /**
     * Return the drawable identifier by name.
     *
     * @param name The name of drawable.
     * @return the drawable identifier by name
     */
    fun getDrawableIdByName(name: String?): Int {
        return BdbApp.sApp.getResources()
            .getIdentifier(name, "drawable", BdbApp.sApp.getPackageName())
    }

    /**
     * Return the mipmap identifier by name.
     *
     * @param name The name of mipmap.
     * @return the mipmap identifier by name
     */
    fun getMipmapIdByName(name: String?): Int {
        return BdbApp.sApp.getResources()
            .getIdentifier(name, "mipmap", BdbApp.sApp.getPackageName())
    }

    /**
     * Return the layout identifier by name.
     *
     * @param name The name of layout.
     * @return the layout identifier by name
     */
    fun getLayoutIdByName(name: String?): Int {
        return BdbApp.sApp.getResources()
            .getIdentifier(name, "layout", BdbApp.sApp.getPackageName())
    }

    /**
     * Return the style identifier by name.
     *
     * @param name The name of style.
     * @return the style identifier by name
     */
    fun getStyleIdByName(name: String?): Int {
        return BdbApp.sApp.getResources()
            .getIdentifier(name, "style", BdbApp.sApp.getPackageName())
    }

    /**
     * Return the anim identifier by name.
     *
     * @param name The name of anim.
     * @return the anim identifier by name
     */
    fun getAnimIdByName(name: String?): Int {
        return BdbApp.sApp.getResources()
            .getIdentifier(name, "anim", BdbApp.sApp.getPackageName())
    }

    /**
     * Return the menu identifier by name.
     *
     * @param name The name of menu.
     * @return the menu identifier by name
     */
    fun getMenuIdByName(name: String?): Int {
        return BdbApp.sApp.getResources()
            .getIdentifier(name, "menu", BdbApp.sApp.getPackageName())
    }

    /**
     * Copy the file from assets.
     *
     * @param assetsFilePath The path of file in assets.
     * @param destFilePath   The path of destination file.
     * @return `true`: success<br></br>`false`: fail
     */
    fun copyFileFromAssets(assetsFilePath: String, destFilePath: String): Boolean {
        try {
            val assets: Array<String>? = BdbApp.sApp.assets.list(assetsFilePath)
            return if (!assets.isNullOrEmpty()) {
                var res = false
                for (asset in assets) {
                    res =
                        res and copyFileFromAssets("$assetsFilePath/$asset", "$destFilePath/$asset")
                }
                res
            } else {
                FileIOs.writeFileFromIS(
                    destFilePath,
                    BdbApp.sApp.assets.open(assetsFilePath)
                )
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * Return the content of assets.
     *
     * @param assetsFilePath The path of file in assets.
     * @return the content of assets
     */
    fun readAssets2String(assetsFilePath: String): String? {
        return readAssets2String(assetsFilePath, null)
    }

    /**
     * Return the content of assets.
     *
     * @param assetsFilePath The path of file in assets.
     * @param charsetName    The name of charset.
     * @return the content of assets
     */
    fun readAssets2String(assetsFilePath: String, charsetName: String?): String? {
        return try {
            val `is`: InputStream = BdbApp.sApp.getAssets().open(assetsFilePath)
            val bytes: ByteArray = Converts.inputStream2Bytes(`is`) ?: return ""
            if (charsetName.isSpace()) {
                String(bytes)
            } else {
                try {
                    String(bytes, Charset.forName(charsetName))
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                    ""
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            ""
        }
    }

    /**
     * Return the content of file in assets.
     *
     * @param assetsPath The path of file in assets.
     * @return the content of file in assets
     */
    fun readAssets2List(assetsPath: String): List<String?>? {
        return readAssets2List(assetsPath, "")
    }

    /**
     * Return the content of file in assets.
     *
     * @param assetsPath  The path of file in assets.
     * @param charsetName The name of charset.
     * @return the content of file in assets
     */
    fun readAssets2List(
        assetsPath: String,
        charsetName: String,
    ): List<String?>? {
        return try {
            Converts.inputStream2Lines(BdbApp.sApp.getResources().getAssets()
                .open(assetsPath), charsetName)
        } catch (e: IOException) {
            e.printStackTrace()
            emptyList<String>()
        }
    }


    /**
     * Copy the file from raw.
     *
     * @param resId        The resource id.
     * @param destFilePath The path of destination file.
     * @return `true`: success<br></br>`false`: fail
     */
    fun copyFileFromRaw(@RawRes resId: Int, destFilePath: String?): Boolean {
        return FileIOs.writeFileFromIS(
            destFilePath,
            BdbApp.sApp.getResources().openRawResource(resId)
        )
    }

    /**
     * Return the content of resource in raw.
     *
     * @param resId The resource id.
     * @return the content of resource in raw
     */
    fun readRaw2String(@RawRes resId: Int): String? {
        return readRaw2String(resId, null)
    }

    /**
     * Return the content of resource in raw.
     *
     * @param resId       The resource id.
     * @param charsetName The name of charset.
     * @return the content of resource in raw
     */
    fun readRaw2String(@RawRes resId: Int, charsetName: String?): String? {
        val `is`: InputStream = BdbApp.sApp.getResources().openRawResource(resId)
        val bytes: ByteArray = Converts.inputStream2Bytes(`is`) ?: return null
        return if (charsetName.isSpace()) {
            String(bytes)
        } else {
            try {
                String(bytes, Charset.forName(charsetName))
            } catch (e: UnsupportedEncodingException) {
                e.printStackTrace()
                ""
            }
        }
    }

    /**
     * Return the content of resource in raw.
     *
     * @param resId The resource id.
     * @return the content of file in assets
     */
    fun readRaw2List(@RawRes resId: Int): List<String?>? {
        return readRaw2List(resId, "")
    }

    /**
     * Return the content of resource in raw.
     *
     * @param resId       The resource id.
     * @param charsetName The name of charset.
     * @return the content of file in assets
     */
    fun readRaw2List(
        @RawRes resId: Int,
        charsetName: String,
    ): List<String?>? {
        return Converts.inputStream2Lines(BdbApp.sApp.getResources().openRawResource(resId),
            charsetName)
    }
}