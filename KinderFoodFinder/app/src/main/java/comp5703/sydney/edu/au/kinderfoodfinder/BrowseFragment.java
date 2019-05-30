package comp5703.sydney.edu.au.kinderfoodfinder;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

import comp5703.sydney.edu.au.greendao.gen.DaoSession;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.Accreditation;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.DaoUnit;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.Product;
import okhttp3.internal.Util;

public class BrowseFragment extends Fragment
{
    // defined variables

    View view;

    private List<String> tabs;
    private RadioGroup browsegorup;
    private SearchView searchView;
    private ListView listView;
    int typeSelected;
    private ArrayList<String> result=new ArrayList<>(  );
    private String selected;
    private ArrayList<String> categroylist=new ArrayList<>(  );
    private ArrayList<String> ratinglist=new ArrayList<>(  );
    private ArrayList<String> accreditationlist=new ArrayList<>(  );
    private ArrayList<Product> egglist=new ArrayList<>(  );
    private ArrayList<Product> piglist=new ArrayList<>(  );
    private ArrayList<Product> chickenlist=new ArrayList<>(  );
    private ArrayList<Accreditation> test=new ArrayList<>(  );
    private Fragment broseresultFragment;
    int type=1;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {

        view = inflater.inflate(R.layout.fragment_browse, container, false);



        categroylist=new ArrayList<>(  );
        ratinglist=new ArrayList<>(  );
        accreditationlist=new ArrayList<>(  );

        BrowsePageAdapter adapter = new BrowsePageAdapter(getChildFragmentManager());
        listView=view.findViewById( R.id.browselv );

        browsegorup=view.findViewById( R.id.radioBrowse );
        additem();
        final Bundle args= new Bundle(  );
        Utility.setListViewHeightBasedOnChildren( listView );
        browsegorup.setOnCheckedChangeListener( new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                typeSelected=checkedId;
                selectList( checkedId );
                ArrayAdapter<String> stringAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,result);

                listView.setAdapter(stringAdapter);
                Utility.setListViewHeightBasedOnChildren(listView);
                Log.d("checkid",String.valueOf( checkedId ));
            }
        } );
        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                broseresultFragment=new BrowseResultFragment();
                args.putInt( "checkid", type);
                args.putInt( "position",position );
                args.putString( "title", result.get( position ) );
//                args.putStringArrayList( "list", finalList );
                broseresultFragment.setArguments( args );
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, broseresultFragment).addToBackStack( null ).commit();
            }
        } );

        return view;
    }

    public void additem() {
        categroylist=new ArrayList<>(  );


        ratinglist.add( "Best" );
        ratinglist.add( "Good" );
        ratinglist.add( "Avoid" );
//        egglist=DaoUnit.getInstance().getcategoryList( "egg" );
//        chickenlist=DaoUnit.getInstance().getcategoryList( "chicken" );
//        piglist=DaoUnit.getInstance().getcategoryList( "pig" );
        test=DaoUnit.getInstance().getAcc();
        String typeinfo="";
        ArrayList<Product> ps =DaoUnit.getInstance().getProduct();

        categroylist.add( "ALL" );
        for (Product product:ps){
            String type=product.getCategory().toUpperCase();
            if(! typeinfo.contains( type)){
                typeinfo=typeinfo+type+";";
                categroylist.add( type );
            }
        }

        String info="";
        for (Accreditation accreditation:test){
            String acc=accreditation.getAccreditation();
            if(! info.contains( acc)){
                info=info+acc+";";
                accreditationlist.add( acc );
            }
        }
    }

    public void selectList(int i){

        switch (i)
        {
            case R.id.radioCategory:
                result=categroylist;
                type=1;
                break;

            case R.id.radioAcc:
                result=accreditationlist;
                type=2;
                break;

            case R.id.radioRate:
                result=ratinglist;
                type=3;
                break;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );

    }

    @Override
    public void onStart() {
        super.onStart();
        browsegorup.clearCheck();
        listView.setAdapter( null );

    }

}
