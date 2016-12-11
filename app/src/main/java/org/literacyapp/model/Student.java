package org.literacyapp.model;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.JoinEntity;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Unique;
import org.literacyapp.dao.DaoSession;
import org.literacyapp.dao.DeviceDao;
import org.literacyapp.dao.StudentDao;
import org.literacyapp.dao.StudentImageDao;

import java.util.List;

/**
 * Based on {@link org.literacyapp.model.gson.StudentGson}
 */
@Entity
public class Student {

    @Id(autoincrement = true)
    private Long id;

    @NotNull
    @Unique
    private String uniqueId; // "<deviceId>_<Long>"

    @ToMany
    @JoinEntity(entity = JoinStudentsWithDevices.class, sourceProperty = "studentId", targetProperty = "deviceId")
    private List<Device> devices;

    private long studentImageId;

    @NotNull
    @ToOne(joinProperty = "studentImageId")
    private StudentImage avatar;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1943931642)
    private transient StudentDao myDao;

    @Generated(hash = 969166306)
    public Student(Long id, @NotNull String uniqueId, long studentImageId) {
        this.id = id;
        this.uniqueId = uniqueId;
        this.studentImageId = studentImageId;
    }

    @Generated(hash = 1556870573)
    public Student() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUniqueId() {
        return this.uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    @Generated(hash = 1907406681)
    private transient Long avatar__resolvedKey;

    /** To-one relationship, resolved on first access. */
    @Generated(hash = 2064991221)
    public StudentImage getAvatar() {
        long __key = this.studentImageId;
        if (avatar__resolvedKey == null || !avatar__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            StudentImageDao targetDao = daoSession.getStudentImageDao();
            StudentImage avatarNew = targetDao.load(__key);
            synchronized (this) {
                avatar = avatarNew;
                avatar__resolvedKey = __key;
            }
        }
        return avatar;
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 822529931)
    public void setAvatar(@NotNull StudentImage avatar) {
        if (avatar == null) {
            throw new DaoException(
                    "To-one property 'studentImageId' has not-null constraint; cannot set to-one to null");
        }
        synchronized (this) {
            this.avatar = avatar;
            studentImageId = avatar.getId();
            avatar__resolvedKey = studentImageId;
        }
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 58270754)
    public List<Device> getDevices() {
        if (devices == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DeviceDao targetDao = daoSession.getDeviceDao();
            List<Device> devicesNew = targetDao._queryStudent_Devices(id);
            synchronized (this) {
                if (devices == null) {
                    devices = devicesNew;
                }
            }
        }
        return devices;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1428662284)
    public synchronized void resetDevices() {
        devices = null;
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

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1701634981)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getStudentDao() : null;
    }

    public long getStudentImageId() {
        return this.studentImageId;
    }

    public void setStudentImageId(long studentImageId) {
        this.studentImageId = studentImageId;
    }
}
