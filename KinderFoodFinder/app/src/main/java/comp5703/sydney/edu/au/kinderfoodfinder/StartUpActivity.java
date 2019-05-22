package comp5703.sydney.edu.au.kinderfoodfinder;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import org.json.JSONArray;
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

import comp5703.sydney.edu.au.greendao.gen.AccreditationDao;
import comp5703.sydney.edu.au.kinderfoodfinder.Database.ProductContract;
import comp5703.sydney.edu.au.kinderfoodfinder.Database.ProductDatabase;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.Accreditation;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.AccreditationHelper;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.Contract;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.DaoUnit;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.MyApplication;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.Product;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.StoreHelper;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.StoreInfo;
import comp5703.sydney.edu.au.kinderfoodfinder.StatisticDatabase.StatisticContract;
import comp5703.sydney.edu.au.kinderfoodfinder.StatisticDatabase.StatisticsDatabase;

import static com.facebook.FacebookSdk.getApplicationContext;

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
    String IP_ADDRESS = "10.16.82.52";

    ProgressDialog pd;

    StartUpActivity startUpActivity;


    String jsonString="";
    String brand_version, store_version;
    String status;
    Boolean updateBrandVersion=false;
    Boolean updateStoreVersion=false;
    String test="";
//    Intent intent=new Intent(StartUpActivity.this,MainActivity.class  );


    public Intent intent;
    Intent startIntent;
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

        // check if the file of version.txt has been created
//        checkProfileFile();
        File fileVersion = new File(getApplicationContext().getFilesDir(), "version.txt");
        if(!(fileVersion.exists())) {
            writeToFile( "1,1,0" );
            Log.d( "VersionDatabase", "Creating!" );
        }
        version = readFromFile();
        String[] v=version.split( "," );
        brand_version=v[0];
        store_version=v[1];
        Log.d("VersionDatabase", "Existing!");

        int bversion=1;
        int sversion=1;
        try{
            bversion=Integer.parseInt( brand_version );
            sversion=Integer.parseInt( store_version );
        }catch (Exception e){

        }
        checkBrandDatabase();

//        Log.d("VersionDatabase", version);
//        Log.d("VersionDatabase1", test);

//        testString();
        intent=new Intent(StartUpActivity.this,MainActivity.class  );
        startIntent=new Intent( StartUpActivity.this,StartUpActivity.class );
//        testString();
//        deleteClickStatistic();

//        readStore();
        gourpBY();
        String test=getResults();
        JsonParser jsonParser = new JsonParser();
        JsonArray jsonElements = jsonParser.parse(test).getAsJsonArray();
