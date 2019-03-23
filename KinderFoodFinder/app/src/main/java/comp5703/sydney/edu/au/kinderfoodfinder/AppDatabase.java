package comp5703.sydney.edu.au.kinderfoodfinder;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.security.AccessControlContext;

/**
 *
 * Basic local database class for the application
 * The only class that should use this is {@link AppProvider}
 */

public class AppDatabase extends SQLiteOpenHelper {

    private static final String TAG = "AppDatabase";

    //Implement AppDatabase as a Singleton
    private static AppDatabase instance = null;
    public static final String DATABASE_NAME = "AnimalFoodProducts.db";
    public static final int DATABASE_VERSION = 1;

    public AppDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.d(TAG, "AppDatabase: constructor");
    }

    /**
     * Get an instance of the app's singleton database helper object
     * @param context the content providers context
     * @return a SQLite database helper object
     *
     */

    static AppDatabase getInstance(Context context){
        if(instance == null){
            Log.d(TAG, "getInstance: creating new instance");
            instance = new AppDatabase(context);
        }
        return instance; // return only one instance (singleton) of the field
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate: starts");
        String sSQL; //Use a string variable to facilitate logging

        sSQL =
//                "DROP TABLE "+ProductsContract.TABLE_NAME +";" +
                "CREATE TABLE " + ProductsContract.TABLE_NAME + "("
                + ProductsContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, "
                + ProductsContract.Columns.BRAND_NAME + " TEXT UNIQUE NOT NULL, "
                + ProductsContract.Columns.BRAND_SORTORDER + " INTEGER);";
        Log.d(TAG, sSQL);
        db.execSQL(sSQL);

        sSQL = "CREATE TABLE " + AccreditationContract.TABLE_NAME + "("
                + AccreditationContract.Columns._ID + " INTEGER PRIMARY KEY NOT NULL, "
                + AccreditationContract.Columns.ACCREDITATION + " TEXT NOT NULL, "
                + AccreditationContract.Columns.RATING + " TEXT NOT NULL, "
                + AccreditationContract.Columns.BRAND + " TEXT NOT NULL, "
                + "FOREIGN KEY ("+AccreditationContract.Columns.BRAND +") REFERENCES "+ProductsContract.TABLE_NAME+" ("+ProductsContract.Columns.BRAND_NAME +"));";
        db.execSQL(sSQL);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: starts");
        switch(oldVersion){
            case 1:
                //upgrade logic from version 1
                break;
            default:
                throw new IllegalArgumentException("onUpgrade() with unknown newVersion: "+newVersion);
        }
        Log.d(TAG, "onUpgrade: ends");
    }
}
