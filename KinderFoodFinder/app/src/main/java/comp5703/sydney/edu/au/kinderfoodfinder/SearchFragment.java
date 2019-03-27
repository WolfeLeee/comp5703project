package comp5703.sydney.edu.au.kinderfoodfinder;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import android.view.WindowManager;
import android.widget.SearchView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements GetProductsData.OnDataAvailable, RecyclerItemClickListener.OnRecyclerClickListener
{
    // defined variables
    private static final String TAG = "SearchFragment";
    private SearchView searchView;
    private ProductsRecyclerViewAdapter mProductsRecyclerViewAdapter;
    static final String PRODUCT_DETAIL = "PRODUCT_DETAIL";
    static final String IMAGE_DETAIL = "IMAGE_DETAIL";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        Log.d(TAG, "onViewCreated: starts");
        SearchManager searchManager = (SearchManager) getContext().getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo searchableInfo =searchManager.getSearchableInfo(getActivity().getComponentName());
        searchView = (SearchView) view.findViewById(R.id.searchProduct);
        searchView.setSearchableInfo(searchableInfo);
        searchView.setIconified(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit: "+query);
                GetProductsData getProductsData = new GetProductsData(SearchFragment.this,SearchFragment.this.getContext());
                getProductsData.execute(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.productRecyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),recyclerView,this));
        mProductsRecyclerViewAdapter = new ProductsRecyclerViewAdapter(new ArrayList<Products>(), getContext());
        recyclerView.setAdapter(mProductsRecyclerViewAdapter);

        Log.d(TAG, "onViewCreated: ends");
        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();

    }

    @Override
    public void onDataAvailable(List<Products> data)
    {
        Log.d(TAG, "onDataAvailable: starts");
        mProductsRecyclerViewAdapter.loadNewData(data);
        Log.d(TAG, "onDataAvailable: ends");
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(getContext(), ProductDetailActivity.class);
        Products pseudoproduct = mProductsRecyclerViewAdapter.getProducts(position);
        Products passingproducts = new Products(pseudoproduct.getCategory(),pseudoproduct.getBrand());
        intent.putExtra(PRODUCT_DETAIL,passingproducts);
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        pseudoproduct.getImage().compress(Bitmap.CompressFormat.PNG,0,bStream);
        byte[] byteArray = bStream.toByteArray();
        intent.putExtra(IMAGE_DETAIL,byteArray);
        startActivity(intent);
    }
}
