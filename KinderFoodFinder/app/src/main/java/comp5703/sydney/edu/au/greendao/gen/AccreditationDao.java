package comp5703.sydney.edu.au.greendao.gen;

import java.util.List;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.Accreditation;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "ACCREDITATION".
*/
public class AccreditationDao extends AbstractDao<Accreditation, Long> {

    public static final String TABLENAME = "ACCREDITATION";

    /**
     * Properties of entity Accreditation.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Sid = new Property(1, String.class, "sid", false, "SID");
        public final static Property ParentId = new Property(2, Long.class, "parentId", false, "PARENT_ID");
        public final static Property Accreditation = new Property(3, String.class, "Accreditation", false, "ACCREDITATION");
        public final static Property Rating = new Property(4, String.class, "Rating", false, "RATING");
    }

    private Query<Accreditation> product_AccreditationQuery;

    public AccreditationDao(DaoConfig config) {
        super(config);
    }
    
    public AccreditationDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"ACCREDITATION\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"SID\" TEXT," + // 1: sid
                "\"PARENT_ID\" INTEGER," + // 2: parentId
                "\"ACCREDITATION\" TEXT," + // 3: Accreditation
                "\"RATING\" TEXT);"); // 4: Rating
        // Add Indexes
        db.execSQL("CREATE UNIQUE INDEX " + constraint + "IDX_ACCREDITATION_SID ON \"ACCREDITATION\"" +
                " (\"SID\" ASC);");
        db.execSQL("CREATE UNIQUE INDEX " + constraint + "IDX_ACCREDITATION_PARENT_ID ON \"ACCREDITATION\"" +
                " (\"PARENT_ID\" ASC);");
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"ACCREDITATION\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Accreditation entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String sid = entity.getSid();
        if (sid != null) {
            stmt.bindString(2, sid);
        }
 
        Long parentId = entity.getParentId();
        if (parentId != null) {
            stmt.bindLong(3, parentId);
        }
 
        String Accreditation = entity.getAccreditation();
        if (Accreditation != null) {
            stmt.bindString(4, Accreditation);
        }
 
        String Rating = entity.getRating();
        if (Rating != null) {
            stmt.bindString(5, Rating);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Accreditation entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String sid = entity.getSid();
        if (sid != null) {
            stmt.bindString(2, sid);
        }
 
        Long parentId = entity.getParentId();
        if (parentId != null) {
            stmt.bindLong(3, parentId);
        }
 
        String Accreditation = entity.getAccreditation();
        if (Accreditation != null) {
            stmt.bindString(4, Accreditation);
        }
 
        String Rating = entity.getRating();
        if (Rating != null) {
            stmt.bindString(5, Rating);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public Accreditation readEntity(Cursor cursor, int offset) {
        Accreditation entity = new Accreditation( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // sid
            cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2), // parentId
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // Accreditation
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4) // Rating
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Accreditation entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setSid(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setParentId(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
        entity.setAccreditation(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setRating(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Accreditation entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Accreditation entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Accreditation entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "Accreditation" to-many relationship of Product. */
    public List<Accreditation> _queryProduct_Accreditation(Long parentId) {
        synchronized (this) {
            if (product_AccreditationQuery == null) {
                QueryBuilder<Accreditation> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.ParentId.eq(null));
                product_AccreditationQuery = queryBuilder.build();
            }
        }
        Query<Accreditation> query = product_AccreditationQuery.forCurrentThread();
        query.setParameter(0, parentId);
        return query.list();
    }

}
