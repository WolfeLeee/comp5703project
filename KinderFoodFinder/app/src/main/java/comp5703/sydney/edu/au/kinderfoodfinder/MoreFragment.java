package comp5703.sydney.edu.au.kinderfoodfinder;

import android.content.Intent;
import android.os.Bundle;
import android.se.omapi.Session;
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
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.io.File;
import java.util.Arrays;

import static com.facebook.FacebookSdk.getApplicationContext;

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
    private Fragment fragmentreportaddress;
    private Fragment fragmentaccount;

//    private LoginButton fb_loginButton;
//    private CallbackManager callbackManager;

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
        fragmentreportaddress = new ReportAddressFragment();
        fragmentaccount = new AccountFragment();

        rating = view.findViewById(R.id.tv_rating);
        report = view.findViewById(R.id.tv_report);
        signout = view.findViewById(R.id.tv_signout);
        aboutus = view.findViewById(R.id.tv_aboutus);
        faqs = view.findViewById(R.id.tv_faqs);
        share = view.findViewById(R.id.tv_share);
        account = view.findViewById(R.id.tv_Account);
        glossary = view.findViewById(R.id.tv_glossary);

        rating.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.fragment_container, fragmentOurRating).addToBackStack( null ).commit();
            }
        });
        report.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Bundle bundle=new Bundle(  );
                bundle.putInt( "key",2 );
                fragmentreport.setArguments(bundle);
                fragmentreportaddress.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.fragment_container, fragmentreport)
                        .addToBackStack( null ).commit();
            }
        });
        glossary.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.fragment_container, fragmentglossary).addToBackStack( null ).commit();
            }
        });
        aboutus.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.fragment_container, fragmentaboutus).addToBackStack( null ).commit();
            }
        });

        faqs.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.fragment_container, fragmentfaqs).addToBackStack( null ).commit();
            }
        });

        signout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // login and register fragment are not in the same activity so need to go to start up activity
                deletefile();

                Intent intent = new Intent(getActivity(), StartUpActivity.class);
                startActivity(intent);
                getActivity().finish();
                LoginManager.getInstance().logOut();


            }
        });
        account.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                        .replace(R.id.fragment_container, fragmentaccount).commit();

            }
        });
        return view;
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
