package comp5703.sydney.edu.au.kinderfoodfinder;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class RegisterFragment extends Fragment
{
    // defined variables
    private Fragment fragmentLogin;
    private Button btnRegister;
//    private TextView textBack;
    private EditText inputName, inputEmail, inputPwd, inputConfirmPwd, inputBirthday;
    private CheckBox checkAgreement;
    private RadioGroup radioGroup;
    private RadioButton radioButton;

    private ProgressDialog registerProgressDialog;

    private Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // set up
        fragmentLogin = new LoginFragment();

        btnRegister = (Button) view.findViewById(R.id.btnRegister);
//        textBack = (TextView) view.findViewById(R.id.textBack);

        inputName = (EditText) view.findViewById(R.id.inputName);
        inputEmail = (EditText) view.findViewById(R.id.inputEmailR);
        inputPwd = (EditText) view.findViewById(R.id.inputPwdR);
        inputConfirmPwd = (EditText) view.findViewById(R.id.inputPwdConfirm);
        inputBirthday = (EditText) view.findViewById(R.id.inputBirthday);

        checkAgreement = (CheckBox) view.findViewById(R.id.checkAgreement);

        radioGroup = (RadioGroup) view.findViewById(R.id.radioGender);
        int selectedId = radioGroup.getCheckedRadioButtonId();
        radioButton = (RadioButton) view.findViewById(selectedId);

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);

        // testing only
//        Toast.makeText(getActivity(), radioButton.getText(), Toast.LENGTH_SHORT).show();

        // button on click listeners
        btnRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {



            }
        });

        // tool bar listener
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // go to login fragment
                getActivity().getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                        .replace(R.id.fragment_container, fragmentLogin).commit();

                // remove toolbar again
                toolbar.setVisibility(View.GONE);
            }
        });

        return view;
    }

    /* * * * * * * * * * * *
     * Register Functions  *
     * * * * * * * * * * * */
    private void registerUser(String name, String email, String pwd, String confirmPwd, Date birthday)
    {
        // check if the texts are empty
        if(TextUtils.isEmpty(name))
        {
            Toast.makeText(getActivity(), "Please enter your name!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(getActivity(), "Please enter your email!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(pwd))
        {
            Toast.makeText(getActivity(), "Please enter your password!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(confirmPwd))
        {
            Toast.makeText(getActivity(), "Please enter your password again!", Toast.LENGTH_SHORT).show();
            return;
        }
        // check pwd equals to confirm pwd
        if(!pwd.equals(confirmPwd))
        {
            Toast.makeText(getActivity(), "Confirmed password is not matched!", Toast.LENGTH_SHORT).show();
            return;
        }

        // if all validations above are passed, show the progress dialog
        registerProgressDialog.setMessage("Registering...");
        registerProgressDialog.show();

        // create the user data
        String timeStamp = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        registerProgressDialog.dismiss();
        Toast.makeText(getActivity(), "Registered Successfully!", Toast.LENGTH_SHORT).show();
    }
}
