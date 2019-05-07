package comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase;





import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class SqliteOpenHelper extends SQLiteOpenHelper {
    //数据库名
    private static final String db_name = "KFF.db";
    //数据库版本
    private static final int version = 33;
    public SqliteOpenHelper(Context context) {
        super(context, db_name, null,version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("Log","没有数据库,创建数据库");
        //建主表
        db.execSQL(
                "CREATE TABLE \"products\" ( \"_id\" INTEGER NOT NULL," +
                        " \"Brand_Name\" TEXT, \"Available\" TEXT," +
                        " \"Category\" TEXT, \"Accreditation\" TEXT, " +
                        "\"Image\" TEXT, PRIMARY KEY ( \"_id\" ) );"
        );
        //建鉴定表
        db.execSQL("CREATE TABLE \"accreditation\" ( \"_id\" INTEGER NOT NULL, " +
                "\"Accreditation\" TEXT, \"Rating\" TEXT, PRIMARY KEY ( \"_id\" ) );");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("updateLog","数据库更新了！");
    }

}
