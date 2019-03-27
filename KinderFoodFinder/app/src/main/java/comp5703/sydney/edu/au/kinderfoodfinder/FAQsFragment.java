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

public class FAQsFragment extends Fragment
{
    // defined variables
    private TextView a1,q1;
    private TextView a2,q2;
    private TextView a3,q3;
    private TextView a4,q4;
    private TextView a5,q5;
    private TextView a6,q6;
    private TextView a7,q7;
    private TextView a8,q8;
    private TextView a9,q9;
    private TextView back;

    private Fragment fragmentMore;

    private Toolbar toolbar;
    private BottomNavigationView navigation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_faqs, container, false);

        // disable navigation bar at the bottom
        navigation = (BottomNavigationView) getActivity().findViewById(R.id.navigation);
        navigation.setVisibility(View.GONE);

        // set up
        a1 =  view.findViewById(R.id.a1);
        q1 =  view.findViewById(R.id.q1);
        a2 =  view.findViewById(R.id.a2);
        q2 =  view.findViewById(R.id.q2);
        a3 =  view.findViewById(R.id.a3);
        q3 =  view.findViewById(R.id.q3);
        a4 =  view.findViewById(R.id.a4);
        q4 =  view.findViewById(R.id.q4);
        a5 =  view.findViewById(R.id.a5);
        q5 =  view.findViewById(R.id.q5);
        a6 =  view.findViewById(R.id.a6);
        q6 =  view.findViewById(R.id.q6);
        a7 =  view.findViewById(R.id.a7);
        q7 =  view.findViewById(R.id.q7);
        a8 =  view.findViewById(R.id.a8);
        q8 =  view.findViewById(R.id.q8);
        a9 =  view.findViewById(R.id.a9);
        q9 =  view.findViewById(R.id.q9);

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
                        .replace(R.id.fragment_container, fragmentMore).commit();

                // remove toolbar again
                toolbar.setVisibility(View.GONE);

                // enable navigation bar again
                navigation.setVisibility(View.VISIBLE);
            }
        });

        return view;

    }
}
