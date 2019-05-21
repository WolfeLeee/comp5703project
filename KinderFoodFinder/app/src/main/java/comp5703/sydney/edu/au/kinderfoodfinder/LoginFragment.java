package comp5703.sydney.edu.au.kinderfoodfinder;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.bumptech.glide.request.RequestOptions;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;

import comp5703.sydney.edu.au.kinderfoodfinder.StatisticDatabase.StatisticContract;
import comp5703.sydney.edu.au.kinderfoodfinder.UserInfomation.UserDBHelper;

import static com.facebook.FacebookSdk.getApplicationContext;

public class LoginFragment extends Fragment {
    // defined variables
    private Fragment fragmentRegister;
    private Button btnLogin,btnFbLogin;
    private TextView textRegister;
    private EditText inputEmail, inputPwd;

    private ProgressDialog loginProgressDialog;

    private LoginButton fb_loginButton;
    private CallbackManager callbackManager;
    public final int Login_REQUEST_CODE = 647;
    private Fragment fragmentFBRegister;
    private boolean fblogin=true;
    String brand_version, store_version,brand_update,store_update;

    String IP_ADDRESS = "10.16.82.52";

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        brand_version=getArguments().getString( "brand_version" );
//        brand_update=getArguments().getString( "brand_update" );
//        store_version=getArguments().getString( "store_version" );
//        store_update=getArguments().getString( "store_update" );
//    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // set up
        LoginManager.getInstance().logOut();

        fragmentRegister = new RegisterFragment();
        loginProgressDialog = new ProgressDialog(getActivity());

        btnLogin = (Button) view.findViewById(R.id.btnLogin);
        textRegister = (TextView) view.findViewById(R.id.textRegister);
        inputEmail = (EditText) view.findViewById(R.id.inputEmail);
        inputPwd = (EditText) view.findViewById(R.id.inputPwd);

        fb_loginButton=view.findViewById( R.id.btn_fblogin );

        // button on click listeners
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // set up
                String email = inputEmail.getText().toString().trim();
                String pwd = inputPwd.getText().toString().trim();

                // login function
                loginUser(email, pwd);
            }
        });

        // text view on click listener
        textRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go to register fragment
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.fragment_container, fragmentRegister).addToBackStack( null ).commit();
            }
        });

