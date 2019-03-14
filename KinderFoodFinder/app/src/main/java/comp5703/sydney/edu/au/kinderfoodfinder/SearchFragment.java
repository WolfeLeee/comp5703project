package comp5703.sydney.edu.au.kinderfoodfinder;

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
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: starts");
        super.onViewCreated(view, savedInstanceState);
        searchView = (SearchView) view.findViewById(R.id.searchProduct);
        searchView.setIconified(false);
        RecyclerView recyclerView =(RecyclerView) view.findViewById(R.id.productRecyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        recyclerView.addOnItemTouchListener(new RecyclerItemClicklistener(getActivity(),recyclerView,getActivity()));

        mProductsRecyclerViewAdapter = new ProductsRecyclerViewAdapter(new ArrayList<Products>(),getContext());
        recyclerView.setAdapter(mProductsRecyclerViewAdapter);
        Log.d(TAG, "onViewCreated: ends");
    }

    @Override
    public void onResume() {
        super.onResume();
        GetProductsData getProductsData = new GetProductsData(this);
        getProductsData.execute("something");
    }

    @Override
    public void onDataAvailable(List<Products> data) {
        Log.d(TAG, "onDataAvailable: starts");
        mProductsRecyclerViewAdapter.loadNewData(data);
        Log.d(TAG, "onDataAvailable: ends");
    }
}
