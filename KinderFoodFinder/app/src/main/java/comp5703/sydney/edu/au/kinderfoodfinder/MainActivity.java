package comp5703.sydney.edu.au.kinderfoodfinder;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;

import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.DaoUnit;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.Product;

public class MainActivity extends AppCompatActivity
{
    /* * * * * * * * * * *
     * Defined Variables *
     * * * * * * * * * * */
    private Fragment selectedFragment;
    private Fragment homeFragment;
    private Fragment searchFragment;
    private Fragment browseFragment;
    private Fragment locateFragment;
    private Fragment moreFragment;

    private Toolbar toolbar;
    private BottomNavigationView navigation;




//    String brandVersion,brandUpdate,storeVersion,storeUpdate,status;

    /* * * * * * * * * * *
     * On Created Method *
     * * * * * * * * * * */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.INTERNET},
                1);
        // set up tool bar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setVisibility(View.GONE);

        // set up
        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        homeFragment = new HomeFragment();
        searchFragment = new SearchFragment();
        browseFragment = new BrowseFragment();
        locateFragment = new LocateFragment();
        moreFragment = new MoreFragment();
        selectedFragment = homeFragment;

        // make the navigation page when first to using app
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

        int id = getIntent().getIntExtra("id", 0);
        String sid= getIntent().getStringExtra( "sid" );
        int key=getIntent().getIntExtra( "key",0 );
        if (id == 1) {
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.fragment_container,new LocateFragment())
//                    .addToBackStack(null)
//                    .commit();
//            selectedFragment=locateFragment;
//
//            getSupportFragmentManager().beginTransaction()
//                    .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
//                    .replace(R.id.fragment_container, selectedFragment).commit();
//            navigation.setSelectedItemId(R.id.navigation_locate);

            Bundle bundle=new Bundle(  );
            String brand_name = getIntent().getStringExtra("LOCATE");
            bundle.putInt( "LOCATE_KEY", 1 );
            bundle.putString("Brand", brand_name);
            Fragment locatefragment = new LocateFragment();
            locatefragment.setArguments( bundle );
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container,locatefragment)
                    .addToBackStack(null).addToBackStack( null )
                    .commit();
        }

        if (id == 2) {
            navigation.setSelectedItemId(R.id.navigation_more);


            Bundle bundle=new Bundle(  );

            bundle.putString( "sid",sid );
            bundle.putInt( "key",key );

            Fragment reportFragment= new ReportAddressFragment();
            reportFragment.setArguments( bundle );
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container,reportFragment)
                    .commit();

        }
        if (id == 3) {

            navigation.setSelectedItemId(R.id.navigation_more);
        }
        if(id==4){
            navigation.setSelectedItemId( R.id.navigation_search );
        }
        if(id==5){
            navigation.setSelectedItemId( R.id.navigation_browse );
        }


//        Intent intent =getIntent();
//        final String brandVersion=intent.getStringExtra( "brand_version" );
//        final String brandUpdate=intent.getStringExtra( "brand_update" );
//        final String status=intent.getStringExtra( "status" );
//        final String userID=intent.getStringExtra( "userID" );
//        final String gender=intent.getStringExtra( "gender" );
//        final String birthday=intent.getStringExtra( "birthday" );
//        Log.d("statistic",brandVersion+", "+brandVersion+", "+status+", "+userID+", "+gender+", "+birthday);
//
//        Intent intent1=new Intent( MainActivity.this,DetailActivity.class );
    }



    /* * * * * *
     * Methods *
     * * * * * */
    public BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener()
    {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item)
        {
            switch (item.getItemId())
            {
                case R.id.navigation_home:
                    selectedFragment = homeFragment;
                    break;
                case R.id.navigation_search:
                    selectedFragment = searchFragment;
                    break;
                case R.id.navigation_browse:
                    selectedFragment = browseFragment;
                    break;
                case R.id.navigation_locate:
                    selectedFragment = locateFragment;
                    break;
                case R.id.navigation_more:
                    selectedFragment = moreFragment;
                    break;
                default:
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).addToBackStack( null ).commit();

            return true;
        }
    };


    public ArrayList<String> getBasicData(String a,String b,String c){
        ArrayList<String> data=new ArrayList<>(  );
        data.add( a );
        data.add( b );
        data.add( c );
        return data;
    }

    public void persionalData(){

        Intent intent =getIntent();

        final String userID=intent.getStringExtra( "userID" );
        final String gender=intent.getStringExtra( "gender" );
        final String birthday=intent.getStringExtra( "birthday" );

        Intent intent1=new Intent( MainActivity.this,DetailActivity.class );

        intent.putExtra( "gender",gender );
        intent.putExtra( "birthday",birthday );
        intent.putExtra( "userID",userID );


    }


    @Override
    public void onBackPressed() {
//        super.onBackPressed();

        int count = getSupportFragmentManager().getBackStackEntryCount();
        Log.d( "back",String.valueOf( count ) );
        Fragment fragment=getSupportFragmentManager().getPrimaryNavigationFragment();

        toolbar.setVisibility( View.GONE );
        navigation.setVisibility( View.VISIBLE );

        navigation.setSelectedItemId( R.id.navigation_home );


    }
}
