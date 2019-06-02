package comp5703.sydney.edu.au.greendao.gen;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.Product;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.Accreditation;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.AccEntity;

import comp5703.sydney.edu.au.greendao.gen.ProductDao;
import comp5703.sydney.edu.au.greendao.gen.AccreditationDao;
import comp5703.sydney.edu.au.greendao.gen.AccEntityDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig productDaoConfig;
    private final DaoConfig accreditationDaoConfig;
    private final DaoConfig accEntityDaoConfig;

    private final ProductDao productDao;
    private final AccreditationDao accreditationDao;
    private final AccEntityDao accEntityDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        productDaoConfig = daoConfigMap.get(ProductDao.class).clone();
        productDaoConfig.initIdentityScope(type);

        accreditationDaoConfig = daoConfigMap.get(AccreditationDao.class).clone();
        accreditationDaoConfig.initIdentityScope(type);

        accEntityDaoConfig = daoConfigMap.get(AccEntityDao.class).clone();
        accEntityDaoConfig.initIdentityScope(type);

        productDao = new ProductDao(productDaoConfig, this);
        accreditationDao = new AccreditationDao(accreditationDaoConfig, this);
        accEntityDao = new AccEntityDao(accEntityDaoConfig, this);

        registerDao(Product.class, productDao);
        registerDao(Accreditation.class, accreditationDao);
        registerDao(AccEntity.class, accEntityDao);
    }
    
    public void clear() {
        productDaoConfig.clearIdentityScope();
        accreditationDaoConfig.clearIdentityScope();
        accEntityDaoConfig.clearIdentityScope();
    }

    public ProductDao getProductDao() {
        return productDao;
    }

    public AccreditationDao getAccreditationDao() {
        return accreditationDao;
    }

    public AccEntityDao getAccEntityDao() {
        return accEntityDao;
    }

}
