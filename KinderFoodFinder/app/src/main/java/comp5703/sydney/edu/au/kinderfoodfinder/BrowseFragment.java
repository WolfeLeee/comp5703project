package comp5703.sydney.edu.au.kinderfoodfinder;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class BrowseFragment extends Fragment
{
    // defined variables

    TabLayout tabLayout;
    ViewPager viewPager;
    View view;



    String[] mTitle = new String[3];
    String[] mData = new String[3];
    private List<String> mTitles;
    private List<Fragment> mFragment;
    private HomeFragment homeFragment;

    private int tabCount = 6;
    private List<String> tabs;
    String [] titles={"s1","s2","s3","s4","s5","s6"};


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_browse, container, false);

        view = inflater.inflate(R.layout.fragment_browse, container, false);


        tabLayout= (TabLayout) view.findViewById(R.id.tabLayout);

        tabLayout.addTab(tabLayout.newTab().setText("Category"));

        tabLayout.addTab(tabLayout.newTab().setText("Accredation"));

        tabLayout.addTab(tabLayout.newTab().setText("Rating"));
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewPager);

        BrowsePageAdapter adapter = new BrowsePageAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager( viewPager );



        return view;
    }
}
