package comp5703.sydney.edu.au.kinderfoodfinder;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Logger;

public class StartUpActivity extends AppCompatActivity
{
    /* * * * * * * * * * *
     * Defined Variables *
     * * * * * * * * * * */
    private Fragment fragmentLogin;
    private Toolbar toolbar;
    Context context;
    File file;
    String version;
    Button btnHit;

    ProgressDialog pd;

    String ab;
    JSONObject jobj = null;
    String jsonString="";

    /* * * * * * * * * * *
     * On Created Method *
     * * * * * * * * * * */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        // set up tool bar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        toolbar.setVisibility(View.GONE);


        version="1";




        checkBrandDatabase();
        Log.d("JSON",jsonString);

//        writeToFile( "1.0.0",context );


        // set up fragment
        fragmentLogin = new LoginFragment();
        Fragment startFragment=new StartFragment();

        // directly make the view to login fragment first
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, startFragment).commit();
    }





    private void writeToFile(String data, Context context) {
        try {

//            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE));
            FileOutputStream fileOutputStream=new FileOutputStream( file );

//            try{
//                fileOutputStream.write( data );
//            }finally {
//                fileOutputStream.close();
//            }

//            outputStreamWriter.write(data);
//            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

//    private String readFromFile(Context context) {
//
//        String ret = "";
//
//        try {
////            InputStream inputStream = context.openFileInput("config.txt");
////            InputStream inputStream =new FileInputStream( file );
//
//            if ( inputStream != null ) {
//                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//                String receiveString = "";
//                StringBuilder stringBuilder = new StringBuilder();
//
//                while ( (receiveString = bufferedReader.readLine()) != null ) {
//                    stringBuilder.append(receiveString);
//                }
//
//                inputStream.close();
//                ret = stringBuilder.toString();
//            }
//        }
//        catch (FileNotFoundException e) {
//            Log.e("login activity", "File not found: " + e.toString());
//        } catch (IOException e) {
//            Log.e("login activity", "Can not read file: " + e.toString());
//        }
//
//        return ret;
//    }

    private void checkBrandDatabase()
    {

        // deal with the gender and birthday format


        // modify the user data to the server
        String url;
        String ipAddress = "10.16.81.139";  //100.101.72.250 Here should be changed to your server IP

        url = "http://" + ipAddress + ":3000/android-app-check-version-brand";

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
                if(version.equals( response )){

                    Toast.makeText(StartUpActivity.this, "Same Version!", Toast.LENGTH_SHORT).show();
                    Log.d("Send Update response:", response);
                }else{
                    Toast.makeText(StartUpActivity.this, "Update database!", Toast.LENGTH_SHORT).show();
                    Log.d("Send Update response:", response);

                    new JsonTask().execute("http://" + "10.16.81.139" + ":3000/GetAllBrand");
                }



            }
        },
                new Response.ErrorListener()  //Create an error listener to handle errors appropriately.
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        //This code is executed if there is an error.

                        Toast.makeText(StartUpActivity.this, "Update database!", Toast.LENGTH_SHORT).show();
                        Log.d("Send query error:", error.toString());
                    }
                });
        ExampleRequestQueue.add(ExampleStringRequest);
    }



    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();

            pd = new ProgressDialog(StartUpActivity.this);
            pd.setMessage("Please wait");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()){
                pd.dismiss();
            }

            jsonString=result;

//            Log.d("JSON",result );
            JsonParser jsonParser = new JsonParser();
            JsonArray jsonElements = jsonParser.parse(jsonString).getAsJsonArray();
            Log.d("JSON size",String.valueOf( jsonElements.size() ));
            String a=jsonElements.get( 0 ).toString();

            Log.d("JSON element",a );

        }
    }
}
