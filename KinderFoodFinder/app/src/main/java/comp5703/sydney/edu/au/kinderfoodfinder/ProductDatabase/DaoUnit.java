package comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase;





import android.app.ActivityManager;
import android.database.Cursor;
import android.util.Log;



import org.greenrobot.greendao.query.Join;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import comp5703.sydney.edu.au.greendao.gen.AccreditationDao;
import comp5703.sydney.edu.au.greendao.gen.ProductDao;
import comp5703.sydney.edu.au.kinderfoodfinder.MainActivity;
import comp5703.sydney.edu.au.kinderfoodfinder.R;


public class DaoUnit {
    private static final DaoUnit ourInstance = new DaoUnit();
    private ProductDao productManager;
    private AccreditationDao accreditationManager;
    public static DaoUnit getInstance() {
        return ourInstance;
    }

    private DaoUnit() {
        productManager = MyApplication.getInstance().getDaoSession().getProductDao();
        accreditationManager = MyApplication.getInstance().getDaoSession().getAccreditationDao();
    }
    //search by category or accreditation
    List<Product> searchByCatogry(int Catogry, int type, String searchKey)
    {
        QueryBuilder accBuilder = accreditationManager.queryBuilder();
        if (Catogry == R.id.radioAccreditation)accBuilder.where(AccreditationDao.Properties.Accreditation.like(searchKey));
        Join productJoin = accBuilder.join(AccreditationDao.Properties.ParentId,Product.class,ProductDao.Properties.Id);

//        try{
//            productJoin.where(productsDao.Properties.Id.eq(accreditationDao.Properties.ParentId));
//        } catch (Exception err) {
//            Log.e("Cuowu",err.getMessage());
//        }
        String ltyp = "";
        switch (type)
        {
            case R.id.radioPig:
                ltyp="pig";
                //productJoin.where(productsDao.Properties.Category.eq("pig"));
                break;
            case R.id.radioEgg:
                ltyp = "egg";
                //productJoin.where(productsDao.Properties.Category.eq("egg"));
                break;
            case R.id.radioChicken:
                ltyp = "chicken";
                //productJoin.where(productsDao.Properties.Category.eq("chicken"));
                break;
        }
        if (Catogry == R.id.radioBrandName)productJoin.whereOr(ProductDao.Properties.Category.eq(ltyp),ProductDao.Properties.Brand_Name.like(searchKey));
        return accBuilder.list();
    }
    //insert data
    long insertProduct(Product product)
    {
        return productManager.insert(product);
    }
    //插入认证
    Long insertAccreditation(Accreditation acc)
    {
        return accreditationManager.insert(acc);
    }

    //get Accreditation dat
    ArrayList<Product>covert2products(List<Product>ps)
    {
        ArrayList<Product>productsArrayList = new ArrayList<>();
        for (Product p:ps)
        {
            p.getAccreditation();
            productsArrayList.add(p);
        }
        return productsArrayList;
    }

    public ArrayList<Product> searchByOption(int Catogry, int type, String searchKey)
    {
        QueryBuilder productBuilder = productManager.queryBuilder();
        switch (type)
        {
            case R.id.radioPig:
                productBuilder.where(ProductDao.Properties.Category.eq("pig"));
                break;
            case R.id.radioEgg:
                productBuilder.where(ProductDao.Properties.Category.eq("egg"));
                break;
            case R.id.radioChicken:
                productBuilder.where(ProductDao.Properties.Category.eq("chicken"));
                break;
        }
        if (Catogry == R.id.radioBrandName)productBuilder.where(ProductDao.Properties.Brand_Name.like("%"+searchKey+"%"));
        //
        Join join= productBuilder.join(ProductDao.Properties.Id,Accreditation.class,AccreditationDao.Properties.ParentId);
        if (Catogry == R.id.radioAccreditation)join.where(AccreditationDao.Properties.Accreditation.like("%"+searchKey+"%"));
        return covert2products(productBuilder.list());
    }

    public ArrayList<Product> getcategoryList(String type){

        QueryBuilder productBuilder=productManager.queryBuilder();
        productBuilder.where( ProductDao.Properties.Category.like( "%"+type+"%" ) );
        Join join= productBuilder.join(ProductDao.Properties.Id,Accreditation.class,AccreditationDao.Properties.ParentId);
        return covert2products( productBuilder.list() );
    }

    public ArrayList<Product> getRatingList(String type){

        QueryBuilder accBuilder=accreditationManager.queryBuilder();
        accBuilder.where( AccreditationDao.Properties.Rating.eq( type ) );
        Join join=accBuilder.join( AccreditationDao.Properties.ParentId,Product.class,ProductDao.Properties.Id );
        return covert2products( accBuilder.list() );
    }



    public Product getProduct(long id, String sid){

        QueryBuilder accBuilder = accreditationManager.queryBuilder();
        Join productJoin = accBuilder.join(AccreditationDao.Properties.ParentId,Product.class,ProductDao.Properties.Id);
        ProductDao productDao=MyApplication.getInstance().getDaoSession().getProductDao();
        Product product=new Product(  );
        return product;
    }




    public void clearProductsTable()
    {
        productManager.deleteAll();
    }
    //清空 accreditation表
    public void clearAccreditationTable()
    {
        accreditationManager.deleteAll();
    }

}
