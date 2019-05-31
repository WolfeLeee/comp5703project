package comp5703.sydney.edu.au.kinderfoodfinder;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.AccEntity;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.AccreditationHelper;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.Contract;
import comp5703.sydney.edu.au.kinderfoodfinder.ProductDatabase.Product;
import comp5703.sydney.edu.au.kinderfoodfinder.StatisticDatabase.StatisticContract;

public class BrandRecyclerAdapter extends RecyclerView.Adapter<BrandRecyclerAdapter.BrandViewHolder> implements Filterable {

    private ArrayList<Product> productsList;
    private Context context;
    private ArrayList<Product> filterList=new ArrayList<>(  );
    private CustomFilter filter;

    // set variables into the constructor
    public BrandRecyclerAdapter(Context context,ArrayList<Product> products){
        this.context=context;
        this.productsList=products;
        this.filterList=products;
    }
    @NonNull
    @Override
    // inflates the item layout from xml
    public BrandViewHolder onCreateViewHolder( ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from( viewGroup.getContext() ).inflate( R.layout.recycler_brand, viewGroup,false );
        BrandViewHolder brandViewHolder=new BrandViewHolder( view,context,productsList );
        return brandViewHolder;
    }


    @Override
    // binds the data to the viewHolder in each row
    public void onBindViewHolder(BrandViewHolder viewHolder, int i) {
        int best_count=0;
        int good_count=0;
        int avoid_count=0;
        String sid=productsList.get( i ).getSid();

        Product product=productsList.get( i );
        viewHolder.brandImage.setTag(productsList.get(i));

        List<AccEntity> accreditationList= readAccreditation( sid );
        viewHolder.brand.setText(productsList.get(i).getBrand_Name());
        for(AccEntity acc:accreditationList){
            if(acc.getRating().equalsIgnoreCase( "best" )){
                best_count++;
            }else if(acc.getRating().equalsIgnoreCase( "good" )){
                good_count++;
            }else if(acc.getRating().equalsIgnoreCase( "avoid" )) {
                avoid_count++;
            }
        }
        String image=product.getImage();
        String url = "http://" + StatisticContract.StatisticEntry.IP_Address + ":3000/uploads/"+ image+".jpg";
//        if(image!=null){
////            Picasso.with(viewHolder.brandImage.getContext()).cancelRequest(viewHolder.brandImage);
//
//
//            Picasso.with(viewHolder.brandImage.getContext()).load(url).into(viewHolder.brandImage);
//
////            Picasso.with( context ).load( url ).into( viewHolder.brandImage );
//        }

        if (productsList.get(i).getImage()!=null) {
            Picasso.with(viewHolder.brandImage.getContext()).load(url).into(viewHolder.brandImage);

        } else {
            viewHolder.brandImage.setImageResource(R.drawable.logo);
        }
        viewHolder.best.setText( "Best: "+String.valueOf( best_count ) );
        viewHolder.good.setText( "Good: "+String.valueOf( good_count ) );
        viewHolder.avoid.setText( "Avoid: "+String.valueOf( avoid_count ) );

    }

    @Override
    // total number of rows in the recycler view
    public int getItemCount() {
        return productsList.size();
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

    // stores and recycles views as they are scrolled off screen
    public static class BrandViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView brand;
        TextView best ;
        TextView good;
        TextView avoid;
        ImageView brandImage;
        Context context;
        ArrayList<Product> productArrayList=new ArrayList<>(  );
        // set variables in to constructor and find views id
        public BrandViewHolder( View itemView,Context context,ArrayList<Product> productArrayList) {
            super( itemView );
            this.productArrayList=productArrayList;
            this.context=context;
            itemView.setOnClickListener( this );
             brand = itemView.findViewById(R.id.brandneme);
             best =itemView.findViewById( R.id.best );
             good=itemView.findViewById( R.id.good );
             avoid=itemView.findViewById( R.id.avoid );
             brandImage = itemView.findViewById( R.id.brand_image );
        }

        @Override
        // set on item click and go to the detail page
        public void onClick(View v) {
            int position=getAdapterPosition();
            Product product=this.productArrayList.get( position );
            Intent intent = new Intent(context, DetailActivity.class);
            String accId=product.getAccreditation().get( 0 ).getSid();
            if (intent != null) {
                        intent.putExtra( "stringId", product.getSid() );
                        intent.putExtra( "page", "search" );
                        intent.putExtra( "accid", accId );
                        this.context.startActivity( intent );

            }

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
