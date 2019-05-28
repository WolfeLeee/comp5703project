package comp5703.sydney.edu.au.kinderfoodfinder;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import comp5703.sydney.edu.au.kinderfoodfinder.Adapter.BrandAdapter;
import comp5703.sydney.edu.au.kinderfoodfinder.LocalDatabase.Database;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.DaoUnit;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.Product;

public class ReportFragment extends Fragment implements AdapterView.OnItemSelectedListener


{
    // defined variables
    TextView tv_cate;

    private Toolbar toolbar;
    private BottomNavigationView navigation;
    Fragment fragmentMore, fragmentBrand_eggs, fragmentBrand_Chicken, fragmentBrand_Pork,fragmentLocation;
    int key;
    ArrayList<String> items;
    int category=0;
    ArrayList<String> brandList,sidList;
    Spinner spinner;
    ListView listView;
    ArrayList<Product> productArrayList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
//        key = getArguments().getInt( "key" );

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_report, container, false);


        tv_cate = view.findViewById(R.id.tv_cate);

        fragmentMore = new MoreFragment();
        fragmentBrand_eggs = new BrandByEggsFragment();
        fragmentBrand_Chicken = new BrandByChickenFragment();
        fragmentBrand_Pork = new BrandByPorkFragment();
        fragmentLocation=new LocateFragment();

        // disable navigation bar at the bottom
        navigation = (BottomNavigationView) getActivity().findViewById(R.id.navigation);
        navigation.setVisibility(View.GONE);

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);

        items=new ArrayList<>(  );
        String info="";
        ArrayList<Product> test=DaoUnit.getInstance().getProduct();

        for (Product product:test){
            String brand=product.getCategory().toUpperCase();
            if(! info.contains( brand)){
                info=info+brand+";";
                items.add( brand );
            }
        }

         spinner=view.findViewById( R.id.spinner );
         listView=view.findViewById( R.id.categoryListView );

//        ArrayList<Product> productArrayList=DaoUnit.getInstance().getcategoryList( items.get( 0));
//        getStringlist( 0 );
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(  getActivity(), android.R.layout.simple_list_item_1, brandList);
//        listView.setAdapter( arrayAdapter );
//        Utility.setListViewHeightBasedOnChildren( listView );

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        spinner.setAdapter(adapter1);
        spinner.setClickable( false );

        spinner.setOnItemSelectedListener( this );
        // tool bar listener
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toolbar.setVisibility(View.GONE);

                // enable navigation bar again
                navigation.setVisibility(View.VISIBLE);
                getActivity().getSupportFragmentManager().popBackStack();


            }
        });

        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                Fragment reportAddressFragment=new ReportAddressFragment();
//                Bundle bundle=new Bundle(  );
//                bundle.putString( "sid",sidList.get( position ) );
////
//
//                bundle.putInt( "key",0 );
//                reportAddressFragment.setArguments( bundle );
//                getActivity().getSupportFragmentManager().beginTransaction()
//                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
//                        .replace(R.id.fragment_container, reportAddressFragment).addToBackStack( null ).commit();
//                toolbar.setVisibility( View.GONE );

                Intent intent=new Intent( getActivity(),ReportResultActivity.class );
                intent.putExtra( "sid",sidList.get( position ) );
                startActivity( intent );
            }
        } );


        return view;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        category=position;
        String value=items.get( position );
        new ReportResult().doInBackground( value );
//        getStringlist( position );
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(  getActivity(), android.R.layout.simple_list_item_1, brandList);
        listView.setAdapter( arrayAdapter );
        Utility.setListViewHeightBasedOnChildren( listView );
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    public void getStringlist(int position){
         productArrayList=DaoUnit.getInstance().getcategoryList( items.get( position));
            brandList=new ArrayList<>(  );
            sidList=new ArrayList<>(  );
            for(Product product:productArrayList){
                brandList.add( product.getBrand_Name() );
                sidList.add( product.getSid() );
            }

        }


    private class ReportResult extends AsyncTask<String, String, String> {
        Context context;
        @Override
        protected String doInBackground(String... strings) {
            //select search type brand name or accreditation
            //select product type eggs, chicken or pork

            productArrayList=DaoUnit.getInstance().getcategoryList( strings[0]);
            brandList=new ArrayList<>(  );
            sidList=new ArrayList<>(  );
            for(Product product:productArrayList){
                brandList.add( product.getBrand_Name() );
                sidList.add( product.getSid() );
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



}
