package comp5703.sydney.edu.au.kinderfoodfinder;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutUsFragment extends Fragment
{
    // defined variables
    private TextView aim,aimcontent,who,whocontent;
    private Fragment fragmentMore;

    private Toolbar toolbar;
    private BottomNavigationView navigation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_aboutus, container, false);

        // disable navigation bar at the bottom
        navigation = (BottomNavigationView) getActivity().findViewById(R.id.navigation);
        navigation.setVisibility(View.GONE);

        // set up
        aim =  view.findViewById(R.id.tv_aim);
        aimcontent =  view.findViewById(R.id.tv_aim_content);
        who =  view.findViewById(R.id.tv_who);
        whocontent =  view.findViewById(R.id.tv_who_content);
        fragmentMore = new MoreFragment();

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);

        // tool bar listener
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // go to login fragment
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.fragment_container, fragmentMore).addToBackStack( null ).commit();

                // remove toolbar again
                toolbar.setVisibility(View.GONE);

                // enable navigation bar again
                navigation.setVisibility(View.VISIBLE);
            }
        });

        return view;

    }
}
