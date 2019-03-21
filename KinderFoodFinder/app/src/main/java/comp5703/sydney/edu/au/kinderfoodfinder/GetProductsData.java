package comp5703.sydney.edu.au.kinderfoodfinder;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

enum GetDatabase{ IDLE, PROCESSING, NOT_INITIALISED, FAILED_OR_EMPTY, SUCCESS}

public class GetProductsData extends AsyncTask<String, Void, List<Products>> {

    private static final String TAG = "GetProductsData";
    private List<Products> productsList;
    private final OnDataAvailable mCallback;
    private final Context mContext;

    interface OnDataAvailable{
        void onDataAvailable(List<Products> data);
    }

    public GetProductsData(OnDataAvailable mCallback, final Context context) {
        this.mCallback = mCallback;
        productsList = new ArrayList<Products>();
        mContext = context;
    }

    @Override
    protected void onPostExecute(List<Products> products) {
        super.onPostExecute(products);
        Log.d(TAG, "onPostExecute: starts");
        mCallback.onDataAvailable(products);
        Log.d(TAG, "onPostExecute: ends");
    }

    @Override
    protected List<Products> doInBackground(String... strings) {
        Log.d(TAG, "doInBackground: starts");
        String[] projection = {ProductsContract.Columns._ID,
                ProductsContract.Columns.BRAND_NAME,
                ProductsContract.Columns.BRAND_RATING,
                ProductsContract.Columns.BRAND_SORTORDER};
        ContentResolver contentResolver = mContext.getContentResolver();
        String selection = ProductsContract.Columns.BRAND_NAME+" LIKE '%"+strings[0]+"%' " ;
        Cursor cursor = contentResolver.query(ProductsContract.CONTENT_URI, projection, selection, null, ProductsContract.Columns.BRAND_SORTORDER);
        if (cursor != null) {
            Log.d(TAG, "doInBackground: number of rows: "+cursor.getCount());

            while (cursor.moveToNext()) {
                String brandname = null;
                String rating = null;
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    if(cursor.getColumnName(i).equalsIgnoreCase("Brand")){
                        brandname = cursor.getString(i);
                    }
                    else if (cursor.getColumnName(i).equalsIgnoreCase("Rating")){
                        rating = cursor.getString(i);
                    }
                    Log.d(TAG, "doInBackground: "+ cursor.getColumnName(i)+": "+cursor.getString(i));
                }
                if(brandname != null & brandname.length() >0 && rating != null && brandname.length() >0){
                    Products products = new Products(brandname,rating);
                    productsList.add(products);
                }
                Log.d(TAG, "doInBackground: ======================================================");
            }
        }
        Log.d(TAG, "doInBackground: ends");
        return productsList;
    }
}
