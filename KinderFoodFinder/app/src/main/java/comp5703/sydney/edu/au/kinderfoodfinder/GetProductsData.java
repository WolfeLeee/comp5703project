package comp5703.sydney.edu.au.kinderfoodfinder;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

enum GetDatabase{ IDLE, PROCESSING, NOT_INITIALISED, FAILED_OR_EMPTY, SUCCESS}

public class GetProductsData extends AsyncTask<String, Void, List<Products>> {

    private static final String TAG = "GetProductsData";
    private List<Products> productsList;
    private final OnDataAvailable mCallback;

    interface OnDataAvailable{
        void onDataAvailable(List<Products> data);
    }

    public GetProductsData(OnDataAvailable mCallback) {
        this.mCallback = mCallback;
        productsList = new ArrayList<Products>();
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
        Products product1 = new Products("Chicken","Good");
        Products product2 = new Products("Pork","Avoid");
        productsList.add(product1);
        productsList.add(product2);
        Log.d(TAG, "doInBackground: ends");
        return productsList;
    }
}
