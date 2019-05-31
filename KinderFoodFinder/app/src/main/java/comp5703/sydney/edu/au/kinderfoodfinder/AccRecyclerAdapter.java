package comp5703.sydney.edu.au.kinderfoodfinder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

public class AccRecyclerAdapter extends RecyclerView.Adapter<AccRecyclerAdapter.AccViewHolder> implements Filterable {
    private Context context;
    private ArrayList<Items> itemsList;
    private ArrayList<Items> filterList;
    private CustomFilter filter;
    // set variables into the constructor
    public AccRecyclerAdapter(Context context,ArrayList<Items> items){
        this.context=context;
        this.itemsList=items;
        this.filterList = itemsList;
    }
    // inflates the item layout from xml
    @NonNull
    @Override
    public AccViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from( viewGroup.getContext() ).inflate( R.layout.recycler_accreditation, viewGroup,false );
        AccViewHolder accViewHolder=new AccViewHolder( view,context,itemsList );
        return accViewHolder;
    }

    // binds the data to the viewHolder in each row
    @Override
    public void onBindViewHolder(AccViewHolder viewHolder, int position) {
        // set view data
        viewHolder.brand.setText(itemsList.get(position).getBrand());
        viewHolder.accreditation.setText( itemsList.get( position ).getAccreditation() );
        String rate=itemsList.get( position ).getRating();
        viewHolder.rating.setText( itemsList.get( position ).getRating() );
        viewHolder.type.setText( itemsList.get( position ).getType() );

        if(rate.equalsIgnoreCase( "BEST" )){
            viewHolder.rating.setTextColor( Color.parseColor("#208E5C"));
        }if(rate.equalsIgnoreCase( "GOOD" )){
            viewHolder.rating.setTextColor( Color.parseColor("#f7912f"));

        }if(rate.equalsIgnoreCase( "AVOID" )){
            viewHolder.rating.setTextColor( Color.parseColor("#FF4081"));

        }
    }
    // total number of rows in the recycler view
    @Override
    public int getItemCount() {
        return itemsList.size();
    }


    // stores and recycles views as they are scrolled off screen
    public static class AccViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView brand ;
        TextView accreditation;
        TextView rating;
        TextView type;
        Context context;
        ArrayList<Items> itemsArrayList=new ArrayList<>(  );
        // set variables in to constructor and find views id
        public AccViewHolder( View itemView,Context context,ArrayList<Items> items) {
            super( itemView );
            this.context=context;
            this.itemsArrayList=items;
            itemView.setOnClickListener( this );
            brand = itemView.findViewById(R.id.brand);
             accreditation =itemView.findViewById( R.id.accreditation );
             rating=itemView.findViewById( R.id.rating );
             type=itemView.findViewById( R.id.type );

        }
        // set on item click and go to the detail page
        @Override
        public void onClick(View v) {
            int position=getAdapterPosition();
            Items items=itemsArrayList.get( position );
            Intent intent = new Intent(context, Detail2Activity.class );
                    if (intent != null) {
//                        Accreditation accreditation= (Accreditation) p.getAccreditation();
                        intent.putExtra( "stringId", items.getSid());
                        intent.putExtra( "page", "search" );
                        intent.putExtra( "accid", items.getAccID() );
                        this.context.startActivity( intent );
                    }
        }
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

                ArrayList<Items> filters = new ArrayList<>();

                for(int i=0; i<filterList.size();i++){
                    if(filterList.get(i).getBrand().toUpperCase().startsWith( (String) constraint )){
                        Items p = filterList.get(i);
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

            itemsList = (ArrayList<Items>) results.values;
            notifyDataSetChanged();
        }
    }


}
