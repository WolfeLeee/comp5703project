package comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase;





import org.greenrobot.greendao.query.Join;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import comp5703.sydney.edu.au.greendao.gen.AccreditationDao;
import comp5703.sydney.edu.au.greendao.gen.ProductDao;
import comp5703.sydney.edu.au.kinderfoodfinder.R;


public class DaoUnit {

    private static final String SQL_DISTINCT_ENAME = "SELECT DISTINCT "+AccreditationDao.Properties.Accreditation.columnName+" FROM "+AccreditationDao.TABLENAME;

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
        String cotegory = "";
        switch (type)
        {
        }
        if (Catogry == R.id.radioBrandName)productJoin.whereOr(ProductDao.Properties.Category.eq(cotegory),ProductDao.Properties.Brand_Name.like(searchKey));
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
    ArrayList<Product>covertproducts(List<Product>ps)
    {
        ArrayList<Product>productsArrayList = new ArrayList<>();
        for (Product p:ps)
        {
            p.getAccreditation();
            productsArrayList.add(p);
        }
        return productsArrayList;
    }

    ArrayList<Product>covertAccs(List<Accreditation>acc)
    {
        QueryBuilder productBuilder=productManager.queryBuilder();
        ArrayList<Product>productsArrayList = new ArrayList<>();
//        for (Accreditation p:acc) {
//            ArrayList<Product> test= (ArrayList<Product>) productBuilder.where( ProductDao.Properties.Id.eq( p.getParentId()) ).list();
//            for( Product t:test){
//                productsArrayList.add(t);
//            }
//        }
        Product product=new Product(  );

        for(Accreditation m:acc){
            productsArrayList.add( searchById( m.getParentId() ) );
        }
        return productsArrayList;
    }

    public ArrayList<Product> searchByOption(int Catogry, int type, String searchKey)
    {
        QueryBuilder productBuilder = productManager.queryBuilder();
        switch (type)
        {
        }
        if (Catogry == R.id.radioBrandName)productBuilder.where(ProductDao.Properties.Brand_Name.like(searchKey+"%"));
        //
        Join join= productBuilder.join(ProductDao.Properties.Id,Accreditation.class,AccreditationDao.Properties.ParentId);
        if (Catogry == R.id.radioAccreditation)join.where(AccreditationDao.Properties.Accreditation.like(searchKey+"%"));
        return covertproducts(productBuilder.list());
    }

    public ArrayList<Product> searchByAcc(int type, String category, String searchKey)
    {
        QueryBuilder productBuilder = productManager.queryBuilder();
        productBuilder.where(ProductDao.Properties.Category.eq(category));


        if ( type == R.id.radioBrandName)productBuilder.where(ProductDao.Properties.Brand_Name.like(searchKey+"%"));
        //
        Join join= productBuilder.join(ProductDao.Properties.Id,Accreditation.class,AccreditationDao.Properties.ParentId);
        if (type == R.id.radioAccreditation)join.where(AccreditationDao.Properties.Accreditation.like(searchKey+"%"));
        return covertproducts(productBuilder.list());
    }

    public ArrayList<Product> searchByBrand(int type,String category,String searchKey){
        QueryBuilder productBuilder = productManager.queryBuilder();
        productBuilder.where(ProductDao.Properties.Category.eq(category));
        if ( type == R.id.radioBrandName)productBuilder.where(ProductDao.Properties.Brand_Name.like(searchKey+"%"));
        ArrayList<Product> productArrayList= (ArrayList<Product>) productBuilder.list();
        return productArrayList;

    }

    public ArrayList<Product> getcategoryList(String type){
        QueryBuilder productBuilder=productManager.queryBuilder();
        productBuilder.where( ProductDao.Properties.Category.like( "%"+type+"%" ) );
        Join join= productBuilder.join(ProductDao.Properties.Id,Accreditation.class,AccreditationDao.Properties.ParentId);
        return covertproducts( productBuilder.list() );
    }

    public ArrayList<Product> getRatingList(String type){

        QueryBuilder accBuilder=accreditationManager.queryBuilder();
        accBuilder.where( AccreditationDao.Properties.Rating.eq( type ) );
        Join join=accBuilder.join( AccreditationDao.Properties.ParentId,Product.class,ProductDao.Properties.Id );
        return covertAccs( accBuilder.list() );
    }

    public ArrayList<Product> getAccList(String type){

        QueryBuilder accBuilder=accreditationManager.queryBuilder();
        accBuilder.where( AccreditationDao.Properties.Accreditation.eq( type ) );
        Join join=accBuilder.join( AccreditationDao.Properties.ParentId,Product.class,ProductDao.Properties.Id );
        return covertAccs( accBuilder.list() );
    }



    public Product getProduct(long id, String sid){

        QueryBuilder accBuilder = accreditationManager.queryBuilder();
        Join productJoin = accBuilder.join(AccreditationDao.Properties.ParentId,Product.class,ProductDao.Properties.Id);
        ProductDao productDao=MyApplication.getInstance().getDaoSession().getProductDao();
        Product product=new Product(  );
        return product;
    }

    public Product getProductAccc(long id){

        QueryBuilder accBuilder = accreditationManager.queryBuilder();
        Join productJoin = accBuilder.join(AccreditationDao.Properties.ParentId,Product.class,ProductDao.Properties.Id);
        ProductDao productDao=MyApplication.getInstance().getDaoSession().getProductDao();
        Product product=new Product( );

        return product;
    }



    public Product searchById(long sid)
    {
        QueryBuilder productBuilder= productManager.queryBuilder().where( ProductDao.Properties.Id.eq(sid));
        productBuilder.join(ProductDao.Properties.Id,Accreditation.class,AccreditationDao.Properties.ParentId);
        Product product=new Product(  );
        ArrayList<Product>productsArrayList = (ArrayList<Product>) productBuilder.list();
        product=productsArrayList.get( 0 );

        return product;
    }

    public Product searchBySid(String sid)
    {
        QueryBuilder productBuilder= productManager.queryBuilder().where( ProductDao.Properties.Sid.eq(sid));
        productBuilder.join(ProductDao.Properties.Id,Accreditation.class,AccreditationDao.Properties.ParentId);
        Product product=new Product(  );
        ArrayList<Product>productsArrayList = (ArrayList<Product>) productBuilder.list();
        product=productsArrayList.get( 0 );

        return product;
    }

    public Accreditation searchAccBySid(String sid){
        QueryBuilder accBuilder= accreditationManager.queryBuilder().where( AccreditationDao.Properties.Sid.eq(sid));

        Accreditation accreditation= (Accreditation) accBuilder.list().get( 0 );
        return accreditation;
    }

    public ArrayList<Accreditation> getAccByParentId(long sid)
    {
        QueryBuilder productBuilder= productManager.queryBuilder().where( ProductDao.Properties.Id.eq(sid));
        QueryBuilder accBuilder=accreditationManager.queryBuilder().where( AccreditationDao.Properties.ParentId.eq( sid ) );


        return (ArrayList<Accreditation>) accBuilder.list();
    }

    public ArrayList<Accreditation> getAcc(){
        ArrayList<Product>productsArrayList = new ArrayList<>();

       QueryBuilder accBuilder=accreditationManager.queryBuilder();

       accBuilder.where( AccreditationDao.Properties.Accreditation.isNotNull() );

       return (ArrayList<Accreditation>) accBuilder.list();

   }

   public ArrayList<Product> getProduct(){
        ArrayList<Product> productArrayList=new ArrayList<>(  );
        productArrayList= (ArrayList<Product>) productManager.loadAll();
        return productArrayList;
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
