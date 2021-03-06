package comp5703.sydney.edu.au.kinderfoodfinder;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Spinner;

import java.util.ArrayList;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.DaoUnit;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.Product;
import comp5703.sydney.edu.au.kinderfoodfinder.StatisticDatabase.StatisticsDatabase;

/* * * * * * * * * * *
 * May be used later *
 * * * * * * * * * * */
//implements GetProductsData.OnDataAvailable, RecyclerItemClickListener.OnRecyclerClickListener

public class SearchFragment extends Fragment implements AdapterView.OnItemSelectedListener
{
    /* * * * * * * * * * *
     * Defined Variables *
     * * * * * * * * * * */
    private static final String TAG = "SearchFragment";
    public static final String PRODUCT_DETAIL = "PRODUCT_DETAIL";
    public static final String IMAGE_DETAIL = "IMAGE_DETAIL";

    private SearchView searchView;
//    private ListView product;

    private ArrayList<Product> result=new ArrayList<>(  );
    ItemsAdapter productAdapter;
    BrandAdapter brandAdapter;

    private ArrayList<Items> accResult;
    RadioGroup catogryRadioes;
    Spinner spinner;

    private int category,type;

    ArrayList<String> items;
    int temp=0;
//******************************************************************
    //RecyclerView set

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private BrandRecyclerAdapter brandRecyclerAdapter;
    private AccRecyclerAdapter accRecyclerAdapter;

//    private ProductsRecyclerViewAdapter mProductsRecyclerViewAdapter;

    /* * * * * * * * * * *
     * On Created Method *
     * * * * * * * * * * */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        searchView=view.findViewById( R.id.searchProduct );
//        product=view.findViewById( R.id.productListView );
        accResult=new ArrayList<>(  );

        items=new ArrayList<>(  );
        String info="All;";
        ArrayList<Product> test=new ArrayList<>(  );

        test=DaoUnit.getInstance().getProduct();

        items.add( "ALL" );
        for (Product products:test){
            String brand=products.getCategory().toUpperCase();
            if(! info.contains( brand)){
                info=info+brand+";";
                items.add( brand );
            }
        }

         catogryRadioes = view.findViewById(R.id.radioCatogory);

         spinner = view.findViewById(R.id.spinner);
        //create a list of items for the spinner.

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        spinner.setAdapter(adapter1);
        spinner.setClickable( false );

        spinner.setOnItemSelectedListener( this );

        // select one radio button and set new adapter for recycler view
        catogryRadioes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // clear search view text
                searchView.setQuery( "",false );
                category = checkedId;
                String type=items.get( temp );
                // get new arrayList for the adapter based on the selection
                new SearchResult().doInBackground(   );
                // set recycler view adapter
                if(category==R.id.radioBrandName){
                    brandRecyclerAdapter=new BrandRecyclerAdapter( getActivity(),result );
                    recyclerView.setAdapter( brandRecyclerAdapter );
                    brandRecyclerAdapter.notifyDataSetChanged();
                }else {
                    accRecyclerAdapter=new AccRecyclerAdapter( getActivity(),accResult );
                    recyclerView.setAdapter( accRecyclerAdapter );
                    accRecyclerAdapter.notifyDataSetChanged();
                }
            }
        });


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if(category==R.id.radioBrandName){
                    new SearchResult().doInBackground( "brand",newText );
                    brandRecyclerAdapter=new BrandRecyclerAdapter( getActivity(),result );
                    recyclerView.setAdapter( brandRecyclerAdapter );
                    brandRecyclerAdapter.notifyDataSetChanged();
                }else if(category==R.id.radioAccreditation){
                    new SearchResult().doInBackground( "acc",newText );
                    accRecyclerAdapter=new AccRecyclerAdapter( getActivity(),accResult );
                    recyclerView.setAdapter( accRecyclerAdapter );
                    accRecyclerAdapter.notifyDataSetChanged();
                }

                return false;
            }
        });



        // find recyclerView ID
        recyclerView=view.findViewById( R.id.recyclerview );
        // set up the RecyclerView
        layoutManager=new LinearLayoutManager( getContext() );
        recyclerView.setHasFixedSize( true );
        recyclerView.setLayoutManager( layoutManager );
        brandRecyclerAdapter=new BrandRecyclerAdapter( getActivity(),result );
        accRecyclerAdapter=new AccRecyclerAdapter( getActivity(),accResult );

        return view;
    }



    // select one item of the drop list and set new adapter for recycler view
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //clear searchview text
        searchView.setQuery( "",false );
        temp=position;
        // get new arrayList for the adapter based on the selection
        new SearchResult().doInBackground( );
        // set adapter for the recycler view
        if(category==R.id.radioBrandName){
            brandRecyclerAdapter=new BrandRecyclerAdapter( getActivity(),result );
            recyclerView.setAdapter( brandRecyclerAdapter );
            brandRecyclerAdapter.notifyDataSetChanged();
        }else if(category==R.id.radioAccreditation) {
            accRecyclerAdapter=new AccRecyclerAdapter( getActivity(),accResult );
            recyclerView.setAdapter( accRecyclerAdapter );
            accRecyclerAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //asyncTask for search features
    private class SearchResult extends AsyncTask<String, String, String> {
        Context context;
        @Override
        protected String doInBackground(String... strings) {
            //select search type brand name or accreditation
            //select product type eggs, chicken or pork
            if(strings.length==2){
                if(strings[0].equalsIgnoreCase( "brand" )){
                    result=DaoUnit.getInstance().searchByBrand( category,items.get( temp ),strings[1] );
                }else if(strings[0].equalsIgnoreCase( "acc" )){
                    accResult=DaoUnit.getInstance().searchByAcc( category,items.get( temp ),strings[1] );
                }
            }else {
                if(category==R.id.radioBrandName){
                    //get the result based on query condition
                    result=DaoUnit.getInstance().getAllinsearchBrand(items.get( temp ) );
                    //set listview adapter
//                    brandAdapter=new BrandAdapter( getActivity(),result );
                }else {
                    //get the result based on query condition
                    accResult=DaoUnit.getInstance().getAllinsearchAcc( items.get( temp ) );
                    //set listview adapter
//                    productAdapter=new ItemsAdapter( getActivity(),accResult );
                }
            }

            return "One Row Insert";
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate( values );
        }
        @Override
        protected void onPostExecute(String s) {
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        catogryRadioes.clearCheck();
        spinner.setSelection( 0 );
        recyclerView.setAdapter( null );

    }
}
