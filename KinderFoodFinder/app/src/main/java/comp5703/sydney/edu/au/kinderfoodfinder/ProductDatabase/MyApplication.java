package comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase;

import android.app.Application;

import org.greenrobot.greendao.database.Database;

import comp5703.sydney.edu.au.greendao.gen.DaoMaster;
import comp5703.sydney.edu.au.greendao.gen.DaoSession;

public class MyApplication extends Application {
    private DaoSession daoSession;
    public static MyApplication instance;
    public static MyApplication getInstance() {
        return instance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        DaoMaster.DevOpenHelper helper = new  DaoMaster.DevOpenHelper(this,"KFF.db",null);
        Database db =  helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();
    }
    public DaoSession getDaoSession() {
        return daoSession;
    }
}
