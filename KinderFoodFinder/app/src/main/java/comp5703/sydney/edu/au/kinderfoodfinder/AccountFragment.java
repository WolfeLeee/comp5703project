package comp5703.sydney.edu.au.kinderfoodfinder;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import comp5703.sydney.edu.au.kinderfoodfinder.UserInfomation.UserDBHelper;

public class AccountFragment extends Fragment {

    private TextView tv_username, tv_birthday, tv_gender;
    private ImageView userlogo;
    private Toolbar toolbar;
    private BottomNavigationView navigation;
    private Fragment fragmentMore;
    UserDBHelper userDBHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_account, container, false);

        tv_username = view.findViewById(R.id.tv_UserName);
        tv_birthday = view.findViewById(R.id.tv_birthday);
        tv_gender = view.findViewById(R.id.tv_gender);

        userlogo = view.findViewById(R.id.account);
        fragmentMore = new MoreFragment();


        String profile=readFromFile().split( ";" )[1];
        String[] result=profile.split( "," );

        if(result.length==5){
            tv_birthday.setText( result[0] );
            tv_gender.setText( result[1] );

            tv_username.setText( result[3] );
        }

//        String id = userDBHelper.getContats("user_db", "")
//        String gender = getArguments().getString("gender");
//        String birthday = getArguments().getString("birthday");

//        tv_username.setText(id);
//        tv_gender.setText(gender);
//        tv_birthday.setText(birthday);

        // disable navigation bar at the bottom
        navigation = (BottomNavigationView) getActivity().findViewById(R.id.navigation);
        navigation.setVisibility(View.GONE);

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.fragment_container,fragmentMore ).commit();
                // remove toolbar again

                toolbar.setVisibility(View.GONE);

                // enable navigation bar again
                navigation.setVisibility(View.VISIBLE);

            }
        });

        return view;


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
}