//        Log.d("Statistics Size",String.valueOf( jsonElements.size() ));

        sendStatistics( test );
        readStore();

        // set up fragment
        fragmentLogin = new LoginFragment();
        Fragment startFragment=new StartFragment();

        // directly make the view to login fragment first
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, startFragment).commit();
    }

    private String readFromFile()
    {
        String ret = "";

        try
        {
            InputStream inputStream = this.openFileInput("version.txt");

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

    private void writeToFile(String version)
    {
//         version = "1,1";

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

    private void checkBrandDatabase( )
{
    // modify the user data to the server
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
            //You can test it by printing response.substring(0,500) to the screen.
            Boolean loaddata=checkVersionFile();
            String[] result=response.split( "," );
            String[] version=readFromFile().split( "," );
            int appbrand=1;
            int appstore=1;
            int serverbrand=1;
            int serverstore=1;
            Log.d("Send Update response:", response);
            Log.d("Send Update version:", readFromFile());

            try{
                serverbrand=Integer.parseInt( result[0] );
                serverstore=Integer.parseInt( result[0] );

                appbrand=Integer.parseInt( version[0] );
                appstore=Integer.parseInt( version[1] );

            }catch (Exception e){

            }

            Log.d("Send Update :", String.valueOf( result[0] )+String.valueOf( result[1] )
                    +String.valueOf( appbrand)+String.valueOf(appstore ));

            if(serverbrand>appbrand||loaddata){

                Toast.makeText(StartUpActivity.this, "Update database!", Toast.LENGTH_SHORT).show();
                Log.d("Send brand Update :", "yes");
                DaoUnit.getInstance().clearProductsTable();
                DaoUnit.getInstance().clearAccreditationTable();

                AccreditationHelper accreditationHelper=new AccreditationHelper(getApplicationContext());
//
                SQLiteDatabase database= accreditationHelper.getWritableDatabase();
                accreditationHelper.deleteAll( database );
//                accreditationHelper.onCreate( database );
                new JsonTask().execute("http://" + StatisticContract.StatisticEntry.IP_Address + ":3000/GetAllBrand");



            }else {
                Toast.makeText(StartUpActivity.this, "Same Version!", Toast.LENGTH_SHORT).show();
                Log.d("Send Brand Update:", "no");
//                if(status.equals( "0" )){
//                    startIntent.putExtra( "brand_version",response );
//                    startIntent.putExtra( "brand_update","yes" );
//                }else {
////                    Intent intent=new Intent(StartUpActivity.this,MainActivity.class  );
//                    intent.putExtra( "brand_version",response );
//                    intent.putExtra( "brand_update","yes" );
//                }
//                Log.d("update versionDatabase","brand"+brand_version+"   "+"store"+store_version)
            }

            if(serverstore>appstore||loaddata){
                Toast.makeText(StartUpActivity.this, "Update Store database!", Toast.LENGTH_SHORT).show();
                Log.d("Send Store Update :", "yes");
                StoreHelper storeHelper=new StoreHelper( getApplicationContext());
                SQLiteDatabase database= storeHelper.getWritableDatabase();
                storeHelper.deleteAll( database );
                new StoreJsonTask().execute("http://" + StatisticContract.StatisticEntry.IP_Address + ":3000/GetAllBrandinStore");

            }else {
                Toast.makeText(StartUpActivity.this, "Same Version!", Toast.LENGTH_SHORT).show();
                Log.d("Send Store Update:", "no");
//
            }
            if(serverbrand>appbrand|| serverstore>appstore){
                deletefile();
                String ver=response+","+"0";
                writeToFile( ver );
                Log.d("Send write file:", "yes");
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
//            Log.d("json",result);
                jsonString = jsonString.replace("_id","sid");
                jsonString = jsonString.replace( "\"pig\"," ,"\"Pork\",");
//            Log.d("JSON",result );
                JsonParser jsonParser = new JsonParser();
                JsonArray jsonElements = jsonParser.parse(jsonString).getAsJsonArray();

                AccreditationHelper accreditationHelper=new AccreditationHelper(getApplicationContext());
//
                SQLiteDatabase database= accreditationHelper.getWritableDatabase();

                Gson gson = new Gson();
                ArrayList<Product>productArrayList = new ArrayList<>();
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
//           jsonString=result;
////            Log.d("json",result);
//            jsonString = jsonString.replace("_id","sid");
////            Log.d("JSON",result );
//            JsonParser jsonParser = new JsonParser();
//            JsonArray jsonElements = jsonParser.parse(jsonString).getAsJsonArray();
//
//            AccreditationHelper accreditationHelper=new AccreditationHelper(getApplicationContext());
////
//            SQLiteDatabase database= accreditationHelper.getWritableDatabase();
//
//            Gson gson = new Gson();
//            ArrayList<Product>productArrayList = new ArrayList<>();
//            for (JsonElement product:jsonElements) {
//                Product pro = gson.fromJson(product,Product.class);
//                pro.setId( MyApplication.getInstance().getDaoSession().getProductDao().insertWithoutSettingPk(pro));
//                for (Accreditation acc:pro.getAccreditation()) {
//                    acc.setParentId(pro.getId());
//                    MyApplication.getInstance().getDaoSession().getAccreditationDao().insertWithoutSettingPk(acc);
//                    accreditationHelper.addAcc( acc.getSid(),pro.getSid(),acc.getAccreditation(),acc.getRating(),database );
//                }
//                productArrayList.add(pro);
//            }
//            accreditationHelper.close();
////            Log.d("JSON size",String.valueOf( jsonElements.size() ));
//            Toast.makeText( StartUpActivity.this,"update database",Toast.LENGTH_LONG ).show();
//            String a=jsonElements.get( 0 ).toString();
////            Log.d("JSON element",jsonString );

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
//                    s.setId( MyApplication.getInstance().getDaoSession().getStoreInfoDao().insertWithoutSettingPk(s));
                    storeInfos.add( s );
                    Log.d("tttttt", store.toString() );

                    String storename=s.getStoreName();
                    String address=s.getStreetAddress();
                    String state=s.getState();
                    String postcod=s.getPostcode();
                    String lon=s.getLong();
                    String lat=s.getLat();

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


//            jsonString=result;
//            Log.d("json",result);
//            jsonString = jsonString.replace("_id","sid");
//           Log.d("JSON",result );
//            JsonParser jsonParser = new JsonParser();
//            JsonArray jsonElements = jsonParser.parse(jsonString).getAsJsonArray();
//
//
//            Gson gson = new Gson();
////            for (JsonElement product:jsonElements) {
////
////            }
//
//            Log.d("JSON size",String.valueOf( jsonElements.size() ));
//            Toast.makeText( StartUpActivity.this,"update database",Toast.LENGTH_LONG ).show();
//            String a=jsonElements.get( 0 ).toString();
//            Log.d("JSON element",jsonString );


        }
    }

    private void readStatistics(){
        StatisticsDatabase statisticsDatabase=new StatisticsDatabase( this );
        SQLiteDatabase database=statisticsDatabase.getReadableDatabase();

        Cursor cursor=statisticsDatabase.readContats( database );

        if(cursor!=null){
            Log.d("Database "," already has information");
            String info ="";
            while (cursor.moveToNext()){

                String id =cursor.getString(  cursor.getColumnIndex( StatisticContract.StatisticEntry.Brand_ID ));
                String date=cursor.getString( cursor.getColumnIndex( StatisticContract.StatisticEntry.DATE ) );
                String count=cursor.getString( cursor.getColumnIndex( StatisticContract.StatisticEntry.COUNT ) );
                String gender=cursor.getString( cursor.getColumnIndex( StatisticContract.StatisticEntry.GENDER ) );
                String age=cursor.getString( cursor.getColumnIndex( StatisticContract.StatisticEntry.AGE ) );

//            info=info+"\n\n"+"ID :"+id+"\nName :"+name+"\nEmail :"+email;

                info=id+"; "+date+"; "+gender+"; "+age+"; "+count;
//                Log.d("statistics record",info);

            }

        }
        statisticsDatabase.close();
    }
    private void readStore(){
        StoreHelper storeHelper=new StoreHelper( this );
        SQLiteDatabase database=storeHelper.getReadableDatabase();

        Cursor cursor=storeHelper.readStore( database );

        if(cursor!=null){
            Log.d("Database "," already has information");
            String info ="";
            while (cursor.moveToNext()){

                String storeName =cursor.getString(  cursor.getColumnIndex( Contract.StoreContract.storeName ));
                String street=cursor.getString( cursor.getColumnIndex( Contract.StoreContract.StreetAddress ) );
                String state=cursor.getString( cursor.getColumnIndex( Contract.StoreContract.State ) );
                String brand=cursor.getString( cursor.getColumnIndex( Contract.StoreContract.Brandname ) );
                String id=cursor.getString( cursor.getColumnIndex( Contract.StoreContract.Brandid ) );

//            info=info+"\n\n"+"ID :"+id+"\nName :"+name+"\nEmail :"+email;

                info=storeName+"; "+street+"; "+state+"; "+brand+"; "+id;
                Log.d("Address record",info);

            }

        }
        storeHelper.close();
    }

    public void deleteClickStatistic(){
        StatisticsDatabase statisticsDatabase=new StatisticsDatabase( this );
        SQLiteDatabase database=statisticsDatabase.getWritableDatabase();

        statisticsDatabase.deleteAll( database );
        statisticsDatabase.onCreate( database );
    }

    public void gourpBY(){
        StatisticsDatabase statisticsDatabase=new StatisticsDatabase( this );
        SQLiteDatabase database=statisticsDatabase.getWritableDatabase();
        statisticsDatabase.groupby( database );
        Cursor cursor=statisticsDatabase.groupby( database );


        if(cursor!=null){
            Log.d("Database "," already has information");
            String info ="";
            while (cursor.moveToNext()){
                String id =cursor.getString(  cursor.getColumnIndex( StatisticContract.StatisticEntry.Brand_ID ));
                String date=cursor.getString( cursor.getColumnIndex( StatisticContract.StatisticEntry.DATE ) );
                String count=cursor.getString( cursor.getColumnIndex( StatisticContract.StatisticEntry.COUNT ) );
                String gender=cursor.getString( cursor.getColumnIndex( StatisticContract.StatisticEntry.GENDER ) );
                String age=cursor.getString( cursor.getColumnIndex( StatisticContract.StatisticEntry.AGE ) );

//            info=info+"\n\n"+"ID :"+id+"\nName :"+name+"\nEmail :"+email;

                info=id+"; "+date+"; "+gender+"; "+age+"; "+count;
//                Log.d("statistics record",info);

            }
        }
        statisticsDatabase.close();
    }

    public String getResults() {
        StatisticsDatabase statisticsDatabase=new StatisticsDatabase(  this);
        SQLiteDatabase myDataBase=statisticsDatabase.getReadableDatabase();

        String searchQuery = "SELECT  * FROM ";
        Cursor cursor = statisticsDatabase.groupby( myDataBase );

        JSONArray resultSet = new JSONArray();
        JSONObject returnObj = new JSONObject();

        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {

            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();

            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {

                    try {

                        if (cursor.getString(i) != null) {
                            Log.d("TAG_NAME", cursor.getString(i));
                            rowObject.put(cursor.getColumnName(i), cursor.getString(i));
                        } else {
                            rowObject.put(cursor.getColumnName(i), "");
                        }
                    } catch (Exception e) {
                        Log.d("TAG_NAME", e.getMessage());
                    }
                }

            }

            resultSet.put(rowObject);
            cursor.moveToNext();
        }

        cursor.close();
        Log.d("TAG_NAME", resultSet.toString());
        return resultSet.toString();
    }


    private void sendStatistics(String result)
    {

        String url;
        String ipAddress = "10.16.206.194";  //100.101.72.250 Here should be changed to your server IP
        url = "http://" + StatisticContract.StatisticEntry.IP_Address + ":3000/android-app-statistic?statistic="+result;

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
                if(response.equals( "Yes" )){
                    deleteClickStatistic();
                }
                Log.d("Send stat response:", response);
            }
        },
                new Response.ErrorListener()  //Create an error listener to handle errors appropriately.
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        //This code is executed if there is an error.

                        Log.d("Send statistics error:", error.toString());
                    }
                });
        ExampleRequestQueue.add(ExampleStringRequest);
    }

    public void testString(){

        String jsonString = "[{\"_id\":\"5cc7da37ba39be1255db1a73\",\"Brand_Name\":\"Adelaide Eggs Cage\",\"Available\":\"\",\"Category\":\"egg\"," +
                "\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1a74\",\"Accreditation\":\"Cage, Caged\",\"Rating\":\"Avoid\"}],\"Image\":null},{\"_id\":\"" +
                "5cc7da37ba39be1255db1a75\",\"Brand_Name\":\"Aussie Pride Multi Grain Eggs Cage Laid\",\"Available\":\"\",\"Category\":\"egg\"," +
                "\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1a76\",\"Accreditation\":\"Cage, Caged\",\"Rating\":\"Avoid\"}],\"Image\":null},{\"_id\":" +
                "\"5cc7da37ba39be1255db1a77\",\"Brand_Name\":\"Black & Gold Cage Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\"" +
                ":\"5cc7da37ba39be1255db1a78\",\"Accreditation\":\"Cage, Caged\",\"Rating\":\"Avoid\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1a79\"" +
                ",\"Brand_Name\":\"Budget Mixed Grade Cage eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1a" +
                "7a\",\"Accreditation\":\"Cage, Caged\",\"Rating\":\"Avoid\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1a7b\",\"Brand_Name\":\"" +
                "Country Classics Cage Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1a7c\",\"Accreditation\":\"" +
                "Cage, Caged\",\"Rating\":\"Avoid\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1a7f\",\"Brand_Name\":\"Country House Cage Eggs\",\"Available\":" +
                "\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1a80\",\"Accreditation\":\"Cage, Caged\",\"Rating\":\"Avoid\"}],\"Image" +
                "\":null},{\"_id\":\"5cc7da37ba39be1255db1a81\",\"Brand_Name\":\"Days Eggs Country Fresh Cage Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"" +
                "Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1a82\",\"Accreditation\":\"Cage, Caged\",\"Rating\":\"Avoid\"}],\"Image\":null},{\"_id\":\"5cc7da37ba" +
                "39be1255db1a7d\",\"Brand_Name\":\"Country Farmhouse Cage Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be" +
                "1255db1a7e\",\"Accreditation\":\"Cage, Caged\",\"Rating\":\"Avoid\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1a83\",\"Brand_Name\":\"Farm F" +
                "resh Cage Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1a84\",\"Accreditation\":\"Cage, Caged\",\"R" +
                "ating\":\"Avoid\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1a85\",\"Brand_Name\":\"Farm Pride Cage Eggs\",\"Available\":\"\",\"Category\":\"egg\"" +
                ",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1a86\",\"Accreditation\":\"Cage, Caged\",\"Rating\":\"Avoid\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39b" +
                "e1255db1a87\",\"Brand_Name\":\"Farmer Brown Fresh Cage Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be125" +
                "5db1a88\",\"Accreditation\":\"Cage, Caged\",\"Rating\":\"Avoid\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1a89\",\"Brand_Name\":\"Flinders All Gr" +
                "ain Cage Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1a8a\",\"Accreditation\":\"Cage, Caged\",\"" +
                "Rating\":\"Avoid\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1a8b\",\"Brand_Name\":\"Flinders Ranges Cage Eggs\",\"Available\":\"\",\"Categ" +
                "ory\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1a8c\",\"Accreditation\":\"Cage, Caged\",\"Rating\":\"Avoid\"}],\"Image\":null},{\"_id\"" +
                ":\"5cc7da37ba39be1255db1a8d\",\"Brand_Name\":\"Foodland South Australian Farm Fresh Eggs Cage Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"A" +
                "ccreditation\":[{\"_id\":\"5cc7da37ba39be1255db1a8e\",\"Accreditation\":\"Cage, Caged\",\"Rating\":\"Avoid\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39" +
                "be1255db1a8f\",\"Brand_Name\":\"Golden Egg Cage Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db" +
                "1a90\",\"Accreditation\":\"Cage, Caged\",\"Rating\":\"Avoid\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1a91\",\"Brand_Name\":\"Gourmet Brea" +
                "kfast Cage Free Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1a92\",\"Accreditation\":\"Cage, Caged\",\"Rating\":\"Avoid\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1a93\",\"Brand_Name\":\"Homebrand cage eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1a94\",\"Accreditation\":\"Cage, Caged\",\"Rating\":\"Avoid\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1a95\",\"Brand_Name\":\"IGA Signature Cage Free Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1a96\",\"Accreditation\":\"Cage, Caged\",\"Rating\":\"Avoid\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1a97\",\"Brand_Name\":\"Jacobs Well Egg Farm Cage Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1a98\",\"Accreditation\":\"Cage, Caged\",\"Rating\":\"Avoid\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1a99\",\"Brand_Name\":\"Just 4 You Cage Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1a9a\",\"Accreditation\":\"Cage, Caged\",\"Rating\":\"Avoid\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1a9b\",\"Brand_Name\":\"Kalbarri Eggs Fresh Laid Cage Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1a9c\",\"Accreditation\":\"Cage, Caged\",\"Rating\":\"Avoid\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1a9d\",\"Brand_Name\":\"Pace Farm Cage Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1a9e\",\"Accreditation\":\"Cage, Caged\",\"Rating\":\"Avoid\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1aa1\",\"Brand_Name\":\"Tassie's Own Cage Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1aa2\",\"Accreditation\":\"Cage, Caged\",\"Rating\":\"Avoid\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1a9f\",\"Brand_Name\":\"Swan Valley Cage Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1aa0\",\"Accreditation\":\"Cage, Caged\",\"Rating\":\"Avoid\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1aa3\",\"Brand_Name\":\"The Good Egg Cage Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1aa4\",\"Accreditation\":\"Cage, Caged\",\"Rating\":\"Avoid\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1aa5\",\"Brand_Name\":\"The Good Egg Tasmanian Fresh Cage Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1aa6\",\"Accreditation\":\"Cage, Caged\",\"Rating\":\"Avoid\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1aa7\",\"Brand_Name\":\"Value Eggs Fresh Caged Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1aa8\",\"Accreditation\":\"Cage, Caged\",\"Rating\":\"Avoid\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1aa9\",\"Brand_Name\":\"MMM Barn Laid Eggs\",\"Available\":\"Available in WA\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1aaa\",\"Accreditation\":\"RSPCA Approved Indoor\",\"Rating\":\"Good\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1aab\",\"Brand_Name\":\"Lucky Chicken Eggs\",\"Available\":\"Available in ACT, NSW, SA, WA and VIC\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1aac\",\"Accreditation\":\"RSPCA Approved Outdoor\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1aad\",\"Brand_Name\":\"Rohde's Free Range\",\"Available\":\"Available in SA, VIC & NSW (Sydney Produce Markets)\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1aae\",\"Accreditation\":\"RSPCA Approved Outdoor\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1aaf\",\"Brand_Name\":\"Farmer Rod's Free Range\",\"Available\":\"Available in NSW\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1ab0\",\"Accreditation\":\"RSPCA Approved Outdoor\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1ab1\",\"Brand_Name\":\"Forage Farms Free Range\",\"Available\":\"Available in QLD\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1ab2\",\"Accreditation\":\"RSPCA Approved Outdoor\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1ab3\",\"Brand_Name\":\"Good Lookin Googees\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1ab4\",\"Accreditation\":\"FREPA\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1ab5\",\"Brand_Name\":\"Nirvana Free Range Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1ab6\",\"Accreditation\":\"FREPA\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1ab7\",\"Brand_Name\":\"Fryar's Kangaroo Island Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1ab8\",\"Accreditation\":\"Humane Choice\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1ab9\",\"Brand_Name\":\"Organigrow Organic Free Range Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1abb\",\"Accreditation\":\"Humane Choice\",\"Rating\":\"Best\"},{\"_id\":\"5cc7da37ba39be1255db1aba\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1abc\",\"Brand_Name\":\"Real Free Range Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1abd\",\"Accreditation\":\"Humane Choice\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1abe\",\"Brand_Name\":\"Kangaroo Island Free Range\",\"Available\":\"South Australia, Melbourne & Sydney\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1abf\",\"Accreditation\":\"Humane Choice\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1ac0\",\"Brand_Name\":\"Mulloon Creek Natural Farms\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1ac1\",\"Accreditation\":\"Humane Choice\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1ac2\",\"Brand_Name\":\"Organigrow\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1ac3\",\"Accreditation\":\"Humane Choice\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1ac4\",\"Brand_Name\":\"Gumview Free Range Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1ac5\",\"Accreditation\":\"Humane Choice\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1ac6\",\"Brand_Name\":\"The Ethical Farmers (Egganic)\",\"Available\":\"BU Organics Bondi Junction\\nEdgecliff Healthfoods\\nGrass Roots Urban Butcher Vaucluse\\nHealth Emporium - Bondi Road Bondi\\nThe Meat Store - Bondi Junction & Taren Point\\nWholefoods House - Dank Street Waterloo, and Queen Street Woollahra\\nHudson Meats Butchers - Cammeray, Lane Cove, Mosman & Surry Hills\\nThomas Dux Stores - Paddington, Surry Hills, Crows Nest, Lane Cove and Mona Vale\\nAl Dente Bathurst\\nM & J's Butchery in Orange\\nA Slice of Orange in Orange\\\\\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1ac7\",\"Accreditation\":\"Humane Choice\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1ac8\",\"Brand_Name\":\"Walker Farm Foods\",\"Available\":\"Maleny Supa IGA, Your Organic Market, Witta Farmers Market and farm gate.\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1ac9\",\"Accreditation\":\"Humane Choice\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1aca\",\"Brand_Name\":\"Family Homestead Free Range Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1acb\",\"Accreditation\":\"Free Range Farmers Association, FRFA\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1acc\",\"Brand_Name\":\"Free As A Bird Free Range Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1acd\",\"Accreditation\":\"Free Range Farmers Association, FRFA\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1ace\",\"Brand_Name\":\"Free Ranger Free Range Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1acf\",\"Accreditation\":\"Free Range Farmers Association, FRFA\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1ad0\",\"Brand_Name\":\"Moore Brown Free Range Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1ad1\",\"Accreditation\":\"Free Range Farmers Association, FRFA\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1ad2\",\"Brand_Name\":\"Rutherford Bros Free Range Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1ad3\",\"Accreditation\":\"Free Range Farmers Association, FRFA\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1ad4\",\"Brand_Name\":\"Macro Organic Free Range Eggs\",\"Available\":\"Woolworths\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1ad5\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1ad6\",\"Brand_Name\":\"Organic Egg Farmers Organic Free Range Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1ad7\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1ad8\",\"Brand_Name\":\"Ovaston Organics Organic Free Range Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1ad9\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1ada\",\"Brand_Name\":\"Pirovic Eggs - Free Range Organic\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1adb\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1ade\",\"Brand_Name\":\"Sunny Queen Organic Free Range Eggs\",\"Available\":\"Woolworths, Coles\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1adf\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1adc\",\"Brand_Name\":\"Southern Highland Organics Organic Free Range Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1add\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1ae0\",\"Brand_Name\":\"Allamburra Organic Egg\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1ae1\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1ae2\",\"Brand_Name\":\"Beechworth Organic Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1ae3\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1ae4\",\"Brand_Name\":\"Corndale Grove Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1ae5\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1ae6\",\"Brand_Name\":\"Country Range Farming Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1ae7\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1ae8\",\"Brand_Name\":\"Country Range Farming Eco Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1ae9\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1aea\",\"Brand_Name\":\"Country Range Farming Macro Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1aeb\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1aec\",\"Brand_Name\":\"Daintree Organic Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1aed\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1aee\",\"Brand_Name\":\"The Organic Fine Food Company Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1aef\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1af0\",\"Brand_Name\":\"Eggert Agri Partner Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1af1\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1af2\",\"Brand_Name\":\"Ellerslie Free Range Farm Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1af3\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1af4\",\"Brand_Name\":\"Fraser Coast Free Range Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1af5\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1af6\",\"Brand_Name\":\"Fuitlink Eggs, Organiclink Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1af7\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1af8\",\"Brand_Name\":\"McGwerriton Organic Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1af9\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1afa\",\"Brand_Name\":\"National Environment Centre - Riverina Pasture Grown Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1afb\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1afc\",\"Brand_Name\":\"Nocelle Foods Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1afd\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1afe\",\"Brand_Name\":\"Organic Growers Group Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1aff\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b00\",\"Brand_Name\":\"Organic Direct Produce Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b01\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b02\",\"Brand_Name\":\"Alumuna Free Range Egg Farm\",\"Available\":\"Scarsdale VIC\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b03\",\"Accreditation\":\"NASAA, NASAA Organic\\\"\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b04\",\"Brand_Name\":\"Black Raven Farm\",\"Available\":\"Arthur's Creek VIC\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b05\",\"Accreditation\":\"NASAA, NASAA Organic\\\"\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b06\",\"Brand_Name\":\"Bruny Island Cheese Co.\",\"Available\":\"Glen Huon TAS\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b07\",\"Accreditation\":\"NASAA, NASAA Organic\\\"\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b08\",\"Brand_Name\":\"Buda Organic Foods\",\"Available\":\"Conondale QLD\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b09\",\"Accreditation\":\"NASAA, NASAA Organic\\\"\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b0a\",\"Brand_Name\":\"Captain Creek Vineyard & Winery\",\"Available\":\"Blampied VIC\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b0b\",\"Accreditation\":\"NASAA, NASAA Organic\\\"\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b0c\",\"Brand_Name\":\"Christine C Sandford\",\"Available\":\"Ballimore NSW\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b0d\",\"Accreditation\":\"NASAA, NASAA Organic\\\"\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b0e\",\"Brand_Name\":\"Elderslie Organics\",\"Available\":\"Ellinbank VIC\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b0f\",\"Accreditation\":\"NASAA, NASAA Organic\\\"\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b10\",\"Brand_Name\":\"Hollyburton Foundation Madelaine's Eggs\",\"Available\":\"Clarkefield VIC\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b11\",\"Accreditation\":\"NASAA, NASAA Organic\\\"\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b12\",\"Brand_Name\":\"ID & JE Phillips\",\"Available\":\"Kendenup WA\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b13\",\"Accreditation\":\"NASAA, NASAA Organic\\\"\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b14\",\"Brand_Name\":\"Keane's Organic Food\",\"Available\":\"Trott Park SA\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b15\",\"Accreditation\":\"NASAA, NASAA Organic\\\"\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b16\",\"Brand_Name\":\"Merryville Farm\",\"Available\":\"Pillar Valley NSW\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b17\",\"Accreditation\":\"NASAA, NASAA Organic\\\"\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b18\",\"Brand_Name\":\"Mount Ruufus Proprietors\",\"Available\":\"Mintaro SA\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b19\",\"Accreditation\":\"NASAA, NASAA Organic\\\"\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b1a\",\"Brand_Name\":\"Mountford Winery & Vineyard\",\"Available\":\"Pemberton WA\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b1b\",\"Accreditation\":\"NASAA, NASAA Organic\\\"\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b1c\",\"Brand_Name\":\"O Batista\",\"Available\":\"Monbulk VIC\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b1d\",\"Accreditation\":\"NASAA, NASAA Organic\\\"\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b1e\",\"Brand_Name\":\"Organic Times\",\"Available\":\"Bayswater VIC\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b1f\",\"Accreditation\":\"NASAA, NASAA Organic\\\"\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b20\",\"Brand_Name\":\"Organic Ways\",\"Available\":\"Mundoona VIC\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b21\",\"Accreditation\":\"NASAA, NASAA Organic\\\"\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b22\",\"Brand_Name\":\"Payneham Vale Organics\",\"Available\":\"Frankland River WA\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b23\",\"Accreditation\":\"NASAA, NASAA Organic\\\"\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b24\",\"Brand_Name\":\"Randall Organic Rice\",\"Available\":\"Murrami NSW\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b25\",\"Accreditation\":\"NASAA, NASAA Organic\\\"\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b26\",\"Brand_Name\":\"Raw Pride Organics\",\"Available\":\"Hallora VIC\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b27\",\"Accreditation\":\"NASAA, NASAA Organic\\\"\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b28\",\"Brand_Name\":\"Redtail Ridge\",\"Available\":\"Mumballup WA\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b29\",\"Accreditation\":\"NASAA, NASAA Organic\\\"\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b2a\",\"Brand_Name\":\"Sandy McNab Partners\",\"Available\":\"Gannawarra VIC\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b2b\",\"Accreditation\":\"NASAA, NASAA Organic\\\"\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b2c\",\"Brand_Name\":\"Wambyn Olive Farm\",\"Available\":\"York WA\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b2d\",\"Accreditation\":\"NASAA, NASAA Organic\\\"\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b2e\",\"Brand_Name\":\"Habitat Vineyard\",\"Available\":\"Nashdale NSW\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b2f\",\"Accreditation\":\"Organic Food Chain, OFC\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b30\",\"Brand_Name\":\"Coles Australian Free Range Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b31\",\"Accreditation\":\"Coles Egg Production Standard for Free Range\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b32\",\"Brand_Name\":\"Katham Springs Bio-Dynamic Free-range Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b33\",\"Accreditation\":\"DEMETER, DEMETER Bio-dynamic\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b34\",\"Brand_Name\":\"Bullfrog Gully\",\"Available\":\"Ceres Market, VIC\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b35\",\"Accreditation\":\"DEMETER, DEMETER Bio-dynamic\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b36\",\"Brand_Name\":\"Powlett Hill\",\"Available\":\"Ceres Market, VIC\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b37\",\"Accreditation\":\"DEMETER, DEMETER Bio-dynamic\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b38\",\"Brand_Name\":\"Fryar's Kangaroo Island Pastured Free Range Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b39\",\"Accreditation\":\"PROOF\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b3a\",\"Brand_Name\":\"Fitzy's Farm Pasture Raised Eggs\",\"Available\":\"Foodland Supermarket, Slick & Son's Butchery\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b3b\",\"Accreditation\":\"PROOF\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b3c\",\"Brand_Name\":\"Chooks at the Rooke Free Range Pastured Eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b3d\",\"Accreditation\":\"PROOF\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b3e\",\"Brand_Name\":\"Forage Farms\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b3f\",\"Accreditation\":\"PROOF\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b40\",\"Brand_Name\":\"The Old Farm Happy Valley\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b41\",\"Accreditation\":\"PROOF\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b42\",\"Brand_Name\":\"Golden Yolk Whitsundays\",\"Available\":\"Bowen IGA, Hamilton Island IGA, Airlie Beach IGA, Proserpine IGA, Master Butchers Whitsundays\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b43\",\"Accreditation\":\"PROOF\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b44\",\"Brand_Name\":\"Gippsland free range eggs\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b45\",\"Accreditation\":\"PROOF\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b46\",\"Brand_Name\":\"Piggy In The Middle\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b47\",\"Accreditation\":\"PROOF\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b48\",\"Brand_Name\":\"Feather and Peck\",\"Available\":\"\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b49\",\"Accreditation\":\"PROOF\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b4a\",\"Brand_Name\":\"Holbrook Paddock Eggs\",\"Available\":\"Sydney, Melbourne, Canberra and the Riverina\",\"Category\":\"egg\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b4b\",\"Accreditation\":\"PROOF\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b4c\",\"Brand_Name\":\"Campbell's Real Stock\",\"Available\":\"Available nationally\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b4d\",\"Accreditation\":\"RSPCA Approved Indoor\",\"Rating\":\"Good\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b4e\",\"Brand_Name\":\"Coles Chicken\",\"Available\":\"Available nationally\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b4f\",\"Accreditation\":\"RSPCA Approved Indoor\",\"Rating\":\"Good\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b50\",\"Brand_Name\":\"Compass Group\",\"Available\":\"Available nationally\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b51\",\"Accreditation\":\"RSPCA Approved Indoor\",\"Rating\":\"Good\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b52\",\"Brand_Name\":\"David Jones\",\"Available\":\"Available in NSW and VIC\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b53\",\"Accreditation\":\"RSPCA Approved Indoor\",\"Rating\":\"Good\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b56\",\"Brand_Name\":\"Game Farm Spatchcock\",\"Available\":\"Available nationally\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b57\",\"Accreditation\":\"RSPCA Approved Indoor\",\"Rating\":\"Good\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b54\",\"Brand_Name\":\"Freedom Farms\",\"Available\":\"Available nationally\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b55\",\"Accreditation\":\"RSPCA Approved Indoor\",\"Rating\":\"Good\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b58\",\"Brand_Name\":\"Gami Chicken\",\"Available\":\"Available ACT, NSW, VIC and WA\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b59\",\"Accreditation\":\"RSPCA Approved Indoor\",\"Rating\":\"Good\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b5a\",\"Brand_Name\":\"Grill'd\",\"Available\":\"Available nationally\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b5b\",\"Accreditation\":\"RSPCA Approved Indoor\",\"Rating\":\"Good\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b5c\",\"Brand_Name\":\"Hazeldene's Chicken\",\"Available\":\"Available in VIC\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b5e\",\"Accreditation\":\"RSPCA Approved Indoor\",\"Rating\":\"Good\"},{\"_id\":\"5cc7da37ba39be1255db1b5d\",\"Accreditation\":\"FREPA\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b5f\",\"Brand_Name\":\"Herbert Adams\",\"Available\":\"Available nationally\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b60\",\"Accreditation\":\"RSPCA Approved Indoor\",\"Rating\":\"Good\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b61\",\"Brand_Name\":\"Nandos\",\"Available\":\"Available nationally\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b62\",\"Accreditation\":\"RSPCA Approved Indoor\",\"Rating\":\"Good\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b66\",\"Brand_Name\":\"Romeo's Chicken\",\"Available\":\"Available in SA & select NSW stores\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b67\",\"Accreditation\":\"RSPCA Approved Indoor\",\"Rating\":\"Good\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b63\",\"Brand_Name\":\"Nichols Poultry\",\"Available\":\"Available in TAS\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b65\",\"Accreditation\":\"RSPCA Approved Indoor\",\"Rating\":\"Good\"},{\"_id\":\"5cc7da37ba39be1255db1b64\",\"Accreditation\":\"PROOF\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b68\",\"Brand_Name\":\"Woolworths Chicken\",\"Available\":\"Available nationally\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b69\",\"Accreditation\":\"RSPCA Approved Indoor\",\"Rating\":\"Good\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b6a\",\"Brand_Name\":\"Zambrero\",\"Available\":\"Available nationally\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b6b\",\"Accreditation\":\"RSPCA Approved Indoor\",\"Rating\":\"Good\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b6c\",\"Brand_Name\":\"Zeus's Street Greek\",\"Available\":\"Available ACT, NSW, QLD and WA\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b6d\",\"Accreditation\":\"RSPCA Approved Indoor\",\"Rating\":\"Good\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b6e\",\"Brand_Name\":\"Coles Free Range Chicken\",\"Available\":\"Currently available in NSW & VIC\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b6f\",\"Accreditation\":\"RSPCA Approved Outdoor\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b70\",\"Brand_Name\":\"Mt Barker Free Range Chicken\",\"Available\":\"Available in WA\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b71\",\"Accreditation\":\"RSPCA Approved Outdoor\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b72\",\"Brand_Name\":\"Baiada Poultry, Lilydale Free Range\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b73\",\"Accreditation\":\"FREPA\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b74\",\"Brand_Name\":\"Canter Valley\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b75\",\"Accreditation\":\"FREPA\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b76\",\"Brand_Name\":\"Golden Cockerel Chicken\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b77\",\"Accreditation\":\"FREPA\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b78\",\"Brand_Name\":\"Inghams Enterprises\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b79\",\"Accreditation\":\"FREPA\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b7a\",\"Brand_Name\":\"Red Lea Chicken\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b7b\",\"Accreditation\":\"FREPA\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b7c\",\"Brand_Name\":\"Turi Foods, Bannockburn\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b7d\",\"Accreditation\":\"FREPA\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b7e\",\"Brand_Name\":\"Almond Grove Free Range Poultry\",\"Available\":\"Feather and Bone, NSW\\nThe Chicken Pantry Victoria Markets, VIC\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b7f\",\"Accreditation\":\"Humane Choice\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b80\",\"Brand_Name\":\"Burrawong Gaian\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b81\",\"Accreditation\":\"Humane Choice\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b82\",\"Brand_Name\":\"Walker Farm Foods\",\"Available\":\"Maleny Supa IGA, Your Organic Market, Witta Farmers Market and farm gate.\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b83\",\"Accreditation\":\"Humane Choice\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b86\",\"Brand_Name\":\"Bellamy's Organic\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b88\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"},{\"_id\":\"5cc7da37ba39be1255db1b87\",\"Accreditation\":\"NASAA, NASAA Organic\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b84\",\"Brand_Name\":\"Arcadian Organic and Natural Meat\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b85\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b89\",\"Brand_Name\":\"Bendele Farm Organic Poultry\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b8a\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b8b\",\"Brand_Name\":\"Brisband Certified Organic\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b8c\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b8d\",\"Brand_Name\":\"Challenge Meat\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b8e\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b8f\",\"Brand_Name\":\"Cherry Tree Kitchen Organics\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b90\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b91\",\"Brand_Name\":\"Cooee Foods\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b92\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b93\",\"Brand_Name\":\"Eaternal Health & Organic Foods\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b94\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b95\",\"Brand_Name\":\"Epoc Foods\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b96\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b97\",\"Brand_Name\":\"Freedom Foods Group\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b98\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b99\",\"Brand_Name\":\"Inghams Enterprises Macro\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b9a\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b9b\",\"Brand_Name\":\"Hannah's Organic\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b9c\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b9d\",\"Brand_Name\":\"Inglewood Poultry Farms Macro\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1b9e\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1b9f\",\"Brand_Name\":\"More Than Organic, Shiralee Organic\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1ba0\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1ba1\",\"Brand_Name\":\"Organic Paws Chicken\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1ba2\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1ba5\",\"Brand_Name\":\"Sherwood Road Organics\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1ba6\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1ba3\",\"Brand_Name\":\"Roots in Nature\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1ba4\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1ba7\",\"Brand_Name\":\"Simply Goodness\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1ba8\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1ba9\",\"Brand_Name\":\"The Meat-Ting Place Organics\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1baa\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1bab\",\"Brand_Name\":\"Woolworths Macro Organic\",\"Available\":\"Woolworths\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1bac\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1baf\",\"Brand_Name\":\"Black Raven Farm\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1bb0\",\"Accreditation\":\"NASAA, NASAA Organic\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1bad\",\"Brand_Name\":\"Bio Bambino\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1bae\",\"Accreditation\":\"NASAA, NASAA Organic\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1bb1\",\"Brand_Name\":\"Brith of Life\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1bb2\",\"Accreditation\":\"NASAA, NASAA Organic\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1bb5\",\"Brand_Name\":\"Buda Organic Foods\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1bb6\",\"Accreditation\":\"NASAA, NASAA Organic\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1bb3\",\"Brand_Name\":\"Bruny Island Cheese Co.\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1bb4\",\"Accreditation\":\"NASAA, NASAA Organic\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1bb7\",\"Brand_Name\":\"Captains Creek Vineyard & Winery\",\"Available\":\"Blampied VIC\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1bb8\",\"Accreditation\":\"NASAA, NASAA Organic\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1bb9\",\"Brand_Name\":\"Cherry Tree Organics\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1bba\",\"Accreditation\":\"NASAA, NASAA Organic\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1bbb\",\"Brand_Name\":\"Elderslie Organics\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1bbc\",\"Accreditation\":\"NASAA, NASAA Organic\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1bbd\",\"Brand_Name\":\"Goldfields Trading Organic Production\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1bbe\",\"Accreditation\":\"NASAA, NASAA Organic\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1bbf\",\"Brand_Name\":\"Henley Square Foodland\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1bc0\",\"Accreditation\":\"NASAA, NASAA Organic\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1bc1\",\"Brand_Name\":\"ID & JE Phillips\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1bc2\",\"Accreditation\":\"NASAA, NASAA Organic\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1bc3\",\"Brand_Name\":\"Loriendale Orchard\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1bc4\",\"Accreditation\":\"NASAA, NASAA Organic\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1bc5\",\"Brand_Name\":\"Merryville Farm\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1bc6\",\"Accreditation\":\"NASAA, NASAA Organic\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1bc7\",\"Brand_Name\":\"Mount Rufus Proprietors\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1bc8\",\"Accreditation\":\"NASAA, NASAA Organic\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1bc9\",\"Brand_Name\":\"Mountford Winery & Vineyard\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1bca\",\"Accreditation\":\"NASAA, NASAA Organic\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1bcb\",\"Brand_Name\":\"Norwood Foodland\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1bcc\",\"Accreditation\":\"NASAA, NASAA Organic\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1bcd\",\"Brand_Name\":\"O Batista\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1bce\",\"Accreditation\":\"NASAA, NASAA Organic\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1bcf\",\"Brand_Name\":\"Organic Ways\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1bd0\",\"Accreditation\":\"NASAA, NASAA Organic\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1bd1\",\"Brand_Name\":\"Payneham Vale Organics\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1bd2\",\"Accreditation\":\"NASAA, NASAA Organic\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1bd3\",\"Brand_Name\":\"Randall Organic Rice\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1bd4\",\"Accreditation\":\"NASAA, NASAA Organic\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1bd5\",\"Brand_Name\":\"Raw Pride Organics\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1bd6\",\"Accreditation\":\"NASAA, NASAA Organic\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1bd7\",\"Brand_Name\":\"RS and CM Underdown\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1bd8\",\"Accreditation\":\"NASAA, NASAA Organic\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1bd9\",\"Brand_Name\":\"Tonemade\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1bda\",\"Accreditation\":\"NASAA, NASAA Organic\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1bdb\",\"Brand_Name\":\"Urban Forager\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1bdc\",\"Accreditation\":\"NASAA, NASAA Organic\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1bdd\",\"Brand_Name\":\"Alderton's Farming\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1bde\",\"Accreditation\":\"PROOF\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1bdf\",\"Brand_Name\":\"Bellasato Farm\",\"Available\":\"Direct from the farm gate;\\nYungaburra Markets (4th Saturday of each month); or\\nSunday at Flinders (Cotters) Market Townsville (3rd Sunday of each month only)\\nFreckle Farm butchers shop - Mackay\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1be0\",\"Accreditation\":\"PROOF\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1be1\",\"Brand_Name\":\"Kinmana Organics\",\"Available\":\"Maidments Meat Service.\\nGlenelg Organic Corner Store Market, Adelaide.\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1be2\",\"Accreditation\":\"PROOF\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1be3\",\"Brand_Name\":\"Dawson valley Free Range\",\"Available\":\"Sunshine Coast Organic Meats, Sherwood Road and Morley Street Organic Meats\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1be4\",\"Accreditation\":\"PROOF\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1be5\",\"Brand_Name\":\"The Old Farm Happy Valley\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1be6\",\"Accreditation\":\"PROOF\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1be7\",\"Brand_Name\":\"Milking Yard Farm\",\"Available\":\"Hagens Organics, Meatsmith, Peter Bouchier, Vue de Monde, Bistro Vue, Lakehouse, Grossi Florentino, Park Hyatt, Wilson and Market etc.\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1be8\",\"Accreditation\":\"PROOF\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1beb\",\"Brand_Name\":\"Grassland Poultry\",\"Available\":\"local farmers market\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1bec\",\"Accreditation\":\"PROOF\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1be9\",\"Brand_Name\":\"Great Northern Poultry\",\"Available\":\"\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1bea\",\"Accreditation\":\"PROOF\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1bed\",\"Brand_Name\":\"Topi Open Range\",\"Available\":\"Blueys Beach News, Farmers Patch Forster, Go Vita Forster & Taree, Seal Rocks General Store, Smiths Lake Butchery, www.farmtofridge.com.au\",\"Category\":\"chicken\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1bee\",\"Accreditation\":\"PROOF\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1bef\",\"Brand_Name\":\"\",\"Available\":\"\",\"Category\":\"pig\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1bf5\",\"Accreditation\":\"APIQP Indoor, APIQP IN, APIQP✓\",\"Rating\":\"Avoid\"},{\"_id\":\"5cc7da37ba39be1255db1bf4\",\"Accreditation\":\"RSPCA Approved Indoor\",\"Rating\":\"Good\"},{\"_id\":\"5cc7da37ba39be1255db1bf3\",\"Accreditation\":\"Outdoor Bred, Raised Indoors on Straw, OB, APIQP✓OB1\",\"Rating\":\"Good\"},{\"_id\":\"5cc7da37ba39be1255db1bf2\",\"Accreditation\":\"APIQP Customer Specification Coles, CSC\",\"Rating\":\"Good\"},{\"_id\":\"5cc7da37ba39be1255db1bf1\",\"Accreditation\":\"APIQP Free Range, APIQP FR, APIQP✓FR\",\"Rating\":\"Best\"},{\"_id\":\"5cc7da37ba39be1255db1bf0\",\"Accreditation\":\"Humane Choice\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1bf6\",\"Brand_Name\":\"Coles Free Range\",\"Available\":\"Available nationally\",\"Category\":\"pig\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1bf7\",\"Accreditation\":\"RSPCA Approved Outdoor Free Range\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1bf8\",\"Brand_Name\":\"Gooralie Free Range\",\"Available\":\"Available in QLD\",\"Category\":\"pig\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1bf9\",\"Accreditation\":\"RSPCA Approved Outdoor Free Range\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1bfa\",\"Brand_Name\":\"Pandani Select\",\"Available\":\"Available in TAS\",\"Category\":\"pig\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1bfb\",\"Accreditation\":\"RSPCA Approved Outdoor Free Range\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1bfc\",\"Brand_Name\":\"Valenca Free Range Pork\",\"Available\":\"\",\"Category\":\"pig\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1bfd\",\"Accreditation\":\"RSPCA Approved Outdoor Free Range\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1bfe\",\"Brand_Name\":\"Grill'd\",\"Available\":\"Available nationally\",\"Category\":\"pig\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1bff\",\"Accreditation\":\"RSPCA Approved Outdoor Free Range\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1c00\",\"Brand_Name\":\"Brisbane Certified Organic\",\"Available\":\"\",\"Category\":\"pig\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1c01\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1c02\",\"Brand_Name\":\"More Than Organic\",\"Available\":\"\",\"Category\":\"pig\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1c03\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1c04\",\"Brand_Name\":\"NC Griggs & Co\",\"Available\":\"\",\"Category\":\"pig\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1c05\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1c06\",\"Brand_Name\":\"Nicholson's Farm Fresh Organic Meat\",\"Available\":\"\",\"Category\":\"pig\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1c07\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1c08\",\"Brand_Name\":\"Sherwood Road Organics\",\"Available\":\"\",\"Category\":\"pig\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1c09\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1c0a\",\"Brand_Name\":\"Zammit Ham and Bacon Curers\",\"Available\":\"\",\"Category\":\"pig\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1c0b\",\"Accreditation\":\"Australian Certified Organic, ACO\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1c0c\",\"Brand_Name\":\"Australian Natural Food Co.\",\"Available\":\"\",\"Category\":\"pig\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1c0d\",\"Accreditation\":\"NASAA, NASAA Organic\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1c0e\",\"Brand_Name\":\"Bertocchi Smallgoods\",\"Available\":\"\",\"Category\":\"pig\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1c0f\",\"Accreditation\":\"NASAA, NASAA Organic\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1c10\",\"Brand_Name\":\"Cherry Tree Organics\",\"Available\":\"\",\"Category\":\"pig\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1c11\",\"Accreditation\":\"NASAA, NASAA Organic\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1c12\",\"Brand_Name\":\"Goldfields Trading-Organic Production\",\"Available\":\"\",\"Category\":\"pig\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1c13\",\"Accreditation\":\"NASAA, NASAA Organic\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1c14\",\"Brand_Name\":\"Henley Square Foodland\",\"Available\":\"\",\"Category\":\"pig\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1c15\",\"Accreditation\":\"NASAA, NASAA Organic\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1c16\",\"Brand_Name\":\"Linley Valley Pork\",\"Available\":\"\",\"Category\":\"pig\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1c17\",\"Accreditation\":\"NASAA, NASAA Organic\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1c18\",\"Brand_Name\":\"Norwood Foodland\",\"Available\":\"\",\"Category\":\"pig\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1c19\",\"Accreditation\":\"NASAA, NASAA Organic\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1c1a\",\"Brand_Name\":\"Kelty Farm\",\"Available\":\"Woodbridge, TAS\",\"Category\":\"pig\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1c1b\",\"Accreditation\":\"Tasmanian Certified Organic, TOP\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1c1c\",\"Brand_Name\":\"Shady Waters\",\"Available\":\"\",\"Category\":\"pig\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1c1d\",\"Accreditation\":\"PROOF\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1c1e\",\"Brand_Name\":\"Summerland House Farm\",\"Available\":\"\",\"Category\":\"pig\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1c1f\",\"Accreditation\":\"PROOF\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1c20\",\"Brand_Name\":\"Forage Farms\",\"Available\":\"\",\"Category\":\"pig\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1c21\",\"Accreditation\":\"PROOF\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1c22\",\"Brand_Name\":\"Sunnyville Free Range Pastured Pigs\",\"Available\":\"Local Farmers Markets, Malanda Meat Co, Sprout Grocers\",\"Category\":\"pig\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1c23\",\"Accreditation\":\"PROOF\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1c24\",\"Brand_Name\":\"Rhodavale Pork\",\"Available\":\"*Direct from our on farm butcher shop - GYMPIE & SUNSHINE COAST\\n*Millers Gourmet Meats - TOOWOOMBA\\n*Shaws Meats - GYMPIE\\n*Maleny Butchery - MALENY\\n*Meat at Sunrise - SUNRISE BEACH\\n*Bargara Meats - BARGARA\\n*Kilkivan Meats - KILKIVAN\",\"Category\":\"pig\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1c25\",\"Accreditation\":\"PROOF\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1c26\",\"Brand_Name\":\"Piggy In The Middle\",\"Available\":\"\",\"Category\":\"pig\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1c27\",\"Accreditation\":\"PROOF\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1c28\",\"Brand_Name\":\"Goulburn Valley Pork\",\"Available\":\"\",\"Category\":\"pig\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1c29\",\"Accreditation\":\"PROOF\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1c2a\",\"Brand_Name\":\"Emperors Pork\",\"Available\":\"\",\"Category\":\"pig\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1c2b\",\"Accreditation\":\"PROOF\",\"Rating\":\"Best\"}],\"Image\":null},{\"_id\":\"5cc7da37ba39be1255db1c2c\",\"Brand_Name\":\"Australian Pastured Pork\",\"Available\":\"Carina Heights Quality Meats at Carina, QLD, Ted's Butchery at Milton, Southern NSW, The Woolly Sheep at Terranorra, NSW, Manling Meats at Crows Nest, NSW, Mary Valley Smallgoods at Cooroy, QLD, Red Bull Butchery at Grafton, NSW, West High Street Boutique Butchery at Coffs Harbour, NSW\",\"Category\":\"pig\",\"Accreditation\":[{\"_id\":\"5cc7da37ba39be1255db1c2d\",\"Accreditation\":\"PROOF\",\"Rating\":\"Best\"}],\"Image\":null}]";

        jsonString = jsonString.replace("_id","sid");
        JsonParser jsonParser = new JsonParser();
        JsonArray jsonElements = jsonParser.parse(jsonString).getAsJsonArray();

        deleteAcc();

        AccreditationHelper accreditationHelper=new AccreditationHelper(this);
//
        SQLiteDatabase database= accreditationHelper.getWritableDatabase();
        Gson gson = new Gson();
        ArrayList<Product>productArrayList = new ArrayList<>();
        for (JsonElement product:jsonElements) {
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
//        Log.d("JSON size",String.valueOf( jsonElements.size() ));
        Toast.makeText( StartUpActivity.this,"update database",Toast.LENGTH_LONG ).show();
        String a=jsonElements.get( 0 ).toString();

//        Log.d("JSON element",jsonString );
    }


    public void deleteAcc(){
        AccreditationHelper accrediationDatabase=new AccreditationHelper( this );
        SQLiteDatabase database=accrediationDatabase.getWritableDatabase();
        accrediationDatabase.deleteAll( database );
        accrediationDatabase.onCreate( database );
    }
    public Boolean checkVersionFile(){

        File fileVersion = new File(getApplicationContext().getFilesDir(), "version.txt");
        if(!(fileVersion.exists())) {
            writeToFile( "1,1" );
            Log.d( "VersionDatabase", "Creating!" );
            return true;
        }else
            Log.d("VersionDatabase", "Existing!");
        return false;
    }

    public void checkProfileFile(){

        File fileVersion = new File(getApplicationContext().getFilesDir(), "profile.txt");
        if(!(fileVersion.exists())) {
            writeProfileFile( "0;1" );
            Log.d( "profile", "Creating!" );

        }else{
            Log.d("profile", "Existing!");

        }


    }
    private void writeProfileFile(String profile)
    {
        try
        {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.openFileOutput("profile.txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(profile);
            outputStreamWriter.close();
        }
        catch (IOException e)
        {
            Log.e("Exception", "File write failed: " + e.toString());
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        Fragment startFragment=new StartFragment();
        // directly make the view to login fragment first
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, startFragment).commit();

    }


    @Override
    protected void onStart() {
        super.onStart();
        File fileVersion = new File(getApplicationContext().getFilesDir(), "profile.txt");
        if((fileVersion.exists())) {

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();

        }else{
            Log.d("profile", "Existing!");

        }
    }
}
