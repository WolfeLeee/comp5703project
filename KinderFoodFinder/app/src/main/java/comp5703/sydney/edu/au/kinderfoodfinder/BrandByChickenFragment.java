package comp5703.sydney.edu.au.kinderfoodfinder;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import comp5703.sydney.edu.au.kinderfoodfinder.Adapter.BrandAdapter;
import comp5703.sydney.edu.au.kinderfoodfinder.LocalDatabase.Database;

public class BrandByChickenFragment extends Fragment {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    BrandAdapter adapter;
    private Database database;
    private Toolbar toolbar;
    private BottomNavigationView navigation;
    Fragment fragmentReport, fragmentReport_address;
    int key;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        key = getArguments().getInt( "key" );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_brand_chicken, container, false);

        // Init view
        recyclerView = view.findViewById(R.id.recycler_chicken);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        fragmentReport = new ReportFragment();
        fragmentReport_address = new ReportAddressFragment();

        // Init DB
        database = new Database(getActivity());

        adapter = new BrandAdapter(getActivity(),database.getBrandByChicken(), key);
        recyclerView.setAdapter(adapter);



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
                Bundle bundle=new Bundle(  );
                bundle.putInt( "key", key );
                fragmentReport.setArguments( bundle );
//                getActivity().getSupportFragmentManager().beginTransaction()
//                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
//                        .replace(R.id.fragment_container, fragmentReport).commit();
                getActivity().getSupportFragmentManager().popBackStack();
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
