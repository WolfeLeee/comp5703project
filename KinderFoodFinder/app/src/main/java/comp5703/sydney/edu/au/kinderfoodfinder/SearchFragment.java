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
        recyclerView=view.findViewById( R.id.recyclerview );
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

        catogryRadioes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // clear search view text
                searchView.setQuery( "",false );
                category = checkedId;
                String type=items.get( temp );
                new Searchbrand().doInBackground(   );
                // set list view
                if(category==R.id.radioBrandName){
//                    product.setAdapter( brandAdapter );
//                    brandAdapter.notifyDataSetChanged();
//                    Utility.setListViewHeightBasedOnChildren( product );
                    brandRecyclerAdapter=new BrandRecyclerAdapter( getActivity(),result );
                    recyclerView.setAdapter( brandRecyclerAdapter );
                    brandRecyclerAdapter.notifyDataSetChanged();
                }else {
//                    product.setAdapter( productAdapter );
//                    productAdapter.notifyDataSetChanged();
//                    Utility.setListViewHeightBasedOnChildren(product);
                    accRecyclerAdapter=new AccRecyclerAdapter( getActivity(),accResult );
                    recyclerView.setAdapter( accRecyclerAdapter );
                    accRecyclerAdapter.notifyDataSetChanged();
                }
            }
        });

//        searchView.setOnSearchClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//            }
//        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(category==R.id.radioBrandName){
                    new Searchbrand().doInBackground( "brand",newText );
//                    result=DaoUnit.getInstance().searchByBrand( category,items.get( temp ),newText );
//                    BrandAdapter brandAdapter=new BrandAdapter( getActivity(),result );
//                    product.setAdapter( brandAdapter );
//                    brandAdapter.notifyDataSetChanged();
//                    Utility.setListViewHeightBasedOnChildren( product );
                    brandRecyclerAdapter=new BrandRecyclerAdapter( getActivity(),result );
                    recyclerView.setAdapter( brandRecyclerAdapter );
                    brandRecyclerAdapter.notifyDataSetChanged();
                }else if(category==R.id.radioAccreditation){
                    new Searchbrand().doInBackground( "acc",newText );

//                    accResult=DaoUnit.getInstance().searchByAcc( category,items.get( temp ),newText );
//                    ItemsAdapter productAdapter=new ItemsAdapter( getActivity(),accResult );
//                    product.setAdapter( productAdapter );
//                    productAdapter.notifyDataSetChanged();
//                    Utility.setListViewHeightBasedOnChildren(product);
                    accRecyclerAdapter=new AccRecyclerAdapter( getActivity(),accResult );
                    recyclerView.setAdapter( accRecyclerAdapter );
                    accRecyclerAdapter.notifyDataSetChanged();
                }

                return false;
            }
        });

//        product.setOnItemClickListener( new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                Product p= (Product) productAdapter.getItem( position );
//                if(category==R.id.radioBrandName){
//                    Product p=result.get( position );
//                    String accId=p.getAccreditation().get( 0 ).getSid();
//                    Intent intent = new Intent(getActivity(), DetailActivity.class);
//                    if (intent != null) {
////
//                        intent.putExtra( "stringId",p.getSid() );
//                        intent.putExtra( "page","search" );
//                        List<Accreditation> accreditation= (List<Accreditation>) p.getAccreditation();
//                        intent.putExtra( "accid",accId );
////                    intent.putExtra("img", String.valueOf(c.getImg()));
//                        startActivity( intent );
//
//                    }
//                }else if(category==R.id.radioAccreditation) {
//                    Intent intent = new Intent( getActivity(), Detail2Activity.class );
//                    Items acc=accResult.get( position );
//                    if (intent != null) {
////                        Accreditation accreditation= (Accreditation) p.getAccreditation();
//                        intent.putExtra( "stringId", acc.getSid());
//                        intent.putExtra( "page", "search" );
//                        intent.putExtra( "accid", acc.getAccID() );
//                        startActivity( intent );
//                    }
//                }
//            }
//        } );

        //*************************************************************************
        //Recyclerview

        layoutManager=new LinearLayoutManager( getContext() );
        recyclerView.setHasFixedSize( true );
        recyclerView.setLayoutManager( layoutManager );
        brandRecyclerAdapter=new BrandRecyclerAdapter( getActivity(),result );
//        recyclerView.setAdapter( brandRecyclerAdapter );

        accRecyclerAdapter=new AccRecyclerAdapter( getActivity(),accResult );

        return view;
    }



    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d("spinner",String.valueOf( position ));
        Log.d("spinner",items.get( position ));
        searchView.setQuery( "",false );
        temp=position;
        new Searchbrand().doInBackground( );
        if(category==R.id.radioBrandName){
//            result=DaoUnit.getInstance().getAllinsearchBrand(items.get( temp ) );

            Log.d( "brandsize",String.valueOf( result.size() ) );
//            BrandAdapter brandAdapter=new BrandAdapter( getActivity(),result );
//            product.setAdapter( brandAdapter );
//            Utility.setListViewHeightBasedOnChildren( product );
            brandRecyclerAdapter=new BrandRecyclerAdapter( getActivity(),result );
            recyclerView.setAdapter( brandRecyclerAdapter );
            brandRecyclerAdapter.notifyDataSetChanged();

        }else if(category==R.id.radioAccreditation) {

////            accResult=DaoUnit.getInstance().getAllinsearchAcc( items.get( temp ) );
//            ItemsAdapter productAdapter=new ItemsAdapter( getActivity(),accResult );
//            product.setAdapter( productAdapter );
//            productAdapter.notifyDataSetChanged();
//            Utility.setListViewHeightBasedOnChildren(product);
            accRecyclerAdapter=new AccRecyclerAdapter( getActivity(),accResult );
            recyclerView.setAdapter( accRecyclerAdapter );
            accRecyclerAdapter.notifyDataSetChanged();

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //asyncTask for search features
    private class Searchbrand extends AsyncTask<String, String, String> {
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
