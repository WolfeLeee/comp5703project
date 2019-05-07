package comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

import comp5703.sydney.edu.au.greendao.gen.DaoSession;
import org.greenrobot.greendao.DaoException;
import comp5703.sydney.edu.au.greendao.gen.AccreditationDao;
import comp5703.sydney.edu.au.greendao.gen.ProductDao;

@Entity
public class Product {

    @Id(autoincrement = true)
    private Long id;
    @Index(unique = true)
    private String sid;
    private String Brand_Name;
    private String Available;
    private String Category;
    @ToMany(referencedJoinProperty = "parentId")
    private List<Accreditation> Accreditation;
    private String Image;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 694336451)
    private transient ProductDao myDao;


    @Generated(hash = 1311468470)
    public Product(Long id, String sid, String Brand_Name, String Available, String Category,
            String Image) {
        this.id = id;
        this.sid = sid;
        this.Brand_Name = Brand_Name;
        this.Available = Available;
        this.Category = Category;
        this.Image = Image;
    }

    @Generated(hash = 1890278724)
    public Product() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSid() {
        return this.sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getBrand_Name() {
        return this.Brand_Name;
    }

    public void setBrand_Name(String Brand_Name) {
        this.Brand_Name = Brand_Name;
    }

    public String getAvailable() {
        return this.Available;
    }

    public void setAvailable(String Available) {
        this.Available = Available;
    }

    public String getCategory() {
        return this.Category;
    }

    public void setCategory(String Category) {
        this.Category = Category;
    }

    public String getImage() {
        return this.Image;
    }

    public void setImage(String Image) {
        this.Image = Image;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 390777421)
    public List<Accreditation> getAccreditation() {
        if (Accreditation == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            AccreditationDao targetDao = daoSession.getAccreditationDao();
            List<Accreditation> AccreditationNew = targetDao._queryProduct_Accreditation(id);
            synchronized (this) {
                if (Accreditation == null) {
                    Accreditation = AccreditationNew;
                }
            }
        }
        return Accreditation;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1355846165)
    public synchronized void resetAccreditation() {
        Accreditation = null;
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
    @Generated(hash = 1171535257)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getProductDao() : null;
    }
}
