package comp5703.sydney.edu.au.kinderfoodfinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
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

public class BrowseResultFragment extends Fragment {

    private Fragment browseFragment;
    private Fragment detailFragment;


    private Toolbar toolbar;
    private BottomNavigationView navigation;
    int mPage;
    TextView textView;




    ListView resultlv;
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

    public static BrowseResultFragment newInstance(int page, int position) {
        Bundle args = new Bundle();

        args.putInt("page", page);
        args.putInt( "position",position );
        BrowseResultFragment fragment = new BrowseResultFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt("page");
        position=getArguments().getInt( "position" );
        title=getArguments().getString( "title" );
    }



    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.result_fragment, container, false );




        browseFragment=new BrowseFragment();

        readEggData();
        readporkData();
        readChickenData();


        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);
        textView=getActivity().findViewById( R.id.title );

//        navigation = (BottomNavigationView) getActivity().findViewById(R.id.navigation);
//        navigation.setVisibility(View.GONE);

        int n=position+1;
        page=mPage;

        Log.d( "pppp",String.valueOf( n ) );

        if(n==1){
            if(page==1){
                resultList=eggsList;
            }else if(page==3){
                resultList=bestList;

            }


        }else if(n==2){
            if(page==1){
                resultList=pigList;
            }else if(page==3) {
                resultList = goodList;
            }
        }else if(n==3){
            if(page==1){
                resultList=chickenList;
            }else if(page==3){
                resultList=avoidList;
            }else {
            }

        }else {
        }

        textView.setText( title );

        Log.d( "pppp",String.valueOf( resultList.size() ) );

        // tool bar listener
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // go to login fragment
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.fragment_container, browseFragment).commit();



                // remove toolbar again
                toolbar.setVisibility(View.GONE);
                textView.setText( "Back" );

                // enable navigation bar again
//                navigation.setVisibility(View.VISIBLE);
            }
        });

        resultlv=view.findViewById( R.id.resultlv );
        eggsv=view.findViewById( R.id.eggsv );


        final ItemsAdapter itemsAdapter= new ItemsAdapter( getActivity(),resultList );
        resultlv.setAdapter( itemsAdapter );
        Utility.setListViewHeightBasedOnChildren(resultlv);


//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,list);
//        resultlv.setAdapter( adapter );

        eggsv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                itemsAdapter.getFilter().filter(newText);
                return false;
            }
        });

        Intent intent =getActivity().getIntent();
        final String userID=intent.getStringExtra( "userID" );
        final String gender=intent.getStringExtra( "gender" );
        final String birthday=intent.getStringExtra( "birthday" );


        resultlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Items c= (Items) itemsAdapter.getItem(position);
            Intent intent = new Intent(getActivity(), DetailActivity.class);
                if (intent != null) {

                    intent.putExtra("brand", c.getBrand());
                    intent.putExtra("type", c.getType());
                    intent.putExtra("accreditation", c.getAccreditation());
                    intent.putExtra("rating", c.getRating());
                    intent.putExtra("location", c.getAvailable());
                    intent.putExtra( "gender",gender );
                    intent.putExtra( "birthday",birthday );
                    intent.putExtra( "userID",userID );

//                    intent.putExtra("img", String.valueOf(c.getImg()));
                    startActivityForResult(intent, VIEW_ITEM_REQUEST_CODE);

                }
            }
        });


        return view;
    }


    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }








    // test the function
    public void readEggData(){
        Items items;
        itemsArrayList=new ArrayList<Items>(  );
        InputStream is=getResources().openRawResource(R.raw.eggs);
        BufferedReader reader= new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );
        String line="";
        try{
            int i=0;
            while ((line=reader.readLine())!= null){


                String[] token= line.split(",(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)",-1);

                Log.d("length :",String.valueOf( i )+"$$$"+String.valueOf( token.length ));
                items=new Items();
                i++;
                Log.d("read",String.valueOf( token.length ));
                if(token.length==4){
                    Log.d("lines :",String.valueOf( i )+line);
                    items.setType( "Eggs" );


                    items.setAccreditation( token[0] );
                    items.setBrand( token[1] );
                    items.setRating( token[2] );
                    items.setAvailable( token[3] );
                    if(token[2].equalsIgnoreCase( "BEST" )){
                        bestList.add( items );
                    }else if(token[2].equalsIgnoreCase( "GOOD" )){
                        goodList.add( items);
                    }else if(token[2].equalsIgnoreCase( "AVOID" )){
                        avoidList.add( items );
                    }

                    eggsList.add( items );

                    itemsArrayList.add(items);
                }
                else {
//
                }

            }
            Log.d("Size :",String.valueOf(itemsArrayList.size()));

        } catch (IOException e) {

            e.printStackTrace();
        }


    }

    public void readChickenData(){
        Items items;
        itemsArrayList=new ArrayList<Items>(  );
        InputStream is=getResources().openRawResource(R.raw.chickens);
        BufferedReader reader= new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );
        String line="";
        try{
            int i=0;
            while ((line=reader.readLine())!= null){


                String[] token= line.split(",(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)",-1);

                Log.d("length :",String.valueOf( i )+"$$$"+String.valueOf( token.length ));

                items=new Items();
                i++;
                Log.d("read",String.valueOf( token.length ));
                if(token.length==4){
                    Log.d("lines :",String.valueOf( i )+line);
                    items.setType( "Chickens" );


                    items.setAccreditation( token[0] );
                    items.setBrand( token[1] );
                    items.setRating( token[2] );
                    items.setAvailable( token[3] );
                    if(token[2].equalsIgnoreCase( "BEST" )){
                        bestList.add( items );
                    }else if(token[2].equalsIgnoreCase( "GOOD" )){
                        goodList.add( items);
                    }else if(token[2].equalsIgnoreCase( "AVOID" )){
                        avoidList.add( items );
                    }

                    chickenList.add( items );
                    itemsArrayList.add(items);
                }
                else {
//
                }

            }
            Log.d("Size :",String.valueOf(itemsArrayList.size()));

        } catch (IOException e) {

            e.printStackTrace();
        }


    }

    public void readporkData(){
        Items items;
        itemsArrayList=new ArrayList<Items>(  );
        InputStream is=getResources().openRawResource(R.raw.pigs);
        BufferedReader reader= new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );
        String line="";
        try{
            int i=0;
            while ((line=reader.readLine())!= null){


                String[] token= line.split(",(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)",-1);

                Log.d("length :",String.valueOf( i )+"$$$"+String.valueOf( token.length ));
                items=new Items();
                i++;
                Log.d("read",String.valueOf( token.length ));
                if(token.length==4){
                    Log.d("lines :",String.valueOf( i )+line);
                    items.setType( "Pork" );


                    items.setAccreditation( token[0] );
                    items.setBrand( token[1] );
                    items.setRating( token[2] );
                    items.setAvailable( token[3] );

                    if(token[2].equalsIgnoreCase( "BEST" )){
                        bestList.add( items );
                    }else if(token[2].equalsIgnoreCase( "GOOD" )){
                        goodList.add( items);
                    }else if(token[2].equalsIgnoreCase( "AVOID" )){
                        avoidList.add( items );
                    }

                    pigList.add( items );




                    itemsArrayList.add(items);
                }
                else {
//
                }

            }
            Log.d("Size :",String.valueOf(itemsArrayList.size()));

        } catch (IOException e) {

            e.printStackTrace();
        }


    }

}
