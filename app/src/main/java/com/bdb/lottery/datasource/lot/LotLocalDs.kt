package com.bdb.lottery.datasource.lot

import android.content.Context
import com.bdb.lottery.database.lot.entity.SubPlayMethod
import com.bdb.lottery.utils.file.Files
import com.bdb.lottery.utils.file.TPath
import com.orange.bdb.DaoMaster
import com.orange.bdb.SubPlayMethodDao
import dagger.hilt.android.qualifiers.ApplicationContext
import org.greenrobot.greendao.query.WhereCondition.PropertyCondition
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LotLocalDs @Inject constructor(
    tPath: TPath,
    @ApplicationContext private val context: Context,
) {
    private var mSubPlayMethodDao: SubPlayMethodDao? = null

    init {
        val lotteryDbPath = tPath.lotteryDbPath()
        if (Files.isFileExists(lotteryDbPath)) {
            mSubPlayMethodDao = DaoMaster(DaoMaster.DevOpenHelper(context,
                lotteryDbPath).writableDatabase).newSession().subPlayMethodDao
        }
    }

    //根据玩法id查找玩法配置信息
    fun queryBetTypeByPlayId(playId: Int): List<SubPlayMethod>? {
        return mSubPlayMethodDao?.queryBuilder()
            ?.where(SubPlayMethodDao.Properties.Parent_play_method.eq(playId),
                SubPlayMethodDao.Properties.Enable.eq(true),
                PropertyCondition(SubPlayMethodDao.Properties.Belongto, " NOT LIKE ?", "PK10"))
            ?.list()
    }
}