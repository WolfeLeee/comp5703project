package comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import comp5703.sydney.edu.au.kinderfoodfinder.Database.ProductContract;

public class StoreHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "kff_database";
    public static final int DATABASE_VERSION=1;

    public static final String CREATE_TABLE="create table "+ Contract.StoreContract.TABLE_NAME+"("
            + Contract.StoreContract.storeName+" TEXT," +Contract.StoreContract.StreetAddress+" TEXT,"
            +Contract.StoreContract.State+" TEXT,"+Contract.StoreContract.Postcode+" TEXT,"
            +Contract.StoreContract.Lat+" TEXT,"+Contract.StoreContract.Long+" TEXT,"
            +Contract.StoreContract.Brandid+" TEXT,"+Contract.StoreContract.Brandname+" TEXT);";
    public static final String DROP_TABLE="drop table if exists "+Contract.StoreContract.TABLE_NAME;

    public StoreHelper(Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        Log.d("Database Operation","Table created...");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addStore(StoreInfo store,SQLiteDatabase database){

        ContentValues contentValues= new ContentValues(  );
//        contentValues.put( ProductContract.ContactEntry.CONTACT_ID,id );
        contentValues.put( Contract.StoreContract.storeName,store.getBrandname());
        contentValues.put( Contract.StoreContract.StreetAddress,store.getStreetAddress());
        contentValues.put( Contract.StoreContract.State,store.getState());
        contentValues.put( Contract.StoreContract.Postcode,store.getPostcode());
        contentValues.put( Contract.StoreContract.Lat,store.getLat());
        contentValues.put( Contract.StoreContract.Long,store.getLong());
        contentValues.put( Contract.StoreContract.Brandid,store.getBrandid());
        contentValues.put( Contract.StoreContract.Brandname,store.getBrandname());
        database.insert( Contract.StoreContract.TABLE_NAME,null,contentValues );
    }

    public Cursor searchbyStoreName(String brandName, SQLiteDatabase database){
        Cursor cursor=database.query( Contract.StoreContract.TABLE_NAME,new String[]{"*"}, Contract.StoreContract.Brandname +" LIKE ?",new String[]{brandName},null,null,null );

        return cursor;
    }

    public Cursor readStore( SQLiteDatabase database){
        String[] projections={Contract.StoreContract.storeName, Contract.StoreContract.StreetAddress, Contract.StoreContract.State,Contract.StoreContract.Postcode,Contract.StoreContract.Lat,Contract.StoreContract.Long,Contract.StoreContract.Brandname,Contract.StoreContract.Brandname};

//        Cursor cursor= (Cursor) database.query( Contract.StoreContract.TABLE_NAME,projections, null,null,null,null );

        Cursor cursor=database.query( Contract.StoreContract.TABLE_NAME,new String[]{"*"},null,null,null,null,null );

        return cursor;
    }

    public void deleteAll(SQLiteDatabase database){


        database.execSQL(DROP_TABLE);
        Log.d("Database Operation","Table Dropped...");

        onCreate( database );

    }
}
