package comp5703.sydney.edu.au.kinderfoodfinder;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
{
    // defined variables
    private Fragment selectedFragment;
    private Fragment homeFragment;
    private Fragment searchFragment;
    private Fragment locateFragment;
    private Fragment reportFragment;
    private Fragment ourRatingFragment;
    private Fragment moreFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set up
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        homeFragment = new HomeFragment();
        searchFragment = new SearchFragment();
        locateFragment = new LocateFragment();
        reportFragment = new ReportFragment();
        ourRatingFragment = new OurRatingFragment();
        moreFragment = new MoreFragment();
        selectedFragment = homeFragment;

        // make the navigation page when first to using app
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

    }

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
                case R.id.navigation_locate:
                    selectedFragment = locateFragment;
                    break;
                case R.id.navigation_report:
                    selectedFragment = reportFragment;
                    break;
                case R.id.navigation_our_rating:
                    selectedFragment = ourRatingFragment;
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
