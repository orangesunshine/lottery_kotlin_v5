package com.bdb.lottery.database.lot

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bdb.lottery.database.lot.dao.LotTypeDao
import com.bdb.lottery.database.lot.entity.LotType
import com.bdb.lottery.utils.file.Files
import com.bdb.lottery.utils.resource.Resources
import java.io.File

@Database(entities = [LotType::class], version = 1, exportSchema = false)
abstract class LotDatabase : RoomDatabase() {
    abstract fun lotTypeDao(): LotTypeDao

    companion object {
        private val LOT_DATABASE_NAME = "bettypeview-v23.db"
        private var instance: LotDatabase? = null
        fun getInstance(context: Context): LotDatabase {
            val path = context.filesDir.absolutePath
            existOrCreateDb(path)
            instance =
                Room.databaseBuilder(context.applicationContext,
                    LotDatabase::class.java,
                    path + File.separator + LOT_DATABASE_NAME)
                    .allowMainThreadQueries().build()
            return instance as LotDatabase
        }

        fun existOrCreateDb(path: String) {
            //文件存放在“data/data/包名/files/bettypeview-v23.db”
            if (!Files.isFileExists(path + File.separator + LOT_DATABASE_NAME)) {
                Resources.copyFileFromAssets("database", path)
            }
        }
    }
}