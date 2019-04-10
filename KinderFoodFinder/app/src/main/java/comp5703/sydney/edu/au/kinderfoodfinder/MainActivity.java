package comp5703.sydney.edu.au.kinderfoodfinder;

import android.Manifest;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

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
            navigation.setSelectedItemId(R.id.navigation_locate);
        }

        if (id == 2) {
            navigation.setSelectedItemId(R.id.navigation_more);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container,new ReportFragment())
                    .addToBackStack(null)
                    .commit();
//            selectedFragment=locateFragment;
//
//            getSupportFragmentManager().beginTransaction()
//                    .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
//                    .replace(R.id.fragment_container, selectedFragment).commit();

        }


        if (id == 3) {

            navigation.setSelectedItemId(R.id.navigation_more);
        }




    }

    /* * * * * *
     * Methods *
     * * * * * */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
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

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

            return true;
        }
    };



}
