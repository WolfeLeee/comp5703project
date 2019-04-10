package comp5703.sydney.edu.au.kinderfoodfinder;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class BrowsePageAdapter extends FragmentPagerAdapter {
    public final int COUNT = 4;
    private String[] titles = new String[]{"Category", "Accreditation", "Rating"};
    private Context context;

    private List<String> mTitles;
    private List<Fragment> mFragments;



    public BrowsePageAdapter(FragmentManager fm) {
        super(fm);

    }




    @Override
    public Fragment getItem(int position) {
        return TestFragment.newInstance(position + 1);
    }

    @Override
    public int getCount() {
        return titles.length;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
