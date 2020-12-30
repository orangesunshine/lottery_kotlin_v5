package com.bdb.lottery.datasource.lot

import android.content.Context
import com.bdb.lottery.database.lot.entity.SubPlayMethod
import com.bdb.lottery.greendao.DaoMaster
import com.bdb.lottery.greendao.SubPlayMethodDao
import com.bdb.lottery.utils.file.Files
import com.bdb.lottery.utils.file.TPath
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LotLocalDs @Inject constructor(
    tPath: TPath,
    @ApplicationContext private val context: Context,
) {
    private var mSubPlayMethodcDao: SubPlayMethodDao? = null

    init {
        val lotteryDbPath = tPath.lotteryDbPath()
        if (Files.isFileExists(lotteryDbPath)) {
            mSubPlayMethodcDao = DaoMaster(
                DaoMaster.DevOpenHelper(
                    context,
                    lotteryDbPath
                ).writableDatabase
            ).newSession().subPlayMethodDao
        }
    }

    //根据玩法id查找玩法配置信息
    fun queryBetTypeByPlayId(playId: Int): List<SubPlayMethod>? {
        return mSubPlayMethodcDao?.queryBuilder()?.where(
            SubPlayMethodDao.Properties.Play_method_id.eq(playId),
            SubPlayMethodDao.Properties.Enable.eq(true)
        )?.list()
    }
}