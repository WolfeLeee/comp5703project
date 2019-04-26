package comp5703.sydney.edu.au.kinderfoodfinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

public class MoreFragment extends Fragment
{
    // defined variables
    private TextView rating;
    private TextView account;
    private TextView report;
    private TextView aboutus;
    private TextView faqs;
    private TextView share;
    private TextView signout;
    private TextView glossary;
    private Fragment fragmentOurRating;
    private Fragment fragmentLogin;
    private Fragment fragmentaboutus;
    private Fragment fragmentfaqs;
    private Fragment fragmentreport;
    private Fragment fragmentglossary;

    private LoginButton fb_loginButton;
    private CallbackManager callbackManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_more, container, false);

        fragmentOurRating = new OurRatingFragment();
        fragmentLogin = new LoginFragment();
        fragmentaboutus = new AboutUsFragment();
        fragmentfaqs = new FAQsFragment();
        fragmentreport = new ReportFragment();
        fragmentglossary = new GlossaryFragment();

        rating = view.findViewById(R.id.tv_rating);
        report = view.findViewById(R.id.tv_report);
        signout = view.findViewById(R.id.tv_signout);
        aboutus = view.findViewById(R.id.tv_aboutus);
        faqs = view.findViewById(R.id.tv_faqs);
        share = view.findViewById(R.id.tv_share);
        account = view.findViewById(R.id.tv_Account);
        glossary = view.findViewById(R.id.tv_glossary);
        fb_loginButton=view.findViewById( R.id.btn_fblogin );

        rating.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.fragment_container, fragmentOurRating).commit();
            }
        });
        report.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.fragment_container, fragmentreport).commit();
            }
        });
        glossary.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.fragment_container, fragmentglossary).commit();
            }
        });
        aboutus.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.fragment_container, fragmentaboutus).commit();
            }
        });

        faqs.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.fragment_container, fragmentfaqs).commit();
            }
        });

        signout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // login and register fragment are not in the same activity so need to go to start up activity
                Intent intent = new Intent(getActivity(), StartUpActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });


        callbackManager= CallbackManager.Factory.create();
        fb_loginButton.setReadPermissions( Arrays.asList("email","public_profile") );
        fb_loginButton.setFragment( this );
        fb_loginButton.registerCallback( callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {


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

                Intent intent = new Intent(getActivity(), StartUpActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
            else {

            }

        }
    };

}
