package comp5703.sydney.edu.au.kinderfoodfinder;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
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
import java.nio.charset.Charset;
import java.util.ArrayList;

import comp5703.sydney.edu.au.kinderfoodfinder.Database.BackgroundTask;
import comp5703.sydney.edu.au.kinderfoodfinder.Database.ProductContract;
import comp5703.sydney.edu.au.kinderfoodfinder.Database.ProductDatabase;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.Accreditation;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.AccreditationHelper;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.DaoUnit;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.MyApplication;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.Product;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.StoreHelper;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.StoreInfo;
import comp5703.sydney.edu.au.kinderfoodfinder.StatisticDatabase.StatisticContract;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;
import static com.facebook.FacebookSdk.getApplicationContext;

public class HomeFragment extends Fragment
{
    // defined variables

    private BottomNavigationView navigation;
    private Button browse;
    private SearchView searchView;
    private ArrayList<Items> itemsArrayList=new ArrayList<Items>(  );

    private TextView textView;
    private TextView helptv;
    private Button btn_submit,refresh;
    String jsonString;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        int textSize1 = getResources().getDimensionPixelSize(R.dimen.text_size_1);
        int textSize2 = getResources().getDimensionPixelSize(R.dimen.text_size_2);
        navigation = (BottomNavigationView) getActivity().findViewById(R.id.navigation);
        textView=view.findViewById( R.id.kff_tv );
        helptv=view.findViewById( R.id.help_tv );
        String a="Welcome to \n";
        String b="Finder Food Kinder";
        SpannableString span1=new SpannableString( a );
        span1.setSpan( new AbsoluteSizeSpan(textSize1),0,a.length(),SPAN_INCLUSIVE_INCLUSIVE );
//        span1.setSpan( new ForegroundColorSpan( Color.BLUE) ,0,5,0);
        SpannableString span2=new SpannableString( b );
        span2.setSpan( new AbsoluteSizeSpan( textSize2 ),0, b.length(),SPAN_INCLUSIVE_INCLUSIVE);
        helptv.getPaint().setFlags( Paint.FAKE_BOLD_TEXT_FLAG);
        helptv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        textView.setText( span1);
        textView.append( span2 );

        refresh=view.findViewById( R.id.update );

