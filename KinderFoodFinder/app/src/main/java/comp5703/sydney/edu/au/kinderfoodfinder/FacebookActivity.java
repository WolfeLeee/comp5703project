package comp5703.sydney.edu.au.kinderfoodfinder;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Calendar;

public class FacebookActivity extends AppCompatActivity {



    private Button btnRegister;
    private EditText inputBirthday,inputName, inputEmail;
    private CheckBox checkAgreement, checkIfDiscloseDOB;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private ImageView datePicker;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private ProgressDialog registerProgressDialog;

    private Toolbar toolbar;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_facebook );



        Intent intent= getIntent();
        final String fb_name = intent.getStringExtra("fb_name");
        final String fb_id = intent.getStringExtra("fb_id");


        Log.d("Face    ",fb_name+"   "+fb_id);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        registerProgressDialog = new ProgressDialog(this );


        radioGroup = (RadioGroup) findViewById(R.id.radioGender);

        inputBirthday = (EditText) findViewById(R.id.inputBirthday);
        datePicker = (ImageView) findViewById(R.id.datePicker);

        checkIfDiscloseDOB = (CheckBox) findViewById(R.id.checkIfDiscloseDOB);
        checkAgreement = (CheckBox) findViewById(R.id.checkAgreement);

        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnBack=findViewById( R.id.btnBack );


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

                DatePickerDialog dialog = new DatePickerDialog(FacebookActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable( Color.TRANSPARENT));
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

                String gender = radioButton.getText().toString();
                String birthday = inputBirthday.getText().toString();
                boolean showBirthday = checkIfDiscloseDOB.isChecked();

                if(checkAgreement.isChecked())
                    registerUser(fb_name, gender, fb_id, birthday, showBirthday);
                else
                    Toast.makeText(FacebookActivity.this, "Sorry, you have to agree before register!", Toast.LENGTH_SHORT).show();
            }
        });

        // tool bar listener
        toolbar.setVisibility( View.GONE );

        btnBack.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        } );
    }


    /* * * * * * * * * * * *
     * Register Functions  *
     * * * * * * * * * * * */
    private void registerUser(String name, String gender, String pwd,  String birthday, boolean showBirthday)
    {

        if(TextUtils.isEmpty(birthday))
        {
            Toast.makeText(FacebookActivity.this, "Please select date for your birthday!", Toast.LENGTH_SHORT).show();
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
        String ipAddress = "192.168.20.30";  //100.101.72.250 Here should be changed to your server IP
        if(!showBirthday)
            url = "http://" + ipAddress + ":3000/android-app-register?name=" + name + "&gender=" + genderModified + "&email=" +
                    pwd + "&password=" + pwd + "&birthday=" + birthdayModified;
        else
            url = "http://" + ipAddress + ":3000/android-app-register?name=" + name + "&gender=" + genderModified + "&email=" +
                    pwd + "&password=" + pwd + "&birthday=Not+Disclose";

        // send the data to the server
        RequestQueue ExampleRequestQueue = Volley.newRequestQueue(this);
        StringRequest ExampleStringRequest = new StringRequest( Request.Method.GET, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                //You can test it by printing response.substring(0,500) to the screen.

                registerProgressDialog.dismiss();

                Intent intent = new Intent(FacebookActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(FacebookActivity.this, "Registered Successfully!", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(FacebookActivity.this, "Registered Failed!", Toast.LENGTH_SHORT).show();
                        Log.d("Send query error:", error.toString());
                    }
                });
        ExampleRequestQueue.add(ExampleStringRequest);
    }


}
