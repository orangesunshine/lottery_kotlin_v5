package com.bdb.lottery.database.lot.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

import com.orange.bdb.DaoSession;
import com.orange.bdb.SubPlayMethodDescDao;
import com.orange.bdb.SubPlayMethodDao;

//active:为true可以增删改，@Transient 表示不存储在数据库中
@Entity(nameInDb = "Sub_play_method", createInDb = false)
public class SubPlayMethod {
    @Id
    @Property(nameInDb = "row_id")
    private Long row_id;
    @NotNull
    private int play_method_id;
    @NotNull
    private long method_desc_id;
    private int parent_play_method;
    private String belongto;
    private boolean enable;
    @ToOne(joinProperty = "method_desc_id")
    private SubPlayMethodDesc subPlayMethodDesc;
    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /**
     * Used for active entity operations.
     */
    @Generated(hash = 1771332282)
    private transient SubPlayMethodDao myDao;

    @Generated(hash = 62422120)
    public SubPlayMethod(Long row_id, int play_method_id, long method_desc_id,
                         int parent_play_method, String belongto, boolean enable) {
        this.row_id = row_id;
        this.play_method_id = play_method_id;
        this.method_desc_id = method_desc_id;
        this.parent_play_method = parent_play_method;
        this.belongto = belongto;
        this.enable = enable;
    }

    @Generated(hash = 2022142516)
    public SubPlayMethod() {
    }

    @Generated(hash = 396724662)
    private transient Long subPlayMethodDesc__resolvedKey;

    public Long getRow_id() {
        return row_id;
    }

    public void setRow_id(Long row_id) {
        this.row_id = row_id;
    }

    public int getPlay_method_id() {
        return play_method_id;
    }

    public void setPlay_method_id(int play_method_id) {
        this.play_method_id = play_method_id;
    }

    public long getMethod_desc_id() {
        return method_desc_id;
    }

    public void setMethod_desc_id(long method_desc_id) {
        this.method_desc_id = method_desc_id;
    }

    public int getParent_play_method() {
        return parent_play_method;
    }

    public void setParent_play_method(int parent_play_method) {
        this.parent_play_method = parent_play_method;
    }

    public String getBelongto() {
        return belongto;
    }

    public void setBelongto(String belongto) {
        this.belongto = belongto;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    @Override
    public String toString() {
        return "SubPlayMethod{" +
                "row_id=" + row_id +
                ", play_method_id=" + play_method_id +
                ", method_desc_id=" + method_desc_id +
                ", parent_play_method=" + parent_play_method +
                ", belongto='" + belongto + '\'' +
                ", enable=" + enable +
                ", subPlayMethodDesc_get=" + getSubPlayMethodDesc() +
                '}';
    }

    public boolean getEnable() {
        return this.enable;
    }

    /**
     * To-one relationship, resolved on first access.
     */
    @Generated(hash = 1347186837)
    public SubPlayMethodDesc getSubPlayMethodDesc() {
        long __key = this.method_desc_id;
        if (subPlayMethodDesc__resolvedKey == null
                || !subPlayMethodDesc__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            SubPlayMethodDescDao targetDao = daoSession.getSubPlayMethodDescDao();
            SubPlayMethodDesc subPlayMethodDescNew = targetDao.load(__key);
            synchronized (this) {
                subPlayMethodDesc = subPlayMethodDescNew;
                subPlayMethodDesc__resolvedKey = __key;
            }
        }
        return subPlayMethodDesc;
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 395912771)
    public void setSubPlayMethodDesc(@NotNull SubPlayMethodDesc subPlayMethodDesc) {
        if (subPlayMethodDesc == null) {
            throw new DaoException(
                    "To-one property 'method_desc_id' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.subPlayMethodDesc = subPlayMethodDesc;
            method_desc_id = subPlayMethodDesc.getRow_id();
            subPlayMethodDesc__resolvedKey = method_desc_id;
        }
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 299391704)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getSubPlayMethodDao() : null;
    }
}