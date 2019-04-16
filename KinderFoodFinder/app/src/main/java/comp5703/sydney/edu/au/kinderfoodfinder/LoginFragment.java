package comp5703.sydney.edu.au.kinderfoodfinder;

import android.content.Intent;
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

import com.bumptech.glide.Glide;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class LoginFragment extends Fragment
{
    // defined variables
    private Fragment fragmentRegister;
    private Button btnLogin;
    private TextView textRegister;
    private EditText inputEmail, inputPwd;


    private LoginButton loginButton;
    private CircleImageView circleImageView;
    private TextView textName, textEmail;
    private CallbackManager callbackManager;
    private Fragment moreFragment;
    String name=new String(  );
    String fb_id =new String(  );
    public final int VIEW_ITEM_REQUEST_CODE = 647;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // set up
        fragmentRegister = new RegisterFragment();
        btnLogin = (Button) view.findViewById(R.id.btnLogin);
//        textRegister = (TextView) view.findViewById(R.id.textRegister);
        inputEmail = (EditText) view.findViewById(R.id.input_Email);
//        inputPwd = (EditText) view.findViewById(R.id.inputPwd);
        textRegister=view.findViewById( R.id.signup );
        inputPwd=view.findViewById( R.id.password );





        // button on click listeners
        btnLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // only for testing *****
                String email = inputEmail.getText().toString().trim();
                String pwd = inputPwd.getText().toString().trim();
                if(email.equals("test") && pwd.equals("test"))
                {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }

            }
        });

        // text view on click listener
        textRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // go to register fragment
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.fragment_container, fragmentRegister).commit();
            }
        });





        loginButton=view.findViewById( R.id.btn_fblogin );
//        circleImageView=view.findViewById( R.id.circleimageview );



        callbackManager=CallbackManager.Factory.create();
        loginButton.setReadPermissions( Arrays.asList("email","public_profile") );
        loginButton.setFragment( this );
        loginButton.registerCallback( callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();

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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult( requestCode,resultCode,data );
        super.onActivityResult( requestCode, resultCode, data );
    }

    AccessTokenTracker tokenTracker=new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

            if(currentAccessToken==null){
//                textEmail.setText( "" );
//                textName.setText( "" );
//                circleImageView.setImageResource( 0 );
//                Toast.makeText( getActivity() , "User log out ", Toast.LENGTH_SHORT ).show();

            }
            else {
                loadUserProfile( currentAccessToken );

//
            }

        }
    };

    private void loadUserProfile(AccessToken newAcceseToken){
        GraphRequest request= GraphRequest.newMeRequest( newAcceseToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {

                try {
                    String first_name=object.getString( "first_name" );
                    String last_name=object.getString( "last_name" );
//                    String email=object.getString( "email" );
                    String id =object.getString( "id" );

                    String image_url="https://graph.facebook.com/"+id+"/picture?type=normal";
//                    textEmail=getActivity().findViewById( R.id.text_email );
//                    textName=getActivity().findViewById( R.id.text_name );
//                    textEmail.setText( email );
//                    textName.setText( first_name+" "+last_name );
                    Log.d("facebook login",id+"   "+first_name+" "+last_name);

                    name=first_name+" "+last_name;
                    fb_id=id;


//                    moreFragment=new MoreFragment();
//                    Bundle args= new Bundle(  );
//
//                    args.putString( "name",first_name+" "+last_name );
//                    args.putString( "id", id );
////                args.putStringArrayList( "list", finalList );
//                    moreFragment.setArguments( args );
                    RequestOptions requestOptions=new RequestOptions();
                    requestOptions.dontAnimate();

                    Intent intent = new Intent(getActivity(), MainActivity.class);


                    if (intent != null) {

                        intent.putExtra("fb_name", name);
                        intent.putExtra("fb_id", fb_id);

                        Log.d( "ttttttt",name+"  "+fb_id );

//                    intent.putExtra("img", String.valueOf(c.getImg()));
                        startActivityForResult(intent, VIEW_ITEM_REQUEST_CODE);

                    }
                    startActivity(intent);
                    getActivity().finish();


//                    Glide.with(getActivity()).load(image_url).into( circleImageView );
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        } );

        Bundle parameters=new Bundle(  );
        parameters.putString( "fields","first_name,last_name,id" );
        request.setParameters( parameters );
        request.executeAsync();

    }
}
