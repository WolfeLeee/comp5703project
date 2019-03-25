package comp5703.sydney.edu.au.kinderfoodfinder;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
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
//        recyclerView.addOnItemTouchListener(new RecyclerItemClicklistener(getActivity(),recyclerView,getActivity()));
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

}
