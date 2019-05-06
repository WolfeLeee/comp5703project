package comp5703.sydney.edu.au.kinderfoodfinder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import comp5703.sydney.edu.au.kinderfoodfinder.Database.StoreDatabase;


public class ReportAddressFragment extends Fragment {

    // defined variables
    TextView tv_product, tv_brand,tv_store, tv_location;
    EditText input_store, input_location, input_statae, input_postcode;
    TextView content_product, content_brand;
    private Button btn_submit;
    private Toolbar toolbar;
    private BottomNavigationView navigation;
    private Fragment fragmentReport;
    private ProgressDialog reportProgressDialog;
    StoreDatabase storeDatabase;



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
        input_postcode=view.findViewById( R.id.input_postcode );
        input_statae=view.findViewById( R.id.input_state );

        btn_submit = view.findViewById(R.id.btn_submit);
        
        storeDatabase = new StoreDatabase(getActivity());

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
                toolbar.setVisibility( View.GONE );
            }
        });


        btn_submit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String storeName=input_store.getText().toString();
                String streetAddress=input_location.getText().toString();
                String state=input_statae.getText().toString();
                String postCode =input_postcode.getText().toString();
                String id ="5ccd8e9b3e36263b52a8d08f";
                String brand =content_brand.getText().toString();
                reportInfomation( storeName,streetAddress,state,postCode,id);
                AddStore(brand, storeName, streetAddress, postCode, state);

            }
        } );



        return view;
    }
    
     private void AddStore(String brandName, String storeName, String streetAddress, String postCode, String state) {
        boolean insertData = storeDatabase.addStore(brandName, storeName, streetAddress, postCode, state);
        storeDatabase.close();

        if(insertData){

            Toast.makeText(getActivity(), "Successful Entered !", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getActivity(), "Something went wrong :( ", Toast.LENGTH_SHORT).show();
        }
    }

    private void reportInfomation(String storeName, String streetAddress, String state, String postCode, String productID)
    {
        // check if the texts are empty
        if(TextUtils.isEmpty(storeName))
        {
            Toast.makeText(getActivity(), "Please enter your store name!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(streetAddress))
        {
            Toast.makeText(getActivity(), "Please enter your street address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(state))
        {
            Toast.makeText(getActivity(), "Please enter your state!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(postCode))
        {
            Toast.makeText(getActivity(), "Please enter your post code!", Toast.LENGTH_SHORT).show();
            return;
        }
        // check pwd equals to confirm pwd


//        if(TextUtils.isEmpty(productID))
//        {
////            Toast.makeText(getActivity(), "Please select date for your birthday!", Toast.LENGTH_SHORT).show();
//            return;
//        }

        // if all validations above are passed, show the progress dialog
//        reportProgressDialog.setMessage("Report...");
//        reportProgressDialog.show();

//        String timeStamp = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        // deal with the gender and birthday format


        // modify the user data to the server
        String url;
        String ipAddress = "10.16.81.139";  //100.101.72.250 Here should be changed to your server IP

            url = "http://" + ipAddress + ":3000/android-app-report-store?storeName=" + storeName + "&streetAddress=" + streetAddress + "&state=" +
                    state + "&postCode=" + postCode + "&productId=" + productID;

        // send the data to the server
        RequestQueue ExampleRequestQueue = Volley.newRequestQueue(getActivity());
        StringRequest ExampleStringRequest = new StringRequest( Request.Method.GET, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                //You can test it by printing response.substring(0,500) to the screen.
//
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.fragment_container, fragmentReport).commit();

                Toast.makeText(getActivity(), "Report Successfully!", Toast.LENGTH_SHORT).show();
                Log.d("Send query response:", response);
            }
        },
                new Response.ErrorListener()  //Create an error listener to handle errors appropriately.
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        //This code is executed if there is an error.

                        Toast.makeText(getActivity(), "Report Failed!", Toast.LENGTH_SHORT).show();
                        Log.d("Send query error:", error.toString());
                    }
                });
        ExampleRequestQueue.add(ExampleStringRequest);
    }
}
