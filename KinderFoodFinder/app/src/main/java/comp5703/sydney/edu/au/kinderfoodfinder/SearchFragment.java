package comp5703.sydney.edu.au.kinderfoodfinder;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements GetProductsData.OnDataAvailable
{
    // defined variables
    private static final String TAG = "SearchFragment";
    private SearchView searchView;
    private ProductsRecyclerViewAdapter mProductsRecyclerViewAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        Log.d(TAG, "onViewCreated: starts");

        searchView = (SearchView) view.findViewById(R.id.searchProduct);
        searchView.setIconified(false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.productRecyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        recyclerView.addOnItemTouchListener(new RecyclerItemClicklistener(getActivity(),recyclerView,getActivity()));

        mProductsRecyclerViewAdapter = new ProductsRecyclerViewAdapter(new ArrayList<Products>(), getContext());
        recyclerView.setAdapter(mProductsRecyclerViewAdapter);


        String[] projection = {ProductsContract.Columns._ID,
                ProductsContract.Columns.BRAND_NAME,
                ProductsContract.Columns.BRAND_RATING,
                ProductsContract.Columns.BRAND_SORTORDER};
        ContentResolver contentResolver = getContext().getContentResolver();

        ContentValues values = new ContentValues();
        values.put(ProductsContract.Columns.BRAND_NAME, "Beef loin chop 200kg");
        values.put(ProductsContract.Columns.BRAND_RATING, "Best");
        values.put(ProductsContract.Columns.BRAND_SORTORDER, "1");
        Uri uri = contentResolver.insert(ProductsContract.CONTENT_URI, values);

        Cursor cursor = contentResolver.query(ProductsContract.CONTENT_URI, projection, null, null, ProductsContract.Columns.BRAND_SORTORDER);

        if (cursor != null) {
            Log.d(TAG, "onViewCreated: number of rows: " + cursor.getCount());
            while (cursor.moveToNext()) {
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    Log.d(TAG, "onViewCreated: " + cursor.getColumnName(i) + ": " + cursor.getString(i));
                }
                Log.d(TAG, "onViewCreated: =================================================");
            }
        }

        Log.d(TAG, "onViewCreated: ends");

        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        GetProductsData getProductsData = new GetProductsData(this);
        getProductsData.execute("something");
    }

    @Override
    public void onDataAvailable(List<Products> data)
    {
        Log.d(TAG, "onDataAvailable: starts");
        mProductsRecyclerViewAdapter.loadNewData(data);
        Log.d(TAG, "onDataAvailable: ends");
    }

}
