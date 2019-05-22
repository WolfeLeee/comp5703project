package comp5703.sydney.edu.au.kinderfoodfinder;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;

import comp5703.sydney.edu.au.kinderfoodfinder.StatisticDatabase.StatisticContract;

import static com.facebook.FacebookSdk.getApplicationContext;

public class FBRegisterFragment extends Fragment {
    private Toolbar toolbar;
    private Fragment fragmentLogin;
    private String name,id,email;

    private Button btnRegister;
    private EditText  inputBirthday,inputName, inputEmail;
    private CheckBox checkAgreement, checkIfDiscloseDOB,checkIfDiscloseGender;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private ImageView datePicker;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private ProgressDialog registerProgressDialog;

    String IP_ADDRESS = "10.16.82.52";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        name = getArguments().getString( "fb_name" );

        email=getArguments().getString( "fb_email" );
        id = getArguments().getString( "fb_id" );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate( R.layout.fragment_fbregister, container, false );

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);
//        name=getArguments().getString( "name" );
//        id=getArguments().getString( "id" );


        registerProgressDialog = new ProgressDialog(getActivity());




        radioGroup = (RadioGroup) view.findViewById(R.id.radioGender);

        inputBirthday = (EditText) view.findViewById(R.id.inputBirthday);
        datePicker = (ImageView) view.findViewById(R.id.datePicker);

        checkIfDiscloseDOB = (CheckBox) view.findViewById(R.id.checkIfDiscloseDOB);
        checkAgreement = (CheckBox) view.findViewById(R.id.checkAgreement);

        inputEmail=view.findViewById( R.id.inputEmailR );

        btnRegister = (Button) view.findViewById(R.id.btnRegister);

        fragmentLogin=new LoginFragment();

        if(email.length()>3){
            inputEmail.setText( email );
            inputEmail.setVisibility( View.GONE );
        }
        // tool bar listener
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // go to login fragment
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.fragment_container, fragmentLogin).addToBackStack( null ).commit();
                LoginManager.getInstance().logOut();




                // remove toolbar again
                toolbar.setVisibility(View.GONE);
            }
        });


        // image view of date picker click listener
        datePicker.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(getActivity(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable( Color.TRANSPARENT));
                dialog.show();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
            {
                month=month+1;
                String date = dayOfMonth + "/" + month + "/" + year;
                inputBirthday.setText(date);
            }
        };

//
        // button on click listeners
        btnRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // set up all the inputs
                int selectedId = radioGroup.getCheckedRadioButtonId();
                radioButton = (RadioButton) radioGroup.findViewById(selectedId);

                String gender = radioButton.getText().toString();
                String birthday = inputBirthday.getText().toString();
                boolean showBirthday = checkIfDiscloseDOB.isChecked();
                email=inputEmail.getText().toString();
                if(checkAgreement.isChecked())
                    registerUser(name, gender, id, birthday, showBirthday,email);
                else
                    Toast.makeText(getActivity(), "Sorry, you have to agree before register!", Toast.LENGTH_SHORT).show();
            }
        });



        Log.d("facebook",name+"   "+id );

        return view;
    }


    /* * * * * * * * * * * *
     * Register Functions  *
     * * * * * * * * * * * */


    AccessTokenTracker tokenTracker=new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

            if(currentAccessToken==null){

            }
            else {


//
            }

        }
    };

    private void loadUserProfile(AccessToken newAcceseToken) {
        GraphRequest request = GraphRequest.newMeRequest( newAcceseToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                try {
                    String first_name = object.getString( "first_name" );
                    String last_name = object.getString( "last_name" );
//                    String email=object.getString( "email" );
                    String id = object.getString( "id" );
                    String image_url = "https://graph.facebook.com/" + id + "/picture?type=normal";

                    String name = first_name + " " + last_name;
                    String fb_id = id;
//                    inputName.setText( name );
//                    inputEmail.setText( id );
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.dontAnimate();




                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        } );

        Bundle parameters = new Bundle();
        parameters.putString( "fields", "first_name,last_name,id" );
        request.setParameters( parameters );
        request.executeAsync();
    }

    private void registerUser(String name, final String gender, final String id, final String birthday, boolean showBirthday,String email)
    {

        if(TextUtils.isEmpty(birthday))
        {
            Toast.makeText(getActivity(), "Please select date for your birthday!", Toast.LENGTH_SHORT).show();
            return;
        }

        // if all validations above are passed, show the progress dialog
        registerProgressDialog.setMessage("Registering...");
        registerProgressDialog.show();

//        String timeStamp = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        // deal with the gender and birthday format
        String genderModified, birthdayModified;
        if(gender.equals("Not Disclose"))
            genderModified = "Not+Disclose";
        else
            genderModified = gender;
        String[] temp = birthday.split("/");
        birthdayModified = temp[0] + "-" + temp[1] + "-" + temp[2];

        // modify the user data to the server
        String url;
        String ipAddress = "10.16.206.194";  //100.101.72.250 Here should be changed to your server IP
        if(!showBirthday)
            url = "http://" + StatisticContract.StatisticEntry.IP_Address + ":3000/android-app-login-register-fb?name=" + name + "&facebookId=" + id + "&gender=" +
                    genderModified +  "&birthday=" + birthdayModified+"&email=" + email;
        else
            url = "http://" + StatisticContract.StatisticEntry.IP_Address + ":3000/android-app-login-register-fb??name=" + name + "&facebookId=" + id + "&gender=" +
                    genderModified + "&birthday=Not+Disclose"+"&email=" + email;

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

                registerProgressDialog.dismiss();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
                deletefile();
                writeToFile( "1;"+gender+birthday );
                Toast.makeText(getActivity(), "Registered Successfully!", Toast.LENGTH_SHORT).show();
                Log.d("Send query response:", response);
            }
        },
                new Response.ErrorListener()  //Create an error listener to handle errors appropriately.
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        //This code is executed if there is an error.
                        registerProgressDialog.dismiss();
                        Toast.makeText(getActivity(), "Registered Failed!", Toast.LENGTH_SHORT).show();
                        Log.d("Send query error:", error.toString());

                    }
                });
        ExampleRequestQueue.add(ExampleStringRequest);
    }

    private void writeToFile(String version)
    {
        try
        {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getActivity().openFileOutput("profile.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(version);
            outputStreamWriter.close();
        }
        catch (IOException e)
        {
            Log.e("Exception", "File write failed: " + e.toString());
        }

    }

    public void deletefile() {
        try {
            //
            File file = new File(getApplicationContext().getFilesDir(), "profile.txt");
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
