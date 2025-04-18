package com.bdb.lottery.greendao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.bdb.lottery.database.lot.entity.SubPlayMethod;
import com.bdb.lottery.database.lot.entity.SubPlayMethodDesc;

import com.bdb.lottery.greendao.SubPlayMethodDao;
import com.bdb.lottery.greendao.SubPlayMethodDescDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig subPlayMethodDaoConfig;
    private final DaoConfig subPlayMethodDescDaoConfig;

    private final SubPlayMethodDao subPlayMethodDao;
    private final SubPlayMethodDescDao subPlayMethodDescDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        subPlayMethodDaoConfig = daoConfigMap.get(SubPlayMethodDao.class).clone();
        subPlayMethodDaoConfig.initIdentityScope(type);

        subPlayMethodDescDaoConfig = daoConfigMap.get(SubPlayMethodDescDao.class).clone();
        subPlayMethodDescDaoConfig.initIdentityScope(type);

        subPlayMethodDao = new SubPlayMethodDao(subPlayMethodDaoConfig, this);
        subPlayMethodDescDao = new SubPlayMethodDescDao(subPlayMethodDescDaoConfig, this);

        registerDao(SubPlayMethod.class, subPlayMethodDao);
        registerDao(SubPlayMethodDesc.class, subPlayMethodDescDao);
    }
    
    public void clear() {
        subPlayMethodDaoConfig.clearIdentityScope();
        subPlayMethodDescDaoConfig.clearIdentityScope();
    }

    public SubPlayMethodDao getSubPlayMethodDao() {
        return subPlayMethodDao;
    }

    public SubPlayMethodDescDao getSubPlayMethodDescDao() {
        return subPlayMethodDescDao;
    }

}
