package comp5703.sydney.edu.au.kinderfoodfinder;

import android.app.ProgressDialog;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class LoginFragment extends Fragment
{
    // defined variables
    private Fragment fragmentRegister;
    private Button btnLogin;
    private TextView textRegister;
    private EditText inputEmail, inputPwd;

    private ProgressDialog loginProgressDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // set up
        fragmentRegister = new RegisterFragment();
        loginProgressDialog = new ProgressDialog(getActivity());

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
                // set up
                String email = inputEmail.getText().toString().trim();
                String pwd = inputPwd.getText().toString().trim();

                // login function
                loginUser(email, pwd);
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

    /* * * * * * * * * *
     * Login Functions *
     * * * * * * * * * */
    private void loginUser(String email, String password)
    {
        // show the progress dialog until the login validation is complete
        loginProgressDialog.setMessage("Login...");
        loginProgressDialog.show();

        // only for developer to test the app
        if(email.equals("test") && password.equals("test"))
        {
            loginProgressDialog.dismiss();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
            return;
        }

        // set up
        String ipAddress = "172.20.10.4";  //100.101.72.250 Here should be changed to your server IP
        String url = "http://" + ipAddress + ":3000/android-app-login?email=" + email + "&password=" + password;

        // send the request to the server for checking user login info
        RequestQueue ExampleRequestQueue = Volley.newRequestQueue(getActivity());
        StringRequest ExampleStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                //You can test it by printing response.substring(0,500) to the screen.
                loginProgressDialog.dismiss();
                if(response.equals("Yes"))
                {
                    Toast.makeText(getActivity(), "Login Successfully!", Toast.LENGTH_SHORT).show();
                    Log.d("Send query response:", response);
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
                else
                {
                    Toast.makeText(getActivity(), "Wrong email or password!", Toast.LENGTH_SHORT).show();
                    Log.d("Send query response:", response);
                }
            }
        },
                new Response.ErrorListener()  //Create an error listener to handle errors appropriately.
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        //This code is executed if there is an error.
                        loginProgressDialog.dismiss();
                        Toast.makeText(getActivity(), "Login Failed!", Toast.LENGTH_SHORT).show();
                        Log.d("Send query error:", error.toString());
                    }
                });
        ExampleRequestQueue.add(ExampleStringRequest);
    }
}
