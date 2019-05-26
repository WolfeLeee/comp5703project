package comp5703.sydney.edu.au.kinderfoodfinder;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.AccEntity;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.Accreditation;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.AccreditationHelper;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.Contract;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.DaoUnit;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.MyApplication;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.Product;
import comp5703.sydney.edu.au.kinderfoodfinder.StatisticDatabase.StatisticContract;
import comp5703.sydney.edu.au.kinderfoodfinder.StatisticDatabase.StatisticsDatabase;

public class DetailActivity extends AppCompatActivity {



    private ImageView imageView;

    private Toolbar toolbar;
    private BottomNavigationView navigation;
    private  String sid,date,age,page,accId;
    private int times;
    private TextView brand_info, rate_info, accreditationtv, location_info;
    private TextView reporttv,availabletv, learntv;
    private ListView listView;
    ArrayList<AccEntity> accreditationList;
    Context ctx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_detail );

        Intent intent = getIntent();
        //
        sid=intent.getStringExtra( "stringId" );
        page=intent.getStringExtra( "page");
        accId=intent.getStringExtra( "accid" );

//
//        final String userID=intent.getStringExtra( "userID" );
//        final String gender=intent.getStringExtra( "gender" );
//        final String birthday=intent.getStringExtra( "birthday" );

        brand_info=findViewById( R.id.dtlbrand );
        location_info=findViewById( R.id.location_info );
        imageView=findViewById( R.id.imgdetail );
        reporttv=findViewById( R.id.report );
        availabletv=findViewById( R.id.location );
        learntv=findViewById( R.id.learnmore );
        listView=findViewById( R.id.detail_listview );
        Product product= DaoUnit.getInstance().searchBySid( sid );

        final String brandname=product.getBrand_Name();
        final String category=product.getCategory();
        String available=product.getAvailable();
        String image=product.getImage();

        accreditationList = readAccreditation( sid );
        AccreditationAdapter accreditationAdapter=new AccreditationAdapter( this,accreditationList );
        listView.setAdapter( accreditationAdapter );
//        Utility.setListViewHeightBasedOnChildren( listView );

        Log.d("detail",String.valueOf( accreditationList.size() ));


        image="https://encrypted-tbn2.gstatic.com/shopping?q=tbn:ANd9GcQ8A0C9JxBqP_M27oZ8oF2PXG9y1hqZy_hjsIcOKHeGdKaj6N_rDrJfWZ9UmdNaTRSpFzKcqHeAeJz8cojEEU0HeqNvYzf6&usqp=CAc";

        //for test load image

        String url = "http://" + StatisticContract.StatisticEntry.IP_Address + ":3000/uploads/"+ product.getImage()+".jpg";

        if(product.getImage()!=null){
            Picasso.with( this ).load( url ).into( imageView );
        }
//        String img = intent.getStringExtra("img");


        brand_info.setText( brandname );
        location_info.setText( available );


//        // test collect click data
//        String[] result=readFromFile().split( ";" );
//        String[] profile= result[1].split( "," );
//        String userID="1";
//        String gender=profile[0];
//        String birthday=profile[1];
//        date=getDate();
//        age=getAge( birthday );
//        times=1;
//        String count ="1";
//        String info=sid+"; "+date+"; "+times+"; "+userID+"; "+gender+"; "+age+"; ";
//        Log.d("statistics add record",info);
//       StatisticsDatabase statisticsDatabase=new StatisticsDatabase(this);
//        SQLiteDatabase database= statisticsDatabase.getWritableDatabase();
//        statisticsDatabase.addProduct( sid,date,gender,age,count,database );
//        statisticsDatabase.close();
//
//        Log.d("statistic","one row insert");


//        new AddClickData( ).execute( sid,period,times,userid,gender,age );

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle( category );

        new AddClickData().execute( sid,readFromFile() );

        // tool bar listener
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if(page.equalsIgnoreCase( "browse" )){

                    Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                    intent.putExtra("id",5);
                    startActivity(intent);
                }
