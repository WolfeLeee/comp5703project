package comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase;

class SqliteManager {
    private static final SqliteManager ourInstance = new SqliteManager();

    static SqliteManager getInstance() {
        return ourInstance;
    }

    private SqliteManager() {
    }
}
