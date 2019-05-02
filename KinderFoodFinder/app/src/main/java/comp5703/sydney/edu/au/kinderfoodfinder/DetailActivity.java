package comp5703.sydney.edu.au.kinderfoodfinder;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    private TextView brandtv, ratetv, accreditationtv, locationtv;
    private ImageView imageView;

    private Toolbar toolbar;
    private BottomNavigationView navigation;



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

    public void backClick(View view) {
        finish();
    }
}
