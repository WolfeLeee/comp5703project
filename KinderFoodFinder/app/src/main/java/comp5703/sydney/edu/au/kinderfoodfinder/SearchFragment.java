package comp5703.sydney.edu.au.kinderfoodfinder;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;

import comp5703.sydney.edu.au.kinderfoodfinder.LocalDatabase.ProductsRecyclerViewAdapter;

//implements GetProductsData.OnDataAvailable, RecyclerItemClickListener.OnRecyclerClickListener

public class SearchFragment extends Fragment
{
    // defined variables
    private static final String TAG = "SearchFragment";
    public static final String PRODUCT_DETAIL = "PRODUCT_DETAIL";
    public static final String IMAGE_DETAIL = "IMAGE_DETAIL";

    private SearchView searchView;
    private LinearLayout touchInterceptor;
//    private ProductsRecyclerViewAdapter mProductsRecyclerViewAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // set up
        touchInterceptor = (LinearLayout) view.findViewById(R.id.touchInterceptor);
        searchView = (SearchView) view.findViewById(R.id.searchProduct);
        searchView.setFocusable(false);  // prevent from opening keyboard first

        // remove focus on search view if touch other area
        touchInterceptor.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                searchView.clearFocus();
            }
        });

        // search view listener for searching result
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                // search by the input from user
//                Log.d(TAG, "onQueryTextSubmit: "+query);
//                GetProductsData getProductsData = new GetProductsData(SearchFragment.this,SearchFragment.this.getContext());
//                getProductsData.execute(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                return false;
            }
        });

//        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.productRecyclerview);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//
//        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),recyclerView,this));
//        mProductsRecyclerViewAdapter = new ProductsRecyclerViewAdapter(new ArrayList<Products>(), getContext());
//        recyclerView.setAdapter(mProductsRecyclerViewAdapter);

        return view;
    }

//    @Override
//    public void onDataAvailable(List<Products> data)
//    {
//        Log.d(TAG, "onDataAvailable: starts");
//        mProductsRecyclerViewAdapter.loadNewData(data);
//        Log.d(TAG, "onDataAvailable: ends");
//    }
//
//    @Override
//    public void onItemClick(View view, int position)
//    {
//        Intent intent = new Intent(getContext(), ProductDetailActivity.class);
//        Products pseudoproduct = mProductsRecyclerViewAdapter.getProducts(position);
//        Products passingproducts = new Products(pseudoproduct.getCategory(),pseudoproduct.getBrand());
//        intent.putExtra(PRODUCT_DETAIL,passingproducts);
//        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
//        pseudoproduct.getImage().compress(Bitmap.CompressFormat.PNG,0,bStream);
//        byte[] byteArray = bStream.toByteArray();
//        intent.putExtra(IMAGE_DETAIL,byteArray);
//        startActivity(intent);
//    }
}
