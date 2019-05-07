package comp5703.sydney.edu.au.kinderfoodfinder;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import comp5703.sydney.edu.au.kinderfoodfinder.Adapter.BrandAdapter;
import comp5703.sydney.edu.au.kinderfoodfinder.LocalDatabase.Database;

public class ReportFragment extends Fragment


{
    // defined variables
    TextView tv_cate;
    Button button_egg, button_chicken, button_pork;
    private Toolbar toolbar;
    private BottomNavigationView navigation;
    Fragment fragmentMore, fragmentBrand_eggs, fragmentBrand_Chicken, fragmentBrand_Pork,fragmentLocation;
    int key;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        key = getArguments().getInt( "key" );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_report, container, false);

        button_egg = view.findViewById(R.id.button_egg);
        button_chicken = view.findViewById(R.id.button_chicken);
        button_pork = view.findViewById(R.id.button_pork);
        tv_cate = view.findViewById(R.id.tv_cate);

        fragmentMore = new MoreFragment();
        fragmentBrand_eggs = new BrandByEggsFragment();
        fragmentBrand_Chicken = new BrandByChickenFragment();
        fragmentBrand_Pork = new BrandByPorkFragment();
        fragmentLocation=new LocateFragment();


        button_egg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Bundle bundle=new Bundle(  );
                bundle.putInt( "key", key );
                fragmentBrand_eggs.setArguments( bundle );
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .add(R.id.fragment_container, fragmentBrand_eggs)
                        .addToBackStack(null).commit();

                // remove toolbar again
                toolbar.setVisibility(View.GONE);

            }
        });

        button_chicken.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Bundle bundle=new Bundle(  );
                bundle.putInt( "key", key );
                fragmentBrand_Chicken.setArguments( bundle );
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .add(R.id.fragment_container, fragmentBrand_Chicken)
                        .addToBackStack(null).commit();

            }
        });

        button_pork.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Bundle bundle=new Bundle(  );
                bundle.putInt( "key", key );
                fragmentBrand_Pork.setArguments( bundle );
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .add(R.id.fragment_container, fragmentBrand_Pork)
                        .addToBackStack(null).commit();

            }
        });

        // disable navigation bar at the bottom
        navigation = (BottomNavigationView) getActivity().findViewById(R.id.navigation);
        navigation.setVisibility(View.GONE);

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);

        // tool bar listener
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to login fragment
//                if(key==1){
//                    getActivity().getSupportFragmentManager().beginTransaction()
//                            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
//                            .replace(R.id.fragment_container,fragmentLocation ).commit();
//
//                }else if(key==2){
//                    getActivity().getSupportFragmentManager().beginTransaction()
//                            .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
//                            .replace(R.id.fragment_container, fragmentMore).commit();
//
//                }
                getActivity().getSupportFragmentManager().popBackStack();

                // remove toolbar again
                toolbar.setVisibility(View.GONE);

                // enable navigation bar again
                navigation.setVisibility(View.VISIBLE);
            }
        });





        return view;
    }


}
