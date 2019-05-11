package comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase;



import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import comp5703.sydney.edu.au.kinderfoodfinder.StartUpActivity;


public class AccreditationHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Accreditation_database";
    public static final int DATABASE_VERSION=1;

    public static final String CREATE_TABLE="create table "+ Contract.TABLE_NAME+"("
            +Contract.sid+" TEXT," +Contract.ParentId+" TEXT,"
            +Contract.Accreditation+" TEXT,"
            +Contract.Rating+" TEXT);";
    public static final String DROP_TABLE="drop table if exists "+Contract.TABLE_NAME;

    public AccreditationHelper(Context context) {
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

    public  void  addAcc(String sid,String parentId,String acc,String rating,SQLiteDatabase database){
        ContentValues contentValues= new ContentValues(  );
//        contentValues.put( ProductContract.ContactEntry.CONTACT_ID,id );
        contentValues.put( Contract.sid,sid);
        contentValues.put( Contract.ParentId,parentId );
        contentValues.put( Contract.Accreditation,acc );
        contentValues.put( Contract.Rating,rating);

        database.insert( Contract.TABLE_NAME,null,contentValues );
        Log.d("Database Operation","One Raw inserted...");
    }

    public void addAccentity(AccEntity acc,SQLiteDatabase database){

        ContentValues contentValues= new ContentValues(  );
//        contentValues.put( ProductContract.ContactEntry.CONTACT_ID,id );
        contentValues.put( Contract.sid,acc.getSid());
        contentValues.put( Contract.ParentId,acc.getParentId() );
        contentValues.put( Contract.Accreditation,acc.getAccreditation() );
        contentValues.put( Contract.Rating,acc.getRating());
        database.insert( Contract.TABLE_NAME,null,contentValues );
    }


    public Cursor searchbyPID(String pid,SQLiteDatabase database){
        Cursor cursor=database.query( Contract.TABLE_NAME,new String[]{"*"},Contract.ParentId+" LIKE ?",new String[]{pid},null,null,null );

        return cursor;
    }
    public void deleteContact(int id,SQLiteDatabase database){
//        String selection=StatisticContract.StatisticEntry.ITEM_ID+" = "+id;
//        database.delete( StatisticContract.StatisticEntry.TABLE_NAME,selection,null );
    }

//    public Cursor readContats(SQLiteDatabase database){
//
//        String[] projiectons={StatisticContract.StatisticEntry.Brand_ID,StatisticContract.StatisticEntry.DATE,StatisticContract.StatisticEntry.GENDER,StatisticContract.StatisticEntry.AGE,StatisticContract.StatisticEntry.COUNT};
//
//        Cursor cursor=database.query( StatisticContract.StatisticEntry.TABLE_NAME ,
//                projiectons,null,null,null,null,null );
//        return cursor;
//    }

    public void deleteAll(SQLiteDatabase database){

        database.execSQL(DROP_TABLE);
        Log.d("Database Operation","Table Dropped...");

        onCreate( database );


    }
    public Cursor groupby(SQLiteDatabase database){

        String[] sqlSelect = {"product_id","Brand_Name","Category","Accreditation","Rating","Location"};

//        Cursor cursor = database.query(database,sqlSelect,"Brand_Name LIKE ?",new String[]{"%"+brand_name+"%"},null,null,null);

        Cursor cursor=database.query( Contract.TABLE_NAME,
                new String[]{ "*", "COUNT(count) AS count"},
                null,null,
                Contract.Accreditation,
                null,null);
        return cursor;


    }



}


