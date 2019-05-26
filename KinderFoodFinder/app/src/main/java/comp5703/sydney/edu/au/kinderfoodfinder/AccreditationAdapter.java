package comp5703.sydney.edu.au.kinderfoodfinder;





import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.Image;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.AccEntity;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.Accreditation;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.AccreditationHelper;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.Contract;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.DaoUnit;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.Product;

import static android.text.Spanned.SPAN_INCLUSIVE_INCLUSIVE;

public class AccreditationAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private ArrayList<Product> itemsList;
    private ArrayList<AccEntity> filterList;
    private ArrayList<AccEntity> accList;
    CustomFilter filter;

    public AccreditationAdapter (Context context, ArrayList<AccEntity> productsList){
        this.context = context;
        this.accList = productsList;
        this.filterList = productsList;
    }

    @Override
    public int getCount() {
        return accList.size();
    }

    @Override
    public Object getItem(int position) {
        return accList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return accList.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.listview_accreditation, null);
        }
        TextView acctv=convertView.findViewById( R.id.accreditation );
        String rate=accList.get( position ).getRating();
//        ratetv.setText( rate );
//        acctv.setText( accList.get( position ).getAccreditation() );
//        if(rate.equalsIgnoreCase( "BEST" )){
//            ratetv.setTextColor( Color.parseColor("#208E5C"));
//        }if(rate.equalsIgnoreCase( "GOOD" )){
//            ratetv.setTextColor( Color.parseColor("#f7912f"));
//
//        }if(rate.equalsIgnoreCase( "AVOID" )){
//            ratetv.setTextColor( Color.parseColor("#FF4081"));
//
//        }

        String acc=accList.get( position ).getAccreditation();
        String a =" is a ";
        String c=" choice, ";
        String best="\ncongratulations!";
        String good="well done but try to buy in moderation.";
        String avoid_a="We suggest you ";
        String avoid_b=" choice, if you can.";

        SpannableString span1=new SpannableString( a );
        SpannableString span2=new SpannableString( rate );
        SpannableString span3=new SpannableString( c );
        SpannableString accspan=new SpannableString( acc );


        int textSize3 =convertView.getResources().getDimensionPixelSize( R.dimen.text_size_4 );
        int textSize1 = convertView.getResources().getDimensionPixelSize(R.dimen.text_size_3);
        accspan.setSpan( new AbsoluteSizeSpan(textSize3),0,acc.length(),SPAN_INCLUSIVE_INCLUSIVE );

        span1.setSpan( new AbsoluteSizeSpan(textSize3),0,a.length(),SPAN_INCLUSIVE_INCLUSIVE );
        span2.setSpan( new AbsoluteSizeSpan( textSize1 ),0,rate.length(),SPAN_INCLUSIVE_INCLUSIVE );
        span3.setSpan( new AbsoluteSizeSpan(textSize3),0,c.length(),SPAN_INCLUSIVE_INCLUSIVE );

        if(rate.equalsIgnoreCase( "Best" )){

            span2.setSpan( new ForegroundColorSpan(Color.parseColor( "#208E5C" )) ,0,rate.length(),0);

            SpannableString span4=new SpannableString( best );
            span4.setSpan( new AbsoluteSizeSpan(textSize3),0,best.length(),SPAN_INCLUSIVE_INCLUSIVE );
            acctv.setText( accspan );
            acctv.append( span1 );
            acctv.append( span2 );
            acctv.append( span3 );
            acctv.append( span4 );
        }else if(rate.equalsIgnoreCase( "Good" )){
            span2.setSpan( new ForegroundColorSpan( Color.parseColor( "#f7912f" ) ) ,0,rate.length(),0);

            SpannableString span4=new SpannableString( good );
            span4.setSpan( new AbsoluteSizeSpan(textSize3),0,good.length(),SPAN_INCLUSIVE_INCLUSIVE );
            acctv.setText( accspan );

            acctv.append( span1 );
            acctv.append( span2 );
            acctv.append( span3 );
            acctv.append( span4 );

        }else if(rate.equalsIgnoreCase( "Avoid" )){
            span2.setSpan( new ForegroundColorSpan( Color.parseColor( "#FF4081" ) ) ,0,rate.length(),0);

            span1=new SpannableString( avoid_a );
            span1.setSpan( new AbsoluteSizeSpan(textSize3),0,avoid_a.length(),SPAN_INCLUSIVE_INCLUSIVE );
            span3=new SpannableString( avoid_b );
            span3.setSpan( new AbsoluteSizeSpan(textSize3),0,avoid_b.length(),SPAN_INCLUSIVE_INCLUSIVE );
            acctv.setText( span1 );
            acctv.append( span2 );
            acctv.append( " " );
            acctv.append( accspan );
            acctv.append( span3 );
        }else {
            acctv.setText( "" );
        }
        return convertView;
    }

    @Override
    public Filter getFilter() {

        if(filter == null ){
            filter = new CustomFilter();
        }
        return filter;
    }

    class CustomFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if(constraint != null && constraint.length()>0){

            }else{
                results.count = filterList.size();
                results.values = filterList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {


            notifyDataSetChanged();
        }
    }

    private ArrayList<AccEntity> readAccreditation(String pid){
        AccreditationHelper accreditationHelper=new AccreditationHelper( context );
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


//            info=info+"\n\n"+"ID :"+id+"\nName :"+name+"\nEmail :"+email

            }

        }
        accreditationHelper.close();
        return accList;
    }

}

