package comp5703.sydney.edu.au.kinderfoodfinder;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class StartFragment extends Fragment {

    private Button signin,signup;
    private Fragment loginFragment,registerFragment;


    public StartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate( R.layout.fragment_start, container, false );

        signin=view.findViewById( R.id.signin );
        signup=view.findViewById( R.id.signup );
        loginFragment=new LoginFragment();
        registerFragment=new RegisterFragment();

        signup.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, registerFragment).commit();

            }
        } );

        signin.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, loginFragment).commit();

            }
        } );

        return view;
    }

}
