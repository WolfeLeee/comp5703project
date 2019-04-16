package comp5703.sydney.edu.au.kinderfoodfinder;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import java.util.Calendar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class RegisterFragment extends Fragment
{
    // defined variables
    private Fragment fragmentLogin;
    private Button btnRegister;
    private EditText inputName, inputEmail, inputPwd, inputConfirmPwd, inputBirthday;
    private CheckBox checkAgreement, checkIfDiscloseDOB;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private ImageView datePicker;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private ProgressDialog registerProgressDialog;

    private Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // set up
        fragmentLogin = new LoginFragment();
        registerProgressDialog = new ProgressDialog(getActivity());

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);

        inputName = (EditText) view.findViewById(R.id.inputName);

        radioGroup = (RadioGroup) view.findViewById(R.id.radioGender);

        inputEmail = (EditText) view.findViewById(R.id.inputEmailR);
        inputPwd = (EditText) view.findViewById(R.id.inputPwdR);
        inputConfirmPwd = (EditText) view.findViewById(R.id.inputPwdConfirm);
        inputBirthday = (EditText) view.findViewById(R.id.inputBirthday);
        datePicker = (ImageView) view.findViewById(R.id.datePicker);

        checkIfDiscloseDOB = (CheckBox) view.findViewById(R.id.checkIfDiscloseDOB);
        checkAgreement = (CheckBox) view.findViewById(R.id.checkAgreement);

        btnRegister = (Button) view.findViewById(R.id.btnRegister);

        // testing only
//        Toast.makeText(getActivity(), radioButton.getText(), Toast.LENGTH_SHORT).show();

        // image view of date picker click listener
        datePicker.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(getActivity(),
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
            {
                String date = dayOfMonth + "/" + month + "/" + year;
                inputBirthday.setText(date);
            }
        };

        // button on click listeners
        btnRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // set up all the inputs
                int selectedId = radioGroup.getCheckedRadioButtonId();
                radioButton = (RadioButton) radioGroup.findViewById(selectedId);

                String name = inputName.getText().toString();
                String gender = radioButton.getText().toString();
                String email = inputEmail.getText().toString();
                String password = inputPwd.getText().toString();
                String passwordConfirm = inputConfirmPwd.getText().toString();
                String birthday = inputBirthday.getText().toString();
                boolean showBirthday = checkIfDiscloseDOB.isChecked();

                if(checkAgreement.isChecked())
                    registerUser(name, gender, email, password, passwordConfirm, birthday, showBirthday);
                else
                    Toast.makeText(getActivity(), "Sorry, you have to agree before register!", Toast.LENGTH_SHORT).show();
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
    private void registerUser(String name, String gender, String email, String pwd, String confirmPwd, String birthday, boolean showBirthday)
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
        if(TextUtils.isEmpty(birthday))
        {
            Toast.makeText(getActivity(), "Please select date for your birthday!", Toast.LENGTH_SHORT).show();
            return;
        }

        // if all validations above are passed, show the progress dialog
        registerProgressDialog.setMessage("Registering...");
        registerProgressDialog.show();

//        String timeStamp = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        // deal with the gender and birthday format
        String genderModified, birthdayModified;
        if(gender.equals("Not Disclose"))
            genderModified = "Not+Disclose";
        else
            genderModified = gender;
        String[] temp = birthday.split("/");
        birthdayModified = temp[0] + "-" + temp[1] + "-" + temp[2];

        // modify the user data to the server
        String url;
        String ipAddress = "172.20.10.4";  //100.101.72.250 Here should be changed to your server IP
        if(!showBirthday)
            url = "http://" + ipAddress + ":3000/android-app-register?name=" + name + "&gender=" + genderModified + "&email=" +
                    email + "&password=" + pwd + "&birthday=" + birthdayModified;
        else
            url = "http://" + ipAddress + ":3000/android-app-register?name=" + name + "&gender=" + genderModified + "&email=" +
                    email + "&password=" + pwd + "&birthday=Not+Disclose";

        // send the data to the server
        RequestQueue ExampleRequestQueue = Volley.newRequestQueue(getActivity());
        StringRequest ExampleStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                //You can test it by printing response.substring(0,500) to the screen.
                registerProgressDialog.dismiss();
                Toast.makeText(getActivity(), "Registered Successfully!", Toast.LENGTH_SHORT).show();
                Log.d("Send query response:", response);
            }
        },
                new Response.ErrorListener()  //Create an error listener to handle errors appropriately.
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        //This code is executed if there is an error.
                        registerProgressDialog.dismiss();
                        Toast.makeText(getActivity(), "Registered Failed!", Toast.LENGTH_SHORT).show();
                        Log.d("Send query error:", error.toString());
                    }
                });
        ExampleRequestQueue.add(ExampleStringRequest);
    }
}