package comp5703.sydney.edu.au.kinderfoodfinder;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

public class AppProvider extends ContentProvider {

    private static final String TAG = "AppProvider";

    private AppDatabase mOpenHelper;

    public static final UriMatcher sUriMatcher = buildUriMatcher();

    static final String CONTENT_AUTHORITY = "comp5703.sydney.edu.au.kinderfoodfinder.provider";
    public static final Uri CONTENT_AUTHORITY_URI = Uri.parse("content://"+CONTENT_AUTHORITY);

    private static final int BRANDS = 100;
    private static final int BRANDS_ID = 101;

    private static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        //eg. content://comp5703.sydney.edu.au.kinderfoodfinder.provider/FoodProducts
        matcher.addURI(CONTENT_AUTHORITY, ProductsContract.TABLE_NAME,BRANDS);
        //eg. content://comp5703.sydney.edu.au.kinderfoodfinder.provider/FoodProducts/#
        matcher.addURI(CONTENT_AUTHORITY,ProductsContract.TABLE_NAME+"/#",BRANDS_ID);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = AppDatabase.getInstance(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,String sortOrder) {
        Log.d(TAG, "query: called with URI "+uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "query: match is "+match);

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        switch(match){
            case BRANDS:
                queryBuilder.setTables(ProductsContract.TABLE_NAME);break;
            case BRANDS_ID:
                queryBuilder.setTables(ProductsContract.TABLE_NAME);
                long productId = ProductsContract.getProductId(uri);
                queryBuilder.appendWhere(ProductsContract.Columns._ID+" = "+productId); //if the Uri does have the id -> append the where id = productId
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: "+uri);
        }
        SQLiteDatabase db = mOpenHelper.getReadableDatabase(); //assuming the database returned is valid -> return mOpenHelder by getReadableDatabase()
        return queryBuilder.query(db,projection,selection,selectionArgs,null,null,sortOrder); // perform a query by combing all current settings and the information passed into this method
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch(match){
            case BRANDS:
                return ProductsContract.CONTENT_TYPE;
            case BRANDS_ID:
                return ProductsContract.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown Uri: "+uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.d(TAG, "insert: called with uri: "+uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "insert: match is "+match);
        final SQLiteDatabase db;
        Uri returnUri;
        long recordId;
        switch(match){
            case BRANDS:
                db = mOpenHelper.getWritableDatabase();
                recordId = db.insert(ProductsContract.TABLE_NAME,null,values);
                if(recordId >=0){
                    returnUri = ProductsContract.buildTaskUri(recordId);
                }
                else {
                    throw new android.database.SQLException("Failed to insert into "+uri.toString());
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown uri: "+uri);
        }

        Log.d(TAG, "insert: Exiting insert, returning "+uri);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.d(TAG, "delete: called with uri "+uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "delete: match is "+match);
        final SQLiteDatabase db;
        int count;

        String selectionCriteria;

        switch(match){
            case BRANDS:
                db = mOpenHelper.getWritableDatabase();
                count = db.delete(ProductsContract.TABLE_NAME,selection,selectionArgs);
                break;
            case BRANDS_ID:
                db = mOpenHelper.getWritableDatabase();
                long productId = ProductsContract.getProductId(uri);
                selectionCriteria = ProductsContract.Columns._ID + " = "+productId;
                if((selection !=null) && (selection.length() >0)){
                    selectionCriteria += " AND (" + selection +")";
                }
                count = db.delete(ProductsContract.TABLE_NAME,selectionCriteria,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown uri: "+uri);
        }
        Log.d(TAG, "delete: Exiting delete, returning "+count);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.d(TAG, "update: called with uri "+uri);
        final int match = sUriMatcher.match(uri);
        Log.d(TAG, "update: match is "+match);
        final SQLiteDatabase db;
        int count;

        String selectionCriteria;

        switch(match){
            case BRANDS:
                db = mOpenHelper.getWritableDatabase();
                count = db.update(ProductsContract.TABLE_NAME,values,selection,selectionArgs);
                break;
            case BRANDS_ID:
                db = mOpenHelper.getWritableDatabase();
                long productId = ProductsContract.getProductId(uri);
                selectionCriteria = ProductsContract.Columns._ID + " = "+productId;
                if((selection != null)&&(selection.length()>0)){
                    selectionCriteria += " AND (" + selection +")";
                }
                count = db.update(ProductsContract.TABLE_NAME,values,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown uri: "+uri);
        }
        Log.d(TAG, "update: Exiting update, returning "+count);
        return count;
    }
}
