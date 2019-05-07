package comp5703.sydney.edu.au.kinderfoodfinder;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.Accreditation;
import comp5703.sydney.edu.au.kinderfoodfinder.StatisticDatabase.StatisticsDatabase;

public class DetailActivity extends AppCompatActivity {


    private TextView brandtv, ratetv, accreditationtv, locationtv;
    private ImageView imageView;

    private Toolbar toolbar;
    private BottomNavigationView navigation;
    String sid,date,age;
    int times;




    private TextView reporttv,availabletv, learntv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_detail );

        Intent intent = getIntent();
        String brand = intent.getStringExtra("brand");
        String type = intent.getStringExtra("type");
        String accreditation = intent.getStringExtra("accreditation");
        String rate = intent.getStringExtra("rating");
        String location = intent.getStringExtra("location");
        sid=intent.getStringExtra( "stringId" );
        final String userID=intent.getStringExtra( "userID" );
        final String gender=intent.getStringExtra( "gender" );
        final String birthday=intent.getStringExtra( "birthday" );
        GregorianCalendar cal = new GregorianCalendar();    //当前时间

        cal.setMinimalDaysInFirstWeek(7);   //第一周最少包含七天
        cal.setFirstDayOfWeek( Calendar.MONDAY);//周一为一周第一天
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());//取开始时间
        int weeks=cal.get(Calendar.WEEK_OF_YEAR);
        Calendar calendar = Calendar.getInstance();
        int year =calendar.get(Calendar.YEAR);




//        String img = intent.getStringExtra("img");

        brandtv=findViewById( R.id.dtlbrand );
        ratetv=findViewById( R.id.dtlrating );
        accreditationtv=findViewById( R.id.dtlaccname );
        locationtv=findViewById( R.id.dtllocationname );
        imageView=findViewById( R.id.imgdetail );

        brandtv.setText( brand );
        ratetv.setText( rate );
        accreditationtv.setText( accreditation );
        locationtv.setText( location );

        if(rate.equalsIgnoreCase( "BEST" )){
            ratetv.setTextColor( Color.parseColor("#208E5C"));
        }if(rate.equalsIgnoreCase( "GOOD" )){
            ratetv.setTextColor( Color.parseColor("#f7912f"));

        }if(rate.equalsIgnoreCase( "AVOID" )){
            ratetv.setTextColor( Color.parseColor("#FF4081"));

        }

//        if(type.equalsIgnoreCase( "eggs" )){
//            imageView.setImageResource( R.drawable.farm3 );
//        }else if(type.equalsIgnoreCase( "pork" )){
//            imageView.setImageResource( R.drawable.farm1 );
//
//        }else if(type.equalsIgnoreCase( "chickens" )){
//            imageView.setImageResource( R.drawable.farm2 );
//        }

        // test collect click data

        date=getDate();
        age=getAge( birthday );
        times=1;
        String count ="1";




        String info=sid+"; "+date+"; "+times+"; "+userID+"; "+gender+"; "+age+"; ";
        Log.d("statistics add record",info);



        StatisticsDatabase statisticsDatabase=new StatisticsDatabase(this);
//
        SQLiteDatabase database= statisticsDatabase.getWritableDatabase();
        statisticsDatabase.addProduct( sid,date,gender,age,count,database );
        statisticsDatabase.close();

        Log.d("statistic","one row insert");

//        new AddClickData( ).execute( sid,period,times,userid,gender,age );

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle( type );


        // tool bar listener
        toolbar.setNavigationOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });


        reporttv=findViewById( R.id.dtlreport );
        availabletv=findViewById( R.id.dtllocation );
        learntv=findViewById( R.id.dtllearn );
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
                startActivity(intent);

            }
        } );


        reporttv.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                intent.putExtra("id",2);
                startActivity(intent);

            }
        } );

        learntv.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                intent.putExtra("id",3);
                startActivity(intent);

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

            StatisticsDatabase statisticsDatabase=new StatisticsDatabase( context);

            SQLiteDatabase database= statisticsDatabase.getWritableDatabase();
            int t=0;
            String sid=strings[0];
            String date=strings[1];
            String gender=strings[2];
            String age=strings[3];
            String count=strings[5];


            statisticsDatabase.addProduct( sid,date,gender,age,count,database );




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

    public void backClick(View view) {
        finish();
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


        Log.d("date",birth.toString()+"；  " +String.valueOf( age ));


        return result;
    }
}
