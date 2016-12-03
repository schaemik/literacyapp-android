package org.literacyapp.dao;

import java.util.List;
import java.util.ArrayList;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.SqlUtils;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.Calendar;
import org.literacyapp.dao.converter.CalendarConverter;
import org.literacyapp.model.Device;
import org.literacyapp.model.StudentImageFeature;

import org.literacyapp.model.StudentImage;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "STUDENT_IMAGE".
*/
public class StudentImageDao extends AbstractDao<StudentImage, Long> {

    public static final String TABLENAME = "STUDENT_IMAGE";

    /**
     * Properties of entity StudentImage.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property TimeCollected = new Property(1, long.class, "timeCollected", false, "TIME_COLLECTED");
        public final static Property ImageFileUrl = new Property(2, String.class, "imageFileUrl", false, "IMAGE_FILE_URL");
        public final static Property StudentImageFeature = new Property(3, Long.class, "studentImageFeature", false, "STUDENT_IMAGE_FEATURE");
    }

    private DaoSession daoSession;

    private final CalendarConverter timeCollectedConverter = new CalendarConverter();
    private Query<StudentImage> studentImageCollectionEvent_StudentImagesQuery;

    public StudentImageDao(DaoConfig config) {
        super(config);
    }
    
    public StudentImageDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"STUDENT_IMAGE\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"TIME_COLLECTED\" INTEGER NOT NULL ," + // 1: timeCollected
                "\"IMAGE_FILE_URL\" TEXT NOT NULL ," + // 2: imageFileUrl
                "\"STUDENT_IMAGE_FEATURE\" INTEGER);"); // 3: studentImageFeature
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"STUDENT_IMAGE\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, StudentImage entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, timeCollectedConverter.convertToDatabaseValue(entity.getTimeCollected()));
        stmt.bindString(3, entity.getImageFileUrl());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, StudentImage entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, timeCollectedConverter.convertToDatabaseValue(entity.getTimeCollected()));
        stmt.bindString(3, entity.getImageFileUrl());
    }

    @Override
    protected final void attachEntity(StudentImage entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public StudentImage readEntity(Cursor cursor, int offset) {
        StudentImage entity = new StudentImage( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            timeCollectedConverter.convertToEntityProperty(cursor.getLong(offset + 1)), // timeCollected
            cursor.getString(offset + 2) // imageFileUrl
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, StudentImage entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setTimeCollected(timeCollectedConverter.convertToEntityProperty(cursor.getLong(offset + 1)));
        entity.setImageFileUrl(cursor.getString(offset + 2));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(StudentImage entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(StudentImage entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(StudentImage entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "studentImages" to-many relationship of StudentImageCollectionEvent. */
    public List<StudentImage> _queryStudentImageCollectionEvent_StudentImages(Long id) {
        synchronized (this) {
            if (studentImageCollectionEvent_StudentImagesQuery == null) {
                QueryBuilder<StudentImage> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.Id.eq(null));
                studentImageCollectionEvent_StudentImagesQuery = queryBuilder.build();
            }
        }
        Query<StudentImage> query = studentImageCollectionEvent_StudentImagesQuery.forCurrentThread();
        query.setParameter(0, id);
        return query.list();
    }

    private String selectDeep;

    protected String getSelectDeep() {
        if (selectDeep == null) {
            StringBuilder builder = new StringBuilder("SELECT ");
            SqlUtils.appendColumns(builder, "T", getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T0", daoSession.getDeviceDao().getAllColumns());
            builder.append(',');
            SqlUtils.appendColumns(builder, "T1", daoSession.getStudentImageFeatureDao().getAllColumns());
            builder.append(" FROM STUDENT_IMAGE T");
            builder.append(" LEFT JOIN DEVICE T0 ON T.\"_id\"=T0.\"_id\"");
            builder.append(" LEFT JOIN STUDENT_IMAGE_FEATURE T1 ON T.\"STUDENT_IMAGE_FEATURE\"=T1.\"_id\"");
            builder.append(' ');
            selectDeep = builder.toString();
        }
        return selectDeep;
    }
    
    protected StudentImage loadCurrentDeep(Cursor cursor, boolean lock) {
        StudentImage entity = loadCurrent(cursor, 0, lock);
        int offset = getAllColumns().length;

        Device device = loadCurrentOther(daoSession.getDeviceDao(), cursor, offset);
        entity.setDevice(device);
        offset += daoSession.getDeviceDao().getAllColumns().length;

        StudentImageFeature studentImageFeature = loadCurrentOther(daoSession.getStudentImageFeatureDao(), cursor, offset);
        entity.setStudentImageFeature(studentImageFeature);

        return entity;    
    }

    public StudentImage loadDeep(Long key) {
        assertSinglePk();
        if (key == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder(getSelectDeep());
        builder.append("WHERE ");
        SqlUtils.appendColumnsEqValue(builder, "T", getPkColumns());
        String sql = builder.toString();
        
        String[] keyArray = new String[] { key.toString() };
        Cursor cursor = db.rawQuery(sql, keyArray);
        
        try {
            boolean available = cursor.moveToFirst();
            if (!available) {
                return null;
            } else if (!cursor.isLast()) {
                throw new IllegalStateException("Expected unique result, but count was " + cursor.getCount());
            }
            return loadCurrentDeep(cursor, true);
        } finally {
            cursor.close();
        }
    }
    
    /** Reads all available rows from the given cursor and returns a list of new ImageTO objects. */
    public List<StudentImage> loadAllDeepFromCursor(Cursor cursor) {
        int count = cursor.getCount();
        List<StudentImage> list = new ArrayList<StudentImage>(count);
        
        if (cursor.moveToFirst()) {
            if (identityScope != null) {
                identityScope.lock();
                identityScope.reserveRoom(count);
            }
            try {
                do {
                    list.add(loadCurrentDeep(cursor, false));
                } while (cursor.moveToNext());
            } finally {
                if (identityScope != null) {
                    identityScope.unlock();
                }
            }
        }
        return list;
    }
    
    protected List<StudentImage> loadDeepAllAndCloseCursor(Cursor cursor) {
        try {
            return loadAllDeepFromCursor(cursor);
        } finally {
            cursor.close();
        }
    }
    

    /** A raw-style query where you can pass any WHERE clause and arguments. */
    public List<StudentImage> queryDeep(String where, String... selectionArg) {
        Cursor cursor = db.rawQuery(getSelectDeep() + where, selectionArg);
        return loadDeepAllAndCloseCursor(cursor);
    }
 
}
