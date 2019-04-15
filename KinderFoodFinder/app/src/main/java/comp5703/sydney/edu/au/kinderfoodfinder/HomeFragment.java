package comp5703.sydney.edu.au.kinderfoodfinder;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;

public class HomeFragment extends Fragment
{
    // defined variables

    private BottomNavigationView navigation;
    private Button browse;
    private SearchView searchView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        navigation = (BottomNavigationView) getActivity().findViewById(R.id.navigation);

        browse=view.findViewById( R.id.category_home );
        searchView=view.findViewById( R.id.search_home );

        browse.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigation.setSelectedItemId( R.id.navigation_browse );
            }
        } );

        searchView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigation.setSelectedItemId( R.id.navigation_search );
            }
        } );




        return view;
    }

    public void BrowseClick(View view){

    }




}
