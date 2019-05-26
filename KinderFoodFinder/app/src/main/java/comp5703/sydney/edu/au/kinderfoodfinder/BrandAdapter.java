package comp5703.sydney.edu.au.kinderfoodfinder;





import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.AccEntity;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.Accreditation;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.AccreditationHelper;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.Contract;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.DaoUnit;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.Product;
import comp5703.sydney.edu.au.kinderfoodfinder.StatisticDatabase.StatisticContract;

public class BrandAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private ArrayList<Product> itemsList;
    private ArrayList<Product> filterList;
    private ArrayList<Product> productsList;
    CustomFilter filter;

    public BrandAdapter (Context context, ArrayList<Product> productsList){
        this.context = context;
        this.productsList = productsList;
        this.filterList = productsList;
    }

    @Override
    public int getCount() {
        return productsList.size();
    }

    @Override
    public Object getItem(int position) {
        return productsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return productsList.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.listview_brand, null);
        }
        TextView brand = convertView.findViewById(R.id.brandneme);
        TextView best =convertView.findViewById( R.id.best );
        TextView good=convertView.findViewById( R.id.good );
        TextView avoid=convertView.findViewById( R.id.avoid );
        ImageView brangImage = convertView.findViewById( R.id.brand_image );

        int best_count=0;
        int good_count=0;
        int avoid_count=0;

//        List<Accreditation> accreditationList=productsList.get( position ).getAccreditation();

        String sid=productsList.get( position ).getSid();
        List<AccEntity> accreditationList= readAccreditation( sid );
        brand.setText(productsList.get(position).getBrand_Name());
        for(AccEntity acc:accreditationList){
            if(acc.getRating().equalsIgnoreCase( "best" )){
                best_count++;
            }else if(acc.getRating().equalsIgnoreCase( "good" )){
                good_count++;
            }else if(acc.getRating().equalsIgnoreCase( "avoid" )) {
                avoid_count++;
            }
        }
        best.setText( "Best: "+String.valueOf( best_count ) );
        good.setText( "Good: "+String.valueOf( good_count ) );
        avoid.setText( "Avoid: "+String.valueOf( avoid_count ) );
        String image=productsList.get(position).getImage();

        String url = "http://" + StatisticContract.StatisticEntry.IP_Address + ":3000/uploads/"+ image+".jpg";

        if(image!=null){
            Picasso.with( context ).load( url ).into( brangImage );
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
                constraint = constraint.toString().toUpperCase();

                ArrayList<Product> filters = new ArrayList<>();

                for(int i=0; i<filterList.size();i++){
                    if(filterList.get(i).getBrand_Name().toUpperCase().startsWith( (String) constraint )){
                        Product p = filterList.get(i);
                        filters.add(p);
                    }
                }

                results.count = filters.size();
                results.values = filters;
            }else{
                results.count = filterList.size();
                results.values = filterList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            productsList = (ArrayList<Product>) results.values;
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

