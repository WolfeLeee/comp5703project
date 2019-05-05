package comp5703.sydney.edu.au.kinderfoodfinder;

import android.app.ProgressDialog;
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
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import comp5703.sydney.edu.au.kinderfoodfinder.UserInfomation.UserDBHelper;

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


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // set up
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
                        .replace(R.id.fragment_container, fragmentRegister).commit();
            }
        });

//                SQLiteDatabase database= contactDbHelper.getWritableDatabase();
//                contactDbHelper.addContact( Integer.parseInt( id ),name,email,database );
//                contactDbHelper.close();


        UserDBHelper userDBHelper=new UserDBHelper( getActivity() );
        SQLiteDatabase database=userDBHelper.getReadableDatabase();



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
    private void loginUser(String email, String password) {
        // show the progress dialog until the login validation is complete
        loginProgressDialog.setMessage("Login...");
        loginProgressDialog.show();

        // only for developer to test the app
        if (email.equals("test") && password.equals("test")) {
            loginProgressDialog.dismiss();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
            return;
        }

        // set up
        String ipAddress = "10.16.81.139";  //100.101.72.250 Here should be changed to your server IP
        String url = "http://" + ipAddress + ":3000/android-app-login?email=" + email + "&password=" + password;

        // send the request to the server for checking user login info
        RequestQueue ExampleRequestQueue = Volley.newRequestQueue(getActivity());
        StringRequest ExampleStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                //You can test it by printing response.substring(0,500) to the screen.

                String[] rep=response.split( "," );
                loginProgressDialog.dismiss();
                if (rep[0].equals("Yes")) {
                    Toast.makeText(getActivity(), "Login Successfully!", Toast.LENGTH_SHORT).show();
                    Log.d("Send query response:", response);
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    Toast.makeText(getActivity(), "Wrong email or password!", Toast.LENGTH_SHORT).show();
                    Log.d("Send query response:", rep[0]);
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

                try {
                    String first_name = object.getString( "first_name" );
                    String last_name = object.getString( "last_name" );
//                    String email=object.getString( "email" );
                    String id = object.getString( "id" );
                    String image_url = "https://graph.facebook.com/" + id + "/picture?type=normal";

                    String name = first_name + " " + last_name;
                    String fb_id = id;
                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.dontAnimate();

//                    loginFBUser( name,id );

                    loginwithFBUser( name,id );

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



    private void loginwithFBUser(final String name,final String password) {

        // set up
        String ipAddress = "10.16.81.139";  //100.101.72.250 Here should be changed to your server IP
        String url = "http://" + ipAddress + ":3000/android-app-login-register-fb?name="+name+"&facebookId=" + password;

        // send the request to the server for checking user login info
        RequestQueue ExampleRequestQueue = (RequestQueue) Volley.newRequestQueue(getActivity());
        StringRequest ExampleStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response.equals("Yes")||response.equals( "Create" )) {
                    Toast.makeText(getActivity(), "Login Successfully!", Toast.LENGTH_SHORT).show();
                    Log.d("Send query response:", response);


                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    Toast.makeText(getActivity(), "Login Failed!", Toast.LENGTH_SHORT).show();
                    Log.d("Send query response:", response);

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
                    }
                });
        ExampleRequestQueue.add(ExampleStringRequest);
    }

}