//                SQLiteDatabase database= contactDbHelper.getWritableDatabase();
//                contactDbHelper.addContact( Integer.parseInt( id ),name,email,database );
//                contactDbHelper.close();


        UserDBHelper userDBHelper=new UserDBHelper( getActivity() );
        SQLiteDatabase database=userDBHelper.getReadableDatabase();

        Intent intent =getActivity().getIntent();
         brand_version=intent.getStringExtra( "brand_version" );
         brand_update=intent.getStringExtra( "brand_update" );
        final String status=intent.getStringExtra( "status" );
        store_update="no";
        store_version="1";

        Log.d("version data",brand_version+"; "+brand_update+", "+store_version+" "+store_update);


        fragmentFBRegister=new FBRegisterFragment();


        //Login with Facebook
        fb_loginButton=view.findViewById( R.id.btn_fblogin );
        callbackManager= CallbackManager.Factory.create();
        fb_loginButton.setReadPermissions( Arrays.asList("email","public_profile") );
        fb_loginButton.setFragment( this );
        fb_loginButton.registerCallback( callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                loadUserProfile( loginResult.getAccessToken() );
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        } );






        return view;
    }

    /* * * * * * * * * *
     * Login Functions *
     * * * * * * * * * */
    private void loginUser(final String email, String password) {
        // show the progress dialog until the login validation is complete
        loginProgressDialog.setMessage("Login...");
        loginProgressDialog.show();
        // only for developer to test the app
        if (email.equals("test") && password.equals("test")) {
            loginProgressDialog.dismiss();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            deletefile();
            writeToFile( "1;"+"Male,"+"7-5-2009,null,test,test");
            startActivity(intent);
            getActivity().finish();
            return;
        }
        checkProfileFile();
        // set up
        String ipAddress = "10.16.206.194";  //100.101.72.250 Here should be changed to your server IP
        String url = "http://" + StatisticContract.StatisticEntry.IP_Address + ":3000/android-app-login?email=" + email + "&password=" + password;
        // send the request to the server for checking user login info
        RequestQueue ExampleRequestQueue = Volley.newRequestQueue(getActivity());
        StringRequest ExampleStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                String[] rep=response.split( "," ,2);
                loginProgressDialog.dismiss();
                // if the email and password are correct, it will turns to MainActivity.
                if (rep[0].equals("Yes")) {
                    Toast.makeText(getActivity(), "Login Successfully!", Toast.LENGTH_SHORT).show();
                    Log.d("Send query response:", response);
                    // rewrite profile.txt which contains id, gender and birthday.
                    if(rep.length==2){
                        Log.d("Send profilefile:", rep[1]);
                        deletefile();
                        writeToFile( "1;"+rep[1] );
                    }
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }// login failed,
                else {
                    Toast.makeText(getActivity(), "Wrong email or password! Please try it again", Toast.LENGTH_SHORT).show();
                    Log.d("Send query response:", rep[0]);
                }
            }
        },// Not connect to the server.
                new Response.ErrorListener()  //Create an error listener to handle errors appropriately.
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //This code is executed if there is an error.
                        loginProgressDialog.dismiss();
                        Toast.makeText(getActivity(), "Login Failed!", Toast.LENGTH_SHORT).show();
                        Log.d("Send query error:", error.toString());
                    }
                });
        ExampleRequestQueue.add(ExampleStringRequest);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult( requestCode,resultCode,data );
        super.onActivityResult( requestCode, resultCode, data );
    }

    AccessTokenTracker tokenTracker=new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

            if(currentAccessToken==null){

            }
            else {
//                loadUserProfile( currentAccessToken );

            }

        }
    };


    private void loadUserProfile(AccessToken newAcceseToken) {
        GraphRequest request = GraphRequest.newMeRequest( newAcceseToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                String email="";
                String name="";
                String id="";
                    Log.d( "facebook",response.toString());
                    try {
                        String first_name = object.getString( "first_name" );
                        String last_name = object.getString( "last_name" );
                        id = object.getString( "id" );
                        String image_url = "https://graph.facebook.com/" + id + "/picture?type=normal";
                        name = first_name + " " + last_name;
                        String fb_id = id;
//                        RequestOptions requestOptions = new RequestOptions();
//                        requestOptions.dontAnimate();

                        Log.d( "facebook",name+" "+email +"; "+id);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try{
                        email=object.getString( "email" );

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                loginwithFBUser( name,id ,email);
                Log.d( "facebook",name+"; "+email +"; "+id);


            }
        } );

        Bundle parameters = new Bundle();
        parameters.putString( "fields", "first_name,last_name,id,email" );
        request.setParameters( parameters );
        request.executeAsync();
    }



    private void loginwithFBUser(final String name, final String password, final String email) {

        // set up
        String ipAddress = "10.16.206.194";  //100.101.72.250 Here should be changed to your server IP
        String url = "http://" + StatisticContract.StatisticEntry.IP_Address + ":3000/android-app-login-register-fb?facebookId=" + password;

        // send the request to the server for checking user login info
        RequestQueue ExampleRequestQueue = (RequestQueue) Volley.newRequestQueue(getActivity());
        StringRequest ExampleStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                String[] rep=response.split( "," ,2);
                if(rep.length==2){
                    Log.d("Send query response:", rep[1]);
                    deletefile();
                    writeToFile( "1;"+rep[1] );
                }
                deletefile();
                writeToFile( "1;"+"Male,"+"7-5-2009,null,test,test");
                if (response.equals("Yes")) {
                    Toast.makeText(getActivity(), "Login Successfully!", Toast.LENGTH_SHORT).show();
                    Log.d("Send query response:", response);
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();

                } else {
                    Toast.makeText(getActivity(), "Login Failed!", Toast.LENGTH_SHORT).show();
                    Log.d("Send query response:", response);

                    Bundle bundle=new Bundle(  );
                    bundle.putString( "fb_name",name );
                    bundle.putString( "fb_id",password );
                    bundle.putString( "fb_email",email );
                    fragmentFBRegister.setArguments( bundle );
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, fragmentFBRegister).commit();
                    LoginManager.getInstance().logOut();
                }
            }
        },
                new Response.ErrorListener()  //Create an error listener to handle errors appropriately.
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //This code is executed if there is an error.
                        loginProgressDialog.dismiss();
                        Toast.makeText(getActivity(), "Login Failed!", Toast.LENGTH_SHORT).show();
                        Log.d("Send query error:", error.toString());
                        LoginManager.getInstance().logOut();
                    }
                });
        ExampleRequestQueue.add(ExampleStringRequest);
    }
    // if profile.txt not exit, create it.
    public void checkProfileFile(){
        File fileVersion = new File(getApplicationContext().getFilesDir(), "profile.txt");
        if(!(fileVersion.exists())) {
            writeToFile( "0;1" );
            Log.d( "profile", "Creating!" );
        }else
            Log.d("profile", "Existing!");
    }

    // write user information in the profile.txt
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
    // delete previous user information
    public void deletefile() {
        try {
            //
            File file = new File(getApplicationContext().getFilesDir(), "profile.txt");
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String readFromFile()
    {
        String ret = "";
        try
        {
            InputStream inputStream = getActivity().openFileInput("profile.txt");
            if (inputStream != null )
            {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null )
                {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();

            }
        }
        catch (FileNotFoundException e)
        {
            Log.e("login activity", "File not found: " + e.toString());
        }
        catch (IOException e)
        {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        return ret;
    }

    @Override
    public void onStart() {
        super.onStart();
        LoginManager.getInstance().logOut();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


}