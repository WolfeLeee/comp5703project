package comp5703.sydney.edu.au.kinderfoodfinder;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.AccEntity;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.Accreditation;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.DaoUnit;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.Product;

public class BrowseResultFragment extends Fragment {

    private Fragment browseFragment;
    private Fragment detailFragment;


    private Toolbar toolbar;
    private BottomNavigationView navigation;
    int mPage;
    TextView textView;




    SearchView eggsv;
    int page=0;
    int position=0;
    String title;
    ArrayList<Items> itemsArrayList;
    ArrayList<String> list;
    public final int VIEW_ITEM_REQUEST_CODE = 647;
    ArrayList<Items> eggsList=new ArrayList<>( );
    ArrayList<Items> pigList =new ArrayList<>(  );
    ArrayList<Items> chickenList =new ArrayList<>(  );
    ArrayList<Items> bestList =new ArrayList<>(  );
    ArrayList<Items> goodList=new ArrayList<>(  );
    ArrayList<Items> avoidList=new ArrayList<>(  );
    ArrayList<Items> resultList =new ArrayList<>(  );
    ArrayList<Product> brandresult=new ArrayList<>(  );
    ArrayList<Items> accResult=new ArrayList<>(  );

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private BrandRecyclerAdapter brandRecyclerAdapter;
    private AccRecyclerAdapter accRecyclerAdapter;


    int checkid;
//    public static BrowseResultFragment newInstance(int page, int position) {
//        Bundle args = new Bundle();
//
//        args.putInt("page", page);
//        args.putInt( "position",position );
//        BrowseResultFragment fragment = new BrowseResultFragment();
//        fragment.setArguments(args);
//
//        return fragment;
//    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkid = getArguments().getInt("checkid");
        position=getArguments().getInt( "position" );
        title=getArguments().getString( "title" );

    }



    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.result_fragment, container, false );




        brandresult=new ArrayList<>(  );
        accResult=new ArrayList<>(  );

        new BrowseResult().doInBackground(  );



        browseFragment=new BrowseFragment();


        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);
        textView=getActivity().findViewById( R.id.title );

        navigation = (BottomNavigationView) getActivity().findViewById(R.id.navigation);
        navigation.setVisibility(View.GONE);

        recyclerView=view.findViewById( R.id.recyclerlist );
        eggsv=view.findViewById( R.id.eggsv );


        textView.setText( title );
        Log.d("t result",String.valueOf( checkid )+String.valueOf( position )+title);

        Log.d( "pppp",String.valueOf( resultList.size() ) );

        // tool bar listener
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // go to login fragment
//                getActivity().getSupportFragmentManager().beginTransaction()
//                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
//                        .replace(R.id.fragment_container, browseFragment).commit();
                // remove toolbar again
                toolbar.setVisibility(View.GONE);
                textView.setText( "Back" );

                // enable navigation bar again
                navigation.setVisibility(View.VISIBLE);
                getActivity().getSupportFragmentManager().popBackStack();

            }
        });



        layoutManager=new LinearLayoutManager( getActivity() );
        recyclerView.setHasFixedSize( true );
        recyclerView.setLayoutManager( layoutManager );
//        brandRecyclerAdapter=new BrandRecyclerAdapter( getActivity(),brandresult );
//        recyclerView.setAdapter( brandRecyclerAdapter );
//
//        accRecyclerAdapter=new AccRecyclerAdapter( getActivity(),accResult );



        if(checkid>1){


//            accRecyclerAdapter=new AccRecyclerAdapter( getActivity(),accResult );
//            recyclerView.setAdapter( accRecyclerAdapter );
//            accRecyclerAdapter.notifyDataSetChanged();

            recyclerView.setAdapter( accRecyclerAdapter );

            Log.d("aaa result",String.valueOf( accResult.size() ));


        }else if(checkid==1){
//            resultlv.setAdapter( brandAdapter );
//            Utility.setListViewHeightBasedOnChildren(resultlv);


//            brandresult= DaoUnit.getInstance().getcategoryList( title );

//            brandRecyclerAdapter=new BrandRecyclerAdapter( getActivity(),brandresult );
            recyclerView.setAdapter( brandRecyclerAdapter );
//            brandRecyclerAdapter.notifyDataSetChanged();

            Log.d("search result",String.valueOf( brandresult.size() ));

        }

        eggsv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(checkid>1){
//                    productAdpater.getFilter().filter(newText);
//                    productAdpater.notifyDataSetChanged();
//                    brandRecyclerAdapter.getFilter().filter( newText );
//                    brandRecyclerAdapter.notifyDataSetChanged();
                    accRecyclerAdapter.getFilter().filter( newText );
                    accRecyclerAdapter.notifyDataSetChanged();
                }else if(checkid==1){
//                    brandAdapter.getFilter().filter( newText );
//                    brandAdapter.notifyDataSetChanged();
//                    accRecyclerAdapter.getFilter().filter( newText );
//                    accRecyclerAdapter.notifyDataSetChanged();
                    brandRecyclerAdapter.getFilter().filter( newText );
                    brandRecyclerAdapter.notifyDataSetChanged();
                }


                return true;
            }
        });

        return view;
    }
    private class BrowseResult extends AsyncTask<String, String, String> {
        Context context;
        @Override
        protected String doInBackground(String... strings) {
            //select search type brand name or accreditation
            //select product type eggs, chicken or pork
            if(checkid==1){
                brandresult= DaoUnit.getInstance().getcategoryList( title );
                brandRecyclerAdapter=new BrandRecyclerAdapter( getActivity(),brandresult );
                brandRecyclerAdapter.notifyDataSetChanged();
                Log.d("result",String.valueOf( checkid ));


            }else if(checkid==2){
                accResult=DaoUnit.getInstance().getAccList( title );
                accRecyclerAdapter=new AccRecyclerAdapter( getActivity(),accResult );
                accRecyclerAdapter.notifyDataSetChanged();

            }else if(checkid==3){
                accResult=DaoUnit.getInstance().getRatingList( title );
                accRecyclerAdapter=new AccRecyclerAdapter( getActivity(),accResult );
                accRecyclerAdapter.notifyDataSetChanged();
            }else{
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
