package comp5703.sydney.edu.au.kinderfoodfinder;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterFragment extends Fragment
{
    // defined variables
    private Fragment fragmentLogin;
    private Button btnRegister;
    private TextView textBack;
    private EditText inputName, inputEmail, inputPwd, inputConfirmPwd, inputBirthday;
    private RadioGroup radioGroup;
    private RadioButton radioButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // set up
        fragmentLogin = new LoginFragment();
        btnRegister = (Button) view.findViewById(R.id.btnLogin);
        textBack = (TextView) view.findViewById(R.id.textRegister);
        inputName = (EditText) view.findViewById(R.id.inputName);
        inputEmail = (EditText) view.findViewById(R.id.inputEmail);
        inputPwd = (EditText) view.findViewById(R.id.inputPwd);
        inputConfirmPwd = (EditText) view.findViewById(R.id.inputPwdConfirm);
        inputBirthday = (EditText) view.findViewById(R.id.inputBirthday);

        int selectedId = radioGroup.getCheckedRadioButtonId();
        radioButton = (RadioButton) view.findViewById(selectedId);
        Toast.makeText(getActivity(), radioButton.getText(), Toast.LENGTH_SHORT).show();

        // button on click listeners
        btnRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {



            }
        });

        // text view on click listener
        textBack.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // go to register fragment
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentLogin).commit();
            }
        });

        return view;
    }
}
