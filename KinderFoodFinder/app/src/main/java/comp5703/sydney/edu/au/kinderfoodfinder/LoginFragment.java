package comp5703.sydney.edu.au.kinderfoodfinder;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginFragment extends Fragment
{
    // defined variables
    private Fragment fragmentRegister;
    private Button btnLogin;
    private TextView textRegister;
    private EditText inputEmail, inputPwd;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // set up
        fragmentRegister = new RegisterFragment();
        btnLogin = (Button) view.findViewById(R.id.btnLogin);
        textRegister = (TextView) view.findViewById(R.id.textRegister);
        inputEmail = (EditText) view.findViewById(R.id.inputEmail);
        inputPwd = (EditText) view.findViewById(R.id.inputPwd);

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

        return view;
    }
}