//                else {
//                    finish();
////                    Intent intent = new Intent(DetailActivity.this, MainActivity.class);
////                    intent.putExtra("id",5);
////                    startActivity(intent);
//                }
//                toolbar.setVisibility( View.GONE );
//                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//                getSupportActionBar().setTitle( "Back" );
//                toolbar.setVisibility( View.INVISIBLE );
//                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//                getSupportActionBar().setTitle( "Back" );
                finish();
            }
        });

        reporttv.getPaint().setFlags( Paint.FAKE_BOLD_TEXT_FLAG);
        availabletv.getPaint().setFlags(Paint.FAKE_BOLD_TEXT_FLAG);
        learntv.getPaint().setFlags(Paint.FAKE_BOLD_TEXT_FLAG);
        reporttv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        availabletv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        learntv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        availabletv.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                intent.putExtra("id",1);
                intent.putExtra("LOCATE", brandname);
                startActivity(intent);
                toolbar.setVisibility( View.GONE );
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle( "Back" );
                finish();


            }
        } );


        reporttv.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                intent.putExtra("id",2);
                intent.putExtra( "sid",sid );
                intent.putExtra("key",1);
                startActivity(intent);

                finish();

            }
        } );

        learntv.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbar.setVisibility( View.INVISIBLE );
                toolbar.setVisibility( View.GONE );
                Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                intent.putExtra("id",3);
                startActivity(intent);
                finish();

            }
        } );



    }


    private class AddClickData extends AsyncTask<String, String, String> {
        Context context;
//
//        public AddClickData(Context context) {
//            this.context=context;
//        }

        @Override
        protected String doInBackground(String... strings) {

            String sid=strings[0];
            String[] result=strings[1].split( ";" );
            String[] profile= result[1].split( "," );
            String userID="1";
            String gender="";
            String birthday="";
            if(profile.length>2){
                 gender=profile[0];
                 birthday=profile[1];
            }

            String date=getDate();
            String age=getAge( birthday );
            int times=1;
            String count ="1";
            String info=sid+"; "+date+"; "+times+"; "+userID+"; "+gender+"; "+age+"; ";
            Log.d("statistics add record",info);



            StatisticsDatabase statisticsDatabase=new StatisticsDatabase(getApplicationContext());
//
            SQLiteDatabase database= statisticsDatabase.getWritableDatabase();
            statisticsDatabase.addProduct( sid,date,gender,age,count,database );
            statisticsDatabase.close();

            return "One Row Insert";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate( values );
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("statistic",s);
        }


    }


    public String getDate(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date d=new Date();

        String year=String.valueOf( calendar.get( Calendar.YEAR ) );
        String month=String.valueOf( calendar.get( Calendar.MONTH ) +1);
        String day=String.valueOf( calendar.get( Calendar.DAY_OF_MONTH ) );
        String date= sdf.format( d );
        return date;
    }

    public String getAge(String birthday)  {
        String result="";
        String string = birthday;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        int age=0;
//        Log.d("brithday" ,birthday);

        if(string!=null){
            Date birth=new Date(  );
            try {
                    birth=sdf.parse( string );
                    Date d=new Date();

                    if(birth.getDay()>d.getDay()&& birth.getMonth()>d.getMonth()){
                        age=d.getYear()-birth.getYear()-1;
                    }else {
                        age=d.getYear()-birth.getYear();
                    }


            } catch (ParseException e) {
                result=e.toString();
                e.printStackTrace();
                return "Not Disclose";
            }
        }
        if(age<18){
            result="Under 18 years";
        }else if(age>=18&&age<30){
            result="18-29 years";
        }else if(age>=30&&age<40){
            result="30-39 years";
        }else if(age>=40&&age<50){
            result="40-49 years";
        }else if(age>=50&&age<60){
            result="50-59 years";
        }else {
            result="60+ years";
        }


//        Log.d("date",birth.toString()+"；  " +String.valueOf( age ));
        return result;
    }

    private ArrayList<AccEntity> readAccreditation(String pid){
        AccreditationHelper accreditationHelper=new AccreditationHelper( this );
        SQLiteDatabase database=accreditationHelper.getReadableDatabase();

        Cursor cursor=accreditationHelper.searchbyPID(pid, database );

        ArrayList<AccEntity> accList=new ArrayList<>(  );
        if(cursor!=null){
            Log.d("Database "," already has information");
            String info ="";
            while (cursor.moveToNext()){

                String sid =cursor.getString(  cursor.getColumnIndex( Contract.sid));
                String parentid=cursor.getString( cursor.getColumnIndex( Contract.ParentId ));
                String acc=cursor.getString( cursor.getColumnIndex( Contract.Accreditation) );
                String rating=cursor.getString( cursor.getColumnIndex( Contract.Rating ) );

                AccEntity accreditationEntity=new AccEntity( );
                accreditationEntity.setSid( sid );
                accreditationEntity.setParentId( parentid );
                accreditationEntity.setAccreditation( acc );
                accreditationEntity.setRating( rating );
                accList.add( accreditationEntity );

            }

        }
        accreditationHelper.close();
        return accList;
    }


    private String readFromFile()
    {
        String ret = "";
        try
        {
            InputStream inputStream = this.openFileInput("profile.txt");

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


}
