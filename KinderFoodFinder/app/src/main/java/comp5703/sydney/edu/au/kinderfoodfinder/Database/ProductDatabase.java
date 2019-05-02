package comp5703.sydney.edu.au.kinderfoodfinder.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import comp5703.sydney.edu.au.kinderfoodfinder.LocalDatabase.ProductsContract;

public class ProductDatabase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "kkf_database";
    public static final int DATABASE_VERSION=1;



    public static final String CREATE_TABLE="create table "+ ProductContract.ProductEntry.TABLE_NAME+"("+
            ProductContract.ProductEntry.PRODUCT_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"+ProductContract.ProductEntry.BRAND_NAME+" TEXT,"
            +ProductContract.ProductEntry.ACCREDITATION+" TEXT,"+ProductContract.ProductEntry.RATING+" TEXT,"
            +ProductContract.ProductEntry.AVAILABLE+" TEXT,"+ProductContract.ProductEntry.CATEGORY+" TEXT);";
    public static final String DROP_TABLE="drop table if exists "+ProductContract.ProductEntry.TABLE_NAME;


    public ProductDatabase(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
//        Log.d("Database Operation","Database created...");
    }
    @Override
    public void onCreate(SQLiteDatabase db) {


        db.execSQL(CREATE_TABLE);
        Log.d("Database Operation","Table created...");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {



    }

    public  void  addProduct(String brand,String accreditation,String rating,String available,String category,SQLiteDatabase database){
        ContentValues contentValues= new ContentValues(  );
//        contentValues.put( ProductContract.ContactEntry.CONTACT_ID,id );
        contentValues.put( ProductContract.ProductEntry.BRAND_NAME,brand );
        contentValues.put( ProductContract.ProductEntry.ACCREDITATION,accreditation );
        contentValues.put( ProductContract.ProductEntry.RATING,rating );
        contentValues.put( ProductContract.ProductEntry.AVAILABLE,available );
        contentValues.put( ProductContract.ProductEntry.CATEGORY,category );
        database.insert( ProductContract.ProductEntry.TABLE_NAME,null,contentValues );
        Log.d("Database Operation","One Raw inserted...");
    }

    public void deleteContact(int id,SQLiteDatabase database){
        String selection=ProductContract.ProductEntry.PRODUCT_ID+" = "+id;
        database.delete( ProductContract.ProductEntry.TABLE_NAME,selection,null );
    }

    public Cursor readContats(SQLiteDatabase database){

        String[] projiectons={ProductContract.ProductEntry.PRODUCT_ID,ProductContract.ProductEntry.BRAND_NAME,ProductContract.ProductEntry.ACCREDITATION,ProductContract.ProductEntry.RATING,ProductContract.ProductEntry.AVAILABLE,ProductContract.ProductEntry.CATEGORY};

        Cursor cursor=database.query( ProductContract.ProductEntry.TABLE_NAME ,
                projiectons,null,null,null,null,null );
        return cursor;
    }

    public Cursor getEggs(SQLiteDatabase database){


        String[] projiectons={ProductContract.ProductEntry.PRODUCT_ID,ProductContract.ProductEntry.BRAND_NAME,ProductContract.ProductEntry.ACCREDITATION,ProductContract.ProductEntry.RATING,ProductContract.ProductEntry.AVAILABLE,ProductContract.ProductEntry.CATEGORY};

        String selection = ProductsContract.Columns.BRAND_NAME+" LIKE '%"+"eggs"+"%' " ;
        String myname = "eggs";
        String sql = "select * from kkf_table where brand_name like ?";
        Cursor cursor = database.rawQuery( sql,new String[]{myname+"%"} );

//        Cursor cursor=database.query( ProductContract.ProductEntry.TABLE_NAME ,
//                projiectons,selection,null,null,null,null );
        return cursor;
    }

    public void dropTable(SQLiteDatabase db) {
        db.execSQL( DROP_TABLE );
    }


    public void deleteTable(SQLiteDatabase db){
        db.execSQL( "drop table " + ProductContract.ProductEntry.TABLE_NAME );
    }

    public void setCreateTable(SQLiteDatabase db){
        db.execSQL( CREATE_TABLE );
    }






}
