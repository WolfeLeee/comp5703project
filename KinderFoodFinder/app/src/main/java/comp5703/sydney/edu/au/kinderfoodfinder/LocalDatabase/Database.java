package comp5703.sydney.edu.au.kinderfoodfinder.LocalDatabase;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

import comp5703.sydney.edu.au.kinderfoodfinder.Model.Product_Info;

public class Database extends SQLiteAssetHelper {

    private static final String DB_NAME = "products.db";
    private static final int DB_VERSION = 1;
    private static final String DB_PATH ="/data/data/comp5703.sydney.edu.au.kinderfoodfinder/databases/";

    public Database(Context context) {
        super(context, DB_NAME, null,DB_VERSION);
    }

    // Function get all products
    public List<Product_Info> getProducts()
    {

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        //Make sure all is column name in your table
        String[] sqlSelect = {"product_id","Brand_Name","Category","Accreditation","Rating","Location"};
        String tableName = "products";

        qb.setTables(tableName);
        Cursor cursor = qb.query(db,sqlSelect,null,null,null,null,null);
        List<Product_Info> result = new ArrayList<>();
        if(cursor.moveToFirst())
        {
            do{
                Product_Info product = new Product_Info();
                product.setId(cursor.getString(cursor.getColumnIndex("product_id")));
                product.setCategory(cursor.getString(cursor.getColumnIndex("Category")));
                product.setAccreditation(cursor.getString(cursor.getColumnIndex("Accreditation")));
                product.setBrand_name(cursor.getString(cursor.getColumnIndex("Brand_Name")));
                product.setRating(cursor.getString(cursor.getColumnIndex("Rating")));
                product.setLocation(cursor.getString(cursor.getColumnIndex("Location")));
                result.add(product);
            }while(cursor.moveToNext());
        }
        return result;
    }

    //Function get all product's brand name
    public List<String> getBrandName()
    {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        //Make sure all is column name in your table
        String[] sqlSelect = {"Brand_Name"};
        String tableName = "products";

        qb.setTables(tableName);
        Cursor cursor = qb.query(db,sqlSelect,null,null,null,null,null);
        List<String> result = new ArrayList<>();
        if(cursor.moveToFirst())
        {
            do{
                result.add(cursor.getString(cursor.getColumnIndex("Brand_Name")));
            }while(cursor.moveToNext());
        }
        return result;
    }

    // Function get brand name by Eggs
    public List<Product_Info> getBrandByEggs()
    {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        //Make sure all is column name in your table
        String[] sqlSelect = {"product_id","Brand_Name","Category","Accreditation","Rating","Location"};
        String tableName = "products";

        qb.setTables(tableName);

        Cursor cursor = qb.query(db,sqlSelect,"Category=?",new String[]{"Eggs"},null,null,null);
        List<Product_Info> result = new ArrayList<>();
        if(cursor.moveToFirst())
        {
            do{
                Product_Info product = new Product_Info();
                product.setId(cursor.getString(cursor.getColumnIndex("product_id")));
                product.setBrand_name(cursor.getString(cursor.getColumnIndex("Brand_Name")));
                product.setCategory(cursor.getString(cursor.getColumnIndex("Category")));
                result.add(product);
            }while(cursor.moveToNext());
        }
        return result;
    }

    // Function get brand name by Chicken
    public List<Product_Info> getBrandByChicken()
    {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        //Make sure all is column name in your table
        String[] sqlSelect = {"product_id","Brand_Name","Category","Accreditation","Rating","Location"};
        String tableName = "products";

        qb.setTables(tableName);

        Cursor cursor = qb.query(db,sqlSelect,"Category=?",new String[]{"Chicken"},null,null,null);
        List<Product_Info> result = new ArrayList<>();
        if(cursor.moveToFirst())
        {
            do{
                Product_Info product = new Product_Info();
                product.setId(cursor.getString(cursor.getColumnIndex("product_id")));
                product.setBrand_name(cursor.getString(cursor.getColumnIndex("Brand_Name")));
                product.setCategory(cursor.getString(cursor.getColumnIndex("Category")));
                result.add(product);
            }while(cursor.moveToNext());
        }
        return result;
    }

    // Function get brand name by Pork
    public List<Product_Info> getBrandByPork()
    {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        //Make sure all is column name in your table
        String[] sqlSelect = {"product_id","Brand_Name","Category","Accreditation","Rating","Location"};
        String tableName = "products";

        qb.setTables(tableName);

        Cursor cursor = qb.query(db,sqlSelect,"Category=?",new String[]{"Pork"},null,null,null);
        List<Product_Info> result = new ArrayList<>();
        if(cursor.moveToFirst())
        {
            do{
                Product_Info product = new Product_Info();
                product.setId(cursor.getString(cursor.getColumnIndex("product_id")));
                product.setBrand_name(cursor.getString(cursor.getColumnIndex("Brand_Name")));
                product.setCategory(cursor.getString(cursor.getColumnIndex("Category")));
                result.add(product);
            }while(cursor.moveToNext());
        }
        return result;
    }

    // Function get product by brand name
    public List<Product_Info> getProductByBrandName(String brand_name)
    {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        //Make sure all is column name in your table
        String[] sqlSelect = {"product_id","Brand_Name","Category","Accreditation","Rating","Location"};
        String tableName = "products";

        qb.setTables(tableName);

        Cursor cursor = qb.query(db,sqlSelect,"Brand_Name LIKE ?",new String[]{"%"+brand_name+"%"},null,null,null);
        List<Product_Info> result = new ArrayList<>();
        if(cursor.moveToFirst())
        {
            do{
                Product_Info product = new Product_Info();
                product.setId(cursor.getString(cursor.getColumnIndex("product_id")));
                product.setBrand_name(cursor.getString(cursor.getColumnIndex("Brand_Name")));
                product.setCategory(cursor.getString(cursor.getColumnIndex("Category")));
                product.setAccreditation(cursor.getString(cursor.getColumnIndex("Accreditation")));
                product.setRating(cursor.getString(cursor.getColumnIndex("Rating")));
                product.setLocation(cursor.getString(cursor.getColumnIndex("Location")));
                result.add(product);
            }while(cursor.moveToNext());
        }
        return result;
    }

}
