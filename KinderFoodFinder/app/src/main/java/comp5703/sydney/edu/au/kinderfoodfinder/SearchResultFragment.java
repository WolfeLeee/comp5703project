package comp5703.sydney.edu.au.kinderfoodfinder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;

public class SearchResultFragment extends Fragment {



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

    private Fragment searchresultFragment;


    ListView resultlv;
    SearchView eggsv;

    ArrayList<Items> itemsArrayList;
    ArrayList<String> list;
    ArrayList<Items> eggsList=new ArrayList<>( );
    ArrayList<Items> pigList =new ArrayList<>(  );
    ArrayList<Items> chickenList =new ArrayList<>(  );
    ArrayList<Items> bestList =new ArrayList<>(  );
    ArrayList<Items> goodList=new ArrayList<>(  );
    ArrayList<Items> avoidList=new ArrayList<>(  );
    ArrayList<Items> resultList =new ArrayList<>(  );



//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        mPage = getArguments().getInt("page");
//
//
//
//
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.tablayout_browse, container, false );


        final ListView listView=view.findViewById(R.id.listview );
        int t=mPage;

        Log.d("page",String.valueOf( t ));




        return view;
    }

    }
