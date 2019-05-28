package comp5703.sydney.edu.au.kinderfoodfinder;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import comp5703.sydney.edu.au.kinderfoodfinder.Database.StoreDatabase;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.Accreditation;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.AccreditationHelper;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.DaoUnit;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.MyApplication;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.Product;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.StoreHelper;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.StoreInfo;
import comp5703.sydney.edu.au.kinderfoodfinder.StatisticDatabase.StatisticContract;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ReportResultActivity extends AppCompatActivity {


    // defined variables
    TextView tv_product, tv_brand,tv_store, tv_location;
    EditText input_store, input_location, input_statae, input_postcode;
    TextView content_product, content_brand;
    private Button btn_submit,refresh;
    private Toolbar toolbar;
    private BottomNavigationView navigation;
    private Fragment fragmentReport, fragmentlocation, fragmentmore;
    private ProgressDialog reportProgressDialog;
    StoreDatabase storeDatabase;
    int key;
    String sid, brandName,category,accId;
    String jsonString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_report_result );

        tv_product = findViewById(R.id.tv_product);
        tv_brand = findViewById(R.id.tv_brand);
        tv_store = findViewById(R.id.tv_store);
        tv_location = findViewById(R.id.tv_location);

        content_brand = findViewById(R.id.content_brand);
        content_product = findViewById(R.id.content_product);

        input_store = findViewById(R.id.input_store);
        input_location = findViewById(R.id.input_location);
        input_postcode=findViewById( R.id.input_postcode );
        input_statae=findViewById( R.id.input_state );

        btn_submit = findViewById(R.id.btn_submit);
        refresh=findViewById( R.id.update );

        storeDatabase = new StoreDatabase(this);
        fragmentlocation = new LocateFragment();
        fragmentmore = new MoreFragment();
        Intent intent=getIntent();
        sid=intent.getStringExtra( "sid" );
        Log.d( "SID",sid );

        String accid="";
        if(sid!=null){
            Product product= DaoUnit.getInstance().searchBySid( sid );
            brandName=product.getBrand_Name();
            category=product.getCategory();
            accid=product.getAccreditation().get( 0 ).getSid();
        }

        content_product.setText(category);
        content_brand.setText(brandName);

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle( "Back" );

        //send report information to the server
        btn_submit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get user_ID from the profile.txt
                String[] profile=readVersionFile().split( "," );
                String user_id="";
                if(profile.length>=3){
                    user_id=profile[2];
                }
                //get address,store name, state, postcode and brand id from the edit text.
                String storeName=input_store.getText().toString();
                String streetAddress=input_location.getText().toString();
                String state=input_statae.getText().toString();
                String postCode =input_postcode.getText().toString();
                String id =sid;
                String brand =content_brand.getText().toString();
                reportInfomation( storeName,streetAddress,state,postCode,id,user_id);
