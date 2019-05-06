package comp5703.sydney.edu.au.kinderfoodfinder.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;


public class StoreDatabase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "kkf_storetable";
    public static final String TABLE_NAME = "kkf_storetable";
    public static final String PRODUCT_ID="product_id";
    public static final String BRAND_NAME="brand_name";
    public static final String STORE_NAME="store_name";
    public static final String STREET="street";
    public static final String POSTCODE="postcode";
    public static final String STATE="state";
    public static final String LAT="latitude";
    public static final String LONG="longitude";

    public static final int DATABASE_VERSION=4;



    public static final String CREATE_TABLE="create table "+ TABLE_NAME+"("+
            PRODUCT_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"+BRAND_NAME+" TEXT,"
            +STORE_NAME+" TEXT,"+ STREET+" TEXT," +POSTCODE + " TEXT," + STATE+" TEXT,"
            +LAT+" TEXT," +LONG+" TEXT );";
    public static final String DROP_TABLE=" drop table if exists "+ TABLE_NAME;


    public StoreDatabase(Context context){
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
        db.execSQL(DROP_TABLE);


    }


    public boolean addStore( String brand,String storename, String street,String postcode,String state){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues= new ContentValues();

       // contentValues.put( StoreContract.StoreEntry.PRODUCT_ID, id );
        contentValues.put( StoreContract.StoreEntry.BRAND_NAME, brand );
        contentValues.put( StoreContract.StoreEntry.STORE_NAME, storename);
        contentValues.put( StoreContract.StoreEntry.STREET,street );
        contentValues.put( StoreContract.StoreEntry.POSTCODE, postcode );
        contentValues.put( StoreContract.StoreEntry.STATE, state );
        long result = db.insert( StoreContract.StoreEntry.TABLE_NAME,null,contentValues );
        db.close();

        if(result == -1){
            return false;
        }else{
            return true;
        }

    }


    public ArrayList<String> getAddress(String brand) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery(
                "SELECT * FROM kkf_storetable WHERE BRAND_NAME LIKE ?", new String[]{brand});

        ArrayList<String> LocationList = new ArrayList<>();

        String location;
        if (data.moveToFirst()) {
            do {
                String brandname = data.getString(data.getColumnIndex("brand_name"));
                String store = data.getString(data.getColumnIndex("store_name"));
                String street = data.getString(data.getColumnIndex("street"));
                String postcode = data.getString(data.getColumnIndex("postcode"));
                String state = data.getString(data.getColumnIndex("state"));

                location = brand.toUpperCase() + ", " + store + ", " + street + ", " + postcode + ", " + state;
                LocationList.add(location);

            } while (data.moveToNext());
        }
        data.close();


//        String address = data.getString(data.getColumnIndex(StoreContract.StoreEntry.STORE_NAME + ","+ StoreContract.StoreEntry.STREET + "," +
//                StoreContract.StoreEntry.POSTCODE + "," + StoreContract.StoreEntry.STATE));
//
//        Log.d("QueryAddress",address);


        return LocationList;
    }


    public void deleteContract(int id,SQLiteDatabase database){
        String selection=StoreContract.StoreEntry.PRODUCT_ID+" = "+id;
        database.delete( StoreContract.StoreEntry.TABLE_NAME,selection,null );
    }

    public Cursor readContract(SQLiteDatabase database){

        String[] projiectons={StoreContract.StoreEntry.BRAND_NAME,StoreContract.StoreEntry.STORE_NAME, StoreContract.StoreEntry.STREET,
                StoreContract.StoreEntry.POSTCODE,StoreContract.StoreEntry.STATE,StoreContract.StoreEntry.LAT,StoreContract.StoreEntry.LONG};

        Cursor cursor=database.query( StoreContract.StoreEntry.TABLE_NAME ,
                projiectons,null,null,null,null,null );
        return cursor;
    }


    public void dropTable(SQLiteDatabase db) {
        db.execSQL( DROP_TABLE );
    }


    public void deleteTable(SQLiteDatabase db){
        db.execSQL( "drop table " + StoreContract.StoreEntry.TABLE_NAME );
    }

    public void setCreateTable(SQLiteDatabase db){
        db.execSQL( CREATE_TABLE );
    }






}
