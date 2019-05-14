package comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase;





import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;


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
        Log.i("Log","create database");
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
        Log.i("updateLog","update database！");
    }

    @Entity
    public static class StoreName {

        @Id(autoincrement = true)
        private long id;
        private String storeName;
        private String StreetAddress;
        private String State;
        private String Postcode;
        private String Lat;
        private String Long;
        @Index(unique = true)
        private String Brandid;
        private String Brandname;

        public StoreName(long id, String storeName, String streetAddress, String state, String postcode, String lat, String aLong, String brandid, String brandname) {
            this.id = id;
            this.storeName = storeName;
            StreetAddress = streetAddress;
            State = state;
            Postcode = postcode;
            Lat = lat;
            Long = aLong;
            Brandid = brandid;
            Brandname = brandname;
        }

        public StoreName(){}

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getStoreName() {
            return storeName;
        }

        public void setStoreName(String storeName) {
            this.storeName = storeName;
        }

        public String getStreetAddress() {
            return StreetAddress;
        }

        public void setStreetAddress(String streetAddress) {
            StreetAddress = streetAddress;
        }

        public String getState() {
            return State;
        }

        public void setState(String state) {
            State = state;
        }

        public String getPostcode() {
            return Postcode;
        }

        public void setPostcode(String postcode) {
            Postcode = postcode;
        }

        public String getLat() {
            return Lat;
        }

        public void setLat(String lat) {
            Lat = lat;
        }

        public String getLong() {
            return Long;
        }

        public void setLong(String aLong) {
            Long = aLong;
        }

        public String getBrandid() {
            return Brandid;
        }

        public void setBrandid(String brandid) {
            Brandid = brandid;
        }

        public String getBrandname() {
            return Brandname;
        }

        public void setBrandname(String brandname) {
            Brandname = brandname;
        }
    }
}