//                AddStore(brand, storeName, streetAddress, postCode, state);

            }
        } );

        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                finish();
            }
        });

        refresh.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBrandDatabase();
            }
        } );
    }


    private void reportInfomation(String storeName, String streetAddress, String state, String postCode, String productID,String user_id)
    {
        // check whether the edit texts are empty
        if(TextUtils.isEmpty(storeName)) {
            Toast.makeText(ReportResultActivity.this, "Please enter your store name!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(streetAddress)) {
            Toast.makeText(ReportResultActivity.this, "Please enter your street address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(state)) {
            Toast.makeText(ReportResultActivity.this, "Please enter your state!", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(postCode)) {
            Toast.makeText(ReportResultActivity.this, "Please enter your post code!", Toast.LENGTH_SHORT).show();
            return;
        }
        // modify the user data to the server
        String url;
        String ipAddress = "10.16.206.194";  //100.101.72.250 Here should be changed to your server IP
        url = "http://" + StatisticContract.StatisticEntry.IP_Address + ":3000/android-app-report-store?storeName="
                + storeName + "&streetAddress=" + streetAddress + "&state=" + state + "&postCode="
                + postCode + "&productId=" + productID+"&userId=" + user_id;
        // send the data to the server
        RequestQueue ExampleRequestQueue = Volley.newRequestQueue(this);
        StringRequest ExampleStringRequest = new StringRequest( Request.Method.GET, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                // go to more page
                finish();
                Toast.makeText(ReportResultActivity.this, "Report Successfully!", Toast.LENGTH_SHORT).show();
                Log.d("Send query response:", response);
            }
        },
                new Response.ErrorListener()  //Create an error listener to handle errors appropriately.
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        //This code is executed if there is an error.
                        Toast.makeText(ReportResultActivity.this, "Report Failed!", Toast.LENGTH_SHORT).show();
                        Log.d("Send query error:", error.toString());
                    }
                });
        ExampleRequestQueue.add(ExampleStringRequest);
    }

    private void checkBrandDatabase( )
    {// modify the user data to the server
        String url;
        String ipAddress = "172.20.10.4";  //100.101.72.250 Here should be changed to your server IP
        url = "http://" + StatisticContract.StatisticEntry.IP_Address + ":3000/android-app-check-version-brand-store";
        // send the data to the server
        RequestQueue ExampleRequestQueue = Volley.newRequestQueue(this);
        StringRequest ExampleStringRequest = new StringRequest( Request.Method.GET, url, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                // get the server database version from the response and get local database from the version.txt
                String[] result=response.split( "," );
                String[] version=readVersionFile().split( "," );
                int appbrand=1;
                int appstore=1;
                int serverbrand=1;
                int serverstore=1;
                Log.d("Send Update response:", response);
                // try to parse the string to the integer
                try{
                    serverbrand=Integer.parseInt( result[0] );
                    serverstore=Integer.parseInt( result[0] );
                    appbrand=Integer.parseInt( version[0] );
                    appstore=Integer.parseInt( version[1] );
                }catch (Exception e){
                }
                // if the server brand version is higher than local version it will send update request.
                if(serverbrand>appbrand){
//                    Toast.makeText(getActivity(), "Updating database!, Please wai...", Toast.LENGTH_SHORT).show();
                    Log.d("Send brand Update :", "yes");
                    DaoUnit.getInstance().clearProductsTable();
                    DaoUnit.getInstance().clearAccreditationTable();
                    // write the new data to the local database
                    AccreditationHelper accreditationHelper=new AccreditationHelper(getApplicationContext());
                    SQLiteDatabase database= accreditationHelper.getWritableDatabase();
                    accreditationHelper.deleteAll( database );
                    new JsonTask().execute("http://" + StatisticContract.StatisticEntry.IP_Address + ":3000/GetAllBrand");
                }else {
//                    Toast.makeText(getActivity(), "Already up to date!", Toast.LENGTH_SHORT).show();
                    Log.d("Send Brand Update:", "no");
                }

                // if the server store version is higher than local version it will send update request.
                if(serverstore>appstore){
//                    Toast.makeText(getActivity(), "Update Store database!", Toast.LENGTH_SHORT).show();
                    Log.d("Send Store Update :", "yes");
                    //write the new data to the store database
                    StoreHelper storeHelper=new StoreHelper( getApplicationContext());
                    SQLiteDatabase database= storeHelper.getWritableDatabase();
                    storeHelper.deleteAll( database );
                    new StoreJsonTask().execute("http://" + StatisticContract.StatisticEntry.IP_Address + ":3000/GetAllBrandinStore");
                }else {
//                    Toast.makeText(getActivity(), "Already up to date", Toast.LENGTH_SHORT).show();
                    Log.d("Send Store Update:", "no");
//
                }
                //if store or brand database has updated, it should rewrite version.txt file
                if(serverbrand>appbrand|| serverstore>appstore){
                    deletefile();
                    String ver=response+","+"0";
                    writeVersionFile( ver );
                    Log.d("Send write file:", "yes");
                    Toast.makeText(ReportResultActivity.this, "Updating database!, Please wai...", Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(ReportResultActivity.this, "Already up to date", Toast.LENGTH_SHORT).show();

                }
            }
        },
                new Response.ErrorListener()  //Create an error listener to handle errors appropriately.
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        //This code is executed if there is an error.

                        Toast.makeText(ReportResultActivity.this, "Update database!", Toast.LENGTH_SHORT).show();

                        Log.d("Send update error:", error.toString());
                    }
                });
        ExampleRequestQueue.add(ExampleStringRequest);
    }

    private class JsonTask extends AsyncTask<String, String, String> {
        Context context;
        protected void onPreExecute() {
            super.onPreExecute();
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
                jsonString=buffer.toString();
                jsonString = jsonString.replace("_id","sid");
                jsonString = jsonString.replace( "\"pig\"," ,"\"Pork\",");
                JsonParser jsonParser = new JsonParser();
                JsonArray jsonElements = jsonParser.parse(jsonString).getAsJsonArray();
                AccreditationHelper accreditationHelper=new AccreditationHelper(getApplicationContext());
                SQLiteDatabase database= accreditationHelper.getWritableDatabase();
                Gson gson = new Gson();
                ArrayList<Product> productArrayList = new ArrayList<>();
                for (JsonElement product:jsonElements) {
                    Log.d("JSON element brand", product.toString() );
                    Product pro = gson.fromJson(product,Product.class);
                    pro.setId( MyApplication.getInstance().getDaoSession().getProductDao().insertWithoutSettingPk(pro));
                    for (Accreditation acc:pro.getAccreditation()) {
                        acc.setParentId(pro.getId());
                        MyApplication.getInstance().getDaoSession().getAccreditationDao().insertWithoutSettingPk(acc);
                        accreditationHelper.addAcc( acc.getSid(),pro.getSid(),acc.getAccreditation(),acc.getRating(),database );
                    }
                    productArrayList.add(pro);
                }
                accreditationHelper.close();
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

        }
    }
    private class StoreJsonTask extends AsyncTask<String, String, String> {
        Context context;

        protected void onPreExecute() {
            super.onPreExecute();
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

                jsonString=buffer.toString();
                jsonString = jsonString.replace("_id","sid");
                JsonParser jsonParser = new JsonParser();
                JsonArray jsonElements = jsonParser.parse(jsonString).getAsJsonArray();
                StoreHelper storeHelper=new StoreHelper(getApplicationContext());
//
                SQLiteDatabase database= storeHelper.getWritableDatabase();
                Gson gson = new Gson();
                ArrayList<StoreInfo> storeInfos=new ArrayList<>(  );

                for (JsonElement store:jsonElements) {
                    Log.d("JSON element store", store.toString() );
                    StoreInfo s = gson.fromJson(store,StoreInfo.class);
                    storeInfos.add( s );

                    storeHelper.addStore( s,database );
                }

                storeHelper.close();
                Log.d("JSON element store", String.valueOf( storeInfos.size() ) );
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


        }
    }
    private String readVersionFile()
    {
        String ret = "";
        try
        { InputStream inputStream = this.openFileInput("version.txt");

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
    private void writeVersionFile(String version)
    {
        try
        {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.openFileOutput("version.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(version);
            outputStreamWriter.close();
        }
        catch (IOException e)
        {
            Log.e("Exception", "File write failed: " + e.toString());
        }

    }
    public void deletefile() {
        try {
            //
            File file = new File(getApplicationContext().getFilesDir(), "version.txt");
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
