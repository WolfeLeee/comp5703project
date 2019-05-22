package comp5703.sydney.edu.au.kinderfoodfinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import java.util.ArrayList;

import comp5703.sydney.edu.au.greendao.gen.AccreditationDao;
import comp5703.sydney.edu.au.greendao.gen.DaoMaster;
import comp5703.sydney.edu.au.greendao.gen.DaoSession;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.DaoUnit;

public class TestFragment extends Fragment {

    public static final String ARGS_PAGE = "args_page";
    public final int VIEW_ITEM_REQUEST_CODE = 647;
    public final int VIEW_PLAYER_REQUEST_CODE = 648;
    private int mPage;
    private ListView listView;
    private ArrayList<String> categroylist=new ArrayList<>(  );
    private ArrayList<String> ratinglist=new ArrayList<>(  );
    private ArrayList<String> accreditationlist=new ArrayList<>(  );
    private Fragment resultFragment;
    private LinearLayout linearLayout;

    ListView resultlv;
    SearchView eggsv;
    int page=0;
    ArrayList<Items> itemsArrayList;
    ArrayList<String> list;
    ArrayList<Items> eggsList=new ArrayList<>( );
    ArrayList<Items> pigList =new ArrayList<>(  );
    ArrayList<Items> chickenList =new ArrayList<>(  );
    ArrayList<Items> bestList =new ArrayList<>(  );
    ArrayList<Items> goodList=new ArrayList<>(  );
    ArrayList<Items> avoidList=new ArrayList<>(  );
    ArrayList<Items> resultList =new ArrayList<>(  );

    public static TestFragment newInstance(int page) {
        Bundle args = new Bundle();

        args.putInt(ARGS_PAGE, page);
        TestFragment fragment = new TestFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARGS_PAGE);

        additem();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tablayout_browse,container,false);


        final ListView listView=view.findViewById(R.id.listview );
//
        ArrayList<String> list=new ArrayList<String>( );

        Log.d("mpage","page =="+String.valueOf( mPage ));


        if(mPage==1) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>( getActivity(), android.R.layout.simple_list_item_1, categroylist );
            listView.setAdapter( adapter );
            list=categroylist;




//
//
        }else if(mPage==3){
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,ratinglist);
            listView.setAdapter(adapter);
            list=ratinglist;
        }else if(mPage==2) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,accreditationlist);
            listView.setAdapter(adapter);
            list=accreditationlist;
        }

//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,list);
//
//        listView.setAdapter(adapter);


        final ArrayList<String> finalList = list;
        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {




                resultFragment=new BrowseResultFragment();
                Bundle args= new Bundle(  );
                args.putInt( "page",mPage );
                args.putInt( "position",position );
                args.putString( "title", finalList.get( position ) );
//                args.putStringArrayList( "list", finalList );
                resultFragment.setArguments( args );
                getActivity().getSupportFragmentManager().beginTransaction()

                        .replace(R.id.fragment_container, resultFragment).commit();

//

            }
        } );
        return view;
    }



    public void additem(){
        categroylist.add( "Eggs" );
        categroylist.add( "Pork" );
        categroylist.add( "Chicken" );
        ratinglist.add( "BEST" );
        ratinglist.add( "GOOD" );
        ratinglist.add( "Avoid" );

        ;


//        accreditationlist.add( "Cage, Caged" );
//        accreditationlist.add( "RSPCA Approved Indoor" );
//        accreditationlist.add( "FREPA" );
//        accreditationlist.add( "Humane Choice" );
//        accreditationlist.add( "Free Range Farmers Association" );
//        accreditationlist.add( "Australian Certified Organic" );
//        accreditationlist.add( "NASAA, NASAA Organic" );
//        accreditationlist.add( "Organic Food Chain, OFC" );
//        accreditationlist.add( "Coles Egg Production Standard for Free Range" );
//        accreditationlist.add( "DEMETER, DEMETER Bio-dynamic" );
//        accreditationlist.add( "PROOF" );

    }


//    public Fragment getItem(int position) {
//        return BrowseResultFragment.newInstance(position + 1);
//    }





}
