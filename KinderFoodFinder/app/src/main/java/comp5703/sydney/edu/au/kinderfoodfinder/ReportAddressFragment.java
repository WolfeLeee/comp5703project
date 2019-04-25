package comp5703.sydney.edu.au.kinderfoodfinder;

import android.content.Intent;
import android.support.v4.app.Fragment;
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
import android.widget.Toast;


public class ReportAddressFragment extends Fragment {

    // defined variables
    TextView tv_product, tv_brand,tv_store, tv_location;
    EditText input_store, input_location;
    TextView content_product, content_brand;
    private Button btn_submit;
    private Toolbar toolbar;
    private BottomNavigationView navigation;
    private Fragment fragmentReport;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_report_address, container, false);


        tv_product = view.findViewById(R.id.tv_product);
        tv_brand = view.findViewById(R.id.tv_brand);
        tv_store = view.findViewById(R.id.tv_store);
        tv_location = view.findViewById(R.id.tv_location);

        content_brand = view.findViewById(R.id.content_brand);
        content_product = view.findViewById(R.id.content_product);

        input_store = view.findViewById(R.id.input_store);
        input_location = view.findViewById(R.id.input_location);

        btn_submit = view.findViewById(R.id.btn_submit);

        // Receive data
        String brand = getArguments().getString("BRAND_KEY");
        String category = getArguments().getString("CATEGORY_KEY");
        content_product.setText(category);
        content_brand.setText(brand);



        // disable navigation bar at the bottom
        navigation = (BottomNavigationView) getActivity().findViewById(R.id.navigation);
        navigation.setVisibility(View.GONE);

        // set up
        fragmentReport = new ReportFragment();

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);

        // tool bar listener
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to login fragment
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.fragment_container, fragmentReport).commit();

                // remove toolbar again
                toolbar.setVisibility(View.GONE);

                // enable navigation bar again
                navigation.setVisibility(View.VISIBLE);
            }
        });





        return view;
    }
}
