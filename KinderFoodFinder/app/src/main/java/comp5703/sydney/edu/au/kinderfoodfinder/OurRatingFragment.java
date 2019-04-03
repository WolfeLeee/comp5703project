package comp5703.sydney.edu.au.kinderfoodfinder;

import android.media.Rating;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OurRatingFragment extends Fragment implements View.OnClickListener{
    // defined variables

    private Fragment fragmentMore;
    private RelativeLayout rating_best, rating_good, rating_avoid;
    private RatingBestFragment fragmentBest;
    private RatingGoodFragment fragmentGood;
    private RatingAvoidFragment fragmentAvoid;
    private Toolbar toolbar;
    private BottomNavigationView navigation;
    private TextView rating_tip_best, rating_tip_good, rating_tip_avoid;
    FragmentManager fm;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_our_rating, container, false);
        rating_best = view.findViewById(R.id.rating_best);
        rating_good = view.findViewById(R.id.rating_good);
        rating_avoid = view.findViewById(R.id.rating_avoid);

        rating_tip_best = view.findViewById(R.id.rating_tip_best);
        rating_tip_good = view.findViewById(R.id.rating_tip_good);
        rating_tip_avoid = view.findViewById(R.id.rating_tip_avoid);

        rating_best.setOnClickListener(this);
        rating_good.setOnClickListener(this);
        rating_avoid.setOnClickListener(this);
        fm = getChildFragmentManager();

        //Load the first Best page when click into OurRating Function
        setTabSelection(0);
        rating_good.setBackgroundResource(R.color.green); // hide background
        rating_tip_good.setVisibility(View.GONE); //hide tip
        rating_avoid.setBackgroundResource(R.color.green);
        rating_tip_avoid.setVisibility(View.GONE);




        // disable navigation bar at the bottom
        navigation = (BottomNavigationView) getActivity().findViewById(R.id.navigation);
        navigation.setVisibility(View.GONE);

        // set up
        fragmentMore = new MoreFragment();

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);

        // tool bar listener
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    public void onClick(View view){
        switch(view.getId()){
            case R.id.rating_best:
                setTabSelection(0);
                // Show Best Page
                rating_best.setBackgroundResource(R.drawable.button_rating);
                rating_tip_best.setVisibility(View.VISIBLE);
                // Hide Good and Avoid Page
                rating_good.setBackgroundResource(R.color.green);
                rating_tip_good.setVisibility(View.GONE);
                rating_avoid.setBackgroundResource(R.color.green);
                rating_tip_avoid.setVisibility(View.GONE);
                break;
            case R.id.rating_good:
                setTabSelection(1);
                rating_good.setBackgroundResource(R.drawable.button_rating);
                rating_tip_good.setVisibility(View.VISIBLE);
                rating_best.setBackgroundResource(R.color.green);
                rating_tip_best.setVisibility(View.GONE);
                rating_avoid.setBackgroundResource(R.color.green);
                rating_tip_avoid.setVisibility(View.GONE);
                break;
            case R.id.rating_avoid:
                setTabSelection(2);
                rating_avoid.setBackgroundResource(R.drawable.button_rating);
                rating_tip_avoid.setVisibility(View.VISIBLE);
                rating_best.setBackgroundResource(R.color.green);
                rating_tip_best.setVisibility(View.GONE);
                rating_good.setBackgroundResource(R.color.green);
                rating_tip_good.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    private void setTabSelection(int index){
        FragmentTransaction ft = fm.beginTransaction();
        hideFragment(ft);
        switch(index){
            case 0:
                if(fragmentBest == null){
                    fragmentBest = new RatingBestFragment();
                    ft.add(R.id.fl, fragmentBest);
                }else{
                    ft.show(fragmentBest);
                }
                break;
            case 1:
                if(fragmentGood == null){
                    fragmentGood = new RatingGoodFragment();
                    ft.add(R.id.fl, fragmentGood);
                }
                ft.show(fragmentGood);
                break;
            case 2:
                if(fragmentAvoid == null){
                    fragmentAvoid = new RatingAvoidFragment();
                    ft.add(R.id.fl, fragmentAvoid);
                }
                ft.show(fragmentAvoid);
                break;
        }
        ft.commit();

    }

    private void hideFragment(FragmentTransaction ft) {
        if(fragmentBest != null){
            ft.hide(fragmentBest);
        }
        if(fragmentGood != null){
            ft.hide(fragmentGood);
        }
        if(fragmentAvoid != null){
            ft.hide(fragmentAvoid);
        }
    }
}



