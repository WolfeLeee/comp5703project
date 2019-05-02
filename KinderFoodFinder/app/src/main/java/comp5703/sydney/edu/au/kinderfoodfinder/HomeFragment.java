package comp5703.sydney.edu.au.kinderfoodfinder;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;

import comp5703.sydney.edu.au.kinderfoodfinder.Database.BackgroundTask;
import comp5703.sydney.edu.au.kinderfoodfinder.Database.ProductContract;
import comp5703.sydney.edu.au.kinderfoodfinder.Database.ProductDatabase;

public class HomeFragment extends Fragment
{
    // defined variables

    private BottomNavigationView navigation;
    private Button browse;
    private SearchView searchView;
    private ArrayList<Items> itemsArrayList=new ArrayList<Items>(  );


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        navigation = (BottomNavigationView) getActivity().findViewById(R.id.navigation);

        browse=view.findViewById( R.id.category_home );
        searchView=view.findViewById( R.id.search_home );

        browse.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigation.setSelectedItemId( R.id.navigation_browse );
            }
        } );

        searchView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigation.setSelectedItemId( R.id.navigation_search );
            }
        } );


//



        BackgroundTask backgroundTask1=new BackgroundTask( getActivity() );
        backgroundTask1.execute( "read_info");


//        Log.d( "Database itemlist",String.valueOf( itemsArrayList.size() ) );
//        ProductDatabase productDatabase=new ProductDatabase( getActivity() );
//        SQLiteDatabase database1=productDatabase.getWritableDatabase();
////        int i=38;
////
////        productDatabase.addProduct( itemsArrayList.get( i ).getBrand(),itemsArrayList.get( i ).getAccreditation(),itemsArrayList.get( i ).getRating(),
////                itemsArrayList.get( i ).getAvailable(),itemsArrayList.get( i ).getType(),database1);
//
//                    productDatabase.addProduct( "brand","acc","rating","location","category",database1 );
//
//        productDatabase.close();

        checkDatabase();



        return view;
    }

    public void BrowseClick(View view){

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
