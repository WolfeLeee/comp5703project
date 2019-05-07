package comp5703.sydney.edu.au.kinderfoodfinder.StatisticDatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import comp5703.sydney.edu.au.kinderfoodfinder.Database.ProductContract;

public class StatisticsDatabase extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "statistics_database";
    public static final int DATABASE_VERSION=1;

    public static final String CREATE_TABLE="create table "+ StatisticContract.StatisticEntry.TABLE_NAME+"("
            +StatisticContract.StatisticEntry.Brand_ID+" TEXT," +StatisticContract.StatisticEntry.DATE+" TEXT,"
            +StatisticContract.StatisticEntry.GENDER+" TEXT,"
            +StatisticContract.StatisticEntry.AGE+" TEXT,"+StatisticContract.StatisticEntry.COUNT+" TEXT);";
    public static final String DROP_TABLE="drop table if exists "+StatisticContract.StatisticEntry.TABLE_NAME;



    public StatisticsDatabase(Context context){
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

    public  void  addProduct(String sid,String date,String gender,String age,String count,SQLiteDatabase database){
        ContentValues contentValues= new ContentValues(  );
//        contentValues.put( ProductContract.ContactEntry.CONTACT_ID,id );
        contentValues.put( StatisticContract.StatisticEntry.Brand_ID,sid);
        contentValues.put( StatisticContract.StatisticEntry.DATE,date );
        contentValues.put( StatisticContract.StatisticEntry.GENDER,gender );
        contentValues.put( StatisticContract.StatisticEntry.COUNT,count );
        contentValues.put( StatisticContract.StatisticEntry.GENDER,gender );
        contentValues.put( StatisticContract.StatisticEntry.AGE,age );
        database.insert( StatisticContract.StatisticEntry.TABLE_NAME,null,contentValues );
        Log.d("Database Operation","One Raw inserted...");
    }

    public void deleteContact(int id,SQLiteDatabase database){
//        String selection=StatisticContract.StatisticEntry.ITEM_ID+" = "+id;
//        database.delete( StatisticContract.StatisticEntry.TABLE_NAME,selection,null );
    }

    public Cursor readContats(SQLiteDatabase database){

        String[] projiectons={StatisticContract.StatisticEntry.Brand_ID,StatisticContract.StatisticEntry.DATE,StatisticContract.StatisticEntry.GENDER,StatisticContract.StatisticEntry.AGE,StatisticContract.StatisticEntry.COUNT};

        Cursor cursor=database.query( StatisticContract.StatisticEntry.TABLE_NAME ,
                projiectons,null,null,null,null,null );
        return cursor;
    }

    public void deleteAll(SQLiteDatabase database){

        database.execSQL(DROP_TABLE);
        Log.d("Database Operation","Table Dropped...");


    }
    public Cursor groupby(SQLiteDatabase database){

        String[] sqlSelect = {"product_id","Brand_Name","Category","Accreditation","Rating","Location"};

//        Cursor cursor = database.query(database,sqlSelect,"Brand_Name LIKE ?",new String[]{"%"+brand_name+"%"},null,null,null);



        Cursor cursor=database.query( StatisticContract.StatisticEntry.TABLE_NAME,
                new String[]{ "*", "COUNT(count) AS count"},
                null,null,
                StatisticContract.StatisticEntry.Brand_ID+", "+StatisticContract.StatisticEntry.DATE,
                null,null);
       return cursor;


    }


}
