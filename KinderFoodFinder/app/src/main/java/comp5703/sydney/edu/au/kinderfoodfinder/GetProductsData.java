package comp5703.sydney.edu.au.kinderfoodfinder;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

//import com.squareup.picasso.Picasso;
//import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
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
                ProductsContract.Columns.BRAND_SORTORDER,
        ProductsContract.Columns.BRAND_CATEGORY,
        ProductsContract.Columns.IMAGE};
        ContentResolver contentResolver = mContext.getContentResolver();

//        ContentValues values = new ContentValues();
//        values.put(ProductsContract.Columns.BRAND_CATEGORY,"Eggs");
//        values.put(ProductsContract.Columns.BRAND_NAME,"Lucky Chicken Eggs");
//        values.put(ProductsContract.Columns.BRAND_SORTORDER,"1");
//        String imageuri = "http://www.luckychickeneggs.com.au/wp-content/uploads/2019/02/Box1-Happy.png";
//        try {
//            Bitmap bitmap = Picasso.with(mContext).load(imageuri).get();
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
//            byte[] image = byteArrayOutputStream.toByteArray();
//            values.put(ProductsContract.Columns.IMAGE,image);
//        }
//        catch(IOException e){
//            e.printStackTrace();
//        }
//
//        Uri uri = contentResolver.insert(ProductsContract.CONTENT_URI,values);



        String selection = ProductsContract.Columns.BRAND_NAME+" LIKE '%"+strings[0]+"%' " ;
        Cursor cursor = contentResolver.query(ProductsContract.CONTENT_URI, projection, selection, null, ProductsContract.Columns.BRAND_SORTORDER);
        if (cursor != null) {
            Log.d(TAG, "doInBackground: number of rows: "+cursor.getCount());

            while (cursor.moveToNext()) {
                String brandname = null;
                String brandcategory = null;
                Bitmap bitmap = null;
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    if(cursor.getColumnName(i).equalsIgnoreCase(ProductsContract.Columns.BRAND_NAME)){
                        brandname = cursor.getString(i);
                    }
                    else if (cursor.getColumnName(i).equalsIgnoreCase("Category")){
                        brandcategory = cursor.getString(i);
                    }
                    else if (cursor.getColumnName(i).equalsIgnoreCase(ProductsContract.Columns.IMAGE)){
                        bitmap = BitmapFactory.decodeByteArray(cursor.getBlob(i),0,cursor.getBlob(i).length);
                    }
                }
                Log.d(TAG, "doInBackground: Brand title is "+brandname);
                Log.d(TAG, "doInBackground: Brand category is "+brandcategory);
                if(brandname != null & brandname.length() >0 && brandcategory != null && brandcategory.length() >0){
                    Products products = new Products(brandcategory,brandname,bitmap);

                    productsList.add(products);

                }
                Log.d(TAG, "doInBackground: ======================================================");
            }
        }
        Log.d(TAG, "doInBackground: ends");
        return productsList;
    }
}
