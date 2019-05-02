package comp5703.sydney.edu.au.kinderfoodfinder.UserInfomation;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class UserDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "user_db";
    public static final int DATABASE_VERSION=1;

    public static final String CREATE_TABLE="create table "+ UserContact.ContactEntry.TABLE_NAME+"("
            +UserContact.ContactEntry.USER_ID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"+UserContact.ContactEntry.NAME+" TEXT,"
            +UserContact.ContactEntry.STATUS+" INTEGER);";

    public static final String DROP_TABLE="drop table if exists "+UserContact.ContactEntry.TABLE_NAME;


    public UserDBHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        Log.d("User Database Operation","Database created...");

    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        Log.d("User Database Operation","Table created...");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL( DROP_TABLE );
        onCreate( db );
    }

    public  void  addContact(String name,int status,SQLiteDatabase database){
        ContentValues contentValues= new ContentValues(  );
//        contentValues.put( ProductContract.ContactEntry.CONTACT_ID,id );
        contentValues.put( UserContact.ContactEntry.NAME,name );
        contentValues.put( UserContact.ContactEntry.STATUS,status );
        database.insert( UserContact.ContactEntry.TABLE_NAME,null,contentValues );
        Log.d("User Database Operation","One Raw inserted...");
    }

    public Cursor getContats(SQLiteDatabase database,String name){

//        String[] projiectons={UserContact.ContactEntry.USER_ID,UserContact.ContactEntry.NAME,UserContact.ContactEntry.STATUS};
//
//        String[] args = {String.valueOf(name)};
//        Cursor cursor=database.query( UserContact.ContactEntry.TABLE_NAME ,
//                projiectons,UserContact.ContactEntry.NAME+"?",args,null,null,null );
//        return cursor;
        Cursor cursor = database.rawQuery("select * from  user_info where name=?", new String[]{name});//得到游标
        if(cursor.moveToFirst()){
            String NUMBER = cursor.getString(cursor.getColumnIndex("NUMBER"));
            String PASSWORD = cursor.getString(cursor.getColumnIndex("PASSWORD"));
            String SOFTNAME=cursor.getString(cursor.getColumnIndex("SOFTNAME"));

            return cursor;
        }
        else
            return null;

    }
}
