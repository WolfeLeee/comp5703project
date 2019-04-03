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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ReportFragment extends Fragment
{
    // defined variables
    private TextView tv_product, tv_brand, tv_store, tv_location, tv_Email;
    private EditText input_product, input_brand, input_store, input_location, input_Email;
    private Button btn_submit;
    private Toolbar toolbar;
    private BottomNavigationView navigation;
    private Fragment fragmentMore;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_report, container, false);

        tv_product = view.findViewById(R.id.tv_product);
        tv_brand = view.findViewById(R.id.tv_brand);
        tv_store = view.findViewById(R.id.tv_store);
        tv_location = view.findViewById(R.id.tv_location);
        tv_Email = view.findViewById(R.id.tv_Email);

        input_product = view.findViewById(R.id.input_product);
        input_brand = view.findViewById(R.id.input_brand);
        input_store = view.findViewById(R.id.input_store);
        input_location = view.findViewById(R.id.input_location);
        input_Email = view.findViewById(R.id.input_Email);

        btn_submit = view.findViewById(R.id.btn_submit);

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


}