        refresh.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBrandDatabase();
            }
        } );




        return view;
    }

    public void BrowseClick(View view){

    }


    private void checkBrandDatabase( )
    {// modify the user data to the server
        String url;
        String ipAddress = "172.20.10.4";  //100.101.72.250 Here should be changed to your server IP
        url = "http://" + StatisticContract.StatisticEntry.IP_Address + ":3000/android-app-check-version-brand-store";
        // send the data to the server
        RequestQueue ExampleRequestQueue = Volley.newRequestQueue(getActivity());
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
                    serverstore=Integer.parseInt( result[1] );
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
                    Toast.makeText(getActivity(), "Updating database!, Please wai...", Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(getActivity(), "Already up to date", Toast.LENGTH_SHORT).show();

                }
            }
        },
                new Response.ErrorListener()  //Create an error listener to handle errors appropriately.
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        //This code is executed if there is an error.

                        Toast.makeText(getActivity(), "Update database!", Toast.LENGTH_SHORT).show();

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
        { InputStream inputStream = getActivity().openFileInput("version.txt");

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
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(getActivity().openFileOutput("version.txt", Context.MODE_PRIVATE));
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


    public void checkDatabase(){
        ProductDatabase productDatabase=new ProductDatabase( getActivity() );
        SQLiteDatabase database=productDatabase.getReadableDatabase();
        SQLiteDatabase writableDatabase=productDatabase.getWritableDatabase();

        Cursor cursor = database.query( ProductContract.ProductEntry.TABLE_NAME,null,null,null,null,null,null );
        if(cursor.moveToFirst()){
            Log.d("Database "," already has information");

        }else{
            Log.d("Database "," Please insert data");


            readEggData();
            readChickenData();
            readporkData();
            for(Items items:itemsArrayList){

                String brand =items.getBrand();
                String acc=items.getAccreditation();
                String rating=items.getRating();
                String available =items.getAvailable();
                String category=items.getType();
                BackgroundTask backgroundTask=new BackgroundTask( getActivity() );

                backgroundTask.execute( "add_info",  brand,acc,rating,available,category );

            }

            Log.d( "Database itemlist",String.valueOf( itemsArrayList.size() ) );




            //            productDatabase.addProduct( "brand","acc","rating","location","category",writableDatabase );
        }
        productDatabase.close();


    }


    private void loadData() {
        // show the progress dialog until the login validation is complete


        // set up
        String ipAddress = "192.168.20.27";  //100.101.72.250 Here should be changed to your server IP
        String url = "http://" + ipAddress + ":3000/GetAllBrand";

        // send the request to the server for checking user login info
        RequestQueue ExampleRequestQueue = Volley.newRequestQueue(getActivity());
        StringRequest ExampleStringRequest = new StringRequest( Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                //The String 'response' contains the server's response.
                //You can test it by printing response.substring(0,500) to the screen.

                if (response.equals("Yes")) {

                    Log.d("Database", "get database from the server");
                } else {
                    Toast.makeText(getActivity(), "Wrong email or password!", Toast.LENGTH_SHORT).show();

                    Log.d("Database", "Fail to get database from the server");

                }
            }
        },
                new Response.ErrorListener()  //Create an error listener to handle errors appropriately.
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //This code is executed if there is an error.
                        Log.d("Database", error.toString());
                    }
                });
        ExampleRequestQueue.add(ExampleStringRequest);
    }


    // test the function
    public void readEggData(){

        InputStream is=getResources().openRawResource(R.raw.eggs);
        BufferedReader reader= new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );
        String line="";
        try{
            int i=0;
            while ((line=reader.readLine())!= null){
                Items items=new Items(  );

                ProductDatabase productDatabase=new ProductDatabase( getActivity() );

                String[] token= line.split(",(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)",-1);

                i++;

                if(token.length==4){
                    Log.d("lines :",String.valueOf( i )+line);
                    String brand =token[1];
                    String acc=token[0];
                    String rating=token[2];
                    String available =token[3];
                    String category="eggs";
                    items.setType( category );
                    items.setBrand( brand );
                    items.setAccreditation( acc );
                    items.setRating( rating );
                    items.setAvailable( available );
                    itemsArrayList.add( items );
//
                }
                else {
//
                }
                productDatabase.close();

            }


        } catch (IOException e) {

            e.printStackTrace();
        }


    }

    public void readChickenData(){
        Items items;
//        itemsArrayList=new ArrayList<Items>(  );
        InputStream is=getResources().openRawResource(R.raw.chickens);
        BufferedReader reader= new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );
        String line="";
        try{
            int i=0;
            while ((line=reader.readLine())!= null){

                items=new Items(  );
                ProductDatabase productDatabase=new ProductDatabase( getActivity() );

                String[] token= line.split(",(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)",-1);

                Log.d("length :",String.valueOf( i )+"$$$"+String.valueOf( token.length ));

                i++;
                Log.d("read",String.valueOf( token.length ));
                if(token.length==4){
                    Log.d("lines :",String.valueOf( i )+line);
                    String brand =token[1];
                    String acc=token[0];
                    String rating=token[2];
                    String available =token[3];
                    String category="chickens";
                    items.setType( category );
                    items.setBrand( brand );
                    items.setAccreditation( acc );
                    items.setRating( rating );
                    items.setAvailable( available );
                    itemsArrayList.add( items );
//
                }
                else {
//
                }

                productDatabase.close();
            }

        } catch (IOException e) {

            e.printStackTrace();
        }


    }

    public void readporkData(){
        Items items;

        InputStream is=getResources().openRawResource(R.raw.pigs);
        BufferedReader reader= new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );
        String line="";
        try{
            int i=0;
            while ((line=reader.readLine())!= null){

                items=new Items(  );
                ProductDatabase productDatabase=new ProductDatabase( getActivity() );

                String[] token= line.split(",(?=(?:[^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)",-1);

                Log.d("length :",String.valueOf( i )+"$$$"+String.valueOf( token.length ));
                items=new Items();
                i++;
                Log.d("read",String.valueOf( token.length ));
                if(token.length==4){
                    Log.d("lines :",String.valueOf( i )+line);
                    items.setType( "Pork" );


                    String brand =token[1];
                    String acc=token[0];
                    String rating=token[2];
                    String available =token[3];
                    String category="eggs";
//
                    items.setType( category );
                    items.setBrand( brand );
                    items.setAccreditation( acc );
                    items.setRating( rating );
                    items.setAvailable( available );
                    itemsArrayList.add( items );
                }
                else {
//
                }

                productDatabase.close();
            }


        } catch (IOException e) {

            e.printStackTrace();
        }


    }


    private void deleteProduct(int id){

        ProductDatabase productDatabase= new ProductDatabase(getActivity());
        SQLiteDatabase database=productDatabase.getWritableDatabase();

        productDatabase.deleteContact( id,database );
        productDatabase.close();
        Toast.makeText( getActivity(),"Contact Removed Successfully",Toast.LENGTH_LONG ).show();
    }


    public void getItem(int i){

        Items items=itemsArrayList.get( i );
//
        String brand =items.getBrand();
        String acc=items.getAccreditation();
        String rating=items.getRating();
        String available =items.getAvailable();
        String category=items.getType();
        BackgroundTask backgroundTask=new BackgroundTask( getActivity() );

        backgroundTask.execute( "add_info",  brand,acc,rating,available,category );

    }


    public String itemtoString(int i){
        Items items=itemsArrayList.get( i );
//
        String brand =items.getBrand();
        String acc=items.getAccreditation();
        String rating=items.getRating();
        String available =items.getAvailable();
        String category=items.getType();

        return String.valueOf( i )+"; "+brand+"; "+acc+"; "+rating+"; "+available+"; "+category;
    }

}
